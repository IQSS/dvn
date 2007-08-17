<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles">
    <f:subview id="CreatorRequestPageView">
        <ui:form  id="creatorRequestForm">
          <input type="hidden" name="pageName" value="CreatorRequestPage"/>
            <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>

            <div class="dvn_section">
                <div class="dvn_sectionTitle">
                    <h3>
                        <h:outputText value="Request to Become a Dataverse Creator"/>
                    </h3>
                </div>            
                <div class="dvn_sectionBox">
                    <div class="dvn_margin12">
                        
                        <h:outputText value="Your request has been received.  Please wait for approval from the administrator."/>
                        <h:outputLink value="/dvn#{VDCRequest.currentVDCURL}/faces/HomePage.jsp" title="Go to Dataverse Home Page"/>
                        
                    </div>
                </div>
            </div>
        </ui:form>
    </f:subview>
</jsp:root>
