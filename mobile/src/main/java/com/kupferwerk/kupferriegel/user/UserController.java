package com.kupferwerk.kupferriegel.user;

import android.app.Activity;

import io.relayr.RelayrSdk;
import io.relayr.model.User;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;

public class UserController {

   private final Activity activity;

   public UserController(Activity activity) {
      this.activity = activity;
   }

   public void logIn(Activity activity, final Observer<User> observer) {
      RelayrSdk.logIn(activity).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
   }

   public void logOut() {
      RelayrSdk.logOut();
   }
}
