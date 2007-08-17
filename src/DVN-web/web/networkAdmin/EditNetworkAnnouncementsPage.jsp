<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles">
                            
<f:subview id="EditNetworkAnnouncementsPageView">
    <ui:form  id="editNetworkAnnouncementsForm"> 

    <div class="dvn_section">
        <div class="dvn_sectionTitle">
            <h3>
                <h:outputText  value="#{bundle.editNetworkAnnouncementsHeading}"/>
            </h3>
        </div>            
        <div class="dvn_sectionBox"> 
            <div class="dvn_margin12"> 
                
                <ui:panelGroup rendered="#{EditNetworkAnnouncementsPage.success}" styleClass="successMessage">
                    <h:messages layout="table" showDetail="false" showSummary="true"/>
                </ui:panelGroup>
                
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
</f:subview>
</jsp:root>
