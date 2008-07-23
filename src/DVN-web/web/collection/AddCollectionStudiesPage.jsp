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
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>

</head>

<body>
<gui:composition template="/template.xhtml">

<gui:param name="pageTitle" value="DVN - Add/Edit Collection" />

<gui:define name="body">
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
                        <div style="padding-top: 10px; padding-left: 5px">
                            <h:outputText value="Enter collection name: "/>
                            <h:inputText binding="#{AddCollectionsPage.textFieldCollectionName}" required="true" requiredMessage="This field is required." id="textFieldCollectionName" validator="#{AddCollectionsPage.validateName}"/>
                            <h:message for="textFieldCollectionName" showSummary="true" showDetail="false" styleClass="errorMessage"/>
                            <h:outputText  id="outputText5" value="Choose a parent: "/>
                            <h:selectOneMenu binding="#{AddCollectionsPage.dropdown3}" id="dropdown3">
                                <f:selectItems  id="dropdown2SelectItems1" value="#{AddCollectionsPage.dropdown2DefaultItems}"/>
                            </h:selectOneMenu>
                        </div>                                                                                        
                        
                        <div style="padding-top: 10px;">
                            <h:outputText  id="outputText7" styleClass="vdcSubHeader" value="Assign Studies to Collection:"/>
                            <div style="padding-top:10px; padding-left:15px; padding-right:15px;">    
                                <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                                <h:outputText  styleClass="vdcHelpText" value="Here you can define a collection by selecting studies and assign them directly to the collection. First, select studies from an existing collection or search for studies, and then add studies to the collection."/>
                            </div>
                            <div style="padding-top: 10px; padding-left:15px;"> 
                                <h:outputText  id="outputText8" value="1) Choose studies from an existing collection: "/>
                                <h:selectOneMenu binding="#{AddCollectionsPage.dropdown4}"  id="dropdown4">
                                    <f:selectItems id="dropdown4SelectItems" value="#{AddCollectionsPage.dropdown4DefaultItems}"/>
                                </h:selectOneMenu>
                                <h:commandButton id="button5" value="Select" actionListener="#{AddCollectionsPage.selectCollectionsStudies}"/>
                            </div>
                            <div style="padding-top: 10px; padding-left:15px;">
                                <h:outputText  id="outputText9"  value="2) or choose from search results:"/>
                                <h:selectOneMenu  id="dropdown5" value="#{AddCollectionsPage.searchField}">
                                    <f:selectItem   itemLabel="Cataloging Information" itemValue="any" />
                                    <f:selectItem   itemLabel="- Author" itemValue="authorName" />
                                    <f:selectItem   itemLabel="- Title" itemValue="title" />
                                    <f:selectItem   itemLabel="- Global ID" itemValue="globalId" />
                                </h:selectOneMenu>
                                <h:inputText  id="textField1" value="#{AddCollectionsPage.searchValue}"/>
                              
                                <h:message for="textField1" showSummary="true" showDetail="false" errorClass="errorMessage"/>
                                <h:commandButton id="button1" value="Search" actionListener="#{AddCollectionsPage.searchStudies}"/>                                                
                            </div>
                            
                            <ui:addRemove vertical="true"  availableItemsLabel="Studies to Choose from:" 
                                          id="addRemoveList1" items="#{AddCollectionsPage.addRemoveList1DefaultOptions.options}" rows="10"  binding="#{AddCollectionsPage.addRemoveList1}"
                                          selectAll="true" selected="#{AddCollectionsPage.addRemoveList1DefaultOptions.selectedValue}"
                                          selectedItemsLabel="Studies Selected:" style="margin-left: 15px; margin-top: 10px; margin-bottom: 10px;"/>
                            
                            <div style="padding-top: 30px; ">
                                <h:commandButton id="button3" value="Save Collection" action="#{AddCollectionsPage.saveCollection}"/>
                                <h:commandButton id="button4" immediate="true" 
                                                 style="margin-left: 30px;" value="Cancel" action="#{AddCollectionsPage.cancel}"/>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </ui:form>
            </gui:define>
        </gui:composition>
    </body>
</html>
