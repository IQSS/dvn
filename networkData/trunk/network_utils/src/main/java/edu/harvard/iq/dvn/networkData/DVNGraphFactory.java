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
        rucl = new RestrictedURLClassLoader(libPath,
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

