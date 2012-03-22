/*
   Copyright (C) 2005-2012, by the President and Fellows of Harvard College.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

   Dataverse Network - A web application to share, preserve and analyze research data.
   Developed at the Institute for Quantitative Social Science, Harvard University.
   Version 3.0.
*/
/*
 * StudyCollector.java
 * 
 * Created on Sep 12, 2007, 10:41:45 AM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.index;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Scorer;

/**
 *
 * @author roberttreacy
 */
//public class DocumentCollector extends HitCollector implements java.io.Serializable {
public class DocumentCollector extends Collector {

    private IndexSearcher searcher;
    private List  documents = new ArrayList<ScoreDoc>();
    private Scorer scorer;
    private int docBase;

    public DocumentCollector(IndexSearcher searcher) {
        this.searcher = searcher;
    }

    /*
    public void collect(int id, float score) {
        try {
            Document doc = searcher.doc(id);
            ScoredDocument scoredDocument = new ScoredDocument(score, doc);
            documents.add (scoredDocument);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
     */

    public List <ScoreDoc> getStudies() {
        return documents;
    }

    @Override
    public void setScorer(Scorer scorer) throws IOException {
        this.scorer = scorer;
    }

    @Override
    public void collect(int doc) throws IOException {
        documents.add(new ScoreDoc(doc+docBase,scorer.score()));
    }

    @Override
    public void setNextReader(IndexReader reader, int docBase) throws IOException {
        this.docBase = docBase;
    }

    @Override
    public boolean acceptsDocsOutOfOrder() {
        return false;
    }
}
