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
import edu.harvard.hmdc.vdcnet.study.StudyOtherId;
import edu.harvard.hmdc.vdcnet.study.StudyProducer;
import edu.harvard.hmdc.vdcnet.study.StudyTopicClass;
import edu.harvard.hmdc.vdcnet.util.StringUtil;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.xml.bind.JAXBException;

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
    public void exportStudy(Study study, OutputStream out) throws IOException, JAXBException {

        OutputStreamWriter writer = new OutputStreamWriter(out);
        writer.write("<oai_dc:dc xmlns:oai_dc=\"http://www.openarchives.org/OAI/2.0/oai_dc/\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd\">");
   
        // Contributor
        for (StudyDistributor distributor : study.getStudyDistributors()) {
            writer.write("<dc:contributor>");
            writer.write(distributor.getName());
            writer.write("</dc:contributor>");
        }
        // Coverage
        if (!StringUtil.isEmpty(study.getTimePeriodCoveredStart())) {
            writer.write("<dc:coverage>" + study.getTimePeriodCoveredStart() + "</dc:coverage>");
        }
        if (!StringUtil.isEmpty(study.getTimePeriodCoveredEnd())) {
            writer.write("<dc:coverage>" + study.getTimePeriodCoveredEnd() + "</dc:coverage>");
        }
        if (!StringUtil.isEmpty(study.getCountry())) {
            writer.write("<dc:coverage>" + study.getCountry() + "</dc:coverage>");
        }
        if (!StringUtil.isEmpty(study.getGeographicCoverage())) {
            writer.write("<dc:coverage>" + study.getGeographicCoverage() + "</dc:coverage>");
        }
        for (StudyGeoBounding geoBounding : study.getStudyGeoBoundings()) {
            writer.write("<dc:coverage>" + geoBounding + "</dc:coverage>");
        }
       
        // Creator
        for (StudyAuthor author : study.getStudyAuthors()) {
            writer.write("<dc:creator>");
            writer.write(author.getName());
            writer.write("</dc:creator>");
        }
        // Date
        if (!StringUtil.isEmpty(study.getProductionDate())) {
            writer.write("<dc:date>" + study.getProductionDate() + "</dc:date>");
        }
        // Description
        for (StudyAbstract studyAbstract : study.getStudyAbstracts()) {
            writer.write("<dc:description>");
            writer.write(studyAbstract.getText());
            writer.write("</dc:description>");
        }
       // Identifier
        writer.write("<dc:identifier>" + study.getGlobalId() + "</dc:identifier>");
   
        
        // Publisher
        for (StudyProducer producer : study.getStudyProducers()) {
            writer.write("<dc:publisher>");
            writer.write(producer.getName());
            writer.write("</dc:publisher>");
        }
        
        // Relation
        if (!StringUtil.isEmpty(study.getReplicationFor())) {
            writer.write("<dc:relation>" + study.getReplicationFor() + "</dc:relation>");  
        }
 
        // Rights
        if (study.getOwner().isDownloadTermsOfUseEnabled() && !StringUtil.isEmpty(study.getOwner().getDownloadTermsOfUse())) {
            writer.write("<dc:rights>" + study.getOwner().getDownloadTermsOfUse()+"</dc:rights>");
        }
        if ( !StringUtil.isEmpty(study.getConfidentialityDeclaration())) {
            writer.write("<dc:rights>" + study.getConfidentialityDeclaration() + "</dc:rights>");
        }
        if ( !StringUtil.isEmpty(study.getSpecialPermissions())) {
            writer.write("<dc:rights>" + study.getSpecialPermissions() + "</dc:rights>");
        }
        if ( !StringUtil.isEmpty(study.getRestrictions())) {
            writer.write("<dc:rights>" + study.getRestrictions() + "</dc:rights>");
        }
        if ( !StringUtil.isEmpty(study.getContact())) {
            writer.write("<dc:rights>" + study.getContact() + "</dc:rights>");
        }
        if ( !StringUtil.isEmpty(study.getCitationRequirements())) {
            writer.write("<dc:rights>" + study.getCitationRequirements() + "</dc:rights>");
        }
        if ( !StringUtil.isEmpty(study.getDepositorRequirements())) {
            writer.write("<dc:rights>" + study.getDepositorRequirements() + "</dc:rights>");
        }
        if ( !StringUtil.isEmpty(study.getConditions())) {
            writer.write("<dc:rights>" + study.getConditions() + "</dc:rights>");
        }
        if ( !StringUtil.isEmpty(study.getDisclaimer())) {
            writer.write("<dc:rights>" + study.getDisclaimer() + "</dc:rights>");
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
        
        // Title
        writer.write("<dc:title>" + study.getTitle() + "</dc:title>");
        
        writer.write("</oai_dc:dc>");
        writer.flush();
    }
    }
