<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html  xmlns="http://www.w3.org/1999/xhtml"
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

<gui:param name="pageTitle" value="DVN - Edit About" />

  <gui:define name="body">
      <f:loadBundle basename="Bundle" var="bundle"/>
     <f:loadBundle basename="BundleAnalysis" var="bundleAnalysis"/>
                            
                            
        <ui:form  id="EditAboutForm">
            <h:inputHidden rendered="#{VDCRequest.currentVDCId != null}" id="vdcId" value="#{VDCRequest.currentVDCId}"/>
            
            <div class="dvn_section">
                <div class="dvn_sectionTitle">
                    
                        <h:outputText  value="#{bundle.editAboutHeading}" rendered="#{VDCRequest.currentVDCId != null}"/>
                        <h:outputText  value="Edit the About Page for your Dataverse Network" rendered="#{VDCRequest.currentVDCId == null}"/>
                    
                </div>            
                <div class="dvn_sectionBox">
                    <div class="dvn_margin12">
                   
                            <h:messages layout="list" globalOnly="true" styleClass="successMessage" showSummary="true" />
                     
                        
                        <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                        <h:outputText styleClass="vdcHelpText" style="padding-left:4px;" value="#{bundle.editAboutHelpMsg} #{bundle.htmlHelpText}" rendered="#{VDCRequest.currentVDCId != null}"/>
                        <h:outputText styleClass="vdcHelpText" style="padding-left:4px;" value="#{bundle.editAboutNetworkHelpMsg} #{bundle.htmlHelpText}" rendered="#{VDCRequest.currentVDCId == null}"/>
                        <ui:panelGroup  block="true" id="groupPanel3" style="padding-top: 10px; padding-bottom: 20px">
                            <h:outputText  id="editAboutLabel" styleClass="vdcFieldTitle" style="vertical-align:top;" value="#{bundle.editAboutLabel}" rendered="#{VDCRequest.currentVDCId != null}"/>
                            <h:outputText  id="editAboutLabelb" styleClass="vdcFieldTitle" style="vertical-align:top;" value="About your Dataverse Network" rendered="#{VDCRequest.currentVDCId == null}"/>
                            <h:message id="aboutMsg"
                                       for="aboutThisDataverse"
                                       styleClass="errorMessage"/>
                            <f:verbatim><br /></f:verbatim>
                            <h:inputTextarea cols="100" 
                                             id="aboutThisDataverse" 
                                             value="#{EditAboutPage.aboutThisDataverse}" 
                                             rows="20"
                                             required="true"
                                             requiredMessage="The About page cannot be blank. Please enter your About page text or html."
                                             styleClass="formHtmlEnabled">
                                <f:validator validatorId="XhtmlValidator"/>
                            </h:inputTextarea>
                        </ui:panelGroup>
                        <ui:panelGroup  block="true" id="groupPanel5" style="padding-left: 200px; padding-top: 20px">
                            <h:commandButton id="btnSave" value="#{bundle.saveButtonLabel}" action="#{EditAboutPage.save_action}"/>
                            <h:commandButton id="btnCancel" immediate="true" style="margin-left: 30px" value="#{bundle.cancelButtonLabel}" action="#{EditAboutPage.cancel_action}"/>
                        </ui:panelGroup>
                    </div>
                </div>
            </div>

        </ui:form>
            </gui:define>
        </gui:composition>
    </body>
</html>
