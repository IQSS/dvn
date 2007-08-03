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
                       
                            <ui:panelLayout  id="layoutPanel1" panelLayout="flow" styleClass="vdcSectionMiddleFixed" style="width: 500px">
                                <ui:panelLayout  id="layoutPanel2" panelLayout="flow" styleClass="vdcSectionHeader">
                                    <h:outputText value="Request to Become a Dataverse Creator"/>
                                </ui:panelLayout>
                                <ui:panelLayout panelLayout="flow" style="padding-left: 50px; padding-top: 40px; padding-bottom: 30px; padding-right: 20px;">
                                    <ui:panelGroup  block="true" >
                                        <h:outputText value="In order to request to become a dataverse Creator, please create an account. If you already have an account, please log in."/>
                                    </ui:panelGroup>
                                    <ui:panelGroup  block="true" style="padding-top: 10px; text-align: center;" >
                                        <h:outputLink value="/dvn#{VDCRequest.currentVDCURL}/faces/login/LoginPage.jsp?workflow=creator">
                                            <h:outputText value="Log in"/>
                                        </h:outputLink>
                                        <h:outputText value=" | " />
                                        <h:outputLink value="/dvn#{VDCRequest.currentVDCURL}/faces/login/AddAccountPage.jsp?workflow=creator">
                                            <h:outputText value="Create Account"/>
                                        </h:outputLink>
                                    </ui:panelGroup>
                                    
                                 
                                  
                                </ui:panelLayout>
                           </ui:panelLayout> 
                    </ui:form>
            </f:subview>
</jsp:root>
