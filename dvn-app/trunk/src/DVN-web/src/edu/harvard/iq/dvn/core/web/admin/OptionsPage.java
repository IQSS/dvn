/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.web.admin;

import edu.harvard.iq.dvn.core.util.PropertyUtil;
import edu.harvard.iq.dvn.core.vdc.VDCNetworkServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDCServiceLocal;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import javax.ejb.EJB;
import javax.faces.bean.ViewScoped;
import javax.inject.Named;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.auth.RequestToken;

/**
 *
 * @author gdurand
 */

@ViewScoped
@Named("OptionsPage")
public class OptionsPage extends VDCBaseBean  implements java.io.Serializable {

    @EJB VDCNetworkServiceLocal vdcNetworkService;
    @EJB VDCServiceLocal vdcService; 
    
    private String twitterVerifier;

    public String getTwitterVerifier() {
        return twitterVerifier;
    }

    public void setTwitterVerifier(String twitterVerifier) {
        this.twitterVerifier = twitterVerifier;
    }
    
    
    
    public void init() {
        
        if (twitterVerifier != null && getSessionMap().get("requestToken") != null) {
            addTwitter();           
        }        
    }
    

    public String authorizeTwitter() {
        String callbackURL = "http://localhost:8080/dvn";
        callbackURL += getVDCRequestBean().getCurrentVDC() == null ? "/faces/networkAdmin/NetworkOptionsPage.xhtml" : 
              getVDCRequestBean().getCurrentVDCURL() + "/faces/admin/OptionsPage.xhtml";
                
        Twitter twitter = new TwitterFactory().getInstance();

        try {
            RequestToken requestToken = twitter.getOAuthRequestToken(callbackURL);
            getSessionMap().put("requestToken", requestToken);
            redirect(requestToken.getAuthorizationURL());
        } catch (TwitterException te) {
            te.printStackTrace();
        }
        
        return null;
    }
    
    public void addTwitter() {
        Long vdcId = getVDCRequestBean().getCurrentVDCId();

        try {
            Twitter twitter = new TwitterFactory().getInstance();
            AccessToken accessToken = twitter.getOAuthAccessToken( (RequestToken) getSessionMap().remove("requestToken"), twitterVerifier );
            vdcService.setTwitterCredentials(accessToken.getToken(),accessToken.getTokenSecret(), vdcId);
            
            if (vdcId != null) {
                // refresh the current vdc object, since it has changed
                getVDCRequestBean().setCurrentVDC( vdcService.findById( vdcId ));
            }
            getVDCRenderBean().getFlash().put("successMessage", "Automatic tweets are now enabled.");
            
        } catch (TwitterException te) {
            te.printStackTrace();
        }                       
    }
    
    public void removeTwitter() {
        Long vdcId = getVDCRequestBean().getCurrentVDCId();
        vdcService.removeTwitterCredentials(vdcId);
        
        if (vdcId != null) {
            // refresh the current vdc object, since it has changed
            getVDCRequestBean().setCurrentVDC( vdcService.findById( vdcId ));
        }        
        
        getVDCRenderBean().getFlash().put("successMessage", "Automatic tweets are now disabled.");
    }

    public boolean isTwitterConfigured() {
        return PropertyUtil.isTwitterConsumerConfigured();
    }    
    
    public boolean isTwitterEnabled() {
        if (getVDCRequestBean().getCurrentVDC() == null ) {
            return (vdcNetworkService.getTwitterCredentials() != null);
        } else {
            return (getVDCRequestBean().getCurrentVDC().getTwitterCredentials() != null);
        }
        
    }
                     
}
