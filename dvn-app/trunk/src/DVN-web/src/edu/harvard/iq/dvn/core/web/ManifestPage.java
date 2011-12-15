/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.web;

import edu.harvard.iq.dvn.core.util.PropertyUtil;
import edu.harvard.iq.dvn.core.vdc.LockssConfig;
import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCNetwork;
import edu.harvard.iq.dvn.core.vdc.VDCNetworkServiceLocal;
import edu.harvard.iq.dvn.core.admin.LockssAuthServiceLocal;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.ejb.EJB;
import javax.faces.bean.ViewScoped;
import javax.inject.Named;



/**
 * <p>Page bean that corresponds to a similarly named JSP page.  This
 * class contains component definitions (and initialization code) for
 * all components that you have defined on this page, as well as
 * lifecycle methods and event handlers where you may add behavior
 * to respond to incoming events.</p>
 */

/**
 *
 * @author skraffmiller
 */
@ViewScoped
@Named("ManifestPage")
public class ManifestPage extends VDCBaseBean implements java.io.Serializable {

    private String dvName;
    private String archivalUnit;
    private String oaiSetUrl;


    private String lockssLicensingUrl;
    private String lockssLicensingTerms;

    private String dvNetworkTermsOfUse;
    private String dvTermsOfUse;
    private String baseUrl;
    private String lockssImageUrl;
    private String lockssLicenseDescription;
    private String ownerString;
    private String basedOnWorkLink;
    private Boolean displayPage = false;
    private Boolean isNetwork = false;
    private Boolean isDataverse = false;
    private String basedOnWorkText;
    private Boolean isRestrictedAuthorized = false;



    private VDCNetwork vdcNetwork;
    private VDC vdc;
    private LockssConfig lockssConfig;
    @EJB VDCNetworkServiceLocal vdcNetworkService;
    @EJB LockssAuthServiceLocal lockssAuthService;


    @Override
    public void init() {
        super.init();
        vdc = getVDCRequestBean().getCurrentVDC();
        vdcNetwork = getVDCRequestBean().getVdcNetwork();

        if (vdc != null){
            isDataverse = true;
            dvName = vdc.getName();
            lockssConfig = vdc.getLockssConfig();
            if (lockssConfig !=null) {
                displayPage = true;
                archivalUnit = vdc.getAlias();
                oaiSetUrl  = "http://" + PropertyUtil.getTimerServerHost() + "/dvn/OAIHandler?verb=ListRecords&metadataPrefix=ddi&set=" + lockssConfig.getOaiSet().getSpec() ;
                ownerString = vdc.getCreator().getFirstName() + " " + vdc.getCreator().getLastName();

                if (vdc.isDownloadTermsOfUseEnabled() == true)
                    dvTermsOfUse = vdc.getDownloadTermsOfUse();

                if (vdcNetwork.isDownloadTermsOfUseEnabled() == true)
                    dvNetworkTermsOfUse = vdcNetwork.getDownloadTermsOfUse();

                basedOnWorkLink = "/dvn" + getVDCRequestBean().getCurrentVDCURL();
                basedOnWorkText = vdc.getName();

                lockssLicensingUrl = lockssConfig.getLicenseType().getLicenseUrl();
                lockssLicensingTerms = lockssConfig.getLicenseText();
                lockssImageUrl = lockssConfig.getLicenseType().getImageUrl();
                lockssLicenseDescription = lockssConfig.getLicenseType().getName();
            }
        }
        else{
            isNetwork = true;
            dvName = vdcNetwork.getName();
            lockssConfig = vdcNetworkService.getLockssConfig();
            if (lockssConfig !=null) {
                displayPage = true;
                if (vdcNetworkService.getLockssConfig().getOaiSet() != null){
                    archivalUnit = vdcNetworkService.getLockssConfig().getOaiSet().getName();
                    oaiSetUrl  = "http://" + PropertyUtil.getTimerServerHost() + "/dvn/OAIHandler?verb=ListRecords&metadataPrefix=ddi&set=" + lockssConfig.getOaiSet().getSpec() ;
                } else {
                    archivalUnit = dvName;
                    oaiSetUrl  = "http://" + PropertyUtil.getTimerServerHost() + "/dvn/OAIHandler?verb=ListRecords&metadataPrefix=ddi";
                }
                ownerString = vdcNetwork.getDefaultNetworkAdmin().getFirstName() + " " + vdcNetwork.getDefaultNetworkAdmin().getLastName();
                basedOnWorkLink =  getVDCRequestBean().getCurrentVDCURL();
                basedOnWorkText = vdcNetwork.getName();

                if (vdcNetwork.isDownloadTermsOfUseEnabled() == true)
                    dvNetworkTermsOfUse = vdcNetwork.getDownloadTermsOfUse();
                
                lockssLicensingUrl =   lockssConfig.getLicenseType().getLicenseUrl();
                lockssLicensingTerms =   lockssConfig.getLicenseText();
                lockssImageUrl = lockssConfig.getLicenseType().getImageUrl();
                lockssLicenseDescription = lockssConfig.getLicenseType().getName();
            }
        }
        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        setIsRestrictedAuthorized(lockssAuthService.isAuthorizedRestrictedFiles(vdc, request ));
    }

    public String getArchivalUnit() {
        return archivalUnit;
    }

    public String getDvName() {
        return dvName;
    }

    public String getDvNetworkTermsOfUse() {

        return dvNetworkTermsOfUse;
    }


    public String getDvTermsOfUse() {
        /*
        if (dvTermsOfUse.trim().length() == 0){
            return null;
        }*/
        return dvTermsOfUse;
    }

    public String getLockssLicensingTerms() {
        return lockssLicensingTerms;
    }

    public String getOaiSetUrl() {
        return oaiSetUrl;
    }

    public void setOaiSetUrl(String oaiSetUrl) {
        this.oaiSetUrl = oaiSetUrl;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getLockssImageUrl() {
        return lockssImageUrl;
    }

    public void setLockssImageUrl(String lockssImageUrl) {
        this.lockssImageUrl = lockssImageUrl;
    }

    public String getLockssLicenseDescription() {
        return lockssLicenseDescription;
    }

    public void setLockssLicenseDescription(String lockssLicenseDescription) {
        this.lockssLicenseDescription = lockssLicenseDescription;
    }

    public String getOwnerString() {
        return ownerString;
    }
    
    public void setOwnerString(String ownerString) {
        this.ownerString = ownerString;
    }

    public String getBasedOnWorkLink() {
        return basedOnWorkLink;
    }

    public String getBasedOnWorkText() {
        return basedOnWorkText;
    }

    public String getLockssLicensingUrl() {
        return lockssLicensingUrl;
    }
    public Boolean getDisplayPage() {
        return displayPage;
    }

    public Boolean getIsDataverse() {
        return isDataverse;
    }

    public Boolean getIsNetwork() {
        return isNetwork;
    }

    public Boolean getIsRestrictedAuthorized() {
        return isRestrictedAuthorized;
    }

    public void setArchivalUnit(String archivalUnit) {
        this.archivalUnit = archivalUnit;
    }

    public void setBasedOnWorkLink(String basedOnWorkLink) {
        this.basedOnWorkLink = basedOnWorkLink;
    }

    public void setBasedOnWorkText(String basedOnWorkText) {
        this.basedOnWorkText = basedOnWorkText;
    }

    public void setDisplayPage(Boolean displayPage) {
        this.displayPage = displayPage;
    }

    public void setDvName(String dvName) {
        this.dvName = dvName;
    }

    public void setDvNetworkTermsOfUse(String dvNetworkTermsOfUse) {
        this.dvNetworkTermsOfUse = dvNetworkTermsOfUse;
    }

    public void setDvTermsOfUse(String dvTermsOfUse) {
        this.dvTermsOfUse = dvTermsOfUse;
    }

    public void setIsDataverse(Boolean isDataverse) {
        this.isDataverse = isDataverse;
    }

    public void setIsNetwork(Boolean isNetwork) {
        this.isNetwork = isNetwork;
    }

    public void setLockssLicensingTerms(String lockssLicensingTerms) {
        this.lockssLicensingTerms = lockssLicensingTerms;
    }

    public void setLockssLicensingUrl(String lockssLicensingUrl) {
        this.lockssLicensingUrl = lockssLicensingUrl;
    }
    public void setLockssConfig(LockssConfig lockssConfig) {
        this.lockssConfig = lockssConfig;
    }

    public void setVdc(VDC vdc) {
        this.vdc = vdc;
    }

    public void setVdcNetwork(VDCNetwork vdcNetwork) {
        this.vdcNetwork = vdcNetwork;
    }

    public void setIsRestrictedAuthorized(Boolean ra) {
        isRestrictedAuthorized = ra;
    }
}
