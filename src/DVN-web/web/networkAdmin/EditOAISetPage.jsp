<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                         xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles">
                            
      <f:verbatim>
      
    </f:verbatim>
      
            
    <f:subview id="EditOAISetPageView">
        <ui:form  id="form1">
         <h:inputHidden value="#{EditOAISetPage.oaiSet.id}" />
          <h:inputHidden value="#{EditOAISetPage.oaiSet.version}" />
         <input type="hidden" name="pageName" value="EditOAISetPage"/>
                                           
          <div class="dvn_section">
              <div class="dvn_sectionTitle">
                 
                      <h:outputText value="Edit OAI Set"/>
                 
              </div>            
              <div class="dvn_sectionBox">
                  <div class="dvn_margin12"> 
              <!--    <ui:panelLayout styleClass="successMessage" rendered="#{EditOAISetPage.success}">
                        <h:outputText value="Update Successful!" />
                  </ui:panelLayout> -->
                  <ui:panelGroup block="true" style="padding-bottom: 15px;">
                          <h:graphicImage value="/resources/icon_required.gif"/> <h:outputText style="vdcHelpText" value="Indicates a required field."/>
                      </ui:panelGroup>
                      <h:panelGrid  cellpadding="0" cellspacing="0" width="98%"
                                    columnClasses="vdcColPadded, vdcColPadded" columns="2" id="gridPanel2">
                        
                          <ui:panelGroup >
                              <h:outputText  value="Set Name"/>
                              <h:graphicImage  value="/resources/icon_required.gif"/>
                          </ui:panelGroup>
                          <ui:panelGroup> 
                              <h:inputText id="oaiSetName" value="#{EditOAISetPage.oaiSet.name}" required="true" requiredMessage="This field is required.">                                  
                              </h:inputText>
                              <h:message for="oaiSetName"  styleClass="errorMessage"/>
                              <br />
                              <h:outputText  styleClass="vdcHelpText" value="User friendly name to display when OAI is called." />
                          </ui:panelGroup>
                           <ui:panelGroup >
                              <h:outputText  value="Set Spec"/>
                              <h:graphicImage  value="/resources/icon_required.gif"/>
                          </ui:panelGroup>
                         <ui:panelGroup> 
                              <h:inputText id="oaiSetSpec" value="#{EditOAISetPage.oaiSet.spec}" required="true" requiredMessage="This field is required.">                                  
                              </h:inputText>
                              <h:message for="oaiSetSpec"  styleClass="errorMessage"/>
                              <br />
                              <h:outputText  styleClass="vdcHelpText" value="Unique Identifier for the set. It's the token passed from OAI server to client." />
                          </ui:panelGroup>
                           <ui:panelGroup >
                              <h:outputText  value="Query Definition"/>
                              <h:graphicImage  value="/resources/icon_required.gif"/>
                          </ui:panelGroup>
                         <ui:panelGroup> 
                              <h:inputText id="oaiSetDefiniton" value="#{EditOAISetPage.oaiSet.definition}" required="true" requiredMessage="This field is required." size="100">                                  
                              </h:inputText>
                              <h:message for="oaiSetDefiniton"  styleClass="errorMessage"/>
                              <br />
                              <h:outputText  styleClass="vdcHelpText" value="Query that defines set. It uses the same syntax as query collections." />
                          </ui:panelGroup>
                            <ui:panelGroup >
                              <h:outputText  value="Description"/>
                          </ui:panelGroup>
                         <ui:panelGroup> 
                              <h:inputText id="oaiSetDescription" value="#{EditOAISetPage.oaiSet.description}" size="100">                                  
                              </h:inputText>
                              <h:message for="oaiSetDescription"  styleClass="errorMessage"/>
                              <br />
                              <h:outputText  styleClass="vdcHelpText" value="Optional description about the OAI set." />
                          </ui:panelGroup>
                          
                       
                         </h:panelGrid>
                   
                      <ui:panelGroup block="true"  style="padding-left: 100px; padding-top: 20px">
                          <h:commandButton  value="Save" action="#{EditOAISetPage.save}"/>
                          <h:commandButton  immediate="true" value="Cancel" action="#{EditOAISetPage.cancel}"/>
                      </ui:panelGroup>
                      
                  </div>
              </div>
          </div>  
                                
       </ui:form>
   </f:subview>
   <script type="text/javascript">
        // initial call to disable subsetting Restricted (if needed)
        updateScheduleInput();
  </script> 
</jsp:root>
