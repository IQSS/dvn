<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles">                          
   <f:subview id="contactUsPageView">
            <ui:form  id="contactUsForm">  
              <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
              
              <div class="dvn_section">
                <div class="dvn_sectionTitle">
                     
                        <h:outputText  value="#{bundle.contactUsHeading}"/>
                     
                </div>            
                <div class="dvn_sectionBox"> 
                    <div class="dvn_margin12">
                        
                        <ui:panelLayout  rendered="#{ContactUsPage.success}" styleClass="successMessage">
                            <h:messages layout="table" globalOnly="true"  />
                        </ui:panelLayout>
                            
                        <ui:panelLayout styleClass="errorMessage" rendered="#{ContactUsPage.exception}">
                            <h:messages layout="table" globalOnly="true" />
                        </ui:panelLayout>
                        
                        <ui:panelGroup  block="true" id="groupPanel2"  style="padding-bottom: 20px">
                            <h:outputText  id="outputText2" value="#{bundle.contactUsFormMessage} ("/>
                            <h:graphicImage value="/resources/icon_required.gif"/> <h:outputText style="vdcHelpText" value="Indicates a required field.)"/>
                        </ui:panelGroup>
                        <h:panelGrid  cellpadding="0" cellspacing="0"
                                      columnClasses="vdcColPadded, vdcColPadded" columns="2" id="gridPanel2">
                            <h:outputText  id="outputText7" style="font-weight: bold" value="#{bundle.contactUsToLabel}"/>
                            <h:outputFormat value="#{bundle.contactUsTo}">
                                <f:param value="#{(VDCRequest.currentVDCId == null) ? VDCRequest.vdcNetwork.name : VDCRequest.currentVDC.name }" />
                                <f:param value="#{ (VDCRequest.currentVDCId == null) ? ('Network') : ('')}"/>
                            </h:outputFormat>
                            
                            <ui:panelGroup  id="groupPanel3">
                                <h:outputText  id="outputText4" style="font-weight: bold" value="#{bundle.contactUsEmailLabel}"/>
                                <h:graphicImage  id="image2" value="/resources/icon_required.gif"/>
                                <h:outputText styleClass="vdcHelpText" value="(E-mail Address)"/>
                            </ui:panelGroup>
                            <ui:panelGroup block="true" separator="&lt;br /&gt;"> 
                                <h:inputText  id="emailAddress" size="30"
                                              value="#{ContactUsPage.emailAddress}" 
                                              required="true">
                                    <f:validator validatorId="EmailValidator"/>
                                </h:inputText>
                                <h:message id="emailAddressMsg" 
                                           for="emailAddress"
                                           styleClass="errorMessage"/>
                            </ui:panelGroup>
                            
                            <ui:panelGroup  id="groupPanel1">
                                <h:outputText  id="outputText3" value="#{bundle.contactUsFromLabel}"/>
                                <h:graphicImage  id="image1" value="/resources/icon_required.gif"/>
                            </ui:panelGroup>
                            <ui:panelGroup block="true" separator="&lt;br /&gt;"> 
                                <h:inputText id="fullName" 
                                             size="30"
                                             value       = "#{ContactUsPage.fullName}"
                                             required="true"
                                />
                                
                                <h:message id="fullNameMsg" 
                                           for="fullName"
                                           styleClass="errorMessage" 
                                />
                            </ui:panelGroup>
                            
                            <ui:panelGroup  id="groupPanel4">
                                <h:outputText  id="outputText5" value="#{bundle.contactUsSubjectLabel}"/>
                                <h:graphicImage  id="image3" value="/resources/icon_required.gif"/>
                            </ui:panelGroup>
                            <ui:panelGroup block="true" separator="&lt;br /&gt;"> 
                                <h:selectOneMenu id="listSubjects"
                                                 required="true" 
                                                 validator="#{ContactUsPage.validateSubject}"
                                                 value="#{ContactUsPage.selectedSubject}"
                                >
                                    <f:selectItems id="listSubjectItems" value="#{ContactUsPage.listSubjectDefaults}"/>
                                </h:selectOneMenu>
                                
                                <h:message id="subjectListMsg" 
                                           for="listSubjects"
                                           styleClass="errorMessage"/>
                            </ui:panelGroup>
                            <ui:panelGroup  id="groupPanel5" styleClass="vdcTextNoWrap">
                                <h:outputText  id="outputText6" value="#{bundle.contactUsBodyLabel}"/>
                                <h:graphicImage  id="image4" value="/resources/icon_required.gif"/>
                            </ui:panelGroup>
                            <ui:panelGroup block="true" separator="&lt;br /&gt;"> 
                                <h:inputTextarea cols="80" id="emailBody" 
                                                 value="#{ContactUsPage.emailBody}" 
                                                 required="true" 
                                                 rows="8"/>
                                
                                <h:message id="emailBodyMsg"
                                           for="emailBody"
                                           styleClass="errorMessage"/>
                            </ui:panelGroup>
                        </h:panelGrid>
                        <ui:panelGroup  block="true" id="groupPanel9" style="padding-left: 170px; padding-top: 20px">
                            <h:commandButton  id="btnSend" value="Send E-mail" action="#{ContactUsPage.send_action}"/>
                        </ui:panelGroup>
                        
                    </div>
                </div>
            </div>
       
         </ui:form>
    </f:subview>
</jsp:root>
