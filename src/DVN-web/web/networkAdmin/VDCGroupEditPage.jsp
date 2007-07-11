<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
          xmlns:h="http://java.sun.com/jsf/html" 
          xmlns:jsp="http://java.sun.com/JSP/Page" 
          xmlns:ui="http://www.sun.com/web/ui"
          xmlns:tiles="http://struts.apache.org/tags-tiles">
    <f:subview id="vDCGroupEditView">
        <f:verbatim>
            <script language="JavaScript">
            function getAddRemoveLength() {
                if (document.getElementById('content:vDCGroupEditView:vdcGroupEditForm:addRemoveList_selected').length > 1) {
                    return true;
                } else {
                    alert("Validation Error: There must be at least one entry in the Selected Dataverse(s) list." + "\n\r" + "Please add a Dataverse to continue.");
                    return false;
                }
            }
            </script>
        </f:verbatim>
        <h:form id="vdcGroupEditForm" onsubmit="return getAddRemoveLength();">
            <ui:panelLayout rendered="#{VDCGroupPage.success}" panelLayout="flow" styleClass="vdcSectionMiddleMessage" style="width: 400px; margin-top: 10px; margin-bottom: -10px">
                 <h:messages styleClass="successMessage" layout="table" showDetail="false" showSummary="true"/>
            </ui:panelLayout>
            <ui:panelLayout panelLayout="flow" styleClass="dvGroupAdminLayout">
                <h:inputHidden id="vdcGroupId" value="#{ (VDCGroupPage.vdcGroupId != null) ? VDCGroupPage.vdcGroupId : requestScope.vdcGroupId}" immediate="true"/>
                <h:panelGrid columns="2" cellspacing="0" styleClass="dvGroupAdminTable" headerClass="groupAdminHeader"  columnClasses="groupEditNameColumn, groupEditDescriptionColumn">
                    <f:facet name="caption">
                        <ui:panelGroup id="dvGroupAdminCaption" block="true" styleClass="vdcSectionHeader">
                            <h:outputText id="dataTableCaption" value="Edit Dataverse Groups"/>
                        </ui:panelGroup>
                    </f:facet>
                    
                     <f:facet name="header">
                        <h:panelGroup layout="block" style="width:100%;">
                            <h:panelGrid style="width:100%;" columns="2" columnClasses="groupEditNameColumn, groupEditDescriptionColumn" cellspacing="0" styleClass="groupAdminHeader">
                                <h:outputText value="Name"/>
                                <h:outputText value="Description"/>
                            </h:panelGrid>
                        </h:panelGroup>
                    </f:facet>
                    <h:column headerClass="groupAdminHeader">
                        <h:panelGroup>
                            <h:inputText id="name" value="#{VDCGroupPage.name}" valueChangeListener="#{VDCGroupPage.changeName}" title="Name" immediate="true"/>
                        </h:panelGroup>
                    </h:column>
                    <h:column>
                        <h:inputTextarea cols="100"  id="description" value="#{VDCGroupPage.description}" valueChangeListener="#{VDCGroupPage.changeDescription}" immediate="true" title="Description" />
                    </h:column>
                </h:panelGrid>
                <h:panelGroup layout="block" style="margin-left:auto;margin-right:auto;">
                    <ui:addRemove vertical="false" availableItemsLabel="Available Dataverse(s):" 
                          id="addRemoveList" items="#{VDCGroupPage.addRemoveListDefaultOptions.items}" rows="10"  binding="#{VDCGroupPage.addRemoveList}"
                          selectAll="false" selected="#{VDCGroupPage.addRemoveListDefaultOptions.selected}"
                          selectedItemsLabel="Selected Dataverse(s):" style="margin-left: 15px; margin-top: 10px; margin-bottom: 10px;"/>
                </h:panelGroup>
                <h:panelGrid columns="1" styleClass="dvGroupAdminFooter" columnClasses="groupEditFooter" cellspacing="0">
                    <h:commandButton action="#{VDCGroupPage.update}" value="Save" immediate="true"/>
                    <!-- <h:commandLink action="VDCGroup_list" value="Show All VDCGroup"/> -->
                </h:panelGrid>
            </ui:panelLayout>
        </h:form>
    </f:subview>
</jsp:root>