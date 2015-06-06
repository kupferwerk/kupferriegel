package com.kupferwerk.kupferriegel.sync;

public class Noise implements SyncContent {

   private float noise;

   public Noise(float noise) {
      this.noise = noise;
   }

   public float getNoise() {
      return noise;
   }
}
