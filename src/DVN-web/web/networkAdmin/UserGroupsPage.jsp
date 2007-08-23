<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles">
<f:subview id="UserGroupsPageView">
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
                    
                    <h:outputLink value="EditUserGroupPage.jsp?userGroupType=new">
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
    </f:subview>
</jsp:root>
