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
    
    public static String getTypecode(String data){
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
            message = data.substring(0,5);
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
            message = data.substring(8,32);
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
    
    public static int getADSBMessageType(String data){
        
        int df = ADSBUtils.binaryToInteger(getDF(data));
        int typeCode = ADSBUtils.binaryToInteger(getTypecode(data));
        
        if(df == 17){
            switch(typeCode){
                case 1:
                case 2:
                case 3:
                case 4:
                    //return Aircraft Identification
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
}
