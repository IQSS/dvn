<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles">
    <f:subview id="EditPasswordPageView">
        <ui:form  id="form1">
           <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
           <h:inputHidden id="hiddenUserId" binding="#{EditPasswordPage.hiddenUserId}" value="#{EditPasswordPage.userId}"/>

           <input type="hidden" name="pageName" value="EditPasswordPage"/>
           <div class="dvn_section">
               <div class="dvn_sectionTitle">   
                       <h:outputText value="Forgot Password"/>
               </div>            
               <div class="dvn_sectionBox"> 
                   <div class="dvn_margin12"> 
                       <ui:panelGroup block="true" style="padding-bottom: 15px" rendered="#{VDCSession.loginBean.networkAdmin}">
                            <h:outputText styleClass="vdcHelpText" value="If you have lost or forgotten your user name or password, enter your email address below and click Submit. We will send you an email with your account information."/>
                       </ui:panelGroup>
                                  
                       <h:panelGrid cellpadding="0" cellspacing="0"
                                     columnClasses="vdcColPadded, vdcColPadded" columns="2" id="gridPanel2">
                      
                           <ui:panelGroup>
                               <h:outputText value="Email Address"/>
                           </ui:panelGroup>
                           <ui:panelGroup>
                               <h:inputText id="inputEmail" value="#{EditAccountPage.user.email}" size="40" required="true" requiredMessage="This field is required.">
                                   <f:validator validatorId="EmailValidator"/>
                               </h:inputText>
                               <h:message styleClass="errorMessage" for="inputEmail"/>
                           </ui:panelGroup>
                           
                       </h:panelGrid>                      
                        
                       <ui:panelGroup block="true" style="padding-left: 100px; padding-top: 20px">
                           <h:commandButton value="Submit" action="#{EditPasswordPage.save}"/>
                       </ui:panelGroup>                     
                   </div>
               </div>
           </div>
                        
        </ui:form>
    </f:subview>
</jsp:root>
