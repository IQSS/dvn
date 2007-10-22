<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
          xmlns:h="http://java.sun.com/jsf/html" 
          xmlns:jsp="http://java.sun.com/JSP/Page" 
          xmlns:ui="http://www.sun.com/web/ui"
          xmlns:tiles="http://struts.apache.org/tags-tiles"
          xmlns:c="http://java.sun.com/jsp/jstl/core">
    
<f:subview id="addHarvestSitePageView">
    <f:verbatim>
        <script language="Javascript">
            function checkSubsetting( copyValue ) {
                filesRestricted = getSelect("dropdown3");
                subsetRestricted = getSelect("dropdown4");

                subsetRestricted.disabled = !(filesRestricted.value == 'true');
                if (copyValue) {
                    subsetRestricted.value = filesRestricted.value;
                    
                }
            }
            function updateScheduleInput(  ) {
                scheduled = getInput("scheduledCheckBox");
                schedulePeriod = getSelect( "selectSchedulePeriod");
                scheduleHourOfDay = getInput("inputScheduleHourOfDay");
                scheduleDayOfWeek = getInput("inputScheduleDayOfWeek");
                scheduleDayOfWeekMsg = getSpan("inputScheduleDayOfWeekMsg");
                        
                  if (scheduled.checked == false) {
                    
                    schedulePeriod.value='notSelected';
                    scheduleHourOfDay.value='';
                    scheduleDayOfWeek.value='';
                    schedulePeriod.disabled = true;
                    scheduleHourOfDay.disabled = true;
                    scheduleDayOfWeek.disabled = true;
              
                } else {
                    schedulePeriod.disabled = false;
                    if (schedulePeriod.value=="daily") {
                        scheduleHourOfDay.disabled = false;
                        scheduleDayOfWeek.disabled = true;
                        scheduleDayOfWeek.value='';     
                    } else if (schedulePeriod.value=="weekly") {
                        scheduleDayOfWeek.disabled = false;
                        scheduleHourOfDay.disabled = false;
                    } else {
                        scheduleDayOfWeek.disabled = true;
                        scheduleHourOfDay.disabled = true;
                        scheduleHourOfDay.value='';
                        scheduleDayOfWeek.value='';
                  
                    }
               
                }
                
               
             }
            
            function getInput( id ) {
         
                elements=document.getElementsByTagName("input");
                for(i=0; i &lt; elements.length; i++) {
                
                    if (elements[i].id.indexOf(id) != -1 ) { 
                
                        return elements[i];
                    }
                }
                               
            }
            
            function getSpan( id ) {
         
                elements=document.getElementsByTagName("span");
                for(i=0; i &lt; elements.length; i++) {
                
                    if (elements[i].id.indexOf(id) != -1 ) { 
                
                        return elements[i];
                    }
                }
                               
            }
            
     
           function getSelect( id ) {
                elements=document.getElementsByTagName("select");
                for(i=0; i &lt; elements.length; i++) {
                    if (elements[i].id.indexOf(id) != -1) {    
                        return elements[i];
                    }
                }
            }
            
        </script>
    </f:verbatim>        
    <ui:form id="form1">
        <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
        <input type="hidden" name="pageName" value="EditHarvestSitePage"/>
        <h:inputHidden id="harvestId" value="#{EditHarvestSitePage.harvestId}"/>

        <div class="dvn_section">
            <div class="dvn_sectionTitle">
                
                    <h:outputText value="Harvest Dataverse"/>
                
            </div>            
            <div class="dvn_sectionBox"> 
                <div class="dvn_margin12">
                    
                    <ui:panelLayout styleClass="successMessage" rendered="#{EditHarvestSitePage.success}">
                        <h:outputText value="Update Successful!" />
                    </ui:panelLayout>
                    <ui:panelGroup block="true" style="padding-left: 20px; padding-right: 30px">
                        <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                        <h:outputText  id="outputText2" styleClass="vdcHelpText" value="A Harvest dataverse gathers studies from an OAI Server (which could be another Dataverse Network). A Harvest dataverse provides most of the same functionality as a regular dataverse with the following differences: 1) You cannot edit a harvested study. 2) You search for the cataloging and variable information (study metadata) locally but you access the files remotely. "/>
                        <h:outputText   styleClass="vdcHelpText" value="Any changes you make to this page will not be saved until you click the Save button. "/>
                    </ui:panelGroup>
                    <h:panelGrid cellpadding="0" cellspacing="0"
                                 columnClasses="vdcAddSiteCol1, vdcAddSiteCol2" columns="2" id="gridPanel1" style="margin-top: 30px; margin-bottom: 30px;">
                        <ui:panelGroup block="true" style="vertical-align: top;">
                            <h:outputLabel  for="componentLabel1" id="componentLabel1">
                                <h:outputText  id="componentLabel1Text" style="white-space: nowrap; padding-right: 10px; " value="Dataverse Name"/>                                              
                            </h:outputLabel>
                        </ui:panelGroup>
                        <ui:panelGroup>
                            
                              <h:inputText id="dataverseName" value="#{EditHarvestSitePage.dataverseName}" required="true" validator="#{EditHarvestSitePage.validateName}" size="60">
                                  
                              </h:inputText>
                          <h:message styleClass="errorMessage" for="dataverseName"/> 
                            <br />
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
                            
                            <br />
                            <h:outputText styleClass="vdcHelpText" value="Short name used to build the URL for this dataverse, e.g., http://.../dv/'alias'. It is case sensitive."/>
                        </ui:panelGroup>
                        <ui:panelGroup block="true" style="vertical-align: top;">
                            <h:outputText style="white-space: nowrap; padding-right: 10px; " value="OAI Server"/> 
                        </ui:panelGroup>
                        <ui:panelGroup>
                            <h:inputText id="dataverseOaiServer" validator="#{EditHarvestSitePage.validateOAIServer}"  value="#{EditHarvestSitePage.harvestingDataverse.oaiServer}"  required="true" size="60">
                                <f:validator validatorId="UrlValidator"/>
                            </h:inputText>  
                            <h:commandButton  value="Validate" />
                            <h:message styleClass="errorMessage" for="dataverseOaiServer"/> 
                            
                            <br />
                            <h:outputText styleClass="vdcHelpText" value="Enter OAI server name, e.g., http://dvn.iq.harvard.edu/dvn/OAIHandler."/> 
                        </ui:panelGroup>               
                   
                        <ui:panelGroup block="true" style="vertical-align: top;" rendered="#{not empty EditHarvestSitePage.editHarvestSiteService.harvestingSets}" > 
                            <h:outputText style="white-space: nowrap; padding-right: 10px; " value="Harvesting Set"/> 
                        </ui:panelGroup>
                        <ui:panelGroup rendered="#{not empty EditHarvestSitePage.editHarvestSiteService.harvestingSets}">                          
                            <h:selectOneMenu  id="radioButtonList1" value="#{EditHarvestSitePage.harvestingDataverse.harvestingSet}"  >
                                <f:selectItems  id="radio1SelectItem" value="#{EditHarvestSitePage.harvestingSetsSelect}" />
                            </h:selectOneMenu>
                            <verbatim><br /></verbatim>
                            <h:outputText styleClass="vdcHelpText" value="Select the set you would like to harvest. If you are harvesting another DVN and a set is not specified, all studies owned by that DVN will be harvested (without including studies harvested by that DVN)."/>
                        </ui:panelGroup>  
                        <ui:panelGroup rendered="#{ EditHarvestSitePage.editHarvestSiteService.harvestingSets==null and  EditHarvestSitePage.harvestingDataverse.oaiServer !=null}">   
                            <h:outputText value="This OAI Server does not support sets. If you are harvesting another DVN and a set is not specified, all studies owned by that DVN will be harvested (without including studies harvested by that DVN)."/>
                        </ui:panelGroup>
                        <ui:panelGroup block="true" style="vertical-align: top;" rendered="#{not empty EditHarvestSitePage.editHarvestSiteService.metadataFormats}">
                            <h:outputText style="white-space: nowrap; padding-right: 10px; " value="Harvesting Format"/> 
                        </ui:panelGroup>
                        <ui:panelGroup  rendered="#{not empty EditHarvestSitePage.editHarvestSiteService.metadataFormats}">
                            <h:selectOneMenu   value="#{EditHarvestSitePage.editHarvestSiteService.selectedMetadataPrefixId}" >
                                <f:selectItems  value="#{EditHarvestSitePage.metadataFormatsSelect}" />
                            </h:selectOneMenu>
                            <br />
                            <h:outputText styleClass="vdcHelpText" value="This is the XML format when used harvesting studies from this OAI Server."/>
                        </ui:panelGroup>  
                       </h:panelGrid>
                       
                        <h:panelGrid cellpadding="0" cellspacing="0"
                                 columnClasses="vdcAddSiteCol1, vdcAddSiteCol2" columns="2" style="margin-top: 30px; margin-bottom: 30px">                       
                        <ui:panelGroup  >
                            <h:outputText style="white-space: nowrap; padding-right: 10px; " value="Handle Registration"/> 
                        </ui:panelGroup >
                        <ui:panelGroup>    
                            <h:selectOneMenu  binding="#{EditHarvestSitePage.handlePrefixSelectOneMenu}" value="#{EditHarvestSitePage.editHarvestSiteService.selectedHandlePrefixId}" >
                                <f:selectItem itemValue="" itemLabel="Do not register harvested studies (studies must already have a handle)"/>
                                <f:selectItems  value="#{EditHarvestSitePage.handlePrefixSelect}" />
                            </h:selectOneMenu>
                            <br />
                            
                        </ui:panelGroup>  
                        <ui:panelGroup  >
                            <h:outputText style="white-space: nowrap; padding-right: 10px; " value="Schedule Harvesting?"/> 
                        </ui:panelGroup >
                      <ui:panelGroup  >
                       
                        <h:selectBooleanCheckbox binding="#{EditHarvestSitePage.scheduledCheckbox}" id="scheduledCheckBox" value="#{EditHarvestSitePage.harvestingDataverse.scheduled}"  onclick='updateScheduleInput();'/>
                      
                      </ui:panelGroup >
                    
                      <ui:panelGroup>
                          <div id="periodDiv1">
                            <h:outputText style="white-space: nowrap; padding-right: 10px; " value="Scheduled Harvesting Period"/> 
                          </div>  
                      </ui:panelGroup>                          
                      <ui:panelGroup>
                          <div id="periodDiv2">
                            <h:selectOneMenu validator="#{EditHarvestSitePage.validateSchedulePeriod}" id="selectSchedulePeriod" value="#{EditHarvestSitePage.harvestingDataverse.schedulePeriod}"  onchange='updateScheduleInput();' >
                                <f:selectItem itemValue="notSelected" itemLabel="Not Selected"/>
                                <f:selectItem itemValue="daily" itemLabel="Harvest daily"/>
                                <f:selectItem itemValue="weekly" itemLabel="Harvest weekly"/>
                            </h:selectOneMenu>
                          
                             <h:message id="selectSchedulePeriodMsg" styleClass="errorMessage" for="selectSchedulePeriod"/> 
                           
                            <br />
                            </div>
                        </ui:panelGroup>  
                        <ui:panelGroup  >
                            <div id="hourOfDayDiv1">
                            <h:outputText style="white-space: nowrap; padding-right: 10px; " value="Scheduled Harvesting Hour of Day (0-23)"/> 
                            </div>
                        </ui:panelGroup >
                      <ui:panelGroup  >
                        
                           <h:inputText required="true" id="inputScheduleHourOfDay" value="#{EditHarvestSitePage.harvestingDataverse.scheduleHourOfDay}">
                               <f:validateLongRange minimum="0" maximum="23" />
                           </h:inputText>
                        
                            <h:message id="inputScheduleHourOfDayMsg" styleClass="errorMessage" for="inputScheduleHourOfDay"/> 
                             
                          </ui:panelGroup >

                       <ui:panelGroup  >
                          <div id="dayOfWeekDiv1">
                            <h:outputText style="white-space: nowrap; padding-right: 10px; " value="Scheduled Harvesting Day Of Week (1-7)"/> 
                            </div>
                        </ui:panelGroup >
                      <ui:panelGroup  >
                          
                          <h:inputText required="true" id="inputScheduleDayOfWeek" value="#{EditHarvestSitePage.harvestingDataverse.scheduleDayOfWeek}">
                               <f:validateLongRange minimum="1" maximum="7" />
                          </h:inputText>
                          
                             <h:message id="inputScheduleDayOfWeekMsg" styleClass="errorMessage" for="inputScheduleDayOfWeek"/> 
                       
                       </ui:panelGroup >
                        
                        
                    </h:panelGrid>
                    <h:panelGrid cellpadding="0" cellspacing="0"
                                 columnClasses="vdcAddSiteCol1, vdcAddSiteCol2" columns="2" style="margin-top: 30px; margin-bottom: 30px">
                        <ui:panelGroup block="true" style="vertical-align: top;">
                            <h:outputText style="white-space: nowrap; padding-right: 10px; " value="File Permissions"/> 
                        </ui:panelGroup>
                        <ui:panelGroup>
                            <h:selectOneMenu  id="dropdown3" value="#{EditHarvestSitePage.harvestingDataverse.filesRestricted}" onchange="checkSubsetting(true)">
                                <f:selectItem   itemLabel="Public" itemValue="false" />
                                <f:selectItem   itemLabel="Restricted" itemValue="true" />
                            </h:selectOneMenu>
                            <br />
                            <h:outputText styleClass="vdcHelpText" value="Select to make all files for all studies in this dataverse public or restricted."/>
                        </ui:panelGroup>
                        <ui:panelGroup block="true" style="vertical-align: top;">
                            <h:outputText style="white-space: nowrap; padding-right: 10px; " value="Subset Permissions"/> 
                        </ui:panelGroup>
                        <ui:panelGroup>
                            <h:selectOneMenu  id="dropdown4" value="#{EditHarvestSitePage.subsetRestrictedWrapper}">
                                <f:selectItem   itemLabel="Public" itemValue="false" />
                                <f:selectItem   itemLabel="Restricted" itemValue="true" />
                            </h:selectOneMenu>                                            
                            <br />
                            <h:outputText styleClass="vdcHelpText" value="Select to make subsetting for all studies in this dataverse public or restricted."/>
                        </ui:panelGroup>                                           
                        <ui:panelGroup block="true" style="vertical-align: top;">
                            <h:outputText style="white-space: nowrap; padding-right: 10px; " value="Allowed Users, if Files are Restricted"/> 
                        </ui:panelGroup>
                        <ui:panelGroup>
                            <h:inputText binding="#{EditHarvestSitePage.userInputText}" value="#{EditHarvestSitePage.addUserName}" />
                            <h:commandButton  value="Add" actionListener="#{EditHarvestSitePage.addUser}" />
                            
                            <br />
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
                            
                            <br />
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
                        
                    </ui:panelGroup>
                    
                </div>
            </div>
        </div>
    </ui:form>
   <f:verbatim>
    
</f:verbatim>    
</f:subview>
 <script language="Javascript">
        // initial call to disable subsetting Restricted (if needed)
        checkSubsetting( false );
        updateScheduleInput();
  </script> 
    
</jsp:root>
