<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
    xmlns:h="http://java.sun.com/jsf/html" 
    xmlns:jsp="http://java.sun.com/JSP/Page" 
    xmlns:ui="http://www.sun.com/web/ui"
    xmlns:tiles="http://struts.apache.org/tags-tiles"
    xmlns:c="http://java.sun.com/jsp/jstl/core">
    
    <f:subview id="termsOfUsePageView">
 
        <ui:form  id="form1">
            <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/> 
            <input type="hidden" value="TermsOfUsePage" name="pageName"/>
            <h:inputHidden id="studyId" value="#{TermsOfUsePage.studyId}"/>
            <h:inputHidden id="redirectPage" value="#{TermsOfUsePage.redirectPage}"/>
            
             <ui:panelLayout panelLayout="flow" styleClass="vdcSectionMiddleFixed" style="width: 600px">
               
                                    
               <ui:panelLayout  panelLayout="flow" >
                                        
            <ui:panelGroup block="true" id="termsDiv" style="background-color:#eeeeee; padding-left: 40px; padding-top: 30px; padding-bottom: 30px;  padding-right: 20px;">
                <ui:panelLayout   panelLayout="flow" style="padding-bottom: 20px;">
                     <h:outputText styleClass="warnMessage" value="Please agree to the terms of use below before accessing the files for study: #{TermsOfUsePage.study.title}"/>
               </ui:panelLayout>
               
                <h:panelGrid  cellpadding="0" cellspacing="0" columns="1" columnClasses="vdcTermsUseColumn" rendered="#{TermsOfUsePage.vdcTermsRequired}">
                    <ui:panelGroup  block="true">
                        <h:outputText styleClass="vdcFieldTitle" value="Dataverse Terms of Use:" />
                        <h:selectBooleanCheckbox id="vdcTermsAccepted" value="#{TermsOfUsePage.vdcTermsAccepted}"/>
                    </ui:panelGroup>
                    <h:outputText value="#{TermsOfUsePage.study.owner.termsOfUse}" escape="false"/>
                </h:panelGrid>
                <h:panelGrid  cellpadding="0" cellspacing="0" columns="1" columnClasses="vdcTermsUseColumn" rendered="#{TermsOfUsePage.studyTermsRequired}">
                    <ui:panelGroup  block="true">
                        <h:outputText styleClass="vdcFieldTitle" value="Study Terms of Use:" />
                        <h:selectBooleanCheckbox id="studyTermsAccepted" value="#{TermsOfUsePage.studyTermsAccepted}"/>
                    </ui:panelGroup>
                    <h:outputText value="Data Access Place:" styleClass="vdcTermsUseField" rendered="#{!empty TermsOfUsePage.study.placeOfAccess}"/>
                    <h:outputText  value="#{TermsOfUsePage.study.placeOfAccess}"  rendered="#{!empty TermsOfUsePage.study.placeOfAccess}" escape="false"/>
                    <h:outputText  value="Original Archive:" styleClass="vdcTermsUseField" rendered="#{!empty TermsOfUsePage.study.originalArchive}"/>
                    <h:outputText  value="#{TermsOfUsePage.study.originalArchive}"  rendered="#{!empty TermsOfUsePage.study.originalArchive}" escape="false"/>
                    <h:outputText  value="Availability Status:" styleClass="vdcTermsUseField" rendered="#{!empty TermsOfUsePage.study.availabilityStatus }"/>
                    <h:outputText  value="#{TermsOfUsePage.study.availabilityStatus}"  rendered="#{!empty TermsOfUsePage.study.availabilityStatus}" escape="false"/>
                    <h:outputText  value="Size of Collection:" styleClass="vdcTermsUseField" rendered="#{!empty TermsOfUsePage.study.collectionSize}" />
                    <h:outputText  value="#{TermsOfUsePage.study.collectionSize}"  rendered="#{!empty TermsOfUsePage.study.collectionSize}" escape="false"/>
                    <h:outputText  value="Study Completion:" styleClass="vdcTermsUseField" rendered="#{!empty TermsOfUsePage.study.studyCompletion}"/>
                    <h:outputText  value="#{TermsOfUsePage.study.studyCompletion}"  rendered="#{!empty TermsOfUsePage.study.studyCompletion}" escape="false"/>
                    <h:outputText  value="Number of Files:" styleClass="vdcTermsUseField" rendered="#{!empty TermsOfUsePage.study.numberOfFiles}"/>
                    <h:outputText  value="#{TermsOfUsePage.study.numberOfFiles}" rendered="#{!empty TermsOfUsePage.study.numberOfFiles}" escape="false"/>
                    <h:outputText  value="Confidentiality Declaration:" styleClass="vdcTermsUseField" rendered="#{!empty TermsOfUsePage.study.confidentialityDeclaration}"/>
                    <h:outputText  value="#{TermsOfUsePage.study.confidentialityDeclaration}" rendered="#{!empty TermsOfUsePage.study.confidentialityDeclaration}" escape="false"/>
                    <h:outputText  value="Special Permissions:" styleClass="vdcTermsUseField" rendered="#{!empty TermsOfUsePage.study.specialPermissions}"/>
                    <h:outputText  value="#{TermsOfUsePage.study.specialPermissions}" rendered="#{!empty TermsOfUsePage.study.specialPermissions}" escape="false"/>
                    <h:outputText  value="Restrictions:" styleClass="vdcTermsUseField" rendered="#{!empty TermsOfUsePage.study.restrictions}" />
                    <h:outputText  value="#{TermsOfUsePage.study.restrictions}" rendered="#{!empty TermsOfUsePage.study.restrictions}" escape="false"/>
                    <h:outputText  value="Contact:" styleClass="vdcTermsUseField" rendered="#{!empty TermsOfUsePage.study.contact}" />
                    <h:outputText  value="#{TermsOfUsePage.study.contact}" rendered="#{!empty tudyPage.study.contact}" escape="false"/>
                    <h:outputText  value="Citation Requirements:" styleClass="vdcTermsUseField" rendered="#{!empty TermsOfUsePage.study.citationRequirements}"/>
                    <h:outputText  value="#{TermsOfUsePage.study.citationRequirements}" rendered="#{!empty TermsOfUsePage.study.citationRequirements}" escape="false"/>
                    <h:outputText  value="Depositor Requirements:" styleClass="vdcTermsUseField" rendered="#{!empty TermsOfUsePage.study.depositorRequirements}"/>
                    <h:outputText  value="#{TermsOfUsePage.study.depositorRequirements}" rendered="#{!empty TermsOfUsePage.study.depositorRequirements}" escape="false"/>
                    <h:outputText  value="Conditions:" styleClass="vdcTermsUseField"  rendered="#{!empty TermsOfUsePage.study.conditions}"/>
                    <h:outputText  value="#{TermsOfUsePage.study.conditions}" rendered="#{!empty TermsOfUsePage.study.conditions}" escape="false"/>
                    <h:outputText  value="Disclaimer:" styleClass="vdcTermsUseField" rendered="#{!empty TermsOfUsePage.study.disclaimer}"/>
                    <h:outputText  value="#{TermsOfUsePage.study.disclaimer}" rendered="#{!empty TermsOfUsePage.study.disclaimer}" escape="false"/>
                </h:panelGrid>   
                
                <ui:panelGroup block="true" style="padding-top: 20px; text-align: center">
                    <h:commandButton  id="termsButton" value="Accept" action="#{TermsOfUsePage.acceptTerms_action}"/>
                </ui:panelGroup>
            </ui:panelGroup> 
            
            </ui:panelLayout>
                                    
                            </ui:panelLayout>
        </ui:form>
    </f:subview>
</jsp:root>
