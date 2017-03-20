package com.ruurdbijlsma.camera.Converters;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * Gemaakt door ruurd op 20-3-2017.
 */

public class ExposureTimeConverter {
    public static LinkedHashMap exposureTimeFractions = new LinkedHashMap() {{
        put("1/6000", 0.000166666667);
        put("1/4000", 0.00025);
        put("1/2000", 0.0005);
        put("1/1000", 0.001);
        put("1/500", 0.002);
        put("1/250", 0.004);
        put("1/125", 0.008);
        put("1/60", 0.0166666667);
        put("1/30", 0.0333333333);
        put("1/15", 0.0666666667);
        put("1/8", 0.125);
        put("1/4", 0.25);
        put("1/2", 0.5);
        put("1", 1);
        put("2", 2);
        put("4", 4);
        put("8", 8);
        put("15", 15);
        put("30", 30);
    }};

    private static double[] values;
    private static String[] keys;

    static {
        Collection c = exposureTimeFractions.values();
        values = new double[c.size()];
        int a = 0;
        for (Object value : c) {
            if (value instanceof Double) {
                values[a] = (double) value;
            } else if (value instanceof Integer) {
                values[a] = (double) (int) value;
            }
            a++;
        }

        Set keySet = exposureTimeFractions.keySet();
        keys = (String[]) keySet.toArray(new String[keySet.size()]);
    }

    public static String secondsToFraction(float seconds) {
        float nearest = Float.POSITIVE_INFINITY;
        int nearestIndex = -1;

        for (int i = 0; i < values.length; i++) {
            float difference = (float) Math.abs(seconds - values[i]);
            if (difference < nearest) {
                nearest = difference;
                nearestIndex = i;
            }
        }

        return keys[nearestIndex];
    }

    public static float fractionToSeconds(String fraction) {
        Object value = exposureTimeFractions.get(fraction);
        float result = 0;
        if (value instanceof Integer)
            result = (float) (int) value;
        else if (value instanceof Double)
            result = (float) (double) value;

        return result;
    }
}
