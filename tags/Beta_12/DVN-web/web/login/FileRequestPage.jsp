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
            
                       
                        <ui:panelLayout  id="layoutPanel1" panelLayout="flow" styleClass="vdcSectionMiddleFixed" style="width: 500px">
                            <ui:panelLayout  id="layoutPanel2" panelLayout="flow" styleClass="vdcSectionHeader">
                                <h:outputText value="Request Access to Restricted File"/>
                            </ui:panelLayout>
                            <ui:panelLayout panelLayout="flow" style="padding-left: 50px; padding-top: 40px; padding-bottom: 30px; padding-right: 20px;">
                            
                                
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
                             
                            </ui:panelLayout> 
                        </ui:panelLayout>
                    </ui:form>
            </f:subview>
</jsp:root>
