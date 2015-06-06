package com.kupferwerk.kupferriegel.sync;

import java.util.HashMap;

public class Syncher {

   public static Syncher instance;
   private HashMap<String, Synchable> synchables = new HashMap<>();

   public static synchronized Syncher getInstance() {
      if (instance == null) {
         instance = new Syncher();
      }
      return instance;
   }

   public void register(String key, Synchable value) {
      synchables.put(key, value);
   }

   public void unregister(String key) {
      synchables.remove(key);
   }

   public Synchable getSynchable(String key) {
      return synchables.get(key);
   }

   public boolean isRegistered(String key) {
      return synchables.containsKey(key);
   }
}
