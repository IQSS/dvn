/*
 * CatalogServiceBean.java
 *
 * Created on August 1, 2007, 3:42 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.catalog;

import edu.harvard.hmdc.vdcnet.ddi.DDI20ServiceLocal;
import edu.harvard.hmdc.vdcnet.index.IndexServiceLocal;
import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.study.StudyExporter;
import edu.harvard.hmdc.vdcnet.study.StudyExporterFactoryLocal;
import edu.harvard.hmdc.vdcnet.util.FileUtil;
import edu.harvard.hmdc.vdcnet.vdc.OAISet;
import edu.harvard.hmdc.vdcnet.vdc.OAISetServiceLocal;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.xml.bind.JAXBException;

/**
 *
 * @author roberttreacy
 */
@Stateless
public class CatalogServiceBean implements CatalogServiceLocal {
    @PersistenceContext(unitName="VDCNet-ejbPU") EntityManager em;
    @EJB DDI20ServiceLocal ddiService;
    @EJB StudyExporterFactoryLocal studyExporterFactory;
    @EJB OAISetServiceLocal oaiSetService;
    @EJB IndexServiceLocal indexService;
    private static final Logger logger = Logger.getLogger("edu.harvard.hmdc.vdcnet.catalog.CatalogServiceBean");
    
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
                beginTime =new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());  // Use yesterday as default value
                cal.add(Calendar.DAY_OF_YEAR,1);
                endTime =new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
            }    else {
                beginTime=from;
                Date date=new SimpleDateFormat("yyyy-MM-dd").parse(from);
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                beginTime = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
                if (until == null){
                    cal.add(Calendar.DAY_OF_YEAR,1);
                } else {
                    Date endDate = new SimpleDateFormat("yyyy-MM-dd").parse(until);
                    cal.setTime(endDate);
                }
                endTime =new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
            }
            String query = "SELECT s from Study s where " ;
            query+="s.lastUpdateTime >'" +beginTime+"'";
            query+=" and s.lastUpdateTime <'" +endTime+"'";
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
        
    public String []  listRecords(String from, String until, String set, String metadataPrefix) {
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
                beginTime =new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());  // Use yesterday as default value
                cal.add(Calendar.DAY_OF_YEAR,1);
                endTime =new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
            }    else {
                beginTime=from;
                Date date=new SimpleDateFormat("yyyy-MM-dd").parse(from);
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                beginTime = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
                if (until == null){
                    cal.add(Calendar.DAY_OF_YEAR,1);
                } else {
                    Date endDate = new SimpleDateFormat("yyyy-MM-dd").parse(until);
                    cal.setTime(endDate);
                }
                endTime =new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
            }
            String query = "SELECT s from Study s where " ;
            query+="s.lastUpdateTime >'" +beginTime+"'";
            query+=" and s.lastUpdateTime <'" +endTime+"'";
            query+=" order by s.studyId";
            List updatedStudies = em.createQuery(query).getResultList();
            
            
            for (Iterator it = updatedStudies.iterator(); it.hasNext();) {
                Study study = (Study) it.next();
                String identifier = "<identifier>" + study.getGlobalId() + "</identifier>";
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String dateStamp = "<datestamp>"+sdf.format(study.getLastExportTime())+"</datestamp>";
                String setSpec = "<setSpec>"+study.getAuthority()+"</setSpec>";
                logger.info("Exporting study "+study.getStudyId());
                if (oaiSet == null || indexedIds.contains(study.getId())){
                    String record = getRecord(study, metadataPrefix);
                    if (record.length()>0){
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
    
    public String getRecord(Study study, String metadataPrefix) {
        String identifier = "<identifier>" + study.getGlobalId() + "</identifier>";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStamp = "<datestamp>"+sdf.format(study.getLastExportTime())+"</datestamp>";
        String setSpec = "<setSpec>"+study.getAuthority()+"</setSpec>";
        Date lastUpdateTime = study.getLastUpdateTime();
        File studyFileDir = FileUtil.getStudyFileDir(study);
        String exportFileName= studyFileDir.getAbsolutePath() + File.separator + "export_" + metadataPrefix+".xml";
        File exportFile = new File(exportFileName);
        String record = exportFile.exists()?identifier+dateStamp+setSpec+readFile(exportFile): "";
        System.out.println("RECORD:\n"+record);
        return record;
    }

}
