<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles">
                            
    <f:subview id="EditAboutView">
        <ui:form  id="EditAboutForm">
            <h:inputHidden rendered="#{VDCRequest.currentVDCId != null}" id="vdcId" value="#{VDCRequest.currentVDCId}"/>
            
            <div class="dvn_section">
                <div class="dvn_sectionTitle">
                    <h3>
                        <h:outputText  value="#{bundle.editAboutHeading}" rendered="#{VDCRequest.currentVDCId != null}"/>
                        <h:outputText  value="Edit the About Page for your Dataverse Network" rendered="#{VDCRequest.currentVDCId == null}"/>
                    </h3>
                </div>            
                <div class="dvn_sectionBox">
                    <div class="dvn_margin12">
                   
                            <h:messages layout="table" globalOnly="true" styleClass="successMessage" showSummary="true" />
                     
                        
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
    </f:subview>
</jsp:root>
