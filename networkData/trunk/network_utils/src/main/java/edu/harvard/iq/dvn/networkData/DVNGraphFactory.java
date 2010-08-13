package edu.harvard.iq.dvn.networkData;

import java.sql.SQLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.MalformedURLException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.commons.io.FileUtils;


public class DVNGraphFactory{
    private RestrictedURLClassLoader rucl;
    public DVNGraphFactory(String libPath) throws IOException{
        File libDir = new File(libPath);
        URL[] urls = (URL[])FileUtils.toURLs(
                        (File[])FileUtils.listFiles(
                            libDir, new String[]{"jar"}, false).
                            toArray(new File[0]));

        rucl = new RestrictedURLClassLoader(urls,
                    DVNGraphFactory.class.getClassLoader());
    }

    public DVNGraphFactory(RestrictedURLClassLoader cl){
        rucl = cl;
    }

    public DVNGraph newInstance(String neoDb, String sqlDb, String neoProps) throws ClassNotFoundException{
        Class dvngraphimplclass = Class.forName("edu.harvard.iq.dvn.networkData.DVNGraphImpl", true, rucl);
        try{
            return (DVNGraph)dvngraphimplclass.getConstructors()[1].
                        newInstance(neoDb, sqlDb, neoProps);
        } catch(Exception e){
            System.out.println(e);
            return null;
        }
    }
}

