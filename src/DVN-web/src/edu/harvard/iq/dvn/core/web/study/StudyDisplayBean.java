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
 * StudyDisplayBean.java
 *
 * Created on September 21, 2006, 5:32 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.web.study;

import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyAbstract;
import edu.harvard.iq.dvn.core.study.StudyAuthor;
import edu.harvard.iq.dvn.core.study.StudyDistributor;
import edu.harvard.iq.dvn.core.study.StudyGrant;
import edu.harvard.iq.dvn.core.study.StudyKeyword;
import edu.harvard.iq.dvn.core.study.StudyNote;
import edu.harvard.iq.dvn.core.study.StudyProducer;
import edu.harvard.iq.dvn.core.study.StudySoftware;
import edu.harvard.iq.dvn.core.study.StudyTopicClass;
import java.util.Iterator;

/**
 *
 * @author Ellen Kraffmiller
 */
public class StudyDisplayBean implements java.io.Serializable  {
    
    /** Creates a new instance of StudyDisplayBean */
    public StudyDisplayBean() {
    }
   public StudyDisplayBean(Study study) {
       this.study=study;
    }

    /**
     * Holds value of property study.
     */
    private Study study;

    /**
     * Getter for property study.
     * @return Value of property study.
     */
    public Study getStudy() {
        return this.study;
    }

    /**
     * Setter for property study.
     * @param study New value of property study.
     */
    public void setStudy(Study study) {
        this.study = study;
    }
    
    public String getCitationDate() {
        String str = "";
        if (study.getProductionDate()!=null) {
            str=study.getProductionDate();
        }
        return str;
    }
    /**
     * Return for each studyAuthor: Author (Affiliation), only if affiliation is not empty
     */
    public String getAuthors() {
        String str="";
        for (Iterator<StudyAuthor> it = study.getStudyAuthors().iterator(); it.hasNext();) {
            StudyAuthor sa =  it.next();
            str += sa.getName();
            if (sa.getAffiliation()!=null) {
                str+=" ("+sa.getAffiliation()+")";
            }
            if (it.hasNext()) {
                str+="; ";
            }
        }
        return str;
        
    }
    
    public String getSoftware() {
        String str="";
        for (Iterator<StudySoftware> it = study.getStudySoftware().iterator(); it.hasNext();) {
            StudySoftware ss =  it.next();
            str+= ss.getName();
            if (ss.getSoftwareVersion()!=null) {
                str+=", "+ss.getSoftwareVersion();
            }
            if (it.hasNext()) {
                str+="; ";
            }
        }
        return str;
    }
    
    public String getGrants() {
        String str="";
        for (Iterator<StudyGrant> it = study.getStudyGrants().iterator(); it.hasNext();) {
            StudyGrant elem =  it.next();
            if (elem.getNumber()!=null) {
                str+=elem.getNumber();
                if (elem.getAgency()!=null) {
                    str+=",";
                }
            }
            if (elem.getAgency()!=null){
                str+=elem.getAgency();
            }
             if (it.hasNext()) {
                str+="; ";
            }
            
        }
        return str;
    }
    
    public String getDistributors() {
        String str = new String("");
        for (Iterator<StudyDistributor> it = study.getStudyDistributors().iterator(); it.hasNext();) {
            StudyDistributor elem = it.next();
            if (elem.getName()!=null) {
                if (elem.getUrl()!=null) {
                    str +="<a href='"+elem.getUrl()+" '> "+elem.getName()+"</a>";
                } else {
                    str+=elem.getName();
                }
                
            }
            if (elem.getAbbreviation()!=null) {
                str+=" ("+elem.getAbbreviation()+")";
            }
            if (elem.getLogo()!=null) {
                //TODO: add href
                str+=", "+elem.getLogo();
            }
           
            if (it.hasNext()) {
                str+="; ";
            }
            
        }
        return str;
    
    }
    
       public String getProducers() { 
        String str = new String("");
        for (Iterator<StudyProducer> it = study.getStudyProducers().iterator(); it.hasNext();) {
            StudyProducer elem = it.next();
            if (elem.getName()!=null) {
                if (elem.getUrl()!=null) {
                    str +="<a href='"+elem.getUrl()+" '> "+elem.getName()+"</a>";
                } else {
                    str+=elem.getName();
                }
                
            }
            if (elem.getAbbreviation()!=null) {
                str+=" ("+elem.getAbbreviation()+")";
            }
            if (elem.getLogo()!=null) {
                //TODO: add href
                str+=", "+elem.getLogo();
            }
            if (elem.getAffiliation()!=null) {
                str+=", "+elem.getAffiliation();
            }
            if (it.hasNext()) {
                str+="; ";
            }
            
        }
        return str;
    
    }
    
    public String getKeywords() {
        String str = "";
        for (Iterator<StudyKeyword> it = study.getStudyKeywords().iterator(); it.hasNext();) {
            StudyKeyword elem =  it.next();
            if (elem.getValue()!=null) {
                str+=elem.getValue();
            }
            if (elem.getVocab()!=null) {
                if (elem.getVocabURI()!=null) {
                   str +="<a href='"+elem.getVocabURI()+" '> "+elem.getValue()+"</a>";
                } else {
                    str+=elem.getValue();
                }
            }
            if (it.hasNext()) {
                str+="; ";
            }
        }
    
        return str;
    }
    
      public String getTopicClasses() {
        String str = "";
        for (Iterator<StudyTopicClass> it = study.getStudyTopicClasses().iterator(); it.hasNext();) {
            StudyTopicClass elem =  it.next();
            if (elem.getValue()!=null) {
                str+=elem.getValue();
            }
            if (elem.getVocab()!=null) {
                if (elem.getVocabURI()!=null) {
                   str +="<a href='"+elem.getVocabURI()+" '> "+elem.getValue()+"</a>";
                } else {
                    str+=elem.getValue();
                }
            }
            if (it.hasNext()) {
                str+="; ";
            }
        }
    
        return str;
    }
      
    public String getAbstracts() {
        String str="";
        for (Iterator<StudyAbstract> it = study.getStudyAbstracts().iterator(); it.hasNext();) {
            StudyAbstract elem =  it.next();
            str+="<p>"+elem.getText()+"</p>";
          
        }
        return str;
    }
    
    public String getAbstractDates() {
        String str="";
        for (Iterator<StudyAbstract> it = study.getStudyAbstracts().iterator(); it.hasNext();) {
            StudyAbstract elem =  it.next();
            str+=elem.getDate();
             if (it.hasNext()) {
                str+="; ";
             }
          
        }
        return str;
    
    }
    
    public String getNotes() {
        String str="";
        for (Iterator<StudyNote> it = study.getStudyNotes().iterator(); it.hasNext();) {
            StudyNote elem = it.next();
            if (elem.getType()!=null) {
                str+=elem.getType();
            }
            if (elem.getSubject()!=null) {
                str+=" ("+elem.getSubject()+")";
            }
            if (elem.getText()!=null) {
                str+=" "+elem.getText();
            }
            if (it.hasNext()) {
                str+="; ";
            }
            
        }
        return str;
    }
    
    
    
    
}
