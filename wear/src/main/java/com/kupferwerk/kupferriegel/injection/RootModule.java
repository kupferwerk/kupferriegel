package com.kupferwerk.kupferriegel.injection;

import com.kupferwerk.kupferriegel.ShowDataActivity;

import dagger.Module;

@Module (
      includes = {AndroidModule.class, AppModule.class},
      injects = {ShowDataActivity.class})
public class RootModule {

}
