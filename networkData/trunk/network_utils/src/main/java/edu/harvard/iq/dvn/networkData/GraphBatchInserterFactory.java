package edu.harvard.iq.dvn.networkData;

import java.sql.SQLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.MalformedURLException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.commons.io.FileUtils;


public class GraphBatchInserterFactory{
    private RestrictedURLClassLoader rucl;
    public GraphBatchInserterFactory(String libPath) throws IOException{
        rucl = new RestrictedURLClassLoader(libPath,
                    GraphBatchInserterFactory.class.getClassLoader());
    }

    public GraphBatchInserterFactory(RestrictedURLClassLoader cl){
        rucl = cl;
    }

    public GraphBatchInserter newInstance(String neoDb, String sqlDb, String insertProps, String neoProps) throws ClassNotFoundException{
        Class gbiimplclass = Class.forName("edu.harvard.iq.dvn.networkData.GraphBatchInserterImpl", true, rucl);
        try{
            return (GraphBatchInserter)gbiimplclass.getConstructors()[0].
                        newInstance(neoDb, sqlDb, insertProps, neoProps);
        } catch(Exception e){
            System.out.println(e);
            return null;
        }
    }
}

