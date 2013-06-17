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
 * Indexer.java
 *
 * Created on September 26, 2006, 9:01 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.index;
import edu.harvard.iq.dvn.core.study.DataTable;
import edu.harvard.iq.dvn.core.study.DataVariable;
import edu.harvard.iq.dvn.core.study.FileMetadata;
import edu.harvard.iq.dvn.core.study.Metadata;
import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyAbstract;
import edu.harvard.iq.dvn.core.study.StudyAuthor;
import edu.harvard.iq.dvn.core.study.StudyDistributor;
import edu.harvard.iq.dvn.core.study.StudyFile;
import edu.harvard.iq.dvn.core.study.StudyField;
import edu.harvard.iq.dvn.core.study.StudyFieldValue;
import edu.harvard.iq.dvn.core.study.StudyGeoBounding;
import edu.harvard.iq.dvn.core.study.StudyGrant;
import edu.harvard.iq.dvn.core.study.StudyKeyword;
import edu.harvard.iq.dvn.core.study.StudyNote;
import edu.harvard.iq.dvn.core.study.StudyOtherId;
import edu.harvard.iq.dvn.core.study.StudyOtherRef;
import edu.harvard.iq.dvn.core.study.StudyProducer;
import edu.harvard.iq.dvn.core.study.StudyRelMaterial;
import edu.harvard.iq.dvn.core.study.StudyRelPublication;
import edu.harvard.iq.dvn.core.study.StudyRelStudy;
import edu.harvard.iq.dvn.core.study.StudySoftware;
import edu.harvard.iq.dvn.core.study.StudyTopicClass;
import edu.harvard.iq.dvn.core.study.StudyVersion;
import edu.harvard.iq.dvn.core.study.TabularDataFile;
import edu.harvard.iq.dvn.core.study.SpecialOtherFile; 
import edu.harvard.iq.dvn.core.study.FileMetadataField; 
import edu.harvard.iq.dvn.core.study.FileMetadataFieldValue;
import edu.harvard.iq.dvn.core.study.StudyFieldServiceLocal;
import edu.harvard.iq.dvn.core.util.DateUtil;
import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCCollection;
import edu.harvard.iq.dvn.core.vdc.VDCServiceLocal;
import edu.harvard.iq.dvn.core.web.AdvSearchPage;
import edu.harvard.iq.dvn.core.web.StudyListingPage;
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
import java.util.Map; 
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBs;
import javax.naming.Context;
import javax.naming.InitialContext;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.PorterStemFilter;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.DateTools.Resolution;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.document.Field;
import org.apache.lucene.facet.index.CategoryDocumentBuilder;
import org.apache.lucene.facet.search.DrillDown;
import org.apache.lucene.facet.search.FacetsCollector;
import org.apache.lucene.facet.search.params.CountFacetRequest;
import org.apache.lucene.facet.search.params.FacetSearchParams;
import org.apache.lucene.facet.search.results.FacetResult;
import org.apache.lucene.facet.search.results.FacetResultNode;
import org.apache.lucene.facet.taxonomy.CategoryPath;
import org.apache.lucene.facet.taxonomy.TaxonomyReader;
import org.apache.lucene.facet.taxonomy.TaxonomyWriter;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyReader;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyWriter;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.MultiCollector;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.util.Version;

/**
 *
 * @author roberttreacy
 */
@EJBs({
@EJB(name="studyField", beanInterface=edu.harvard.iq.dvn.core.study.StudyFieldServiceLocal.class),
@EJB(name = "vdcService", beanInterface = edu.harvard.iq.dvn.core.vdc.VDCServiceLocal.class),
})
public class Indexer implements java.io.Serializable  {

    private static final Logger logger = Logger.getLogger(Indexer.class.getCanonicalName());
    private static IndexWriter writer;
    private static IndexWriter writerVar;
    private static IndexWriter writerVersions;
    private static IndexWriter writerFileMeta; 
    private static IndexReader r;
    private static IndexSearcher searcher;
    private static Indexer indexer;
    
    StudyFieldServiceLocal studyFieldService;
    VDCServiceLocal vdcService;
    
    Directory dir;
    String indexDir = "index-dir";
    int dvnMaxClauseCount = Integer.MAX_VALUE;
    String taxoDirName = "taxo-dir";
    Directory taxoDir;
    private static DirectoryTaxonomyReader taxoReader;

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
            assureIndexDirExists();
            dir = FSDirectory.open(new File(indexDir));
            r = IndexReader.open(dir, true);
            searcher = new IndexSearcher(r);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        // should we use System.getProperty("dvn.taxonomyindex.location") instead?
        taxoDirName = dvnIndexLocation + "/" + taxoDirName;
        try {
            assureTaxoDirExists();
            taxoDir = FSDirectory.open(new File(taxoDirName));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    protected void setup() throws IOException {
        assureIndexDirExists();
        dir = FSDirectory.open(new File(indexDir));
    }

    public static Indexer getInstance(){
        if (indexer != null && indexer.indexDirExists()) {
            return indexer;
        }
        
        // If the Indexer hasn't been initialized yet; or if the index
        // directory disappeared (for example, if they wiped it clean
        // in order to run IndexAll), we create and initialize a new
        // Indexer: 
        
        indexer = new Indexer();
        try {
            indexer.setup();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return indexer;
    }

    public void deleteDocument(long studyId) {
        IndexWriter deleteWriter = null; 

        try {
            while (IndexWriter.isLocked(dir));
            
            // TODO: 
            // Figure out why we are using *IndexReader*, not Writer, for 
            // this operation in the first place? -- L.A.
            // (this will become a moot point if we upgrade to the next 
            // version of Lucene; then we'll be forced to use IndexWriter 
            // for all operations that modify the index)
            
            //reader = IndexReader.open(dir, false);
            deleteWriter = new IndexWriter(dir, getAnalyzer(), isIndexEmpty(), IndexWriter.MaxFieldLength.UNLIMITED);
            deleteWriter.deleteDocuments(new Term("id", Long.toString(studyId)));
            deleteWriter.deleteDocuments(new Term("varStudyId",Long.toString(studyId)));
            deleteWriter.deleteDocuments(new Term("versionStudyId",Long.toString(studyId)));
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (deleteWriter != null) {
                try {
                    deleteWriter.close();
                } catch (Exception ex) {
                    
                }
            }
        }
    }

    public void deleteDocumentCarefully(long studyId) throws IOException {
        IndexWriter deleteWriter = null; 
        boolean success = true; 
        String errorMessage = "";
        
        try {
            //while (IndexWriter.isLocked(dir));
            
            // TODO: 
            // Figure out why we are using *IndexReader*, not Writer, for 
            // this operation in the first place? -- L.A.
            // (this will become a moot point if we upgrade to the next 
            // version of Lucene; then we'll be forced to use IndexWriter 
            // for all operations that modify the index)
            
            //reader = IndexReader.open(dir, false);
            deleteWriter = new IndexWriter(dir, getAnalyzer(), isIndexEmpty(), IndexWriter.MaxFieldLength.UNLIMITED);
            deleteWriter.deleteDocuments(new Term("id", Long.toString(studyId)));
            deleteWriter.deleteDocuments(new Term("varStudyId",Long.toString(studyId)));
            deleteWriter.deleteDocuments(new Term("versionStudyId",Long.toString(studyId)));
        } catch (Exception ex) {
            success = false;
            errorMessage = "Caught an exception trying to delete index document for study " + studyId + "; " + ex.getMessage();
            logger.info(errorMessage);
            ex.printStackTrace();
        } finally {
            if (deleteWriter != null) {
                try {
                    deleteWriter.close();
                    // Should I be checking for, and deleting a lock file 
                    // left behind??
                } catch (Exception ex) {
                    logger.fine("Caught an exception trying to close the index reader.");
                    // I'm guessing this is not a super dangerous condition...
                    // IndexReaders does try to put a lock on the 
                    // index files... so in theory it could be dangerous -
                    // as we may be leaving such a lock behind (?). -- L.A. 
                    
                }
            }
        }
        
        if (!success) {
            throw new IOException(errorMessage);
        }
    }
    
    public void deleteVersionDocuments(long studyId){
        try{
            IndexReader reader = IndexReader.open(dir, false);
            reader.deleteDocuments(new Term("versionStudyId",Long.toString(studyId)));
            reader.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    protected void addDocument(Study study) throws IOException{
        
        StudyVersion sv = null;
        if (study.getReleasedVersion() != null) {
            sv = study.getReleasedVersion();
            Metadata metadata = sv.getMetadata();


            Document doc = new Document();
            logger.fine("Start indexing study " + study.getStudyId());
            addText(4.0f, doc, "title", metadata.getTitle());
            addKeyword(doc, "id", study.getId().toString());
            addText(1.0f, doc, "studyId", study.getStudyId());
            addKeyword(doc, "studyId", study.getStudyId());
//        addText(1.0f,  doc,"owner",study.getOwner().getName());
            addText(1.0f, doc, "dvOwnerId", Long.toString(study.getOwner().getId()));
            String dvNetworkId = study.getOwner().getVdcNetwork().getId().toString();
            /* This is the ID of the DV Network to which the study belongs 
             * directly, through its owner DV:
             */
            addText(1.0f, doc, "ownerDvNetworkId", dvNetworkId);
            /* Plus it may belong to these extra Networks, through linking into
             * collections in DVs that belong to other Networks:
             */
            logger.fine("Using network id "+dvNetworkId);
            addText(1.0f, doc, "dvNetworkId", dvNetworkId); 
            List<Long> linkedToNetworks = study.getLinkedToNetworkIds();
            if (linkedToNetworks != null) {
                for (Long vdcnetworkid : linkedToNetworks) {
                    addText(1.0f, doc, "dvNetworkId", vdcnetworkid.toString());
                }
            }
            addDate(1.0f, doc, "productionDate", metadata.getProductionDate());
            addDate(1.0f, doc, "distributionDate", metadata.getDistributionDate());

            Collection<StudyKeyword> keywords = metadata.getStudyKeywords();
            for (Iterator it = keywords.iterator(); it.hasNext();) {
                StudyKeyword elem = (StudyKeyword) it.next();
                addText(1.0f, doc, "keywordValue", elem.getValue());
            }
            Collection<StudyTopicClass> topicClassifications = metadata.getStudyTopicClasses();
            for (Iterator it = topicClassifications.iterator(); it.hasNext();) {
                StudyTopicClass elem = (StudyTopicClass) it.next();
                addText(1.0f, doc, "topicClassValue", elem.getValue());
                addText(1.0f, doc, "topicVocabClassURI", elem.getVocabURI());
                addText(1.0f, doc, "topicClassVocabulary", elem.getVocab());
            }
            Collection<StudyAbstract> abstracts = metadata.getStudyAbstracts();
            for (Iterator it = abstracts.iterator(); it.hasNext();) {
                StudyAbstract elem = (StudyAbstract) it.next();
                addText(2.0f, doc, "abstractText", elem.getText());
                addDate(1.0f, doc, "abstractDate", elem.getDate());

            }
            Collection<StudyAuthor> studyAuthors = metadata.getStudyAuthors();
            for (Iterator it = studyAuthors.iterator(); it.hasNext();) {
                StudyAuthor elem = (StudyAuthor) it.next();
                addText(3.0f, doc, "authorName", elem.getName());
                addText(1.0f, doc, "authorName", elem.getName());
                addText(1.0f, doc, "authorAffiliation", elem.getAffiliation());
            }
            Collection<StudyProducer> studyProducers = metadata.getStudyProducers();
            for (Iterator itProducers = studyProducers.iterator(); itProducers.hasNext();) {
                StudyProducer studyProducer = (StudyProducer) itProducers.next();
                addText(1.0f, doc, "producerName", studyProducer.getName());
                addText(1.0f, doc, "producerName", studyProducer.getAbbreviation());
                addText(1.0f, doc, "producerName", studyProducer.getLogo());
                addText(1.0f, doc, "producerName", studyProducer.getUrl());
                addText(1.0f, doc, "producerName", studyProducer.getAffiliation());
                addText(1.0f, doc, "producerName", studyProducer.getMetadata().getProductionPlace());
            }
            Collection<StudyDistributor> studyDistributors = metadata.getStudyDistributors();
            for (Iterator it = studyDistributors.iterator(); it.hasNext();) {
                StudyDistributor studyDistributor = (StudyDistributor) it.next();
                addText(1.0f, doc, "distributorName", studyDistributor.getName());
                addText(1.0f, doc, "distributorName", studyDistributor.getAbbreviation());
                addText(1.0f, doc, "distributorName", studyDistributor.getLogo());
                addText(1.0f, doc, "distributorName", studyDistributor.getUrl());
                addText(1.0f, doc, "distributorName", studyDistributor.getAffiliation());
            }
            Collection<StudyOtherId> otherIds = metadata.getStudyOtherIds();
            for (Iterator it = otherIds.iterator(); it.hasNext();) {
                StudyOtherId elem = (StudyOtherId) it.next();
                addText(1.0f, doc, "otherId", elem.getOtherId());
                addText(1.0f, doc, "otherIdAgency", elem.getAgency());
            }
            addText(1.0f, doc, "fundingAgency", metadata.getFundingAgency());
            addText(1.0f, doc, "distributorContact", metadata.getDistributorContact());
            addText(1.0f, doc, "distributorContactAffiliation", metadata.getDistributorContactAffiliation());
            addText(1.0f, doc, "distributorContactEmail", metadata.getDistributorContactEmail());
            addDate(1.0f, doc, "dateOfDeposit", metadata.getDateOfDeposit());
            addText(1.0f, doc, "depositor", metadata.getDepositor());
            addText(1.0f, doc, "seriesName", metadata.getSeriesName());
            addText(1.0f, doc, "seriesInformation", metadata.getSeriesInformation());
            addText(1.0f, doc, "studyVersion", metadata.getStudyVersionText());
            addText(1.0f, doc, "versionDate", metadata.getVersionDate());
            addText(1.0f, doc, "originOfSources", metadata.getOriginOfSources());
            addText(1.0f, doc, "dataSources", metadata.getDataSources());
            addText(1.0f, doc, "frequencyOfDataCollection", metadata.getFrequencyOfDataCollection());
            addText(1.0f, doc, "universe", metadata.getUniverse());
            addText(1.0f, doc, "unitOfAnalysis", metadata.getUnitOfAnalysis());
            addText(1.0f, doc, "dataCollector", metadata.getDataCollector());
            addText(1.0f, doc, "kindOfData", metadata.getKindOfData());
            addText(1.0f, doc, "geographicCoverage", metadata.getGeographicCoverage());
            addText(1.0f, doc, "geographicUnit", metadata.getGeographicUnit());
            addDate(1.0f, doc, "timePeriodCoveredEnd", metadata.getTimePeriodCoveredEnd());
            addDate(1.0f, doc, "timePeriodCoveredStart", metadata.getTimePeriodCoveredStart());
            addDate(1.0f, doc, "dateOfCollection", metadata.getDateOfCollectionStart());
            addDate(1.0f, doc, "dateOfCollectionEnd", metadata.getDateOfCollectionEnd());
            addText(1.0f, doc, "country", metadata.getCountry());
            addText(1.0f, doc, "timeMethod", metadata.getTimeMethod());
            addText(1.0f, doc, "samplingProcedure", metadata.getSamplingProcedure());
            addText(1.0f, doc, "deviationsFromSampleDesign", metadata.getDeviationsFromSampleDesign());
            addText(1.0f, doc, "collectionMode", metadata.getCollectionMode());
            addText(1.0f, doc, "researchInstrument", metadata.getResearchInstrument());
            addText(1.0f, doc, "characteristicOfSources", metadata.getCharacteristicOfSources());
            addText(1.0f, doc, "accessToSources", metadata.getAccessToSources());
            addText(1.0f, doc, "dataCollectionSituation", metadata.getDataCollectionSituation());
            addText(1.0f, doc, "actionsToMinimizeLoss", metadata.getActionsToMinimizeLoss());
            addText(1.0f, doc, "controlOperations", metadata.getControlOperations());
            addText(1.0f, doc, "weighting", metadata.getWeighting());
            addText(1.0f, doc, "cleaningOperations", metadata.getCleaningOperations());
            addText(1.0f, doc, "studyLevelErrorNotes", metadata.getStudyLevelErrorNotes());
            List<StudyNote> studyNotes = metadata.getStudyNotes();
            for (Iterator it = studyNotes.iterator(); it.hasNext();) {
                StudyNote elem = (StudyNote) it.next();
                addText(1.0f, doc, "studyNoteType", elem.getType());
                addText(1.0f, doc, "studyNoteSubject", elem.getSubject());
                addText(1.0f, doc, "studyNoteText", elem.getText());
            }
            addText(1.0f, doc, "responseRate", metadata.getResponseRate());
            addText(1.0f, doc, "samplingErrorEstimate", metadata.getSamplingErrorEstimate());
            addText(1.0f, doc, "otherDataAppraisal", metadata.getOtherDataAppraisal());
            addText(1.0f, doc, "placeOfAccess", metadata.getPlaceOfAccess());
            addText(1.0f, doc, "originalArchive", metadata.getOriginalArchive());
            addText(1.0f, doc, "availabilityStatus", metadata.getAvailabilityStatus());
            addText(1.0f, doc, "collectionSize", metadata.getCollectionSize());
            addText(1.0f, doc, "studyCompletion", metadata.getStudyCompletion());
            addText(1.0f, doc, "confidentialityDeclaration", metadata.getConfidentialityDeclaration());
            addText(1.0f, doc, "specialPermissions", metadata.getSpecialPermissions());
            addText(1.0f, doc, "restrictions", metadata.getRestrictions());
            addText(1.0f, doc, "contact", metadata.getContact());
            addText(1.0f, doc, "citationRequirements", metadata.getCitationRequirements());
            addText(1.0f, doc, "depositorRequirements", metadata.getDepositorRequirements());
            addText(1.0f, doc, "conditions", metadata.getConditions());
            addText(1.0f, doc, "disclaimer", metadata.getDisclaimer());
            List<StudyRelMaterial> relMaterials = metadata.getStudyRelMaterials();
            for (Iterator it = relMaterials.iterator(); it.hasNext();) {
                StudyRelMaterial elem = (StudyRelMaterial) it.next();
                addText(1.0f, doc, "relatedMaterial", elem.getText());
            }
            List<StudyRelStudy> relStudies = metadata.getStudyRelStudies();
            for (Iterator it = relStudies.iterator(); it.hasNext();) {
                StudyRelStudy elem = (StudyRelStudy) it.next();
                addText(1.0f, doc, "relatedStudy", elem.getText());
            }
            List<StudyOtherRef> otherRefs = metadata.getStudyOtherRefs();
            for (Iterator it = otherRefs.iterator(); it.hasNext();) {
                StudyOtherRef elem = (StudyOtherRef) it.next();
                addText(1.0f, doc, "otherReferences", elem.getText());
            }
            

            for (StudyRelPublication elem : metadata.getStudyRelPublications()) {
                String publicationId = (elem.getIdType() != null ? elem.getIdType() + ":" : "") + elem.getIdNumber();
                if (elem.isReplicationData()) {
                    addText(1.0f, doc, "replicationFor", elem.getText());
                    addText(1.0f, doc, "replicationForId", publicationId);
                    addText(1.0f, doc, "replicationForURL", elem.getUrl());                    
                } else {
                    addText(1.0f, doc, "relatedPublications", elem.getText());
                    addText(1.0f, doc, "relatedPublicationsId", publicationId);
                    addText(1.0f, doc, "relatedPublicationsURL", elem.getUrl());
                }
            }
            

            /*     addText(1.0f,  doc,"relatedMaterial",metadata.getRelatedMaterial());
            addText(1.0f,  doc,"relatedPublications",metadata.getRelatedPublications());
            addText(1.0f,  doc,"otherReferences",metadata.getOtherReferences());
             */
            addText(1.0f, doc, "subtitle", metadata.getSubTitle());
            List<StudyKeyword> studyKeywords = metadata.getStudyKeywords();
            for (Iterator it = studyKeywords.iterator(); it.hasNext();) {
                StudyKeyword elem = (StudyKeyword) it.next();
                addText(1.0f, doc, "keywordVocabulary", elem.getVocab());
                addText(1.0f, doc, "keywordVocabulary", elem.getVocabURI());
            }
            addText(1.0f, doc, "protocol", study.getProtocol());
            addText(1.0f, doc, "authority", study.getAuthority());
            addText(1.0f, doc, "globalId", study.getGlobalId());
            List<StudySoftware> studySoftware = metadata.getStudySoftware();
            for (Iterator it = studySoftware.iterator(); it.hasNext();) {
                StudySoftware elem = (StudySoftware) it.next();
                addText(1.0f, doc, "studySoftware", elem.getName());
                addText(1.0f, doc, "studySoftwareVersion", elem.getSoftwareVersion());
            }
            List<StudyGrant> studyGrants = metadata.getStudyGrants();
            for (Iterator it = studyGrants.iterator(); it.hasNext();) {
                StudyGrant elem = (StudyGrant) it.next();
                addText(1.0f, doc, "studyGrantNumber", elem.getNumber());
                addText(1.0f, doc, "studyGrantNumberAgency", elem.getAgency());
            }
            List<StudyGeoBounding> studyGeoBounding = metadata.getStudyGeoBoundings();
            for (Iterator it = studyGeoBounding.iterator(); it.hasNext();) {
                StudyGeoBounding elem = (StudyGeoBounding) it.next();
                addText(1.0f, doc, "studyEastLongitude", elem.getEastLongitude());
                addText(1.0f, doc, "studyWestLongitude", elem.getWestLongitude());
                addText(1.0f, doc, "studyNorthLatitude", elem.getNorthLatitude());
                addText(1.0f, doc, "studySouthLatitude", elem.getSouthLatitude());
            }


            // Extented metadata fields: 
            
            
            String templateName = metadata.getStudy().getTemplate().getName();
            
            for (StudyFieldValue extFieldValue : metadata.getStudyFieldValues()) {
                try {
                    StudyField extStudyField = extFieldValue.getStudyField();
                    String extFieldName = extStudyField.getName();
                    String extFieldStrValue = extFieldValue.getStrValue();

                    if (extFieldName != null
                            && !extFieldName.equals("")
                            && extFieldStrValue != null
                            && !extFieldStrValue.equals("")) {
                        
                        addText(2.0f, doc, extFieldName, extFieldStrValue);

                        // Whenever we encounter an extended field actually 
                        // used in a study metadata, we want it to be searchable,
                        // on the "Advanced Search" page: (or do we?)
                        
                        //extFieldValue.getTemplateField().getStudyField().setAdvancedSearchField(true);
                        
                        // note that the above will only control the appearance of the 
                        // field on the Network-level Advanced Search page. (that 
                        // page uses the default list of advanced search fields, 
                        // which is simply all the lists from the StudyField DB
                        // table where isAdvancedField=true. Individual DVs 
                        // have their own lists of advanced fields. 
                        // As of now, we will make the field "advanced" only in
                        // its own dataverse: 
                        // (this is to be reviewed with Merce next week -- L.A. Feb. 22, 2012)
                        
                        
                        if (!metadata.getStudy().getOwner().getAdvSearchFields().contains(extStudyField)) {
                            metadata.getStudy().getOwner().getAdvSearchFields().add(extStudyField);
                        }
                        
                    }

                } catch (Exception ex) {
                    // do nothing - if we can't retrieve the field, we are 
                    // not going to index it, that's all.  
                }
            }   
            
            for (FileMetadata fileMetadata : sv.getFileMetadatas()) {
                addText(1.0f, doc, "fileDescription", fileMetadata.getDescription());
                
            }

            addText(1.0f, doc, "unf", metadata.getUNF());
//        writer = new IndexWriter(dir, true, getAnalyzer(), isIndexEmpty());
            logger.fine("Indexing study db id " + study.getId() + " (" + study.getStudyId() + ": " + metadata.getTitle() + ") from dataverse id " + study.getOwner().getId() + " (" + study.getOwner().getAlias() + ")");
            writer = new IndexWriter(dir, getAnalyzer(), isIndexEmpty(), IndexWriter.MaxFieldLength.UNLIMITED);
            writer.setUseCompoundFile(true);
            TaxonomyWriter taxo = new DirectoryTaxonomyWriter(taxoDir);
            List<CategoryPath> categoryPaths = new ArrayList<CategoryPath>();
            addFacet(categoryPaths, "dvName", study.getOwner().getName());
            addFacetDate(categoryPaths, "productionDate", metadata.getProductionDate());
            addFacetDate(categoryPaths, "distributionDate", metadata.getDistributionDate());
            for (Iterator it = studyDistributors.iterator(); it.hasNext();) {
                StudyDistributor studyDistributor = (StudyDistributor) it.next();
                addFacet(categoryPaths, "distributorName", studyDistributor.getName());
            }
            for (Iterator it = studyAuthors.iterator(); it.hasNext();) {
                StudyAuthor elem = (StudyAuthor) it.next();
                addFacet(categoryPaths, "authorName", elem.getName());
                addFacet(categoryPaths, "authorAffiliation", elem.getAffiliation());
            }
            addFacet(categoryPaths, "country", metadata.getCountry());
            for (Iterator it = keywords.iterator(); it.hasNext();) {
                StudyKeyword elem = (StudyKeyword) it.next();
                addFacet(categoryPaths, "keywordValue", elem.getValue());
            }
            for (Iterator it = topicClassifications.iterator(); it.hasNext();) {
                StudyTopicClass elem = (StudyTopicClass) it.next();
                if (elem.getValue() != null && (!elem.getValue().equals("")) && elem.getVocab() != null && (!elem.getVocab().equals(""))) {
                    addFacet(categoryPaths, "topicClassValueParensVocab", elem.getValue().trim() + " (" + elem.getVocab().trim() + ")");
                }
            }

            CategoryDocumentBuilder categoryDocBuilder = new CategoryDocumentBuilder(taxo);
            categoryDocBuilder.setCategoryPaths(categoryPaths);
            categoryDocBuilder.build(doc);
            writer.addDocument(doc);
            // warnings from https://svn.apache.org/repos/asf/lucene/dev/tags/lucene_solr_3_5_0/lucene/contrib/facet/src/examples/org/apache/lucene/facet/example/simple/SimpleIndexer.java
            // we commit changes to the taxonomy index prior to committing them to the search index.
            // this is important, so that all facets referred to by documents in the search index 
            // will indeed exist in the taxonomy index.
            taxo.commit();
            writer.commit();
            // close the taxonomy index and the index - all modifications are 
            // now safely in the provided directories: indexDir and taxoDir.
            taxo.close();
            writer.close();
            
            writerVar = new IndexWriter(dir, getAnalyzer(), isIndexEmpty(), IndexWriter.MaxFieldLength.UNLIMITED);
            
            
            StudyFile studyFile = null;
            DataTable dataTable = null; 
            List<DataVariable> dataVariables = null; 

            for (FileMetadata fileMetadata : sv.getFileMetadatas()) {
                //TODO: networkDataFile
                studyFile = fileMetadata.getStudyFile();
                if (studyFile instanceof TabularDataFile) {
                    dataTable = ((TabularDataFile) studyFile).getDataTable();
                    if (dataTable != null) {
                        dataVariables = dataTable.getDataVariables();
                        for (int j = 0; j < dataVariables.size(); j++) {
                            Document docVariables = new Document();
                            addText(1.0f, docVariables, "varStudyId", study.getId().toString());
                            addText(1.0f, docVariables, "varStudyFileId", studyFile.getId().toString());
                            DataVariable dataVariable = dataVariables.get(j);
                            addText(1.0f, docVariables, "varId", dataVariable.getId().toString());
                            addText(1.0f, docVariables, "varName", dataVariable.getName());
                            addText(1.0f, docVariables, "varLabel", dataVariable.getLabel());
                            writerVar.addDocument(docVariables);
                        }
                        dataVariables = null; 
                        dataTable = null; 
                    }
                }
                studyFile = null; 
            }
            
            writerVar.close();

            writerFileMeta = new IndexWriter(dir, getAnalyzer(), isIndexEmpty(), IndexWriter.MaxFieldLength.UNLIMITED);
            
            for (FileMetadata fileMetadata : sv.getFileMetadatas()) {
                studyFile = fileMetadata.getStudyFile();
                if (studyFile instanceof SpecialOtherFile) {
                    Document docFileMetadata = new Document();
                    // the "id" is the database id of the *study*; - for 
                    // compatibility with the study-level index files. 
                    addKeyword(docFileMetadata, "id", study.getId().toString());
                    addText(1.0f, docFileMetadata, "studyFileId", studyFile.getId().toString());
                    
                    List<FileMetadataFieldValue> fileMetadataFieldValues = fileMetadata.getStudyFile().getFileMetadataFieldValues(); 
                    for (int j = 0; j < fileMetadataFieldValues.size(); j++) {
                                               
                        String fieldValue = fileMetadataFieldValues.get(j).getStrValue();
                        
                        FileMetadataField fmf = fileMetadataFieldValues.get(j).getFileMetadataField();
                        String fileMetadataFieldName = fmf.getName(); 
                        String fileMetadataFieldFormatName = fmf.getFileFormatName(); 
                        String indexFileName = fileMetadataFieldFormatName + "-" + fileMetadataFieldName;
                        
                        addText(1.0f, docFileMetadata, indexFileName, fieldValue); 
                        
                    }                   
                    writerFileMeta.addDocument(docFileMetadata);
                }
                studyFile = null; 
            }
            
            writerFileMeta.close(); 
           
            
            writerVersions = new IndexWriter(dir, new WhitespaceAnalyzer(), isIndexEmpty(), IndexWriter.MaxFieldLength.UNLIMITED);
            for (StudyVersion version : study.getStudyVersions()) {
                // The current(released) version UNF is indexed in the main document
                // only index previous(archived) version UNFs here
                if (version.isArchived()) {
                    Document docVersions = new Document();
                    addKeyword(docVersions, "versionStudyId", study.getId().toString());
                    addText(1.0f, docVersions, "versionId", version.getId().toString());
                    addText(1.0f, docVersions, "versionNumber", version.getVersionNumber().toString());
                    addKeyword(docVersions, "versionUnf", version.getMetadata().getUNF());
                    writerVersions.addDocument(docVersions);
                }
            }
            writerVersions.close();
            logger.fine("End indexing study " + study.getStudyId());
        }
    }
    
    /* 
     * Experimental method; disregard. --L.A.
     */
    protected void updateDocument(Document doc, long studyId) throws IOException{
        try {
            IndexWriter writer = new IndexWriter(dir, getAnalyzer(), isIndexEmpty(), IndexWriter.MaxFieldLength.UNLIMITED);
            writer.updateDocument(new Term("id", Long.toString(studyId)), doc);
            // TODO: 
            // Figure out, eventually, what to do with the variable and file 
            // metadata searches here. 
            // -- L.A. 
            /*
             * our deleteDocument() method contains these 2 lines, below, 
             * in addition to the deleteDocument() method for the term based on 
             * "id", as above. 
                reader.deleteDocuments(new Term("varStudyId",Long.toString(studyId)));
                reader.deleteDocuments(new Term("versionStudyId",Long.toString(studyId)));
             */
            writer.commit();
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }  
    }
    
    /* 
     * Experimental method; disregard. --L.A.
     */
    protected void updateStudyDocument(long studyId, String field, String value) throws IOException {
        IndexReader reader = IndexReader.open(dir, false);  
        
        try {
            if (reader != null) {
                TermDocs matchingDocuments = reader.termDocs();

                if (matchingDocuments != null) {
                    int c = 1;
                    if (matchingDocuments.next()) {
                        // We only expect 1 document when searching by study id.
                        Document studyDocument = reader.document(matchingDocuments.doc());

                        logger.fine("processing matching document number " + c++);
                        if (studyDocument != null) {
                            logger.fine("got a non-zero doc;");

                            reader.close(); 
                            reader = null;

                            logger.fine("deleted the document;");

                            //updateDocument(studyDocument, studyId);
                            IndexWriter localWriter = new IndexWriter(dir, getAnalyzer(), isIndexEmpty(), IndexWriter.MaxFieldLength.UNLIMITED);
                            localWriter.updateDocument(new Term("id", Long.toString(studyId)), studyDocument);
                            
                            localWriter.commit();
                            localWriter.close();
                            logger.fine("wrote the updated version of the document;");

                        }
                    }
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
    
    /*
     * Experimental method: If the VDC has collections defined, the Indexer runs
     * the queries defining them, thus finding all the study ids that are 
     * "attached" to the dataverse via these collections. 
     * This way we can periodically update the index for all such studies; and then 
     * we can rely on a query "dvnOwnerId = foo" to find *all* the studies in 
     * the dataverse - both directly in the VDC by "owner_id" and linked through 
     * collections (at least per the last time the index was rebuilt). 
     */  
    
    List<Long> findStudiesInCollections (VDC vdc) {
        List <Long> linkedStudyIds = null;
        List <Query> collectionQueries = getCollectionQueriesForSubnetworkIndexing(vdc);
        
        if (collectionQueries != null && collectionQueries.size() > 0) {
            logger.fine("running combined collections query for the vdc id "+vdc.getId()+", "+vdc.getName()+"; "+collectionQueries.size()+" queries total.");
            
            BooleanQuery queryAcrossAllCollections = new BooleanQuery();
            for (Query collectionQuery : collectionQueries) {
                queryAcrossAllCollections.add(collectionQuery, BooleanClause.Occur.SHOULD);
            }
         
            try {
                linkedStudyIds = getHitIds(queryAcrossAllCollections);
            } catch (Exception ex) {
                logger.warning("Caught exception while executing combined colleciton query on VDC "+vdc.getId());
                ex.printStackTrace();
            }
            
            
        }
        
        return linkedStudyIds;
    }

    protected Analyzer getAnalyzer(){
//        return new StandardAnalyzer();
//        return new WhitespaceAnalyzer();
//        return new DataverseNetworkAnalyzer();
        return new DVNAnalyzer();
    }

    protected void addDate(float boost, Document doc,String key, String value){
        addText( 1.0f, doc, key, value );
        addKeyword( doc,key, value );
    }
    
    protected void addKeyword(Document doc,String key, String value){
        if (value != null && value.length()>0){
            doc.add(new Field(key,value.toLowerCase().trim(), Field.Store.YES, Field.Index.NOT_ANALYZED));
            doc.add(new Field(key,value.trim(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        }
    }

    protected void addText(float boost, Document doc,String key, String value){
        if (value != null && value.length()>0){
            Field f1 = new Field(key,value.toLowerCase().trim(),Field.Store.YES, Field.Index.ANALYZED);
            Field f2 = new Field(key,value.trim(), Field.Store.YES, Field.Index.NOT_ANALYZED);
            f1.setBoost(boost);
            f2.setBoost(boost);
            doc.add(f1);
            doc.add(f2);
        }
    }

    private void addFacet(List<CategoryPath> categoryPaths, String key, String value) {
        if (value != null && value.length() > 0) {
            categoryPaths.add(new CategoryPath(key.trim(), value.trim()));
        }
    }

    private void addFacetDate(List<CategoryPath> categoryPaths, String key, String value) {
        boolean isValid = DateUtil.validateDate(value);
        if (isValid) {
            addFacet(categoryPaths, key, value);
        }
    }

    public List search(List <Long> studyIds, List <SearchTerm> searchTerms) throws IOException{
        logger.fine("Start search: "+DateTools.dateToString(new Date(), Resolution.MILLISECOND));
        Long[] studyIdsArray = null;
        if (studyIds != null) {
            studyIdsArray = studyIds.toArray(new Long[studyIds.size()]);
            Arrays.sort(studyIdsArray);
        }
        List <Long> results = null;
        List <BooleanQuery> searchParts = new ArrayList();
       
        // "study-level search" is our "normal", default search, that is 
        // performed on the study metadata keywords.
        boolean studyLevelSearch = false; 
        boolean containsStudyLevelAndTerms = false; 
 
        // We also support searches on variables and file-level metadata:
        // We do have to handle these 2 separately, because of the 2 different
        // levels of granularity: one searches on variables, the other on files.
        boolean variableSearch = false;
        boolean fileMetadataSearch = false; 
        
        // And the boolean below indicates any file-level searche - i.e., 
        // either a variable, or file metadata search.  
        // -- L.A. 
        boolean fileLevelSearch = false; 

        
        List <SearchTerm> studyLevelSearchTerms = new ArrayList();
        List <SearchTerm> variableSearchTerms = new ArrayList();
        List <SearchTerm> fileMetadataSearchTerms = new ArrayList();
        
        for (Iterator it = searchTerms.iterator(); it.hasNext();){
            SearchTerm elem = (SearchTerm) it.next();
            logger.fine("INDEXER: processing term; name="+elem.getFieldName()+"; value="+elem.getValue());
            if (elem.getFieldName().equals("variable")){
//                SearchTerm st = dvnTokenizeSearchTerm(elem);
//                variableSearchTerms.add(st);
                variableSearchTerms.add(elem);
                variableSearch = true;
                
            } else if (isFileMetadataField(elem.getFieldName())) {
                fileMetadataSearch = true; 
                fileMetadataSearchTerms.add(elem);
                
            } else {
//                SearchTerm nvst = dvnTokenizeSearchTerm(elem);
//                nonVariableSearchTerms.add(nvst);
                if (elem.getOperator().equals("=")){
                    containsStudyLevelAndTerms = true;
                }
                studyLevelSearchTerms.add(elem);
                studyLevelSearch = true;
                
            }
        }
        
        // For now we are not supporting searches on variables and file-level
        // metadata *at the same time*. 
        // -- L.A. 
        
        if (variableSearch && fileMetadataSearch) {
            throw new IOException ("Unsupported search term combination! "+
                    "Searches on both variables and file-level metadata "+
                    "at the same time are not supported.");
        }
        
        if (variableSearch || fileMetadataSearch) {
            fileLevelSearch = true; 
        } 
        
        List <Long> nvResults = null;
        List<Long> filteredResults = null;
        
        // If there are "AND"-type Study-level search terms in the search, 
        // let's run it now:
        
        if ( containsStudyLevelAndTerms ) {
            BooleanQuery searchTermsQuery = andSearchTermClause(studyLevelSearchTerms);
            logger.fine("INDEXER: search terms query (native): "+searchTermsQuery.toString());
            searchParts.add(searchTermsQuery);
            BooleanQuery searchQuery = andQueryClause(searchParts);
            logger.fine("Start hits: " + DateTools.dateToString(new Date(), Resolution.MILLISECOND));
            logger.fine("INDEXER: search query (native): "+searchQuery.toString());
            nvResults = getHitIds(searchQuery);
            logger.fine("Done hits: " + DateTools.dateToString(new Date(), Resolution.MILLISECOND));
            logger.fine("Start filter: " + DateTools.dateToString(new Date(), Resolution.MILLISECOND));
            logger.fine("before intersectionResults... studyIds: " + studyIds + " nvResults: " + nvResults);
            filteredResults = studyIds != null ? intersectionResults(nvResults, studyIdsArray) : nvResults;
            logger.fine("after intersectionResults... filteredResults: " + filteredResults);
            logger.fine("Done filter: " + DateTools.dateToString(new Date(), Resolution.MILLISECOND));
        }
        
        // If there is a file-level portion of the search, we'll run it now, 
        // combining the results with (or, rather filtering them against) the
        // hit list produced by the study-level search above, or supplied to this
        // method as an argument (if any). 
        // IMPORTANT: 
        // do note that this logic assumes that the file-level search can be 
        // EITHER on variables, or on file-level metadata; but never on both. 
        // -- L.A. 
        
        logger.fine("fileLevelSearch: " + fileLevelSearch);
        if (filteredResults != null) {
            logger.fine("filteredResults.size: " + filteredResults.size());
        }
        logger.fine("containsStudyLevelAndTerms: " + containsStudyLevelAndTerms);
        logger.fine("variableSearch: " + variableSearch);
        if (fileLevelSearch){
            if (containsStudyLevelAndTerms && (filteredResults.size() > 0 )) {
                if (variableSearch) {
                    logger.fine("Start nonvar search variables: " + DateTools.dateToString(new Date(), Resolution.MILLISECOND));
                    results = searchVariables(filteredResults, variableSearchTerms, true); // get var ids
                    logger.fine("Done nonvar search variables: " + DateTools.dateToString(new Date(), Resolution.MILLISECOND));
                } else if (fileMetadataSearch) {
                    logger.fine("Start file-level metadata search; searching for file ids." + DateTools.dateToString(new Date(), Resolution.MILLISECOND));
                    results = searchFileMetadata(filteredResults, fileMetadataSearchTerms, true); // get var ids
                    logger.fine("Done searching for file ids, on file-level metadata: " + DateTools.dateToString(new Date(), Resolution.MILLISECOND));
                }
            } else {
                logger.fine("Start file-level metadata search: " + DateTools.dateToString(new Date(), Resolution.MILLISECOND));
                
                if (studyLevelSearch && !containsStudyLevelAndTerms) {
                    if (variableSearch) {
                        results = searchVariables(studyIds, variableSearchTerms, false);
                    } else if (fileMetadataSearch) {
                        results = searchFileMetadata(studyIds, fileMetadataSearchTerms, false);
                    }
                    if (results != null) {
                        studyIdsArray = results.toArray(new Long[results.size()]);
                        Arrays.sort(studyIdsArray);
                    }
                    BooleanQuery searchQuery = new BooleanQuery();
                    List <TermQuery> termQueries = orLongEqSearchTermQueries(results, "id");
                    for (Iterator clausesIter = termQueries.iterator(); clausesIter.hasNext();){
                        TermQuery termQuery = (TermQuery) clausesIter.next();
                        searchQuery.add(termQuery, BooleanClause.Occur.SHOULD);
                    }
                    
                    for (Iterator it = searchTerms.iterator(); it.hasNext();) {
                        SearchTerm elem = (SearchTerm) it.next();
                        if (!elem.getFieldName().equalsIgnoreCase("variable") 
                                &&
                                !isFileMetadataField(elem.getFieldName())) {
                            
                            Term term = new Term(elem.getFieldName(), elem.getValue());
                            TermQuery termQuery = new TermQuery(term);
                            if (elem.getOperator().equals("=")) {
                                searchQuery.add(termQuery, BooleanClause.Occur.MUST);
                            } else {
                                searchQuery.add(termQuery, BooleanClause.Occur.MUST_NOT);
                            }
                        }
                    }
                    List <Long> studyIdResults = getHitIds(searchQuery);
                    
                    if (variableSearch) {
                        results = searchVariables(studyIdResults, variableSearchTerms, true); // get var ids
                    } else if (fileMetadataSearch) {
                        results = searchFileMetadata(studyIdResults, fileMetadataSearchTerms, true); // get file ids
                    }
                } else {
                    List<Long> finalResults = filteredResults != null ? filteredResults : studyIds;
                    logger.fine("in else... this was false: (studyLevelSearch && !containsStudyLevelAndTerms) ... finalResults: " + finalResults);
                    if (variableSearch) {
                        results = searchVariables(finalResults, variableSearchTerms, true); // get var ids
                    } else if (fileMetadataSearch) {
                        results = searchFileMetadata(finalResults, fileMetadataSearchTerms, true); // get file ids
                    }
                }
                logger.fine("Done searching on file-level metadata: " + DateTools.dateToString(new Date(), Resolution.MILLISECOND));
            }
        } else {
            results = filteredResults;
        }
        logger.fine("Done search: "+DateTools.dateToString(new Date(), Resolution.MILLISECOND));

        return results;

    }

    // returns a list of study version ids for a given unf
    public List<Long> searchVersionUnf(List <Long> studyIds, String unf) throws IOException {
        List <Long> results = null;
        Query unfQuery = null;
        SearchTerm st = new SearchTerm();
        st.setFieldName("versionUnf");
        st.setValue(unf);
        Term t = new Term(st.getFieldName(), st.getValue().toLowerCase().trim());
        unfQuery = new TermQuery(t);
        List<Document> documents = getVersionUnfHits(unfQuery);
        results = studyIds != null ? getVersionHitIds(getFilteredByStudyIdVersionHitIds(documents, studyIds)) : getVersionHitIds(documents);
        return results;
    }

    private String getDVNTokenString(final String value) {
        PorterStemFilter p = new PorterStemFilter(new DVNTokenizer(new StringReader(value)));
        TermAttribute ta = p.addAttribute(TermAttribute.class);
        StringBuffer dvnValueSb = new StringBuffer();
        try {
            while (p.incrementToken()) {
                dvnValueSb.append(new String(ta.termBuffer()).substring(0, ta.termLength()) + " ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String dvnValue = dvnValueSb.toString();
        return dvnValue;
    }

    /*
     * The 2 methods below are for filtering lists of variable and file metadata
     * search results, respectively, against a list of study ids. 
     * Again, we maintain 2 distinct methods here, since variable and file metadata
     * searching is different in its granularity (study->studyfile->variable vs. 
     * study->studyfile, respectively).
     */
    
    private List <Long> intersectionVarDocResults(final List<Document> results1, final List<Long> results2) throws IOException {
        List <Long>  mergeResults = new ArrayList();
        for (Iterator it = results1.iterator(); it.hasNext();){
            Document d = (Document) it.next();
            Field studyId = d.getField("varStudyId");
            String studyIdStr = studyId.stringValue();
            Long studyIdLong = Long.valueOf(studyIdStr);
            if (results2.contains(studyIdLong)) {
                Field varId = d.getField("varId");
                String varIdStr = varId.stringValue();
                Long varIdLong = Long.valueOf(varIdStr);
                if (!mergeResults.contains(varIdLong)) {
                    mergeResults.add(varIdLong);
                }
            }
        }
        return mergeResults;
    }

    private List <Long> intersectionFileDocResults(final List<Document> results1, final List<Long> results2) throws IOException {
        List <Long>  mergeResults = new ArrayList();
        for (Iterator it = results1.iterator(); it.hasNext();){
            Document d = (Document) it.next();
            Field studyId = d.getField("id");
            String studyIdStr = studyId.stringValue();
            Long studyIdLong = Long.valueOf(studyIdStr);
            if (results2.contains(studyIdLong)) {
                Field varId = d.getField("studyFileId");
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


        initIndexSearcher();
//        Hits hits = exactMatchQuery(searcher, field, value);
        DocumentCollector c = exactMatchQuery(searcher, field, value);
        List <ScoreDoc> s = c.getStudies();
        for (int i=0; i < s.size(); i++){
//        for (int i = 0; i < hits.length(); i++) {
            Document d = searcher.doc(s.get(i).doc);
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
//        QueryParser parser = new QueryParser("abstract",new DVNAnalyzer());
//        QueryParser parser = new QueryParser(Version.LUCENE_30,"abstract",new DVNSearchAnalyzer());
        logger.fine("INDEXER: adhoc query: "+adhocQuery);
        
        QueryParser parser = new QueryParser(Version.LUCENE_30,"abstract",new DVNAnalyzer());
//        QueryParser parser = new QueryParser("abstract",new StandardAnalyzer());
        parser.setDefaultOperator(QueryParser.AND_OPERATOR);
        Query query=null;
        try {
            query = parser.parse(adhocQuery);
            logger.fine("INDEXER: parsed adhoc query: "+query.toString());
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return getHitIds(getHits(query));
//        return getHitIds(query);
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

    // It appears that this method (searchVariables(SearchTerm) is not being
    // used anymore; it also appears that it would *not* work if used - 
    // because getHitIds(Query) relies on the field "id" in the returned 
    // Documents, but the field is not present in documents created for variable 
    // info. 
    // Should it just be removed, to avoid confusion? 
    // -- L.A. 
    /*
     * 
     *
    public List searchVariables(SearchTerm searchTerm) throws IOException {
        Query indexQuery = null;
        if (searchTerm.getFieldName().equalsIgnoreCase("variable")){
//            indexQuery = buildVariableQuery(searchTerm.getValue().toLowerCase().trim());
            indexQuery = buildVariableQuery(searchTerm);
        }
        return getHitIds(indexQuery);
    }
    */

    public List searchVariables(List <Long> studyIds,SearchTerm searchTerm) throws IOException {
        BooleanQuery indexQuery = null;
        BooleanQuery searchQuery = new BooleanQuery();
        BooleanQuery.setMaxClauseCount(dvnMaxClauseCount);
        if (studyIds != null) {
            searchQuery.add(orIdSearchTermClause(studyIds, "varStudyId"), BooleanClause.Occur.MUST);
        }
        if (searchTerm.getFieldName().equalsIgnoreCase("variable")){
            indexQuery = buildVariableQuery(searchTerm);
            if (searchTerm.getOperator().equals("=")) {
                searchQuery.add(indexQuery, BooleanClause.Occur.MUST);
            } else {
                searchQuery.add(indexQuery, BooleanClause.Occur.MUST_NOT);
            }
        }
        List <Document> variableResults = getHits(searchQuery);
        List <Long> variableIdResults = getVariableHitIds(variableResults);
        // TODO: 
        // Double-check if the intersectionVarDocResults() below - i.e., filtering
        // the hit list against the list of supplied study ids - is necessary at all. 
        // I would think not - because the study IDs were already added to the
        // search query, above. -- L.A.
        List<Long> finalResults = studyIds != null ? intersectionVarDocResults(variableResults, studyIds) : variableIdResults;
        return finalResults;
    }

    public List searchVariables(List<Long> studyIds, List<SearchTerm> searchTerms, boolean varIdReturnValues) throws IOException {
        BooleanQuery searchQuery = new BooleanQuery();
        BooleanQuery.setMaxClauseCount(dvnMaxClauseCount);
        if (studyIds != null) {
            searchQuery.add(orIdSearchTermClause(studyIds, "varStudyId"), BooleanClause.Occur.MUST);
        }
        for (Iterator it = searchTerms.iterator(); it.hasNext();) {
            SearchTerm elem = (SearchTerm) it.next();
            BooleanQuery indexQuery = null;
            if (elem.getFieldName().equalsIgnoreCase("variable")) {
                indexQuery = buildVariableQuery(elem);
                if (elem.getOperator().equals("=")) {
                    searchQuery.add(indexQuery, BooleanClause.Occur.MUST);
                } else {
                    searchQuery.add(indexQuery, BooleanClause.Occur.MUST_NOT);
                }
            }
        }
        List<Long> finalResults = null;
        // TODO: 
        // Double-check if the intersection(Var)DocResults() below - i.e., filtering
        // the hit list against the list of supplied study ids - is necessary at all. 
        // I would think not - because the study IDs were already added to the
        // search query, above. -- L.A.
        if (varIdReturnValues) {
            List<Document> variableResults = getHits(searchQuery);
            List<Long> variableIdResults = getVariableHitIds(variableResults);
            finalResults = studyIds != null ? intersectionVarDocResults(variableResults, studyIds) : variableIdResults;
        } else {
            List<Long> studyIdResults = getVariableHitStudyIds(searchQuery); // gets the study ids
            finalResults = studyIds != null ? intersectionResults(studyIdResults, studyIds) : studyIdResults;
        }
        return finalResults;
    }
    
    /* 
     * Similar method for running a search on file-level metadata keywords.
     * -- L.A. 
     **/ 
    
    public List searchFileMetadata(List<Long> studyIds, List<SearchTerm> searchTerms, boolean fileIdReturnValues) throws IOException {
        BooleanQuery searchQuery = new BooleanQuery();
        BooleanQuery.setMaxClauseCount(dvnMaxClauseCount);
        if (studyIds != null) {
            searchQuery.add(orIdSearchTermClause(studyIds, "id"), BooleanClause.Occur.MUST);
        }
        for (Iterator it = searchTerms.iterator(); it.hasNext();) {
            SearchTerm elem = (SearchTerm) it.next();
            BooleanQuery indexQuery = null;
            // Determine if this is a file-level metadata search term:
            if (isFileMetadataField(elem.getFieldName())) {
                indexQuery = buildFileMetadataQuery(elem);
                logger.fine("INDEXER: filemetadata element query (native): "+indexQuery.toString());
                if (elem.getOperator().equals("=")) {
                    // We only support "=" on file metadata, for now, anyway. 
                    // -- L.A. 
                    searchQuery.add(indexQuery, BooleanClause.Occur.MUST);
                } else {
                    searchQuery.add(indexQuery, BooleanClause.Occur.MUST_NOT);
                }
            }
        }
        
        logger.fine("INDEXER: filemetadata combined query (native): "+searchQuery.toString());

        List<Long> finalResults = null;
        // TODO: 
        // Double-check if the intersection(File)DocResults() below - i.e., filtering
        // the hit list against the list of supplied study ids - is necessary at all. 
        // I would think not - because the study IDs were already added to the
        // search query, above. -- L.A.
        if (fileIdReturnValues) {
            List<Document> fileMetadataResults = getHits(searchQuery);
            List<Long> fileMetadataIdResults = getFileMetadataHitIds(fileMetadataResults);
            finalResults = studyIds != null ? intersectionFileDocResults(fileMetadataResults, studyIds) : fileMetadataIdResults;
        } else {
            List<Long> studyIdResults = getFileMetadataHitStudyIds(searchQuery); // gets the study ids
            finalResults = studyIds != null ? intersectionResults(studyIdResults, studyIds) : studyIdResults;
        }
        return finalResults;
    }

    /* 
     * Method for determining if this is a file-level metadata field. 
     * The way this is currently implemented, we make a studyFieldService lookup
     * on the field and the file format names (that are encoded in the index
     * name). 
     */
    
    boolean isFileMetadataField (String fieldName) {
        int i = fieldName.indexOf('-'); 
        
        if (i <= 0) {
            return false; 
        }
        
        String prefix = fieldName.substring(0, i);
        String suffix = fieldName.substring(i+1); 

        FileMetadataField fmf = null; 
        
        try {
            Context ctx = new InitialContext();
            studyFieldService = (StudyFieldServiceLocal) ctx.lookup("java:comp/env/studyField");
        } catch (Exception ex) {
            logger.fine("Caught an exception looking up StudyField Service; " + ex.getMessage());
        }
        
        if (studyFieldService == null) {
            logger.warning("No StudyField Service; exiting file-level metadata ingest.");
            return false;
        }
        
        fmf = studyFieldService.findFileMetadataFieldByNameAndFormat(suffix, prefix); 
        
        if (fmf != null) {
            logger.fine("Checked the field "+fieldName+" with the StudyFieldService; ok.");
            return true; 
        }
        
        return false; 
    }
    
    /*
     * Method for determining if prefix-wildcard seraches are supported on this
     * file metadata field. 
     * TODO: Merge this method with the one above somehow; or add some caching
     * for the known filemetadata fields, in such a way that we don't have to
     * be doing these lookups constantly. 
     */
    boolean isPrefixSearchableFileMetadataField (String fieldName) {
        int i = fieldName.indexOf('-'); 
        
        if (i <= 0) {
            return false; 
        }
        
        String prefix = fieldName.substring(0, i);
        String suffix = fieldName.substring(i+1); 

        FileMetadataField fmf = null; 
        
        try {
            if (studyFieldService == null) {
                Context ctx = new InitialContext();
                studyFieldService = (StudyFieldServiceLocal) ctx.lookup("java:comp/env/studyField");
            }
        } catch (Exception ex) {
            logger.fine("Caught an exception looking up StudyField Service; " + ex.getMessage());
        }
        
        if (studyFieldService == null) {
            logger.warning("No StudyField Service; exiting file-level metadata ingest.");
            return false;
        }
        
        fmf = studyFieldService.findFileMetadataFieldByNameAndFormat(suffix, prefix); 
        
        if (fmf != null) {
            logger.fine("Checked the field "+fieldName+" with the StudyFieldService; ok.");
            return fmf.isPrefixSearchable(); 
        }
        
        return false; 
    }
    
    private List<Document> getHits( Query query ) throws IOException {
        List <Document> documents = new ArrayList();
        if (query != null){
            initIndexSearcher();
            logger.fine("Start searcher: " + DateTools.dateToString(new Date(), Resolution.MILLISECOND));
            DocumentCollector s = new DocumentCollector(searcher);
            searcher.search(query, s);
            List <ScoreDoc> hits = s.getStudies();
            for (int i = 0; i < hits.size(); i++) {
                documents.add(searcher.doc(((ScoreDoc)hits.get(i)).doc));
            }
            logger.fine("done iterate: " + DateTools.dateToString(new Date(), Resolution.MILLISECOND));
            searcher.close();
        }
        return documents;
    }

    private List<Document> getVersionUnfHits( Query query ) throws IOException {
         List <Document> documents = new ArrayList();
        if (query != null){
            initIndexSearcher();
            logger.fine("Start searcher: " + DateTools.dateToString(new Date(), Resolution.MILLISECOND));
            DocumentCollector s = new DocumentCollector(searcher);
            searcher.search(query, s);
            List hits = s.getStudies();
            for (int i = 0; i < hits.size(); i++) {
                ScoreDoc sd = (ScoreDoc) hits.get(i);
                Document d = searcher.doc(sd.doc);
                documents.add(d);
//                documents.add(searcher.doc(((ScoreDoc)hits.get(i)).doc));
            }
            logger.fine("done iterate: " + DateTools.dateToString(new Date(), Resolution.MILLISECOND));
            searcher.close();
        }
        return documents;
    }

 
    private List getHitIds( Query query) throws IOException {
        ArrayList matchIds = new ArrayList();
        LinkedHashSet matchIdsSet = new LinkedHashSet();
        if (query != null){
            initIndexSearcher();
            logger.fine("Start searcher: " + DateTools.dateToString(new Date(), Resolution.MILLISECOND));
            DocumentCollector s = new DocumentCollector(searcher);
            searcher.search(query, s);
//            searcher.close();
            logger.fine("done searcher: " + DateTools.dateToString(new Date(), Resolution.MILLISECOND));
            logger.fine("Start iterate: " + DateTools.dateToString(new Date(), Resolution.MILLISECOND));
            List hits = s.getStudies();
            for (int i = 0; i < hits.size(); i++) {
                ScoreDoc sd = (ScoreDoc) hits.get(i);
                Document d = searcher.doc(sd.doc);                
                try {
                    Field studyId = d.getField("id");
                    String studyIdStr = studyId.stringValue();
                    Long studyIdLong = Long.valueOf(studyIdStr);
                    matchIdsSet.add(studyIdLong);
                } catch (Exception ex) {
                    logger.fine("Query for " + query + "matched but id was null, dumping Lucene doc...\n" + d);
                    ex.printStackTrace();
                }
            }
            logger.fine("done iterate: " + DateTools.dateToString(new Date(), Resolution.MILLISECOND));
            searcher.close();
        }
        matchIds.addAll(matchIdsSet);
        return matchIds;
    }

    ResultsWithFacets searchNew(DvnQuery dvnQuery) throws IOException {
        logger.fine("called searchNew() in Indexer.java");

        List<CategoryPath> facetsOfInterest = new ArrayList<CategoryPath>();

        if (dvnQuery.facetsToQuery != null) {
            for (int i = 0; i < dvnQuery.facetsToQuery.size(); i++) {
                CategoryPath facetToAdd = dvnQuery.facetsToQuery.get(i);
                if (!facetsOfInterest.contains(facetToAdd)) {
                    logger.fine("in searchNew in Indexer, adding to facetsOfInterest: facet " + facetToAdd);
                    facetsOfInterest.add(facetToAdd);
                }
            }
        }
        Query baseQuery = dvnQuery.getQuery();
        DocumentCollector s = new DocumentCollector(searcher);
        TaxonomyReader taxo = new DirectoryTaxonomyReader(taxoDir);
        FacetSearchParams facetSearchParams = new FacetSearchParams();
        facetSearchParams.addFacetRequest(new CountFacetRequest(new CategoryPath("dvName"), 10));
        facetSearchParams.addFacetRequest(new CountFacetRequest(new CategoryPath("authorName"), 10));
        facetSearchParams.addFacetRequest(new CountFacetRequest(new CategoryPath("authorAffiliation"), 10));
        facetSearchParams.addFacetRequest(new CountFacetRequest(new CategoryPath("country"), 10));
        facetSearchParams.addFacetRequest(new CountFacetRequest(new CategoryPath("distributorName"), 10));
        facetSearchParams.addFacetRequest(new CountFacetRequest(new CategoryPath("productionDate"), 10));
        facetSearchParams.addFacetRequest(new CountFacetRequest(new CategoryPath("distributionDate"), 10));
        facetSearchParams.addFacetRequest(new CountFacetRequest(new CategoryPath("keywordValue"), 10));
        facetSearchParams.addFacetRequest(new CountFacetRequest(new CategoryPath("topicClassValueParensVocab"), 10));

        int numFacetsOfInterest = facetsOfInterest != null ? facetsOfInterest.size() : 0;
        CategoryPath[] facetsArray = new CategoryPath[numFacetsOfInterest];
        for (int i = 0; i < numFacetsOfInterest; i++) {
            facetsArray[i] = facetsOfInterest.get(i);
        }

        Query q2;
        if (facetsArray.length > 0) {
            q2 = DrillDown.query(baseQuery, facetsArray);
        } else {
            q2 = baseQuery;
        }

        initIndexSearcher();
        FacetsCollector facetsCollector = new FacetsCollector(facetSearchParams, r, taxo);

        logger.fine("\n--BEGIN query dump (from searchNew)--\n" + q2 + "\n--END query dump (from searchNew)--");
        Collector collector = MultiCollector.wrap(s, facetsCollector);
        try {
            searcher.search(q2, collector);
        } catch (NullPointerException npe) {
            logger.fine("Query contains null, returning no results: " + q2);
            return new ResultsWithFacets();
        } catch (Exception e) {
            logger.fine("Query may contain null, returning no results: " + q2);
            return new ResultsWithFacets();
        }
        List<FacetResult> facetResults = facetsCollector.getFacetResults();
        ResultsWithFacets resultsWithFacets = new ResultsWithFacets();
        resultsWithFacets.setResultList(facetResults);
        ArrayList matchIds = new ArrayList();
        LinkedHashSet matchIdsSet = new LinkedHashSet();

        List hits = s.getStudies();
        for (int i = 0; i < hits.size(); i++) {
            ScoreDoc sd = (ScoreDoc) hits.get(i);
            Document d = searcher.doc(sd.doc);
            try {
                Field studyId = d.getField("id");
                String studyIdStr = studyId.stringValue();
                Long studyIdLong = Long.valueOf(studyIdStr);
                matchIdsSet.add(studyIdLong);
            } catch (Exception ex) {
                logger.fine("Query for " + baseQuery + "matched but field \"id\" was null");
//                ex.printStackTrace();
            }
        }
        matchIds.addAll(matchIdsSet);
        resultsWithFacets.setMatchIds(matchIds);
        // don't let user remove dvName facet if on DV page
        if (dvnQuery.vdc != null) {
            CategoryPath facetToRemove = new CategoryPath("dvName", dvnQuery.vdc.getName());
            for (Iterator<CategoryPath> it = facetsOfInterest.iterator(); it.hasNext();) {
                CategoryPath facet = it.next();
                if (facet.equals(facetToRemove)) {
                    it.remove();
                }
            }
        }
        logger.fine("facetsOfInterest about to setFacetsQueried: " + facetsOfInterest);
        resultsWithFacets.setFacetsQueried(facetsOfInterest);
        resultsWithFacets.setClearPreviousFacetRequests(dvnQuery.isClearPreviousFacetRequests());
        resultsWithFacets.setBaseQuery(baseQuery);
        return resultsWithFacets;
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

    // Note that there are 2 methods for getting IDs out of variable search
    // hit lists - getVariableHitIds and getVariableHitStudyIds. One returns
    // the ids of the variables, the other - of the corresponding studies. 
    // -- L.A. 
    
    private List getVariableHitIds(List <Document> hits) throws IOException {
        ArrayList matchIds = new ArrayList();
        LinkedHashSet matchIdsSet = new LinkedHashSet();
        for (Iterator it = hits.iterator(); it.hasNext();){
            Document d = (Document) it.next();
            Field studyId = d.getField("varId");
            String studyIdStr = studyId.stringValue();
            Long studyIdLong = Long.valueOf(studyIdStr);
            matchIdsSet.add(studyIdLong);
        }
        matchIds.addAll(matchIdsSet);
        return matchIds;
    }

    /* returns studyIds for variable query
     */
    private List getVariableHitStudyIds( Query query) throws IOException {
        ArrayList matchIds = new ArrayList();
        LinkedHashSet matchIdsSet = new LinkedHashSet();
        if (query != null){
            initIndexSearcher();
            DocumentCollector s = new DocumentCollector(searcher);
            searcher.search(query, s);
            searcher.close();
            List hits = s.getStudies();
//            Hits hits = searcher.search(query);
            for (int i = 0; i < hits.size(); i++) {
                Document d = searcher.doc(((ScoreDoc) hits.get(i)).doc);
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

    /* 
     * Similarly to the 2 methods above for extracting the IDs out of variable
     * search hit lists, the 2 methods below are for getting the ids of the 
     * study files and the corresponding studies, respectively, from the 
     * file metadata sarch hit lists. 
     */
    
    private List getFileMetadataHitIds(List <Document> hits) throws IOException {
        ArrayList matchIds = new ArrayList();
        LinkedHashSet matchIdsSet = new LinkedHashSet();
        for (Iterator it = hits.iterator(); it.hasNext();){
            Document d = (Document) it.next();
            Field studyId = d.getField("studyFileId");
            String studyIdStr = studyId.stringValue();
            Long studyIdLong = Long.valueOf(studyIdStr);
            matchIdsSet.add(studyIdLong);
        }
        matchIds.addAll(matchIdsSet);
        return matchIds;
    }
    
    
    private List getFileMetadataHitStudyIds( Query query) throws IOException {
        ArrayList matchIds = new ArrayList();
        LinkedHashSet matchIdsSet = new LinkedHashSet();
        if (query != null){
            initIndexSearcher();
            DocumentCollector s = new DocumentCollector(searcher);
            searcher.search(query, s);
            searcher.close();
            List hits = s.getStudies();
//            Hits hits = searcher.search(query);
            for (int i = 0; i < hits.size(); i++) {
                Document d = searcher.doc(((ScoreDoc) hits.get(i)).doc);
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
    
    private List getVersionHitIds(List <Document> hits) throws IOException {
        ArrayList matchIds = new ArrayList();
        LinkedHashSet matchIdsSet = new LinkedHashSet();
        for (Iterator it = hits.iterator(); it.hasNext();){
            Document d = (Document) it.next();
            Field versionId = d.getField("versionId");
            String versionIdStr = versionId.stringValue();
            Long versionIdLong = Long.valueOf(versionIdStr);
            matchIdsSet.add(versionIdLong);
        }
        matchIds.addAll(matchIdsSet);
        return matchIds;
    }

    private List <Document> getFilteredByStudyIdVersionHitIds(List <Document> hits, List<Long> studyIds) throws IOException {
        ArrayList matchDocuments = new ArrayList();
        LinkedHashSet matchIdsSet = new LinkedHashSet();
        for (Iterator it = hits.iterator(); it.hasNext();){
            Document d = (Document) it.next();
            Field studyId = d.getField("versionStudyId");
            String studyIdStr = studyId.stringValue();
            Long studyIdLong = Long.valueOf(studyIdStr);
            if (studyIds.contains(studyIdLong)) {
                matchIdsSet.add(d);
            }
        }
        matchDocuments.addAll(matchIdsSet);
        return matchDocuments;
    }

    
    private void initIndexSearcher() throws IOException {
        if (r != null) {
            if (!r.isCurrent()) {
                while (IndexWriter.isLocked(dir));

                r = IndexReader.open(dir, true);
                searcher = new IndexSearcher(r);
            }
        } else {
            r = IndexReader.open(dir, true);
            searcher = new IndexSearcher(r);
        }
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

    
    private DocumentCollector exactMatchQuery(IndexSearcher searcher, String field, String value) throws IOException{
        Term t = new Term(field,value.toLowerCase().trim());
        TermQuery indexQuery = new TermQuery(t);
        DocumentCollector c = new DocumentCollector(searcher);
        searcher.search(indexQuery, c);
        return c;

 //       return searcher.search(indexQuery);
    }
     

    BooleanQuery orPhraseQuery(List <SearchTerm> orSearchTerms){
        BooleanQuery orTerms = new BooleanQuery();
        orTerms.setMaxClauseCount(dvnMaxClauseCount);
        for (Iterator it = orSearchTerms.iterator(); it.hasNext();) {
            SearchTerm elem = (SearchTerm) it.next();
            String[] phrase = getPhrase(elem.getValue().toLowerCase().trim());
            if (phrase.length > 1) {
                PhraseQuery phraseQuery = new PhraseQuery();
                phraseQuery.setSlop(10);

                for (int i = 0; i < phrase.length; i++) {
                    phraseQuery.add(new Term(elem.getFieldName(), phrase[i].toLowerCase().trim()));
                }
                orTerms.add(phraseQuery, BooleanClause.Occur.SHOULD);
            } else if (phrase.length == 1){
//                Term t = new Term(elem.getFieldName(), elem.getValue().toLowerCase().trim());
                logger.fine("INDEXER: orPhraseQuery: search element value: "+phrase[0].toLowerCase().trim());
                Term t = new Term(elem.getFieldName(), phrase[0].toLowerCase().trim());
                logger.fine("INDEXER: orPhraseQuery: term value="+t.text());
                TermQuery orQuery = new TermQuery(t);
                logger.fine("INDEXER: TermQuery orQuery (native): "+orQuery.toString());
                orTerms.add(orQuery, BooleanClause.Occur.SHOULD);
            }
        }
        return orTerms;
    }
    
    BooleanQuery orPhraseOrWildcardQuery(List <SearchTerm> orSearchTerms){
        BooleanQuery orTerms = new BooleanQuery();
        orTerms.setMaxClauseCount(dvnMaxClauseCount);
        for (Iterator it = orSearchTerms.iterator(); it.hasNext();) {
            SearchTerm elem = (SearchTerm) it.next();
            String[] phrase = getPhrase(elem.getValue().toLowerCase().trim());
            if (phrase.length > 1) {
                PhraseQuery phraseQuery = new PhraseQuery();
                phraseQuery.setSlop(10);

                for (int i = 0; i < phrase.length; i++) {
                    phraseQuery.add(new Term(elem.getFieldName(), phrase[i].toLowerCase().trim()));
                }
                orTerms.add(phraseQuery, BooleanClause.Occur.SHOULD);
            } else if (phrase.length == 1){
//                Term t = new Term(elem.getFieldName(), elem.getValue().toLowerCase().trim());
                logger.fine("INDEXER: wildcardQuery: search element value: "+phrase[0].toLowerCase().trim());
                if (isPrefixSearchableFileMetadataField(elem.getFieldName())) { 
                    Term t = new Term(elem.getFieldName(), phrase[0].toLowerCase().trim()+"*");
                    logger.fine("INDEXER: wildcardQuery: term value="+t.text());
                    WildcardQuery wcQuery = new WildcardQuery(t);
                    logger.fine("INDEXER: Term wildcardQuery (native): "+wcQuery.toString());
                    orTerms.add(wcQuery, BooleanClause.Occur.SHOULD);
                } else {
                    logger.fine("INDEXER: building PhraseQuery: search element value: "+phrase[0].toLowerCase().trim());
                    Term t = new Term(elem.getFieldName(), phrase[0].toLowerCase().trim());
                    logger.fine("INDEXER: building PhraseQuery: term value="+t.text());
                    TermQuery orQuery = new TermQuery(t);
                    logger.fine("INDEXER: TermQuery orQuery (native): "+orQuery.toString());
                    orTerms.add(orQuery, BooleanClause.Occur.SHOULD);
                }
            
            }
        }
        return orTerms;
    }

    BooleanQuery orIdSearchTermClause(List <Long> id, String fieldName){
        BooleanQuery idTerms = new BooleanQuery();
        for (Iterator it = id.iterator(); it.hasNext();){
            Long lId= (Long) it.next();
            //Term varStudyId = new Term ("varStudyId",lId.toString());
            Term varStudyId = new Term (fieldName,lId.toString());
            TermQuery varStudyIdQuery = new TermQuery(varStudyId);
            idTerms.add(varStudyIdQuery, BooleanClause.Occur.SHOULD);
        }
        return idTerms;
    }

    List <TermQuery> orLongEqSearchTermQueries(List <Long> id, String var){
        List <TermQuery> termQuery = new ArrayList<TermQuery>();
        for (Iterator it = id.iterator(); it.hasNext();){
            Long lId= (Long) it.next();
            Term longEq = new Term (var,lId.toString());
            TermQuery longEqQuery = new TermQuery(longEq);
            termQuery.add(longEqQuery);
        }
        return termQuery;
    }

    public BooleanQuery andSearchTermClause(List <SearchTerm> andSearchTerms){
        BooleanQuery andTerms = new BooleanQuery();
        andTerms.setMaxClauseCount(dvnMaxClauseCount);
        Query rQuery=null;
        for (Iterator it = andSearchTerms.iterator(); it.hasNext();) {
            SearchTerm elem = (SearchTerm) it.next();
            if (elem.getOperator().equals("<")) {
                Term end = new Term(elem.getFieldName(),elem.getValue().toLowerCase().trim());
                Term begin = null;
                rQuery = new TermRangeQuery(elem.getFieldName(), null, elem.getValue().toLowerCase().trim(), false, false);
//                rQuery = new RangeQuery(begin,end,true);
                andTerms.add(rQuery, BooleanClause.Occur.MUST);
            }
            else if ( elem.getOperator().equals(">")){
                Term end = null;
                Term begin = new Term(elem.getFieldName(),elem.getValue().toLowerCase().trim());
                rQuery = new TermRangeQuery(elem.getFieldName(), elem.getValue().toLowerCase().trim(), null, false, false);
//                rQuery = new RangeQuery(begin,end,true);
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
        /*
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
        anyTerms.add(buildAnyTerm("topicVocabClassURI",string));
        anyTerms.add(buildAnyTerm("keywordValue",string));
        anyTerms.add(buildAnyTerm("topicClassVocabulary",string));
        anyTerms.add(buildAnyTerm("topicClassValue",string));
        anyTerms.add(buildAnyTerm("protocol",string));
        anyTerms.add(buildAnyTerm("authority",string));
        anyTerms.add(buildAnyTerm("globalId",string));
        anyTerms.add(buildAnyTerm("otherId",string));
        anyTerms.add(buildAnyTerm("otherIdAgency",string));
        anyTerms.add(buildAnyTerm("versionDate",string));
        anyTerms.add(buildAnyTerm("studySoftware",string));
        anyTerms.add(buildAnyTerm("studySoftwareVersion",string));
        anyTerms.add(buildAnyTerm("studyGrantNumber",string));
        anyTerms.add(buildAnyTerm("studyGrantNumberAgency",string));
        anyTerms.add(buildAnyTerm("replicationFor",string));
        anyTerms.add(buildAnyTerm("studyEastLongitude",string));
        anyTerms.add(buildAnyTerm("studyWestLongitude",string));
        anyTerms.add(buildAnyTerm("studyNorthLatitude",string));
        anyTerms.add(buildAnyTerm("studySouthLatitude",string));
        anyTerms.add(buildAnyTerm("fileDescription",string));
        anyTerms.add(buildAnyTerm("unf",string));
         
         */

        if (r != null) {
            Collection<String> allfields = r.getFieldNames(IndexReader.FieldOption.INDEXED);
        
            for (String indexedFieldName : allfields) {
                //logger.fine("INDEXREADER: "+indexedFieldName);
                if (!"varName".equals(indexedFieldName)
                        && !"varLabel".equals(indexedFieldName)
                        && !"varId".equals(indexedFieldName)
                        && !"id".equals(indexedFieldName)
                        && !"versionNumber".equals(indexedFieldName)
                        && !"versionStudyId".equals(indexedFieldName)
                        && !"varStudyId".equals(indexedFieldName)
                        && !"varStudyFileId".equals(indexedFieldName)
                        && !isFileMetadataField(indexedFieldName)) {
                    anyTerms.add(buildAnyTerm(indexedFieldName, string));
                }
            }
        }
        
        return orPhraseQuery(anyTerms);
    }

    private BooleanQuery buildVariableQuery(SearchTerm term) {
        List <SearchTerm> variableTerms = new ArrayList();
        variableTerms.add(buildAnyTerm("varName",term.getValue().toLowerCase().trim(),term.getOperator()));
        variableTerms.add(buildAnyTerm("varLabel",term.getValue().toLowerCase().trim(),term.getOperator()));
        return orPhraseQuery(variableTerms);
    }

    // Similar method for creating a File-level metadata search query. 
    // Note that we default to "=" for the term operator. 
    // -- L.A. 
    private BooleanQuery buildFileMetadataQuery(SearchTerm term) {
        List <SearchTerm> fileMetadataTerms = new ArrayList();
        fileMetadataTerms.add(buildAnyTerm(term.getFieldName(), term.getValue().toLowerCase().trim())); 
        
        // insert the code for custom wildcard searching here - ?
        
        
        return orPhraseOrWildcardQuery(fileMetadataTerms);
    }
    SearchTerm buildAnyTerm(String fieldName,String value){
        SearchTerm term = new SearchTerm();
        term.setOperator("=");
        term.setFieldName(fieldName);
        term.setValue(value.toLowerCase().trim());
        return term;
    }

    SearchTerm buildAnyTerm(String fieldName,String value, String operator){
        SearchTerm term = new SearchTerm();
        term.setOperator(operator);
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

    private void assureIndexDirExists() {
        File indexDirFile = new File(indexDir);
        if (!indexDirFile.exists()) {
            logger.fine("Index directory does not exist - creating "+indexDir);
            indexDirFile.mkdir();
            logger.fine(indexDir + " created");
        }
    }
    
    private boolean indexDirExists() {
        File indexDirFile = new File(indexDir);
        return indexDirFile.exists();
    }
    
    private void assureTaxoDirExists() {
        File taxoDirFile = new File(taxoDirName);
        if (!taxoDirFile.exists()) {
            logger.fine("Taxonomy directory does not exist - creating " + taxoDir);
            taxoDirFile.mkdir();
            logger.fine(taxoDir + " created");
        }
    }

    List<Query> getCollectionQueries(VDC vdc) {
        return getCollectionQueries(vdc, false);
        
    }
    
    List<Query> getCollectionQueriesForSubnetworkIndexing(VDC vdc) {
        return getCollectionQueries(vdc, true);
    }
    
    List<Query> getCollectionQueries(VDC vdc, boolean noSubnetworkScope) {
        List<Query> collectionQueries = new ArrayList<Query>();

        QueryParser parser = new QueryParser(Version.LUCENE_30, "abstract", new DVNAnalyzer());
        parser.setDefaultOperator(QueryParser.AND_OPERATOR);

        Collection<VDCCollection> collections = vdc.getOwnedCollections();
        Collection<VDCCollection> linkedCollections = vdc.getLinkedCollections();
        collections.addAll(linkedCollections);
        for (VDCCollection col : collections) {
            StringBuilder sbOuter = new StringBuilder();
            String type = col.getType();
            String queryString = col.getQuery();
            boolean isDynamic = col.isDynamic();
            boolean isLocalScope = col.isLocalScope();
            boolean isSubnetworkScope = col.isSubnetworkScope();
            
            boolean isRootCollection = col.isRootCollection();
            
            VDC collOwner = col.getOwner();
            
            if (queryString != null && !queryString.isEmpty()) {
                // Must be a dynamic collection; 

                // We are creating this list of queries for the purposes of finding 
                // all the linked studies that belong to the dataverse, in addition 
                // to the ones that are directly "owned" by it (assigned to it by
                // the "owner field). So all the "Local Scope" queries can be 
                // dropped - as they are assumed to be applied only to the subsets
                // of studies owned by the DV. (i.e., they are combined with 
                // "AND dvOwnerId = ..." when the collections are looked up).
                
                // However... This can be a *LINKED* collection, owned by another
                // dataverse! In that case we still want to include the query, 
                // regardless of the scope; AND make sure the scope is properly 
                // reflected in the query. 
                if ((vdc.getId().equals(collOwner.getId()))) {
                    if (isLocalScope) {
                    
                        // It's our own (not linked) collection; and the scope is local.
                        // skipping it: 
                        queryString = null;
                    } else if (isSubnetworkScope) {
                        if (noSubnetworkScope) {
                            // We want to skip subnetwork-scoped queries too; if 
                            // we are running this for the purposes of looking for 
                            // studies linked across subnetworks. -- L.A. 
                            // Why? - Because we are looking for studies linked 
                            // from *other* subnetworks; and dvNetworkId=myNetwork
                            // added to the query guarantees that this isn't going
                            // to happen... But more importantly, it's not just 
                            // about spending cycles running an unnecessary query: 
                            // this may actually prevent properly reindexing 
                            // a study that's no longer linked to this subnetwork... 
                            
                            queryString = null; 
                        } else {
                            queryString = "ownerDvNetworkId:" + col.getOwner().getVdcNetwork().getId() + " AND (" + queryString + ")";
                        }
                    } else {
                        // and if it's a network-scope collection, then we'll just add 
                        // whatever is in queryString
                    }
                    
                } else {
                    
                    // This is a linked collection;
                    // Let's check its scope, and modify the query string - 
                    // if necessary: 
                    
                    if (isLocalScope) {
                        queryString = "dvOwnerId:" + col.getOwner().getId() + " AND (" + queryString + ")";
                    } else if (isSubnetworkScope)  {
                        queryString = "ownerDvNetworkId:" + col.getOwner().getVdcNetwork().getId() + " AND (" + queryString + ")";
                    }
                    
                    // (and if it's a full DVN-scope collection - we leave 
                    // queryString intact....
                }
                
                // OK, if we still have the query, let's try to parse it and add
                // it to the combined query: 
                
                if (queryString != null) {
                    try {
                        logger.fine("For " + col.getName() + " (isRootCollection=" + isRootCollection + "|type=" + type + "|isDynamic=" + isDynamic + "|isLocalScope=" + isLocalScope + ") adding query: <<<" + queryString + ">>>");
                        Query query = parser.parse(queryString);
                        collectionQueries.add(query);
                    } catch (org.apache.lucene.queryParser.ParseException ex) {
                        Logger.getLogger(StudyListingPage.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else {
                logger.fine("For " + col.getName() + " (isRootCollection=" + isRootCollection + "|type=" + type + "|isDynamic=" + isDynamic + "|isLocalScope=" + isLocalScope + ") skipping add of query: <<<" + queryString + ">>>");
                List<Study> studies = col.getStudies();
                StringBuilder sbInner = new StringBuilder();
                for (Study study : studies) {
                    logger.fine("- has StudyId: " + study.getId());
                    String idColonId = "id:" + study.getId().toString() + " ";
                    sbInner.append(idColonId);
                }
                if (isRootCollection) {
                    try {
                        Context ctx = new InitialContext();
                        vdcService = (VDCServiceLocal) ctx.lookup("java:comp/env/vdcService");
                    } catch (Exception ex) {
                        logger.fine("Caught an exception looking up VDC Service; " + ex.getMessage());
                    }

                    if (vdcService != null) {
                        List<Long> rootCollectionStudies = vdcService.getOwnedStudyIds(col.getOwner().getId());
                        for (Long id : rootCollectionStudies) {
                            logger.fine("- has StudyId: " + id);
                            String idColonId = "id:" + id.toString() + " ";
                            sbInner.append(idColonId);
                        }
                    }
                }
                logger.fine("sbInner: " + sbInner.toString());
                sbOuter.append(sbInner);

            }
            logger.fine("sbOuter: " + sbOuter);
            if (!sbOuter.toString().isEmpty()) {
                try {
                    parser.setDefaultOperator(QueryParser.OR_OPERATOR);
                    /**
                     * @todo: stop parsing a string... "If you are
                     * programmatically generating a query string and then
                     * parsing it with the query parser then you should
                     * seriously consider building your queries directly with
                     * the query API. In other words, the query parser is
                     * designed for human-entered text, not for
                     * program-generated text." --
                     * http://lucene.apache.org/core/old_versioned_docs/versions/3_5_0/queryparsersyntax.html
                     */
                    Query staticColQuery = parser.parse(sbOuter.toString());
                    parser.setDefaultOperator(QueryParser.AND_OPERATOR);
                    logger.fine("staticCollectionQuery: " + staticColQuery);
                    collectionQueries.add(staticColQuery);
                } catch (org.apache.lucene.queryParser.ParseException ex) {
                    Logger.getLogger(AdvSearchPage.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        return collectionQueries;


    }

    Query constructDvOwnerIdQuery(VDC vdc) {
        QueryParser parser = new QueryParser(Version.LUCENE_30, "abstract", new DVNAnalyzer());
        parser.setDefaultOperator(QueryParser.AND_OPERATOR);
        Query dvnOwnerIdQuery = null;
        try {
            dvnOwnerIdQuery = parser.parse("dvOwnerId:" + vdc.getId().toString());
        } catch (org.apache.lucene.queryParser.ParseException ex) {
            Logger.getLogger(AdvSearchPage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dvnOwnerIdQuery;
    }
    
    Query constructDvNetworkIdQuery(Long dvNetworkId) {
        QueryParser parser = new QueryParser(Version.LUCENE_30, "abstract", new DVNAnalyzer());
        parser.setDefaultOperator(QueryParser.AND_OPERATOR);
        Query dvNetworkIdQuery = null;
        try {
            dvNetworkIdQuery = parser.parse("dvNetworkId:" + dvNetworkId.toString());
        } catch (org.apache.lucene.queryParser.ParseException ex) {
            Logger.getLogger(AdvSearchPage.class.getName()).log(Level.SEVERE, null, ex);
            dvNetworkIdQuery = null; 
        }
        return dvNetworkIdQuery;
    }
    
    Query constructDvNetworkOwnerIdQuery(Long dvNetworkId) {
        QueryParser parser = new QueryParser(Version.LUCENE_30, "abstract", new DVNAnalyzer());
        parser.setDefaultOperator(QueryParser.AND_OPERATOR);
        Query dvNetworkIdQuery = null;
        try {
            dvNetworkIdQuery = parser.parse("ownerDvNetworkId:" + dvNetworkId.toString());
        } catch (org.apache.lucene.queryParser.ParseException ex) {
            Logger.getLogger(AdvSearchPage.class.getName()).log(Level.SEVERE, null, ex);
            dvNetworkIdQuery = null; 
        }
        return dvNetworkIdQuery;
    }
}
