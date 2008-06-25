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
import edu.harvard.hmdc.vdcnet.study.StudyNote;
import edu.harvard.hmdc.vdcnet.study.StudyOtherId;
import edu.harvard.hmdc.vdcnet.study.StudyOtherRef;
import edu.harvard.hmdc.vdcnet.study.StudyProducer;
import edu.harvard.hmdc.vdcnet.study.StudyRelMaterial;
import edu.harvard.hmdc.vdcnet.study.StudyRelPublication;
import edu.harvard.hmdc.vdcnet.study.StudyRelStudy;
import edu.harvard.hmdc.vdcnet.study.StudyTopicClass;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseTokenizer;
import org.apache.lucene.analysis.PorterStemFilter;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.DateTools.Resolution;
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
public class Indexer implements java.io.Serializable  {
    
    private static final Logger logger = Logger.getLogger("edu.harvard.hmdc.vdcnet.index.Indexer");
    private static IndexWriter writer;
    private static IndexWriter writer2;
    private static IndexWriter writerStem;
    private static IndexWriter writerVar;
    private static IndexReader reader;
    private static IndexReader r;
    private static IndexSearcher searcher;
    private static Indexer indexer;
    Directory dir;
    String indexDir = "index-dir";
    int dvnMaxClauseCount = 4096;
    
    
    /** Creates a new instance of Indexer */
    public Indexer() {
        String dvnIndexLocation = System.getProperty("dvn.index.location");
        File locationDirectory = null;
        if (dvnIndexLocation != null){
            locationDirectory = new File(dvnIndexLocation);
            if (locationDirectory.exists() && locationDirectory.isDirectory()){
                indexDir = dvnIndexLocation + "/index-dir";
            }
        }
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
            dir = FSDirectory.getDirectory(indexDir,isIndexEmpty());
            r = IndexReader.open(dir);
            searcher = new IndexSearcher(r);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    protected void setup() throws IOException {
        dir = FSDirectory.getDirectory(indexDir,isIndexEmpty());
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
        logger.info("Start indexing study "+study.getStudyId());
        addText(4.0f,  doc,"title",study.getTitle());
        addKeyword(doc,"title",study.getTitle());
        addKeyword(doc,"id",study.getId().toString());
        addKeyword(doc,"studyId", study.getStudyId());
        addText(1.0f,  doc,"studyId", study.getStudyId());
        addText(1.0f,  doc,"owner",study.getOwner().getName());
        addKeyword(doc,"productionDate", study.getProductionDate());
        addKeyword(doc,"distributionDate", study.getDistributionDate());
        Collection <StudyKeyword> keywords = study.getStudyKeywords();
        for (Iterator it = keywords.iterator(); it.hasNext();) {
            StudyKeyword elem = (StudyKeyword) it.next();
            addText(1.0f,  doc,"keywordValue", elem.getValue());
        }
        Collection <StudyTopicClass> topicClassifications = study.getStudyTopicClasses();
        for (Iterator it = topicClassifications.iterator(); it.hasNext();) {
            StudyTopicClass elem = (StudyTopicClass) it.next();
            addText(1.0f,  doc,"topicClassValue", elem.getValue());
        }
        Collection <StudyAbstract> abstracts = study.getStudyAbstracts();
        for (Iterator it = abstracts.iterator(); it.hasNext();) {
            StudyAbstract elem = (StudyAbstract) it.next();
            addText(2.0f,  doc,"abstractText",elem.getText());
            addKeyword(doc,"abstractDate",elem.getDate());
            
        }
        Collection <StudyAuthor> studyAuthors = study.getStudyAuthors();
        for (Iterator it = studyAuthors.iterator(); it.hasNext();) {
            StudyAuthor elem = (StudyAuthor) it.next();
            addText(3.0f,  doc,"authorName",elem.getName());
            addKeyword(doc,"authorName",elem.getName());
            addText(1.0f,  doc,"authorAffiliation", elem.getAffiliation());
        }
        Collection <StudyProducer> studyProducers = study.getStudyProducers();
        for (Iterator itProducers = studyProducers.iterator(); itProducers.hasNext();) {
            StudyProducer studyProducer = (StudyProducer) itProducers.next();
            addText(1.0f,  doc,"producerName", studyProducer.getName());
            addText(1.0f,  doc,"producerName", studyProducer.getAbbreviation());
            addText(1.0f,  doc,"producerName", studyProducer.getLogo());
            addText(1.0f,  doc,"producerName", studyProducer.getUrl());
        }
        Collection <StudyDistributor> studyDistributors = study.getStudyDistributors();
        for (Iterator it = studyDistributors.iterator(); it.hasNext();) {
            StudyDistributor studyDistributor = (StudyDistributor) it.next();
            addText(1.0f,  doc,"distributorName", studyDistributor.getName());
            addText(1.0f,  doc,"distributorName", studyDistributor.getAbbreviation());
            addText(1.0f,  doc,"distributorName", studyDistributor.getLogo());
            addText(1.0f,  doc,"distributorName", studyDistributor.getUrl());
        }
        Collection <StudyOtherId> otherIds = study.getStudyOtherIds();
        for (Iterator it = otherIds.iterator(); it.hasNext();) {
            StudyOtherId elem = (StudyOtherId) it.next();
            addText(1.0f,  doc,"otherId", elem.getOtherId());
        }
        addText(1.0f,  doc,"fundingAgency",study.getFundingAgency());
        addText(1.0f,  doc,"distributorContact",study.getDistributorContact());
        addText(1.0f,  doc,"distributorContactAffiliation",study.getDistributorContactAffiliation());
        addText(1.0f,  doc,"distributorContactEmail",study.getDistributorContactEmail());
        addKeyword(doc,"dateOfDeposit",study.getDateOfDeposit());
        addText(1.0f,  doc,"depositor",study.getDepositor());
        addText(1.0f,  doc,"seriesName",study.getSeriesName());
        addText(1.0f,  doc,"seriesInformation",study.getSeriesInformation());
        addKeyword(doc,"studyVersion",study.getStudyVersion());
        addText(1.0f,  doc,"originOfSources",study.getOriginOfSources());
        addText(1.0f,  doc,"dataSources",study.getDataSources());
        addKeyword(doc,"frequencyOfDataCollection",study.getFrequencyOfDataCollection());
        addText(1.0f,  doc,"universe",study.getUniverse());
        addKeyword(doc,"unitOfAnalysis",study.getUnitOfAnalysis());
        addText(1.0f,  doc,"dataCollector",study.getDataCollector());
        addText(1.0f,  doc,"kindOfData", study.getKindOfData());
        addText(1.0f,  doc,"geographicCoverage",study.getGeographicCoverage());
        addText(1.0f,  doc,"geographicUnit",study.getGeographicUnit());
        addKeyword(doc,"timePeriodCoveredEnd",study.getTimePeriodCoveredEnd());
        addKeyword(doc,"timePeriodCoveredStart",study.getTimePeriodCoveredStart());
        addKeyword(doc,"dateOfCollection",study.getDateOfCollectionStart());
        addKeyword(doc,"dateOfCollectionEnd",study.getDateOfCollectionEnd());
        addKeyword(doc,"country",study.getCountry());
        addText(1.0f,  doc,"country",study.getCountry());
        addKeyword(doc,"timeMethod",study.getTimeMethod());
        addKeyword(doc,"samplingProcedure",study.getSamplingProcedure());
        addKeyword(doc,"deviationsFromSampleDesign",study.getDeviationsFromSampleDesign());
        addKeyword(doc,"collectionMode",study.getCollectionMode());
        addKeyword(doc,"researchInstrument",study.getResearchInstrument());
        addText(1.0f,  doc,"characteristicOfSources",study.getCharacteristicOfSources());
        addText(1.0f,  doc,"accessToSources",study.getAccessToSources());
        addText(1.0f,  doc,"dataCollectionSituation",study.getDataCollectionSituation());
        addText(1.0f,  doc,"actionsToMinimizeLoss",study.getActionsToMinimizeLoss());
        addText(1.0f,  doc,"controlOperations",study.getControlOperations());
        addText(1.0f,  doc,"weighting",study.getWeighting());
        addText(1.0f,  doc,"cleaningOperations",study.getCleaningOperations());
        addText(1.0f,  doc,"studyLevelErrorNotes",study.getStudyLevelErrorNotes());
        List <StudyNote> studyNotes = study.getStudyNotes();
        for (Iterator it = studyNotes.iterator(); it.hasNext();){
            StudyNote elem = (StudyNote) it.next(); 
            addText(1.0f,  doc, "studyNoteType", elem.getType());
            addText(1.0f,  doc, "studyNoteSubject", elem.getSubject());
            addText(1.0f,  doc, "studyNoteText", elem.getText());
        }
        addKeyword(doc,"responseRate",study.getResponseRate());
        addKeyword(doc,"samplingErrorEstimate",study.getSamplingErrorEstimate());
        addText(1.0f,  doc,"otherDataAppraisal",study.getOtherDataAppraisal());
        addText(1.0f,  doc,"placeOfAccess",study.getPlaceOfAccess());
        addText(1.0f,  doc,"originalArchive",study.getOriginalArchive());
        addKeyword(doc,"availabilityStatus",study.getAvailabilityStatus());
        addKeyword(doc,"collectionSize",study.getCollectionSize());
        addKeyword(doc,"studyCompletion",study.getStudyCompletion());
        addText(1.0f,  doc,"confidentialityDeclaration",study.getConfidentialityDeclaration());
        addText(1.0f,  doc,"specialPermissions",study.getSpecialPermissions());
        addText(1.0f,  doc,"restrictions",study.getRestrictions());
        addText(1.0f,  doc,"contact",study.getContact());
        addText(1.0f,  doc,"citationRequirements",study.getCitationRequirements());
        addText(1.0f,  doc,"depositorRequirements",study.getDepositorRequirements());
        addText(1.0f,  doc,"conditions",study.getConditions());
        addText(1.0f,  doc,"disclaimer",study.getDisclaimer());
        List <StudyRelMaterial> relMaterials = study.getStudyRelMaterials();
        for (Iterator it = relMaterials.iterator(); it.hasNext();) {
            StudyRelMaterial elem = (StudyRelMaterial) it.next();
            addText(1.0f,  doc,"relatedMaterial",elem.getText());
        }
        List <StudyRelPublication> relPublications = study.getStudyRelPublications();
        for (Iterator it = relPublications.iterator(); it.hasNext();) {
            StudyRelPublication elem = (StudyRelPublication) it.next();
            addText(1.0f,  doc,"relatedPublications",elem.getText());
        }
        List <StudyRelStudy> relStudies = study.getStudyRelStudies();
        for (Iterator it = relStudies.iterator(); it.hasNext();) {
            StudyRelStudy elem = (StudyRelStudy) it.next();
            addText(1.0f,  doc,"relatedStudy",elem.getText());
        }
        List <StudyOtherRef> otherRefs = study.getStudyOtherRefs();
        for (Iterator it = otherRefs.iterator(); it.hasNext();) {
            StudyOtherRef elem = (StudyOtherRef) it.next();
            addText(1.0f,  doc,"otherReferences",elem.getText());
        }
       
   /*     addText(1.0f,  doc,"relatedMaterial",study.getRelatedMaterial());
        addText(1.0f,  doc,"relatedPublications",study.getRelatedPublications());
        addText(1.0f,  doc,"otherReferences",study.getOtherReferences());
    */
        addText(1.0f,  doc,"subtitle",study.getSubTitle());
        List <StudyKeyword> studyKeywords = study.getStudyKeywords();
        for (Iterator it = studyKeywords.iterator(); it.hasNext();) {
            StudyKeyword elem = (StudyKeyword) it.next();
            addText(1.0f,  doc,"keywordVocabulary",elem.getVocab());
        }
        List <StudyTopicClass> studyTopicClasses =study.getStudyTopicClasses();
        for (Iterator it = studyTopicClasses.iterator(); it.hasNext();) {
            StudyTopicClass elem = (StudyTopicClass) it.next();
            addText(1.0f,  doc,"topicClassVocabulary", elem.getVocab());            
        }
        addText(1.0f,  doc,"protocol",study.getProtocol());
        addText(1.0f,  doc,"authority",study.getAuthority());
        addText(1.0f,  doc,"globalId",study.getGlobalId());
        List <FileCategory> fileCategories = study.getFileCategories();
        writer = new IndexWriter(dir, true, getAnalyzer(),isIndexEmpty());
        
        writer.setUseCompoundFile(true);
        writer.addDocument(doc);
        writer.close();
        writerVar = new IndexWriter(dir, getAnalyzer());
        for (int i = 0; i < fileCategories.size(); i++) {
            FileCategory fileCategory = fileCategories.get(i);
            Collection<StudyFile> studyFiles = fileCategory.getStudyFiles();
            for (Iterator it = studyFiles.iterator(); it.hasNext();) {
                StudyFile elem = (StudyFile) it.next();
                DataTable dataTable = elem.getDataTable();
                if (dataTable != null) {
                    List<DataVariable> dataVariables = dataTable.getDataVariables();
                    for (int j = 0; j < dataVariables.size(); j++) {
                        Document docVariables = new Document();
                        addText(1.0f,  docVariables, "varStudyId", study.getId().toString());
                        addText(1.0f,  docVariables, "varStudyFileId", elem.getId().toString());
                        DataVariable dataVariable = dataVariables.get(j);
                        addText(1.0f,  docVariables, "id", dataVariable.getId().toString());
                        addText(1.0f,  docVariables, "varName", dataVariable.getName());
                        addText(1.0f,  docVariables, "varLabel", dataVariable.getLabel());
                        addText(1.0f,  docVariables, "varId", dataVariable.getId().toString());
                        writerVar.addDocument(docVariables);
                    }
                }
            }
        }
        writerVar.close();
        logger.info("End indexing study "+study.getStudyId());
    }
    
    
    protected Analyzer getAnalyzer(){
//        return new StandardAnalyzer();
//        return new WhitespaceAnalyzer();
//        return new DataverseNetworkAnalyzer();
        return new DVNAnalyzer();
    }
    
    protected void addKeyword(Document doc,String key, String value){
        if (value != null && value.length()>0){
            doc.add(new Field(key,value.toLowerCase().trim(), Field.Store.YES, Field.Index.UN_TOKENIZED));
            doc.add(new Field(key,value.trim(), Field.Store.YES, Field.Index.UN_TOKENIZED));
        }
    }
    
    protected void addText(float boost, Document doc,String key, String value){
        if (value != null && value.length()>0){
            Field f1 = new Field(key,value.toLowerCase().trim(),Field.Store.YES, Field.Index.TOKENIZED);
            Field f2 = new Field(key,value.trim(), Field.Store.YES, Field.Index.UN_TOKENIZED);
            f1.setBoost(boost);
            f2.setBoost(boost);
            doc.add(f1);
            doc.add(f2);
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
        logger.info("Start search: "+DateTools.dateToString(new Date(), Resolution.MILLISECOND));
        Long[] studyIdsArray = null;
        if (studyIds != null) {
            studyIdsArray = studyIds.toArray(new Long[studyIds.size()]);
            Arrays.sort(studyIdsArray);
        }
        List <Long> results = null;
        List <BooleanQuery> searchParts = new ArrayList();
        boolean variableSearch = false;
        boolean nonVariableSearch = false;
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
        List<Long> filteredResults = null;
        if (nonVariableSearch) {
            BooleanQuery searchTermsQuery = andSearchTermClause(nonVariableSearchTerms);
            searchParts.add(searchTermsQuery);
            BooleanQuery searchQuery = andQueryClause(searchParts);
            logger.info("Start hits: " + DateTools.dateToString(new Date(), Resolution.MILLISECOND));
            nvResults = getHitIds(searchQuery);
            logger.info("Done hits: " + DateTools.dateToString(new Date(), Resolution.MILLISECOND));
            logger.info("Start filter: " + DateTools.dateToString(new Date(), Resolution.MILLISECOND));
            filteredResults = studyIds != null ? intersectionResults(nvResults, studyIdsArray) : nvResults;
            logger.info("Done filter: " + DateTools.dateToString(new Date(), Resolution.MILLISECOND));
        }
        if (variableSearch){
            if (nonVariableSearch) {
                logger.info("Start nonvar search variables: " + DateTools.dateToString(new Date(), Resolution.MILLISECOND));
                results = searchVariables(filteredResults, variableSearchTerms, true); // get var ids
                logger.info("Done nonvar search variables: " + DateTools.dateToString(new Date(), Resolution.MILLISECOND));
            } else {
                logger.info("Start search variables: " + DateTools.dateToString(new Date(), Resolution.MILLISECOND));
                results = searchVariables(studyIds, variableSearchTerms, true); // get var ids
                logger.info("Done search variables: " + DateTools.dateToString(new Date(), Resolution.MILLISECOND));
            }
        } else {
            results = filteredResults;
        }
        logger.info("Done search: "+DateTools.dateToString(new Date(), Resolution.MILLISECOND));

        return results;
        
    }

    private String getDVNTokenString(final String value) {
        PorterStemFilter p = new PorterStemFilter(new DVNTokenizer(new StringReader(value)));
        StringBuffer dvnValueSb = new StringBuffer();
        try {
            Token n = p.next();
            while (n != null) {
                dvnValueSb.append(new String(n.termBuffer()).substring(0, n.termLength()) + " ");
                n = p.next(n);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String dvnValue = dvnValueSb.toString();
        return dvnValue;
    }

    private List <Long> intersectionDocResults(final List<Document> results1, final List<Long> results2) throws IOException {
        List <Long>  mergeResults = new ArrayList();
        for (Iterator it = results1.iterator(); it.hasNext();){
            Document d = (Document) it.next();
            Field studyId = d.getField("varStudyId");
            String studyIdStr = studyId.stringValue();
            Long studyIdLong = Long.valueOf(studyIdStr);
            if (results2.contains(studyIdLong)) {
                Field varId = d.getField("id");
                String varIdStr = varId.stringValue();
                Long varIdLong = Long.valueOf(varIdStr);
                if (!mergeResults.contains(varIdLong)) {
                    mergeResults.add(varIdLong);
                }
            }
        }
        return mergeResults;
    }

    private List<Long> intersectionResults(final List<Long> results1, final List<Long> results2) {
        List <Long> mergeResults = new ArrayList();
        for (Iterator it = results1.iterator(); it.hasNext();){
            Long elem = (Long) it.next();
            if (results2.contains(elem)){
                mergeResults.add(elem);
            }
        }
        return mergeResults;
    }

    private List<Long> intersectionResults(final List<Long> results1, final Long[] results2) {
        List <Long> mergeResults = new ArrayList();
        for (Iterator it = results1.iterator(); it.hasNext();){
            Long elem = (Long) it.next();
            if (Arrays.binarySearch(results2, elem)>=0){
                mergeResults.add(elem);
            }
        }
        return mergeResults;
    }

    public List search(String query) throws IOException {
        String field = query.substring(0,query.indexOf("=")).trim();
        String value = query.substring(query.indexOf("=")+1).trim();
        ArrayList matchIds = new ArrayList();
        LinkedHashSet matchIdsSet = new LinkedHashSet();
        String[] phrase = getPhrase(value);
        
        
        Directory searchdir = FSDirectory.getDirectory(indexDir,isIndexEmpty());
        if (searcher == null) {
            searcher = new IndexSearcher(searchdir);
        }
        Hits hits = exactMatchQuery(searcher, field, value);
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
        QueryParser parser = new QueryParser("abstract",getAnalyzer());
        Query query=null;
        try {
            query = parser.parse(adhocQuery);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return getHitIds(query);
    }
        
    private String[] getPhrase(final String value) {
        String dvnValue = getDVNTokenString(value);
        StringTokenizer tk = new StringTokenizer(dvnValue);
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
        BooleanQuery indexQuery = null;        
        if (searchTerm.getFieldName().equalsIgnoreCase("variable")){
            indexQuery = buildVariableQuery(searchTerm.getValue().toLowerCase().trim());
            searchParts.add(indexQuery);
        }
        BooleanQuery searchQuery = andQueryClause(searchParts);
        List <Document> variableResults = getHits(searchQuery);
        List <Long> variableIdResults = getHitIds(variableResults);
        List<Long> finalResults = studyIds != null ? intersectionDocResults(variableResults, studyIds) : variableIdResults;
        return finalResults;
    }
    
    public List searchVariables(List<Long> studyIds, List<SearchTerm> searchTerms, boolean varIdReturnValues) throws IOException {
        List<BooleanQuery> searchParts = new ArrayList();
        for (Iterator it = searchTerms.iterator(); it.hasNext();) {
            SearchTerm elem = (SearchTerm) it.next();
            BooleanQuery indexQuery = null;
            if (elem.getFieldName().equalsIgnoreCase("variable")) {
                indexQuery = buildVariableQuery(elem.getValue().toLowerCase().trim());
                searchParts.add(indexQuery);
            }
        }
        BooleanQuery searchQuery = andQueryClause(searchParts);
        List<Long> finalResults = null;
        if (varIdReturnValues) {
            List <Document> variableResults = getHits(searchQuery);
            List<Long> variableIdResults = getHitIds(variableResults);
            finalResults = studyIds != null ? intersectionDocResults(variableResults, studyIds) : variableIdResults;
        } else {
            List <Long> studyIdResults = getVarHitIds(searchQuery); // gets the study ids
            finalResults = studyIds != null ? intersectionResults(studyIdResults, studyIds) : studyIdResults;
        } 
        return finalResults;
    }
    
    public List searchBetween(Term begin,Term end, boolean inclusive) throws IOException{
        RangeQuery query = new RangeQuery(begin,end,inclusive);
        return getHitIds(query);
    }
    
    private List<Document> getHits( Query query ) throws IOException {
        Hits hits = null;
        List <Document> documents = new ArrayList();
        if (query != null){
            if (searcher == null) {
                searcher = new IndexSearcher(dir);
            }
            logger.info("Start searcher: " + DateTools.dateToString(new Date(), Resolution.MILLISECOND));
            hits = searcher.search(query); 
            logger.info("done searcher: " + DateTools.dateToString(new Date(), Resolution.MILLISECOND));
            logger.info("Start iterate: " + DateTools.dateToString(new Date(), Resolution.MILLISECOND));
            for (int i = 0; i < hits.length(); i++) {
                System.out.println("Score: "+hits.score(i));
                documents.add(hits.doc(i));
            }
            logger.info("done iterate: " + DateTools.dateToString(new Date(), Resolution.MILLISECOND));
            searcher.close();
        }
        return documents;
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
            logger.info("Start searcher: " + DateTools.dateToString(new Date(), Resolution.MILLISECOND));
            DocumentCollector s = new DocumentCollector(searcher);
            searcher.search(query, s);
            searcher.close();
            logger.info("done searcher: " + DateTools.dateToString(new Date(), Resolution.MILLISECOND));
            logger.info("Start iterate: " + DateTools.dateToString(new Date(), Resolution.MILLISECOND));
            List hits = s.getStudies();
            for (int i = 0; i < hits.size(); i++) {
                Document d = ((ScoredDocument) hits.get(i)).getScoredDocument();
                Field studyId = d.getField("id");
                String studyIdStr = studyId.stringValue();
                Long studyIdLong = Long.valueOf(studyIdStr);
                matchIdsSet.add(studyIdLong);
            }
            logger.info("done iterate: " + DateTools.dateToString(new Date(), Resolution.MILLISECOND));
            searcher.close();
        }
        matchIds.addAll(matchIdsSet);
        return matchIds;
    }
    
    private List<Long> getHitIds(List<Document> hits) throws IOException {
        ArrayList matchIds = new ArrayList();
        LinkedHashSet matchIdsSet = new LinkedHashSet();
         for (Iterator it = hits.iterator(); it.hasNext();) {
            Document d = (Document) it.next();
            Field studyId = d.getField("id");
            String studyIdStr = studyId.stringValue();
            Long studyIdLong = Long.valueOf(studyIdStr);
            matchIdsSet.add(studyIdLong);
        }

        matchIds.addAll(matchIdsSet);
        return matchIds;
    }
    
    private List getVarHitIds(List <Document> hits) throws IOException {
        ArrayList matchIds = new ArrayList();
        LinkedHashSet matchIdsSet = new LinkedHashSet();
        for (Iterator it = hits.iterator(); it.hasNext();){
            Document d = (Document) it.next();
            Field studyId = d.getField("varStudyId");
            String studyIdStr = studyId.stringValue();
            Long studyIdLong = Long.valueOf(studyIdStr);
            matchIdsSet.add(studyIdLong);
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
            if (r != null) {
                if (!r.isCurrent()) {
                    while (r.isLocked(dir));

                    r = IndexReader.open(dir);
                    searcher = new IndexSearcher(r);
                }
            } else {
                r = IndexReader.open(dir);
                searcher = new IndexSearcher(r);
            }
 
            DocumentCollector s = new DocumentCollector(searcher);
            searcher.search(query, s);
            searcher.close();
            List hits = s.getStudies();
//            Hits hits = searcher.search(query);
            for (int i = 0; i < hits.size(); i++) {
                Document d = (Document) hits.get(i);
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
            String[] phrase = getPhrase(elem.getValue().toLowerCase().trim());
            if (phrase.length > 1) {
                BooleanClause partialMatchClause = null;
                PhraseQuery phraseQuery = new PhraseQuery();
                phraseQuery.setSlop(10);

                for (int i = 0; i < phrase.length; i++) {
                    phraseQuery.add(new Term(elem.getFieldName(), phrase[i].toLowerCase().trim()));
                }
                orTerms.add(phraseQuery, BooleanClause.Occur.SHOULD);
            } else if (phrase.length == 1){
//                Term t = new Term(elem.getFieldName(), elem.getValue().toLowerCase().trim());
                Term t = new Term(elem.getFieldName(), phrase[0].toLowerCase().trim());
                TermQuery orQuery = new TermQuery(t);
                orTerms.add(orQuery, BooleanClause.Occur.SHOULD);
            }
        }
        return orTerms;
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
                    phraseQuery.setSlop(10);
                    andTerms.add(partialMatch(elem,10));
                } else if (phrase.length==1){
//                    Term t = new Term(elem.getFieldName(), elem.getValue().toLowerCase().trim());
                    Term t = new Term(elem.getFieldName(), phrase[0].toLowerCase().trim());
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
        BooleanQuery.setMaxClauseCount(dvnMaxClauseCount);
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

    public String getIndexDir() {
        return indexDir;
    }
    
    private boolean isIndexEmpty(){
        String [] indexFiles = new File(indexDir).list();
        int n = indexFiles.length;
        boolean isEmpty = (n == 0);
        return isEmpty;
    }
}
