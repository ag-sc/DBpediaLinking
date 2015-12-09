/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.sc.query;

import edu.stanford.nlp.util.ArraySet;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.RAMDirectory;

/**
 *
 * @author sherzod
 */
public class DBpediaRetriever extends LabelRetriever {

    private String predicatesIndexPath = "predicatesindex";
    private String instancesIndexPath = "instancesindex";
    private String directory;
    private StandardAnalyzer analyzer;
    private Directory predicateIndexDirectory;
    private Directory instanceIndexDirectory;

    public DBpediaRetriever(String directory, boolean loadIntoMemory) {
        this.directory = directory;
        initIndexDirectory(loadIntoMemory);

    }

    private void initIndexDirectory(boolean loadToMemory) {
        try {
            String predicatePath = directory + "/" + this.predicatesIndexPath + "/";
            String instancePath = directory + "/" + this.instancesIndexPath + "/";
            analyzer = new StandardAnalyzer();
            if (loadToMemory) {
                predicateIndexDirectory = new RAMDirectory(FSDirectory.open(Paths.get(predicatePath)), IOContext.DEFAULT);
                instanceIndexDirectory = new RAMDirectory(FSDirectory.open(Paths.get(instancePath)), IOContext.DEFAULT);
            } else {
                predicateIndexDirectory = FSDirectory.open(Paths.get(predicatePath));
                instanceIndexDirectory = FSDirectory.open(Paths.get(instancePath));
            }

        } catch (Exception e) {
            System.err.println("Problem with initializing InstanceQueryProcessor\n" + e.getMessage());
        }
    }

    /**
     * return predicates with namespace http://dbpedia.org/ontology/
     */
    public Set<String> getPredicates(String searchTerm, int k, String nameSpace) {

        //get the highest number of math
        Set<Instance> resultDirectMatches = getDirectMatches(searchTerm, "label", "URI", 10000, predicateIndexDirectory);
        Set<Instance> resultPartialMatches = getPartialMatches(searchTerm, "label", "URI", 10000, predicateIndexDirectory, analyzer);

        Set<String> predicates = new LinkedHashSet<>();

        resultDirectMatches.addAll(resultPartialMatches);

        //filter out classes, predicates with namespace "http:dbpedia,org/property"
        List<String> namespaces = new ArrayList<>();
        namespaces.add(nameSpace);

        for (Instance i1 : resultDirectMatches) {

            if (predicates.size() < k) {
                String firstLetter = "";
                String uri = i1.getUri();

                String n = uri.substring(0, uri.lastIndexOf("/") + 1);//rhttp://dbpedia.org/ontology/, http://dbpedia.org/property/

                //if any of the namespaces match
                if (namespaces.contains(n)) {
                    if (uri.replace(n, "").length() > 1) {
                        firstLetter = uri.replace(n, "").substring(0, 1);

                        //this part means that if the firstLetter is in lowercase it stands for predicates
                        //unlike classes which start with upperCase e.g. http://dbpedia.org/ontology/River
                        if (firstLetter.equals(firstLetter.toLowerCase())) {
                            if (!predicates.contains(uri)) {
                                predicates.add(uri);
                            }
                        }
                    }
                }
            } else {
                break;
            }

        }

        //predicates = sortBySimilarity(predicates, searchTerm, k);
        return predicates;
    }

    /**
     * return predicates with namespace http://dbpedia.org/ontology/
     */
    public Set<String> getAllPredicates(String searchTerm, int k) {

        //get the highest number of math
        Set<Instance> resultDirectMatches = getDirectMatches(searchTerm, "label", "URI", 10000, predicateIndexDirectory);
        Set<Instance> resultPartialMatches = getPartialMatches(searchTerm, "label", "URI", 10000, predicateIndexDirectory, analyzer);

        Set<String> predicates = new LinkedHashSet<>();

        resultDirectMatches.addAll(resultPartialMatches);

        //filter out classes, predicates with namespace "http:dbpedia,org/property"
        List<String> namespaces = new ArrayList<>();
        namespaces.add("http://dbpedia.org/ontology/");
        namespaces.add("http://dbpedia.org/property/");

        for (Instance i1 : resultDirectMatches) {

            if (predicates.size() < k) {
                String firstLetter = "";
                String uri = i1.getUri();

                String n = uri.substring(0, uri.lastIndexOf("/") + 1);//rhttp://dbpedia.org/ontology/, http://dbpedia.org/property/

                //if any of the namespaces match
                if (namespaces.contains(n)) {
                    if (uri.replace(n, "").length() > 1) {
                        firstLetter = uri.replace(n, "").substring(0, 1);

                        //this part means that if the firstLetter is in lowercase it stands for predicates
                        //unlike classes which start with upperCase e.g. http://dbpedia.org/ontology/River
                        if (firstLetter.equals(firstLetter.toLowerCase())) {
                            if (!predicates.contains(uri)) {
                                predicates.add(uri);
                            }
                        }
                    }
                }
            } else {
                break;
            }

        }

        //predicates = sortBySimilarity(predicates, searchTerm, k);
        return predicates;
    }

    /**
     * return classes with namespace http://dbpedia.org/ontology/
     */
    public Set<String> getClasses(String searchTerm, int k) {

        //get the highest number of math
        Set<Instance> resultDirectMatches = getDirectMatches(searchTerm, "label", "URI", 10000, predicateIndexDirectory);
        Set<Instance> resultPartialMatches = getPartialMatches(searchTerm, "label", "URI", 10000, predicateIndexDirectory, analyzer);
        //add partial ones to direct
        resultDirectMatches.addAll(resultPartialMatches);

        Set<String> predicates = new LinkedHashSet<>();

        //filter out classes, predicates with namespace "http:dbpedia,org/property"
        String nameSpace = "http://dbpedia.org/ontology/";
        for (Instance i1 : resultDirectMatches) {
            String firstLetter = "";
            String uri = i1.getUri();

            if (uri.contains(nameSpace)) {
                if (uri.replace(nameSpace, "").length() > 1) {
                    firstLetter = uri.replace(nameSpace, "").substring(0, 1);

                    //this part means that if the firstLetter is in lowercase it stands for predicates
                    //unlike classes which start with upperCase e.g. http://dbpedia.org/ontology/River
                    if (firstLetter.equals(firstLetter.toUpperCase())) {
                        if (!predicates.contains(uri)) {
                            predicates.add(uri);
                        }
                    }
                }
            }
        }

        //predicates = sortBySimilarity(predicates, searchTerm, k);
        return predicates;
    }

    /**
     * return resources with namespace http://dbpedia.org/ontology/
     */
    public Set<String> getResources(String searchTerm, int k) {

        Set<Instance> resultDirectMatch = getDirectMatches(searchTerm, "label", "URI", 1000, instanceIndexDirectory);
        Set<Instance> resultPartialMatch = getPartialMatches(searchTerm, "labelTokenized", "URI", 1000, instanceIndexDirectory, analyzer);

        //add partial matches to direct
        resultDirectMatch.addAll(resultPartialMatch);
        Set<String> resources = new LinkedHashSet<>();

        String nameSpace = "http://dbpedia.org/resource/";
        for (Instance i1 : resultDirectMatch) {

            if (resources.size() < k) {
                String uri = i1.getUri();

                if (uri.contains(nameSpace)) {
                    resources.add(uri);
                }
            } else {
                break;
            }
        }

        //resources = sortBySimilarity(resources, searchTerm, k);
        return resources;

    }

}
