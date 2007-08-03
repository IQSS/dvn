<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles">
            <f:subview id="vanillaLoginPageView">
                    <ui:form  id="vanillaLoginForm">
                        <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
                        <h:inputHidden binding="#{LoginPage.hiddenWorkflow}" value="#{LoginPage.workflow}"/>
                         <h:inputHidden binding="#{LoginPage.hiddenStudyId}" value="#{LoginPage.studyId}"/>
                         <f:verbatim>
                 <!-- <div class="ContentArea">
		<h3>Article header</h3> controls top right
		
		<div class="ContentAreaBody">
		<p>

		A few paragraphs of article text.<br />
		A few paragraphs of article text.
		</p>
			
		<p>
		A few paragraphs of article text.<br />
		A few paragraphs of article text.
		</p>
		</div>
		
		<div class="ContentAreaFooter">

		<p>
		A paragraph containing author information
		</p>  writes bottom right
		</div>
                </div> -->

                         </f:verbatim>
                        <ui:panelLayout id="layoutPanel1" panelLayout="flow" styleClass="ContentArea">
                                <h:outputText escape="false" value="&lt;p&gt;&lt;span class=&quot;headingText&quot;&gt;Log in&lt;/span&gt;&lt;/p&gt;"/>
                                
                               
                                <ui:panelLayout panelLayout="flow" style="padding-left: 50px; padding-top: 30px; padding-bottom: 40px" styleClass="ContentAreaBody">
                                    <ui:panelGroup styleClass="loginPageMessages"  block="true" style="padding-bottom: 15px; padding-right:50px "  rendered="#{LoginPage.redirect!=null}">
                                        <h:outputText styleClass="warnMessage" value="We are sorry, this page is restricted and you need to have special permissions to access it. Please log in if you have the appropriate permissions."/>
                                    </ui:panelGroup>
                                   
                                    <ui:panelGroup style="padding-bottom: 10px;"  block="true" id="groupPanel5">
                                            <h:outputText  id="outputText4" styleClass="vdcSubHeader" value="Log in with a Dataverse account:" />
                                        </ui:panelGroup>
                                    <!-- ************ Username login section ******************* -->
                                    <h:panelGrid style="margin-left:auto; margin-right:auto; background-color:#e6f2ff !important; border: 1px solid #a3b1bf; padding-left:15px;  " cellpadding="0" cellspacing="0" columnClasses="vdcLoginCol1, vdcLoginCol2" columns="2" id="gridPanel2" width="70%">
                                        <ui:panelGroup block="true" style="padding-top: 20px;">
                                            <h:outputLabel  for="componentLabel1" id="componentLabel1" >
                                                <h:outputText id="componentLabel1Text1" value="Username" />
                                            </h:outputLabel>
                                        </ui:panelGroup>
                                        <ui:panelGroup block="true" style="padding-top: 20px;">
                                            <h:inputText id="username" value="#{LoginPage.userName}" required="true"  />
                                            <h:message for="username" styleClass="errorMessage" />
                                        </ui:panelGroup>
                                        <h:outputLabel for="componentLabel2" id="componentLabel2">
                                            <h:outputText id="componentLabel2Text1" value="Password" />
                                        </h:outputLabel>
                                        <ui:panelGroup>
                                            <h:inputSecret onkeypress="if (window.event) return processEvent('', 'content:LoginPageView:loginForm:button1'); else return processEvent(event, 'content:LoginPageView:loginForm:button1');" id="password" value="#{LoginPage.password}" required="true" />
                                            <h:message for="password" styleClass="errorMessage"/>
                                        </ui:panelGroup>
                                        <ui:panelGroup>
                                            <f:verbatim><!-- placeholder --></f:verbatim>
                                        </ui:panelGroup>
                                        <ui:panelGroup  block="true" id="groupPanel1" style="padding-left: 50px; padding-top: 10px">
                                            <h:commandButton  id="button1" value="Log in" action="#{LoginPage.login}" />
                                        </ui:panelGroup>
                                    </h:panelGrid>
                                    <h:outputText value="Login Failed. Please check your username and password and try again." styleClass="errorMessage" rendered="#{LoginPage.loginFailed}"/>
                                     <!-- ************* end username login section *************** -->
                                     
                                    <!-- ************ Affiliate login section ******************* -->
                                    
                                    <ui:panelGroup style="padding-bottom: 10px; padding-top: 10px;"  block="true" id="groupPanelAffiliate1" rendered="#{LoginPage.isAffiliates != null}" >
                                            <h:outputText  id="outputTextAffiliate" style="font-weight: bold" value="Log in through an affiliate:"/>
                                        </ui:panelGroup>
                                    <h:panelGrid rendered="#{LoginPage.isAffiliates != null}" style="margin-left:auto; margin-right:auto; background-color:#f2ffe6;border:1px solid #a3bfb1; padding-left:15px;" cellpadding="0" cellspacing="0" columnClasses="vdcLoginCol1, vdcLoginCol2" columns="2" id="gridPanelAffiliate" width="70%">
                                        <ui:panelGroup block="true" style="padding-top: 20px;">
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
                                        <ui:panelGroup  block="true" id="groupPanelAffiliate2" style="padding-left: 5px; padding-top: 10px">
                                            <h:commandButton immediate="true" id="btnAffiliateLogin" value="Log in" action="#{LoginPage.loginAffiliate}" />
                                        </ui:panelGroup>
                                    </h:panelGrid>
                                    <!-- ************* end affiliate login section *************** -->
                                   <!-- <h:outputText value="Login Failed. Please check your username and password and try again." styleClass="errorMessage" rendered="#{LoginPage.loginFailed}"/> -->
                                </ui:panelLayout>
                                <ui:panelLayout panelLayout="flow" styleClass="ContentAreaFooter">
                                    <f:verbatim>
                                        <p>
                                            <h:graphicImage value="/resources/poweredby_logo.gif"/>
                                        </p>
                                    </f:verbatim>
                               
                            </ui:panelLayout>
                            </ui:panelLayout> 
                            
                    </ui:form>
            </f:subview>
</jsp:root>
