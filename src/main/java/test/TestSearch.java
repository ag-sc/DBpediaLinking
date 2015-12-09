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

        Lemmatizer lemmatizer = new StanfordLemmatizer();

        String word = "wheat";
        int topK = 1000;

        Set<String> queryTerms = new LinkedHashSet<>();
        queryTerms.add(word);
        queryTerms.add(lemmatizer.lemmatize(word));
        queryTerms.add(word + "~");
        queryTerms.add(lemmatizer.lemmatize(word) + "~");

        Set<String> result = new LinkedHashSet<>();
        for (String q : queryTerms) {
            result.addAll(indexSearch.getResourcesFromDBpedia(q, topK));
        }
        System.out.println("Entities from DBpedia Ontology:\n");
        result.forEach(System.out::println);

        result = new LinkedHashSet<>();
        for (String q : queryTerms) {
            result.addAll(indexSearch.getResourcesFromAnchors(q, topK));
        }
        System.out.println("\nEntities from Anchor Text:\n");
        result.forEach(System.out::println);

        result = new LinkedHashSet<>();
        for (String q : queryTerms) {
            result.addAll(indexSearch.getAllPredicates(q, topK));
        }
        System.out.println("======================================\nProperties:\n");
        result.forEach(System.out::println);

        result = new LinkedHashSet<>();
        for (String q : queryTerms) {
            result.addAll(indexSearch.getClassesFromDBpedia(q, topK));
        }
        System.out.println("======================================\nClasses:\n");
        result.forEach(System.out::println);

        result = new LinkedHashSet<>();
        for (String q : queryTerms) {
            result.addAll(indexSearch.getRestrictionClassesFromMATOLL(q, topK));
        }
        System.out.println("======================================\nRestriction Classes:\n");
        result.forEach(System.out::println);

    }
}
