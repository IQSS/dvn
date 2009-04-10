/*
 * Dataverse Network - A web application to distribute, share and analyze quantitative data.
 * Copyright (C) 2007
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses
 * or write to the Free Software Foundation,Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

/*
 * LoginFilter.java
 *
 * Created on November 7, 2006, 2:42 PM
 */
package edu.harvard.hmdc.vdcnet.web.servlet;

import edu.harvard.hmdc.vdcnet.admin.GroupServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.NetworkRoleServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.PageDef;
import edu.harvard.hmdc.vdcnet.admin.PageDefServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.UserServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.RoleServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.UserGroup;
import edu.harvard.hmdc.vdcnet.admin.VDCRole;
import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import edu.harvard.hmdc.vdcnet.study.DataTable;
import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.study.StudyLock;
import edu.harvard.hmdc.vdcnet.study.StudyServiceLocal;
import edu.harvard.hmdc.vdcnet.study.VariableServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
import edu.harvard.hmdc.vdcnet.vdc.VDCServiceLocal;
import edu.harvard.hmdc.vdcnet.web.common.LoginBean;
import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import edu.harvard.hmdc.vdcnet.web.common.VDCSessionBean;
import java.io.*;
import java.util.Enumeration;
import java.util.Iterator;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.EJBs;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author  Ellen Kraffmiller
 * @version
 */

// These EJBs are defined here to make them available to any class in the WAR package.
// (Since it's a Filter on every request, we can be guaranteed that it will be instantiated.)
// We need to do this here so that objects which are not backing beans (for example StudyUI, CollectionUI)
// can have access to EJB Session Beans.
@EJBs({
@EJB(name = "collectionService", beanInterface = edu.harvard.hmdc.vdcnet.vdc.VDCCollectionServiceLocal.class),
@EJB(name = "studyService", beanInterface = edu.harvard.hmdc.vdcnet.study.StudyServiceLocal.class),
@EJB(name = "templateService", beanInterface = edu.harvard.hmdc.vdcnet.study.TemplateServiceLocal.class),
@EJB(name = "indexService", beanInterface = edu.harvard.hmdc.vdcnet.index.IndexServiceLocal.class),
@EJB(name = "catalogService", beanInterface = edu.harvard.hmdc.vdcnet.catalog.CatalogServiceLocal.class),
@EJB(name = "oaiSetService", beanInterface = edu.harvard.hmdc.vdcnet.vdc.OAISetServiceLocal.class),
@EJB(name = "harvestStudyService", beanInterface = edu.harvard.hmdc.vdcnet.harvest.HarvestStudyServiceLocal.class),
@EJB(name = "vdcGroupService", beanInterface = edu.harvard.hmdc.vdcnet.vdc.VDCGroupServiceLocal.class),
@EJB(name = "vdcService", beanInterface = edu.harvard.hmdc.vdcnet.vdc.VDCServiceLocal.class),
@EJB(name = "vdcUserService", beanInterface = edu.harvard.hmdc.vdcnet.admin.UserServiceLocal.class)
})
public class LoginFilter implements Filter {

    @EJB
    PageDefServiceLocal pageDefService;
    @EJB
    StudyServiceLocal studyService;
    @EJB
    VariableServiceLocal varService;
    @EJB
    UserServiceLocal userService;
    @EJB
    VDCServiceLocal vdcService;
    @EJB
    GroupServiceLocal groupService;
    @EJB
    VariableServiceLocal variableService;
    // The filter configuration object we are associated with.  If
    // this value is null, this filter instance is not currently
    // configured.
    private FilterConfig filterConfig = null;

    public LoginFilter() {
    }

    private void doBeforeProcessing(ServletRequest request, ServletResponse response)
            throws IOException, ServletException {
    }

    /**
     *
     * @param request The servlet request we are processing
     * @param result The servlet response we are creating
     * @param chain The filter chain we are processing
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String requestPath = httpRequest.getPathInfo();
        VDC currentVDC = vdcService.getVDCFromRequest(httpRequest);
        
        if (requestPath!=null && requestPath.endsWith(".jsp")) {
              String redirectURL = httpRequest.getContextPath();
              if (currentVDC!=null) {
                  redirectURL+="/dv/"+currentVDC.getAlias();
              }
              httpResponse.sendRedirect(redirectURL + "/faces/NotFoundPage.xhtml" );
              return;
        }
        PageDef pageDef = pageDefService.findByPath(requestPath);

        // check for invalid study Id
        // for right now, do this with a sendRedirect, though we should try to figure out a solution
        // with a forward isntead; that way the user can fix the issue in the URL and easily try again
        if ( isViewStudyPage(pageDef) || isEditStudyPage(pageDef) ) {
            Long studyId = determineStudyId(pageDef, httpRequest);
            if (studyId != null && studyId > 0 ) {
                try {
                    Study study = studyService.getStudy(studyId);
                } catch (EJBException e) {
                    if (e.getCause() instanceof IllegalArgumentException) {
                        String redirectURL = httpRequest.getContextPath();
                        if (currentVDC!=null) {
                            redirectURL+="/dv/"+currentVDC.getAlias();
                        }
                        httpResponse.sendRedirect(redirectURL + "/faces/IdDoesNotExistPage.xhtml" );
                        return;
                    } else {
                        throw e;
                    }
                }
            }
        }

        setOriginalUrl(httpRequest, httpResponse, currentVDC);

        LoginBean loginBean = getLoginBean(request);
        UserGroup ipUserGroup = null;
        if (loginBean == null) {
            ipUserGroup = getIpGroup(httpRequest);
        } else {
            HttpSession session =
                    ((HttpServletRequest) request).getSession(false);
        }

        if (httpRequest.getSession().getAttribute("LOGIN_REDIRECT") != null) {
            httpResponse.sendRedirect((String) httpRequest.getSession().getAttribute("LOGIN_REDIRECT"));
            httpRequest.getSession().removeAttribute("LOGIN_REDIRECT");
        } else {
            boolean authorized = false;
            if (isRolePage(pageDef, httpRequest)) {
                if (isUserAuthorizedForRolePage(pageDef, httpRequest, loginBean)) {
                    authorized = true;
                }

            } else if (isUserAuthorizedForNonRolePage(pageDef, httpRequest, loginBean, ipUserGroup)) {
                authorized = true;
            }

            if (!authorized) {
                if (loginBean == null) {
                    redirectToLogin(httpRequest, httpResponse, currentVDC);
                } else {
                    PageDef redirectPageDef = pageDefService.findByName(PageDefServiceLocal.UNAUTHORIZED_PAGE);
                    httpResponse.sendRedirect(httpRequest.getContextPath() + "/faces" + redirectPageDef.getPath());
                }
            } else {
                if (isCheckLockPage(pageDef) && studyLockedMessage(pageDef, httpRequest) != null) {

                    PageDef redirectPageDef = pageDefService.findByName(PageDefServiceLocal.STUDYLOCKED_PAGE);
                    httpResponse.sendRedirect(httpRequest.getContextPath() + "/faces" + redirectPageDef.getPath() + "?message=" + studyLockedMessage(pageDef, httpRequest));
                } else {

                    try {
                        chain.doFilter(request, response);
                    } catch (Throwable t) {
                        //
                        // If an exception is thrown somewhere down the filter chain,
                        // we still want to execute our after processing, and then
                        // rethrow the problem after that.
                        //

                        t.printStackTrace();
                    }
                }
            }
        }

    }

    private boolean isUserAuthorizedForRolePage(PageDef pageDef, HttpServletRequest request, LoginBean loginBean) {
        if (loginBean == null) {
            return false;
        }
        VDC currentVDC = vdcService.getVDCFromRequest(request);
        VDCUser user = loginBean.getUser();

        VDCRole userRole = null;
        String userRoleName = null;
        if (currentVDC != null) {
            userRole = loginBean.getVDCRole(currentVDC);
        }
        if (userRole != null) {
            userRoleName = userRole.getRole().getName();
        }


        if (user.getNetworkRole() != null && user.getNetworkRole().getName().equals(NetworkRoleServiceLocal.ADMIN)) {
            // If you are network admin, you can do anything!
            return true;

        }
        // Do special authorization for EditStudyPages 
        if (isEditStudyPage(pageDef)) {
            return isAuthorizedToEditStudy(pageDef, user, request, currentVDC);
        }

        // If this page has a network role, and it is being requested in a network context,
        // (that is, there is no current vdc), authorize the user if his network role matches the page role.
        if (pageDef != null && pageDef.getNetworkRole() != null && currentVDC == null) {
            if (user.getNetworkRole() != null) {
                if (user.getNetworkRole().getId().equals(pageDef.getNetworkRole().getId())) {
                    return true;
                } else {
                    return false;
                }
            } else
                return false;

        }
        // If this page has a VDC Role, and it is being requested in a VDC Context,
        // authorize the user if his Role has the required privileges
        if (pageDef != null && pageDef.getRole() != null && currentVDC != null) {
            String pageRoleName = pageDef.getRole().getName();
            if (userRoleName == null && !isUserStudyCreator(user, request)) {
                return false;
            }
            if (pageRoleName.equals(RoleServiceLocal.ADMIN)) {
                if (userRoleName.equals(RoleServiceLocal.ADMIN)) {
                    return true;
                } else {
                    return false;
                }
            }
            if (pageRoleName.equals(RoleServiceLocal.CURATOR)) {
                if (userRoleName.equals(RoleServiceLocal.CURATOR) || userRoleName.equals(RoleServiceLocal.ADMIN) || isUserStudyCreator(user, request)) {
                    return true;
                } else {
                    return false;
                }
            }
            if (pageRoleName.equals(RoleServiceLocal.CONTRIBUTOR)) {
                if (userRoleName.equals(RoleServiceLocal.CONTRIBUTOR) || userRoleName.equals(RoleServiceLocal.CURATOR) || userRoleName.equals(RoleServiceLocal.ADMIN)) {
                    return true;
                } else {
                    return false;
                }
            }

        }

        return true;
    }

    private boolean isUserAuthorizedForNonRolePage(PageDef pageDef, HttpServletRequest request, LoginBean loginBean, UserGroup ipUserGroup) {
        VDCUser user = null;
        if (loginBean != null) {
            user = loginBean.getUser();
        }
        
        if (user!=null && user.getNetworkRole() != null && user.getNetworkRole().getName().equals(NetworkRoleServiceLocal.ADMIN)) {
            // If you are network admin, you can do anything!
            return true;
        }
        
        VDC currentVDC = vdcService.getVDCFromRequest(request);
        if (currentVDC != null && !isTermsOfUsePage(pageDef) && isVdcRestricted(pageDef, request) ) {
            if (currentVDC.isVDCRestrictedForUser(user, ipUserGroup)) {
                return false;
            }
        } else if (isViewStudyPage(pageDef)) {
            String studyId = VDCBaseBean.getParamFromRequestOrComponent("studyId", request);
            Study study = studyService.getStudy(Long.parseLong(studyId));
            if (study.isStudyRestrictedForUser(user, ipUserGroup)) {
                return false;
            }

        } else if (isSubsettingPage(pageDef)) {
            String dtId = VDCBaseBean.getParamFromRequestOrComponent("dtId", request);
            DataTable dataTable = variableService.getDataTable(Long.parseLong(dtId));
            Study study = dataTable.getStudyFile().getFileCategory().getStudy();
            if (study.isStudyRestrictedForUser(user, ipUserGroup)) {
                return false;
            }        
        } else if (isEditAccountPage(pageDef)) {
            String userId = VDCBaseBean.getParamFromRequestOrComponent("userId", request);
            if (user==null || user.getId()!=Long.parseLong(userId)) {
                return false;
            }
        }
        return true;
    }

    private UserGroup getIpGroup(HttpServletRequest request) {
        UserGroup ipUserGroup = null;
        HttpSession session = ((HttpServletRequest) request).getSession(true);
        if (session.getAttribute("isIpGroupChecked") == null) {
            ipUserGroup = groupService.findIpGroupUser(request.getRemoteHost());
            if (ipUserGroup != null) {
                session.setAttribute("ipUserGroup", ipUserGroup);
                session.setAttribute("isIpGroupChecked", true);
            }
        } else {
            ipUserGroup = (UserGroup) session.getAttribute("ipUserGroup");
        }
        return ipUserGroup;
    }

    private boolean isEditStudyPage(PageDef pageDef) {

        if (pageDef != null &&
                (pageDef.getName().equals(PageDefServiceLocal.EDIT_STUDY_PAGE) || pageDef.getName().equals(PageDefServiceLocal.EDIT_VARIABLE_PAGE) || pageDef.getName().equals(PageDefServiceLocal.ADD_FILES_PAGE) || pageDef.getName().equals(PageDefServiceLocal.DELETE_STUDY_PAGE) || pageDef.getName().equals(PageDefServiceLocal.STUDY_PERMISSIONS_PAGE))) {
            return true;
        }
        return false;
    }

    private boolean isCheckLockPage(PageDef pageDef) {
         if (pageDef != null &&
                (pageDef.getName().equals(PageDefServiceLocal.EDIT_STUDY_PAGE) || pageDef.getName().equals(PageDefServiceLocal.EDIT_VARIABLE_PAGE)  || pageDef.getName().equals(PageDefServiceLocal.DELETE_STUDY_PAGE) || pageDef.getName().equals(PageDefServiceLocal.STUDY_PERMISSIONS_PAGE))) {
            return true;
        }
        return false;
       
    }
    private boolean isUserStudyCreator(VDCUser user, HttpServletRequest request) {
        boolean ret = false;
        String studyIdParam = getStudyIdFromRequest(request);
        if (studyIdParam != null) {
            Long studyId = new Long(Long.parseLong(studyIdParam));
            Study study = studyService.getStudy(studyId);
            if (study.getCreator().getId().equals(user.getId())) {
                ret = true;
            }
        }
        return ret;

    }

    /**
     * Used to extract studyId that was submitted as a JSF hidden field
     * (We can't just look for "studyId" because JSF assigns a parameter name
     * based on where it is in the component tree.)
     */
    private String getStudyIdFromRequest(HttpServletRequest request) {
        return getIdFromRequest("studyId", request);
    }

    private String getIdFromRequest(String idName, HttpServletRequest request) {
        String studyIdParam = request.getParameter(idName);
        if (studyIdParam == null) {
            Iterator iter = request.getParameterMap().keySet().iterator();
            while (iter.hasNext()) {
                Object key = (Object) iter.next();
                if (key instanceof String && ((String) key).indexOf(idName) != -1) {
                    studyIdParam = request.getParameter((String) key);
                    break;
                }
            }
        }
        return studyIdParam;

    }

    private boolean isAddStudyPage(PageDef pageDef, HttpServletRequest request) {
        if (pageDef != null && pageDef.getName().equals(PageDefServiceLocal.EDIT_STUDY_PAGE) && request.getParameter("studyId") == null) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isRolePage(PageDef pageDef, HttpServletRequest request) {
        //
        // Return true if pageDef has a network role or a vdc role
       
        //
        boolean rolePage = false;

        if (pageDef != null && (pageDef.getNetworkRole() != null || pageDef.getRole() != null) ) {
            rolePage = true;
        }
        return rolePage;
    }

    private boolean isViewStudyPage(PageDef pageDef) {
        if (pageDef != null && pageDef.getName().equals(pageDefService.VIEW_STUDY_PAGE)) {
            return true;
        }
        return false;
    }

      private boolean isEditAccountPage(PageDef pageDef) {
        if (pageDef != null && pageDef.getName().equals(pageDefService.EDIT_ACCOUNT_PAGE)) {
            return true;
        }
        return false;
    }
    private boolean isTermsOfUsePage(PageDef pageDef) {
         if (pageDef != null &&( pageDef.getName().equals(pageDefService.TERMS_OF_USE_PAGE) ||pageDef.getName().equals(pageDefService.ACCOUNT_TERMS_OF_USE_PAGE)) ) {
            return true;
        }
        return false;
       
    }
    private boolean isSubsettingPage(PageDef pageDef) {
        if (pageDef != null && pageDef.getName().equals(pageDefService.SUBSETTING_PAGE)) {
            return true;
        }
        return false;
    }

    private boolean isVdcRestricted(PageDef pageDef, HttpServletRequest request) {
        boolean restricted = false;
        VDC currentVDC = vdcService.getVDCFromRequest(request);
        if (pageDef != null && (pageDef.getName().equals(PageDefServiceLocal.LOGIN_PAGE) || pageDef.getName().equals(PageDefServiceLocal.LOGOUT_PAGE))) {
            restricted = false;
        } else if (currentVDC != null && currentVDC.isRestricted()) {
            restricted = true;
        }

        return restricted;
    }

    private void setOriginalUrl(HttpServletRequest request, HttpServletResponse response, VDC currentVDC) {
        String requestURI = request.getRequestURI();
        String originalUrl = requestURI;
        String queryString = null;

        if (currentVDC != null) {
            queryString = "?vdcId=" + currentVDC.getId();
        }

        if (request.getQueryString() != null) {
            if (queryString != null) {
                queryString += "&";
            } else {
                queryString = "?";
            }
            queryString += request.getQueryString();
        }

        Enumeration requestNames = request.getAttributeNames();
        while (requestNames.hasMoreElements()) {

            String requestName = (String) requestNames.nextElement();
            if (requestName.startsWith("userId")) {
                if (queryString != null) {
                    queryString += "&";
                } else {
                    queryString = "?";
                }
                queryString += requestName + "=" + request.getAttribute(requestName);
            }
        }
        if (queryString != null) {
            originalUrl += queryString;

        }
        if (requestURI.indexOf(".xhtml") > -1 
                && requestURI.indexOf(PageDefServiceLocal.LOGIN_PAGE) == -1
                && requestURI.indexOf(PageDefServiceLocal.LOGOUT_PAGE) == -1
                && requestURI.indexOf(PageDefServiceLocal.ADD_ACCOUNT_PAGE) == -1
                && requestURI.indexOf(PageDefServiceLocal.EDIT_ACCOUNT_PAGE) == -1
                && requestURI.indexOf(PageDefServiceLocal.UNAUTHORIZED_PAGE) == -1
                && requestURI.indexOf(PageDefServiceLocal.CONTRIBUTOR_REQUEST_ACCOUNT_PAGE) == -1
                && requestURI.indexOf(PageDefServiceLocal.CONTRIBUTOR_REQUEST_SUCCESS_PAGE) == -1
                && requestURI.indexOf(PageDefServiceLocal.CONTRIBUTOR_REQUEST_INFO_PAGE) == -1
                && requestURI.indexOf(PageDefServiceLocal.CONTRIBUTOR_REQUEST_PAGE) == -1
                && requestURI.indexOf(PageDefServiceLocal.CREATOR_REQUEST_ACCOUNT_PAGE) == -1
                && requestURI.indexOf(PageDefServiceLocal.CREATOR_REQUEST_SUCCESS_PAGE) == -1
                && requestURI.indexOf(PageDefServiceLocal.CREATOR_REQUEST_INFO_PAGE) == -1
                && requestURI.indexOf(PageDefServiceLocal.CREATOR_REQUEST_PAGE) == -1
                && requestURI.indexOf(PageDefServiceLocal.FILE_REQUEST_ACCOUNT_PAGE) == -1
                && requestURI.indexOf(PageDefServiceLocal.FILE_REQUEST_SUCCESS_PAGE) == -1
                && requestURI.indexOf(PageDefServiceLocal.FILE_REQUEST_PAGE) == -1 && request.getMethod().equals("GET")) {
            request.getSession().setAttribute("ORIGINAL_URL", originalUrl);
        }

    }

    private void redirectToLogin(HttpServletRequest request, HttpServletResponse response, VDC currentVDC) throws IOException, ServletException {
        String vdcParam = "?redirect=true";
        PageDef loginPage = pageDefService.findByName(PageDefServiceLocal.LOGIN_PAGE);
        if (currentVDC != null) {
            vdcParam += "&vdcId=" + currentVDC.getId();

        }
        response.sendRedirect(request.getContextPath() + "/faces" + loginPage.getPath() + vdcParam);



    }

    /**
     * Return the filter configuration object for this filter.
     */
    public FilterConfig getFilterConfig() {
        return (this.filterConfig);
    }

    /**
     * Set the filter configuration object for this filter.
     *
     * @param filterConfig The filter configuration object
     */
    public void setFilterConfig(FilterConfig filterConfig) {

        this.filterConfig = filterConfig;
    }

    /**
     * Destroy method for this filter
     *
     */
    public void destroy() {
    }

    /**
     * Init method for this filter
     *
     */
    public void init(FilterConfig filterConfig) {


    }

    public static LoginBean getLoginBean(ServletRequest request) {
        boolean isLoggedIn = false;
        HttpSession session =
                ((HttpServletRequest) request).getSession(false);
        LoginBean loginBean = null;
        // If there is a UserBean in the session, and it has
        // the isLoggedIn property set to true.
        if (session != null) {
            VDCSessionBean vdcSession = null;
            vdcSession = (VDCSessionBean) session.getAttribute("VDCSession");
            if (vdcSession != null) {
                loginBean = vdcSession.getLoginBean();
            }
        }
        return loginBean;
    }

    /**
     * Return a String representation of this object.
     */
    public String toString() {

        if (filterConfig == null) {
            return ("LoginFilter()");
        }
        StringBuffer sb = new StringBuffer("LoginFilter(");
        sb.append(filterConfig);
        sb.append(")");
        return (sb.toString());

    }

    private void sendProcessingError(Throwable t, ServletResponse response) {

        String stackTrace = getStackTrace(t);

        if (stackTrace != null && !stackTrace.equals("")) {

            try {

                response.setContentType("text/html");
                PrintStream ps = new PrintStream(response.getOutputStream());
                PrintWriter pw = new PrintWriter(ps);
                pw.print("<html>\n<head>\n<title>Error</title>\n</head>\n<body>\n"); //NOI18N

                // PENDING! Localize this for next official release
                pw.print("<h1>The resource did not process correctly</h1>\n<pre>\n");
                pw.print(stackTrace);
                pw.print("</pre></body>\n</html>"); //NOI18N
                pw.close();
                ps.close();
                response.getOutputStream().close();
                ;
            } catch (Exception ex) {
            }
        } else {
            try {
                PrintStream ps = new PrintStream(response.getOutputStream());
                t.printStackTrace(ps);
                ps.close();
                response.getOutputStream().close();
                ;
            } catch (Exception ex) {
            }
        }
    }

    public static String getStackTrace(Throwable t) {

        String stackTrace = null;

        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            pw.close();
            sw.close();
            stackTrace = sw.getBuffer().toString();
        } catch (Exception ex) {
        }
        return stackTrace;
    }

    public void log(String msg) {
        filterConfig.getServletContext().log(msg);
    }

    /**
     * 
     */
    private boolean isAuthorizedToEditStudy(PageDef pageDef, VDCUser user, HttpServletRequest request, VDC currentVDC) {
        boolean authorized = false;
        // If this is a new study being created, then user is authorized if he or she is admin, curator or contributor
        // in currentVDC
        if (pageDef.getName().equals(PageDefServiceLocal.EDIT_STUDY_PAGE) && (getStudyIdFromRequest(request) == null || Integer.parseInt(getStudyIdFromRequest(request)) < 0)) {
            String currentVDCRoleName = null;
            if (currentVDC != null && user.getVDCRole(currentVDC) != null) {
                currentVDCRoleName = user.getVDCRole(currentVDC).getRole().getName();
            }
            if (currentVDC != null && (currentVDCRoleName.equals(RoleServiceLocal.ADMIN) || currentVDCRoleName.equals(RoleServiceLocal.CURATOR) || currentVDCRoleName.equals(RoleServiceLocal.CONTRIBUTOR))) {
                authorized = true;
            }
        } else {
            // If we are editing an existing study, then the user
            // must be admin or curator in the owning VDC, or user must be
            // study creator.
            Long studyId = Long.parseLong(getStudyIdFromRequest(request));
            Study study = studyService.getStudy(studyId);
            authorized = study.isUserAuthorizedToEdit(user);

        }
        return authorized;
    }

    private String studyLockedMessage(PageDef pageDef, HttpServletRequest request) {
        Long studyId = determineStudyId(pageDef, request);
        String studyLockMessage = null;
        StudyLock studyLock = null;
        if (studyId != null) {
            Study study = null;
            if (studyId > 0) { // If Id<0, this means we are adding a new study, no need to check for a lock
                study = studyService.getStudy(studyId);
                studyLock = study.getStudyLock();
            }
            if (studyLock != null) {
                studyLockMessage = "Study upload details: " + study.getGlobalId() + " - " + studyLock.getDetail();
            }
        }
        System.out.println("Study locked = " + studyLock != null);

        return studyLockMessage;

    }

    private Long determineStudyId(PageDef pageDef, HttpServletRequest request) {
        if (pageDef.getName().equals(PageDefServiceLocal.EDIT_VARIABLE_PAGE)) {
            String dtIdParam = getIdFromRequest("dtId", request);
            return varService.getDataTable(new Long(dtIdParam)).getStudyFile().getFileCategory().getStudy().getId();
        }

        String studyIdParam = getStudyIdFromRequest(request);

        if (studyIdParam != null) {
            return new Long(studyIdParam);
        }

        return null;
    }
}


