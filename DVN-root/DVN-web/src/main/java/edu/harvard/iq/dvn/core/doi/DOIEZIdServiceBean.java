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
package edu.harvard.iq.dvn.core.doi;

import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyAuthor;
import edu.harvard.iq.dvn.core.study.StudyProducer;
import edu.harvard.iq.dvn.core.util.PropertyUtil;
import edu.harvard.iq.dvn.core.vdc.VDCNetworkServiceLocal;
import edu.ucsb.nceas.ezid.EZIDClient;
import edu.ucsb.nceas.ezid.EZIDException;
import edu.ucsb.nceas.ezid.EZIDService;
import edu.ucsb.nceas.ezid.EZIDServiceRequest;
import java.util.*;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author skraffmiller
 */
@Stateless
public class DOIEZIdServiceBean implements edu.harvard.iq.dvn.core.doi.DOIEZIdServiceLocal {
    EZIDService ezidService;
    EZIDServiceRequest ezidServiceRequest;    
    String baseURLString = "https://n2t.net/ezid/";  
    @EJB VDCNetworkServiceLocal vdcNetworkService;
    
    // get username and password from system properties
    private String DOISHOULDER = "";
    private String USERNAME = "";
    private String PASSWORD = "";    
    
    public DOIEZIdServiceBean(){
        ezidService = new EZIDService (baseURLString); 
        USERNAME  = System.getProperty("doi.username");
        PASSWORD  = System.getProperty("doi.password");
        try {
           ezidService.login(USERNAME, PASSWORD);  
        } catch(Exception e){
            System.out.print("login failed ");
            System.out.print("String " + e.toString() );
            System.out.print("localized message " + e.getLocalizedMessage());
            System.out.print("cause " + e.getCause());
            System.out.print("message " + e.getMessage());           
        }
    }    
    
    public String createIdentifier(Study studyIn) {
        String retString = "";
        String identifier = getIdentifierFromStudy(studyIn);
        HashMap metadata = getMetadataFromStudyForCreateIndicator(studyIn);
        metadata.put("_status", "reserved");
        try {
            retString = ezidService.createIdentifier(identifier, metadata);
            System.out.print("create DOI identifier retString : " + retString);
        } catch (EZIDException e) {
            System.out.print("create failed");
            System.out.print("String " + e.toString());
            System.out.print("localized message " + e.getLocalizedMessage());
            System.out.print("cause " + e.getCause());
            System.out.print("message " + e.getMessage());
            return "Identifier not created";
        }
        System.out.print("createIdentifier return string : " + retString);
        return retString;
    }
    
   
    public HashMap getIdentifierMetadata(Study studyIn){
        String identifier = getIdentifierFromStudy(studyIn);        
        HashMap metadata = new HashMap();
       try {
              metadata = ezidService.getMetadata(identifier);
            }  catch (EZIDException e){                
            System.out.print("getIdentifierMetadata failed");
            System.out.print("String " + e.toString() );
            System.out.print("localized message " + e.getLocalizedMessage());
            System.out.print("cause " + e.getCause());
            System.out.print("message " + e.getMessage());    
                   return metadata;
        }         
       return metadata;
    }
    
    
    public void modifyIdentifier(Study studyIn, HashMap metadata ){
        String identifier = getIdentifierFromStudy(studyIn);
       try {
               ezidService.setMetadata(identifier, metadata);
            }  catch (EZIDException e){                
            System.out.print("modifyMetadata failed");
            System.out.print("String " + e.toString() );
            System.out.print("localized message " + e.getLocalizedMessage());
            System.out.print("cause " + e.getCause());
            System.out.print("message " + e.getMessage());    
        }                
    }
    
    public void deleteIdentifier(Study studyIn) {
        String identifier = getIdentifierFromStudy(studyIn);
        HashMap doiMetadata = new HashMap();
        try {
            doiMetadata = ezidService.getMetadata(identifier);
        } catch (EZIDException e) {
            System.out.print("get matadata failed cannot delete");
            System.out.print("String " + e.toString());
            System.out.print("localized message " + e.getLocalizedMessage());
            System.out.print("cause " + e.getCause());
            System.out.print("message " + e.getMessage());
            return;
        }

        String idStatus = (String) doiMetadata.get("_status");
        
        if (idStatus.equals("reserved")) {
            System.out.print("Delete status is reserved..");
            try {
                ezidService.deleteIdentifier(identifier);
            } catch (EZIDException e) {
                System.out.print("delete failed");
                System.out.print("String " + e.toString());
                System.out.print("localized message " + e.getLocalizedMessage());
                System.out.print("cause " + e.getCause());
                System.out.print("message " + e.getMessage());
            }
            return;
        }
        if (idStatus.equals("public")) { 
            //if public then it has been released set to unavaialble and reset target to n2t url
            updateIdentifierStatus(studyIn, "unavailable | withdrawn by author");
            HashMap metadata = new HashMap();
            metadata.put("_target", "http://n2t.net/ezid/id/" + studyIn.getProtocol() + ":" + studyIn.getAuthority() + "/" + studyIn.getStudyId());
            modifyIdentifier(studyIn, metadata);
        }
    }
    
    private HashMap getMetadataFromStudyForCreateIndicator(Study studyIn) {
        HashMap<String, String> metadata = new HashMap<String, String>();
        String authorString = "";
        for (StudyAuthor author: studyIn.getLatestVersion().getMetadata().getStudyAuthors()){
        if(authorString.isEmpty()) {
               authorString = author.getName(); 
            } else{
               authorString = authorString + ", " + author.getName();
            }
        }
        if(authorString.isEmpty()) {
            authorString = ":unav";
        }
        String producerString = "";
        for (StudyProducer producer: studyIn.getLatestVersion().getMetadata().getStudyProducers()){
        if(producerString.isEmpty()) {
               producerString = producer.getName(); 
            } else{
               producerString = producerString + ", " + producer.getName();
            }
        }
        if(producerString.isEmpty()) {
            producerString = ":unav";
        }
        metadata.put("datacite.creator", authorString);
	metadata.put("datacite.title", studyIn.getLatestVersion().getMetadata().getTitle());
	metadata.put("datacite.publisher", producerString);       
	metadata.put("datacite.publicationyear", generateYear());
	metadata.put("datacite.resourcetype", "Text");
        String inetAddress = PropertyUtil.getHostUrl();
        String targetUrl = "";     
        DOISHOULDER = "doi:" + studyIn.getAuthority();
        if (inetAddress.equals("localhost")){                    
           targetUrl ="http://localhost:8080" + "/dvn/study?globalId=" + DOISHOULDER + "/" + studyIn.getStudyId();
           System.out.print("inetAddress.equals localhost" + targetUrl);
        } else{
           targetUrl = "http://" + inetAddress + "/dvn/study?globalId=" + DOISHOULDER + "/" + studyIn.getStudyId();
        }              
        System.out.print("targetUrl: " + targetUrl);
        metadata.put("_target", targetUrl);
        return metadata;
    }
   
    private String getIdentifierFromStudy(Study studyIn){
        DOISHOULDER = "doi:" + studyIn.getAuthority();
        return DOISHOULDER + "/" + studyIn.getStudyId();
    }
    
    @Override
    public void publicizeIdentifier(Study studyIn) {
        updateIdentifierStatus(studyIn, "public");
    }
    
    private void updateIdentifierStatus(Study studyIn, String statusIn){
        String identifier = getIdentifierFromStudy(studyIn);
        HashMap metadata = new HashMap();
        metadata.put("_status", statusIn);
       try {
               ezidService.setMetadata(identifier, metadata);
            }  catch (EZIDException e){                
            System.out.print("modifyMetadata failed");
            System.out.print("String " + e.toString() );
            System.out.print("localized message " + e.getLocalizedMessage());
            System.out.print("cause " + e.getCause());
            System.out.print("message " + e.getMessage());    
        }
        
    }
    
    public void test() {
        System.out.print("calling test");
        try {
            System.out.print("in test");
            HashMap<String, String> metadata = new HashMap<String, String>();
            metadata.put("datacite.creator", "testAuthor");
            metadata.put("datacite.title", "test title");
            metadata.put("datacite.publisher", "testProd");
            metadata.put("datacite.publicationyear", "2013");
            metadata.put("datacite.resourcetype", "Text");
            String timestamp = generateTimeString();
            String identifier = DOISHOULDER + "/" + "TEST" + "/" + timestamp;
            //Required metadata for DOI identifier

            metadata.put("timestamp", timestamp);
                        if(ezidService == null){
                           ezidService = new  EZIDService();
                        }
            String newId = ezidService.createIdentifier(identifier, metadata);
            System.out.print("createdIdentifier: " + newId);
            HashMap<String, String> moreMetadata = new HashMap<String, String>();
            moreMetadata.put("datacite.title", "This is a test identifier");
            ezidService.setMetadata(newId, moreMetadata);
            HashMap<String, String> getMetadata = ezidService.getMetadata(identifier);
            
            System.out.print("gotten metadata title: " + getMetadata.get("datacite.title"));
        }
        catch (Exception e){
            System.out.print("test exceptions - regular exception");
            System.out.print("String " + e.toString() );
            System.out.print("localized message " + e.getLocalizedMessage());
            System.out.print("cause " + e.getCause());
            System.out.print("message " + e.getMessage());
        }       
    }
    
    public static String generateYear()
    {
        StringBuffer guid = new StringBuffer();

        // Create a calendar to get the date formatted properly
        String[] ids = TimeZone.getAvailableIDs(-8 * 60 * 60 * 1000);
        SimpleTimeZone pdt = new SimpleTimeZone(-8 * 60 * 60 * 1000, ids[0]);
        pdt.setStartRule(Calendar.APRIL, 1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
        pdt.setEndRule(Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
        Calendar calendar = new GregorianCalendar(pdt);
        Date trialTime = new Date();
        calendar.setTime(trialTime);
        guid.append(calendar.get(Calendar.YEAR));

        return guid.toString();
    }

    
    
    public static String generateTimeString()
    {
        StringBuffer guid = new StringBuffer();

        // Create a calendar to get the date formatted properly
        String[] ids = TimeZone.getAvailableIDs(-8 * 60 * 60 * 1000);
        SimpleTimeZone pdt = new SimpleTimeZone(-8 * 60 * 60 * 1000, ids[0]);
        pdt.setStartRule(Calendar.APRIL, 1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
        pdt.setEndRule(Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
        Calendar calendar = new GregorianCalendar(pdt);
        Date trialTime = new Date();
        calendar.setTime(trialTime);
        guid.append(calendar.get(Calendar.YEAR));
        guid.append(calendar.get(Calendar.DAY_OF_YEAR));
        guid.append(calendar.get(Calendar.HOUR_OF_DAY));
        guid.append(calendar.get(Calendar.MINUTE));
        guid.append(calendar.get(Calendar.SECOND));
        guid.append(calendar.get(Calendar.MILLISECOND));
        double random = Math.random();
        guid.append(random);

        return guid.toString();
    }
   
}
