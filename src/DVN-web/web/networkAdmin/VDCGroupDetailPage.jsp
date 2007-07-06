<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
          xmlns:h="http://java.sun.com/jsf/html" 
          xmlns:jsp="http://java.sun.com/JSP/Page" 
          xmlns:ui="http://www.sun.com/web/ui"
          xmlns:tiles="http://struts.apache.org/tags-tiles">
    <f:subview id="vDCGroupPageView">
        <h:form id="vdcGroupPageForm">
            <ui:panelLayout rendered="#{VDCGroupPage.success}" panelLayout="flow" styleClass="vdcSectionMiddleMessage" style="width: 400px; margin-top: 10px; margin-bottom: -10px">
                 <h:messages styleClass="successMessage" layout="table" showDetail="false" showSummary="true"/>
            </ui:panelLayout>
            <ui:panelLayout panelLayout="flow" styleClass="dvGroupAdminLayout">
                <h:dataTable id="VDCGroups" value="#{VDCGroupPage.VDCGroups}" var="item" cellspacing="0" styleClass="dvGroupAdminTable" headerClass="groupAdminHeader" columnClasses="groupAdminOrderColumn, groupAdminNameColumn, groupAdminDescriptionColumn, groupAdminDeleteColumn" rowClasses="whiteRow, shadedRow">
                    <f:facet name="caption">
                        <ui:panelGroup id="dvGroupAdminCaption" block="true" styleClass="vdcSectionHeader">
                            <h:outputText id="dataTableCaption" value="Dataverse Groups"/>
                        </ui:panelGroup>
                    </f:facet>
                    <h:column headerClass="groupAdminHeader">
                        <f:facet name="header">
                            <h:outputText id="displayColumnCaption" escape="false" value="Display Order"/>
                        </f:facet>
                        <h:inputText valueChangeListener="#{VDCGroupPage.changeOrder}" onblur="submit();" id="displayOrder" styleClass="groupAdminOrderInput" size="2" maxlength="3" value="#{item.displayOrder}"/>
                            
                    </h:column>
                    <h:column>
                        <f:facet name="header">
                            <h:outputText value="Name"/>
                        </f:facet>
                        <h:commandLink id="editGroup" action="#{VDCGroupPage.edit}" value="#{item.name}" immediate="true"/>
                    </h:column>
                    <h:column>
                        <f:facet name="header">
                            <h:outputText value="Description"/>
                        </f:facet>
                        <h:outputText value="#{item.description}"/>
                    </h:column>
                    <h:column>
                        <f:facet name="header">
                            <h:outputText value="Delete"/>
                        </f:facet>
                        <h:selectBooleanCheckbox valueChangeListener="#{VDCGroupPage.changeSelect}" onchange="submit();" id="selected" immediate="true" label="delete" value="#{item.selected}"/>
                        
                    </h:column>
                </h:dataTable>
                <h:panelGrid columns="4" styleClass="dvGroupAdminFooter" columnClasses="groupAdminOrderFooter, groupAdminNameFooter, groupAdminDescriptionFooter, groupAdminDeleteFooter" cellspacing="0">
                    <h:column><h:outputText escape="false" value="&lt;!-- placeholder --&gt;"/></h:column>
                    <h:column><h:commandLink id="add" value="Add Group" action="#{VDCGroupPage.addGroup}"/></h:column>
                    <h:column><h:outputText escape="false" value="&lt;!-- placeholder --&gt;"/></h:column>
                    <h:column><h:commandButton id="save" value="Save" action="#{VDCGroupPage.save}"/></h:column>
                </h:panelGrid>
            </ui:panelLayout>
        </h:form>
    </f:subview>
</jsp:root>