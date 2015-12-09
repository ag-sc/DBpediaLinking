package de.citec.sc.loader;

import de.citec.sc.indexer.AnchorTextIndexer;
import de.citec.sc.indexer.Indexer;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryparser.classic.ParseException;

public class AnchorTextLoader implements Loader {

    //docDirectory => dbpedia *.nt files
    //luceneIndex => lucene creates indexes 
   
    HashMap<String, String> redirects;

    @Override
    public void load(boolean deleteIndexFiles, String indexDirectory, String anchorFilesDirectory) {

        //delete old indice files
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

            //processor = new DBpediaRedirectQueryProcessor(true);
            System.out.println("Adding 'dbpediaFiles/redirects_en.nt' to memory for indexing");
            redirects = getRedirects(new File("dbpediaFiles/redirects_en.nt"));

            //load files
            File folder = new File(anchorFilesDirectory);
            File[] listOfFiles = folder.listFiles();

            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile() && !listOfFiles[i].isHidden()) {
                    String fileExtension = listOfFiles[i].getName().substring(listOfFiles[i].getName().lastIndexOf(".") + 1);
                    if (fileExtension.equals("ttl")) {

                        try {
                            AnchorTextIndexer indexer = new AnchorTextIndexer(indexDirectory);

                            long startTime = System.currentTimeMillis();

                            indexData(anchorFilesDirectory + listOfFiles[i].getName(), indexer);

                            indexer.finilize();

                            long endTime = System.currentTimeMillis();
                            System.out.println((endTime - startTime) / 1000 + " sec.");

                        } catch (Exception e) {
                            System.err.println("Problem loading : " + listOfFiles[i].getName());
                        }
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static HashMap<String, String> getRedirects(File file) {
        HashMap<String, String> content = new HashMap<>();

        try {
            FileInputStream fstream = new FileInputStream(file);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;

            while ((line = br.readLine()) != null) {

                if (!line.startsWith("#")) {
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

                    Properties p1 = new Properties();
                    try {
                        p1.load(new StringReader("key=" + s));
                    } catch (IOException ex) {

                    }
                    s = p1.getProperty("key");

                    p1 = new Properties();
                    try {
                        p1.load(new StringReader("key=" + o));
                    } catch (IOException ex) {

                    }
                    o = p1.getProperty("key");

                    content.put(s, o);

                }
            }
            in.close();
        } catch (Exception e) {
            System.err.println("Error reading the file: " + file.getPath() + "\n" + e.getMessage());
        }

        return content;
    }

    private static void deleteFolder(File folder) {
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

            AnchorTextIndexer anchorIndexer = (AnchorTextIndexer) indexer;

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

                        try {
                            String[] all = line.split("\t");
                            String label = all[0];
                            String uri = all[1];
                            int freq = Integer.parseInt(all[2]);

                            label = label.toLowerCase();

                            Properties p1 = new Properties();
                            try {
                                p1.load(new StringReader("key=" + uri));
                            } catch (IOException ex) {

                            }
                            uri = p1.getProperty("key");
                            uri = URLDecoder.decode(uri, "UTF-8");
                            
                            if(!redirects.containsKey(uri)){
                                anchorIndexer.addEntity(label, uri, freq);
                            }

//                            Set<String> subjects = processor.getSubject(uri);
//                            if (subjects.isEmpty()) {
//                                anchorIndexer.addEntity(label, uri, freq);
//                            }
//                            else{
//                                int z=2;
//                            }

                        } catch (Exception ex) {
                            System.err.println(line);
                        }

                        line = "";

                    } else {
                        line += b;

                    }

                }
                buffer.clear();
            }
            inChannel.close();
            aFile.close();
        } catch (Exception e) {
            e.printStackTrace();
            //System.err.println(e.printStackTrace());

        }
    }

}
