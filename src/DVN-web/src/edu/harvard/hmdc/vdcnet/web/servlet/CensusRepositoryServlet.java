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

package edu.harvard.hmdc.vdcnet.web.servlet;


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
	

	ResultSet dbIds = null; 

	// attempt to get a list of all local, publicly-available, 
	// subsettable datafiles from the database.
	//
	// on SQL failures, print a clear error page and send a
	// standard "Service Temporarily Unavailable" status code. 

	try {
	    dbIds = generateListOfDatafiles (); 
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

	try {
	    res.setHeader ( "Content-Type", "text/plain;charset=ISO-8859-1" ); 
	    //OutputStream out = res.getOutputStream();
	    out = res.getWriter();

	    try {
		while (dbIds.next()) {
		    fileId = dbIds.getString(1);  

		    lineOut = hostHttpPrefix + "/FileDownload/?fileId=" + fileId; 
		    out.println (lineOut); 
		}
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
    

    private ResultSet generateListOfDatafiles () throws SQLException {
	// We are looking for the files that are
	// a. subsettable
	// b. public (non-restricted)
	// c. locally-produced
	String sqlCmd= "SELECT id from studyfile WHERE restricted = false AND subsettable = true AND NOT (filesystemlocation LIKE 'http%')"; 
	// we only run the search, then return the SQL
	// handle; the actual retrieval of individual records
	// will be done by the code in the body of the service 
	// routine.
  
	Connection sqlConn = dvnDatasource.getConnection();	 
	PreparedStatement sth = sqlConn.prepareStatement(sqlCmd);
	
	ResultSet rs = sth.executeQuery();
        
	return rs; 
    }
    
}

