/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.util;

import edu.harvard.iq.dvn.core.study.StudyServiceBean;
import edu.harvard.iq.dvn.core.vdc.TwitterCredentials;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

/**
 *
 * @author gdurand
 */

public class TwitterUtil implements java.io.Serializable  {

    private static TwitterFactory twitterFactory = new TwitterFactory();    
    
    public static TwitterFactory getTwitterFactory() {
        return twitterFactory;
    }
    
    public static void tweet(TwitterCredentials tc, String message, URL url) {

        if (tc != null) {              
            try {        
                Twitter twitter = twitterFactory.getInstance( new AccessToken(tc.getAccessToken(), tc.getAccessTokenSecret() ) );

                
                // Twitter treats links as fixed lengths (22 for http, 23 for https, effective February 20, 2013)
                // we add one to account for a space, and subtract that from 140 to determine how much is left for the
                // rest of the text
                int urlLength = url.getProtocol().equals("https") ? 23 : 22;
                int availableCharacters = 140 - (urlLength + 1) ;
                
                if (message.length() > availableCharacters) {
                    message = message.substring(0, (availableCharacters - 3) ) + "...";
                }
                message += " " + url.toString();
                Status status = twitter.updateStatus(message);
            } catch (TwitterException ex) {
                Logger.getLogger(StudyServiceBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }  
    }    
    
}
