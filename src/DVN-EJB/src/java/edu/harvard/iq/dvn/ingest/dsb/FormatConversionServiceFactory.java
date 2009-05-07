/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.ingest.dsb;

/**
 *
 * @author asone
 */
public class FormatConversionServiceFactory {
    /**  */
    public static String FORMAT_CONVERSION_SERVICE_DEFAULT = 
        "edu.harvard.iq.dvn.ingest.dsb.impl.DvnFormatConversionServiceImpl";

    /**
     * 
     *
     * @param     
     * @return    
     */
    public static FormatConversionService getServiceInstance(String implClassName)
        throws ClassNotFoundException, 
            InstantiationException, IllegalAccessException{
            if (implClassName == null){
                // default fallout class
                return (FormatConversionService)
                    Class.forName("edu.harvard.iq.dvn.ingest.dsb.impl.MockDvnFormatConversionServiceImpl").newInstance();
            } else {
                return (FormatConversionService)
                    Class.forName(implClassName).newInstance();
            }
    }

}
