<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:gui="http://java.sun.com/jsf/facelets"
      xmlns:h="http://java.sun.com/jsf/html"
      xmlns:f="http://java.sun.com/jsf/core" 
      xmlns:jsp="http://java.sun.com/JSP/Page"
      xmlns:ui="http://www.sun.com/web/ui"
      >
<head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>

</head>

<body>
<gui:composition template="/template.xhtml">

<gui:param name="pageTitle" value="DVN - View User Group" />

  <gui:define name="body">

            <ui:form id="form1">
                  <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
                 <input type="hidden" name="pageName" value="EditUserGroupPage"/>

                 <div class="dvn_section">
                     <div class="dvn_sectionTitle">
                         
                             <h:outputText value="User Group Details"/>
                          
                     </div>            
                     <div class="dvn_sectionBox"> 
                         <div class="dvn_margin12">
                             
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
                             
                         </div>
                     </div>
                 </div>
            </ui:form>               
            </gui:define>
        </gui:composition>
    </body>
</html>
