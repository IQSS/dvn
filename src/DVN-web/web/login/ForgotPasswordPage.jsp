<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles">
    <f:subview id="ForgotPasswordPageView">
        <ui:form  id="form1">
           <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
       
           <input type="hidden" name="pageName" value="ForgotPasswordPage"/>
           <div class="dvn_section">
               <div class="dvn_sectionTitle">   
                       <h:outputText value="Forgot Password"/>
               </div>            
               <div class="dvn_sectionBox"> 
               
                   <div class="dvn_margin12"> 
               
                       <ui:panelGroup block="true" style="padding-bottom: 15px" >
                            <h:outputText styleClass="vdcHelpText" value="If you have lost or forgotten your password, enter your user name below and click Submit. We will send you an email with your new password."/>
                       </ui:panelGroup>
                                  
                       <h:panelGrid cellpadding="0" cellspacing="0"
                                     columnClasses="vdcColPadded, vdcColPadded" columns="2" id="gridPanel2">
                      
                           <ui:panelGroup>
                               <h:outputText value="User Name"/>
                           </ui:panelGroup>
                           <ui:panelGroup>
                               <h:inputText id="inputUserName" value="#{ForgotPasswordPage.userName}" validator="#{ForgotPasswordPage.validateUserName}" size="40" required="true" requiredMessage="This field is required."/>
                                   
                           
                               <h:message styleClass="errorMessage" for="inputUserName"/>
                           </ui:panelGroup>
                           
                       </h:panelGrid>                      
                        
                       <ui:panelGroup block="true" style="padding-left: 100px; padding-top: 20px">
                           <h:commandButton value="Submit" action="#{ForgotPasswordPage.submit}"/>
                       </ui:panelGroup>                     
                   </div>
               </div>
           </div>
                        
        </ui:form>
    </f:subview>
</jsp:root>
