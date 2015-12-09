package de.citec.sc.loader;

import de.citec.sc.indexer.Indexer;
import de.citec.sc.indexer.MATOLLIndexer;
import de.citec.sc.matoll.core.LexicalEntry;
import de.citec.sc.matoll.core.Lexicon;
import de.citec.sc.matoll.core.Provenance;
import de.citec.sc.matoll.core.Reference;
import de.citec.sc.matoll.core.Restriction;
import de.citec.sc.matoll.core.Sense;
import de.citec.sc.matoll.io.LexiconLoader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryparser.classic.ParseException;

public class MATOLLLoader implements Loader {

    //docDirectory => dbpedia *.nt files
    //luceneIndex => lucene creates indexes 
    @Override
    public void load(boolean deleteIndexFiles, String indexDirectory, String matollFilesDirectory) {

        try {
            File indexFolder = new File(indexDirectory);
            if (!indexFolder.exists()) {
                indexFolder.mkdir();
                System.out.println(indexDirectory + " directory is created!");
            }
            //delete old index files        
            if (deleteIndexFiles) {
                File[] listOfIndexFiles = indexFolder.listFiles();

                for (int i = 0; i < listOfIndexFiles.length; i++) {
                    if (listOfIndexFiles[i].isDirectory()) {
                        deleteFolder(listOfIndexFiles[i]);
                    }
                }
            }

            //load files
            File folder = new File(matollFilesDirectory);
            File[] listOfFiles = folder.listFiles();

            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile() && !listOfFiles[i].isHidden()) {
                    String fileExtension = listOfFiles[i].getName().substring(listOfFiles[i].getName().lastIndexOf(".") + 1);
                    if (fileExtension.equals("ttl")) {

                        try {
                            MATOLLIndexer indexer = new MATOLLIndexer(indexDirectory);

                            long startTime = System.currentTimeMillis();
                            indexData(matollFilesDirectory + listOfFiles[i].getName(), indexer);
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

            System.out.println("Loading file: " + filePath);

            MATOLLIndexer matollIndexer = (MATOLLIndexer) indexer;

            LexiconLoader loader = new LexiconLoader();
            Lexicon lexicon = loader.loadFromFile(filePath);

            for (LexicalEntry entry : lexicon.getEntries()) {

                String word = "", POS = "", URI = "", onProperty = "";
                int frequency = 0;

                word = entry.getCanonicalForm();
                POS = entry.getPOS();

                for (Sense sense : entry.getSenseBehaviours().keySet()) {

                    URI = "";
                    onProperty = "";
                    frequency = 0;

                    Provenance provenance = entry.getProvenance(sense);
                    Reference ref = sense.getReference();

                    if (ref instanceof de.citec.sc.matoll.core.SimpleReference) {
                        URI = ref.getURI();
                    } else if (ref instanceof de.citec.sc.matoll.core.Restriction) {

                        Restriction reference = (Restriction) ref;

                        onProperty = reference.getProperty();
                        URI = reference.getValue();
                    }

                    frequency = provenance.getFrequency();

                    //change to lower case
                    word = word.toLowerCase();

                    matollIndexer.addEntry(URI, word, frequency, POS, onProperty);
//                    if (URI.equals("http://dbpedia.org/ontology/almaMater")) {
//                        System.out.println(URI + word + frequency + POS + onProperty);
//                    }
                }

            }

        } catch (Exception e) {
            System.err.println("Problem with reading from file : " + filePath + " !!!\n" + e.getMessage());

        }
    }

}
