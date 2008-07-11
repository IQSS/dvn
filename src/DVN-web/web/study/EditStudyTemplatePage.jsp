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

<gui:param name="pageTitle" value="DVN - Edit Study Template" />

  <gui:define name="body">
      <f:loadBundle basename="EditStudyBundle" var="editstudybundle"/>

                    <ui:form binding="#{EditStudyTemplatePage.form1}" id="form1">
                        <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
                        <ui:panelLayout binding="#{EditStudyTemplatePage.layoutPanel1}" id="layoutPanel1" panelLayout="flow" styleClass="vdcSectionMiddleNoBorder">
                            <ui:panelGroup binding="#{EditStudyTemplatePage.groupPanel1}" block="true" id="groupPanel1" styleClass="vdcStudyTitle">
                                <h:outputText binding="#{EditStudyTemplatePage.outputText1}" id="outputText1" styleClass="vdcStudyTitleName" value="Study Tempalte:  X"/>
                            </ui:panelGroup>
                            <ui:panelGroup binding="#{EditStudyTemplatePage.groupPanel2}" block="true" id="groupPanel2" styleClass="vdcTextRight">
                                <h:commandButton binding="#{EditStudyTemplatePage.button1}" id="button1" value="Save"/>
                                <h:commandButton binding="#{EditStudyTemplatePage.button2}" id="button2" style="margin-left: 30px" value="Cancel"/>
                            </ui:panelGroup>
                          
                                <ui:tabSet binding="#{EditStudyTemplatePage.tabSet1}" id="tabSet1" lite="true" mini="true" selected="tab1" styleClass="vdcStudyTabSet">
                                    <ui:tab action="#{EditStudyTemplatePage.tab1_action}" binding="#{EditStudyTemplatePage.tab1}" id="tab1"
                                        styleClass="MniTabLnk" text="Cataloging Information">
                                        <ui:panelLayout binding="#{EditStudyTemplatePage.layoutPanel2}" id="layoutPanel2" panelLayout="flow" style="margin: 0px; padding: 0px; ">
                                            <ui:panelGroup binding="#{EditStudyTemplatePage.groupPanel4}" block="true" id="groupPanel4" styleClass="vdcStudyInfoHeader">
                                                <h:outputText binding="#{EditStudyTemplatePage.outputText3}" id="outputText3" value="Citation Information"/>
                                                <h:graphicImage binding="#{EditStudyTemplatePage.image1}" id="image1" value="/resources/icon_contract.gif"/>
                                            </ui:panelGroup>
                                            <h:panelGrid binding="#{EditStudyTemplatePage.gridPanel2}" cellpadding="0" cellspacing="0"
                                                columnClasses="vdcStudyInfoCol1, vdcStudyInfoCol2" columns="2" id="gridPanel2" title="Citation Information" >
                                                <h:outputText binding="#{EditStudyTemplatePage.outputText4}" id="outputText4"
                                                    styleClass="vdcStudyInfoField" value="Title"/>
                                                <ui:panelGroup binding="#{EditStudyTemplatePage.groupPanel3}" id="groupPanel3">
                                                    <h:graphicImage alt="Required" binding="#{EditStudyTemplatePage.image13}" id="image13"
                                                        title="Required" value="/resources/icon_required.gif"/>
                                                    <h:selectBooleanCheckbox binding="#{EditStudyTemplatePage.checkbox1}" disabled="true" id="checkbox1"/>
                                                    <h:graphicImage alt="Recommended" binding="#{EditStudyTemplatePage.image14}" id="image14"
                                                        title="Recommended" value="/resources/icon_recommended.gif"/>
                                                    <h:selectBooleanCheckbox binding="#{EditStudyTemplatePage.checkbox7}" disabled="true" id="checkbox7"/>
                                                </ui:panelGroup>
                                                <h:outputText binding="#{EditStudyTemplatePage.outputText6}" id="outputText6"
                                                    styleClass="vdcStudyInfoField" value="Author"/>
                                                <ui:panelGroup binding="#{EditStudyTemplatePage.groupPanel9}" id="groupPanel9">
                                                    <h:graphicImage alt="Required" binding="#{EditStudyTemplatePage.image15}" id="image15"
                                                        title="Required" value="/resources/icon_required.gif"/>
                                                    <h:selectBooleanCheckbox binding="#{EditStudyTemplatePage.checkbox8}" id="checkbox8"/>
                                                    <h:graphicImage alt="Recommended" binding="#{EditStudyTemplatePage.image16}" id="image16"
                                                        title="Recommended" value="/resources/icon_recommended.gif"/>
                                                    <h:selectBooleanCheckbox binding="#{EditStudyTemplatePage.checkbox9}" id="checkbox9"/>
                                                </ui:panelGroup>
                                                <h:outputText binding="#{EditStudyTemplatePage.outputText8}" id="outputText8"
                                                    styleClass="vdcStudyInfoField" value="Producer"/>
                                                <ui:panelGroup binding="#{EditStudyTemplatePage.groupPanel10}" id="groupPanel10">
                                                    <h:graphicImage alt="Required" binding="#{EditStudyTemplatePage.image17}" id="image17"
                                                        title="Required" value="/resources/icon_required.gif"/>
                                                    <h:selectBooleanCheckbox binding="#{EditStudyTemplatePage.checkbox10}" id="checkbox10"/>
                                                    <h:graphicImage alt="Recommended" binding="#{EditStudyTemplatePage.image18}" id="image18"
                                                        title="Recommended" value="/resources/icon_recommended.gif"/>
                                                    <h:selectBooleanCheckbox binding="#{EditStudyTemplatePage.checkbox11}" id="checkbox11"/>
                                                </ui:panelGroup>
                                                <h:outputText binding="#{EditStudyTemplatePage.outputText10}" id="outputText10"
                                                    styleClass="vdcStudyInfoField" value="..."/>
                                                <ui:panelGroup binding="#{EditStudyTemplatePage.groupPanel16}" id="groupPanel16">
                                                    <h:graphicImage alt="Required" binding="#{EditStudyTemplatePage.image19}" id="image19"
                                                        title="Required" value="/resources/icon_required.gif"/>
                                                    <h:selectBooleanCheckbox binding="#{EditStudyTemplatePage.checkbox12}" id="checkbox12"/>
                                                    <h:graphicImage alt="Recommended" binding="#{EditStudyTemplatePage.image20}" id="image20"
                                                        title="Recommended" value="/resources/icon_recommended.gif"/>
                                                    <h:selectBooleanCheckbox binding="#{EditStudyTemplatePage.checkbox13}" id="checkbox13"/>
                                                </ui:panelGroup>
                                            </h:panelGrid>
                                            <ui:panelGroup binding="#{EditStudyTemplatePage.groupPanel5}" block="true" id="groupPanel5" styleClass="vdcStudyInfoHeader">
                                                <h:outputText binding="#{EditStudyTemplatePage.outputText42}" id="outputText42" value="Abstract and Scope"/>
                                                <h:graphicImage binding="#{EditStudyTemplatePage.image2}" id="image2" value="/resources/icon_contract.gif"/>
                                            </ui:panelGroup>
                                            <h:panelGrid binding="#{EditStudyTemplatePage.gridPanel3}" cellpadding="0" cellspacing="0"
                                                columnClasses="vdcStudyInfoCol1, vdcStudyInfoCol2" columns="2" id="gridPanel3" title="Abstract and Scope" width="100%">
                                                <h:outputText binding="#{EditStudyTemplatePage.outputText43}" id="outputText43"
                                                    styleClass="vdcStudyInfoField" value="Keywords"/>
                                                <ui:panelGroup binding="#{EditStudyTemplatePage.groupPanel17}" id="groupPanel17">
                                                    <h:graphicImage alt="Required" binding="#{EditStudyTemplatePage.image21}" id="image21"
                                                        title="Required" value="/resources/icon_required.gif"/>
                                                    <h:selectBooleanCheckbox binding="#{EditStudyTemplatePage.checkbox14}" id="checkbox14"/>
                                                    <h:graphicImage alt="Recommended" binding="#{EditStudyTemplatePage.image22}" id="image22"
                                                        title="Recommended" value="/resources/icon_recommended.gif"/>
                                                    <h:selectBooleanCheckbox binding="#{EditStudyTemplatePage.checkbox15}" id="checkbox15"/>
                                                    <h:outputText binding="#{EditStudyTemplatePage.outputText2}" id="outputText2" value="List of Values to choose from (separated by comma):"/>
                                                    <h:inputTextarea binding="#{EditStudyTemplatePage.textArea2}" cols="50" id="textArea2" rows="3"/>
                                                </ui:panelGroup>
                                                <h:outputText binding="#{EditStudyTemplatePage.outputText45}" id="outputText45"
                                                    styleClass="vdcStudyInfoField" value="Topic Classifications"/>
                                                <ui:panelGroup binding="#{EditStudyTemplatePage.groupPanel18}" id="groupPanel18">
                                                    <h:graphicImage alt="Required" binding="#{EditStudyTemplatePage.image23}" id="image23"
                                                        title="Required" value="/resources/icon_required.gif"/>
                                                    <h:selectBooleanCheckbox binding="#{EditStudyTemplatePage.checkbox16}" id="checkbox16"/>
                                                    <h:graphicImage alt="Recommended" binding="#{EditStudyTemplatePage.image24}" id="image24"
                                                        title="Recommended" value="/resources/icon_recommended.gif"/>
                                                    <h:selectBooleanCheckbox binding="#{EditStudyTemplatePage.checkbox17}" id="checkbox17"/>
                                                    <h:outputText binding="#{EditStudyTemplatePage.outputText5}" id="outputText5" value="List of Values to choose from (separated by comma):"/>
                                                    <h:inputTextarea binding="#{EditStudyTemplatePage.textArea3}" cols="50" id="textArea3" rows="3"/>
                                                </ui:panelGroup>
                                                <h:outputText binding="#{EditStudyTemplatePage.outputText47}" id="outputText47"
                                                    styleClass="vdcStudyInfoField" value="Abstract"/>
                                                <ui:panelGroup binding="#{EditStudyTemplatePage.groupPanel19}" id="groupPanel19">
                                                    <h:graphicImage alt="Required" binding="#{EditStudyTemplatePage.image25}" id="image25"
                                                        title="Required" value="/resources/icon_required.gif"/>
                                                    <h:selectBooleanCheckbox binding="#{EditStudyTemplatePage.checkbox18}" id="checkbox18"/>
                                                    <h:graphicImage alt="Recommended" binding="#{EditStudyTemplatePage.image26}" id="image26"
                                                        title="Recommended" value="/resources/icon_recommended.gif"/>
                                                    <h:selectBooleanCheckbox binding="#{EditStudyTemplatePage.checkbox19}" id="checkbox19"/>
                                                </ui:panelGroup>
                                                <h:outputText binding="#{EditStudyTemplatePage.outputText49}" id="outputText49"
                                                    styleClass="vdcStudyInfoField" value="..."/>
                                                <ui:panelGroup binding="#{EditStudyTemplatePage.groupPanel20}" id="groupPanel20">
                                                    <h:graphicImage alt="Required" binding="#{EditStudyTemplatePage.image27}" id="image27"
                                                        title="Required" value="/resources/icon_required.gif"/>
                                                    <h:selectBooleanCheckbox binding="#{EditStudyTemplatePage.checkbox20}" id="checkbox20"/>
                                                    <h:graphicImage alt="Recommended" binding="#{EditStudyTemplatePage.image28}" id="image28"
                                                        title="Recommended" value="/resources/icon_recommended.gif"/>
                                                    <h:selectBooleanCheckbox binding="#{EditStudyTemplatePage.checkbox21}" id="checkbox21"/>
                                                </ui:panelGroup>
                                            </h:panelGrid>
                                            <ui:panelGroup binding="#{EditStudyTemplatePage.groupPanel6}" block="true" id="groupPanel6" styleClass="vdcStudyInfoHeader">
                                                <h:outputText binding="#{EditStudyTemplatePage.outputText67}" id="outputText67" value="Data Collection / Methodology"/>
                                                <h:graphicImage binding="#{EditStudyTemplatePage.image3}" id="image3" value="/resources/icon_expand.gif"/>
                                            </ui:panelGroup>
                                            <h:panelGrid binding="#{EditStudyTemplatePage.gridPanel4}" cellpadding="0" cellspacing="0"
                                                columnClasses="vdcStudyInfoCol1, vdcStudyInfoCol2" columns="2" id="gridPanel4" rendered="false"
                                                title="Data Collection / Methodology" width="100%"/>
                                            <ui:panelGroup binding="#{EditStudyTemplatePage.groupPanel7}" block="true" id="groupPanel7" styleClass="vdcStudyInfoHeader">
                                                <h:outputText binding="#{EditStudyTemplatePage.outputText108}" id="outputText108" value="Terms of Use"/>
                                                <h:graphicImage binding="#{EditStudyTemplatePage.image4}" id="image4" value="/resources/icon_expand.gif"/>
                                            </ui:panelGroup>
                                            <h:panelGrid binding="#{EditStudyTemplatePage.gridPanel5}" cellpadding="0" cellspacing="0"
                                                columnClasses="vdcStudyInfoCol1, vdcStudyInfoCol2" columns="2" id="gridPanel5" rendered="false"
                                                title="Abstract and Scope" width="100%"/>
                                            <ui:panelGroup binding="#{EditStudyTemplatePage.groupPanel8}" block="true" id="groupPanel8" styleClass="vdcStudyInfoHeader">
                                                <h:outputText binding="#{EditStudyTemplatePage.outputText137}" id="outputText137" value="Other Information"/>
                                                <h:graphicImage binding="#{EditStudyTemplatePage.image5}" id="image5" value="/resources/icon_expand.gif"/>
                                            </ui:panelGroup>
                                            <h:panelGrid binding="#{EditStudyTemplatePage.gridPanel6}" cellpadding="0" cellspacing="0"
                                                columnClasses="vdcStudyInfoCol1, vdcStudyInfoCol2" columns="2" id="gridPanel6" rendered="false"
                                                title="Abstract and Scope" width="100%"/>
                                        </ui:panelLayout>
                                    </ui:tab>
                                    <ui:tab action="#{EditStudyTemplatePage.tab2_action}" binding="#{EditStudyTemplatePage.tab2}" id="tab2" text="Study Files">
                                        <ui:panelLayout binding="#{EditStudyTemplatePage.layoutPanel3}" id="layoutPanel3" panelLayout="flow" style="width: 100%;">
                                            <ui:panelGroup binding="#{EditStudyTemplatePage.groupPanel12}" block="true" id="groupPanel12" style="padding-left: 40px; padding-top: 40px; padding-bottom: 40px">
                                                <ui:helpInline binding="#{EditStudyTemplatePage.helpInline1}" id="helpInline1"
                                                    style="padding-bottom: 20px" text="Add a list of categories (separated by comma) for this template that curators and contributors can choose from:"/>
                                                <h:outputText binding="#{EditStudyTemplatePage.outputText7}" id="outputText7" styleClass="vdcFieldTitle" value="Categories"/>
                                                <h:inputTextarea binding="#{EditStudyTemplatePage.textArea1}" cols="50" id="textArea1" rows="4"/>
                                            </ui:panelGroup>
                                        </ui:panelLayout>
                                    </ui:tab>
                                </ui:tabSet>
                        </ui:panelLayout>
                    </ui:form>
            </gui:define>
        </gui:composition>
    </body>
</html>
