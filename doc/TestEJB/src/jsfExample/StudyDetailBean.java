/*
 * StudyDetailBean.java
 *
 * Created on September 12, 2006, 5:28 PM
 *
 */

package jsfExample;

import edu.harvard.hmdc.vdcnet.study.StudyServiceLocal;
import edu.harvard.hmdc.vdcnet.study.Study;
import java.util.Collection;
import javax.ejb.EJB;

/**
 *
 * @author Ellen Kraffmiller
 */
public class StudyDetailBean extends VDCBaseBean {
    @EJB StudyServiceLocal studyService;
    Study study;
    
    /** Creates a new instance of StudyDetailBean */
    public StudyDetailBean() {
    }
    
    public Study getStudy() {
        return study;
    }
    
    public void setStudy(Study study) {
        this.study=study;
    }

    public boolean getIsNew() {
        return study.getId()==null;
    }
    
    public String add() {
        studyService.addStudy(study);
        return "success";
    }
    
    public String update() {
        studyService.updateStudy(study);     
        return "success";
    
    }
    
     public String delete() {
        studyService.deleteStudy(study.getId());     
        return "success";
    
    }
    
}
