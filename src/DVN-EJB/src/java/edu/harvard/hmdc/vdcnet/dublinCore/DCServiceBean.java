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
package edu.harvard.hmdc.vdcnet.dublinCore;

import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.study.StudyAbstract;
import edu.harvard.hmdc.vdcnet.study.StudyAuthor;
import edu.harvard.hmdc.vdcnet.study.StudyDistributor;
import edu.harvard.hmdc.vdcnet.study.StudyGeoBounding;
import edu.harvard.hmdc.vdcnet.study.StudyKeyword;
import edu.harvard.hmdc.vdcnet.study.StudyProducer;
import edu.harvard.hmdc.vdcnet.study.StudyRelMaterial;
import edu.harvard.hmdc.vdcnet.study.StudyTopicClass;
import edu.harvard.hmdc.vdcnet.util.StringUtil;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 *
 * @author ekraffmiller
 */
@Stateless
public class DCServiceBean implements DCServiceLocal {

    private static final Logger logger = Logger.getLogger("edu.harvard.hmdc.vdcnet.dublinCore.DCServiceBean");

    /** Creates a new instance of DDI20ServiceBean */
    public DCServiceBean() {
    }

    public boolean isXmlFormat() {
        return true;
    }
    
      @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void exportStudy(Study study, OutputStream out) throws IOException {
         XMLStreamWriter xmlw = null;
            try {
                javax.xml.stream.XMLOutputFactory xmlof = javax.xml.stream.XMLOutputFactory.newInstance();
                //xmlof.setProperty("javax.xml.stream.isPrefixDefaulting", java.lang.Boolean.TRUE);
                xmlw = xmlof.createXMLStreamWriter(out);

                xmlw.writeStartDocument();
                createDC(xmlw,study);
                  //  createCodeBook(xmlw,s);
                 xmlw.writeEndDocument();
            } catch (XMLStreamException ex) {
                logger.log(Level.SEVERE, null, ex);
                throw new EJBException("ERROR occurred in exportStudy.", ex);
            } finally {
                try {
                    if (xmlw != null) { xmlw.close(); }
                } catch (XMLStreamException ex) {}
            } 
    }

      
    public void createDC(XMLStreamWriter xmlw, Study study) throws XMLStreamException {

        xmlw.writeStartElement("oai_dc:dc");
        xmlw.writeAttribute("xmlns:oai_dc", "http://www.openarchives.org/OAI/2.0/oai_dc/");
        xmlw.writeAttribute("xmlns:dc", "http://purl.org/dc/elements/1.1/");
        xmlw.writeAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        xmlw.writeAttribute("xsi:schemaLocation", "http://www.openarchives.org/OAI/2.0/oai_dc/    http://www.openarchives.org/OAI/2.0/oai_dc.xsd");

        // Title
        xmlw.writeStartElement("dc:title");
        xmlw.writeCharacters(study.getTitle());
        xmlw.writeEndElement();

        // Identifier
        xmlw.writeStartElement("dc:identifier");
        xmlw.writeCharacters(study.getHandleURL());
        xmlw.writeEndElement();

        //Creator
        for (StudyAuthor author : study.getStudyAuthors()) {
            xmlw.writeStartElement("dc:creator");
            xmlw.writeCharacters(author.getName());
            xmlw.writeEndElement();
        }

        //Publisher
        for (StudyProducer producer : study.getStudyProducers()) {
            xmlw.writeStartElement("dc:publisher");
            xmlw.writeCharacters(producer.getName());
            xmlw.writeEndElement();
        }

        // Date
        if (!StringUtil.isEmpty(study.getProductionDate())) {
            xmlw.writeStartElement("dc:date");
            xmlw.writeCharacters(study.getProductionDate());
            xmlw.writeEndElement();

        }

        //Relation              
        if (!StringUtil.isEmpty(study.getReplicationFor())) {
            xmlw.writeStartElement("dc:relation");
            xmlw.writeCharacters(study.getReplicationFor());
            xmlw.writeEndElement();
        } else {
            for (StudyRelMaterial relMaterial : study.getStudyRelMaterials()) {
                xmlw.writeStartElement("dc:relation");
                xmlw.writeCharacters(relMaterial.getText());
                xmlw.writeEndElement();
            }
        }

        //Subject
        for (StudyKeyword keyword : study.getStudyKeywords()) {
            xmlw.writeStartElement("dc:subject");
            xmlw.writeCharacters(keyword.getValue());
            xmlw.writeEndElement();
        }
        for (StudyTopicClass topicClass : study.getStudyTopicClasses()) {
            xmlw.writeStartElement("dc:subject");
            xmlw.writeCharacters(topicClass.getValue());
            xmlw.writeEndElement();
        }

        // Description
        for (StudyAbstract studyAbstract : study.getStudyAbstracts()) {
            xmlw.writeStartElement("dc:description");
            xmlw.writeCharacters(studyAbstract.getText());
            xmlw.writeEndElement();
        }
        xmlw.writeStartElement("dc:description");
        xmlw.writeCharacters("Citation: " + study.getCitation());
        xmlw.writeEndElement();

        // Coverage
        writeCoverage(xmlw, study);

        // Type
        if (!StringUtil.isEmpty(study.getKindOfData())) {
            xmlw.writeStartElement("dc:type");
            xmlw.writeCharacters(study.getKindOfData());
            xmlw.writeEndElement();
        }

        // Source
        if (!StringUtil.isEmpty(study.getDataSources())) {
            xmlw.writeStartElement("dc:source");
            xmlw.writeCharacters(study.getDataSources());
            xmlw.writeEndElement();
        }

        // Rights
        writeRights(xmlw, study);

        //End root element
        xmlw.writeEndElement();


    }

    private void writeRights(XMLStreamWriter xmlw, Study study) throws XMLStreamException {
        // Rights
        if (study.getOwner().isDownloadTermsOfUseEnabled() && !StringUtil.isEmpty(study.getOwner().getDownloadTermsOfUse())) {
            xmlw.writeStartElement("dc:rights");
            xmlw.writeCharacters(study.getOwner().getDownloadTermsOfUse());
            xmlw.writeEndElement();

        }
        if (!StringUtil.isEmpty(study.getConfidentialityDeclaration())) {
            xmlw.writeStartElement("dc:rights");
            xmlw.writeCharacters(study.getConfidentialityDeclaration());
            xmlw.writeEndElement();


        }
        if (!StringUtil.isEmpty(study.getSpecialPermissions())) {
            xmlw.writeStartElement("dc:rights");
            xmlw.writeCharacters(study.getSpecialPermissions());
            xmlw.writeEndElement();

        }
        if (!StringUtil.isEmpty(study.getRestrictions())) {
            xmlw.writeStartElement("dc:rights");
            xmlw.writeCharacters(study.getRestrictions());
            xmlw.writeEndElement();
        }
        if (!StringUtil.isEmpty(study.getContact())) {
            xmlw.writeStartElement("dc:rights");
            xmlw.writeCharacters(study.getContact());
            xmlw.writeEndElement();
        }
        if (!StringUtil.isEmpty(study.getCitationRequirements())) {
            xmlw.writeStartElement("dc:rights");
            xmlw.writeCharacters(study.getCitationRequirements());
            xmlw.writeEndElement();
        }
        if (!StringUtil.isEmpty(study.getDepositorRequirements())) {
            xmlw.writeStartElement("dc:rights");
            xmlw.writeCharacters(study.getDepositorRequirements());
            xmlw.writeEndElement();
        }
        if (!StringUtil.isEmpty(study.getConditions())) {
            xmlw.writeStartElement("dc:rights");
            xmlw.writeCharacters(study.getConditions());
            xmlw.writeEndElement();
        }
        if (!StringUtil.isEmpty(study.getDisclaimer())) {
            xmlw.writeStartElement("dc:rights");
            xmlw.writeCharacters(study.getDisclaimer());
            xmlw.writeEndElement();
        }
    }
    
    private void writeCoverage(XMLStreamWriter xmlw, Study study) throws XMLStreamException {
        // Time Period Covered
        String elementText=null;
        if (!StringUtil.isEmpty(study.getTimePeriodCoveredStart()) ||!StringUtil.isEmpty(study.getTimePeriodCoveredEnd()) ) {      
            xmlw.writeStartElement("dc:coverage");
            elementText ="Time Period Covered: ";
             if (!StringUtil.isEmpty(study.getTimePeriodCoveredStart())) {
                  elementText+=study.getTimePeriodCoveredStart();
             }
             if (!StringUtil.isEmpty(study.getTimePeriodCoveredEnd())) {
                  if (!StringUtil.isEmpty(study.getTimePeriodCoveredStart())) {
                      elementText+=" - ";
                  }
                  elementText+=study.getTimePeriodCoveredEnd();                
             }
             xmlw.writeCharacters(elementText);
             xmlw.writeEndElement();
        }
 
        // Date Of Collection
        if (!StringUtil.isEmpty(study.getDateOfCollectionStart()) ||!StringUtil.isEmpty(study.getDateOfCollectionEnd()) ) {      
            xmlw.writeStartElement("dc:coverage");
            elementText ="Date of Collection: ";
             if (!StringUtil.isEmpty(study.getDateOfCollectionStart())) {
                  elementText+=study.getDateOfCollectionStart();
             }
             if (!StringUtil.isEmpty(study.getDateOfCollectionEnd())) {
                  if (!StringUtil.isEmpty(study.getDateOfCollectionStart())) {
                      elementText+=" - ";
                  }
                  elementText+=study.getDateOfCollectionEnd();                
             }
             xmlw.writeCharacters(elementText);
             xmlw.writeEndElement();
        }
        
        //Country/Nation
        if (!StringUtil.isEmpty(study.getCountry())) {
            xmlw.writeStartElement("dc:coverage");
            xmlw.writeCharacters("Country/Nation: "+study.getCountry());
            xmlw.writeEndElement();
        }
       
       // Geographic Data 
       if (!StringUtil.isEmpty(study.getGeographicCoverage())) {
            xmlw.writeStartElement("dc:coverage");
            xmlw.writeCharacters("Geographic Coverage: "+study.getGeographicCoverage());
            xmlw.writeEndElement();
       }
        if (!StringUtil.isEmpty(study.getGeographicUnit())) {
            xmlw.writeStartElement("dc:coverage");
            xmlw.writeCharacters("Geographic Unit: "+study.getGeographicUnit());
            xmlw.writeEndElement();
       }
       
       for (StudyGeoBounding geoBounding : study.getStudyGeoBoundings()) {
             xmlw.writeStartElement("dc:coverage");
             xmlw.writeCharacters("Geographic Bounding: " + geoBounding );
             xmlw.writeEndElement();
       }
      
       
        
    }

    }
