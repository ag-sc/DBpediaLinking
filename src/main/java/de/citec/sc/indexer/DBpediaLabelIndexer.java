package de.citec.sc.indexer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class DBpediaLabelIndexer implements Indexer {

    private Path ontologypredicateIndexPath;
    private Path instancesIndexPath;
    private Path classIndexPath;
    private Path propertyPredicateIndexPath;

    private IndexWriter ontologyPredicateIndexWriter;
    private IndexWriter propertyPredicateIndexWriter;
    private IndexWriter instancesIndexWriter;
    private IndexWriter classIndexWriter;

    private Document ontologyPredicateDoc;
    private Document propertyPredicateDoc;
    private Document instancesDoc;
    private Document classDoc;

    public void addClass(String label, String uri) throws IOException {
        classDoc = new Document();

        Field labelField = new StringField("label", label, Field.Store.YES);
        classDoc.add(labelField);

        Field uriField = new StringField("URI", uri, Field.Store.YES);
        classDoc.add(uriField);

        Field tokenized = new TextField("labelTokenized", label, Field.Store.YES);
        classDoc.add(tokenized);

        //System.out.println(triplesDoc.toString());
        classIndexWriter.addDocument(classDoc);
    }

    public void addPredicate(String label, String uri) throws IOException {
        ontologyPredicateDoc = new Document();

        Field labelField = new StringField("label", label, Field.Store.YES);
        ontologyPredicateDoc.add(labelField);

        Field uriField = new StringField("URI", uri, Field.Store.YES);
        ontologyPredicateDoc.add(uriField);

        Field tokenized = new TextField("labelTokenized", label, Field.Store.YES);
        ontologyPredicateDoc.add(tokenized);

        ontologyPredicateIndexWriter.addDocument(ontologyPredicateDoc);
    }

    public void addPropertyPredicate(String label, String uri) throws IOException {
        propertyPredicateDoc = new Document();

        Field labelField = new StringField("label", label, Field.Store.YES);
        propertyPredicateDoc.add(labelField);

        Field uriField = new StringField("URI", uri, Field.Store.YES);
        propertyPredicateDoc.add(uriField);

        Field tokenized = new TextField("labelTokenized", label, Field.Store.YES);
        propertyPredicateDoc.add(tokenized);

        propertyPredicateIndexWriter.addDocument(propertyPredicateDoc);
    }

    public void addInstance(String label, String uri, double pageRank) throws IOException {
        instancesDoc = new Document();

        Field labelField = new StringField("label", label, Field.Store.YES);
        instancesDoc.add(labelField);

        Field tokenized = new TextField("labelTokenized", label, Field.Store.YES);
        instancesDoc.add(tokenized);

        Field uriField = new StringField("URI", uri, Field.Store.YES);
        instancesDoc.add(uriField);

        Field rankField = new DoubleField("rank", pageRank, Field.Store.YES);
        instancesDoc.add(rankField);

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

        try {

            ontologypredicateIndexPath = Paths.get(folderPath, "ontologyPredicateIndex");
            ontologyPredicateIndexWriter = initIndexWriter(ontologypredicateIndexPath, true);
            ontologyPredicateDoc = new Document();

            propertyPredicateIndexPath = Paths.get(folderPath, "propertyPredicateIndex");
            propertyPredicateIndexWriter = initIndexWriter(propertyPredicateIndexPath, true);
            propertyPredicateDoc = new Document();

            instancesIndexPath = Paths.get(folderPath, "resourceIndex");
            instancesIndexWriter = initIndexWriter(instancesIndexPath, true);
            instancesDoc = new Document();

            classIndexPath = Paths.get(folderPath, "classIndex");
            classIndexWriter = initIndexWriter(classIndexPath, true);
            classDoc = new Document();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void finilize() {
        try {
            propertyPredicateIndexWriter.close();
            ontologyPredicateIndexWriter.close();
            classIndexWriter.close();
            instancesIndexWriter.close();

        } catch (IOException ex) {
            Logger.getLogger(DBpediaLabelIndexer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public DBpediaLabelIndexer(String filePath) {
        initIndex(filePath);
    }

}
