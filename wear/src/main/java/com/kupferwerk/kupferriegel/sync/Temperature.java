package com.kupferwerk.kupferriegel.sync;

public class Temperature implements SyncContent {

   private float temperature;

   public Temperature(float temperature) {
      this.temperature = temperature;
   }

   public float getTemperature() {
      return temperature;
   }
}
