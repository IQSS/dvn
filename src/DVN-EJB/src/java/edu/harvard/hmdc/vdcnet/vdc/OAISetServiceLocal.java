/*
 * OAISetServiceLocal.java
 *
 * Created on Oct 2, 2007, 5:13:53 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.vdc;

import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Ellen Kraffmiller
 */
@Local
public interface OAISetServiceLocal {
   public OAISet findBySpec(String spec);   
   public List findAll();
}
