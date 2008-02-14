/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web.z3950;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.RangeQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 *
 * @author roberttreacy
 */
public class LuceneSearch {
    private static LuceneSearch indexer;
    private static IndexSearcher searcher;
    Directory dir;
    private static IndexReader r;
    String indexDir = "/usr/local/lucene/index-dir";
    int dvnMaxClauseCount = 4096;

    public LuceneSearch() {
        String dvnIndexLocation = System.getProperty("dvn.index.location");
//        System.out.println("INDEX LOCATION " + dvnIndexLocation);
        File locationDirectory = null;
        if (dvnIndexLocation != null) {
            locationDirectory = new File(dvnIndexLocation);
            if (locationDirectory.exists() && locationDirectory.isDirectory()) {
                indexDir = dvnIndexLocation + "/index-dir";
//                System.out.println("INDEX " + indexDir);
            }
        }
//        System.out.println("INDEX DEFAULT " + indexDir);
        String dvnMaxClauseCountStr = System.getProperty("dvn.search.maxclausecount");
        if (dvnMaxClauseCountStr != null) {
            try {
                dvnMaxClauseCount = Integer.parseInt(dvnMaxClauseCountStr);
            } catch (Exception e) {
                e.printStackTrace();
                dvnMaxClauseCount = 1024;
            }
        }
        try {
            dir = FSDirectory.getDirectory(indexDir, false);
            r = IndexReader.open(dir);
            searcher = new IndexSearcher(r);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    protected void setup() throws IOException {
        File indexDirectory = new File(indexDir);
        dir = FSDirectory.getDirectory(indexDir,!indexDirectory.exists());
    }

    public static LuceneSearch getInstance(){
        if (indexer == null){
            indexer = new LuceneSearch();
            try {
                indexer.setup();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return indexer;
    }
    public List search( List <SearchTerm> searchTerms) throws IOException{
        List <String> results = null;
        List <BooleanQuery> searchParts = new ArrayList();
        List <SearchTerm> variableSearchTerms = new ArrayList();
        List<SearchTerm> nonVariableSearchTerms = new ArrayList();
        for (Iterator it = searchTerms.iterator(); it.hasNext();) {
            SearchTerm elem = (SearchTerm) it.next();
            nonVariableSearchTerms.add(elem);
        }

        BooleanQuery searchTermsQuery = andSearchTermClause(nonVariableSearchTerms);
        searchParts.add(searchTermsQuery);
        BooleanQuery searchQuery = andQueryClause(searchParts);
        results = getHitIds(searchQuery);

        return results;
        
    }

    private List getHitIds( Query query) throws IOException {
        ArrayList matchIds = new ArrayList();
        LinkedHashSet matchIdsSet = new LinkedHashSet();
        if (query != null){
            if (r != null) {
                if (!r.isCurrent()) {
                    while(r.isLocked(dir));
                    r = IndexReader.open(dir);
                    searcher = new IndexSearcher(r);
                }
            } else {
                r = IndexReader.open(dir);
                searcher = new IndexSearcher(r);
            }
            DocumentCollector s = new DocumentCollector(searcher);
            searcher.search(query, s);
//            searcher.search(query);
            searcher.close();
            List hits = s.getStudies();
            for (int i = 0; i < hits.size(); i++) {
                Document d = (Document) hits.get(i);
                Field authority = d.getField("authority");
                String authorityStr = authority.stringValue();
                Field studyId = d.getField("studyId");
                String studyIdStr = studyId.stringValue();
                /*
                if (studyIdStr.length() < 5) {
                    StringBuffer pad = new StringBuffer();
                    for (int j = studyIdStr.length(); j < 5; j++) {
                        pad.append('0');
                    }
                    studyIdStr = pad.toString() + studyIdStr;

                }
                 */
//                Long studyIdLong = Long.valueOf(studyIdStr);
                String fileName = authorityStr + File.separator + studyIdStr;
//                System.out.println(fileName);
                matchIdsSet.add(fileName);
            }
            searcher.close();
        }
        matchIds.addAll(matchIdsSet);
        return matchIds;
    } 
    
    BooleanQuery andSearchTermClause(List <SearchTerm> andSearchTerms){
        BooleanQuery andTerms = new BooleanQuery();
        andTerms.setMaxClauseCount(dvnMaxClauseCount);
        Query rQuery=null;
        for (Iterator it = andSearchTerms.iterator(); it.hasNext();) {
            SearchTerm elem = (SearchTerm) it.next();
            if (elem.getOperator().equals("<")) {
                Term end = new Term(elem.getFieldName(),elem.getValue().toLowerCase().trim());
                Term begin = null;
                rQuery = new RangeQuery(begin,end,true);
                andTerms.add(rQuery, BooleanClause.Occur.MUST); 
            }
            else if ( elem.getOperator().equals(">")){
                Term end = null;
                Term begin = new Term(elem.getFieldName(),elem.getValue().toLowerCase().trim());
                rQuery = new RangeQuery(begin,end,true);
                andTerms.add(rQuery, BooleanClause.Occur.MUST); 
            }
            else if (elem.getFieldName().equalsIgnoreCase("any")){
                andTerms = buildAnyQuery(elem.getValue().toLowerCase().trim());
            } else {
                String [] phrase = getPhrase( elem.getValue().toLowerCase().trim());
                if (phrase.length > 1){
                    PhraseQuery phraseQuery = new PhraseQuery();
                    phraseQuery.setSlop(0);
                    andTerms.add(partialMatch(elem,3));
                } else{
                    Term t = new Term(elem.getFieldName(), elem.getValue().toLowerCase().trim());
                    TermQuery andQuery = new TermQuery(t);
                    if (elem.getOperator().equals("=")){
                        andTerms.add(andQuery, BooleanClause.Occur.MUST);
                    } else if (elem.getOperator().equalsIgnoreCase("-")){
                        andTerms.add(andQuery, BooleanClause.Occur.MUST_NOT);
                    }
                }
            }
            
        }
        return andTerms;
    }
    
    BooleanClause partialMatch(SearchTerm s, int slop){
        String[] phrase = getPhrase(s.getValue().toLowerCase().trim());
        PhraseQuery query = new PhraseQuery();
        BooleanClause partialMatchClause = null;
        query.setSlop(slop);
        for (int i = 0; i < phrase.length; i++) {
            query.add(new Term(s.getFieldName(), phrase[i].toLowerCase().trim()));
        }
        if (s.getOperator().equalsIgnoreCase("=")){
            partialMatchClause = new BooleanClause(query, BooleanClause.Occur.MUST);            
        }
        else if (s.getOperator().equalsIgnoreCase("-")){
            partialMatchClause = new BooleanClause(query, BooleanClause.Occur.MUST_NOT);
        }
        return partialMatchClause;
    }
    
    SearchTerm buildAnyTerm(String fieldName,String value){
        SearchTerm term = new SearchTerm();
        term.setOperator("=");
        term.setFieldName(fieldName);
        term.setValue(value.toLowerCase().trim());
        return term;
    }
    
    BooleanQuery andQueryClause(List <BooleanQuery> andQueries){
        BooleanQuery andTerms = new BooleanQuery();
        andTerms.setMaxClauseCount(dvnMaxClauseCount);
        for (Iterator it = andQueries.iterator(); it.hasNext();) {
            BooleanQuery elem = (BooleanQuery) it.next();
            BooleanClause clause = new BooleanClause(elem, BooleanClause.Occur.MUST);
            andTerms.add(clause);
        }
        return andTerms;
    }
    
    private BooleanQuery buildAnyQuery(String string) {
        List <SearchTerm> anyTerms = new ArrayList();
        anyTerms.add(buildAnyTerm("title",string));
        anyTerms.add(buildAnyTerm("studyId", string));
        anyTerms.add(buildAnyTerm("abstractText",string));
        anyTerms.add(buildAnyTerm("abstractDate",string));
        anyTerms.add(buildAnyTerm("authorName",string));
        anyTerms.add(buildAnyTerm("authorAffiliation", string));
        anyTerms.add(buildAnyTerm("fundingAgency",string));
        anyTerms.add(buildAnyTerm("producerName",string));
        anyTerms.add(buildAnyTerm("distributorName",string));
        anyTerms.add(buildAnyTerm("distributorContact",string));
        anyTerms.add(buildAnyTerm("distributorContactAffiliation",string));
        anyTerms.add(buildAnyTerm("distributorContactEmail",string));
        anyTerms.add(buildAnyTerm("productionDate",string));
        anyTerms.add(buildAnyTerm("distributionDate",string));
        anyTerms.add(buildAnyTerm("dateOfDeposit",string));
        anyTerms.add(buildAnyTerm("depositor",string));
        anyTerms.add(buildAnyTerm("seriesName",string));
        anyTerms.add(buildAnyTerm("seriesInformation",string));
        anyTerms.add(buildAnyTerm("studyVersion",string));
        anyTerms.add(buildAnyTerm("originOfSources",string));
        anyTerms.add(buildAnyTerm("dataSources",string));
        anyTerms.add(buildAnyTerm("frequencyOfDataCollection",string));
        anyTerms.add(buildAnyTerm("universe",string));
        anyTerms.add(buildAnyTerm("unitOfAnalysis",string));
        anyTerms.add(buildAnyTerm("dataCollector",string));
        anyTerms.add(buildAnyTerm("kindOfData", string));
        anyTerms.add(buildAnyTerm("dataCollector",string));
        anyTerms.add(buildAnyTerm("timePeriodCoveredEnd",string));
        anyTerms.add(buildAnyTerm("timePeriodCoveredStart",string));
        anyTerms.add(buildAnyTerm("dateOfCollection",string));
        anyTerms.add(buildAnyTerm("dateOfCollectionEnd",string));
        anyTerms.add(buildAnyTerm("country",string));
        anyTerms.add(buildAnyTerm("timeMethod",string));
        anyTerms.add(buildAnyTerm("samplingProcedure",string));
        anyTerms.add(buildAnyTerm("deviationsFromSampleDesign",string));
        anyTerms.add(buildAnyTerm("collectionMode",string));
        anyTerms.add(buildAnyTerm("researchInstrument",string));
        anyTerms.add(buildAnyTerm("characteristicOfSources",string));
        anyTerms.add(buildAnyTerm("accessToSources",string));
        anyTerms.add(buildAnyTerm("dataCollectionSituation",string));
        anyTerms.add(buildAnyTerm("actionsToMinimizeLoss",string));
        anyTerms.add(buildAnyTerm("controlOperations",string));
        anyTerms.add(buildAnyTerm("weighting",string));
        anyTerms.add(buildAnyTerm("cleaningOperations",string));
        anyTerms.add(buildAnyTerm("studyLevelErrorNotes",string));
        anyTerms.add(buildAnyTerm("studyNoteType",string));
        anyTerms.add(buildAnyTerm("studyNoteSubject",string));
        anyTerms.add(buildAnyTerm("studyNoteText",string));
        anyTerms.add(buildAnyTerm("responseRate",string));
        anyTerms.add(buildAnyTerm("samplingErrorEstimate",string));
        anyTerms.add(buildAnyTerm("otherDataAppraisal",string));
        anyTerms.add(buildAnyTerm("placeOfAccess",string));
        anyTerms.add(buildAnyTerm("originalArchive",string));
        anyTerms.add(buildAnyTerm("geographicCoverage",string));
        anyTerms.add(buildAnyTerm("geographicUnit",string));
        anyTerms.add(buildAnyTerm("availabilityStatus",string));
        anyTerms.add(buildAnyTerm("collectionSize",string));
        anyTerms.add(buildAnyTerm("studyCompletion",string));
        anyTerms.add(buildAnyTerm("confidentialityDeclaration",string));
        anyTerms.add(buildAnyTerm("specialPermissions",string));
        anyTerms.add(buildAnyTerm("restrictions",string));
        anyTerms.add(buildAnyTerm("contact",string));
        anyTerms.add(buildAnyTerm("citationRequirements",string));
        anyTerms.add(buildAnyTerm("depositorRequirements",string));
        anyTerms.add(buildAnyTerm("conditions",string));
        anyTerms.add(buildAnyTerm("disclaimer",string));
        anyTerms.add(buildAnyTerm("relatedMaterial",string));
        anyTerms.add(buildAnyTerm("relatedPublications",string));
        anyTerms.add(buildAnyTerm("relatedStudy",string));
        anyTerms.add(buildAnyTerm("otherReferences",string));
        anyTerms.add(buildAnyTerm("subtitle",string));
        anyTerms.add(buildAnyTerm("keywordVocabulary",string));
        anyTerms.add(buildAnyTerm("topicClassVocabulary",string));
        anyTerms.add(buildAnyTerm("keywordValue",string));
        anyTerms.add(buildAnyTerm("topicClassValue",string));
        anyTerms.add(buildAnyTerm("protocol",string));
        anyTerms.add(buildAnyTerm("authority",string));
        anyTerms.add(buildAnyTerm("globalId",string));
        anyTerms.add(buildAnyTerm("otherId",string));
        
        return orPhraseQuery(anyTerms);
    }
    
    BooleanQuery orPhraseQuery(List <SearchTerm> orSearchTerms){
        BooleanQuery orTerms = new BooleanQuery();
        orTerms.setMaxClauseCount(dvnMaxClauseCount);
        for (Iterator it = orSearchTerms.iterator(); it.hasNext();) {
            SearchTerm elem = (SearchTerm) it.next();
            String [] phrase = getPhrase( elem.getValue().toLowerCase().trim());
            if (phrase.length > 1){
                BooleanClause partialMatchClause = null;
                PhraseQuery phraseQuery = new PhraseQuery();
                phraseQuery.setSlop(3);
                
                for (int i=0; i < phrase.length;i++){
                    phraseQuery.add(new Term(elem.getFieldName(),phrase[i].toLowerCase().trim()));
                }
                orTerms.add(phraseQuery,BooleanClause.Occur.SHOULD);
            } else{
                Term t = new Term(elem.getFieldName(), elem.getValue().toLowerCase().trim());
                TermQuery orQuery = new TermQuery(t);
                orTerms.add(orQuery,BooleanClause.Occur.SHOULD);
            }
        }
        return orTerms;
    }

    private String[] getPhrase(final String value) {
        StringTokenizer tk = new StringTokenizer(value);
        String[] phrase = new String[tk.countTokens()];
        for (int i = 0; i < phrase.length; i++) {
            phrase[i] = tk.nextToken();
        }
        return phrase;
    }
    
}
