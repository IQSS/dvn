<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles">
    <f:subview id="AccountPageView">
        <ui:form id="accountForm">
            <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
 
            <div class="dvn_section">
                <div class="dvn_sectionTitle">
                    <h3>
                         <h:outputText value="Account for "/>
                                    <h:outputText  value="#{AccountPage.user.userName}"/>
                    </h3>
                </div>            
                <div class="dvn_sectionBox">
                    <div class="dvn_margin12"> 
                                        
                        <ui:panelLayout styleClass="#{AccountPage.statusMessage.styleClass}" rendered="#{!empty AccountPage.statusMessage.messageText}">
                            <h:outputText id="statusMessage"  value="#{AccountPage.statusMessage.messageText}" />
                        </ui:panelLayout>                  
                        
                        <ui:panelGroup block="true" id="groupPanel2" style="padding-right: 10px; padding-bottom: 20px; text-align: right">
                            <h:outputLink id="linkAction1" value="/dvn/faces/login/EditAccountPage.jsp?userId=#{AccountPage.user.id}&amp;vdcId=#{VDCRequest.currentVDCId}">
                                <h:outputText id="linkAction1Text" value="Update Account"/>
                            </h:outputLink>
                            <h:outputText value=" | " rendered="#{VDCSession.loginBean.networkAdmin and VDCRequest.currentVDC == null}" />
                            <h:outputLink  rendered="#{VDCSession.loginBean.networkAdmin and VDCRequest.currentVDC == null}" value="/dvn/faces/networkAdmin/AllUsersPage.jsp?vdcId=#{VDCRequest.currentVDCId}">
                                <h:outputText  value="Go To All Users List"/>
                            </h:outputLink>
                        </ui:panelGroup>
                        <h:panelGrid cellpadding="0" cellspacing="0"
                                     columnClasses="vdcColPadded, vdcColPadded" columns="2" id="gridPanel2">
                            <ui:panelGroup id="groupPanel1">
                                <h:outputText id="outputText2" styleClass="vdcFieldTitle" value="Username"/>
                            </ui:panelGroup>
                            <h:outputText id="outputText3" value="#{AccountPage.user.userName}"/>
                            <ui:panelGroup id="groupPanel3">
                                <h:outputText id="outputText4" styleClass="vdcFieldTitle" value="Full Name"/>
                            </ui:panelGroup>
                            <ui:panelGroup>
                                <h:outputText  value="#{AccountPage.user.firstName}"/>
                                <h:outputText   value="#{AccountPage.user.lastName}"/>
                            </ui:panelGroup>
                            <ui:panelGroup id="groupPanel4">
                                <h:outputText id="outputText5" styleClass="vdcFieldTitle" value="E-Mail"/>
                            </ui:panelGroup>
                            <h:outputText id="outputText11" value="#{AccountPage.user.email}"/>
                            <ui:panelGroup id="groupPanel5">
                                <h:outputText id="outputText6" styleClass="vdcFieldTitle" value="Institution"/>
                            </ui:panelGroup>
                            <h:outputText id="outputText12" value="#{AccountPage.user.institution}"/>
                            <ui:panelGroup id="groupPanel6">
                                <h:outputText id="outputText7" styleClass="vdcFieldTitle" value="Position"/>
                            </ui:panelGroup>
                            <h:outputText id="outputText15" value="#{AccountPage.user.position}"/>
                            <ui:panelGroup id="groupPanel7">
                                <h:outputText id="outputText8" styleClass="vdcFieldTitle" value="Phone Number"/>
                            </ui:panelGroup>
                            <h:outputText id="outputText13" value="#{AccountPage.user.phoneNumber}"/>
                        </h:panelGrid>
                        
                    </div>
                </div>
            </div>
                        
        </ui:form> 
    </f:subview>
</jsp:root>
