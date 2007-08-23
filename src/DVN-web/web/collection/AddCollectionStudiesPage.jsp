<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
    xmlns:h="http://java.sun.com/jsf/html" 
    xmlns:jsp="http://java.sun.com/JSP/Page" 
    xmlns:ui="http://www.sun.com/web/ui"
    xmlns:tiles="http://struts.apache.org/tags-tiles"
    xmlns:c="http://java.sun.com/jsp/jstl/core">

    <f:subview id="addCollectionStudiesPageView">
        <ui:form id="form1" >
            <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>                       
            <h:inputHidden id="edit" value="#{AddCollectionsPage.edit}"/>                       
            <h:inputHidden id="collId" value="#{AddCollectionsPage.collId}"/>                       
            <h:inputHidden id="parentId" value="#{AddCollectionsPage.parentId}"/>  
                        
            <div class="dvn_section">
                <div class="dvn_sectionTitle">
                                         
                        <h:outputText value="Collection by Assigning Studies"/>
                    
                </div>            
                <div class="dvn_sectionBox">
                    <div class="dvn_margin12">  
                        <ui:panelGroup block="true" style="padding-top: 10px; padding-left: 5px">
                            <h:outputText value="Enter collection name: "/>
                            <h:inputText binding="#{AddCollectionsPage.textFieldCollectionName}" required="true" id="textFieldCollectionName"/>
                            <h:message for="textFieldCollectionName" showSummary="true" showDetail="false" errorClass="errorMessage"/>
                            <h:outputText  id="outputText5" value="Choose a parent: "/>
                            <h:selectOneMenu binding="#{AddCollectionsPage.dropdown3}" id="dropdown3">
                                <f:selectItems  id="dropdown2SelectItems1" value="#{AddCollectionsPage.dropdown2DefaultItems}"/>
                            </h:selectOneMenu>
                        </ui:panelGroup>                                                                                        
                        
                        <ui:panelGroup  block="true" id="groupPanel3" style="padding-top: 10px;">
                            <h:outputText  id="outputText7" styleClass="vdcSubHeader" value="Assign Studies to Collection:"/>
                            <ui:panelGroup block="true" style="padding-top: 10px; padding-left: 15px, padding-right: 15px;">    
                                <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                                <h:outputText  styleClass="vdcHelpText" value="Here you can define a collection by selecting studies and assign them directly to the collection. First, select studies from an existing collection or search for studies, and then add studies to the collection."/>
                            </ui:panelGroup>
                            <ui:panelGroup block="true" style="padding-top: 10px; padding-left:15px;"> 
                                <h:outputText  id="outputText8" value="1) Choose studies from an existing collection: "/>
                                <h:selectOneMenu binding="#{AddCollectionsPage.dropdown4}"  id="dropdown4">
                                    <f:selectItems id="dropdown4SelectItems" value="#{AddCollectionsPage.dropdown4DefaultItems}"/>
                                </h:selectOneMenu>
                                <h:commandButton id="button5" value="Select" actionListener="#{AddCollectionsPage.selectCollectionsStudies}"/>
                            </ui:panelGroup>
                            <ui:panelGroup block="true" style="padding-top: 10px; padding-left:15px;">
                                <h:outputText  id="outputText9"  value="2) or choose from search results:"/>
                                <h:selectOneMenu  id="dropdown5" value="#{AddCollectionsPage.searchField}">
                                    <f:selectItem   itemLabel="Cataloging Information" itemValue="any" />
                                    <f:selectItem   itemLabel="- Author" itemValue="authorName" />
                                    <f:selectItem   itemLabel="- Title" itemValue="title" />
                                    <f:selectItem   itemLabel="- Study ID" itemValue="studyId" />
                                    <f:selectItem   itemLabel="Variable Information" itemValue="variable" />
                                    <f:selectItem   itemLabel="Files" itemValue="file" />
                                </h:selectOneMenu>
                                <h:inputText  id="textField1" value="#{AddCollectionsPage.searchValue}"/>
                                <!--
                                            <h:selectOneMenu binding="#{AddCollectionsPage.dropdown5}" id="dropdown5">
                                                <f:selectItems binding="#{AddCollectionsPage.dropdown5SelectItems}" id="dropdown5SelectItems" value="#{AddCollectionsPage.dropdown5DefaultItems}"/>
                                            </h:selectOneMenu> 
                                             <h:inputText binding="#{AddCollectionsPage.textField1}" id="textField1" required="true" size="30"/>
                                            -->
                                <h:message for="textField1" showSummary="true" showDetail="false" errorClass="errorMessage"/>
                                <h:commandButton id="button1" value="Search" actionListener="#{AddCollectionsPage.searchStudies}"/>                                                
                            </ui:panelGroup>
                            
                            <ui:addRemove vertical="true"  availableItemsLabel="Studies to Choose from:" 
                                          id="addRemoveList1" items="#{AddCollectionsPage.addRemoveList1DefaultOptions.options}" rows="10"  binding="#{AddCollectionsPage.addRemoveList1}"
                                          selectAll="true" selected="#{AddCollectionsPage.addRemoveList1DefaultOptions.selectedValue}"
                                          selectedItemsLabel="Studies Selected:" style="margin-left: 15px; margin-top: 10px; margin-bottom: 10px;"/>
                            
                            <ui:panelGroup  block="true" id="groupPanel6" style="padding-top: 30px; ">
                                <h:commandButton id="button3" value="Save Collection" action="#{AddCollectionsPage.saveCollection}"/>
                                <h:commandButton id="button4" immediate="true" 
                                                 style="margin-left: 30px;" value="Cancel" action="#{AddCollectionsPage.cancel}"/>
                            </ui:panelGroup>
                        </ui:panelGroup>
                    </div>
                </div>
            </div>
        </ui:form>
    </f:subview>
</jsp:root>
