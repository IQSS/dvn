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
