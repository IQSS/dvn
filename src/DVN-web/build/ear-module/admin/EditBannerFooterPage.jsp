<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles">

    <f:subview id="editBannerFooterView">
        <ui:form id="editBannerFooterForm">
            <h:inputHidden rendered="#{VDCRequest.currentVDC != null}" id="vdcId" value="#{VDCRequest.currentVDCId}"/>
                <ui:panelLayout  id="layoutPanel1" panelLayout="flow" styleClass="vdcSectionMiddleFixed" style="width: 750px">
                    <ui:panelLayout  id="layoutPanel2" panelLayout="flow" styleClass="vdcSectionHeader">
                        <h:outputText  value="#{bundle.editBannerFooterHeading}"/>
                    </ui:panelLayout>
                    
                    <ui:panelLayout panelLayout="flow" styleClass="vdcSectionMiddleMessage" style="width: 200px; margin-top: 10px; margin-bottom: -10px" rendered="#{EditBannerFooterPage.success}">
                      <ui:panelGroup block="true" styleClass="successMessage" >
                         <h:messages layout="table" globalOnly="true"/>
                      </ui:panelGroup>
                    </ui:panelLayout>
                    
                    <ui:panelLayout  id="layoutPanel3" panelLayout="flow" style="padding: 30px 40px 30px 40px; ">
                        <ui:panelGroup block="true" style="padding-bottom: 10px">
                            <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                            <h:outputText id="htmlHelp" styleClass="vdcHelpText" value="#{bundle.htmlHelpText}"/>
                        </ui:panelGroup>
                        <h:panelGrid cellpadding="0" cellspacing="0"
                            columnClasses="vdcColPadded, vdcColPadded" columns="2" id="gridPanel2" style="margin-top: 10px">
                            <h:outputText  id="outputText2" styleClass="vdcFieldTitle" value="#{bundle.editCustomBannerLabel}"/>
                            <h:panelGroup>
                                <h:message for="combined" styleClass="errorMessage"/>   
                                <h:message  id="bannerMsg" 
                                            for="banner"
                                            styleClass="errorMessage"/>
                                    <f:verbatim><br /><br /></f:verbatim>
                                <h:inputTextarea    cols="100" 
                                                    id="banner" 
                                                    rows="15" 
                                                    value="#{EditBannerFooterPage.banner}"
                                                    styleClass="formHtmlEnabled">
                                    <f:validator validatorId="XhtmlValidator"/>
                                </h:inputTextarea>
                            </h:panelGroup>
                            <h:outputText  id="footerLabel" styleClass="vdcFieldTitle" value="#{bundle.editCustomFooterLabel}"/>
                            <h:panelGroup>
                                <h:message  id="footerMsg" 
                                            for="footer"
                                            styleClass="errorMessage"/>
                                         
                                <f:verbatim><br /></f:verbatim>
                                <h:inputTextarea    cols="100" 
                                                    id="footer" 
                                                    rows="7" 
                                                    value="#{EditBannerFooterPage.footer}"
                                                    styleClass="formHtmlEnabled">
                                    <f:validator validatorId="XhtmlValidator"/>
                                </h:inputTextarea> 
                                <h:inputHidden id="combined" value="combined"/>
                                
                            </h:panelGroup>
                            
                        </h:panelGrid>
                        <ui:panelGroup  block="true" id="groupPanel1" style="padding-left: 200px; padding-top: 20px">
                             <h:commandButton id="btnSave" value="#{bundle.saveButtonLabel}" action="#{EditBannerFooterPage.save_action}"/>
                             <h:commandButton id="btnCancel" immediate="true" style="margin-left: 30px" value="#{bundle.cancelButtonLabel}" action="#{EditBannerFooterPage.cancel_action}"/>
                        </ui:panelGroup>
                    </ui:panelLayout>
                </ui:panelLayout>
        </ui:form>
    </f:subview>
</jsp:root>
