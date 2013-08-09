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

import edu.ucsb.nceas.ezid.EZIDClient;
import edu.ucsb.nceas.ezid.EZIDException;
import edu.ucsb.nceas.ezid.EZIDService;
import edu.ucsb.nceas.ezid.EZIDServiceRequest;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import javax.ejb.Stateless;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author skraffmiller
 */
@Stateless
public class DOIEZIdServiceBean implements edu.harvard.iq.dvn.core.doi.DOIEZIdServiceLocal {
    EZIDService ezidService;
    EZIDServiceRequest ezidServiceRequest;    
    String baseURLString = "https://n2t.net/ezid/";  
    String identifier = "doi:10.5072/FK2"; //test identifier
    private String USERNAME = "apitest";
    private String PASSWORD = "apitest";    
    
    public DOIEZIdServiceBean(){
        ezidService = new EZIDService (baseURLString);    
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
    
    public String createIdentifier(HashMap metadata){
        String retString = "";
       try {
             retString=  ezidService.createIdentifier(identifier, metadata);
            }  catch (EZIDException e){                
            System.out.print("create failed");
            System.out.print("String " + e.toString() );
            System.out.print("localized message " + e.getLocalizedMessage());
            System.out.print("cause " + e.getCause());
            System.out.print("message " + e.getMessage());   
            return "Identifier not created";
        }    
       return retString;
    }
    
    public String mintIdentifier(HashMap metadata){
        String retString = "";
       try {
               ezidService.mintIdentifier(identifier, metadata);
            }  catch (EZIDException e){                
            System.out.print("getIdentifierMetadata failed");
            System.out.print("String " + e.toString() );
            System.out.print("localized message " + e.getLocalizedMessage());
            System.out.print("cause " + e.getCause());
            System.out.print("message " + e.getMessage()); 
            return retString;
        }  
       return retString;
    }
   
    public HashMap getIdentifierMetadata(String identifierIn){
        HashMap metadata = new HashMap();
       try {
              metadata = ezidService.getMetadata(identifierIn);
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
    

    
    public void modifyIdentifier(HashMap metadata){
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
    
    public void delete(String identifierIn){
       try {
               ezidService.deleteIdentifier(identifierIn);
            }  catch (EZIDException e){                
            System.out.print("delete failed");
            System.out.print("String " + e.toString() );
            System.out.print("localized message " + e.getLocalizedMessage());
            System.out.print("cause " + e.getCause());
            System.out.print("message " + e.getMessage());    
        }                
    }
    
    public void test() {
        try {
            System.out.print("in test");
            HashMap<String, String> metadata = new HashMap<String, String>();
            
            //Required metadata for DOI identifier
			metadata.put("datacite.creator", "IQSS Test");
			metadata.put("datacite.title", "StudyTitle");
			metadata.put("datacite.publisher", "HU");
			metadata.put("datacite.publicationyear", "2013");
			metadata.put("datacite.resourcetype", "Text");
            String newId = ezidService.createIdentifier("doi:10.5072/FK2", metadata);
            System.out.print("createdIdentifier: " + newId);
            HashMap<String, String> moreMetadata = new HashMap<String, String>();
            moreMetadata.put("datacite.title", "This is a test identifier");
            ezidService.setMetadata(newId, moreMetadata);
        }
        catch (Exception e){
            System.out.print("test exceptions - regular exception");
            System.out.print("String " + e.toString() );
            System.out.print("localized message " + e.getLocalizedMessage());
            System.out.print("cause " + e.getCause());
            System.out.print("message " + e.getMessage());
        }       
    }
}
