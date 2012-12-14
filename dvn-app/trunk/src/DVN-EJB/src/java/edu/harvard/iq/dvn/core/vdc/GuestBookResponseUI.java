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
   Version 3.1.
*/
package edu.harvard.iq.dvn.core.vdc;

import java.util.ArrayList;
import java.util.List;
import javax.naming.InitialContext;

/**
 *
 * @author skraffmiller
 */
public class GuestBookResponseUI {
    private GuestBookResponseServiceBean gbrServiceBean;
    private Long id;
    private GuestBookResponse guestBookResponse;
    private List <String> customQuestionResponses = new ArrayList();
    private List <Long> customQuestionIds = new ArrayList();
    
    public GuestBookResponseUI(Long id, List<Long> cqIds){
        this.id = id;
        this.customQuestionIds = cqIds;
    }
    
    public List<String> getCustomQuestionResponses() {
        return customQuestionResponses;
    }

    public void setCustomQuestionResponses(List<String> customQuestionResponses) {
        this.customQuestionResponses = customQuestionResponses;
    }

    public GuestBookResponse getGuestBookResponse() {
        if (this.guestBookResponse !=null){
            return guestBookResponse;
        } else {
            return this.guestBookResponse = initGuestBookResponse(id);
        }        
    }
    
    public void setGuestBookResponse(GuestBookResponse guestBookResponse) {
        this.guestBookResponse = guestBookResponse;
    }
    
    
    private void initGuestBookResponseService() {
        if (gbrServiceBean == null) {
            try {
                gbrServiceBean = (GuestBookResponseServiceBean) new InitialContext().lookup("java:comp/env/guestBookResponseService");
            } catch (Exception e) {
               
                e.printStackTrace();
            }
        }
    }
    
    private GuestBookResponse initGuestBookResponse(long id) {
        if (gbrServiceBean == null) {
            initGuestBookResponseService();
        }        
        GuestBookResponse gbr = gbrServiceBean.findById(id);
        if (gbr.getCustomQuestionResponses() !=null && gbr.getCustomQuestionResponses().size() > 0) {
            List<String> customQuestionResponseStrings = new ArrayList(customQuestionIds.size());
            for (int i = 0; i < gbr.getCustomQuestionResponses().size(); i++) {
                customQuestionResponseStrings.add(i, "");
            }
            if (!gbr.getCustomQuestionResponses().isEmpty()) {
                for (Long qid : customQuestionIds) {
                    int index = customQuestionIds.indexOf(qid);
                    for (CustomQuestionResponse cqr : gbr.getCustomQuestionResponses()) {
                        if (cqr.getCustomQuestion().getId().equals(qid)) {
                            customQuestionResponseStrings.set(index, cqr.getResponse());
                        }
                    }
                }
            }
            this.setCustomQuestionResponses(customQuestionResponseStrings);
        }
        return gbr;
    }
}
