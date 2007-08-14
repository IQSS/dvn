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
                <div class="dvn_sectionTitle">
                   <h3>              
                        <h:outputText value="#{VariablePage.variable.dataTable.studyFile.fileCategory.study.title}"/>                            
                    </h3>
                </div>            
                <div class="dvn_sectionBoxNoBorders">     
                    
                    <ui:panelGroup  block="true" style="padding-bottom: 10px; padding-left: 4px;">
                        <h:outputText styleClass="vdcSubHeaderColor" style="font-size: 1.2em" value="#{VariablePage.variable.name}"/>
                        <h:outputText value=" #{VariablePage.variable.label}"/>
                        <h:outputLink style="margin-left: 2px;" value="/dvn#{VDCRequest.currentVDCURL}/faces/study/StudyPage?studyId=#{VariablePage.variable.dataTable.studyFile.fileCategory.study.id}">
                            <h:outputText style="font-size: 0.9em; font-weight: bold;" value="#{VariablePage.variable.dataTable.studyFile.fileCategory.study.title}"/>                            
                        </h:outputLink>
                    </ui:panelGroup> 
                    
                    <ui:panelGroup  block="true" style="padding-left: 4px;">
                        <h:outputText value="UNF: #{VariablePage.variable.unf}"/>
                    </ui:panelGroup> 
                    <ui:panelGroup  block="true" style="text-align: right; padding-bottom: 10px;">
                        <h:outputLink  value="/dvn#{VDCRequest.currentVDCURL}/faces/subsetting/SubsettingPage?dtId=#{VariablePage.variable.dataTable.id}">
                            <h:outputText value="See all variables in data file: #{VariablePage.variable.dataTable.studyFile.fileName}"/>
                        </h:outputLink>
                    </ui:panelGroup>
                    
                    <ui:panelGroup block="true" style="margin-bottom: 30px; padding: 20px 10px 20px 10px; border: 1px solid #cccccc; background-color: #f5faff; " rendered="#{!empty VariablePage.variable.summaryStatistics}" >
                        <ui:panelGroup  block="true" style="padding-bottom: 10px;" styleClass="vdcSubHeaderColor">
                            <h:outputText value="Summary Statistics"/>
                        </ui:panelGroup>  
                        <h:dataTable     value="#{VariablePage.variable.summaryStatistics}" var="ss" >
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
                        <h:dataTable   value="#{VariablePage.variable.categories}" var="cs">
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
