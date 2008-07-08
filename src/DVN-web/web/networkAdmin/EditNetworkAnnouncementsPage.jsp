<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:gui="http://java.sun.com/jsf/facelets"
      xmlns:h="http://java.sun.com/jsf/html"
      xmlns:f="http://java.sun.com/jsf/core" 
      xmlns:jsp="http://java.sun.com/JSP/Page"
      xmlns:ui="http://www.sun.com/web/ui"
      >
<head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>

</head>

<body>
<gui:composition template="/template.xhtml">

<gui:param name="pageTitle" value="DVN - Edit Network Announcements" />

  <gui:define name="body">
      <f:loadBundle basename="Bundle" var="bundle"/>
     <f:loadBundle basename="BundleAnalysis" var="bundleAnalysis"/>
                            
     
                            

    <ui:form  id="editNetworkAnnouncementsForm"> 

    <div class="dvn_section">
        <div class="dvn_sectionTitle">
          
                <h:outputText  value="#{bundle.editNetworkAnnouncementsHeading}"/>
            
        </div>            
        <div class="dvn_sectionBox"> 
            <div class="dvn_margin12"> 
                
                <h:messages layout="list" showDetail="false" showSummary="true" styleClass="successMessage" />
                
                <ui:panelGroup  block="true" id="groupPanel2">
                    <h:outputText  id="outputText4" value="#{bundle.enableNetworkAnnouncementsLabel}"/>
                    <h:selectBooleanCheckbox  id="chkEnableNetworkAnnouncements" value="#{EditNetworkAnnouncementsPage.chkEnableNetworkAnnouncements}"/>
                    <ui:panelGroup  block="true" style="padding-right: 70px">
                        <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />  
                        <h:outputText value="#{bundle.enableNetworkAnnouncementsHelpMsg} #{bundle.htmlHelpText}" styleClass="vdcHelpText" />
                    </ui:panelGroup>
                </ui:panelGroup>
                <h:message id="networkAnnouncementsMsg" 
                           for="networkAnnouncements"
                           styleClass="errorMessage"/>
                <f:verbatim><br /></f:verbatim>
                <ui:panelGroup  block="true" id="groupPanel3" style="padding-top: 5px; padding-bottom: 20px">
                    <h:inputTextarea cols="100" 
                                     id="networkAnnouncements" 
                                     rows="15" 
                                     value="#{EditNetworkAnnouncementsPage.networkAnnouncements}"
                                     styleClass="formHtmlEnabled">
                        <f:validator validatorId="XhtmlValidator"/>
                    </h:inputTextarea>
                </ui:panelGroup>
                <ui:panelGroup  block="true" id="groupPanel5" style="padding-left: 200px; padding-top: 20px">
                    <h:commandButton  id="btnSave" value="#{bundle.saveButtonLabel}" action="#{EditNetworkAnnouncementsPage.save_action}"/>
                    <h:commandButton  id="btnCancel" style="margin-left: 30px" value="#{bundle.cancelButtonLabel}" action="#{EditNetworkAnnouncementsPage.cancel_action}"/>
                </ui:panelGroup>
                
            </div>
        </div>
    </div>
    
                
    </ui:form>               
            </gui:define>
        </gui:composition>
    </body>
</html>
