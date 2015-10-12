package de.citec.sc.distributedSimilarity;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class DistribSemClient {

    private static final String W2V_BASE_URL = "http://vmdgsit04.deri.ie/relatedness/w2v/en?";
    private static final String ESA_BASE_URL = "http://vmdgsit04.deri.ie/relatedness/esa/en?";
    private static final String GLOVE_BASE_URL = "http://vmdgsit04.deri.ie/relatedness/glove/en?";
    private static final String TERM = "term=";
    private static final String TARGETSET = "&targetSet=";

    public DistribSemClient() {

    }

    public HashMap<String, Double> getRelatedness(String term, Collection<String> targetSet, Double threshold,
            String method) {
        if (term == null) {
            return null;
        }
        if (targetSet == null || targetSet.isEmpty()) {
            return null;
        }
        if (threshold >= 1) {
            return null;
        }

        HashMap<String, Double> result = new HashMap<String, Double>();
        String fullUrl = "";
        ArrayList<String> encodedList = new ArrayList<>();

        try {
            term = URLEncoder.encode(term, "UTF-8");

            for (String t : targetSet) {
                t = URLEncoder.encode(t, "UTF-8");
                encodedList.add(t);
            }

        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        if (method.equals("w2v")) {
            fullUrl = W2V_BASE_URL + TERM + term + TARGETSET + String.join(";", encodedList);
        } else if (method.equals("esa")) {
            fullUrl = ESA_BASE_URL + TERM + term + TARGETSET + String.join(";", encodedList);
        } else {
            fullUrl = GLOVE_BASE_URL + TERM + term + TARGETSET + String.join(";", encodedList);
        }

        try {
            URL url = new URL(fullUrl);
            URLConnection conn = url.openConnection();
            InputStream stream = conn.getInputStream();

            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(new InputStreamReader(stream));

            for (String t : encodedList) {

                Double score = (Double) obj.get(t);
                if (score != null) {
                    if (score >= threshold) {
                        result.put(t, score);
                    }
                }
            }
            return result;

        } catch (IOException | ParseException e) {
            System.out.println("Exception caught of type " + e.getClass() + " the message " + e.getMessage());
            System.out.println(e.getStackTrace());
            return null;
        }
    }

    public static void main(String[] args) {
        DistribSemClient client = new DistribSemClient();
        String term = "product";
        // “faith” and a target set [“religion”, “car”, “church”, “table”]
        Set<String> targetSet = new HashSet<String>();
        targetSet.add("produce");
        targetSet.add("company");
        targetSet.add("pie");
        targetSet.add("car");

        HashMap<String, Double> result = client.getRelatedness(term, targetSet, 0.1, "w2v");
        for (String word : result.keySet()) {
            System.out.println(word + " " + result.get(word));
        }
    }
}
