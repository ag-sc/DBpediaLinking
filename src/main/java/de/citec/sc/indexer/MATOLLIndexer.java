package de.citec.sc.indexer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class MATOLLIndexer implements Indexer {

    private Path entryIndexPath;

    private IndexWriter entryIndexWriter;

    private Document entryDoc;

    public void addEntry(String URI, String word, int frequency, String POS, String onProperty) throws IOException {

        entryDoc = new Document();

        Field uriField = new StringField("URI", URI, Field.Store.YES);
        entryDoc.add(uriField);

        Field wordField = new StringField("label", word, Field.Store.YES);
        entryDoc.add(wordField);

        Field frequencyField = new IntField("freq", frequency, Field.Store.YES);
        entryDoc.add(frequencyField);

        Field posField = new StringField("POS", POS, Field.Store.YES);
        entryDoc.add(posField);

        Field onPropertyField = new StringField("onProperty", onProperty, Field.Store.YES);
        entryDoc.add(onPropertyField);

        //System.out.println(triplesDoc.toString());
        entryIndexWriter.addDocument(entryDoc);
    }

    
    private IndexWriter initIndexWriter(Path path) {
        try {
            Directory dir = FSDirectory.open(path);
            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

            IndexWriter writer = new IndexWriter(dir, iwc);
            return writer;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public void finilize() {
        try {
            entryIndexWriter.close();
        } catch (IOException ex) {
            Logger.getLogger(MATOLLIndexer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void initIndex(String indexPath) {
        
        if (indexPath == null) {
            throw new RuntimeException("The indexes directory path must be specified");
        }

        entryIndexPath = Paths.get(indexPath, "index");
        entryIndexWriter = initIndexWriter(entryIndexPath);
        entryDoc = new Document();
    }

    public MATOLLIndexer(String indexPath) {
        initIndex(indexPath);
    }
    
    
}
