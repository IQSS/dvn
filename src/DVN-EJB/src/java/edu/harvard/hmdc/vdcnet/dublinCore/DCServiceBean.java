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




import edu.harvard.hmdc.vdcnet.jaxb.ddi20.CodeBook;

import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.study.StudyAbstract;
import edu.harvard.hmdc.vdcnet.study.StudyAuthor;
import edu.harvard.hmdc.vdcnet.study.StudyFile;
import edu.harvard.hmdc.vdcnet.study.StudyGeoBounding;
import edu.harvard.hmdc.vdcnet.study.StudyKeyword;
import edu.harvard.hmdc.vdcnet.study.StudyOtherId;
import edu.harvard.hmdc.vdcnet.study.StudyProducer;
import edu.harvard.hmdc.vdcnet.study.StudyTopicClass;
import edu.harvard.hmdc.vdcnet.util.StringUtil;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.EJBs;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

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
   
    public boolean isXmlFormat() {return true;}
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public  void exportStudy(Study study,OutputStream out) throws IOException, JAXBException {
     
        OutputStreamWriter writer = new OutputStreamWriter(out);
        writer.write("<oai_dc:dc xmlns:oai_dc=\"http://www.openarchives.org/OAI/2.0/oai_dc/\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd\">");
        writer.write("<dc:title>" + study.getTitle() + "</dc:title>");

        // Creator
        for (StudyAuthor author : study.getStudyAuthors()) {
            writer.write("<dc:creator>");
            writer.write(author.getName());
            writer.write("</dc:creator>");
        }
      
        //Subject
       for (StudyKeyword keyword : study.getStudyKeywords()) {
            writer.write("<dc:subject>");
            writer.write(keyword.getValue());
            writer.write("</dc:subject>");
        }
       for (StudyTopicClass topicClass : study.getStudyTopicClasses()) {
            writer.write("<dc:subject>");
            writer.write(topicClass.getValue());
            writer.write("</dc:subject>");
        }
 
        // Description
        for (StudyAbstract studyAbstract: study.getStudyAbstracts()) {
            writer.write("<dc:description>");
            writer.write(studyAbstract.getText());
            writer.write("</dc:description>");
        }

        // Publisher
        for (StudyProducer producer : study.getStudyProducers()) {
            writer.write("<dc:publisher>");
            writer.write(producer.getName());
            writer.write("</dc:publisher>");
        }

        // Contributor
        for (StudyOtherId otherId: study.getStudyOtherIds()) {
            writer.write("<dc:contributor>");
            writer.write(otherId.getAgency() + " " + otherId.getOtherId());
            writer.write("</dc:contributor>");
        }

        // Date
        if (!StringUtil.isEmpty(study.getDistributionDate())) {
            writer.write("<dc:date>"+study.getDistributionDate()+"</dc:date>");
        }
        writer.write("<dc:identifier>" + study.getGlobalId() + "</dc:identifier>");

        // Coverage
        if (!StringUtil.isEmpty(study.getTimePeriodCoveredStart())) {
            writer.write("<dc:coverage>"+study.getTimePeriodCoveredStart()+"</dc:coverage>");
        }
        if (!StringUtil.isEmpty(study.getTimePeriodCoveredEnd())) {
            writer.write("<dc:coverage>"+study.getTimePeriodCoveredEnd()+"</dc:coverage>");
        }
        if (!StringUtil.isEmpty(study.getDateOfCollectionStart())) {
            writer.write("<dc:coverage>"+study.getDateOfCollectionStart()+"</dc:coverage>");
        }
        if (!StringUtil.isEmpty(study.getDateOfCollectionEnd())) {
            writer.write("<dc:coverage>"+study.getDateOfCollectionEnd()+"</dc:coverage>");
        }
        if (!StringUtil.isEmpty(study.getCountry())) {
            writer.write("<dc:coverage>"+study.getCountry()+"</dc:coverage>");
        }
        if (!StringUtil.isEmpty(study.getGeographicCoverage())) {
            writer.write("<dc:coverage>"+study.getGeographicCoverage()+"</dc:coverage>");
        }
        for (StudyGeoBounding geoBounding : study.getStudyGeoBoundings()) {
            writer.write("<dc:coverage>"+geoBounding+"</dc:coverage>");
        }
        writer.write("</oai_dc:dc>");
        writer.flush();
    }
    

 
}
