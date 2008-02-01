<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles">
       <f:subview id="StudyPermissionsPageView">
          
            <script type="text/javascript">
              // <![CDATA[                  
                    function updateAllCheckboxes( selectAllCheckbox ) {
                        checkboxes=document.getElementsByTagName("input");
                         for(i=0; i < checkboxes.length; i++) {
                            if (checkboxes[i].id.indexOf("filePermissionCheckbox") != -1) {
                                checkboxes[i].checked = selectAllCheckbox.checked;               
                            }
                         }
                    }  
               // ]]> 
            </script> 

    <ui:form  id="form1">
        <input type="hidden" name="pageName" value="StudyPermissionsPage"/>

        <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
        <h:inputHidden id="studyId" value="#{StudyPermissionsPage.studyId}"/>


            <div class="dvn_section">
                <div class="dvn_sectionTitle">
                     
                        <h:outputText value="Set Permissions for this Study" />
                     
                </div>            
                <div class="dvn_sectionBox"> 
                    <div class="dvn_margin12">
                        
                        <h:outputText   styleClass="vdcSubHeaderColor" value="- Entire Study Permissions:"/>
                        <ui:panelGroup  block="true"  style="padding-top: 10px">
                            <h:outputText   value="This  entire study (including Cataloging Information) is set as: "/>
                            <h:selectOneMenu value="#{StudyPermissionsPage.editStudyPermissions.studyRestriction}">
                                <f:selectItem itemLabel="Restricted" itemValue="Restricted"/>
                                <f:selectItem itemLabel="Public" itemValue="Public"/>
                            </h:selectOneMenu>
                        </ui:panelGroup>
                        <ui:panelGroup block="true" style="padding-top: 5px">
                            <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                            <h:outputText styleClass="vdcHelpText" value="Users without access to this Study will not see the study in search results or browsing collections."/>
                        </ui:panelGroup>
                        
                        <ui:panelGroup  block="true"  style="padding-top: 15px; padding-bottom: 20px; padding-left: 50px;">
                            <ui:panelGroup block="true" style="padding-bottom: 5px">
                                <h:outputText styleClass="vdcSubHeader" value="If study is set as Restricted:"/>
                            </ui:panelGroup>  
                            <h:inputText id="newStudyUser" binding="#{StudyPermissionsPage.studyUserInputText}" value="#{StudyPermissionsPage.newStudyUser}"/>                         
                            <h:commandButton   value="Add User/Group" actionListener="#{StudyPermissionsPage.addStudyPermission}"/>
                            <h:message styleClass="errorMessage" for="newStudyUser"/> 
                            <ui:panelGroup  block="true" rendered="#{!empty StudyPermissionsPage.editStudyPermissions.studyPermissions}">
                                <h:dataTable  
                                    value="#{StudyPermissionsPage.editStudyPermissions.studyPermissions}" var="currentRow" style="margin-top: 10px;" headerClass="list-header-left">
                                    <h:column>
                                        <h:selectBooleanCheckbox value="#{currentRow.checked}" />
                                    </h:column>
                                    <h:column  >
                                        <f:facet name="header">
                                            <h:outputText id="access_header"  value="Users/Groups with Access"/>
                                        </f:facet>
                                        
                                        <h:outputLink rendered="#{currentRow.user != null}" value="../login/AccountPage.jsp?userId=#{currentRow.user.id}&amp;vdcId=#{VDCRequest.currentVDCId}" >
                                            <h:outputText   value="#{currentRow.user.userName}"/>
                                        </h:outputLink>
                                        <h:outputLink rendered="#{currentRow.group != null}" value="../login/AccountPage.jsp?userId=#{currentRow.group.id}&amp;vdcId=#{VDCRequest.currentVDCId}" >
                                            <h:outputText   value="#{currentRow.group.name}"/>
                                        </h:outputLink>
                                    </h:column>
                                </h:dataTable>
                                <h:commandButton value="Remove Checked Users/Groups" actionListener = "#{StudyPermissionsPage.removeStudyUserGroup}"/>
                            </ui:panelGroup>
                        </ui:panelGroup>
                        <h:outputText   styleClass="vdcSubHeaderColor" value="- All Files Permissions:"/>
                        <ui:panelGroup  block="true"  style="padding-top: 10px; padding-bottom: 10px">
                            <h:outputText   value="Allow users to request access to restricted files for this study by creating a Dataverse Network account:"/>
                            <h:selectBooleanCheckbox value="#{StudyPermissionsPage.editStudyPermissions.study.requestAccess}" />
                            <ui:panelGroup block="true" style="padding-top: 5px">
                                <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                                <h:outputText styleClass="vdcHelpText" value="By checking this option, when users try to access a restricted file, they will have the choice to log in or create an account to request access. After user's request, you can choose to grant them access to restricted files."/>
                            </ui:panelGroup>
                        </ui:panelGroup>
                        <ui:panelGroup  block="true" style="padding-bottom: 20px; padding-left: 50px;" rendered="#{!empty StudyPermissionsPage.editStudyPermissions.studyRequests}"> 
                            <h:dataTable  columnClasses="vdcColPadded, vdcColPadded"
                                          value="#{StudyPermissionsPage.editStudyPermissions.studyRequests}" var="currentRow" headerClass="list-header-left">
                                <h:column  >
                                    <f:facet name="header">
                                        <h:outputText id="accessRequest_header" value="Users Requesting to have Access to Restricted Files"/>
                                    </f:facet>
                                    <h:outputLink   value="../login/AccountPage.jsp?userId=#{currentRow.studyRequest.vdcUser.id}&amp;vdcId=#{VDCRequest.currentVDCId}" >
                                        <h:outputText   value="#{currentRow.studyRequest.vdcUser.userName}"/>
                                    </h:outputLink>
                                </h:column>
                                <h:column  >
                                    <h:selectOneRadio value="#{currentRow.accept}">
                                        <f:selectItem itemLabel="Accept" itemValue="true"/>
                                        <f:selectItem itemLabel="Reject" itemValue="false"/>
                                    </h:selectOneRadio>
                                </h:column>
                            </h:dataTable>
                            <h:outputText value="Update Individual File Permissions for these users:"/>
                            <h:commandButton value="Update" actionListener="#{StudyPermissionsPage.updateRequests}"/>
                        </ui:panelGroup>
                        <h:outputText   styleClass="vdcSubHeaderColor" value="- Individual File Permissions:"/>
                        <ui:panelGroup  block="true" 
                                        style="padding-bottom: 10px" styleClass="vdcTextLeft">
                            <h:outputText   value="Update checked files -  Add User/Group: "/>
                            <h:inputText value="#{StudyPermissionsPage.newFileUser}" id="newFileUser" binding="#{StudyPermissionsPage.fileUserInputText}" />
                            <h:outputText   value="Set file permission: "/>
                            <h:selectOneMenu value="#{StudyPermissionsPage.selectFilePermission}">
                                <f:selectItem itemLabel="Choose permission" itemValue=""/>                                            
                                <f:selectItem itemLabel="Restricted" itemValue="Restricted"/>
                                <f:selectItem itemLabel="Public" itemValue="Public"/>
                            </h:selectOneMenu>
                            
                            <h:commandButton   value="Update" actionListener="#{StudyPermissionsPage.addFilePermission}"/>
                            <h:message styleClass="errorMessage" for="newFileUser"/> 
                            
                        </ui:panelGroup>
                        <ui:panelGroup  block="true" 
                                        style="padding-bottom: 10px" styleClass="vdcTextLeft">
                            
                            <h:commandButton   value="Remove Checked Users/Groups" actionListener="#{StudyPermissionsPage.removeFilePermissions}"/>
                        </ui:panelGroup>
                        
                        
                        <ui:panelGroup  block="true" 
                                        style="padding-bottom: 10px" styleClass="vdcTextLeft">
                            
                            <h:dataTable  cellpadding="0" cellspacing="0"
                                          headerClass="list-header-left" columnClasses="vdcColPadded, vdcColPadded, vdcColPadded, vdcColPadded"  rowClasses="list-row-even, list-row-odd"
                                          value="#{StudyPermissionsPage.editStudyPermissions.fileDetails}" var="currentRow" width="98%">
                                <h:column id="fileuser_col1">
                                    <f:facet name="header">
                                        <h:selectBooleanCheckbox id="selectAllCheckbox"  onchange="updateAllCheckboxes(this);"/>
                                    </f:facet>
                                    <h:selectBooleanCheckbox id="filePermissionCheckbox" value="#{currentRow.checked}"/>
                                </h:column>
                                <h:column >
                                    <h:outputText   value="#{currentRow.studyFile.fileCategory.name}"/>
                                    <f:facet name="header">
                                        <h:outputText id="fileuser_tcol1" value="Category Name"/>
                                    </f:facet>
                                </h:column>
                                <h:column id="fileuser_col2">
                                    <h:outputText   value="#{currentRow.studyFile.fileName}"/>
                                    <f:facet name="header">
                                        <h:outputText id="fileuser_tcol2" value="File Name"/>
                                    </f:facet>
                                </h:column>
                                <h:column id="fileuser_col3">
                                    <h:outputText   value="#{currentRow.fileRestriction}"/>
                                    <f:facet name="header">
                                        <h:outputText id="fileuser_tcol3" value="Permission"/>
                                    </f:facet>
                                </h:column>
                                <h:column id="fileuser_col4">
                                    <f:facet name="header">
                                        <h:outputText id="fileuser_tcol4" value="User/Groups with Access if File is Restricted"/>
                                    </f:facet>
                                    <h:dataTable value = "#{currentRow.filePermissions}"  var="currPermission">
                                        <h:column >     <h:selectBooleanCheckbox  value="#{currPermission.checked}" /></h:column>
                                        <h:column >                        
                                            <h:outputLink   value="http://www.sun.com/jscreator">
                                                <h:outputText value="#{currPermission.user.userName}" rendered="#{currPermission.user!=null}"/>
                                                <h:outputText value="#{currPermission.group.name}" rendered="#{currPermission.group!=null}"/>
                                            </h:outputLink>
                                        </h:column>
                                    </h:dataTable>
                                </h:column>
                                <!-- Commented h:column - see version prior to 2/1/08 to find changes, MC-->
                            </h:dataTable>
                        </ui:panelGroup>
                        <ui:panelGroup block="true" id="groupPanel6"
                                       style="padding-top: 10px; padding-right: 5px;" styleClass="vdcTextRight">
                            <h:commandButton   value="Save Changes" action="#{StudyPermissionsPage.save}" />
                            <h:commandButton   value="Cancel" action="#{StudyPermissionsPage.cancel}" />
                            
                        </ui:panelGroup>
                        
                    </div>
                </div>
            </div>
        </ui:form>               
    </f:subview>
</jsp:root>
