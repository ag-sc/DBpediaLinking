/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import de.citec.sc.lemmatizer.Lemmatizer;
import de.citec.sc.lemmatizer.StanfordLemmatizer;
import de.citec.sc.query.Search;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 * @author sherzod
 */
public class TestSearch {

    public static void main(String[] args) {
        Search indexSearch = new Search(false);

        String word = "obama";
        int topK = 1;
        boolean lemmatize = true;
        boolean useWordNet = true;

        
        Set<String> result = new LinkedHashSet<>();
        
        long start = System.currentTimeMillis();
        result.addAll(indexSearch.getResourcesFromDBpedia(word, topK, lemmatize, useWordNet));
        long end = System.currentTimeMillis();
        System.out.println((end - start) +" ms");
        System.out.println("Entities from DBpedia Ontology:\n");
        result.forEach(System.out::println);

        result = new LinkedHashSet<>();
        start = System.currentTimeMillis();
        result.addAll(indexSearch.getResourcesFromAnchors(word, topK, lemmatize, useWordNet));
        end = System.currentTimeMillis();
        System.out.println((end - start) +" ms");
        System.out.println("\nEntities from Anchor Text:\n");
        result.forEach(System.out::println);

        result = new LinkedHashSet<>();
        
        result.addAll(indexSearch.getAllPredicates(word, topK, lemmatize, useWordNet));
        System.out.println("======================================\nProperties:\n");
        result.forEach(System.out::println);

        result = new LinkedHashSet<>();
        
        result.addAll(indexSearch.getClassesFromDBpedia(word, topK, lemmatize, useWordNet));
        System.out.println("======================================\nClasses:\n");
        result.forEach(System.out::println);

        result = new LinkedHashSet<>();
        
        result.addAll(indexSearch.getRestrictionClassesFromMATOLL(word, topK, lemmatize, useWordNet));
        System.out.println("======================================\nRestriction Classes:\n");
        result.forEach(System.out::println);

        
    }
}
