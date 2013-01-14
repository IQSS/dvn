package edu.harvard.iq.dvn.core.web.study;


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
   Version 3.1.
*/
import edu.harvard.iq.dvn.core.study.*;
import edu.harvard.iq.dvn.core.visualization.VisualizationServiceLocal;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
/**
 *
 * @author skraffmiller
 */
@EJB(name="visualizationService", beanInterface=edu.harvard.iq.dvn.core.visualization.VisualizationServiceBean.class)
@ViewScoped
@Named("MigrateDataExplorationPage")
public class MigrateDataExplorationPage extends VDCBaseBean implements java.io.Serializable {
    @EJB
    VisualizationServiceLocal visualizationService;
    @EJB
    VariableServiceLocal varService;

    private Long studyId;
    private Study study;
    
    private Long studyOldFileId;

    private Long studyNewFileId;

    private EditStudyFilesService editStudyFilesService;
    private StudyVersion studyVersion;
    private List <SelectItem> studyOldFileIdSelectItems = new ArrayList();

    private List <SelectItem> studyNewFileIdSelectItems = new ArrayList();
    
    public void init() {
        super.init();
        if (studyId == null) {
            studyId = new Long(getVDCRequestBean().getRequestParam("studyId"));
        }

        try {
            Context ctx = new InitialContext();
            editStudyFilesService = (EditStudyFilesService) ctx.lookup("java:comp/env/editStudyFiles");

        } catch (NamingException e) {
            e.printStackTrace();
            FacesContext context = FacesContext.getCurrentInstance();
            FacesMessage errMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null);
            context.addMessage(null, errMessage);

        }
        if (getStudyId() != null) {
            editStudyFilesService.setStudyVersion(studyId);
            study = editStudyFilesService.getStudyVersion().getStudy();
            studyVersion = study.getEditVersion();
            setFiles(editStudyFilesService.getCurrentFiles());
        } else {
            FacesContext context = FacesContext.getCurrentInstance();
            FacesMessage errMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, "The Study ID is null", null);
            context.addMessage(null, errMessage);
            //Should not get here.
            //Must always be in a study to get to this page.
        }

        studyOldFileIdSelectItems = loadStudyFileSelectItems(true);
        studyNewFileIdSelectItems = loadStudyFileSelectItems(false);
    }
    
    public void runDataMigration(){
        if (studyOldFileId.intValue()> 0 && studyNewFileId.intValue() > 0){
             visualizationService.migrateVisualization(studyOldFileId, studyNewFileId);
             getVDCRenderBean().getFlash().put("successMessage", "Successfully migrated data exploration.");
        } else {
             getVDCRenderBean().getFlash().put("warningMessage", "Please select source and target files for migration.");
        }       
    }

    public String exit() {        
        Long redirectVersionNumber = new Long(0);
        if (studyVersion.getId() == null) {
            redirectVersionNumber = study.getReleasedVersion().getVersionNumber();
        } else {
            redirectVersionNumber = studyVersion.getVersionNumber();
        }
        return "/study/StudyPage?faces-redirect=true&studyId=" + study.getId() + "&versionNumber=" + redirectVersionNumber + "&tab=files&vdcId=" + getVDCRequestBean().getCurrentVDCId();
    } 
    
    public List<SelectItem> loadStudyFileSelectItems(boolean hasExploration) {
        List selectItems = new ArrayList<SelectItem>();
        if (hasExploration){
            selectItems.add(new SelectItem(0, "Select a Source File"));
        } else {
            selectItems.add(new SelectItem(0, "Select a Target File"));
        }

        for (FileMetadata fileMetaData : studyVersion.getFileMetadatas()) {
            
            if (fileMetaData.getStudyFile().isSubsettable()  && hasExploration && fileMetaData.getStudyFile().getDataTables().get(0).isVisualizationEnabled()  ) {
                selectItems.add(new SelectItem(fileMetaData.getStudyFile().getId(), fileMetaData.getStudyFile().getFileName()));
            }
            
            if (fileMetaData.getStudyFile().isSubsettable()  && !hasExploration && !fileMetaData.getStudyFile().getDataTables().get(0).isVisualizationEnabled()  ) {
                selectItems.add(new SelectItem(fileMetaData.getStudyFile().getId(), fileMetaData.getStudyFile().getFileName()));
            }
        }
        return selectItems;
    }
    
    public void setStudyId(Long studyId) {
        this.studyId = studyId;
    }
    
    public Long getStudyId() {
        return studyId;
    }
    
    private List files;

    public List getFiles() {
        return files;
    }
    
    public void setFiles(List files) {
        this.files = files;
    }
    
    
    public List<SelectItem> getStudyNewFileIdSelectItems() {
        return studyNewFileIdSelectItems;
    }

    public void setStudyNewFileIdSelectItems(List<SelectItem> studyNewFileIdSelectItems) {
        this.studyNewFileIdSelectItems = studyNewFileIdSelectItems;
    }

    public List<SelectItem> getStudyOldFileIdSelectItems() {
        return studyOldFileIdSelectItems;
    }

    public void setStudyOldFileIdSelectItems(List<SelectItem> studyOldFileIdSelectItems) {
        this.studyOldFileIdSelectItems = studyOldFileIdSelectItems;
    }
    
    
    public Long getStudyNewFileId() {
        return studyNewFileId;
    }

    public void setStudyNewFileId(Long studyNewFileId) {
        this.studyNewFileId = studyNewFileId;
    }

    public Long getStudyOldFileId() {
        return studyOldFileId;
    }

    public void setStudyOldFileId(Long studyOldFileId) {
        this.studyOldFileId = studyOldFileId;
    }

}
