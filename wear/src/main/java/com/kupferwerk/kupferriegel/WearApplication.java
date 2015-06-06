package com.kupferwerk.kupferriegel;

import android.app.Application;

import com.kupferwerk.kupferriegel.injection.Injector;
import com.kupferwerk.kupferriegel.injection.RootModule;

public class WearApplication extends Application {

   @Override
   public void onCreate() {
      super.onCreate();
      Injector.init(getRootModule(), this);
   }

   private Object getRootModule() {
      return new RootModule();
   }
}
