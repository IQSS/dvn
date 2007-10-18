<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
    xmlns:h="http://java.sun.com/jsf/html" 
    xmlns:jsp="http://java.sun.com/JSP/Page" 
    xmlns:ui="http://www.sun.com/web/ui"
    xmlns:tiles="http://struts.apache.org/tags-tiles"
    xmlns:c="http://java.sun.com/jsp/jstl/core">

    <f:subview id="addSitePageView">
         <f:verbatim>
            <script language="JavaScript">
                function setCheckedValue(frmElement) {
                    var theForm = document.forms['content:addSitePageView:form1'];
                    for (var i = 0; i &lt; theForm.elements.length; i++) {
                        if (theForm.elements[i].checked) {
                            frmElement.value = theForm.elements[i].value;
                            break;
                        }
                    }
                 }
                 
                 //init the hidden fields
                 function showAll(){
                    var theForm = document.forms['content:addSitePageView:form1'];
                    var showScholarFields = false;
                    for (var i = 0; i &lt; theForm.elements.length; i++) {
                        if (theForm.elements[i].checked &amp;&amp; theForm.elements[i].value == "Scholar") {
                            showScholarFields = true;
                            break;
                        }
                    }
                    if (!showScholarFields) {
                        document.getElementById('content:addSitePageView:form1:firstName').style.display = 'none';
                        document.getElementById('content:addSitePageView:form1:lastName').style.display = 'none';
                    }
                }
                
                function createDvName() {
                    if (document.getElementById('content:addSitePageView:form1:firstName').value != "" &amp;&amp; document.getElementById('content:addSitePageView:form1:lastName').value != "") {
                        document.getElementById('content:addSitePageView:form1:dataverseName').value = document.getElementById('content:addSitePageView:form1:firstName').value + " " + document.getElementById('content:addSitePageView:form1:lastName').value;
                    }
                }
            </script>
        </f:verbatim>
        <h:outputText id="statusMessage" styleClass="#{AddSitePage.msg.styleClass}" value="#{AddSitePage.msg.messageText}" />
        <ui:form binding="#{AddSitePage.form1}" id="form1">
            
            <div class="dvn_section">
                <div class="dvn_sectionTitle">
                    <a name="addSite" title="">
                        <h:outputText binding="#{AddSitePage.outputText1}" value="Add a New Dataverse"/>
                    </a>
                </div>            
                <div class="dvn_sectionBox">
                    <div class="dvn_margin12"> 
                        <ui:panelGroup block="true" style="padding-left: 20px; padding-right: 30px">
                            <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                            <h:outputText binding="#{AddSitePage.outputText2}" id="outputText2" styleClass="vdcHelpText" value="When your dataverse is  created, it will be set to 'restricted' by default. As soon as you are ready to make it public, you can do so by going to 'My Options' in your new dataverse.  "/>
                        </ui:panelGroup>
                        <h:panelGrid binding="#{AddSitePage.gridPanel1}" cellpadding="0" cellspacing="0"
                                     columnClasses="vdcAddSiteCol1, vdcAddSiteCol2" columns="2" id="gridPanel1" style="margin-top: 30px; margin-bottom: 30px">
                            <!-- dataverse type -->
                            <ui:panelGroup block="true" style="vertical-align: top;">
                                <h:outputLabel for="dataverseOption" id="dataverseLabel">
                                    <h:outputText style="white-space: nowrap; padding-right: 10px; " value="Type of Dataverse"/>                                              
                                </h:outputLabel>
                            </ui:panelGroup>
                            <ui:panelGroup block="true" style="vertical-align: top;">
                                <h:selectOneRadio id="dataverseOption" 
                                                    layout="lineDirection" 
                                                    onchange="setCheckedValue(document.getElementById('content:addSitePageView:form1:dataverseType'));submit();"
                                                    valueChangeListener="#{AddSitePage.changeDataverseOption}"
                                                    required="true">
                                    <f:selectItems value="#{AddSitePage.dataverseOptions}"/>
                                </h:selectOneRadio>
                             <h:inputHidden id="dataverseType" immediate="true" value="#{AddSitePage.dataverseType}" valueChangeListener="#{AddSitePage.changeDataverseOption}"/>
                            </ui:panelGroup>
                            <!-- first name -->
                            <ui:panelGroup rendered="#{AddSitePage.dataverseType == 'Scholar'}" block="true" style="vertical-align: top;">
                                <h:outputLabel for="firstName" id="firstnameLabel">
                                    <h:outputText style="white-space: nowrap; padding-right: 10px; " value="First Name"/>                                              
                                    <h:graphicImage value="#{bundle.iconRequired}"/>
                                </h:outputLabel>
                            </ui:panelGroup>
                            <ui:panelGroup block="true" style="vertical-align: top;">
                                <h:inputText id="firstName" 
                                                immediate="true"
                                                onblur="createDvName();"
                                                required="true"
                                                requiredMessage="This field is required."
                                                value="#{AddSitePage.firstName}" 
                                                valueChangeListener="#{AddSitePage.changeFirstName}"
                                                style="display:block;"/>
                            </ui:panelGroup>
                            <!-- last name -->
                            <ui:panelGroup rendered="#{AddSitePage.dataverseType == 'Scholar'}" block="true" style="vertical-align: top;">
                                <h:outputLabel for="lastName" id="lastnameLabel">
                                    <h:outputText style="white-space: nowrap; padding-right: 10px; " value="Last Name"/> 
                                    <h:graphicImage value="#{bundle.iconRequired}"/>
                                </h:outputLabel>
                            </ui:panelGroup>
                            <ui:panelGroup block="true" style="vertical-align: top;">
                                <h:inputText id="lastName" 
                                                immediate="true"
                                                onblur="createDvName();"
                                                required="true"
                                                requiredMessage="This field is required."
                                                value="#{AddSitePage.lastName}" 
                                                valueChangeListener="#{AddSitePage.changeLastName}"
                                                style="display:block;"/>
                            </ui:panelGroup>
                            <!-- affiliation -->
                            <ui:panelGroup block="true" style="vertical-align: top;">
                                <h:outputLabel for="affiliation" id="affiliationLabel">
                                    <h:outputText style="white-space: nowrap; padding-right: 10px; " value="Affiliation"/>                                              
                                </h:outputLabel>
                            </ui:panelGroup>
                            <ui:panelGroup block="true" style="vertical-align: top;">
                                <h:inputText id="affiliation" immediate="true" value="#{AddSitePage.affiliation}" valueChangeListener="#{AddSitePage.changeAffiliation}" />
                            </ui:panelGroup>
                            <!-- Dataverse Name -->
                            <ui:panelGroup block="true" style="vertical-align: top;">
                                <h:outputLabel binding="#{AddSitePage.componentLabel1}" for="componentLabel1" id="componentLabel1">
                                    <h:outputText binding="#{AddSitePage.componentLabel1Text}" id="componentLabel1Text" style="white-space: nowrap; padding-right: 10px; " value="Dataverse Name"/>                                              
                                    <h:graphicImage value="#{bundle.iconRequired}"/>
                                </h:outputLabel>
                            </ui:panelGroup>
                            <ui:panelGroup>
                                <h:inputText binding="#{AddSitePage.dataverseName}" id="dataverseName" required="true" validator="#{AddSitePage.validateName}" size="60"/>
                                <f:verbatim><br /></f:verbatim>
                                <h:outputText styleClass="vdcHelpText" value="Name used to refer to this dataverse in Dataverse Network Homepage and other pages."/>
                                <h:message for="dataverseName" showSummary="true" showDetail="false" errorClass="errorMessage" styleClass="errorMessage"/>
                            </ui:panelGroup>
                            <!-- Dataverse Alias -->
                            <ui:panelGroup block="true" style="vertical-align: top;">
                                <h:outputLabel binding="#{AddSitePage.componentLabel2}" for="componentLabel2" id="componentLabel2">
                                    <h:outputText binding="#{AddSitePage.componentLabel2Text}" id="componentLabel2Text" style="white-space: nowrap; padding-right: 10px; " value="Dataverse Alias"/> 
                                    <h:graphicImage value="#{bundle.iconRequired}"/>
                                </h:outputLabel>
                            </ui:panelGroup>
                            <ui:panelGroup>
                                <h:inputText binding="#{AddSitePage.dataverseAlias}" id="dataverseAlias" required="true" validator="#{AddSitePage.validateAlias}" />
                                <f:verbatim><br /></f:verbatim>
                                <h:outputText styleClass="vdcHelpText" value="Short name used to build the URL for this dataverse, i.e., http://.../dv/'alias'. It is case sensitive."/>
                                <h:message for="dataverseAlias" showSummary="true" showDetail="false" errorClass="errorMessage" styleClass="errorMessage"/>
                            </ui:panelGroup>
                        </h:panelGrid>
                        <ui:panelGroup binding="#{AddSitePage.groupPanel1}" block="true" id="groupPanel1" style="padding-left: 160px">
                            <h:commandButton rendered="#{AddSitePage.dataverseType != 'Scholar'}" binding="#{AddSitePage.button1}" id="button1" value="Save" action="#{AddSitePage.create}"/>
                            <h:commandButton rendered="#{AddSitePage.dataverseType == 'Scholar'}" id="btnCreateSdv" value="Save" action="#{AddSitePage.createScholarDataverse}"/>
                            <h:commandButton binding="#{AddSitePage.button2}" id="button2" style="margin-left: 20px" immediate="true" value="Cancel" action="#{AddSitePage.cancel}"/>
                        </ui:panelGroup>
                        <f:verbatim>
                             <script language="JavaScript">
                                    // this is done to ensure that the scholar fields are properly inited. wjb
                                    showAll();
                             </script>
                         </f:verbatim>
                    </div>
                </div>
            </div>
        </ui:form>
    </f:subview>
</jsp:root>
