<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
    xmlns:h="http://java.sun.com/jsf/html" 
    xmlns:jsp="http://java.sun.com/JSP/Page" 
    xmlns:ui="http://www.sun.com/web/ui"
    xmlns:tiles="http://struts.apache.org/tags-tiles"
    xmlns:c="http://java.sun.com/jsp/jstl/core">

    <f:subview id="searchFieldsPageView">
                    <ui:form  id="form1">
                        <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
                            <ui:panelLayout id="layoutPanel1" panelLayout="flow" styleClass="vdcSectionMiddle">
                                <ui:panelLayout  id="layoutPanel2" panelLayout="flow" styleClass="vdcSectionHeader">
                                    <h:outputText value="Additional Fields in Search Results"/>
                                </ui:panelLayout>
                                <ui:panelLayout panelLayout="flow" styleClass="vdcSectionMiddleMessage" style="width: 300px; margin-top: 10px; margin-bottom: -10px" rendered="#{!empty SearchFieldsPage.msg.messageText}">
                                  <ui:panelGroup block="true" styleClass="#{SearchFieldsPage.msg.styleClass}">
                                    <h:outputText id="statusMessage" value="#{SearchFieldsPage.msg.messageText}" />
                                  </ui:panelGroup>
                                </ui:panelLayout>
                                <ui:panelLayout  id="layoutPanel3" panelLayout="flow" style="padding-top: 30px; padding-bottom: 30px; padding-left: 50px; padding-right: 50px">
                                    
                                <ui:panelGroup  block="true" >                                             
                                    <h:outputText value="Select any of the cataloging information fields below to be displayed in the search results page (or studies listing). If selected, these fields will be displayed below the default information (Study ID, title, authors, production date, abstract), and will only be displayed if they are available in the study cataloging information. These settings apply to this Dataverse only."  />
                                </ui:panelGroup>
                                
                                <ui:panelGroup  block="true" style="padding-top: 10px;">
                                    <h:selectBooleanCheckbox binding="#{SearchFieldsPage.producerCheckbox}" id="producercheckbox" value="#{SearchFieldsPage.producerResults}" /> 
                                    <h:outputLabel for="producercheckbox" value="Producer"/>
                                    <ui:panelGroup  block="true" style="padding-left: 20px;">
                                        <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />  
                                        <h:outputText value="The Producer name, logo and URL will be displayed, if they are available." styleClass="vdcHelpText" />
                                    </ui:panelGroup>
                                </ui:panelGroup>
                                
                                <ui:panelGroup  block="true" style="padding-top: 10px;">
                                    <h:selectBooleanCheckbox binding="#{SearchFieldsPage.distributionDateCheckbox}" id="distributiondatecheckbox"  value="#{SearchFieldsPage.distributionDateResults}"/> 
                                    <h:outputLabel for="distributiondatecheckbox" value="Distribution Date"/>
                                </ui:panelGroup>
                                
                                <ui:panelGroup  block="true" style="padding-top: 10px;">
                                    <h:selectBooleanCheckbox binding="#{SearchFieldsPage.distributorCheckbox}" id="distributorcheckbox" value="#{SearchFieldsPage.distributorResults}"/> 
                                    <h:outputLabel for="distributorcheckbox" value="Distributor"/>
                                    <ui:panelGroup  block="true" style="padding-left: 20px;">
                                        <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />  
                                        <h:outputText value="The Distributor name, logo and URL will be displayed, if they are available." styleClass="vdcHelpText" />
                                    </ui:panelGroup>
                                </ui:panelGroup>
                                
                                <ui:panelGroup  block="true" style="padding-top: 10px;">
                                    <h:selectBooleanCheckbox binding="#{SearchFieldsPage.replicationCheckbox}" id="replicationcheckbox"  value="#{SearchFieldsPage.replicationForResults}"/> 
                                    <h:outputLabel for="replicationcheckbox" value="Replication For"/>
                                </ui:panelGroup>

                                <ui:panelGroup  block="true" style="padding-top: 10px;">
                                    <h:selectBooleanCheckbox binding="#{SearchFieldsPage.relatedpubCheckbox}" id="relatedpubcheckbox" value="#{SearchFieldsPage.relatedPublicationsResults}"/> 
                                    <h:outputLabel for="relatedpubcheckbox" value="Related Publications"/>
                                </ui:panelGroup>
                                
                                <ui:panelGroup  block="true" style="padding-top: 10px;">
                                    <h:selectBooleanCheckbox binding="#{SearchFieldsPage.relatedmatCheckbox}" id="relatedmatcheckbox"  value="#{SearchFieldsPage.relatedMaterialResults}"/> 
                                    <h:outputLabel for="relatedmatcheckbox" value="Related Material"/>
                                </ui:panelGroup>
                                
                                <ui:panelGroup  block="true" style="padding-top: 10px;">
                                    <h:selectBooleanCheckbox binding="#{SearchFieldsPage.relatedstudiesCheckbox}" id="relatedstudiescheckbox" value="#{SearchFieldsPage.relatedStudiesResults}"/> 
                                    <h:outputLabel for="relatedstudiescheckbox" value="Related Studies"/>
                                </ui:panelGroup>
                                
                                <ui:panelGroup  block="true"  style="padding-top: 20px" >
                                        <h:commandButton  id="saveButton" value="Save"  action="#{SearchFieldsPage.save}"/>
                                        <h:commandButton  id="cancelButton" value="Cancel" style="margin-left: 30px" action="#{SearchFieldsPage.cancel}"/>
                                </ui:panelGroup>
                                
<!-- Comment this out since we are changing this page  - TO BE REMOVED LATER                            
                                    <ui:helpInline binding="#{SearchFieldsPage.helpInline1}" id="helpInline1" text="The list of search fields on the left includes everything that is getting indexed by the Index Server. &#xa;Choose want you would like to display to the users in the searches pull-downs and the search results page.&#xa;"/>
                                    <ui:panelGroup binding="#{SearchFieldsPage.groupPanel3}" block="true" id="groupPanel3" style="padding-bottom: 10px" styleClass="vdcTextRight">
                                        <h:commandButton binding="#{SearchFieldsPage.button3}" id="button3" value="Update"  action="#{SearchFieldsPage.update}"/>
                                    </ui:panelGroup>
                                    <h:dataTable binding="#{SearchFieldsPage.dataTable1}" cellpadding="0" cellspacing="0"
                                        columnClasses="vdcColPadded, vdcColPadded, vdcColPadded" headerClass="list-header-left" id="dataTable1"
                                        rowClasses="list-row-even,list-row-odd" value="#{SearchFieldsPage.studyFields}" var="currentRow" width="100%">
                                        <h:column binding="#{SearchFieldsPage.column1}" id="column1">
                                            <f:facet name="header">
                                                <h:outputText binding="#{SearchFieldsPage.outputText3}" id="outputText3" value="Search Fields"/>
                                            </f:facet>
                           
                                            <h:outputText binding="#{SearchFieldsPage.hyperlink2Text1}" id="hyperlink2Text1" value="#{currentRow.name}"/>
                                        </h:column>
                                        <h:column binding="#{SearchFieldsPage.column2}" id="column2">
                                            <h:selectBooleanCheckbox binding="#{SearchFieldsPage.checkbox1}" id="checkbox1" value="#{currentRow.displayAdvancedSearch}"/>
                                            <f:facet name="header">
                                                <h:outputText binding="#{SearchFieldsPage.outputText5}" id="outputText5" value="Display in Advanced Search"/>
                                            </f:facet>
                                        </h:column>
                                        <h:column binding="#{SearchFieldsPage.column3}" id="column3">
                                            <f:facet name="header">
                                                <h:outputText binding="#{SearchFieldsPage.outputText6}" id="outputText6" value="Display in Search Results"/>
                                            </f:facet>
                                            <h:selectBooleanCheckbox binding="#{SearchFieldsPage.checkbox2}" id="checkbox2" value="#{currentRow.displaySearchResults}" disabled="#{currentRow.displaySearchResultsDisabled}"/>
                                        </h:column>
                                    </h:dataTable>
                                    <ui:panelGroup binding="#{SearchFieldsPage.groupPanel5}" block="true" id="groupPanel5" style="padding-top: 10px" styleClass="vdcTextRight">
                                        <h:commandButton binding="#{SearchFieldsPage.button4}" id="button4" value="Update" action="#{SearchFieldsPage.update}"/>
                                    </ui:panelGroup>
-->                                   
                                    
                                </ui:panelLayout>
                            </ui:panelLayout>
                    </ui:form>
    </f:subview>
</jsp:root>
