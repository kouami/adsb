/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adsb;

import java.math.BigInteger;

/**
 *
 * @author U329022
 */
public class ADSBUtils {

    /**
     *
     * @param s
     * @return
     */
    public static String hexToBinary(String s) {
        return new BigInteger(s, 16).toString(2);
    }

    /**
     *
     * @param s
     * @return
     */
    public static int binaryToInteger(String s) {
        return Integer.parseInt(s, 2);
    }

}
