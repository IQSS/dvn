<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
          xmlns:h="http://java.sun.com/jsf/html" 
          xmlns:jsp="http://java.sun.com/JSP/Page" 
          xmlns:ui="http://www.sun.com/web/ui"
          xmlns:tiles="http://struts.apache.org/tags-tiles">
    
    <f:subview id="vDCGroupPageView">
        <h:form id="vdcGroupPageForm">
            <h:messages errorStyle="color: red" infoStyle="color: green" layout="table"/>
            <h:dataTable  id="VDCGroups" value="#{VDCGroupPage.VDCGroups}" var="item" cellspacing="0" style="width:100%; border-width:1px 1px 0px 1px; border-style:solid; border-color:silver; padding:0px;" headerClass="groupEditHeader" columnClasses="groupEditColumnCenter, groupEditColumnLeft, groupEditColumnLeft, groupEditColumnCenter">
                <f:facet name="caption">
                    <ui:panelGroup block="true" style="margin-top:25px;border-left:1px solid silver; border-right:1px solid silver;font-size:1.3em;" styleClass="vdcSectionHeader">
                        <h:outputText id="dataTableCaption" value="Dataverse Groups"/>
                    </ui:panelGroup>
                </f:facet>
                <h:column headerClass="groupEditHeader">
                    <f:facet name="header">
                        <h:outputText id="displayColumnCaption" escape="false" value="Display Order"/>
                    </f:facet>
                        <h:inputText id="displayOrder" size="2" maxlength="3" style="text-align:right" value="#{item.displayOrder}"/>
                </h:column>
                <h:column>
                    <f:facet name="header">
                        <h:outputText value="Name"/>
                    </f:facet>
                    <h:outputText value="#{item.name}"/>
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
                    <h:selectBooleanCheckbox label="delete" value="false"/>
              
                </h:column>
            </h:dataTable>
            <h:panelGrid columns="4" style="width:100%; border-width:0px 1px 1px 1px; border-style:solid; border-color:silver; background-color:#eeeeee;" columnClasses="groupEditActionLeft, groupEditActionLeft, groupEditActionCenter, groupEditActionRight" cellspacing="0">
                <h:column><h:commandButton id="order" value="Save Order" action="#{VDCGroupPage.saveOrder}"/></h:column>
                <h:column><h:commandButton id="add" value="Add Group" action="#{VDCGroupPage.addGroup}"/></h:column>
                <h:column><h:outputText escape="false" value="&lt;!-- placeholder this is an empty column --&gt;"/></h:column>
                <h:column><h:commandButton id="delete" value="Delete Checked" action="#{VDCGroupPage.deleteGroup}"/></h:column>
            </h:panelGrid>
        </h:form>
    </f:subview>
</jsp:root>