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

<gui:param name="pageTitle" value="DVN - Dataverse Network Users" />

  <gui:define name="body">
      <f:loadBundle basename="Bundle" var="bundle"/>
     <f:loadBundle basename="BundleAnalysis" var="bundleAnalysis"/>

    <ui:form id="form1">
            <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>

            <div class="dvn_section">
                <div class="dvn_sectionTitle">
                   
                        <h:outputText value="All Users in #{VDCRequest.vdcNetwork.name} Dataverse Network"/>
                   
                </div>            
                <div class="dvn_sectionBox">
                    <div class="dvn_margin12">
                        
                        <ui:panelGroup block="true" style="padding-bottom: 15px">
                            <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                            <h:outputText styleClass="vdcHelpText" value="The default network admin has admin privileges to all Dataverses (note that only Dataverses created by the network admin will be listed here under his/her name)"/>
                        </ui:panelGroup>
                        <h:dataTable binding="#{AllUsersPage.dataTable}" cellpadding="0" cellspacing="0"
                                     columnClasses="vdcColPadded, vdcColPadded, vdcColPadded, vdcColPadded" headerClass="list-header-left" id="dataTable1"
                                     rowClasses="list-row-even,list-row-odd" value="#{AllUsersPage.userData}" var="currentRow" width="100%">
                            <h:column id="column1">
                                <f:facet name="header">
                                    <h:outputText id="outputText2" value="Username"/>
                                </f:facet>
                                <h:outputLink id="hyperlink2"  value="../login/AccountPage.jsp?userId=#{currentRow.user.id}&amp;vdcId=#{VDCRequest.currentVDCId}" >
                                    <h:outputText id="hyperlink2Text1" value="#{currentRow.user.userName}"/>
                                </h:outputLink>
                            </h:column>
                            <h:column id="column3">
                                <f:facet name="header">
                                    <h:outputText id="outputText5" value="Full Name"/>
                                </f:facet>
                                <h:outputText id="outputText6" value="#{currentRow.user.firstName} "/>
                                <h:outputText  value="#{currentRow.user.lastName}"/>
                            </h:column>
                            <h:column id="column2">
                                <f:facet name="header">
                                    <h:outputText id="outputText3" value="Role/s"/>
                                </f:facet>
                                <h:outputText id="outputText4" value="#{currentRow.roles}"/>
                            </h:column>
                            <h:column>
                                <f:facet name="header">
                                    <h:outputText id="outputText10" value="Status"/>
                                </f:facet>
                                <h:outputText value="Active" rendered="#{currentRow.user.active}"/>
                                <h:outputText value="Inactive" rendered="#{!currentRow.user.active}"/>
                            </h:column>
                            <h:column>
                                <h:commandLink actionListener="#{AllUsersPage.deactivateUser}" rendered="#{currentRow.user.active and !currentRow.defaultNetworkAdmin}">
                                    <h:outputText value="Deactivate"/>
                                </h:commandLink>
                                <h:commandLink actionListener="#{AllUsersPage.activateUser}" rendered="#{!currentRow.user.active and !currentRow.defaultNetworkAdmin}">
                                    <h:outputText value="Activate"/>
                                </h:commandLink>
                                <h:outputText value="Cannot Deactivate Network Admin" rendered ="#{currentRow.defaultNetworkAdmin}"/>
                                
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
