<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
    xmlns:h="http://java.sun.com/jsf/html" 
    xmlns:jsp="http://java.sun.com/JSP/Page" 
    xmlns:ui="http://www.sun.com/web/ui"
    xmlns:tiles="http://struts.apache.org/tags-tiles"
    xmlns:c="http://java.sun.com/jsp/jstl/core">
    <f:subview id="advSearchPageView">
        <ui:form binding="#{AdvSearchPage.form1}" id="form1">
            <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
            
            <div class="dvn_section">
                <div class="dvn_sectionTitle">
                    <a name="advancedSearch" title="">
                        Advanced Search
                    </a>
                </div>            
                <div class="dvn_sectionBox"> 
                    <div class="dvn_margin12">
                        
                        <ui:panelGroup block="true" style="padding-bottom: 20px;">
                            <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                            <h:outputText  styleClass="vdcHelpText" value="Date searches accept the following formats: YYYY or YYYY-MM or YYYY-MM-DD."/>
                        </ui:panelGroup>
                        
                        <h:panelGrid binding="#{AdvSearchPage.gridPanel1}" cellpadding="0" cellspacing="0" columnClasses="vdcAdvSearchCol1, vdcAdvSearchCol2, vdcAdvSearchCol3" columns="3"
                                     id="gridPanel1" rowClasses="list-row-odd, list-row-even"  width="100%">
                            
                            <h:outputText binding="#{AdvSearchPage.outputText1}" id="outputText1" styleClass="vdcSubHeaderColor" value="Search Scope"/>
                            <h:outputText binding="#{AdvSearchPage.outputText2}" id="outputText2" styleClass="vdcSubHeaderColor" value="Operator"/>
                            <h:outputText binding="#{AdvSearchPage.outputText3}" id="outputText3" styleClass="vdcSubHeaderColor" value="Search Term"/>
                            
                            <h:selectOneMenu binding="#{AdvSearchPage.dropdown1}" id="dropdown1" value="Title" style="margin-left:24px" onchange="submit()" valueChangeListener="#{AdvSearchPage.searchFieldListener}" immediate="true">
                                <f:selectItems binding="#{AdvSearchPage.dropdown1SelectItems}" id="dropdown1SelectItems" value="#{AdvSearchPage.dropdown3DefaultItems}" />
                            </h:selectOneMenu>
                            <h:selectOneMenu binding="#{AdvSearchPage.dropdown9}" id="dropdown9" >
                                <f:selectItems binding="#{AdvSearchPage.dropdown4SelectItems3}" id="dropdown4SelectItems3" value="#{AdvSearchPage.dateItem1 ? AdvSearchPage.dropdown4DateItems : AdvSearchPage.dropdown4NotDateItems}"/>
                            </h:selectOneMenu>
                            <ui:panelGroup>
                                <h:inputText binding="#{AdvSearchPage.textField1}" id="textField1" size="40" required="true"/>
                                <h:message for="textField1" errorClass="errorMessage" showDetail="false" showSummary="true" />
                            </ui:panelGroup>
                            <ui:panelGroup binding="#{AdvSearchPage.groupPanel1}" id="groupPanel1" style="white-space: nowrap;">
                                <h:outputText binding="#{AdvSearchPage.outputText7}" id="outputText7" value="and "/>
                                <h:selectOneMenu binding="#{AdvSearchPage.dropdown3}" id="dropdown3" value="Producer" onchange="submit()" valueChangeListener="#{AdvSearchPage.searchFieldListener}" immediate="true">
                                    <f:selectItems binding="#{AdvSearchPage.dropdown3SelectItems}" id="dropdown3SelectItems" value="#{AdvSearchPage.dropdown3DefaultItems}"/>
                                </h:selectOneMenu>
                            </ui:panelGroup>
                            <h:selectOneMenu binding="#{AdvSearchPage.dropdown4}" id="dropdown4">
                                <f:selectItems binding="#{AdvSearchPage.dropdown4SelectItems}" id="dropdown4SelectItems" value="#{AdvSearchPage.dateItem2 ? AdvSearchPage.dropdown4DateItems : AdvSearchPage.dropdown4NotDateItems}"/>
                            </h:selectOneMenu>
                            <h:inputText binding="#{AdvSearchPage.textField2}" id="textField2" size="40"/>
                            <ui:panelGroup binding="#{AdvSearchPage.groupPanel2}" id="groupPanel2" style="white-space: nowrap;">
                                <h:outputText binding="#{AdvSearchPage.outputText6}" id="outputText6" value="and "/>
                                <h:selectOneMenu binding="#{AdvSearchPage.dropdown5}" id="dropdown5" value="Production Date" onchange="submit()" valueChangeListener="#{AdvSearchPage.searchFieldListener}" immediate="true">
                                    <f:selectItems binding="#{AdvSearchPage.dropdown3SelectItems1}" id="dropdown3SelectItems1" value="#{AdvSearchPage.dropdown3DefaultItems}"/>
                                </h:selectOneMenu>
                            </ui:panelGroup>
                            <h:selectOneMenu binding="#{AdvSearchPage.dropdown6}" id="dropdown6">
                                <f:selectItems binding="#{AdvSearchPage.dropdown4SelectItems1}" id="dropdown4SelectItems1" value="#{AdvSearchPage.dateItem3 ? AdvSearchPage.dropdown4DateItems : AdvSearchPage.dropdown4NotDateItems}"/>
                            </h:selectOneMenu>
                            <h:inputText binding="#{AdvSearchPage.textField3}" id="textField3" size="40"/>
                            <ui:panelGroup binding="#{AdvSearchPage.groupPanel3}" id="groupPanel3" style="white-space: nowrap;">
                                <h:outputText binding="#{AdvSearchPage.outputText8}" id="outputText8" value="and "/>
                                <h:selectOneMenu binding="#{AdvSearchPage.dropdown7}" id="dropdown7" value="Time Period Covered Start" onchange="submit()" valueChangeListener="#{AdvSearchPage.searchFieldListener}" immediate="true">
                                    <f:selectItems binding="#{AdvSearchPage.dropdown3SelectItems2}" id="dropdown3SelectItems2" value="#{AdvSearchPage.dropdown3DefaultItems}"/>
                                </h:selectOneMenu>
                            </ui:panelGroup>
                            <h:selectOneMenu binding="#{AdvSearchPage.dropdown8}" id="dropdown8">
                                <f:selectItems binding="#{AdvSearchPage.dropdown4SelectItems2}" id="dropdown4SelectItems2" value="#{AdvSearchPage.dateItem4 ? AdvSearchPage.dropdown4DateItems : AdvSearchPage.dropdown4NotDateItems}"/>
                            </h:selectOneMenu>
                            <h:inputText binding="#{AdvSearchPage.textField4}" id="textField4" size="40"/>
                        </h:panelGrid>
                        
                        <!--
                    <h:commandButton binding="#{AdvSearchPage.button1}" id="button1"
                        style="margin-left: 10px; margin-top: 10px; margin-bottom: 10px" value="Add Row"/>
                        -->
                        <ui:panelGroup binding="#{AdvSearchPage.groupPanel4}" block="true" id="groupPanel4" style="padding-left: 4px; padding-top: 20px; padding-bottom: 10px">
                            <h:outputText binding="#{AdvSearchPage.outputText5}" id="outputText5" style="font-size: 1.2em; font-weight: bold" value="Collections" rendered="#{AdvSearchPage.collectionsIncluded}"/>
                            <h:selectOneRadio binding="#{AdvSearchPage.radioButtonList1}" id="radioButtonList1" layout="pageDirection"  style="padding-top: 10px" value="Search All Collections" rendered="#{AdvSearchPage.collectionsIncluded}">
                                <f:selectItems binding="#{AdvSearchPage.radioButtonList1SelectItems}" id="radioButtonList1SelectItems" value="#{AdvSearchPage.radioButtonList1DefaultItems}"/>
                            </h:selectOneRadio>
                            <ui:panelGroup block="true" style="padding-bottom: 5px;" rendered="#{AdvSearchPage.collectionsIncluded}">
                                <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                                <h:outputText  styleClass="vdcHelpText" value="Note: By checking a collection you are requesting to search for studies directly associated with that collection. Your search will not necessarily include studies in sub-collections, unless the sub-collections are explicitly checked."/>
                            </ui:panelGroup>
                            <h:dataTable binding="#{AdvSearchPage.dataTable1}" cellpadding="0" cellspacing="0" headerClass="" id="dataTable1" rendered="#{AdvSearchPage.collectionsIncluded}"
                                         style="padding-left: 10px; margin-top: 20px" columnClasses="vdcColPadded" value="#{AdvSearchPage.dataTable1Model}" var="currentRow" width="100%">
                                <h:column binding="#{AdvSearchPage.column1}" id="column1">
                                    <h:selectBooleanCheckbox binding="#{AdvSearchPage.checkbox1}" id="checkbox1" style="margin-left: 10px; margin-right:  #{currentRow['level']}0px;" value="#{currentRow['selected']}" />
                                    <h:graphicImage binding="#{AdvSearchPage.image1}" id="image1"  style="vertical-align: bottom" value="/resources/tree_folder.gif"/>
                                    <h:outputText binding="#{AdvSearchPage.outputText11}" id="outputText11" value="#{currentRow['name']}"/>
                                </h:column>
                            </h:dataTable>
                            <ui:panelGroup binding="#{AdvSearchPage.groupPanel5}" block="true" id="groupPanel5" style="padding-left: 10px; padding-top: 30px; padding-bottom: 20px">
                                <h:commandButton binding="#{AdvSearchPage.button2}" id="button2" style="" value="Search" action="#{AdvSearchPage.search}"/>
                                <h:commandButton binding="#{AdvSearchPage.button3}" id="button3" style="margin-left:10px" value="Cancel" immediate="true" action="#{AdvSearchPage.cancel}"/>
                            </ui:panelGroup>
                        </ui:panelGroup>
                        
                    </div>
                </div>
            </div>

       </ui:form>
    </f:subview>
</jsp:root>
