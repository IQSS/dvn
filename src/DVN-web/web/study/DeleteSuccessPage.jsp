<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
    xmlns:h="http://java.sun.com/jsf/html" 
    xmlns:jsp="http://java.sun.com/JSP/Page" 
    xmlns:ui="http://www.sun.com/web/ui"
    xmlns:tiles="http://struts.apache.org/tags-tiles"
    xmlns:c="http://java.sun.com/jsp/jstl/core">
    
    <f:subview id="deleteStudyPageView">
      
            <h:form  id="form1">
                <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
                <input type="hidden" name="pageName" value="DeleteSuccessPage"/>

                <div class="dvn_section">
                    <div class="dvn_sectionTitle">
                        <h3>
                            <h:outputText  value="Delete Susscessful"/>
                        </h3>
                    </div>            
                    <div class="dvn_sectionBox dvn_pad12"> 

                            <h:outputText  id="outputText2" value="The study has been deleted successfully. "/>

                    </div>
                </div>

            </h:form>
               
    </f:subview>
</jsp:root>
