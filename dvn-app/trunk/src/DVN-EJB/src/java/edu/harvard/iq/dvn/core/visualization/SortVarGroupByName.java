/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.visualization;

import java.util.Comparator;

/**
 *
 * @author skraffmiller
 */
public class SortVarGroupByName implements Comparator {

    public int compare(VarGroup a, VarGroup b) {
                return a.getName().compareTo(b.getName());
        }

    public int compare(Object t, Object t1) {
        VarGroup a = (VarGroup) t;
        VarGroup b = (VarGroup) t1;
        return a.getName().compareTo(b.getName());
    }
    
}
