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
                           <h:outputText rendered="#{TermsOfUsePage.touTypeDeposit and TermsOfUsePage.studyId!=null}" value="Please agree to the terms of use below before editing study: "/>
                           <h:outputText styleClass="warnMessage" value="#{TermsOfUsePage.study.title}"/>
                           <h:outputText rendered="#{TermsOfUsePage.studyId==null}" value="Please agree to the terms of use below before creating a new study."/>
                       </ui:panelGroup>
                       <ui:panelLayout rendered="#{TermsOfUsePage.studyId!=null and TermsOfUsePage.touTypeDownload}" style="padding: .5em 0;">
                            <h:outputText value="Once you have been prompted to download the file, you may return "/>
                            <h:outputLink value="/dvn#{VDCRequest.currentVDCURL}/faces/study/StudyPage.jsp?studyId=#{TermsOfUsePage.study.id}&amp;tab=files">
                                <h:outputText value="back to the Study"/>
                            </h:outputLink>
                            <h:outputText value="."/>
                       </ui:panelLayout>
                        <ui:panelGroup styleClass="termsAgreementBox" block="true">
                            <ui:panelLayout rendered="#{TermsOfUsePage.touTypeDownload}">
                                   
                                <h:outputText value="Dataverse Network Terms of Use" styleClass="termsAgreementMessageTitle" rendered="#{TermsOfUsePage.downloadDvnTermsRequired}" />
                                <h:outputText value="#{TermsOfUsePage.vdcNetwork.downloadTermsOfUse}" rendered="#{TermsOfUsePage.downloadDvnTermsRequired}" escape="false"/>
                             
                                <h:outputText value="Dataverse Terms of Use" styleClass="termsAgreementMessageTitle" rendered="#{TermsOfUsePage.downloadDataverseTermsRequired}" />
                                <h:outputText value="#{TermsOfUsePage.study.owner.downloadTermsOfUse}" rendered="#{TermsOfUsePage.downloadDataverseTermsRequired}" escape="false"/>
                                
                                 <h:outputText value="Original Dataverse Network Terms of Use" styleClass="termsAgreementMessageTitle" rendered="#{!empty TermsOfUsePage.study.harvestDVNTermsOfUse}"/>
                                <h:outputText value="#{TermsOfUsePage.study.harvestDVNTermsOfUse}" rendered="#{!empty TermsOfUsePage.study.harvestDVNTermsOfUse}" escape="false"/>
                             
                                <h:outputText value="Original Dataverse Terms of Use" styleClass="termsAgreementMessageTitle" rendered="#{!empty TermsOfUsePage.study.harvestDVTermsOfUse}" />
                                <h:outputText value="#{TermsOfUsePage.study.harvestDVTermsOfUse}" rendered="#{!empty TermsOfUsePage.study.harvestDVTermsOfUse}"  escape="false"/>
                              
                             
                                <h:outputText value="Study Terms of Use" styleClass="termsAgreementMessageTitle" rendered="#{TermsOfUsePage.study.termsOfUseEnabled}" />
                                    
                                <h:outputText value="Confidentiality Declaration" styleClass="termsAgreementMessageTitle" rendered="#{!empty TermsOfUsePage.study.confidentialityDeclaration}"/>     
                                <h:outputText value="#{TermsOfUsePage.study.confidentialityDeclaration}" rendered="#{!empty TermsOfUsePage.study.confidentialityDeclaration}" escape="false"/>
                                
                                <h:outputText value="Special Permissions" styleClass="termsAgreementMessageTitle" rendered="#{!empty TermsOfUsePage.study.specialPermissions}"/>
                                <h:outputText value="#{TermsOfUsePage.study.specialPermissions}" rendered="#{!empty TermsOfUsePage.study.specialPermissions}" escape="false"/>
                                
                                <h:outputText value="Restrictions" styleClass="termsAgreementMessageTitle" rendered="#{!empty TermsOfUsePage.study.restrictions}" />
                                <h:outputText value="#{TermsOfUsePage.study.restrictions}" rendered="#{!empty TermsOfUsePage.study.restrictions}" escape="false"/>
                                
                                <h:outputText value="Contact" styleClass="termsAgreementMessageTitle" rendered="#{!empty TermsOfUsePage.study.contact}" />
                                <h:outputText value="#{TermsOfUsePage.study.contact}" rendered="#{!empty TermsOfUsePage.study.contact}" escape="false"/>
                                
                                <h:outputText value="Citation Requirements" styleClass="termsAgreementMessageTitle" rendered="#{!empty TermsOfUsePage.study.citationRequirements}"/>
                                <h:outputText value="#{TermsOfUsePage.study.citationRequirements}" rendered="#{!empty TermsOfUsePage.study.citationRequirements}" escape="false"/>
                                
                                <h:outputText value="Depositor Requirements" styleClass="termsAgreementMessageTitle" rendered="#{!empty TermsOfUsePage.study.depositorRequirements}"/>
                                <h:outputText value="#{TermsOfUsePage.study.depositorRequirements}" rendered="#{!empty TermsOfUsePage.study.depositorRequirements}" escape="false"/>
                                
                                <h:outputText value="Conditions" styleClass="termsAgreementMessageTitle"  rendered="#{!empty TermsOfUsePage.study.conditions}"/>
                                <h:outputText value="#{TermsOfUsePage.study.conditions}" rendered="#{!empty TermsOfUsePage.study.conditions}" escape="false"/>
                                
                                <h:outputText value="Disclaimer" styleClass="termsAgreementMessageTitle" rendered="#{!empty TermsOfUsePage.study.disclaimer}"/>
                                <h:outputText value="#{TermsOfUsePage.study.disclaimer}" rendered="#{!empty TermsOfUsePage.study.disclaimer}" escape="false"/>
                          
                            </ui:panelLayout>
                            
                            <ui:panelLayout rendered="#{TermsOfUsePage.touTypeDeposit}">
                                <h:outputText value="Dataverse Network Deposit Terms of Use" styleClass="termsAgreementMessageTitle" rendered="#{TermsOfUsePage.depositDvnTermsRequired}" />
                                <h:outputText value="#{TermsOfUsePage.vdcNetwork.depositTermsOfUse}" rendered="#{TermsOfUsePage.depositDvnTermsRequired}" escape="false"/>
                                
                                <h:outputText value="Dataverse Deposit Terms of Use" styleClass="termsAgreementMessageTitle" rendered="#{TermsOfUsePage.depositDataverseTermsRequired}" />
                                <h:outputText value="#{VDCRequest.currentVDC.depositTermsOfUse}" rendered="#{TermsOfUsePage.depositDataverseTermsRequired}" escape="false"/>
                             </ui:panelLayout>
                        </ui:panelGroup>
                        
                         <ui:panelGroup block="true">
                            <h:selectBooleanCheckbox id="termsAccepted"  required="true" validator="#{TermsOfUsePage.validateTermsAccepted}"  value="#{TermsOfUsePage.termsAccepted}" />
                            <h:outputText styleClass="vdcFieldTitle" value="I agree and accept these terms of use." />
                            <h:message for="termsAccepted" styleClass="errorMessage"/>

                        </ui:panelGroup>
                        
                        <ui:panelGroup block="true" styleClass="termsAgreementButtons">
                            <h:commandButton id="termsButton" value="Continue" action="#{TermsOfUsePage.acceptTerms_action}"/>
                            <f:verbatim rendered="#{TermsOfUsePage.studyId!=null}">
                                <input id="cancelButton" type="button" value="Cancel" style="margin-left: 20px;" onclick="window.location='/dvn${VDCRequest.currentVDCURL}/faces/study/StudyPage.jsp?studyId=${TermsOfUsePage.study.id}&amp;tab=files'" />
                            </f:verbatim>
                        </ui:panelGroup>
                    </div>
                </div>
            </div>
        </ui:form>
    </f:subview>
</jsp:root>
