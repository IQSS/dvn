<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
    xmlns:h="http://java.sun.com/jsf/html" 
    xmlns:jsp="http://java.sun.com/JSP/Page" 
    xmlns:ui="http://www.sun.com/web/ui"
    xmlns:tiles="http://struts.apache.org/tags-tiles"
    xmlns:c="http://java.sun.com/jsp/jstl/core">

    <f:subview id="addHarvestSitePageView">
                    
                    <ui:form id="form1">
                        <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
                            <ui:panelLayout  id="layoutPanel1" panelLayout="flow" styleClass="vdcSectionMiddle">
                                <ui:panelLayout  id="layoutPanel2" panelLayout="flow" styleClass="vdcSectionHeader">
                                    <h:outputText  value="Harvest Dataverse"/>
                                </ui:panelLayout>
                                <ui:panelLayout  id="layoutPanel3" panelLayout="flow" style="padding-left: 20px; padding-top: 30px; padding-bottom: 20px">
                                    <ui:panelGroup block="true" style="padding-left: 20px; padding-right: 30px">
                                     <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                                     <h:outputText  id="outputText2" styleClass="vdcHelpText" value="A Harvest dataverse provides most of the functionality included in a regular dataverse but all of its studies are harvested form an OAI Server (which in some cases might be another Dataverse Network). Harvested studies cannot be edited. The study metadata (cataloging and variable information) is searched locally but files are accessed remotely. "/>
                                    </ui:panelGroup>
                                    <h:panelGrid cellpadding="0" cellspacing="0"
                                        columnClasses="vdcAddSiteCol1, vdcAddSiteCol2" columns="2" id="gridPanel1" style="margin-top: 30px; margin-bottom: 30px">
                                        <ui:panelGroup block="true" style="vertical-align: top;">
                                            <h:outputLabel  for="componentLabel1" id="componentLabel1">
                                                <h:outputText  id="componentLabel1Text" style="white-space: nowrap; padding-right: 10px; " value="Dataverse Name"/>                                              
                                            </h:outputLabel>
                                        </ui:panelGroup>
                                        <ui:panelGroup>
                                            <h:inputText id="dataverseName" required="true"  size="60"/>
                                            <verbatim><br /></verbatim>
                                            <h:outputText styleClass="vdcHelpText" value="Name used to refer to this dataverse in Dataverse Network Homepage and other pages."/>
                                        </ui:panelGroup>
                                        <ui:panelGroup block="true" style="vertical-align: top;">
                                            <h:outputLabel for="componentLabel2" id="componentLabel2">
                                                <h:outputText id="componentLabel2Text" style="white-space: nowrap; padding-right: 10px; " value="Dataverse Alias"/> 
                                            </h:outputLabel>
                                        </ui:panelGroup>
                                        <ui:panelGroup>
                                            <h:inputText id="dataverseAlias" required="true"  requiredMessage="This field is required."/>
                                            <verbatim><br /></verbatim>
                                            <h:outputText styleClass="vdcHelpText" value="Short name used to build the URL for this dataverse, i.e., http://.../dv/'alias'."/>
                                        </ui:panelGroup>
                                        <ui:panelGroup block="true" style="vertical-align: top;">
                                           <h:outputText style="white-space: nowrap; padding-right: 10px; " value="OAI Server"/> 
                                        </ui:panelGroup>
                                        <ui:panelGroup>
                                            <h:inputText  required="true" requiredMessage="This field is required."/>  <h:commandButton  value="Validate" />
                                            <verbatim><br /></verbatim>
                                            <h:outputText styleClass="vdcHelpText" value="Enter OAI server name, e.g., http://..."/> 
                                        </ui:panelGroup>
                                    </h:panelGrid>
                                    
                                    <h:panelGrid cellpadding="0" cellspacing="0"
                                                 columnClasses="vdcAddSiteCol1, vdcAddSiteCol2" columns="2" style="margin-top: 30px; margin-bottom: 30px">
                                        <ui:panelGroup block="true" style="vertical-align: top;">
                                           <h:outputText style="white-space: nowrap; padding-right: 10px; " value="Harvesting Set"/> 
                                        </ui:panelGroup>
                                        <ui:panelGroup>
                                            <h:selectOneMenu  id="dropdown1" >
                                                <f:selectItem   itemLabel="Select a Set" itemValue="dummy" />
                                                <f:selectItem   itemLabel="Set 1" itemValue="set1" />
                                                <f:selectItem   itemLabel="Set 2" itemValue="set2" />
                                            </h:selectOneMenu>
                                            <verbatim><br /></verbatim>
                                            <h:outputText styleClass="vdcHelpText" value="Select the set you would like to harvest."/>
                                        </ui:panelGroup>  
                                        <ui:panelGroup block="true" style="vertical-align: top;">
                                           <h:outputText style="white-space: nowrap; padding-right: 10px; " value="Harvesting Format"/> 
                                        </ui:panelGroup>
                                        <ui:panelGroup>
                                            <h:selectOneMenu  id="dropdown2" >
                                                <f:selectItem   itemLabel="DDI" itemValue="DDI" />
                                            </h:selectOneMenu>
                                            <verbatim><br /></verbatim>
                                            <h:outputText styleClass="vdcHelpText" value="This is the XML format when used harvesting studies from this OAI Server."/>
                                        </ui:panelGroup>  
                                       
                                    </h:panelGrid>
                                    <h:panelGrid cellpadding="0" cellspacing="0"
                                                 columnClasses="vdcAddSiteCol1, vdcAddSiteCol2" columns="2" style="margin-top: 30px; margin-bottom: 30px">
                                        <ui:panelGroup block="true" style="vertical-align: top;">
                                            <h:outputText style="white-space: nowrap; padding-right: 10px; " value="File Permissions"/> 
                                        </ui:panelGroup>
                                        <ui:panelGroup>
                                            <h:selectOneMenu  id="dropdown3" >
                                                <f:selectItem   itemLabel="Public" itemValue="public" />
                                                <f:selectItem   itemLabel="Restricted" itemValue="restricted" />
                                            </h:selectOneMenu>
                                            <verbatim><br /></verbatim>
                                            <h:outputText styleClass="vdcHelpText" value="Select to make all files for all studies in this dataverse public or restricted."/>
                                        </ui:panelGroup>  
                                        <ui:panelGroup block="true" style="vertical-align: top;">
                                            <h:outputText style="white-space: nowrap; padding-right: 10px; " value="Allowed Users, if Files are Restricted"/> 
                                        </ui:panelGroup>
                                        <ui:panelGroup>
                                            <h:inputText required="true" requiredMessage="This field is required." /> <h:commandButton  value="Add" />
                                            <verbatim><br /></verbatim>
                                            <h:outputText styleClass="vdcHelpText" value="Enter username to allow them to access restricted studies."/>
                                        </ui:panelGroup>
                                        <ui:panelGroup block="true" style="vertical-align: top;">
                                            <h:outputText style="white-space: nowrap; padding-right: 10px; " value="Allowed Groups, if Files are Restricted"/> 
                                        </ui:panelGroup>
                                        <ui:panelGroup>
                                            <h:inputText required="true" requiredMessage="This field is required." /> <h:commandButton  value="Add" />
                                            <verbatim><br /></verbatim>
                                            <h:outputText styleClass="vdcHelpText" value="Enter group name to allow them to access restricted studies."/>
                                        </ui:panelGroup>
                                    </h:panelGrid>
                                        
                                    <ui:panelGroup block="true"  style="padding-left: 160px">
                                        <h:commandButton id="button1" value="Save" />
                                        <h:commandButton id="button2" style="margin-left: 20px" immediate="true" value="Cancel"/>
                                        <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                                     <h:outputText   styleClass="vdcHelpText" value="Any changes you make to this page will not be saved until you click the Save button. "/>
                                    </ui:panelGroup>
                                   
                                </ui:panelLayout>
                            </ui:panelLayout>
                    </ui:form>
    </f:subview>
</jsp:root>
