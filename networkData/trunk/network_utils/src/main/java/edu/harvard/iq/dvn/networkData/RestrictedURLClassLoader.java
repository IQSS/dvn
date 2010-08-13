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

    public RestrictedURLClassLoader(URL[] urls, ClassLoader parent){
        super(urls, null);
        this.parent = parent;
    }

    public RestrictedURLClassLoader(String libPath, ClassLoader parent) throws IOException{
        super(new URL[]{}, null);
        File libDir = new File(libPath);
        URL[] urls = (URL[])FileUtils.toURLs(
                        (File[])FileUtils.listFiles(
                            libDir, new String[]{"jar"}, false).
                            toArray(new File[0]));

        for(int i = 0; i < urls.length; i++)
            this.addURL(urls[i]);

        this.parent=parent;
        cache = new HashMap<String, Class>();
    }

    public Class loadClass(String name) throws ClassNotFoundException {
//        System.out.println(name);
        Class cls;
        if//(luceneFinder.matcher(name).matches())
          (!(name.equals("edu.harvard.iq.dvn.networkData.DVNGraph") ||
           name.equals("edu.harvard.iq.dvn.networkData.GraphBatchInserter")))
            cls = super.loadClass(name);
        else
            cls = parent.loadClass(name);
        if(cls==null)
            throw new ClassNotFoundException(name);

        return cls;
    }
}

