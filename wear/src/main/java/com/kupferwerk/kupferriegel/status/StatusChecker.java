package com.kupferwerk.kupferriegel.status;

import com.kupferwerk.kupferriegel.R;

public class StatusChecker {

   public interface NOISE {
   }

   private static final int[] TEMP_COLOR =
         {R.color.temp_0, R.color.temp_1, R.color.temp_2, R.color.temp_3, R.color.temp_4,
               R.color.temp_5, R.color.temp_6, R.color.temp_7, R.color.temp_8,};

   public static int getColorTemperatur(float temperature) {
      if (temperature < 24.0f) {
         return TEMP_COLOR[0];
      }
      if (24.0f >= temperature && temperature < 25.0f) {
         return TEMP_COLOR[1];
      }
      if (25.0f >= temperature && temperature < 26.0f) {
         return TEMP_COLOR[2];
      }
      if (26.0f >= temperature && temperature < 27.0f) {
         return TEMP_COLOR[3];
      }
      if (27.0f >= temperature && temperature < 28f) {
         return TEMP_COLOR[4];
      }
      if (28.0f >= temperature && temperature < 29f) {
         return TEMP_COLOR[5];
      }
      if (29.0f >= temperature && temperature < 30f) {
         return TEMP_COLOR[6];
      }
      if (30.0f >= temperature && temperature < 31f) {
         return TEMP_COLOR[7];
      }
      if (temperature >= 31.0f) {
         return TEMP_COLOR[8];
      }
      return android.R.color.white;
   }
}
