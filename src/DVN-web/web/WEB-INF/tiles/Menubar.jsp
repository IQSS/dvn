<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
    xmlns:h="http://java.sun.com/jsf/html" 
    xmlns:jsp="http://java.sun.com/JSP/Page" 
    xmlns:ui="http://www.sun.com/web/ui"
    xmlns:tiles="http://struts.apache.org/tags-tiles"
    xmlns:c="http://java.sun.com/jsp/jstl/core">
    
    <h:panelGrid  cellpadding="0" cellspacing="0"
        columnClasses="vdcConnectBannerCol0n, vdcConnectBannerColb, vdcConnectBannerColcn" columns="3" title="Connected Banner" width="100%" styleClass="vdcConnectBanner" rendered="#{VDCRequest.currentVDC == null}">             
         <ui:panelGroup styleClass="vdcMenu0" block="true">
            <h:outputText value=""/>
         </ui:panelGroup>
         <ui:panelGroup styleClass="vdcMenubn" block="true">
             <h:outputLink value="/dvn"  styleClass="vdcConnectTextActiven" title="#{VDCRequest.vdcNetwork.name} Dataverse Network Homepage">
                <h:outputText  value="#{VDCRequest.vdcNetwork.name} Dataverse Network" style="white-space: nowrap" />
             </h:outputLink>
         </ui:panelGroup>
        <ui:panelGroup  block="true">
            <h:outputLink  value="http://thedata.org" target="_blank"  title="Dataverse Network Project Site">
                <h:graphicImage alt="Powered by the Dataverse Network Project" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/poweredby_logo.gif" />
            </h:outputLink>
        </ui:panelGroup>
    </h:panelGrid>
    <h:panelGrid  cellpadding="0" cellspacing="0"
        columnClasses="vdcConnectBannerCol0, vdcConnectBannerColb, vdcConnectBannerCola, vdcConnectBannerColc" columns="4" title="Connected Banner" width="100%" styleClass="vdcConnectBanner" rendered="#{VDCRequest.currentVDC != null}"  > 
            <ui:panelGroup styleClass="vdcMenu0" block="true">
                <h:outputText value=""/>
            </ui:panelGroup>
                        <ui:panelGroup styleClass="vdcMenub" block="true">
                 <h:outputLink value="/dvn/dv/#{VDCRequest.currentVDC.alias}"  styleClass="vdcConnectTextActive" title="#{VDCRequest.currentVDC.name} dataverse Homepage">
                    <h:outputText value="#{VDCRequest.currentVDC.name} Dataverse " style="white-space: nowrap"/>
                 </h:outputLink>
           </ui:panelGroup>   
            <ui:panelGroup styleClass="vdcMenua" block="true">
                    <h:outputLink value="/dvn"  styleClass="vdcConnectText" title="#{VDCRequest.vdcNetwork.name} Dataverse Network Homepage">
                        <h:outputText  value="#{VDCRequest.vdcNetwork.name} Dataverse Network" style="white-space: nowrap"/> 
                    </h:outputLink>
            </ui:panelGroup>

        <ui:panelGroup  block="true">
            <h:outputLink  value="http://thedata.org" target="_blank"  title="Dataverse Network Project Site">
                <h:graphicImage alt="Powered by the Dataverse Network Project" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/poweredby_logo.gif" />
            </h:outputLink>
        </ui:panelGroup>
    </h:panelGrid>    
    
    <h:panelGrid  cellpadding="0" cellspacing="0" columnClasses="vdcMenubarCol1n, vdcMenubarCol2n"
        columns="2" width="100%" rendered="#{VDCRequest.currentVDC == null}" >
        <ui:panelGroup  block="true">

            <h:outputLink  value="/dvn" title="#{VDCRequest.vdcNetwork.name} Dataverse Network Homepage">
                <h:outputText styleClass="vdcMenuItem" value="#{bundle.homeLabel} "/>
            </h:outputLink>
            
              <h:outputText  styleClass="vdcMenuItemBorder" value=" | "/>
            <h:outputLink  value="/dvn/faces/AboutPage.jsp" title="About #{VDCRequest.vdcNetwork.name} Dataverse Network">
                <h:outputText styleClass="vdcMenuItem" value="#{bundle.aboutLabel} "/>
            </h:outputLink>
            
             <h:outputText  styleClass="vdcMenuItemBorder" value=" | "/>
            <h:outputLink  value="http://thedata.org/help" target="_blank" title="Open User Manual">
                <h:outputText styleClass="vdcMenuItem" value="#{bundle.helpLabel}"/>
            </h:outputLink>
             
             <h:outputText styleClass="vdcMenuItemBorder" value=" | "/>
            <h:outputLink  value="/dvn/faces/SiteMapPage.jsp" title="Site Map for #{VDCRequest.vdcNetwork.name} Dataverse Network">
                <h:outputText  styleClass="vdcMenuItem" value="#{bundle.siteMapLabel}"/>
            </h:outputLink>
            
             <h:outputText styleClass="vdcMenuItemBorder" value=" | "/>
            <h:outputLink  value="/dvn/faces/ContactUsPage.jsp"  title="Contact #{VDCRequest.vdcNetwork.name} Dataverse Network Help">
                <h:outputText styleClass="vdcMenuItem" value="#{bundle.contactUsLabel}"/>
            </h:outputLink>
           
            <h:outputText styleClass="vdcMenuItemBorder" value=" | "/>
            
            <h:outputLink  rendered="#{VDCRequest.logoutPage or VDCSession.loginBean.user==null}"  value="/dvn/faces/login/LoginPage.jsp" title="Log in">
                <h:outputText  styleClass="vdcMenuItem" value="#{bundle.loginLabel}"/>
            </h:outputLink>
           
            <h:outputLink rendered="#{VDCSession.loginBean.user!=null and !VDCRequest.logoutPage }" value="/dvn/faces/login/LogoutPage.jsp" title="Logout">
                <h:outputText styleClass="vdcMenuItem" value="#{bundle.logoutLabel}"/>
            </h:outputLink>
            
            
        </ui:panelGroup>
        <ui:panelGroup  block="true" rendered="#{!VDCRequest.logoutPage}" style="padding-left: 10px;">
            <h:outputLink  styleClass="vdcMenuUserName" value="/dvn/faces/login/AccountPage.jsp?userId=#{VDCSession.loginBean.user.id}" title="Edit Account Information" rendered="#{VDCSession.loginBean.user.firstName != null}">
                <h:outputText   styleClass="vdcMenuUserName" value="#{VDCSession.loginBean.user.firstName} #{VDCSession.loginBean.user.lastName}"/>
            </h:outputLink>
            <h:outputText rendered="#{ VDCSession.loginBean.user.firstName == null }" styleClass="vdcMenuUserName" value="#{ VDCSession.ipUserGroup.friendlyName }"/>          
            <h:outputLink   styleClass="vdcMenuNetworkOptions" value="/dvn/faces/networkAdmin/NetworkOptionsPage.jsp" rendered="#{VDCRequest.currentVDC == null and VDCSession.loginBean.networkAdmin }" title="#{VDCRequest.vdcNetwork.name} Dataverse Network Admin Options">
                <h:outputText  id="hyperlink6Text" value="Network Options"/>
            </h:outputLink>
             <h:outputLink styleClass="vdcMenuNetworkOptions" value="/dvn/faces/site/AddSitePage.jsp" rendered="#{VDCRequest.currentVDC == null and (VDCSession.loginBean.networkCreator) }" title="Create a new dataverse in the Network">
                <h:outputText  value="Create a Dataverse"/>
            </h:outputLink>
        </ui:panelGroup>
    </h:panelGrid>
    
    
    <h:panelGrid  cellpadding="0" cellspacing="0" columnClasses="vdcMenubarCol1, vdcMenubarCol2"
        columns="2" id="gridPanel1" width="100%" rendered="#{VDCRequest.currentVDC != null}">
        <ui:panelGroup  block="true">
            
            <h:outputLink value="/dvn/dv/#{VDCRequest.currentVDC.alias}" title="#{VDCRequest.currentVDC.name} dataverse Homepage" >
                <h:outputText styleClass="vdcMenuItem" value="#{bundle.homeLabel} "/>
            </h:outputLink>
            
            <h:outputText styleClass="vdcMenuItemBorder" value=" | "/>
            <h:outputLink value="/dvn/dv/#{VDCRequest.currentVDC.alias}/faces/AboutPage.jsp" title="About #{VDCRequest.currentVDC.name} dataverse" >
                <h:outputText styleClass="vdcMenuItem" value="#{bundle.aboutLabel} "/>
            </h:outputLink>
            
             <h:outputText styleClass="vdcMenuItemBorder" value=" | "/>
             <h:outputLink value="http://thedata.org/help" target="_blank" title="Open User Manual">
                <h:outputText styleClass="vdcMenuItem" value="#{bundle.helpLabel}"/>
            </h:outputLink> 
            
             <h:outputText styleClass="vdcMenuItemBorder" value=" | "/>           
            <h:outputLink  id="hyperlink4b" value="/dvn/dv/#{VDCRequest.currentVDC.alias}/faces/SiteMapPage.jsp" title="Site Map for #{VDCRequest.currentVDC.name} dataverse">
                <h:outputText styleClass="vdcMenuItem" value="#{bundle.siteMapLabel}"/>
            </h:outputLink>
            
             <h:outputText styleClass="vdcMenuItemBorder" value=" | "/>
            <h:outputLink value="/dvn/dv/#{VDCRequest.currentVDC.alias}/faces/ContactUsPage.jsp" title="Contact #{VDCRequest.currentVDC.name} dataverse Help">
                <h:outputText styleClass="vdcMenuItem" value="#{bundle.contactUsLabel}"/>
            </h:outputLink>
            
            <h:outputText styleClass="vdcMenuItemBorder" value=" | " />
            
            <h:outputLink  rendered="#{VDCRequest.logoutPage or VDCSession.loginBean.user==null}" value="/dvn/dv/#{VDCRequest.currentVDC.alias}/faces/login/LoginPage.jsp" title="Login">
                <h:outputText styleClass="vdcMenuItem" value="#{bundle.loginLabel}"/>
            </h:outputLink>
           
            <h:outputLink rendered="#{VDCSession.loginBean.user!=null and !VDCRequest.logoutPage }" styleClass="vdcMenuItemLast" value="/dvn/dv/#{VDCRequest.currentVDC.alias}/faces/login/LogoutPage.jsp"  title="Log out">
                <h:outputText styleClass="vdcMenuItem" value="#{bundle.logoutLabel}"/>
            </h:outputLink>
            
        </ui:panelGroup>
        <ui:panelGroup  block="true" rendered="#{!VDCRequest.logoutPage}" style="padding-left: 20px;">
            <h:outputLink  styleClass="vdcMenuUserName" value="/dvn/dv/#{VDCRequest.currentVDC.alias}/faces/login/AccountPage.jsp?userId=#{VDCSession.loginBean.user.id}" title="Edit Account Information" rendered="#{VDCSession.loginBean.user.firstName != null}">
                <h:outputText   styleClass="vdcMenuUserName" value="#{VDCSession.loginBean.user.firstName} #{VDCSession.loginBean.user.lastName}"/>
            </h:outputLink>
             <h:outputText rendered="#{ VDCSession.loginBean.user.firstName == null }" styleClass="vdcMenuUserName" value="#{ VDCSession.ipUserGroup.friendlyName }"/>
           <h:outputLink  styleClass="vdcMenuUserOptions" value="/dvn/dv/#{VDCRequest.currentVDC.alias}/faces/admin/OptionsPage.jsp"  rendered="#{VDCSession.loginBean.admin or VDCSession.loginBean.curator  or VDCSession.loginBean.contributor  or VDCSession.loginBean.networkAdmin}" title="My Options in #{VDCRequest.currentVDC.name} dataverse">
                <h:outputText  value="#{bundle.myOptionsLabel}"/>
            </h:outputLink>

        </ui:panelGroup>
    </h:panelGrid>
</jsp:root>
