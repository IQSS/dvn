<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles">
       <f:subview id="ManageStudyTemplatesPageView">
                    <ui:form  id="form1">
                        <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
                            <ui:panelLayout  id="layoutPanel1" panelLayout="flow" styleClass="vdcSectionMiddle">
                                <ui:panelLayout  id="layoutPanel2" panelLayout="flow" styleClass="vdcSectionHeader">
                                    <h:outputText value="Manage Study Templates"/>
                                </ui:panelLayout>
                                <ui:panelLayout  id="layoutPanel3" panelLayout="flow" style="padding: 40px 40px 30px 40px; ">
                                    <ui:panelGroup  block="true" id="groupPanel1" style="padding-bottom: 20px">
                                        <h:outputText  id="outputText8" value="Create a New Template for this Dataverse (provide a name):"/>
                                        <h:inputText  id="textField1"/>
                                        <h:commandButton  id="button1" value="Add"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup  block="true" id="groupPanel2"
                                        style="padding-bottom: 10px" styleClass="vdcTextRight">
                                        <h:commandButton  id="button2" value="Update"/>
                                    </ui:panelGroup>
                                    <h:dataTable  cellpadding="0" cellspacing="0"
                                        headerClass="list-header-left" id="dataTable1" rowClasses="list-row-even,list-row-odd"
                                        value="#{ManageStudyTemplatesPage.dataTable1Model}" var="currentRow" width="100%">
                                        <h:column  id="column1">
                                            <f:facet name="header">
                                                <h:outputText  id="outputText3" value="Templates"/>
                                            </f:facet>
                                            <h:outputLink  id="hyperlink1" value="http://www.sun.com/jscreator">
                                                <h:outputText  id="hyperlink1Text" value="Template Name"/>
                                            </h:outputLink>
                                        </h:column>
                                        <h:column  id="column2">
                                            <h:inputText  id="textField2" size="4"/>
                                            <f:facet name="header">
                                                <h:outputText  id="outputText5" value="Order"/>
                                            </f:facet>
                                        </h:column>
                                        <h:column  id="column3">
                                            <f:facet name="header">
                                                <h:selectBooleanCheckbox  id="checkbox1"/>
                                                <h:outputText  id="outputText7" value="Delete"/>
                                            </f:facet>
                                        </h:column>
                                    </h:dataTable>
                                </ui:panelLayout>
                            </ui:panelLayout>
                    </ui:form>
    </f:subview>
</jsp:root>
