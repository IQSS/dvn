package edu.harvard.iq.dvn.networkData;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.MalformedURLException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import org.apache.commons.io.FileUtils;

public class RestrictedURLClassLoader extends URLClassLoader{
    private ClassLoader parent;
    private HashMap<String, Class> cache;
    private static final String[] jarNames = {
            "colt-1.2.0.jar",
            "commons-io-1.4.jar",
            "concurrent-1.3.4.jar",
            "junit-3.8.1.jar",
            "jung-algorithms-2.0.jar",
            "jung-api-2.0.jar",
            "jung-visualization-2.0.jar",
            "collections-generic-4.01.jar",
            "geronimo-jta_1.1_spec-1.1.1.jar",
            "lucene-core-2.9.2.jar",
            "neo4j-index-1.1.jar",
            "neo4j-kernel-1.1.jar",
            "neo4j-utils-1.1.jar",
            "sqlite-jdbc-3.6.16.jar",
            "nestedvm-1.0.jar",
            "network_utils-1.0-SNAPSHOT.jar"
        };

    public RestrictedURLClassLoader(URL[] urls, ClassLoader parent){
        super(urls, null);
        this.parent = parent;
    }

    public RestrictedURLClassLoader(String libPath, ClassLoader parent) {
        super(new URL[]{}, null);
        //File libDir = new File(libPath);
        /*URL[] urls = (URL[])FileUtils.toURLs(
                        (File[])FileUtils.listFiles(
                            libDir, new String[]{"jar"}, false).
                            toArray(new File[0]));*/
        if(!libPath.substring(libPath.length()-1).equals("/"))
            libPath = libPath + "/";

        try{
            for(int i = 0; i < jarNames.length; i++){
                //System.out.println("jar:file:"+libPath+jarNames[i]+"!/");
                this.addURL(new URL("jar:file:"+libPath+jarNames[i]+"!/"));
            }
        } catch (MalformedURLException e){
            System.out.println(e);
        }

        this.parent=parent;
        cache = new HashMap<String, Class>();
    }

    public Class loadClass(String name) throws ClassNotFoundException {
//        System.out.println(name);
        Class cls;
        if//(luceneFinder.matcher(name).matches())
          (!(name.equals("edu.harvard.iq.dvn.networkData.DVNGraph") ||
           name.equals("edu.harvard.iq.dvn.networkData.GraphBatchInserter") ||
           name.equals("org.sqlite.JDBC")))
            cls = super.loadClass(name);
        else
            cls = parent.loadClass(name);
        if(cls==null)
            throw new ClassNotFoundException(name);

        return cls;
    }
}

