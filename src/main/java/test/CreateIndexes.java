/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import de.citec.sc.loader.AnchorTextLoader;
import de.citec.sc.loader.DBpediaLoader;
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
        dbpediaLoader.load(true, "luceneIndex", "dbpediaFiles/");

//        MATOLLLoader matollLoader= new MATOLLLoader();
//        matollLoader.load(true, "matollIndex", "matollFiles/");
//        
//        AnchorTextLoader loadAnchors = new AnchorTextLoader();
//        loadAnchors.load(true, "anchorIndex", "anchorFiles/");

        System.out.println("DONE.");
    }

    public static HashMap<String, Set<String>> getRedirects(File file) {
        HashMap<String, Set<String>> content = new HashMap<>();

        try {
            FileInputStream fstream = new FileInputStream(file);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;

            while ((line = br.readLine()) != null) {

                if (!line.startsWith("#")) {
                    //System.out.println(line);
                    String[] a = line.split(" ");

                    String s = a[0];

                    String p = a[1];

                    String o = a[2];

                    s = s.replace("<", "");
                    s = s.replace(">", "");
                    p = p.replace("<", "");
                    p = p.replace(">", "");
                    o = o.replace("<", "");
                    o = o.replace(">", "");

                    if (content.containsKey(o)) {
                        content.get(o).add(s);
                    } else {
                        Set<String> r = new LinkedHashSet<>();
                        r.add(s);
                        content.put(o, r);
                    }
                }
            }
            in.close();
        } catch (Exception e) {
            System.err.println("Error reading the file: " + file.getPath() + "\n" + e.getMessage());
        }

        return content;
    }
}
