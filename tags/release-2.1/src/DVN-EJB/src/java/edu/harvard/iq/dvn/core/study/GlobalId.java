/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.study;

/**
 *
 * @author gdurand
 */
public class GlobalId implements java.io.Serializable {

    public GlobalId(String identifier) {

        int index1 = identifier.indexOf(':');
        int index2 = identifier.indexOf('/');
        if (index1 == -1) {
            throw new IllegalArgumentException("Error parsing identifier: " + identifier + ". ':' not found in string");
        } else {
            protocol = identifier.substring(0, index1);
        }
        if (index2 == -1) {
            throw new IllegalArgumentException("Error parsing identifier: " + identifier + ". '/' not found in string");

        } else {
            authority = identifier.substring(index1 + 1, index2);
        }
        studyId = identifier.substring(index2 + 1).toUpperCase();

    }

    public GlobalId(String protocol, String authority, String studyId) {
        this.protocol = protocol;
        this.authority = authority;
        this.studyId = studyId;
    }


    private String protocol;
    private String authority;
    private String studyId;


    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public String getStudyId() {
        return studyId;
    }

    public void setStudyId(String studyId) {
        this.studyId = studyId;
    }

    public String toString() {
        return protocol + ":" + authority + "/" + studyId;
    }


}
