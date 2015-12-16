/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.sc.query;

import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.RAMDirectory;

/**
 *
 * @author sherzod
 */
public class AnchorRetriever extends LabelRetriever {

    private String indexPath = "anchorsindex";
    private String directory;
    private StandardAnalyzer analyzer;
    private Directory indexDirectory;

    public AnchorRetriever(String directory, boolean loadIntoMemory) {
        this.directory = directory;

        initIndexDirectory(loadIntoMemory);
    }

    private void initIndexDirectory(boolean loadToMemory) {
        try {
            String path = directory + "/" + this.indexPath + "/";
            analyzer = new StandardAnalyzer();
            if (loadToMemory) {
                indexDirectory = new RAMDirectory(FSDirectory.open(Paths.get(path)), IOContext.DEFAULT);
            } else {
                indexDirectory = FSDirectory.open(Paths.get(path));
            }

        } catch (Exception e) {
            System.err.println("Problem with initializing InstanceQueryProcessor\n" + e.getMessage());
        }
    }
    
    public Set<String> getResources(String searchTerm, int k){
        super.comparator = super.frequencyComparator;
        
        Set<Instance> result = getDirectMatches(searchTerm, "label", "URI", k, indexDirectory);
        
        Set<String> resources = new LinkedHashSet<>();
        result.forEach(i1 -> resources.add(i1.getUri()));
        
        return resources;
    }

}
