<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
    xmlns:h="http://java.sun.com/jsf/html" 
    xmlns:jsp="http://java.sun.com/JSP/Page" 
    xmlns:ui="http://www.sun.com/web/ui"
    xmlns:tiles="http://struts.apache.org/tags-tiles"
    xmlns:c="http://java.sun.com/jsp/jstl/core">

    <f:subview id="addCollectionsPageView">
                    <ui:form binding="#{AddCollectionsPage.form1}" id="form1" virtualFormsConfig="existingCollection | dropdown4 | button5, searchResults | dropdown5 textField1 | button1, query | textField2 dropdown3 textArea1 | button2, saveCollections | textField2 dropdown3 addRemoveList1 | button3">
                        <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>                       
                            <ui:panelLayout binding="#{AddCollectionsPage.layoutPanel1}" id="layoutPanel1" panelLayout="flow" styleClass="vdcSectionMiddle">
                                <ui:panelLayout binding="#{AddCollectionsPage.layoutPanel2}" id="layoutPanel2" panelLayout="flow" styleClass="vdcSectionHeader">
                                    <h:outputText binding="#{AddCollectionsPage.outputText1}" value="Add a Collection"/>
                                </ui:panelLayout>
                                <ui:panelLayout binding="#{AddCollectionsPage.layoutPanel3}" id="layoutPanel3" panelLayout="flow" style="padding-left: 30px; padding-top: 20px; padding-bottom: 20px">
                                    <ui:panelGroup binding="#{AddCollectionsPage.groupPanel2}" block="true" id="groupPanel2" style="padding-top: 10px; padding-bottom: 10px">
                                        <h:outputText binding="#{AddCollectionsPage.outputText3}" id="outputText3" styleClass="vdcSubHeaderColor" value="Define a new collection:"/>
                                        <ui:panelGroup block="true" style="padding-top: 10px; padding-left: 15px">
                                            <h:outputText value="Collection Name: "/>
                                            <h:inputText binding="#{AddCollectionsPage.textFieldCollectionName}" required="true" id="textField2"/>
                                            <h:message for="textField2" showSummary="true" showDetail="false" errorClass="errorMessage"/>
                                            <h:outputText binding="#{AddCollectionsPage.outputText5}" id="outputText5" value="Add to node: "/>
                                            <h:selectOneMenu binding="#{AddCollectionsPage.dropdown3}" id="dropdown3">
                                                <f:selectItems binding="#{AddCollectionsPage.dropdown2SelectItems1}" id="dropdown2SelectItems1" value="#{AddCollectionsPage.dropdown2DefaultItems}"/>
                                            </h:selectOneMenu>
                                        </ui:panelGroup>
                                        
                                         <ui:panelGroup block="true" style="padding-top: 10px; padding-left: 15px, padding-right: 15px;">    
                                            <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                                            <h:outputText  styleClass="vdcHelpText" value="You can define a collection by selecting studies and assign them directly to the collection or by defining a query (in this case, any new - or modified - study that satisfies the query will be automatically added to the collection)."/>
                                         </ui:panelGroup>
                                    </ui:panelGroup>
                                    <ui:panelGroup binding="#{AddCollectionsPage.groupPanel3}" block="true" id="groupPanel3" style="padding-top: 10px;">
                                        <h:outputText binding="#{AddCollectionsPage.outputText7}" id="outputText7" styleClass="vdcSubHeader" value="Option A) Select Studies to Assign to the Collection:"/>
                                        <ui:panelGroup block="true" style="padding-top: 10px; padding-left:15px;"> 
                                            <h:outputText binding="#{AddCollectionsPage.outputText8}" id="outputText8" value="1) Choose studies from an existing collection: "/>
                                            <h:selectOneMenu binding="#{AddCollectionsPage.dropdown4}" id="dropdown4">
                                                 <f:selectItems binding="#{AddCollectionsPage.dropdown4SelectItems}" id="dropdown4SelectItems" value="#{AddCollectionsPage.dropdown4DefaultItems}"/>
                                            </h:selectOneMenu>
                                            <h:commandButton binding="#{AddCollectionsPage.button5}" id="button5" value="Select" actionListener="#{AddCollectionsPage.selectCollectionsStudies}"/>
                                        </ui:panelGroup>
                                        <ui:panelGroup block="true" style="padding-top: 10px; padding-left:15px;">
                                            <h:outputText binding="#{AddCollectionsPage.outputText9}" id="outputText9"  value="2) or choose from search results:"/>
                                            <h:selectOneMenu  id="dropdown5" value="#{AddCollectionsPage.searchField}">
                                                <f:selectItem   itemLabel="Everything" itemValue="ANY" />
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
                                            <h:commandButton binding="#{AddCollectionsPage.button1}" id="button1" value="Search" actionListener="#{AddCollectionsPage.searchStudies}"/>                                                
                                        </ui:panelGroup>
                                        
                                        <ui:addRemove vertical="true"  availableItemsLabel="Studies to Choose from:" binding="#{AddCollectionsPage.addRemoveList1}"
                                            id="addRemoveList1" items="#{AddCollectionsPage.addRemoveList1DefaultOptions.options}" rows="10" 
                                            selectAll="true" selected="#{AddCollectionsPage.addRemoveList1DefaultOptions.selectedValue}"
                                            selectedItemsLabel="Studies Selected:" style="margin-left: 15px; margin-top: 10px; margin-bottom: 10px;"/>
                                        
                                         <h:outputText styleClass="vdcSubHeader" value="Option B) Define Collection as a Query: "/>  
                                         <ui:panelGroup block="true" style="padding-top: 10px; padding-left: 15px">   
                                             <h:outputText binding="#{AddCollectionsPage.outputText10}" id="outputText10" value="Enter query: "/>
                                             <h:inputText binding="#{AddCollectionsPage.textAreaQuery}" size="80" id="textArea1" required="true" onkeypress="if (window.event) return processEvent('', 'content:addCollectionsPageView:form1:button2'); else return processEvent(event, 'content:addCollectionsPageView:form1:button2');"/>
                                             <h:message for="textArea1" showSummary="true" showDetail="false"  errorClass="errorMessage"/>
                                             <h:commandButton binding="#{AddCollectionsPage.button2}" id="button2" value="Query" action="#{AddCollectionsPage.queryStudies}"/>
                                             <ui:panelGroup block="true" style="padding-top: 5px">
                                                 <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                                                 <h:outputText  styleClass="vdcHelpText" escape="false" value="E.g., title: &quot;The Right Way&quot; AND abstractText:world - For more information on the query syntax, see &lt;a href='http://lucene.apache.org/java/docs/queryparsersyntax.html' style='font-size: 1em' target='_blank'&gt;Lucene website&lt;/a&gt;." />
                                             </ui:panelGroup>   
                                        </ui:panelGroup>
                                                                               
                                        <ui:panelGroup binding="#{AddCollectionsPage.groupPanel6}" block="true" id="groupPanel6" style="padding-top: 30px; ">
                                            <h:commandButton binding="#{AddCollectionsPage.button3}" id="button3" value="Save Collection" action="#{AddCollectionsPage.saveCollection}"/>
                                            <h:commandButton binding="#{AddCollectionsPage.button4}" id="button4" immediate="true" 
                                                style="margin-left: 30px;" value="Cancel" action="#{AddCollectionsPage.cancel}"/>
                                        </ui:panelGroup>
                                    </ui:panelGroup>
                                </ui:panelLayout>                             
                            </ui:panelLayout>                     
                    </ui:form>
    </f:subview>
</jsp:root>
