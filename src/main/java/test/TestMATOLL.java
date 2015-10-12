/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import de.citec.sc.indexer.MATOLLIndexer;
import de.citec.sc.matoll.core.LexicalEntry;
import de.citec.sc.matoll.core.Lexicon;
import de.citec.sc.matoll.core.Provenance;
import de.citec.sc.matoll.core.Reference;
import de.citec.sc.matoll.core.Restriction;
import de.citec.sc.matoll.core.Sense;
import de.citec.sc.matoll.io.LexiconLoader;
import de.citec.sc.query.Instance;
import de.citec.sc.query.MATOLLQueryProcessor;
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
public class TestMATOLL {

    public static void main(String[] args) {
        
//        try {
//            MATOLLIndexer indexer = new MATOLLIndexer("matollIndex");
//            indexer.addEntry("URI1", "husband", 10, "noun", "");
//            indexer.addEntry("URI2", "husband", 3, "noun", "");
//            indexer.addEntry("URI3", "husband", 1, "adj", "marriage");
//            indexer.addEntry("URI4", "wife", 1, "adj", "marriage");
//            
//            indexer.finilize();
//            
//        } catch (IOException ex) {
//            Logger.getLogger(TestMATOLL.class.getName()).log(Level.SEVERE, null, ex);
//        }

        MATOLLQueryProcessor processor = new MATOLLQueryProcessor("matollIndex");
        List<Instance> top = processor.getTopMatches("catholic", 10);
        for(Instance t : top){
            System.out.println(t);
        }
        
        
        
    }
}
