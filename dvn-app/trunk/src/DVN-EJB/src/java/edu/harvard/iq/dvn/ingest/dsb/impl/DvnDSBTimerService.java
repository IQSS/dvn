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
package edu.harvard.iq.dvn.ingest.dsb.impl;

import org.apache.commons.lang.*;
import java.util.*;
import java.util.logging.*;
import java.io.*;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;

/**
 *
 * @author asone
 */
@Stateless
public class DvnDSBTimerService implements DvnDSBTimerServiceLocal{

    /** logger setting(use the package name) */
    private static Logger dbgLog = Logger.getLogger(DvnDSBTimerService.class.getPackage().getName());

    @Resource TimerService ts;
    
    private List<File> deleteTempFileList;
    
    
    public void createTimer(List<File> lst, long minute){
        deleteTempFileList = lst;
        System.out.println("ts: "+ts);
        Timer timer = ts.createTimer(minute*60*1000, null);
    }


    @Timeout
    public void handletimeout(Timer timer){
        // delete temp files

        for (File f : deleteTempFileList){
            dbgLog.fine("file to be deleted: path="+f.getAbsolutePath() +"\tname="+ f.getName());
            if(f.exists()){
                boolean sc = f.delete();
                if (!sc){
                    dbgLog.fine("failed to delete file: path="+f.getAbsolutePath() +"\tname="+ f.getName());
                } else{
                    dbgLog.fine("successfully deleted? let's check its existence");
                    if(f.exists()){
                        dbgLog.fine("surprise: actually the File still exists");
                    } else {
                        dbgLog.fine("The file no longer exists");
                    }
                }
            }
        }
    }


}
