<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles">
    <f:subview id="UnauthorizedPageView">
        <ui:form  id="fileRequestForm">
            <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
            <h:inputHidden binding="#{FileRequestPage.hiddenUserId}" value="#{FileRequestPage.userId}"/>
            <h:inputHidden binding="#{FileRequestPage.hiddenStudyId}" value="#{FileRequestPage.studyId}"/>

            <div class="dvn_section">
                <div class="dvn_sectionTitle">
                    <h3>
                        <h:outputText value="Request Access to Restricted File"/>
                    </h3>
                </div>            
                <div class="dvn_sectionBox">
                    <div class="dvn_margin12">
                        
                        <ui:panelGroup rendered="#{ !FileRequestPage.alreadyRequested}">
                            <ui:panelGroup block="true"  style="padding-top: 10px" >
                                <h:outputText value="I would like to access restricted files in this study."/>
                            </ui:panelGroup>
                            <ui:panelGroup block="true"  style="padding-top: 10px; text-align: center;"  >                                      
                                <h:commandButton value="Submit Request" action="#{FileRequestPage.generateRequest}"/>
                                <h:commandButton value="Cancel" action="home"/>
                            </ui:panelGroup>
                        </ui:panelGroup>
                        <ui:panelGroup  block="true"  style="padding-top: 10px" rendered="#{FileRequestPage.alreadyRequested}">
                            <h:outputText value="You have already requested access to files in this study.  Please wait for approval from the administrator."/>
                            <h:outputLink value="/dvn#{VDCRequest.currentVDCURL}/faces/HomePage.jsp" title="Go to Dataverse Home Page"/>
                        </ui:panelGroup>
                        
                    </div>
                </div>
            </div>
        </ui:form>
    </f:subview>
</jsp:root>
