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
      if (24.5f - 0.5f >= temperature && 24.5f + 0.5f < temperature) {
         return TEMP_COLOR[1];
      }
      if (25.5f - 0.5f >= temperature && 25.5f + 0.5f < temperature) {
         return TEMP_COLOR[2];
      }
      if (26.5f - 0.5f >= temperature && 26.5f + 0.5f < temperature) {
         return TEMP_COLOR[3];
      }
      if (27.5f - 0.5f >= temperature && 27.5f + 0.5f < temperature) {
         return TEMP_COLOR[4];
      }
      if (28.5f - 0.5f >= temperature && 28.5f + 0.5f < temperature) {
         return TEMP_COLOR[5];
      }
      if (29.5f - 0.5f >= temperature && 29.5f + 0.5f < temperature) {
         return TEMP_COLOR[6];
      }
      if (30.5f - 0.5f >= temperature && 30.5f + 0.5f < temperature) {
         return TEMP_COLOR[7];
      }
      if (temperature > 31.0f) {
         return TEMP_COLOR[8];
      }
      return android.R.color.white;
   }
}
