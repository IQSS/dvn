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
 * CensusRepositoryServlet.java
 *
 * Created on November 17, 2007, 2:23 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.web.servlet;


import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import java.nio.ByteBuffer; 
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.sql.DataSource;

/**
 *
 * @author leonid
 * @version 
 */
public class CensusRepositoryServlet extends HttpServlet{
    
    /** Creates a new instance of CensusRepositoryServlet */
    public CensusRepositoryServlet() {
    }

    @Resource(name="jdbc/VDCNetDS") DataSource dvnDatasource;
  
    public void service(HttpServletRequest req, HttpServletResponse res)  {

	// no parameters supported,
	// for now
	

	Connection sqlConn = null;	 
	PreparedStatement sth = null; 
	boolean[] categoryMap = null; 
	ResultSet dbIds = null; 


	// attempt to get a list of all local, publicly-available, 
	// subsettable datafiles from the database.
	//
	// on SQL failures, print a clear error page and send a
	// standard "Service Temporarily Unavailable" status code. 



	try {
	    // Here's what I'm doing with the SQL searches: 
	    //
	    // A datafile can be restricted in more ways than one; 
	    // it can have its own "restricted" flag set, OR it can 
	    // be part of a restricted study, which in turn can be part 
	    // of a restricted Dataverse. 
	    // So, if we need to do this on a single datafile, we first 
	    // retreive the file's studyfile entry and check the restricted 
	    // flag; then we retreive the filecategory to which the file 
	    // then the study to which the filecategory belongs -- and now
	    // we can check the study's own restriction flag. Then we find 
	    // the owner Dataverse for the study, and check if that is 
	    // restricted. However, now that we need to retreive ALL the
	    // unrestricted datafiles, we cannot possibly do it this way 
	    // -- it would take forever to run 4 SQL queries for every 
	    // matching datafile we find in the studyfile table!
	    // So this is how I'm doing it: I first find all unrestricted 
	    // dataverses; then I find all unrestricted studies, with the
	    // corresponding dataverse ids and filter the list against the list
	    // of unrestricted dataversese from step 1; now I have a list (map)
	    // of studies that are unrestricted AND are not part of a 
	    // restricted dataverse; then I generate a list of all 
	    // filecategories in these studies (file categories cannot be 
	    // restricted!). And now I can run the final search for all 
	    // unrestricted subsettable datafiles and filter the list against
	    // the unrestricted filecategoris from the previous step. 
	    // There's only 4 SQL queries total; the drawback of this approach
	    // is that I have to carry around these bit maps of higher-level
	    // objects that are not restricted. However, these are only 
	    // arrays of boolean values, so they should be manageable even if 
	    // our archive grows significantly.
	    

	    String sqlCmd= "SELECT id,filecategory_id from studyfile WHERE restricted = false AND subsettable = true AND NOT (filesystemlocation LIKE 'http%')"; 
	    sqlConn = dvnDatasource.getConnection();	 
	    sth = sqlConn.prepareStatement(sqlCmd);
	    dbIds = sth.executeQuery();

	    categoryMap = generateCategoryMap(); 

	} catch(SQLException e) {
	    createErrorResponseGeneric(res, res.SC_SERVICE_UNAVAILABLE, 
				       "Database services temporarily unavailable: " + 
				       "Could not execute SQL statement" ); 
	    e.printStackTrace();
	    return; 
	}

	// 
	// iterate through the list we received in response to the database
	// query and print a list of FileDownload URLs.
	// catch possible exceptions. 

	String fileId = null; 
	String hostHttpPrefix = null; 
	String lineOut = null; 

	PrintWriter out = null; 

	if ( req.getServerPort() != 80 ) {	   
	    hostHttpPrefix = req.getScheme() +"://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath();
	} else {
	    hostHttpPrefix = req.getScheme() +"://" + req.getServerName() + req.getContextPath();
	}

	int categoryId = 0; 

	try {
	    res.setHeader ( "Content-Type", "text/plain;charset=ISO-8859-1" ); 
	    //OutputStream out = res.getOutputStream();
	    out = res.getWriter();

	    out.println ("available unrestricted subsettable datafiles:\n"); 


	    try {
		while (dbIds.next()) {

		    categoryId = dbIds.getInt(2); 

		    if ( categoryMap[categoryId] ) {
			fileId = dbIds.getString(1);  

			lineOut = hostHttpPrefix + "/FileDownload/?fileId=" + fileId; 
			out.println (lineOut); 
		    }
		}
		sqlConn.close(); 
	    } catch (SQLException e) {
		createErrorResponseGeneric(res, res.SC_SERVICE_UNAVAILABLE, 
					   "Database services temporarily unavailable: " + 
					   "Could not retreive data from SQL Database" ); 
		e.printStackTrace();
	    }

	    out.close(); 
	    
	} catch (IOException ex) {
	    if ( out != null ) {
		out.close(); 
	    }
		    
	    ex.printStackTrace();
	}

	// the end.

    }

    private void createErrorResponseGeneric(HttpServletResponse res, int status, String statusLine) {
        res.setContentType("text/html");
	res.setStatus ( status ); 

        try {
            PrintWriter out = res.getWriter();
            out.println("<HTML>");
            out.println("<HEAD><TITLE>Census Servlet</TITLE></HEAD>");
            out.println("<BODY>");
            out.println("<BIG>" + statusLine + "</BIG>");
            
            if (status == res.SC_NOT_FOUND) {
                for (int i = 0; i < 10; i++) {
                    out.println("<!-- This line is filler to handle IE case for 404 errors   -->");
                }
            }
            
            out.println("</BODY></HTML>");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    

    private boolean[] generateCategoryMap () throws SQLException {
	boolean[] categoryMap = new boolean[128*1024]; 
	boolean[] studyMap = generateStudyMap(); 

	int catId = 0; 
	int studyId = 0; 

	int i = 0; 

	String sqlCmd= "SELECT id,study_id from filecategory ORDER BY id"; 

	Connection sqlConn = dvnDatasource.getConnection();	 
	PreparedStatement sth = sqlConn.prepareStatement(sqlCmd);
	
	ResultSet rs = sth.executeQuery();

	while (rs.next()) {
	    catId = rs.getInt(1);  
	    studyId = rs.getInt(2);  

	    while ( i < catId ) {
		categoryMap[i++] = false; 
	    }

	    if ( studyMap[studyId] ) {
		categoryMap[catId] = true;
	    } else { 
		categoryMap[catId] = false; 
	    }

	    i++; 	    
	}

	sqlConn.close(); 	 
	return categoryMap; 
    }

    private boolean[] generateStudyMap () throws SQLException {
	boolean[] studyMap = new boolean[32*1024]; 
	boolean[] dvMap = generateDataverseMap(); 

	int studyId = 0; 
	int dvId = 0; 

	int i = 0; 

	String sqlCmd= "SELECT id,owner_id from study WHERE restricted = false ORDER BY id"; 
  
	Connection sqlConn = dvnDatasource.getConnection();	 
	PreparedStatement sth = sqlConn.prepareStatement(sqlCmd);
	
	ResultSet rs = sth.executeQuery();

	while (rs.next()) {
	    studyId = rs.getInt(1);  
	    dvId = rs.getInt(2);  

	    while ( i < studyId ) {
		studyMap[i++] = false; 
	    }

	    if ( dvMap[dvId] ) {
		studyMap[studyId] = true;
	    } else { 
		studyMap[studyId] = false; 
	    }

	    i++;
	}

	sqlConn.close(); 	 
	return studyMap; 
    }


    private boolean[] generateDataverseMap () throws SQLException {
	boolean[] dvMap = new boolean[256]; 
	int dvId = 0; 
	int i = 0; 

	String sqlCmd= "SELECT id from vdc WHERE restricted = false ORDER BY id"; 
  
	Connection sqlConn = dvnDatasource.getConnection();	 
	PreparedStatement sth = sqlConn.prepareStatement(sqlCmd);
	
	ResultSet rs = sth.executeQuery();

	while (rs.next()) {
	    dvId = rs.getInt(1);  
	    while ( i < dvId ) {
		dvMap[i++] = false; 
	    }
	    dvMap[dvId] = true;
	    i++; 
	}
	 
	sqlConn.close(); 
	return dvMap; 
    }

   
}

