/*
 * PasswordEncryption.java
 * 
 * Created on Aug 30, 2007, 12:28:01 PM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.admin;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Base64;


/**
 *
 * @author Ellen Kraffmiller
 */
public final class PasswordEncryption
{
  private static PasswordEncryption instance;

  private PasswordEncryption()
  {
  }

   public synchronized String encrypt(String plaintext) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA"); //step 2
        } catch (NoSuchAlgorithmException e) {

            throw new RuntimeException(e);
        }

        try {
            md.update(plaintext.getBytes("UTF-8")); //step 3
        } catch (UnsupportedEncodingException e) {

            throw new RuntimeException(e);
        }
        byte[] raw = md.digest(); //step 4
        try {
            String hash = new String(new Base64().encode(raw), "ASCII"); //step 5
            return hash; //step 6
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
  
  public static synchronized PasswordEncryption getInstance() //step 1
  {
    if(instance == null)
    {
       instance = new PasswordEncryption(); 
    } 
    return instance;
  }
}
