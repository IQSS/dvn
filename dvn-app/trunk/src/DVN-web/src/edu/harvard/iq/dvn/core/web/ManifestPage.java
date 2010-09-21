/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.web;

import edu.harvard.iq.dvn.core.util.PropertyUtil;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;



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
public class ManifestPage extends VDCBaseBean implements java.io.Serializable {

    private String dvName;
    private String archivalUnit;
    private String oaiSetUrl;
    private String lockssLicensingTerms;
    private String dvNetworkTermsOfUse;
    private String dvTermsOfUse;
    private String baseUrl;
    private String lockssImageUrl;
    private String lockssLicenseDescription;


    @Override
    public void init() {
        super.init();
        dvName = getVDCRequestBean().getCurrentVDC().getName();
        archivalUnit = getVDCRequestBean().getCurrentVDC().getAlias();
        initOaiSetUrl();

        dvTermsOfUse = getVDCRequestBean().getCurrentVDC().getDownloadTermsOfUse();
        dvNetworkTermsOfUse = getVDCRequestBean().getVdcNetwork().getDownloadTermsOfUse();
        lockssLicensingTerms = getVDCRequestBean().getCurrentVDC().getLockssConfig().getLicenseType().getLicenseUrl();
        lockssImageUrl = getVDCRequestBean().getCurrentVDC().getLockssConfig().getLicenseType().getImageUrl();
        lockssLicenseDescription = getVDCRequestBean().getCurrentVDC().getLockssConfig().getLicenseType().getName();
    }

    public void initOaiSetUrl(){

        oaiSetUrl  = "http://" + PropertyUtil.getHostUrl() + "/dvn/OAIHandler?verb=ListRecords&metadataPrefix=ddi&set=" + archivalUnit ;
    }

    public String getArchivalUnit() {
        return archivalUnit;
    }

    public void setArchivalUnit(String archivalUnit) {
        this.archivalUnit = archivalUnit;
    }

    public String getDvName() {
        return dvName;
    }

    public void setDvName(String dvName) {
        this.dvName = dvName;
    }

    public String getDvNetworkTermsOfUse() {
        return dvNetworkTermsOfUse;
    }

    public void setDvNetworkTermsOfUse(String dvNetworkTermsOfUse) {
        this.dvNetworkTermsOfUse = dvNetworkTermsOfUse;
    }

    public String getDvTermsOfUse() {
        return dvTermsOfUse;
    }

    public void setDvTermsOfUse(String dvTermsOfUse) {
        this.dvTermsOfUse = dvTermsOfUse;
    }

    public String getLockssLicensingTerms() {
        return lockssLicensingTerms;
    }

    public void setLockssLicensingTerms(String lockssLicensingTerms) {
        this.lockssLicensingTerms = lockssLicensingTerms;
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



}
