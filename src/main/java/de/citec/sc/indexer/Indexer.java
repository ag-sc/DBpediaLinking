/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.citec.sc.indexer;

import java.io.IOException;
import java.nio.file.Path;
import org.apache.lucene.index.IndexWriter;

/**
 *
 * @author sherzod
 * 
 * 
 */
public interface Indexer {
    public void initIndex(String folderPath);
    public void finilize();
}
