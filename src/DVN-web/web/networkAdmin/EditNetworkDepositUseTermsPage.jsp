<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles">
   <f:subview id="EditNetworkDepositUseTermsPageView">
        <ui:form  id="form1"> 
          <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
          
            <div class="dvn_section">
                <div class="dvn_sectionTitle">
                             
                    <h:outputText value="Edit Terms of Use"/>
                    
                </div>            
                <div class="dvn_sectionBox"> 
                    <div class="dvn_margin12">                    
                        
                       <h:messages  layout="list" showDetail="false" showSummary="true" styleClass="successMessage" />                        
                        
                        <h:outputText  value="Please enter the Terms of Use or Conditions you would like to define for all studies created in (owned by) this dataverse (does not include studies that you are adding from other Dataverses in the Network)."/>
                        <ui:panelGroup block="true" style="padding-bottom: 15px">
                            <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                            <h:outputText styleClass="vdcHelpText" value="By enabling these Terms of Use, users will be asked to agree to them before downloading a file or accessing the subsetting/analysis page. These Terms of Use will be also displayed in the Cataloging Information under the Terms of Use section as 'General Term of Use'."/>
                        </ui:panelGroup>
                        
                        <h:outputText value="Enable Terms of Use?"/>
                        <h:selectBooleanCheckbox value="#{EditNetworkDepositUseTermsPage.termsOfUseEnabled}"/>
                        <h:panelGrid  cellpadding="0" cellspacing="0"
                                      columnClasses="vdcColPadded, vdcColPadded" columns="2" id="gridPanel2">
                            <h:outputText  id="outputText2" value="Terms of Use"/>
                            <h:panelGroup><!-- my add -->
                                <h:message for="textArea1" styleClass="errorMessage" id="outputText2Msg"/>
                                <f:verbatim><br /></f:verbatim>
                                <h:inputTextarea styleClass="formHtmlEnabled" immediate="true" cols="70"
                                                 id="textArea1" 
                                                 rows="8" 
                                                 value="#{EditNetworkDepositUseTermsPage.termsOfUse}"
                                >
                                    <f:validator validatorId="XhtmlValidator"/>
                                </h:inputTextarea>
                            </h:panelGroup>
                            
                        </h:panelGrid>
                        <ui:panelGroup block="true" id="groupPanel2" style="padding-left: 140px; padding-top: 20px">
                            <h:commandButton id="button1" value="Save" action="#{EditNetworkDepositUseTermsPage.save_action}"/>
                            <h:commandButton id="button2" style="margin-left: 30px" value="Cancel" action="#{EditNetworkDepositUseTermsPage.cancel_action}"/>
                        </ui:panelGroup>
                    </div> 
                </div>
            </div>
        </ui:form>             
     </f:subview>
</jsp:root>
