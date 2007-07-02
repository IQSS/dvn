<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles">
            <f:subview id="StudyLockedPageView">
                    <ui:form  id="studyLockedForm">
                        <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
                              <ui:panelLayout  id="layoutPanel1" panelLayout="flow" styleClass="vdcSectionMiddleFixed" style="width: 500px">
                                <ui:panelLayout  id="layoutPanel2" panelLayout="flow" styleClass="vdcSectionHeader">
                                             <h:outputText value="File Upload In Progress"/>
                                </ui:panelLayout>
                                <ui:panelLayout  panelLayout="flow" style="padding-left: 50px; padding-top: 50px; padding-bottom: 10px; padding-right: 50px;">
                                    <h:outputText  style="warnMessage" value="We are sorry; this study is currently unavailable for editing because files are being uploaded.   When the file upload is completed the study will be editable again. "/>
                                </ui:panelLayout>
                                <ui:panelLayout  panelLayout="flow" style="padding-left: 50px; padding-top: 10px; padding-bottom: 50px; padding-right: 50px;">
                                   
                                    <h:outputText   style="warnMessage" value="#{param.message}"/>   
                                </ui:panelLayout>
                            </ui:panelLayout>
                            
                    </ui:form>
            </f:subview>
</jsp:root>
