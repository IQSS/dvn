/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web.z3950;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.FileHandler;
import org.jzkit.search.provider.iface.IRQuery;
import org.jzkit.search.provider.iface.Searchable;
import org.jzkit.search.util.QueryModel.Internal.InternalModelRootNode;
import org.jzkit.search.util.QueryModel.InvalidQueryException;
import org.jzkit.search.util.QueryModel.QueryModel;
import org.jzkit.search.util.ResultSet.IRResultSet;
import org.jzkit.search.util.ResultSet.IRResultSetStatus;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author roberttreacy
 */
public class Z3950search implements Searchable {

    ApplicationContext ctx = null;
    private Map archetypes = new HashMap();
    private static Logger log = Logger.getLogger(Z3950search.class.getName());
    
    public Z3950search(){
        try {
            log.addHandler(new FileHandler("Z3950.log"));
        } catch (IOException ex) {
            Logger.getLogger(Z3950search.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(Z3950search.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void close() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public IRResultSet evaluate(IRQuery q) {
        return evaluate(q,null);
    }

    public IRResultSet evaluate(IRQuery q, Object user_info) {
        return evaluate(q,user_info, null );
    }

    public IRResultSet evaluate(IRQuery q, Object user_info, Observer[] observers) {
        LuceneQueryVisitor lqv = new LuceneQueryVisitor();
        try {
            QueryModel qm = q.getQueryModel();
            q.getCollections();
            InternalModelRootNode imrn = qm.toInternalQueryModel(ctx);

            try {
                lqv.visit(q.getQueryModel().toInternalQueryModel(ctx));
            } catch (IOException ex) {
                Logger.getLogger(Z3950search.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidQueryException ex) {
                Logger.getLogger(Z3950search.class.getName()).log(Level.SEVERE, null, ex);
            }
            log.info("Z3950search evaluate()");
        } catch (InvalidQueryException ex) {
            Logger.getLogger(Z3950search.class.getName()).log(Level.SEVERE, null, ex);
        }
        LuceneSearch search = LuceneSearch.getInstance();
        List<SearchTerm> searchTerms = new ArrayList();
        searchTerms.add(lqv.getSearchTerm());
//        List<Long> results = null;
        List<String> results = null;
        try {
            results = search.search(searchTerms);
        } catch (IOException e) {
            log.severe("Search error " + e.getMessage());
        }
        String repositoryDir = "/nfs/iqss/DVN/data/";
        File repositoryDirFile = new File(repositoryDir);
        String [] handleDirs = repositoryDirFile.list();
//        String fileDir = "/nfs/iqss/DVN/data/1902.1/";
        LuceneResultSet result = new LuceneResultSet();
        for (Iterator it = results.iterator(); it.hasNext();) {
            String elem = (String) it.next();
            String marcFileStr = repositoryDir + elem.toUpperCase() + "/" + "export_marc";
//            System.out.println("MARC file "+ marcFileStr);
            File marcFile = new File(marcFileStr);
//                log.info(marcFileStr);
            if (marcFile.exists()) {
//                byte[] marcStudy = readFile(marcFile);
                String marcStudy = readFile(marcFile);
//                System.out.println(marcFileStr+" exists");
                result.add(marcStudy);
            }else{
//                System.out.println(marcFileStr+" doesn't exist");
            }
        }
        result.setStatus(IRResultSetStatus.COMPLETE);
        return result;
    }

    public void setRecordArchetypes(Map record_syntax_archetypes) {
    this.archetypes = record_syntax_archetypes;
    }

    public Map getRecordArchetypes() {
        return archetypes;
    }

    public void setApplicationContext(ApplicationContext arg0) throws BeansException {
        this.ctx = ctx;
    }

    public static void copy(InputStream in, OutputStream out)
    throws IOException {
        byte[] buffer = new byte[8192];
        while (true) {
            int bytesRead = in.read(buffer);
            if (bytesRead == -1) break;
            out.write(buffer, 0, bytesRead);
            out.flush();
        }
    }
    public String readFile(File inputFile) {
//    public byte[] readFile(File inputFile) {
        FileInputStream instream = null;
        FileChannel in = null;
        ByteArrayOutputStream out = null;
        String outString = null;
        InputStream instream2 = null;
        
        try {
            try {
                instream = new FileInputStream(inputFile);
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
            in = instream.getChannel();
            instream2 = Channels.newInputStream(in);
            out = new ByteArrayOutputStream();
            try {
                copy(instream2, out);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            outString = out.toString();
        } finally {
            if (instream != null) {
                try {instream.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                } }
            if (in != null) {
                try {in.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                } }
            if (out != null) {
                try {out.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                } }
        }
        byte[] outByteArray = out.toByteArray();
        return new String(outByteArray);
//        return out.toByteArray();
//        return outString;
    }
    
}
