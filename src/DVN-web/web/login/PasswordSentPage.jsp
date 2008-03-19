<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles">
    <f:subview id="LogoutPageView">
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
    </f:subview>
</jsp:root>
