<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles">
    <f:subview id="ContributorRequestInfoPageView">
        <ui:form  id="ContributorRequestForm">
          <input type="hidden" name="pageName" value="ContributorRequestPage"/>
            <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>

            <div id="dvn_createDvRequest" class="dvn_section dvn_overflow">
    	<div id="requestHeader" class="dvn_overflow">
        	<div id="requestContentTitle">Become a Contributor</div>
        	<div id="requestContentDescRight"> Archiving with the dataverse extends the useful life of data, preserving it in perpetuity against the failure of computers systems and the obsolescence of data formats.</div>
        </div>

        <div><div class="dvn_margin12 dvn_overflow" id="requestIntroSteps">
        	<div class="requestIntroStepCard" style="margin-right: 24px; background: #f4ebc7 url(/resources/request-intro-rndtop1.png) no-repeat;">
            	<h3>Send a<br />Request</h3>
                <p>Start by sending a request to be approved for a dataverse.</p>
                <p>Registering with our network, agree to the terms of use and then tell us why you would like a dataverse.</p>
            	<div class="stepCardBtm">
                 <h:outputText   value="&#160;" escape="false"/>
                </div>
			</div>
        	<div class="requestIntroStepCard" style="margin-right: 24px; background: #f4ebc7 url(/resources/request-intro-rndtop2.png) no-repeat;">
            	<h3>Approval<br />Process</h3>
                <p>Our group will review your request and will notify you within 10 days of our decision.</p>
                <p>You will receive an email invitation to start creating your dataverse.</p>
            	<div class="stepCardBtm"> 
                  <h:outputText   value="&#160;" escape="false"/>                
             </div>
			</div>
        	<div class="requestIntroStepCard" style="background: #f4ebc7 url(/resources/request-intro-rndtop3.png) no-repeat;">
            	<h3>Start<br />Building</h3>
                <p>Simply log into your dataverse, create a title and then begin adding data studies or create collections of data from other dataverses.</p>
                <p>Customize the layout and you are ready to release your dataverse live!</p>
            	<div class="stepCardBtm">
                     <h:outputText   value="&#160;" escape="false"/>             
             </div>
			</div>
        </div>
        <div id="requestIntroCont">
                  <h:commandLink  action="#{LoginWorkflowBean.beginContributorWorkflow}">
                                <h:outputText value="Continue" escape="false"/>
                  </h:commandLink>
                  <br></br>
                   <h:commandLink rendered="#{VDCSession.loginBean==null }" action="#{LoginWorkflowBean.beginLoginContributorWorkflow}">
                                <h:outputText value="If you already have an account, login and continue" escape="false"/>
                  </h:commandLink>

            <!--a href="#" id="requestIntroContLink">Start. Create Your Own Dataverse /a-->
            </div>
        </div>
    </div>

        </ui:form>
    </f:subview>
</jsp:root>
