<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles">
            <f:subview id="PrivilegedUsersPageView">
                <ui:form id="privilegedUsersForm">
                       <input type="hidden" name="pageName" value="PrivilegedUsersPage"/>
                       <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/> 
                            <ui:panelLayout id="layoutPanel1" panelLayout="flow" styleClass="vdcSectionMiddle">
                                <ui:panelLayout id="layoutPanel2" panelLayout="flow" styleClass="vdcSectionHeader">
                                    <h:outputText value="Site Restrictions and Users' Privileges"/>
                                </ui:panelLayout>
                                 <ui:panelLayout panelLayout="flow" styleClass="vdcSectionMiddleMessage" style="width: 200px; margin-top: 10px; margin-bottom: -10px" rendered="#{PrivilegedUsersPage.success}">
                                   <ui:panelGroup block="true" styleClass="successMessage" >
                                      <h:outputText value="Update Successful!" />
                                   </ui:panelGroup>
                                 </ui:panelLayout>
                                <ui:panelLayout id="layoutPanel3" panelLayout="flow" style="padding: 30px 40px 30px 40px; "> 
                                    <h:outputText id="outputText13" styleClass="vdcSubHeaderColor" value="- Site Restriction Settings:"/>
                                    <ui:panelGroup block="true" id="groupPanel6" style="padding-top: 10px; padding-bottom: 10px">
                                        <h:outputText id="outputText12" value="This dataverse is set as: "/>
                                        <h:selectOneMenu value="#{PrivilegedUsersPage.siteRestriction}">
                                            <f:selectItem itemLabel="Restricted" itemValue="Restricted"/>
                                            <f:selectItem itemLabel="Public" itemValue="Public"/>
                                        </h:selectOneMenu>
                                        <ui:panelGroup block="true" >
                                         <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                                         <h:outputText styleClass="vdcHelpText" value="If site is set as restricted, only users with appropriate privileges can access the site by logging in."/>
                                        </ui:panelGroup>  
                                    </ui:panelGroup>
                                    
                                    <h:outputText id="outputText14" styleClass="vdcSubHeaderColor" value="- Contributions Settings:"/>
                                    <ui:panelGroup block="true" id="groupPanel7" style="padding-top: 10px; padding-bottom: 10px">
                                        <h:outputText id="outputText15" value="Allow users to request to be contributors when they create an account: "/>
                                        <h:selectBooleanCheckbox id="checkbox5" value="#{PrivilegedUsersPage.vdc.allowContributorRequests}"/>
                                        <ui:panelGroup block="true" >
                                         <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                                         <h:outputText styleClass="vdcHelpText" value="By checking this option, your dataverse homepage will display an invitation to become a contributor. Once the request is submitted, you can choose to accept that user as a contributor. (Note that only end-users without the permission to contribute will see that invitation.)"/>
                                        </ui:panelGroup>
                                        <ui:panelGroup block="true" style="padding-left: 50px;" rendered="#{!empty PrivilegedUsersPage.contributorRequests}">   
                                            <h:dataTable cellpadding="0" cellspacing="0" headerClass="list-header-left"
                                                columnClasses="vdcColPadded, vdcColPadded" id="dataTable2"
                                                style="margin-top: 10px;" value="#{PrivilegedUsersPage.contributorRequests}" var="currentRow">
                                                <h:column id="column5">
                                                    <f:facet name="header">
                                                        <h:outputText id="contributorRequest_tcol1" value="Users Requesting to be Contributors"/>
                                                    </f:facet>
                                                    <h:outputLink id="hyperlink2" value="../login/AccountPage.jsp?userId=#{currentRow.roleRequest.vdcUser.id}">
                                                        <h:outputText id="hyperlink2Text" value="#{currentRow.roleRequest.vdcUser.userName}"/>
                                                    </h:outputLink>
                                                </h:column>
                                                <h:column id="column6">
                                                   <h:selectOneRadio value="#{currentRow.accept}">
                                                     <f:selectItem itemLabel="Accept" itemValue="true"/>
                                                     <f:selectItem itemLabel="Reject" itemValue="false"/>
                                                   </h:selectOneRadio>
                                                </h:column>
                                            </h:dataTable>               
                                        </ui:panelGroup>
                                    </ui:panelGroup>
                                    
                                    <ui:panelGroup block="true" style="padding-top: 10px; padding-bottom: 10px">
                                         <h:outputText styleClass="vdcSubHeaderColor" value="- Edit Privileged Users:"/>  
                                    </ui:panelGroup>
                                    <ui:panelGroup block="true" id="groupPanel3" style="padding-bottom: 10px">
                                        <h:outputText id="outputText2" value="Enter the Username of the user you want to add: "/>
                                        <h:inputText  binding="#{PrivilegedUsersPage.userInputText}" id="userName" value="#{PrivilegedUsersPage.userName}" onkeypress="if (window.event) return processEvent('', 'content:PrivilegedUsersPageView:privilegedUsersForm:addUserButton'); else return processEvent(event, 'content:PrivilegedUsersPageView:privilegedUsersForm:addUserButton');"/>
                                        <h:commandButton  id="addUserButton" value="Add" actionListener="#{PrivilegedUsersPage.addUser}" />
                                        <h:message styleClass="errorMessage" for="userName"/> 
                                     </ui:panelGroup>
                                     <ui:panelGroup block="true" style="padding-bottom: 10px;">
                                         <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                                         <h:outputText styleClass="vdcHelpText" value="You can add a privileged user to be a contributor, curator or admin for this dataverse, or to gain access to this dataverse, if it's restricted. "/>
                                     </ui:panelGroup>
                                    <h:dataTable binding="#{PrivilegedUsersPage.userTable}" cellpadding="0" cellspacing="0"
                                        columnClasses="vdcColPadded, vdcColPadded, vdcColPadded, vdcColPadded" headerClass="list-header-left" id="dataTable1"
                                        rowClasses="list-row-even,list-row-odd" value="#{PrivilegedUsersPage.privilegedUsers}" var="currentRow" width="100%">
                                        <h:column id="column1">
                                            <f:facet name="header">
                                                <h:outputText id="outputText3" value="Username"/>
                                            </f:facet>
                                            <h:outputLink id="hyperlink2" value="../login/AccountPage.jsp?userId=#{currentRow[0].vdcUser.id}">
                                                <h:outputText id="hyperlink2Text" value="#{currentRow[0].vdcUser.userName}"/>
                                            </h:outputLink>
                                        </h:column>
                                        <h:column id="column9" >
                                            <f:facet name="header">
                                                <ui:panelGroup  block="true"  >
                                                    <h:outputText id="outputText16" value="Privileged Role"/>
                                                    <h:outputText styleClass="vdcHelpText" style="padding-left: 30px;" value="('Access To Site' is only applicable if site is restricted.)"/>
                                                </ui:panelGroup>
                                            </f:facet>
                                                     <h:selectOneRadio  disabled="#{currentRow[0].vdcUser.id==PrivilegedUsersPage.vdc.creator.id}" id="roleSelectMenu" value="#{currentRow[1]}">
                                                            <h:message for="roleSelectMenu"  styleClass="errorMessage"/>
                                                     <f:selectItems id="roleSelectItems" value="#{PrivilegedUsersPage.roleSelectItems}"/>
                                                   
                                                   </h:selectOneRadio>
                                     
                                        </h:column>
                                        <h:column >
                                            <h:commandButton  id="removeRoleButton" value="Remove Role" rendered="#{currentRow[0].vdcUser.id!=PrivilegedUsersPage.vdc.creator.id}" actionListener="#{PrivilegedUsersPage.removeRole}" />    
                                             <h:outputText value="(Dataverse Creator - cannot modify Role)" rendered="#{currentRow[0].vdcUser.id==PrivilegedUsersPage.vdc.creator.id}"  />    
                                          
                                        </h:column>
                                        
                                    </h:dataTable>
                                    
                                    <ui:panelGroup block="true" style="padding-top: 20px; padding-bottom: 10px">
                                        <h:outputText styleClass="vdcSubHeaderColor" value="- Edit Privileged Groups:"/>
                                    </ui:panelGroup>
                                     
                                    <ui:panelGroup block="true"  style="padding-bottom: 10px">
                                        <h:outputText  value="Enter the name of the Group you want to add: "/>
                                        <h:inputText id="groupName" binding="#{PrivilegedUsersPage.groupInputText}" value="#{PrivilegedUsersPage.groupName}" onkeypress="if (window.event) return processEvent('', 'content:PrivilegedUsersPageView:privilegedUsersForm:addGroupButton'); else return processEvent(event, 'content:PrivilegedUsersPageView:privilegedUsersForm:addGroupButton');"/>
                                        <h:commandButton id="addGroupButton" value="Add" actionListener="#{PrivilegedUsersPage.addGroup}" />
                                       <h:message styleClass="errorMessage" for="groupName"/> 
                                    </ui:panelGroup>
                                    <ui:panelGroup block="true" style="padding-bottom: 10px;">
                                         <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                                         <h:outputText styleClass="vdcHelpText" value="You can add a privileged group to gain access to this dataverse, if it is restricted. "/>
                                     </ui:panelGroup>
                                    <h:dataTable binding="#{PrivilegedUsersPage.groupTable}" cellpadding="0" cellspacing="0"
                                        columnClasses="vdcColPadded, vdcColPadded, vdcColPadded, vdcColPadded" headerClass="list-header-left" 
                                        rowClasses="list-row-even,list-row-odd" value="#{PrivilegedUsersPage.vdc.allowedGroups}" var="currentRow" width="100%">
                                        <h:column >
                                            <f:facet name="header">
                                                <h:outputText id="groups_tcol1" value="Group Name"/>
                                            </f:facet>
                                            <h:outputLink  value="../networkAdmin/ViewUserGroupPage.jsp?userGroupId=#{currentRow.id}">
                                                <h:outputText  value="#{currentRow.name}"/>
                                            </h:outputLink>
                                        </h:column>
                                        <h:column>
                                            <f:facet name="header">
                                                <h:outputText id="roleHeader" value="Privileged Role"/>
                                            </f:facet>
                                                    
                                                <h:outputText value="Access To Site"/>
                                     
                                        </h:column>                                  
                                        <h:column>
                                            <h:commandButton  id="removeGroupButton" value="Remove Group" actionListener="#{PrivilegedUsersPage.removeGroup}" />                                            
                                        </h:column>
                                    </h:dataTable>
                                   <ui:panelGroup block="true"  style="padding-bottom: 10px; padding-top: 10px; padding-right: 5px;" styleClass="vdcTextRight">
                                        <h:outputText  styleClass="vdcHelpText" value="(Nothing in this page will be saved until you click  Save Changes)"/>
                                        <h:commandButton  id = "saveChangesButton" value="Save Changes" action="#{PrivilegedUsersPage.saveChanges}"/>   
                                       <h:commandButton immediate="true" id = "cancelButton" value="Cancel" action="#{PrivilegedUsersPage.cancel}"/>   
                                    </ui:panelGroup>
   
                                  </ui:panelLayout>
                            </ui:panelLayout>
                    </ui:form>
             
    </f:subview>
</jsp:root>
