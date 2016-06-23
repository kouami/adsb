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
    
    //https://developer.android.com/guide/topics/connectivity/usb/host.html
    //http://mobilemerit.com/android-app-for-usb-host-with-source-code/
    //https://gist.github.com/archeg/8333021   // read from usb devices

    //http://adsb-decode-guide.readthedocs.org/en/latest/index.html
    
    //https://www.sussex.ac.uk/webteam/gateway/file.php?name=coote-proj.pdf&site=20
    
    //https://github.com/junzis/pyModeS/blob/master/pyModeS/adsb.py
    
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
        
        String message2 = ADSBUtils.hexToBinary("8D40621D58C382D690C8AC2863A7");
        System.out.println("Is Frame Even ? " + ADSBMessageUtils.isEven(message2));
        System.out.println("LATCPR " + ADSBMessageUtils.getLatitudeCPR(message2));
        System.out.println("LONGCPR " + ADSBMessageUtils.getLongitudeCPR(message2));
        
        String message3 = ADSBUtils.hexToBinary("8D40621D58C386435CC412692AD6");
        System.out.println("Is Frame Even ? " + ADSBMessageUtils.isEven(message3));
        System.out.println("LATCPR " + ADSBMessageUtils.getLatitudeCPR(message3));
        System.out.println("LONGCPR " + ADSBMessageUtils.getLongitudeCPR(message3));
        System.out.println("Latitude Index " + ADSBMessageUtils.getLatitudeIndex(message2, message3));
        
        //System.out.println("Get Altitude " + ADSBMessageUtils.getAltitude(message2, message3));
        
        System.out.println("Get Position " + ADSBMessageUtils.getCPR2Position(message2, message3));
        
        String message4 = ADSBUtils.hexToBinary("8D485020994409940838175B284F");
        System.out.println("Get Velocity " + ADSBMessageUtils.getVelocity(message4));
        
        String message5 = ADSBUtils.hexToBinary("8DA05F219B06B6AF189400CBC33F");
        System.out.println("Get Velocity " + ADSBMessageUtils.getVelocity(message5));
        
        
    }
    
}
