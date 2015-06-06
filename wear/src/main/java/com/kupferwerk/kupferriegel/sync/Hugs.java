package com.kupferwerk.kupferriegel.sync;

public class Hugs implements SyncContent {

   private int hugs;

   public Hugs(int hugs) {
      this.hugs = hugs;
   }

   public int getHugs() {
      return hugs;
   }
}
