package de.citec.sc.query;

import de.citec.sc.wordNet.WordNetAnalyzer;
import edu.stanford.nlp.util.ArraySet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IndexSearch {

    /**
     * <p>
     * get list of DBpedia classes for given label </p>
     *
     * @return List of DBpedia classes
     * @param label to search the index
     */
    public Set<String> getClasses(String label) {
        QueryProcessor queryProcessor = new PredicateQueryProcessor();
        List<String> instances = queryProcessor.getMatches(label);

        Set<String> classes = new ArraySet<String>();
        for (String c : instances) {
            // check if the first letter start with Uppercase character
            String firstLetter = c.replace("http://dbpedia.org/ontology/", "").substring(0, 1);
            if (firstLetter.equals(firstLetter.toUpperCase())) {
                if (!classes.contains(c)) {
                    classes.add(c);
                }
            }
        }

        return classes;
    }

    /**
     * <p>
     * get list of DBpedia predicates for given label </p>
     *
     * @return List of DBpedia predicates
     * @param label to search the index
     */
    public Set<String> getPredicates(String label) {
        QueryProcessor queryProcessor = new PredicateQueryProcessor();
        List<String> instances = queryProcessor.getMatches(label);

        Set<String> predicates = new ArraySet<String>();
        for (String c : instances) {
            // check if the first letter start with Lowercase character
            String firstLetter = c.replace("http://dbpedia.org/ontology/", "").substring(0, 1);
            if (firstLetter.equals(firstLetter.toLowerCase())) {
                if (!predicates.contains(c)) {
                    predicates.add(c);
                }
            }
        }

        return predicates;
    }

    /**
     * <p>
     * get set of DBpedia predicates for given label </p>
     * <p>
     * retrieve derivational words from Wordnet for the given label and combine
     * all results </p>
     *
     * @return List of DBpedia predicates
     * @param label to search the index
     */
    public Set<String> getPredicatesCombinedWithWordnet(String label) {
        QueryProcessor queryProcessor = new PredicateQueryProcessor();
        Set<String> predicates = new ArraySet<String>();

        WordNetAnalyzer analyzer = new WordNetAnalyzer("src/main/resources/WordNet-3.0/dict");
        Set<String> words = analyzer.getDerivationalWords(label, "\t");

        for (String w : words) {
            List<String> instances = queryProcessor.getMatches(w);

            for (String c : instances) {
                // check if the first letter start with Lowercase character
                String firstLetter = c.replace("http://dbpedia.org/ontology/", "").substring(0, 1);
                if (firstLetter.equals(firstLetter.toLowerCase())) {
                    if (!predicates.contains(c)) {
                        predicates.add(c);
                    }
                }
            }
        }

        return predicates;
    }

    /**
     * <p>
     * get list of DBpedia resources for given label </p>
     *
     * @return List of DBpedia resources from AnchorText
     * @param label to search the index
     */
    public Set<String> getEntitiesFromAnchorText(String label) {

        QueryProcessor queryProcessor = new AnchorTextQueryProcessor();

        List<Instance> instances = queryProcessor.getTopMatches(label, 10);

        Set<String> result = new ArraySet<>();
        for (Instance c : instances) {
            // check if the first letter start with Lowercase character
            result.add(c.getUri());
        }

        return result;
    }

    /**
     * <p>
     * get list of DBpedia predicates for given label and POS tag </p>
     *
     * @return List of 10 DBpedia predicates from MATOLL (nouns, verb and
     * adjectives)
     * @param label to search the index
     */
    public Set<String> getPredicatesFromMATOLL(String label) {

        QueryProcessor queryProcessor = new MATOLLQueryProcessor();

        List<Instance> instances = queryProcessor.getTopMatches(label, 10);

        Set<String> result = new ArraySet<String>();
        for (Instance c : instances) {

            if (c.getPos().equals("http://www.lexinfo.net/ontology/2.0/lexinfo#adjective")) {
                if (c.getOnProperty().equals("")) {
                    result.add(c.getUri());
                }
            } else {
                result.add(c.getUri());
            }

        }

        return result;
    }

    /**
     * <p>
     * get set of DBpedia restriction classes (predicate###resource) for given
     * label </p>
     * E.g. catholic -> dbo:religion###dbr:Catholic_church E.g. female ->
     * dbo:gender###dbr:Female
     *
     * @return List of 10 DBpedia predicates and resources from MATOLL
     * (restriction class)
     * @param label to search the index
     */
    public Set<String> getRestrictionClassesFromMATOLL(String label) {

        QueryProcessor queryProcessor = new MATOLLQueryProcessor();

        List<Instance> instances = queryProcessor.getTopMatches(label, 10);

        Set<String> result = new ArraySet<String>();
        for (Instance c : instances) {
            if (c.getPos().equals("http://www.lexinfo.net/ontology/2.0/lexinfo#adjective")) {
                if (!c.getOnProperty().equals("")) {
                    result.add(c.getOnProperty() + "###" + c.getUri());
                }
            }
        }

        return result;
    }

    /**
     * <p>
     * get list of DBpedia predicates for given label </p>
     *
     * @return List of DBpedia predicates from MATOLL
     * @param label to search the index
     */
    public Set<String> getPredicatesFromMATOLLcombinedWithWordNet(String label) {

        QueryProcessor queryProcessor = new MATOLLQueryProcessor();
        Set<String> predicates = new ArraySet<String>();

        WordNetAnalyzer analyzer = new WordNetAnalyzer("src/main/resources/WordNet-3.0/dict");
        Set<String> words = analyzer.getDerivationalWords(label, "\t");

        Set<Instance> instances = new ArraySet<>();

        for (String w : words) {
            List<Instance> inst = queryProcessor.getTopMatches(label, 10);
            instances.addAll(inst);
        }

        for (Instance i : instances) {
            // check if the first letter start with Lowercase character
            String c = i.getUri();
            String firstLetter = c.replace("http://dbpedia.org/ontology/", "").substring(0, 1);
            if (firstLetter.equals(firstLetter.toLowerCase())) {
                if (!predicates.contains(c)) {
                    predicates.add(c);
                }
            }
        }

        return predicates;
    }
    
    /**
     * <p>
     * get list of DBpedia predicates for given label combining Wordnet MATOLL and DBpedia Ontology</p>
     *
     * @return List of DBpedia predicates from MATOLL
     * @param label to search the index
     */
    public Set<String> getAllPredicates(String label) {
        Set<String> uris = getPredicates(label);
        
        uris.addAll(getPredicatesCombinedWithWordnet(label));
        uris.addAll(getPredicatesFromMATOLL(label));
        uris.addAll(getPredicatesFromMATOLLcombinedWithWordNet(label));
        
        return uris;
    }
}
