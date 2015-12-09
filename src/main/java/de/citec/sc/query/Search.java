/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.citec.sc.query;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.LinkedHashSet;
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
    
    private boolean loadToMemory;
    
    public Search(boolean loadToMemory){
        this.loadToMemory = loadToMemory;
        
        this.anchorRetriever = new AnchorRetriever("anchorIndex", loadToMemory);
        this.dbpediaRetriever = new DBpediaRetriever("dbpediaIndex", loadToMemory);
        this.matollRetriever = new MATOLLRetriever("matollIndex", loadToMemory);        
    }
    
    public Set<String> getPredicatesFromDBpedia(String nameSpace, String searchTerm, int topK){
        Set<String> result = new LinkedHashSet<>();
        
        result = dbpediaRetriever.getPredicates(searchTerm, topK, nameSpace);
        
        return result;
    }
    
    public Set<String> getPredicatesFromMATOLL(String nameSpace, String searchTerm, int topK){
        Set<String> result = new LinkedHashSet<>();
        
        result = matollRetriever.getPredicates(searchTerm, topK, nameSpace);
        
        return result;
    }
    
    public Set<String> getAllPredicatesFromDBpedia(String searchTerm, int topK){
        Set<String> result = new LinkedHashSet<>();

        result = dbpediaRetriever.getAllPredicates(searchTerm, topK);
        
        return result;
    }
    
    public Set<String> getAllPredicatesFromMATOLL(String searchTerm, int topK){
        Set<String> result = new LinkedHashSet<>();

        result = matollRetriever.getAllPredicates(searchTerm, topK);
        
        return result;
    }
    
    /**
     * @return returns all predicates that match the label using DBpedia and MATOLL indexes
     * @param searchTerm
     * @param topK
     */
    public Set<String> getAllPredicates(String searchTerm, int topK){
        Set<String> result = new LinkedHashSet<>();

        //get all from MATOLL
        result = matollRetriever.getAllPredicates(searchTerm, topK);
        //add all from DBpedia
        result.addAll(dbpediaRetriever.getAllPredicates(searchTerm, topK));
        
        return result;
    }
    
    /**
     * @return returns all predicates with the specified "namespace" that match the label using DBpedia and MATOLL indexes
     * @param searchTerm
     * @param topK
     * @param nameSpace
     */
    public Set<String> getAllPredicates(String searchTerm, int topK, String nameSpace){
        Set<String> result = new LinkedHashSet<>();

        //get all from MATOLL
        result = matollRetriever.getPredicates(searchTerm, topK, nameSpace);
        //add all from DBpedia
        result.addAll(dbpediaRetriever.getPredicates(searchTerm, topK, nameSpace));
        
        return result;
    }
    
    /**
     * @return returns all resources that match the label using DBpedia
     * @param searchTerm
     * @param topK
     */
    public Set<String> getResourcesFromDBpedia(String searchTerm, int topK){
        Set<String> result = new LinkedHashSet<>();

        //get all from DBpedia
        for(String e1 : dbpediaRetriever.getResources(searchTerm, topK)){
            try {
                result.add(URLDecoder.decode(e1, "UTF-8"));
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(Search.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
        return result;
    }
    
    /**
     * @return returns all resources that match the label using DBpedia
     * @param searchTerm
     * @param topK
     */
    public Set<String> getResourcesFromAnchors(String searchTerm, int topK){
        Set<String> result = new LinkedHashSet<>();

        //get all from Anchor
        for(String e1 : anchorRetriever.getResources(searchTerm, topK)){
            try {
                result.add(URLDecoder.decode(e1, "UTF-8"));
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(Search.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
        return result;
    }
    
    /**
     * @return returns all resources that match the label using DBpedia and Anchors
     * @param searchTerm
     * @param topK
     */
    public Set<String> getAllResources(String searchTerm, int topK){
        Set<String> result = new LinkedHashSet<>();
        
        for(String e1 : anchorRetriever.getResources(searchTerm, topK/2)){
            try {
                result.add(URLDecoder.decode(e1, "UTF-8"));
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(Search.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //add resources from DBpedia
        for(String e1 : dbpediaRetriever.getResources(searchTerm, topK/2)){
            try {
                result.add(URLDecoder.decode(e1, "UTF-8"));
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(Search.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return result;
    }
    
    /**
     * @return returns all classes that match the searchTerm using DBpedia
     * @param searchTerm
     * @param topK
     */
    public Set<String> getClassesFromDBpedia(String searchTerm, int topK){
        Set<String> result = new LinkedHashSet<>();

        //get all from DBpedia
        result = dbpediaRetriever.getClasses(searchTerm, topK);
        
        return result;
    }
    
    /**
     * @return returns all classes that match the searchTerm using DBpedia
     * @param searchTerm
     * @param topK
     */
    public Set<String> getRestrictionClassesFromMATOLL(String searchTerm, int topK){
        Set<String> result = new LinkedHashSet<>();

        //get all from DBpedia
        result = matollRetriever.getRestrictionClasses(searchTerm, topK);
        
        return result;
    }
}
