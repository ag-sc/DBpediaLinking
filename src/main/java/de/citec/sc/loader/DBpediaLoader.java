package de.citec.sc.loader;

import de.citec.sc.indexer.DBpediaIndexer;
import de.citec.sc.indexer.Indexer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryparser.classic.ParseException;

public class DBpediaLoader implements Loader {

    //docDirectory => dbpedia *.nt files
    //luceneIndex => lucene creates indexes 
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

            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile() && !listOfFiles[i].isHidden()) {
                    String fileExtension = listOfFiles[i].getName().substring(listOfFiles[i].getName().lastIndexOf(".") + 1);
                    if (fileExtension.equals("nt")) {

                        try {
                            DBpediaIndexer indexer = new DBpediaIndexer(indexDirectory);

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

            DBpediaIndexer dbpediaIndexer = (DBpediaIndexer) indexer;

            System.out.println("Loading file: " + filePath);
            RandomAccessFile aFile = new RandomAccessFile(filePath, "r");
            FileChannel inChannel = aFile.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            String line = "";
            while (inChannel.read(buffer) > 0) {
                buffer.flip();
                for (int i = 0; i < buffer.limit(); i++) {
                    String b = (char) buffer.get() + "";
                    if (b.equals("\n")) {
                        if (line.startsWith("#")) {
                            line = "";
                        } else {

                            //System.out.println(line);
                            String s = line.substring(0, line.indexOf(" ")).trim();
                            line = line.replace(s, "").trim();
                            String p = line.substring(0, line.indexOf(" ")).trim();
                            line = line.replace(p, "").trim();
                            String o = line.trim();

                            s = s.replace("<", "");
                            s = s.replace(">", "");
                            p = p.replace("<", "");
                            p = p.replace(">", "");
                            o = o.replace("<", "");
                            o = o.replace(">", "");

                            if (o.lastIndexOf(".") == o.length() - 1) {
                                o = o.substring(0, o.length() - 1).trim();
                            }
                            s = s.trim();
                            p = p.trim();
                            o = o.trim();

                            if (!s.equals("") && !p.equals("") && !o.equals("")) {
                                if (s.contains("http://dbpedia.org/resource/")
                                        && p.equals("http://www.w3.org/2000/01/rdf-schema#label")) {
                                    // create entity index
                                    if (o.contains("\"@en")) {
                                        //remove " and "@en from string
                                        o = o.substring(1, o.length() - 4);
                                        o = o.toLowerCase();
                                        dbpediaIndexer.addInstance(o, s);
                                    }
                                } else if (s.startsWith("http://dbpedia.org/ontology/") && p.equals("http://www.w3.org/2000/01/rdf-schema#label")) {

                                    if (o.contains("\"@en")) {
                                        o = o.substring(1, o.length() - 4);
                                        o = o.toLowerCase();
                                        dbpediaIndexer.addPredicate(o, s, "", "");
                                    }

                                } else if (s.startsWith("http") && p.startsWith("http") && o.contains("http")) {
                                    // create triple index
                                    //dbpediaIndexer.addTriple(s, p, o);
                                }
                            }

                            line = "";
                        }

                    } else {
                        line += b;

                    }

                }
                buffer.clear();
            }
            inChannel.close();
            aFile.close();
        } catch (Exception e) {
            System.err.println("Problem with reading from file : " + filePath + " !!!\n" + e.getMessage());

        }

    }

}
