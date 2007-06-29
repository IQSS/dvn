/*
 * Indexer.java
 *
 * Created on September 26, 2006, 9:01 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.index;
import edu.harvard.hmdc.vdcnet.study.DataTable;
import edu.harvard.hmdc.vdcnet.study.DataVariable;
import edu.harvard.hmdc.vdcnet.study.FileCategory;
import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.study.StudyAbstract;
import edu.harvard.hmdc.vdcnet.study.StudyAuthor;
import edu.harvard.hmdc.vdcnet.study.StudyDistributor;
import edu.harvard.hmdc.vdcnet.study.StudyFile;
import edu.harvard.hmdc.vdcnet.study.StudyKeyword;
import edu.harvard.hmdc.vdcnet.study.StudyOtherId;
import edu.harvard.hmdc.vdcnet.study.StudyOtherRef;
import edu.harvard.hmdc.vdcnet.study.StudyProducer;
import edu.harvard.hmdc.vdcnet.study.StudyRelMaterial;
import edu.harvard.hmdc.vdcnet.study.StudyRelPublication;
import edu.harvard.hmdc.vdcnet.study.StudyRelStudy;
import edu.harvard.hmdc.vdcnet.study.StudyTopicClass;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.StringTokenizer;
import lia.analysis.positional.PositionalPorterStopAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.RangeQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.document.Field;

/**
 *
 * @author roberttreacy
 */
public class Indexer {
    
    private static IndexWriter writer;
    private static IndexWriter writer2;
    private static IndexWriter writerStem;
    private static IndexReader reader;
    private static Indexer indexer;
    Directory dir;
    String indexDir = "index-dir";
    int dvnMaxClauseCount = 2048;

    
    
    /** Creates a new instance of Indexer */
    public Indexer() {
        String dvnMaxClauseCountStr = System.getProperty("dvn.search.maxclausecount");
        if (dvnMaxClauseCountStr != null){
            try {
                dvnMaxClauseCount = Integer.parseInt(dvnMaxClauseCountStr);
            } catch (Exception e){
                e.printStackTrace();
                dvnMaxClauseCount = 1024;
            }
        }
        try {
            dir = FSDirectory.getDirectory(indexDir,false);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    protected void setup() throws IOException {
        File indexDirectory = new File(indexDir);
        dir = FSDirectory.getDirectory(indexDir,!indexDirectory.exists());
    }
    
    public static Indexer getInstance(){
        if (indexer == null){
            indexer = new Indexer();
            try {
                indexer.setup();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return indexer;
    }
    
    public void deleteDocument(long studyId){
        try {
            IndexReader reader = IndexReader.open(dir);
            reader.deleteDocuments(new Term("id",Long.toString(studyId)));
            reader.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    protected void addDocument(Study study) throws IOException{
        Document doc = new Document();
        addText(doc,"title",study.getTitle());
        addKeyword(doc,"title",study.getTitle());
        addKeyword(doc,"id",study.getId().toString());
        addKeyword(doc,"studyId", study.getStudyId());
        addText(doc,"studyId", study.getStudyId());
        addKeyword(doc,"productionDate", study.getProductionDate());
        addKeyword(doc,"distributionDate", study.getDistributionDate());
        Collection <StudyKeyword> keywords = study.getStudyKeywords();
        for (Iterator it = keywords.iterator(); it.hasNext();) {
            StudyKeyword elem = (StudyKeyword) it.next();
            addText(doc,"keywordValue", elem.getValue());
        }
        Collection <StudyTopicClass> topicClassifications = study.getStudyTopicClasses();
        for (Iterator it = topicClassifications.iterator(); it.hasNext();) {
            StudyTopicClass elem = (StudyTopicClass) it.next();
            addText(doc,"topicClassValue", elem.getValue());
        }
        Collection <StudyAbstract> abstracts = study.getStudyAbstracts();
        for (Iterator it = abstracts.iterator(); it.hasNext();) {
            StudyAbstract elem = (StudyAbstract) it.next();
            addText(doc,"abstractText",elem.getText());
            addKeyword(doc,"abstractDate",elem.getDate());
            
        }
        Collection <StudyAuthor> studyAuthors = study.getStudyAuthors();
        for (Iterator it = studyAuthors.iterator(); it.hasNext();) {
            StudyAuthor elem = (StudyAuthor) it.next();
            addText(doc,"authorName",elem.getName());
            addKeyword(doc,"authorName",elem.getName());
            addText(doc,"authorAffiliation", elem.getAffiliation());
        }
        Collection <StudyProducer> studyProducers = study.getStudyProducers();
        for (Iterator itProducers = studyProducers.iterator(); itProducers.hasNext();) {
            StudyProducer studyProducer = (StudyProducer) itProducers.next();
            addText(doc,"producerName", studyProducer.getName());
            addText(doc,"producerName", studyProducer.getAbbreviation());
            addText(doc,"producerName", studyProducer.getLogo());
            addText(doc,"producerName", studyProducer.getUrl());
        }
        Collection <StudyDistributor> studyDistributors = study.getStudyDistributors();
        for (Iterator it = studyDistributors.iterator(); it.hasNext();) {
            StudyDistributor studyDistributor = (StudyDistributor) it.next();
            addText(doc,"distributorName", studyDistributor.getName());
            addText(doc,"distributorName", studyDistributor.getAbbreviation());
            addText(doc,"distributorName", studyDistributor.getLogo());
            addText(doc,"distributorName", studyDistributor.getUrl());
        }
        Collection <StudyOtherId> otherIds = study.getStudyOtherIds();
        for (Iterator it = otherIds.iterator(); it.hasNext();) {
            StudyOtherId elem = (StudyOtherId) it.next();
            addText(doc,"otherId", elem.getOtherId());
        }
        addText(doc,"fundingAgency",study.getFundingAgency());
        addText(doc,"distributorContact",study.getDistributorContact());
        addText(doc,"distributorContactAffiliation",study.getDistributorContactAffiliation());
        addText(doc,"distributorContactEmail",study.getDistributorContactEmail());
        addKeyword(doc,"dateOfDeposit",study.getDateOfDeposit());
        addText(doc,"depositor",study.getDepositor());
        addText(doc,"seriesName",study.getSeriesName());
        addText(doc,"seriesInformation",study.getSeriesInformation());
        addKeyword(doc,"studyVersion",study.getStudyVersion());
        addText(doc,"originOfSources",study.getOriginOfSources());
        addText(doc,"dataSources",study.getDataSources());
        addKeyword(doc,"frequencyOfDataCollection",study.getFrequencyOfDataCollection());
        addText(doc,"universe",study.getUniverse());
        addKeyword(doc,"unitOfAnalysis",study.getUnitOfAnalysis());
        addText(doc,"dataCollector",study.getDataCollector());
        addText(doc,"kindOfData", study.getKindOfData());
        addText(doc,"dataCollector",study.getDataCollector());
        addText(doc,"geographicCoverage",study.getGeographicCoverage());
        addText(doc,"geographicUnit",study.getGeographicUnit());
        addKeyword(doc,"timePeriodCoveredEnd",study.getTimePeriodCoveredEnd());
        addKeyword(doc,"timePeriodCoveredStart",study.getTimePeriodCoveredStart());
        addKeyword(doc,"dateOfCollection",study.getDateOfCollectionStart());
        addKeyword(doc,"dateOfCollectionEnd",study.getDateOfCollectionEnd());
        addKeyword(doc,"country",study.getCountry());
        addText(doc,"country",study.getCountry());
        addKeyword(doc,"timeMethod",study.getTimeMethod());
        addKeyword(doc,"samplingProcedure",study.getSamplingProcedure());
        addKeyword(doc,"deviationsFromSampleDesign",study.getDeviationsFromSampleDesign());
        addKeyword(doc,"collectionMode",study.getCollectionMode());
        addKeyword(doc,"researchInstrument",study.getResearchInstrument());
        addText(doc,"characteristicOfSources",study.getCharacteristicOfSources());
        addText(doc,"accessToSources",study.getAccessToSources());
        addText(doc,"dataCollectionSituation",study.getDataCollectionSituation());
        addText(doc,"actionsToMinimizeLoss",study.getActionsToMinimizeLoss());
        addText(doc,"controlOperations",study.getControlOperations());
        addText(doc,"weighting",study.getWeighting());
        addText(doc,"cleaningOperations",study.getCleaningOperations());
        addText(doc,"studyLevelErrorNotes",study.getStudyLevelErrorNotes());
        addKeyword(doc,"responseRate",study.getResponseRate());
        addKeyword(doc,"samplingErrorEstimate",study.getSamplingErrorEstimate());
        addText(doc,"otherDataAppraisal",study.getOtherDataAppraisal());
        addText(doc,"placeOfAccess",study.getPlaceOfAccess());
        addText(doc,"originalArchive",study.getOriginalArchive());
        addKeyword(doc,"availabilityStatus",study.getAvailabilityStatus());
        addKeyword(doc,"collectionSize",study.getCollectionSize());
        addKeyword(doc,"studyCompletion",study.getStudyCompletion());
        addText(doc,"confidentialityDeclaration",study.getConfidentialityDeclaration());
        addText(doc,"specialPermissions",study.getSpecialPermissions());
        addText(doc,"restrictions",study.getRestrictions());
        addText(doc,"contact",study.getContact());
        addText(doc,"citationRequirements",study.getCitationRequirements());
        addText(doc,"depositorRequirements",study.getDepositorRequirements());
        addText(doc,"conditions",study.getConditions());
        addText(doc,"disclaimer",study.getDisclaimer());
        List <StudyRelMaterial> relMaterials = study.getStudyRelMaterials();
        for (Iterator it = relMaterials.iterator(); it.hasNext();) {
            StudyRelMaterial elem = (StudyRelMaterial) it.next();
            addText(doc,"relatedMaterial",elem.getText());
        }
        List <StudyRelPublication> relPublications = study.getStudyRelPublications();
        for (Iterator it = relPublications.iterator(); it.hasNext();) {
            StudyRelPublication elem = (StudyRelPublication) it.next();
            addText(doc,"relatedPublications",elem.getText());
        }
        List <StudyRelStudy> relStudies = study.getStudyRelStudies();
        for (Iterator it = relStudies.iterator(); it.hasNext();) {
            StudyRelStudy elem = (StudyRelStudy) it.next();
            addText(doc,"relatedStudy",elem.getText());
        }
        List <StudyOtherRef> otherRefs = study.getStudyOtherRefs();
        for (Iterator it = otherRefs.iterator(); it.hasNext();) {
            StudyOtherRef elem = (StudyOtherRef) it.next();
            addText(doc,"otherReferences",elem.getText());
        }
       
   /*     addText(doc,"relatedMaterial",study.getRelatedMaterial());
        addText(doc,"relatedPublications",study.getRelatedPublications());
        addText(doc,"otherReferences",study.getOtherReferences());
    */
        addText(doc,"subtitle",study.getSubTitle());
        List <StudyKeyword> studyKeywords = study.getStudyKeywords();
        for (Iterator it = studyKeywords.iterator(); it.hasNext();) {
            StudyKeyword elem = (StudyKeyword) it.next();
            addText(doc,"keywordVocabulary",elem.getVocab());
        }
        List <StudyTopicClass> studyTopicClasses =study.getStudyTopicClasses();
        for (Iterator it = studyTopicClasses.iterator(); it.hasNext();) {
            StudyTopicClass elem = (StudyTopicClass) it.next();
            addText(doc,"topicClassVocabulary", elem.getVocab());            
        }
        addText(doc,"protocol",study.getProtocol());
        addText(doc,"authority",study.getAuthority());
        addText(doc,"globalId",study.getGlobalId());
        List <FileCategory> fileCategories = study.getFileCategories();
        writer = new IndexWriter(dir,getAnalyzer(),!(new File(indexDir+"/segments").exists()));    
        writer.setUseCompoundFile(true);
        for (int i = 0; i < fileCategories.size(); i++) {
            FileCategory fileCategory = fileCategories.get(i);
            Collection <StudyFile> studyFiles = fileCategory.getStudyFiles();
            for (Iterator it = studyFiles.iterator(); it.hasNext();) {
                StudyFile elem = (StudyFile) it.next();
                DataTable dataTable = elem.getDataTable();
                if (dataTable != null){
                    List <DataVariable> dataVariables = dataTable.getDataVariables();
                    for (int j = 0; j < dataVariables.size(); j++) {
                        Document docVariables = new Document();
                        addText(docVariables,"varStudyId",study.getId().toString());
                        addText(docVariables,"varStudyFileId",elem.getId().toString());
                        DataVariable dataVariable = dataVariables.get(j);
                        addText(docVariables,"id",dataVariable.getId().toString());
                        addText(docVariables,"varName",dataVariable.getName());
                        addText(docVariables,"varLabel",dataVariable.getLabel());
                        addText(docVariables,"varId",dataVariable.getId().toString());
                        writer.addDocument(docVariables);
                    }
                }
            }
        }
        
        writer.addDocument(doc);
        writer.close();
        writer2 = new IndexWriter(dir,new StandardAnalyzer(),!(new File(indexDir+"/segments").exists()));    
        writer2.setUseCompoundFile(true);
        writer2.addDocument(doc);
        writer2.close();
        writerStem = new IndexWriter(dir,new PositionalPorterStopAnalyzer(),!(new File(indexDir+"/segments").exists()));    
        writerStem.setUseCompoundFile(true);
        writerStem.addDocument(doc);
        writerStem.close();
    }
    
    
    protected Analyzer getAnalyzer(){
//        return new StandardAnalyzer();
        return new WhitespaceAnalyzer();
    }
    
    protected void addKeyword(Document doc,String key, String value){
        if (value != null && value.length()>0){
            doc.add(new Field(key,value.toLowerCase().trim(), Field.Store.YES, Field.Index.UN_TOKENIZED));
            doc.add(new Field(key,value.trim(), Field.Store.YES, Field.Index.UN_TOKENIZED));
        }
    }
    
    protected void addText(Document doc,String key, String value){
        if (value != null && value.length()>0){
            doc.add(new Field(key,value.toLowerCase().trim(),Field.Store.YES, Field.Index.TOKENIZED));
            doc.add(new Field(key,value.trim(), Field.Store.YES, Field.Index.UN_TOKENIZED));
        }      
    }
    
    protected void addUnstored(Document doc,String key, String value){
        if (value != null && value.length()>0){
            doc.add(new Field(key,value.toLowerCase().trim(), Field.Store.NO, Field.Index.TOKENIZED));
            doc.add(new Field(key,value.trim(), Field.Store.YES, Field.Index.UN_TOKENIZED));
        }     
    }
    
    protected void addUnindexed(Document doc,String key, String value){
        if (value != null && value.length()>0){
            doc.add(new Field(key,value.toLowerCase().trim(),Field.Store.YES, Field.Index.NO));
            doc.add(new Field(key,value.trim(), Field.Store.YES, Field.Index.UN_TOKENIZED));
        }      
    }
    
    public List search(List <Long> studyIds, List <SearchTerm> searchTerms) throws IOException{
        List <Long> results = null;
        List <BooleanQuery> searchParts = new ArrayList();
        boolean variableSearch = false;
        boolean nonVariableSearch = false;
        if (studyIds.size() > 0){
            List <SearchTerm> studyIdTerms = new ArrayList();
            for (Iterator it = studyIds.iterator(); it.hasNext();) {
                Long elem = (Long) it.next();
                SearchTerm t = new SearchTerm();
                t.setFieldName("id");
                t.setValue(elem.toString());
                studyIdTerms.add(t);
            }
            BooleanQuery studiesQuery = orClause(studyIdTerms);
            searchParts.add(studiesQuery);
        }
        List <SearchTerm> variableSearchTerms = new ArrayList();
        List <SearchTerm> nonVariableSearchTerms = new ArrayList();
        for (Iterator it = searchTerms.iterator(); it.hasNext();){
            SearchTerm elem = (SearchTerm) it.next();
            if (elem.getFieldName().equals("variable")){
                variableSearchTerms.add(elem);
                variableSearch = true;
            } else {
                nonVariableSearchTerms.add(elem);
                nonVariableSearch = true;
            }
        }
        List <Long> nvResults = null;
        if (nonVariableSearch){
            BooleanQuery searchTermsQuery = andSearchTermClause(nonVariableSearchTerms);
            searchParts.add(searchTermsQuery);
            BooleanQuery searchQuery = andQueryClause(searchParts);
            nvResults = getHitIds(searchQuery);
        }
        if (variableSearch){
            List <Long> vResults = searchVariables(studyIds,variableSearchTerms,false);
            if (nonVariableSearch){
                List <Long> mergeResults = new ArrayList();
                for (Iterator it = vResults.iterator(); it.hasNext();){
                    Long elem = (Long) it.next();
                    if (nvResults.contains(elem)){
                        mergeResults.add(elem);
                    }
                }
                results = searchVariables(mergeResults,variableSearchTerms,true);
            } else{
                results = searchVariables(vResults,variableSearchTerms,true);
            }
        } else {
            results = nvResults;
        }

        return results;
        
    }

    public List search(String query) throws IOException {
        String field = query.substring(0,query.indexOf("=")).trim();
        String value = query.substring(query.indexOf("=")+1).trim();
        ArrayList matchIds = new ArrayList();
        LinkedHashSet matchIdsSet = new LinkedHashSet();
        String[] phrase = getPhrase(value);
        
        
        String indexDir = "index-dir";
        Directory searchdir = FSDirectory.getDirectory(indexDir,false);
        IndexSearcher searcher = new IndexSearcher(searchdir);
        Hits hits = exactMatchQuery(searcher, field, value);
//        Hits hits = partialMatch(searcher, field, value);
        for (int i = 0; i < hits.length(); i++) {
            Document d = hits.doc(i);
            Field studyId = d.getField("id");
            String studyIdStr = studyId.stringValue();
            Long studyIdLong = Long.getLong(studyIdStr);
            matchIdsSet.add(studyIdLong);
        }
        matchIds.addAll(matchIdsSet);
        searcher.close();
         
        return matchIds;
    }

    public List query(String adhocQuery) throws IOException {
//        QueryParser parser = new QueryParser("abstract",getAnalyzer());
        QueryParser parser = new QueryParser("abstract",new StandardAnalyzer());
//        Hits hits = null;
//        ArrayList matchIds = new ArrayList();
        Query query=null;
        try {
            query = parser.parse(adhocQuery);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return getHitIds(query);
    }
        
    private String[] getPhrase(final String value) {
        StringTokenizer tk = new StringTokenizer(value);
        String[] phrase = new String[tk.countTokens()];
        for (int i = 0; i < phrase.length; i++) {
            phrase[i] = tk.nextToken();
        }
        return phrase;
    }
    
    public List search(SearchTerm searchTerm) throws IOException {
        Query indexQuery = null;
        if (searchTerm.getFieldName().equalsIgnoreCase("any")){
            indexQuery = buildAnyQuery(searchTerm.getValue().toLowerCase().trim());
        }else{
            Term t = new Term(searchTerm.getFieldName(),searchTerm.getValue().toLowerCase().trim());
            indexQuery = new TermQuery(t);
        }
        return getHitIds(indexQuery);
    }
    
    public List searchVariables(SearchTerm searchTerm) throws IOException {
        Query indexQuery = null;        
        if (searchTerm.getFieldName().equalsIgnoreCase("variable")){
            indexQuery = buildVariableQuery(searchTerm.getValue().toLowerCase().trim());
        }
        return getHitIds(indexQuery);
    }
    
    public List searchVariables(List <Long> studyIds,SearchTerm searchTerm) throws IOException {
        List <BooleanQuery> searchParts = new ArrayList();
        if (studyIds.size() > 0){
            List <SearchTerm> studyIdTerms = new ArrayList();
            for (Iterator it = studyIds.iterator(); it.hasNext();) {
                Long elem = (Long) it.next();
                SearchTerm t = new SearchTerm();
                t.setFieldName("varStudyId");
                t.setValue(elem.toString());
                studyIdTerms.add(t);
            }
            BooleanQuery studiesQuery = orClause(studyIdTerms);
            searchParts.add(studiesQuery);
        }
//        return getHitIds(searchQuery);
        BooleanQuery indexQuery = null;        
        if (searchTerm.getFieldName().equalsIgnoreCase("variable")){
            indexQuery = buildVariableQuery(searchTerm.getValue().toLowerCase().trim());
            searchParts.add(indexQuery);
        }
        BooleanQuery searchQuery = andQueryClause(searchParts);
//        return getHitIds(indexQuery);
        return getHitIds(searchQuery);
    }
    
    public List searchVariables(List <Long> studyIds, List <SearchTerm> searchTerms, boolean varIdReturnValues) throws IOException {
        List <Long> returnValues = null;
        List <BooleanQuery> searchParts = new ArrayList();
        if (studyIds.size() > 0){
            List <SearchTerm> studyIdTerms = new ArrayList();
            for (Iterator it = studyIds.iterator(); it.hasNext();) {
                Long elem = (Long) it.next();
                SearchTerm t = new SearchTerm();
                t.setFieldName("varStudyId");
                t.setValue(elem.toString());
                studyIdTerms.add(t);
            }
            BooleanQuery studiesQuery = orClause(studyIdTerms);
            searchParts.add(studiesQuery);
        }
        for (Iterator it = searchTerms.iterator(); it.hasNext();){
            SearchTerm elem = (SearchTerm) it.next();
            BooleanQuery indexQuery = null;
            if (elem.getFieldName().equalsIgnoreCase("variable")){
                indexQuery = buildVariableQuery(elem.getValue().toLowerCase().trim());
                searchParts.add(indexQuery);
            }
        }
        BooleanQuery searchQuery = andQueryClause(searchParts);
        if (varIdReturnValues){
            returnValues = getHitIds(searchQuery);
        } else {
            returnValues = getVarHitIds(searchQuery);
        }
        return returnValues;
    }
    
    public List searchBetween(Term begin,Term end, boolean inclusive) throws IOException{
        RangeQuery query = new RangeQuery(begin,end,inclusive);
        return getHitIds(query);
    }
    
    private List getHitIds( Query query) throws IOException {
        ArrayList matchIds = new ArrayList();
        LinkedHashSet matchIdsSet = new LinkedHashSet();
        if (query != null){
            IndexSearcher searcher = new IndexSearcher(dir);
            Hits hits = searcher.search(query);
            for (int i = 0; i < hits.length(); i++) {
                Document d = hits.doc(i);
                Field studyId = d.getField("id");
                String studyIdStr = studyId.stringValue();
                Long studyIdLong = Long.valueOf(studyIdStr);
                matchIdsSet.add(studyIdLong);
            }
            searcher.close();
        }
        matchIds.addAll(matchIdsSet);
        return matchIds;
    }
    
    /* returns studyIds for variable query
     */
    private List getVarHitIds( Query query) throws IOException {
        ArrayList matchIds = new ArrayList();
        LinkedHashSet matchIdsSet = new LinkedHashSet();
        if (query != null){
            IndexSearcher searcher = new IndexSearcher(dir);
            Hits hits = searcher.search(query);
            for (int i = 0; i < hits.length(); i++) {
                Document d = hits.doc(i);
                Field studyId = d.getField("varStudyId");
                String studyIdStr = studyId.stringValue();
                Long studyIdLong = Long.valueOf(studyIdStr);
                matchIdsSet.add(studyIdLong);
            }
            searcher.close();
        }
        matchIds.addAll(matchIdsSet);
        return matchIds;
    }

    /* phraseQuery supports partial match, if slop == 0, phrase must match in exact order
     */
    
    private Hits partialMatchQuery(IndexSearcher searcher, String field, String[] phrase, int slop) throws IOException{
        PhraseQuery query = new PhraseQuery();
        query.setSlop(slop);
        
        for (int i=0; i < phrase.length; i++) {
            query.add(new Term(field, phrase[i].toLowerCase().trim()));
        }
        
        return searcher.search(query);
    }
    
    BooleanClause partialMatchAndClause(String field, String value, int slop){
        String[] phrase = getPhrase(value);
        PhraseQuery query = new PhraseQuery();
        query.setSlop(slop);
        for (int i = 0; i < phrase.length; i++) {
            query.add(new Term(field, phrase[i].toLowerCase().trim()));
        }
        return new BooleanClause(query, BooleanClause.Occur.MUST);
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
    
    private Hits exactMatchQuery(IndexSearcher searcher, String field, String value) throws IOException{
        Term t = new Term(field,value.toLowerCase().trim());
        TermQuery indexQuery = new TermQuery(t);
        return searcher.search(indexQuery);
    }
    
    BooleanClause exactMatchClause(SearchTerm s){
        Term t = new Term(s.getFieldName(),s.getValue().toLowerCase().trim());
        TermQuery query = new TermQuery(t);
        BooleanClause exactMatchClause = null;
        if (s.getOperator().equalsIgnoreCase("=")){
            exactMatchClause = new BooleanClause(query, BooleanClause.Occur.MUST);            
        }
        else if (s.getOperator().equalsIgnoreCase("-")){
            exactMatchClause = new BooleanClause(query, BooleanClause.Occur.MUST_NOT);
        }
        return exactMatchClause;
    }
    
    BooleanQuery orClause(List <SearchTerm> orSearchTerms){
        BooleanQuery orTerms = new BooleanQuery();
        orTerms.setMaxClauseCount(dvnMaxClauseCount);
        for (Iterator it = orSearchTerms.iterator(); it.hasNext();) {
            SearchTerm elem = (SearchTerm) it.next();
            Term t = new Term(elem.getFieldName(), elem.getValue().toLowerCase().trim());
            TermQuery orQuery = new TermQuery(t);
            orTerms.add(orQuery,BooleanClause.Occur.SHOULD);
            
        }
        return orTerms;
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

    BooleanQuery andSearchTermClause(List <SearchTerm> andSearchTerms){
        BooleanQuery andTerms = new BooleanQuery();
        andTerms.setMaxClauseCount(dvnMaxClauseCount);
//        boolean required;
//        boolean prohibited;
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
        anyTerms.add(buildAnyTerm("distributorContact",string));
        anyTerms.add(buildAnyTerm("distributorContactAffiliation",string));
        anyTerms.add(buildAnyTerm("distributorContactEmail",string));
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
        anyTerms.add(buildAnyTerm("responseRate",string));
        anyTerms.add(buildAnyTerm("samplingErrorEstimate",string));
        anyTerms.add(buildAnyTerm("otherDataAppraisal",string));
        anyTerms.add(buildAnyTerm("placeOfAccess",string));
        anyTerms.add(buildAnyTerm("originalArchive",string));
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
    
    private BooleanQuery buildVariableQuery(String string) {
        List <SearchTerm> variableTerms = new ArrayList();
        variableTerms.add(buildAnyTerm("varName",string));
        variableTerms.add(buildAnyTerm("varLabel",string));
        return orPhraseQuery(variableTerms);        
    }

    SearchTerm buildAnyTerm(String fieldName,String value){
        SearchTerm term = new SearchTerm();
        term.setOperator("=");
        term.setFieldName(fieldName);
        term.setValue(value.toLowerCase().trim());
        return term;
    }
            
}
