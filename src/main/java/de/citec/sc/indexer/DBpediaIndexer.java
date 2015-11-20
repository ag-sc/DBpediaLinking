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
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class DBpediaIndexer implements Indexer {

    private Path triplesIndexPath;
    private Path predicatesIndexPath;
    private Path instancesIndexPath;

    private IndexWriter triplesIndexWriter;
    private IndexWriter predicatesIndexWriter;
    private IndexWriter instancesIndexWriter;

    private Document triplesDoc;
    private Document predicatesDoc;
    private Document instancesDoc;

    public void addTriple(String subjectUri, String predicateUri, String objectUri) throws IOException {
        triplesDoc = new Document();

        Field subjectField = new StringField("subject", subjectUri, Field.Store.YES);
        triplesDoc.add(subjectField);

        Field predicateField = new StringField("predicate", predicateUri, Field.Store.YES);
        triplesDoc.add(predicateField);

        Field objectField = new StringField("object", objectUri, Field.Store.YES);
        triplesDoc.add(objectField);

        //System.out.println(triplesDoc.toString());
        triplesIndexWriter.addDocument(triplesDoc);
    }

    public void addPredicate(String label, String uri, String synonyms, String hypernyms) throws IOException {
        predicatesDoc = new Document();

        Field labelField = new TextField("label", label, Field.Store.YES);
        predicatesDoc.add(labelField);

        Field uriField = new StringField("URI", uri, Field.Store.YES);
        predicatesDoc.add(uriField);

        Field synonymsField = new TextField("synonyms", synonyms, Field.Store.NO);
        predicatesDoc.add(synonymsField);

        Field hypernymsField = new TextField("hyponyms", hypernyms, Field.Store.NO);
        predicatesDoc.add(hypernymsField);

        //Field derivationalWordsField = new TextField("derivationalWords", derivationalWords, Field.Store.NO);
        //predicatesDoc.add(derivationalWordsField);
        predicatesIndexWriter.addDocument(predicatesDoc);
    }

    public void addInstance(String label, String uri) throws IOException {
        instancesDoc = new Document();

        Field labelField = new TextField("label", label, Field.Store.YES);
        instancesDoc.add(labelField);

        Field uriField = new StringField("URI", uri, Field.Store.YES);
        instancesDoc.add(uriField);
        instancesIndexWriter.addDocument(instancesDoc);
    }

    private IndexWriter initIndexWriter(Path path, boolean create) throws IOException {
        Directory dir = FSDirectory.open(path);
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

        /*
         * if (create) { iwc.setOpenMode(OpenMode.CREATE); } else { // Add new
         * documents to an existing index:
         * iwc.setOpenMode(OpenMode.CREATE_OR_APPEND); }
         */
        IndexWriter writer = new IndexWriter(dir, iwc);
        return writer;
    }



    @Override
    public void initIndex(String folderPath) {
        if (folderPath == null) {
            throw new RuntimeException("The indexes directory path must be specified");
        }

        try{
            triplesIndexPath = Paths.get(folderPath, "triplesindex");
        triplesIndexWriter = initIndexWriter(triplesIndexPath, true);

        predicatesIndexPath = Paths.get(folderPath, "predicatesindex");
        predicatesIndexWriter = initIndexWriter(predicatesIndexPath, true);
        predicatesDoc = new Document();

        instancesIndexPath = Paths.get(folderPath, "instancesindex");
        instancesIndexWriter = initIndexWriter(instancesIndexPath, true);
        instancesDoc = new Document();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
        
    }

    @Override
    public void finilize() {
        try {
            triplesIndexWriter.close();
            predicatesIndexWriter.close();
            instancesIndexWriter.close();
        } catch (IOException ex) {
            Logger.getLogger(DBpediaIndexer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public DBpediaIndexer(String filePath) {
        initIndex(filePath);
    }
    
    
}
