/*
 * ReviewStateServiceLocal.java
 *
 * Created on November 17, 2006, 2:15 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.study;

import edu.harvard.hmdc.vdcnet.vdc.ReviewState;
import javax.ejb.Local;

/**
 *
 * @author Ellen Kraffmiller
 */
@Local
public interface ReviewStateServiceLocal {
    public static final String REVIEW_STATE_NEW = "New";
    public static final String REVIEW_STATE_IN_REVIEW = "In Review";
    public static final String REVIEW_STATE_RELEASED = "Released";
    
   public ReviewState findByName(String name);
    
}
