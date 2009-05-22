/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.study;

import java.util.ArrayList;
import javax.persistence.Entity;

/**
 *
 * @author gdurand
 */

@Entity
public class TabularDataFile extends StudyFile  {

    public TabularDataFile() {
    }

    public TabularDataFile(Study study) {
        super(study);
    }

    
    public boolean isSubsettable() {
        return true;
    }

    public boolean isUNFable() {
        return true;
    }

    public DataTable getDataTable() {
        if ( getDataTables() != null && getDataTables().size() > 0 ) {
            return getDataTables().get(0);
        } else {
            return null;
        }
    }

    public void setDataTable(DataTable dt) {
        if (this.getDataTables() == null) {
            this.setDataTables( new ArrayList() );
        } else {
            this.getDataTables().clear();
        }

        this.getDataTables().add(dt);
    }
}
