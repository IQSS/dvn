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
package edu.harvard.hmdc.vdcnet.marc;

import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.study.StudyAbstract;
import edu.harvard.hmdc.vdcnet.study.StudyOtherId;
import edu.harvard.hmdc.vdcnet.study.StudyTopicClass;
import edu.harvard.hmdc.vdcnet.util.PropertyUtil;
import edu.harvard.hmdc.vdcnet.util.StringUtil;

import java.io.IOException;
import java.io.OutputStream;

import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import javax.xml.bind.JAXBException;
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

    private static final Logger logger = Logger.getLogger("edu.harvard.hmdc.vdcnet.marc.MarcServiceBean");

    /** Creates a new instance of DDI20ServiceBean */
    public MarcServiceBean() {
    }

    public boolean isXmlFormat() {
        return false;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void exportStudy(Study study, OutputStream out) throws IOException {

        MarcFactory factory = MarcFactory.newInstance();
        Record record = factory.newRecord();


        DataField title = factory.newDataField("245", '0', ' ');
        title.addSubfield(factory.newSubfield('a', study.getTitle()));
        if (!StringUtil.isEmpty(study.getAuthorsStr())) {
            title.addSubfield(factory.newSubfield('c', study.getAuthorsStr()));
        }
        if (!StringUtil.isEmpty(study.getDistributionDate())) {
            title.addSubfield(factory.newSubfield('s', study.getDistributionDate()));
        }
        record.addVariableField(title);

        DataField globalId = factory.newDataField("440", ' ', ' ');
        globalId.addSubfield(factory.newSubfield('v', study.getGlobalId()));
        record.addVariableField(globalId);


        for (StudyOtherId studyOtherId : study.getStudyOtherIds()) {
            DataField otherId = factory.newDataField("440", ' ', ' ');
            otherId.addSubfield(factory.newSubfield('v', studyOtherId.getOtherId()));
            record.addVariableField(otherId);
        }
        for (StudyAbstract studyAbstract : study.getStudyAbstracts()) {
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
        
        for (StudyTopicClass studyTopicClass : study.getStudyTopicClasses()) {
            DataField topicClass = factory.newDataField("650", ' ', ' ');
            topicClass.addSubfield(factory.newSubfield('a', studyTopicClass.getValue()));
            record.addVariableField(topicClass);

        }
        MarcStreamWriter writer = new MarcStreamWriter(out);

        writer.write(record);

        out.flush();
    }
}
