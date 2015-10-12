/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package test;

import de.citec.sc.lemmatizer.Lemmatizer;
import de.citec.sc.lemmatizer.StanfordLemmatizer;
import de.citec.sc.query.IndexSearch;
import java.util.List;

/**
 *
 * @author sherzod
 */
public class TestIndexSearch {
    public static void main(String[] args){
        IndexSearch indexSearch =  new IndexSearch();
        Lemmatizer lemmatizer = new StanfordLemmatizer();
        
        
        List<String> result = indexSearch.getPredicates("producer");
        System.out.println("Predicates"+result);
        
        result = indexSearch.getClasses("actor");
        System.out.println("Classes"+result);
        
        
        String word = "produced";
        word = lemmatizer.lemmatize(word);
        
        result = indexSearch.getPredicatesCombinedWithWordnet(word);
        System.out.println("Predicates + Wordnet"+result);
        
        result = indexSearch.getEntitiesFromAnchorText("Obama");
        System.out.println("Entities"+result);
        
        result = indexSearch.getPredicatesFromMATOLL("daughter");
        System.out.println("MATOLL Predicates:"+result);
        
        
        result = indexSearch.getRestrictionClassesFromMATOLL("catholic");
        System.out.println("MATOLL Restriction classes:"+result);
    }
}
