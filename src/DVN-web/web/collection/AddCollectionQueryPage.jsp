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

<gui:param name="pageTitle" value="DVN - Add/Edit Collection" />

  <gui:define name="body">
      <f:loadBundle basename="Bundle" var="bundle"/>
     <f:loadBundle basename="BundleAnalysis" var="bundleAnalysis"/>

     
        <ui:form id="form1" >
            <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>                       
            <h:inputHidden id="edit" value="#{AddCollectionsPage.edit}"/>                       
            <h:inputHidden id="collId" value="#{AddCollectionsPage.collId}"/>                       
            <h:inputHidden id="parentId" value="#{AddCollectionsPage.parentId}"/>  
            
            <div class="dvn_section">
                <div class="dvn_sectionTitle">
                            
                        <h:outputText value="Collection as a Query"/>
                    
                </div>            
                <div class="dvn_sectionBox"> 
                    <div class="dvn_margin12"> 
                        <ui:panelGroup block="true" style="padding-top: 10px; padding-left: 5px">
                            <h:outputText value="Enter collection name: "/>
                            <h:inputText binding="#{AddCollectionsPage.textFieldCollectionName}" required="true" requiredMessage="This field is required." id="textFieldCollectionName" validator="#{AddCollectionsPage.validateName}"/>
                            <h:message for="textFieldCollectionName" showSummary="true" showDetail="false" errorClass="errorMessage"/>
                            <h:outputText id="outputText5" value="Choose a parent: "/>
                            <h:selectOneMenu binding="#{AddCollectionsPage.dropdown3}" id="dropdown3">
                                <f:selectItems id="dropdown2SelectItems1" value="#{AddCollectionsPage.dropdown2DefaultItems}"/>
                            </h:selectOneMenu>
                        </ui:panelGroup>
                        
                        <ui:panelGroup block="true" id="groupPanel3" style="padding-top: 10px;">
                            <h:outputText styleClass="vdcSubHeader" value="Define Collection as a Query: "/> 
                            <ui:panelGroup block="true" style="padding-top: 10px; padding-left: 15px, padding-right: 15px;">    
                                <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                                <h:outputText  styleClass="vdcHelpText" value="By defining a collection as a query any new - or modified - study that satisfies the query will be automatically added to the collection."/>
                            </ui:panelGroup>
                            
                            <ui:panelGroup block="true" style="padding-top: 10px; padding-left: 15px"> 
                                <ui:panelGroup block="true" style="padding-top: 10px">   
                                    <h:outputText id="outputText10" value="Enter query: "/>
                                </ui:panelGroup>
                                <h:inputTextarea rows="5" cols="100" binding="#{AddCollectionsPage.textAreaQuery}"  id="textArea1" required="true" requiredMessage="This field is required."/>  
                                <h:message for="textArea1" showSummary="true" showDetail="false"  errorClass="errorMessage"/>
                            </ui:panelGroup>
                            
                            <ui:panelGroup block="true" id="groupPanel6" style="padding-top: 30px; ">
                                <h:commandButton id="button2" value="Save Collection" action="#{AddCollectionsPage.queryStudies}"/> 
                                <h:commandButton  id="button4" immediate="true" 
                                                  style="margin-left: 30px;" value="Cancel" action="#{AddCollectionsPage.cancel}"/>
                            </ui:panelGroup>
                            
                            <ui:panelGroup block="true" style="padding-top: 15px">
                                <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                                <h:outputText  styleClass="vdcHelpText" escape="false" value="Practically all study fields can be used to build a collection query. The study fields need to be entered in the appropriate format, such as: title, studyId, productionDate, distributionDate, keywordValue, topicClassValue, authorName, authorAffiliation, producerName, distributorName, otherId (all fields will be listed in Help) &lt;br /&gt; &lt;b&gt;Query examples:&lt;/b&gt;  &lt;br /&gt; title:Elections AND keywordValue:world &lt;br /&gt; authorName: John Smith &lt;br /&gt; &lt;br /&gt; For more information on query syntax, see &lt;a href='http://lucene.apache.org/java/docs/queryparsersyntax.html' style='font-size: 1em' target='_blank'&gt;Lucene website&lt;/a&gt;." />
                            </ui:panelGroup>   
                        </ui:panelGroup>
                    </div>
                </div>                             
            </div>                     
        </ui:form>
   </gui:define>
        </gui:composition>
    </body>
</html>
