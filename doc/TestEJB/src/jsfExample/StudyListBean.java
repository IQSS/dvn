/*
 * StudyListBean.java
 *
 * Created on September 12, 2006, 4:21 PM
 *
 */

package jsfExample;
import edu.harvard.hmdc.vdcnet.study.StudyServiceLocal;
import edu.harvard.hmdc.vdcnet.study.Study;
import java.util.Collection;
import javax.ejb.EJB;
import javax.faces.component.UIData;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

/**
 *
 * @author Ellen Kraffmiller
 */
public class StudyListBean extends VDCBaseBean{
    @EJB StudyServiceLocal studyService;
    private javax.faces.component.UIData studyTable;
    
    
    /** Creates a new instance of StudyListBean */
    public StudyListBean() {
    }
    
    /**
     * Holds value of property studies.
     */
    private Collection<Study> studies;
    
    /**
     * Getter for property studies.
     * @return Value of property studies.
     */
    public Collection<Study> getStudies() {
        
        if (this.studies==null) {
            studies = studyService.getStudies();
        }
        System.out.println("Getting studies, "+studies);
        return this.studies;
    }
    
    /**
     * Setter for property studies.
     * @param studies New value of property studies.
     */
    public void setStudies(Collection<Study> studies) {
        this.studies = studies;
    }
    
    public String goToDetails() {
        getSelectedStudy();
        return "success";
    }
    
    public String goToUpdate() {
        getSelectedStudy();
        return "success";
    }
    public String goToAdd() {
        getVDCSessionBean().setCurrentStudy( new Study());
        return "success";
    }
    
    public String goToDelete() {
        getSelectedStudy();
        return "success";
    }
    
    private void getSelectedStudy() {
        Study currentStudy = studyService.getStudy(getSelectedId());
        getVDCSessionBean().setCurrentStudy(currentStudy);
        
    }
    
    private Long getSelectedId() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        
        UIViewRoot root = facesContext.getViewRoot();
        UIData table = (UIData)root.findComponent("studyListForm").findComponent("dataTable1");
        Study study = (Study) table.getRowData();
        return study.getId();
    }
    
    public javax.faces.component.UIData getStudyTable() {
        return studyTable;
    }
    
    public void setStudyTable(javax.faces.component.UIData studyTable) {
        this.studyTable = studyTable;
        
    }
    
}
