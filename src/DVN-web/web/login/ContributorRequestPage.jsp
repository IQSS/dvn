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

<gui:param name="pageTitle" value="DVN - Request to Contribute" />

  <gui:define name="body">
      <f:loadBundle basename="Bundle" var="bundle"/>
     <f:loadBundle basename="BundleAnalysis" var="bundleAnalysis"/>
                            
     

    <ui:form  id="contributorRequestForm">
       <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
       <h:inputHidden binding="#{ContributorRequestPage.hiddenUserId}" value="#{ContributorRequestPage.userId}"/>


        <div class="dvn_section">
            <div class="dvn_sectionTitle">
                <a name="request" title="">
                    <h:outputText value="Request to Become a Dataverse Contributor"/>
                </a>
            </div>            
            <div class="dvn_sectionBox">
                <div class="dvn_margin12">
                    
                    <ui:panelGroup  block="true"  style="padding-top: 10px" rendered="#{ContributorRequestPage.alreadyRequested}">
                        <h:outputText value="You have already requested to become a Contributor to #{VDCRequest.currentVDC.name} dataverse.  Please wait for approval from the administrator."/>
                        <h:outputLink value="/dvn#{VDCRequest.currentVDCURL}/faces/HomePage.jsp">
                            <h:outputText value="Go to Dataverse Home Page"/>
                        </h:outputLink>
                    </ui:panelGroup>
                    
                    <ui:panelGroup rendered="#{!ContributorRequestPage.alreadyRequested}">
                        <ui:panelGroup block="true"  style="padding-top: 10px"  >
                            <!--h:selectBooleanCheckbox id="contributorCheckbox" value="#{!ContributorRequestPage.alreadyRequested}" /-->
                            <h:outputText value="I want to become a contributor to #{VDCRequest.currentVDC.name} dataverse. This will allow me to upload my own data in this archive."/>
                        </ui:panelGroup>
                        <ui:panelGroup block="true"  style="padding-top: 10px; text-align: center;"  >                                      
                            <h:commandButton value="Submit Request" action="#{ContributorRequestPage.generateRequest}"/>
                            <h:commandButton value="Cancel" action="home"/>
                        </ui:panelGroup>
                    </ui:panelGroup>
                    
                </div>
            </div>
        </div>
    </ui:form>
</body>
</html>
