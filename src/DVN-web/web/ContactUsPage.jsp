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

<gui:param name="pageTitle" value="DVN - Contact Us" />

  <gui:define name="body">

            <ui:form  id="contactUsForm">  
              <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
              
              <div class="dvn_section">
                <div class="dvn_sectionTitle">
                    <a name="contact" title="">
                        <h:outputText  value="#{bundle.contactUsHeading}"/>
                    </a>
                </div>            
                <div class="dvn_sectionBox"> 
                    <div class="dvn_margin12">
                        
                        <ui:panelLayout  rendered="#{ContactUsPage.success}" styleClass="successMessage">
                            <h:messages layout="list" globalOnly="true"  />
                        </ui:panelLayout>
                            
                        <ui:panelLayout styleClass="errorMessage" rendered="#{ContactUsPage.exception}">
                            <h:messages layout="table" globalOnly="true" />
                        </ui:panelLayout>
                        
                        <ui:panelGroup  block="true" id="groupPanel2"  style="padding-bottom: 20px">
                            <h:outputText  id="outputText2" escape="false"  value="#{bundle.contactUsFormMessage} ("/>
                            <h:graphicImage value="/resources/icon_required.gif"/> <h:outputText style="vdcHelpText" value="Indicates a required field.)"/>
                        </ui:panelGroup>
                        <h:panelGrid  cellpadding="0" cellspacing="0"
                                      columnClasses="vdcColPadded, vdcColPadded" columns="2" id="gridPanel2">
                            <h:outputText  id="outputText7" style="font-weight: bold" value="#{bundle.contactUsToLabel}"/>
                            <h:outputFormat value="#{bundle.contactUsTo}">
                                <f:param value="#{(VDCRequest.currentVDCId == null) ? VDCRequest.vdcNetwork.name : VDCRequest.currentVDC.name }" />
                                <f:param value="#{ (VDCRequest.currentVDCId == null) ? ('Network') : ('')}"/>
                                <f:param value="(#{ (VDCRequest.currentVDCId == null) ? VDCRequest.vdcNetwork.contactEmail : VDCRequest.currentVDC.contactEmail})"/>
                            </h:outputFormat>
                             
                            <ui:panelGroup  id="groupPanel3">
                                <h:outputText  id="outputText4" style="font-weight: bold" value="#{bundle.contactUsEmailLabel}"/>
                                <h:graphicImage  id="image2" value="/resources/icon_required.gif"/>
                                <h:outputText styleClass="vdcHelpText" value="(E-mail Address)"/>
                            </ui:panelGroup>
                            <ui:panelGroup block="true" separator="&lt;br /&gt;"> 
                                <h:inputText  id="emailAddress" size="30"
                                              value="#{ContactUsPage.emailAddress}" 
                                              required="true"
                                              requiredMessage="This field is required.">
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
                                             requiredMessage="This field is required."
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
                                <h:inputTextarea cols="50" id="emailBody" 
                                                 value="#{ContactUsPage.emailBody}" 
                                                 required="true" 
                                                 requiredMessage="This field is required."
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
            </gui:define>
        </gui:composition>
    </body>
</html>
