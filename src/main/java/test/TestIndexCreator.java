/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import de.citec.sc.loader.MATOLLLoader;



/**
 *
 * @author sherzod
 */
public class TestIndexCreator {

    public static void main(String[] args) {
        //DBpediaLoader loader = new DBpediaLoader();
        //loader.loadDBpedia(true, "luceneIndex", "dbpediaFiles/");
        
        MATOLLLoader loader= new MATOLLLoader();
        loader.load(true, "matollIndex", "matollFiles/");


        //AnchorTextLoader loadAnchors = new AnchorTextLoader();
        //loadAnchors.load(true, "anchorIndex", "anchorFiles/");
        
    }
}
