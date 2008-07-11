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

<gui:param name="pageTitle" value="DVN - Dataverse Network Utilities" />

  <gui:define name="body">


        <ui:form  id="utilitiesForm1">
            <h:inputHidden rendered="#{VDCRequest.currentVDCId != null}" id="vdcId" value="#{VDCRequest.currentVDCId}"/>
            <input type ="hidden"  name="selectedPanel" value="${UtilitiesPage.selectedPanel}"/>
            <div class="dvn_section">
                <div class="dvn_sectionTitle">
                    <h:outputText value="Dataverse Network Utilities" />
                </div>            
                <div class="dvn_sectionBox"> 
                    <div class="dvn_margin12">
                        
                        <!-- STUDY LOCK PANEL -->
                        <ui:panelGroup  block="true" styleClass="vdcStudyInfoHeader" style="margin-top: 5px;">
                            <h:outputText value="StudyLock Utilities"/>
                            <h:outputLink  title="Display this panel" rendered="#{!UtilitiesPage.studyLockPanelRendered}" value="/dvn#{VDCRequest.currentVDCURL}/faces/networkAdmin/UtilitiesPage.jsp?selectedPanel=studyLock">  
                                <h:graphicImage  styleClass="vdcNoBorders" value="/resources/icon_contract.gif" />
                            </h:outputLink>
                            <h:graphicImage  styleClass="vdcNoBorders" value="/resources/icon_expand.gif" rendered="#{UtilitiesPage.studyLockPanelRendered}" />
                        </ui:panelGroup>
                        
                        <h:panelGrid style="margin-left: auto; margin-right: auto;" rendered="#{UtilitiesPage.studyLockPanelRendered}" > 
                            <h:messages id="studyLockMessage"  styleClass="errorMessage"/> 
                            <h:outputText value="Current Locks:"/>
                            <h:dataTable value="#{UtilitiesPage.studyLockList}" var="studyLock" width="100%">
                                <h:column>
                                    <f:facet name="header">
                                        <h:outputText value="Study Id"/>
                                    </f:facet> 
                                    <h:outputText value="#{studyLock.study.id}"/>
                                </h:column>
                                <h:column>
                                    <f:facet name="header">
                                        <h:outputText value="User Name"/>
                                    </f:facet>                                     
                                    <h:outputText value="#{studyLock.user.userName}"/>
                                </h:column>
                                <h:column>
                                    <f:facet name="header">
                                        <h:outputText value="Start Time"/>
                                    </f:facet>                                     
                                    <h:outputText value="#{studyLock.startTime}"/>
                                </h:column>
                                <h:column>
                                    <f:facet name="header">
                                        <h:outputText value="Detail"/>
                                    </f:facet>                                     
                                    <h:outputText value="#{studyLock.detail}"/>
                                </h:column>                                
                            </h:dataTable> 
                            <hr/>
                            <h:outputText value="To remove a study lock, input the study id and click on the button below:"/>
                            <ui:panelGroup>
                                <h:inputText value="#{UtilitiesPage.studyLockStudyId}" size="6" />
                                <h:commandButton  value="Remove Lock" action="#{UtilitiesPage.removeLock_action}"/>
                            </ui:panelGroup>
                        </h:panelGrid>
                        
                        
                        <!-- INDEX PANEL -->
                        <ui:panelGroup  block="true" styleClass="vdcStudyInfoHeader" style="margin-top: 5px;">
                            <h:outputText value="Index Utilities"/>
                            <h:outputLink  title="Display this panel" rendered="#{!UtilitiesPage.indexPanelRendered}" value="/dvn#{VDCRequest.currentVDCURL}/faces/networkAdmin/UtilitiesPage.jsp?selectedPanel=index">  
                                <h:graphicImage  styleClass="vdcNoBorders" value="/resources/icon_contract.gif" />
                            </h:outputLink>
                            <h:graphicImage  styleClass="vdcNoBorders" value="/resources/icon_expand.gif" rendered="#{UtilitiesPage.indexPanelRendered}" />
                        </ui:panelGroup>   
                        
                        <h:panelGrid style="margin-left: auto; margin-right: auto;" rendered="#{UtilitiesPage.indexPanelRendered}" > 
                            <h:messages id="indexMessage"  styleClass="errorMessage"/> 
                            <h:outputText value="To reindex all studies, click on the button below:"/>
                            <ui:panelGroup>
                                <h:commandButton  value="Index All" action="#{UtilitiesPage.indexAll_action}"/>
                            </ui:panelGroup>
                            <hr/>
                            <h:outputText value="To index studies owned by a specific dataverse, input the dataverse id and click on the button below:"/>
                            <ui:panelGroup>
                                <h:inputText value="#{UtilitiesPage.indexDVId}" size="6" />
                                <h:commandButton  value="Index Dataverse" action="#{UtilitiesPage.indexDV_action}"/>
                            </ui:panelGroup>
                            <hr/>
                            <h:outputText value="To index arbitrary studies, input the study ids and click on the button below:"/>
                            <ui:panelGroup>
                                <h:inputTextarea value="#{UtilitiesPage.indexStudyIds}" rows="8" cols="80"/>
                                <h:commandButton  value="Index Studies" action="#{UtilitiesPage.indexStudies_action}"/>
                            </ui:panelGroup>                            
                            <hr/>
                            <h:outputText value="To index unindexed studies, click on the button below:"/>
                            <ui:panelGroup>
                                <h:commandButton  value="Index Update" action="#{UtilitiesPage.indexBatch_action}"/>
                            </ui:panelGroup>
                            <hr/>
                            <h:outputText value="#{UtilitiesPage.indexLocks}"/>
                            <ui:panelGroup>
                                <h:commandButton  value="Delete Lock" action="#{UtilitiesPage.indexLocks_action}" disabled="#{UtilitiesPage.deleteLockDisabled}"/>
                            </ui:panelGroup>                            
                        </h:panelGrid>
                        
                        
                        <!-- EXPORT PANEL -->  
                        <ui:panelGroup  block="true" styleClass="vdcStudyInfoHeader" style="margin-top: 5px;">
                            <h:outputText value="Export Utilities"/>                           
                            <h:outputLink  title="Display this panel" rendered="#{!UtilitiesPage.exportPanelRendered}" value="/dvn#{VDCRequest.currentVDCURL}/faces/networkAdmin/UtilitiesPage.jsp?selectedPanel=export">  
                                <h:graphicImage  styleClass="vdcNoBorders" value="/resources/icon_contract.gif" />
                            </h:outputLink>
                            <h:graphicImage  styleClass="vdcNoBorders" value="/resources/icon_expand.gif" rendered="#{UtilitiesPage.exportPanelRendered}" />
                        </ui:panelGroup>  
                        
                        <h:panelGrid style="margin-left: auto; margin-right: auto;" rendered="#{UtilitiesPage.exportPanelRendered}" > 
                            <h:messages id="exportMessage"  styleClass="errorMessage"/> 
                            <h:outputText value="To run export now (all updated studies in all formats), click on the button below:"/>
                            <ui:panelGroup>
                                <h:commandButton  value="Run Export" action="#{UtilitiesPage.exportUpdated_action}"/>
                            </ui:panelGroup> 
                            <hr/>
                            <hr/>
                            <hr/>
                            <h:outputText value="OR, for a CUSTOM export, select export format(s):"/>
                            <h:selectOneMenu id="dropdown1" value="#{UtilitiesPage.exportFormat}">
                                <f:selectItem itemLabel="Export all formats" itemValue="" />
                                <f:selectItem itemLabel="- Export DDI only" itemValue="ddi" />
                                <f:selectItem itemLabel="- Export Dublin Core only" itemValue="oai_dc" />
                                <f:selectItem itemLabel="- Export Marc only" itemValue="marc" /> 
                            </h:selectOneMenu>
                            <hr/>
                            <h:outputText value="To export all studies (regardless of update time), click the button below:"/>
                            <ui:panelGroup>
                                <h:commandButton  value="Export All" action="#{UtilitiesPage.exportAll_action}"/>
                            </ui:panelGroup>
                            <hr/>
                            <h:outputText value="To export studies owned by a specific dataverse (regardless of update time), input the dataverse id and click on the button below:"/>
                            <ui:panelGroup>
                                <h:inputText value="#{UtilitiesPage.exportDVId}" size="6" />
                                <h:commandButton  value="Export Dataverse" action="#{UtilitiesPage.exportDV_action}"/>
                            </ui:panelGroup>
                            <hr/>
                            <h:outputText value="To export arbitrary studies (regardless of update time), input the study ids and click on the button below:"/>
                            <ui:panelGroup>
                                <h:inputTextarea value="#{UtilitiesPage.exportStudyIds}" rows="8" cols="80"/>
                                <h:commandButton  value="Export Studies" action="#{UtilitiesPage.exportStudies_action}"/>
                            </ui:panelGroup>  
                        </h:panelGrid>   
                        
                        
                        <!-- HARVEST PANEL -->
                        <ui:panelGroup  block="true" styleClass="vdcStudyInfoHeader" style="margin-top: 5px;">
                            <h:outputText value="Harvest Utilities"/>
                            <h:outputLink  title="Display this panel" rendered="#{!UtilitiesPage.harvestPanelRendered}" value="/dvn#{VDCRequest.currentVDCURL}/faces/networkAdmin/UtilitiesPage.jsp?selectedPanel=harvest">  
                                <h:graphicImage  styleClass="vdcNoBorders" value="/resources/icon_contract.gif" />
                            </h:outputLink>
                            <h:graphicImage  styleClass="vdcNoBorders" value="/resources/icon_expand.gif" rendered="#{UtilitiesPage.harvestPanelRendered}" />
                        </ui:panelGroup>   
                        
                        <h:panelGrid style="margin-left: auto; margin-right: auto;" rendered="#{UtilitiesPage.harvestPanelRendered}" > 
                            <h:messages id="harvestMessage"  styleClass="errorMessage"/> 
                            <h:outputText value="To harvest a specific study, input the DV and the harvest identifier, and click on the button below:"/>
                            <ui:panelGroup>
                                <h:selectOneMenu  value="#{UtilitiesPage.harvestDVId}">
                                    <f:selectItems value="#{UtilitiesPage.harvestDVs}" />
                                </h:selectOneMenu>                            
                                <h:inputText value="#{UtilitiesPage.harvestIdentifier}" size="60" />                                
                                <h:commandButton  value="Harvest Study" action="#{UtilitiesPage.harvestStudy_action}"/>
                            </ui:panelGroup>                            
                        </h:panelGrid>                        
                        
                        
                        <!-- FILE PANEL -->
                        <ui:panelGroup  block="true" styleClass="vdcStudyInfoHeader" style="margin-top: 5px;">
                            <h:outputText value="File Utilities"/>
                            <h:outputLink  title="Display this panel" rendered="#{!UtilitiesPage.filePanelRendered}" value="/dvn#{VDCRequest.currentVDCURL}/faces/networkAdmin/UtilitiesPage.jsp?selectedPanel=file">  
                                <h:graphicImage  styleClass="vdcNoBorders" value="/resources/icon_contract.gif" />
                            </h:outputLink>
                            <h:graphicImage  styleClass="vdcNoBorders" value="/resources/icon_expand.gif" rendered="#{UtilitiesPage.filePanelRendered}" />
                        </ui:panelGroup>   
                        
                        <h:panelGrid style="margin-left: auto; margin-right: auto;" rendered="#{UtilitiesPage.filePanelRendered}" > 
                            <h:messages id="fileMessage"  styleClass="errorMessage"/>
                            <h:outputText value="To determine the file types for files with a particular extension:"/>
                            <ui:panelGroup>
                                <h:inputText value="#{UtilitiesPage.fileExtension}" size="6" />
                                <h:commandButton  value="Determine File Types for Extension" action="#{UtilitiesPage.determineFileTypeForExtension_action}"/>
                            </ui:panelGroup>
                            <hr/>
                            <h:outputText value="To determine the file types for files in arbitrary studies, input the study ids and click on the button below:"/>
                            <h:inputTextarea value="#{UtilitiesPage.fileStudyIds}" rows="8" cols="80"/>
                            <h:commandButton  value="Determine File Types for Studies" action="#{UtilitiesPage.determineFileTypeForStudies_action}"/>
                        </h:panelGrid>  
                        
                        
                        <!-- IMPORT PANEL -->
                        <ui:panelGroup  block="true" styleClass="vdcStudyInfoHeader" style="margin-top: 5px;">
                            <h:outputText value="Import Utilities"/>
                            <h:outputLink  title="Display this panel" rendered="#{!UtilitiesPage.importPanelRendered}" value="/dvn#{VDCRequest.currentVDCURL}/faces/networkAdmin/UtilitiesPage.jsp?selectedPanel=import">  
                                <h:graphicImage  styleClass="vdcNoBorders" value="/resources/icon_contract.gif" />
                            </h:outputLink>
                            <h:graphicImage  styleClass="vdcNoBorders" value="/resources/icon_expand.gif" rendered="#{UtilitiesPage.importPanelRendered}" />
                        </ui:panelGroup>  
                        
                        <h:panelGrid style="margin-left: auto; margin-right: auto;" rendered="#{UtilitiesPage.importPanelRendered}" > 
                            <h:messages id="importMessage"  styleClass="errorMessage"/> 
                            <h:outputText value="Select the file format and the dataverse for the import, then either:"/>
                            <ui:panelGroup>
                                <h:selectOneMenu  value="#{UtilitiesPage.importFileFormat}">
                                    <f:selectItems value="#{UtilitiesPage.importFileFormatTypes}" />
                                </h:selectOneMenu>                                   
                                <h:selectOneMenu  value="#{UtilitiesPage.importDVId}">
                                    <f:selectItems value="#{UtilitiesPage.importDVs}" />
                                </h:selectOneMenu> 
                            </ui:panelGroup>    
                            <hr/>
                            
                            <h:outputText value="a) Batch import - input directory and click on the button:"/>
                            <ui:panelGroup>
                                <h:inputText value="#{UtilitiesPage.importBatchDir}" size="80" />
                                <h:commandButton  value="Batch Import" action="#{UtilitiesPage.importBatch_action}"/>
                            </ui:panelGroup>
                            <hr/>

                            <h:outputText value="or b) Single import - browse for the file and click on the button:"/>
                            <ui:panelGroup>                         
                                <ui:upload  id="fileBrowser"  uploadedFile ="#{UtilitiesPage.importFile}"/>
                                <h:commandButton  value="Single Import" action="#{UtilitiesPage.importSingleFile_action}"/>
                            </ui:panelGroup>  
                            <hr/>
                            
                        </h:panelGrid>
                        
                        
                    </div>
                </div>
            </div>
        </ui:form>
            </gui:define>
        </gui:composition>
    </body>
</html>
