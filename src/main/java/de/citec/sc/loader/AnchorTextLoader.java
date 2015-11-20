package de.citec.sc.loader;

import de.citec.sc.indexer.AnchorTextIndexer;
import de.citec.sc.indexer.Indexer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryparser.classic.ParseException;

public class AnchorTextLoader implements Loader{

    //docDirectory => dbpedia *.nt files
    //luceneIndex => lucene creates indexes 
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

                            label  = label.toLowerCase();
                            
                            anchorIndexer.addEntity(label, uri, freq);
                            
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
