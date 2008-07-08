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

<gui:param name="pageTitle" value="DVN - Import Study" />

  <gui:define name="body">
      <f:loadBundle basename="Bundle" var="bundle"/>
     <f:loadBundle basename="BundleAnalysis" var="bundleAnalysis"/>

        <ui:form  id="importStudyForm1">
            <h:inputHidden rendered="#{VDCRequest.currentVDCId != null}" id="vdcId" value="#{VDCRequest.currentVDCId}"/>
            <div class="dvn_section">
                <div class="dvn_sectionTitle">
                   
                        <h:outputText value="Import Study" />
                     
                </div>            
                <div class="dvn_sectionBox"> 
                    <div class="dvn_margin12">
                        
                        <h:messages id="importMsg"  styleClass="errorMessage"/> 
                        
                        <h:panelGrid columns="2"    style="padding: 30px 40px 40px 40px; ">                           
                            <h:outputText value="XML file (study):" />
                            <ui:upload  id="fileBrowser"  uploadedFile ="#{ImportStudyPage.browserFile}"/>
                            
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
                            
                            <h:outputText value="Import into:" />
                            <h:selectOneMenu  id="radioButtonList1" value="#{ImportStudyPage.vdcId}">
                                <f:selectItems  id="radio1SelectItem" value="#{ImportStudyPage.vdcRadioItems}" />
                            </h:selectOneMenu>                    
                            
                        </h:panelGrid>
                        
                        <h:commandButton  value="Import" action="#{ImportStudyPage.import_action}"/>
                        
                    </div>
                </div>
            </div>
        </ui:form>
            </gui:define>
        </gui:composition>
    </body>
</html>
