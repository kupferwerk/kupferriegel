package com.kupferwerk.kupferriegel.utils.dtw;

import java.util.ArrayList;
import java.util.List;

public class SimpleDTWModel implements DTWModel {

   private double value;

   public static List<SimpleDTWModel> buildFromDouble(Double... values) {
      List<SimpleDTWModel> model = new ArrayList<>();
      for (Double value : values) {
         model.add(new SimpleDTWModel(value));
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
