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
  double xCoord;
  double yCoord;

  MethodPoint(double x, double y, String name) {
    methodName = name;
    xCoord = x;
    yCoord = y;
  //  int rowIndex = methodPointsTable.getRowIndex(name); //Grab the Row Index for each Method Name
  //  xCoord = float(methodPointsTable.getString(rowIndex, 0)); //Draw in each Point from the Methods Table
  //  yCoord = float(methodPointsTable.getString(rowIndex, 1));
  //  xCoord = map(xCoord, methodPointsTable.getColumnMin(0), methodPointsTable.getColumnMax(0), 0, methodScale); //Map the Coordinates to a 2-D space of size methodScale by methodScale
  //  yCoord = map(yCoord, methodPointsTable.getColumnMin(1), methodPointsTable.getColumnMax(1), 0, methodScale);
  }

}
