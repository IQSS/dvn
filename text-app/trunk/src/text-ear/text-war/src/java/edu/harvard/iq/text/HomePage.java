/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.text;

import java.util.ArrayList;
import javax.annotation.PostConstruct;

/**
 *
 * @author ekraffmiller
 * 
 *  This class isn't used yet, but may be needed in the future
 *  for dynamically displaying sets on the HomePage.
 */

public class HomePage {

    private ArrayList<SetInfo> setList = new ArrayList<SetInfo>();


    @PostConstruct
    public void init() {
        

    }

    class SetInfo {
        String id;
        String description;

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

    }

}
