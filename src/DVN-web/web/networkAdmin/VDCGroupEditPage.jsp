<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
          xmlns:h="http://java.sun.com/jsf/html" 
          xmlns:jsp="http://java.sun.com/JSP/Page" 
          xmlns:ui="http://www.sun.com/web/ui"
          xmlns:tiles="http://struts.apache.org/tags-tiles"
          xmlns:webuijsf="http://www.sun.com/webui/webuijsf"
          >
<f:subview id="vDCGroupEditView">
    <f:verbatim>
        <script type="text/javascript">
            //<![CDATA[
                function getAddRemoveLength() {
                    if (document.getElementById('content:vDCGroupEditView:vdcGroupEditForm:addRemoveList_selected').length > 1) {
                        return true;
                    } else {
                        alert("Validation Error: There must be at least one entry in the Selected Dataverse(s) list." + "\n\r" + "Please add a Dataverse to continue.");
                        return false;
                    }
                }

                //workaround for the issue in Opera where the dvs are not getting added to the group.
                 function highlightSelectedItems() {
                    for (var i = 0; i < document.getElementById('content:vDCGroupEditView:vdcGroupEditForm:addRemoveList_list_value').length; i++) {
                        document.getElementById('content:vDCGroupEditView:vdcGroupEditForm:addRemoveList_list_value').options[i].selected = true;
                    }
                }
            // ]]>
        </script>
    </f:verbatim>
    <h:form onclick="highlightSelectedItems();" id="vdcGroupEditForm" onsubmit="return getAddRemoveLength();">

        <h:messages layout="list" showDetail="false" showSummary="true" styleClass="successMessage" />

            <div class="dvn_section">
                <div class="dvn_sectionTitle">    
                    <h:outputText  value="Dataverse Group"/> 
                </div>            
                <div class="dvn_sectionBox">
                    <div class="dvn_margin12"> 
                        
                        <h:inputHidden id="vdcGroupId" value="#{ (VDCGroupPage.vdcGroupId != null) ? VDCGroupPage.vdcGroupId : requestScope.vdcGroupId}" immediate="true"/>
                        <h:panelGrid columns="2" cellspacing="0" styleClass="dvGroupAdminTable" headerClass="groupAdminHeader"  columnClasses="groupEditNameColumn, groupEditDescriptionColumn">
                            
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
                                    <h:message for="name" styleClass="errorMessage"/>
                                </h:panelGroup>
                            </h:column>
                            <h:column>
                                <h:inputTextarea cols="100"  id="description" value="#{VDCGroupPage.description}" valueChangeListener="#{VDCGroupPage.changeDescription}" immediate="true" title="Description" />
                            </h:column>
                        </h:panelGrid>
                        <h:panelGrid columns="1" style="width:98%" cellspacing="0" styleClass="dvGroupAdminTable" headerClass="groupAdminHeader" columnClasses="groupAdminNoneColumn">
                            <ui:addRemove  vertical="true" availableItemsLabel="All Dataverses:" 
                                          id="addRemoveList" items="#{VDCGroupPage.addRemoveListDefaultOptions.items}" rows="8"  binding="#{VDCGroupPage.addRemoveList}"
                                          selectAll="false" selected="#{VDCGroupPage.addRemoveListDefaultOptions.selected}"
                                          selectedItemsLabel="Dataverse(s) in this Group:" style="margin-top: 10px; margin-bottom: 10px;"/>
                        </h:panelGrid>
                        <h:panelGrid columns="1" styleClass="dvGroupAdminFooter" columnClasses="groupEditFooter" cellspacing="0">
                            <h:commandButton id="btnSave" action="#{VDCGroupPage.update}" value="Save" immediate="true"/>
                        </h:panelGrid>
                        
                    </div>
                </div>
            </div>
        </h:form>
    </f:subview>
</jsp:root>