/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web.study;

import edu.harvard.hmdc.vdcnet.study.StudyServiceLocal;
import edu.harvard.hmdc.vdcnet.web.SortableList;
import edu.harvard.hmdc.vdcnet.web.common.LoginBean;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;





/**
 *
 * @author Ellen Kraffmiller
 */
public class ManageStudiesList extends SortableList { 
    private @EJB StudyServiceLocal studyService;
    private List<StudyUI> studyUIList;
    
    // dataTable Columns to sort by:
    private static final String ID_COLUMN= "id";
    private static final String TITLE_COLUMN = "title";
    private static final String CREATOR_COLUMN = "creator";
    private static final String DATE_CREATED_COLUMN = "dateCreated";
    private static final String STATUS_COLUMN = "status";
    private static final String TEMPLATE_COLUMN = "template";
    private static final String DATE_UPDATED_COLUMN = "lastUpdated";
    
 
    private Long vdcId;
    private LoginBean loginBean;
    
    public ManageStudiesList() {
        super(ID_COLUMN);
   
    }
    
    protected void sort() {
            String orderBy=null;
            if (sortColumnName == null) {
                return;
            }
            
            if (sortColumnName.equals(TITLE_COLUMN)) {
                orderBy = "metadata.title";
            } else if (sortColumnName.equals(ID_COLUMN)) {
                orderBy="studyId";
            } else if (sortColumnName.equals(CREATOR_COLUMN)){
                orderBy="creator.userName";
            } else if (sortColumnName.equals(DATE_CREATED_COLUMN)) {
                orderBy="createTime";
            } else if (sortColumnName.equals(STATUS_COLUMN)) {
                orderBy="reviewState.name";
            } else if (sortColumnName.equals(TEMPLATE_COLUMN)) {
                orderBy="template.name";
            } else if (sortColumnName.equals(DATE_UPDATED_COLUMN)) {
                orderBy="lastUpdateTime";
            } else {
                throw new RuntimeException("Unknown sortColumnName: "+sortColumnName);
            }
            List studyIds =null;
            if (loginBean!=null && loginBean.isContributor()) {
                studyIds = studyService.getDvOrderedStudyIdsByCreator(vdcId, loginBean.getUser().getId(), orderBy, ascending);
            } else {
                studyIds = studyService.getDvOrderedStudyIds(vdcId, orderBy, ascending);
            }
            studyUIList = new ArrayList<StudyUI>();
            for (Object studyId: studyIds) {
                studyUIList.add(new StudyUI((Long)studyId));
            }
}

    public LoginBean getLoginBean() {
        return loginBean;
    }

    public void setLoginBean(LoginBean loginBean) {
        this.loginBean = loginBean;
    }
    public boolean isDefaultAscending(String columnName) {
        return true;
        
    }
    
    public String getIdColumn() { return ID_COLUMN;}
    public String getTitleColumn() {return TITLE_COLUMN; }
    public String getCreatorColumn() {return CREATOR_COLUMN; }
    public String getCreatedColumn() {return DATE_CREATED_COLUMN; }
    public String getStatusColumn() { return STATUS_COLUMN; }
    public String getTemplateColumn() {return TEMPLATE_COLUMN; }
    public String getUpdatedColumn() { return DATE_UPDATED_COLUMN; }

    public List<StudyUI> getStudyUIList() {     
        System.out.println("getting StudyUIList");
        if (studyUIList==null) {
          
            sort();
        } else {
            checkSort();
        }
        return studyUIList;
    }

    public void setStudyUIList(List<StudyUI> studyUIList) {
        this.studyUIList = studyUIList;
    }

    public Long getVdcId() {
        return vdcId;
    }

    public void setVdcId(Long vdcId) {
        this.vdcId = vdcId;
    }
    

}
