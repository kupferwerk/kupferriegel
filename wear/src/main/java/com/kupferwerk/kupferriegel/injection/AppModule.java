package com.kupferwerk.kupferriegel.injection;

import android.content.Context;

import com.kupferwerk.kupferriegel.sync.SyncController;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger module for setting up provides statements. Register all of your entry points below.
 */
@Module (complete = false, library = true)
public class AppModule {

   @Provides
   @Singleton
   SyncController provideSyncController(final Context context) {
      return new SyncController(context);
   }
}
