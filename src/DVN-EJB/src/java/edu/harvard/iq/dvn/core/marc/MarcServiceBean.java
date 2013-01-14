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
 * DDI20ServiceBean.java
 *
 * Created on November 9, 2006, 4:59 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.marc;

import edu.harvard.iq.dvn.core.study.Metadata;
import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyAbstract;
import edu.harvard.iq.dvn.core.study.StudyOtherId;
import edu.harvard.iq.dvn.core.study.StudyTopicClass;
import edu.harvard.iq.dvn.core.util.PropertyUtil;
import edu.harvard.iq.dvn.core.util.StringUtil;

import java.io.IOException;
import java.io.OutputStream;

import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.marc4j.MarcStreamWriter;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;

/**
 *
 * @author ekraffmiller
 */
@Stateless
public class MarcServiceBean implements MarcServiceLocal {

    private static final Logger logger = Logger.getLogger("edu.harvard.iq.dvn.core.marc.MarcServiceBean");

    /** Creates a new instance of DDI20ServiceBean */
    public MarcServiceBean() {
    }

    public boolean isXmlFormat() {
        return false;
    }
    /**
     *
     * @param study - study that we are exporting
     * @param out
     * @throws IOException
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void exportStudy(Study study, OutputStream out) throws IOException {
         if (study.getReleasedVersion() == null) {
            throw new IllegalArgumentException("Study does not have released version, study.id = " + study.getId());
        }
        Metadata metadata = study.getReleasedVersion().getMetadata();

        MarcFactory factory = MarcFactory.newInstance();
        Record record = factory.newRecord();


        DataField title = factory.newDataField("245", '0', ' ');
        title.addSubfield(factory.newSubfield('a', metadata.getTitle()));
        if (!StringUtil.isEmpty(metadata.getAuthorsStr())) {
            title.addSubfield(factory.newSubfield('c', metadata.getAuthorsStr()));
        }
        if (!StringUtil.isEmpty(metadata.getDistributionDate())) {
            title.addSubfield(factory.newSubfield('s', metadata.getDistributionDate()));
        }
        record.addVariableField(title);

        DataField globalId = factory.newDataField("440", ' ', ' ');
        globalId.addSubfield(factory.newSubfield('v', study.getGlobalId()));
        record.addVariableField(globalId);


        for (StudyOtherId studyOtherId : metadata.getStudyOtherIds()) {
            DataField otherId = factory.newDataField("440", ' ', ' ');
            otherId.addSubfield(factory.newSubfield('v', studyOtherId.getOtherId()));
            record.addVariableField(otherId);
        }
        for (StudyAbstract studyAbstract : metadata.getStudyAbstracts()) {
            DataField abstractField = factory.newDataField("520", ' ', ' ');
            abstractField.addSubfield(factory.newSubfield('a', studyAbstract.getText()));
            record.addVariableField(abstractField);
        }

        DataField handle = factory.newDataField("856", ' ', ' ');
        handle.addSubfield(factory.newSubfield('u', study.getHandleURL()));
        record.addVariableField(handle);

        DataField dataverseUrl = factory.newDataField("535", ' ', ' ');
        dataverseUrl.addSubfield(factory.newSubfield('d', "http://" + PropertyUtil.getHostUrl() + "/dvn/study?globalId=" + study.getGlobalId()));
        record.addVariableField(dataverseUrl);
        
        for (StudyTopicClass studyTopicClass : metadata.getStudyTopicClasses()) {
            DataField topicClass = factory.newDataField("650", ' ', ' ');
            topicClass.addSubfield(factory.newSubfield('a', studyTopicClass.getValue()));
            record.addVariableField(topicClass);

        }
        MarcStreamWriter writer = new MarcStreamWriter(out);

        writer.write(record);

        out.flush();
    }
    
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void exportStudy(Study s, OutputStream os, String xpathExclude, String xpathInclude) throws IOException {
        throw new IllegalArgumentException("Partial export not supported for MARC.");
    }
}
