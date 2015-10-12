/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.sc.lemmatizer;

import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author sherzod
 */
public class StanfordLemmatizer implements Lemmatizer{

    protected StanfordCoreNLP pipeline;

    public StanfordLemmatizer() {
    // Create StanfordCoreNLP object properties, with POS tagging
        // (required for lemmatization), and lemmatization
        Properties props;
        props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma");

    // StanfordCoreNLP loads a lot of models, so you probably
        // only want to do this once per execution
        this.pipeline = new StanfordCoreNLP(props);
    }

    public List<String> lemmatizeDocument(String documentText) {
        List<String> lemmas = new LinkedList<>();

        // create an empty Annotation just with the given text
        Annotation document = new Annotation(documentText);

        // run all Annotators on this text
        this.pipeline.annotate(document);

        // Iterate over all of the sentences found
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            // Iterate over all tokens in a sentence
            for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
            // Retrieve and add the lemma for each word into the
                // list of lemmas
                lemmas.add(token.get(LemmaAnnotation.class));
            }
        }

        return lemmas;
    }
    
    /**
     *
     * @param t
     * @return
     */
    @Override
    public String lemmatize(String t) {
        String lemma = "";
        // create an empty Annotation just with the given text
        Annotation document = new Annotation(t);

        // run all Annotators on this text
        this.pipeline.annotate(document);

        // Iterate over all of the sentences found
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            // Iterate over all tokens in a sentence
            for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
            // Retrieve and add the lemma for each word into the
                // list of lemmas
                lemma += " "+ token.get(LemmaAnnotation.class);
                
            }
        }

        return lemma.trim();
    }
    
    public static void main(String[] args){
        StanfordLemmatizer lemmatizer = new StanfordLemmatizer();
        
        String s = lemmatizer.lemmatize("countries");
        
        System.out.println(s);
        
    }


}
