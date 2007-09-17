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
 * RepositoryWrapper.java
 *
 * Created on November 1, 2006, 3:24 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.study;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author gdurand
 */
public class RepositoryWrapper {
    
    private HttpClient client = null;
    
    
    
    /**
     * Creates a new instance of RepositoryWrapper
     */
    public RepositoryWrapper() {
    }
    
    private String getRepositoryUrl() throws IOException{
        String repositoryUrl = System.getProperty("vdc.repository.url");
        
        if (repositoryUrl != null) {
            return repositoryUrl;
        } else {
            throw new IOException("System property \"vdc.repository.url\" has not been set.");
        }
        
    }
    
    private HttpClient getClient() {
        if (client == null) {
            client = new HttpClient( new MultiThreadedHttpConnectionManager() );
        }
        return client;
    }
    
    private void executeGetMethod(GetMethod method) throws IOException {
        int state = getClient().executeMethod(method);
        
        if (state != 200) {
            String msg =" GetMethod ="+method.getPath()+" ";
            throw new IOException(
                    (method.getStatusLine() != null)
                    ? msg+method.getStatusLine().toString()
                    : msg+ "Repository Error");
        }
    }
    
   
    
    public Document accessMeta(String objectHandle) throws IOException, SAXException{
        BufferedReader rd = null;
        GetMethod method = null;
        Document xmlDoc=null;
        
        try {
            // create method
            method = new GetMethod(getRepositoryUrl()+"AccessMeta/" + objectHandle);
            
            
            // execute
            executeGetMethod(method);
            
            // parse the response
            
            DOMParser parser = new DOMParser();
            InputStream in = method.getResponseBodyAsStream();
            InputSource source = new InputSource(in);
            parser.parse(source);
            in.close();
            
            xmlDoc = parser.getDocument();
        } finally {
            if (method != null) { method.releaseConnection(); }
            try {
                if (rd != null) { rd.close(); }
            } catch (IOException ex) {
            }
        }
        return xmlDoc;
    }
    
    public List<String> getStudyDDIPaths(String authority) throws IOException, SAXException {
        List paths= new ArrayList<String>();
        Document doc = getAllAccessMeta(authority);
        NodeList nodes = doc.getElementsByTagName("RepositoryMetadata");
        for (int i=0;i<nodes.getLength(); i++) {
            String filePath=null;
            NodeList metaFields = nodes.item(i).getChildNodes();
            filePath= getMetaValue(metaFields,"objid");
            if (filePath==null) {
                throw new RuntimeException("Did not find meta-value for objId");
            } else {
                paths.add(filePath);
            }
            
        }
        
        return paths;
    }
    
    
    private Document getAllAccessMeta(String authority)throws IOException, SAXException {
        
        BufferedReader rd = null;
        GetMethod method = null;
        Document xmlDoc=null;
        
        try {
            
            // create method
            method = new GetMethod(getRepositoryUrl()+"AccessMeta?iquery=((rname%20LIKE%20'hdl:"+authority+"/%')AND(mimeu='ddi'))");
            // execute
            executeGetMethod(method);
            
            // parse the response
            
            DOMParser parser = new DOMParser();
            InputStream in = method.getResponseBodyAsStream();
            InputSource source = new InputSource(in);
            parser.parse(source);
            in.close();
            
            xmlDoc = parser.getDocument();
        } finally {
            if (method != null) { method.releaseConnection(); }
            try {
                if (rd != null) { rd.close(); }
            } catch (IOException ex) {
            }
        }
        return xmlDoc;
        
    }
    
    public boolean isObjectRestricted(String objectHandle)  throws IOException, SAXException{
        boolean isRestricted=true;
        Document doc = this.accessMeta(objectHandle);
        
        // There should only be one Repository Metatadata node
        Node repositoryMetadata = doc.getElementsByTagName("RepositoryMetadata").item(0);
        String classValue= getMetaValue(repositoryMetadata.getChildNodes(), "class");
        if (classValue.contains("PUBLIC_OBJ")) {
            isRestricted=false;
        }
        return isRestricted;
    }
    
    private String getMetaValue(NodeList metaFields, String metaName) {
        String metaValue=null;
        for (int j=0;j<metaFields.getLength();j++) {
            boolean isMetaName=false;
            NodeList fieldChildren = metaFields.item(j).getChildNodes();
            
            for (int k=0; k < fieldChildren.getLength(); k++) {
                String localName = fieldChildren.item(k).getLocalName();
                String text = fieldChildren.item(k).getTextContent();
                if ( "meta-name".equals(fieldChildren.item(k).getLocalName())
                && metaName.equals(fieldChildren.item(k).getTextContent())) {
                    isMetaName=true;
                }
                if (isMetaName && "meta-value".equals(fieldChildren.item(k).getLocalName())) {
                    metaValue=fieldChildren.item(k).getTextContent();
                    break;
                }          
            }
        }
        return metaValue;
    }
}









