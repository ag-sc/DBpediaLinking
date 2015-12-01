/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import de.citec.sc.indexer.AnchorTextIndexer;
import de.citec.sc.indexer.DBpediaIndexer;
import de.citec.sc.indexer.MATOLLIndexer;
import de.citec.sc.matoll.core.LexicalEntry;
import de.citec.sc.matoll.core.Lexicon;
import de.citec.sc.matoll.core.Provenance;
import de.citec.sc.matoll.core.Reference;
import de.citec.sc.matoll.core.Restriction;
import de.citec.sc.matoll.core.Sense;
import de.citec.sc.matoll.io.LexiconLoader;
import de.citec.sc.query.AnchorTextQueryProcessor;
import de.citec.sc.query.Instance;
import de.citec.sc.query.MATOLLQueryProcessor;
import de.citec.sc.query.PredicateQueryProcessor;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author sherzod
 */
public class TestDBpedia {

    public static void main(String[] args) {
        
//        try {
//            DBpediaIndexer indexer = new DBpediaIndexer("luceneIndex");
//            //indexer.addPredicate("actor", "http://dbpedia.org/ontology/Actor", "", "");
//            //indexer.addPredicate("producer", "http://dbpedia.org/ontology/producer", "", "");
//            
//            indexer.addInstance("free university in amsterdam", "VU");
//    
//            
//            indexer.finilize();
//            
//        } catch (IOException ex) {
//            Logger.getLogger(TestMATOLL.class.getName()).log(Level.SEVERE, null, ex);
//        }

        PredicateQueryProcessor processor = new PredicateQueryProcessor("luceneIndex");
        Set<String> s = processor.getDBpediaResources("free university", 1000);
        s.forEach(s1 -> System.out.println(s1));
        
//        List<Instance> top = processor.getTopMatches("developer", 1);
//        for(Instance t : top){
//            System.out.println(t);
//        }
        
        
        
    }
}
