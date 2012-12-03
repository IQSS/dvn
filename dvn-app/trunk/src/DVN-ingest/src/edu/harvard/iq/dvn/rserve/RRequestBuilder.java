/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.rserve;

/**
 *
 * @author matt
 */
public class RRequestBuilder {
  private String mHost, mUser, mPassword, mScript;
  private int mPort;
  
  /*
   * Construct a ZeligRequestBuilder Object
   * @return an object ready to be evaluated
   */
  public RRequestBuilder () {
    mHost = mUser = mPassword = "";
    mPort = -1;
  }
  /*
   * Add a Host Name to the Request
   */
  public RRequestBuilder host (String host) {
    mHost = host;
    return this;
  }
  /*
   * Add a Username to the Request
   */
  public RRequestBuilder user (String user) {
    mUser = user;
    return this;
  }
  /*
   * Add a Password to the Request
   */
  public RRequestBuilder password (String password) {
    mPassword = password;
    return this;
  }
  /*
   * Add a Port Number to the
   */
  public RRequestBuilder port (int port) {
    mPort = port;
    return this;
  }
  /*
   * Set the String to be Evaluated by the R Server
   */
  public RRequestBuilder script (String script) {
    mScript = script;
    return this;
  }
  /*
   * Build
   */
  public RRequest build () {
    return new RRequest(mHost, mPort, mUser, mPassword, mScript);
  }
}