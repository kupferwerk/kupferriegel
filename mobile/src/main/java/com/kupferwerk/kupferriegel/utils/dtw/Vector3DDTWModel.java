package com.kupferwerk.kupferriegel.utils.dtw;

public class Vector3DDTWModel implements DTWModel {

   private double x;
   private double y;
   private double z;

   public Vector3DDTWModel(double x, double y, double z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   @Override
   public double distance(DTWModel other) {
      if (!(other instanceof Vector3DDTWModel)) {
         return Double.MAX_VALUE;
      }

      Vector3DDTWModel o2 = (Vector3DDTWModel) other;
      double x2 = o2.x;
      double y2 = o2.y;
      double z2 = o2.z;

      return Math.sqrt((x - x2) * (x - x2) + (y - y2) * (y - y2) + (z - z2) * (z - z2));
   }
}
