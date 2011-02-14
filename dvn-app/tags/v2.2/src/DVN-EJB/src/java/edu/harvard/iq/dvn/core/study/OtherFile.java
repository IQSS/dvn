/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.study;

import javax.persistence.Entity;

/**
 *
 * @author gdurand
 */
@Entity
public class OtherFile extends StudyFile {


    public OtherFile() {
    }

    public OtherFile(Study study) {
        super(study);
    }

    public boolean isSubsettable() {
        return false;
    }

    public boolean isUNFable() {
        return false;
    }

}
