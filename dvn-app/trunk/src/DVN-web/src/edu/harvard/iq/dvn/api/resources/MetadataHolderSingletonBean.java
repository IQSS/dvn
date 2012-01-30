
package edu.harvard.iq.dvn.api.resources;

import edu.harvard.iq.dvn.api.entities.MetadataInstance;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.ejb.Singleton;
import javax.ejb.EJB;

import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyVersion;


/**
 *
 * @author leonidandreev
 */
@Singleton
public class MetadataHolderSingletonBean {
    @EJB private StudyServiceLocal studyService;

    private LinkedList<MetadataInstance> list = new LinkedList<MetadataInstance>();
    private int maxM = 10;

    int currentId = 0;

    public MetadataHolderSingletonBean() {
        // initial content
        addMetadata("msg0", new Date(0));
        addMetadata("msg1", new Date(1000));
        addMetadata("msg2", new Date(2000));
    }

    public List<MetadataInstance> getMetadata() {
        List<MetadataInstance> l = new LinkedList<MetadataInstance>();

        int index = 0;

        while(index < list.size() && index < maxM) {
            l.add(list.get(index));
            index++;
        }

        return l;
    }

    private synchronized int getNewId() {
        return currentId++;
    }
    

    public synchronized MetadataInstance addMetadata(String msg) {
        return addMetadata(msg, new Date());
    }

    private synchronized MetadataInstance addMetadata(String msg, Date date) {
        //MetadataInstance m = new edu.harvard.iq.dvn.api.entities.MetadataInstance(date, msg, this.getNewId());
        MetadataInstance m = null; 

        list.addFirst(m);

        return m;
    }
    
    // Looks up Metadata Instance by Global ID:
    public MetadataInstance getMetadata(String globalId, String formatType, Long versionNumber) {
        MetadataInstance m = null; 
        StudyVersion sv = null; 
        Long studyId = null; 
        
        if (globalId != null) {
            try {
                sv = studyService.getStudyVersion(globalId, versionNumber);
                if (sv != null) {
                    studyId = sv.getStudy().getId();
                    m = new MetadataInstance (globalId, formatType); 
                    // local database id:
                    m.setStudyId(studyId);
                    return m; 
                } 
            } catch (IllegalArgumentException ex) {
                return null; 
                // We don't need to do anything special here -- we simply
                // return null, and jersey app will cook a proper 404 
                // response. 
            }
        } 
        
        return null; 
    }
    
    // Looks up Metadata Instance by Local (database) ID:
    public MetadataInstance getMetadata(Long studyId, String formatType, Long versionNumber) {
        MetadataInstance m = null; 
        StudyVersion sv = null; 
        String globalId = null; 
        
        if (studyId != null) {
            try {
                sv = studyService.getStudyVersion(studyId, versionNumber);
                if (sv != null) {
                    globalId = sv.getStudy().getGlobalId();
                    m = new MetadataInstance (globalId, formatType);
                    m.setGlobalStudyId(globalId);
                
                    return m; 
                } 
            } catch (java.lang.IllegalArgumentException ex) {
                return null; 
                // We don't need to do anything special here -- we simply
                // return null, and jersey app will cook a proper 404 
                // response. 
            }
        } 

        return null; 
 
    }

    public synchronized boolean deleteMetadata(int uniqueId) {
        int index = 0;

        while(index < list.size()) {
            //if(list.get(index).getUniqueId() == uniqueId) {
            //    list.remove(index);
            //    return true;
            //}
            index++;
        }

        return false;
    }
}
