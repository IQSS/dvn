<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
    xmlns:h="http://java.sun.com/jsf/html" 
    xmlns:jsp="http://java.sun.com/JSP/Page" 
    xmlns:ui="http://www.sun.com/web/ui"
    xmlns:tiles="http://struts.apache.org/tags-tiles"
    xmlns:c="http://java.sun.com/jsp/jstl/core">

    <f:subview id="manageCollectionsPageView">
                       
                    <ui:form binding="#{ManageCollectionsPage.form1}" id="form1">   
                     <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
                            <ui:panelLayout binding="#{ManageCollectionsPage.layoutPanel1}" id="layoutPanel1" panelLayout="flow" styleClass="vdcSectionMiddle">
                                <ui:panelLayout binding="#{ManageCollectionsPage.layoutPanel2}" id="layoutPanel2" panelLayout="flow" styleClass="vdcSectionHeader">
                                    <h:outputText binding="#{ManageCollectionsPage.outputText1}" value="Manage Collections"/>
                                </ui:panelLayout>
                                <ui:panelLayout panelLayout="flow" styleClass="vdcSectionMiddleMessage" style="width: 300px; margin-top: 10px; margin-bottom: -10px" rendered="#{!empty ManageCollectionsPage.msg.messageText}">
                                  <ui:panelGroup block="true" styleClass="#{ManageCollectionsPage.msg.styleClass}">
                                    <h:outputText id="statusMessage" value="#{ManageCollectionsPage.msg.messageText}" />
                                  </ui:panelGroup>
                                </ui:panelLayout>
                                <ui:panelLayout binding="#{ManageCollectionsPage.layoutPanel3}" id="layoutPanel3" panelLayout="flow" style="padding-left: 10px; padding-right: 10px; padding-top: 20px; padding-bottom: 20px">

                                    <!--
                                    <ui:panelGroup block="true" style="padding-bottom:10px">
                                        <h:outputLink  id="hyperlinkAddCollection" value="/dvn#{VDCRequest.currentVDCURL}/faces/collection/AddCollectionsPage.jsp">
                                            <h:outputText  id="hyperlinkAddCollectionText" value="Add Collection"/>
                                        </h:outputLink>                                    
                                    </ui:panelGroup>
                                    -->
                                    <ui:panelGroup block="true" style="padding-bottom:10px">
                                        <h:outputLink id="hyperlinkAddStudiesCollection" value="/dvn#{VDCRequest.currentVDCURL}/faces/collection/AddCollectionStudiesPage.jsp">
                                            <h:outputText  id="hyperlink3Text2" value="Add Collection by Assigning Studies"/>
                                        </h:outputLink>
                                        <verbatim> | </verbatim>
                                        <h:outputLink id="hyperlinkAddQueryCollection" value="/dvn#{VDCRequest.currentVDCURL}/faces/collection/AddCollectionQueryPage.jsp">
                                            <h:outputText  id="hyperlink3Text1" value="Add Collection as a Query"/>
                                        </h:outputLink>
                                        <verbatim> | </verbatim>
                                        <h:outputLink  value="/dvn#{VDCRequest.currentVDCURL}/faces/collection/AddLinkPage.jsp">
                                            <h:outputText value="Add Collection Link"/>
                                        </h:outputLink>
                                    </ui:panelGroup>
                                    <ui:panelGroup block="true">    
                                        <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                                        <h:outputText  styleClass="vdcHelpText" value="Select a collection before applying one of the actions below. Collection Links (links to collection from other Dataverses) are only editable in their original Dataverse."/>
                                    </ui:panelGroup>
                                    
                                    <ui:panelGroup binding="#{ManageCollectionsPage.groupPanel1}" block="true" id="groupPanel1" style="background-color: rgb(226, 226, 226); font-size: 1em; margin-right: 10px; margin-top: 20px; margin-bottom: 5px; padding-top: 10px; padding-bottom: 5px">
                                        <h:outputText binding="#{ManageCollectionsPage.outputText2}" id="outputText2" style="padding-left: 30px" value="Change Status to: "/>
                                        <h:selectOneMenu binding="#{ManageCollectionsPage.dropdown1}" id="dropdown1">
                                            <f:selectItems binding="#{ManageCollectionsPage.dropdown1SelectItems}" id="dropdown1SelectItems" value="#{ManageCollectionsPage.dropdown1DefaultItems}"/>
                                        </h:selectOneMenu>
                                        <h:commandButton binding="#{ManageCollectionsPage.changeStatusButton}" action="#{ManageCollectionsPage.changeStatusButton_action}" id="changeStatusButton" value="Change Status"/>
                                        <h:commandButton binding="#{ManageCollectionsPage.removeButton}" action="#{ManageCollectionsPage.removeButton_action}" id="removeButton" style="margin-left: 30px"
                                             value="Remove"/>
                                    </ui:panelGroup>
                                    <h:dataTable binding="#{ManageCollectionsPage.dataTable1}" cellpadding="0" cellspacing="0" headerClass="" id="dataTable1"
                                        style="padding-left: 10px; margin-top: 20px" columnClasses="vdcColPadded" value="#{ManageCollectionsPage.dataTable1Model}" var="currentRow" >
                                        <h:column binding="#{ManageCollectionsPage.column1}" id="column1">
                                            <h:selectBooleanCheckbox  id="checkbox1" style="margin-left: 10px; margin-right:  #{currentRow['level']}0px;"  value="#{currentRow['selected']}"/>
                                            <h:outputLink disabled="#{currentRow['link']}" id="hyperlink3" value="/dvn#{VDCRequest.currentVDCURL}/faces/collection/#{currentRow['queryType'] ? 'AddCollectionQueryPage' : 'AddCollectionStudiesPage' }.jsp?collectionId=#{currentRow['id']}&amp;parentId=#{currentRow['parentId']}">
                                                <h:outputText binding="#{ManageCollectionsPage.outputText11}" id="outputText11" value="#{currentRow['name']}"/>
                                            </h:outputLink>
                                        </h:column>
                                    </h:dataTable>
                                </ui:panelLayout>
                            </ui:panelLayout>
                    </ui:form>
    </f:subview>
</jsp:root>
