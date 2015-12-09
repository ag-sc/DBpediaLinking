/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.sc.query;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.RAMDirectory;

/**
 *
 * @author sherzod
 */
public class MATOLLRetriever extends LabelRetriever {

    private String indexPath = "index";
    private String directory;
    private StandardAnalyzer analyzer;
    private Directory indexDirectory;

    public MATOLLRetriever(String directory, boolean loadIntoMemory) {
        this.directory = directory;

        initIndexDirectory(loadIntoMemory);
    }

    private void initIndexDirectory(boolean loadToMemory) {
        try {
            String path = directory + "/" + this.indexPath + "/";
            analyzer = new StandardAnalyzer();
            if (loadToMemory) {
                indexDirectory = new RAMDirectory(FSDirectory.open(Paths.get(path)), IOContext.DEFAULT);
            } else {
                indexDirectory = FSDirectory.open(Paths.get(path));
            }

        } catch (Exception e) {
            System.err.println("Problem with initializing InstanceQueryProcessor\n" + e.getMessage());
        }
    }

    public Set<String> getPredicates(String searchTerm, int k, String nameSpace) {

        Set<Instance> result = getDirectMatches(searchTerm, "label", "URI", 10000, indexDirectory);

        List<Instance> filteredAndSorted = new ArrayList<>();

        for (Instance i1 : result) {
            String firstLetter = "";
            String uri = i1.getUri();

            if (uri.contains(nameSpace)) {
                if (uri.replace(nameSpace, "").length() > 1) {
                    firstLetter = uri.replace(nameSpace, "").substring(0, 1);

                    //this part means that if the firstLetter is in lowercase it stands for predicates
                    //unlike classes which start with upperCase e.g. http://dbpedia.org/ontology/River
                    if (firstLetter.equals(firstLetter.toLowerCase())) {
                        filteredAndSorted.add(i1);
                    }
                }
            }
        }

        Collections.sort(filteredAndSorted);

        if (filteredAndSorted.size() > k) {
            filteredAndSorted = filteredAndSorted.subList(0, k);
        }

        Set<String> predicates = new LinkedHashSet<>();

        for (Instance i1 : filteredAndSorted) {
            predicates.add(i1.getUri());
        }

        return predicates;
    }
    
    public Set<String> getAllPredicates(String searchTerm, int k) {

        Set<Instance> result = getDirectMatches(searchTerm, "label", "URI", 10000, indexDirectory);

        List<Instance> filteredAndSorted = new ArrayList<>();

        List<String> namespaces = new ArrayList<>();
        namespaces.add("http://dbpedia.org/ontology/");
        namespaces.add("http://dbpedia.org/property/");
        
        for (Instance i1 : result) {
            String firstLetter = "";
            String uri = i1.getUri();

            String n = uri.substring(0, uri.lastIndexOf("/")+1);//rhttp://dbpedia.org/ontology/, http://dbpedia.org/property/
            
            //if any of the namespaces match
            if (namespaces.contains(n)) {
                if (uri.replace(n, "").length() > 1) {
                    firstLetter = uri.replace(n, "").substring(0, 1);
                    
                    //this part means that if the firstLetter is in lowercase it stands for predicates
                    //unlike classes which start with upperCase e.g. http://dbpedia.org/ontology/River
                    if (firstLetter.equals(firstLetter.toLowerCase())) {
                        filteredAndSorted.add(i1);
                    }
                }
            }
        }
        

        Collections.sort(filteredAndSorted);

        if (filteredAndSorted.size() > k) {
            filteredAndSorted = filteredAndSorted.subList(0, k);
        }

        Set<String> predicates = new LinkedHashSet<>();

        for (Instance i1 : filteredAndSorted) {
            predicates.add(i1.getUri());
        }

        return predicates;
    }

    public Set<String> getRestrictionClasses(String searchTerm, int k) {

        Set<Instance> result = getDirectMatches(searchTerm, "label", "URI", 10000, indexDirectory);

        Set<String> classes = new LinkedHashSet<>();
        
        List<Instance> filteredAndSorted = new ArrayList<>();
        
        for (Instance c : result) {
            if (c.getPos().equals("http://www.lexinfo.net/ontology/2.0/lexinfo#adjective")) {
                if (!c.getOnProperty().equals("")) {
                    filteredAndSorted.add(c);
                }
            }
        }
        
        Collections.sort(filteredAndSorted);

        if (filteredAndSorted.size() > k) {
            filteredAndSorted = filteredAndSorted.subList(0, k);
        }

        for (Instance c : filteredAndSorted) {
            if (c.getPos().equals("http://www.lexinfo.net/ontology/2.0/lexinfo#adjective")) {
                if (!c.getOnProperty().equals("")) {
                    classes.add(c.getOnProperty() + "###" + c.getUri());
                }
            }
        }

        return classes;
    }

}
