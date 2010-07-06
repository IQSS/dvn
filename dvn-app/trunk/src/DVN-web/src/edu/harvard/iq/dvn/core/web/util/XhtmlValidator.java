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
    
    private static boolean isStructuralTag(String htmlString) {
        boolean isStructuralTag = false;
        if (htmlString.contains("<html") || htmlString.contains("</html>") || htmlString.contains("<body") || htmlString.contains("</body>")) {
            msg="Found one or more of the element types html and/or body. Html and body are not allowed.";
            isStructuralTag = true;
        }
        return isStructuralTag;
    }
}
