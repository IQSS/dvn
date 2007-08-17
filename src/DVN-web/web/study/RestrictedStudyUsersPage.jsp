<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles">
   <f:subview id="RestrictedStudyUsersPageView">
        <ui:form binding="#{RestrictedStudyUsersPage.form1}" id="form1">
            <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>

            <div class="dvn_section">
                <div class="dvn_sectionTitle">
                    <h3>
                        <h:outputText value="List of Users with Access to Restricted Study/Files" />
                    </h3>
                </div>            
                <div class="dvn_sectionBox"> 
                    <div class="dvn_margin12">
                        
                        <ui:panelGroup binding="#{RestrictedStudyUsersPage.groupPanel1}" block="true" id="groupPanel1" style="padding-bottom: 10px">
                            <h:outputText binding="#{RestrictedStudyUsersPage.outputText3}" id="outputText3" style="font-weight: bold" value="Users with access to  ' Restricted File/Study':"/>
                            <h:outputText binding="#{RestrictedStudyUsersPage.outputText2}" id="outputText2" value=" Edit List or see users/groups details (by clciking name)."/>
                        </ui:panelGroup>
                        <ui:panelGroup binding="#{RestrictedStudyUsersPage.groupPanel2}" block="true" id="groupPanel2"
                                       style="padding-bottom: 5px" styleClass="vdcTextRight">
                            <h:commandButton binding="#{RestrictedStudyUsersPage.button1}" id="button1" value="Update"/>
                        </ui:panelGroup>
                        <h:dataTable binding="#{RestrictedStudyUsersPage.dataTable3}" cellpadding="0" cellspacing="0"
                                     headerClass="list-header-left" id="dataTable3" rowClasses="list-row-even vdcColPadded, list-row-odd vdcColPadded"
                                     value="#{StudyPermissionsPage.dataTable2Model}" var="currentRow" width="100%">
                            <h:column binding="#{RestrictedStudyUsersPage.column4}" id="column4">
                                <f:facet name="header">
                                    <h:outputText binding="#{RestrictedStudyUsersPage.outputText9}" id="outputText9" value="Remove from List"/>
                                </f:facet>
                                <h:selectBooleanCheckbox binding="#{RestrictedStudyUsersPage.checkbox3}" id="checkbox3"/>
                            </h:column>
                            <h:column binding="#{RestrictedStudyUsersPage.column8}" id="column8">
                                <f:facet name="header">
                                    <h:outputText binding="#{RestrictedStudyUsersPage.outputText15}" id="outputText15" value="Username/ Group Name"/>
                                </f:facet>
                                <h:outputLink binding="#{RestrictedStudyUsersPage.hyperlink3}" id="hyperlink3" value="http://www.sun.com/jscreator">
                                    <h:outputText binding="#{RestrictedStudyUsersPage.hyperlink2Text1}" id="hyperlink2Text1" value="username"/>
                                </h:outputLink>
                            </h:column>
                        </h:dataTable>
                        
                    </div>
                </div>
            </div>
        </ui:form>           
    </f:subview>
</jsp:root>
