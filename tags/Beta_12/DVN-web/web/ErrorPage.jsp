<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles">
            <f:subview id="ErrorPageView">
                    <ui:form  id="errorForm">
                        <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
                            <ui:panelLayout  id="layoutPanel1" panelLayout="flow" styleClass="vdcSectionMiddleFixed" style="width: 500px">
                                <ui:panelLayout  id="layoutPanel2" panelLayout="flow" styleClass="vdcSectionHeader">
                                    <h:outputText value="Error"/>
                                </ui:panelLayout>
                                <ui:panelLayout  panelLayout="flow" style="padding-left: 50px; padding-top: 50px; padding-bottom: 50px; padding-right: 50px;">
                                    <h:outputText styleClass="errorMessage" value="We are sorry. An application error has occurred. "/>
                                    <f:verbatim><br /><br /></f:verbatim>
                                    <h:outputText value="Error Message:" rendered="#{param.errorMsg!=null}"/>
                                    <h:outputText styleClass="errorMessage" value="#{param.errorMsg}" rendered="#{param.errorMsg!=null}"/>
                                    
                                    <h:dataTable value="#{ErrorPage.messages}" var="currentRow">
                                        <h:column>
                                            <h:outputText styleClass="errorMessage" value="#{currentRow}"/>
                                        </h:column>
                                        
                                    </h:dataTable>
                                    
                                    
                                    <f:verbatim><br /><br /></f:verbatim>
                                    <h:outputText styleClass="errorMessage" value="Event occurred at " rendered="#{param.time!=null}" />
                                    <h:outputText styleClass="errorMessage" value="#{param.time}" rendered="#{param.time!=null}">
                                        <f:convertDateTime dateStyle="full"/>
                                    </h:outputText>
                                </ui:panelLayout>   
                            </ui:panelLayout> 
                    </ui:form>
            </f:subview>
</jsp:root>
