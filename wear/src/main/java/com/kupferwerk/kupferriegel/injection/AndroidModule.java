package com.kupferwerk.kupferriegel.injection;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

import dagger.Module;
import dagger.Provides;

/**
 * Module for all Android related provisions
 */
@Module (complete = false, library = true)
public class AndroidModule {

   @Provides
   SharedPreferences provideDefaultSharedPreferences(final Context context) {
      return PreferenceManager.getDefaultSharedPreferences(context);
   }

   @Provides
   PackageInfo providePackageInfo(Context context) {
      try {
         return context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
      } catch (PackageManager.NameNotFoundException e) {
         throw new RuntimeException(e);
      }
   }
}
