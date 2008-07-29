/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.dsb.impl;

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
                }
            }
        }
    }


}
