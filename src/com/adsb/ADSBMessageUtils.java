/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adsb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author U329022
 */
public class ADSBMessageUtils {

    private static int ADSB_MESSAGE_LENGTH = 112;
    /**
     * The type of the message can be identified by checking its Downlink Format
     * (DF), bit 1 to 5. For ADS-B message, we need: DF = 17 (in decimal), or
     * 10001 (in binary)
     */
    public static final int DOWNLINK_FORMAT_LENGTH = 5;   // Downlink Format

    public static final int MESSAGE_SUB_TYPE_LENGTH = 3;  // Message Subtype

    public static final int ICAO_AIRCRAFT_ADDRESS = 24;   // ICAO aircraft address

    public static final int DATA_FRAME_LENTGH = 56;       //Data frame

    public static final int PARITY_CHECK_LENGTH = 24; // Parity Check

    /**
     * Within the data frame, another import value is the Type Code. it tells
     * what is inside of the data frame, it is located from bit 33 to 37 (5
     * bits)
     */
    public static final int DATA_FRAME_TYPE_CODE_LENTGH = 5;

    private static final Map<Integer, String> CODE2ID = new HashMap<>();
    private static final String CHARSET = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ#####_###############0123456789######";
    
    private static double latCPREven = 0.0;
    private static double latCPROdd = 0.0;

    static {

        for (int index = 0; index < CHARSET.length(); index++) {
            if (!"#".equals(Character.toString(CHARSET.charAt(index)))) {
                CODE2ID.put(new Integer(index), Character.toString(CHARSET.charAt(index)));
            }
        }
        System.out.println(CODE2ID.toString());
    }

    public static String getTypecode(String data) {
        String message = null;
        if (ensureDataSize(data)) {
            int offsetStart = DOWNLINK_FORMAT_LENGTH + MESSAGE_SUB_TYPE_LENGTH + ICAO_AIRCRAFT_ADDRESS + 1;
            //message = data.substring(offsetStart, 5);
            message = data.substring(32, 37);
        }
        return message;
    }

    /**
     *
     * @param data
     * @return
     */
    private static boolean ensureDataSize(String data) {
        if (data == null) {
            return false;
        }
        return data.length() == ADSB_MESSAGE_LENGTH;
    }

    /**
     *
     * @param data
     * @return
     */
    public static String getDF(String data) {
        String message = null;
        if (ensureDataSize(data)) {
            message = data.substring(0, 5);
        }
        return message;
    }

    /**
     *
     * @param data
     * @return
     */
    public static String getCA(String data) {
        String message = null;
        if (ensureDataSize(data)) {
            message = data.substring(5, 8);
        }
        return message;
    }

    /**
     *
     * @param data
     * @return
     */
    public static String getICAO24(String data) {
        String message = null;
        if (ensureDataSize(data)) {
            int offsetStart = DOWNLINK_FORMAT_LENGTH + MESSAGE_SUB_TYPE_LENGTH + 1;
            //message = data.substring(offsetStart, ICAO_AIRCRAFT_ADDRESS);
            message = data.substring(8, 32);
        }
        return message;
    }

    /**
     *
     * @param data
     * @return
     */
    public static String getDataFrame(String data) {
        String message = null;
        if (ensureDataSize(data)) {
            int offsetStart = DOWNLINK_FORMAT_LENGTH + MESSAGE_SUB_TYPE_LENGTH + ICAO_AIRCRAFT_ADDRESS + 1;
            //message = data.substring(offsetStart, DATA_FRAME_LENTGH);
            message = data.substring(32, 88);
        }
        return message;
    }

    /**
     *
     * @param data
     * @return
     */
    public static String getPC(String data) {
        String message = null;
        if (ensureDataSize(data)) {
            int offsetStart = DOWNLINK_FORMAT_LENGTH + MESSAGE_SUB_TYPE_LENGTH + ICAO_AIRCRAFT_ADDRESS + PARITY_CHECK_LENGTH + 1;
            //message = data.substring(offsetStart, PARITY_CHECK_LENGTH);
            message = data.substring(88, 112);
        }
        return message;
    }

    private static List<String> splitEqually(String text, int size) {
        // Give the list the right capacity to start with. You could use an array
        // instead if you wanted.
        List<String> ret = new ArrayList<String>((text.length() + size - 1) / size);

        for (int start = 0; start < text.length(); start += size) {
            ret.add(text.substring(start, Math.min(text.length(), start + size)));
        }
        return ret;
    }

    public static String getAircraftIdentifier(String data) {
        String dataFrame = getDataFrame(data);
        if (dataFrame != null) {
            // Skip the Type Code bits (5 bits ) and 3 more bits  
            dataFrame = dataFrame.substring(8);
        }

        List<String> ids = splitEqually(dataFrame, 6);
        StringBuilder aircraftId = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) {
            aircraftId.append(CODE2ID.get(ADSBUtils.binaryToInteger(ids.get(i))));
        }

        String callSign = aircraftId.toString().replaceAll("#", "");
        callSign = callSign.replaceAll("_", "");

        return callSign;
    }

    /**
     *
     * @param data1
     * @param data2
     */
    public static void getOddEvenFrame(String hex1, String hex2) {
        if (hex1 != null && hex2 == null) {
            String d1 = ADSBUtils.hexToBinary(hex1);
            String d2 = ADSBUtils.hexToBinary(hex2);
            //index 52
        }
    }

    /**
     *
     * @param bitString
     * @return
     */
    public static boolean isEven(String bitString) {
        return Character.toString(bitString.charAt(53)).equals("0");
    }

    /**
     * 
     * @param bitString
     * @return 
     */
    public static double getLatitudeCPR(String bitString) {
        double latCPR = ADSBUtils.binaryToInteger(bitString.substring(54, 71));
        //System.out.println("LAT CPR " + latCPR);
        
        return latCPR / 131072;
    }

    /**
     * 
     * @param bitString
     * @return 
     */
    public static double getLongitudeCPR(String bitString) {
        double longCPR = ADSBUtils.binaryToInteger(bitString.substring(71, 88));
        //System.out.println("LON CPR " + longCPR);
        return longCPR / 131072;
    }
    
    /**
     * 
     * @param bitString1
     * @param bitString2
     * @return 
     */
    public static double getLatitudeIndex(String bitString1, String bitString2) {
        
        double latitudeIndex = -1;
    
        
        if(isEven(bitString1)){
            setLatCPREven(getLatitudeCPR(bitString1));
            setLatCPROdd(getLatitudeCPR(bitString2));
        } else if (isEven(bitString2)){
            setLatCPREven(getLatitudeCPR(bitString2));
            setLatCPROdd(getLatitudeCPR(bitString1));
        }
        
        latitudeIndex = Math.floor((59 * getLatCPREven()) - (60 * getLatCPROdd()) + 0.5);
        return latitudeIndex;
    }
    
    public static double getLatitude(String bitString1, String bitString2){
        
        double DLat_EVEN = 360.0 / 60;
        double DLat_ODD  = 360.0 / 59;
        double latEven = 0.0;
        double latOdd = 0.0;
        
        double latIndex = getLatitudeIndex(bitString1, bitString2);
        //latEven = DLat_EVEN * (Math.mo)
        return 0.0;
    }

    /**
     * 
     * @param data
     * @return 
     */
    public static int getADSBMessageType(String data) {

        int df = ADSBUtils.binaryToInteger(getDF(data));
        int typeCode = ADSBUtils.binaryToInteger(getTypecode(data));

        if (df == 17) {
            switch (typeCode) {
                case 1:
                case 2:
                case 3:
                case 4:
                    //return getAircraftIdentifier()
                    break;
                case 5:
                case 6:
                case 7:
                case 8:
                    // return Surface Position
                    break;
                case 9:
                case 10:
                case 11:
                case 12:
                case 13:
                case 14:
                case 15:
                case 16:
                case 17:
                case 18:
                    // return Airborn position 9Baro Altitude)
                    break;
                case 19:
                    // return Airborne velocities
                    break;
                case 20:
                case 21:
                case 22:
                    // return Airborne position (GNSS Height)
                    break;
                case 23:
                    // return Test Message
                    break;
                case 24:
                    // return Surface system status
                    break;
                case 25:
                case 26:
                case 27:
                    // Reserved
                    break;
                case 28:
                    // Extended squitter AC status
                    break;
                case 29:
                    // Target state and status (V.2)
                    break;
                case 30:
                    // Reserved
                    break;
                case 31:
                    // Aircraft Operation status
                    break;

                default:

            }
        }
        return 0;
    }

    /**
     * @return the latCPREven
     */
    public static double getLatCPREven() {
        return latCPREven;
    }

    /**
     * @param aLatCPREven the latCPREven to set
     */
    public static void setLatCPREven(double aLatCPREven) {
        latCPREven = aLatCPREven;
    }

    /**
     * @return the latCPROdd
     */
    public static double getLatCPROdd() {
        return latCPROdd;
    }

    /**
     * @param aLatCPROdd the latCPROdd to set
     */
    public static void setLatCPROdd(double aLatCPROdd) {
        latCPROdd = aLatCPROdd;
    }
    
}
