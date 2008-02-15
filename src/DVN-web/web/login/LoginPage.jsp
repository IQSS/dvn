<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles">
            <f:subview id="vanillaLoginPageView">
                <div id="loginPageWrap">
                    <ui:form id="vanillaLoginForm">
                        <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
                           <h:inputHidden binding="#{LoginPage.hiddenStudyId}" value="#{LoginPage.studyId}"/>
                         <f:verbatim>

                         </f:verbatim>
                        <ui:panelLayout id="layoutPanel1" panelLayout="flow" styleClass="ContentArea">
                                <p id="loginPageHeader"><a name="login" title=""><h:outputText escape="false" styleClass="headingText" value="Login"/></a></p>
                               
                                <ui:panelLayout panelLayout="flow" styleClass="ContentAreaBody">
                                    <ui:panelGroup styleClass="loginPageMessages"  block="true" style="padding-bottom: 15px; padding-right:50px "  rendered="#{LoginPage.redirect!=null}">
                                        <h:outputText styleClass="warnMessage" value="We are sorry, this page is restricted and you need to have special permissions to access it. Please log in if you have the appropriate permissions."/>
                                    </ui:panelGroup>
                                   
                                    <ui:panelGroup style="padding-top: 10px; margin-left:auto; margin-right:auto; width:70%; text-align: left;" block="true" id="groupPanel5">
                                        <h:outputText id="outputText4" styleClass="loginPageSubHeader" value="Dataverse Account" />
                                    </ui:panelGroup>
                                    <!-- ************ Username login section ******************* -->
                                    <h:panelGrid style="margin-left:auto; margin-right:auto; background-color: #f4ebc7 !important; border: 1px solid #ddd;" cellpadding="0" cellspacing="0" columnClasses="vdcLoginCol1, vdcLoginCol2" columns="2" id="gridPanel2" width="70%">
                                        <ui:panelGroup block="true" style="padding-top: 20px; padding-right:10px; text-align:right;">
                                            <h:outputLabel  for="componentLabel1" id="componentLabel1" >
                                                <h:outputText id="componentLabel1Text1" value="Username" />
                                            </h:outputLabel>
                                        </ui:panelGroup>
                                        <ui:panelGroup block="true" style="padding-top: 20px;">
                                            <h:inputText id="username" value="#{LoginPage.userName}" required="true" requiredMessage="This field is required."  />
                                            <br />
                                            <h:message for="username" styleClass="errorMessage" />
                                        </ui:panelGroup>
                                        <ui:panelGroup block="true" style="text-align:right; padding-right:10px;">
                                            <h:outputLabel for="componentLabel2" id="componentLabel2">
                                                <h:outputText id="componentLabel2Text1" value="Password" />
                                            </h:outputLabel>
                                        </ui:panelGroup>
                                        <ui:panelGroup>
                                            <h:inputSecret onkeypress="if (window.event) return processEvent('', 'content:LoginPageView:loginForm:button1'); else return processEvent(event, 'content:LoginPageView:loginForm:button1');" id="password" value="#{LoginPage.password}" required="true" requiredMessage="This field is required."/>
                                            <br />
                                            <h:message for="password" styleClass="errorMessage"/>
                                        </ui:panelGroup>
                                        <ui:panelGroup>
                                            <f:verbatim><!-- placeholder --></f:verbatim>
                                        </ui:panelGroup>
                                        <ui:panelGroup  block="true" id="groupPanel1" style="padding-left: 50px; padding-top: 10px; padding-right:5px; text-align:right;">
                                            <h:commandButton  id="button1" value="Log in" action="#{LoginPage.login}" />
                                        </ui:panelGroup>
                                    </h:panelGrid>
                                    <h:outputText value="Login Failed. Please check your username and password and try again." styleClass="errorMessage" rendered="#{LoginPage.loginFailed}"/>
                                     <!-- ************* end username login section *************** -->
                                     
                                    <!-- ************ Affiliate login section ******************* -->
                                    
                                    <ui:panelGroup style="padding-top: 20px; margin-left:auto; margin-right:auto; width:70%; text-align: left;"  block="true" id="groupPanelAffiliate1" rendered="#{LoginPage.isAffiliates != null}" >
                                            <h:outputText id="outputTextAffiliate" styleClass="loginPageSubHeader" value="Affiliate"/>
                                        </ui:panelGroup>
                                    <h:panelGrid rendered="#{LoginPage.isAffiliates != null}" style="margin-left:auto; margin-right:auto; background-color: #e6ebed; border: 1px solid #ddd;" cellpadding="0" cellspacing="0" columnClasses="vdcLoginCol1, vdcLoginCol2" columns="2" id="gridPanelAffiliate" width="70%">
                                        <ui:panelGroup block="true" style="padding-top: 20px; padding-right:10px; text-align:right;">
                                            <h:outputLabel  for="affiliateName" id="componentLabelAffiliateName" >
                                                <h:outputText id="componentLabelAffiliateText" value="Affiliate Name" />
                                            </h:outputLabel>
                                        </ui:panelGroup>
                                        <ui:panelGroup block="true" style="padding-top: 20px;">
                                            <h:selectOneMenu valueChangeListener="#{LoginPage.changeAffiliateName}" immediate="true" id="affiliateName" value="#{LoginPage.affiliateName}" style="#{ (LoginPage.isAffiliates != null) ? 'display:block;' : 'display:none;' }">
                                                <f:selectItems value="#{LoginPage.affiliateNames}"/>
                                            </h:selectOneMenu>
                                            
                                            <h:message for="affiliateName" styleClass="errorMessage" />
                                        </ui:panelGroup>
                                        <ui:panelGroup>
                                            <f:verbatim><!-- placeholder --></f:verbatim>
                                        </ui:panelGroup>
                                        <ui:panelGroup  block="true" id="groupPanelAffiliate2" style="padding-left: 5px; padding-top: 10px; padding-right:5px; text-align:right;">
                                            <h:commandButton immediate="true" id="btnAffiliateLogin" value="Log in" action="#{LoginPage.loginAffiliate}" />
                                        </ui:panelGroup>
                                    </h:panelGrid>
                                    <!-- ************* end affiliate login section *************** -->
                                   <!-- <h:outputText value="Login Failed. Please check your username and password and try again." styleClass="errorMessage" rendered="#{LoginPage.loginFailed}"/> -->
                                </ui:panelLayout>
                                <ui:panelLayout panelLayout="flow" styleClass="ContentAreaFooter">
                                    <f:verbatim>
                                        <p>
                                            <ui:imageHyperlink alt="Powered by the Dataverse Network Project" border="0" imageURL="/resources/dvnPoweredByLogo.gif" toolTip="Link to the Dataverse Network Project" url="http://thedata.org" target="_blank" />
                                        </p>
                                    </f:verbatim>
                               
                            </ui:panelLayout>
                            </ui:panelLayout> 
                            
                    </ui:form>
                </div>
            </f:subview>
</jsp:root>
