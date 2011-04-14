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
 * CatalogServiceBean.java
 *
 * Created on August 1, 2007, 3:42 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.catalog;

import ORG.oclc.oai.server.verb.NoItemsMatchException;
import edu.harvard.iq.dvn.core.harvest.HarvestStudy;
import edu.harvard.iq.dvn.core.harvest.HarvestStudyServiceLocal;
import edu.harvard.iq.dvn.core.index.IndexServiceLocal;
import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyExporterFactoryLocal;
import edu.harvard.iq.dvn.core.util.FileUtil;
import edu.harvard.iq.dvn.core.vdc.OAISetServiceLocal;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author roberttreacy
 */
@Stateless
public class CatalogServiceBean implements CatalogServiceLocal {
    @PersistenceContext(unitName="VDCNet-ejbPU") EntityManager em;
    @EJB StudyExporterFactoryLocal studyExporterFactory;
    @EJB OAISetServiceLocal oaiSetService;
    @EJB IndexServiceLocal indexService;
    @EJB HarvestStudyServiceLocal harvestStudyService;
    
    private static final Logger logger = Logger.getLogger("edu.harvard.iq.dvn.core.catalog.CatalogServiceBean");
    
    /** Creates a new instance of CatalogServiceBean */
    public CatalogServiceBean() {
    }
    
    public static void copy(InputStream in, OutputStream out)
    throws IOException {
        byte[] buffer = new byte[8192];
        while (true) {
            int bytesRead = in.read(buffer);
            if (bytesRead == -1) break;
            out.write(buffer, 0, bytesRead);
            out.flush();
        }
    }
    public String readFile(File inputFile) {
        FileInputStream instream = null;
        FileChannel in = null;
        ByteArrayOutputStream out = null;
        String outString = null;
        InputStream instream2 = null;
        
        try {
            try {
                instream = new FileInputStream(inputFile);
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
            in = instream.getChannel();
            instream2 = Channels.newInputStream(in);
            out = new ByteArrayOutputStream();
            try {
                copy(instream2, out);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            outString = out.toString();
        } finally {
            if (instream != null) {
                try {instream.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                } }
            if (in != null) {
                try {in.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                } }
            if (out != null) {
                try {out.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                } }
        }
        return outString;
    }
    
    public Study [] listStudies(String from, String until, String set, String metadataPrefix){
        List <Study> studies = new ArrayList();
        try{
            String beginTime=null;
            String endTime=null;
            if (from==null) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_YEAR, -1);
                beginTime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime());  // Use yesterday as default value
                cal.add(Calendar.DAY_OF_YEAR,1);
                endTime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime());
            }    else {
                beginTime=from;
                Date date=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(from);
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                beginTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime());
                if (until == null){
                    cal.add(Calendar.DAY_OF_YEAR,1);
                } else {
                    Date endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(until);
                    cal.setTime(endDate);
                }
                endTime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime());
            }
            String query = "SELECT s from Study s where " ;
            query+="s.lastUpdateTime >='" +beginTime+"'";
            query+=" and s.lastUpdateTime <='" +endTime+"'";
            query+=" order by s.studyId";
            List updatedStudies = em.createQuery(query).getResultList();
            
            
            for (Iterator it = updatedStudies.iterator(); it.hasNext();) {
                Study study = (Study) it.next();
                logger.info("Exporting study "+study.getStudyId());
                studies.add(study);
                
            }
        } catch(Exception e) {
            logger.severe(e.getMessage());
            
            String stackTrace = "StackTrace: \n";
            logger.severe("Exception caused by: "+e+"\n");
            StackTraceElement[] ste = e.getStackTrace();
            for(int m=0;m<ste.length;m++) {
                stackTrace+=ste[m].toString()+"\n";
            }
            logger.severe(stackTrace);
        }
        Study [] s = new Study[studies.size()];
        return studies.toArray(s);
    }

     public String []  listRecords(String from, String until, String set, String metadataPrefix) throws NoItemsMatchException{
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        List <String> records = new ArrayList();
        
        try {
            List<HarvestStudy> harvestStudies = harvestStudyService.findHarvestStudiesBySetName(set, sdf.parse(from), sdf.parse(until) );

            for (HarvestStudy hs : harvestStudies) {
                String record = "<identifier>" + ( set != null ? set + "//" : "") +  hs.getGlobalId() + "</identifier>";
                record += "<datestamp>" + sdf.format(hs.getLastUpdateTime()) + "</datestamp>";            

                if (hs.isRemoved() ) {
                    record += "<status>deleted</status>";
                    records.add(record);
                    
                } else {
                    record += set != null ? "<setSpec>"+set+"</setSpec>" : "";

                    int index1 = hs.getGlobalId().indexOf(':');
                    int index2 = hs.getGlobalId().indexOf('/');
                    String authority = hs.getGlobalId().substring(index1 + 1, index2);
                    String studyId = hs.getGlobalId().substring(index2 + 1).toUpperCase();

                    File studyFileDir = FileUtil.getStudyFileDir(authority, studyId);
                    String exportFileName= studyFileDir.getAbsolutePath() + File.separator + "export_" + metadataPrefix+".xml";
                    File exportFile = new File(exportFileName);

                    if ( exportFile.exists() ) {
                        record += readFile(exportFile);
                        records.add(record);
                    }

                }
              }
         } catch(Exception e) {
            logger.severe(e.getMessage());
            
            String stackTrace = "StackTrace: \n";
            logger.severe("Exception caused by: "+e+"\n");
            StackTraceElement[] ste = e.getStackTrace();
            for(int m=0;m<ste.length;m++) {
                stackTrace+=ste[m].toString()+"\n";
            }
            logger.severe(stackTrace);
        }
        String [] s = new String[records.size()];
        return records.toArray(s);        
     }
     
    /*
    public String []  listRecords(String from, String until, String set, String metadataPrefix) throws NoItemsMatchException{
        OAISet oaiSet = null;
        List <Long> indexedIds = null;
        if (set != null){
            oaiSet = oaiSetService.findBySpec(set);
        } 
        if (oaiSet != null){
            String definition = oaiSet.getDefinition();
            indexedIds = indexService.query(definition);
        }
        List <String> records = new ArrayList();
        try{
            String beginTime=null;
            String endTime=null;
            if (from==null) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_YEAR, -1);
                beginTime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime());  // Use yesterday as default value
                cal.add(Calendar.DAY_OF_YEAR,1);
                endTime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime());
            }    else {
                beginTime=from;
                Date date=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(from);
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                beginTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss':000'").format(cal.getTime());
                if (until == null){
                    cal.add(Calendar.DAY_OF_YEAR,1);
                } else {
                    Date endDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(until);
                    cal.setTime(endDate);
                }
//                untilCal = cal;
                endTime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss':999'").format(cal.getTime());
            }
            String query = "SELECT s from Study s where " ;
            query+="s.lastExportTime >='" +beginTime+"'";
            query+=" and s.lastExportTime <='" +endTime+"' and s.reviewState.name = 'Released' ";
            query+=" and s.owner.restricted = false and s.restricted = false ";
            query+=" order by s.studyId";
            Query q = em.createQuery(query);
            List updatedStudies = q.getResultList();
            
            
            for (Iterator it = updatedStudies.iterator(); it.hasNext();) {
                Study study = (Study) it.next();
                String identifier = "<identifier>" + study.getGlobalId() + "</identifier>";
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                if (study.getLastExportTime() != null) {
                    logger.info("Exporting study " + study.getStudyId());
                    if ((oaiSet == null && !study.isIsHarvested()) || (indexedIds != null && indexedIds.contains(study.getId()))) {
                        String record = getRecord(study, metadataPrefix, set);
                        if (record.length() > 0) {
                            records.add(record);
                        }
                    }
                }
            }
            String deleteQuery = "SELECT d from DeletedStudy d where ";
            deleteQuery+="d.deletedTime >='" + beginTime+"'";
            deleteQuery+=" and d.deletedTime <='" + endTime+"'";
            Query dq = em.createQuery(deleteQuery);
            List deletedStudies = dq.getResultList();
            String deleteStatus = "<status>deleted</status>";
            for (Iterator it = deletedStudies.iterator(); it.hasNext();) {
                String record = null;
                DeletedStudy deletedStudy = (DeletedStudy) it.next();
                String identifier = "<identifier>" + deletedStudy.getGlobalId() + "</identifier>";
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                if (deletedStudy.getDeletedTime() != null) {
                    String dateStamp = "<datestamp>" + sdf.format(deletedStudy.getDeletedTime()) + "</datestamp>";
                    record = identifier+dateStamp+deleteStatus;
                    records.add(record);
                } else {
                    logger.severe("Deleted time is a mandatory field for deleted study " + deletedStudy.getGlobalId());
                }
            }
        } catch(Exception e) {
            logger.severe(e.getMessage());
            
            String stackTrace = "StackTrace: \n";
            logger.severe("Exception caused by: "+e+"\n");
            StackTraceElement[] ste = e.getStackTrace();
            for(int m=0;m<ste.length;m++) {
                stackTrace+=ste[m].toString()+"\n";
            }
            logger.severe(stackTrace);
        }
        String [] s = new String[records.size()];
        return records.toArray(s);
   }
   */
    
    public String getRecord(Study study, String metadataPrefix) {
        return getRecord(study, metadataPrefix, null);
    }

    private String getRecord(Study study, String metadataPrefix, String set) {

        DateFormat gmtFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        TimeZone gmtTime = TimeZone.getTimeZone("GMT");
        gmtFormat.setTimeZone(gmtTime);

        String identifier = "<identifier>" + study.getGlobalId() + "</identifier>";
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); --getting UTC now.

        String dateStamp = "<datestamp>"+gmtFormat.format(study.getLastExportTime())+"</datestamp>";
        String setSpec = set != null ? "<setSpec>"+set+"</setSpec>" : "";
//        Date lastUpdateTime = study.getLastUpdateTime();
        File studyFileDir = FileUtil.getStudyFileDir(study);
        String exportFileName= studyFileDir.getAbsolutePath() + File.separator + "export_" + metadataPrefix+".xml";
        File exportFile = new File(exportFileName);
        String record = exportFile.exists()?identifier+dateStamp+setSpec+readFile(exportFile): "";
        return record;
    }

}
