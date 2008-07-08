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

<gui:param name="pageTitle" value="DVN - Edit Variables" />

  <gui:define name="body">
      <f:loadBundle basename="Bundle" var="bundle"/>
      <f:loadBundle basename="BundleAnalysis" var="bundleAnalysis"/>
      <f:loadBundle basename="EditStudyBundle" var="editstudybundle"/>
    

        
        <ui:form  id="form1">
            <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
            <input type="hidden"  name="pageName" value="EditVariablePage"/>
            <h:inputHidden id="dtId" value="#{EditVariablePage.dtId}"/>
            <h:inputHidden id="dvFilter" value="#{EditVariablePage.dvFilter}"/>
            
            <div class="dvn_section">
                <div class="dvn_sectionTitle">
                    <h:outputText value="#{EditVariablePage.dt.studyFile.fileName}"/>
                 </div>   
                 <div class="dvn_sectionBoxNoBorders">    
                    <br />
                    <span class="dvn_preFileTitle">Variables: </span>
                    <br />
                    <ui:panelGroup id="messagePanel" styleClass="errorMessage" rendered="#{! empty facesContext.maximumSeverity}">
                        <h:outputText value="Some errors occurred, please check details below" rendered="#{! empty facesContext.maximumSeverity}" />
                    </ui:panelGroup>
                    <br />
                    <h:dataTable   value="#{EditVariablePage.dataVariables}"  var="dv"  columnClasses="vdcColPadded">
                        <h:column>
                            <h:inputHidden  value="#{dv.id}" />
                            <h:inputText  id="dv_name" value="#{dv.name}" required="true" requiredMessage="This field is required." validator ="#{EditVariablePage.validateDVName}"/>
                            <br />
                            <h:message styleClass="errorMessage" for="dv_name"/>
                            <f:facet name="header">
                                <h:outputText   value="Name"/>
                            </f:facet>
                        </h:column>
                        <h:column>
                            <h:inputText  id="dv_label" value="#{dv.label}" size="80"/>
                            <br />
                            <h:message styleClass="errorMessage" for="dv_label"/>
                            <f:facet name="header">
                                <h:outputText   value="Label"/>
                            </f:facet>
                        </h:column>                                            
                    </h:dataTable>    
                    <h:commandButton  id="saveButton" value="Save" action="#{EditVariablePage.save_action}"/>  
                    <h:commandButton  id="cancelButton" value="Cancel" action="#{EditVariablePage.cancel_action}" immediate="true" />
                </div>            
            </div>
        </ui:form>
            </gui:define>
        </gui:composition>
    </body>
</html>
