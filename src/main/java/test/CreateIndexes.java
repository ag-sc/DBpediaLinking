/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import de.citec.sc.loader.AnchorTextLoader;
import de.citec.sc.loader.DBpediaLoader;
import de.citec.sc.loader.DBpediaLoader2;
import de.citec.sc.loader.MATOLLLoader;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 * @author sherzod
 */
public class CreateIndexes {

    public static void main(String[] args) {

        System.out.println("Creating index files ...");
        
        DBpediaLoader dbpediaLoader = new DBpediaLoader();
        dbpediaLoader.load(true, "dbpediaIndex", "dbpediaFiles/");

        MATOLLLoader matollLoader= new MATOLLLoader();
        matollLoader.load(true, "matollIndex", "matollFiles/");
        
        AnchorTextLoader loadAnchors = new AnchorTextLoader();
        loadAnchors.load(true, "anchorIndex", "anchorFiles/");

        System.out.println("DONE.");
    }
}
