<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles">
       <f:subview id="NetworkPrivilegedUsersPage">
                    <ui:form id="NetworkPrivilegedUsersPageView">
                           <input type="hidden" name="pageName" value="NetworkPrivilegedUsersPage"/>
                           <ui:panelLayout id="layoutPanel1" panelLayout="flow" styleClass="vdcSectionMiddle">
                                <ui:panelLayout id="layoutPanel2" panelLayout="flow" styleClass="vdcSectionHeader">
                                     <h:outputText value="Dataverse Creation Permissions and Network Users' Privileges"/>
                                </ui:panelLayout>
                                <ui:panelLayout panelLayout="flow" styleClass="vdcSectionMiddleMessage" style="width: 200px; margin-top: 10px; margin-bottom: -10px" rendered="#{NetworkPrivilegedUsersPage.success}">
                                  <ui:panelGroup block="true"  styleClass="successMessage" >
                                    <h:outputText value="Update Successful!" />
                                  </ui:panelGroup>
                                </ui:panelLayout>
                                <ui:panelLayout id="layoutPanel3" panelLayout="flow" style="padding: 30px 40px 30px 40px; ">
                                    <h:outputText id="outputText4"
                                        styleClass="vdcSubHeaderColor" value="- Creating a New Dataverse:"/>
                                    <ui:panelGroup block="true" id="groupPanel2" style="padding-top: 10px; padding-bottom: 10px">
                                        <h:outputText id="outputText5" value="Allow users to request to create a new Dataverse when they create an account: "/>
                                        <h:selectBooleanCheckbox id="checkbox1" value="#{NetworkPrivilegedUsersPage.privileges.network.allowCreateRequest}"/>
                                        <ui:panelGroup block="true" >
                                         <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                                         <h:outputText styleClass="vdcHelpText" value="By checking this option, your Dataverse Network homepage will display an invitation to create a dataverse (only users without Dataverse creator privileges will see the invitation). Once a request is submitted, you can choose to accept the user as a Dataverse cretaor."/>
                                        </ui:panelGroup>  
                                       
                                        <ui:panelGroup block="true" style="padding-left:50px;" rendered="#{!empty NetworkPrivilegedUsersPage.privileges.creatorRequests}">
                                         <h:dataTable cellpadding="0" cellspacing="0" headerClass="list-header-left"
                                                columnClasses="vdcColPadded, vdcColPadded" style="margin-top: 10px; "
                                                value="#{NetworkPrivilegedUsersPage.privileges.creatorRequests}" var="currentRow">
                                                <h:column id="column1">
                                                     <f:facet name="header">
                                                            <h:outputText id="createUsers_tcol1" value="Users Requesting to Create a Dataverse"/>
                                                    </f:facet>
                                                    <h:outputLink id="hyperlink1" value="../login/AccountPage.jsp?userId=#{currentRow.networkRoleRequest.vdcUser.id}&amp;vdcId=#{VDCRequest.currentVDCId}">
                                                        <h:outputText id="hyperlink2Text1" value="#{currentRow.networkRoleRequest.vdcUser.userName}"/>
                                                    </h:outputLink>
                                                </h:column>
                                                <h:column id="column2">
                                                   <h:selectOneRadio value="#{currentRow.accept}">
                                                     <f:selectItem itemLabel="Accept" itemValue="true"/>
                                                     <f:selectItem itemLabel="Reject" itemValue="false"/>
                                                   </h:selectOneRadio>
                                                </h:column>
                                           </h:dataTable>
                                         </ui:panelGroup> 
                                     </ui:panelGroup>
                                    
                                     <ui:panelGroup block="true" style="padding-top: 10px; padding-bottom: 10px">
                                        <h:outputText id="outputText2" styleClass="vdcSubHeaderColor" value="- Network Privileged Users:"/>
                                     </ui:panelGroup>
                                    <ui:panelGroup block="true"  style="padding-bottom: 5px">
                                        <h:outputText id="outputText7" value="Enter the Username of the user you want to add: "/>
                                        <h:inputText onkeypress="if (window.event) return processEvent('', 'content:NetworkPrivilegedUsersPage:NetworkPrivilegedUsersPageView:addUserbutton'); else return processEvent(event, 'content:NetworkPrivilegedUsersPage:NetworkPrivilegedUsersPageView:addUserbutton');" id="textField1" value="#{NetworkPrivilegedUsersPage.userName}"/>
                                        <h:commandLink  id="addUserbutton" value="Add" actionListener="#{NetworkPrivilegedUsersPage.addUser}"/>
                                        <h:outputText styleClass="errorMessage" value="User Not Found." rendered="#{NetworkPrivilegedUsersPage.userNotFound}"/>
                                  </ui:panelGroup>
                                   <ui:panelGroup block="true" style="padding-bottom: 10px;">
                                         <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                                         <h:outputText styleClass="vdcHelpText" value="You can add a network privileged user to be another network admin or a Dataverse creator. After privileged users are added to this list, if you want to remove a user, click 'Clear Role' for that user."/>
                                     </ui:panelGroup>
                                  <h:dataTable binding="#{NetworkPrivilegedUsersPage.userTable}" cellpadding="0" cellspacing="0"
                                        columnClasses="vdcColPadded, vdcColPadded, vdcColPadded, vdcColPadded" headerClass="list-header-left" 
                                        rowClasses="list-row-even,list-row-odd" value="#{NetworkPrivilegedUsersPage.privileges.privilegedUsers}" var="currentRow" width="100%">
                                        <h:column id="column1" rendered="#{currentRow.user.id!=NetworkPrivilegedUsersPage.privileges.network.defaultNetworkAdmin.id}">
                                            <f:facet name="header">
                                                <h:outputText id="outputText3" value="Username"/>
                                            </f:facet>
                                            <h:outputLink id="hyperlink2" value="../login/AccountPage.jsp?userId=#{currentRow.user.id}">
                                                <h:outputText id="hyperlink2Text" value="#{currentRow.user.userName}"/>
                                            </h:outputLink>
                                        </h:column>
                                        <h:column id="column9" rendered="#{currentRow.user.id!=NetworkPrivilegedUsersPage.privileges.network.defaultNetworkAdmin.id}">
                                            <f:facet name="header">
                                                <h:outputText id="outputText16" value="Privileged Role"/>
                                            </f:facet>
                                                     <h:selectOneRadio id="roleSelectMenu" value="#{currentRow.networkRoleId}">
                                                     <f:selectItems id="roleSelectItems" value="#{NetworkPrivilegedUsersPage.roleSelectItems}"/>
                                                   
                                                   </h:selectOneRadio>
                                     
                                        </h:column>
                                        <h:column rendered="#{currentRow.user.id!=NetworkPrivilegedUsersPage.privileges.network.defaultNetworkAdmin.id}">
                                            <h:commandButton  value="Clear Role" actionListener="#{NetworkPrivilegedUsersPage.clearRole}" />                                            
                                        </h:column>
                                    </h:dataTable>
                                    
                                      <ui:panelGroup block="true" id="groupPanel6"
                                        style="padding-bottom: 10px; padding-top: 10px; padding-right: 5px;" styleClass="vdcTextRight">
                                        <h:outputText  styleClass="vdcHelpText" value="(Nothing in this page will be saved until you click  Save Changes)"/>  
                                        <h:commandButton id="button5" value="Save Changes" action="#{NetworkPrivilegedUsersPage.save}"/>
                                        <h:commandButton  id = "cancelButton" value="Cancel" action="#{NetworkPrivilegedUsersPage.cancel}"/>   
                                        </ui:panelGroup>
                              </ui:panelLayout>
                            </ui:panelLayout>
                    </ui:form>
      </f:subview>
</jsp:root>
