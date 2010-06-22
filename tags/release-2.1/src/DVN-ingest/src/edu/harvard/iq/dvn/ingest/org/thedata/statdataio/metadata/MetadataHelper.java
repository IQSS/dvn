/*
 * Dataverse Network - A web application to distribute, share and
 * analyze quantitative data.
 * Copyright (C) 2009
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 *  along with this program; if not, see http://www.gnu.org/licenses
 * or write to the Free Software Foundation,Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package edu.harvard.iq.dvn.ingest.org.thedata.statdataio.metadata;

import static java.lang.System.*;
import java.util.*;
import java.util.logging.*;


/**
 * A helper class that combines value-label data in a data file with 
 * missing-value data and actual tabulation results and creates a 
 * unified value-label table.
 * 
 * @author Akio Sone
 */
public class MetadataHelper {

   private static Logger dbgLog =
       Logger.getLogger(MetadataHelper.class.getPackage().getName());

    /**
     * Returns the merged results from the given value-label data, 
     * missing-value data, and tabulation results.
     * 
     * @param valueLabeli a given variable's value-label table recorded
     * in the given data file.
     *
     * @param catStati a given variable's frequency table.
     *
     * @param missingValuei a given variabl's missing-value data.
     *
     * @return a unified value-label table as a <code>List</code> of 
     * <code>CategoricalStatistic</code> objects.
     */
    public static List<CategoricalStatistic> getMergedResult(
        Map<String, String> valueLabeli,
        Map<String, Integer> catStati,
        List<String> missingValuei){

        // protection block
        if (missingValuei == null){
            missingValuei = new ArrayList<String>();
        }
//        if (valueLabeli == null){
//
//        }
        //
        int caseTypeNumber = 0;
        Set<String> catStatiKeys = null;
        Set<String>valueLabeliKeys = null;
        Set<String> mvs = new TreeSet(missingValuei);
        dbgLog.finer("mvs="+mvs);
        if ((valueLabeli == null)&& (catStati ==null)){
            return null;
        } else if ((valueLabeli != null)&& (catStati !=null)){
            // create duplicates
            catStatiKeys= new TreeSet(catStati.keySet());
            valueLabeliKeys= new TreeSet(valueLabeli.keySet());


            dbgLog.finer("catStatiKeys="+catStatiKeys);
            dbgLog.finer("valueLabeliKeys="+valueLabeliKeys);
            // get the set-operation case number
            caseTypeNumber = getRelationsBetweenTwoSets(catStatiKeys, valueLabeliKeys);
            dbgLog.finer("caseTypeNumber="+caseTypeNumber +"\n\n");
        } else if (valueLabeli == null){
            catStatiKeys= new TreeSet(catStati.keySet());
            caseTypeNumber = 6;

        } else if (catStati == null){
            valueLabeliKeys= new TreeSet(valueLabeli.keySet());
             caseTypeNumber = 7;
        }

        // merged result
        List<CategoricalStatistic> merged = new ArrayList<CategoricalStatistic>();
        Set<String> included = new TreeSet<String>();


        switch (caseTypeNumber){
            case 1:
                dbgLog.finer("case 1: no intersection");
                // V side
                for (String kv: valueLabeliKeys){

                    CategoricalStatistic cs = new CategoricalStatistic();
                    cs.setValue(kv);
                    cs.setLabel(valueLabeli.get(kv));
                    cs.setFrequency(0);

                    if (mvs.contains(kv)){
                        cs.setMissingValue(true);
                        included.add(kv);
                    }
                    merged.add(cs);
                }
                // C side
                for (String kv:catStatiKeys){
                    CategoricalStatistic cs = new CategoricalStatistic();
                    cs.setValue(kv);
                    cs.setLabel(null);
                    cs.setFrequency(catStati.get(kv));
                    if (mvs.contains(kv)){
                        cs.setMissingValue(true);
                        included.add(kv);
                    }
                    merged.add(cs);
                }
                dbgLog.finer("case 1: merged="+merged);
                break;
            case 2:
                dbgLog.finer("case 2: C intersect with V");
                // C & V part
                dbgLog.finer("C(initial)="+catStatiKeys);
                dbgLog.finer(catStati.values().toString());
                Set<String>diffKeysCat = new TreeSet(catStatiKeys);
                catStatiKeys.retainAll(valueLabeliKeys);
                dbgLog.finer("C and V="+catStatiKeys);
                for (String kv:catStatiKeys){
                    CategoricalStatistic cs = new CategoricalStatistic();
                    cs.setValue(kv);
                    cs.setLabel(valueLabeli.get(kv));
                    cs.setFrequency(catStati.get(kv));
                    if (mvs.contains(kv)){
                        cs.setMissingValue(true);
                        included.add(kv);
                    }
                    merged.add(cs);
                }
                dbgLog.finer(catStati.values().toString());
                dbgLog.finer("merged(C & V only)="+merged);
                // V-only part
                valueLabeliKeys.removeAll(catStatiKeys);
                dbgLog.finer("V only="+valueLabeliKeys);
                for (String kv: valueLabeliKeys){
                    CategoricalStatistic cs = new CategoricalStatistic();
                    cs.setValue(kv);
                    cs.setLabel(valueLabeli.get(kv));
                    cs.setFrequency(0);
                    if (mvs.contains(kv)){
                        cs.setMissingValue(true);
                        included.add(kv);
                    }
                    merged.add(cs);
                }
                dbgLog.finer("merged(V-only)="+merged);
                dbgLog.finer(catStati.values().toString());
                // C-only part
                diffKeysCat.removeAll(catStatiKeys);
                dbgLog.finer("C only="+diffKeysCat);
                dbgLog.finer(catStati.values().toString());
                for (String kv: diffKeysCat){
                    CategoricalStatistic cs = new CategoricalStatistic();
                    cs.setValue(kv);
                    cs.setLabel(null);
                    cs.setFrequency(catStati.get(kv));
                    if (mvs.contains(kv)){
                        cs.setMissingValue(true);
                        included.add(kv);
                    }
                    merged.add(cs);
                }
                dbgLog.finer("case 2: merged="+merged);
                break;
            case 3:
                dbgLog.finer("case 3: V inclues C");
                // V only part
                Set<String>diffKeysV = new TreeSet(valueLabeliKeys);
                diffKeysV.removeAll(catStatiKeys);
                for (String kv: diffKeysV){
                    CategoricalStatistic cs = new CategoricalStatistic();
                    cs.setValue(kv);
                    cs.setLabel(valueLabeli.get(kv));
                    cs.setFrequency(0);
                    if (mvs.contains(kv)){
                        cs.setMissingValue(true);
                        included.add(kv);
                    }
                    merged.add(cs);
                }
                dbgLog.finer("merged(V-only)="+merged);
                // V & C part (== C)
                for (String kv:catStatiKeys){
                    CategoricalStatistic cs = new CategoricalStatistic();
                    cs.setValue(kv);
                    cs.setLabel(valueLabeli.get(kv));
                    cs.setFrequency(catStati.get(kv));
                    if (mvs.contains(kv)){
                        cs.setMissingValue(true);
                        included.add(kv);
                    }

                    merged.add(cs);
                }
                dbgLog.finer("\ncase 3: merged="+merged);
                break;
            case 4:
                dbgLog.finer("case 4: C includes V");
                // C only part
                Set<String>diffKeysC = new TreeSet(catStatiKeys);
                diffKeysC.removeAll(valueLabeliKeys);
                for (String kv: diffKeysC){
                    CategoricalStatistic cs = new CategoricalStatistic();
                    cs.setValue(kv);
                    cs.setLabel(null);
                    cs.setFrequency(catStati.get(kv));
                    if (mvs.contains(kv)){
                        cs.setMissingValue(true);
                        included.add(kv);
                    }
                    merged.add(cs);
                }
                dbgLog.finer("merged(C-only)="+merged);
                // C & V part (== V)
                for (String kv:valueLabeliKeys){
                    CategoricalStatistic cs = new CategoricalStatistic();
                    cs.setValue(kv);
                    cs.setLabel(valueLabeli.get(kv));
                    cs.setFrequency(catStati.get(kv));
                    if (mvs.contains(kv)){
                        cs.setMissingValue(true);
                        included.add(kv);
                    }
                    merged.add(cs);
                }
                dbgLog.finer("\ncase 4: merged="+merged);
                break;
            case 5:
                dbgLog.finer("case 5: C == V");

                // V side
                for (String kv: valueLabeliKeys){

                    CategoricalStatistic cs = new CategoricalStatistic();
                    cs.setValue(kv);
                    cs.setLabel(valueLabeli.get(kv));
                    cs.setFrequency(catStati.get(kv));

                    if (mvs.contains(kv)){
                        cs.setMissingValue(true);
                        included.add(kv);
                    }
                    merged.add(cs);
                }

                dbgLog.finer("case 5: merged="+merged);
                break;
            case 6:
                // V is null == C only case
                dbgLog.finer("case 6: V is null");
                // C side
                for (String kv:catStatiKeys){
                    CategoricalStatistic cs = new CategoricalStatistic();
                    cs.setValue(kv);
                    cs.setLabel(null);
                    cs.setFrequency(catStati.get(kv));
                    if (mvs.contains(kv)){
                        cs.setMissingValue(true);
                        included.add(kv);
                    }

                    merged.add(cs);
                }
                break;
            case 7:
                // C is null == V only case
                dbgLog.finer("case 7: C is null");
                // V side
                for (String kv: valueLabeliKeys){

                    CategoricalStatistic cs = new CategoricalStatistic();
                    cs.setValue(kv);
                    cs.setLabel(valueLabeli.get(kv));
                    cs.setFrequency(0);

                    if (mvs.contains(kv)){
                        cs.setMissingValue(true);
                        included.add(kv);
                    }
                    merged.add(cs);
                }
                break;
            default:

        }  // end of switch
        // missing values

        mvs.removeAll(included);
        dbgLog.finer("not called missing values:"+mvs);
        if (!mvs.isEmpty()){
            for (String mv: mvs){
                CategoricalStatistic csmv = new CategoricalStatistic();
                csmv.setValue(mv);
                csmv.setLabel(null);
                csmv.setFrequency(0);
                csmv.setMissingValue(true);
                merged.add(csmv);
            }
        }

        dbgLog.finer("merged"+merged);
        return merged;
    }





    /**
     * Returns the relationship between the recorded value-label table
     * and actual tabulation in <code>integer</code> ranging from 0 to 7.
     * 
     * @param setC  the set of values of the tallied frequency table
     *
     * @param setV  the set of values of the value-label table recoded in the
     * data file
     * 
     * @return an <code>integer</code> value between 0 and 7.
     */
    public static int getRelationsBetweenTwoSets(Set<String> setC, Set<String> setV){
        int relation = 0;
        Set<String> newC = new TreeSet<String>(setC);
        Set<String> newV = new TreeSet<String>(setV);
        dbgLog.finer("newC:before="+newC);
        dbgLog.finer("newV:before="+newV);
        // check the intersection
        boolean Rintersection = newC.retainAll(newV);
        // if no intersection case, newC becomes empty
        dbgLog.finer("newC:after="+newC);
        dbgLog.finer("newV:after="+newV);
        if (newC.size()==0){
            // no intersection element: case #1
            dbgLog.finer("no intersections between the two: case #1");
            dbgLog.finer("newC="+newC);
            dbgLog.finer("newV="+newV);
            relation = 1;
        } else {
            // some intersection exists
            dbgLog.finer("some intersections between the two sets");
            dbgLog.finer("difference=("+ (setC.size() - newC.size())+") newC="+newC);
            if (newC.containsAll(newV) && newC.containsAll(setC)){
                // here newC is the intersection; SetC is used because newC is modified
                dbgLog.finer(" C == V: case #5 :");
                dbgLog.finer("newC="+newC);
                relation=5;
            } else {
                dbgLog.finer(" C != V case:");
                dbgLog.finer("C contains V or V contains C or have the intersection");
                dbgLog.finer("setC="+setC);
                dbgLog.finer("newV="+newV);
                if (setC.containsAll(newV)){
                    dbgLog.finer("newC contains newV: case # 4");
                    dbgLog.finer("setC="+setC);
                    dbgLog.finer("newV="+newV);
                    relation = 4;
                } else if (newV.containsAll(setC)){
                    dbgLog.finer("newV contains newC: case # 3");
                    dbgLog.finer("newV="+newV);
                    dbgLog.finer("setC="+setC);
                    relation = 3;
                } else {
                    Set<String> setC2 = new TreeSet<String>(setC);
                    setC2.removeAll(newC);
                    newV.removeAll(newC);
                    if (!setC2.isEmpty() && !newV.isEmpty()){
                        dbgLog.finer("C and V partially intersect: case #2");
                        dbgLog.finer("setC="+setC2);
                        dbgLog.finer("newV="+newV);
                        relation = 2;
                    }

                }
            }
        }

        dbgLog.fine("relation="+relation);

        return relation;
    }

}

