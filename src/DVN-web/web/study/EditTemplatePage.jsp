<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
    xmlns:h="http://java.sun.com/jsf/html" 
    xmlns:jsp="http://java.sun.com/JSP/Page" 
    xmlns:ui="http://www.sun.com/web/ui"
    xmlns:tiles="http://struts.apache.org/tags-tiles"
    xmlns:c="http://java.sun.com/jsp/jstl/core">
    
    <f:subview id="editTemplatePageView">
      
        <h:form  id="form1">
            <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
            <input type="hidden" name="pageName" value="EditTemplatePage"/>
            <h:inputHidden id="studyId" value="#{EditTemplatePage.studyId}"/>                                    

            <div class="dvn_section">
                <div class="dvn_sectionTitle">
                  
                        <h:outputText  value="Creating a Study Template"/>
                     
                </div>            
                <div class="dvn_sectionBox">
                    <div class="dvn_margin12">
                        
                        <ui:panelGroup  block="true" id="groupPanel1">
                            <h:outputText   value="Creating a Template from Study"/>
                             <br />
                           
                           <h:outputText   value="Enter Template Name:"/>
                           <h:inputText value="#{EditTemplatePage.templateName}" />
                        </ui:panelGroup>
                        <ui:panelGroup  block="true" id="groupPanel2" style="padding-top: 30px; text-align: center">
                            <h:commandButton  id="button1" value="Create Template" action="#{EditTemplatePage.save}"/>
                            <h:commandButton  immediate="true" id="button2" style="margin-left: 30px" value="Cancel" action="#{EditTemplatePage.cancel}"/>
                        </ui:panelGroup>
                        
                    </div>
                </div>
            </div>
        </h:form>
               
    </f:subview>
</jsp:root>
