/*
 * Dataverse Network - A web application to distribute, share and analyze quantitative data.
 * Copyright (C) 2007
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses
 * or write to the Free Software Foundation,Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301 USA
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
public class DocumentCollector extends HitCollector implements java.io.Serializable {

    private IndexSearcher searcher;
    private ArrayList  documents = new ArrayList();

    public DocumentCollector(IndexSearcher searcher) {
        this.searcher = searcher;
    }

    public void collect(int id, float score) {
        try {
            Document doc = searcher.doc(id);
            ScoredDocument scoredDocument = new ScoredDocument(score, doc);
            documents.add (scoredDocument);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List getStudies() {
        Collections.sort(documents);
        return documents;
    }
}
