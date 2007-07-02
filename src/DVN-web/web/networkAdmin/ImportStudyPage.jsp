<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
    xmlns:h="http://java.sun.com/jsf/html" 
    xmlns:jsp="http://java.sun.com/JSP/Page" 
    xmlns:ui="http://www.sun.com/web/ui"
    xmlns:tiles="http://struts.apache.org/tags-tiles">
    <f:subview id="ImportStudyView">
        <ui:form  id="importStudyForm1">
            <h:inputHidden rendered="#{VDCRequest.currentVDCId != null}" id="vdcId" value="#{VDCRequest.currentVDCId}"/>
            <ui:panelLayout  panelLayout="flow" styleClass="vdcSectionMiddle">
                <ui:panelLayout  panelLayout="flow" styleClass="vdcSectionHeader">
                    <h:outputText value="Import Study" />
                </ui:panelLayout>
                <ui:panelLayout   style="padding: 30px 40px 40px 40px; ">
                    <h:messages id="importMsg"  styleClass="errorMessage"/> 
                </ui:panelLayout>
                <h:panelGrid columns="2"    style="padding: 30px 40px 40px 40px; ">                           
                    <h:outputText value="DDI" />
                    <ui:upload  id="fileBrowser"  uploadedFile ="#{ImportStudyPage.browserFile}"/>
 
                    <h:outputText value="Import into:" />
                    <h:selectOneMenu  id="radioButtonList1" value="#{ImportStudyPage.vdcId}">
                        <f:selectItems  id="radio1SelectItem" value="#{ImportStudyPage.vdcRadioItems}" />
                    </h:selectOneMenu>
                    
                    <h:outputText value="XML file format:" />
                    <h:selectOneMenu  id="xmlFileFormatList" value="#{ImportStudyPage.xmlFileFormat}">
                        <f:selectItems  id="xmlFileFormatItems" value="#{ImportStudyPage.xmlFileFormatRadioItems}" />
                    </h:selectOneMenu>
                    
                    <h:outputText value="Register Handle?" />
                    <h:selectBooleanCheckbox  id="registerHandleCheckBox" value="#{ImportStudyPage.registerHandle}"/> 
                    
                    <h:outputText value="Generate Handle?" />
                    <h:selectBooleanCheckbox  id="generateHandleCheckBox" value="#{ImportStudyPage.generateHandle}"/> 
                    
                    <h:outputText value="Allow Updates?" />
                    <h:selectBooleanCheckbox  id="allowUpdatesCheckBox" value="#{ImportStudyPage.allowUpdates}"/> 
                            
                    <h:outputText value="Check Restrictions?" />
                    <h:selectBooleanCheckbox  id="checkRestrictionsCheckBox" value="#{ImportStudyPage.checkRestrictions}"/> 
                      
                    <h:outputText value="Copy Files?" />
                    <h:selectBooleanCheckbox  id="copyFilesCheckBox" value="#{ImportStudyPage.copyFiles}"/> 
                    
                </h:panelGrid>
                <ui:panelLayout   style="padding: 30px 40px 40px 40px; ">
                    <h:commandButton  value="Import" action="#{ImportStudyPage.import_action}"/>
                </ui:panelLayout>
            </ui:panelLayout>
        </ui:form>
    </f:subview>
</jsp:root>
