<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:gui="http://java.sun.com/jsf/facelets"
      xmlns:h="http://java.sun.com/jsf/html"
      xmlns:f="http://java.sun.com/jsf/core" 
      xmlns:jsp="http://java.sun.com/JSP/Page"
      xmlns:ui="http://www.sun.com/web/ui"
      xmlns:dvn="/WEB-INF/tlds/dvn-components"
      >
<head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>

</head>

<body>
<gui:composition template="/template.xhtml">

<gui:param name="pageTitle" value="DVN - Study" />

  <gui:define name="body">
    <f:loadBundle basename="StudyBundle" var="studybundle"/>

     <dvn:inlinehelpsupport writeHelpDiv="true" writeTipDiv="true" writePopupDiv="true" rendered="true"/>
        <ui:form id="form1">
            <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
            <input type="hidden" value="StudyPage" name="pageName"/>
            <h:inputHidden id="studyId" value="#{studyPage.studyId}"/>
            <h:inputHidden id="slIndex" value="#{studyPage.studyListingIndex}"/>
                        
                <div class="dvn_section">       
                    <div class="dvn_sectionTitle">
                        <h:outputText value="#{studyPage.studyUI.study.title}"/>
                    </div>

                    <ui:panelGroup styleClass="dvn_sectionTitleLinksL" rendered="#{!empty studyPage.studyListingIndex}"> 
                      <h:outputLink value="/dvn#{VDCRequest.currentVDCURL}/faces/SearchPage.jsp?studyListingIndex=#{studyPage.studyListingIndex}">
                          <h:outputText value="&lt; View Previous Study Listing"/>
                      </h:outputLink>
                    </ui:panelGroup>

                    <ui:panelGroup styleClass="dvn_sectionTitleLinksR" rendered="#{studyPage.userAuthorizedToEdit or studyPage.studyUI.study.inReview or studyPage.studyUI.study.new}">
                        <h:outputText styleClass="vdcStudyStatus" value=" (In Review) " rendered="#{studyPage.studyUI.study.inReview}"/>
                        <h:outputText styleClass="vdcStudyStatus" value=" (New) " rendered="#{studyPage.studyUI.study.new}"/>
                        <h:outputText styleClass="vdcStudyStatus" value=" Currently unavailable for editing because file upload is in progress." rendered="#{studyPage.userAuthorizedToEdit and !(studyPage.studyUI.study.studyLock==null)}"/>

                        <ui:panelGroup styleClass="dvn_tooltip" rendered="#{studyPage.userAuthorizedToEdit and studyPage.studyUI.study.studyLock==null}">
                            <ui:script type="text/javascript">
                                <![CDATA[
                                    $('.dvn_tooltip a').tooltip({
                                            track: true,
                                            delay: 0,
                                            showURL: false,
                                            showBody: " - "
                                    });
                                ]]>
                            </ui:script>
                            
                            <dvn:tooltip tooltipMessage="#{studybundle.editTip}" linkText="#{studybundle.editText}" eventType="mouseover" linkUrl="/dvn#{VDCRequest.currentVDCURL}/faces/study/EditStudyPage.jsp?studyId=#{studyPage.studyUI.study.id}&amp;tab=#{studyPage.tabSet1.selected}" cssClass="vdcTooltipLink"/>

                            <dvn:tooltip tooltipMessage="#{studybundle.addTip}" linkText="#{studybundle.addText}"  eventType="mouseover" linkUrl="/dvn#{VDCRequest.currentVDCURL}/faces/study/AddFilesPage.jsp?studyId=#{studyPage.studyUI.study.id}" cssClass="vdcTooltipLink"/>

                            <dvn:tooltip tooltipMessage="#{studybundle.permissionsTip}" linkText="#{studybundle.permissionsText}"  eventType="mouseover" linkUrl="/dvn#{VDCRequest.currentVDCURL}/faces/study/StudyPermissionsPage.jsp?studyId=#{studyPage.studyUI.study.id}" cssClass="vdcTooltipLink"/>

                            <dvn:tooltip tooltipMessage="#{studybundle.deleteTip}" linkText="#{studybundle.deleteText}"  eventType="mouseover" linkUrl="/dvn#{VDCRequest.currentVDCURL}/faces/study/DeleteStudyPage.jsp?studyId=#{studyPage.studyUI.study.id}" cssClass="vdcTooltipLink"/>
                            
                            <ui:panelGroup rendered="#{studyPage.userAuthorizedToRelease}">
                                <dvn:tooltip tooltipMessage="#{studybundle.templateTip}" linkText="#{studybundle.templateText}"  eventType="mouseover" linkUrl="/dvn#{VDCRequest.currentVDCURL}/faces/study/TemplateFormPage.jsp?studyId=#{studyPage.studyUI.study.id}" cssClass="vdcTooltipLink"/>
                            </ui:panelGroup>
                            <h:commandButton rendered="#{studyPage.studyUI.study.new}" value="Ready for Review" actionListener="#{studyPage.setReadyForReview}"/>
                        </ui:panelGroup>
                        <ui:panelGroup rendered="#{studyPage.userAuthorizedToRelease and studyPage.studyUI.study.studyLock==null and studyPage.studyUI.study.inReview}">                              
                            <h:commandButton  value="Release" actionListener="#{studyPage.setReleased}"/>
                        </ui:panelGroup>
                    
                    </ui:panelGroup>           
             
                <div class="dvn_sectionBoxNoBorders"> 
                    <ui:tabSet binding="#{studyPage.tabSet1}" id="tabSet1" lite="true" mini="true"  >
                        <ui:tab id="catalog" text="Cataloging Information" url="#{VDCRequest.currentVDCURL}/faces/study/StudyPage.jsp?studyId=#{studyPage.studyUI.study.id}&amp;tab=catalog#{studyPage.studyListingIndexAsParameter}">
                            <ui:panelLayout id="layoutPanel1" panelLayout="flow" style="width: 98%"> 
                            
                                <ui:panelGroup block="true" id="groupPanel11" styleClass="vdcStudyInfoHeader" style="margin-top: 5px;">
                                    <h:outputText id="outputText11" value="Citation Information"/>
                                    <h:commandButton id="commandButtonCitationInfoContract" image="/resources/icon_expand.gif" title="Hide fields in this section" rendered="#{studyPage.studyUI.citationInformationPanelIsRendered}" actionListener="#{studyPage.updateCitationInfoDisplay}" />                      
                                    <h:commandButton id="commandButtonCitationInfoExpand" image="/resources/icon_contract.gif" title="Show fields in this section" rendered="#{!studyPage.studyUI.citationInformationPanelIsRendered}" actionListener="#{studyPage.updateCitationInfoDisplay}"/>    
                                </ui:panelGroup>
                                <h:panelGrid binding="#{studyPage.citationInformationPanel}" id="panelGridCitationInfo" cellpadding="0" cellspacing="0" 
                                             columnClasses="vdcStudyInfoCol1, vdcStudyInfoCol2" columns="2" width="100%" rendered="#{studyPage.studyUI.citationInformationPanelIsRendered}" > 
                                    <dvn:inlinehelp helpMessage="#{studybundle.howCiteHelp}"  linkText="#{studybundle.howCiteLabel}" heading="#{studybundle.howCiteHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink"/>
                                    <ui:panelGroup  block="true" styleClass="vdcCitationText" >
                                        <h:outputText value="#{studyPage.studyUI.study.citation}" escape="false"/>
                                    </ui:panelGroup>
                                    <dvn:inlinehelp helpMessage="#{studybundle.subtitleHelp}"  linkText="#{studybundle.subtitleLabel}" heading="#{studybundle.subtitleHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.subTitle!='' and studyPage.studyUI.study.subTitle!=null}"/>
                                    <h:outputText id="text1" value="#{studyPage.studyUI.study.subTitle}" rendered="#{!empty studyPage.studyUI.study.subTitle}"/>
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.studyGlobalIdHelp}"  linkText="#{studybundle.studyGlobalIdLabel}" heading="#{studybundle.studyGlobalIdHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink"/>
                                    <h:outputText id="text2" value="#{studyPage.studyUI.study.globalId}" />
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.otheridHelp}"  linkText="#{studybundle.otheridLabel}" heading="#{studybundle.otheridHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.otherIds != ''}"/>
                                    <h:outputText id="text3" value="#{studyPage.studyUI.otherIds}" rendered="#{!empty studyPage.studyUI.otherIds}"/>
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.authorHelp}"  linkText="#{studybundle.authorLabel}" heading="#{studybundle.authorHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.authorAffiliations != ''}" />
                                    <h:outputText id="text4"  value="#{studyPage.studyUI.authorAffiliations}"  rendered="#{!empty studyPage.studyUI.authorAffiliations}" escape="false"/>
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.producerHelp}"  linkText="#{studybundle.producerLabel}" heading="#{studybundle.producerHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.producers != ''}" />
                                    <h:outputText id="text5"  value="#{studyPage.studyUI.producers}" rendered="#{!empty studyPage.studyUI.producers}" escape="false" />
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.productionDateHelp}"  linkText="#{studybundle.productionDateLabel}" heading="#{studybundle.productionDateHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.productionDate != '' and studyPage.studyUI.study.productionDate != null}" />
                                    <h:outputText id="text6"  value="#{studyPage.studyUI.productionDate}" rendered="#{!empty studyPage.studyUI.study.productionDate}" />
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.productionPlaceHelp}"  linkText="#{studybundle.productionPlaceLabel}" heading="#{studybundle.productionPlaceHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.productionPlace != '' and studyPage.studyUI.study.productionPlace != null }" />
                                    <h:outputText id="text7"  value="#{studyPage.studyUI.study.productionPlace}" rendered="#{!empty studyPage.studyUI.study.productionPlace}" escape="false"/>
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.softwareHelp}"  linkText="#{studybundle.softwareLabel}" heading="#{studybundle.softwareHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.software != '' and studyPage.studyUI.software != null}"/>  
                                    <h:outputText id="text8"  value="#{studyPage.studyUI.software}" rendered="#{!empty studyPage.studyUI.software}" escape="false"/>
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.fundingAgencyHelp}"  linkText="#{studybundle.fundingAgencyLabel}" heading="#{studybundle.fundingAgencyHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.fundingAgency != '' and studyPage.studyUI.study.fundingAgency != null }" />
                                    <h:outputText id="text9"  value="#{studyPage.studyUI.study.fundingAgency}" rendered="#{!empty studyPage.studyUI.study.fundingAgency}" escape="false"/>
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.grantNumberHelp}"  linkText="#{studybundle.grantNumberLabel}" heading="#{studybundle.grantNumberHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.grants != ''}" />
                                    <h:outputText id="text10"  value="#{studyPage.studyUI.grants}" rendered="#{!empty studyPage.studyUI.grants}" />
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.distributorHelp}"  linkText="#{studybundle.distributorLabel}" heading="#{studybundle.distributorHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.distributors != ''}"/>
                                    <h:outputText id="text11" value="#{studyPage.studyUI.distributors}" rendered="#{!empty studyPage.studyUI.distributors}" escape="false"/>
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.distributorContactHelp}"  linkText="#{studybundle.distributorContactLabel}" heading="#{studybundle.distributorContactHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.distributorContact != ''  }"/>
                                    <h:outputText id="text12" value="#{studyPage.studyUI.distributorContact}" rendered="#{!empty studyPage.studyUI.distributorContact}"  escape="false"/>
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.distributionDateHelp}"  linkText="#{studybundle.distributionDateLabel}" heading="#{studybundle.distributionDateHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.distributionDate != '' and studyPage.studyUI.study.distributionDate != null }"/>
                                    <h:outputText id="text13" value="#{studyPage.studyUI.distributionDate}" rendered="#{!empty studyPage.studyUI.study.distributionDate}"/>
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.depositorHelp}"  linkText="#{studybundle.depositorLabel}" heading="#{studybundle.depositorHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.depositor != '' and studyPage.studyUI.study.depositor != null }"/>
                                    <h:outputText  id="text14" value="#{studyPage.studyUI.study.depositor}" rendered="#{!empty studyPage.studyUI.study.depositor}" />
                                    <dvn:inlinehelp helpMessage="#{studybundle.depositDateHelp}"  linkText="#{studybundle.depositDateLabel}" heading="#{studybundle.depositDateHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.dateOfDeposit != '' and studyPage.studyUI.study.dateOfDeposit != null }"/>
                                    <h:outputText id="text15"  value="#{studyPage.studyUI.dateOfDeposit}" rendered="#{!empty studyPage.studyUI.study.dateOfDeposit}"/>
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.seriesHelp}"  linkText="#{studybundle.seriesLabel}" heading="#{studybundle.seriesHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.series != '' }"/>
                                    <h:outputText  id="text16" value="#{studyPage.studyUI.series}" rendered="#{!empty studyPage.studyUI.series}" escape="false"/>
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.seriesVersionHelp}"  linkText="#{studybundle.seriesVersionLabel}" heading="#{studybundle.seriesVersionHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.studyVersion != '' }"/>
                                    <h:outputText id="text17"  value="#{studyPage.studyUI.studyVersion}" rendered="#{!empty studyPage.studyUI.studyVersion}"/>
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.replicationForHelp}"  linkText="#{studybundle.replicationForLabel}" heading="#{studybundle.replicationForHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.replicationFor != '' and studyPage.studyUI.study.replicationFor != null }"/>
                                    <h:outputText id="text18"  value="#{studyPage.studyUI.study.replicationFor}" rendered="#{!empty studyPage.studyUI.study.replicationFor}" escape="false" />
                                    
                                    <!--
                                    <dvn:inlinehelp helpMessage="#{studybundle.dataverseOwnerHelp}"  linkText="#{studybundle.dataverseOwnerLabel}" heading="#{studybundle.dataverseOwnerHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.owner.Id != VDCRequest.currentVDCId}" />  
                                    <h:outputLink value="/dvn/dv/#{studyPage.studyUI.study.owner.alias}" rendered="#{studyPage.studyUI.study.owner.Id != VDCRequest.currentVDCId}" >
                                        <h:outputText value="#{studyPage.studyUI.study.owner.name} Dataverse" />
                                    </h:outputLink> 
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.harvestHoldingsHelp}"  linkText="#{studybundle.harvestHoldingsLabel}" heading="#{studybundle.harvestHoldingsHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{ !empty studyPage.studyUI.study.harvestHoldings and studyPage.studyUI.study.isHarvested}"/>  
                                    <h:outputLink value="#{studyPage.studyUI.study.harvestHoldings}" rendered="#{!empty studyPage.studyUI.study.harvestHoldings and studyPage.studyUI.study.isHarvested}">
                                        <h:outputText value="#{studyPage.studyUI.study.harvestHoldings}" />
                                    </h:outputLink> 
                                    -->
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.provenanceHelp}"  linkText="#{studybundle.provenanceLabel}" heading="#{studybundle.provenanceHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink"  />  
                                    <ui:panelGroup block="true"> 
                                            <h:outputLink value="#{studyPage.studyUI.study.harvestHoldings}" rendered="#{!empty studyPage.studyUI.study.harvestHoldings and studyPage.studyUI.study.isHarvested}">
                                                <h:outputText value="Original Source > " />
                                            </h:outputLink>
                                        <h:outputLink value="/dvn/dv/#{studyPage.studyUI.study.owner.alias}/faces/study/StudyPage.jsp?studyId=#{studyPage.studyUI.study.id}&amp;tab=catalog" >
                                            <h:outputText value="#{studyPage.studyUI.study.owner.name} Dataverse" />
                                        </h:outputLink> 
                                    </ui:panelGroup>
                                </h:panelGrid>
                                
                                <ui:panelGroup block="true" id="groupPanel12" styleClass="vdcStudyInfoHeader" rendered="#{!studyPage.abstractAndScopePanelIsEmpty}" >
                                    <h:outputText   id="AbstractAndScopeText" value="Abstract and Scope"/>
                                    <h:commandButton id="commandButtonAbstractScopeContract"  image="/resources/icon_expand.gif" title="Hide fields in this section" rendered="#{studyPage.studyUI.abstractAndScopePanelIsRendered }" actionListener="#{studyPage.updateAbstractScopeDisplay}"/> 
                                    <h:commandButton id="commandButtonAbstractScopeExpand" image="/resources/icon_contract.gif" title="Show fields in this section"  rendered="#{!studyPage.studyUI.abstractAndScopePanelIsRendered }" actionListener="#{studyPage.updateAbstractScopeDisplay}"/> 
                                    <!--h:outputText   value="(This section is empty)" rendered="#{studyPage.abstractAndScopePanelIsEmpty}" /-->
                                </ui:panelGroup>
                                <h:panelGrid   binding="#{studyPage.abstractAndScopePanel}"  cellpadding="0" cellspacing="0"
                                               columnClasses="vdcStudyInfoCol1, vdcStudyInfoCol2" columns="2" id="gridPanelAbstractAndScope" rendered="#{studyPage.studyUI.abstractAndScopePanelIsRendered}"  width="100%">
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.abstractHelp}"  linkText="#{studybundle.abstractLabel}" heading="#{studybundle.abstractHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.abstracts != null and studyPage.studyUI.abstracts != ''}"/>
                                    <h:outputText id="text19"  value="#{studyPage.studyUI.abstracts}" rendered="#{!empty studyPage.studyUI.abstracts }" escape="false"/>
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.abstractDateHelp}"  linkText="#{studybundle.abstractDateLabel}" heading="#{studybundle.abstractDateHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.abstractDates != null and studyPage.studyUI.abstractDates != ''}" />
                                    <h:outputText id="text20"  value="#{studyPage.studyUI.abstractDates}" rendered="#{!empty studyPage.studyUI.abstractDates }"/>
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.keywordsHelp}"  linkText="#{studybundle.keywordsLabel}" heading="#{studybundle.keywordsHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.keywords != ''}"/>
                                    <h:outputText id="text21"  value="#{studyPage.studyUI.keywords}" rendered="#{!empty studyPage.studyUI.keywords}" escape="false"/>
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.topicClassificationHelp}"  linkText="#{studybundle.topicClassificationLabel}" heading="#{studybundle.topicClassificationHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.topicClasses != ''}"/>
                                    <h:outputText id="text22"  value="#{studyPage.studyUI.topicClasses}" rendered="#{!empty studyPage.studyUI.topicClasses }" escape="false"/>                                          
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.relatedPublicationsHelp}"  linkText="#{studybundle.relatedPublicationsLabel}" heading="#{studybundle.relatedPublicationsHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.relPublications != '' and studyPage.studyUI.relPublications != null }"/>
                                    <h:outputText id="text23"  value="#{studyPage.studyUI.relPublications}" rendered="#{!empty studyPage.studyUI.relPublications }" escape="false" />
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.relatedMaterialHelp}"  linkText="#{studybundle.relatedMaterialLabel}" heading="#{studybundle.relatedMaterialHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.relMaterials != '' and studyPage.studyUI.relMaterials != null }" />
                                    <h:outputText  id="outputText150" value="#{studyPage.studyUI.relMaterials}" rendered="#{!empty studyPage.studyUI.relMaterials}" escape="false"/>
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.relatedStudiesHelp}"  linkText="#{studybundle.relatedStudiesLabel}" heading="#{studybundle.relatedStudiesHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.relStudies != '' and studyPage.studyUI.relStudies != null }"/>
                                    <h:outputText  id="outputText152" value="#{studyPage.studyUI.relStudies}" rendered="#{!empty studyPage.studyUI.relStudies}" escape="false"/>
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.otherReferencesHelp}"  linkText="#{studybundle.otherReferencesLabel}" heading="#{studybundle.otherReferencesHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.otherRefs != '' and studyPage.studyUI.otherRefs != null }" />
                                    <h:outputText  id="outputText154" value="#{studyPage.studyUI.otherRefs}" rendered="#{!empty studyPage.studyUI.otherRefs }" escape="false"/>                                            
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.timePeriodCoveredHelp}"  linkText="#{studybundle.timePeriodCoveredLabel}" heading="#{studybundle.timePeriodCoveredHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.timePeriodCovered != '' }"/>
                                    <h:outputText id="text24"  value="#{studyPage.studyUI.timePeriodCovered }" rendered="#{!empty studyPage.studyUI.timePeriodCovered }" />
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.collectionDateHelp}"  linkText="#{studybundle.collectionDateLabel}" heading="#{studybundle.collectionDateHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.dateOfCollection != ''  }"/>
                                    <h:outputText id="text25"  value="#{studyPage.studyUI.dateOfCollection} " rendered="#{!empty studyPage.studyUI.dateOfCollection }"/>
                                    
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.countryHelp}"  linkText="#{studybundle.countryLabel}" heading="#{studybundle.countryHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.country != '' and studyPage.studyUI.study.country != null }"/>
                                    <h:outputText  id="text26" value="#{studyPage.studyUI.study.country}" rendered="#{!empty studyPage.studyUI.study.country }"/>
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.geographicCoverageHelp}"  linkText="#{studybundle.geographicCoverageLabel}" heading="#{studybundle.geographicCoverageHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.geographicCoverage != '' and studyPage.studyUI.study.geographicCoverage!= null }"/>
                                    <h:outputText id="text27"  value="#{studyPage.studyUI.study.geographicCoverage}" rendered="#{!empty studyPage.studyUI.study.geographicCoverage}"/>
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.geographicUnitHelp}"  linkText="#{studybundle.geographicUnitLabel}" heading="#{studybundle.geographicUnitHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.geographicUnit != '' and studyPage.studyUI.study.geographicUnit != null }"/>
                                    <h:outputText id="text28"  value="#{studyPage.studyUI.study.geographicUnit}" rendered="#{!empty studyPage.studyUI.study.geographicUnit}"/>
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.geographicBoundingHelp}"  linkText="#{studybundle.geographicBoundingLabel}" heading="#{studybundle.geographicBoundingHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.geographicBoundings != '' and studyPage.studyUI.geographicBoundings != null }"/>
                                    <h:outputText id="text29"  value="#{studyPage.studyUI.geographicBoundings}" rendered="#{!empty studyPage.studyUI.geographicBoundings}"/>
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.unitOfAnalysisHelp}"  linkText="#{studybundle.unitOfAnalysisLabel}" heading="#{studybundle.unitOfAnalysisHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.unitOfAnalysis != '' and studyPage.studyUI.study.unitOfAnalysis != null }" />
                                    <h:outputText id="text30"  value="#{studyPage.studyUI.study.unitOfAnalysis}" rendered="#{!empty studyPage.studyUI.study.unitOfAnalysis }" escape="false"/>
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.universeHelp}"  linkText="#{studybundle.universeLabel}" heading="#{studybundle.universeHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.universe != '' and studyPage.studyUI.study.universe != null }"/>
                                    <h:outputText id="text31"  value="#{studyPage.studyUI.study.universe}" rendered="#{!empty studyPage.studyUI.study.universe}" escape="false"/>
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.kindOfDataHelp}"  linkText="#{studybundle.kindOfDataLabel}" heading="#{studybundle.kindOfDataHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.kindOfData != '' and studyPage.studyUI.study.kindOfData != null }"/>
                                    <h:outputText id="text32"  value="#{studyPage.studyUI.study.kindOfData}" rendered="#{!empty studyPage.studyUI.study.kindOfData}"/>
                                </h:panelGrid>
                                
                                <ui:panelGroup  block="true" id="groupPanel13" styleClass="vdcStudyInfoHeader" rendered="#{!studyPage.dataCollectionIsEmpty}">
                                    <h:outputText  id="outputText78" value="Data Collection / Methodology"/>
                                    <h:commandButton id="commandButtondataCollectionoContract" image="/resources/icon_expand.gif" title="Hide fields in this section" rendered="#{studyPage.studyUI.dataCollectionPanelIsRendered}" actionListener="#{studyPage.updateDataCollectionDisplay}" />                      
                                    <h:commandButton id="commandButtondataCollectionExpand" image="/resources/icon_contract.gif" title="Show fields in this section" rendered="#{!studyPage.studyUI.dataCollectionPanelIsRendered}" actionListener="#{studyPage.updateDataCollectionDisplay}"/> 
                                </ui:panelGroup>
                                <h:panelGrid   binding="#{studyPage.dataCollectionPanel}" cellpadding="0" cellspacing="0"
                                               columnClasses="vdcStudyInfoCol1, vdcStudyInfoCol2" columns="2" id="gridPanelDataCollection" 
                                               width="100%"  rendered="#{studyPage.studyUI.dataCollectionPanelIsRendered}" > 
                                    <dvn:inlinehelp helpMessage="#{studybundle.timeMethodHelp}"  linkText="#{studybundle.timeMethodLabel}" heading="#{studybundle.timeMethodHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink"  rendered="#{studyPage.studyUI.study.timeMethod != '' and studyPage.studyUI.study.timeMethod != null }"/>
                                    <h:outputText  id="text33" value="#{studyPage.studyUI.study.timeMethod}"  rendered="#{!empty studyPage.studyUI.study.timeMethod}" escape="false"/>
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.dataCollectorHelp}"  linkText="#{studybundle.dataCollectorLabel}" heading="#{studybundle.dataCollectorHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink"  rendered="#{studyPage.studyUI.study.dataCollector != '' and studyPage.studyUI.study.dataCollector != null }"/> 
                                    <h:outputText  id="outputText77" value="#{studyPage.studyUI.study.dataCollector}"  rendered="#{!empty studyPage.studyUI.study.dataCollector }" escape="false"/>
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.frequencyHelp}"  linkText="#{studybundle.frequencyLabel}" heading="#{studybundle.frequencyHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink"  rendered="#{studyPage.studyUI.study.frequencyOfDataCollection != '' and studyPage.studyUI.study.frequencyOfDataCollection != null }"/>
                                    <h:outputText  id="outputText80" value="#{studyPage.studyUI.study.frequencyOfDataCollection}" rendered="#{!empty studyPage.studyUI.study.frequencyOfDataCollection}" escape="false"/>
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.samplingProcedureHelp}"  linkText="#{studybundle.samplingProcedureLabel}" heading="#{studybundle.samplingProcedureHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink"  rendered="#{studyPage.studyUI.study.samplingProcedure != '' and studyPage.studyUI.study.samplingProcedure != null }"/>
                                    <h:outputText  id="outputText82" value="#{studyPage.studyUI.study.samplingProcedure}"  rendered="#{!empty studyPage.studyUI.study.samplingProcedure}" escape="false"/>
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.majorDeviationsHelp}"  linkText="#{studybundle.majorDeviationsLabel}" heading="#{studybundle.majorDeviationsHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.deviationsFromSampleDesign != '' and studyPage.studyUI.study.deviationsFromSampleDesign != null }"/>
                                    <h:outputText  id="outputText84" value="#{studyPage.studyUI.study.deviationsFromSampleDesign}"  rendered="#{!empty studyPage.studyUI.study.deviationsFromSampleDesign }" escape="false"/>
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.collectionModeHelp}"  linkText="#{studybundle.collectionModeLabel}" heading="#{studybundle.collectionModeHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.collectionMode != '' and studyPage.studyUI.study.collectionMode != null }"/>
                                    <h:outputText  id="outputText86" value="#{studyPage.studyUI.study.collectionMode}"  rendered="#{!empty studyPage.studyUI.study.collectionMode}" escape="false"/>
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.researchInstrumentHelp}"  linkText="#{studybundle.researchInstrumentLabel}" heading="#{studybundle.researchInstrumentHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.researchInstrument != '' and studyPage.studyUI.study.researchInstrument != null }"/>
                                    <h:outputText  id="outputText88" value="#{studyPage.studyUI.study.researchInstrument}"  rendered="#{!empty studyPage.studyUI.study.researchInstrument}" escape="false"/>
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.dataSourcesHelp}"  linkText="#{studybundle.dataSourcesLabel}" heading="#{studybundle.dataSourcesHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.dataSources != '' and studyPage.studyUI.study.dataSources != null }"/>
                                    <h:outputText  id="outputText90" value="#{studyPage.studyUI.study.dataSources}"  rendered="#{!empty studyPage.studyUI.study.dataSources}" escape="false"/>
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.originOfSourcesHelp}"  linkText="#{studybundle.originOfSourcesLabel}" heading="#{studybundle.originOfSourcesHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.originOfSources != '' and studyPage.studyUI.study.originOfSources != null }"/>
                                    <h:outputText  id="outputText92" value="#{studyPage.studyUI.study.originOfSources}"  rendered="#{!empty studyPage.studyUI.study.originOfSources }" escape="false"/>
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.sourceCharacteristicsHelp}"  linkText="#{studybundle.sourceCharacteristicsLabel}" heading="#{studybundle.sourceCharacteristicsHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.characteristicOfSources != '' and studyPage.studyUI.study.characteristicOfSources != null }"/>
                                    <h:outputText  id="outputText94" value="#{studyPage.studyUI.study.characteristicOfSources}" rendered="#{!empty studyPage.studyUI.study.characteristicOfSources}" escape="false"/>
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.sourceDocumentationHelp}"  linkText="#{studybundle.sourceDocumentationLabel}" heading="#{studybundle.sourceDocumentationHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.accessToSources != '' and studyPage.studyUI.study.accessToSources != null }"/>
                                    <h:outputText  id="outputText96" value="#{studyPage.studyUI.study.accessToSources}"  rendered="#{!empty studyPage.studyUI.study.accessToSources}" escape="false"/>
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.dataCollectionCharacteristicsHelp}"  linkText="#{studybundle.dataCollectionCharacteristicsLabel}" heading="#{studybundle.dataCollectionCharacteristicsHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.dataCollectionSituation != '' and studyPage.studyUI.study.dataCollectionSituation != null }"/>
                                    <h:outputText  id="outputText98" value="#{studyPage.studyUI.study.dataCollectionSituation}"  rendered="#{!empty studyPage.studyUI.study.dataCollectionSituation}" escape="false"/>
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.minimizeActionsHelp}"  linkText="#{studybundle.minimizeActionsLabel}" heading="#{studybundle.minimizeActionsHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.actionsToMinimizeLoss != '' and studyPage.studyUI.study.actionsToMinimizeLoss != null }"/>
                                    <h:outputText  id="outputText100" value="#{studyPage.studyUI.study.actionsToMinimizeLoss}"  rendered="#{!empty studyPage.studyUI.study.actionsToMinimizeLoss}" escape="false"/>
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.controlOperationsHelp}"  linkText="#{studybundle.controlOperationsLabel}" heading="#{studybundle.controlOperationsHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.controlOperations != '' and studyPage.studyUI.study.controlOperations != null }"/>
                                    <h:outputText  id="outputText102" value="#{studyPage.studyUI.study.controlOperations}"  rendered="#{!empty studyPage.studyUI.study.controlOperations}" escape="false"/>
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.weightingHelp}"  linkText="#{studybundle.weightingLabel}" heading="#{studybundle.weightingHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.weighting != '' and studyPage.studyUI.study.weighting != null }"/>
                                    <h:outputText  id="outputText104" value="#{studyPage.studyUI.study.weighting}"  rendered="#{!empty studyPage.studyUI.study.weighting}" escape="false"/>
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.cleaningOperationsHelp}"  linkText="#{studybundle.cleaningOperationsLabel}" heading="#{studybundle.cleaningOperationsHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.cleaningOperations != '' and studyPage.studyUI.study.cleaningOperations != null }"/>
                                    <h:outputText  id="outputText106" value="#{studyPage.studyUI.study.cleaningOperations}"  rendered="#{!empty studyPage.studyUI.study.cleaningOperations}" escape="false"/>
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.studyLevelErrorNotesHelp}"  linkText="#{studybundle.studyLevelErrorNotesLabel}" heading="#{studybundle.studyLevelErrorNotesHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.studyLevelErrorNotes != '' and studyPage.studyUI.study.studyLevelErrorNotes != null }"/>
                                    <h:outputText  id="outputText108" value="#{studyPage.studyUI.study.studyLevelErrorNotes}"  rendered="#{!empty studyPage.studyUI.study.studyLevelErrorNotes}" escape="false"/>
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.responseRateHelp}"  linkText="#{studybundle.responseRateLabel}" heading="#{studybundle.responseRateHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.responseRate != '' and studyPage.studyUI.study.responseRate != null }"/>
                                    <h:outputText  id="outputText110" value="#{studyPage.studyUI.study.responseRate}"  rendered="#{!empty studyPage.studyUI.study.responseRate}" escape="false"/>
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.samplingErrorHelp}"  linkText="#{studybundle.samplingErrorLabel}" heading="#{studybundle.samplingErrorHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.samplingErrorEstimate != '' and studyPage.studyUI.study.samplingErrorEstimate != null }"/>
                                    <h:outputText  id="outputText112" value="#{studyPage.studyUI.study.samplingErrorEstimate}"  rendered="#{!empty studyPage.studyUI.study.samplingErrorEstimate}" escape="false"/>
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.otherDataAppraisalHelp}"  linkText="#{studybundle.otherDataAppraisalLabel}" heading="#{studybundle.otherDataAppraisalHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.otherDataAppraisal != '' and studyPage.studyUI.study.otherDataAppraisal != null }"/>
                                    <h:outputText  id="outputText114" value="#{studyPage.studyUI.study.otherDataAppraisal}"  rendered="#{!empty studyPage.studyUI.study.otherDataAppraisal}" escape="false" />
                                </h:panelGrid>
                                
                                <ui:panelGroup   block="true" id="groupPanel14a" styleClass="vdcStudyInfoHeader" rendered="#{!studyPage.dataAvailIsEmpty}">
                                    <h:outputText  id="outputText115a" value="Data Set Availability"/>
                                    <h:commandButton id="commandButtonDataAvailContract" image="/resources/icon_expand.gif" title="Hide fields in this section" rendered="#{studyPage.studyUI.dataAvailPanelIsRendered}" actionListener="#{studyPage.updateDataAvailDisplay}" />                      
                                    <h:commandButton id="commandButtonDataAvailExpand" image="/resources/icon_contract.gif" title="Show fields in this section" rendered="#{!studyPage.studyUI.dataAvailPanelIsRendered}" actionListener="#{studyPage.updateDataAvailDisplay}"/> 
                                </ui:panelGroup>
                                <h:panelGrid  binding="#{studyPage.dataAvailPanel}" cellpadding="0" cellspacing="0"
                                             columnClasses="vdcStudyInfoCol1, vdcStudyInfoCol2" columns="2" id="gridPanelDataAvail" rendered="#{studyPage.studyUI.dataAvailPanelIsRendered}"
                                             width="100%">
                                    <dvn:inlinehelp helpMessage="#{studybundle.dataAccessPlaceHelp}"  linkText="#{studybundle.dataAccessPlaceLabel}" heading="#{studybundle.dataAccessPlaceHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.placeOfAccess != '' and studyPage.studyUI.study.placeOfAccess != null }"/>
                                    <h:outputText  id="outputText117" value="#{studyPage.studyUI.study.placeOfAccess}"  rendered="#{!empty studyPage.studyUI.study.placeOfAccess}" escape="false"/>
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.originalArchiveHelp}"  linkText="#{studybundle.originalArchiveLabel}" heading="#{studybundle.originalArchiveHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.originalArchive != '' and studyPage.studyUI.study.originalArchive != null }"/>
                                    <h:outputText  id="outputText119" value="#{studyPage.studyUI.study.originalArchive}"  rendered="#{!empty studyPage.studyUI.study.originalArchive}" escape="false"/>
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.availabilityHelp}"  linkText="#{studybundle.availabilityLabel}" heading="#{studybundle.availabilityHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.availabilityStatus != '' and studyPage.studyUI.study.availabilityStatus != null }"/>
                                    <h:outputText  id="outputText121" value="#{studyPage.studyUI.study.availabilityStatus}"  rendered="#{!empty studyPage.studyUI.study.availabilityStatus}" escape="false" />
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.collectionSizeHelp}"  linkText="#{studybundle.collectionSizeLabel}" heading="#{studybundle.collectionSizeHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.collectionSize != '' and studyPage.studyUI.study.collectionSize != null }" />
                                    <h:outputText  id="outputText123" value="#{studyPage.studyUI.study.collectionSize}"  rendered="#{!empty studyPage.studyUI.study.collectionSize}" escape="false"/>
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.studyCompletionHelp}"  linkText="#{studybundle.studyCompletionLabel}" heading="#{studybundle.studyCompletionHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.studyCompletion != '' and studyPage.studyUI.study.studyCompletion != null }"/>
                                    <h:outputText  id="outputText125" value="#{studyPage.studyUI.study.studyCompletion}"  rendered="#{!empty studyPage.studyUI.study.studyCompletion != '' and studyPage.studyUI.study.studyCompletion != null }" escape="false"/>
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.numberOfFilesHelp}"  linkText="#{studybundle.numberOfFilesLabel}" heading="#{studybundle.numberOfFilesHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.numberOfFiles != '' and studyPage.studyUI.study.numberOfFiles != null }"/>
                                    <h:outputText  id="outputText127" value="#{studyPage.studyUI.study.numberOfFiles}" rendered="#{!empty studyPage.studyUI.study.numberOfFiles}" escape="false"/>
                                </h:panelGrid>
                                
                                <ui:panelGroup   block="true" id="groupPanel14b" styleClass="vdcStudyInfoHeader" rendered="#{!studyPage.termsOfUseIsEmpty}">
                                    <a name="termsofuse"><!--TERMS OF USE--></a>
                                    <h:outputText id="outputText115" value="Terms of Use"/>
                                    <h:commandButton id="commandButtonTermsOfUseContract" image="/resources/icon_expand.gif" title="Hide fields in this section" rendered="#{studyPage.studyUI.termsOfUsePanelIsRendered}" actionListener="#{studyPage.updateTermsOfUseDisplay}" />                      
                                    <h:commandButton id="commandButtonTermsOfUseExpand" image="/resources/icon_contract.gif" title="Show fields in this section" rendered="#{!studyPage.studyUI.termsOfUsePanelIsRendered}" actionListener="#{studyPage.updateTermsOfUseDisplay}"/> 
                                </ui:panelGroup>
                                <h:panelGrid binding="#{studyPage.termsOfUsePanel}" cellpadding="0" cellspacing="0"
                                             columnClasses="vdcStudyInfoCol1, vdcStudyInfoCol2" columns="2" id="gridPanelTermsOfUse"  rendered="#{studyPage.studyUI.termsOfUsePanelIsRendered}"
                                             width="100%">
                                    <dvn:inlinehelp helpMessage="#{studybundle.networkHarvestedTermsofUseHelp}"  linkText="#{studybundle.networkHarvestedTermsofUseLabel}" heading="#{studybundle.networkHarvestedTermsofUseHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{!empty studyPage.studyUI.study.harvestDVNTermsOfUse}"/>
                                    <h:outputText  value="#{studyPage.studyUI.study.harvestDVNTermsOfUse}" rendered="#{!empty studyPage.studyUI.study.harvestDVNTermsOfUse}" escape="false"/>
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.dataverseHarvestedTermsofUseHelp}"  linkText="#{studybundle.dataverseHarvestedTermsofUseLabel}" heading="#{studybundle.dataverseHarvestedTermsofUseHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{!empty studyPage.studyUI.study.harvestDVTermsOfUse}"/>
                                    <h:outputText  value="#{studyPage.studyUI.study.harvestDVTermsOfUse}" rendered="#{!empty studyPage.studyUI.study.harvestDVTermsOfUse}" escape="false"/>
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.networkTermsofUseHelp}"  linkText="#{studybundle.networkTermsofUseLabel}" heading="#{studybundle.networkTermsofUseHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{VDCRequest.vdcNetwork.downloadTermsOfUseEnabled}"/>
                                    <h:outputText value="#{VDCRequest.vdcNetwork.downloadTermsOfUse}" rendered="#{VDCRequest.vdcNetwork.downloadTermsOfUseEnabled}" escape="false"/>
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.dataverseTermsofUseHelp}"  linkText="#{studybundle.dataverseTermsofUseLabel}" heading="#{studybundle.dataverseTermsofUseHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{!empty studyPage.studyUI.dataverseTermsOfUse}"/>
                                    <h:outputText   value="#{studyPage.studyUI.dataverseTermsOfUse}" rendered="#{!empty studyPage.studyUI.dataverseTermsOfUse}" escape="false"/>
                                    
                                    <h:outputText value="Study Terms of Use: " style="font-weight: bold;" rendered="#{studyPage.studyUI.study.termsOfUseEnabled}" />
                                    <h:outputText value=" " rendered="#{studyPage.studyUI.study.termsOfUseEnabled}" />
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.confidentialityHelp}"  linkText="#{studybundle.confidentialityLabel}" heading="#{studybundle.confidentialityHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.confidentialityDeclaration != '' and studyPage.studyUI.study.confidentialityDeclaration != null }"/>
                                    <h:outputText  id="outputText129" value="#{studyPage.studyUI.study.confidentialityDeclaration}" rendered="#{!empty studyPage.studyUI.study.confidentialityDeclaration}" escape="false"/>
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.specialPermissionsHelp}"  linkText="#{studybundle.specialPermissionsLabel}" heading="#{studybundle.specialPermissionsHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.specialPermissions != '' and studyPage.studyUI.study.specialPermissions != null }"/>
                                    <h:outputText  id="outputText131" value="#{studyPage.studyUI.study.specialPermissions}" rendered="#{!empty studyPage.studyUI.study.specialPermissions}" escape="false"/>
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.restrictionsHelp}"  linkText="#{studybundle.restrictionsLabel}" heading="#{studybundle.restrictionsHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.restrictions != '' and studyPage.studyUI.study.restrictions != null }" />
                                    <h:outputText  id="outputText133" value="#{studyPage.studyUI.study.restrictions}" rendered="#{!empty studyPage.studyUI.study.restrictions}" escape="false"/>
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.contactHelp}"  linkText="#{studybundle.contactLabel}" heading="#{studybundle.contactHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.contact != '' and studyPage.studyUI.study.contact != null }" />
                                    <h:outputText  id="outputText135" value="#{studyPage.studyUI.study.contact}" rendered="#{!empty studyPage.studyUI.study.contact}" escape="false"/>
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.citationRequirementsHelp}"  linkText="#{studybundle.citationRequirementsLabel}" heading="#{studybundle.citationRequirementsHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.citationRequirements != '' and studyPage.studyUI.study.citationRequirements != null }"/>
                                    <h:outputText  id="outputText137" value="#{studyPage.studyUI.study.citationRequirements}" rendered="#{!empty studyPage.studyUI.study.citationRequirements}" escape="false"/>
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.depositorRequirementsHelp}"  linkText="#{studybundle.depositorRequirementsLabel}" heading="#{studybundle.depositorRequirementsHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.depositorRequirements != '' and studyPage.studyUI.study.depositorRequirements != null }"/>
                                    <h:outputText  id="outputText139" value="#{studyPage.studyUI.study.depositorRequirements}" rendered="#{!empty studyPage.studyUI.study.depositorRequirements}" escape="false"/>
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.conditionsHelp}"  linkText="#{studybundle.conditionsLabel}" heading="#{studybundle.conditionsHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.conditions != '' and studyPage.studyUI.study.conditions != null }"/>
                                    <h:outputText  id="outputText141" value="#{studyPage.studyUI.study.conditions}" rendered="#{!empty studyPage.studyUI.study.conditions}" escape="false"/>
                                    
                                    <dvn:inlinehelp helpMessage="#{studybundle.disclaimerHelp}"  linkText="#{studybundle.disclaimerLabel}" heading="#{studybundle.disclaimerHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.disclaimer != '' and studyPage.studyUI.study.disclaimer != null }"/>
                                    <h:outputText  id="outputText143" value="#{studyPage.studyUI.study.disclaimer}" rendered="#{!empty studyPage.studyUI.study.disclaimer}" escape="false"/>
                                </h:panelGrid>
                                
                                <ui:panelGroup  block="true" id="groupPanel15" styleClass="vdcStudyInfoHeader" rendered="#{!studyPage.notesIsEmpty}">
                                    <h:outputText  id="outputText144" value="Other Information"/>
                                    <h:commandButton id="commandButtonNotesContract" image="/resources/icon_expand.gif" title="Hide fields in this section" rendered="#{studyPage.studyUI.notesPanelIsRendered}" actionListener="#{studyPage.updateNotesDisplay}" />                      
                                    <h:commandButton id="commandButtonNotesExpand" image="/resources/icon_contract.gif" title="Show fields in this section" rendered="#{!studyPage.studyUI.notesPanelIsRendered}" actionListener="#{studyPage.updateNotesDisplay}"/> 
                                </ui:panelGroup>
                                <h:panelGrid   binding="#{studyPage.notesPanel}" cellpadding="0" cellspacing="0"
                                               columnClasses="vdcStudyInfoCol1, vdcStudyInfoCol2" columns="2" id="gridPanelNotes"  rendered="#{studyPage.studyUI.notesPanelIsRendered}" 
                                               width="100%">
                                    <dvn:inlinehelp helpMessage="#{studybundle.notesHelp}"  linkText="#{studybundle.notesLabel}" heading="#{studybundle.notesHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.notes != ''}"/>
                                    <h:outputText  id="outputText146" value="#{studyPage.studyUI.notes}" rendered="#{!empty studyPage.studyUI.notes}" escape="false"/>
                                </h:panelGrid>
                                
                                <!--
                                <ui:panelGroup  block="true" id="groupPanel3" styleClass="vdcStudyInfoDownload" rendered="false">
                                    <h:outputLink  id="hyperlink2" value="http://www.sun.com/jscreator">
                                        <h:outputText  id="hyperlink2Text" value="Download the complete cataloging information in XML format (without variables)"/>
                                    </h:outputLink>
                                </ui:panelGroup>
                                <ui:panelGroup  block="true" id="groupPanel2" styleClass="vdcStudyInfoDownload" rendered="false">
                                    <h:outputLink  id="hyperlink1" value="http://www.sun.com/jscreator">
                                        <h:outputText  id="hyperlink1Text" value="Download the complete cataloging information in XML format (with variables)"/>
                                    </h:outputLink>
                                </ui:panelGroup>
                                -->
                            </ui:panelLayout>
                        </ui:tab>
<!-- FILES -->                        
                        <ui:tab id="files" text="Documentation, Data and Analysis" url="#{VDCRequest.currentVDCURL}/faces/study/StudyPage.jsp?studyId=#{studyPage.studyUI.study.id}&amp;tab=files#{studyPage.studyListingIndexAsParameter}">
                           <ui:panelLayout id="layoutPanel2" panelLayout="flow" styleClass="vdcStudyFilesPage">
                            <ui:panelLayout id="noFilesPanel1" panelLayout="flow" styleClass="vdcStudyFilesEmpty" rendered="#{empty studyPage.studyUI.study.fileCategories}">
                                <ui:panelGroup block="true" id="noFilesPanel2" styleClass="vdcStudyFilesEmptyMessage" rendered="#{!studyPage.userAuthorizedToEdit}">
                                    <h:outputText value="No files have been provided for this study."/>
                                </ui:panelGroup>
                                <ui:panelGroup block="true" id="noFilesPanel3" styleClass="vdcStudyFilesEmptyMessage" rendered="#{studyPage.userAuthorizedToEdit and (studyPage.studyUI.study.studyLock==null)}">
                                    <h:outputText value="No files have been uploaded to this study. To add a file, follow the 'Add File(s)' link on the top right of this page."/>
                                </ui:panelGroup>
                                <ui:panelGroup block="true" id="noFilesPanel4a" styleClass="vdcStudyFilesEmptyMessage" rendered="#{studyPage.userAuthorizedToEdit and !(studyPage.studyUI.study.studyLock==null)}">
                                    <h:outputText styleClass="warnMessage" value="One or more data files are being uploaded..."/>
                                </ui:panelGroup>
                            </ui:panelLayout>
                            <ui:panelGroup block="true" id="noFilesPanel4b" styleClass="vdcStudyFilesUploadingMsg" rendered="#{studyPage.userAuthorizedToEdit and !(studyPage.studyUI.study.studyLock==null) and !empty studyPage.studyUI.study.fileCategories}">
                                <h:outputText styleClass="warnMessage" value="One or more data files are being uploaded..."/>
                            </ui:panelGroup>
                            <ui:panelLayout styleClass="vdcStudyFilesMessage" rendered="#{studyPage.studyUI.anyFileRestricted or !empty studyPage.studyUI.study.harvestHoldings and studyPage.studyUI.study.isHarvested}">
                                <ui:panelGroup rendered="#{!empty studyPage.studyUI.study.harvestHoldings and studyPage.studyUI.study.isHarvested}">
                                    Files for this study may also be accessed from their
                                    <h:outputLink value="#{studyPage.studyUI.study.harvestHoldings}">
                                        <h:outputText value="original source." />
                                    </h:outputLink>
                                </ui:panelGroup>
                                <ui:panelGroup rendered="#{studyPage.studyUI.anyFileRestricted}">
                                        <h:outputText value="Access to some files is restricted, and those files are not available for downloading." />
                                        <h:outputText value=" No information about the restriction is available." rendered="#{studyPage.termsOfUseIsEmpty}" />
                                        <h:outputText value=" For more information, check the " rendered="#{!studyPage.termsOfUseIsEmpty}" />
                                        <h:outputLink value="/dvn#{VDCRequest.currentVDCURL}/faces/study/StudyPage.jsp?studyId=#{studyPage.studyUI.study.id}&amp;tab=catalog#termsofuse" rendered="#{!studyPage.termsOfUseIsEmpty}">
                                            <h:outputText value="Terms of Use."/>
                                        </h:outputLink>
                                </ui:panelGroup>
                            </ui:panelLayout>
                            <ui:panelLayout id="layoutPanel3" panelLayout="flow" styleClass="vdcStudyFilesContent" rendered="#{!empty studyPage.studyUI.study.fileCategories}">
                                <ui:panelGroup block="true" id="groupPanel7a" styleClass="vdcStudyFilesDownloadAll" rendered="#{studyPage.studyUI.anyFileUnrestricted}">
                                    <h:commandButton onclick="window.location.href='/dvn#{VDCRequest.currentVDCURL}/FileDownload/study_#{studyPage.studyUI.study.studyId}.zip?studyId=#{studyPage.studyUI.study.id}#{studyPage.xff}';return false;" value="Download All Files"/>
                                    <h:outputText id="outputText33" styleClass="vdcStudyFilesDownloadAllMessage" value="Note: you will be prompted to save a single archive file. Study files that have restricted access will not be downloaded. Data files will be downloaded in their default format."/>
                                </ui:panelGroup>
                                
                                <h:dataTable cellpadding="0" cellspacing="0" value="#{studyPage.studyUI.categoryUIList}" id="catDataTable" var="catUI" width="100%">
                                    <h:column id="catColumn">
                                <div class="TogglePaneFancy">
                                <div>
                                        <ui:panelGroup block="true" id="groupPanel4" styleClass="TogglePaneFancyHeader vdcStudyFilesCat">
                                            <ui:panelGroup block="true" styleClass="vdcStudyFilesCatLabel">
                                                <h:outputText id="outputText16" value="#{catUI.fileCategory.name}"/>
                                            </ui:panelGroup>
                                            <ui:panelGroup block="true" styleClass="vdcStudyFilesCatDownload">
                                                <h:graphicImage styleClass="vdcNoBorders" value="/resources/icon_downloadall_locked.gif" rendered="#{!catUI.anyFileUnrestricted}" alt="You do not have permissions to access any files in this category." />
                                                <h:outputText styleClass="vdcStudyFileRowFileDownloadRestricted" value="Files in this category are restricted" rendered="#{!catUI.anyFileUnrestricted}" />
                                                <h:outputLink styleClass="vdcStudyCatDownloadLink" id="catDownloadLink" value="/dvn#{VDCRequest.currentVDCURL}/FileDownload/study_#{studyPage.studyUI.study.studyId}_#{catUI.downloadName}.zip?catId=#{catUI.fileCategory.id}#{studyPage.xff}" title="Download all files in this category in a single archive file." rendered="#{catUI.anyFileUnrestricted}">
                                                    <h:outputText styleClass="vdcStudyCatDownloadLink" value="Download all files in this category" />
                                                </h:outputLink>
                                            </ui:panelGroup>
                                        </ui:panelGroup>
                                        <ui:panelGroup block="true" styleClass="TogglePaneContent">
                                            <h:dataTable rendered="#{catUI.rendered}" cellpadding="0" cellspacing="0"
                                                         id="dataTable5" rowClasses="list-row-even,list-row-odd"
                                                         value="#{catUI.studyFileUIs}" var="studyFileUI" width="100%">

                                                <h:column id="column9">
                                                    
                                                    <ui:panelGroup block="true" styleClass="vdcStudyFileRow">

                                                        <ui:panelGroup block="true" styleClass="vdcStudyFileRowLeft">
                                                            <ui:panelGroup block="true" styleClass="vdcStudyFileRowFileInfo">
                                                                <ui:panelGroup block="true" styleClass="vdcStudyFileRowFileThumb" rendered="#{!studyFileUI.restrictedForUser and studyFileUI.image}">
                                                                    <h:graphicImage styleClass="vdcNoBorders" value="/FileDownload/?fileId=#{studyFileUI.studyFile.id}&amp;imageThumb" alt="Thumbnail" />
                                                                </ui:panelGroup>
                                                                <ui:panelGroup block="true" styleClass="vdcStudyFileRowFileName">
                                                                    <h:outputText id="outputText15" value="#{studyFileUI.studyFile.fileName}"/>
                                                                    <h:inputHidden id="vdcIdforFile" value="#{studyFileUI.vdcId}"/>
                                                                    <br/>
                                                                    <h:outputText styleClass="vdcStudyFileRowFileDetails" value="#{studyFileUI.userFriendlyFileType}" />
                                                                    <h:outputText styleClass="vdcStudyFileRowFileDetails" value=" - #{studyFileUI.fileSize}" rendered="#{!empty studyFileUI.studyFile.fileSystemLocation and !studyFileUI.studyFile.remote}" />
                                                                    <h:outputText styleClass="vdcStudyFileRowFileDetails" value=" - Unknown file size" rendered="#{empty studyFileUI.studyFile.fileSystemLocation or studyFileUI.studyFile.remote}" />
                                                                    <h:outputText styleClass="vdcStudyFileRowFileDetails" value=" - #{studyFileUI.downloadCount}" /><h:outputText styleClass="vdcStudyFileRowFileDetails" value=" downloads/analysis" rendered="#{studyFileUI.downloadCount != 1}" /><h:outputText styleClass="vdcStudyFileRowFileDetails" value=" download" rendered="#{studyFileUI.downloadCount == 1}" />
                                                                </ui:panelGroup>
                                                            </ui:panelGroup>

                                                            <ui:panelGroup block="true" styleClass="vdcStudyFileRowFileDownload">
                                                                <h:graphicImage styleClass="vdcNoBorders" value="/resources/icon_download_locked.gif" rendered="#{studyFileUI.restrictedForUser}" alt="You do not have permissions to access this file."/>
                                                                <h:outputText styleClass="vdcStudyFileRowFileDownloadRestricted" value="Restricted Access" rendered="#{studyFileUI.restrictedForUser}" />
                                                                
                                                                <h:outputLink styleClass="vdcStudyFileRowFileDownloadLink" value="#{studyFileUI.fileDownloadURL}" rendered="#{!studyFileUI.restrictedForUser and !studyFileUI.studyFile.subsettable}">
                                                                    <h:outputText value="Download" rendered="#{!studyFileUI.restrictedForUser and !studyFileUI.studyFile.subsettable}" />
                                                                </h:outputLink>
                                                                
                                                                <ui:panelGroup block="true" rendered="#{!studyFileUI.restrictedForUser and studyFileUI.studyFile.subsettable}">
                                                                    <ui:script type="text/javascript">
                                                                        // <![CDATA[
                                                                              jQuery(document).ready(function(){
                                                                                            jQuery(".studyFileDownloadAs li").hover(
                                                                                                    function(){ jQuery("ul", this).fadeIn("fast"); }, 
                                                                                                    function() { } 
                                                                                            );
                                                                                    if (document.all) {
                                                                                                    jQuery(".studyFileDownloadAs li").hoverClass ("sfHover");
                                                                                            }
                                                                              });
                                                                              jQuery.fn.hoverClass = function(c) {
                                                                                      return this.each(function(){
                                                                                              jQuery(this).hover( 
                                                                                                      function() { jQuery(this).addClass(c);  },
                                                                                                      function() { jQuery(this).removeClass(c); }
                                                                                              );
                                                                                      });
                                                                              };
                                                                        // ]]>
                                                                    </ui:script>
                                                                    <ul class="studyFileDownloadAs downloadas">
                                                                        <li>
                                                                            <h:graphicImage styleClass="vdcNoBorders" value="/resources/icon_download.gif" alt="View or download this File."/><a class="downloadasLink">Download as...</a>
                                                                            <ul>
                                                                                <gui:repeat value="#{studyFileUI.dataFileFormatTypes}" var="studyDataFileType">
                                                                                    <li>
                                                                                        <h:outputLink value="#{studyFileUI.fileDownloadURL}&amp;downloadOriginalFormat=true" rendered="#{!empty studyDataFileType.value and studyDataFileType.originalFileDataFileFormat}">
                                                                                            <h:outputText value="#{studyDataFileType.name}" />
                                                                                        </h:outputLink>
                                                                                        <h:outputLink value="#{studyFileUI.fileDownloadURL}&amp;format=#{studyDataFileType.value}" rendered="#{!empty studyDataFileType.value and !studyDataFileType.originalFileDataFileFormat}">
                                                                                            <h:outputText value="#{studyDataFileType.name}" />
                                                                                        </h:outputLink>
                                                                                        <h:outputLink value="#{studyFileUI.fileDownloadURL}" rendered="#{empty studyDataFileType.value}">
                                                                                            <h:outputText value="#{studyDataFileType.name}" />
                                                                                        </h:outputLink>
                                                                                    </li>
                                                                                </gui:repeat>
                                                                            </ul>
                                                                        </li>
                                                                    </ul>
                                                                </ui:panelGroup>
                                                            </ui:panelGroup>
                                                        </ui:panelGroup>

                                                        <ui:panelGroup block="true" styleClass="vdcStudyFileRowRight">
                                                            <h:outputText styleClass="vdcStudyDesc" id="outputText17" value="#{studyFileUI.studyFile.description}"/>
                                                        </ui:panelGroup>

                                                    </ui:panelGroup>

                                                    <ui:panelGroup rendered="#{studyFileUI.studyFile.subsettable}" block="true" styleClass="vdcStudyFileRowSubset">

                                                        <ui:panelGroup block="true" styleClass="vdcStudyFileRowSubsetLeft">
                                                            <ui:panelGroup block="true" styleClass="vdcStudyFileSubsetTable">
                                                                <h:panelGrid columns="3" cellpadding="2" cellspacing="0" border="0">
                                                                    <h:column id="column1">
                                                                        <span>Subsetting</span>
                                                                    </h:column>
                                                                    <h:column id="column2">
                                                                        <h:outputText value="#{studyFileUI.studyFile.dataTable.caseQuantity} Cases"/>
                                                                    </h:column>
                                                                    <h:column id="column3">
                                                                        <h:outputText value="#{studyFileUI.studyFile.dataTable.varQuantity} Variables"/>
                                                                    </h:column>
                                                                </h:panelGrid>
                                                            </ui:panelGroup>

                                                            <ui:panelGroup block="true" styleClass="vdcStudyFileSubsetAnalysis">
                                                                <h:outputLink id="fileSubset2" styleClass="vdcStudyFileSubsetAnalysisLink" value="/dvn#{VDCRequest.currentVDCURL}/faces/subsetting/SubsettingPage.jsp?dtId=#{studyFileUI.studyFile.dataTable.id}">
                                                                    <h:outputText value="Access Subset/Analysis" />
                                                                </h:outputLink>
                                                            </ui:panelGroup>
                                                        </ui:panelGroup>

                                                        <ui:panelGroup block="true" styleClass="vdcStudyDataCitationContainer">
                                                            <h:outputText styleClass="vdcStudyDataCitation" escape="false" value="&lt;span&gt;View Data Citation&lt;/span&gt;&lt;br /&gt;#{studyPage.studyUI.study.citation} #{studyFileUI.studyFile.fileName} [fileDscr/fileName (DDI)]" />
                                                        </ui:panelGroup>

                                                    </ui:panelGroup>
                                                    
                                                </h:column>

                                            </h:dataTable>
                                        </ui:panelGroup>
                                </div>
                                </div>
                                    </h:column>
                                </h:dataTable>
                                
                            </ui:panelLayout>
                          </ui:panelLayout>
                        </ui:tab>
                    </ui:tabSet>
                </div>
            </div>

        </ui:form>
            </gui:define>
        </gui:composition>
    </body>
</html>
