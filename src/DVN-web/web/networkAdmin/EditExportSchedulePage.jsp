<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles">
                            
      <f:verbatim>
        <script language="Javascript">                        
         function updateScheduleInput(  ) {
                exportPeriod = getSelect("exportPeriod");
                exportHourOfDay = getInput("exportHourOfDay");
                exportDayOfWeek = getInput("exportDayOfWeek");
                
                requiredPeriod = getSelect("requiredPeriod");
               
                requiredDayOfWeek = document.getElementById("requiredDayOfWeek");
                requiredDayOfWeekText = document.getElementById("requiredDayOfWeekText");
          
                  
                if (exportPeriod.value=="daily") {
                        exportHourOfDay.disabled = false;
                        requiredDayOfWeek.style.display='none';
                        requiredDayOfWeekText.style.display='none';
                        exportDayOfWeek.disabled = true;
                        exportDayOfWeek.value='';
                } else {
                        exportDayOfWeek.disabled = false;
                        exportHourOfDay.disabled = false;
                        requiredDayOfWeek.style.display='block';
                        requiredDayOfWeekText.style.display='block';
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
      
            
    <f:subview id="EditExportSchedulePageView">
        <ui:form  id="form1">
         
          <input type="hidden" name="pageName" value="EditExportSchedulePage"/>
                                           
          <div class="dvn_section">
              <div class="dvn_sectionTitle">
                 
                      <h:outputText value="Edit Export Schedule"/>
                 
              </div>            
              <div class="dvn_sectionBox">
                  <div class="dvn_margin12"> 
                  <ui:panelLayout styleClass="successMessage" rendered="#{EditExportSchedulePage.success}">
                        <h:outputText value="Update Successful!" />
                  </ui:panelLayout>
                  <ui:panelGroup block="true" style="padding-bottom: 15px;">
                          <h:graphicImage value="/resources/icon_required.gif"/> <h:outputText style="vdcHelpText" value="Indicates a required field."/>
                      </ui:panelGroup>
                      <h:panelGrid  cellpadding="0" cellspacing="0"
                                    columnClasses="vdcColPadded, vdcColPadded" columns="2" id="gridPanel2">
                          <ui:panelGroup >
                              <h:outputText   value="Export Period"/>
                              <h:graphicImage  id="requiredPeriod" value="/resources/icon_required.gif"/>
                          </ui:panelGroup>
                          <ui:panelGroup>
                          
                            <h:selectOneMenu required="true"  id="exportPeriod" value="#{EditExportSchedulePage.exportPeriod}"  onchange='updateScheduleInput();' >
                                <f:selectItem itemValue="" itemLabel="Not Selected"/>
                                <f:selectItem itemValue="daily" itemLabel="Harvest daily"/>
                                <f:selectItem itemValue="weekly" itemLabel="Harvest weekly"/>
                            </h:selectOneMenu>
                             <h:message styleClass="errorMessage" for="exportPeriod"/> 
                            <br />
         
                          </ui:panelGroup>
                          <ui:panelGroup >
                              <h:outputText  value="Export Hour of Day (0-23)"/>
                              <h:graphicImage id="requiredHourOfDay" value="/resources/icon_required.gif"/>
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
                              <h:graphicImage  value="/resources/icon_required.gif"/>
                          </div>
                          </ui:panelGroup>
                           
                          <ui:panelGroup> 
                                <div id="requiredDayOfWeekText">
                              <h:inputText  id="exportDayOfWeek" value="#{EditExportSchedulePage.exportDayOfWeek}" required="true">
                                     <f:validateLongRange minimum="1" maximum="7" />
                              </h:inputText>
                              <h:message  for="exportDayOfWeek"  styleClass="errorMessage"/>
                                </div>
                          </ui:panelGroup>      
                          
                         </h:panelGrid>
                   
                      <ui:panelGroup block="true"  style="padding-left: 100px; padding-top: 20px">
                          <h:commandButton  value="Save" action="#{EditExportSchedulePage.save}"/>
                          <h:commandButton  immediate="true" value="Cancel" action="#{EditExportSchedulePage.cancel}"/>
                      </ui:panelGroup>
                      
                  </div>
              </div>
          </div>  
                                
       </ui:form>
   </f:subview>
   <script language="Javascript">
        // initial call to disable subsetting Restricted (if needed)
        updateScheduleInput();
  </script> 
</jsp:root>
