<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
    xmlns:h="http://java.sun.com/jsf/html" 
    xmlns:jsp="http://java.sun.com/JSP/Page" 
    xmlns:ui="http://www.sun.com/web/ui"
    xmlns:tiles="http://struts.apache.org/tags-tiles"
    xmlns:c="http://java.sun.com/jsp/jstl/core"
    xmlns:ihelp="/WEB-INF/tlds/InlineHelp"
    xmlns:tip="/WEB-INF/tlds/Tooltip"
    xmlns:ihelpsupport="/WEB-INF/tlds/InlineHelpSupport">
    
    <f:loadBundle basename="StudyBundle" var="studybundle"/>
        
    <f:subview id="studyPageView">
                
                <f:verbatim>   
                <script language="Javascript">
                    var fileCitationPopup;
                    var termsPopup;
                    
                    function checkTerms(anchorId) {
                        if (document.getElementById("content:studyPageView:form1:termsAcceptanceRequired").value == "true" ) {
                            termsPopup = createNewPopup(termsPopup,"content:studyPageView:form1:termsDiv",anchorId);
                            return false;
                        }
                        return true;
                    }
                    
                    function createFileCitationPopup(divId, anchorId) {
                        fileCitationPopup = createNewPopup(fileCitationPopup,divId,anchorId);
                    }
                    
                    function createNewPopup(popup, divId, anchorId) {
                        // hide any previous popup
                        if (popup != null) {
                            popup.hidePopup();
                        }
                        
                        //Create Popup div
                        popup = new PopupWindow(divId);
                        popup.autoHide();
                        popup.offsetX=-300;
                        popup.offsetY=20;
                        popup.showPopup(anchorId);
                        return popup;
                    }
                 </script> 
                 </f:verbatim>
                 <ihelpsupport:inlinehelpsupport writeHelpDiv="true" writeTipDiv="true" rendered="true"/>
                    <ui:form  id="form1">
                        <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>                        
                        <input type="hidden" value="StudyPage" name="pageName"/>
                        <h:inputHidden id="studyId" value="#{studyPage.studyId}"/>                        
                      <ui:panelLayout id="ContentlayoutPanel" panelLayout="flow" styleClass="vdcSectionMiddleNoBorder">
                          <!--
                          <ui:panelGroup block="true" style="text-align: center; padding-top: 0px; padding-bottom: 15px;" rendered="#{studyPage.studyUI.study.inReview or studyPage.studyUI.study.new}">
                              <h:outputText styleClass="vdcTextStandOut" value="This study is in Review. " rendered="#{studyPage.studyUI.study.inReview}"/>
                              <h:outputText styleClass="vdcTextStandOut" value="This study is New. " rendered="#{studyPage.studyUI.study.new}"/>
                              <h:outputText styleClass="warnMessage" value=" Go to " rendered="#{!studyPage.studyUI.study.released and (VDCSession.loginBean.curator or  VDCSession.loginBean.admin or VDCSession.loginBean.networkAdmin)}"/>
                              <h:outputLink value="/dvn/dv/#{VDCRequest.currentVDC.alias}/faces/study/ReviewStudiesPage.jsp" rendered="#{!studyPage.studyUI.study.released and (VDCSession.loginBean.curator or  VDCSession.loginBean.admin or VDCSession.loginBean.networkAdmin)}">
                                  <h:outputText value="Review Studies" />
                              </h:outputLink>
                              <h:outputText styleClass="warnMessage" value=" to change its status." rendered="#{!studyPage.studyUI.study.released and (VDCSession.loginBean.curator or  VDCSession.loginBean.admin or VDCSession.loginBean.networkAdmin)}"/>
                        </ui:panelGroup>
                        <ui:panelGroup block="true" style="text-align: center; padding-top: 0px; padding-bottom: 15px;" rendered="#{studyPage.userAuthorizedToEdit and !(studyPage.studyUI.study.studyLock==null)}">
                             <h:outputText styleClass="warnMessage" value=" This study is currently locked because files are being uploaded."/>
                        </ui:panelGroup>                         
                        -->    
                        <ui:panelGroup  block="true" id="groupPanel1" styleClass="vdcStudyTitle">
                            <h:outputText  id="outputText2" styleClass="vdcStudyTitleName" value="#{studyPage.studyUI.study.title}"/> 
                        </ui:panelGroup>
                       <ui:panelGroup   block="true" styleClass="vdcTextRight">
                       
                                <h:outputText styleClass="vdcStudyStatus" value="(In Review)" rendered="#{studyPage.studyUI.study.inReview}"/>
                                <h:outputText styleClass="vdcStudyStatus" value="(New)" rendered="#{studyPage.studyUI.study.new}"/>
                                <h:outputText styleClass="vdcStudyStatus" value="Currently unavailable for editing because file upload is in progress." rendered="#{studyPage.userAuthorizedToEdit and !(studyPage.studyUI.study.studyLock==null)}"/>
                            
                                <ui:panelGroup  rendered="#{studyPage.userAuthorizedToEdit and studyPage.studyUI.study.studyLock==null}">
                                    <tip:tooltip tooltipMessage="#{studybundle.editTip}" linkText="#{studybundle.editText}" eventType="mouseover" linkUrl="/dvn#{VDCRequest.currentVDCURL}/faces/study/EditStudyPage.jsp?studyId=#{studyPage.studyUI.study.id}&amp;tab=#{studyPage.tabSet1.selected}" cssClass="vdcTooltipLink"/>
                                    <h:outputText value=" | "/>
                                    
                                    <tip:tooltip tooltipMessage="#{studybundle.addTip}" linkText="#{studybundle.addText}"  eventType="mouseover" linkUrl="/dvn#{VDCRequest.currentVDCURL}/faces/study/AddFilesPage.jsp?studyId=#{studyPage.studyUI.study.id}" cssClass="vdcTooltipLink"/>
                                    <h:outputText value=" | "/>
                                    
                                    <tip:tooltip tooltipMessage="#{studybundle.permissionsTip}" linkText="#{studybundle.permissionsText}"  eventType="mouseover" linkUrl="/dvn#{VDCRequest.currentVDCURL}/faces/study/StudyPermissionsPage.jsp?studyId=#{studyPage.studyUI.study.id}" cssClass="vdcTooltipLink"/>
                                    <h:outputText value=" | "/>
                                    
                                    <tip:tooltip tooltipMessage="#{studybundle.deleteTip}" linkText="#{studybundle.deleteText}"  eventType="mouseover" linkUrl="/dvn#{VDCRequest.currentVDCURL}/faces/study/DeleteStudyPage.jsp?studyId=#{studyPage.studyUI.study.id}" cssClass="vdcTooltipLink"/>
                                    
                                    <h:commandButton rendered="#{studyPage.studyUI.study.new}" value="Ready for Review" actionListener="#{studyPage.setReadyForReview}"/>
                                </ui:panelGroup>
                                <ui:panelGroup rendered="#{studyPage.userAuthorizedToRelease and studyPage.studyUI.study.studyLock==null and studyPage.studyUI.study.inReview}">                              
                                    <h:commandButton  value="Release" actionListener="#{studyPage.setReleased}"/>
                                </ui:panelGroup>
                            
                        </ui:panelGroup>
                        <ui:panelGroup   block="true" style="text-align: left;">
                            
                            <ui:tabSet binding="#{studyPage.tabSet1}" id="tabSet1" lite="true" mini="true"  styleClass="vdcStudyTabSet" >
                                <ui:tab   id="catalog" style="font-weight: bold"
                                          styleClass="vdcStudyTab" text="Cataloging Information" url="#{VDCRequest.currentVDCURL}/faces/study/StudyPage.jsp?studyId=#{studyPage.studyUI.study.id}&amp;tab=catalog">
                                    <ui:panelLayout  id="layoutPanel1" panelLayout="flow" style="margin: 0px; padding: 0px 0px 10px 0px; ">
                                        <ui:panelGroup  block="true" id="groupPanel11" styleClass="vdcStudyInfoHeader" style="margin-top: 5px;">
                                            <h:outputText  id="outputText11" value="Citation Information"/>
                                            <h:commandButton id="commandButtonCitationInfoContract" image="/resources/icon_expand.gif" title="Hide fields in this section" rendered="#{studyPage.studyUI.citationInformationPanelIsRendered}" actionListener="#{studyPage.updateCitationInfoDisplay}" />                      
                                            <h:commandButton id="commandButtonCitationInfoExpand" image="/resources/icon_contract.gif" title="Show fields in this section" rendered="#{!studyPage.studyUI.citationInformationPanelIsRendered}" actionListener="#{studyPage.updateCitationInfoDisplay}"/>    
                                        </ui:panelGroup>
                                        <h:panelGrid binding="#{studyPage.citationInformationPanel}" id="panelGridCitationInfo" cellpadding="0" cellspacing="0" 
                                                     columnClasses="vdcStudyInfoCol1, vdcStudyInfoCol2" columns="2" width="100%" rendered="#{studyPage.studyUI.citationInformationPanelIsRendered}" > 
                                            <ihelp:inlinehelp helpMessage="#{studybundle.howCiteHelp}"  linkText="#{studybundle.howCiteLabel}" heading="#{studybundle.howCiteHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink"/>
                                            <ui:panelGroup  block="true" styleClass="vdcCitationText" >
                                                <h:outputText value="#{studyPage.studyUI.study.citation}" escape="false"/>
                                            </ui:panelGroup>
                                            <ihelp:inlinehelp helpMessage="#{studybundle.subtitleHelp}"  linkText="#{studybundle.subtitleLabel}" heading="#{studybundle.subtitleHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.subTitle!='' and studyPage.studyUI.study.subTitle!=null}"/>
                                            <h:outputText  value="#{studyPage.studyUI.study.subTitle}" rendered="#{studyPage.studyUI.study.subTitle!='' and studyPage.studyUI.study.subTitle!=null}"/>
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.studyGlobalIdHelp}"  linkText="#{studybundle.studyGlobalIdLabel}" heading="#{studybundle.studyGlobalIdHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink"/>
                                            <h:outputText  value="#{studyPage.studyUI.study.globalId}" />
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.otheridHelp}"  linkText="#{studybundle.otheridLabel}" heading="#{studybundle.otheridHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.otherIds != ''}"/>
                                            <h:outputText  value="#{studyPage.studyUI.otherIds}" rendered="#{studyPage.studyUI.otherIds != ''}"/>
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.authorHelp}"  linkText="#{studybundle.authorLabel}" heading="#{studybundle.authorHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.authorAffiliations != ''}" />
                                            <h:outputText   value="#{studyPage.studyUI.authorAffiliations}"  rendered="#{studyPage.studyUI.authorAffiliations != ''}" escape="false"/>
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.producerHelp}"  linkText="#{studybundle.producerLabel}" heading="#{studybundle.producerHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.producers != ''}" />
                                            <h:outputText   value="#{studyPage.studyUI.producers}" rendered="#{studyPage.studyUI.producers != ''}" escape="false" />
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.productionDateHelp}"  linkText="#{studybundle.productionDateLabel}" heading="#{studybundle.productionDateHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.productionDate != '' and studyPage.studyUI.study.productionDate != null}" />
                                            <h:outputText   value="#{studyPage.studyUI.study.productionDate}" rendered="#{studyPage.studyUI.study.productionDate != '' and studyPage.studyUI.study.productionDate != null}" />
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.productionPlaceHelp}"  linkText="#{studybundle.productionPlaceLabel}" heading="#{studybundle.productionPlaceHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.productionPlace != '' and studyPage.studyUI.study.productionPlace != null }" />
                                            <h:outputText   value="#{studyPage.studyUI.study.productionPlace}" rendered="#{studyPage.studyUI.study.productionPlace != '' and studyPage.studyUI.study.productionPlace != null}" escape="false"/>
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.softwareHelp}"  linkText="#{studybundle.softwareLabel}" heading="#{studybundle.softwareHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.software != '' and studyPage.studyUI.software != null}"/>  
                                            <h:outputText   value="#{studyPage.studyUI.software}" rendered="#{studyPage.studyUI.software != '' and studyPage.studyUI.software!= null}" escape="false"/>
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.fundingAgencyHelp}"  linkText="#{studybundle.fundingAgencyLabel}" heading="#{studybundle.fundingAgencyHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.fundingAgency != '' and studyPage.studyUI.study.fundingAgency != null }" />
                                            <h:outputText   value="#{studyPage.studyUI.study.fundingAgency}" rendered="#{studyPage.studyUI.study.fundingAgency != '' and studyPage.studyUI.study.fundingAgency != null }" escape="false"/>
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.grantNumberHelp}"  linkText="#{studybundle.grantNumberLabel}" heading="#{studybundle.grantNumberHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.grants != ''}" />
                                            <h:outputText   value="#{studyPage.studyUI.grants}" rendered="#{studyPage.studyUI.grants != ''}" />
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.distributorHelp}"  linkText="#{studybundle.distributorLabel}" heading="#{studybundle.distributorHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.distributors != ''}"/>
                                            <h:outputText  value="#{studyPage.studyUI.distributors}" rendered="#{studyPage.studyUI.distributors != ''}" escape="false"/>
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.distributorContactHelp}"  linkText="#{studybundle.distributorContactLabel}" heading="#{studybundle.distributorContactHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.distributorContact != ''  }"/>
                                            <h:outputText value="#{studyPage.studyUI.distributorContact}" rendered="#{studyPage.studyUI.distributorContact != '' }"  escape="false"/>
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.distributionDateHelp}"  linkText="#{studybundle.distributionDateLabel}" heading="#{studybundle.distributionDateHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.distributionDate != '' and studyPage.studyUI.study.distributionDate != null }"/>
                                            <h:outputText value="#{studyPage.studyUI.study.distributionDate}" rendered="#{studyPage.studyUI.study.distributionDate != '' and studyPage.studyUI.study.distributionDate != null }"/>
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.depositorHelp}"  linkText="#{studybundle.depositorLabel}" heading="#{studybundle.depositorHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.depositor != '' and studyPage.studyUI.study.depositor != null }"/>
                                            <h:outputText   value="#{studyPage.studyUI.study.depositor}" rendered="#{studyPage.studyUI.study.depositor != '' and studyPage.studyUI.study.depositor != null }" />
                                            <ihelp:inlinehelp helpMessage="#{studybundle.depositDateHelp}"  linkText="#{studybundle.depositDateLabel}" heading="#{studybundle.depositDateHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.dateOfDeposit != '' and studyPage.studyUI.study.dateOfDeposit != null }"/>
                                            <h:outputText   value="#{studyPage.studyUI.study.dateOfDeposit}" rendered="#{studyPage.studyUI.study.dateOfDeposit != '' and studyPage.studyUI.study.dateOfDeposit != null }"/>
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.seriesHelp}"  linkText="#{studybundle.seriesLabel}" heading="#{studybundle.seriesHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.series != '' }"/>
                                            <h:outputText   value="#{studyPage.studyUI.series}" rendered="#{studyPage.studyUI.series != '' }" escape="false"/>
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.seriesVersionHelp}"  linkText="#{studybundle.seriesVersionLabel}" heading="#{studybundle.seriesVersionHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.studyVersion != '' }"/>
                                            <h:outputText   value="#{studyPage.studyUI.studyVersion}" rendered="#{studyPage.studyUI.studyVersion!=''}"/>
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.replicationForHelp}"  linkText="#{studybundle.replicationForLabel}" heading="#{studybundle.replicationForHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.replicationFor != '' and studyPage.studyUI.study.replicationFor != null }"/>
                                            <h:outputText   value="#{studyPage.studyUI.study.replicationFor}" rendered="#{studyPage.studyUI.study.replicationFor != '' and studyPage.studyUI.study.replicationFor != null }" escape="false" />
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.dataverseOwnerHelp}"  linkText="#{studybundle.dataverseOwnerLabel}" heading="#{studybundle.dataverseOwnerHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink"/>  
                                            <h:outputLink value="/dvn/dv/#{studyPage.studyUI.study.owner.alias}">
                                                <h:outputText value="#{studyPage.studyUI.study.owner.name} Dataverse" />
                                            </h:outputLink> 
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.harvestHoldingsHelp}"  linkText="#{studybundle.harvestHoldingsLabel}" heading="#{studybundle.harvestHoldingsHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.harvestHoldings != '' and studyPage.studyUI.study.isHarvested}"/>
                                            <h:outputText   value="#{studyPage.studyUI.study.harvestHoldings}" rendered="#{studyPage.studyUI.study.harvestHoldings!='' and studyPage.studyUI.study.isHarvested}"/>
                                        </h:panelGrid>
                                        
                                        <ui:panelGroup block="true" id="groupPanel12" styleClass="vdcStudyInfoHeader" rendered="#{!studyPage.abstractAndScopePanelIsEmpty}" >
                                            <h:outputText   id="AbstractAndScopeText" value="Abstract and Scope"/>
                                            <h:commandButton id="commandButtonAbstractScopeContract"  image="/resources/icon_expand.gif" title="Hide fields in this section" rendered="#{studyPage.studyUI.abstractAndScopePanelIsRendered }" actionListener="#{studyPage.updateAbstractScopeDisplay}"/> 
                                            <h:commandButton id="commandButtonAbstractScopeExpand" image="/resources/icon_contract.gif" title="Show fields in this section"  rendered="#{!studyPage.studyUI.abstractAndScopePanelIsRendered }" actionListener="#{studyPage.updateAbstractScopeDisplay}"/> 
                                            <!--h:outputText   value="(This section is empty)" rendered="#{studyPage.abstractAndScopePanelIsEmpty}" /-->
                                        </ui:panelGroup>
                                        <h:panelGrid   binding="#{studyPage.abstractAndScopePanel}"  cellpadding="0" cellspacing="0"
                                                       columnClasses="vdcStudyInfoCol1, vdcStudyInfoCol2" columns="2" id="gridPanelAbstractAndScope" rendered="#{studyPage.studyUI.abstractAndScopePanelIsRendered}"  width="100%">
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.abstractHelp}"  linkText="#{studybundle.abstractLabel}" heading="#{studybundle.abstractHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.abstracts != null and studyPage.studyUI.abstracts != ''}"/>
                                            <h:outputText   value="#{studyPage.studyUI.abstracts}" rendered="#{studyPage.studyUI.abstracts !=  null and studyPage.studyUI.abstracts != ''}" escape="false"/>
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.abstractDateHelp}"  linkText="#{studybundle.abstractDateLabel}" heading="#{studybundle.abstractDateHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.abstractDates != null and studyPage.studyUI.abstractDates != ''}" />
                                            <h:outputText   value="#{studyPage.studyUI.abstractDates}" rendered="#{studyPage.studyUI.abstractDates != null and studyPage.studyUI.abstractDates != ''}"/>
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.keywordsHelp}"  linkText="#{studybundle.keywordsLabel}" heading="#{studybundle.keywordsHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.keywords != ''}"/>
                                            <h:outputText   value="#{studyPage.studyUI.keywords}" rendered="#{studyPage.studyUI.keywords != ''}" escape="false"/>
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.topicClassificationHelp}"  linkText="#{studybundle.topicClassificationLabel}" heading="#{studybundle.topicClassificationHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.topicClasses != ''}"/>
                                            <h:outputText   value="#{studyPage.studyUI.topicClasses}" rendered="#{studyPage.studyUI.topicClasses != ''}" escape="false"/>                                          
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.relatedPublicationsHelp}"  linkText="#{studybundle.relatedPublicationsLabel}" heading="#{studybundle.relatedPublicationsHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.relPublications != '' and studyPage.studyUI.relPublications != null }"/>
                                            <h:outputText   value="#{studyPage.studyUI.relPublications}" rendered="#{studyPage.studyUI.relPublications != '' and studyPage.studyUI.relPublications != null }" escape="false" />
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.relatedMaterialHelp}"  linkText="#{studybundle.relatedMaterialLabel}" heading="#{studybundle.relatedMaterialHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.relMaterials != '' and studyPage.studyUI.relMaterials != null }" />
                                            <h:outputText  id="outputText150" value="#{studyPage.studyUI.relMaterials}" rendered="#{studyPage.studyUI.relMaterials != '' and studyPage.studyUI.relMaterials != null }" escape="false"/>
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.relatedStudiesHelp}"  linkText="#{studybundle.relatedStudiesLabel}" heading="#{studybundle.relatedStudiesHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.relStudies != '' and studyPage.studyUI.relStudies != null }"/>
                                            <h:outputText  id="outputText152" value="#{studyPage.studyUI.relStudies}" rendered="#{studyPage.studyUI.relStudies != '' and studyPage.studyUI.relStudies != null }" escape="false"/>
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.otherReferencesHelp}"  linkText="#{studybundle.otherReferencesLabel}" heading="#{studybundle.otherReferencesHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.otherRefs != '' and studyPage.studyUI.otherRefs != null }" />
                                            <h:outputText  id="outputText154" value="#{studyPage.studyUI.otherRefs}" rendered="#{studyPage.studyUI.otherRefs != '' and studyPage.studyUI.otherRefs != null }" escape="false"/>                                            
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.timePeriodCoveredHelp}"  linkText="#{studybundle.timePeriodCoveredLabel}" heading="#{studybundle.timePeriodCoveredHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.timePeriodCovered != '' }"/>
                                            <h:outputText   value="#{studyPage.studyUI.timePeriodCovered }" rendered="#{studyPage.studyUI.timePeriodCovered != '' }" />
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.collectionDateHelp}"  linkText="#{studybundle.collectionDateLabel}" heading="#{studybundle.collectionDateHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.dateOfCollection != ''  }"/>
                                            <h:outputText   value="#{studyPage.studyUI.dateOfCollection} " rendered="#{studyPage.studyUI.dateOfCollection != ''  }"/>
                                            
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.countryHelp}"  linkText="#{studybundle.countryLabel}" heading="#{studybundle.countryHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.country != '' and studyPage.studyUI.study.country != null }"/>
                                            <h:outputText   value="#{studyPage.studyUI.study.country}" rendered="#{studyPage.studyUI.study.country != '' and studyPage.studyUI.study.country != null }"/>
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.geographicCoverageHelp}"  linkText="#{studybundle.geographicCoverageLabel}" heading="#{studybundle.geographicCoverageHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.geographicCoverage != '' and studyPage.studyUI.study.geographicCoverage!= null }"/>
                                            <h:outputText   value="#{studyPage.studyUI.study.geographicCoverage}" rendered="#{studyPage.studyUI.study.geographicCoverage != '' and studyPage.studyUI.study.geographicCoverage != null }"/>
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.geographicUnitHelp}"  linkText="#{studybundle.geographicUnitLabel}" heading="#{studybundle.geographicUnitHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.geographicUnit != '' and studyPage.studyUI.study.geographicUnit != null }"/>
                                            <h:outputText   value="#{studyPage.studyUI.study.geographicUnit}" rendered="#{studyPage.studyUI.study.geographicUnit != '' and studyPage.studyUI.study.geographicUnit != null }"/>
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.geographicBoundingHelp}"  linkText="#{studybundle.geographicBoundingLabel}" heading="#{studybundle.geographicBoundingHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.geographicBoundings != '' and studyPage.studyUI.geographicBoundings != null }"/>
                                            <h:outputText   value="#{studyPage.studyUI.geographicBoundings}" rendered="#{studyPage.studyUI.geographicBoundings != '' and studyPage.studyUI.geographicBoundings != null }"/>
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.unitOfAnalysisHelp}"  linkText="#{studybundle.unitOfAnalysisLabel}" heading="#{studybundle.unitOfAnalysisHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.unitOfAnalysis != '' and studyPage.studyUI.study.unitOfAnalysis != null }" />
                                            <h:outputText   value="#{studyPage.studyUI.study.unitOfAnalysis}" rendered="#{studyPage.studyUI.study.unitOfAnalysis != '' and studyPage.studyUI.study.unitOfAnalysis != null }"/>
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.universeHelp}"  linkText="#{studybundle.universeLabel}" heading="#{studybundle.universeHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.universe != '' and studyPage.studyUI.study.universe != null }"/>
                                            <h:outputText   value="#{studyPage.studyUI.study.universe}" rendered="#{studyPage.studyUI.study.universe != '' and studyPage.studyUI.study.universe != null }"/>
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.kindOfDataHelp}"  linkText="#{studybundle.kindOfDataLabel}" heading="#{studybundle.kindOfDataHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.kindOfData != '' and studyPage.studyUI.study.kindOfData != null }"/>
                                            <h:outputText   value="#{studyPage.studyUI.study.kindOfData}" rendered="#{studyPage.studyUI.study.kindOfData != '' and studyPage.studyUI.study.kindOfData != null }"/>
                                        </h:panelGrid>
                                        
                                        <ui:panelGroup  block="true" id="groupPanel13" styleClass="vdcStudyInfoHeader" rendered="#{!studyPage.dataCollectionIsEmpty}"
                                        >
                                            <h:outputText  id="outputText78" value="Data Collection / Methodology"/>
                                            <h:commandButton id="commandButtondataCollectionoContract" image="/resources/icon_expand.gif" title="Hide fields in this section" rendered="#{studyPage.studyUI.dataCollectionPanelIsRendered}" actionListener="#{studyPage.updateDataCollectionDisplay}" />                      
                                            <h:commandButton id="commandButtondataCollectionExpand" image="/resources/icon_contract.gif" title="Show fields in this section" rendered="#{!studyPage.studyUI.dataCollectionPanelIsRendered}" actionListener="#{studyPage.updateDataCollectionDisplay}"/> 
                                        </ui:panelGroup>
                                        <h:panelGrid   binding="#{studyPage.dataCollectionPanel}" cellpadding="0" cellspacing="0"
                                                       columnClasses="vdcStudyInfoCol1, vdcStudyInfoCol2" columns="2" id="gridPanelDataCollection" 
                                                       width="100%"  rendered="#{studyPage.studyUI.dataCollectionPanelIsRendered}" > 
                                            <ihelp:inlinehelp helpMessage="#{studybundle.timeMethodHelp}"  linkText="#{studybundle.timeMethodLabel}" heading="#{studybundle.timeMethodHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink"  rendered="#{studyPage.studyUI.study.timeMethod != '' and studyPage.studyUI.study.timeMethod != null }"/>
                                            <h:outputText   value="#{studyPage.studyUI.study.timeMethod}"  rendered="#{studyPage.studyUI.study.timeMethod != '' and studyPage.studyUI.study.timeMethod != null }" escape="false"/>
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.dataCollectorHelp}"  linkText="#{studybundle.dataCollectorLabel}" heading="#{studybundle.dataCollectorHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink"  rendered="#{studyPage.studyUI.study.dataCollector != '' and studyPage.studyUI.study.dataCollector != null }"/> 
                                            <h:outputText  id="outputText77" value="#{studyPage.studyUI.study.dataCollector}"  rendered="#{studyPage.studyUI.study.dataCollector != '' and studyPage.studyUI.study.dataCollector != null }" escape="false"/>
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.frequencyHelp}"  linkText="#{studybundle.frequencyLabel}" heading="#{studybundle.frequencyHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink"  rendered="#{studyPage.studyUI.study.frequencyOfDataCollection != '' and studyPage.studyUI.study.frequencyOfDataCollection != null }"/>
                                            <h:outputText  id="outputText80" value="#{studyPage.studyUI.study.frequencyOfDataCollection}" rendered="#{studyPage.studyUI.study.frequencyOfDataCollection != '' and studyPage.studyUI.study.frequencyOfDataCollection != null }" escape="false"/>
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.samplingProcedureHelp}"  linkText="#{studybundle.samplingProcedureLabel}" heading="#{studybundle.samplingProcedureHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink"  rendered="#{studyPage.studyUI.study.samplingProcedure != '' and studyPage.studyUI.study.samplingProcedure != null }"/>
                                            <h:outputText  id="outputText82" value="#{studyPage.studyUI.study.samplingProcedure}"  rendered="#{studyPage.studyUI.study.samplingProcedure != '' and studyPage.studyUI.study.samplingProcedure != null }" escape="false"/>
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.majorDeviationsHelp}"  linkText="#{studybundle.majorDeviationsLabel}" heading="#{studybundle.majorDeviationsHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.deviationsFromSampleDesign != '' and studyPage.studyUI.study.deviationsFromSampleDesign != null }"/>
                                            <h:outputText  id="outputText84" value="#{studyPage.studyUI.study.deviationsFromSampleDesign}"  rendered="#{studyPage.studyUI.study.deviationsFromSampleDesign != '' and studyPage.studyUI.study.deviationsFromSampleDesign != null }" escape="false"/>
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.collectionModeHelp}"  linkText="#{studybundle.collectionModeLabel}" heading="#{studybundle.collectionModeHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.collectionMode != '' and studyPage.studyUI.study.collectionMode != null }"/>
                                            <h:outputText  id="outputText86" value="#{studyPage.studyUI.study.collectionMode}"  rendered="#{studyPage.studyUI.study.collectionMode != '' and studyPage.studyUI.study.collectionMode != null }" escape="false"/>
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.researchInstrumentHelp}"  linkText="#{studybundle.researchInstrumentLabel}" heading="#{studybundle.researchInstrumentHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.researchInstrument != '' and studyPage.studyUI.study.researchInstrument != null }"/>
                                            <h:outputText  id="outputText88" value="#{studyPage.studyUI.study.researchInstrument}"  rendered="#{studyPage.studyUI.study.researchInstrument != '' and studyPage.studyUI.study.researchInstrument != null }" escape="false"/>
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.dataSourcesHelp}"  linkText="#{studybundle.dataSourcesLabel}" heading="#{studybundle.dataSourcesHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.dataSources != '' and studyPage.studyUI.study.dataSources != null }"/>
                                            <h:outputText  id="outputText90" value="#{studyPage.studyUI.study.dataSources}"  rendered="#{studyPage.studyUI.study.dataSources != '' and studyPage.studyUI.study.dataSources != null }" escape="false"/>
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.originOfSourcesHelp}"  linkText="#{studybundle.originOfSourcesLabel}" heading="#{studybundle.originOfSourcesHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.originOfSources != '' and studyPage.studyUI.study.originOfSources != null }"/>
                                            <h:outputText  id="outputText92" value="#{studyPage.studyUI.study.originOfSources}"  rendered="#{studyPage.studyUI.study.originOfSources != '' and studyPage.studyUI.study.originOfSources != null }" escape="false"/>
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.sourceCharacteristicsHelp}"  linkText="#{studybundle.sourceCharacteristicsLabel}" heading="#{studybundle.sourceCharacteristicsHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.characteristicOfSources != '' and studyPage.studyUI.study.characteristicOfSources != null }"/>
                                            <h:outputText  id="outputText94" value="#{studyPage.studyUI.study.characteristicOfSources}" rendered="#{studyPage.studyUI.study.characteristicOfSources != '' and studyPage.studyUI.study.characteristicOfSources != null }" escape="false"/>
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.sourceDocumentationHelp}"  linkText="#{studybundle.sourceDocumentationLabel}" heading="#{studybundle.sourceDocumentationHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.accessToSources != '' and studyPage.studyUI.study.accessToSources != null }"/>
                                            <h:outputText  id="outputText96" value="#{studyPage.studyUI.study.accessToSources}"  rendered="#{studyPage.studyUI.study.accessToSources != '' and studyPage.studyUI.study.accessToSources != null }" escape="false"/>
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.dataCollectionCharacteristicsHelp}"  linkText="#{studybundle.dataCollectionCharacteristicsLabel}" heading="#{studybundle.dataCollectionCharacteristicsHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.dataCollectionSituation != '' and studyPage.studyUI.study.dataCollectionSituation != null }"/>
                                            <h:outputText  id="outputText98" value="#{studyPage.studyUI.study.dataCollectionSituation}"  rendered="#{studyPage.studyUI.study.dataCollectionSituation != '' and studyPage.studyUI.study.dataCollectionSituation != null }" escape="false"/>
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.minimizeActionsHelp}"  linkText="#{studybundle.minimizeActionsLabel}" heading="#{studybundle.minimizeActionsHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.actionsToMinimizeLoss != '' and studyPage.studyUI.study.actionsToMinimizeLoss != null }"/>
                                            <h:outputText  id="outputText100" value="#{studyPage.studyUI.study.actionsToMinimizeLoss}"  rendered="#{studyPage.studyUI.study.actionsToMinimizeLoss!= '' and studyPage.studyUI.study.actionsToMinimizeLoss != null }" escape="false"/>
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.controlOperationsHelp}"  linkText="#{studybundle.controlOperationsLabel}" heading="#{studybundle.controlOperationsHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.controlOperations != '' and studyPage.studyUI.study.controlOperations != null }"/>
                                            <h:outputText  id="outputText102" value="#{studyPage.studyUI.study.controlOperations}"  rendered="#{studyPage.studyUI.study.controlOperations != '' and studyPage.studyUI.study.controlOperations != null }" escape="false"/>
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.weightingHelp}"  linkText="#{studybundle.weightingLabel}" heading="#{studybundle.weightingHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.weighting != '' and studyPage.studyUI.study.weighting != null }"/>
                                            <h:outputText  id="outputText104" value="#{studyPage.studyUI.study.weighting}"  rendered="#{studyPage.studyUI.study.weighting != '' and studyPage.studyUI.study.weighting != null }" escape="false"/>
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.cleaningOperationsHelp}"  linkText="#{studybundle.cleaningOperationsLabel}" heading="#{studybundle.cleaningOperationsHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.cleaningOperations != '' and studyPage.studyUI.study.cleaningOperations != null }"/>
                                            <h:outputText  id="outputText106" value="#{studyPage.studyUI.study.cleaningOperations}"  rendered="#{studyPage.studyUI.study.cleaningOperations != '' and studyPage.studyUI.study.cleaningOperations != null }" escape="false"/>
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.studyLevelErrorNotesHelp}"  linkText="#{studybundle.studyLevelErrorNotesLabel}" heading="#{studybundle.studyLevelErrorNotesHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.studyLevelErrorNotes != '' and studyPage.studyUI.study.studyLevelErrorNotes != null }"/>
                                            <h:outputText  id="outputText108" value="#{studyPage.studyUI.study.studyLevelErrorNotes}"  rendered="#{studyPage.studyUI.study.studyLevelErrorNotes != '' and studyPage.studyUI.study.studyLevelErrorNotes != null }" escape="false"/>
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.responseRateHelp}"  linkText="#{studybundle.responseRateLabel}" heading="#{studybundle.responseRateHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.responseRate != '' and studyPage.studyUI.study.responseRate != null }"/>
                                            <h:outputText  id="outputText110" value="#{studyPage.studyUI.study.responseRate}"  rendered="#{studyPage.studyUI.study.responseRate != '' and studyPage.studyUI.study.responseRate != null }" escape="false"/>
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.samplingErrorHelp}"  linkText="#{studybundle.samplingErrorLabel}" heading="#{studybundle.samplingErrorHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.samplingErrorEstimate != '' and studyPage.studyUI.study.samplingErrorEstimate != null }"/>
                                            <h:outputText  id="outputText112" value="#{studyPage.studyUI.study.samplingErrorEstimate}"  rendered="#{studyPage.studyUI.study.samplingErrorEstimate != '' and studyPage.studyUI.study.samplingErrorEstimate != null }" escape="false"/>
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.otherDataAppraisalHelp}"  linkText="#{studybundle.otherDataAppraisalLabel}" heading="#{studybundle.otherDataAppraisalHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.otherDataAppraisal != '' and studyPage.studyUI.study.otherDataAppraisal != null }"/>
                                            <h:outputText  id="outputText114" value="#{studyPage.studyUI.study.otherDataAppraisal}"  rendered="#{studyPage.studyUI.study.otherDataAppraisal != '' and studyPage.studyUI.study.otherDataAppraisal != null }" escape="false" />
                                        </h:panelGrid>
                                        <ui:panelGroup   block="true" id="groupPanel14" styleClass="vdcStudyInfoHeader" rendered="#{!studyPage.termsOfUseIsEmpty}">
                                            <h:outputText  id="outputText115" value="Terms of Use"/>
                                            <h:commandButton id="commandButtonTermsOfUseContract" image="/resources/icon_expand.gif" title="Hide fields in this section" rendered="#{studyPage.studyUI.termsOfUsePanelIsRendered}" actionListener="#{studyPage.updateTermsOfUseDisplay}" />                      
                                            <h:commandButton id="commandButtonTermsOfUseExpand" image="/resources/icon_contract.gif" title="Show fields in this section" rendered="#{!studyPage.studyUI.termsOfUsePanelIsRendered}" actionListener="#{studyPage.updateTermsOfUseDisplay}"/> 
                                        </ui:panelGroup>
                                        <h:panelGrid binding="#{studyPage.termsOfUsePanel}" cellpadding="0" cellspacing="0"
                                                     columnClasses="vdcStudyInfoCol1, vdcStudyInfoCol2" columns="2" id="gridPanelTermsOfUse"  rendered="#{studyPage.studyUI.termsOfUsePanelIsRendered}"
                                                     width="100%">
                                            <ihelp:inlinehelp helpMessage="#{studybundle.dataAccessPlaceHelp}"  linkText="#{studybundle.dataAccessPlaceLabel}" heading="#{studybundle.dataAccessPlaceHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.placeOfAccess != '' and studyPage.studyUI.study.placeOfAccess != null }"/>
                                            <h:outputText  id="outputText117" value="#{studyPage.studyUI.study.placeOfAccess}"  rendered="#{studyPage.studyUI.study.placeOfAccess != '' and studyPage.studyUI.study.placeOfAccess != null }" escape="false"/>
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.originalArchiveHelp}"  linkText="#{studybundle.originalArchiveLabel}" heading="#{studybundle.originalArchiveHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.originalArchive != '' and studyPage.studyUI.study.originalArchive != null }"/>
                                            <h:outputText  id="outputText119" value="#{studyPage.studyUI.study.originalArchive}"  rendered="#{studyPage.studyUI.study.originalArchive != '' and studyPage.studyUI.study.originalArchive != null }" escape="false"/>
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.availabilityHelp}"  linkText="#{studybundle.availabilityLabel}" heading="#{studybundle.availabilityHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.availabilityStatus != '' and studyPage.studyUI.study.availabilityStatus != null }"/>
                                            <h:outputText  id="outputText121" value="#{studyPage.studyUI.study.availabilityStatus}"  rendered="#{studyPage.studyUI.study.availabilityStatus != '' and studyPage.studyUI.study.availabilityStatus != null }" escape="false" />
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.collectionSizeHelp}"  linkText="#{studybundle.collectionSizeLabel}" heading="#{studybundle.collectionSizeHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.collectionSize != '' and studyPage.studyUI.study.collectionSize != null }" />
                                            <h:outputText  id="outputText123" value="#{studyPage.studyUI.study.collectionSize}"  rendered="#{studyPage.studyUI.study.collectionSize != '' and studyPage.studyUI.study.collectionSize != null }" escape="false"/>
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.studyCompletionHelp}"  linkText="#{studybundle.studyCompletionLabel}" heading="#{studybundle.studyCompletionHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.studyCompletion != '' and studyPage.studyUI.study.studyCompletion != null }"/>
                                            <h:outputText  id="outputText125" value="#{studyPage.studyUI.study.studyCompletion}"  rendered="#{studyPage.studyUI.study.studyCompletion != '' and studyPage.studyUI.study.studyCompletion != null }" escape="false"/>
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.numberOfFilesHelp}"  linkText="#{studybundle.numberOfFilesLabel}" heading="#{studybundle.numberOfFilesHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.numberOfFiles != '' and studyPage.studyUI.study.numberOfFiles != null }"/>
                                            <h:outputText  id="outputText127" value="#{studyPage.studyUI.study.numberOfFiles}" rendered="#{studyPage.studyUI.study.numberOfFiles!= '' and studyPage.studyUI.study.numberOfFiles != null }" escape="false"/>
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.confidentialityHelp}"  linkText="#{studybundle.confidentialityLabel}" heading="#{studybundle.confidentialityHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.confidentialityDeclaration != '' and studyPage.studyUI.study.confidentialityDeclaration != null }"/>
                                            <h:outputText  id="outputText129" value="#{studyPage.studyUI.study.confidentialityDeclaration}" rendered="#{studyPage.studyUI.study.confidentialityDeclaration != '' and studyPage.studyUI.study.confidentialityDeclaration != null }" escape="false"/>
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.specialPermissionsHelp}"  linkText="#{studybundle.specialPermissionsLabel}" heading="#{studybundle.specialPermissionsHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.specialPermissions != '' and studyPage.studyUI.study.specialPermissions != null }"/>
                                            <h:outputText  id="outputText131" value="#{studyPage.studyUI.study.specialPermissions}" rendered="#{studyPage.studyUI.study.specialPermissions != '' and studyPage.studyUI.study.specialPermissions != null }" escape="false"/>
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.restrictionsHelp}"  linkText="#{studybundle.restrictionsLabel}" heading="#{studybundle.restrictionsHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.restrictions != '' and studyPage.studyUI.study.restrictions != null }" />
                                            <h:outputText  id="outputText133" value="#{studyPage.studyUI.study.restrictions}" rendered="#{studyPage.studyUI.study.restrictions != '' and studyPage.studyUI.study.restrictions != null }" escape="false"/>
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.contactHelp}"  linkText="#{studybundle.contactLabel}" heading="#{studybundle.contactHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.contact != '' and studyPage.studyUI.study.contact != null }" />
                                            <h:outputText  id="outputText135" value="#{studyPage.studyUI.study.contact}" rendered="#{studyPage.studyUI.study.contact != '' and studyPage.studyUI.study.contact != null }" escape="false"/>
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.citationRequirementsHelp}"  linkText="#{studybundle.citationRequirementsLabel}" heading="#{studybundle.citationRequirementsHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.citationRequirements != '' and studyPage.studyUI.study.citationRequirements != null }"/>
                                            <h:outputText  id="outputText137" value="#{studyPage.studyUI.study.citationRequirements}" rendered="#{studyPage.studyUI.study.citationRequirements != '' and studyPage.studyUI.study.citationRequirements != null }" escape="false"/>
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.depositorRequirementsHelp}"  linkText="#{studybundle.depositorRequirementsLabel}" heading="#{studybundle.depositorRequirementsHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.depositorRequirements != '' and studyPage.studyUI.study.depositorRequirements != null }"/>
                                            <h:outputText  id="outputText139" value="#{studyPage.studyUI.study.depositorRequirements}" rendered="#{studyPage.studyUI.study.depositorRequirements != '' and studyPage.studyUI.study.depositorRequirements != null }" escape="false"/>
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.conditionsHelp}"  linkText="#{studybundle.conditionsLabel}" heading="#{studybundle.conditionsHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.conditions != '' and studyPage.studyUI.study.conditions != null }"/>
                                            <h:outputText  id="outputText141" value="#{studyPage.studyUI.study.conditions}" rendered="#{studyPage.studyUI.study.conditions != '' and studyPage.studyUI.study.conditions != null }" escape="false"/>
                                            
                                            <ihelp:inlinehelp helpMessage="#{studybundle.disclaimerHelp}"  linkText="#{studybundle.disclaimerLabel}" heading="#{studybundle.disclaimerHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.study.disclaimer != '' and studyPage.studyUI.study.disclaimer != null }"/>
                                            <h:outputText  id="outputText143" value="#{studyPage.studyUI.study.disclaimer}" rendered="#{studyPage.studyUI.study.disclaimer != '' and studyPage.studyUI.study.disclaimer != null }" escape="false"/>
                                        </h:panelGrid>
                                        <ui:panelGroup  block="true" id="groupPanel15" styleClass="vdcStudyInfoHeader" rendered="#{!studyPage.notesIsEmpty}">
                                            <h:outputText  id="outputText144" value="Other Information"/>
                                            <h:commandButton id="commandButtonNotesContract" image="/resources/icon_expand.gif" title="Hide fields in this section" rendered="#{studyPage.studyUI.notesPanelIsRendered}" actionListener="#{studyPage.updateNotesDisplay}" />                      
                                            <h:commandButton id="commandButtonNotesExpand" image="/resources/icon_contract.gif" title="Show fields in this section" rendered="#{!studyPage.studyUI.notesPanelIsRendered}" actionListener="#{studyPage.updateNotesDisplay}"/> 
                                        </ui:panelGroup>
                                        <h:panelGrid   binding="#{studyPage.notesPanel}" cellpadding="0" cellspacing="0"
                                                       columnClasses="vdcStudyInfoCol1, vdcStudyInfoCol2" columns="2" id="gridPanelNotes"  rendered="#{studyPage.studyUI.notesPanelIsRendered}" 
                                                       width="100%">
                                            <ihelp:inlinehelp helpMessage="#{studybundle.notesHelp}"  linkText="#{studybundle.notesLabel}" heading="#{studybundle.notesHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="#{studyPage.studyUI.notes != ''}"/>
                                            <h:outputText  id="outputText146" value="#{studyPage.studyUI.notes}" rendered="#{studyPage.studyUI.notes != '' }" escape="false"/>
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
                                
                                <ui:tab  id="files"  style="font-weight: bold" text="Study Files" url="#{VDCRequest.currentVDCURL}/faces/study/StudyPage.jsp?studyId=#{studyPage.studyUI.study.id}&amp;tab=files">
                                    <ui:panelLayout  id="noFilesPanel1" panelLayout="flow" style="margin: 0px; padding: 0px 0px 10px 0px; " rendered="#{empty studyPage.studyUI.study.fileCategories}">
                                        <ui:panelGroup  block="true" id="noFilesPanel2" style="padding-top: 30px; padding-bottom: 30px; padding-left: 20px;" rendered="#{!studyPage.userAuthorizedToEdit}">
                                            <h:outputText value="No files have been provided for this study."/>
                                        </ui:panelGroup>
                                        <ui:panelGroup  block="true" id="noFilesPanel3" style="padding-top: 30px; padding-bottom: 30px; padding-left: 20px;" rendered="#{studyPage.userAuthorizedToEdit and (studyPage.studyUI.study.studyLock==null)}">
                                            <h:outputText value="No files have been yet been uploaded to this study. To add a file, follow the 'Add File(s)' link on the top right of this page."/>
                                        </ui:panelGroup>
                                        <ui:panelGroup  block="true" id="noFilesPanel4a" style="padding-top: 30px; padding-bottom: 30px; padding-left: 20px;" rendered="#{studyPage.userAuthorizedToEdit and !(studyPage.studyUI.study.studyLock==null)}">
                                            <h:outputText styleClass="warnMessage" value="One or more data files are being uploaded..."/>
                                        </ui:panelGroup>
                                    </ui:panelLayout>
                                   <ui:panelGroup  block="true" id="noFilesPanel4b" style="align-text: left; padding-top: 10px; padding-bottom: 10px;" rendered="#{studyPage.userAuthorizedToEdit and !(studyPage.studyUI.study.studyLock==null) and !empty studyPage.studyUI.study.fileCategories}">
                                            <h:outputText styleClass="warnMessage" value="One or more data files are being uploaded..."/>
                                        </ui:panelGroup>
                                    <ui:panelLayout  id="layoutPanel2" panelLayout="flow" style="margin: 0px; padding: 0px 0px 10px 0px; " rendered="#{!empty studyPage.studyUI.study.fileCategories}">
                                        <ui:panelGroup  block="true" styleClass="vdcRequestPanelFiles" rendered="#{studyPage.studyUI.anyFileRestricted and studyPage.studyUI.study.requestAccess}">
                                            <h:outputText value="Would you like to access the restricted files in this study?" />
                                            <h:outputLink rendered="#{VDCSession.loginBean!=null}" value="/dvn#{VDCRequest.currentVDCURL}/faces/login/FileRequestPage.jsp?studyId=#{studyPage.studyId}" styleClass="vdcRequestPanelLink">
                                                <h:outputText  value="Send a Request"/>
                                            </h:outputLink>            
                                            <h:outputLink rendered="#{VDCSession.loginBean==null}" value="/dvn#{VDCRequest.currentVDCURL}/faces/login/FileRequestAccountPage.jsp?studyId=#{studyPage.studyId}" styleClass="vdcRequestPanelLink">
                                                <h:outputText  value="Send a Request"/>
                                            </h:outputLink>            
                                        </ui:panelGroup>
                                       
                                        <ui:panelGroup  block="true" id="groupPanel7a" style="align-text: left; padding-bottom: 2px; padding-top: 5px;">
                                            <h:outputText  id="outputText33"  value="Download all files in a single archive file (files that you cannot access will not be downloaded): "/>                                            
                                            <h:graphicImage  value="/resources/icon_downloadall_locked.gif" rendered="#{!studyPage.studyUI.anyFileUnrestricted}"
                                                             alt="You do not have permissions to access any files in this study." title="You do not have permissions to access any files in this study."/>
                                            <h:outputLink  id="linkAction7" onclick="return checkTerms(this.id);" value="/dvn#{VDCRequest.currentVDCURL}/FileDownload/study_#{studyPage.studyUI.study.studyId}.zip?studyId=#{studyPage.studyUI.study.id}" title="Download all files in a single archive file." rendered="#{studyPage.studyUI.anyFileUnrestricted}">
                                                <h:graphicImage  id="image7" styleClass="vdcNoBorders" value="/resources/icon_downloadall.gif"/>
                                            </h:outputLink>
                                        </ui:panelGroup>
 
                                        
                                        <h:panelGrid cellpadding="3" cellspacing="0" columnClasses="vdcStudyFilesHead1, vdcStudyFilesHead2, vdcStudyFilesHead3, vdcStudyFilesHead4, vdcStudyFilesHead5, vdcStudyFilesHead6" columns="6" width="100%" rendered="#{!empty studyPage.studyUI.study.fileCategories}" > 
                                           <h:outputText value="File Name"/>
                                           <h:outputText value="Description"/>
                                           <h:outputText value="Type"/>
                                           <h:outputText value="Cases"/>
                                           <h:outputText value="Variables"/>
                                           <h:outputText value="Controls"/> 
                                        </h:panelGrid>
                                        
                                        <h:dataTable  cellpadding="0" cellspacing="0" value="#{studyPage.studyUI.categoryUIList}" id="catDataTable" var="catUI" width="100%">                                        
                                            <h:column  id="catColumn">
                                                <ui:panelGroup  block="true" id="groupPanel4" styleClass="vdcStudyFilesCat">
                                                    <h:commandLink  id="linkAction1" actionListener="#{catUI.toggleRendered}">
                                                        <h:graphicImage  id="image1a" styleClass="vdcNoBorders" value="/resources/icon_expand.gif" title="Hide files in this category" rendered="#{catUI.rendered}"/>
                                                        <h:graphicImage  id="image1b" styleClass="vdcNoBorders" value="/resources/icon_contract.gif" title="Show files in this category" rendered="#{!catUI.rendered}"/>
                                                    </h:commandLink>
                                                    <h:outputText  id="outputText16" styleClass="vdcStudyFilesCatLabel" value="#{catUI.fileCategory.name}"/>
                                                    <h:graphicImage   styleClass="vdcNoBorders" value="/resources/icon_downloadall_locked.gif" rendered="#{!catUI.anyFileUnrestricted}" alt="You do not have permissions to access any files in this category." title="You do not have permission to access any files in this category." />
                                                    <h:outputLink   id="catDownloadLink" onclick="return checkTerms(this.id);" value="/dvn#{VDCRequest.currentVDCURL}/FileDownload/study_#{studyPage.studyUI.study.studyId}_#{catUI.downloadName}.zip?catId=#{catUI.fileCategory.id}" title="Download all files in this category in a single archive file." rendered="#{catUI.anyFileUnrestricted}">
                                                        <h:graphicImage  styleClass="vdcNoBorders" value="/resources/icon_downloadall.gif" />
                                                    </h:outputLink>
                                                </ui:panelGroup>
                                                <h:dataTable  rendered="#{catUI.rendered}" cellpadding="0" cellspacing="1"
                                                              columnClasses="vdcStudyFilesCol1, vdcStudyFilesCol2, vdcStudyFilesCol3, vdcStudyFilesCol4, vdcStudyFilesCol5, vdcStudyFilesCol6"
                                                              headerClass="vdcStudyFilesHeader" id="dataTable5" rowClasses="list-row-even,list-row-odd"
                                                              value="#{catUI.studyFileUIs}" var="studyFileUI" width="100%">
                                                    <h:column  id="column9">
                                                        <h:outputText  id="outputText15" value="#{studyFileUI.studyFile.fileName}"/>
                                                    </h:column>
                                                    <h:column  id="column10">
                                                        <h:outputText  id="outputText17" value="#{studyFileUI.studyFile.description}"/>
                                                    </h:column>
                                                    <h:column  id="column11">
                                                        <h:outputText  id="outputText19" value="#{studyFileUI.studyFile.fileType}"/>
                                                    </h:column>
                                                    <h:column  id="column12a">
                                                        <h:outputText value="#{studyFileUI.studyFile.dataTable.caseQuantity}"/>                         
                                                    </h:column>
                                                    <h:column  id="column12b">
                                                        <h:outputText value="#{studyFileUI.studyFile.dataTable.varQuantity}"/>
                                                    </h:column>
                                                    <h:column  id="column13">
                                                        <ui:panelGroup  block="true" > 
                                                        
                                                            <h:graphicImage  styleClass="vdcNoBorders" style="margin-left: 2px; margin-right: 0px;" value="/resources/icon_download_locked.gif" rendered="#{studyFileUI.restrictedForUser}" 
                                                                         alt="You do not have permissions to access this file." title="You do not have permissions to access this file."/>
                                                            <h:outputLink  id="fileLink" style="padding-right: 15px" onclick="return checkTerms(this.id);" target="_file" value="/dvn#{VDCRequest.currentVDCURL}/FileDownload/#{studyFileUI.studyFile.fileName}?fileId=#{studyFileUI.studyFile.id}" rendered="#{!studyFileUI.restrictedForUser}" title="View or download this File.">
                                                                <h:graphicImage   styleClass="vdcNoBorders" style="margin-left: 2px; margin-right: 0px;" value="/resources/icon_download.gif"/>
                                                            </h:outputLink> 
                                                           
                                                            <h:outputLink rendered="#{studyFileUI.studyFile.subsettable}"  id="fileSubset" onclick="return checkTerms(this.id);" value="/dvn#{VDCRequest.currentVDCURL}/faces/subsetting/SubsettingPage.jsp?dtId=#{studyFileUI.studyFile.dataTable.id}" title="Go to Subset and Analysis page for this file." >
                                                                <h:graphicImage  id="imagefs" styleClass="vdcNoBorders" style="margin-left: 0px; margin-right: 2px;" value="/resources/icon_subsettable.gif" />
                                                            </h:outputLink> 
                                                            
                                                            <ui:panelGroup rendered="#{studyFileUI.studyFile.subsettable}">  
                                                                <f:verbatim>
                                                                    <h:outputLink  id="citation"  onclick="createFileCitationPopup(this.nextSibling.id,this.id);return false;"  value="#">
                                                                        <h:graphicImage styleClass="vdcNoBorders" style="margin-left: 6px; margin-right: 2px;" value="/resources/icon_citation.gif" title="View data citation for this file." />
                                                                    </h:outputLink>                                                                      
                                                                    
                                                                    <ui:panelGroup block="true" id="fileCitationDiv" style="position:absolute;visibility:hidden;background-color:#FFFFCC; border-color:#000099; border-width: 1px; border-style: solid; padding: 8px;">
                                                                        <div style="text-align: right; font-size: 0.9em;"><a href="#" style="font-size: 0.9em;" onClick="fileCitationPopup.hidePopup();return false;">Hide It</a></div>
                                                                        
                                                                        <h:outputText   value="#{studyPage.studyUI.study.citation}" escape="false"/>
                                                                        <h:outputText   value="#{studyFileUI.studyFile.fileName} [fileDscr/fileName (DDI)] #{studyFileUI.studyFile.dataTable.unf}" />
                                                                    </ui:panelGroup>
                                                                </f:verbatim>
                                                                
                                                            </ui:panelGroup>
                                                        </ui:panelGroup>
                                                    </h:column>
                                                </h:dataTable> 
                                            </h:column> 
                                         </h:dataTable>
                                        
                                        <ui:panelGroup style="margin-top:10px;" block="true" rendered="#{!empty studyPage.studyUI.study.fileCategories}" >
                                            <ui:panelGroup style="padding-top: 15px; font-weight: bold; font-size:1.2em; color:#666666; font-style:italic;" block="true">
                                                <h:outputText    value="Legend:"/>                                              
                                            </ui:panelGroup>
                                            <h:panelGrid cellpadding="3" cellspacing="0" columns="3" style="border-width:0px; border-style:solid; border-color: #ffff66;" > 
                                                <ui:panelGroup  block="true">
                                                    <h:graphicImage  value="/resources/icon_downloadall.gif" alt="Download"/>
                                                    <h:outputText   styleClass="vdcHelpText" value="Download all files you have access in the study or category"/> 
                                                </ui:panelGroup>  
                                                <ui:panelGroup  block="true">
                                                    <h:graphicImage  value="/resources/icon_download.gif" alt="Download"/>
                                                    <h:outputText styleClass="vdcHelpText"   value="Download this file"/> 
                                                </ui:panelGroup>  
                                                <ui:panelGroup  block="true">
                                                    <h:graphicImage  value="/resources/icon_subsettable.gif" alt="Subset"/>
                                                    <h:outputText  styleClass="vdcHelpText"  value="Subset/Analysis available for this file"/> 
                                                </ui:panelGroup>
                                                <ui:panelGroup  block="true">                                               
                                                    <h:graphicImage  value="/resources/icon_downloadall_locked.gif" alt="Download Locked"/>
                                                    <h:outputText  styleClass="vdcHelpText"  value="No access to download any file in the study or category"/>
                                                </ui:panelGroup>
                                                <ui:panelGroup  block="true">
                                                    <h:graphicImage  value="/resources/icon_download_locked.gif" alt="Download Locked"/>
                                                    <h:outputText  styleClass="vdcHelpText"  value="No access to download this file"/>
                                                </ui:panelGroup>                 
                                                <ui:panelGroup  block="true">
                                                    <h:graphicImage styleClass="vdcNoBorders" value="/resources/icon_citation.gif"/>
                                                    <h:outputText  styleClass="vdcHelpText"  value="View Data Citation for this file"/>
                                                </ui:panelGroup>
                                            </h:panelGrid>
                                        </ui:panelGroup>
                                        
                                    </ui:panelLayout>
                                </ui:tab>
                            </ui:tabSet>
                        </ui:panelGroup>
                      </ui:panelLayout>
                        <!-- this is the panel group for terms of use, hidden and appears as a popup -->
                        <ui:panelGroup block="true" id="termsDiv" style="position:absolute;visibility:hidden;" styleClass="vdcScrollDivTermsUse">
                            <div style="text-align: right; font-size: 0.9em;"><a href="#" style="font-size: 0.9em;" onclick="termsPopup.hidePopup();return false;">Close</a></div>
                            <h:inputHidden id="termsAcceptanceRequired" value="#{TermsOfUsePage.termsAcceptanceRequired}"/>
                                <ui:panelGroup  block="true" style="padding-bottom: 5px">
                                     <h:outputText styleClass="warnMessage" value="You need to agree to the Terms of Use before accessing the files  of this study. (Please click the icon again after you have agreed.)" />
                                </ui:panelGroup>
                                <h:panelGrid  cellpadding="0" cellspacing="0" columns="1" columnClasses="vdcTermsUseColumn" rendered="#{TermsOfUsePage.vdcTermsRequired}">
                                    <ui:panelGroup  block="true">
                                        <h:outputText styleClass="vdcFieldTitle" value="Dataverse Terms of Use:" />
                                        <h:selectBooleanCheckbox id="vdcTermsAccepted" disabled="#{!TermsOfUsePage.vdcTermsRequired}" value="#{TermsOfUsePage.vdcTermsAccepted}"/>
                                    </ui:panelGroup>
                                    <h:outputText value="#{TermsOfUsePage.study.owner.termsOfUse}" escape="false"/>
                                </h:panelGrid>
                                <h:panelGrid  cellpadding="0" cellspacing="0" columns="1" columnClasses="vdcTermsUseColumn" rendered="#{TermsOfUsePage.studyTermsRequired}">
                                    <ui:panelGroup  block="true" style="padding-bottom: 10px">
                                        <h:outputText styleClass="vdcFieldTitle" value="Study Terms of Use:" />
                                        <h:selectBooleanCheckbox id="studyTermsAccepted" disabled="#{!TermsOfUsePage.studyTermsRequired}" value="#{TermsOfUsePage.studyTermsAccepted}"/>
                                    </ui:panelGroup>
                                    <h:outputText value="Data Access Place:" styleClass="vdcTermsUseField" rendered="#{!empty TermsOfUsePage.study.placeOfAccess}"/>
                                    <h:outputText  value="#{TermsOfUsePage.study.placeOfAccess}"  rendered="#{!empty TermsOfUsePage.study.placeOfAccess}" escape="false"/>
                                    <h:outputText  value="Original Archive:" styleClass="vdcTermsUseField" rendered="#{!empty TermsOfUsePage.study.originalArchive}"/>
                                    <h:outputText  value="#{TermsOfUsePage.study.originalArchive}"  rendered="#{!empty TermsOfUsePage.study.originalArchive}" escape="false" />
                                    <h:outputText  value="Availability Status:" styleClass="vdcTermsUseField" rendered="#{!empty TermsOfUsePage.study.availabilityStatus }"/>
                                    <h:outputText  value="#{TermsOfUsePage.study.availabilityStatus}"  rendered="#{!empty TermsOfUsePage.study.availabilityStatus}" escape="false"/>
                                    <h:outputText  value="Size of Collection:" styleClass="vdcTermsUseField" rendered="#{!empty TermsOfUsePage.study.collectionSize}"/>
                                    <h:outputText  value="#{TermsOfUsePage.study.collectionSize}"  rendered="#{!empty TermsOfUsePage.study.collectionSize}" escape="false"/>
                                    <h:outputText  value="Study Completion:"  styleClass="vdcTermsUseField" rendered="#{!empty TermsOfUsePage.study.studyCompletion}"/>
                                    <h:outputText  value="#{TermsOfUsePage.study.studyCompletion}"  rendered="#{!empty TermsOfUsePage.study.studyCompletion}" escape="false"/>
                                    <h:outputText  value="Number of Files:"  styleClass="vdcTermsUseField" rendered="#{!empty TermsOfUsePage.study.numberOfFiles}"/>
                                    <h:outputText  value="#{TermsOfUsePage.study.numberOfFiles}" rendered="#{!empty TermsOfUsePage.study.numberOfFiles}" escape="false" />
                                    <h:outputText  value="Confidentiality Declaration:" styleClass="vdcTermsUseField" rendered="#{!empty TermsOfUsePage.study.confidentialityDeclaration}" />
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
                                    <h:outputText  value="Conditions:" styleClass="vdcTermsUseField" rendered="#{!empty TermsOfUsePage.study.conditions}"/>
                                    <h:outputText  value="#{TermsOfUsePage.study.conditions}" rendered="#{!empty TermsOfUsePage.study.conditions}" escape="false"/>
                                    <h:outputText  value="Disclaimer:" styleClass="vdcTermsUseField" rendered="#{!empty TermsOfUsePage.study.disclaimer}"/>
                                    <h:outputText  value="#{TermsOfUsePage.study.disclaimer}" rendered="#{!empty TermsOfUsePage.study.disclaimer}" escape="false"/>
                                </h:panelGrid> 
                                <ui:panelGroup  block="true" style="padding-top: 10px; text-align:center;">
                                    <h:commandButton  id="termsButton" value="Accept" action="#{TermsOfUsePage.acceptTerms_action}"/>
                                </ui:panelGroup>
                        </ui:panelGroup> 
                       <!--end terms of use panel -->
                    </ui:form>
    </f:subview>
</jsp:root>
