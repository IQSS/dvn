/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.text;

/**
 *
 * @author ekraffmiller
 */
public class WordValue implements Comparable<WordValue>{

  // the difference of means between inCluster/outCluster
  protected float value;
  
  // the word itself
  protected String title;



    WordValue(String wordName,float diffValue ) {
   
   value = diffValue;
   title = wordName;
   
  }
  /*
   * This is descending order by value
   */
  public int compareTo(WordValue wv) {
      if (this.value< wv.value) {
          return 1;
      } else if (this.value > wv.value) {
          return -1;
      } else {
          return 0;
      }
  }

  public String toString() {
      return "WordList: "+title+", "+value;
  }
}