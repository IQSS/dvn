<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles">
   <f:subview id="EditHomePanelsPageView">
        <ui:form  id="editHomePanelsForm">
            <h:inputHidden rendered="#{VDCRequest.currentVDCId != null}" id="vdcId" value="#{VDCRequest.currentVDCId}"/>
            
            <div class="dvn_section">
                <div class="dvn_sectionTitle">
                              
                        <h:outputText value="#{bundle.editHomePanelsHeading}"/>
                    
                </div>            
                <div class="dvn_sectionBox">
                    <div class="dvn_margin12">

                       <h:messages layout="table" showDetail="false" showSummary="true" styleClass="successMessage" />
                        
                        <h:outputText  id="outputText4" styleClass="vdcSubHeaderColor" value="#{bundle.announcementsPanelHeading}"/>
                        <ui:panelGroup  block="true" id="groupPanel2" style="padding-top: 10px; padding-bottom: 20px">
                            <h:outputText  id="outputText2" value="1)  #{bundle.displayNetworkAnnouncementsLabel}"/>
                            <h:selectBooleanCheckbox  id="chkNetworkAnnouncements" value="#{EditHomePanelsPage.chkNetworkAnnouncements}"/>
                            <ui:panelGroup  block="true" style="padding-right: 70px">
                                <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />  
                                <h:outputText value="By checking this, Network announcements will be displayed in your dataverse homepage only when they are available." styleClass="vdcHelpText" />
                            </ui:panelGroup>
                        </ui:panelGroup>
                        <ui:panelGroup  block="true" id="groupPanel3" style="padding-bottom: 10px">
                            <h:outputText  id="outputText3" value="2)  #{bundle.displayLocalAnnouncementsLabel}"/>
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
                        <h:outputText  id="outputText5" styleClass="vdcSubHeaderColor" value="#{bundle.newStudiesPanelHeading}"/>
                        <ui:panelGroup  block="true" id="groupPanel4" style="padding-top: 10px; padding-bottom: 20px">
                            <h:outputText  id="outputText6" value="#{bundle.displayNewStudiesPanelHeading}"/>
                            <h:selectBooleanCheckbox  id="chkNewStudies" value="#{EditHomePanelsPage.chkNewStudies}"/>
                            
                            <ui:panelGroup block="true" style="padding-right: 65px">
                                <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                                <h:outputText value="#{bundle.newStudiesMessage}" styleClass="vdcHelpText"/>
                            </ui:panelGroup>
                        </ui:panelGroup>
                        <ui:panelGroup  block="true" id="groupPanel1" style="padding-left: 200px; padding-top: 20px">
                            <h:commandButton id="btnSave" value="#{bundle.saveButtonLabel}" action="#{EditHomePanelsPage.save_action}"/>
                            <h:commandButton id="btnCancel" style="margin-left: 30px" value="#{bundle.cancelButtonLabel}" action="#{EditHomePanelsPage.cancel_action}"/>
                        </ui:panelGroup>
                    </div>  
                </div>
            </div>
                 
        </ui:form>             
     </f:subview>
</jsp:root>
