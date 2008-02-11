<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
    xmlns:h="http://java.sun.com/jsf/html" 
    xmlns:jsp="http://java.sun.com/JSP/Page" 
    xmlns:ui="http://www.sun.com/web/ui"
    xmlns:tiles="http://struts.apache.org/tags-tiles"
    xmlns:c="http://java.sun.com/jsp/jstl/core">

    <f:subview id="addSitePageView">
        <h:outputText id="statusMessage" styleClass="#{AddSitePage.msg.styleClass}" value="#{AddSitePage.msg.messageText}" />
        <ui:form binding="#{AddSitePage.form1}" id="form1">
            <ui:script type="text/javascript">
                //<![CDATA[
                 function changeValue(obj) {
                    if (window.event)
                        obj.value = window.event.srcElement.value;
                        document.getElementById('content:addSitePageView:form1').submit();
                }
                 
                 //init the hidden fields
                 function showAll(){
                    var theForm = document.getElementById('content:addSitePageView:form1');
                    var showScholarFields = false;
                    for (var i = 0; i < theForm.elements.length; i++) {
                        if ( (theForm.elements[i].checked) && (theForm.elements[i].value == "Scholar") ) {
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
                    if (document.getElementById('content:addSitePageView:form1:firstName').value != "" && document.getElementById('content:addSitePageView:form1:lastName').value != "") {
                        document.getElementById('content:addSitePageView:form1:dataverseName').value = document.getElementById('content:addSitePageView:form1:firstName').value + " " + document.getElementById('content:addSitePageView:form1:lastName').value;
                    }
                }
                 // ]]>
            </ui:script>
            <ui:panelLayout styleClass="#{ (LoginWorkflowBean.creatorWorkflow) ? 'dvn_createDvRequest dvn_section dvn_overflow' : 'dvn_section'}">
                <ui:panelLayout styleClass="#{ (LoginWorkflowBean.creatorWorkflow) ? 'requestHeader dvn_overflow' : 'dvn_sectionTitle'}">
                      <h:outputText value="Add a New Dataverse" rendered="#{LoginWorkflowBean.plainWorkflow}"/>
                      <h:outputText value="Add a New Dataverse" rendered="#{LoginWorkflowBean.fileAccessWorkflow}"/>
                      <h:outputText value="Name Dataverse &lt;span&gt;&gt; Create Your Own Dataverse&lt;/span&gt;" rendered="#{LoginWorkflowBean.creatorWorkflow}" escape="false"/>
                </ui:panelLayout>
              <ui:panelLayout rendered="#{LoginWorkflowBean.creatorWorkflow}" styleClass="requestTimeline" style="background-position: 0 -8px;">
                        <div class="requestTimelinePoint" style="left: 53px;"><img src="/dvn/resources/lrg-blue-bullet.gif" class="vdcNoBorder" alt=""/><br/><strong style="color:#035899;">Create Account</strong></div>
                        <div class="requestTimelinePoint" style="left: 271px;"><img src="/dvn/resources/lrg-blue-bullet.gif" class="vdcNoBorder" alt=""/><br/><strong style="color:#035899;">Terms of Use</strong></div>
                        <div class="requestTimelinePoint" style="left: 474px;"><img src="/dvn/resources/lrg-blue-bullet.gif" class="vdcNoBorder" alt=""/><br/><strong style="color:#035899; font-weight:bold;">Name Dataverse</strong></div>
                        <div class="requestTimelinePoint" style="left: 709px;"><img src="/dvn/resources/lrg-grey-bullet.gif" class="vdcNoBorder" alt=""/><br/><strong>Success!</strong></div>
              </ui:panelLayout>
                <ui:panelLayout styleClass="#{ (LoginWorkflowBean.creatorWorkflow) ? 'requestContent' : 'dvn_sectionBox'}">
                    
                    <ui:panelLayout rendered="#{LoginWorkflowBean.creatorWorkflow}" styleClass="requestContentDescLeft">
                        <p>Choose Scholar if this dataverse will have your own name and will contain your own research, and Basic for any other dataverse.</p>
                        <p>Select the group that will most likely fit your dataverse, be it a university department, a journal, a research center, etc. If you create a Scholar dataverse, it will be automatically entered under the Scholar group.</p>
                    </ui:panelLayout>
                  
                    <ui:panelLayout styleClass="#{ (LoginWorkflowBean.creatorWorkflow) ? 'requestContentDescRight' : 'empty'}">
                    <ui:panelLayout styleClass="dvn_margin12"> 
                    
                        <ui:panelGroup rendered="#{LoginWorkflowBean.plainWorkflow or LoginWorkflowBean.fileAccessWorkflow}" block="true" style="padding-left: 20px; padding-right: 30px">
                            <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                            <br />
                            <h:outputText id="outputText2a" styleClass="vdcHelpText" value="1) Choose 'Scholar' if this dataverse will 
                            have your own name and will contain your own research, and 'Basic' for any other dataverse."/>
                            <br />
                            <h:outputText id="outputText2b" styleClass="vdcHelpText" value="2) Select the group that will most likely 
                            fit your dataverse, be it a university department, a journal, a research center, etc. If you create
                            a Scholar dataverse, it will be automatically entered under the Scholar group."/>
                            <br />
                            <h:outputText id="outputText2c" styleClass="vdcHelpText" value="3) Once your dataverse is created, it will be set to 'Coming Soon' (Not Released) by
                            default. As soon as you are ready to make it available (Released), you can do so by going to 'My Options' in your new dataverse."/>
                        </ui:panelGroup>
                        <h:panelGrid binding="#{AddSitePage.gridPanel1}" cellpadding="0" cellspacing="0"
                                     columnClasses="vdcAddSiteCol1, vdcAddSiteCol2" columns="2" id="gridPanel1" style="margin-top: 30px; margin-bottom: 30px">
                            <!-- dataverse type -->
                            <ui:panelGroup block="true" style="vertical-align: top;">
                                <h:outputLabel for="dataverseType" id="dataverseLabel">
                                    <h:outputText style="white-space: nowrap; padding-right: 10px; " value="Type of Dataverse"/>                                              
                                </h:outputLabel>
                            </ui:panelGroup>
                            <ui:panelGroup block="true" style="vertical-align: top;">
                                <h:selectOneRadio id="dataverseType" 
                                                    layout="lineDirection" 
                                                    onclick="changeValue(this);"
                                                    value="#{AddSitePage.dataverseType}"
                                                    valueChangeListener="#{AddSitePage.changeDataverseOption}"
                                                    required="true"
                                                    requiredMessage="This field is required.">
                                    <f:selectItems value="#{AddSitePage.dataverseOptions}"/>
                                </h:selectOneRadio>
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
                                                onblur="createDvName();"
                                                required="#{(AddSitePage.dataverseType == 'Scholar') ? true : false}"
                                                requiredMessage="This field is required."
                                                validator="#{AddSitePage.validateIsEmpty}"
                                                value="#{AddSitePage.firstName}" 
                                                valueChangeListener="#{AddSitePage.changeFirstName}"
                                                style="display:block;"/>
                                 <h:message for="firstName" showSummary="true" showDetail="false" errorClass="errorMessage" styleClass="errorMessage"/>
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
                                             onblur="createDvName();"
                                             required="#{(AddSitePage.dataverseType == 'Scholar') ? true : false}"
                                             requiredMessage="This field is required."
                                             validator="#{AddSitePage.validateIsEmpty}"
                                             value="#{AddSitePage.lastName}" 
                                             valueChangeListener="#{AddSitePage.changeLastName}"
                                             style="display:block;"/>
                                <h:message for="lastName" showSummary="true" showDetail="false" errorClass="errorMessage" styleClass="errorMessage"/>
                            </ui:panelGroup>
                            <!-- affiliation -->
                            <ui:panelGroup block="true" style="vertical-align: top;">
                                <h:outputLabel for="affiliation" id="affiliationLabel">
                                    <h:outputText style="white-space: nowrap; padding-right: 10px; " value="Affiliation"/>                                              
                                </h:outputLabel>
                            </ui:panelGroup>
                            <ui:panelGroup block="true" style="vertical-align: top;">
                                <h:inputText id="affiliation" immediate="true" value="#{AddSitePage.affiliation}" valueChangeListener="#{AddSitePage.changeAffiliation}" />
                                <br />
                                <h:outputText styleClass="vdcHelpText" value="University, center, or research project."/>
                            </ui:panelGroup>
                            <!-- Dataverse Name -->
                            <ui:panelGroup block="true" style="vertical-align: top;">
                                <h:outputLabel binding="#{AddSitePage.componentLabel1}" for="componentLabel1" id="componentLabel1">
                                    <h:outputText binding="#{AddSitePage.componentLabel1Text}" id="componentLabel1Text" style="white-space: nowrap; padding-right: 10px; " value="Dataverse Name"/>                                              
                                    <h:graphicImage value="#{bundle.iconRequired}"/>
                                </h:outputLabel>
                            </ui:panelGroup>
                            <ui:panelGroup>
                                <h:inputText binding="#{AddSitePage.dataverseName}" 
                                                id="dataverseName" 
                                                required="true" 
                                                requiredMessage="This field is required." 
                                                validator="#{AddSitePage.validateName}" size="60"/>
                                <br />
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
                                <h:inputText binding="#{AddSitePage.dataverseAlias}" 
                                                id="dataverseAlias" 
                                                required="true" 
                                                requiredMessage="This field is required."
                                                validator="#{AddSitePage.validateAlias}" />
                                <f:verbatim><br /></f:verbatim>
                                <h:outputText styleClass="vdcHelpText" value="Short name used to build the URL for this dataverse, e.g., http://.../dv/'alias'. It is case sensitive."/>
                                <h:message for="dataverseAlias" showSummary="true" showDetail="false" errorClass="errorMessage" styleClass="errorMessage"/>
                            </ui:panelGroup>
                            <!-- Group Assignment -->
                            <ui:panelGroup block="true" style="vertical-align: top;">
                                <h:outputLabel for="selectedGroup" id="selectedGroupLabel">
                                    <h:outputText id="selectedGroupText" style="white-space: nowrap; padding-right: 10px; " value="Group Assignment"/> 
                                </h:outputLabel>
                            </ui:panelGroup>
                            <ui:panelGroup rendered="#{AddSitePage.dataverseType == 'Basic'}">
                                <h:selectOneMenu id="selectedGroup" 
                                    immediate="true"
                                    onchange="this.form.submit();"
                                    value="#{AddSitePage.selectedGroup}"
                                    valueChangeListener="#{AddSitePage.changeSelectedGroup}">
                                    <f:selectItems value="#{AddSitePage.groupItems}" />
                                </h:selectOneMenu>
                            </ui:panelGroup>
                            <ui:panelGroup rendered="#{AddSitePage.dataverseType == 'Scholar'}">
                                <h:outputText id="scholarDataverseGroup" 
                                    value="Scholar Dataverse Group" />
                            </ui:panelGroup>
                        </h:panelGrid>
                        <ui:panelGroup binding="#{AddSitePage.groupPanel1}" block="true" id="groupPanel1" style="padding-left: 160px">
                            <h:commandButton rendered="#{AddSitePage.dataverseType != 'Scholar'}" binding="#{AddSitePage.button1}" id="button1" value="Save" action="#{AddSitePage.create}"/>
                            <h:commandButton rendered="#{AddSitePage.dataverseType == 'Scholar'}" id="btnCreateSdv" value="Save" action="#{AddSitePage.createScholarDataverse}"/>
                            <h:commandButton binding="#{AddSitePage.button2}" id="button2" style="margin-left: 20px" immediate="true" value="Cancel" action="#{AddSitePage.cancel}"/>
                        </ui:panelGroup>
                       <ui:script type="text/javascript">
                            //<![CDATA[
                                 
                                    // this is done to ensure that the scholar fields are properly inited. wjb
                                    showAll();
                            // ]]>
                        </ui:script>
                        
                    </ui:panelLayout>
                    </ui:panelLayout>
                    
                </ui:panelLayout>
            </ui:panelLayout>
        </ui:form>
    </f:subview>
</jsp:root>
