/*
 * StatusMessage.java
 *
 * Created on November 27, 2006, 1:22 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web.common;

/**
 *
 * @author roberttreacy
 */
public class StatusMessage {
    
    /** Creates a new instance of StatusMessage */
    public StatusMessage() {
    }

    private String messageText;

    private String styleClass;

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getStyleClass() {
        return styleClass;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    
}
