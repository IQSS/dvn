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
                        <input type="hidden" name="pageName" value="EditHarvestSitePage"/>
                        <h:inputHidden id="harvestId" value="#{EditHarvestSitePage.harvestId}"/>
                            <ui:panelLayout  id="layoutPanel1" panelLayout="flow" styleClass="vdcSectionMiddle">
                                <ui:panelLayout  id="layoutPanel2" panelLayout="flow" styleClass="vdcSectionHeader">
                                    <h:outputText  id="outputText1" value="Harvest Dataverse"/>
                                </ui:panelLayout>
                                <ui:panelLayout  id="layoutPanel3" panelLayout="flow" style="padding-left: 20px; padding-top: 30px; padding-bottom: 20px">
                                    <ui:panelLayout panelLayout="flow" styleClass="vdcSectionMiddleMessage" style="width: 200px; margin-top: 10px; margin-bottom: 20px" rendered="#{EditHarvestSitePage.success}">
                                        <ui:panelGroup block="true"  styleClass="successMessage" >
                                            <h:outputText value="Update Successful!" />
                                        </ui:panelGroup>
                                     </ui:panelLayout>
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
                                            <h:inputText id="dataverseName" value="#{EditHarvestSitePage.dataverseName}" required="true" validator="#{EditHarvestSitePage.validateName}" size="60"/>
                                             <h:message styleClass="errorMessage" for="dataverseName"/> 
                                            <verbatim><br /></verbatim>
                                            <h:outputText styleClass="vdcHelpText" value="Name used to refer to this dataverse in Dataverse Network Homepage and other pages."/>
                                        </ui:panelGroup>
                                        <ui:panelGroup block="true" style="vertical-align: top;">
                                            <h:outputLabel for="componentLabel2" id="componentLabel2">
                                                <h:outputText id="componentLabel2Text" style="white-space: nowrap; padding-right: 10px; " value="Dataverse Alias"/> 
                                            </h:outputLabel>
                                        </ui:panelGroup>
                                        <ui:panelGroup>
                                            <h:inputText id="dataverseAlias" value="#{EditHarvestSitePage.dataverseAlias}" validator="#{EditHarvestSitePage.validateAlias}" required="true"  />
                                            <h:message styleClass="errorMessage" for="dataverseAlias"/> 
                                        
                                            <verbatim><br /></verbatim>
                                            <h:outputText styleClass="vdcHelpText" value="Short name used to build the URL for this dataverse, i.e., http://.../dv/'alias'."/>
                                        </ui:panelGroup>
                                        <ui:panelGroup block="true" style="vertical-align: top;">
                                           <h:outputText style="white-space: nowrap; padding-right: 10px; " value="OAI Server"/> 
                                        </ui:panelGroup>
                                        <ui:panelGroup>
                                            <h:inputText id="dataverseOaiServer" validator="#{EditHarvestSitePage.validateOAIServer}"  value="#{EditHarvestSitePage.harvestingDataverse.oaiServer}"  required="true" >
                                                   <f:validator validatorId="UrlValidator"/>
                                            </h:inputText>  
                                            <h:commandButton  value="Validate" />
                                             <h:message styleClass="errorMessage" for="dataverseOaiServer"/> 
                                                                              
                                            <verbatim><br /></verbatim>
                                            <h:outputText styleClass="vdcHelpText" value="Enter OAI server name, e.g., http://..."/> 
                                        </ui:panelGroup>
                                    </h:panelGrid>
                                    
                                    <h:panelGrid cellpadding="0" cellspacing="0"
                                                 columnClasses="vdcAddSiteCol1, vdcAddSiteCol2" columns="2" style="margin-top: 30px; margin-bottom: 30px">
                                        <ui:panelGroup block="true" style="vertical-align: top;" rendered="#{not empty EditHarvestSitePage.editHarvestSiteService.harvestingSets}" > 
                                           <h:outputText style="white-space: nowrap; padding-right: 10px; " value="Harvesting Set"/> 
                                        </ui:panelGroup>
                                        <ui:panelGroup rendered="#{not empty EditHarvestSitePage.editHarvestSiteService.harvestingSets}">                          
                                             <h:selectOneMenu  id="radioButtonList1" value="#{EditHarvestSitePage.harvestingDataverse.harvestingSet}"  >
                                                <f:selectItems  id="radio1SelectItem" value="#{EditHarvestSitePage.harvestingSetsSelect}" />
                                            </h:selectOneMenu>
                                             <verbatim><br /></verbatim>
                                            <h:outputText styleClass="vdcHelpText" value="Select the set you would like to harvest."/>
                                        </ui:panelGroup>  
                                        <ui:panelGroup rendered="#{ EditHarvestSitePage.editHarvestSiteService.harvestingSets==null and  EditHarvestSitePage.harvestingDataverse.oaiServer !=null}">   
                                            <h:outputText value="This OAI Server does not support sets"/>
                                        </ui:panelGroup>
                                        <ui:panelGroup block="true" style="vertical-align: top;" rendered="#{not empty EditHarvestSitePage.editHarvestSiteService.metadataFormats}">
                                           <h:outputText style="white-space: nowrap; padding-right: 10px; " value="Harvesting Format"/> 
                                        </ui:panelGroup>
                                        <ui:panelGroup  rendered="#{not empty EditHarvestSitePage.editHarvestSiteService.metadataFormats}">
                                            <h:selectOneMenu   value="#{EditHarvestSitePage.harvestingDataverse.format}" >
                                                <f:selectItems  value="#{EditHarvestSitePage.metadataFormatsSelect}" />
                                            </h:selectOneMenu>
                                            <verbatim><br /></verbatim>
                                            <h:outputText styleClass="vdcHelpText" value="This is the XML format when used harvesting studies from this OAI Server."/>
                                        </ui:panelGroup>  
   
                                         <ui:panelGroup  >
                                              <h:outputText style="white-space: nowrap; padding-right: 10px; " value="Handle Registration"/> 
                                          </ui:panelGroup >
                                          <ui:panelGroup>    
                                            <h:selectOneMenu  binding="#{EditHarvestSitePage.handlePrefixSelectOneMenu}" value="#{EditHarvestSitePage.handlePrefixId}" >
                                                <f:selectItem itemValue="" itemLabel="Do not register harvested studies (studies must already have a handle)"/>
                                                <f:selectItems  value="#{EditHarvestSitePage.handlePrefixSelect}" />
                                            </h:selectOneMenu>
                                            <verbatim><br /></verbatim>
                                           
                                        </ui:panelGroup>  
                                          
                                    </h:panelGrid>
                                    <h:panelGrid cellpadding="0" cellspacing="0"
                                                 columnClasses="vdcAddSiteCol1, vdcAddSiteCol2" columns="2" style="margin-top: 30px; margin-bottom: 30px">
                                        <ui:panelGroup block="true" style="vertical-align: top;">
                                            <h:outputText style="white-space: nowrap; padding-right: 10px; " value="File Permissions"/> 
                                        </ui:panelGroup>
                                        <ui:panelGroup>
                                            <h:selectOneMenu  id="dropdown3" value="#{EditHarvestSitePage.harvestingDataverse.filesRestricted}">
                                                <f:selectItem   itemLabel="Public" itemValue="false" />
                                                <f:selectItem   itemLabel="Restricted" itemValue="true" />
                                            </h:selectOneMenu>
                                            <verbatim><br /></verbatim>
                                            <h:outputText styleClass="vdcHelpText" value="Select to make all files for all studies in this dataverse public or restricted."/>
                                        </ui:panelGroup>  
                                        <ui:panelGroup block="true" style="vertical-align: top;">
                                            <h:outputText style="white-space: nowrap; padding-right: 10px; " value="Allowed Users, if Files are Restricted"/> 
                                        </ui:panelGroup>
                                        <ui:panelGroup>
                                            <h:inputText binding="#{EditHarvestSitePage.userInputText}" value="#{EditHarvestSitePage.addUserName}" />
                                            <h:commandButton  value="Add" actionListener="#{EditHarvestSitePage.addUser}" />
                                            
                                            <verbatim><br /></verbatim>
                                            <h:outputText styleClass="vdcHelpText" value="Enter username to allow them to access restricted studies."/>
                                            <h:dataTable binding="#{EditHarvestSitePage.userTable}" cellpadding="0" cellspacing="0"
                                                columnClasses="vdcColPadded, vdcColPadded, vdcColPadded, vdcColPadded" headerClass="list-header-left" 
                                                rowClasses="list-row-even,list-row-odd" value="#{EditHarvestSitePage.harvestingDataverse.allowedFileUsers}" var="currentRow" width="100%" 
                                                rendered="#{not empty EditHarvestSitePage.harvestingDataverse.allowedFileUsers}">
                                                <h:column >
                                                <f:facet name="header">
                                                    <h:outputText id="users_tcol1" value="User Name"/>
                                                </f:facet>
                                                <h:outputLink  value="../login/AccountPage.jsp?userId=#{currentRow.id}">
                                                    <h:outputText  value="#{currentRow.userName}"/>
                                                </h:outputLink>
                                            </h:column>
                                                               
                                            <h:column>
                                                <h:commandButton  id="removeUserButton" value="Remove User" actionListener="#{EditHarvestSitePage.removeUser}" />                                            
                                            </h:column>
                                        </h:dataTable>                                            
                                        </ui:panelGroup>
                                        <ui:panelGroup block="true" style="vertical-align: top;">
                                            <h:outputText style="white-space: nowrap; padding-right: 10px; " value="Allowed Groups, if Files are Restricted"/> 
                                        </ui:panelGroup>
                                        <ui:panelGroup>
                                            <h:inputText value="#{EditHarvestSitePage.addGroupName}" /> <h:commandButton  value="Add"  actionListener="#{EditHarvestSitePage.addGroup}" />
                                            
                                            <verbatim><br /></verbatim>
                                            <h:outputText styleClass="vdcHelpText" value="Enter group name to allow them to access restricted studies."/>
                                          <h:dataTable binding="#{EditHarvestSitePage.groupTable}" cellpadding="0" cellspacing="0"
                                                columnClasses="vdcColPadded, vdcColPadded, vdcColPadded, vdcColPadded" headerClass="list-header-left" 
                                                rowClasses="list-row-even,list-row-odd" value="#{EditHarvestSitePage.harvestingDataverse.allowedFileGroups}" var="currentRow" width="100%" 
                                                rendered="#{not empty EditHarvestSitePage.harvestingDataverse.allowedFileGroups}">
                                                <h:column >
                                                <f:facet name="header">
                                                    <h:outputText id="groups_tcol1" value="User Name"/>
                                                </f:facet>
                                                <h:outputLink  value="../networkAdmin/ViewUserGroupPage.jsp?userGroupId=#{currentRow.id}">
                                                    <h:outputText  value="#{currentRow.name}"/>
                                                </h:outputLink>
                                            </h:column>
                                                               
                                            <h:column>
                                                <h:commandButton  id="removeGroupButton" value="Remove Group" actionListener="#{EditHarvestSitePage.removeGroup}" />                                            
                                            </h:column>
                                        </h:dataTable>                                            
                                              
                                        </ui:panelGroup>
                                    </h:panelGrid>
                                        
                                    <ui:panelGroup block="true"  style="padding-left: 160px">
                                        <h:commandButton id="button1" value="Save"  action="#{EditHarvestSitePage.save}"/>
                                        <h:commandButton id="button2" style="margin-left: 20px" immediate="true" value="Cancel" action="#{EditHarvestSitePage.cancel}"/>
                                        <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                                     <h:outputText   styleClass="vdcHelpText" value="Any changes you make to this page will not be saved until you click the Save button. "/>
                                    </ui:panelGroup>
                                   
                                </ui:panelLayout>
                            </ui:panelLayout>
                    </ui:form>
    </f:subview>
</jsp:root>
