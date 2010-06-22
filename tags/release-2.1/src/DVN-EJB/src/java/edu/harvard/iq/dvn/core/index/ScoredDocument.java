/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.index;

import org.apache.lucene.document.Document;

/**
 *
 * @author roberttreacy
 */
public class ScoredDocument implements Comparable {
    private float score;
    private Document scoredDocument;
    
    public ScoredDocument (float score, Document doc){
        this.score = score;
        this.scoredDocument = doc;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public Document getScoredDocument() {
        return scoredDocument;
    }

    public void setScoredDocument(Document scoredDocument) {
        this.scoredDocument = scoredDocument;
    }

    public int compareTo(Object o) {
        int c = 0;
        ScoredDocument s = (ScoredDocument) o;
        if (!(this.score == s.getScore())){
            if (this.score > s.getScore()){
                c = -1;
            } else {
                c = 1;
            }
        }
        return c;
    }

}
