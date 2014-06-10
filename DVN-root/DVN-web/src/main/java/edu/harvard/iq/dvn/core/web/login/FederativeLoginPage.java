// Licence info to be added
package edu.harvard.iq.dvn.core.web.login;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.icesoft.faces.component.ext.HtmlInputHidden;

import edu.harvard.iq.dvn.core.admin.GroupServiceLocal;
import edu.harvard.iq.dvn.core.admin.UserServiceLocal;
import edu.harvard.iq.dvn.core.admin.VDCUser;
import edu.harvard.iq.dvn.core.vdc.VDCNetworkServiceLocal;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import edu.harvard.iq.dvn.core.web.util.AccessExpressionParser;

/**
 *
 * @author Eko Indarto
 */
@Named("FederativeLoginPage")
@ViewScoped
public class FederativeLoginPage extends VDCBaseBean implements java.io.Serializable {


    @EJB
    UserServiceLocal userService;
    @EJB
    GroupServiceLocal groupService;
    @EJB
    VDCNetworkServiceLocal vdcNetworkService;
    // ---
    
    private final static Logger LOGGER = Logger.getLogger(FederativeLoginPage.class.getPackage().getName());
    String refererUrl = "";
    private String errMessage = "";
    String userId = "";
    HttpServletRequest request;
    HttpServletResponse response;
    private boolean loginFailed;
    private String redirect;
    private Long studyId;
    protected String tab;
    private HtmlInputHidden hiddenStudyId;
    private Boolean clearWorkflow = true;
    private String ATTR_NAME_EMAIL = "mail";
    private String ATTR_NAME_SURNAME = "sn";
    private String ATTR_NAME_PREFIX = "prefix";
    private String ATTR_NAME_GIVENNAME = "givenName";
    private String ATTR_NAME_ROLE = "eduPersonAffiliation";
    private String ATTR_NAME_ORG = "schacHomeOrganization";
    private String ATTR_NAME_PRINCIPAL = "eduPersonPrincipalName";
    
    /* Shibboleth attributes */
    private String SHIB_ATTR_NAME_EMAIL = "Shib_email";
    private String SHIB_ATTR_NAME_SURNAME = "Shib_surName";
    private String SHIB_ATTR_NAME_PREFIX = "prefix";
    private String SHIB_ATTR_NAME_GIVENNAME = "Shib_givenName";
    private String SHIB_ATTR_NAME_ROLE = "Shib_affiliation";//"eduPersonAffiliation";
    private String SHIB_ATTR_NAME_ORG = "Shib_HomeOrg";
    private String SHIB_ATTR_NAME_PRINCIPAL = "Shib_eduPersonPN";
    
    private String ACL_ADMIN = null;
    private String ACL_CREATOR = null;
    private String ACL_USER = null;
    private Boolean ALLOW_ADMIN = false;
    private Boolean USE_REFERER = false;
    private String USERID_METHOD = "attr";
    private String USERID_ATTR = "email";
    private String USERID_PREFIX = "";
    private HashMap userdata = new HashMap();
    private Map<String, String> shibProps;
    private String SHIB_PROPS_SESSION = "shibPropsSession";

    /**
     * <p>Callback method that is called whenever a page is navigated to,
     * either directly via a URL, or indirectly via page navigation.
     * Customize this method to acquire resources that will be needed
     * for event handlers and lifecycle methods, whether or not this
     * page is performing post back processing.</p>
     *
     * <p>Note that, if the current request is a postback, the property
     * values of the components do <strong>not</strong> represent any
     * values submitted with this request.  Instead, they represent the
     * property values that were saved for this view when it was rendered.</p>
     */
    @Override
    public void init() {
        super.init();
        
        if (clearWorkflow != null) {
            LoginWorkflowBean lwf = (LoginWorkflowBean) getBean("LoginWorkflowBean");
            lwf.clearWorkflowState();
        }
        
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        HttpSession session = (HttpSession) context.getSession(true);
        request = (HttpServletRequest) context.getRequest();
        response = (HttpServletResponse) context.getResponse();
        String protocol = resolveProtocol(request.getServerPort());
        String defaultPage = "";
        String serverPort = (request.getServerPort() != 80) ? ":" + request.getServerPort() : "";
        if (getVDCRequestBean().getCurrentVDC() != null) {
            defaultPage = protocol + "://" + request.getServerName() + serverPort + request.getContextPath() + "/dv/" + getVDCRequestBean().getCurrentVDC().getAlias();
        } else {
            defaultPage = protocol + "://" + request.getServerName() + serverPort + request.getContextPath();
        }
        if (USE_REFERER && request.getHeader("referer") != null && !request.getHeader("referer").equals("")) {
            if (request.getHeader("referer").indexOf("/login/") != -1 || request.getHeader("referer").contains("/admin/") || request.getHeader("referer").contains("/networkAdmin/")) {
                refererUrl = defaultPage;
            } else {
                refererUrl = request.getHeader("referer");
            }
        } else {
            refererUrl = defaultPage;
        }
        
        // Get Shibboleth session variables
        shibProps = (Map<String, String>)session.getAttribute(SHIB_PROPS_SESSION);
        

        if (shibProps == null || shibProps.isEmpty()) {
        	errMessage = "ERROR:  is empty. Try to read shibboleth properties again.";
        	LOGGER.log(Level.SEVERE, errMessage);
        	//try to read the shibboleth environment 
        	readShibProps();
        } 
       
        ///check the shibProps again and if it is still null redirect to surfconnect
        if (shibProps == null || shibProps.isEmpty()) {
        	String baseUrl = request.getRequestURL().substring(0, request.getRequestURL().length() - request.getRequestURI().length());
        	String surfconnectUrl = baseUrl + "/Shibboleth.sso/Login?target=" + baseUrl + "%2Fdvn%2Ffaces%2Flogin%2FFederativeLoginPage.xhtml%3FclearWorkflow%3Dtrue";
        	try {
        		LOGGER.log(Level.INFO, "surfconnectUrl: " + surfconnectUrl);
				response.sendRedirect(surfconnectUrl);
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, e.getMessage());
			}
            errMessage = "No assertion; this stage should never be reached; check the Shibboleth configuration";
            loginFailed = true;
            LOGGER.log(Level.SEVERE, errMessage);
        }
    
    	/*
    	 * Shibboleth login worked; find the Dataverse user or proceed to adding the account. 
    	 * 
    	 * A Shibboleth-authorised user may have zero or more email addresses in mixed case.
    	 * Make sure we have an email address (to enable lookup) and that it is always in
    	 * lower case. Case in email addresses may change over time.
    	 */
        
    	LOGGER.log(Level.INFO, "Reading email attribute as ", shibProps.get(ATTR_NAME_EMAIL));
        String attr_email = shibProps.get(ATTR_NAME_EMAIL).toLowerCase();
        
        if (attr_email == null) {
        	errMessage = "No email address was found";
        	loginFailed = true;
        	LOGGER.log(Level.SEVERE, "No email address was found in the Shibboleth attributes.");
        }
        
        final Iterator<String> email_list = Arrays.asList(attr_email.split(",")).iterator();
        VDCUser user = null;
        try {
        	/*
        	 * Find the user based on her/his email address.
        	 * 
        	 * There may be more than one user with a certain address,
        	 * though only one should be active at a time.
        	 */
            while (email_list.hasNext() && user == null) {
                String email = (String) email_list.next();
                user = userService.findByEmail(email);
            }
            if (user != null) {
                if (user.isActive()) {
                    LOGGER.log(Level.INFO, "User is active!", user.getUserName());
                    if (!user.isNetworkAdmin() || ALLOW_ADMIN) {
                        final String forward = dvnLogin(user, studyId);
                        LOGGER.log(Level.INFO, "User forwarded to {0}", forward);
                        redirect = forward;
                        /*
                         * A user reported not being forwarded to the next page after logging in.
                         * The logs show he (and others) should have been forwarded to /login/AccountTermsOfUsePage?faces-redirect=true
                         * Ben assumes the redirect somehow stopped here (i.e. was not performed),
                         *  because of the requirement of `forward.startsWith("/HomePage")`.
                         * Put the check back in place.
                         */
                        if (forward != null && forward.startsWith("/HomePage")) {
                            try {
                                LOGGER.log(Level.INFO, "refererUrl + redirect = {0}", refererUrl + redirect);
                                response.sendRedirect(refererUrl);
                                //response.sendRedirect(refererUrl + redirect);
                            	//response.sendRedirect(redirect); // `refererUrl + redirect`?!
                            } catch (IOException ex) {
                                errMessage = ex.toString();
                                LOGGER.log(Level.SEVERE, null, ex);
                            }
                        } /* else {
                        	LOGGER.log(Level.SEVERE, "No forward location received or , sending user back to {0}.", refererUrl);
                        	try {
                                response.sendRedirect(refererUrl);
                                //response.sendRedirect(refererUrl + redirect);
                            } catch (IOException ex) {
                                errMessage = ex.toString();
                                LOGGER.log(Level.SEVERE, null, ex);
                            }
                        } */
                    } else {
                        loginFailed = true;
                        errMessage = "Admin access is not allowed using federated login";
                    }
                } else {
                    loginFailed = true;
                    errMessage = "Account is not active";
                    LOGGER.log(Level.INFO, "User {0} not active!", user.getUserName());
                }
            } else {
            	/*
            	 * User is not in the database, prepare to create a new account.
            	 */
                final String usrgivenname = shibProps.get(ATTR_NAME_GIVENNAME);
                final String usrprefix = shibProps.get(ATTR_NAME_PREFIX);
                final String usrsurname = shibProps.get(ATTR_NAME_SURNAME);
                final String usremail = shibProps.get(ATTR_NAME_EMAIL).toLowerCase();
                final String usrprincipal = shibProps.get(ATTR_NAME_PRINCIPAL);
                final String usrrole = shibProps.get(ATTR_NAME_ROLE);
                final String usrorg = shibProps.get(ATTR_NAME_ORG);

                if (usrgivenname != null) {
                    LOGGER.log(Level.INFO, "Given Name: ", usrgivenname);
                    userdata.put("givenname", usrgivenname);
                }
                if (usrprefix != null) {
                    LOGGER.log(Level.INFO, "Prefix: ", usrprefix);
                    userdata.put("prefix", usrprefix);
                }
                if (usrsurname != null) {
                    LOGGER.log(Level.INFO, "Surname: ", usrsurname);
                    userdata.put("surname", usrsurname);
                }
                if (usremail != null) {
                    LOGGER.log(Level.INFO, "Email: ", usremail);
                    userdata.put("email", usremail);
                }
                if (usrprincipal != null) {
                    LOGGER.log(Level.INFO, "Principal Name: ", usrprincipal);
                    userdata.put("principal", usrprincipal);
                }
                if (usrrole != null) {
                    LOGGER.log(Level.INFO, "Role: ", usrrole);
                    userdata.put("role", usrrole);
                }
                if (usrorg != null) {
                    LOGGER.log(Level.INFO, "Organization:", usrorg);
                    userdata.put("organization", usrorg);
                }
                final String usertype = getUserType(userdata);
                LOGGER.log(Level.INFO, "User type: {0}", usertype);
                final String tempusername = uniqueUserId(userdata);

                if (!ALLOW_ADMIN && "admin".equals(usertype)) {
                    loginFailed = true;
                    errMessage = "Admin access is not allowed using federated login";
                    String name = ((usrprincipal != null) ? usrprincipal : ((usremail != null) ? usremail : "unknown"));
                    LOGGER.log(Level.SEVERE, "Admin login not allowed for {0}!", name);
                } else if (usertype != null && tempusername != null) {
                    session.setAttribute("usrusertype", usertype);
                    session.setAttribute("usrusername", tempusername);
                    session.setAttribute("ALLOW_ADMIN", ALLOW_ADMIN);
                    if (usrgivenname != null) {
                        session.setAttribute("usrgivenname", usrgivenname);
                    }
                    if (usrprefix != null) {
                        session.setAttribute("usrprefix", usrprefix);
                    }
                    if (usrsurname != null) {
                        session.setAttribute("usrsurname", usrsurname);
                    }
                    if (usremail != null) {
                        session.setAttribute("usremail", usremail);
                    }
                    if (usrprincipal != null) {
                        session.setAttribute("usrprincipal", usrprincipal);
                    }
                    if (usrrole != null) {
                        session.setAttribute("usrrole", usrrole);
                    }
                    if (usrorg != null) {
                        session.setAttribute("usrorg", usrorg);
                    }
                    redirectToShibAddAccount();
                } else {
                    loginFailed = true;
                    if (tempusername == null) {
                        errMessage = "Unable to determine a unique user id";
                    } else {
                        errMessage = "You are not allowed to log in using your federation account";
                    }
                    String name = ((usrprincipal != null) ? usrprincipal : ((usremail != null) ? usremail : "unknown"));
                    LOGGER.log(Level.INFO, "Login not allowed for {0}!", name);
                }
            }
        } catch (Exception e) {
            errMessage = e.toString();
            LOGGER.log(Level.SEVERE, null, e);
        }
        
    }

    private String resolveProtocol(int portNumber) {
        //String protocol = request.getProtocol().substring(0, request.getProtocol().indexOf("/")).toLowerCase();
        //Something went wrong in setting the protocol according to the request. Hard coded based on the port number
        
        switch (portNumber) {
            case 443:
                return "https";
            case 80:
            default:
                return "http";
        }
        
    }

    /** 
     * <p>Construct a new Page bean instance.</p>
     */
    public FederativeLoginPage() {
    	readShibProps();
    }
    
    /**
     * Process the login for a user (possibly looking at a study).
     * @param user the user
     * @param studyId an optional study ID
     * @return a String of the next page to go to
     */
    private String dvnLogin(VDCUser user, Long studyId) {
        LOGGER.log(Level.INFO, "dvnLogin for user {0}", user.getUserName());
        LoginWorkflowBean lwf = (LoginWorkflowBean) getBean("LoginWorkflowBean");
        LOGGER.log(Level.INFO, "Workflow available; processing login");
        return lwf.processLogin(user, studyId);
    }

    public String samlLogin() {
        // Unused as of now
        return null;
    }

    /**
     * Getter for property loginFailed.
     * @return Value of property loginFailed.
     */
    public boolean isLoginFailed() {
        return this.loginFailed;
    }

    /**
     * Setter for property loginFailed.
     * @param loginFailed New value of property loginFailed.
     */
    public void setLoginFailed(boolean loginFailed) {
        this.loginFailed = loginFailed;
    }

    /**
     * Getter for property errMessage.
     * @return Value of property errMessage.
     */
    public String getErrMessage() {
        return errMessage;
    }

    /**
     * Setter for property errMessage.
     * @param errMessage New value of property errMessage.
     */
    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }

    /**
     * Getter for property redirect.
     * @return Value of property redirect.
     */
    public String getRedirect() {
        return this.redirect;
    }

    /**
     * Setter for property redirect.
     * @param redirect New value of property redirect.
     */
    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    /**
     * Getter for property studyId.
     * @return Value of property studyId.
     */
    public Long getStudyId() {
        return this.studyId;
    }

    /**
     * Setter for property studyId.
     * @param studyId New value of property studyId.
     */
    public void setStudyId(Long studyId) {
        this.studyId = studyId;
    }

    /**
     * Get the value of tab
     *
     * @return the value of tab
     */
    public String getTab() {
        return tab;
    }

    /**
     * Set the value of tab
     *
     * @param tab new value of tab
     */
    public void setTab(String tab) {
        this.tab = tab;
    }

    /**
     * Getter for property hiddenStudyId.
     * @return Value of property hiddenStudyId.
     */
    public HtmlInputHidden getHiddenStudyId() {
        return this.hiddenStudyId;
    }

    /**
     * Setter for property hiddenStudyId.
     * @param hiddenStudyId New value of property hiddenStudyId.
     */
    public void setHiddenStudyId(HtmlInputHidden hiddenStudyId) {
        this.hiddenStudyId = hiddenStudyId;
    }

    public Boolean isClearWorkflow() {
        return clearWorkflow;
    }

    public void setClearWorkflow(Boolean clearWorkflow) {
        this.clearWorkflow = clearWorkflow;
    }

    /**
     * Read the Shibboleth properties from the request and put them in the session variable.
     */
    private void readShibProps() {
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        HttpSession session = (HttpSession) context.getSession(true);
        
        request = (HttpServletRequest) FacesContext.getCurrentInstance()
                .getExternalContext().getRequest();
        
        shibProps = getShibAttValues(request);
        
        session.setAttribute(SHIB_PROPS_SESSION, shibProps);

    }

    /**
     * Evaluate whether the user data matches the ACL
     * @param acl an encoded access control list
     * @param userdata the user data
     * @return true when the user data matches the ACL, false otherwise
     */
    private Boolean evaluateAccess(final String acl, final HashMap userdata) {
        Boolean access = false;
        try {
            AccessExpressionParser aep = new AccessExpressionParser(acl);
            access = aep.evaluate(userdata);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error evaluating access", e);
        }
        return access;
    }

    /**
     * Determine the user type (admin, creator or user) based on user's characteristics.
     * Check, in order of most rights, whether the user is admin, creator or normal user.
     * @param userdata user data
     * @return String "admin", "creator", "user" or null after evaluating the ACLs
     */
    private String getUserType(HashMap userdata) {
        if (ACL_ADMIN != null && evaluateAccess(ACL_ADMIN, userdata)) {
            LOGGER.log(Level.FINE, "User type Admin");
            return "admin";
        }
        if (ACL_CREATOR != null && evaluateAccess(ACL_CREATOR, userdata)) {
            LOGGER.log(Level.FINE, "User type Creator");
            return "creator";
        }
        if (ACL_USER != null && evaluateAccess(ACL_USER, userdata)) {
            LOGGER.log(Level.FINE, "User type User");
            return "user";
        }
        return null;
    }
    
    public String getUUID() {
        return UUID.randomUUID().toString().toLowerCase();
    }

    /**
     * Return a copy of a string without non alpha-numeric characters.
     * @param in a string
     * @return a copy of the input string without character outside the [a-zA-Z0-9] range
     */
    public String stripNonAlphaNum(String in) {
        String out = "";
        if (in == null) {
            return "";
        }
        int l = in.length();
        for (int i = 0; i < l; i++) {
            char c = in.charAt(i);
            if ((c >= 'A' & c <= 'Z')
                    || (c >= 'a' & c <= 'z')
                    || (c >= '0' & c <= '9')) {
                out += c;
            }
        }
        return out;
    }

    /**
     * Generate a unique user ID either based on the provided user data or a UUID
     * @param data user data. If <code>USERID_METHOD</code> is <code>attr</code>, this should not be null.
     * @return a unique user ID
     */
    private String uniqueUserId(Map<String, Object> data) {
        if ("uuid".equalsIgnoreCase(USERID_METHOD)) {
            LOGGER.log(Level.INFO, "User Id generation method: UUID");
            String uuid = stripNonAlphaNum(getUUID());
            LOGGER.log(Level.INFO, "Generated user id {0}.", uuid);
            return uuid;
        }
        if ("attr".equalsIgnoreCase(USERID_METHOD)) {
            LOGGER.log(Level.INFO, "User Id generation method: attribute {0}", USERID_ATTR);
            String base = null;
            if ("surname".equalsIgnoreCase(USERID_ATTR)) {
                base = (String) data.get("surname");
            } else if ("givenname".equalsIgnoreCase(USERID_ATTR)) {
                base = (String) data.get("givenname");
            } else if ("principal".equalsIgnoreCase(USERID_ATTR)) {
                base = (String) data.get("principal");
            } else { // email
            	Object o = data.get("email");
            	if (o instanceof List) {
                final List emaillist = (List) data.get("email");
                if (emaillist != null && emaillist.size() > 0) {
                    base = (String) emaillist.get(0); // first item in list
                }
            	} else {
            		base = (String)o;
            	}
            }
            if (base != null) {
                if (USERID_PREFIX != null) {
                    base = USERID_PREFIX + base;
                }
                String tuid = stripNonAlphaNum(base);
                String xuid = tuid;
                LOGGER.log(Level.INFO, "Base user id {0}.", tuid);
                int n = 0;
                Boolean unique = false;
                while (!unique) {
                    VDCUser user = userService.findByUserName(xuid);
                    unique = (user == null);
                    if (!unique) {
                        n++;
                        xuid = tuid + Integer.toString(n);
                    }
                }
                LOGGER.log(Level.INFO, "Generated user id {0}.", xuid);
                return xuid;
            }
        }
        LOGGER.log(Level.SEVERE, "Unable to generate user id");
        return null;
    }

    private HttpServletResponse getHttpResponse() {
        if (response == null) {
            ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
            HttpSession session = (HttpSession) context.getSession(true);
            response = (HttpServletResponse) context.getResponse();
        }
        return response;
    }

    private void redirectToShibAddAccount() {
        FacesContext fc = javax.faces.context.FacesContext.getCurrentInstance();
        HttpServletResponse hresponse = (javax.servlet.http.HttpServletResponse) fc.getExternalContext().getResponse();
        String requestContextPath = fc.getExternalContext().getRequestContextPath();
        try {
            hresponse.sendRedirect(requestContextPath + "/faces/login/FederativeAddAccountPage.xhtml");
            fc.responseComplete();
        } catch (IOException ex) {
            throw new RuntimeException("IOException thrown while trying to redirect to addaccount");
        }
    }

    
    /**
     * Read the Shibboleth properties from the HTTP request and map them.
     * FIXME: Instead of reading the mapping from an external file, this 
     * method specifies the mapping. This means you have to redeploy when something changes.
     *  
     * SamlLogin.properties mapping to shibboleth
     * /home/ubudvn_homedir/glassfish3/glassfish/domains/domain1/applications/DVN-web/WEB-INF/SamlLogin.properties
     */
    private Map<String, String> getShibAttValues(HttpServletRequest request) {
    	Map<String, String> shibAtt = new HashMap<String, String>();
    	
    	/* User attribute names */
    	//saml.attributes.email=urn:mace:dir:attribute-def:mail
    	if (request.getAttribute(SHIB_ATTR_NAME_EMAIL) != null) {
    		shibAtt.put(ATTR_NAME_EMAIL, (String)request.getAttribute(SHIB_ATTR_NAME_EMAIL));
    	}
    	
    	//saml.attributes.surname=urn:mace:dir:attribute-def:sn
    	if (request.getAttribute(SHIB_ATTR_NAME_SURNAME) != null) {
    		shibAtt.put(ATTR_NAME_SURNAME, (String)request.getAttribute(SHIB_ATTR_NAME_SURNAME));
    	}
    	
    	//saml.attributes.prefix=snPrefix
    	//NOTE: Dit attribuut wordt niet aangeleverd door SURFnet; er is een verstekwaarde ingevuld
    	if (request.getAttribute(SHIB_ATTR_NAME_PREFIX) != null) {
    		shibAtt.put(SHIB_ATTR_NAME_PREFIX, "shib");
    	} 
    	
    	//saml.attributes.givenname=urn:mace:dir:attribute-def:givenName
    	if (request.getAttribute(SHIB_ATTR_NAME_GIVENNAME) != null) {
    		shibAtt.put(ATTR_NAME_GIVENNAME, (String)request.getAttribute(SHIB_ATTR_NAME_GIVENNAME));
    	}
    	
    	//saml.attributes.organization=urn:mace:terena.org:attribute-def:schacHomeOrganization
    	if (request.getAttribute(SHIB_ATTR_NAME_ORG) != null) {
    		shibAtt.put(ATTR_NAME_ORG, (String)request.getAttribute(SHIB_ATTR_NAME_ORG));
    	}
    	
    	//NOTE: role: de rol bij de organisatie. De SURFfederatie kent de waarden student, employee, staff, alum en affiliate. 
    	// Dit attribuut is meerwaardig.
    	//attribute role
    	//saml.attributes.role=urn:mace:dir:attribute-def:eduPersonAffiliation
    	if (request.getAttribute(SHIB_ATTR_NAME_ROLE) != null) {
    		shibAtt.put(ATTR_NAME_ROLE, (String)request.getAttribute(SHIB_ATTR_NAME_ROLE));
    	}

    	//saml.attributes.principal=urn:mace:dir:attribute-def:eduPersonPrincipalName
    	if (request.getAttribute(SHIB_ATTR_NAME_PRINCIPAL) != null) {
    		shibAtt.put(ATTR_NAME_PRINCIPAL, (String)request.getAttribute(SHIB_ATTR_NAME_PRINCIPAL));
    	}
    	
    	//TODO: Shouldn't be here. Very ugly coding
    	
    	/* Role access */
    	//saml.access.admin=  
    	ACL_ADMIN = "";
    	
    	//saml.access.creator=email=*&role=employee&organization=uu.nl|email=*&role=employee&organization=umcutrecht.nl|email=*&role=employee&organization=maastrichtuniversity.nl|email=*&role=employee&organization=utwente.nl|email=*&role=employee&organization=rsm.nl|email=*&role=employee&organization=nioo.knaw.nl|
    	ACL_CREATOR = "email=*&role=student&organization=diy.surfconext.nl|email=*&role=employee&organization=uu.nl|email=*&role=employee&organization=umcutrecht.nl|email=*&role=employee&organization=maastrichtuniversity.nl|email=*&role=employee&organization=utwente.nl|email=*&role=employee&organization=rsm.nl|email=*&role=employee&organization=nioo.knaw.nl|";
    	
    	//saml.access.user=email=*
    	ACL_USER = "email=*";
    	
    	//saml.username.method=attr
    	USERID_METHOD = "attr";
    	
    	//saml.username.attribute=email
    	USERID_ATTR = "email";
    	
    	//saml.username.prefix=
    	USERID_PREFIX = "";
    	
    	//# saml.redirect.referer=yes 
    	//NOTE: saml.redirect.referer=yes is unused (#).
    	USE_REFERER = false;
    	
    	/* Admin access */
    	//saml.access.allow.admin=no
    	ALLOW_ADMIN = false;
    	
    	return shibAtt;
    }
    
}
