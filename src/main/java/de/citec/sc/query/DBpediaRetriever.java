/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.sc.query;

import edu.stanford.nlp.util.ArraySet;
import java.io.IOException;
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

    private String ontologyPredicateIndexPath = "ontologyPredicateIndex";
    private String propertyPredicateIndexPath = "propertyPredicateIndex";
    private String classIndexPath = "classIndex";
    private String instancesIndexPath = "resourceIndex";
    private String directory;
    private StandardAnalyzer analyzer;

    private Directory ontologyPredicateIndexDirectory;
    private Directory propertyPredicateIndexDirectory;
    private Directory classIndexDirectory;
    private Directory instanceIndexDirectory;

    public DBpediaRetriever(String directory, boolean loadIntoMemory) {
        this.directory = directory;
        initIndexDirectory(loadIntoMemory);

    }

    private void initIndexDirectory(boolean loadToMemory) {
        try {
            String ontologyPredicatePath = directory + "/" + this.ontologyPredicateIndexPath + "/";
            String propertyPredicatePath = directory + "/" + this.propertyPredicateIndexPath + "/";
            String classPath = directory + "/" + this.classIndexPath + "/";
            String instancePath = directory + "/" + this.instancesIndexPath + "/";

            analyzer = new StandardAnalyzer();
            if (loadToMemory) {
                ontologyPredicateIndexDirectory = new RAMDirectory(FSDirectory.open(Paths.get(ontologyPredicatePath)), IOContext.DEFAULT);
                propertyPredicateIndexDirectory = new RAMDirectory(FSDirectory.open(Paths.get(propertyPredicatePath)), IOContext.DEFAULT);
                classIndexDirectory = new RAMDirectory(FSDirectory.open(Paths.get(classPath)), IOContext.DEFAULT);
                instanceIndexDirectory = new RAMDirectory(FSDirectory.open(Paths.get(instancePath)), IOContext.DEFAULT);
            } else {
                ontologyPredicateIndexDirectory = FSDirectory.open(Paths.get(ontologyPredicatePath));
                propertyPredicateIndexDirectory = FSDirectory.open(Paths.get(propertyPredicatePath));
                classIndexDirectory = FSDirectory.open(Paths.get(classPath));
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
        Directory searchDirectory = null;
        if (nameSpace.equals("http://dbpedia.org/ontology/")) {
            searchDirectory = ontologyPredicateIndexDirectory;
        }
        if (nameSpace.equals("http://dbpedia.org/property/")) {
            searchDirectory = propertyPredicateIndexDirectory;
        }

        Set<Instance> resultDirectMatches = getDirectMatches(searchTerm, "label", "URI", k, searchDirectory);
        //direct matches aren't enough search within tokenized part too
        if (resultDirectMatches.size() < k) {
            Set<Instance> resultPartialMatches = getPartialMatches(searchTerm, "labelTokenized", "URI", k - resultDirectMatches.size(), searchDirectory, analyzer);
            resultDirectMatches.addAll(resultPartialMatches);
        }

        Set<String> predicates = new LinkedHashSet<>();

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
     * return predicates with namespace http://dbpedia.org/ontology/ and http://dbpedia.org/property/
     */
    public Set<String> getAllPredicates(String searchTerm, int k) {

        //get 
        Directory searchDirectory = ontologyPredicateIndexDirectory;

        Set<Instance> resultDirectMatches = getDirectMatches(searchTerm, "label", "URI", k/2, searchDirectory);

        if (resultDirectMatches.size() < k/2) {
            Set<Instance> resultPartialMatch = getPartialMatches(searchTerm, "labelTokenized", "URI", k/2 - resultDirectMatches.size(), searchDirectory, analyzer);

            //add partial matches to direct
            resultDirectMatches.addAll(resultPartialMatch);
        }

        Set<String> predicates = new LinkedHashSet<>();
        resultDirectMatches.forEach(e1 -> predicates.add(e1.getUri()));
        
        
        //get property predicates
        resultDirectMatches.clear();
        searchDirectory = propertyPredicateIndexDirectory;

        resultDirectMatches = getDirectMatches(searchTerm, "label", "URI", k/2, searchDirectory);

        if (resultDirectMatches.size() < k/2) {
            Set<Instance> resultPartialMatch = getPartialMatches(searchTerm, "labelTokenized", "URI", k/2 - resultDirectMatches.size(), searchDirectory, analyzer);

            //add partial matches to direct
            resultDirectMatches.addAll(resultPartialMatch);
        }
        
        resultDirectMatches.forEach(e1 -> predicates.add(e1.getUri()));
        
        
        return predicates;
    }

    /**
     * return classes with namespace http://dbpedia.org/ontology/
     */
    public Set<String> getClasses(String searchTerm, int k) {

        //get the highest number of math
        Set<Instance> resultDirectMatches = getDirectMatches(searchTerm, "label", "URI", k, classIndexDirectory);

        if (resultDirectMatches.size() < k) {
            Set<Instance> resultPartialMatch = getPartialMatches(searchTerm, "labelTokenized", "URI", k - resultDirectMatches.size(), classIndexDirectory, analyzer);

            //add partial matches to direct
            resultDirectMatches.addAll(resultPartialMatch);
        }

        Set<String> predicates = new LinkedHashSet<>();

        resultDirectMatches.forEach(e1 -> predicates.add(e1.getUri()));

        //predicates = sortBySimilarity(predicates, searchTerm, k);
        return predicates;
    }

    /**
     * return resources with namespace http://dbpedia.org/ontology/
     */
    public Set<String> getResources(String searchTerm, int k) {

        super.comparator = super.pageRankComparator;

        Set<Instance> resultDirectMatch = getDirectMatches(searchTerm, "label", "URI", k, instanceIndexDirectory);

        if (resultDirectMatch.size() < k) {
            Set<Instance> resultPartialMatch = getPartialMatches(searchTerm, "labelTokenized", "URI", k - resultDirectMatch.size(), instanceIndexDirectory, analyzer);

            //add partial matches to direct
            resultDirectMatch.addAll(resultPartialMatch);
        }

        Set<String> resources = new LinkedHashSet<>();
        resultDirectMatch.forEach(i1 -> resources.add(i1.getUri()));

        //resources = sortBySimilarity(resources, searchTerm, k);
        return resources;

    }

}
