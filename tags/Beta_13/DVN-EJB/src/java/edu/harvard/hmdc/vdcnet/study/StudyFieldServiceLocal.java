
package edu.harvard.hmdc.vdcnet.study;

import java.util.List;
import javax.ejb.Local;


/**
 * This is the business interface for StudyFieldService enterprise bean.
 */
@Local
public interface StudyFieldServiceLocal {
    public List findAll();
    public StudyField findByName(String name);
    public StudyField findById(Long id);    
    public List findAdvSearchDefault();
}
