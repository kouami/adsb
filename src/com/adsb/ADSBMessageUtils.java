/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adsb;

import java.util.ArrayList;
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
    
    public static final int CPR_ODD_EVEN_FRAME_FLAG = 53; // CPR odd/even frame flag

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
    private static double lonCPREven = 0.0;
    private static double lonCPROdd = 0.0;

    private static int timeStampEven = 0;
    private static int timeStampOdd = 0;

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
            message = data.substring(0, DOWNLINK_FORMAT_LENGTH);
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
            message = data.substring(DOWNLINK_FORMAT_LENGTH, DOWNLINK_FORMAT_LENGTH + MESSAGE_SUB_TYPE_LENGTH);
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
            int offsetStart = DOWNLINK_FORMAT_LENGTH + MESSAGE_SUB_TYPE_LENGTH; // 8
            //message = data.substring(offsetStart, ICAO_AIRCRAFT_ADDRESS);
            message = data.substring(offsetStart, offsetStart + ICAO_AIRCRAFT_ADDRESS);
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
            int offsetStart = DOWNLINK_FORMAT_LENGTH + MESSAGE_SUB_TYPE_LENGTH + ICAO_AIRCRAFT_ADDRESS; // 32
            //message = data.substring(offsetStart, DATA_FRAME_LENTGH);
            message = data.substring(offsetStart, offsetStart + DATA_FRAME_LENTGH);
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
            int offsetStart = DOWNLINK_FORMAT_LENGTH + MESSAGE_SUB_TYPE_LENGTH + ICAO_AIRCRAFT_ADDRESS + DATA_FRAME_LENTGH; // 88
            //message = data.substring(offsetStart, PARITY_CHECK_LENGTH);
            message = data.substring(offsetStart, offsetStart + PARITY_CHECK_LENGTH);
        }
        return message;
    }

    private static List<String> splitEqually(String text, int size) {
        // Give the list the right capacity to start with. You could use an array
        // instead if you wanted.
        List<String> ret = new ArrayList<>((text.length() + size - 1) / size);

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
        for (String id : ids) {
            aircraftId.append(CODE2ID.get(ADSBUtils.binaryToInteger(id)));
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
        return Character.toString(bitString.charAt(CPR_ODD_EVEN_FRAME_FLAG)).equals("0");
    }

    /**
     *
     * @param bitString
     * @return
     */
    public static double getLatitudeCPR(String bitString) {
        double latCPR = ADSBUtils.binaryToInteger(bitString.substring(54, 71));
        return latCPR / 131072;
    }

    /**
     *
     * @param bitString
     * @return
     */
    public static double getLongitudeCPR(String bitString) {
        double longCPR = ADSBUtils.binaryToInteger(bitString.substring(71, 88));
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

        if (isEven(bitString1)) {
            setLatCPREven(getLatitudeCPR(bitString1));
            setLatCPROdd(getLatitudeCPR(bitString2));
            setLonCPREven(getLongitudeCPR(bitString1));
            setLonCPROdd(getLongitudeCPR(bitString2));
            setTimeStampEven(ADSBUtils.binaryToInteger(Character.toString(bitString1.charAt(52))));
            setTimeStampOdd(ADSBUtils.binaryToInteger(Character.toString(bitString2.charAt(52))));
        } else if (isEven(bitString2)) {
            setLatCPREven(getLatitudeCPR(bitString2));
            setLatCPROdd(getLatitudeCPR(bitString1));
            setLonCPREven(getLongitudeCPR(bitString2));
            setLonCPROdd(getLongitudeCPR(bitString1));
            setTimeStampEven(ADSBUtils.binaryToInteger(Character.toString(bitString2.charAt(52))));
            setTimeStampOdd(ADSBUtils.binaryToInteger(Character.toString(bitString1.charAt(52))));
        }

        latitudeIndex = Math.floor((59 * getLatCPREven()) - (60 * getLatCPROdd()) + 0.5);
        return latitudeIndex;
    }

    public static Map getCPR2Position(String bitString1, String bitString2) {

        double DLat_EVEN = 360.0 / 60;
        double DLat_ODD = 360.0 / 59;
        double latEven = 0.0;
        double latOdd = 0.0;
        double ni = 0.0;
        double m = 0.0;

        double latitude = 0.0;
        double longitude = 0.0;

        Map<String, Double> position = new HashMap<>();

        double latIndex = getLatitudeIndex(bitString1, bitString2);
        latEven = DLat_EVEN * ((latIndex % 60) + getLatCPREven());
        if (latEven >= 270) {
            latEven = latEven - 360;
        }

        latOdd = DLat_ODD * ((latIndex % 59) + getLatCPROdd());
        if (latOdd >= 270) {
            latOdd = latOdd - 360;
        }

        // check if both are in the same latitude zone, exit if not
        if (cprNL(latEven) != cprNL(latOdd)) {
            return null;
        }

        //compute ni, longitude index m, and longitude
        if (getTimeStampEven() > getTimeStampOdd()) {

            ni = cprN(latEven, 0);
            m = Math.floor(getLonCPREven() * (cprNL(latEven) - 1) - getLonCPROdd() * cprNL(latEven) + 0.5);
            longitude = (360.0 / ni) * (m % ni + getLonCPREven());
            latitude = latEven;

        } else {

            ni = cprN(latOdd, 1);
            m = Math.floor(getLonCPREven() * (cprNL(latOdd) - 1) - getLonCPROdd() * cprNL(latOdd) + 0.5);
            longitude = (360.0 / ni) * (m % ni + getLonCPROdd());
            latitude = latOdd;

        }

        if (longitude > 180) {
            longitude = longitude - 360;
        }

        // Round them off to 5 decimal place
        latitude = (double) Math.round(latitude * 100000) / 100000;
        longitude = (double) Math.round(longitude * 100000) / 100000;

        position.put("LAT", latitude);
        position.put("LON", longitude);
        position.put("ALT", getAltitude(bitString1, bitString2));

        System.out.println("LAT ::: " + position.get("LAT"));
        System.out.println("LON ::: " + position.get("LON"));
        System.out.println("ALT ::: " + position.get("ALT"));

        return position;
    }

    private static int cprNL(double latitude) {

        if (latitude != 90 || latitude != -90) {
            int nz = 60;
            double a = 1 - Math.cos(Math.PI * 2 / nz);
            double b = Math.pow(Math.cos(Math.PI / 180.0 * Math.abs(latitude)), 2);
            double nl = 2 * Math.PI / (Math.acos(1 - a / b));
            return (int) nl;
        }

        return 1;
    }

    private static double cprN(double latitude, double isOdd) {

        double nl = cprNL(latitude) - isOdd;
        if (nl > 1) {
            return nl;
        }
        return 1;
    }

    public static double getAltitude(String bitString1, String bitString2) {
        double altitude = 0.0;
        String altitudeFrameBits = null;
        String qBit = "";
        if (bitString1 != null && bitString2 != null && bitString1.length() == bitString2.length()) {
            //altitude = ADSBUtils.binaryToInteger(bitString1.substring(40, 52));
            qBit = Character.toString(bitString1.charAt(47));
            if (qBit != null && qBit.equals("1")) {
                altitudeFrameBits = bitString1.substring(40, 47).concat(bitString1.substring(48, 52));
            } else {
                return 0.0;
            }
            altitude = (ADSBUtils.binaryToInteger(altitudeFrameBits) * 25) - 1000;

        }
        return altitude;
    }

    public static Map<String, Object> getVelocity(String bitString) {

        int subType = ADSBUtils.binaryToInteger(bitString.substring(37, 40));

        int east_west_dir = -1;
        int velocityEW = -1;
        int north_south_dir = -1;
        int velocityNS = -1;
        int actualVelocity = -1;
        double heading = 0.0;
        int verticalRateSign = -1;
        int verticalRate = -1;
        int rateOfClimbAndDescent = -1;

        Map<String, Object> velocityAndHeading = new HashMap<>();;

        if (subType == 1 || subType == 2) { // We are dealing with a subsonic plane

            east_west_dir = ADSBUtils.binaryToInteger(Character.toString(bitString.charAt(45)));
            int velocityRawValueEW = ADSBUtils.binaryToInteger(bitString.substring(46, 56));

            north_south_dir = ADSBUtils.binaryToInteger(Character.toString(bitString.charAt(56)));
            int velocityRawValueNS = ADSBUtils.binaryToInteger(bitString.substring(57, 67));

            if (east_west_dir == 1) { //Flying East to West
                velocityEW = -1 * (velocityRawValueEW - 1);
            } else { //flying West to East
                velocityEW = velocityRawValueEW - 1;
            }

            if (north_south_dir == 1) { //Flying North to South
                velocityNS = -1 * (velocityRawValueNS - 1);
            } else { //Flying South to North
                velocityNS = velocityRawValueNS - 1;
            }

            actualVelocity = (int) Math.sqrt((velocityEW * velocityEW) + (velocityNS * velocityNS));
            heading = Math.atan2(velocityEW, velocityNS) * (360 / (2 * Math.PI));

            velocityAndHeading.put("TAG", "Ground Speed");

        } else {

            heading = ADSBUtils.binaryToInteger(bitString.substring(46, 56)) * 360 / 1024.0;
            actualVelocity = ADSBUtils.binaryToInteger(bitString.substring(57, 67));
            velocityAndHeading.put("TAG", "Airspeed");
        }

        verticalRateSign = ADSBUtils.binaryToInteger(Character.toString(bitString.charAt(68)));
        verticalRate = ADSBUtils.binaryToInteger(bitString.substring(69, 78));

        if (verticalRateSign == 1) {
            rateOfClimbAndDescent = -1 * verticalRate;
        } else {
            rateOfClimbAndDescent = verticalRate;
        }

        if (heading < 0) {
            heading += 360;
        }
        // round it up
        heading = (double) Math.round(heading * 10) / 10;

        velocityAndHeading.put("VERTICAL_RATE", verticalRate);
        velocityAndHeading.put("RATE_OF_CLIMB_DESCENT", rateOfClimbAndDescent);
        velocityAndHeading.put("VEL", actualVelocity);
        velocityAndHeading.put("HEADING", heading);

        return velocityAndHeading;
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
                    // return getAltitude()
                    break;
                case 19:
                    // return getVelocity()
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

    /**
     * @return the timeStampEven
     */
    public static int getTimeStampEven() {
        return timeStampEven;
    }

    /**
     * @param aTimeStampEven the timeStampEven to set
     */
    public static void setTimeStampEven(int aTimeStampEven) {
        timeStampEven = aTimeStampEven;
    }

    /**
     * @return the timeStampOdd
     */
    public static int getTimeStampOdd() {
        return timeStampOdd;
    }

    /**
     * @param aTimeStampOdd the timeStampOdd to set
     */
    public static void setTimeStampOdd(int aTimeStampOdd) {
        timeStampOdd = aTimeStampOdd;
    }

    /**
     * @return the lonCPREven
     */
    public static double getLonCPREven() {
        return lonCPREven;
    }

    /**
     * @param aLonCPREven the lonCPREven to set
     */
    public static void setLonCPREven(double aLonCPREven) {
        lonCPREven = aLonCPREven;
    }

    /**
     * @return the lonCPROdd
     */
    public static double getLonCPROdd() {
        return lonCPROdd;
    }

    /**
     * @param aLonCPROdd the lonCPROdd to set
     */
    public static void setLonCPROdd(double aLonCPROdd) {
        lonCPROdd = aLonCPROdd;
    }

}
