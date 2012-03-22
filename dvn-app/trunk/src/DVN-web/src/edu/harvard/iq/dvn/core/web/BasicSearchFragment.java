/*
   Copyright (C) 2005-2012, by the President and Fellows of Harvard College.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

   Dataverse Network - A web application to share, preserve and analyze research data.
   Developed at the Institute for Quantitative Social Science, Harvard University.
   Version 3.0.
*/
package edu.harvard.iq.dvn.core.web;

import edu.harvard.iq.dvn.core.index.IndexServiceLocal;
import edu.harvard.iq.dvn.core.index.SearchTerm;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.study.StudyVersion;
import edu.harvard.iq.dvn.core.study.VariableServiceLocal;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import edu.harvard.iq.dvn.core.vdc.VDCCollectionServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDCServiceLocal;
import javax.faces.bean.ViewScoped;
import javax.inject.Named;


@Named ("BasicSearchFragment")
@ViewScoped
public class BasicSearchFragment extends VDCBaseBean implements java.io.Serializable {
    @EJB
    IndexServiceLocal      indexService;
    @EJB
    VariableServiceLocal varService;
    @EJB
    StudyServiceLocal studyService;
    @EJB
    VDCServiceLocal vdcService;
    @EJB
    VDCCollectionServiceLocal vdcCollectionService;
    private String searchValue = "Search Studies";
    private String searchField;

    public String search_action() {
        searchField = (searchField == null) ? "any" : searchField; // default searchField, in case no dropdown

        List searchTerms    = new ArrayList();
        SearchTerm st       = new SearchTerm();
        st.setFieldName( searchField );
        st.setValue( searchValue );
        searchTerms.add(st);
        List studies        = new ArrayList();
        Map variableMap     = new HashMap();
        Map versionMap = new HashMap();
        List displayVersionList = new ArrayList();

        if ( searchField.equals("variable") ) {
            List variables  = indexService.searchVariables(getVDCRequestBean().getCurrentVDC(), st);
            varService.determineStudiesFromVariables(variables, studies, variableMap);

        } else {
            studies         = indexService.search(getVDCRequestBean().getCurrentVDC(), searchTerms);
        }
        if (searchField.equals("any")) {
            List<Long> versionIds = indexService.searchVersionUnf(getVDCRequestBean().getCurrentVDC(), searchValue);
            Iterator iter = versionIds.iterator();
            Long studyId = null;
            while (iter.hasNext()) {
                Long vId = (Long) iter.next();
                StudyVersion sv = null;
                try {
                    sv = studyService.getStudyVersionById(vId);
                    studyId = sv.getStudy().getId();
                    List<StudyVersion> svList = (List<StudyVersion>) versionMap.get(studyId);
                    if (svList == null) {
                        svList = new ArrayList<StudyVersion>();
                    }
                    svList.add(sv);
                    if (!studies.contains(studyId)) {
                        displayVersionList.add(studyId);
                        studies.add(studyId);
                    }
                    versionMap.put(studyId, svList);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }

        }


        StudyListing sl = new StudyListing(StudyListing.SEARCH);
        sl.setVdcId(getVDCRequestBean().getCurrentVDCId());
        sl.setStudyIds(studies);
        sl.setSearchTerms(searchTerms);
        sl.setVariableMap(variableMap);
        sl.setVersionMap(versionMap);
        sl.setDisplayStudyVersionsList(displayVersionList);
        
        //getVDCRequestBean().setStudyListing(sl);
        String studyListingIndex = StudyListing.addToStudyListingMap(sl, getSessionMap());
        return "/StudyListingPage.xhtml?faces-redirect=true&studyListingIndex=" + studyListingIndex + "&vdcId=" + getVDCRequestBean().getCurrentVDCId();
    }

 

 public void setSearchField(String searchField) {
        this.searchField = searchField;
    }

    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }

    public String getSearchField() {
        return searchField;
    }

    public String getSearchValue() {
        return searchValue;
    }

}
