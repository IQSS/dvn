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

<gui:param name="pageTitle" value="DVN - Edit About" />

  <gui:define name="body">


                            
      <f:verbatim>
          
        <script type="text/javascript">  
         //<![CDATA[               
         function updateExportInput(  ) {
                exportPeriod = getSelect("exportPeriod");
                exportHourOfDay = getInput("exportHourOfDay");
                exportDayOfWeek = getInput("exportDayOfWeek");
                            
                   if (exportPeriod.value=="daily") {
                        exportHourOfDay.disabled = false;
                        exportDayOfWeek.disabled = true;
                        exportDayOfWeek.value='';     
                    } else if (exportPeriod.value=="weekly") {
                        exportDayOfWeek.disabled = false;
                        exportHourOfDay.disabled = false;
                    } else {
                        exportDayOfWeek.disabled = true;
                        exportHourOfDay.disabled = true;
                        exportHourOfDay.value='';
                        exportDayOfWeek.value='';
                    }                
                                  
             }
            
            function getInput( id ) {
         
                elements=document.getElementsByTagName("input");
                for(i=0; i < elements.length; i++) {
                
                    if (elements[i].id.indexOf(id) != -1 ) { 
                
                        return elements[i];
                    }
                }                               
            }
            function getSelect( id ) {
                elements=document.getElementsByTagName("select");
                for(i=0; i < elements.length; i++) {
                    if (elements[i].id.indexOf(id) != -1) {    
                        return elements[i];
                    }
                }
            }
           // ]]> 
         </script>
    </f:verbatim>
      
            
        <ui:form  id="form1">
         
          <input type="hidden" name="pageName" value="EditExportSchedulePage"/>
                                           
          <div class="dvn_section">
              <div class="dvn_sectionTitle">
                 
                      <h:outputText value="Study Export Schedule"/>
                 
              </div>            
              <div class="dvn_sectionBox">
                  <div class="dvn_margin12"> 
                  <ui:panelLayout styleClass="successMessage" rendered="#{EditExportSchedulePage.success}">
                        <h:outputText value="Update Successful!" />
                  </ui:panelLayout>
                  <ui:panelGroup block="true" style="padding-bottom: 15px;">
                       <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                      <h:outputText  styleClass="vdcHelpText" value=" Here you can schedule how often the studies in your Dataverse Network (DVN) will be exported into XML, 
                      using DDI (with study and variable metadata) and Dublin Core (with study metadata only) formats. 
                      These export files are used by the OAI server that comes with your DVN and, in general, for preservation and sharing of your DVN data."/>
                          <!--h:graphicImage value="/resources/icon_required.gif"/--> <!--h:outputText style="vdcHelpText" value="Indicates a required field."/-->
                      </ui:panelGroup>
                      <h:panelGrid  cellpadding="0" cellspacing="0"
                                    columnClasses="vdcColPadded, vdcColPadded" columns="2" id="gridPanel2">
                          <ui:panelGroup >
                              <h:outputText   value="Export Period"/>
                              <!--h:graphicImage  id="requiredPeriod" value="/resources/icon_required.gif"/-->
                          </ui:panelGroup>
                          <ui:panelGroup>
                          
                            <h:selectOneMenu  id="exportPeriod" value="#{EditExportSchedulePage.exportPeriod}"  onchange='updateExportInput();' >
                                <f:selectItem itemValue="" itemLabel="Not Selected"/>
                                <f:selectItem itemValue="daily" itemLabel="Export daily"/>
                                <f:selectItem itemValue="weekly" itemLabel="Export weekly"/>
                            </h:selectOneMenu>
                             <h:message styleClass="errorMessage" for="exportPeriod"/> 
                            <br />
         
                          </ui:panelGroup>
                          <ui:panelGroup >
                              <h:outputText  value="Export Hour of Day (0-23)"/>
                              <!--h:graphicImage id="requiredHourOfDay" value="/resources/icon_required.gif"/-->
                          </ui:panelGroup>
                          <ui:panelGroup> 
                              <h:inputText id="exportHourOfDay" value="#{EditExportSchedulePage.exportHourOfDay}" required="true">
                                     <f:validateLongRange minimum="0" maximum="23" />
                              </h:inputText>
                              <h:message for="exportHourOfDay"  styleClass="errorMessage"/>
                          </ui:panelGroup>
                           
                          <ui:panelGroup >
                          <div id="requiredDayOfWeek">
                              <h:outputText  value="Export Day of Week (1-7)"/>
                              <!--h:graphicImage  value="/resources/icon_required.gif"/-->
                          </div>
                          </ui:panelGroup>
                           
                          <ui:panelGroup> 
                                <div id="requiredDayOfWeekText">
                              <h:inputText  id="exportDayOfWeek" value="#{EditExportSchedulePage.exportDayOfWeek}" required="true" requiredMessage="This field is required.">
                                     <f:validateLongRange minimum="1" maximum="7" />
                              </h:inputText>
                              <h:message  for="exportDayOfWeek"  styleClass="errorMessage"/>
                                </div>
                          </ui:panelGroup>      
                          
                         </h:panelGrid>
                   
                      <ui:panelGroup block="true"  style="padding-left: 100px; padding-top: 20px">
                          <h:commandButton  value="Save" action="#{EditExportSchedulePage.save}"/>
                          <h:commandButton  immediate="true" value="Cancel" action="myNetworkOptions"/>
                      </ui:panelGroup>
                      
                  </div>
              </div>
          </div>  
                                
       </ui:form>
       <script type="text/javascript">
            // initial call to disable subsetting Restricted (if needed)
            updateExportInput();
        </script> 
            </gui:define>
        </gui:composition>
    </body>
</html>
