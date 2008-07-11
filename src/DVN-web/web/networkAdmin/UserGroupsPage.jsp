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

<gui:param name="pageTitle" value="DVN - Dataverse Network User Groups" />

  <gui:define name="body">

    <ui:form id="form1">
        <div class="dvn_section">
            <div class="dvn_sectionTitle">
                 
                    <h:outputText value="User Groups in #{VDCRequest.vdcNetwork.name} Dataverse Network"/>
                
            </div>            
            <div class="dvn_sectionBox"> 
                <div class="dvn_margin12">
                    
                    <ui:panelLayout styleClass="#{UserGroupsPage.statusMessage.styleClass}" rendered="#{!empty UserGroupsPage.statusMessage.messageText}" >
                        <h:outputText id="statusMessage"  value="#{UserGroupsPage.statusMessage.messageText}" />
                    </ui:panelLayout>
                    
                    <h:outputLink value="/dvn/faces/networkAdmin/EditUserGroupPage.jsp?userGroupType=usergroup">
                        <h:outputText value="Create a New Group"/>
                    </h:outputLink>
                    
                    <h:dataTable cellpadding="0" cellspacing="0" binding="#{UserGroupsPage.dataTable}"
                                 columnClasses="vdcColPadded, vdcColPadded" headerClass="list-header-left" id="dataTable1"
                                 rowClasses="list-row-even,list-row-odd" value="#{UserGroupsPage.goups}" var="currentRow" width="100%">
                        <h:column id="column1">
                            <f:facet name="header">
                                <h:outputText id="column1Header" value="Group"/>
                            </f:facet>
                            <h:outputLink value="EditUserGroupPage.jsp?userGroupId=#{currentRow.group.id}">
                                <h:outputText value="#{currentRow.group.name}"/>
                            </h:outputLink>
                        </h:column>
                        <h:column id="column2">
                            <f:facet name="header">
                                <h:outputText id="column2Header" value="Friendly Group Name"/>
                            </f:facet>
                            <h:outputLink value="EditUserGroupPage.jsp?userGroupId=#{currentRow.group.id}">
                                <h:outputText value="#{currentRow.group.friendlyName}"/>
                            </h:outputLink>
                        </h:column>
                        <h:column id="column3">
                            <f:facet name="header">
                                <h:outputText id="column3Header" value="Users"/>
                            </f:facet>
                            <h:outputText value="#{currentRow.details}"/>
                        </h:column>
                        <!-- Commented for now since it's not implemented - add later
                        <h:column id="column3">
                            <f:facet name="header">
                                <h:outputText id="column3Header" value="PIN Service"/>
                            </f:facet>
                            <h:outputText value="#{currentRow.group.pinService}"/>
                        </h:column>
                        -->
                        <h:column id="column4">
                            <f:facet name="header">
                                <h:outputText id="outputText10" value="Delete"/>
                            </f:facet>
                            <h:commandLink actionListener="#{UserGroupsPage.deleteGroup}">
                                <h:outputText value="Delete Group"/>
                            </h:commandLink>
                        </h:column>
                        
                    </h:dataTable>
                    
                </div>
            </div>
        </div>
    </ui:form>
            </gui:define>
        </gui:composition>
    </body>
</html>
