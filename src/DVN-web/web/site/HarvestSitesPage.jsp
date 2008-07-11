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

<gui:param name="pageTitle" value="DVN - Manage Dataverses" />

  <gui:define name="body">

        <ui:form  id="form1">
            <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
            
            <div class="dvn_section">
                <div class="dvn_sectionTitle">
                     
                        <h:outputText value="Manage Dataverses"/>
                      
                </div>            
                <div class="dvn_sectionBox">
                  <div class="dvn_margin12">  
                    
                    <ui:panelGroup block="true" style="padding-bottom: 10px">
                        <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                        <h:outputText  styleClass="vdcHelpText" escape="false" value="To edit a harvest dataverse settings, click on the dataverse name. If you have set up a harvest dataverse, you can schedule harvesting from this page. The harvest schedule runs every night and gets any new studies or studies that have changed in the original source (OAI server) since the last harvesting. You have also the option to run harvesting on demand." />
                    </ui:panelGroup>
                    
                    <h:dataTable cellpadding="0" cellspacing="0"
                                 binding="#{HarvestSitesPage.harvestDataTable}"
                                 headerClass="list-header-left vdcColPadded" id="dataTable1" 
                                 rowClasses="list-row-even vdcColPadded, list-row-odd vdcColPadded" 
                                 value="#{HarvestSitesPage.harvestSiteList}" var="currentRow" width="100%">
                        <h:column >
                            <f:facet name="header">
                                <h:outputText id="outputText2" value="Harvest Dataverse"/>
                            </f:facet>
                            <h:outputText  value="#{currentRow.vdc.name}" rendered="#{currentRow.harvestingNow}" />
                            <h:outputLink rendered="#{!currentRow.harvestingNow}"  value="/dvn/faces/site/EditHarvestSitePage.jsp?harvestId=#{currentRow.id}">
                                <h:outputText  value="#{currentRow.vdc.name}"/>
                            </h:outputLink>
                        </h:column>
                        <h:column >
                            <f:facet name="header">
                                <h:outputText  id="outputText4" value="Status"/>
                            </f:facet>
                            <h:outputText  id="outputText4b" value="Scheduled" rendered="#{currentRow.oai and currentRow.scheduled and  !empty currentRow.schedulePeriod}"/>
                            <h:outputText  id="outputText4c" value="Not Scheduled" rendered="#{currentRow.oai and !currentRow.scheduled and !empty currentRow.schedulePeriod}"/>
                            <h:outputText   value="Harvesting Schedule Not Defined " rendered="#{currentRow.oai and empty currentRow.schedulePeriod}"/>
  
                        </h:column>
                        <h:column  >
                            <f:facet name="header">
                                <h:outputText value=""/>
                            </f:facet>
                            <h:commandButton value="Schedule Harvesting" rendered="#{currentRow.oai and !currentRow.scheduled and !empty currentRow.schedulePeriod }" actionListener="#{HarvestSitesPage.doSchedule}"/>
                            <h:commandButton value="Unschedule Harvesting" rendered="#{currentRow.oai and currentRow.scheduled and !empty currentRow.schedulePeriod }" actionListener="#{HarvestSitesPage.doUnschedule}"/>                      
                             <h:outputLink rendered="#{currentRow.oai and !currentRow.harvestingNow and empty currentRow.schedulePeriod}"  value="/dvn/faces/site/EditHarvestSitePage.jsp?harvestId=#{currentRow.id}">
                                <h:outputText  value=" Define Harvesting Schedule"/>
                            </h:outputLink>
                       </h:column>
                        <h:column  >
                            <f:facet name="header">
                                <h:outputText  value=""/>
                            </f:facet>
                            <h:commandButton value="Run Harvester Now"  rendered="#{!currentRow.harvestingNow and (currentRow.oai or empty currentRow.lastHarvestTime)}"   actionListener="#{HarvestSitesPage.doRunNow}"/>
                            <h:outputText  value="Harvesting Currently Running" rendered="#{currentRow.harvestingNow}" />   
                            
                        </h:column>
                        <h:column >
                            <f:facet name="header">
                                <h:outputText id="outputText3" value="Remove"/>
                            </f:facet>
                            <!--h:commandButton  value="Remove"  actionListener="#{HarvestSitesPage.doRemoveHarvestDataverse}"/-->
                            <h:outputLink disabled="#{currentRow.harvestingNow}" value="/dvn/faces/site/DeleteDataversePage.jsp?deleteId=#{currentRow.vdc.id}">
                                <h:outputText  value="Remove"/>
                            </h:outputLink>                           
                        </h:column>
                        
                    </h:dataTable>
                   
                    <ui:panelGroup block="true" style="padding-bottom: 10px">
                        <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                        <h:outputText  styleClass="vdcHelpText" escape="false" value="Clicking a dataverse name takes you to the 'My Options' page for that dataverse. Keep in mind that by removing a dataverse you delete any study owned by that dataverse." />
                    </ui:panelGroup>
                    
                    <h:dataTable cellpadding="0" cellspacing="0"
                                 binding="#{HarvestSitesPage.dataverseDataTable}"
                                 headerClass="list-header-left vdcColPadded" 
                                 rowClasses="list-row-even vdcColPadded, list-row-odd vdcColPadded" 
                                 value="#{HarvestSitesPage.dataverseSiteList}" var="currentRow" width="100%">
                        <h:column >
                            <f:facet name="header">
                                <h:outputText  value="Dataverse"/>
                            </f:facet>
                            <h:outputLink value="/dvn/dv/#{currentRow.alias}/faces/admin/OptionsPage.jsp?currentVDCId=#{currentRow.id}">
                                <h:outputText  value="#{currentRow.name}"/>
                            </h:outputLink>
                        </h:column>
                        
                        
                        <h:column >
                            <f:facet name="header">
                                <h:outputText  value="Remove"/>
                            </f:facet>
                            <!--h:commandButton  value="Remove" actionListener="#{HarvestSitesPage.doRemoveDataverse}"/-->
                            <h:outputLink   value="/dvn/faces/site/DeleteDataversePage.jsp?deleteId=#{currentRow.id}">
                                <h:outputText id="hyperlink1Text1" value="Remove"/>
                            </h:outputLink>                     
                        </h:column>
                        
                    </h:dataTable>
                    
                    
                    </div>
                </div>
             </div>
        </ui:form>          
            </gui:define>
        </gui:composition>
    </body>
</html>
