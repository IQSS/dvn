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
 * XhtmlValidator.java
 *
 * Created on January 3, 2007, 1:04 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.web.util;

import com.icesoft.faces.component.ext.HtmlInputTextarea;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
/**
 *
 * @author wbossons
 */
public class XhtmlValidator implements Validator, java.io.Serializable  {
    private static String msg = new String();
    
    /** Creates a new instance of XhtmlValidator */
    public XhtmlValidator() {
    }
    
    public void validate(FacesContext context, UIComponent component, 
                            Object value) {
        String htmlString = new String("");
        if (value != null)
            htmlString = (String) value;
        else
            return;
        try {
            if ( isStructuralTag(htmlString) || (htmlString.indexOf("<") != -1 && !validateXhtml(htmlString)) ) {
                FacesMessage message = new FacesMessage(msg);
                message.setSeverity(FacesMessage.SEVERITY_ERROR);
                ((UIInput)component).setValid(false);
                context.addMessage(component.getClientId(context), message);
                context.renderResponse();
            }
        } catch (Exception e) {
            System.out.println("An exception was thrown ... " + e.toString());
        } 
    }
    

    
    public static boolean validateXhtml (String htmlString)
        throws javax.xml.parsers.ParserConfigurationException, java.io.UnsupportedEncodingException, 
            org.xml.sax.SAXException, java.io.IOException {
      boolean isValidXhtml = true;
      htmlString = "<validate>" + htmlString + "</validate>";// avoid prolog errors
      try {
          DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
          DocumentBuilder docbuilder = dbfactory.newDocumentBuilder();
          InputStream inputStream = new ByteArrayInputStream(htmlString.getBytes("UTF-8"));// set up the InputSource to send to the parser
          Document document = docbuilder.parse(inputStream);
      } catch (javax.xml.parsers.ParserConfigurationException pce) {
          System.out.println( "ParserConfigurationException . . . " + pce.toString());
          msg = "ParserConfigurationException . . . " + pce.toString();
          isValidXhtml = false;
      } catch (java.io.UnsupportedEncodingException uee) {
          System.out.println( "Unsupported Encoding Exception . . . " + uee.toString());
          msg = "Unsupported Encoding Exception . . . " + uee.toString();
          isValidXhtml = false;
      } catch (org.xml.sax.SAXException se) {
          String cause = new String();
          if (se.toString().indexOf("element type \"validate\" must be terminated") != -1)
              cause = "Found a closing html tag (ex. &lt;/tagname...) without a starting html tag (ex. &lt;tagname...).";
          else if (se.toString().indexOf("element type \"br\" must be terminated") != -1)
              cause = "Please check that all \"br\" tags are formatted properly. Change all \"<br>\" to  \"&lt;br /&gt;\".";
          else if (se.toString().indexOf("element type \"image\" must be terminated") != -1)
              cause = "Please check that all \"img\" tags are formatted properly. Make sure that the end of the tag is closed: \"&lt;img /&gt;\".";
          else if (se.toString().indexOf("The entity name must immediately follow the '&' in the entity") !=-1)
              cause = "Please check that all \"&\" tags are properly escaped. Change \"&\" to \"&amp;\".";  
          else
              cause = se.toString().substring(se.toString().indexOf(":")+1, se.toString().length()) + "&#160;&#160;It's possible the end tag is missing, or the markup is unbalanced.";
          System.out.println( "HTML Error . . . " + cause);
          msg = "HTML Error . . . " + cause;
          isValidXhtml = false;
      } catch (java.io.IOException ioe) {
          System.out.println( "IO Exception . . . " + ioe.toString());
          msg = "IO Exception . . . " + ioe.toString();
          isValidXhtml = false;
      } finally {
          return isValidXhtml;
      }
    }
    
    public boolean validateXhtmlMessage (String htmlString, List errorMsg)
        throws javax.xml.parsers.ParserConfigurationException, java.io.UnsupportedEncodingException, 
            org.xml.sax.SAXException, java.io.IOException {
      boolean isValidXhtml = true;
      htmlString = "<validate>" + htmlString + "</validate>";// avoid prolog errors
      try {
          DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
          DocumentBuilder docbuilder = dbfactory.newDocumentBuilder();
          InputStream inputStream = new ByteArrayInputStream(htmlString.getBytes("UTF-8"));// set up the InputSource to send to the parser
          Document document = docbuilder.parse(inputStream);
      } catch (javax.xml.parsers.ParserConfigurationException pce) {
          System.out.println( "ParserConfigurationException . . . " + pce.toString());
          msg = "ParserConfigurationException . . . " + pce.toString();
          if (errorMsg !=null ){
              errorMsg.add("ParserConfigurationException . . . " + pce.toString()); 
          }
          isValidXhtml = false;
      } catch (java.io.UnsupportedEncodingException uee) {
          System.out.println( "Unsupported Encoding Exception . . . " + uee.toString());
          msg = "Unsupported Encoding Exception . . . " + uee.toString();
          if (errorMsg !=null ){
              errorMsg.add("Unsupported Encoding Exception . . . " + uee.toString());
          }
          isValidXhtml = false;
      } catch (org.xml.sax.SAXException se) {
          String cause = new String();
          if (se.toString().indexOf("element type \"validate\" must be terminated") != -1)
              cause = "Found a closing html tag (ex. &lt;/tagname...) without a starting html tag (ex. &lt;tagname...).";
          else if (se.toString().indexOf("element type \"br\" must be terminated") != -1)
              cause = "Please check that all \"br\" tags are formatted properly. Change all \"<br>\" to  \"&lt;br /&gt;\".";
          else if (se.toString().indexOf("element type \"image\" must be terminated") != -1)
              cause = "Please check that all \"img\" tags are formatted properly. Make sure that the end of the tag is closed: \"&lt;img /&gt;\".";
          else if (se.toString().indexOf("The entity name must immediately follow the '&' in the entity") !=-1)
              cause = "Please check that all \"&\" tags are properly escaped. Change \"&\" to \"&amp;\".";  
          else if (se.toString().indexOf("element type \"div\" must be terminated") != -1)
              cause = "The element type \"div\" must be terminated by the matching end-tag \"/div\". It's possible the end tag is missing, or the markup is unbalanced.";       
          else
              cause = se.toString().substring(se.toString().indexOf(":")+1, se.toString().length()) + " It's possible the end tag is missing, or the markup is unbalanced.";
          System.out.println( "HTML Error . . . " + cause);
          msg = "HTML Error . . . " + cause;
          if (errorMsg !=null ){
              errorMsg.add("HTML Error . . . " + cause);
          }
          isValidXhtml = false;
      } catch (java.io.IOException ioe) {
          System.out.println( "IO Exception . . . " + ioe.toString());
          msg = "IO Exception . . . " + ioe.toString();
          if (errorMsg !=null ){
              errorMsg.add("IO Exception . . . " + ioe.toString());
          }
          isValidXhtml = false;
      } finally {
          return isValidXhtml;
      }
    }
    
    private static boolean isStructuralTag(String htmlString) {
        boolean isStructuralTag = false;
        if (htmlString.contains("<html") || htmlString.contains("</html>") || htmlString.contains("<body") || htmlString.contains("</body>")) {
            msg="Found one or more of the element types html and/or body. Html and body are not allowed.";
            isStructuralTag = true;
        }
        return isStructuralTag;
    }
}
