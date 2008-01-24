<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
          xmlns:h="http://java.sun.com/jsf/html" 
          xmlns:jsp="http://java.sun.com/JSP/Page" 
          xmlns:ui="http://www.sun.com/web/ui"
          xmlns:tiles="http://struts.apache.org/tags-tiles"
          xmlns:c="http://java.sun.com/jsp/jstl/core">
    
    <f:subview id="termsOfUsePageView">
        
        <ui:form  id="form1">
            <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/> 
            <input type="hidden" value="TermsOfUsePage" name="pageName"/>
       
              <div class="dvn_section">
               
                <div class="dvn_sectionTitle">
                    Terms of Use
                </div>   
                
                <div class="dvn_sectionBox">  
                    
                    <div class="dvn_margin12">
                        
                        <ui:panelGroup  block="true" style="padding-bottom: 20px;"  styleClass="warnMessage" >
                            <h:outputText value="Please agree to the terms of use below before accessing the Dataverse Network "/>
                         
                        </ui:panelGroup>
                        
                        <ui:panelGroup  block="true">
                              
                            <h:panelGrid columnClasses="vdcColPadded" width="98%">
                                <h:outputText  value="General Terms of Use:" styleClass="vdcTermsUseField" />
                                <h:outputText value="#{AccountTermsOfUsePage.termsOfUse}" escape="false"/>    
                            </h:panelGrid>
                         
                        </ui:panelGroup>  
                        
                         <ui:panelGroup  style="padding-top: 20px;" block="true">
                            <h:outputText styleClass="vdcFieldTitle" value="I accept the Terms of Use above:" />
                            <h:selectBooleanCheckbox id="termsAccepted" required="true" value="#{AccountTermsOfUsePage.termsAccepted}" />
                        </ui:panelGroup>
                        
                        <ui:panelGroup block="true" style="padding-top: 20px; text-align: center">
                            <h:commandButton  id="termsButton" value="Continue" action="#{AccountTermsOfUsePage.acceptTerms_action}"/>
                        </ui:panelGroup>
                        
                    </div>
                </div>
            </div>
        </ui:form>
    </f:subview>
</jsp:root>
