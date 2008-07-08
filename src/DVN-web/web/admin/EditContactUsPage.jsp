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
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

</head>

<body>
<gui:composition template="/template.xhtml">

<gui:param name="pageTitle" value="DVN - Edit Contact Us E-Mail" />

  <gui:define name="body">
      <f:loadBundle basename="Bundle" var="bundle"/>
     <f:loadBundle basename="BundleAnalysis" var="bundleAnalysis"/>

                            
        <ui:form  id="editContactUsForm">
            <h:inputHidden rendered="#{VDCRequest.currentVDCId != null}" id="vdcId" value="#{VDCRequest.currentVDCId}"/>
            
            <div class="dvn_section">
                <div class="dvn_sectionTitle">
                    
                        <h:outputText  value="#{bundle.editContactUsHeading}"/>
                    
                </div>            
                <div class="dvn_sectionBox">
                    <div class="dvn_margin12">
 
                        <h:messages  layout="list" showDetail="false" showSummary="true" styleClass="successMessage" />
                        
                        <ui:helpInline  id="helpInline1" style="margin-bottom: 20px" text="#{bundle.editContactUsHelpMsg}"/>
                        <ui:panelGroup>
                            <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                            <h:outputText id="outputText4" styleClass="vdcHelpText" value="Separate multiple email addresses with a comma (and no spaces). Example: admin@mydvn.edu,user@mydvn.edu"/>
                        </ui:panelGroup>
                        <f:verbatim><br /></f:verbatim>
                        <h:panelGrid  cellpadding="0" cellspacing="0"
                                      columnClasses="vdcColPadded, vdcColPadded" columns="2" id="gridPanel2">
                            
                            <ui:panelGroup  id="groupPanel2">
                                <h:outputText  id="outputText5" value="#{bundle.editContactUsEmailLabel}"/>
                                <h:graphicImage  id="image1" value="#{bundle.iconRequired}"/>
                            </ui:panelGroup>
                            
                            <h:inputText onkeypress="if (window.event) return processEvent('', 'content:EditContactUsPageView:editContactUsForm:btnSave'); else return processEvent('content:EditContactUsPageView:editContactUsForm:btnSave');"  
                                         id="contactUsEmail" 
                                         size="50" 
                                         value="#{EditContactUsPage.contactUsEmail}" 
                                         required="true"
                                         requiredMessage="This field is required.">
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
            </div>
              
        </ui:form>              
            </gui:define>
        </gui:composition>
    </body>
</html>