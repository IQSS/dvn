/*
 * Dataverse Network - A web application to distribute, share and analyze quantitative data.
 * Copyright (C) 2011
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
 * DataVisServlet.java
 *
 * Created on July 28, 2011, 2:07 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.web.servlet;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

import java.io.OutputStream;
import java.io.PrintWriter;


import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.net.URL;
import java.net.URLDecoder;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.imageio.IIOException;

import java.util.logging.Logger;

/**
 *
 * @author landreev
 * uses Steve Kraffmiller's code for generating visualization graph images
 *
 */
public class DataVisServlet extends HttpServlet {

    /** Creates a new instance of DataVisServlet */
    public DataVisServlet() {
    }

    /** Sets the logger (use the package name) */
    private static Logger dbgLog = Logger.getLogger(DataVisServlet.class.getPackage().getName());


    private String graphTitle = "";
    private String yAxisLabel = "";
    private String sources = "";


    public void service(HttpServletRequest req, HttpServletResponse res) {

        // Parameters:

        String googleImageURL = req.getParameter("googleImageUrl");
        graphTitle = req.getParameter("graphTitle");
        yAxisLabel = req.getParameter("yAxisLabel");
        sources = req.getParameter("sources");

        OutputStream out = null;

        try {
            String decoded = URLDecoder.decode(googleImageURL, "UTF-8");



            out = res.getOutputStream();

            if (!decoded.isEmpty()){
                URL imageURLnew = new URL(googleImageURL);

                try{
                    BufferedImage image =     ImageIO.read(imageURLnew);
                    BufferedImage combinedImage = getCompositeImage(image);

                    // MIME type header:

                    res.setHeader("Content-Type", "image/png");
                    // image content:

                    ImageIO.write(combinedImage, "png", out);

                } catch (IIOException io){
                    // TODO:
                    // log diagnostic messages;
                    // provide more info in the error response;
                    createErrorResponse404(res);
                }
            }


        } catch (UnsupportedEncodingException uee){
            // TODO:
            // log diagnostic messages;
            // provide more info in the error response;
            createErrorResponse404(res);

        } catch (MalformedURLException mue){
            // TODO:
            // log diagnostic messages;
            // provide more info in the error response;
        } catch (IOException io){
            // TODO:
            // log diagnostic messages;
            // provide more info in the error response;
        }

        if (out != null) {
            try {
                out.close();
            } catch (IOException x) {}
        }

        return;
    } // end of the main service() method.


    // The 3 methods below, compositeImage, rotateImage and stringToImage, have
    // been cut-and-pasted from ExploreDataPage by Steve Kraffmiller;
    // In the long run, we'll want this code to be in one place and not
    // duplicated. For now, I'm just trying to get it all to work.

    private BufferedImage getCompositeImage(BufferedImage image){
        BufferedImage yAxisImage = new BufferedImage(100, 500, BufferedImage.TYPE_INT_ARGB);
        BufferedImage yAxisImageHoriz = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
        BufferedImage combinedImage = new BufferedImage(776, 550, BufferedImage.TYPE_INT_ARGB);
        BufferedImage titleImage = new BufferedImage(676, 50, BufferedImage.TYPE_INT_ARGB );
        BufferedImage sourceImage = new BufferedImage(676, 50, BufferedImage.TYPE_INT_ARGB );
        BufferedImage yAxisVert = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);

        Graphics2D yag2 = yAxisImage.createGraphics();
        Graphics2D cig2 = combinedImage.createGraphics();
        Graphics2D tig2 = titleImage.createGraphics();
        Graphics2D sig2 = sourceImage.createGraphics();
        Graphics2D yahg2 = yAxisImageHoriz.createGraphics();
        Graphics2D yaxg2 = yAxisVert.createGraphics();

        yag2.setColor(Color.WHITE);
        tig2.setColor(Color.WHITE);
        sig2.setColor(Color.WHITE);
        yahg2.setColor(Color.WHITE);
        yaxg2.setColor(Color.WHITE);
        yag2.fillRect(0, 0, 676, 500);
        tig2.fillRect(0, 0, 876, 500);
        sig2.fillRect(0, 0, 876, 500);
        yahg2.fillRect(0, 0, 876, 500);
        yaxg2.fillRect(0, 0, 100, 500);
        Font font = new Font("Arial", Font.BOLD, 10);
        Font hFont = new Font("Arial", Font.BOLD, 12);
        Font tFont = new Font("Arial", Font.BOLD, 14);
        Font sFont = new Font("Arial", Font.PLAIN, 10);
        yag2.setFont(font);
        tig2.setFont(tFont);
        sig2.setFont(sFont);
        yahg2.setFont(hFont);

        String source = "";

        if (!sources.trim().isEmpty()) {
             source = "Source: " + sources;
        }

        tig2.setPaint(Color.black);
        sig2.setPaint(Color.black);
        yahg2.setPaint(Color.black);

        writeStringToImage(yAxisImageHoriz, yahg2, yAxisLabel, true, 20, 10);
        writeStringToImage(titleImage, tig2, graphTitle, true, 20, 10 );
        writeStringToImage(sourceImage, sig2, source, false, 15, 10 );
        
        BufferedImage yAxisImageRotated = rotateImage(yAxisImageHoriz );

        yaxg2.drawImage ( yAxisImageRotated,
              0, 0, 200, 200,
              0, 0, 200, 200,
        null);

        cig2.drawImage(yAxisImage, 0, 0, null);
        cig2.drawImage(yAxisVert, 0, 120, null);
        cig2.drawImage(image, 50, 50, null);
        cig2.drawImage(titleImage, 50, 0, null);
        cig2.drawImage(sourceImage, 50, 450, null);
        
        BufferedImage smushedCombinedImage = new BufferedImage(590, 500, BufferedImage.TYPE_INT_ARGB);
        Graphics2D scig2 = yAxisVert.createGraphics();
        
        scig2.drawImage ( yAxisImageRotated,
              0, 0, 590, 500,
              0, 0, 776, 550,
        null);

        yag2.dispose();
        tig2.dispose();
        sig2.dispose();
        yahg2.dispose();
        cig2.dispose();
        scig2.dispose();
        
        combinedImage = scaleImage(combinedImage);
        
        return combinedImage;
    }

    private BufferedImage scaleImage(BufferedImage img){
        final float SCALE = .75f;

        float newHeight = SCALE * img.getHeight(null);
        int height = Math.round(newHeight);
        
        float newWidth = SCALE * img.getWidth(null);
        int width = Math.round(newWidth);

        BufferedImage bi = new BufferedImage( width,
                                             height,
                                             BufferedImage.TYPE_INT_ARGB);

        Graphics2D grph = (Graphics2D) bi.getGraphics();
        grph.scale(SCALE, SCALE);

        grph.drawImage(img, 0, 0, null);
        grph.dispose();
        
        return bi;

    }
    
    
    private void writeStringToImage(BufferedImage imageIn, 
            Graphics2D gr, String stringIn, boolean center, int startHeight, int startWidth){

        int width = imageIn.getWidth();
        int height = imageIn.getHeight();
        String splitString1 = "";
        String splitString2 = "";
        FontRenderContext context = gr.getFontRenderContext();
        Double strWidth = new Double (gr.getFont().getStringBounds(stringIn, context).getWidth());        
                
        if (strWidth > width){            
            int strLen = stringIn.length();
            int half = stringIn.length()/2;
            int nextSpace = stringIn.indexOf(" ",  half);                
            splitString1 = stringIn.substring(0, nextSpace);
            splitString2 = stringIn.substring(nextSpace + 1, strLen);
        }
        if (strWidth < width){
            if (center){
                Double halfWidth = new Double(Math.round(strWidth/2));
                int iHalf = halfWidth.intValue();
                int startpoint = width/2 - (iHalf);
                gr.drawString(stringIn, startpoint, startHeight * 2);
            } else {
                gr.drawString(stringIn, startWidth, startHeight);
            }
        }  else {
            if (center){
                Double strWidth1 = new Double (gr.getFont().getStringBounds(splitString1, context).getWidth()); 
                Double strWidth2 = new Double (gr.getFont().getStringBounds(splitString2, context).getWidth()); 
                Double halfWidth1 = new Double(Math.round(strWidth1/2));
                Double halfWidth2 = new Double(Math.round(strWidth2/2));
                int iHalf1 = halfWidth1.intValue();
                int iHalf2 = halfWidth2.intValue();
                int startpoint1 = width/2 - (iHalf1);
                int startpoint2 = width/2 - (iHalf2);
                gr.drawString(splitString1, startpoint1, startHeight);
                gr.drawString(splitString2, startpoint2, startHeight * 2);
            } else {
                gr.drawString(splitString1, startWidth, startHeight);
                gr.drawString(splitString2, startWidth, startHeight * 2);
            }
            
        }
        
        
    }


    private BufferedImage rotateImage(BufferedImage bufferedImage) {

        AffineTransform transform = new AffineTransform();
        transform.rotate((3.*Math.PI)/2., bufferedImage.getWidth()/2, bufferedImage.getHeight()/2);
        AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
        bufferedImage = op.filter(bufferedImage, null);
        return bufferedImage;
    }


    // This method can be used to send extra diagnostics information with
    // error responses:

    private void createErrorResponseGeneric(HttpServletResponse res, int status, String statusLine) {
        res.setContentType("text/html");

        if (status == 0) {
            status = 200;
        }

        res.setStatus(status);
        PrintWriter out = null;
        try {
            out = res.getWriter();
            out.println("<HTML>");
            out.println("<HEAD><TITLE>File Download</TITLE></HEAD>");
            out.println("<BODY>");
            out.println("<BIG>" + statusLine + "</BIG>");

            if (status == res.SC_NOT_FOUND) {
                for (int i = 0; i < 10; i++) {
                    out.println("<!-- This line is filler to handle IE case for 404 errors   -->");
                }
            }

            out.println("</BODY></HTML>");
            out.flush();
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }


    private void createErrorResponse404(HttpServletResponse res) {
        createErrorResponseGeneric(res, res.SC_NOT_FOUND, "Sorry. The file you are looking for could not be found.");
        //createRedirectResponse(res, "/dvn/faces/ErrorPage.xhtml?errorMsg=Sorry. The file you are looking for could not be found.&errorCode=404");
    }

}

