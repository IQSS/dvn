/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.vdc;

import java.io.Serializable;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author roberttreacy
 */
@Entity
public class Captcha implements Serializable {
    private String privateKey;
    private String publicKey;
    private String domainName;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Captcha)) {
            return false;
        }
        Captcha other = (Captcha) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.harvard.iq.dvn.core.vdc.Captcha[id=" + id + "]";
    }

    /**
     * @return the privateKey
     */
    public String getPrivateKey() {
        return privateKey;
    }

    /**
     * @return the publicKey
     */
    public String getPublicKey() {
        return publicKey;
    }

    public String getHost(){
        String hostName = null;
        try {
            hostName = InetAddress.getLocalHost().getCanonicalHostName();
            Logger.getLogger(Captcha.class.getName()).info("HOST NAME: "+hostName);
            if (hostName.indexOf(domainName) == -1){
                hostName = "127.0.0.1"; // for dev testing
            }
        } catch (UnknownHostException ex) {
            Logger.getLogger(Captcha.class.getName()).log(Level.SEVERE, null, ex);
        }
        return hostName;
    }

}
