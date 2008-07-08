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

<gui:param name="pageTitle" value="DVN - Edit User Group" />

  <gui:define name="body">
      <f:loadBundle basename="Bundle" var="bundle"/>
     <f:loadBundle basename="BundleAnalysis" var="bundleAnalysis"/>

    
        <h:form id="form1">
                  <f:verbatim rendered="#{param.userGroupId == null}">
                                 <script type="text/javascript">
                                         //<![CDATA[
                                            function showAll(){
                                                if (location.href.indexOf("userGroupType") != -1) {
                                                    if (document.getElementById('content:EditUserGroupPageView:form1:usergroups') != null)
                                                        document.getElementById('content:EditUserGroupPageView:form1:usergroups').style.display='none';
                                                    if (document.getElementById('content:EditUserGroupPageView:form1:ipgroups') != null)
                                                        document.getElementById('content:EditUserGroupPageView:form1:ipgroups').style.display='none';
                                                }

                                            }

                                            function setUserGroupType(obj) {
                                                 if (obj.checked) { 
                                                    if (obj.value == "usergroup")
                                                        document.getElementById('content:EditUserGroupPageView:form1:userGroupType').value = "usergroup";
                                                    else
                                                        document.getElementById('content:EditUserGroupPageView:form1:userGroupType').value = "ipgroup";
                                                 }
                                            }
                                        //]]>
                                 </script>
                             </f:verbatim>
                 <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
                 <input type="hidden" name="pageName" value="EditUserGroupPage"/>
                 
                 <div class="dvn_section">
                     <div class="dvn_sectionTitle">                       
                             <h:outputText value="User Group"/>  
                     </div>            
                     <div class="dvn_sectionBox"> 
                         <div class="dvn_margin12">
                             <ui:panelGroup block="true" style="padding-bottom: 15px;">
                                 <h:graphicImage value="/resources/icon_required.gif"/> <h:outputText style="vdcHelpText" value="Indicates a required field."/>
                             </ui:panelGroup>
                             <!-- group name -->
                             <h:panelGrid  cellpadding="0" cellspacing="0"
                                    columnClasses="vdcColPadded, vdcColPadded" columns="2" id="gridPanel2">
                                  <ui:panelGroup id="groupPanel1">
                                      <h:outputText value="Group Name"/>
                                      <h:graphicImage id="image1" value="/resources/icon_required.gif"/>
                                  </ui:panelGroup>
                                  <ui:panelGroup>
                                      <h:inputText id="inputGroupName" size="40" value="#{EditUserGroupPage.group.name}" required="true" requiredMessage="This field is required." onkeypress="if (window.event) return processEvent('', 'content:EditUserGroupPageView:form1:btnSave'); else return processEvent(event, 'content:EditUserGroupPageView:form1:btnSave');">
                                         <f:validator validatorId="CharacterValidator"/>
                                      </h:inputText>
                                      <h:message styleClass="errorMessage" for="inputGroupName"/>
                                  </ui:panelGroup>
                                  <ui:panelGroup id="groupPanelFriendlyName">
                                      <h:outputText value="Friendly Group Name"/>
                                      <h:graphicImage value="/resources/icon_required.gif"/>
                                  </ui:panelGroup>
                                  <ui:panelGroup>
                                      <h:inputText id="inputFriendlyGroupName" size="40" value="#{EditUserGroupPage.group.friendlyName}" required="true" requiredMessage="This field is required." onkeypress="if (window.event) return processEvent('', 'content:EditUserGroupPageView:form1:btnSave'); else return processEvent(event, 'content:EditUserGroupPageView:form1:btnSave');"/>
                                      <h:message styleClass="errorMessage" for="inputFriendlyGroupName"/>
                                  </ui:panelGroup>
                             </h:panelGrid>
                             <br />
                             <!--TBD (wjb) What type of group is this -->
                             <ui:panelGroup>
                                 <h:selectOneRadio rendered="#{EditUserGroupPage.userGroupType == 'none' and param.userGroupId == null}" onclick="setUserGroupType(this);document.forms[0].submit();" value="#{EditUserGroupPage.userGroupType}">
                                     <f:selectItems value="#{EditUserGroupPage.userGroupTypes}"/>
                                 </h:selectOneRadio>
                                 
                             </ui:panelGroup>
                             <h:inputHidden valueChangeListener="#{EditUserGroupPage.changeUserGroupType}" value="#{EditUserGroupPage.userGroupType}" id="userGroupType"/>
                             
                             <h:message for="userGroupType" styleClass="errorMessage" id="userGroupTypeMsg"/>
                             <br />
                             <!-- User Groups -->
                             <ui:panelGroup rendered="#{ EditUserGroupPage.userGroupType == 'none' or EditUserGroupPage.userGroupType == 'usergroup'}" id="usergroups" style="display:block;">
                                 <ui:panelGroup block="true" style="padding-bottom: 10px">
                                     <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                                     <h:outputText styleClass="vdcHelpText" value="Enter usernames in the field below. Add/remove a user by clicking the icons next to the username field."/>
                                 </ui:panelGroup>
                                 <h:outputText value="Usernames"/>
                                 <h:dataTable binding="#{EditUserGroupPage.dataTableUserNames}" var="currentRow" cellpadding="0" cellspacing="0" value="#{EditUserGroupPage.userDetails}">
                                     <h:column>
                                         <h:inputText id="usertable" value="#{currentRow.userName}" validator="#{EditUserGroupPage.validateUserName}" immediate="true"/>
                                     </h:column>
                                     <h:column>
                                         <h:commandButton  image="/resources/icon_add.gif" actionListener="#{EditUserGroupPage.addRow}"/> 
                                         <h:commandButton  image="/resources/icon_remove.gif" actionListener="#{EditUserGroupPage.removeRow}" /> 
                                         
                                     </h:column>
                                     <h:column>
                                         <h:message for="usertable" styleClass="errorMessage"/>
                                         <h:outputText styleClass="errorMessage" value="Username is invalid." rendered = "#{!currentRow.valid}"/>
                                         <h:outputText styleClass="errorMessage" value="Duplicate username." rendered = "#{currentRow.duplicate}"/>
                                     </h:column>
                                 </h:dataTable> 
                             </ui:panelGroup>
                             
                             <!-- IP Based Users Groups -->
                             <ui:panelGroup rendered="#{ (EditUserGroupPage.userGroupType == 'none' or EditUserGroupPage.userGroupType == 'ipgroup')}" id="ipgroups" style="display:block;">
                                 <ui:panelGroup block="true" style="padding-bottom: 10px">
                                     <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                                     <h:outputText styleClass="vdcHelpText" value="Enter IP addresses (192.168.2.1) and/or domain names (www.host.edu) in the field below. Wildcards are acceptable. Add/remove an entry by clicking the icons next to the username field."/>
                                 </ui:panelGroup>
                                 <h:outputText value="IP Addresses/Domains"/>
                                 <h:dataTable binding="#{EditUserGroupPage.dataTableIpAddresses}"  var="currentIpRow" cellpadding="0" cellspacing="0" value="#{EditUserGroupPage.group.loginDomains}">
                                     <h:column>
                                         <h:inputText id="iptable" immediate="true" validator="#{EditUserGroupPage.validateLoginDomain}" value="#{currentIpRow.ipAddress}"/>
                                     </h:column>
                                     <h:column>
                                         <h:commandButton  image="/resources/icon_add.gif" actionListener="#{EditUserGroupPage.addIpRow}"/> 
                                         <h:commandButton  image="/resources/icon_remove.gif" actionListener="#{EditUserGroupPage.removeIpRow}" /> 
                                     </h:column>
                                     <h:column>
                                         <h:message for="iptable" styleClass="errorMessage"/>
                                     </h:column>
                                 </h:dataTable> 
                                 
                             </ui:panelGroup>
                             
                             <!-- ******************** Affiliate login services for IP Groups ****************** -->
                             <ui:panelGroup rendered="#{ (EditUserGroupPage.userGroupType == 'none' or EditUserGroupPage.userGroupType == 'ipgroup')}" block="true" id="affiliateLoginServices" style="#{(EditUserGroupPage.userGroupType == 'ipgroup') ? 'display:block' : 'display:none;'}">
                                 <h:selectBooleanCheckbox id="chkAffiliateLoginService" valueChangeListener="#{EditUserGroupPage.changeChkAffiliateLoginService}" onclick="this.form.submit();" immediate="true" value="#{EditUserGroupPage.chkAffiliateLoginService}">
                                     <h:outputLabel for="chkAffiliateLoginService" value="This IP group has an affiliate login service."/>
                                 </h:selectBooleanCheckbox>
                                 <f:verbatim><br /></f:verbatim><f:verbatim><br /></f:verbatim>
                                 <ui:panelGroup block="true" style="padding-right:15px;padding-bottom: 10px; #{(EditUserGroupPage.chkAffiliateLoginService) ? 'display:block' : 'display:none;'}">
                                     <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                                     <h:outputText styleClass="vdcHelpText" value="Affiliate login services can be configured here. Enter affiliate names (ex. MySchool's Pin Service)  and urls (http://ez.pinserver.edu) in the field below."/>
                                 </ui:panelGroup>
                                 <h:dataTable binding="#{EditUserGroupPage.affiliatesTable}" rendered="true" value="#{EditUserGroupPage.group.loginAffiliates}" var="affiliate" style="margin-right:50px;#{(EditUserGroupPage.chkAffiliateLoginService) ? 'display:block' : 'display:none;'}">
                                     <h:column>
                                         <f:facet name="header">
                                             <h:outputText value="Affiliate Name"/>
                                         </f:facet>
                                         <h:inputText id="affiliateName" value="#{affiliate.name}"/> 
                                     </h:column>
                                     <h:column>
                                         <f:facet name="header">
                                             <h:outputText value="Affiliate URL"/>
                                         </f:facet>
                                         <h:inputText id="affiliateURL" value="#{affiliate.url}" />
                                     </h:column>
                                     <h:column>
                                         <h:commandButton image="/resources/icon_add.gif" actionListener="#{EditUserGroupPage.addAffiliateRow}"/> 
                                         <h:commandButton image="/resources/icon_remove.gif" actionListener="#{EditUserGroupPage.removeAffiliateRow}" /> 
                                     </h:column>
                                     <h:column>
                                         <h:message for="affiliateName" styleClass="errorMessage"/>
                                     </h:column>
                                 </h:dataTable>
                             </ui:panelGroup>
                             
                             <!-- end ip groups -->
                             <!-- Part of the code related to affiliates has been removed - can be recovered from earlier versions of this file (from revision 1672) -  1/29/08 by MC
                                -->
                             <f:verbatim rendered="#{param.userGroupId == null}">
                                 <script type="text/javascript">
                                        // this is done to ensure that the collections are properly inited. wjb
                                        showAll();
                                 </script>
                             </f:verbatim>
                             
                             
                             <ui:panelGroup block="true" style="padding-left: 100px; padding-top: 10px">
                                 <h:commandButton id="btnSave" value="Save" action="#{EditUserGroupPage.save}"/>
                                 <h:commandButton immediate="true" style="margin-left: 30px" value="Cancel" action="#{EditUserGroupPage.cancel}"/>
                             </ui:panelGroup>
                             
                         </div>
                     </div>
                 </div>
                            
            </h:form>
            </gui:define>
        </gui:composition>
    </body>
</html>
