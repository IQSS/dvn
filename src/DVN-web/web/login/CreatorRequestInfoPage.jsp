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

<gui:param name="pageTitle" value="DVN - Request to Create a Dataverse" />

  <gui:define name="body">
      <f:loadBundle basename="Bundle" var="bundle"/>
     <f:loadBundle basename="BundleAnalysis" var="bundleAnalysis"/>
                            
     

        <ui:form  id="creatorRequestForm">
          <input type="hidden" name="pageName" value="CreatorRequestPage"/>
            <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>

            <div class="dvn_createDvRequest dvn_section dvn_overflow">
    	<div class="requestHeader dvn_overflow">
        	<div class="requestContentTitle">Create Your Own Dataverse</div>
        	<div class="requestContentDescRight">Create a dataverse to upload your own data sets and create collections of data. Archiving with a dataverse extends the useful life of data, preserving it in perpetuity against the failure of computers systems and the obsolescence of data formats.</div>
        </div>
        
        <div style="margin-bottom:12px; background-image:none; background-color:#ccc;" class="requestTimeline">
            <div style="left:87px;" class="requestTimelinePoint"><img src="/dvn/resources/lrg-grey-bullet.gif" alt="" /></div>
            <div style="left:278px;" class="requestTimelinePoint"><img src="/dvn/resources/lrg-grey-bullet.gif" alt="" /></div>
            <div style="left:473px;" class="requestTimelinePoint"><img src="/dvn/resources/lrg-grey-bullet.gif" alt="" /></div>
            <div style="left:668px;" class="requestTimelinePoint"><img src="/dvn/resources/lrg-grey-bullet.gif" alt="" /></div>
        </div>

        <div><div class="requestIntroSteps dvn_margin12 dvn_overflow">
        	<div class="requestIntroStepCard" style="margin-right:12px; background:#f4ebc7 url(/dvn/resources/request-intro-rndtop1.png) no-repeat;">
                    <h3>Create<br/>Account</h3>
                    <p>Register with our network, by creating an account...</p>
                    <div class="stepCardBtm">
                     <h:outputText value="&#160;" escape="false"/>
                    </div>
		</div>
        	<div class="requestIntroStepCard" style="margin-right:12px; background:#f4ebc7 url(/dvn/resources/request-intro-rndtop2.png) no-repeat;">
                    <h3>Agree<br/>to Terms</h3>
                    <p>Read and agree to the terms of use...</p>
                    <div class="stepCardBtm">
                     <h:outputText value="&#160;" escape="false"/>
                    </div>
		</div>
                <div class="requestIntroStepCard" style="margin-right:12px; background:#f4ebc7 url(/dvn/resources/request-intro-rndtop3.png) no-repeat;">
                    <h3>Name<br/>Dataverse</h3>
                    <p>Enter the title, type and affiliation...</p>
                    <p>Click save...</p>
                    <div class="stepCardBtm">
                     <h:outputText value="&#160;" escape="false"/>
                    </div>
		</div>
                <div class="requestIntroStepCard" style="background:#f4ebc7 url(/dvn/resources/request-intro-rndtop4.png) no-repeat;">
                    <h3 style="line-height:1.85;">Success!</h3>
                    <p>You have created your own dataverse; now you are ready to start adding data.</p>
                    <div class="stepCardBtm">
                     <h:outputText value="&#160;" escape="false"/>
                    </div>
		</div>
        </div>
        <div id="requestIntroCont">
                  <h:commandLink styleClass="requestIntroContLink" action="#{LoginWorkflowBean.beginCreatorWorkflow}">
                                <h:outputText value="Continue" escape="false"/>
                  </h:commandLink>
                  <br/>
                  <h:commandLink styleClass="requestIntroContLinkNote" rendered="#{VDCSession.loginBean==null }" action="#{LoginWorkflowBean.beginLoginCreatorWorkflow}">
                                <h:outputText value="Already have an account? Log In" escape="false"/>
                  </h:commandLink>
            </div>
        </div>
    </div>

        </ui:form>
            </gui:define>
        </gui:composition>
    </body>
</html>
