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


<gui:param name="pageTitle" value="DVN - Edit Dataverse Homepage Description" />

  <gui:define name="body">
      <f:loadBundle basename="Bundle" var="bundle"/>
     <f:loadBundle basename="BundleAnalysis" var="bundleAnalysis"/>

        <ui:form  id="editHomePanelsForm">
            <h:inputHidden rendered="#{VDCRequest.currentVDCId != null}" id="vdcId" value="#{VDCRequest.currentVDCId}"/>
            
            <div class="dvn_section">
                <div class="dvn_sectionTitle">
                              
                        <h:outputText value="#{bundle.editHomePanelsHeading}"/>
                    
                </div>            
                <div class="dvn_sectionBox">
                    <div class="dvn_margin12">

                       <h:messages layout="list" showDetail="false" showSummary="true" styleClass="successMessage" />
                        
                        <ui:panelGroup  block="true" id="groupPanel3" style="padding-bottom: 10px">
                            <h:outputText  id="outputText3" value="#{bundle.displayLocalAnnouncementsLabel}"/>
                            <h:selectBooleanCheckbox  id="chkLocalAnnouncements"  value="#{EditHomePanelsPage.chkLocalAnnouncements}"/>
                            <ui:panelGroup  block="true" style="padding-right: 70px">
                                <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                                <h:outputText id="htmlHelp" styleClass="vdcHelpText" value="#{bundle.htmlHelpText}"/>
                            </ui:panelGroup>
                            <h:message id="announcementsMsg" 
                                       for="localAnnouncements"
                                       styleClass="errorMessage"/>                  
                        </ui:panelGroup>
                        <ui:panelGroup  block="true" id="groupPanel5" style="padding-top: 5px; padding-bottom: 20px">
                            <h:inputTextarea  cols="100" 
                                              id="localAnnouncements" 
                                              rows="15"
                                              value="#{EditHomePanelsPage.localAnnouncements}"
                                              styleClass="formHtmlEnabled">
                                <f:validator validatorId="XhtmlValidator"/>
                            </h:inputTextarea>
                        </ui:panelGroup>
                                             
                        <ui:panelGroup  block="true" id="groupPanel1" style="padding-left: 200px; padding-top: 20px">
                            <h:commandButton id="btnSave" value="#{bundle.saveButtonLabel}" action="#{EditHomePanelsPage.save_action}"/>
                            <h:commandButton id="btnCancel" style="margin-left: 30px" value="#{bundle.cancelButtonLabel}" action="#{EditHomePanelsPage.cancel_action}"/>
                        </ui:panelGroup>
                    </div>  
                </div>
            </div>
                 
        </ui:form>             
            </gui:define>
        </gui:composition>
    </body>
</html>
