/*
 * Copyright 2004-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */  
  
package org.apache.shale.tiles;

import com.sun.faces.RIConstants;
import com.sun.faces.application.*;
import com.sun.faces.application.ViewHandlerResponseWrapper;
import com.sun.faces.util.MessageUtils;
import com.sun.faces.util.Util;
import com.sun.faces.config.WebConfiguration;
import com.sun.faces.config.WebConfiguration.WebContextInitParameter;
import com.sun.faces.io.FastStringWriter;
import com.sun.faces.util.DebugUtil;

import java.io.IOException;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.FacesException;
import javax.faces.FactoryFinder;
import javax.faces.application.StateManager;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.jstl.core.Config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tiles.ComponentContext;
import org.apache.tiles.ComponentDefinition;
import org.apache.tiles.DefinitionsFactoryException;
import org.apache.tiles.NoSuchDefinitionException;
import org.apache.tiles.TilesUtil;
import org.apache.tiles.TilesContext;
import org.apache.tiles.context.TilesContextFactory;

/**
 * This view handler strips the suffix off of the view ID and looks
 * for a tile whose name matches the resulting string. For example, if the
 * view ID is /tiles/test.jsp, this view handler will look for a tile named
 * /tiles/test. If the tile is found, it is rendered; otherwise, this view handler
 * delegates to the default JSF view handler.
 * <p/>
 * To render a tile, this view handler first locates the tile by name. Then it
 * creates or accesses the Tile Context, and stores the tile's attributes
 * in the context. Finally, it dispatches the request to the tile's layout
 * by calling JSF's <code>ExternalContext.dispatch()</code>. Layouts typically
 * contain &lt;tiles:insert&gt; tags that include dynamic content.
 * <p/>
 * If the request does not reference a tile, this view handler delegates
 * view rendering to the default view handler. That means that URLs like this:
 * <code>http://localhost:8080/example/index.faces</code> will work as
 * expected.
 *<p/>
 * Most of the methods in this class simply delegate to the default view
 * handler, which JSF passes to this view handler's constructor. The only
 * method that has a meaningful implementation is <code>void
 * renderView(FacesContext, UIViewRoot)</code>, which renders the current
 * view in accordance with the algorithm discussed above.
 *
 * <strong>Note:</strong> This Tiles view handler is tied to the standalone
 * version of Tiles, which resides in the Struts sandbox. This view handler
 * will not work with Struts Tiles.
 */
public class TilesViewHandler extends ViewHandler {

     // Log instance for this class
    private static final Logger logger = Util.getLogger(Util.FACES_LOGGER
                                                  + Util.APPLICATION_LOGGER);

    private static final String AFTER_VIEW_CONTENT = RIConstants.FACES_PREFIX+
                                                     "AFTER_VIEW_CONTENT"; 
       // -------------------------------------------------------- Static Variables


   /**
    * <p><code>MessageFormat</code> used to perform parameter substitution.</p>
    */
   private MessageFormat format = new MessageFormat("");


   /**
    * <p>Log instance for this class.</p>
    */
   private static final Log log = LogFactory.getLog(
                                            TilesViewHandler.class.getName());
   /**
    * <p>Message resources for this class.</p>
    */
   private static ResourceBundle bundle =
      ResourceBundle.getBundle("org.apache.shale.tiles.Bundle",
                               Locale.getDefault(),
                               TilesViewHandler.class.getClassLoader());

    /**
     * <p>The default JSF view handler.</p>
     */
   private ViewHandler defaultViewHandler;
   
   // ------------------------------------------------------------- Constructor


   /**
    * <p>Stores the reference to the default view handler for later use.</p>
    *
    * @param defaultViewHandler The default view handler
    */
   public TilesViewHandler(ViewHandler defaultViewHandler) {
      this.defaultViewHandler = defaultViewHandler;
   }

   // ----------------------------------------------------- ViewHandler Methods
   /**
    * <p>Render a view according to the algorithm described in this class's
    * description: Based on the view Id of the <code>viewToRender</code>,
    * this method either renders a tile or delegates rendering to the default
    * view handler, which takes care of business as usual.</p>
    *
    * @param facesContext The faces context object for this request
    * @param viewToRender The view that we're rendering
    *
    * Modified to do the same thing as SunRI wjb Oct 2006 courtesy sdoglesby
    *
    * March 2007 significantly rewritten 
    * @author wbossons
    */
   public void renderView(FacesContext facesContext, UIViewRoot viewToRender)
                                           throws IOException, FacesException {
         String viewId = viewToRender.getViewId();
         String tileName = getTileName(viewId);
         ComponentDefinition tile = getTile(tileName);
         //DEBUG: Significant rewrite.
        if (!viewToRender.isRendered()) {
            return;
        }
        
        if (tile != null) {
            ExternalContext extContext = facesContext.getExternalContext();

            dispatchToTile(extContext, tile);

            ServletRequest request = (ServletRequest) extContext.getRequest();
            ServletResponse response = (ServletResponse) extContext.getResponse();

            try {
                 if (executePageToBuildView(facesContext, viewToRender, tile)) {
                     response.flushBuffer();
                     ApplicationAssociate applicationassociate = ApplicationAssociate.getInstance(extContext);
                     this.responseRendered();
                     return;
                 }
             } catch (IOException e) {
                 throw new FacesException(e);
             }
             if (logger.isLoggable(Level.WARNING)) { //default was fine
                logger.log(Level.FINE, "Completed building view for : \n" +
                        viewToRender.getViewId());
            }
            if (logger.isLoggable(Level.INFO)) { // default was finest
                logger.log(Level.FINEST, "+=+=+=+=+=+= Printout for " + viewToRender.getViewId() + " about to render.");
                DebugUtil.printTree(viewToRender, logger, Level.FINEST);
            }

            // set up the ResponseWriter

            RenderKitFactory renderFactory = (RenderKitFactory)
            FactoryFinder.getFactory(FactoryFinder.RENDER_KIT_FACTORY);
            RenderKit renderKit = renderFactory.getRenderKit(facesContext, viewToRender.getRenderKitId());

            ResponseWriter oldWriter = facesContext.getResponseWriter();

            if (bufSize == -1) {
                WebConfiguration webConfig =
                      WebConfiguration
                            .getInstance(facesContext.getExternalContext());
                try {
                    bufSize = Integer
                          .parseInt(webConfig.getContextInitParameter(
                                WebContextInitParameter.ResponseBufferSize));
                } catch (NumberFormatException nfe) {
                    bufSize = Integer
                          .parseInt(WebContextInitParameter.ResponseBufferSize.getDefaultValue());
                }
                }
               WriteBehindStringWriter strWriter =
   	               new WriteBehindStringWriter(facesContext, bufSize);
   	         ResponseWriter newWriter;
   	         if (null != oldWriter) {
   	             newWriter = oldWriter.cloneWithWriter(strWriter);
   	         } else {
   	             newWriter = renderKit.createResponseWriter(strWriter, null,
   	                     request.getCharacterEncoding());
   	         }
   	         facesContext.setResponseWriter(newWriter);

   	         newWriter.startDocument();

   	         doRenderView(facesContext, viewToRender);

   	         newWriter.endDocument();

   	         // replace markers in the body content and write it to response.

   	         ResponseWriter responseWriter;
   	         if (null != oldWriter) {
   	             responseWriter = oldWriter.cloneWithWriter(response.getWriter());
   	         } else {
   	             responseWriter = newWriter.cloneWithWriter(response.getWriter());
   	         }
   	         facesContext.setResponseWriter(responseWriter);

   	         strWriter.flushToWriter(responseWriter);

   	         if (null != oldWriter) {
   	             facesContext.setResponseWriter(oldWriter);
   	         }

   	         // write any AFTER_VIEW_CONTENT to the response
   	         writeAfterViewContent(extContext, response);
        } else { // no tile, use default viewhandler
             if (logger.isLoggable(Level.WARNING))  //default was fine
                logger.log(Level.WARNING, "tiles.dispatchingToViewHandler");
             if (logger.isLoggable(Level.INFO)) { // default was finest
                logger.log(Level.FINEST, "+=+=+=+=+=+= Printout for " + viewToRender.getViewId() + " about to render.");
                DebugUtil.printTree(viewToRender, logger, Level.INFO);
             }
           getWrapped().renderView(facesContext, viewToRender);
        }
      }
      
           // Flag indicating that a response has been rendered.  
        private boolean responseRendered = false; 
         
        private static final String ASSOCIATE_KEY = RIConstants.FACES_PREFIX +  
                                                  "ApplicationAssociate";
        
         // This is called by renderView().  
        synchronized void responseRendered() {  
            responseRendered = true;  
        }  
        
        boolean isResponseRendered() {  
            return responseRendered;  
        }  


        /**
         * <p>Special handling of getRenderKitId for decorated
         * implementations.</p>
         * @param context the <code>FacesContext</code> for the current request
         * @return the calculated RenderKit ID
         */
        private String getRenderkitId(FacesContext context) {
            // this is necessary to allow decorated impls.        
            return context.getApplication().getViewHandler().calculateRenderKitId(context);
        }

        /**
         * initBufSize
         *
         * added by wjb oct 2006 per SunRI and sdoglesby
         *  (bufSize is private in ViewHandlerImpl)
         *
         */
         private int bufSize = -1;
         
        private void initBuffSize(FacesContext context) {
            if (bufSize == -1) {
                    synchronized (this) {
                        if (bufSize == -1) {
                            WebConfiguration webConfig = WebConfiguration.getInstance(context.getExternalContext());
                            try {
                                bufSize = Integer
                                      .parseInt(webConfig.getContextInitParameter(
                                            WebContextInitParameter.ResponseBufferSize));
                            } catch (NumberFormatException nfe) {
                                bufSize = Integer
                                      .parseInt(WebContextInitParameter.ResponseBufferSize.getDefaultValue());
                            }
                        }
                    }
                }
            }
	/**
	 * executePageToBuildView
	 *
	 * added by wjb oct 2006 per SunRI and sdoglesby
	 *
	 */
	 private boolean executePageToBuildView(FacesContext context, UIViewRoot viewToExecute, ComponentDefinition tile)
	    throws IOException {
                System.out.println("in executePageToBuildView ...");
	        if (null == context) {
	            String message = MessageUtils.getExceptionMessageString
	                    (MessageUtils.NULL_PARAMETERS_ERROR_MESSAGE_ID, "context");
	            throw new NullPointerException(message);
	        }
	        if (null == viewToExecute) {
	            String message = MessageUtils.getExceptionMessageString
	                    (MessageUtils.NULL_PARAMETERS_ERROR_MESSAGE_ID, "viewToExecute");
	            throw new NullPointerException(message);
	        }

	        String mapping = Util.getFacesMapping(context);

	        String requestURI =
	            updateRequestURI(tile.getPath(), mapping);
	        if (mapping.equals(requestURI)) {
	            // The request was to the FacesServlet only - no path info
	            // on some containers this causes a recursion in the
	            // RequestDispatcher and the request appears to hang.
	            // If this is detected, return status 404
	            HttpServletResponse response = (HttpServletResponse)
	                  context.getExternalContext().getResponse();
	            response.sendError(HttpServletResponse.SC_NOT_FOUND);
	            return true;
	        }

	        ExternalContext extContext = context.getExternalContext();

	        // update the JSTL locale attribute in request scope so that JSTL
	        // picks up the locale from viewRoot. This attribute must be updated
	        // before the JSTL setBundle tag is called because that is when the
	        // new LocalizationContext object is created based on the locale.
	        // PENDING: this only works for servlet based requests
	        if (extContext.getRequest()
	        instanceof ServletRequest) {
	            Config.set((ServletRequest)
	            extContext.getRequest(),
	                       Config.FMT_LOCALE, context.getViewRoot().getLocale());
	        }

	        // save the original response
	        Object originalResponse = extContext.getResponse();

	        // replace the response with our wrapper
	        ViewHandlerResponseWrapper wrapped =
	              new ViewHandlerResponseWrapper(
	                    (HttpServletResponse)extContext.getResponse());
	        extContext.setResponse(wrapped);

	        // build the view by executing the page
	        extContext.dispatch(requestURI);

	        // replace the original response
	        extContext.setResponse(originalResponse);
	        // Follow the JSTL 1.2 spec, section 7.4,
	        // on handling status codes on a forward
	        if (wrapped.getStatus() < 200 || wrapped.getStatus() > 299) {
	            // flush the contents of the wrapper to the response
	            // this is necessary as the user may be using a custom
	            // error page - this content should be propagated
	            wrapped.flushContentToWrappedResponse();
	            return true;
	        }

	        // Put the AFTER_VIEW_CONTENT into request scope
	        // temporarily
	        if (wrapped.isBytes()) {
	            extContext.getRequestMap().put(AFTER_VIEW_CONTENT,
	                                           wrapped.getBytes());
	        } else if (wrapped.isChars()) {
	            extContext.getRequestMap().put(AFTER_VIEW_CONTENT,
	                                           wrapped.getChars());
	        }

	        return false;

	    }
         
     /* added from SunRI wjb oct 2006
     *
     */
     private void writeAfterViewContent(ExternalContext extContext,
                                       ServletResponse response)
    throws IOException {
        Object content = extContext.getRequestMap().get(AFTER_VIEW_CONTENT);
        assert(null != content);
        if (content instanceof char []) {
            response.getWriter().write((char []) content);
        } else if (content instanceof byte []) {
            response.getWriter().write(new String((byte[]) content));
        } else {
            assert(false);
        }

        response.flushBuffer();

        // remove the AFTER_VIEW_CONTENT from the view root
        extContext.getRequestMap().remove(AFTER_VIEW_CONTENT);
    }
   
         
         /**
     * <p>This is a separate method to account for handling the content
     * after the view tag.</p>
     *
     * <p>Create a new ResponseWriter around this response's Writer.
     * Set it into the FacesContext, saving the old one aside.</p>
     *
     * <p>call encodeBegin(), encodeChildren(), encodeEnd() on the
     * argument <code>UIViewRoot</code>.</p>
     *
     * <p>Restore the old ResponseWriter into the FacesContext.</p>
     *
     * <p>Write out the after view content to the response's writer.</p>
     *
     * <p>Flush the response buffer, and remove the after view content
     * from the request scope.</p>
     * 
     * @param context the <code>FacesContext</code> for the current request
     * @param viewToRender the view to render
     * @throws IOException if an error occurs rendering the view to the client
     * @throws FacesException if some error occurs within the framework
     *  processing
          * This method is private in ViewHandlerImpl
          * @author wbossons
     */

    private void doRenderView(FacesContext context,
                              UIViewRoot viewToRender) 
    throws IOException, FacesException {   

        ApplicationAssociate associate =
            ApplicationAssociate.getInstance(context.getExternalContext());

        if (null != associate) {
            responseRendered();
        }

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "About to render view " + viewToRender.getViewId());
        }

        viewToRender.encodeAll(context);
    }


/** for jsf 1.2 support
 *  getWrapped()
 *
 *
 */
   public ViewHandler getWrapped() {
       return defaultViewHandler;
   }


   /**
    * <p>Pass through to the default view handler.</p>
    *
    */
   public UIViewRoot createView(FacesContext context, String viewId) {
      return getWrapped().createView(context, viewId);
   }


   /**
    * <p>Pass through to the default view handler.</p>
    *
    */
   public Locale calculateLocale(FacesContext context) {
      return getWrapped().calculateLocale(context);
   }


   /**
    * <p>Pass through to the default view handler.</p>
    *
    */
   public String calculateRenderKitId(FacesContext context) {
      return getWrapped().calculateRenderKitId(context);
   }


   /**
    * <p>Pass through to the default view handler.</p>
    *
    */
   public String getActionURL(FacesContext context, String viewId) {
      return getWrapped().getActionURL(context, viewId);
   }


   /**
    * <p>Pass through to the default view handler.</p>
    *
    */
   public String getResourceURL(FacesContext context, String path) {
      return getWrapped().getResourceURL(context, path);
   }

   /**
    * <p>Pass through to the default view handler.</p>
    *
    */
   public UIViewRoot restoreView(FacesContext context, String viewId) {
      return getWrapped().restoreView(context, viewId);
   }


   /**
    * <p>Pass through to the default view handler.</p>
    *
    */
   public void writeState(FacesContext context) throws IOException {
      getWrapped().writeState(context);
   }

   /**
     * <p>if the specified mapping is a prefix mapping, and the provided 
     * request URI (usually the value from <code>ExternalContext.getRequestServletPath()</code>)
     * starts with <code>mapping + '/'</code>, prune the mapping from the
     * URI and return it, otherwise, return the original URI. 
     * @param uri the servlet request path
     * @param mapping the FacesServlet mapping used for this request
     * @return the URI without additional FacesServlet mappings
     * @since 1.2
    *
    * Also added from SunRi by wjb oct 2006
     */
    private String updateRequestURI(String uri, String mapping) {
        
        if (!Util.isPrefixMapped(mapping)) {
            return uri;
        } else {
            int length = mapping.length() + 1;
            StringBuilder builder = new StringBuilder(length);
            builder.append(mapping).append('/');
            String mappingMod = builder.toString();
            boolean logged = false;
            while (uri.startsWith(mappingMod)) {
                
                uri = uri.substring(length - 1);
            }
            return uri;
        }
    }    

   // --------------------------------------------------------- Private Methods


   /**
    * <p>Looks up a tile, given a name. If the tile does not exist, and the
    * <code>name</code> begins with a slash ('/'), look for a tile
    * without the slash. If no tile is found, return <code>null</code>.</p>
    *
    * @param name The tile to lookup
    */
   private ComponentDefinition getTile(String name) {
      if (name == null)
         return null;

      ExternalContext externalContext = FacesContext.getCurrentInstance()
                                                    .getExternalContext();
      Object request = externalContext.getRequest();
      Object context = externalContext.getContext();
      ComponentDefinition tile = null;
      try {
          TilesContext tilesContext = TilesContextFactory.getInstance(context, request);
          tile = TilesUtil.getDefinition(name, tilesContext);
      } catch (NoSuchDefinitionException nsex) {
          log.error("Couldn't find Tiles definition.", nsex);
      } catch (DefinitionsFactoryException dex) {
          log.error("Tiles error", dex);
      }
      return tile;

   }

   /**
    * <p>Given a view ID, returns the name of the corresponding tile. For
    * example, for a view ID of /tiles/example/main.jsp, the tile name
    * returned by this method would be /tiles/example/main.</p>
    *
    * @param viewId The view ID
    */
   private String getTileName(String viewId) {
      int suffixIndex = viewId.lastIndexOf('.');
      return suffixIndex != -1 ? viewId.substring(0, suffixIndex)
                               : viewId;
   }

   /**
    * <p>Dispatches to a tile's layout. Layouts typically contain
    * &lt;tiles:insert&gt; tags that include content, so dispatching
    * to the tile's layout will automatically build the tile.</p>
    * <p>
    * Before dispatching to the tile, this method sets up the Tile
    * context.</p>
    *
    * @param externalContext The JSF external context
    * @param tile The tile definition
    */
   private void dispatchToTile(ExternalContext externalContext,
                               ComponentDefinition tile)
                               throws java.io.IOException {
      Object request = externalContext.getRequest();
      Object context = externalContext.getContext();
      TilesContext tilesContext = TilesContextFactory.getInstance(context, request);
      ComponentContext tileContext = ComponentContext.getContext(tilesContext);
      if (tileContext == null) {
         tileContext = new ComponentContext(tile.getAttributes());
         ComponentContext.setContext(tileContext, tilesContext);
      }
      else
         tileContext.addMissing(tile.getAttributes());
      // renderToTile rem'd in favor of renderView calling
      // dispatch to the tile's layout
      //DEBUG: rem this line
      //externalContext.dispatch(tile.getPath());
   }
   
   // ----------------------------------------------------------- Inner Classes
// ----------------------------------------------------------- Inner Classes

    /**
     * Thanks to the Facelets folks for some of the concepts incorporated
     * into this class.
     */
    private static final class WriteBehindStateWriter extends Writer {
        // length of the state marker
        private static final int STATE_MARKER_LEN =
              RIConstants.SAVESTATE_FIELD_MARKER.length();

        private static final ThreadLocal<WriteBehindStateWriter> CUR_WRITER =
             new ThreadLocal<WriteBehindStateWriter>();
        private Writer out;
        private Writer orig;
        private FastStringWriter fWriter;
        private boolean stateWritten;
        private int bufSize;
        private char[] buf;
        private FacesContext context;


        // -------------------------------------------------------- Constructors


        public WriteBehindStateWriter(Writer out, FacesContext context, int bufSize) {
            this.out = out;
            this.orig = out;
            this.context = context;
            this.bufSize = bufSize;
            this.buf = new char[bufSize];
            CUR_WRITER.set(this);
        }


        // ------------------------------------------------- Methods from Writer



        public void write(int c) throws IOException {
            out.write(c);
        }


        public void write(char cbuf[]) throws IOException {
            out.write(cbuf);
        }


        public void write(String str) throws IOException {
            out.write(str);
        }


        public void write(String str, int off, int len) throws IOException {
            out.write(str, off, len);
        }


        public void write(char cbuf[], int off, int len) throws IOException {
            out.write(cbuf, off, len);
        }


        public void flush() throws IOException {
            // no-op
        }


        public void close() throws IOException {
           // no-op
        }


        // ------------------------------------------------------ Public Methods


        public static WriteBehindStateWriter getCurrentInstance() {
            return CUR_WRITER.get();
        }


        public void release() {
            CUR_WRITER.set(null);
        }


        public void writingState() {
            if (!stateWritten) {
                this.stateWritten = true;
                out = fWriter = new FastStringWriter(1024);
            }
        }

        public boolean stateWritten() {
            return stateWritten;
        }

        /**
         * <p> Write directly from our FastStringWriter to the provided
         * writer.</p>
         * @param writer where to write
         * @throws IOException if an error occurs
         */
        public void flushToWriter() throws IOException {
            // Save the state to a new instance of StringWriter to
            // avoid multiple serialization steps if the view contains
            // multiple forms.
            StateManager stateManager = Util.getStateManager(context);
            ResponseWriter origWriter = context.getResponseWriter();
            FastStringWriter state =
                  new FastStringWriter((stateManager.isSavingStateInClient(
                        context)) ? bufSize : 128);
            context.setResponseWriter(origWriter.cloneWithWriter(state));
            stateManager.writeState(context, stateManager.saveView(context));
            context.setResponseWriter(origWriter);
            StringBuilder builder = fWriter.getBuffer();
            // begin writing...
            int totalLen = builder.length();
            StringBuilder stateBuilder = state.getBuffer();
            int stateLen = stateBuilder.length();
            int pos = 0;
            int tildeIdx = getNextDelimiterIndex(builder, pos);
            while (pos < totalLen) {
                if (tildeIdx != -1) {
                    if (tildeIdx > pos && (tildeIdx - pos) > bufSize) {
                        // theres enough content before the first ~
                        // to fill the entire buffer
                        builder.getChars(pos, (pos + bufSize), buf, 0);
                        orig.write(buf);
                        pos += bufSize;
                    } else {
                        // write all content up to the first '~'
                        builder.getChars(pos, tildeIdx, buf, 0);
                        int len = (tildeIdx - pos);
                        orig.write(buf, 0, len);
                        // now check to see if the state saving string is
                        // at the begining of pos, if so, write our
                        // state out.
                        if (builder.indexOf(
                              RIConstants.SAVESTATE_FIELD_MARKER,
                              pos) == tildeIdx) {
                            // buf is effectively zero'd out at this point
                            int statePos = 0;
                            while (statePos < stateLen) {
                                if ((stateLen - statePos) > bufSize) {
                                    // enough state to fill the buffer
                                    stateBuilder.getChars(statePos,
                                                          (statePos + bufSize),
                                                          buf,
                                                          0);
                                    orig.write(buf);
                                    statePos += bufSize;
                                } else {
                                    int slen = (stateLen - statePos);
                                    stateBuilder.getChars(statePos,
                                                          stateLen,
                                                          buf,
                                                          0);
                                    orig.write(buf, 0, slen);
                                    statePos += slen;
                                }

                            }
                             // push us past the last '~' at the end of the marker
                            pos += (len + STATE_MARKER_LEN);
                            tildeIdx = getNextDelimiterIndex(builder, pos);
                        } else {
                            pos = tildeIdx;
                            tildeIdx = getNextDelimiterIndex(builder,
                                                             tildeIdx + 1);

                        }
                    }
                } else {
                    // we've written all of the state field markers.
                    // finish writing content
                    if (totalLen - pos > bufSize) {
                        // there's enough content to fill the buffer
                        builder.getChars(pos, (pos + bufSize), buf, 0);
                        orig.write(buf);
                        pos += bufSize;
                    } else {
                        // we're near the end of the response
                        builder.getChars(pos, totalLen, buf, 0);
                        int len = (totalLen - pos);
                        orig.write(buf, 0, len);
                        pos += (len + 1);
                    }
                }
            }
        }

        private static int getNextDelimiterIndex(StringBuilder builder,
                                                 int offset) {
            return builder.indexOf(RIConstants.SAVESTATE_FIELD_DELIMITER,
                                   offset);
        }

    }
    
    // ----------------------------------------------------------- Inner Classes

    /**
     * <p>Handles writing the response from the dispatched request
     * and replaces any state markers with the actual
     * state supplied by the <code>StateManager</code>.
     */
    private static final class WriteBehindStringWriter extends FastStringWriter {
                        
        // length of the state marker
        private static final int STATE_MARKER_LEN = 
              RIConstants.SAVESTATE_FIELD_MARKER.length();
                        
        // the context for the current request
        private final FacesContext context;
        
        // char buffer
        private final char[] buf;
        
        // buffer length
        private final int bufSize;        
        

        /**
         * <p>Create a new <code>WriteBehindStringWriter</code> for the current
         * request with an initial capacity.</p>
         * @param context the <code>FacesContext</code> for the current request
         * @param initialCapcity the StringBuilder's initial capacity
         */
        public WriteBehindStringWriter(FacesContext context, int initialCapcity) {
            super(initialCapcity);         
            this.context = context;      
            bufSize = initialCapcity;
            buf = new char[bufSize];
        }

        /**
         * <p> Write directly from our StringBuilder to the provided
         * writer.</p>
         * @param writer where to write
         * @throws IOException if an error occurs
         */
        public void flushToWriter(Writer writer) throws IOException {
            StateManager stateManager = Util.getStateManager(context);
            Object stateToWrite = stateManager.saveView(context);
            int totalLen = builder.length();
            int pos = 0;
            int tildeIdx = getNextDelimiterIndex(pos);
            while (pos < totalLen) {
                if (tildeIdx != -1) {
                    if (tildeIdx > pos && (tildeIdx - pos) > bufSize) {
                        // theres enough content before the first ~ 
                        // to fill the entire buffer
                        builder.getChars(pos, (pos + bufSize), buf, 0);
                        writer.write(buf);
                        pos += bufSize;
                    } else {
                        // write all content up to the first '~'
                        builder.getChars(pos, tildeIdx, buf, 0);
                        int len = (tildeIdx - pos);
                        writer.write(buf, 0, len);                       
                        // now check to see if the state saving string is
                        // at the begining of pos, if so, write our 
                        // state out.                               
                        if (builder.indexOf(
                              RIConstants.SAVESTATE_FIELD_MARKER, 
                              pos) == tildeIdx) {
                            stateManager.writeState(context, stateToWrite);
                         // push us past the last '~' at the end of the marker
                            pos += (len + STATE_MARKER_LEN);
                            tildeIdx = getNextDelimiterIndex(pos);
                        } else {
                            pos = tildeIdx;
                            tildeIdx = getNextDelimiterIndex(tildeIdx + 1);
                        }
                    }
                    } else {
                    // we've written all of the state field markers.
                    // finish writing content
                    if (totalLen - pos > bufSize) {
                        // there's enough content to fill the buffer
                        builder.getChars(pos, (pos + bufSize), buf, 0);
                        writer.write(buf);
                        pos += bufSize;
                    } else {
                        // we're near the end of the response
                        builder.getChars(pos, totalLen, buf, 0);
                        int len = (totalLen - pos);
                        writer.write(buf, 0, len);
                        pos += (len + 1);                      
                    }
                }
            }
        }

        /**         
         * @return return the length of the underlying 
         * <code>StringBuilder</code>.
         */
        public int length() {
            return builder.length();
        }

        /**
         * <p>Get the next `~' from the StringBuilder.</p>
         * @param offset the offset from where to search from
         * @return the index of the first '~' from the specified
         *  offset
         */
        private int getNextDelimiterIndex(int offset) {
            return builder.indexOf(RIConstants.SAVESTATE_FIELD_DELIMITER, 
                                   offset);            
        }
    }

}
