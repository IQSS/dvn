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
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<title>Facelets: DVN Style</title>
</head>

<body>
<gui:composition template="/template.xhtml">


This text will also not be displayed.

  <gui:define name="body">

        <h:form id="vdcGroupPageForm">
            
            <ui:panelGroup rendered="#{VDCGroupPage.success}" styleClass="successMessage" >
                 <h:messages  layout="list" showDetail="false" showSummary="true"/>
            </ui:panelGroup>
            
            <!--<ui:panelLayout panelLayout="flow" styleClass="dvGroupAdminLayout"> -->
            
            <div class="dvn_section">
                <div class="dvn_sectionTitle">    
                        <h:outputText  value="Dataverse Groups"/> 
                </div>            
                <div class="dvn_sectionBox">
                    <div class="dvn_margin12">
                         <ui:panelGroup block="true" >
                                    <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                                    <h:outputText  styleClass="vdcHelpText" value="You can create dataverse groups to organize the dataverses in the Network Homepage. A dataverse can belong to multiple groups. Any dataverse that is not part of a group will be displayed in an 'Other' category at the bottom of the Network Homepage. 
                                    Keep in mind that if you want to move a dataverse from one group to another, you will need to explicitly remove it from one group and add it to the other. To change the order, enter a display order number, and then click Save. "/>
                        </ui:panelGroup>
                        <br />
                        <br />
                         <ui:panelGroup block="true" >
                            <h:message for="defaultDisplayNumber" styleClass="errorMessage"/>
                         </ui:panelGroup>
                        <h:panelGroup layout="block">
                            <h:outputLabel value="1)  Number of dataverses to display per group, per page."/>
                            <h:inputText id="defaultDisplayNumber" 
                                                        immediate="true"  
                                                        size="4" 
                                                        maxlength="2"
                                                        required="true"
                                                        requiredMessage="This field is required. Please enter the number of dataverses to display."
                                                        value="#{VDCGroupPage.defaultDisplayNumber}" 
                                                        validator="#{VDCGroupPage.validateDivisible}"/>
                        </h:panelGroup>
                        <h:panelGroup layout="block">
                                <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                                    <h:outputText  styleClass="vdcHelpText" value="Each dataverse group will display up to this number of its dataverses on the network home page. Move next and previous with links
                                    at the bottom of each listed dataverse group. To balance the four- column display on the network home page, this number should be divisible by four (4)."/>
                        </h:panelGroup>
                        <br />
                        <br />
                        <h:panelGroup layout="block">
                            <h:outputLabel value="2)  Add/Edit a Dataverse group."/>
                        </h:panelGroup>
                        <h:dataTable rendered="#{VDCGroupPage.VDCGroups != null}" 
                            id="VDCGroups" 
                            value="#{VDCGroupPage.VDCGroups}" 
                            var="item" 
                            cellspacing="0" 
                            styleClass="dvGroupAdminTable" 
                            headerClass="groupAdminHeader" 
                            columnClasses="groupAdminOrderColumn, groupAdminNameColumn, groupAdminDescriptionColumn, groupAdminDeleteColumn" 
                            rowClasses="whiteRow, shadedRow">
                            
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
                                    <h:outputText value="Display Number"/>
                                </f:facet>
                                <h:inputText 
                                id="groupDisplayNumber"
                                required="true" 
                                requiredMessage="This field is required." 
                                size="4" 
                                maxlength="2" 
                                value="#{item.defaultDisplayNumber}"
                                validator="#{VDCGroupPage.validateDivisible}"/>
                                <h:message for="groupDisplayNumber" styleClass="errorMessage"/>
                            </h:column>
                            <h:column>
                                <f:facet name="header">
                                    <h:outputText value="Delete"/>
                                </f:facet>
                                <h:selectBooleanCheckbox valueChangeListener="#{VDCGroupPage.changeSelect}" onchange="submit();" id="selected" immediate="true" label="delete" value="#{item.selected}"/>
                                
                            </h:column>
                        </h:dataTable>
                        
                        <ui:panelGroup rendered="#{VDCGroupPage.VDCGroups == null}" block="true" style="padding-top: 0.5em; padding-bottom: 0.5em;">
                            <h:outputText escape="false" value="There are no Dataverse groups to display"/> 
                        </ui:panelGroup>
                        <h:panelGrid columns="5" styleClass="dvGroupAdminFooter" columnClasses="groupAdminOrderFooter, groupAdminNameFooter, groupAdminDescriptionFooter, groupAdminDeleteFooter" cellspacing="0">
                            <h:column><h:outputText escape="false" value="&lt;!-- placeholder --&gt;"/></h:column>
                            <h:column><h:commandLink id="add" value="Add Group" action="#{VDCGroupPage.addGroup}"/></h:column>
                            <h:column><h:outputText escape="false" value="&lt;!-- placeholder --&gt;"/></h:column>
                            <h:column><h:outputText escape="false" value="&lt;!-- placeholder --&gt;"/></h:column>
                            <h:column><h:commandButton rendered="#{VDCGroupPage.VDCGroups != null}" id="save" value="Save" action="#{VDCGroupPage.save}"/></h:column>
                        </h:panelGrid>
                        
                    </div>
                </div>
            </div>
        </h:form>
            </gui:define>
        </gui:composition>
    </body>
</html>