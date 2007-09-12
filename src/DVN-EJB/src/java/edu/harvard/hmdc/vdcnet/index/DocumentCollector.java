/*
 * StudyCollector.java
 * 
 * Created on Sep 12, 2007, 10:41:45 AM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.index;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.HitCollector;
import org.apache.lucene.search.IndexSearcher;

/**
 *
 * @author roberttreacy
 */
public class DocumentCollector extends HitCollector {

    private IndexSearcher searcher;
    private ArrayList  documents = new ArrayList();

    public DocumentCollector(IndexSearcher searcher) {
        this.searcher = searcher;
    }

    public void collect(int id, float score) {
        try {
            Document doc = searcher.doc(id);
            documents.add (doc);
        } catch (IOException e) {
// ignored
        }
    }

    public List getStudies() {
        return documents;
    }
}
