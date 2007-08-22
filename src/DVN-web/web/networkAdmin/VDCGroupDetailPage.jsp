<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
          xmlns:h="http://java.sun.com/jsf/html" 
          xmlns:jsp="http://java.sun.com/JSP/Page" 
          xmlns:ui="http://www.sun.com/web/ui"
          xmlns:tiles="http://struts.apache.org/tags-tiles">
    <f:subview id="vDCGroupPageView">
        <h:form id="vdcGroupPageForm">
            
            <ui:panelGroup rendered="#{VDCGroupPage.success}" styleClass="successMessage" >
                 <h:messages  layout="table" showDetail="false" showSummary="true"/>
            </ui:panelGroup>
            
            <!--<ui:panelLayout panelLayout="flow" styleClass="dvGroupAdminLayout"> -->
            
            <div class="dvn_section">
                <div class="dvn_sectionTitle">
                    <h3>
                        <h:outputText  value="Dataverse Groups"/>
                    </h3>
                </div>            
                <div class="dvn_sectionBox">
                    <div class="dvn_margin12">
                        <h:dataTable rendered="#{VDCGroupPage.VDCGroups != null}" id="VDCGroups" value="#{VDCGroupPage.VDCGroups}" var="item" cellspacing="0" styleClass="dvGroupAdminTable" headerClass="groupAdminHeader" columnClasses="groupAdminOrderColumn, groupAdminNameColumn, groupAdminDescriptionColumn, groupAdminDeleteColumn" rowClasses="whiteRow, shadedRow">
                            
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
                                <h:commandLink id="editGroup" action="#{VDCGroupPage.edit}" value="#{item.name}" onclick="submit()" immediate="true"/>
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
                        <h:panelGrid columns="1" rendered="#{VDCGroupPage.VDCGroups == null}" style="width:100%" cellspacing="0" styleClass="dvGroupAdminTable" headerClass="groupAdminHeader" columnClasses="groupAdminNoneColumn">
                            <f:facet name="caption">
                                <ui:panelGroup id="dvGroupNoneCaption" block="true" styleClass="vdcSectionHeader">
                                    <h:outputText id="dataNoneCaption" value="Dataverse Groups"/>
                                </ui:panelGroup>
                            </f:facet> 
                            <h:outputText rendered="#{VDCGroupPage.VDCGroups == null}" escape="false" value="There are no Dataverse groups to display"/>
                        </h:panelGrid>
                        <h:panelGrid columns="4" styleClass="dvGroupAdminFooter" columnClasses="groupAdminOrderFooter, groupAdminNameFooter, groupAdminDescriptionFooter, groupAdminDeleteFooter" cellspacing="0">
                            <h:column><h:outputText escape="false" value="&lt;!-- placeholder --&gt;"/></h:column>
                            <h:column><h:commandLink id="add" value="Add Group" action="#{VDCGroupPage.addGroup}"/></h:column>
                            <h:column><h:outputText escape="false" value="&lt;!-- placeholder --&gt;"/></h:column>
                            <h:column><h:commandButton rendered="#{VDCGroupPage.VDCGroups != null}" id="save" value="Save" action="#{VDCGroupPage.save}"/></h:column>
                        </h:panelGrid>
                        
                    </div>
                </div>
            </div>
        </h:form>
    </f:subview>
</jsp:root>