<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles">
    <f:subview id="EditAccountPageView">
        <ui:form  id="form1">
           <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
           <input type="hidden" name="pageName" value="EditAccountPage"/>
           <div class="dvn_section">
               <div class="dvn_sectionTitle">
                
                       <h:outputText value="Edit Dataverse Network Account"/>
                 
               </div>            
               <div class="dvn_sectionBox"> 
                   <div class="dvn_margin12"> 
                       
                       <h:panelGrid  cellpadding="0" cellspacing="0"
                                     columnClasses="vdcColPadded, vdcColPadded" columns="2" id="gridPanel2">
                           <ui:panelGroup >
                               <h:outputText   value="Username"/>
                               <h:graphicImage  id="image1" value="/resources/icon_required.gif"/>
                           </ui:panelGroup>
                           <ui:panelGroup>
                               <h:inputText immediate="true" id="inputUserName" validator ="#{EditAccountPage.validateUserName}" value="#{EditAccountPage.user.userName}" required="true" requiredMessage="This field is required.">
                               </h:inputText>
                               <h:message styleClass="errorMessage" for="inputUserName"/>
                           </ui:panelGroup>
                           <ui:panelGroup >
                               <h:outputText  value="First Name"/>
                               <h:graphicImage  value="/resources/icon_required.gif"/>
                           </ui:panelGroup>
                           <ui:panelGroup>
                               <h:inputText id="inputFirstName" value="#{EditAccountPage.user.firstName}" required="true" requiredMessage="This field is required."/> 
                               <h:message styleClass="errorMessage" for="inputFirstName"/>
                           </ui:panelGroup>
                           
                           
                           <ui:panelGroup >
                               <h:outputText  value="Last Name"/>
                               <h:graphicImage  value="/resources/icon_required.gif"/>
                           </ui:panelGroup>
                           <ui:panelGroup>
                               <h:inputText id="inputLastName" value="#{EditAccountPage.user.lastName}" required="true" requiredMessage="This field is required."/> 
                               <h:message styleClass="errorMessage" for="inputLastName"/>
                           </ui:panelGroup>
                           
                           <ui:panelGroup >
                               <h:outputText  value="E-Mail"/>
                               <h:graphicImage  value="/resources/icon_required.gif"/>
                           </ui:panelGroup>
                           <ui:panelGroup>
                               <h:inputText id="inputEmail" value="#{EditAccountPage.user.email}" size="40" required="true" requiredMessage="This field is required.">
                                   <f:validator validatorId="EmailValidator"/>
                               </h:inputText>
                               <h:message styleClass="errorMessage" for="inputEmail"/>
                           </ui:panelGroup>
                           
                           <ui:panelGroup>    
                               <h:outputText value="Institution"/>
                           </ui:panelGroup>
                           <h:inputText id="inputInstitution" value="#{EditAccountPage.user.institution}"  size="40"/>
                           
                           <ui:panelGroup >
                               <h:outputText  value="Position"/>
                           </ui:panelGroup>
                           
                           <h:selectOneMenu value="#{EditAccountPage.user.position}" >
                               <f:selectItem itemLabel="Student" itemValue="Student" />
                               <f:selectItem itemLabel="Faculty" itemValue="Faculty" />
                               <f:selectItem itemLabel="Staff" itemValue="Staff" />
                               <f:selectItem itemLabel="Other" itemValue="Other" />                                            
                           </h:selectOneMenu>
                           
                           <ui:panelGroup >
                               <h:outputText  value="Phone Number"/>
                           </ui:panelGroup>
                           <h:inputText  id="inputPhoneNumber" value="#{EditAccountPage.user.phoneNumber}"  />
                           
                       </h:panelGrid>
                          
                       
                       <ui:panelGroup block="true"  style="padding-left: 100px; padding-top: 20px">
                           <h:commandButton  value="Save" action="#{EditAccountPage.save}"/>
                       </ui:panelGroup>
                       
                   </div>
               </div>
           </div>
                        
        </ui:form>
    </f:subview>
</jsp:root>
