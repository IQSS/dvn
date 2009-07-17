/*
 * Dataverse Network - A web application to distribute, share and analyze quantitative data.
 * Copyright (C) 2009
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
 *  along with this program; if not, see http://www.gnu.org/licenses
 * or write to the Free Software Foundation,Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package edu.harvard.iq.dvn.ingest.org.thedata.statdataio.spi;

import java.util.*;
import java.util.logging.*;

/**
 * A singleton-registry class that provides classes with
 * the functionality of thread-safe singleton generation.
 * Coded after 
 * <a href="http://www.javaworld.com/javaworld/jw-04-2003/jw-0425-designpatterns.html>
 * Simply Singleton</a>.
 *
 * @author akio sone
 */
public class Registry {
    
   private static Logger dbgLog =
       Logger.getLogger(Registry.class.getPackage().getName());

    /**
     * for a thread-safe singleto implementation, use a static field.
     */
    public static Registry REGISTRY = new Registry();

    private static Map<String, Object> map = new HashMap<String, Object>();

    // avoids instantiations from the outside.
    private Registry() {
       dbgLog.fine("Registry constructor is called");
    }

   /**
    * Returns a singleton of a given class by refection 
    * 
    * @param classname the name of a class to be instantiated.
    * @return the singleton of the supplied class.
    */
    public static synchronized Object getInstance(String classname) {

        Object singleton = map.get(classname);

        if(singleton != null) {
            dbgLog.fine("Registry.getInstance(): already-registered case");
            return singleton;
        }
        try {
            singleton = Class.forName(classname).newInstance();
        } catch(ClassNotFoundException cnf) {
            cnf.printStackTrace();
        } catch(InstantiationException ie) {
            ie.printStackTrace();
        } catch(IllegalAccessException ia) {
            ia.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }
        map.put(classname, singleton);
        dbgLog.fine("Registry.getInstance(): not-registered case");
        return singleton;
   }

   /**
    * Tells whether or not the given class is already registered in the
    * <code>Registry REGISTRY</code>.
    * 
    * @param classname the name of a class for the testing
    * @return true if the class is already registered.
    */
   public static boolean isThisClassRegistered(String classname){
        dbgLog.fine("********** within isThisClassRegistered **********");
        dbgLog.fine("classname to be checked="+classname);
        Object singleton = map.get(classname);
        if (singleton != null){
           return true;
        } else {
           return false;
        }
    }
}
