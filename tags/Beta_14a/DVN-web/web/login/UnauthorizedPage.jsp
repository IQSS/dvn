<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles">
            <f:subview id="UnauthorizedPageView">
                    <ui:form  id="unauthorizedForm">
                        <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
                            <ui:panelLayout  id="layoutPanel1" panelLayout="flow" styleClass="vdcSectionMiddleFixed" style="width: 500px">
                                <ui:panelLayout  id="layoutPanel2" panelLayout="flow" styleClass="vdcSectionHeader">
                                    <h:outputText value="Restricted Page"/>
                                </ui:panelLayout>
                                <ui:panelLayout  panelLayout="flow" style="padding-left: 50px; padding-top: 50px; padding-bottom: 50px; padding-right: 50px;">
                                    <h:outputText  id="outputText4" style="warnMessage" value="We are sorry; you do not have permissions to view this page."/>            
                                </ui:panelLayout>
                            </ui:panelLayout> 
                    </ui:form>
            </f:subview>
</jsp:root>
