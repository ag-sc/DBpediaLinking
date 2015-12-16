/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.sc.query;

import de.citec.sc.lemmatizer.Lemmatizer;
import de.citec.sc.lemmatizer.StanfordLemmatizer;
import de.citec.sc.wordNet.WordNetAnalyzer;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sherzod
 */
public class Search {

    private AnchorRetriever anchorRetriever;
    private MATOLLRetriever matollRetriever;
    private DBpediaRetriever dbpediaRetriever;
    private Lemmatizer lemmatizer;
    private WordNetAnalyzer wordNetAnalyzer;

    private boolean loadToMemory;

    public Search(boolean loadToMemory) {
        this.loadToMemory = loadToMemory;

        this.anchorRetriever = new AnchorRetriever("anchorIndex", loadToMemory);
        this.dbpediaRetriever = new DBpediaRetriever("dbpediaIndex", loadToMemory);
        this.matollRetriever = new MATOLLRetriever("matollIndex", loadToMemory);
        this.lemmatizer = new StanfordLemmatizer();

        try {
            //URL wordNetDirectory = Search.class.getClassLoader().getResource("WordNet-3.0");
            String s = "src/main/resources/WordNet-3.0/dict";//wordNetDirectory.toURI().getPath();
            this.wordNetAnalyzer = new WordNetAnalyzer(s);
        } catch (Exception ex) {
            System.err.println("Can't locate 'src/main/resources/WordNet-3.0/dict'");
        }

    }

    public Set<String> getPredicatesFromDBpedia(String nameSpace, String searchTerm, int topK, boolean lemmatize, boolean useWordNet) {
        Set<String> result = new LinkedHashSet<>();

        Set<String> searchTermSet = new LinkedHashSet<>();
        searchTermSet.add(searchTerm);

        //lemmatize and add lemmatized version too
        if (lemmatize) {
            searchTermSet.add(lemmatizer.lemmatize(searchTerm));
            searchTermSet.add(searchTerm + "~");
            searchTermSet.add(lemmatizer.lemmatize(searchTerm) + "~");
        }

        //get derivational words from WordNet and add all
        if (useWordNet) {
            Set<String> derivationalWords = wordNetAnalyzer.getDerivationalWords(searchTerm);

            searchTermSet.addAll(derivationalWords);
        }

        for (String term : searchTermSet) {
            result.addAll(dbpediaRetriever.getPredicates(term, topK, nameSpace));
        }

        return result;
    }

    public Set<String> getPredicatesFromMATOLL(String nameSpace, String searchTerm, int topK, boolean lemmatize, boolean useWordNet) {
        Set<String> result = new LinkedHashSet<>();

        Set<String> searchTermSet = new LinkedHashSet<>();
        searchTermSet.add(searchTerm);

        //lemmatize and add lemmatized version too
        if (lemmatize) {
            searchTermSet.add(lemmatizer.lemmatize(searchTerm));
            searchTermSet.add(searchTerm + "~");
            searchTermSet.add(lemmatizer.lemmatize(searchTerm) + "~");
        }

        //get derivational words from WordNet and add all
        if (useWordNet) {
            Set<String> derivationalWords = wordNetAnalyzer.getDerivationalWords(searchTerm);

            searchTermSet.addAll(derivationalWords);
        }

        for (String term : searchTermSet) {
            result.addAll(matollRetriever.getPredicates(term, topK, nameSpace));
        }

        return result;
    }

    public Set<String> getAllPredicatesFromDBpedia(String searchTerm, int topK, boolean lemmatize, boolean useWordNet) {
        Set<String> result = new LinkedHashSet<>();

        Set<String> searchTermSet = new LinkedHashSet<>();
        searchTermSet.add(searchTerm);

        //lemmatize and add lemmatized version too
        if (lemmatize) {
            searchTermSet.add(lemmatizer.lemmatize(searchTerm));
            searchTermSet.add(searchTerm + "~");
            searchTermSet.add(lemmatizer.lemmatize(searchTerm) + "~");
        }

        //get derivational words from WordNet and add all
        if (useWordNet) {
            Set<String> derivationalWords = wordNetAnalyzer.getDerivationalWords(searchTerm);

            searchTermSet.addAll(derivationalWords);
        }

        for (String term : searchTermSet) {
            result.addAll(dbpediaRetriever.getAllPredicates(term, topK));
        }

        return result;
    }

    public Set<String> getAllPredicatesFromMATOLL(String searchTerm, int topK, boolean lemmatize, boolean useWordNet) {
        Set<String> result = new LinkedHashSet<>();

        Set<String> searchTermSet = new LinkedHashSet<>();
        searchTermSet.add(searchTerm);

        //lemmatize and add lemmatized version too
        if (lemmatize) {
            searchTermSet.add(lemmatizer.lemmatize(searchTerm));
            searchTermSet.add(searchTerm + "~");
            searchTermSet.add(lemmatizer.lemmatize(searchTerm) + "~");
        }

        //get derivational words from WordNet and add all
        if (useWordNet) {
            Set<String> derivationalWords = wordNetAnalyzer.getDerivationalWords(searchTerm);

            searchTermSet.addAll(derivationalWords);
        }

        for (String term : searchTermSet) {
            result.addAll(matollRetriever.getAllPredicates(term, topK));
        }

        return result;
    }

    /**
     * @return returns all predicates that match the label using DBpedia and
     * MATOLL indexes
     * @param searchTerm
     * @param topK
     */
    public Set<String> getAllPredicates(String searchTerm, int topK, boolean lemmatize, boolean useWordNet) {
        Set<String> result = new LinkedHashSet<>();

        Set<String> searchTermSet = new LinkedHashSet<>();
        searchTermSet.add(searchTerm);

        //lemmatize and add lemmatized version too
        if (lemmatize) {
            searchTermSet.add(lemmatizer.lemmatize(searchTerm));
            searchTermSet.add(searchTerm + "~");
            searchTermSet.add(lemmatizer.lemmatize(searchTerm) + "~");
        }

        //get derivational words from WordNet and add all
        if (useWordNet) {
            Set<String> derivationalWords = wordNetAnalyzer.getDerivationalWords(searchTerm);

            searchTermSet.addAll(derivationalWords);
        }

        for (String term : searchTermSet) {
            //get all from MATOLL
            result.addAll(matollRetriever.getAllPredicates(term, (topK / 2)));
            //add all from DBpedia
            result.addAll(dbpediaRetriever.getAllPredicates(term, (topK / 2)));
        }

        if (result.size() > topK) {

            List<String> all = new ArrayList<>();
            all.addAll(result);
            result.clear();
            
            //get subset from the list
            result.addAll(all.subList(0, topK));
        }

        return result;
    }

    /**
     * @return returns all predicates with the specified "namespace" that match
     * the label using DBpedia and MATOLL indexes
     * @param searchTerm
     * @param topK
     * @param nameSpace
     */
    public Set<String> getAllPredicates(String searchTerm, int topK, String nameSpace, boolean lemmatize, boolean useWordNet) {
        Set<String> result = new LinkedHashSet<>();

        Set<String> searchTermSet = new LinkedHashSet<>();
        searchTermSet.add(searchTerm);

        //lemmatize and add lemmatized version too
        if (lemmatize) {
            searchTermSet.add(lemmatizer.lemmatize(searchTerm));
            searchTermSet.add(searchTerm + "~");
            searchTermSet.add(lemmatizer.lemmatize(searchTerm) + "~");
        }

        //get derivational words from WordNet and add all
        if (useWordNet) {
            Set<String> derivationalWords = wordNetAnalyzer.getDerivationalWords(searchTerm);

            searchTermSet.addAll(derivationalWords);
        }

        for (String term : searchTermSet) {

            //get all from MATOLL
            result.addAll(matollRetriever.getPredicates(term, (topK / 2), nameSpace));
            //add all from DBpedia
            result.addAll(dbpediaRetriever.getPredicates(term, (topK / 2), nameSpace));
        }

        return result;
    }

    /**
     * @return returns all resources that match the label using DBpedia
     * @param searchTerm
     * @param topK
     */
    public Set<String> getResourcesFromDBpedia(String searchTerm, int topK, boolean lemmatize, boolean useWordNet) {
        Set<String> result = new LinkedHashSet<>();

        Set<String> searchTermSet = new LinkedHashSet<>();
        searchTermSet.add(searchTerm);

        //lemmatize and add lemmatized version too
        if (lemmatize) {
            searchTermSet.add(lemmatizer.lemmatize(searchTerm));
            searchTermSet.add(searchTerm + "~");
            searchTermSet.add(lemmatizer.lemmatize(searchTerm) + "~");
        }

        //get derivational words from WordNet and add all
        if (useWordNet) {
            Set<String> derivationalWords = wordNetAnalyzer.getDerivationalWords(searchTerm);

            searchTermSet.addAll(derivationalWords);
        }

        for (String term : searchTermSet) {
            //get all from DBpedia
            for (String e1 : dbpediaRetriever.getResources(term, topK)) {
//                try {
                result.add(e1);
//                    result.add(URLDecoder.decode(e1, "UTF-8"));
//                } catch (UnsupportedEncodingException ex) {
//                    Logger.getLogger(Search.class.getName()).log(Level.SEVERE, null, ex);
//                }
            }
        }

        return result;
    }

    /**
     * @return returns all resources that match the label using DBpedia
     * @param searchTerm
     * @param topK
     */
    public Set<String> getResourcesFromAnchors(String searchTerm, int topK, boolean lemmatize, boolean useWordNet) {
        Set<String> result = new LinkedHashSet<>();

        Set<String> searchTermSet = new LinkedHashSet<>();
        searchTermSet.add(searchTerm);

        //lemmatize and add lemmatized version too
        if (lemmatize) {
            searchTermSet.add(lemmatizer.lemmatize(searchTerm));
            searchTermSet.add(searchTerm + "~");
            searchTermSet.add(lemmatizer.lemmatize(searchTerm) + "~");
        }

        //get derivational words from WordNet and add all
        if (useWordNet) {
            Set<String> derivationalWords = wordNetAnalyzer.getDerivationalWords(searchTerm);

            searchTermSet.addAll(derivationalWords);
        }

        for (String term : searchTermSet) {
            //get all from Anchor
            for (String e1 : anchorRetriever.getResources(term, topK)) {
                try {

                    result.add(URLDecoder.decode(e1, "UTF-8"));

                    int z = 1;
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(Search.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        return result;
    }

    /**
     * @return returns all resources that match the label using DBpedia and
     * Anchors
     * @param searchTerm
     * @param topK
     */
    public Set<String> getAllResources(String searchTerm, int topK, boolean lemmatize, boolean useWordNet) {
        Set<String> result = new LinkedHashSet<>();

        Set<String> searchTermSet = new LinkedHashSet<>();
        searchTermSet.add(searchTerm);

        //lemmatize and add lemmatized version too
        if (lemmatize) {
            searchTermSet.add(lemmatizer.lemmatize(searchTerm));
            searchTermSet.add(searchTerm + "~");
            searchTermSet.add(lemmatizer.lemmatize(searchTerm) + "~");
        }

        //get derivational words from WordNet and add all
        if (useWordNet) {
            Set<String> derivationalWords = wordNetAnalyzer.getDerivationalWords(searchTerm);

            searchTermSet.addAll(derivationalWords);
        }

        for (String term : searchTermSet) {
            for (String e1 : anchorRetriever.getResources(term, topK / 2)) {

                result.add(e1);

            }
            //add resources from DBpedia
            for (String e1 : dbpediaRetriever.getResources(term, topK / 2)) {
                result.add(e1);
            }
        }
        return result;
    }

    /**
     * @return returns all classes that match the searchTerm using DBpedia
     * @param searchTerm
     * @param topK
     */
    public Set<String> getClassesFromDBpedia(String searchTerm, int topK, boolean lemmatize, boolean useWordNet) {
        Set<String> result = new LinkedHashSet<>();

        Set<String> searchTermSet = new LinkedHashSet<>();
        searchTermSet.add(searchTerm);

        //lemmatize and add lemmatized version too
        if (lemmatize) {
            searchTermSet.add(lemmatizer.lemmatize(searchTerm));
            searchTermSet.add(searchTerm + "~");
            searchTermSet.add(lemmatizer.lemmatize(searchTerm) + "~");
        }

        //get derivational words from WordNet and add all
        if (useWordNet) {
            Set<String> derivationalWords = wordNetAnalyzer.getDerivationalWords(searchTerm);

            searchTermSet.addAll(derivationalWords);
        }

        for (String term : searchTermSet) {
            Set<String> r = dbpediaRetriever.getClasses(term, topK);

            r.forEach(e1 -> result.add("http://www.w3.org/1999/02/22-rdf-syntax-ns#type###" + e1));
        }

        return result;
    }

    /**
     * @return returns all classes that match the searchTerm using DBpedia
     * @param searchTerm
     * @param topK
     */
    public Set<String> getRestrictionClassesFromMATOLL(String searchTerm, int topK, boolean lemmatize, boolean useWordNet) {
        Set<String> result = new LinkedHashSet<>();

        Set<String> searchTermSet = new LinkedHashSet<>();
        searchTermSet.add(searchTerm);

        //lemmatize and add lemmatized version too
        if (lemmatize) {
            searchTermSet.add(lemmatizer.lemmatize(searchTerm));
            searchTermSet.add(searchTerm + "~");
            searchTermSet.add(lemmatizer.lemmatize(searchTerm) + "~");
        }

        //get derivational words from WordNet and add all
        if (useWordNet) {
            Set<String> derivationalWords = wordNetAnalyzer.getDerivationalWords(searchTerm);

            searchTermSet.addAll(derivationalWords);
        }

        for (String term : searchTermSet) {
            //get all from DBpedia
            Set<String> r = matollRetriever.getRestrictionClasses(term, topK);

            result.addAll(r);
        }

        return result;
    }
}
