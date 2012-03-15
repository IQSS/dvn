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
 * GNRSServiceBean.java
 *
 * Created on February 14, 2007, 1:18 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.gnrs;

import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.vdc.*;
import java.io.File;
import java.io.FileInputStream;
import java.security.PrivateKey;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import net.handle.hdllib.AbstractMessage;
import net.handle.hdllib.AbstractResponse;
import net.handle.hdllib.AdminRecord;
import net.handle.hdllib.ClientSessionTracker;
import net.handle.hdllib.CreateHandleRequest;
import net.handle.hdllib.DeleteHandleRequest;
import net.handle.hdllib.Encoder;
import net.handle.hdllib.HandleException;
import net.handle.hdllib.HandleResolver;
import net.handle.hdllib.HandleValue;
import net.handle.hdllib.ModifyValueRequest;
import net.handle.hdllib.PublicKeyAuthenticationInfo;
import net.handle.hdllib.ResolutionRequest;
import net.handle.hdllib.ResolutionResponse;
import net.handle.hdllib.Util;

/**
 *
 * @author roberttreacy
 */
@Stateless
public class GNRSServiceBean implements edu.harvard.iq.dvn.core.gnrs.GNRSServiceLocal {
    @PersistenceContext(unitName="VDCNet-ejbPU") EntityManager em;
    @EJB VDCNetworkServiceLocal vdcNetworkService;
    @EJB StudyServiceLocal studyService;
    
    private static final Logger logger = Logger.getLogger("edu.harvard.iq.dvn.core.gnrs.GNRSServiceBean");

    /** Creates a new instance of GNRSServiceBean */
    public GNRSServiceBean() {
    }
    
    public String getNewObjectId(String protocol, String authority){
        /*
        NRS nrs = nrsFactory.getNRS(protocol, authority);
        String objectId =  nrs.getNewObjectId();
         */
        return generateStudyIdSequence(protocol, authority);
    }
    
    private String generateStudyIdSequence(String protocol, String authority) {
     
        String studyId=null;
        do {
            studyId = ((Long) em.createNativeQuery("select nextval('studyid_seq')").getSingleResult()).toString();

        } while (!isUniqueStudyId(studyId, protocol, authority));
        
        
        return studyId;
        
    }
    
    /**
     *  Check that a studyId entered by the user is unique (not currently used for any other study in this Dataverse Network)
     */
   private boolean isUniqueStudyId(String userStudyId, String protocol,String authority) {
       String query = "SELECT s FROM Study s WHERE s.studyId = '" + userStudyId +"'";
       query += " and s.protocol ='"+protocol+"'";
       query += " and s.authority = '"+authority+"'";
       boolean u = em.createQuery(query).getResultList().size()==0;
       return u;
    }

   public void delete(String authority, String studyId) {
       String handle = authority + "/" + studyId;
       VDCNetwork vdcNetwork = vdcNetworkService.find();
       if (vdcNetwork.isHandleRegistration() && isAuthority(authority)){
           deleteHandle(handle);
       }
   }

   public void fixHandle( String handle){
   
       if (isHandleRegistered(handle)) {
           // Rebuild/Modify an existing handle

           String prefix = handle.substring(0, handle.indexOf("/"));
           String localHandle = handle.substring(handle.indexOf("/") + 1);
           String authHandle = getAuthHandle();
           int index = 300;


           byte[] key = null;
           String adminCredFile = System.getProperty("dvn.handle.admcredfile");
           key = readKey(adminCredFile);

           PrivateKey privkey = null;
           privkey = readPrivKey(key, adminCredFile);


           HandleResolver resolver = new HandleResolver();

           String urlStr = getUrlStr(prefix, handle);

           try {
               PublicKeyAuthenticationInfo auth =
                       new PublicKeyAuthenticationInfo(authHandle.getBytes("UTF8"),
                       index,
                       privkey);

               AdminRecord admin = new AdminRecord(authHandle.getBytes("UTF8"), 300,
                       true, true, true, true, true, true,
                       true, true, true, true, true, true);

               int timestamp = (int) (System.currentTimeMillis() / 1000);

               HandleValue[] val = {new HandleValue(100, "HS_ADMIN".getBytes("UTF8"),
                   Encoder.encodeAdminRecord(admin),
                   HandleValue.TTL_TYPE_RELATIVE, 86400,
                   timestamp, null, true, true, true, false), new HandleValue(1, "URL".getBytes("UTF8"),
                   urlStr.getBytes(),
                   HandleValue.TTL_TYPE_RELATIVE, 86400,
                   timestamp, null, true, true, true, false)};


               ModifyValueRequest req = new ModifyValueRequest(handle.getBytes("UTF8"), val, auth);

               resolver.traceMessages = true;
               AbstractResponse response = resolver.processRequest(req);
               if (response.responseCode == AbstractMessage.RC_SUCCESS) {
                   logger.fine("\nGot Response: \n" + response);
               } else {
                   logger.fine("\nGot Error: \n" + response);
               }
           } catch (Throwable t) {
               logger.fine("\nError: " + t);
           }
       } else {
           // Create a new handle from scratch:
           
           createHandle(handle);
       }
    }

   public void createHandle( String handle){
    String prefix = handle.substring(0,handle.indexOf("/"));
    logger.fine("Creating handle "+handle);
       if (vdcNetworkService.find().isHandleRegistration() && isAuthority(prefix)){
           String localHandle = handle.substring(handle.indexOf("/")+1);
           String authHandle =  getAuthHandle();
           
           byte[] key = null;
           String adminCredFile = System.getProperty("dvn.handle.admcredfile");
           key = readKey(adminCredFile);
           PrivateKey privkey = readPrivKey(key, adminCredFile);

           HandleResolver resolver = new HandleResolver();
           
           String urlStr = getUrlStr(prefix, handle);
           
           int index = 300;
           
           try {
               PublicKeyAuthenticationInfo auth =
                       new PublicKeyAuthenticationInfo(authHandle.getBytes("UTF8"),
                       index,
                       privkey);
               
               AdminRecord admin = new AdminRecord(authHandle.getBytes("UTF8"), 300,
                       true, true , true, true, true, true,
                       true, true, true, true, true, true);
               
               int timestamp = (int)(System.currentTimeMillis()/1000);
               
               HandleValue[] val = { new HandleValue(100, "HS_ADMIN".getBytes("UTF8"),
                       Encoder.encodeAdminRecord(admin),
                       HandleValue.TTL_TYPE_RELATIVE, 86400,
                       timestamp, null, true, true, true, false), new HandleValue(1,"URL".getBytes("UTF8"),
                       urlStr.getBytes(),
                       HandleValue.TTL_TYPE_RELATIVE, 86400,
                       timestamp, null, true, true, true, false) };
               
               CreateHandleRequest req =
                       new CreateHandleRequest(handle.getBytes("UTF8"), val, auth);
               
               resolver.traceMessages = true;
               AbstractResponse response = resolver.processRequest(req);
               if (response.responseCode == AbstractMessage.RC_SUCCESS){
                   logger.fine("\nGot Response: \n"+response);
               } else {
                   logger.fine("\nGot Error: \n"+response);
               }
           } catch (Throwable t) {
               logger.severe("\nError: "+t);
           }
       }
   }

   public void deleteHandle( String handle) {
       String prefix = handle.substring(0,handle.indexOf("/"));
       if (vdcNetworkService.find().isHandleRegistration() && isAuthority(prefix)){
           String localHandle = handle.substring(handle.indexOf("/")+1);
           String authHandle =  getAuthHandle();
           
           byte[] key = null;
           String adminCredFile = System.getProperty("dvn.handle.admcredfile");
           key = readKey(adminCredFile);
           PrivateKey privkey = readPrivKey(key, adminCredFile);
                      
           HandleResolver resolver = new HandleResolver();
           resolver.setSessionTracker(new ClientSessionTracker());
           
           PublicKeyAuthenticationInfo auth =
                   new PublicKeyAuthenticationInfo(Util.encodeString(authHandle), 300, privkey);
           
           DeleteHandleRequest req =
                   new DeleteHandleRequest(Util.encodeString(handle), auth);
           AbstractResponse response=null;
           try {
               response = resolver.processRequest(req);
           } catch (HandleException ex) {
               ex.printStackTrace();
           }
           if(response==null || response.responseCode!=AbstractMessage.RC_SUCCESS) {
               logger.fine("error deleting '"+handle+"': "+response);
           } else {
               logger.fine("deleted "+handle);
           }
       }
   }
   
    public String resolveHandleUrl(String handle){
        ResolutionRequest req = buildResolutionRequest(handle);
        AbstractResponse response=null;
        HandleResolver resolver = new HandleResolver();
        try {
            response = resolver.processRequest(req);
        } catch (HandleException ex) {
            ex.printStackTrace();
        }
        if(response==null || response.responseCode!=AbstractMessage.RC_SUCCESS) {
            logger.fine("error resolving '"+handle+"': "+response);
        } else {
            logger.fine("resolved "+handle);
        }
        String handleUrl = null;
        try {
            HandleValue[] values = ((ResolutionResponse) response).getHandleValues();
            logger.fine("Size " + values.length);
            for (int i = 0; i < values.length; i++) {
                logger.fine("Handle "+ i + values[i].getTypeAsString()+values[i].getDataAsString());
                if (values[i].getTypeAsString().equals("URL")){
                    handleUrl = values[i].getDataAsString();
                }
            }
        } catch (HandleException ex) {
            ex.printStackTrace();
        }
        return handleUrl;
    }
    
    public boolean isHandleRegistered(String handle){
        boolean handleRegistered = false;
        ResolutionRequest req = buildResolutionRequest(handle);
        AbstractResponse response = null;
        HandleResolver resolver = new HandleResolver();
        try {
            response = resolver.processRequest(req);
        } catch (HandleException ex) {
            ex.printStackTrace();
        }
        if((response!=null && response.responseCode==AbstractMessage.RC_SUCCESS)) {
            handleRegistered = true;;
        } 
        return handleRegistered;
    }

    private ResolutionRequest buildResolutionRequest(final String handle) {
        String prefix = handle.substring(0,handle.indexOf("/"));
        String localHandle = handle.substring(handle.indexOf("/")+1);
        String authHandle =  getAuthHandle();
        byte[] key = null;
        String adminCredFile = System.getProperty("dvn.handle.admcredfile");

        key = readKey(adminCredFile);        
        PrivateKey privkey = null;
        privkey = readPrivKey(key, adminCredFile);
        PublicKeyAuthenticationInfo auth =
                new PublicKeyAuthenticationInfo(Util.encodeString(authHandle), 300, privkey);
        
        byte[][] types = null;
        int[] indexes = null;
        ResolutionRequest req =
                new ResolutionRequest(Util.encodeString(handle),
                types, indexes,
                auth);
        req.certify = false;
        req.cacheCertify = true;
        req.authoritative = false;
        req.ignoreRestrictedValues = true;
        return req;
    }

    private PrivateKey readPrivKey(byte[] key, final String file) {
        PrivateKey privkey=null;
        
        String secret = System.getProperty("dvn.handle.admprivphrase");
        byte secKey[] = null;
        try {
            if(Util.requiresSecretKey(key)){
                secKey = secret.getBytes();
            }
            key = Util.decrypt(key, secKey);
            privkey = Util.getPrivateKeyFromBytes(key, 0);
        } catch (Throwable t){
            logger.severe("Can't load private key in " + file +": " + t);
        }
        return privkey;
    }

    private byte[] readKey(final String file) {
        byte[] key = null;
        try {
            File f = new File(file);
            FileInputStream fs = new FileInputStream(f);
            key = new byte[(int)f.length()];
            int n=0;
            while(n<key.length) {
                key[n++] = (byte)fs.read();
            }
        } catch (Throwable t){
            logger.severe("Cannot read private key " + file +": " + t);
        }
        return key;
    }

    
    private String getUrlStr(String prefix, String handle) {
        VDCNetwork network = vdcNetworkService.find();
        String vdcAuthority = network.getAuthority(); 
        String baseUrl = null;
        if (isAuthority(prefix)){
            baseUrl = "http://" + System.getProperty("dvn.inetAddress") + "/dvn/study?globalId=hdl:";
            if (baseUrl == null) {
                baseUrl = "http://dvn.iq.harvard.edu/dvn/study?globalId=hdl:";
            }
        }
        return baseUrl + handle;
    }
    
    private boolean isAuthority(String prefix){
        VDCNetwork network = vdcNetworkService.find();
        String vdcAuthority = network.getAuthority(); 
        boolean auth = false;
        if (prefix.equals(vdcAuthority)){
            auth = true;
        } else {
            String query = "select h from HandlePrefix h where h.prefix = '" +prefix + "'";
            List <HandlePrefix> handlePrefixList = em.createQuery(query).getResultList();
            if (handlePrefixList.size() > 0){
                auth = true;
            }
        }
        return auth;
    }
    
    public void registerAll(){
        List<Study> studies = studyService.getStudies();
        for (Iterator it = studies.iterator(); it.hasNext();) {
            Study elem = (Study) it.next();
            String handle = elem.getAuthority()+"/"+ elem.getStudyId();
            if (isAuthority(elem.getAuthority()) && !isHandleRegistered(handle)){
                createHandle(handle);
            }
        }
        
    }
    
    public void deleteAll(){
        List<Study> studies = studyService.getStudies();
        for (Iterator it = studies.iterator(); it.hasNext();) {
            Study elem = (Study) it.next();
            String handle = elem.getAuthority()+"/"+ elem.getStudyId();
            if (isAuthority(elem.getAuthority()) && isHandleRegistered(handle) && !handle.startsWith("1902.1")){
                deleteHandle(handle);
            }
        }
        
    }

    public void fixAll() {
        List<Study> studies = studyService.getStudies();
        for (Iterator it = studies.iterator(); it.hasNext();) {
            Study elem = (Study) it.next();
            String handle = elem.getAuthority() + "/" + elem.getStudyId();
            if (isAuthority(elem.getAuthority()) && isHandleRegistered(handle)) {
                fixHandle(handle);
            }
        }

    }

    private String getAuthHandle(){
        VDCNetwork network = vdcNetworkService.find();
        return "0.NA/" + network.getAuthority();
    }
}
