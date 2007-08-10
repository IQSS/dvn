<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles">
    <f:subview id="LogoutPageView">
        <ui:form  id="logoutForm">
            <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
            <div class="dvn_section">
                <div class="dvn_sectionTitle">
                    <h3>  
                        <h:outputText  value="Logout"/>
                    </h3>
                </div>            
                <div class="dvn_sectionBox dvn_pad12"> 
                    <ui:panelGroup  block="true"  style="padding-top: 40px; padding-left:20px" >
                        <h:outputText value="You are now logged out.  Thanks for visiting." styleClass="vdcTextStandOut"/>
                    </ui:panelGroup>
                    <ui:panelGroup  block="true"  style="padding-top: 20px; padding-left:70px" >
                        
                        <h:outputLink    value="/dvn#{VDCRequest.currentVDCURL}/faces/HomePage.jsp"  >
                            <h:outputText value="Go to the Home Page"/>
                        </h:outputLink>
                    </ui:panelGroup>
                    <ui:panelGroup  block="true"  style="padding-top: 10px; padding-bottom: 40px; padding-left:70px" >
                        <h:outputLink    value="/dvn#{VDCRequest.currentVDCURL}/faces/login/LoginPage.jsp"  >
                            <h:outputText value="Log in"/>
                        </h:outputLink>
                    </ui:panelGroup>
                </div>
            </div>
        </ui:form>
    </f:subview>
</jsp:root>
