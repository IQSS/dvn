/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.text;
import java.io.File;

/**
 *
 * @author ekraffmiller
 */
public class ClusterUtil {
        public static String getDocRoot() {
            String docRoot = System.getProperty("text.documentRoot");
            if (docRoot==null) {
                throw new ClusterException("Error: JVM property 'text.documentRoot' is undefined.");
            }
            return docRoot;
        }

        public static File getSetDir(String setId) {
            return new File(getDocRoot(),setId );
        }

}
