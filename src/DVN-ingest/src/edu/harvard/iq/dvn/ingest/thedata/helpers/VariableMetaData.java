/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.ingest.thedata.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// R-specific
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.RList;

/**
 * Variable Meta Data
 * An object storing meta-data for a collection of values of a DataTable. This
 * is used to properly restore file from the TAB format to other formats.
 * Notably, this helps characterize data in the Rdata format.
 * Additionally, this is a good place to start formalizing the TAB -> Other format
 * out-gest (opposite of ingest) procedure.
 * @author Matt Owen
 * @since 2013-04-10
 * @version 1.0
 */
public class VariableMetaData {
  public static ArrayList <Integer> VARIABLE_TYPES = new ArrayList <Integer> () {{
    add(0);
    add(1);
  }};
  
  public static Map VARIABLE_TYPE_STRINGS = new HashMap <Integer, String> () {{
    put(-1, null);
    put(0, "character");
    put(1, "integer");
    put(2, "numeric");
  }};
  
  // Types of "strings" that have special meta-data associated with them
  public static String [] VARIABLE_CHARACTER_SUBCLASSES = { "Date", "Time", "DateTime", "Factor" };
  
  Integer mType, mSubType;
  String mTypeString, mDateTimeFormat;
  String [] mClass, mFactorLevels;
  
  /**
   * Construct a Meta-data-less Object
   * @since 2013-04-10
   */
  public VariableMetaData () {
    mType = -1;
    mTypeString = null;
    mFactorLevels = new String[0];
  }
  /**
   * Construct an Variable Meta Data Object from its Numeric Type
   * @param type 
   */
  public VariableMetaData (int type) {
    mType = VARIABLE_TYPES.contains(type) ? type : -1;
    mTypeString = (String) VARIABLE_TYPE_STRINGS.get(mType);
    mFactorLevels = new String[0];
  }
  /**
   * Construct a Variable Meta Data Object with Levels
   * @param type
   * @param factorLevels 
   */
  public VariableMetaData (int type, String [] factorLevels) {
    mType = VARIABLE_TYPES.contains(type) ? type : -1;
    mTypeString = (String) VARIABLE_TYPE_STRINGS.get(mType);
    mFactorLevels = mType == 0 ? factorLevels : new String[0];
  }
  /**
   * Whether Column is a Character
   * @return true if variable stores meta-data for a character-variable. Note
   * that meta-data could also represent date, time, datetime, or factor values.
   */
  public boolean isCharacter () {
    return mType == 0;
  }
  /**
   * Whether Column is an Integer
   * @return true if variable stores meta-data for an integer-variable
   */
  public boolean isInteger () {
    return mType == 1;
  }
  /**
   * Whether Column is a Continuous Number
   * @return true if variable stores meta-data for a a continuous-type
   * variable - double, float, etc.
   */
  public boolean isContinuous () {
    return mType == 2;
  }
  /**
   * Whether Column is a Date Value
   * @return true if variable stores meta-data for a Date variable
   */
  public boolean isDate () {
    return false;
  }
  /**
   * Whether Column is a Time Value
   * @return true if variable stores meta-data for a Date variable
   */
  public boolean isTime () {
    return false;
  }
  /**
   * Whether Column is a Date-Time Value
   * @return true if variable stores meta-data for a Date-Time variable
   */
  public boolean isDateTime () {
    return mType == 0 && mDateTimeFormat != null && ! mDateTimeFormat.equals("");
  }
  /**
   * Whether Column is a Factor Value
   * @return true if variable stores meta-data for a Factor variable. Note this 
   * data-type is only in the Rdata format
   */
  public boolean isFactor () {
    return false;
  }
  /**
   * 
   */
  public RList asRList () {
    /**
     * This List has a Special Format:
     * list(
     *      type = NUMBER,
     *      class = CHARACTER-VECTOR,
     *      levels = CHARACTER-VECTOR
     *      )
     */
    return new RList() {{
      put("type", mType);
      put("class", null);
      put("levels", mFactorLevels);
    }};
  }
  /**
   * Set the Date-Time Format for a Column of Data
   * @param format a string specifying the date-time format of the column. This
   * isn't always reliable.
   */
  public void setDateTimeFormat (String format) {
    mDateTimeFormat = format;
  }
  public void setFactorLevels (String [] levels) {
    mFactorLevels = levels;
  }
}