package com.kupferwerk.kupferriegel.sync;

public class Noise implements SyncContent {

   private float temperature;

   public Noise(float temperature) {
      this.temperature = temperature;
   }

   public float getTemperature() {
      return temperature;
   }
}
