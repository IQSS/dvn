<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles">
                            
   <f:subview id="EditContactUsPageView">
        <ui:form  id="editContactUsForm">
            <h:inputHidden rendered="#{VDCRequest.currentVDCId != null}" id="vdcId" value="#{VDCRequest.currentVDCId}"/>
            
            <div class="dvn_section">
                <div class="dvn_sectionTitle">
                    <h3>
                        <h:outputText  value="#{bundle.editContactUsHeading}"/>
                    </h3>
                </div>            
                <div class="dvn_sectionBox dvn_pad12">
                    
                    <ui:panelGroup styleClass="successMessage" rendered="#{EditContactUsPage.success}">
                        <h:messages  layout="table" showDetail="false" showSummary="true"/>
                    </ui:panelGroup>   
                    
                    
                    <ui:helpInline  id="helpInline1" style="margin-bottom: 20px" text="#{bundle.editContactUsHelpMsg}"/>
                    <h:panelGrid  cellpadding="0" cellspacing="0"
                                  columnClasses="vdcColPadded, vdcColPadded" columns="2" id="gridPanel2">
                        <ui:panelGroup  id="groupPanel2">
                            <h:outputText  id="outputText5" value="#{bundle.editContactUsEmailLabel}"/>
                            <h:graphicImage  id="image1" value="#{bundle.iconRequired}"/>
                        </ui:panelGroup>
                        <h:inputText onkeypress="if (window.event) return processEvent('', 'content:EditContactUsPageView:editContactUsForm:btnSave'); else return processEvent('content:EditContactUsPageView:editContactUsForm:btnSave');"  
                                     id="contactUsEmail" 
                                     size="30" 
                                     value="#{EditContactUsPage.contactUsEmail}" 
                                     required="true">
                            <f:validator validatorId="EmailValidator"/>
                        </h:inputText>
                        <h:message style="column-span:2;" 
                                   id="emailAddressMsg" 
                                   for="contactUsEmail"
                                   styleClass="errorMessage"/>
                    </h:panelGrid>
                    <ui:panelGroup  block="true" id="groupPanel6" style="padding-left: 140px; padding-top: 20px">
                        <h:commandButton  id="btnSave" value="#{bundle.saveButtonLabel}" action="#{EditContactUsPage.save_action}"/>
                        <h:commandButton  id="btnCancel" style="margin-left: 30px" immediate="true" value="#{bundle.cancelButtonLabel}" action="#{EditContactUsPage.cancel_action}"/>
                    </ui:panelGroup>
                    
                </div>
            </div>
              
        </ui:form>              
    </f:subview>
</jsp:root>