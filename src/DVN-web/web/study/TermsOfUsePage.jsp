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
            <h:inputHidden id="tou" value="#{TermsOfUsePage.touParam}"/>
        
            <div class="dvn_section">
                <div class="dvn_sectionTitle">
                    Terms of Use
                </div>
                
                <div class="dvn_sectionBox">
                    <div class="dvn_margin12">
                        
                        <ui:panelGroup block="true" styleClass="termsAgreementMessage">
                           <h:outputText rendered="#{TermsOfUsePage.touTypeDownload}" value="Please agree to the terms of use below before accessing the files for study: "/>
                           <h:outputText rendered="#{TermsOfUsePage.touTypeDeposit}" value="Please agree to the terms of use below before creating or editing a study: "/>
                           <h:outputText styleClass="warnMessage" value="#{TermsOfUsePage.study.title}"/>
                        </ui:panelGroup>
                        <ui:panelGroup styleClass="termsAgreementBox" block="true">
                            <ui:panelLayout rendered="#{TermsOfUsePage.touTypeDownload}">
                                   
                                <h:outputText value="Dataverse Network Terms of Use:" styleClass="vdcTermsUseField" rendered="#{TermsOfUsePage.downloadDvnTermsRequired}" />
                                <h:outputText value="(These Terms of Use apply to all studies in this Dataverse Network)" rendered="#{TermsOfUsePage.downloadDvnTermsRequired}" />
                                <h:outputText value="#{TermsOfUsePage.vdcNetwork.downloadTermsOfUse}" rendered="#{TermsOfUsePage.downloadDvnTermsRequired}" escape="false"/>
                             
                                <h:outputText value="Dataverse Terms of Use:" styleClass="vdcTermsUseField" rendered="#{TermsOfUsePage.downloadDataverseTermsRequired}" />
                          
                                <h:outputText value="(These Terms of Use apply to all studies owned by the original dataverse)" rendered="#{TermsOfUsePage.downloadDataverseTermsRequired}" />
                      
                                <h:outputText value="#{TermsOfUsePage.study.owner.downloadTermsOfUse}" rendered="#{TermsOfUsePage.downloadDataverseTermsRequired}" escape="false"/>
                             
                                <h:outputText value="Confidentiality Declaration:" styleClass="vdcTermsUseField" rendered="#{!empty TermsOfUsePage.study.confidentialityDeclaration}"/>
                                
                                <h:outputText value="#{TermsOfUsePage.study.confidentialityDeclaration}" rendered="#{!empty TermsOfUsePage.study.confidentialityDeclaration}" escape="false"/>
                                
                                <h:outputText value="Special Permissions:" styleClass="vdcTermsUseField" rendered="#{!empty TermsOfUsePage.study.specialPermissions}"/>
                                
                                <h:outputText value="#{TermsOfUsePage.study.specialPermissions}" rendered="#{!empty TermsOfUsePage.study.specialPermissions}" escape="false"/>
                                
                                <h:outputText value="Restrictions:" styleClass="vdcTermsUseField" rendered="#{!empty TermsOfUsePage.study.restrictions}" />
                                
                                <h:outputText value="#{TermsOfUsePage.study.restrictions}" rendered="#{!empty TermsOfUsePage.study.restrictions}" escape="false"/>
                                
                                <h:outputText value="Contact:" styleClass="vdcTermsUseField" rendered="#{!empty TermsOfUsePage.study.contact}" />
                                
                                <h:outputText value="#{TermsOfUsePage.study.contact}" rendered="#{!empty TermsOfUsePage.study.contact}" escape="false"/>
                                
                                <h:outputText value="Citation Requirements:" styleClass="vdcTermsUseField" rendered="#{!empty TermsOfUsePage.study.citationRequirements}"/>
                                
                                <h:outputText value="#{TermsOfUsePage.study.citationRequirements}" rendered="#{!empty TermsOfUsePage.study.citationRequirements}" escape="false"/>
                                
                                <h:outputText value="Depositor Requirements:" styleClass="vdcTermsUseField" rendered="#{!empty TermsOfUsePage.study.depositorRequirements}"/>
                                
                                <h:outputText value="#{TermsOfUsePage.study.depositorRequirements}" rendered="#{!empty TermsOfUsePage.study.depositorRequirements}" escape="false"/>
                                
                                <h:outputText value="Conditions:" styleClass="vdcTermsUseField"  rendered="#{!empty TermsOfUsePage.study.conditions}"/>
                                
                                <h:outputText value="#{TermsOfUsePage.study.conditions}" rendered="#{!empty TermsOfUsePage.study.conditions}" escape="false"/>
                                
                                <h:outputText value="Disclaimer:" styleClass="vdcTermsUseField" rendered="#{!empty TermsOfUsePage.study.disclaimer}"/>
                                
                                <h:outputText value="#{TermsOfUsePage.study.disclaimer}" rendered="#{!empty TermsOfUsePage.study.disclaimer}" escape="false"/>
                          
                            </ui:panelLayout>
                            
                            <ui:panelLayout rendered="#{TermsOfUsePage.touTypeDeposit}">
                                <h:outputText value="Dataverse Network Deposit Terms of Use:" styleClass="vdcTermsUseField" rendered="#{TermsOfUsePage.depositDvnTermsRequired}" />
                                <h:outputText value="(These Terms of Use apply to all studies created in this Dataverse Network)" rendered="#{TermsOfUsePage.depositDvnTermsRequired}" />
                                <h:outputText value="#{TermsOfUsePage.vdcNetwork.depositTermsOfUse}" rendered="#{TermsOfUsePage.depositDvnTermsRequired}" escape="false"/>
                                
                                <h:outputText value="Dataverse Deposit Terms of Use:" styleClass="vdcTermsUseField" rendered="#{TermsOfUsePage.depositDataverseTermsRequired}" />
                                <h:outputText value="(These Terms of Use apply to all studies created in this dataverse)" rendered="#{TermsOfUsePage.depositDataverseTermsRequired}" />
                                <h:outputText value="#{VDCRequest.currentVDC.depositTermsOfUse}" rendered="#{TermsOfUsePage.depositDataverseTermsRequired}" escape="false"/>
                             </ui:panelLayout>                
                        </ui:panelGroup>  
                        
                         <ui:panelGroup block="true">
                            <h:selectBooleanCheckbox id="termsAccepted" value="#{TermsOfUsePage.termsAccepted}" />
                            <h:outputText styleClass="vdcFieldTitle" value="I agree and accept these terms of use." />
                        </ui:panelGroup>
                        
                        <ui:panelGroup block="true" styleClass="termsAgreementButtons">
                            <h:commandButton id="termsButton" value="Continue" action="#{TermsOfUsePage.acceptTerms_action}"/>
                            <f:verbatim rendered="#{TermsOfUsePage.studyId!=null}">
                                <input id="cancelButton" type="button" value="Cancel" style="margin-left: 20px;" onclick="window.location='/dvn${VDCRequest.currentVDCURL}/faces/study/StudyPage.jsp?studyId=${TermsOfUsePage.study.id}&amp;tab=files'" />
                            </f:verbatim>
                        </ui:panelGroup>
                       <ui:panelLayout rendered="#{TermsOfUsePage.studyId!=null}" style="padding-top: 20px;">
                            <h:outputLink value="/dvn#{VDCRequest.currentVDCURL}/faces/study/StudyPage.jsp?studyId=#{TermsOfUsePage.study.id}&amp;tab=files">
                                <h:outputText value="Back to Study"/>                       
                            </h:outputLink>
                       </ui:panelLayout>
                    </div>
                </div>
            </div>
        </ui:form>
    </f:subview>
</jsp:root>
