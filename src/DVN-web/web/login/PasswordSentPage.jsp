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

<gui:param name="pageTitle" value="DVN - Password Sent" />

  <gui:define name="body">
      <f:loadBundle basename="Bundle" var="bundle"/>
     <f:loadBundle basename="BundleAnalysis" var="bundleAnalysis"/>
                            
     

        <ui:form  id="logoutForm">
            <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
            <ui:panelLayout styleClass="successMessage">
                <h:outputText value="Your password has been emailed to you."/>
            </ui:panelLayout>
            <div class="dvn_section">
                <div class="dvn_sectionTitle">
                        <h:outputText value="Forgot Password"/>
                </div>
                <div class="dvn_sectionBox">
                    <div class="dvn_margin12">
                        <ui:panelGroup block="true" rendered="#{LogoutPage.success}">
                           <p> 
                               <h:outputLink value="/dvn">
                                    <h:outputText value="Go to the #{VDCRequest.vdcNetwork.name} Dataverse Network homepage"/>
                               </h:outputLink>
                            </p>
                            <p>
                               <h:outputLink rendered="#{VDCRequest.currentVDC != null}" value="/dvn#{VDCRequest.currentVDCURL}">
                                    <h:outputText value="Go to the #{VDCRequest.currentVDC.name} Dataverse homepage"/>
                               </h:outputLink>
                            </p>
                            <p>
                               <h:outputLink value="/dvn#{VDCRequest.currentVDCURL}/faces/login/LoginPage.jsp?clearWorkflow=true">
                                    <h:outputText value="Log in"/>
                               </h:outputLink>
                            </p>
                         </ui:panelGroup>
                    </div>
                </div>
            </div>
        </ui:form>
            </gui:define>
        </gui:composition>
    </body>
</html>
