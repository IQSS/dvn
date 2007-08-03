<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
    xmlns:h="http://java.sun.com/jsf/html" 
    xmlns:jsp="http://java.sun.com/JSP/Page" 
    xmlns:ui="http://www.sun.com/web/ui"
    xmlns:tiles="http://struts.apache.org/tags-tiles"
    xmlns:c="http://java.sun.com/jsp/jstl/core">


    <f:subview id="editCollectionPageView">
                    <ui:form binding="#{EditCollectionPage.form1}" id="form1" virtualFormsConfig="existingCollection | dropdown4 | selectButton, searchResults | dropdown5 textField2 | button4, query | textField1 textArea1 | button5, saveCollections | textField1 addRemoveList2 | saveCollectionButton">
                       <input type="hidden" name="pageName" value="EditCollectionPage"/>
                        <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
                        <h:inputHidden id="collectionId" value="#{EditCollectionPage.collId}"/>
                            <ui:panelLayout binding="#{EditCollectionPage.layoutPanel1}" id="layoutPanel1" panelLayout="flow" styleClass="vdcSectionMiddle">
                                <ui:panelLayout binding="#{EditCollectionPage.layoutPanel2}" id="layoutPanel2" panelLayout="flow" styleClass="vdcSectionHeader">
                                    <h:outputText binding="#{EditCollectionPage.outputText1}" value="Edit Collection"/>
                                </ui:panelLayout>
                                <ui:panelLayout binding="#{EditCollectionPage.layoutPanel3}" id="layoutPanel3" panelLayout="flow" style="padding-left: 30px; padding-top: 20px; padding-bottom: 20px">
                                    <ui:panelGroup binding="#{EditCollectionPage.groupPanel3}" block="true" id="groupPanel3" style="padding-top: 10px; padding-bottom: 10px">                           
                                        <h:outputText styleClass="vdcSubHeaderColor" value="Edit Collection Name:"/>
                                        <ui:panelGroup block="true" style="padding-top: 10px; padding-left: 15px">  
                                            <h:outputText binding="#{EditCollectionPage.outputText7}" id="outputText7" value="Collection Name:"/>
                                            <h:inputText binding="#{EditCollectionPage.textField1}" id="textField1" />
                                        </ui:panelGroup>
                                    </ui:panelGroup>
                                    <ui:panelGroup binding="#{EditCollectionPage.groupPanel4}" block="true" id="groupPanel4" style="padding-top: 10px">
                                        <h:outputText binding="#{EditCollectionPage.outputText10}" id="outputText10" styleClass="vdcSubHeader" value="Option A) Select Studies to Assign to the Collection:"/>
                                         <ui:panelGroup block="true" style="padding-top: 10px; padding-left: 15px"> 
                                            <h:outputText binding="#{EditCollectionPage.outputText11}" id="outputText11" value="1) Choose studies from an existing collection: "/>
                                            <h:selectOneMenu binding="#{EditCollectionPage.dropdown4}" id="dropdown4">
                                                <f:selectItems binding="#{EditCollectionPage.dropdown4SelectItems1}" id="dropdown4SelectItems1" value="#{EditCollectionPage.dropdown4DefaultItems}"/>
                                            </h:selectOneMenu>
                                            <h:commandButton binding="#{EditCollectionPage.selectButton}" id="selectButton" value="Select" actionListener="#{EditCollectionPage.selectCollectionsStudies}"/>
                                         </ui:panelGroup>
                                         <ui:panelGroup block="true" style="padding-top: 10px; padding-left:15px;">   
                                            <h:outputText binding="#{EditCollectionPage.outputText12}" id="outputText12"  value="2) or choose from search results:"/>
                                            <h:selectOneMenu binding="#{EditCollectionPage.dropdown5}" id="dropdown5">
                                                 <f:selectItems binding="#{EditCollectionPage.dropdown5SelectItems1}" id="dropdown5SelectItems1" value="#{EditCollectionPage.dropdown5DefaultItems}"/>
                                            </h:selectOneMenu>
                                            <h:inputText binding="#{EditCollectionPage.textField2}" id="textField2" size="30"/>
                                            <h:commandButton binding="#{EditCollectionPage.button4}" id="button4" value="Search"/>
                                        </ui:panelGroup>
                                        <ui:addRemove vertical="true" availableItemsLabel="Studies to Choose from:" binding="#{EditCollectionPage.addRemoveList2}"
                                            id="addRemoveList2" items="#{EditCollectionPage.addRemoveList2DefaultOptions.options}" rows="10"
                                            selectAll="true" selected="#{EditCollectionPage.addRemoveList2DefaultOptions.selectedValue}"
                                            selectedItemsLabel="Studies Selected:" style="margin-left: 15px; margin-top: 10px; margin-bottom:10px;"/>
                                        
                                        <h:outputText value="Option B) Define Collection as a Query: " styleClass="vdcSubHeader" /> 
                                        <ui:panelGroup  block="true" style="padding-top: 10px; padding-left: 15px">
                                            <h:outputText binding="#{EditCollectionPage.outputText13}" id="outputText13" value="Enter query: "/>
                                            <h:inputText binding="#{EditCollectionPage.textAreaQuery}" size="80" id="textAreaQuery" onkeypress="if (window.event) return processEvent('', 'content:editCollectionPageView:form1:button5'); else return processEvent(event, 'content:editCollectionPageView:form1:button5');"/>
                                            <h:commandButton binding="#{EditCollectionPage.button5}" id="button5" value="Search" action="#{EditCollectionPage.queryStudies}"/>
                                            <ui:panelGroup block="true" style="padding-top: 5px">
                                                    <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                                                    <h:outputText  styleClass="vdcHelpText" escape="false" value="E.g., title: &quot;The Right Way&quot; AND abstractText:world - For more information on the query syntax, see &lt;a href='http://lucene.apache.org/java/docs/queryparsersyntax.html' style='font-size: 1em' target='_blank'&gt;Lucene website&lt;/a&gt;." />
                                             </ui:panelGroup> 
                                        </ui:panelGroup>
                                        
                                        <ui:panelGroup binding="#{EditCollectionPage.groupPanel9}" block="true" id="groupPanel9" style="padding-top: 30px;">
                                            <h:commandButton binding="#{EditCollectionPage.saveCollectionButton}" id="saveCollectionButton" value="Save Collection" action="#{EditCollectionPage.saveCollection}"/>
                                            <h:commandButton binding="#{EditCollectionPage.button7}" id="button7" style="margin-left: 30px; margin-right: 30px" value="Cancel" action="#{AddCollectionsPage.cancel}"/>
                                        </ui:panelGroup>
                                    </ui:panelGroup>
                                </ui:panelLayout>
                            </ui:panelLayout>
                    </ui:form>
    </f:subview>
</jsp:root>
