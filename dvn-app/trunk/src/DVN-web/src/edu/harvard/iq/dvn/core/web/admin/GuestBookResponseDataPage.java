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
 Version 3.1.
 */
package edu.harvard.iq.dvn.core.web.admin;

import com.icesoft.faces.context.Resource;
import edu.harvard.iq.dvn.core.study.DataVariable;
import edu.harvard.iq.dvn.core.vdc.*;
import edu.harvard.iq.dvn.core.visualization.DataVariableMapping;
import edu.harvard.iq.dvn.core.web.VisualizationLineDefinition;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import edu.harvard.iq.dvn.ingest.dsb.FieldCutter;
import edu.harvard.iq.dvn.ingest.dsb.impl.DvnJavaFieldCutter;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.inject.Named;

/**
 *
 * @author skraffmiller
 */
@ViewScoped
@Named("GuestBookResponseDataPage")
public class GuestBookResponseDataPage extends VDCBaseBean implements java.io.Serializable {

    @EJB GuestBookResponseServiceBean guestBookResponseServiceBean;
    private List<GuestBookResponse> guestBookResponses = new ArrayList();
    private List<GuestBookResponseDisplay> guestBookResponsesDisplay = new ArrayList();
    private List<GuestBookResponse> guestBookResponsesAll = new ArrayList();
    private List<String> columnHeadings = new ArrayList();
    private List<Long> customQuestionIds = new ArrayList();
    private GuestBookQuestionnaire guestBookQuestionnaire;
    private Long fullCount = new Long(0);
    private Long thirtyDayCount = new Long(0);
    private String csvString = "";
    private VDC vdc;

    public void init() {

         //Date thirtyDaysAgo = now.     
        Date today = new Date();
        Calendar cal = new GregorianCalendar();
        cal.setTime(today);
        System.out.print("before " + cal.getTime());
        cal.add(Calendar.DAY_OF_MONTH, -30);
        Date today30 = cal.getTime();     
        System.out.print("after " + today30.toString());
        guestBookResponsesAll = guestBookResponseServiceBean.findAll();
        vdc = getVDCRequestBean().getCurrentVDC();        
        for (GuestBookResponse gbr : guestBookResponsesAll) {
            if (vdc !=null && gbr.getStudy().getOwner().equals(vdc)) {
                guestBookResponses.add(gbr);
                fullCount++;
                if (today30.before(gbr.getResponseTime())){
                    thirtyDayCount++;
                }
                if (!gbr.getCustomQuestionResponses().isEmpty()) {
                    for (CustomQuestionResponse cqr : gbr.getCustomQuestionResponses()) {
                        if (!customQuestionIds.contains(cqr.getCustomQuestion().getId())) {
                            customQuestionIds.add(cqr.getCustomQuestion().getId());
                            columnHeadings.add(cqr.getCustomQuestion().getQuestionString());
                        }
                    }
                }
            }  else if (vdc == null) {
                  guestBookResponses.add(gbr);
                  fullCount++;
                if (today30.before(gbr.getResponseTime())){
                    thirtyDayCount++;
                }
            }
        }
        if (!customQuestionIds.isEmpty()) {
            for (GuestBookResponse gbr : guestBookResponses) {
                GuestBookResponseDisplay guestBookResponseDisplay = new GuestBookResponseDisplay();
                guestBookResponseDisplay.setGuestBookResponse(gbr);
                List<String> customQuestionResponseStrings = new ArrayList(customQuestionIds.size());
                for (int i=0; i<customQuestionIds.size(); i++){
                    customQuestionResponseStrings.add(i, "");
                }
                if (!gbr.getCustomQuestionResponses().isEmpty()) {
                    for (Long id : customQuestionIds) {
                        int index = customQuestionIds.indexOf(id);
                        for (CustomQuestionResponse cqr : gbr.getCustomQuestionResponses()) {
                            if (cqr.getCustomQuestion().getId().equals(id)) {
                                customQuestionResponseStrings.set(index, cqr.getResponse());
                            }
                        }
                    }
                }
                guestBookResponseDisplay.setCustomQuestionResponses(customQuestionResponseStrings);
                guestBookResponsesDisplay.add(guestBookResponseDisplay);
            }
        } else {
            for (GuestBookResponse gbr : guestBookResponses) {
                GuestBookResponseDisplay guestBookResponseDisplay = new GuestBookResponseDisplay();
                guestBookResponseDisplay.setGuestBookResponse(gbr);
                guestBookResponsesDisplay.add(guestBookResponseDisplay);
            }
            
        }
        writeCSVString();
    }
    
    private String getColumnString() {
        String csvColumnString = "";
        csvColumnString += "User/Session,First Name,Last Name,Email,Institution,Position,";
        if (vdc==null){
           csvColumnString += "Dataverse,";
        }
        csvColumnString += "Study Global ID, Study Title,Study File,Time,Type,Session";
        if (!columnHeadings.isEmpty()){
            for (String heading: columnHeadings){
                csvColumnString += "," + getSafeCString(heading);
            }
        }
        return csvColumnString;
    }
    


    private void writeCSVString() {

        String csvOutput = getColumnString() + "\n";
        String csvCol;
        for (GuestBookResponseDisplay gbrd: guestBookResponsesDisplay){
            csvCol = "";
            if(gbrd.getGuestBookResponse().getVdcUser() != null){
               csvCol += gbrd.getGuestBookResponse().getVdcUser().getUserName();
            } else {
               csvCol += "Anonymous - + " + gbrd.getGuestBookResponse().getSessionId();
            }
            csvCol += "," +getSafeCString(gbrd.getGuestBookResponse().getFirstname());
            csvCol += "," + getSafeCString(gbrd.getGuestBookResponse().getLastname());   
            csvCol += "," + getSafeCString(gbrd.getGuestBookResponse().getEmail());
            csvCol += "," + getSafeCString(gbrd.getGuestBookResponse().getInstitution());
            csvCol += "," + getSafeCString(gbrd.getGuestBookResponse().getPosition());
            if (vdc==null){
                csvCol += "," + getSafeCString(gbrd.getGuestBookResponse().getStudy().getOwner().getName());
            }
            csvCol += "," + getSafeCString(gbrd.getGuestBookResponse().getStudy().getGlobalId());
            csvCol += "," + getSafeCString(gbrd.getGuestBookResponse().getStudy().getLatestVersion().getMetadata().getTitle());
            csvCol += "," + getSafeCString(gbrd.getGuestBookResponse().getStudyFile().getFileName());
            csvCol += "," + getSafeCString(gbrd.getGuestBookResponse().getResponseTime().toString());
            csvCol += "," + getSafeCString(gbrd.getGuestBookResponse().getDownloadtype());
            csvCol += "," + getSafeCString(gbrd.getGuestBookResponse().getSessionId());
            if (!gbrd.getCustomQuestionResponses().isEmpty()){
                for (String response: gbrd.getCustomQuestionResponses()){
                    csvCol += "," + getSafeCString(response);
                }
            }
            csvCol += "\n";
            csvOutput = csvOutput + csvCol;
        }
        csvString = csvOutput;
    }
    
    private String getSafeCString(String strIn){
        String retString = strIn;
        if (strIn == null){
            return "";
        }
        int nextSpace = strIn.indexOf(",");  
        if(nextSpace > 0){
            // If the string is already enclosed in double quotes, remove them:
            retString = retString.replaceFirst("^\"", "");
            retString = retString.replaceFirst("\"$", "");

            // Escape all remaining double quotes, by replacing each one with
            // 2 double quotes in a row ("").
            // This is an ancient (IBM Mainframe ancient) CSV convention.
            // (see http://en.wikipedia.org/wiki/Comma-separated_values#Basic_rules)
            // Excel apparently follows it to this day. On the other hand,
            // Excel does NOT understand backslash escapes and will in fact
            // be confused by it!

            retString = retString.replaceAll("\"", "\"\"");

            // finally, add double quotes around the string:
            retString = "\"" + retString + "\"";
        } 
        return retString;
    }
    
    public GuestBookQuestionnaire getGuestBookQuestionnaire() {
        return guestBookQuestionnaire;
    }

    public void setGuestBookQuestionnaire(GuestBookQuestionnaire guestBookQuestionnaire) {
        this.guestBookQuestionnaire = guestBookQuestionnaire;
    }
    
    public List<GuestBookResponse> getGuestBookResponses() {
        return guestBookResponses;
    }

    public void setGuestBookResponses(List<GuestBookResponse> guestBookResponses) {
        this.guestBookResponses = guestBookResponses;
    }
    
    public VDC getVdc() {
        return vdc;
    }

    public void setVdc(VDC vdc) {
        this.vdc = vdc;
    }
    
    public Long getFullCount() {
        return fullCount;
    }

    public void setFullCount(Long fullCount) {
        this.fullCount = fullCount;
    }

    public Long getThirtyDayCount() {
        return thirtyDayCount;
    }

    public void setThirtyDayCount(Long thirtyDayCount) {
        this.thirtyDayCount = thirtyDayCount;
    }
    
    public List<String> getColumnHeadings() {
        return columnHeadings;
    }

    public void setColumnHeadings(List<String> columnHeadings) {
        this.columnHeadings = columnHeadings;
    }

    public List<GuestBookResponseDisplay> getGuestBookResponsesDisplay() {
        return guestBookResponsesDisplay;
    }

    public void setGuestBookResponsesDisplay(List<GuestBookResponseDisplay> guestBookResponsesDisplay) {
        this.guestBookResponsesDisplay = guestBookResponsesDisplay;
    }
    
    public Resource getDownloadCSV() {
        return new ExportFileResource("csv");
    }
    
    private void writeFile(File fileIn, String dataIn, int bufSize) {
        ByteBuffer dataByteBuffer = ByteBuffer.wrap(dataIn.getBytes());
        try {
            FileOutputStream outputFile = null;
            outputFile = new FileOutputStream(fileIn, true);
            WritableByteChannel outChannel = outputFile.getChannel();

            try {
                outChannel.write(dataByteBuffer);
                outputFile.close();
            } catch (IOException e) {
                e.printStackTrace(System.err);
            }
        } catch (IOException e) {
            throw new EJBException(e);
        }
    }

    
    public class ExportFileResource implements Resource, Serializable{
        File file;
        String fileType;

        public ExportFileResource(String fileType) {
            
            this.fileType = fileType;
        }

        public String calculateDigest() {
            return file != null ? file.getPath() : null;
        }

        public Date lastModified() {
            return file != null ? new Date(file.lastModified()) : null;
        }

        public InputStream open() throws IOException {
            try {
                file = File.createTempFile("downloadFile","tmp");
            } catch (IOException ioException){               
                 System.out.print("Guestbookresponse open exception: " + ioException);
            }

            writeFile(file, csvString, csvString.length());
            return new FileInputStream(file);
        }

        public void withOptions(Resource.Options options) throws IOException {
            String filePrefix = "dataDownload_" + new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss").format(new Date());
            options.setFileName(filePrefix + "." + fileType);

        }

        public File getFile() {
            return file;
        }

    }
}
