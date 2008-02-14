
package edu.harvard.hmdc.vdcnet.web.z3950;

import java.util.HashMap;
import java.util.Map;
import org.jzkit.search.provider.iface.*;
import org.jzkit.search.util.ResultSet.*;
import org.jzkit.search.util.RecordModel.*;
import java.util.logging.*;
import jdbm.btree.BTree;

/**
 * @author Ian Ibbotson
 * @version $Id: LuceneResultSet.java,v 1.6 2005/02/18 09:24:22 ibbo Exp $
 */ 
public class LuceneResultSet extends AbstractIRResultSet implements IRResultSet
{
  private static Logger log = Logger.getLogger(AbstractIRResultSet.class.getName());
  int num_hits = 0;
  private BTree tree = null;
  private Map map = null;

  public LuceneResultSet() {
      map = new HashMap();
  }

  public void add(String record) {
//  public void add(byte[] record) {
      map.put(new Long(num_hits++), record);
  }

    // Fragment Source methods
  public InformationFragment[] getFragment(int starting_fragment,
                                           int count,
                                           RecordFormatSpecification spec) throws IRResultSetException
  {
      InformationFragment[] result = new InformationFragment[count];
      for (int i = 0; i < count; i++) {
          long recno = starting_fragment - 1 + i;
          String record = (String) map.get(new Long(recno));
          TextFragment marc  = new org.jzkit.search.util.RecordModel.TextFragment(record.getBytes());
          result[i] = marc;
      }
      return result;
  }

  public void asyncGetFragment(int starting_fragment,
                               int count,
                               RecordFormatSpecification spec,
                               IFSNotificationTarget target)
  {
    try {
      InformationFragment[] result = getFragment(starting_fragment,count,spec);
      target.notifyRecords(result);
    }
    catch ( IRResultSetException irrse ) {
      target.notifyError("bib-1",new Integer(0),"Problem obtaining result records",irrse);
    }
  }

  /** Current number of fragments available */
  public int getFragmentCount()
  {
    return num_hits;
  }

  /** The size of the result set (Estimated or known) */
  public int getRecordAvailableHWM()
  {
    return num_hits;
  }

  // public AsynchronousEnumeration elements()
  // {
  //   return null;
  // }

  /** Release all resources and shut down the object */
  public void close()
  {
  }

  public IRResultSetInfo getResultSetInfo() {
    return new IRResultSetInfo(getResultSetName(),
                               getFragmentCount(),
                               getStatus());
  }
}
