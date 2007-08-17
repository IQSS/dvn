<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
    xmlns:h="http://java.sun.com/jsf/html" 
    xmlns:jsp="http://java.sun.com/JSP/Page" 
    xmlns:ui="http://www.sun.com/web/ui"
    xmlns:tiles="http://struts.apache.org/tags-tiles"
    xmlns:c="http://java.sun.com/jsp/jstl/core">
        
<div class="dvn_section">
    
        <div class="dvn_headerTitle">
            <h5><span>powered by the </span> <a href="http://thedata.org/"><b>DATAVERSE NETWORK&#8482;</b> <em>PROJECT</em></a></h5>
            <h3>
             <h:outputText  value="&#160;" rendered="#{VDCRequest.currentVDC == null}" />
             <h:outputLink value="/dvn" title="#{VDCRequest.vdcNetwork.name} Dataverse Network Homepage" rendered="#{VDCRequest.currentVDC != null}">
                <h:outputText  value="#{VDCRequest.vdcNetwork.name} Dataverse Network &#160;&gt;"  />
             </h:outputLink>
            </h3>
            <h1>
             <h:outputLink value="/dvn"   title="#{VDCRequest.vdcNetwork.name} Dataverse Network Homepage" rendered="#{VDCRequest.currentVDC == null}">
                <h:outputText  value="#{VDCRequest.vdcNetwork.name} Dataverse Network"/>
             </h:outputLink>
             <h:outputLink value="/dvn/dv/#{VDCRequest.currentVDC.alias}"  title="#{VDCRequest.currentVDC.name} dataverse Homepage" rendered="#{VDCRequest.currentVDC != null}">
                <h:outputText value="#{VDCRequest.currentVDC.name} Dataverse " />
             </h:outputLink>
             </h1>
        </div>
        
        <div class="dvn_headerBar">
            <div class="dvn_headerBarContent">
                <ul class="dvn_floatL">
                    <li>
                      <h:outputLink  value="/dvn" title="Browse and Search #{VDCRequest.vdcNetwork.name} Dataverse Network" rendered="#{VDCRequest.currentVDC == null}">
                        <h:outputText value="Search/Browse "/>
                       </h:outputLink>
                      <h:outputLink value="/dvn/dv/#{VDCRequest.currentVDC.alias}" title="Browse and Search #{VDCRequest.currentVDC.name} dataverse" rendered="#{VDCRequest.currentVDC != null}">
                        <h:outputText value="Search/Browse "/>
                      </h:outputLink>
                    </li>
                    <li>
                      <h:outputLink  value="/dvn/faces/AboutPage.jsp" title="About #{VDCRequest.vdcNetwork.name} Dataverse Network" rendered="#{VDCRequest.currentVDC == null}">
                        <h:outputText value="#{bundle.aboutLabel} "/>
                       </h:outputLink>
                      <h:outputLink value="/dvn/dv/#{VDCRequest.currentVDC.alias}/faces/AboutPage.jsp" title="About #{VDCRequest.currentVDC.name} dataverse" rendered="#{VDCRequest.currentVDC != null}">
                        <h:outputText value="#{bundle.aboutLabel} "/>
                      </h:outputLink>
                    </li>
                    <li>
                      <h:outputLink  value="http://thedata.org/help" target="_blank" title="Open Help Manual">
                         <h:outputText value="#{bundle.helpLabel}"/>
                      </h:outputLink>
                    </li>
                    <li>
                     <h:outputLink  value="/dvn/faces/SiteMapPage.jsp" title="Site Map for #{VDCRequest.vdcNetwork.name} Dataverse Network" rendered="#{VDCRequest.currentVDC == null}">
                        <h:outputText value="#{bundle.siteMapLabel}"/>
                     </h:outputLink>
                     <h:outputLink  id="hyperlink4b" value="/dvn/dv/#{VDCRequest.currentVDC.alias}/faces/SiteMapPage.jsp" title="Site Map for #{VDCRequest.currentVDC.name} dataverse" rendered="#{VDCRequest.currentVDC != null}">
                        <h:outputText value="#{bundle.siteMapLabel}"/>
                     </h:outputLink>
                    </li>
                    <li>
                      <h:outputLink  value="/dvn/faces/ContactUsPage.jsp"  title="Contact #{VDCRequest.vdcNetwork.name} Dataverse Network Help" rendered="#{VDCRequest.currentVDC == null}">
                        <h:outputText value="#{bundle.contactUsLabel}"/>
                     </h:outputLink>
                     <h:outputLink value="/dvn/dv/#{VDCRequest.currentVDC.alias}/faces/ContactUsPage.jsp" title="Contact #{VDCRequest.currentVDC.name} dataverse Help" rendered="#{VDCRequest.currentVDC != null}">
                        <h:outputText value="#{bundle.contactUsLabel}"/>
                        </h:outputLink>
                    </li>
                    <li>
                      <h:outputLink  rendered="#{VDCRequest.logoutPage or VDCSession.loginBean.user==null}" value="/dvn#{VDCRequest.currentVDCURL}/faces/login/LoginPage.jsp" title="Login">
                        <h:outputText value="#{bundle.loginLabel}"/>
                      </h:outputLink>
           
                    <h:outputLink rendered="#{VDCSession.loginBean.user!=null and !VDCRequest.logoutPage }"  value="/dvn#{VDCRequest.currentVDCURL}/faces/login/LogoutPage.jsp"  title="Logout">
                        <h:outputText value="#{bundle.logoutLabel}"/>
                    </h:outputLink>  
                    </li>
                </ul>
                <ul class="dvn_floatR">
                    <li class="dvn_networkOptions">
                    <h:outputLink   value="/dvn#{VDCRequest.currentVDCURL}/faces/login/AccountPage.jsp?userId=#{VDCSession.loginBean.user.id}" title="Edit Account Information" rendered="#{!VDCRequest.logoutPage and VDCSession.loginBean.user.firstName != null}">
                        <h:outputText value="#{VDCSession.loginBean.user.firstName} #{VDCSession.loginBean.user.lastName}"/>
                    </h:outputLink>
                    <h:outputText rendered="#{!VDCRequest.logoutPage and VDCSession.loginBean.user.firstName == null }" value="#{ VDCSession.ipUserGroup.friendlyName }"/>
                    </li>
                    <li class="dvn_networkOptions dvn_boldRed">
                    
                    <h:outputLink  value="/dvn#{VDCRequest.currentVDCURL}/faces/networkAdmin/NetworkOptionsPage.jsp" rendered="#{VDCRequest.currentVDC == null and !VDCRequest.logoutPage and VDCRequest.currentVDC == null and VDCSession.loginBean.networkAdmin }" title="#{VDCRequest.vdcNetwork.name} Dataverse Network Admin Options">
                        <h:outputText  value="Network Options"/>
                    </h:outputLink>
                    
                    <h:outputLink value="/dvn#{VDCRequest.currentVDCURL}/faces/site/AddSitePage.jsp" rendered="#{VDCRequest.currentVDC == null and !VDCRequest.logoutPage and VDCRequest.currentVDC == null and (VDCSession.loginBean.networkCreator) }" title="Create a new dataverse in the Network">
                        <h:outputText  value="Create a Dataverse"/>
                    </h:outputLink>
                    
                    <h:outputLink  value="/dvn#{VDCRequest.currentVDCURL}/faces/admin/OptionsPage.jsp"  rendered="#{VDCRequest.currentVDC != null and !VDCRequest.logoutPage and (VDCSession.loginBean.admin or VDCSession.loginBean.curator  or VDCSession.loginBean.contributor  or VDCSession.loginBean.networkAdmin)}" title="My Options in #{VDCRequest.currentVDC.name} dataverse">
                        <h:outputText  value="#{bundle.myOptionsLabel}"/>
                    </h:outputLink>       
                    </li>
                </ul>
            </div>
            <span class="dvn_xbottom"><span class="dvn_xb4"><h:outputText  value="&#160;" escape="false"/></span><span class="dvn_xb3"><h:outputText  value="&#160;" escape="false"/></span><span class="dvn_xb2"><h:outputText  value="&#160;" escape="false"/></span><span class="dvn_xb1"><h:outputText  value="&#160;" escape="false"/></span></span>
        </div>
    
    </div>

</jsp:root>
