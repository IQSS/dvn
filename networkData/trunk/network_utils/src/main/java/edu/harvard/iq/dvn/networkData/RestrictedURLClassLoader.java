package edu.harvard.iq.dvn.networkData;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.MalformedURLException;
import java.io.File;

public class RestrictedURLClassLoader extends URLClassLoader{
    private ClassLoader parent;

    public RestrictedURLClassLoader(URL[] urls, ClassLoader parent){
        super(urls, null);
        this.parent = parent;
    }

    public Class loadClass(String name) throws ClassNotFoundException {
//        System.out.println(name);
        Class cls;
        if(name.equals("edu.harvard.iq.dvn.networkData.DVNGraph")
                || name.equals("edu.harvard.iq.dvn.networkData.GraphBatchInserter"))
            cls = parent.loadClass(name);

        else
           cls = super.loadClass(name); 

        if(cls==null)
            throw new ClassNotFoundException(name);

        return cls;
    }
}

