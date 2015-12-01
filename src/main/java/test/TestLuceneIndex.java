/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import de.citec.sc.lemmatizer.Lemmatizer;
import de.citec.sc.lemmatizer.StanfordLemmatizer;
import de.citec.sc.query.IndexSearch;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sherzod
 */
public class TestLuceneIndex {

    public static void main(String[] args) {
        IndexSearch indexSearch = new IndexSearch();
        Lemmatizer lemmatizer = new StanfordLemmatizer();

        String word = "film festival";
        
        Set<String> queryTerms = new LinkedHashSet<>();
        queryTerms.add(word);
        queryTerms.add(lemmatizer.lemmatize(word));
        queryTerms.add(word + "~");
        queryTerms.add(lemmatizer.lemmatize(word) + "~");
        
        
        Set<String> result = new LinkedHashSet<>();
        for(String  q : queryTerms){
            result.addAll(indexSearch.getEntitiesFromDBpediaOntology(q));
        }
        System.out.println("Entities from DBpedia Ontology:\n");
        result.forEach(System.out::println);
        
        result = new LinkedHashSet<>();
        for(String  q : queryTerms){
            result.addAll(indexSearch.getEntitiesFromAnchorText(q));
        }
        System.out.println("\nEntities from Anchor Text:\n");
        result.forEach(System.out::println);
        
        
        result = new LinkedHashSet<>();
        
        for(String  q : queryTerms){
            result.addAll(indexSearch.getAllPredicates(q));
        }
        System.out.println("======================================\nProperties:\n");
        result.forEach(System.out::println);
        
        
        result = new LinkedHashSet<>();
        for(String  q : queryTerms){
            result.addAll(indexSearch.getClasses(q, true));
        }
        System.out.println("======================================\nClasses:\n");
        result.forEach(System.out::println);
        
        
        result = new LinkedHashSet<>();
        for(String  q : queryTerms){
            result.addAll(indexSearch.getRestrictionClassesFromMATOLL(q, true));
        }
        System.out.println("======================================\nnRestriction Classes:\n");
        result.forEach(System.out::println);
        
       


    }
}
