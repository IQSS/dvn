/*
 * VDCContextListener.java
 *
 * Created on February 6, 2007, 10:15 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.util;

import java.util.Iterator;
import javax.faces.FactoryFinder;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import com.sun.faces.lifecycle.LifecycleFactoryImpl;
import com.sun.faces.context.FacesContextFactoryImpl;

/**
 *
 * @author wbossons
 */
public class VDCContextListener implements ServletContextListener {
    
    /** Creates a new instance of VDCContextListener */
    public VDCContextListener() {
    }
    
    public void contextInitialized(ServletContextEvent servletcontextevent){
        System.out.println("the context is initialized");
        
    }
    
    public void contextDestroyed(ServletContextEvent servletcontextevent) {
        System.out.println("the context is destroyed");
    }
}
