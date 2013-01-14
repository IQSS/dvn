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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.study;

import java.util.ArrayList;
import javax.persistence.Entity;

/**
 *
 * @author gdurand
 */

@Entity
public class NetworkDataFile extends StudyFile  {


    public NetworkDataFile() {
    }

    public NetworkDataFile(Study study) {
        super(study);
    }


    public boolean isSubsettable() {
        return true;
    }

    public boolean isUNFable() {
        return false;
    }

    public DataTable getVertexDataTable() {
        for (DataTable dt : getDataTables()) {
            if (DataTable.TYPE_VERTEX.equals(dt.getType()) ) {
                return dt;
            }
        }

        return null;
    }

    public void setVertexDataTable(DataTable dt) {
        if (this.getDataTables() == null) {
            this.setDataTables( new ArrayList() );
        } else {
            DataTable vdt = getVertexDataTable();
            if (vdt != null) {
                this.getDataTables().remove(vdt);
            }
        }

        dt.setType(DataTable.TYPE_VERTEX);
        this.getDataTables().add(dt);
    }

    public DataTable getEdgeDataTable() {
        for (DataTable dt : getDataTables()) {
            if (DataTable.TYPE_EDGE.equals(dt.getType()) ) {
                return dt;
            }
        }

        return null;
    }

    public void setEdgeDataTable(DataTable dt) {
        if (this.getDataTables() == null) {
            this.setDataTables( new ArrayList() );
        } else {
            DataTable edt = getEdgeDataTable();
            if (edt != null) {
                this.getDataTables().remove(edt);
            }
        }

        dt.setType(DataTable.TYPE_EDGE);
        this.getDataTables().add(dt);
    }


}
