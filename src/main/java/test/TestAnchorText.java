/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import de.citec.sc.indexer.AnchorTextIndexer;
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
public class TestAnchorText {

    public static void main(String[] args) {
        
//        try {
//            AnchorTextIndexer indexer = new AnchorTextIndexer("anchorIndex");
//            indexer.addEntity("Obama", "Barack_Obama", 100);
//            indexer.addEntity("Obama", "Presindency_Obama", 1);
//            
//            indexer.addEntity("Michelle", "Michelle_Obama", 10);
//            
//            indexer.finilize();
//            
//        } catch (IOException ex) {
//            Logger.getLogger(TestMATOLL.class.getName()).log(Level.SEVERE, null, ex);
//        }

        AnchorTextQueryProcessor processor = new AnchorTextQueryProcessor("anchorIndex");
        List<Instance> top = processor.getTopMatches("Barack", 100);
        for(Instance t : top){
            System.out.println(t);
        }
        
        
        
    }
}
