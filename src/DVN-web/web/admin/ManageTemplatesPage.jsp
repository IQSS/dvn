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


<gui:param name="pageTitle" value="DVN - Manage Study Templates" />

  <gui:define name="body">
      <f:loadBundle basename="Bundle" var="bundle"/>
     <f:loadBundle basename="BundleAnalysis" var="bundleAnalysis"/>
       
         <f:verbatim>           
           <script type="text/javascript">
                function updateDefault(radioButton) {
                    hiddenNetworkTemplate = document.getElementById("content:ManageTemplatesPageView:templateForm:tabSet1:files:fileDataTableWrapper:0:fileDataTable:catDropdown");
                    hiddenNetworkTemplate.value=radioButton.value;
                }
           
           </script>
           
       </f:verbatim>
        <ui:form  id="templateForm">
            <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
            <h:inputHidden id="hiddenNetworkTemplateId" value="#{ManageTemplatesPage.networkTemplateId}"/>
           
            <div class="dvn_section">
                <div class="dvn_sectionTitle">
                     
                        <h:outputText value="Manage Templates"/>
                        <br/>
                        <h:commandButton value="Add Template" action="addTemplateAction"/>
                      
                </div>            
                <div class="dvn_sectionBox">
                  <div class="dvn_margin12">  
                    
                    <ui:panelGroup block="true" style="padding-bottom: 10px">
                        <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                        <h:outputText  styleClass="vdcHelpText" escape="false" value="Template help text here" />
                    </ui:panelGroup>
                    
                    <h:dataTable cellpadding="0" cellspacing="0"
                                 binding="#{ManageTemplatesPage.templateDataTable}"
                                 headerClass="list-header-left vdcColPadded" id="dataTable1" 
                                 rowClasses="list-row-even vdcColPadded, list-row-odd vdcColPadded" 
                                 value="#{ManageTemplatesPage.templateList}" var="currentRow" width="100%">
                       
                                     
                         <h:column >
                            <f:facet name="header">
                                <h:outputText  value="Default"/>
                            </f:facet>
                             
                            <h:outputText  value="Current Default Template" rendered="#{ManageTemplatesPage.defaultTemplateId==currentRow.id}" />   
                            <h:commandLink  value="Make Default" rendered="#{ManageTemplatesPage.defaultTemplateId!=currentRow.id}"  action="#{ManageTemplatesPage.updateDefaultAction}"/>
                                
                        </h:column>
                       <h:column >
                            <f:facet name="header">
                                <h:outputText  value="Name"/>
                            </f:facet>
                            <h:outputText  value="#{currentRow.name}" />
                            
                        </h:column>
              
                        <h:column >
                            <f:facet name="header">
                                <h:outputText  value="Remove"/>
                            </f:facet>
                           
                            <h:outputLink  rendered="#{currentRow.id!=ManageTemplatesPage.networkTemplateId}" value="/dvn#{VDCRequest.currentVDCURL}/faces/study/DeleteTemplatePage.jsp?templateId=#{currentRow.id}">
                                <h:outputText  value="Remove"/>
                            </h:outputLink>                           
                        </h:column>
                         <h:column >
                            <f:facet name="header">
                                <h:outputText value="Edit"/>
                            </f:facet>
                           
                            <h:outputLink  rendered="#{currentRow.id!=ManageTemplatesPage.networkTemplateId}" value="/dvn#{VDCRequest.currentVDCURL}/faces/study/TemplateFormPage.jsp?templateId=#{currentRow.id}">
                                <h:outputText  value="Edit"/>
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
