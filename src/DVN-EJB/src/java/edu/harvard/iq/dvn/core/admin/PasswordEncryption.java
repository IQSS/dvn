/*
   Copyright (C) 2005-2012, by the President and Fellows of Harvard College.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

   Dataverse Network - A web application to share, preserve and analyze research data.
   Developed at the Institute for Quantitative Social Science, Harvard University.
   Version 3.0.
*/
/*
 * PasswordEncryption.java
 * 
 * Created on Aug 30, 2007, 12:28:01 PM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.admin;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.RandomStringUtils;


/**
 *
 * @author Ellen Kraffmiller
 */
public final class PasswordEncryption  implements java.io.Serializable 
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
  
  public static String generateRandomPassword() {
      return RandomStringUtils.randomAlphanumeric(8);
  }
  
  public static void main(String[] args) {
      for (int i=0;i<10;i++) {
        System.out.println("Random String-"+generateRandomPassword());
      }
  }
}
