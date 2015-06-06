package com.kupferwerk.kupferriegel.injection;

import android.content.Context;

import com.kupferwerk.kupferriegel.ShowDataActivity;
import com.kupferwerk.kupferriegel.WearApplication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module (
      includes = {AndroidModule.class, AppModule.class},
      injects = {WearApplication.class, ShowDataActivity.class})
public class RootModule {

   protected Context context;

   public RootModule(Context context) {
      this.context = context;
   }

   @Provides
   @Singleton
   Context provideContext() {
      return this.context;
   }
}
