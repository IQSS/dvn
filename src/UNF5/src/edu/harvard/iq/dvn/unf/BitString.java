/*
 * Dataverse Network - A web application to distribute, share and
 * analyze quantitative data.
 * Copyright (C) 2008
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

package edu.harvard.iq.dvn.unf;

/**
 *
 * @author roberttreacy
 */
public class BitString implements CharSequence{

    private String bits;

    public BitString(){
        
    }

    public BitString(String bitString){
        if (validate(bitString)){
            setBits(bitString);
        }
    }

    public BitString(Long l){
        setBits(Long.toBinaryString(l));
    }

    public void setBits(Long l){
        setBits(Long.toBinaryString(l));
    }

    private boolean validate(String bitstring){
        boolean ok = true;
        for (int i=0;i<bitstring.length(); i++){
            if (!(bitstring.charAt(i)=='0' || bitstring.charAt(i)=='1')){
                ok = false;
                break;
            }
        }
        return ok;
    }

    private void alignToByteBoundary(){
        if (bits != null){
            int padding = 8 - (bits.length() % 8);
            if (padding > 0){
                StringBuilder bitsBuilder = new StringBuilder(bits);
                for (int i = 0; i < padding; i++){
                    bitsBuilder.insert(0, '0');
                }
                bits = bitsBuilder.toString();
            }
        }
    }

    private void truncateLeadingEmptyBits(){
        bits = bits.substring(bits.indexOf('1'));
    }

    private void normalize(){
        truncateLeadingEmptyBits();
        alignToByteBoundary();
    }

    @Override
    public int length() {
        return bits.length();
    }

    @Override
    public char charAt(int index) {
        return bits.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return bits.subSequence(start, end);
    }

    /**
     * @return the bits
     */
    public String getBits() {
        return bits;
    }

    /**
     * @param bits the bits to set
     */
    public void setBits(String bits) {
        if (validate(bits)) {
            this.bits = bits;
            normalize();
        }
    }
}
