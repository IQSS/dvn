/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.text;

/**
 *
 * @author ekraffmiller
 */
public class MethodPoint {
  String methodName;
  int numberOfClusters;
  double xCoord;
  double yCoord;

  MethodPoint(double x, double y, String name) {
    methodName = name;
    xCoord = x;
    yCoord = y;
 
  }

}
