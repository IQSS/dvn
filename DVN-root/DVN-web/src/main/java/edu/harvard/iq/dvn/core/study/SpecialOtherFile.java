/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.study;

import javax.persistence.Entity;


/**
 *
 * @author leonidandreev
 */
@Entity
public class SpecialOtherFile extends OtherFile {
    public SpecialOtherFile() {
    }

    public SpecialOtherFile(Study study) {
        super(study);
    }
    
}
