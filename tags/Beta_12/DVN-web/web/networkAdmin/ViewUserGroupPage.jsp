<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles">
       <f:subview id="EditUserGroupPageView">
                    <ui:form id="form1">
                          <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
                         <input type="hidden" name="pageName" value="EditUserGroupPage"/>
 
                            <ui:panelLayout panelLayout="flow" styleClass="vdcSectionMiddleFixed" style="width: 600px">
                                <ui:panelLayout panelLayout="flow" styleClass="vdcSectionHeader">
                                    <h:outputText value="User Group Details"/>
                                </ui:panelLayout>
                                <ui:panelLayout panelLayout="flow" style="padding-left: 50px; padding-top: 40px; padding-bottom: 30px">
                                   
                                 <h:panelGrid cellpadding="0" cellspacing="0"
                                        columnClasses="vdcColPadded, vdcColPadded" columns="2" id="gridPanel2">
                                        <ui:panelGroup id="groupPanel1">
                                            <h:outputText id="outputText2" styleClass="vdcFieldTitle" value="Group Name: "/>
                                        </ui:panelGroup>
                                        <ui:panelGroup>
                                            <h:outputText id="outputText3" value="#{ViewUserGroupPage.group.name}"/>
                                        </ui:panelGroup>
                                        <ui:panelGroup >
                                            <h:outputText  styleClass="vdcFieldTitle" value="Users: " rendered="#{!empty ViewUserGroupPage.group.users}"/>
                                        </ui:panelGroup>
                                        <h:outputText  value="#{ViewUserGroupPage.userList}" rendered="#{!empty ViewUserGroupPage.group.users}"/>
                                            <!-- Commented for now since it's not implemented - Add later
                                        <h:outputText value="IP Addresses"/>
                                        <ui:panelGroup id="groupPanel3">
                                            <h:dataTable binding="#{EditUserGroupPage.dataTableIpAddresses}"  var="currentRow" cellpadding="0" cellspacing="0" value="#{EditUserGroupPage.group.loginDomains}">
                                                <h:column>
                                                    <h:inputText value="#{currentRow.ipAddress}" />
                                                </h:column>
                                                <h:column>
                                                     <h:commandButton  image="/resources/icon_add.gif" actionListener="#{EditUserGroupPage.addRow}"/> 
                                                    <h:commandButton  image="/resources/icon_remove.gif" actionListener="#{EditUserGroupPage.removeRow}" /> 
              
                                                </h:column>
                                            </h:dataTable> 
                                        </ui:panelGroup>
                                        -->
                                   
                                        <!-- Commented for now - Add Later
                                         <h:outputText value="PIN Service Name"/>
                                        <h:inputText size="40" value="#{EditUserGroupPage.group.pinService}"/>
                                        -->
                                    </h:panelGrid>
                                    
                                  
                                </ui:panelLayout>
                            </ui:panelLayout>
                    </ui:form>               
          </f:subview>
</jsp:root>
