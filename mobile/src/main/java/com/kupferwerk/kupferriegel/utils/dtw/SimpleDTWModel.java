package com.kupferwerk.kupferriegel.utils.dtw;

public class SimpleDTWModel implements DTWModel {

   private double value;

   public static SimpleDTWModel[] buildFromDouble(Double... values) {
      final SimpleDTWModel[] model = new SimpleDTWModel[values.length];
      for (int i = 0; i < values.length; i++) {
         model[i] = new SimpleDTWModel(values[i]);
      }
      return model;
   }

   public SimpleDTWModel(double value) {
      this.value = value;
   }

   @Override
   public double distance(DTWModel other) {
      if (!(other instanceof SimpleDTWModel)) {
         return Double.MAX_VALUE;
      }
      return Math.abs(value - ((SimpleDTWModel) other).value);
   }
}
