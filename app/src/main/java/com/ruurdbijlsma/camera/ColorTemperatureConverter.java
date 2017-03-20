package com.ruurdbijlsma.camera;

import android.hardware.camera2.params.RggbChannelVector;

/**
 * Gemaakt door ruurd op 20-3-2017.
 */

public class ColorTemperatureConverter {
    public static RggbChannelVector rggbToNormalized(RggbChannelVector rggb) {
        float r = rggb.getRed();
        float g1 = rggb.getGreenEven();
        float g2 = rggb.getGreenOdd();
        float b = rggb.getBlue();

        r /= 127.5f;
        g1 /= 255f;
        g2 /= 255f;
        b /= 127.5f;

        RggbChannelVector normalizedRggb = new RggbChannelVector(r + 1, g1 + 1, g2 + 1, b + 1);
        return normalizedRggb;
    }

    public static RggbChannelVector normalizedRggbToRggb(RggbChannelVector normalizedRggb) {
        float r = normalizedRggb.getRed() - 1;
        float g1 = normalizedRggb.getGreenEven() - 1;
        float g2 = normalizedRggb.getGreenOdd() - 1;
        float b = normalizedRggb.getBlue() - 1;

        r *= 127.5f;
        g1 *= 255f;
        g2 *= 255f;
        b *= 127.5f;

        return new RggbChannelVector(r, g1, g2, b);
    }

    public static int rgbNormalizedToKelvin(RggbChannelVector normalizedRggb) {
        RggbChannelVector rggb = normalizedRggbToRggb(normalizedRggb);

        return rgbToKelvin(rggb);
    }

    public static int rgbToKelvin(RggbChannelVector rgb) {
        float r = rgb.getRed();
        float b = rgb.getBlue();

        float temperature = 0;
        RggbChannelVector testRGB;
        float epsilon = 0.4f;
        float minTemperature = 1000;
        float maxTemperature = 40000;
        while (maxTemperature - minTemperature > epsilon) {
            temperature = (maxTemperature + minTemperature) / 2;
            testRGB = kelvinToRgb(temperature);
            if ((testRGB.getBlue() / testRGB.getRed()) >= (b / r)) {
                maxTemperature = temperature;
            } else {
                minTemperature = temperature;
            }
        }
        return Math.round(temperature);
    }

    public static RggbChannelVector kelvinToNormalizedRgb(float kelvin) {
        RggbChannelVector rggb = kelvinToRgb(kelvin);

        return rggbToNormalized(rggb);
    }

    public static RggbChannelVector kelvinToRgb(float kelvin) {
        double temperature = (kelvin / 100.0);
        double red, green, blue;
        if (temperature < 66.0) {
            red = 255;
        } else {
            // a + b x + c Log[x] /.
            // {a -> 351.97690566805693`,
            // b -> 0.114206453784165`,
            // c -> -40.25366309332127
            //x -> (kelvin/100) - 55}
            red = temperature - 55.0;
            red = 351.97690566805693 + 0.114206453784165 * red - 40.25366309332127 * Math.log(red);
            if (red < 0) red = 0;
            if (red > 255) red = 255;
        }
    /* Calculate green */
        if (temperature < 66.0) {
            // a + b x + c Log[x] /.
            // {a -> -155.25485562709179`,
            // b -> -0.44596950469579133`,
            // c -> 104.49216199393888`,
            // x -> (kelvin/100) - 2}
            green = temperature - 2;
            green = -155.25485562709179 - 0.44596950469579133 * green + 104.49216199393888 * Math.log(green);
            if (green < 0) green = 0;
            if (green > 255) green = 255;
        } else {
            // a + b x + c Log[x] /.
            // {a -> 325.4494125711974`,
            // b -> 0.07943456536662342`,
            // c -> -28.0852963507957`,
            // x -> (kelvin/100) - 50}
            green = temperature - 50.0;
            green = 325.4494125711974 + 0.07943456536662342 * green - 28.0852963507957 * Math.log(green);
            if (green < 0) green = 0;
            if (green > 255) green = 255;
        }
    /* Calculate blue */
        if (temperature >= 66.0) {
            blue = 255;
        } else {
            if (temperature <= 20.0) {
                blue = 0;
            } else {
                // a + b x + c Log[x] /.
                // {a -> -254.76935184120902`,
                // b -> 0.8274096064007395`,
                // c -> 115.67994401066147`,
                // x -> kelvin/100 - 10}
                blue = temperature - 10;
                blue = -254.76935184120902 + 0.8274096064007395 * blue + 115.67994401066147 * Math.log(blue);
                if (blue < 0) blue = 0;
                if (blue > 255) blue = 255;
            }
        }
        float r = Math.round(red);
        float g = Math.round(green);
        float b = Math.round(blue);
        return new RggbChannelVector(r, g, g, b);
    }
}
