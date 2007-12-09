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
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.ItmType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.KeywordType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.LablType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.ListType;
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
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.RecPrCasType;
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
import edu.harvard.hmdc.vdcnet.util.FileUtil;
import edu.harvard.hmdc.vdcnet.util.PropertyUtil;
import edu.harvard.hmdc.vdcnet.util.StringUtil;
import edu.harvard.hmdc.vdcnet.vdc.VDCNetworkServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDCServiceLocal;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.EJBs;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

/**
 *
 * @author gdurand
 */
@Stateless

public class DDI20ServiceBean implements edu.harvard.hmdc.vdcnet.ddi.DDI20ServiceLocal {
    @EJB VariableServiceLocal varService;
    @EJB VDCNetworkServiceLocal vdcNetworkService;
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
    
    public static final String NOTE_TYPE_TERMS_OF_USE = "DVN:TOU";
    public static final String NOTE_SUBJECT_TERMS_OF_USE = "Dataverse Terms Of Use";
    
    // db constants
    public static final String DB_VAR_INTERVAL_TYPE_CONTINUOUS = "continuous";
    public static final String DB_VAR_RANGE_TYPE_POINT = "point";
    public static final String DB_VAR_RANGE_TYPE_MIN = "min";
    public static final String DB_VAR_RANGE_TYPE_MIN_EX = "min exclusive";
    public static final String DB_VAR_RANGE_TYPE_MAX = "max";
    public static final String DB_VAR_RANGE_TYPE_MAX_EX = "max exclusive";
    
    public List<VariableFormatType> variableFormatTypeList =  null;
    public List<VariableIntervalType> variableIntervalTypeList =  null;
    public List<SummaryStatisticType> summaryStatisticTypeList =  null;
    public List<VariableRangeType> variableRangeTypeList =  null;    
  
    private JAXBContext jaxbContext;
    private Marshaller marshaller;
  
    public void ejbCreate() {
        try {
            jaxbContext = JAXBContext.newInstance("edu.harvard.hmdc.vdcnet.jaxb.ddi20");
            marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
            marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://www.icpsr.umich.edu/DDI http://www.icpsr.umich.edu/DDI/Version2-0.xsd");
        } catch (JAXBException ex) {
            Logger.getLogger("global").log(Level.SEVERE, null, ex);
        }
        // initialize lists
        variableFormatTypeList = varService.findAllVariableFormatType();
        variableIntervalTypeList = varService.findAllVariableIntervalType();
        summaryStatisticTypeList = varService.findAllSummaryStatisticType();
        variableRangeTypeList = varService.findAllVariableRangeType();
    }
    
    // Note: in this classes' methods:
    // for ease of determining which variables come from the XML objects,
    // I have decided to prefix those with an "_"; the entity objects have
    // no such prefix; e.g. "_ss" would represent the SumStatType object generated
    // by the unmarshaller, while "ss" represents the entity object
    
    public String determineId(CodeBook _cb, String agency) {
        // some of this code is redundant logic from what happens in
        // mapping, and could probably be consolidated
        
        String returnId = null;
        
        // first check StudyDscr
        List<IDNoType> _idList = _cb.getStdyDscr().get(0).getCitation().get(0).getTitlStmt().getIDNo();
        returnId = agency == null ? getFirstId(_idList) : getIdByAgency(_idList, agency);
        
        if (returnId == null) {
            // now check docDscr
            Iterator iter = _cb.getDocDscr().iterator();
            while ( returnId == null && iter.hasNext()) {
                DocDscrType _dd = (DocDscrType) iter.next();
                if (_dd.getCitation() != null) {
                    _idList = _dd.getCitation().getTitlStmt().getIDNo();
                    returnId = agency == null ? getFirstId(_idList) : getIdByAgency(_idList, agency);
                }
            }
        }
        
        return returnId;
    }
    
    
    private String getFirstId(List<IDNoType> _idList) {
        if ( _idList != null && _idList.size() > 0) {
            IDNoType _id = _idList.get(0);
            String _idContent = mapFirstContent( _id.getContent() );
            if ( _idContent != null && !_idContent.trim().equals("") ) {
                return _idContent;
            }
        }
        return null;
    }
    
    private String getIdByAgency(List<IDNoType> _idList, String agency) {
        for (IDNoType _id : _idList) {
            if (_id.getAgency()!=null && _id.getAgency().equals(agency)) {
                String _idContent = mapFirstContent( _id.getContent() );
                if ( _idContent != null && !_idContent.trim().equals("") ) {
                    return _idContent;
                }
            }
        }

        return null;
    }    
    
    //*************************************************************
    //
    // Methods for mapping from DDI to Study
    //
    //*************************************************************
    
    public Study mapDDI(CodeBook _cb) {
        //   Logger logger = Logger.getLogger()
        // this is currently only used by ingest; so it is OK
        // to pass true for the isAnIngest variable
        return mapDDI(_cb, new Study(), true);
    }
    
    public Study mapDDI(CodeBook _cb, Study study) {
        return mapDDI(_cb, study, false);
    }
    
    
    private Study mapDDI(CodeBook _cb, Study study, boolean isAnIngest) {
        logger.info("begin mapDDI()");
        // map used to link dataVariables to appropriate dataTable
        Map filesMap = new HashMap();
        
        // map used to determine which, if any, fields are needed from the docDscr
        Map docDscrMap = new HashMap();
        docDscrMap.put("holdings", "holdings");
        
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
            } else if ( key.equals("holdings")) {
                if (_dd.getCitation() != null && _dd.getCitation().getHoldings().size() > 0) {
                    s.setHarvestHoldings( _dd.getCitation().getHoldings().get(0).getURI() );
                    docDscrMap.remove("holdings");
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
        
        
        s.setTitle( mapFirstContent( _citation.getTitlStmt().getTitl().getContent() ) );
        if (_citation.getTitlStmt().getSubTitl().size() != 0) {
            s.setSubTitle( mapFirstContent( _citation.getTitlStmt().getSubTitl().get(0).getContent() ) );
        }
        
        if ( _citation.getRspStmt() != null ) {
            Iterator authorIter = _citation.getRspStmt().getAuthEnty().iterator();
            int authorCount = 0;
            while (authorIter.hasNext()) {
                AuthEntyType _author = (AuthEntyType) authorIter.next();
                StudyAuthor author = new StudyAuthor();
                author.setName( mapFirstContent( _author.getContent() ) );
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
                prod.setName( mapCompoundContent( _prod.getContent(), "value" ) );
                prod.setUrl( mapCompoundContent( _prod.getContent(), "uri" ) );
                prod.setLogo( mapCompoundContent( _prod.getContent(), "logo" ) );
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
                s.setProductionPlace( mapFirstContent( _ps.getProdPlac().get(0).getContent() ) );
            }
            
            Iterator softIter = _ps.getSoftware().iterator();
            int softCount = 0;
            while (softIter.hasNext()) {
                SoftwareType _soft = (SoftwareType) softIter.next();
                StudySoftware ss = new StudySoftware();
                ss.setName( mapFirstContent( _soft.getContent() ) );
                ss.setSoftwareVersion( _soft.getVersion() );
                ss.setDisplayOrder( softCount++ );
                ss.setStudy(s);
                s.getStudySoftware().add(ss);
            }
            
            if ( _ps.getFundAg().size() != 0) {
                s.setFundingAgency( mapFirstContent( _ps.getFundAg().get(0).getContent() ) );
            }
            
            Iterator grantIter = _ps.getGrantNo().iterator();
            int grantCount = 0;
            while (grantIter.hasNext()) {
                GrantNoType _grant = (GrantNoType) grantIter.next();
                StudyGrant sg = new StudyGrant();
                sg.setNumber( mapFirstContent( _grant.getContent() ) );
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
                dist.setName( mapCompoundContent( _dist.getContent(), "value" ) );
                dist.setUrl( mapCompoundContent( _dist.getContent(), "uri" ) );
                dist.setLogo( mapCompoundContent( _dist.getContent(), "logo" ) );
                dist.setAbbreviation( _dist.getAbbr() );
                dist.setAffiliation( _dist.getAffiliation() );
                dist.setDisplayOrder( distCount++ );
                dist.setStudy(s);
                s.getStudyDistributors().add(dist);
                
            }
            
            if ( _citation.getDistStmt().getContact().size() != 0) {
                ContactType _contact = _citation.getDistStmt().getContact().get(0);
                s.setDistributorContact( mapFirstContent( _contact.getContent() ) );
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
                s.setDepositor( mapCompoundContent(_depositor.getContent(), "value") );
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
                s.setSeriesName( mapFirstContent( _ss.getSerName().get(0).getContent() )  );
            }
            if (_ss.getSerInfo().size() != 0) {
                s.setSeriesInformation( mapFirstContent( _ss.getSerInfo().get(0).getContent() )  );
            }
        }
        
        if ( _citation.getVerStmt().size() != 0 ) {
            VerStmtType _vs = _citation.getVerStmt().get(0);
            if (_vs.getVersion() != null) {
                s.setStudyVersion( mapFirstContent( _vs.getVersion().getContent() ) );
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
                s.setUNF( mapUNF( mapFirstContent(_note.getContent() ) ) );
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
                studyAbstract.setText( mapContentList(_abstract.getContent()).trim() );
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
                    kw.setValue( mapFirstContent(_kw.getContent()) );
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
                    tc.setValue( mapFirstContent(_tc.getContent()) );
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
                    geoBound.setWestLongitude( mapFirstContent( _sum.getGeoBndBox().getWestBL().getContent() ) );
                    geoBound.setEastLongitude( mapFirstContent( _sum.getGeoBndBox().getEastBL().getContent() ) );
                    geoBound.setSouthLatitude( mapFirstContent( _sum.getGeoBndBox().getSouthBL().getContent() ) );
                    geoBound.setNorthLatitude( mapFirstContent( _sum.getGeoBndBox().getNorthBL().getContent() ) );
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
                    s.setTimeMethod( mapContentList( _dc.getTimeMeth().get(0).getContent() ) );
                }
                if (_dc.getDataCollector().size() != 0) {
                    s.setDataCollector( mapContentList( _dc.getDataCollector().get(0).getContent() ) );
                }
                if (_dc.getFrequenc().size() != 0) {
                    s.setFrequencyOfDataCollection( mapContentList( _dc.getFrequenc().get(0).getContent() ) );
                }
                if (_dc.getSampProc().size() != 0) {
                    s.setSamplingProcedure( mapContentList( _dc.getSampProc().get(0).getContent() ) );
                }
                if (_dc.getDeviat().size() != 0) {
                    s.setDeviationsFromSampleDesign( mapContentList( _dc.getDeviat().get(0).getContent() ) );
                }
                if (_dc.getCollMode().size() != 0) {
                    s.setCollectionMode( mapContentList( _dc.getCollMode().get(0).getContent() ) );
                }
                if (_dc.getResInstru().size() != 0) {
                    s.setResearchInstrument( mapContentList( _dc.getResInstru().get(0).getContent()) );
                }
                if (_dc.getSources() != null) {
                    SourcesType _sources = _dc.getSources();
                    if (_sources.getDataSrc().size() != 0) {
                        s.setDataSources( mapContentList( _sources.getDataSrc().get(0).getContent() ) );
                    }
                    if (_sources.getSrcOrig().size() != 0) {
                        s.setOriginOfSources( mapContentList( _sources.getSrcOrig().get(0).getContent() ) );
                    }
                    if (_sources.getSrcChar().size() != 0) {
                        s.setCharacteristicOfSources( mapContentList( _sources.getSrcChar().get(0).getContent()) );
                    }
                    if (_sources.getSrcDocu().size() != 0) {
                        s.setAccessToSources( mapContentList( _sources.getSrcDocu().get(0).getContent() ) );
                    }
                }
                if (_dc.getCollSitu().size() != 0) {
                    s.setDataCollectionSituation( mapContentList( _dc.getCollSitu().get(0).getContent() ) );
                }
                if (_dc.getActMin().size() != 0) {
                    s.setActionsToMinimizeLoss( mapContentList( _dc.getActMin().get(0).getContent() ) );
                }
                if (_dc.getConOps().size() != 0) {
                    s.setControlOperations( mapContentList( _dc.getConOps().get(0).getContent() ) );
                }
                if (_dc.getWeight().size() != 0) {
                    s.setWeighting( mapContentList( _dc.getWeight().get(0).getContent() ) );
                }
                if (_dc.getCleanOps().size() != 0) {
                    s.setCleaningOperations( mapContentList( _dc.getCleanOps().get(0).getContent() ) );
                }
            }
            
            Iterator methodNotesIter = _method.getNotes().iterator();
            String sleNote = "";
            while (methodNotesIter.hasNext()) {
                NotesType _note = (NotesType) methodNotesIter.next();
                sleNote +=  mapFirstContent(_note.getContent() );
                sleNote += methodNotesIter.hasNext() ? "; " : "";
            }
            
            s.setStudyLevelErrorNotes( sleNote );
            
            
            if (_method.getAnlyInfo() != null) {
                AnlyInfoType _ai = _method.getAnlyInfo();
                if (_ai.getRespRate().size() != 0) {
                    s.setResponseRate( mapContentList( _ai.getRespRate().get(0).getContent() ) );
                }
                if (_ai.getEstSmpErr().size() != 0) {
                    s.setSamplingErrorEstimate( mapContentList( _ai.getEstSmpErr().get(0).getContent() ) );
                }
                if (_ai.getDataAppr().size() != 0) {
                    s.setOtherDataAppraisal( mapContentList( _ai.getDataAppr().get(0).getContent() ) );
                }
            }
        }
        
        
        //terms of use
        if (_sd.getDataAccs().size() != 0) {
            DataAccsType _da = _sd.getDataAccs().get(0);
            
            if (_da.getSetAvail().size() != 0) {
                SetAvailType _sa = _da.getSetAvail().get(0);
                if ( _sa.getAccsPlac().size() != 0 ) {
                    s.setPlaceOfAccess( mapContentList( _sa.getAccsPlac().get(0).getContent() ) );
                }
                if (_sa.getOrigArch() != null) {
                    s.setOriginalArchive( mapContentList(_sa.getOrigArch().getContent() ) );
                }
                if ( _sa.getAvlStatus().size() != 0 ) {
                    s.setAvailabilityStatus( mapContentList( _sa.getAvlStatus().get(0).getContent() ) );
                }
                if (_sa.getCollSize() != null) {
                    s.setCollectionSize( mapContentList(_sa.getCollSize().getContent() ) );
                }
                if (_sa.getComplete() != null) {
                    s.setStudyCompletion( mapContentList(_sa.getComplete().getContent() ) );
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
                    s.setConfidentialityDeclaration( mapContentList(_us.getConfDec().getContent() ) );
                }
                if (_us.getSpecPerm() != null) {
                    s.setSpecialPermissions( mapContentList(_us.getSpecPerm().getContent() ) );
                }
                if (_us.getRestrctn() != null) {
                    s.setRestrictions( mapContentList(_us.getRestrctn().getContent() ) );
                }
                if (_us.getContact().size() != 0) {
                    s.setContact( mapContentList(_us.getContact().get(0).getContent() ) );
                }
                if (_us.getCitReq() != null) {
                    s.setCitationRequirements( mapContentList(_us.getCitReq().getContent() ) );
                }
                if (_us.getDeposReq() != null) {
                    s.setDepositorRequirements( mapContentList(_us.getDeposReq().getContent() ) );
                }
                if (_us.getConditions() != null) {
                    s.setConditions( mapContentList(_us.getConditions().getContent() ) );
                }
                if (_us.getDisclaimer() != null) {
                    s.setDisclaimer( mapContentList(_us.getDisclaimer().getContent()) );
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
                    s.setReplicationFor( mapContentList( _rm.getContent() ) );
                    replicationForFound = true;
                } else {
                    StudyRelMaterial rm = new StudyRelMaterial();
                    rm.setText( mapContentList( _rm.getContent() ) );
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
                rs.setText( mapContentList( _rs.getContent() ) );
                rs.setDisplayOrder(rsCount++);
                rs.setStudy(s);
                s.getStudyRelStudies().add(rs);
            }
            
            Iterator rpIter = _osm.getRelPubl().iterator();
            int rpCount = 0;
            while (rpIter.hasNext()) {
                RelPublType _rp = (RelPublType) rpIter.next();
                StudyRelPublication rp = new StudyRelPublication();
                rp.setText( mapContentList( _rp.getContent() ) );
                rp.setDisplayOrder(rpCount++);
                rp.setStudy(s);
                s.getStudyRelPublications().add(rp);
            }
            
            Iterator orIter = _osm.getOthRefs().iterator();
            int orCount = 0;
            while (orIter.hasNext()) {
                OthRefsType _or = (OthRefsType) orIter.next();
                StudyOtherRef or = new StudyOtherRef();
                or.setText( mapContentList( _or.getContent() ) );
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
        String _noteText = mapFirstContent( _note.getContent() );
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
        String _idContent = mapFirstContent( _idNo.getContent() );

        if (_idContent != null && !_idContent.trim().equals("") ) {
        
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
        
        if (_om.getLabl().size() > 0) {
            file.setFileName( mapFirstContent( _om.getLabl().get(0).getContent() ) );
        }
        // if fileName has not been set or it is empty; set as default
        if (file.getFileName() == null || file.getFileName().trim().equals("") ) {
            file.setFileName("file");
        }
        
        if (_om.getTxt() != null) {
            file.setDescription( mapFirstContent( _om.getTxt().getContent() ) );
        }
        
        file.setFileSystemLocation( _om.getURI() );
        
        addFileToCategory( file, determineFileCategory(_om.getNotes()), s );
    }
    
    
    private void mapFileDscr(FileDscrType _fd, Study s, Map filesMap) {
        StudyFile file = new StudyFile();
        
        file.setFileSystemLocation( _fd.getURI() );
        file.setFileName("file"); // default filename; if specified by DDI, will be replaced
        
        FileTxtType _fileTxt = null;
        
        if ( _fd.getFileTxt().size() > 0 ) {
            _fileTxt = _fd.getFileTxt().get(0);
            if (  _fileTxt.getFileName() != null ) {
                file.setFileName( mapFirstContent( _fd.getFileTxt().get(0).getFileName().getContent() ) );
                // check to make sure content wasn't empty
                if (file.getFileName() == null || file.getFileName().trim().equals("") ) {
                    file.setFileName("file");
                }
            }
            if (  _fileTxt.getFileCont() != null ) {
                file.setDescription( mapFirstContent( _fd.getFileTxt().get(0).getFileCont().getContent() ) );
            }
        }
        
       // now check if we have any variables associated (to see if we are subsettable)
        DataTable dt = (DataTable) filesMap.get( _fd.getID() );
        if (dt != null) {
            file.setSubsettable(true);
            
            dt.setStudyFile(file);
            file.setDataTable(dt);
            
            if (_fileTxt != null && _fileTxt.getDimensns() != null) {
                DimensnsType _dim = _fileTxt.getDimensns();
                if ( _dim.getCaseQnty().size() > 0) {
                    dt.setCaseQuantity( new Long( mapFirstContent( _dim.getCaseQnty().get(0).getContent() ) ) );
                }
                if ( _dim.getVarQnty().size() > 0) {
                    dt.setVarQuantity( new Long( mapFirstContent( _dim.getVarQnty().get(0).getContent() ) ) );
                }
                if ( _dim.getRecPrCas().size() > 0) {
                    dt.setRecordsPerCase( new Long( mapFirstContent( _dim.getRecPrCas().get(0).getContent() ) ) );
                }
            }
            
            String unf = mapFileNote(_fd.getNotes(), "VDC:UNF");
            if (unf != null) {
                dt.setUnf( mapUNF( unf ) );    
            }
        }
          
        addFileToCategory( file, determineFileCategory(_fd.getNotes()) , s );
        
        // for now, don't do anything about content type
    }
    
    private String determineFileCategory(List<NotesType> notes) {
        // first check for vdc:category
        String catName = mapFileNote(notes, "vdc:category");
        
        if (catName == null) {
            // check icpsr:category
            catName = mapFileNote( notes, "icpsr:category", "description");
            
            if (catName != null) {
                String id = mapFileNote( notes, "icpsr:category", "id");
                if (id != null && !id.trim().equals("") ) {
                    catName = id + ". " + catName;
                }
            }
        }  
        
        return (catName != null ? catName : "");
    }
    
    private String  mapFileNote(List<NotesType> notes, String type ) {
        return mapFileNote( notes, type, null );
    }    
    
    private String  mapFileNote(List<NotesType> notes, String type, String subject) {
        for (NotesType _note : notes) {
            if (_note.getType() != null &&_note.getType().equals(type) ) {
                if (subject == null || (_note.getSubject() != null &&_note.getSubject().equals(subject) ) ) {
                    return mapFirstContent( _note.getContent() );
                }
            }
        }
        
        return null;
    }
    
    private void mapDataDscr(DataDscrType _dd, Map filesMap) {
        
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
                        dv.setLabel( mapFirstContent( _labl.getContent() ) );
                    }
                }
                
                // summaryStats
                dv.setSummaryStatistics(new ArrayList());
                Iterator sumStatIter = _dv.getSumStat().iterator();
                while (sumStatIter.hasNext()) {
                    SumStatType _ss = (SumStatType) sumStatIter.next();
                    SummaryStatistic ss = new SummaryStatistic();
                    ss.setType( varService.findSummaryStatisticTypeByName( summaryStatisticTypeList, _ss.getType() ) );
                    ss.setValue( mapFirstContent( _ss.getContent() ) );
                    ss.setDataVariable(dv);
                    
                    dv.getSummaryStatistics().add(ss);
                }
                
                // notes (UNF)
                Iterator noteIter = _dv.getNotes().iterator();
                while (noteIter.hasNext()) {
                    NotesType _note = (NotesType) noteIter.next();
                    if ( "VDC:UNF".equals(_note.getType()) ) {
                        dv.setUnf( mapUNF( mapFirstContent( _note.getContent() ) ) );
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
                    dv.setConcept( mapFirstContent( _dv.getConcept().get(0).getContent() ) );
                }
                
                // universe
                if (_dv.getUniverse().size() != 0) {
                    dv.setUniverse( mapFirstContent( _dv.getUniverse().get(0).getContent() ) );
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
                cat.setLabel( mapFirstContent( _labl.getContent() ) );
            }
        }
        
        // catStat
        Iterator catStatIter = _cat.getCatStat().iterator();
        while (catStatIter.hasNext()) {
            CatStatType _catStat =  (CatStatType) catStatIter.next();
            if ( "freq".equals(_catStat.getType()) ) {
                String _freq = mapFirstContent( _catStat.getContent() );
                if (_freq != null && !_freq.equals("") ) {
                    cat.setFrequency( new Long( _freq ) );
                }
            }
        }
        
        cat.setValue( mapFirstContent( _cat.getCatValu().getContent() ) );
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
                returnVal += mapFirstContent( ((NationType) _elem).getContent() );
            } else if (_elem instanceof GeogCoverType) {
                returnVal += mapFirstContent( ((GeogCoverType) _elem).getContent() );
            } else if (_elem instanceof GeogUnitType) {
                returnVal += mapFirstContent( ((GeogUnitType) _elem).getContent() );
            } else if (_elem instanceof AnlyUnitType) {
                returnVal += mapFirstContent( ((AnlyUnitType) _elem).getContent() );
            } else if (_elem instanceof UniverseType) {
                returnVal += mapFirstContent( ((UniverseType) _elem).getContent() );
            } else if (_elem instanceof DataKindType) {
                returnVal += mapFirstContent( ((DataKindType) _elem).getContent() );
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
            return mapFirstContent( _dateContent ) ;
        }
    }
    
    private String mapFirstContent(List contentList) {
        if (contentList.size() == 0 ) {
           return ""; 
        } else {
            return mapContentItem(contentList.get(0));
        }
    }    
    
    private String mapContentList(List contentList) {
        Iterator iter = contentList.iterator();
        String content = "";
        while (iter.hasNext()) {
            Object item = (Object) iter.next();
            content += mapContentItem(item);
            content += iter.hasNext() ? "\n" : "";
        }

        
        return content;
    }
    
    private String mapContentItem(Object _content) {
        if (_content instanceof String) {
            return ((String) _content).trim().replace('\n',' ');
        } else if (_content instanceof JAXBElement) {
            Object _html = ((JAXBElement)_content).getValue();
            if ( _html instanceof PType) {
                return "<p>" + mapFirstContent( ((PType)_html).getContent() ) + "</p>";
            } else if ( _html instanceof ExtLinkType) {
                ExtLinkType _link = (ExtLinkType)_html;
                String _linkName = StringUtil.isEmpty( _link.getContent() ) ? _link.getURI() : _link.getContent().trim();
                return "<a href=\"" + _link.getURI() +"\" >" + _linkName + "</a>";                
            } else if ( _html instanceof ListType) {
                ListType _list = (ListType)_html;
                String listString = null;
                String listCloseTag = null;
                
                // check type
                if ("bulleted".equals(_list.getType()) ){
                    listString = "<ul>\n";
                    listCloseTag = "</ul>";
                } else if ("ordered".equals(_list.getType()) ) {
                    listString = "<ol>\n";
                    listCloseTag = "</ol>";
                } else {
                    throw new EJBException("mapContent: ListType of types other than {bulleted, ordered} not currently supported.");    
                }
                
                // add items
                for (Object _item : ((ListType)_html).getLabelOrItm()) {
                    if ( _item instanceof ItmType) {
                        listString += "<li>" + mapFirstContent( ((ItmType) _item).getContent() ) + "</li>\n";
                    } else {
                        throw new EJBException("mapContent: ListType does not currently supported contained LabelType.");
                    }
                }
                return (listString + listCloseTag);
                
            } else if ( _html instanceof CitationType) {
                CitationType _citation = (CitationType)_html;
                String citation = "<!--  parsed from DDI citation title and holdings -->";
                citation += mapFirstContent( _citation.getTitlStmt().getTitl().getContent() );
                
                boolean addHoldings = false;
                String holdings = "";
                for (HoldingsType _holdings : _citation.getHoldings() ) {
                    holdings += addHoldings ? ", " : "";
                    if (_holdings.getURI() != null && !_holdings.getURI().trim().equals("") ) {
                        holdings += "<a href=\"" + _holdings.getURI() + "\">";
                        holdings += mapFirstContent(_holdings.getContent());
                        holdings += "</a>";
                    } else {
                        holdings += mapFirstContent(_holdings.getContent());
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
    
    private String mapCompoundContent(List _content, String searchingFor) {
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
                        break;
                    } else if ( searchingFor.equals("logo") && "image".equals(_link.getRole() ) ) {
                        returnVal = _link.getURI();
                        break;
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
        _cb.getDocDscr().add(createDocDscr(s));
        
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
    
    // this method is public, so we can add a DocDscr to harvested ddis
    public DocDscrType createDocDscr(Study s) {
        ObjectFactory objFactory = new ObjectFactory();
        DocDscrType _doc = objFactory.createDocDscrType();
        CitationType _citation = objFactory.createCitationType();
        
        // titlStmt
        TitlStmtType _titleStmt = objFactory.createTitlStmtType();
        _citation.setTitlStmt(_titleStmt);
        
        TitlType _title = objFactory.createTitlType();
        _title.getContent().add(s.getTitle());
        _titleStmt.setTitl(_title);
        
        mapStudyId(s, _titleStmt);
        
        // distStmt
        DistStmtType _distStmt = objFactory.createDistStmtType();
        _citation.setDistStmt(_distStmt);
        
        DistrbtrType _dist = objFactory.createDistrbtrType();
        String name = vdcNetworkService.find().getName();
        _dist.getContent().add( name+" Dataverse Network" );
        _distStmt.getDistrbtr().add(_dist);
        
        if ( s.getLastUpdateTime() != null ) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String lastUpdateString = sdf.format(s.getLastUpdateTime());
            
            DistDateType _distDate = objFactory.createDistDateType();
            _distDate.setDate( lastUpdateString );
            _distDate.getContent().add(lastUpdateString );
            _distStmt.setDistDate( _distDate );
        }
        
        // holdings
        HoldingsType _holdings = objFactory.createHoldingsType();
        _holdings.setURI( "http://" + PropertyUtil.getHostUrl() + "/dvn/study?globalId=" + s.getGlobalId() );
        _citation.getHoldings().add(_holdings);
               
        _doc.setCitation( _citation );
        return _doc; 
    }
     
  
    
    private void mapStudyId(Study s, TitlStmtType _titleStmt) {
        ObjectFactory objFactory = new ObjectFactory();
        IDNoType _studyId = objFactory.createIDNoType();
        _studyId.setAgency( AGENCY_HANDLE );
        _studyId.getContent().add( s.getGlobalId() );
        _titleStmt.getIDNo().add(_studyId);
               
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
        mapStudyId(s, _titleStmt);
        
        
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
        if (!StringUtil.isEmpty(s.getOwner().getDownloadTermsOfUse()) && s.getOwner().isDownloadTermsOfUseEnabled()){
              NotesType _notes = objFactory.createNotesType();
              _notes.setType(NOTE_TYPE_TERMS_OF_USE);
              _notes.setSubject(NOTE_SUBJECT_TERMS_OF_USE);
              _notes.getContent().add( s.getOwner().getDownloadTermsOfUse() );
              _da.getNotes().add(_notes);
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
        
        boolean addDimensions = false;
        DimensnsType _dim = objFactory.createDimensnsType();
        if (dt.getCaseQuantity() != null) {
            CaseQntyType _cq = objFactory.createCaseQntyType();
            _cq.getContent().add(dt.getCaseQuantity().toString());
            _dim.getCaseQnty().add(_cq);
            addDimensions = true;
        }
        if (dt.getVarQuantity() != null) {
            VarQntyType _vq = objFactory.createVarQntyType();
            _vq.getContent().add(dt.getVarQuantity().toString());
            _dim.getVarQnty().add(_vq);
            addDimensions = true;
        }
        if (dt.getRecordsPerCase() != null) {
            RecPrCasType _rpc = objFactory.createRecPrCasType();
            _rpc.getContent().add(dt.getRecordsPerCase().toString());
            _dim.getRecPrCas().add(_rpc);
            addDimensions = true;
        }        
        
        FileTypeType _fileType = objFactory.createFileTypeType();
        _fileType.getContent().add(sf.getFileType());
        
        FileTxtType _fileText = objFactory.createFileTxtType();
        _fileText.setFileName(_fileName);
        _fileText.setFileCont(_fileCont);
        if (addDimensions) { _fileText.setDimensns(_dim); }
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
        if (sf.isRemote() ) {
            return sf.getFileSystemLocation();
        } else {
            fileLocation = "http://" + PropertyUtil.getHostUrl() + "/dvn/dv/" + s.getOwner().getAlias() + "/FileDownload/";
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
    
    public boolean isXmlFormat() {
        return true;
    }
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public  void exportStudy(Study study,OutputStream os) throws JAXBException {
        exportStudy(study, new OutputStreamWriter(os), false, false);
    }
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public  void exportStudy(Study study, Writer out, boolean generateCodeBookForHarvestedStudy, boolean exportToLegacyVDC) throws  JAXBException {
        if ( study.isIsHarvested() && !generateCodeBookForHarvestedStudy) {
            exportOriginalDDIPlus(study, out);
        } else {
            exportCodeBook(mapStudy(study, exportToLegacyVDC), out ); 
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void exportCodeBook(CodeBook _cb, Writer out) throws JAXBException {
        marshaller.marshal(_cb,out);
    }
    
    public  void exportDataFile(StudyFile sf, Writer out) throws IOException, JAXBException {
         
        marshaller.marshal(mapDataFile(sf), out ); 

    }    
    
    private CodeBook mapDataFile(StudyFile sf) {
        ObjectFactory objFactory = new ObjectFactory();
        CodeBook _cb = objFactory.createCodeBook();
        _cb.setVersion("2.0"); 

        FileDscrType _fd = mapSubsettableFile( sf, false );
        _cb.getFileDscr().add(_fd);
     
        if ( sf.getDataTable().getDataVariables().size() > 0 ) {
            DataDscrType _dd = objFactory.createDataDscrType();   
            Iterator varIter = varService.getDataVariablesByFileOrder( sf.getDataTable().getId() ).iterator();
            while (varIter.hasNext()) {
                DataVariable dv = (DataVariable) varIter.next();
                _dd.getVar().add( mapDataVariable(dv, _fd) );
            }
            _cb.getDataDscr().add(_dd);     
        }
        return _cb;
        
    }   
    
    private void exportOriginalDDIPlus (Study s, Writer out) throws JAXBException {
        File studyDir = new File(FileUtil.getStudyFileDir(), s.getAuthority() + File.separator + s.getStudyId());
        File originalImport = new File(studyDir, "original_imported_study.xml");
        BufferedReader in = null;      
        
        if (originalImport.exists()) {
            try {
                in = new BufferedReader( new FileReader(originalImport) );
                String line = null; //not declared within while loop
                /*
                * readLine is a bit quirky :
                * it returns the content of a line MINUS the newline.
                * it returns null only for the END of the stream.
                * it returns an empty String if two newlines appear in a row.
                */
                while (( line = in.readLine()) != null){
                    // check to see if this is the StudyDscr in order to add the extra docDscr
                    if (line.indexOf("<stdyDscr>") != -1) {
                        out.write(createDocDscrSnippet(s));
                        out.write(System.getProperty("line.separator"));
                        out.flush();                        
                    }
                    
                    out.write(line);
                    out.write(System.getProperty("line.separator"));
                    out.flush();
                }     
                               
            } catch (IOException ex) {
                throw new EJBException ("A problem occurred trying to export this study (original DDI Plus).");                
            } finally {
                try {
                    if (out!=null) { out.close(); }
                    if (in!=null) { in.close(); }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } 
        } else {
            throw new EJBException ("There is no original import DDI for this study.");
        }     
    }
    
    private String createDocDscrSnippet(Study s) throws JAXBException {
        // create dummy Codebook for purpose of generate extra DocDesc
        ObjectFactory objFactory = new ObjectFactory();
        CodeBook _cb = objFactory.createCodeBook();
        _cb.setVersion("2.0");
        _cb.getDocDscr().add(createDocDscr(s));
        
        StringWriter sw = new StringWriter();
        marshaller.marshal( _cb , sw );

        // commented out attempt to marshall just DocDscrType; still needs more investigation
        //marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
        //marshaller.marshal( new JAXBElement( new QName("","rootTag"),DocDscrType.class, createDocDscr(s) ) ,out );
        
        
        // now remove the codebook and return
        String returnString = sw.toString();
        returnString = returnString.substring(returnString.indexOf("<docDscr>"));
        returnString = returnString.substring(0, returnString.lastIndexOf("</docDscr>") + 10);
        return returnString;
    }
}
