<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
    xmlns:h="http://java.sun.com/jsf/html" 
    xmlns:jsp="http://java.sun.com/JSP/Page" 
    xmlns:ui="http://www.sun.com/web/ui"
    xmlns:tiles="http://struts.apache.org/tags-tiles"
    xmlns:c="http://java.sun.com/jsp/jstl/core">
    
    <f:subview id="deleteStudyPageView">
      
                    <h:form  id="form1">
                        <input type="hidden" name="pageName" value="DeleteSuccessPage"/>
  
                        <ui:panelLayout  id="layoutPanel1" panelLayout="flow" styleClass="vdcSectionMiddle">
                            <ui:panelLayout  id="layoutPanel2" panelLayout="flow" styleClass="vdcSectionHeader">
                                <h:outputText value="Delete Successful"/>
                            </ui:panelLayout>
                            <ui:panelLayout  id="layoutPanel3" panelLayout="flow"  style="padding: 40px 50px 40px 50px; ">
                                <ui:panelGroup  block="true" id="groupPanel1">
                                    <h:outputText  id="outputText2" value="The dataverse has been deleted successfully. "/>
  
                                </ui:panelGroup>
                            </ui:panelLayout>
                        </ui:panelLayout>
                    </h:form>

               
    </f:subview>
</jsp:root>
