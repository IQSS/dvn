<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
          xmlns:h="http://java.sun.com/jsf/html" 
          xmlns:jsp="http://java.sun.com/JSP/Page" 
          xmlns:ui="http://www.sun.com/web/ui"
          xmlns:tiles="http://struts.apache.org/tags-tiles"
          xmlns:c="http://java.sun.com/jsp/jstl/core">
    
    <f:subview id="editVariablePageView">
        
        <ui:form  id="form1">
            <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
            <input type="hidden"  name="pageName" value="EditVariablePage"/>
            <h:inputHidden id="dtId" value="#{EditVariablePage.dtId}"/>
            <h:inputHidden id="dvFilter" value="#{EditVariablePage.dvFilter}"/>
            
            <div class="dvn_section">
                <div class="dvn_sectionTitle">
                    <h:outputText value="#{EditVariablePage.dt.studyFile.fileName}"/>
                    <br />
                    <span class="dvn_preFileTitle">Variables: </span>
                    <br />
                    <ui:panelGroup id="messagePanel" styleClass="errorMessage" rendered="#{! empty facesContext.maximumSeverity}">
                        <h:outputText value="Some errors occurred, please check details below" rendered="#{! empty facesContext.maximumSeverity}" />
                    </ui:panelGroup>
                    <br />
                    <h:dataTable   value="#{EditVariablePage.dataVariables}"  var="dv" width="500px;" columnClasses="vdcColPadded">
                        <h:column>
                            <h:inputHidden  value="#{dv.id}" />
                            <h:inputText  id="dv_name" value="#{dv.name}" required="true" validator ="#{EditVariablePage.validateDVName}"/>
                            <h:message styleClass="errorMessage" for="dv_name"/>
                            <f:facet name="header">
                                <h:outputText   value="Name"/>
                            </f:facet>
                        </h:column>
                        <h:column>
                            <h:inputText  id="dv_label" value="#{dv.label}" size="80"/>
                            <h:message styleClass="errorMessage" for="dv_label"/>
                            <f:facet name="header">
                                <h:outputText   value="Label"/>
                            </f:facet>
                        </h:column>                                            
                    </h:dataTable>    
                    <h:commandButton  id="saveButton" value="Save" action="#{EditVariablePage.save_action}"/>                    
                </div>            
            </div>
        </ui:form>
    </f:subview>
</jsp:root>
