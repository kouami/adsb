/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adsb;

/**
 *
 * @author U329022
 */
public class ADSBDirtyTests {

    //http://adsb-decode-guide.readthedocs.org/en/latest/index.html
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        System.out.println(ADSBUtils.hexToBinary("8D4840D6202CC371C32CE0576098"));
        //System.out.println(ADSBUtils.hexToBinary("0x80"));
        System.out.println(ADSBUtils.binaryToInteger("10001"));
        System.out.println(ADSBUtils.binaryToInteger("0010"));
        
        String message = ADSBUtils.hexToBinary("8D4840D6202CC371C32CE0576098");
        System.out.println("DF :: " + ADSBMessageUtils.getDF(message));
        System.out.println("CA :: " + ADSBMessageUtils.getCA(message));
        System.out.println("ICA024 :: " + ADSBMessageUtils.getICAO24(message));
        System.out.println("Data Frame :: " + ADSBMessageUtils.getDataFrame(message));
        System.out.println("Parity Check :: " + ADSBMessageUtils.getPC(message));
        System.out.println("Type Code :: " + ADSBMessageUtils.getTypecode(message));
        System.out.println("Aircraft Identifier :: " + ADSBMessageUtils.getAircraftIdentifier(message));
    }
    
}
