<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
    xmlns:h="http://java.sun.com/jsf/html" 
    xmlns:jsp="http://java.sun.com/JSP/Page" 
    xmlns:ui="http://www.sun.com/web/ui"
    xmlns:tiles="http://struts.apache.org/tags-tiles"
    xmlns:c="http://java.sun.com/jsp/jstl/core">

    <f:subview id="variablePageView">
       
        <ui:form  id="form1">
            <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
            
            <div class="dvn_section">
                  <span class="dvn_sectionTitleR">
                    <h:outputLink   value="/dvn#{VDCRequest.currentVDCURL}/faces/study/StudyPage?studyId=#{VariablePage.variable.dataTable.studyFile.fileCategory.study.id}">
                        <h:outputText  value="Go to Study"/>                       
                    </h:outputLink>
                 </span>
                <div class="dvn_sectionTitle">
                    <h:outputText value="#{VariablePage.variable.dataTable.studyFile.fileCategory.study.title}"/>                            
                     <br />
                    <span class="dvn_preFileTitle">Variable: </span>
                    <h:outputText styleClass="dvn_fileTitle" value="#{VariablePage.variable.name}"/><h:outputText styleClass="dvn_fileTitle" value=" - #{VariablePage.variable.label}" rendered="#{!empty VariablePage.variable.label}" />  
                </div>            
                <div class="dvn_sectionBoxNoBorders">     

                    <ui:panelGroup  block="true" style="padding-left: 4px;">
                        <h:outputText value="#{VariablePage.variable.unf}"/>
                    </ui:panelGroup> 
                    <ui:panelGroup  block="true" style="text-align: right; padding-bottom: 10px;">
                        <h:outputLink  value="/dvn#{VDCRequest.currentVDCURL}/faces/subsetting/SubsettingPage?dtId=#{VariablePage.variable.dataTable.id}">
                            <h:outputText value="See all variables in data file: #{VariablePage.variable.dataTable.studyFile.fileName}"/>
                        </h:outputLink>
                    </ui:panelGroup>
                    
                    <ui:panelGroup block="true" style="margin-bottom: 30px; padding: 20px 10px 20px 10px; border: 1px solid #cccccc; background-color: #f5faff;" rendered="#{!empty VariablePage.variable.summaryStatistics}" >
                        <ui:panelGroup  block="true" style="padding-bottom: 10px;" styleClass="vdcSubHeaderColor">
                            <h:outputText value="Summary Statistics"/>
                        </ui:panelGroup>  
                        <h:dataTable     value="#{VariablePage.variable.summaryStatistics}" var="ss" width="500px;" columnClasses="vdcColPadded">
                            <h:column>
                                <h:outputText  value="#{ss.type.name}" />
                                <f:facet name="header">
                                    <h:outputText  value="Statistic"/>
                                </f:facet>
                            </h:column>
                            <h:column>
                                <h:outputText  value="#{ss.value}" />
                                <f:facet name="header">
                                    <h:outputText  value="Value"/>
                                </f:facet>
                            </h:column>                                            
                        </h:dataTable>                        
                    </ui:panelGroup>    
                    
                <ui:panelGroup block="true" style="padding: 30px 10px 30px 10px; border: 1px solid #cccccc; background-color: #f5faff;" rendered="#{!empty VariablePage.variable.categories}">
                        <ui:panelGroup  block="true" style="padding-bottom: 10px;" styleClass="vdcSubHeaderColor">
                            <h:outputText value="Category Statistics"/>
                        </ui:panelGroup>  
                        <h:dataTable   value="#{VariablePage.variable.categories}" var="cs" width="500px;" columnClasses="vdcColPadded">
                            <h:column>
                                <h:outputText  value="#{cs.label} (#{cs.value})" />
                                <f:facet name="header">
                                    <h:outputText   value="Label (Value)"/>
                                </f:facet>
                            </h:column>
                            <h:column>
                                <h:outputText  value="#{cs.frequency}" />
                                <f:facet name="header">
                                    <h:outputText   value="Frequency"/>
                                </f:facet>
                            </h:column>                                            
                        </h:dataTable>   
                    </ui:panelGroup>
                    
                </div>
            </div>
        </ui:form>
    </f:subview>
</jsp:root>
