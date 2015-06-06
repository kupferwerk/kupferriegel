package com.kupferwerk.kupferriegel.login;

import android.app.Activity;

import io.relayr.RelayrSdk;
import io.relayr.model.User;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;

public class LoginHelper {

   public void logIn(Activity activity, final Observer<User> observer) {
      RelayrSdk.logIn(activity).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
   }

   public void logOut() {
      RelayrSdk.logOut();
   }
}
