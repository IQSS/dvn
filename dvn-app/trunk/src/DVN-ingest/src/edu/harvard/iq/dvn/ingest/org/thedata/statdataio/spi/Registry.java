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
   Version 3.0.
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
