/*
 * DDI20ServiceBean.java
 *
 * Created on November 9, 2006, 4:59 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.ddi;

import edu.harvard.hmdc.vdcnet.jaxb.ddi20.AbstractType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.AccsPlacType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.ActMinType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.AnlyInfoType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.AnlyUnitType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.AuthEntyType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.AvlStatusType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.CaseQntyType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.CatStatType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.CatValuType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.CatgryType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.CitReqType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.CitationType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.CleanOpsType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.CodeBook;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.CollDateType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.CollModeType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.CollSituType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.CollSizeType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.CompleteType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.ConOpsType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.ConceptType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.ConditionsType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.ConfDecType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.ContactType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.DataAccsType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.DataApprType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.DataCollType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.DataCollectorType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.DataDscrType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.DataKindType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.DataSrcType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.DepDateType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.DeposReqType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.DepositrType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.DeviatType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.DimensnsType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.DisclaimerType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.DistDateType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.DistStmtType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.DistrbtrType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.DocDscrType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.EastBLType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.EstSmpErrType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.ExtLinkType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.FileContType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.FileDscrType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.FileNameType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.FileTxtType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.FileTypeType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.FrequencType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.FundAgType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.GeoBndBoxType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.GeogCoverType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.GeogUnitType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.GrantNoType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.HoldingsType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.IDNoType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.InvalrngType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.ItemType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.KeywordType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.LablType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.LocationType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.MethodType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.NationType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.NorthBLType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.NotesType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.ObjectFactory;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.OrigArchType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.OthRefsType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.OtherMatType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.OthrStdyMatType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.PType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.ProdDateType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.ProdPlacType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.ProdStmtType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.ProducerType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.RangeType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.RelMatType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.RelPublType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.RelStdyType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.ResInstruType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.RespRateType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.RestrctnType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.RspStmtType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.SampProcType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.SerInfoType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.SerNameType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.SerStmtType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.SetAvailType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.SoftwareType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.SourcesType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.SouthBLType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.SpecPermType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.SrcCharType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.SrcDocuType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.SrcOrigType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.StdyDscrType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.StdyInfoType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.SubTitlType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.SubjectType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.SumDscrType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.SumStatType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.TimeMethType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.TimePrdType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.TitlStmtType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.TitlType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.TopcClasType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.TxtType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.UniverseType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.UseStmtType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.VarFormatType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.VarQntyType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.VarType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.VerStmtType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.VersionType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.WeightType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.WestBLType;
import edu.harvard.hmdc.vdcnet.study.DataTable;
import edu.harvard.hmdc.vdcnet.study.DataVariable;
import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.study.StudyAbstract;
import edu.harvard.hmdc.vdcnet.study.StudyAuthor;
import edu.harvard.hmdc.vdcnet.study.StudyDistributor;
import edu.harvard.hmdc.vdcnet.study.StudyFile;
import edu.harvard.hmdc.vdcnet.study.StudyFileEditBean;
import edu.harvard.hmdc.vdcnet.study.StudyGeoBounding;
import edu.harvard.hmdc.vdcnet.study.StudyGrant;
import edu.harvard.hmdc.vdcnet.study.StudyKeyword;
import edu.harvard.hmdc.vdcnet.study.StudyNote;
import edu.harvard.hmdc.vdcnet.study.StudyOtherId;
import edu.harvard.hmdc.vdcnet.study.StudyOtherRef;
import edu.harvard.hmdc.vdcnet.study.StudyProducer;
import edu.harvard.hmdc.vdcnet.study.StudyRelMaterial;
import edu.harvard.hmdc.vdcnet.study.StudyRelPublication;
import edu.harvard.hmdc.vdcnet.study.StudyRelStudy;
import edu.harvard.hmdc.vdcnet.study.StudyServiceLocal;
import edu.harvard.hmdc.vdcnet.study.StudySoftware;
import edu.harvard.hmdc.vdcnet.study.StudyTopicClass;
import edu.harvard.hmdc.vdcnet.study.SummaryStatistic;
import edu.harvard.hmdc.vdcnet.study.SummaryStatisticType;
import edu.harvard.hmdc.vdcnet.study.VariableCategory;
import edu.harvard.hmdc.vdcnet.study.VariableFormatType;
import edu.harvard.hmdc.vdcnet.study.VariableIntervalType;
import edu.harvard.hmdc.vdcnet.study.VariableRange;
import edu.harvard.hmdc.vdcnet.study.VariableRangeType;
import edu.harvard.hmdc.vdcnet.study.VariableServiceLocal;
import edu.harvard.hmdc.vdcnet.util.DateUtil;
import edu.harvard.hmdc.vdcnet.util.StringUtil;
import edu.harvard.hmdc.vdcnet.vdc.VDCNetworkServiceLocal;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 *
 * @author gdurand
 */
@Stateless
public class DDI20ServiceBean implements edu.harvard.hmdc.vdcnet.ddi.DDI20ServiceLocal {
    @EJB VariableServiceLocal varService;
    @EJB VDCNetworkServiceLocal networkService;
    @EJB StudyServiceLocal studyService;
    private static final Logger logger = Logger.getLogger("edu.harvard.hmdc.vdcnet.ddi.DDI20ServiceBean");
 
    
    /** Creates a new instance of DDI20ServiceBean */
    public DDI20ServiceBean() {
    }
    
    public static final String URL_PREFIX = "http://localhost:8080";
    public static final String LEGACY_VDC_PREFIX = "http://vdc.hmdc.harvard.edu/VDC/Repository/0.1/Access/hdl:";
    
    // ddi constants
    public static final String AGENCY_HANDLE = "handle";
    public static final String REPLICATION_FOR_TYPE = "replicationFor";
    public static final String VAR_WEIGHTED = "wgtd";
    public static final String VAR_INTERVAL_CONTIN = "contin";
    
    public static final String EVENT_START = "start";
    public static final String EVENT_END = "end";
    public static final String EVENT_SINGLE = "single";
    
    public static final String LEVEL_STUDY = "study";
    public static final String LEVEL_FILE = "file";
    public static final String LEVEL_VARIABLE = "variable";
    public static final String LEVEL_CATEGORY = "category";
    
    public static final String NOTE_TYPE_UNF = "VDC:UNF";
    public static final String NOTE_SUBJECT_UNF = "Universal Numeric Fingerprint";
    
    // db constants
    public static final String DB_VAR_INTERVAL_TYPE_CONTINUOUS = "continuous";
    public static final String DB_VAR_RANGE_TYPE_POINT = "point";
    public static final String DB_VAR_RANGE_TYPE_MIN = "min";
    public static final String DB_VAR_RANGE_TYPE_MIN_EX = "min exclusive";
    public static final String DB_VAR_RANGE_TYPE_MAX = "max";
    public static final String DB_VAR_RANGE_TYPE_MAX_EX = "max exclusive";
    
    // Note: in this classes' methods:
    // for ease of determining which variables come from the XML objects,
    // I have decided to prefix those with an "_"; the entity objects have
    // no such prefix; e.g. "_ss" would represent the SumStatType object generated
    // by the unmarshaller, while "ss" represents the entity object
    
    
    //*************************************************************
    //
    // Methods for mapping from DDI to Study
    //
    //*************************************************************
    
    public Study mapDDI(CodeBook _cb) {
     //   Logger logger = Logger.getLogger()
        // this is currently only used by ingest; so it is OK
        // to pass true for the isAnIngest variable
        return mapDDI(_cb, new Study(), true, false);
    }
    
    public Study mapDDI(CodeBook _cb, Study study, boolean allowUpdates) {
        return mapDDI(_cb, study, false, allowUpdates);
    }
    
    private Study mapDDI(CodeBook _cb, Study study, boolean isAnIngest, boolean allowUpdates) {
        logger.info("begin mapDDI()");
        // map used to link dataVariables to appropriate dataTable
        Map filesMap = new HashMap();
        
        // map used to determine which, if any, fields are needed from the docDscr
        Map docDscrMap = new HashMap();
        
        // initialize the collections
        study.setFileCategories( new ArrayList() );
        study.setStudyAbstracts( new ArrayList() );
        study.setStudyAuthors( new ArrayList() );
        study.setStudyDistributors( new ArrayList() );
        study.setStudyGeoBoundings(new ArrayList());
        study.setStudyGrants(new ArrayList());
        study.setStudyKeywords(new ArrayList());
        study.setStudyNotes(new ArrayList());
        study.setStudyOtherIds(new ArrayList());
        study.setStudyOtherRefs(new ArrayList());
        study.setStudyProducers(new ArrayList());
        study.setStudyRelMaterials(new ArrayList());
        study.setStudyRelPublications(new ArrayList());
        study.setStudyRelStudies(new ArrayList());
        study.setStudySoftware(new ArrayList());
        study.setStudyTopicClasses(new ArrayList());
        
        
        // begin mapping
        if (!isAnIngest) {
            logger.info("calling mapStudyDscr()");
            mapStdyDscr( _cb.getStdyDscr().get(0), study, docDscrMap );
            
            // only go through docDscr if we have something in our docDscrMap
            // (for now only studyId);
            Iterator iter = _cb.getDocDscr().iterator();
            while ( !docDscrMap.isEmpty() && iter.hasNext()) {
                DocDscrType _dd = (DocDscrType) iter.next();
                logger.info("calling mapDocDscr()");
                mapDocDscr( _dd, study, docDscrMap );
            }
            
            // now check studyId
            if (!allowUpdates) {
                if (!studyService.isUniqueStudyId(study.getStudyId(),study.getProtocol(),study.getAuthority())) {
                    // There is already a study in the database with this id, so we can't save this study in the database
                    throw new MappingException("Study "+study.getGlobalId()+" already exists. ");
                }                
            }
            
            iter = _cb.getOtherMat().iterator();
            while (iter.hasNext()) {
                logger.info("calling mapOtherMat()");
                mapOtherMat( (OtherMatType) iter.next(), study, filesMap );
            }
        }
        
        Iterator iter = _cb.getDataDscr().iterator();
        while (iter.hasNext()) {
            logger.info("calling mapDataDscr");
            mapDataDscr( (DataDscrType) iter.next(), filesMap );
        }
        
        iter = _cb.getFileDscr().iterator();
        while (iter.hasNext()) {
            logger.info("calling mapFileDscr");
            mapFileDscr( (FileDscrType) iter.next(), study, filesMap );
        }
        
        
        logger.info("returning study "+study.getGlobalId());
        return study;
    }
    
    private void mapDocDscr(DocDscrType _dd, Study s, Map docDscrMap) {
        // iterate through map and determine which, if any, fields we get from the docDscr
        Iterator iter = docDscrMap.keySet().iterator();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            
            //for now we only can have studyId
            if ( key.equals("studyId") ) {
                if (_dd.getCitation() != null) {
                    Iterator idIter = _dd.getCitation().getTitlStmt().getIDNo().iterator();
                    while (idIter.hasNext()) {
                        mapIdNo( (IDNoType) idIter.next(), s, false );
                        if (s.getStudyId() != null) {
                            docDscrMap.remove("studyId");
                            break;
                        }
                    }
                }
            }
            
        }
    }
    
    
    
    private void mapStdyDscr(StdyDscrType _sd, Study s, Map docDscrMap) {
        // these are used for all noters throughout the study descr
        Iterator notesIter = null;
        int noteCount = 0;
        
        // citation info
        CitationType _citation = _sd.getCitation().get(0);
        
        Iterator idIter = _citation.getTitlStmt().getIDNo().iterator();
        while (idIter.hasNext()) {
            mapIdNo( (IDNoType) idIter.next(), s );
        }
        
        if (s.getStudyId() == null ) {
            docDscrMap.put("studyId", "studyId");
        }
        
        
        s.setTitle( mapContent( _citation.getTitlStmt().getTitl().getContent().get(0) ) );
        if (_citation.getTitlStmt().getSubTitl().size() != 0) {
            s.setSubTitle( mapContent( _citation.getTitlStmt().getSubTitl().get(0).getContent().get(0) ) );
        }
        
        if ( _citation.getRspStmt() != null ) {
            Iterator authorIter = _citation.getRspStmt().getAuthEnty().iterator();
            int authorCount = 0;
            while (authorIter.hasNext()) {
                AuthEntyType _author = (AuthEntyType) authorIter.next();
                StudyAuthor author = new StudyAuthor();
                author.setName( mapContent( _author.getContent().get(0) ) );
                author.setAffiliation( _author.getAffiliation() );
                author.setDisplayOrder( authorCount++ );
                author.setStudy(s);
                s.getStudyAuthors().add(author);
            }
        }
        
        if ( _citation.getProdStmt() != null ) {
            ProdStmtType _ps =  _citation.getProdStmt();
            Iterator prodIter = _ps.getProducer().iterator();
            int prodCount = 0;
            while (prodIter.hasNext()) {
                ProducerType _prod = (ProducerType) prodIter.next();
                StudyProducer prod = new StudyProducer();
                prod.setName( mapContentList( _prod.getContent(), "value" ) );
                prod.setUrl( mapContentList( _prod.getContent(), "uri" ) );
                prod.setLogo( mapContentList( _prod.getContent(), "logo" ) );
                prod.setAbbreviation( _prod.getAbbr() );
                prod.setAffiliation( _prod.getAffiliation() );
                prod.setDisplayOrder( prodCount++ );
                prod.setStudy(s);
                s.getStudyProducers().add(prod);
            }
            
            // DDI has a list, but we only take the first if it exists
            List _prodDateList = _ps.getProdDate();
            if (_prodDateList != null && _prodDateList.size() != 0) {
                ProdDateType _prodDate = (ProdDateType) _prodDateList.get(0);
                s.setProductionDate( mapDate(   _prodDate.getDate(), _prodDate.getContent() ) );
            }
            
            if ( _ps.getProdPlac().size() != 0) {
                s.setProductionPlace( mapContent( _ps.getProdPlac().get(0).getContent().get(0) ) );
            }
            
            Iterator softIter = _ps.getSoftware().iterator();
            int softCount = 0;
            while (softIter.hasNext()) {
                SoftwareType _soft = (SoftwareType) softIter.next();
                StudySoftware ss = new StudySoftware();
                ss.setName( mapContent( _soft.getContent().get(0) ) );
                ss.setSoftwareVersion( _soft.getVersion() );
                ss.setDisplayOrder( softCount++ );
                ss.setStudy(s);
                s.getStudySoftware().add(ss);
            }
            
            if ( _ps.getFundAg().size() != 0) {
                s.setFundingAgency( mapContent( _ps.getFundAg().get(0).getContent().get(0) ) );
            }
            
            Iterator grantIter = _ps.getGrantNo().iterator();
            int grantCount = 0;
            while (grantIter.hasNext()) {
                GrantNoType _grant = (GrantNoType) grantIter.next();
                StudyGrant sg = new StudyGrant();
                sg.setNumber( mapContent( _grant.getContent().get(0) ) );
                sg.setAgency( _grant.getAgency() );
                sg.setDisplayOrder( grantCount++ );
                sg.setStudy(s);
                s.getStudyGrants().add(sg);
            }
        }
        
        if ( _citation.getDistStmt() != null ) {
            Iterator distIter = _citation.getDistStmt().getDistrbtr().iterator();
            int distCount = 0;
            while (distIter.hasNext()) {
                DistrbtrType _dist = (DistrbtrType) distIter.next();
                StudyDistributor dist = new StudyDistributor();
                dist.setName( mapContentList( _dist.getContent(), "value" ) );
                dist.setUrl( mapContentList( _dist.getContent(), "uri" ) );
                dist.setLogo( mapContentList( _dist.getContent(), "logo" ) );
                dist.setAbbreviation( _dist.getAbbr() );
                dist.setAffiliation( _dist.getAffiliation() );
                dist.setDisplayOrder( distCount++ );
                dist.setStudy(s);
                s.getStudyDistributors().add(dist);
                
            }
            
            if ( _citation.getDistStmt().getContact().size() != 0) {
                ContactType _contact = _citation.getDistStmt().getContact().get(0);
                s.setDistributorContact( mapContent( _contact.getContent().get(0) ) );
                s.setDistributorContactEmail( _contact.getEmail() );
                s.setDistributorContactAffiliation( _contact.getAffiliation() );
            }
            
            if ( _citation.getDistStmt().getDistDate() != null) {
                DistDateType _distDate = _citation.getDistStmt().getDistDate();
                s.setDistributionDate( mapDate( _distDate.getDate(), _distDate.getContent() ) );
            }
            
            // DDI has as list, but we only take the first if it exists
            List _depositerList = _citation.getDistStmt().getDepositr();
            if (_depositerList != null && _depositerList.size() != 0) {
                DepositrType _depositor = (DepositrType) _depositerList.get(0);
                s.setDepositor( mapContentList(_depositor.getContent(), "value") );
            }
            
            List _depDateList = _citation.getDistStmt().getDepDate();
            if (_depDateList != null && _depDateList.size() != 0) {
                DepDateType _depDate = (DepDateType) _depDateList.get(0);
                s.setDateOfDeposit( mapDate(    _depDate.getDate(), _depDate.getContent() ) );
            }
        }
        
        if ( _citation.getSerStmt() != null ) {
            SerStmtType _ss = _citation.getSerStmt();
            if (_ss.getSerName().size() != 0) {
                s.setSeriesName( mapContent( _ss.getSerName().get(0).getContent().get(0) )  );
            }
            if (_ss.getSerInfo().size() != 0) {
                s.setSeriesInformation( mapContent( _ss.getSerInfo().get(0).getContent().get(0) )  );
            }
        }
        
        if ( _citation.getVerStmt().size() != 0 ) {
            VerStmtType _vs = _citation.getVerStmt().get(0);
            if (_vs.getVersion() != null) {
                s.setStudyVersion( mapContent( _vs.getVersion().getContent().get(0) ) );
                s.setVersionDate( _vs.getVersion().getDate() );
            }
            
            // ver stmt notes
            notesIter = _vs.getNotes().iterator();
            while (notesIter.hasNext()) {
                NotesType _note = (NotesType) notesIter.next();
                mapStudyNote( _note, s, noteCount++ );
            }
        }
        
        notesIter = _citation.getNotes().iterator();
        while (notesIter.hasNext()) {
            NotesType _note = (NotesType) notesIter.next();
            if ( NOTE_TYPE_UNF.equals(_note.getType()) ) {
                s.setUNF( mapUNF( (String)_note.getContent().get(0) ) );
            } else {
                mapStudyNote( _note, s, noteCount++ );
            }
        }
        
        // abstract and scope
        if ( _sd.getStdyInfo().size() != 0 ) {
            StdyInfoType _studyInfo = _sd.getStdyInfo().get(0);
            
            Iterator abstractsIter = _studyInfo.getAbstract().iterator();
            int abstractCount = 0;
            while (abstractsIter.hasNext()) {
                AbstractType _abstract = (AbstractType) abstractsIter.next();
                StudyAbstract studyAbstract = new StudyAbstract();
                studyAbstract.setDate( _abstract.getDate() );
                
                Iterator abstractsContentIter = _abstract.getContent().iterator();
                String abstractText = "";
                while (abstractsContentIter.hasNext()) {
                    Object content = (Object) abstractsContentIter.next();
                     abstractText += mapContent(content);
                     abstractText += abstractsContentIter.hasNext() ? "\n" : "";
                }
                studyAbstract.setText( abstractText.trim() );
                
                studyAbstract.setDisplayOrder( abstractCount++ );
                studyAbstract.setStudy(s);
                s.getStudyAbstracts().add(studyAbstract);
            }
            
            // keywords and topicclasses
            if (_studyInfo.getSubject() != null) {
                Iterator kwIter = _studyInfo.getSubject().getKeyword().iterator();
                int kwCount = 0;
                while (kwIter.hasNext()) {
                    KeywordType _kw = (KeywordType) kwIter.next();
                    StudyKeyword kw = new StudyKeyword();
                    kw.setVocab( _kw.getVocab() );
                    kw.setVocabURI( _kw.getVocabURI() );
                    kw.setValue( mapContent(_kw.getContent().get(0)) );
                    kw.setDisplayOrder( kwCount++ );
                    kw.setStudy(s);
                    s.getStudyKeywords().add(kw);
                }
                
                Iterator tcIter = _studyInfo.getSubject().getTopcClas().iterator();
                int tcCount = 0;
                while (tcIter.hasNext()) {
                    TopcClasType _tc = (TopcClasType) tcIter.next();
                    StudyTopicClass tc = new StudyTopicClass();
                    tc.setVocab( _tc.getVocab() );
                    tc.setVocabURI( _tc.getVocabURI() );
                    tc.setValue( mapContent(_tc.getContent().get(0)) );
                    tc.setDisplayOrder( tcCount++ );
                    tc.setStudy(s);
                    s.getStudyTopicClasses().add(tc);
                }
            }
            
            if ( _studyInfo.getSumDscr().size() != 0 ) {
                SumDscrType _sum = _studyInfo.getSumDscr().get(0);
                
                // first search for a start time
                boolean tpStartFound = false;
                Iterator tpIter = _sum.getTimePrd().iterator();
                while (tpIter.hasNext()) {
                    TimePrdType _tp = (TimePrdType) tpIter.next();
                    if (_tp.getEvent().equals(EVENT_START)) {
                        s.setTimePeriodCoveredStart( mapDate( _tp.getDate(), _tp.getContent() ) );
                        tpStartFound = true;
                        break;
                    }
                }
                
                // iterate through a 2nd time; if we had a start; look for an end; otherwise look for single
                tpIter = _sum.getTimePrd().iterator();
                while (tpIter.hasNext()) {
                    TimePrdType _tp = (TimePrdType) tpIter.next();
                    if (tpStartFound) {
                        if (_tp.getEvent().equals(EVENT_END)) {
                            s.setTimePeriodCoveredEnd( mapDate( _tp.getDate(), _tp.getContent() ) );
                            break;
                        }
                    } else {
                        if (_tp.getEvent().equals(EVENT_SINGLE)) {
                            s.setTimePeriodCoveredStart( mapDate( _tp.getDate(), _tp.getContent() ) );
                            break;
                        }
                    }
                }
                
                // first search for a start time
                boolean docStartFound = false;
                Iterator docIter = _sum.getCollDate().iterator();
                while (docIter.hasNext()) {
                    CollDateType _doc = (CollDateType) docIter.next();
                    if (_doc.getEvent().equals(EVENT_START)) {
                        s.setDateOfCollectionStart( mapDate( _doc.getDate(), _doc.getContent() ) );
                        docStartFound = true;
                        break;
                    }
                }
                
                // iterate through a 2nd time; if we had a start; look for an end; otherwise look for single
                docIter = _sum.getCollDate().iterator();
                while (docIter.hasNext()) {
                    CollDateType _doc = (CollDateType) docIter.next();
                    if (docStartFound) {
                        if (_doc.getEvent().equals(EVENT_END)) {
                            s.setDateOfCollectionEnd( mapDate( _doc.getDate(), _doc.getContent() ) );
                            break;
                        }
                    } else {
                        if (_doc.getEvent().equals(EVENT_SINGLE)) {
                            s.setDateOfCollectionStart( mapDate( _doc.getDate(), _doc.getContent() ) );
                            break;
                        }
                    }
                }
                
                s.setCountry( mapList( _sum.getNation() ) );
                s.setGeographicCoverage( mapList( _sum.getGeogCover() ) );
                s.setGeographicUnit( mapList( _sum.getGeogUnit() ) );
                s.setUnitOfAnalysis( mapList( _sum.getAnlyUnit() ) );
                s.setUniverse( mapList( _sum.getUniverse() ) );
                s.setKindOfData( mapList( _sum.getDataKind() ) );
                
                if (_sum.getGeoBndBox() != null) {
                    // currently we store geographic bounds as a list, although there is only ever one set
                    StudyGeoBounding geoBound = new StudyGeoBounding();
                    geoBound.setWestLongitude( mapContent( _sum.getGeoBndBox().getWestBL().getContent().get(0) ) );
                    geoBound.setEastLongitude( mapContent( _sum.getGeoBndBox().getEastBL().getContent().get(0) ) );
                    geoBound.setSouthLatitude( mapContent( _sum.getGeoBndBox().getSouthBL().getContent().get(0) ) );
                    geoBound.setNorthLatitude( mapContent( _sum.getGeoBndBox().getNorthBL().getContent().get(0) ) );
                    geoBound.setDisplayOrder(0);
                    geoBound.setStudy(s);
                    s.getStudyGeoBoundings().add(geoBound);
                }
                
                
            }
            
            // study info notes
            notesIter = _studyInfo.getNotes().iterator();
            while (notesIter.hasNext()) {
                NotesType _note = (NotesType) notesIter.next();
                mapStudyNote( _note, s, noteCount++ );
            }
        }
        
        
        
        //data collection and methodology
        if (_sd.getMethod().size() != 0) {
            MethodType _method = _sd.getMethod().get(0);
            
            if (_method.getDataColl().size() != 0 ) {
                DataCollType _dc = _method.getDataColl().get(0);
                if (_dc.getTimeMeth().size() != 0) {
                    s.setTimeMethod( mapContent( _dc.getTimeMeth().get(0).getContent().get(0) ) );
                }
                if (_dc.getDataCollector().size() != 0) {
                    s.setDataCollector( mapContent( _dc.getDataCollector().get(0).getContent().get(0) ) );
                }
                if (_dc.getFrequenc().size() != 0) {
                    s.setFrequencyOfDataCollection( mapContent( _dc.getFrequenc().get(0).getContent().get(0) ) );
                }
                if (_dc.getSampProc().size() != 0) {
                    s.setSamplingProcedure( mapContent( _dc.getSampProc().get(0).getContent().get(0) ) );
                }
                if (_dc.getDeviat().size() != 0) {
                    s.setDeviationsFromSampleDesign( mapContent( _dc.getDeviat().get(0).getContent().get(0) ) );
                }
                if (_dc.getCollMode().size() != 0) {
                    s.setCollectionMode( mapContent( _dc.getCollMode().get(0).getContent().get(0) ) );
                }
                if (_dc.getResInstru().size() != 0) {
                    s.setResearchInstrument( mapContent( _dc.getResInstru().get(0).getContent().get(0) ) );
                }
                if (_dc.getSources() != null) {
                    SourcesType _sources = _dc.getSources();
                    if (_sources.getDataSrc().size() != 0) {
                        s.setDataSources( mapContent( _sources.getDataSrc().get(0).getContent().get(0) ) );
                    }
                    if (_sources.getSrcOrig().size() != 0) {
                        s.setOriginOfSources( mapContent( _sources.getSrcOrig().get(0).getContent().get(0) ) );
                    }
                    if (_sources.getSrcChar().size() != 0) {
                        s.setCharacteristicOfSources( mapContent( _sources.getSrcChar().get(0).getContent().get(0) ) );
                    }
                    if (_sources.getSrcDocu().size() != 0) {
                        s.setAccessToSources( mapContent( _sources.getSrcDocu().get(0).getContent().get(0) ) );
                    }
                }
                if (_dc.getCollSitu().size() != 0) {
                    s.setDataCollectionSituation( mapContent( _dc.getCollSitu().get(0).getContent().get(0) ) );
                }
                if (_dc.getActMin().size() != 0) {
                    s.setActionsToMinimizeLoss( mapContent( _dc.getActMin().get(0).getContent().get(0) ) );
                }
                if (_dc.getConOps().size() != 0) {
                    s.setControlOperations( mapContent( _dc.getConOps().get(0).getContent().get(0) ) );
                }
                if (_dc.getWeight().size() != 0) {
                    s.setWeighting( mapContent( _dc.getWeight().get(0).getContent().get(0) ) );
                }
                if (_dc.getCleanOps().size() != 0) {
                    s.setCleaningOperations( mapContent( _dc.getCleanOps().get(0).getContent().get(0) ) );
                }
            }
            
            Iterator methodNotesIter = _method.getNotes().iterator();
            String sleNote = "";
            while (methodNotesIter.hasNext()) {
                NotesType _note = (NotesType) methodNotesIter.next();
                sleNote +=  mapContent(_note.getContent().get(0) );
                sleNote += methodNotesIter.hasNext() ? "; " : "";
            }
            
            s.setStudyLevelErrorNotes( sleNote );
            
            
            if (_method.getAnlyInfo() != null) {
                AnlyInfoType _ai = _method.getAnlyInfo();
                if (_ai.getRespRate().size() != 0) {
                    s.setResponseRate( mapContent( _ai.getRespRate().get(0).getContent().get(0) ) );
                }
                if (_ai.getEstSmpErr().size() != 0) {
                    s.setSamplingErrorEstimate( mapContent( _ai.getEstSmpErr().get(0).getContent().get(0) ) );
                }
                if (_ai.getDataAppr().size() != 0) {
                    s.setOtherDataAppraisal( mapContent( _ai.getDataAppr().get(0).getContent().get(0) ) );
                }
            }
        }
        
        
        //terms of use
        if (_sd.getDataAccs().size() != 0) {
            DataAccsType _da = _sd.getDataAccs().get(0);
            
            if (_da.getSetAvail().size() != 0) {
                SetAvailType _sa = _da.getSetAvail().get(0);
                if ( _sa.getAccsPlac().size() != 0 ) {
                    s.setPlaceOfAccess( mapContent( _sa.getAccsPlac().get(0).getContent().get(0) ) );
                }
                if (_sa.getOrigArch() != null) {
                    s.setOriginalArchive( mapContent(_sa.getOrigArch().getContent().get(0) ) );
                }
                if ( _sa.getAvlStatus().size() != 0 ) {
                    s.setAvailabilityStatus( mapContent( _sa.getAvlStatus().get(0).getContent().get(0) ) );
                }
                if (_sa.getCollSize() != null) {
                    s.setCollectionSize( mapContent(_sa.getCollSize().getContent().get(0) ) );
                }
                if (_sa.getComplete() != null) {
                    s.setStudyCompletion( mapContent(_sa.getComplete().getContent().get(0) ) );
                }
                
                // set avail notes
                notesIter = _sa.getNotes().iterator();
                while (notesIter.hasNext()) {
                    NotesType _note = (NotesType) notesIter.next();
                    mapStudyNote( _note, s, noteCount++ );
                }
            }
            
            if (_da.getUseStmt().size() != 0) {
                UseStmtType _us = _da.getUseStmt().get(0);
                if (_us.getConfDec() != null) {
                    s.setConfidentialityDeclaration( mapContent(_us.getConfDec().getContent().get(0) ) );
                }
                if (_us.getSpecPerm() != null) {
                    s.setSpecialPermissions( mapContent(_us.getSpecPerm().getContent().get(0) ) );
                }
                if (_us.getRestrctn() != null) {
                    s.setRestrictions( mapContent(_us.getRestrctn().getContent().get(0) ) );
                }
                if (_us.getContact().size() != 0) {
                    s.setContact( mapContent(_us.getContact().get(0).getContent().get(0) ) );
                }
                if (_us.getCitReq() != null) {
                    s.setCitationRequirements( mapContent(_us.getCitReq().getContent().get(0) ) );
                }
                if (_us.getDeposReq() != null) {
                    s.setDepositorRequirements( mapContent(_us.getDeposReq().getContent().get(0) ) );
                }
                if (_us.getConditions() != null) {
                    s.setConditions( mapContent(_us.getConditions().getContent().get(0) ) );
                }
                if (_us.getDisclaimer() != null) {
                    s.setDisclaimer( mapContent(_us.getDisclaimer().getContent().get(0) ) );
                }
            }
            
            // data access notes
            notesIter = _da.getNotes().iterator();
            while (notesIter.hasNext()) {
                NotesType _note = (NotesType) notesIter.next();
                mapStudyNote( _note, s, noteCount++ );
            }
        }
        
        // other studyMat
        if (_sd.getOthrStdyMat().size() != 0) {
            OthrStdyMatType _osm = _sd.getOthrStdyMat().get(0);
            
            Iterator rmIter = _osm.getRelMat().iterator();
            int rmCount = 0;
            boolean replicationForFound = false;
            while (rmIter.hasNext()) {
                RelMatType _rm = (RelMatType) rmIter.next();
                if (!replicationForFound && _rm.getType() != null && _rm.getType().equals(REPLICATION_FOR_TYPE) ) {
                    s.setReplicationFor( mapContent( _rm.getContent().get(0) ) );
                    replicationForFound = true;
                } else {
                    StudyRelMaterial rm = new StudyRelMaterial();
                    rm.setText( mapContent( _rm.getContent().get(0) ) );
                    rm.setDisplayOrder(rmCount++);
                    rm.setStudy(s);
                    s.getStudyRelMaterials().add(rm);
                }
            }
            
            Iterator rsIter = _osm.getRelStdy().iterator();
            int rsCount = 0;
            while (rsIter.hasNext()) {
                RelStdyType _rs = (RelStdyType) rsIter.next();
                StudyRelStudy rs = new StudyRelStudy();
                rs.setText( mapContent( _rs.getContent().get(0) ) );
                rs.setDisplayOrder(rsCount++);
                rs.setStudy(s);
                s.getStudyRelStudies().add(rs);
            }
            
            Iterator rpIter = _osm.getRelPubl().iterator();
            int rpCount = 0;
            while (rpIter.hasNext()) {
                RelPublType _rp = (RelPublType) rpIter.next();
                StudyRelPublication rp = new StudyRelPublication();
                rp.setText( mapContent( _rp.getContent().get(0) ) );
                rp.setDisplayOrder(rpCount++);
                rp.setStudy(s);
                s.getStudyRelPublications().add(rp);
            }
            
            Iterator orIter = _osm.getOthRefs().iterator();
            int orCount = 0;
            while (orIter.hasNext()) {
                OthRefsType _or = (OthRefsType) orIter.next();
                StudyOtherRef or = new StudyOtherRef();
                or.setText( mapContent( _or.getContent().get(0) ) );
                or.setDisplayOrder(orCount++);
                or.setStudy(s);
                s.getStudyOtherRefs().add(or);
            }
            
        }
        
        // study descr notes
        notesIter = _sd.getNotes().iterator();
        while (notesIter.hasNext()) {
            NotesType _note = (NotesType) notesIter.next();
            mapStudyNote( _note, s, noteCount++ );
        }
        
    }
    
    private void mapStudyNote(NotesType _note, Study s, int count) {
        String _noteText = mapContent( _note.getContent().get(0) );
        if ( _noteText != null && !_noteText.trim().equals("") ) {
            StudyNote note = new StudyNote();
            note.setText( _noteText );
            note.setSubject( _note.getSubject() );
            note.setType( _note.getType() );
            note.setDisplayOrder(count);
            note.setStudy(s);
            s.getStudyNotes().add(note);
        }
    }
    
    private String mapUNF(String unfString) {
        if (unfString.indexOf("UNF:") != -1) {
            return unfString.substring( unfString.indexOf("UNF:") );
        } else {
            return null;
        }
    }
    
    private void mapIdNo(IDNoType _idNo, Study s){
        mapIdNo( _idNo, s, true);
    }
    
    private void mapIdNo(IDNoType _idNo, Study s, boolean mapAllIds){
        String _idContent = mapContent( _idNo.getContent().get(0) );
        
        if (_idNo.getAgency()!=null && _idNo.getAgency().equals(AGENCY_HANDLE)) {
            mapIdContent(s, _idContent );
            
        } else if (mapAllIds) {
            StudyOtherId studyOtherId = new StudyOtherId();
            studyOtherId.setOtherId(_idContent);
            studyOtherId.setAgency(_idNo.getAgency());
            studyOtherId.setStudy(s);
            studyOtherId.setDisplayOrder(0);
            s.getStudyOtherIds().add(studyOtherId);
        }
    }
    
    
    
    private void mapIdContent(Study s, String _id) {
        
        int index1 = _id.indexOf(':');
        int index2 = _id.indexOf('/');
        if (index1==-1) {
            throw new EJBException("Error parsing IdNoType: "+_id+". ':' not found in string");
        } else {
            s.setProtocol(_id.substring(0,index1));
        }
        if (index2 == -1) {
            throw new EJBException("Error parsing IdNoType: "+_id+". '/' not found in string");
            
        } else {
            s.setAuthority(_id.substring(index1+1, index2));
        }
        s.setStudyId(_id.substring(index2+1));
    }
    
    private void mapOtherMat(OtherMatType _om, Study s, Map filesMap) {
        StudyFile file = new StudyFile();
        boolean fileAdded = false;
        
        if (_om.getLabl().size() > 0) {
            file.setFileName( (String) _om.getLabl().get(0).getContent().get(0) );
        } else {
            file.setFileName("file");
        }
        
        if (_om.getTxt() != null) {
            file.setDescription( mapContent( (String) _om.getTxt().getContent().get(0) ) );
        }
        
        file.setFileSystemLocation( _om.getURI() );
        
        Iterator iter = _om.getNotes().iterator();
        while (iter.hasNext()) {
            NotesType _note = (NotesType) iter.next();
            if ( "vdc:category".equals(_note.getType()) ) {
                String catName = (String) _note.getContent().get(0);
                addFileToCategory( file, catName, s );
                fileAdded = true;
            }
        }
        if (!fileAdded) {
            addFileToCategory( file, "", s );
        }
    }
    
    
    private void mapFileDscr(FileDscrType _fd, Study s, Map filesMap) {
        StudyFile file = new StudyFile();
        boolean fileAdded = false;
        
        if (  _fd.getFileTxt().get(0).getFileName() != null ) {
            file.setFileName( mapContent( _fd.getFileTxt().get(0).getFileName().getContent().get(0) ) );
        } else {
            file.setFileName("file");
        }
        
        if (  _fd.getFileTxt().get(0).getFileCont() != null ) {
            file.setDescription( mapContent( _fd.getFileTxt().get(0).getFileCont().getContent().get(0) ) );
        }
        
        file.setFileSystemLocation( _fd.getURI() );
        // for now, don't do anything about content type'

        
        // now check if we have any variables associated (to see if we are subsettable)
        DataTable dt = (DataTable) filesMap.get( _fd.getID() );    
        if (dt != null) {
            file.setSubsettable(true);            

            dt.setStudyFile(file);
            file.setDataTable(dt);        

            DimensnsType _dim = _fd.getFileTxt().get(0).getDimensns();
            dt.setCaseQuantity( new Long( (String) _dim.getCaseQnty().get(0).getContent().get(0) ) );
            dt.setVarQuantity( new Long( (String) _dim.getVarQnty().get(0).getContent().get(0) ) );
        }
        
        
       
        Iterator iter = _fd.getNotes().iterator();
        while (iter.hasNext()) {
            NotesType _note = (NotesType) iter.next();
            if ( file.isSubsettable()  && "VDC:UNF".equals(_note.getType()) ) {
                dt.setUnf( mapUNF( (String) _note.getContent().get(0) ) );
            } else if ( "vdc:category".equals(_note.getType()) ) {
                String catName = (String) _note.getContent().get(0);
                addFileToCategory( file, catName, s );
                fileAdded = true;
            }
        }
        if (!fileAdded) {
            addFileToCategory( file, "", s );
        }
        
    }
    
    private void mapDataDscr(DataDscrType _dd, Map filesMap) {
        // Pre-fetch Variable data for efficiency
        List<VariableFormatType> variableFormatTypeList =  varService.findAllVariableFormatType(); 
        List<VariableIntervalType> variableIntervalTypeList =  varService.findAllVariableIntervalType(); 
        List<SummaryStatisticType> summaryStatisticTypeList =  varService.findAllSummaryStatisticType(); 
        List<VariableRangeType> variableRangeTypeList =  varService.findAllVariableRangeType(); 

        Iterator iter = _dd.getVar().iterator();
        int fileOrder = 0;
        while (iter.hasNext()) {
            VarType _dv = (VarType) iter.next();
            
            // if no location, then we can't map this variable to a file'
            if ( _dv.getLocation().size() != 0) {
                DataVariable dv = new DataVariable();
                dv.setName(_dv.getName());
                dv.setFileOrder( fileOrder++ );
                
                // map to file from FileMap (via location fileid)
                LocationType _loc = _dv.getLocation().get(0);
                String fileId = ((FileDscrType) _loc.getFileid()).getID();
                
                
                // find the datatable in our map; if null, create it
                DataTable dt = (DataTable) filesMap.get( fileId );
                if (dt == null) {
                    dt = new DataTable();
                    dt.setDataVariables( new ArrayList() );
                    filesMap.put(fileId, dt);
                }
                
                dt.getDataVariables().add(dv);
                dv.setDataTable( dt );
                
                // fileStartPos, FileEndPos, and RecSegNo
                // if these fields don't convert to Long, just leave blank'
                try {
                    dv.setFileStartPosition( new Long(_loc.getStartPos()) );
                } catch (NumberFormatException ex) {}
                try {
                    dv.setFileEndPosition( new Long(_loc.getEndPos()) );
                } catch (NumberFormatException ex) {}
                try {
                    dv.setRecordSegmentNumber( new Long(_loc.getRecSegNo()) );
                } catch (NumberFormatException ex) {}
                
                
                
                VarFormatType _format = _dv.getVarFormat();
                if (_format != null) {
                    dv.setVariableFormatType( varService.findVariableFormatTypeByName(variableFormatTypeList, _format.getType() ) );
                    dv.setFormatSchema( _format.getSchema() );
                    dv.setFormatSchemaName( _format.getFormatname() );
                }
                
                // interval type (DB value may be different than DDI value)
                String _interval = _dv.getIntrvl();
                _interval = VAR_INTERVAL_CONTIN.equals(_interval) ? DB_VAR_INTERVAL_TYPE_CONTINUOUS : _interval;
                dv.setVariableIntervalType( varService.findVariableIntervalTypeByName(variableIntervalTypeList, _interval ));
                
                dv.setWeighted( (_dv.getWgt() != null &&_dv.getWgt().equals(VAR_WEIGHTED)) );
                
                // labl
                Iterator labelIter = _dv.getLabl().iterator();
                while (labelIter.hasNext()) {
                    LablType _labl =  (LablType) labelIter.next();
                    if ( _labl.getLevel() != null && _labl.getLevel().toLowerCase().equals(LEVEL_VARIABLE) ) {
                        dv.setLabel( (String) _labl.getContent().get(0) );
                    }
                }
                
                // summaryStats
                dv.setSummaryStatistics(new ArrayList());
                Iterator sumStatIter = _dv.getSumStat().iterator();
                while (sumStatIter.hasNext()) {
                    SumStatType _ss = (SumStatType) sumStatIter.next();
                    SummaryStatistic ss = new SummaryStatistic();
                    ss.setType( varService.findSummaryStatisticTypeByName( summaryStatisticTypeList, _ss.getType() ) );
                    ss.setValue( (String) _ss.getContent().get(0) );
                    ss.setDataVariable(dv);
                    
                    dv.getSummaryStatistics().add(ss);
                }
                
                // notes (UNF)
                Iterator noteIter = _dv.getNotes().iterator();
                while (noteIter.hasNext()) {
                    NotesType _note = (NotesType) noteIter.next();
                    if ( "VDC:UNF".equals(_note.getType()) ) {
                        dv.setUnf( mapUNF( (String) _note.getContent().get(0) ) );
                    }
                }
                
                // category
                dv.setCategories(new ArrayList());
                Iterator catIter = _dv.getCatgry().iterator();
                while (catIter.hasNext()) {
                    dv.getCategories().add( mapCatgry( (CatgryType) catIter.next(), dv ) );
                }
                
                // invalid ranges
                dv.setInvalidRanges(new ArrayList());
                Iterator iRangeIter = _dv.getInvalrng().iterator();
                while (iRangeIter.hasNext()) {
                    InvalrngType _irange = (InvalrngType) iRangeIter.next();
                    Iterator choiceIter = _irange.getItemOrRange().iterator();
                    while (choiceIter.hasNext()) {
                        Object _choice = (Object) choiceIter.next();
                        if (_choice instanceof ItemType) {
                            ItemType _item = (ItemType) _choice;
                            VariableRange range = new VariableRange();
                            range.setBeginValue( _item.getVALUE() );
                            range.setBeginValueType(varService.findVariableRangeTypeByName( variableRangeTypeList, DB_VAR_RANGE_TYPE_POINT )  );
                            range.setDataVariable(dv);
                            dv.getInvalidRanges().add(range);
                            
                        } else if (_choice instanceof RangeType) {
                            RangeType _range = (RangeType) _choice;
                            VariableRange range = new VariableRange();
                            if ( _range.getMin() != null && !_range.getMin().equals("") ) {
                                range.setBeginValue( _range.getMin() );
                                range.setBeginValueType(varService.findVariableRangeTypeByName( variableRangeTypeList,DB_VAR_RANGE_TYPE_MIN )  );
                            } else if ( _range.getMinExclusive() != null && !_range.getMinExclusive().equals("") ) {
                                range.setBeginValue( _range.getMinExclusive() );
                                range.setBeginValueType(varService.findVariableRangeTypeByName( variableRangeTypeList,DB_VAR_RANGE_TYPE_MIN_EX )  );
                            }
                            
                            if ( _range.getMax() != null && !_range.getMax().equals("") ) {
                                range.setEndValue( _range.getMax() );
                                range.setEndValueType(varService.findVariableRangeTypeByName( variableRangeTypeList,DB_VAR_RANGE_TYPE_MAX )  );
                            } else if ( _range.getMaxExclusive() != null && !_range.getMaxExclusive().equals("") ) {
                                range.setEndValue( _range.getMaxExclusive() );
                                range.setEndValueType(varService.findVariableRangeTypeByName(variableRangeTypeList, DB_VAR_RANGE_TYPE_MAX_EX )  );
                            }
                            
                            range.setDataVariable(dv);
                            dv.getInvalidRanges().add(range);
                        }
                        
                    }
                }
                
                // concept
                if (_dv.getConcept().size() != 0) {
                    dv.setConcept( mapContent( _dv.getConcept().get(0).getContent().get(0) ) );
                }
                
                // universe
                if (_dv.getUniverse().size() != 0) {
                    dv.setUniverse( mapContent( _dv.getUniverse().get(0).getContent().get(0) ) );
                }
                
                // todo: qstnTxt: wait to handle until we know more of how we will use it
                // todo: wgt-var : waitng to see example
                
            }
        }
    }
    
    private VariableCategory mapCatgry( CatgryType _cat, DataVariable dv) {
        VariableCategory cat = new VariableCategory();
        
        // label
        Iterator labelIter = _cat.getLabl().iterator();
        while (labelIter.hasNext()) {
            LablType _labl =  (LablType) labelIter.next();
            if ( _labl.getLevel() != null && _labl.getLevel().toLowerCase().equals(LEVEL_CATEGORY) ) {
                cat.setLabel( (String) _labl.getContent().get(0) );
            }
        }
        
        // catStat
        Iterator catStatIter = _cat.getCatStat().iterator();
        while (catStatIter.hasNext()) {
            CatStatType _catStat =  (CatStatType) catStatIter.next();
            if ( "freq".equals(_catStat.getType()) ) {
                String _freq = mapContent( _catStat.getContent().get(0) );
                if (_freq != null && !_freq.equals("") ) {
                    cat.setFrequency( new Long( _freq ) );
                }
            }
        }
        
        cat.setValue( (String) _cat.getCatValu().getContent().get(0) );
        cat.setMissing( "Y".equals(_cat.getMissing())  );
        
        cat.setDataVariable(dv);
        return cat;
    }
    
    
    // generic helper methods
    
    private String mapList(List _list) {
        String returnVal = "";
        Iterator iter = _list.iterator();
        while (iter.hasNext()) {
            Object _elem = (Object) iter.next();
            // check type
            if (_elem instanceof NationType) {
                returnVal += mapContent( ((NationType) _elem).getContent().get(0) );
            } else if (_elem instanceof GeogCoverType) {
                returnVal += mapContent( ((GeogCoverType) _elem).getContent().get(0) );
            } else if (_elem instanceof GeogUnitType) {
                returnVal += mapContent( ((GeogUnitType) _elem).getContent().get(0) );
            } else if (_elem instanceof AnlyUnitType) {
                returnVal += mapContent( ((AnlyUnitType) _elem).getContent().get(0) );
            } else if (_elem instanceof UniverseType) {
                returnVal += mapContent( ((UniverseType) _elem).getContent().get(0) );
            } else if (_elem instanceof DataKindType) {
                returnVal += mapContent( ((DataKindType) _elem).getContent().get(0) );
            } else {
                throw new EJBException("Undefined type passed to mapList method");
            }
            
            if ( iter.hasNext() ) {
                returnVal += "; ";
            }
        }
        
        
        // if no values have been set, return null
        if (returnVal.equals("") ) {
            return null;
        }
        
        return returnVal;
    }
    
    private String mapDate(String _dateAttr, List _dateContent) {
        if ( _dateAttr != null && !_dateAttr.trim().equals("") ) {
            return _dateAttr;
        } else {
            return mapContent( _dateContent.get(0) ) ;
        }
    }
    
    private String mapContent(Object _content) {
        if (_content instanceof String) {
            return ((String) _content).trim().replace('\n',' ');
        } else if (_content instanceof JAXBElement) {
            Object _html = ((JAXBElement)_content).getValue();
            if ( _html instanceof PType) {
                return "<p>" + mapContent( ((PType)_html).getContent().get(0) ) + "</p>";
            } else if ( _html instanceof CitationType) {
                CitationType _citation = (CitationType)_html;
                String citation = "<!--  parsed from DDI citation title and holdings -->";
                citation += mapContent( _citation.getTitlStmt().getTitl().getContent().get(0) );
                
                boolean addHoldings = false;
                String holdings = "";
                for (HoldingsType _holdings : _citation.getHoldings() ) {
                    holdings += addHoldings ? ", " : "";     
                    if (_holdings.getURI() != null && !_holdings.getURI().trim().equals("") ) {
                        holdings += "<a href=\"" + _holdings.getURI() + "\">";
                        holdings += mapContent(_holdings.getContent().get(0));
                        holdings += "</a>";
                    } else {
                        holdings += mapContent(_holdings.getContent().get(0));
                    }
                    
                    addHoldings = true;
                }
                
                if (addHoldings) {
                    citation += " (" + holdings + ")";
                }
                return citation;
            } else {
                throw new EJBException("Unexpected JAXBElement in mapContent method: " + _html.getClass().getName() );
            }
        } else {
            throw new EJBException("Unexpected Type in mapContent method: " + _content.getClass().getName() );
        }
    }
    
    private String mapContentList(List _content, String searchingFor) {
        String returnVal = "";
        Iterator iter = _content.iterator();
        
        if ( searchingFor.equals("value") ) {
            while (iter.hasNext()) {
                Object elem = (Object) iter.next();
                if (elem instanceof String) {
                    returnVal += elem;
                }
            }
            
        } else {
            while (iter.hasNext()) {
                Object elem = (Object) iter.next();
                if (elem instanceof JAXBElement && ((JAXBElement) elem).getValue() instanceof ExtLinkType ) {
                    ExtLinkType _link = (ExtLinkType) ((JAXBElement) elem).getValue();
                    if ( searchingFor.equals("uri") && !"image".equals(_link.getRole() ) ) {
                        returnVal = _link.getURI();
                    } else if ( searchingFor.equals("logo") && "image".equals(_link.getRole() ) ) {
                        returnVal = _link.getURI();
                    }
                }
            }
        }
        
        return returnVal.trim();
    }
    
    
    private void addFileToCategory(StudyFile sf, String catName, Study study) {
        StudyFileEditBean fileBean = new StudyFileEditBean(sf);
        fileBean.setFileCategoryName(catName);
        fileBean.addFileToCategory(study);
    }
    
    //*************************************************************
    //
    // Methods for mapping from Study to DDI
    //
    //*************************************************************
    
    private CodeBook mapStudy(Study s) {
        return mapStudy( s, false );
    }
    
    private CodeBook mapStudy(Study s, boolean exportToLegacyVDC) {
        ObjectFactory objFactory = new ObjectFactory();
        CodeBook _cb = objFactory.createCodeBook();
        _cb.setVersion("2.0");
        
        // docdscr
        DocDscrType _doc = objFactory.createDocDscrType();
        _doc.setCitation( mapDocCitation(s) );
        _cb.getDocDscr().add(_doc);
        
        // studydscr
        StdyDscrType _sd = objFactory.createStdyDscrType();
        _cb.getStdyDscr().add(_sd);
        
        _sd.getCitation().add( mapCitation(s) );
        _sd.getStdyInfo().add( mapStudyInfo(s) );
        _sd.getMethod().add( mapMethod(s) );
        _sd.getDataAccs().add( mapDataAccess(s) );
        _sd.getOthrStdyMat().add( mapOtherStudyMaterial(s) );
        
        Iterator iter = s.getStudyNotes().iterator();
        while (iter.hasNext()) {
            StudyNote note = (StudyNote) iter.next();
            NotesType _note = objFactory.createNotesType();
            
            _note.getContent().add( note.getText() );
            _note.setSubject( note.getSubject() );
            _note.setType( note.getType() );
            
            _sd.getNotes().add(_note);
        }
        
        // files and data
        DataDscrType _dd = objFactory.createDataDscrType();
        boolean addDataDscr = false;
        
        // iterate through files
        iter = s.getStudyFiles().iterator();
        while (iter.hasNext()) {
            StudyFile sf = (StudyFile) iter.next();
            
            if ( sf.isSubsettable() ) {
                FileDscrType _fd = mapSubsettableFile( sf, exportToLegacyVDC );
                _cb.getFileDscr().add(_fd);
                
                if ( sf.getDataTable().getDataVariables().size() > 0 ) {
                    Iterator varIter = varService.getDataVariablesByFileOrder( sf.getDataTable().getId() ).iterator();
                    while (varIter.hasNext()) {
                        DataVariable dv = (DataVariable) varIter.next();
                        _dd.getVar().add( mapDataVariable(dv, _fd) );
                    }
                    addDataDscr = true;
                }
            } else {
                OtherMatType _om = mapOtherMaterialsFile(sf, exportToLegacyVDC);
                _cb.getOtherMat().add(_om);
            }
            
            
        }
        
        if (addDataDscr) { _cb.getDataDscr().add(_dd); }
        
        return _cb;
    }
    
    private CitationType mapDocCitation(Study s) {
        // DocDscr just jas title, handle, distributor and dist date
        ObjectFactory objFactory = new ObjectFactory();
        CitationType _citation = objFactory.createCitationType();
        
        // titlStmt
        TitlStmtType _titleStmt = objFactory.createTitlStmtType();
        _citation.setTitlStmt(_titleStmt);
        
        TitlType _title = objFactory.createTitlType();
        _title.getContent().add(s.getTitle());
        _titleStmt.setTitl(_title);
        
        
        IDNoType _studyId = objFactory.createIDNoType();
        _studyId.setAgency( AGENCY_HANDLE );
        _studyId.getContent().add( s.getGlobalId() );
        _titleStmt.getIDNo().add(_studyId);
        
        
        // distStmt
        DistStmtType _distStmt = objFactory.createDistStmtType();
        _citation.setDistStmt(_distStmt);
        
        DistrbtrType _dist = objFactory.createDistrbtrType();
        _dist.getContent().add( "Dataverse Network" );
        _distStmt.getDistrbtr().add(_dist);
        
        if ( s.getLastUpdateTime() != null ) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String lastUpdateString = sdf.format(s.getLastUpdateTime());
            
            DistDateType _distDate = objFactory.createDistDateType();
            _distDate.setDate( lastUpdateString );
            _distDate.getContent().add(lastUpdateString );
            _distStmt.setDistDate( _distDate );
        }
        
        return _citation;
    }
    
    private CitationType mapCitation(Study s) {
        ObjectFactory objFactory = new ObjectFactory();
        CitationType _citation = objFactory.createCitationType();
        
        // titlStmt
        TitlStmtType _titleStmt = objFactory.createTitlStmtType();
        
        // title is required
        TitlType _title = objFactory.createTitlType();
        _title.getContent().add(s.getTitle());
        _titleStmt.setTitl(_title);
        
        if (!StringUtil.isEmpty( s.getSubTitle() )) {
            SubTitlType _subtitle = objFactory.createSubTitlType();
            _subtitle.getContent().add(s.getSubTitle());
            _titleStmt.getSubTitl().add(_subtitle);
        }
        
        // we will always have a handle
        IDNoType _studyId = objFactory.createIDNoType();
        _studyId.setAgency( AGENCY_HANDLE );
        _studyId.getContent().add( s.getGlobalId() );
        _titleStmt.getIDNo().add(_studyId);
        
        Iterator iter = s.getStudyOtherIds().iterator();
        while (iter.hasNext()) {
            StudyOtherId otherId = (StudyOtherId) iter.next();
            IDNoType _otherId = objFactory.createIDNoType();
            _otherId.setAgency( otherId.getAgency() );
            _otherId.getContent().add(otherId.getOtherId());
            _titleStmt.getIDNo().add(_otherId);
        }
        
        // rspStmt
        RspStmtType _rspStmt = objFactory.createRspStmtType();
        boolean addRspStmt = false;
        
        iter = s.getStudyAuthors().iterator();
        while (iter.hasNext()) {
            StudyAuthor author = (StudyAuthor) iter.next();
            AuthEntyType _author = objFactory.createAuthEntyType();
            _author.setAffiliation( author.getAffiliation());
            _author.getContent().add(author.getName());
            _rspStmt.getAuthEnty().add(_author);
            addRspStmt = true;
        }
        
        
        // prodStmt
        ProdStmtType _prodStmt = objFactory.createProdStmtType();
        boolean addProdStmt = false;
        
        iter = s.getStudyProducers().iterator();
        while (iter.hasNext()) {
            StudyProducer prod = (StudyProducer) iter.next();
            ProducerType _prod = objFactory.createProducerType();
            _prod.setAbbr( prod.getAbbreviation() );
            _prod.setAffiliation( prod.getAffiliation() );
            _prod.getContent().add( objFactory.createProducerTypeExtLink( mapExternalLink(prod.getLogo(), "image") ) );
            _prod.getContent().add( objFactory.createProducerTypeExtLink( mapExternalLink( prod.getUrl(), null) ) );
            _prod.getContent().add( prod.getName() );
            _prodStmt.getProducer().add(_prod);
            addProdStmt = true;
        }
        
        if (!StringUtil.isEmpty( s.getProductionDate() )) {
            ProdDateType _prodDate = objFactory.createProdDateType();
            _prodDate.getContent().add( s.getProductionDate() );
            _prodDate.setDate( mapDateAttribute(s.getProductionDate()) );
            _prodStmt.getProdDate().add(_prodDate);
            addProdStmt = true;
        }
        
        if (!StringUtil.isEmpty( s.getProductionPlace() )) {
            ProdPlacType _prodPlac = objFactory.createProdPlacType();
            _prodPlac.getContent().add( s.getProductionPlace() );
            _prodStmt.getProdPlac().add(_prodPlac);
            addProdStmt = true;
        }
        
        iter = s.getStudySoftware().iterator();
        while (iter.hasNext()) {
            StudySoftware soft = (StudySoftware) iter.next();
            SoftwareType _soft = objFactory.createSoftwareType();
            _soft.getContent().add( soft.getName()) ;
            _soft.setVersion( soft.getSoftwareVersion() );
            _prodStmt.getSoftware().add(_soft);
            addProdStmt = true;
        }
        
        if (!StringUtil.isEmpty( s.getFundingAgency() )) {
            FundAgType _fundAg = objFactory.createFundAgType();
            _fundAg.getContent().add( s.getFundingAgency() );
            _prodStmt.getFundAg().add(_fundAg);
            addProdStmt = true;
        }
        
        iter = s.getStudyGrants().iterator();
        while (iter.hasNext()) {
            StudyGrant grant = (StudyGrant) iter.next();
            GrantNoType _grant = objFactory.createGrantNoType();
            _grant.getContent().add( grant.getNumber() );
            _grant.setAgency( grant.getAgency() );
            _prodStmt.getGrantNo().add(_grant);
            addProdStmt = true;
        }
        
        
        // distStmt
        DistStmtType _distStmt = objFactory.createDistStmtType();
        boolean addDistStmt = false;
        
        iter = s.getStudyDistributors().iterator();
        while (iter.hasNext()) {
            StudyDistributor dist = (StudyDistributor) iter.next();
            DistrbtrType _dist = objFactory.createDistrbtrType();
            _dist.setAbbr( dist.getAbbreviation() );
            _dist.setAffiliation( dist.getAffiliation() );
            _dist.getContent().add( objFactory.createDistrbtrTypeExtLink( mapExternalLink(dist.getLogo(), "image") ) );
            _dist.getContent().add( objFactory.createDistrbtrTypeExtLink( mapExternalLink( dist.getUrl(), null) ) );
            _dist.getContent().add( dist.getName() );
            _distStmt.getDistrbtr().add(_dist);
            addDistStmt = true;
        }
        
        if (!StringUtil.isEmpty( s.getDistributorContact()) || 
                !StringUtil.isEmpty( s.getDistributorContactEmail()) || 
                !StringUtil.isEmpty( s.getDistributorContactAffiliation()) ) {
            
            ContactType _contact = objFactory.createContactType();
            _contact.setAffiliation( s.getDistributorContactAffiliation() );
            _contact.setEmail( s.getDistributorContactEmail() );
            _contact.getContent().add( s.getDistributorContact() );
            _distStmt.getContact().add(_contact);
            addDistStmt = true;
        }
        
        if (!StringUtil.isEmpty( s.getDepositor() )) {
            DepositrType _depositer = objFactory.createDepositrType();
            _depositer.getContent().add( s.getDepositor() );
            _distStmt.getDepositr().add(_depositer);
            addDistStmt = true;
        }
        
        if (!StringUtil.isEmpty( s.getDistributionDate() )) {
            DistDateType _distDate = objFactory.createDistDateType();
            _distDate.getContent().add( s.getDistributionDate() );
            _distDate.setDate( mapDateAttribute(s.getDistributionDate()) );
            _distStmt.setDistDate( _distDate );
            addDistStmt = true;
        }
        
        if (!StringUtil.isEmpty( s.getDateOfDeposit() )) {
            DepDateType _depDate = objFactory.createDepDateType();
            _depDate.getContent().add( s.getDateOfDeposit() );
            _depDate.setDate( mapDateAttribute(s.getDateOfDeposit()) );
            _distStmt.getDepDate().add(_depDate);
            addDistStmt = true;
        }
        
        // serStmt
        SerStmtType _serStmt = objFactory.createSerStmtType();
        boolean addSerStmt = false;
        
        if (!StringUtil.isEmpty( s.getSeriesName() )) {
            SerNameType _serName = objFactory.createSerNameType();
            _serName.getContent().add( s.getSeriesName() );
            _serStmt.getSerName().add(_serName);
            addSerStmt = true;
        }
        
        if (!StringUtil.isEmpty( s.getSeriesInformation() )) {
            SerInfoType _serInfo = objFactory.createSerInfoType();
            _serInfo.getContent().add( s.getSeriesInformation() );
            _serStmt.getSerInfo().add(_serInfo);
            addSerStmt = true;
        }
        
        // verstmt
        VerStmtType _verStmt = objFactory.createVerStmtType();
        boolean addVerStmt = false;
        
        if (!StringUtil.isEmpty( s.getStudyVersion()) || !StringUtil.isEmpty( s.getVersionDate()) ) {
            VersionType _version = objFactory.createVersionType();
            _version.setDate( s.getVersionDate() );
            _version.getContent().add( s.getStudyVersion() );
            _verStmt.setVersion(_version);
            addVerStmt = true;
        }
        
        // study UNF
        if (! StringUtil.isEmpty( s.getUNF()) ) {
            NotesType _unf = objFactory.createNotesType();
            _unf.getContent().add( s.getUNF() );
            _unf.setLevel(LEVEL_STUDY);
            _unf.setSubject( NOTE_SUBJECT_UNF );
            _unf.setType( NOTE_TYPE_UNF );
            
            _citation.getNotes().add(_unf);
        }
        
        // now add the pieces
        _citation.setTitlStmt(_titleStmt);
        if (addRspStmt)     { _citation.setRspStmt(_rspStmt); }
        if (addProdStmt)    { _citation.setProdStmt(_prodStmt); }
        if (addDistStmt)    { _citation.setDistStmt(_distStmt); }
        if (addSerStmt)     { _citation.setSerStmt(_serStmt); }
        if (addVerStmt)     { _citation.getVerStmt().add(_verStmt); }
        
        return _citation;
    }
    
    private StdyInfoType mapStudyInfo(Study s) {
        ObjectFactory objFactory = new ObjectFactory();
        StdyInfoType _si = objFactory.createStdyInfoType();
        boolean addStudyInfo = false;
        
        Iterator iter = s.getStudyAbstracts().iterator();
        while (iter.hasNext()) {
            StudyAbstract studyAbstract = (StudyAbstract) iter.next();
            AbstractType _abstract = objFactory.createAbstractType();
            _abstract.setDate(studyAbstract.getDate());
            _abstract.getContent().add( studyAbstract.getText() );
            _si.getAbstract().add(_abstract);
            addStudyInfo = true;
        }
        
        // subject
        SubjectType _subject = objFactory.createSubjectType();
        boolean addSubject = false;
        
        iter = s.getStudyKeywords().iterator();
        while (iter.hasNext()) {
            StudyKeyword kw = (StudyKeyword) iter.next();
            KeywordType _kw = objFactory.createKeywordType();
            _kw.setVocab( kw.getVocab() );
            _kw.setVocabURI( kw.getVocabURI() );
            _kw.getContent().add( kw.getValue() );
            _subject.getKeyword().add(_kw);
            addSubject = true;
        }
        
        iter = s.getStudyTopicClasses().iterator();
        while (iter.hasNext()) {
            StudyTopicClass tc = (StudyTopicClass) iter.next();
            TopcClasType _tc = objFactory.createTopcClasType();
            _tc.setVocab( tc.getVocab() );
            _tc.setVocabURI( tc.getVocabURI() );
            _tc.getContent().add( tc.getValue() );
            _subject.getTopcClas().add(_tc);
            addSubject = true;
        }
        
        // sumDscr *
        SumDscrType _sum = objFactory.createSumDscrType();
        boolean addSumDscr = false;
        
        //time period
        if (!StringUtil.isEmpty( s.getTimePeriodCoveredStart() )) {
            TimePrdType _tp_start = objFactory.createTimePrdType();
            _tp_start.getContent().add( s.getTimePeriodCoveredStart() );
            _tp_start.setDate( mapDateAttribute(s.getTimePeriodCoveredStart()) );
            _tp_start.setEvent(EVENT_START);
            _sum.getTimePrd().add( _tp_start );
            addSumDscr = true;
        }
        
        if (!StringUtil.isEmpty( s.getTimePeriodCoveredEnd() )) {
            TimePrdType _tp_end = objFactory.createTimePrdType();
            _tp_end.getContent().add( s.getTimePeriodCoveredEnd() );
            _tp_end.setDate( mapDateAttribute(s.getTimePeriodCoveredEnd()) );
            _tp_end.setEvent(EVENT_END);
            _sum.getTimePrd().add( _tp_end );
            addSumDscr = true;
        }
        
        // collection date
        if (!StringUtil.isEmpty( s.getDateOfCollectionStart() )) {
            CollDateType _cd_start = objFactory.createCollDateType();
            _cd_start.getContent().add( s.getDateOfCollectionStart() );
            _cd_start.setDate( mapDateAttribute(s.getDateOfCollectionStart()) );
            _cd_start.setEvent(EVENT_START);
            _sum.getCollDate().add( _cd_start );
            addSumDscr = true;
        }
        
        if (!StringUtil.isEmpty( s.getDateOfCollectionEnd() )) {
            CollDateType _cd_end = objFactory.createCollDateType();
            _cd_end.getContent().add( s.getDateOfCollectionEnd() );
            _cd_end.setDate( mapDateAttribute(s.getDateOfCollectionEnd()) );
            _cd_end.setEvent(EVENT_END);
            _sum.getCollDate().add( _cd_end );
            addSumDscr = true;
        }
        
        if (!StringUtil.isEmpty( s.getCountry() )) {
            NationType _nation = objFactory.createNationType();
            _nation.getContent().add( s.getCountry() );
            _sum.getNation().add(_nation);
            addSumDscr = true;
        }
        
        if (!StringUtil.isEmpty( s.getGeographicCoverage() )) {
            GeogCoverType _gc = objFactory.createGeogCoverType();
            _gc.getContent().add( s.getGeographicCoverage() );
            _sum.getGeogCover().add(_gc);
            addSumDscr = true;
        }
        
        if (!StringUtil.isEmpty( s.getGeographicUnit() )) {
            GeogUnitType _gu = objFactory.createGeogUnitType();
            _gu.getContent().add( s.getGeographicUnit() );
            _sum.getGeogUnit().add(_gu);
            addSumDscr = true;
        }
        
        // we store geoboundings as list but there is only one
        if (s.getStudyGeoBoundings() != null && s.getStudyGeoBoundings().size() != 0) {
            StudyGeoBounding gbb = s.getStudyGeoBoundings().get(0);
            
            GeoBndBoxType _gbb = objFactory.createGeoBndBoxType();
            _sum.setGeoBndBox(_gbb);
            
            WestBLType _west = objFactory.createWestBLType();
            _west.getContent().add( gbb.getWestLongitude() );
            _gbb.setWestBL(_west);
            
            EastBLType _east = objFactory.createEastBLType();
            _east.getContent().add( gbb.getEastLongitude() );
            _gbb.setEastBL(_east);
            
            NorthBLType _north = objFactory.createNorthBLType();
            _north.getContent().add( gbb.getNorthLatitude() );
            _gbb.setNorthBL(_north);
            
            SouthBLType _south = objFactory.createSouthBLType();
            _south.getContent().add( gbb.getSouthLatitude() );
            _gbb.setSouthBL(_south);
            
            addSumDscr = true;
        }
        
        if (!StringUtil.isEmpty( s.getUnitOfAnalysis() )) {
            AnlyUnitType _au = objFactory.createAnlyUnitType();
            _au.getContent().add( s.getUnitOfAnalysis() );
            _sum.getAnlyUnit().add(_au);
            addSumDscr = true;
        }
        
        if (!StringUtil.isEmpty( s.getUniverse() )) {
            UniverseType _universe = objFactory.createUniverseType();
            _universe.getContent().add( s.getUniverse() );
            _sum.getUniverse().add(_universe);
            addSumDscr = true;
        }
        
        if (!StringUtil.isEmpty( s.getKindOfData() )) {
            DataKindType _dk = objFactory.createDataKindType();
            _dk.getContent().add( s.getKindOfData() );
            _sum.getDataKind().add(_dk);
            addSumDscr = true;
        }
        
        if (addSubject) {
            _si.setSubject(_subject);
            addStudyInfo = true;
        }
        if (addSumDscr) {
            _si.getSumDscr().add(_sum);
            addStudyInfo = true;
        }
        // and we already checked for abstracts
        
        return addStudyInfo ? _si : null;
    }
    
    private MethodType mapMethod(Study s) {
        ObjectFactory objFactory = new ObjectFactory();
        MethodType _method = objFactory.createMethodType();
        boolean addMethod = false;
        
        // Data Coll
        DataCollType _dc = objFactory.createDataCollType();
        boolean addDataColl = false;
        
        if (!StringUtil.isEmpty( s.getTimeMethod() )) {
            TimeMethType _tm = objFactory.createTimeMethType();
            _tm.getContent().add(s.getTimeMethod() );
            _dc.getTimeMeth().add(_tm);
            addDataColl = true;
        }
        
        if (!StringUtil.isEmpty( s.getDataCollector() )) {
            DataCollectorType _dcr = objFactory.createDataCollectorType();
            _dcr.getContent().add(s.getDataCollector() );
            _dc.getDataCollector().add(_dcr);
            addDataColl = true;
        }
        
        if (!StringUtil.isEmpty( s.getFrequencyOfDataCollection() )) {
            FrequencType _freq = objFactory.createFrequencType();
            _freq.getContent().add(s.getFrequencyOfDataCollection() );
            _dc.getFrequenc().add(_freq);
            addDataColl = true;
        }
        
        if (!StringUtil.isEmpty( s.getSamplingProcedure() )) {
            SampProcType _sp = objFactory.createSampProcType();
            _sp.getContent().add(s.getSamplingProcedure() );
            _dc.getSampProc().add(_sp);
            addDataColl = true;
        }
        
        if (!StringUtil.isEmpty( s.getDeviationsFromSampleDesign() )) {
            DeviatType _dev = objFactory.createDeviatType();
            _dev.getContent().add(s.getDeviationsFromSampleDesign() );
            _dc.getDeviat().add(_dev);
            addDataColl = true;
        }
        
        if (!StringUtil.isEmpty( s.getCollectionMode() )) {
            CollModeType _cm = objFactory.createCollModeType();
            _cm.getContent().add(s.getCollectionMode() );
            _dc.getCollMode().add(_cm);
            addDataColl = true;
        }
        
        if (!StringUtil.isEmpty( s.getResearchInstrument() )) {
            ResInstruType _ri = objFactory.createResInstruType();
            _ri.getContent().add(s.getResearchInstrument() );
            _dc.getResInstru().add(_ri);
            addDataColl = true;
        }
        
        //source
        SourcesType _sources = objFactory.createSourcesType();
        boolean addSources = false;
        
        if (!StringUtil.isEmpty( s.getDataSources() )) {
            DataSrcType _ds = objFactory.createDataSrcType();
            _ds.getContent().add( s.getDataSources() );
            _sources.getDataSrc().add(_ds);
            addSources = true;
        }
        
        if (!StringUtil.isEmpty( s.getOriginOfSources() )) {
            SrcOrigType _so = objFactory.createSrcOrigType();
            _so.getContent().add( s.getOriginOfSources() );
            _sources.getSrcOrig().add(_so);
            addSources = true;
        }
        
        if (!StringUtil.isEmpty( s.getCharacteristicOfSources() )) {
            SrcCharType _sc = objFactory.createSrcCharType();
            _sc.getContent().add( s.getCharacteristicOfSources() );
            _sources.getSrcChar().add(_sc);
            addSources = true;
        }
        
        if (!StringUtil.isEmpty( s.getAccessToSources() )) {
            SrcDocuType _sd = objFactory.createSrcDocuType();
            _sd.getContent().add( s.getAccessToSources() );
            _sources.getSrcDocu().add(_sd);
            addSources = true;
        }
        
        if (addSources) {
            _dc.setSources(_sources);
            addDataColl = true;
        }
        // end source
        
        if (!StringUtil.isEmpty( s.getDataCollectionSituation() )) {
            CollSituType _cs = objFactory.createCollSituType();
            _cs.getContent().add(s.getDataCollectionSituation() );
            _dc.getCollSitu().add(_cs);
            addDataColl = true;
        }
        
        if (!StringUtil.isEmpty( s.getActionsToMinimizeLoss() )) {
            ActMinType _am = objFactory.createActMinType();
            _am.getContent().add(s.getActionsToMinimizeLoss() );
            _dc.getActMin().add(_am);
            addDataColl = true;
        }
        
        if (!StringUtil.isEmpty( s.getControlOperations() )) {
            ConOpsType _conOps = objFactory.createConOpsType();
            _conOps.getContent().add(s.getControlOperations() );
            _dc.getConOps().add(_conOps);
            addDataColl = true;
        }
        
        if (!StringUtil.isEmpty( s.getWeighting() )) {
            WeightType _weight = objFactory.createWeightType();
            _weight.getContent().add(s.getWeighting() );
            _dc.getWeight().add(_weight);
            addDataColl = true;
        }
        
        if (!StringUtil.isEmpty( s.getCleaningOperations() )) {
            CleanOpsType _cleanOps = objFactory.createCleanOpsType();
            _cleanOps.getContent().add(s.getCleaningOperations() );
            _dc.getCleanOps().add(_cleanOps);
            addDataColl = true;
        }
        
        //notes
        if (!StringUtil.isEmpty( s.getStudyLevelErrorNotes() )) {
            NotesType _notes = objFactory.createNotesType();
            _notes.getContent().add( s.getStudyLevelErrorNotes() );
            _method.getNotes().add(_notes);
            addMethod = true;
        }
        
        //Anly Info
        AnlyInfoType _ai = objFactory.createAnlyInfoType();
        boolean addAnlyInfo = false;
        
        if (!StringUtil.isEmpty( s.getResponseRate() )) {
            RespRateType _rr = objFactory.createRespRateType();
            _rr.getContent().add(s.getResponseRate() );
            _ai.getRespRate().add(_rr);
            addAnlyInfo = true;
        }
        
        if (!StringUtil.isEmpty( s.getSamplingErrorEstimate() )) {
            EstSmpErrType _ese = objFactory.createEstSmpErrType();
            _ese.getContent().add(s.getSamplingErrorEstimate() );
            _ai.getEstSmpErr().add(_ese);
            addAnlyInfo = true;
        }
        
        if (!StringUtil.isEmpty( s.getOtherDataAppraisal() )) {
            DataApprType _da = objFactory.createDataApprType();
            _da.getContent().add(s.getOtherDataAppraisal() );
            _ai.getDataAppr().add(_da);
            addAnlyInfo = true;
        }
        
        
        if (addDataColl) {
            _method.getDataColl().add(_dc);
            addMethod = true;
        }
        if (addAnlyInfo) {
            _method.setAnlyInfo(_ai);
            addMethod = true;
        }
        // and we alreayd checked for notes
        
        return addMethod ? _method : null;
    }
    
    private DataAccsType mapDataAccess(Study s) {
        ObjectFactory objFactory = new ObjectFactory();
        DataAccsType _da = objFactory.createDataAccsType();
        boolean addDataAccess = false;
        
        // set avail
        SetAvailType _sa = objFactory.createSetAvailType();
        boolean addSetAvail = false;
        
        if (!StringUtil.isEmpty( s.getPlaceOfAccess() )) {
            AccsPlacType _ap = objFactory.createAccsPlacType();
            _ap.getContent().add( s.getPlaceOfAccess() );
            _sa.getAccsPlac().add(_ap);
            addSetAvail = true;
        }
        
        if (!StringUtil.isEmpty( s.getOriginalArchive() )) {
            OrigArchType _oa = objFactory.createOrigArchType();
            _oa.getContent().add( s.getOriginalArchive() );
            _sa.setOrigArch(_oa);
            addSetAvail = true;
        }
        
        if (!StringUtil.isEmpty( s.getAvailabilityStatus() )) {
            AvlStatusType _as = objFactory.createAvlStatusType();
            _as.getContent().add( s.getAvailabilityStatus() );
            _sa.getAvlStatus().add(_as);
            addSetAvail = true;
        }
        
        if (!StringUtil.isEmpty( s.getCollectionSize() )) {
            CollSizeType _cs = objFactory.createCollSizeType();
            _cs.getContent().add( s.getCollectionSize() );
            _sa.setCollSize(_cs);
            addSetAvail = true;
        }
        
        if (!StringUtil.isEmpty( s.getStudyCompletion() )) {
            CompleteType _comp = objFactory.createCompleteType();
            _comp.getContent().add( s.getStudyCompletion() );
            _sa.setComplete(_comp);
            addSetAvail = true;
        }
        
        
        // use statement
        UseStmtType _us = objFactory.createUseStmtType();
        boolean addUseStmt = false;
        
        if (!StringUtil.isEmpty( s.getConfidentialityDeclaration() )) {
            ConfDecType _cd = objFactory.createConfDecType();
            _cd.getContent().add( s.getConfidentialityDeclaration() );
            _us.setConfDec(_cd);
            addUseStmt = true;
        }
        
        if (!StringUtil.isEmpty( s.getSpecialPermissions() )) {
            SpecPermType _sp = objFactory.createSpecPermType();
            _sp.getContent().add( s.getSpecialPermissions() );
            _us.setSpecPerm(_sp);
            addUseStmt = true;
        }
        
        if (!StringUtil.isEmpty( s.getRestrictions() )) {
            RestrctnType _rest = objFactory.createRestrctnType();
            _rest.getContent().add( s.getRestrictions() );
            _us.setRestrctn(_rest);
            addUseStmt = true;
        }
        
        
        if (!StringUtil.isEmpty( s.getContact() )) {
            ContactType _contact = objFactory.createContactType();
            _contact.getContent().add( s.getContact() );
            _us.getContact().add(_contact);
            addUseStmt = true;
        }
        
        if (!StringUtil.isEmpty( s.getCitationRequirements() )) {
            CitReqType _cr = objFactory.createCitReqType();
            _cr.getContent().add( s.getCitationRequirements() );
            _us.setCitReq(_cr);
            addUseStmt = true;
        }
        
        if (!StringUtil.isEmpty( s.getDepositorRequirements() )) {
            DeposReqType _dr = objFactory.createDeposReqType();
            _dr.getContent().add( s.getDepositorRequirements() );
            _us.setDeposReq(_dr);
            addUseStmt = true;
        }
        
        if (!StringUtil.isEmpty( s.getConditions() )) {
            ConditionsType _cond = objFactory.createConditionsType();
            _cond.getContent().add( s.getConditions() );
            _us.setConditions(_cond);
            addUseStmt = true;
        }
        
        if (!StringUtil.isEmpty( s.getDisclaimer() )) {
            DisclaimerType _disc = objFactory.createDisclaimerType();
            _disc.getContent().add( s.getDisclaimer() );
            _us.setDisclaimer(_disc);
            addUseStmt = true;
        }
        
        if (addSetAvail) {
            _da.getSetAvail().add(_sa);
            addDataAccess = true;
        }
        if (addUseStmt) {
            _da.getUseStmt().add(_us);
            addDataAccess = true;
        }
        
        return addDataAccess ? _da : null;
    }
    
    private OthrStdyMatType mapOtherStudyMaterial(Study s) {
        ObjectFactory objFactory = new ObjectFactory();
        OthrStdyMatType _osm = objFactory.createOthrStdyMatType();
        boolean addOSM = false;
        
        Iterator iter = s.getStudyRelMaterials().iterator();
        while (iter.hasNext()) {
            StudyRelMaterial rm = (StudyRelMaterial) iter.next();
            RelMatType _rm = objFactory.createRelMatType();
            _rm.getContent().add( rm.getText() );
            _osm.getRelMat().add(_rm);
            addOSM = true;
        }
        
        iter = s.getStudyRelStudies().iterator();
        while (iter.hasNext()) {
            StudyRelStudy rs = (StudyRelStudy) iter.next();
            RelStdyType _rs = objFactory.createRelStdyType();
            _rs.getContent().add( rs.getText() );
            _osm.getRelStdy().add(_rs);
            addOSM = true;
        }
        
        iter = s.getStudyRelPublications().iterator();
        while (iter.hasNext()) {
            StudyRelPublication rp = (StudyRelPublication) iter.next();
            RelPublType _rp = objFactory.createRelPublType();
            _rp.getContent().add( rp.getText() );
            _osm.getRelPubl().add(_rp);
            addOSM = true;
        }
        
        iter = s.getStudyOtherRefs().iterator();
        while (iter.hasNext()) {
            StudyOtherRef or = (StudyOtherRef) iter.next();
            OthRefsType _or = objFactory.createOthRefsType();
            _or.getContent().add( or.getText() );
            _osm.getOthRefs().add(_or);
            addOSM = true;
        }
        
        // add replication for as a related material
        if (!StringUtil.isEmpty( s.getReplicationFor() )) {
            RelMatType _rm = objFactory.createRelMatType();
            _rm.getContent().add( s.getReplicationFor() );
            _rm.setType("replicationFor");
            _osm.getRelMat().add(_rm);
            addOSM = true;
        }
        
        return addOSM ? _osm : null;
    }
    
    
    private FileDscrType mapSubsettableFile(StudyFile sf, boolean exportToLegacyVDC) {
        DataTable dt = sf.getDataTable();
        
        ObjectFactory objFactory = new ObjectFactory();
        FileDscrType _fd = objFactory.createFileDscrType();
        _fd.setID("f" + sf.getId().toString());
        _fd.setURI( mapFileLocation(sf, exportToLegacyVDC) );
        
        // File Text (file name, file cont, dimensions, file type)
        FileNameType _fileName = objFactory.createFileNameType();
        _fileName.getContent().add(sf.getFileName());
        
        FileContType _fileCont = objFactory.createFileContType();
        _fileCont.getContent().add(sf.getDescription());
        
        CaseQntyType _cq = objFactory.createCaseQntyType();
        _cq.getContent().add(dt.getCaseQuantity().toString());
        VarQntyType _vq = objFactory.createVarQntyType();
        _vq.getContent().add(dt.getVarQuantity().toString());
        
        DimensnsType _dim = objFactory.createDimensnsType();
        _dim.getCaseQnty().add(_cq);
        _dim.getVarQnty().add(_vq);
        
        FileTypeType _fileType = objFactory.createFileTypeType();
        _fileType.getContent().add(sf.getFileType());
        
        FileTxtType _fileText = objFactory.createFileTxtType();
        _fileText.setFileName(_fileName);
        _fileText.setFileCont(_fileCont);
        _fileText.setDimensns(_dim);
        _fileText.setFileType(_fileType);
        
        _fd.getFileTxt().add(_fileText);
        
        // notes
        NotesType _unf = objFactory.createNotesType();
        _unf.setLevel(LEVEL_FILE);
        _unf.setType(NOTE_TYPE_UNF);
        _unf.setSubject(NOTE_SUBJECT_UNF);
        _unf.getContent().add(dt.getUnf());
        
        NotesType _cat = objFactory.createNotesType();
        _cat.setType("vdc:category");
        _cat.getContent().add(sf.getFileCategory().getName());
        
        // we don't yet store original file type!!!'
        // do we want this in the DDI export????
        //NotesType _origFileType = objFactory.createNotesType();
        //_origFileType.setLevel(LEVEL_FILE);
        //_origFileType.setType("VDC:MIME");
        //_origFileType.setSubject("original file format");
        //_origFileType.getContent().add( ORIGINAL_FILE_TYPE );
        
        _fd.getNotes().add(_unf);
        _fd.getNotes().add(_cat);
        //_fd.getNotes().add(_origFileType);
        
        // other todo's from import
        
        return _fd;
    }
    
    private VarType mapDataVariable(DataVariable dv, FileDscrType _fd) {
        ObjectFactory objFactory = new ObjectFactory();
        VarType _dv = objFactory.createVarType();
        _dv.setID("v" + dv.getId().toString());
        _dv.setName(dv.getName());
        if (dv.getVariableIntervalType() != null) {
            String interval = dv.getVariableIntervalType().getName();
            interval = DB_VAR_INTERVAL_TYPE_CONTINUOUS.equals(interval) ? VAR_INTERVAL_CONTIN : interval;
            _dv.setIntrvl( interval );
        }
        
        LocationType _loc = objFactory.createLocationType();
        _loc.setFileid( _fd );
        _loc.setRecSegNo( dv.getRecordSegmentNumber() != null ? dv.getRecordSegmentNumber().toString() : null );
        _loc.setStartPos( dv.getFileStartPosition() != null ? dv.getFileStartPosition().toString() : null );
        _loc.setEndPos( dv.getFileEndPosition() != null ? dv.getFileEndPosition().toString() : null );
        _dv.getLocation().add( _loc );
        
        if (!StringUtil.isEmpty( dv.getLabel() )) {
            LablType _labl = objFactory.createLablType();
            _labl.setLevel(LEVEL_VARIABLE);
            _labl.getContent().add(dv.getLabel());
            _dv.getLabl().add(_labl);
        }
        
        // summary stats
        Iterator iter = dv.getSummaryStatistics().iterator();
        while (iter.hasNext()) {
            SummaryStatistic ss = (SummaryStatistic) iter.next();
            SumStatType _ss = objFactory.createSumStatType();
            _ss.setType(ss.getType().getName());
            _ss.getContent().add(ss.getValue());
            _dv.getSumStat().add(_ss);
            
        }
        
        // category
        iter = dv.getCategories().iterator();
        while (iter.hasNext()) {
            VariableCategory cat = (VariableCategory) iter.next();
            CatgryType _cat = objFactory.createCatgryType();
            _cat.setMissing( cat.isMissing() ? "Y" : "N" );
            
            // catValu
            CatValuType _catValu = objFactory.createCatValuType();
            _catValu.getContent().add(cat.getValue());
            _cat.setCatValu(_catValu);
            
            // label
            if (!StringUtil.isEmpty( cat.getLabel() )) {
                LablType _catLabl = objFactory.createLablType();
                _catLabl.setLevel(LEVEL_CATEGORY);
                _catLabl.getContent().add(cat.getLabel());
                _cat.getLabl().add(_catLabl);
            }
            
            // catStat: freq
            if (cat.getFrequency() != null) {
                CatStatType _catStat = objFactory.createCatStatType();
                _catStat.setType("freq");
                _catStat.getContent().add(cat.getFrequency().toString());
                _cat.getCatStat().add(_catStat);
            }
            
            _dv.getCatgry().add(_cat);
            
        }
        
        // invalid ranges
        iter = dv.getInvalidRanges().iterator();
        InvalrngType _invalidRange = objFactory.createInvalrngType();
        
        while (iter.hasNext()) {
            VariableRange range = (VariableRange) iter.next();
            if (range.getBeginValueType() != null && range.getBeginValueType().getName().equals(DB_VAR_RANGE_TYPE_POINT)) {
                // create item
                if (range.getBeginValue() != null ) {
                    ItemType _item = objFactory.createItemType();
                    _item.setVALUE( range.getBeginValue() );
                    _invalidRange.getItemOrRange().add( _item );
                }
                
            } else {
                //create range
                RangeType _range = objFactory.createRangeType();
                _invalidRange.getItemOrRange().add( _range );
                
                if ( range.getBeginValueType() != null && range.getBeginValue() != null ) {
                    if ( range.getBeginValueType().getName().equals(DB_VAR_RANGE_TYPE_MIN) ) {
                        _range.setMin( range.getBeginValue() );
                    } else if ( range.getBeginValueType().getName().equals(DB_VAR_RANGE_TYPE_MIN_EX) ) {
                        _range.setMinExclusive( range.getBeginValue() );
                    }
                }
                
                if ( range.getEndValueType() != null && range.getEndValue() != null) {
                    if ( range.getEndValueType().getName().equals(DB_VAR_RANGE_TYPE_MAX) ) {
                        _range.setMax( range.getEndValue() );
                    } else if ( range.getEndValueType().getName().equals(DB_VAR_RANGE_TYPE_MAX_EX) ) {
                        _range.setMaxExclusive( range.getEndValue() );
                    }
                    
                }
            }
        }
        
        if (_invalidRange.getItemOrRange().size() > 0) {
            _dv.getInvalrng().add(_invalidRange);
        }
        
        VarFormatType _format = objFactory.createVarFormatType();
        _format.setType(dv.getVariableFormatType().getName());
        _format.setSchema( dv.getFormatSchema() );
        _format.setFormatname( dv.getFormatSchemaName() );
        _dv.setVarFormat(_format);
        
        //concept
        if (!StringUtil.isEmpty( dv.getConcept() )) {
            ConceptType _concept = objFactory.createConceptType();
            _concept.getContent().add( dv.getConcept() );
            _dv.getConcept().add( _concept );
        }
        //universe
        if (!StringUtil.isEmpty( dv.getUniverse() )) {
            UniverseType _universe = objFactory.createUniverseType();
            _universe.getContent().add( dv.getUniverse() );
            _dv.getUniverse().add( _universe );
        }
        
        // notes
        NotesType _unf = objFactory.createNotesType();
        _unf.setLevel(LEVEL_VARIABLE);
        _unf.setType(NOTE_TYPE_UNF);
        _unf.setSubject(NOTE_SUBJECT_UNF);
        _unf.getContent().add(dv.getUnf());
        _dv.getNotes().add(_unf);
        
        return _dv;
    }
    private ExtLinkType mapExternalLink(String url, String type) {
        ObjectFactory objFactory = new ObjectFactory();
        ExtLinkType _link = objFactory.createExtLinkType();
        _link.setURI(url);
        _link.setRole(type);
        return _link;
    }
    
    private OtherMatType mapOtherMaterialsFile(StudyFile sf, boolean exportToLegacyVDC) {
        ObjectFactory objFactory = new ObjectFactory();
        OtherMatType _om = objFactory.createOtherMatType();
        _om.setLevel(LEVEL_STUDY);
        
        LablType _name = objFactory.createLablType();
        _name.getContent().add( sf.getFileName() );
        _om.getLabl().add( _name );
        
        TxtType _desc = objFactory.createTxtType();
        _desc.getContent().add( sf.getDescription() );
        _om.setTxt( _desc );
        
        _om.setURI( mapFileLocation( sf, exportToLegacyVDC ) );
        
        NotesType _cat = objFactory.createNotesType();
        _cat.setType("vdc:category");
        _cat.getContent().add(sf.getFileCategory().getName());
        _om.getNotes().add(_cat);
        
        return _om;
    }
    
    private String mapFileLocation(StudyFile sf, boolean exportToLegacyVDC) {
        String fileLocation = "";
        Study s = sf.getFileCategory().getStudy();
        
        if (exportToLegacyVDC) {
            fileLocation = LEGACY_VDC_PREFIX + s.getAuthority() + "/";
            fileLocation += s.getStudyId() + "/";
            fileLocation += sf.getFileSystemName();
            return fileLocation;
        }
        
        // otherwise, determine whether file is local or harvested
        if (sf.getFileSystemLocation() != null && sf.getFileSystemLocation().startsWith("http") ) {
            return sf.getFileSystemLocation();
        } else {
            fileLocation = URL_PREFIX + "/dvn/dv/" + s.getOwner().getAlias() + "/FileDownload/";
            fileLocation += sf.getFileName()+ "?fileId=" + sf.getId();
            return fileLocation;
        }
    }
    
    private String mapDateAttribute(String date) {
        if ( DateUtil.validateDate(date) ) {
            return date;
        } else {
            return null;
        }
    }
    
    public  void exportStudy(Study study, Writer out) throws IOException, JAXBException {
        exportStudy(study, out, false);
    }
    
    public  void exportStudy(Study study, Writer out, boolean exportToLegacyVDC) throws IOException, JAXBException {
        
        JAXBContext jc = JAXBContext.newInstance("edu.harvard.hmdc.vdcnet.jaxb.ddi20");
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(true));
        marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://www.icpsr.umich.edu/DDI http://www.icpsr.umich.edu/DDI/Version2-0.xsd");
        
        marshaller.marshal(mapStudy(study, exportToLegacyVDC), out );
        
        
        
    }
}
