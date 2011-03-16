/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.text;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author ekraffmiller
 * 
 *  This class isn't used yet, but may be needed in the future
 *  for dynamically displaying sets on the HomePage.
 */
@ManagedBean (name="HomePage")
@ViewScoped
public class HomePage {

    private ArrayList<SetInfo> setList = new ArrayList<SetInfo>();


    @PostConstruct
    public void init() {
        System.out.println("INIT-----------------------------------------------------------------------------");
        /*
        File setDir = new File(ClusterUtil.getDocRoot());
        File[] files = setDir.listFiles();
        for (File f : files) {
            if (f.isDirectory()) {
                // get set id
                SetInfo setInfo = new SetInfo();
                setInfo.id = f.getName();
                // get set description
                File desc = new File(f, "description.txt");
                if (desc.exists()) {
                    byte[] buffer = new byte[(int) desc.length()];
                    BufferedInputStream bi = null;
                    try {
                        bi = new BufferedInputStream(new FileInputStream(desc));
                        bi.read(buffer);
                    } catch  (IOException ignored) {

                    }finally {
                        if (bi != null) {
                            try {
                                bi.close();
                            } catch (IOException ignored) {
                            }
                        }
                    }
                    setInfo.description = new String(buffer);
                }
                setList.add(setInfo);
            }
        }
         
         */
    }

    public String clickAction() {
      
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!link was clicked");
        return "";
    }

    public ArrayList<SetInfo> getSetList() {
        System.out.println("in get set list");
        return setList;
    }

    public void setSetList(ArrayList<SetInfo> setList) {
        this.setList = setList;
    }

    public class SetInfo {
        String id;
        String description;

        public String getId() {
            return id;
        }
 
        public void setId(String id) {
            this.id = id;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

    }

}
