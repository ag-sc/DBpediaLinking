package de.citec.sc.loader;

import de.citec.sc.indexer.DBpediaLabelIndexer;
import de.citec.sc.indexer.Indexer;
import edu.stanford.nlp.util.ArrayHeap;
import edu.stanford.nlp.util.ArraySet;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryparser.classic.ParseException;

public class DBpediaLoader2 implements Loader {

    //docDirectory => dbpedia *.nt files
    //luceneIndex => lucene creates indexes 
    private Set<String> properties;
    private Set<String> redirects;
    private HashMap<String, Double> pageRanks = new HashMap<String, Double>();

    @Override
    public void load(boolean deleteIndexFiles, String indexDirectory, String dbpediaFilesDirectory) {

        try {
            File indexFolder = new File(indexDirectory);
            if (!indexFolder.exists()) {
                indexFolder.mkdir();
                System.out.println(indexDirectory + " directory is created!");
            }
            //delete old indice files        
            if (deleteIndexFiles) {
                File[] listOfIndexFiles = indexFolder.listFiles();

                for (int i = 0; i < listOfIndexFiles.length; i++) {
                    if (listOfIndexFiles[i].isDirectory()) {
                        deleteFolder(listOfIndexFiles[i]);
                    }
                }
            }

            //load files
            File folder = new File(dbpediaFilesDirectory);
            File[] listOfFiles = folder.listFiles();

            properties = readFile(new File("src/main/resources/propertyList.txt"));
            //get redirects pages only, not actual resources
            //create redirect index before
            //processor = new DBpediaRedirectQueryProcessor(true);
            long start = System.currentTimeMillis();
            redirects = getRedirects("dbpediaFiles/redirects_en.nt.bz2");
            long end = System.currentTimeMillis() - start;
            System.out.println("DONE " + (end)+ " ms.");
            
            
            start = System.currentTimeMillis();
            System.out.println("Adding 'dbpediaFiles/pageranks.ttl' to memory for indexing");
            pageRanks = getPageRanks(new File("dbpediaFiles/pageranks.ttl"));
            end = System.currentTimeMillis() - start;
            System.out.println("DONE " + (end)+ " ms.");

            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile() && !listOfFiles[i].isHidden()) {
                    String fileExtension = listOfFiles[i].getName().substring(listOfFiles[i].getName().lastIndexOf(".") + 1);
                    if (fileExtension.equals("bz2")) {

                        try {
                            DBpediaLabelIndexer indexer = new DBpediaLabelIndexer(indexDirectory);

                            long startTime = System.currentTimeMillis();
                            indexData(dbpediaFilesDirectory + listOfFiles[i].getName(), indexer);
                            indexer.finilize();
                            long endTime = System.currentTimeMillis();
                            System.out.println((endTime - startTime) / 1000 + " sec.");

                        } catch (Exception ex) {
                            System.err.println("Problem loading file: " + listOfFiles[i].getName());
                        }
                    }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) { //some JVMs return null for empty dirs
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }

    // reads chunks of data from filePath
    @Override
    public void indexData(String filePath, Indexer indexer) {

        try {
            DBpediaLabelIndexer dbpediaIndexer = (DBpediaLabelIndexer) indexer;

            System.out.println("Loading file: " + filePath);

            FileInputStream fin = new FileInputStream(filePath);
            BufferedInputStream bis = new BufferedInputStream(fin);
            CompressorInputStream input = new CompressorStreamFactory(true).createCompressorInputStream(bis);
            BufferedReader br = new BufferedReader(new InputStreamReader(input));

            System.out.println(br.lines().count());
            br.lines().filter(line -> line.startsWith("<")).forEach(line -> {

                Pattern entityPattern = Pattern.compile(
                        "<http://dbpedia.org/resource/(.*?)> (.*?) (.*?) .");

                Pattern redirectPattern = Pattern.compile(
                        "<http://dbpedia.org/resource/(.*?)> <http://dbpedia.org/ontology/wikiPageRedirects> <http://dbpedia.org/resource/(.*?)> .");

                Matcher matcher = entityPattern.matcher(line);

                //LOAD Properties in "properties" list as labels for resources
                if (matcher.matches()) {
                    String s = "http://dbpedia.org/resource/" + matcher.group(1);
                    String p = matcher.group(2).replace("<", "").replace(">", "");
                    String o = matcher.group(3);

                    if (s.equals("http://dbpedia.org/resource/Barack_Obama")) {
                        int z = 21;
                    }

                    //DON'T index redirect pages, disambiguation pages
                    if (properties.contains(p) && !s.contains("disambiguation") && !redirects.contains(s)) {

                        if (o.contains("\"@en")) {
                            //remove " and "@en from string
                            o = o.substring(1, o.length() - 4);
                        }

                        //remove after comma e.g. AS,_b
                        if (o.contains(",")) {
                            o = o.substring(0, o.indexOf(","));
                        }

                        //remove parantheses e.g. AS_(song)
                        if (o.contains("(") && o.contains(")")) {
                            o = o.substring(0, o.indexOf("(")).trim();
                        }

                        o = o.trim();
                        o = o.toLowerCase();

                        //check if Subject is not a redirect page
                        //get subject for the given String s
                        //empty means this subject is not a redirect page
                        try {

                            double rank = 0;
                            if (pageRanks.get(s) != null) {
                                rank = pageRanks.get(s);
                            }
                            dbpediaIndexer.addInstance(o, s, rank);
                        } catch (Exception e) {
                            System.err.println("Problem with adding " + o + " " + s);
                        }

                    }
                }

                matcher = redirectPattern.matcher(line);

                //LOAD REDIRECT PAGES AS LABELS
                if (matcher.matches()) {
                    String s = matcher.group(1);
                    String o = "http://dbpedia.org/resource/" + matcher.group(2);

                    if (!o.contains("disambiguation")) {
                        s = s.replace("_", " ");
                        String label = "";

                        //replace each big character with space and the same character
                        //indexing  accessible computing is better than AccessibleComputing
                        for (int i = 0; i < s.length(); i++) {
                            char c = s.charAt(i);
                            if (Character.isUpperCase(c)) {
                                label += " " + c;
                            } else {
                                label += c + "";
                            }
                        }

                        s = label.toLowerCase();

                        //remove after comma e.g. AS,_b
                        if (s.contains(",")) {
                            s = s.substring(0, s.indexOf(","));
                        }

                        //remove parantheses e.g. AS_(song)
                        if (s.contains("(") && s.contains(")")) {
                            s = s.substring(0, s.indexOf("(")).trim();
                        }

                        s = s.trim();

                        //s is the redirect page, o is the original resource
                        try {
                            double rank = 0;
                            if (pageRanks.get(o) != null) {
                                rank = pageRanks.get(o);
                            }

                            dbpediaIndexer.addInstance(s, o, rank);
                        } catch (Exception e) {
                            System.err.println("Problem with adding " + o + " " + s);
                        }
                    }
                }

                //LOAD Ontology Property predicates
                Pattern predicatePattern = Pattern.compile(
                        "<http://dbpedia.org/ontology/(.*?)> <http://www.w3.org/2000/01/rdf-schema#label> (.*?) .");

                Pattern propertyPattern = Pattern.compile(
                        "<http://dbpedia.org/property/(.*?)> <http://www.w3.org/2000/01/rdf-schema#label> (.*?) .");

                matcher = predicatePattern.matcher(line);
                if (matcher.matches()) {
                    String s = "http://dbpedia.org/ontology/" + matcher.group(1);
                    String o = matcher.group(2);

                    if (o.contains("\"@en")) {
                        o = o.substring(1, o.length() - 4);
                        o = o.toLowerCase();
                        o = o.trim();

                        try {
                            dbpediaIndexer.addPredicate(o, s);
                        } catch (Exception e) {
                            System.err.println("Problem with adding " + o + " " + s);
                        }

                    }
                }

                matcher = propertyPattern.matcher(line);
                if (matcher.matches()) {
                    String s = "http://dbpedia.org/property/" + matcher.group(1);
                    String o = matcher.group(2);

                    if (o.contains("\"@en")) {
                        o = o.substring(1, o.length() - 4);
                        o = o.toLowerCase();
                        o = o.trim();

                        try {
                            //don't add http://dbpedia.org/property/n, http://dbpedia.org/property/ss
                            if (o.length() > 2) {
                                dbpediaIndexer.addPredicate(o, s);
                            }

                        } catch (Exception e) {
                            System.err.println("Problem with adding " + o + " " + s);
                        }
                    }
                }

            });
            br.close();
            bis.close();
            fin.close();

        } catch (IOException e) {
            System.err.println("Problem with reading from file : " + filePath + " !!!\n" + e.getMessage());
        } catch (CompressorException ex) {
            Logger.getLogger(DBpediaLoader2.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public Set<String> getRedirects(String filePath) {
        //HashMap<String, Set<String>> content = new HashMap<>();

        Set<String> content = new LinkedHashSet<>();

        try {
            FileInputStream fin = new FileInputStream(filePath);
            BufferedInputStream bis = new BufferedInputStream(fin);
            CompressorInputStream input = new CompressorStreamFactory(true).createCompressorInputStream(bis);
            BufferedReader br = new BufferedReader(new InputStreamReader(input));

            br.lines().filter(line -> line.startsWith("<")).forEach(line -> {
                //System.out.println(line);
                String[] a = line.split(" ");

                String s = a[0];

                String p = a[1];

                String o = a[2];

                s = s.replace("<", "");
                s = s.replace(">", "");
                p = p.replace("<", "");
                p = p.replace(">", "");
                o = o.replace("<", "");
                o = o.replace(">", "");

                content.add(s);
            });

            br.close();
            bis.close();
            fin.close();
        } catch (Exception e) {
            System.err.println("Error reading the file: " + filePath + "\n" + e.getMessage());
        }

        return content;
    }

    public Set<String> readFile(File file) {
        Set<String> content = null;
        try {
            FileInputStream fstream = new FileInputStream(file);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;

            while ((strLine = br.readLine()) != null) {
                if (content == null) {
                    content = new ArraySet<>();
                }
                content.add(strLine);
            }
            in.close();
        } catch (Exception e) {
            System.err.println("Error reading the file: " + file.getPath() + "\n" + e.getMessage());
        }

        return content;
    }

    public HashMap<String, Double> getPageRanks(File file) {
        //HashMap<String, Set<String>> content = new HashMap<>();

        HashMap<String, Double> ranks = new HashMap<>();

        try {
            FileInputStream fstream = new FileInputStream(file);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;

            while ((line = br.readLine()) != null) {

                if (!line.startsWith("#")) {
                    //System.out.println(line);
                    try {
                        String[] content = line.split("\t");

                        String uri = content[0];
                        uri = uri.substring(1, uri.length() - 1);
                        String value = content[3].replace("^^<http://www.w3.org/2001/XMLSchema#float>] .", "").replace("\"", "");
                        double rank = Double.parseDouble(value);

                        ranks.put(uri, rank);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
            in.close();
        } catch (Exception e) {
            System.err.println("Error reading the file: " + file.getPath() + "\n" + e.getMessage());
        }

        return ranks;
    }
}
