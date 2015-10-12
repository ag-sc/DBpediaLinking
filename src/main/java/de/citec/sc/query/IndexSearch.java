package de.citec.sc.query;

import de.citec.sc.wordNet.WordNetAnalyzer;
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
    public List<String> getClasses(String label) {
        QueryProcessor queryProcessor = new PredicateQueryProcessor();
        List<String> instances = queryProcessor.getMatches(label);

        List<String> classes = new ArrayList<String>();
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
    public List<String> getPredicates(String label) {
        QueryProcessor queryProcessor = new PredicateQueryProcessor();
        List<String> instances = queryProcessor.getMatches(label);

        List<String> predicates = new ArrayList<String>();
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
     * get list of DBpedia predicates for given label </p>
     * <p>
     * retrieve derivational words from Wordnet for the given label and combine
     * all results </p>
     *
     * @return List of DBpedia predicates
     * @param label to search the index
     */
    public List<String> getPredicatesCombinedWithWordnet(String label) {
        QueryProcessor queryProcessor = new PredicateQueryProcessor();
        List<String> predicates = new ArrayList<String>();

        WordNetAnalyzer analyzer = new WordNetAnalyzer("src/main/resources/WordNet-3.0/dict");
        List<String> words = analyzer.getDerivationalWords(label, "\t");

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
    public List<String> getEntitiesFromAnchorText(String label) {

        QueryProcessor queryProcessor = new AnchorTextQueryProcessor();

        List<Instance> instances = queryProcessor.getTopMatches(label, 10);

        List<String> result = new ArrayList<String>();
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
    public List<String> getPredicatesFromMATOLL(String label) {

        QueryProcessor queryProcessor = new MATOLLQueryProcessor();

        List<Instance> instances = queryProcessor.getTopMatches(label, 10);

        List<String> result = new ArrayList<String>();
        for (Instance c : instances) {

            if(c.getPos().equals("http://www.lexinfo.net/ontology/2.0/lexinfo#adjective")){
                if(c.getOnProperty().equals("")){
                    result.add(c.getUri());
                }
            }
            else{
                result.add(c.getUri());
            }
            

        }

        return result;
    }

    /**
     * <p>
     * get list of DBpedia restriction classes (predicate###resource) for given
     * label </p>
     * E.g. catholic -> dbo:religion###dbr:Catholic_church E.g. female ->
     * dbo:gender###dbr:Female
     *
     * @return List of 10 DBpedia predicates and resources from MATOLL
     * (restriction class)
     * @param label to search the index
     */
    public List<String> getRestrictionClassesFromMATOLL(String label) {

        QueryProcessor queryProcessor = new MATOLLQueryProcessor();

        List<Instance> instances = queryProcessor.getTopMatches(label, 10);

        List<String> result = new ArrayList<String>();
        for (Instance c : instances) {
            if(c.getPos().equals("http://www.lexinfo.net/ontology/2.0/lexinfo#adjective")){
                if(!c.getOnProperty().equals("")){
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
    public List<String> getPredicatesFromMATOLLcombinedWithWordNet(String label) {

        QueryProcessor queryProcessor = new MATOLLQueryProcessor();
        List<String> predicates = new ArrayList<String>();

        WordNetAnalyzer analyzer = new WordNetAnalyzer("src/main/resources/WordNet-3.0/dict");
        List<String> words = analyzer.getDerivationalWords(label, "\t");

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

}
