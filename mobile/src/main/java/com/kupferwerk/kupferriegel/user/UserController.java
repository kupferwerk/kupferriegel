package com.kupferwerk.kupferriegel.user;

import android.app.Activity;

import com.kupferwerk.kupferriegel.utils.RxUtils;

import io.relayr.RelayrSdk;
import io.relayr.model.User;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class UserController {

   private final Activity activity;
   private Subscription userInfoSubscription;
   private User user;

   public UserController(Activity activity) {
      this.activity = activity;
   }

   public void logIn(Activity activity, final Observer<User> observer) {
      RelayrSdk.logIn(activity).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
   }

   public void logOut() {
      RelayrSdk.logOut();
   }

   private void loadUserInfo() {
      userInfoSubscription =
            RelayrSdk.getRelayrApi().getUserInfo().observeOn(AndroidSchedulers.mainThread())
                  .subscribe(new Subscriber<User>() {
                     @Override
                     public void onCompleted() {
                     }

                     @Override
                     public void onError(Throwable e) {
                        e.printStackTrace();
                     }

                     @Override
                     public void onNext(final User user) {
                        UserController.this.user = user;
                     }
                  });
   }

   public User getUser() {
      return user;
   }

   public void onResume() {
      resubsribeUserInfo();
   }

   private void resubsribeUserInfo() {
      if (RelayrSdk.isUserLoggedIn()) {
         loadUserInfo();
      }
   }

   public void onPause() {
      unsubscribeUserInfo();
   }

   private void unsubscribeUserInfo() {
      if (RxUtils.isSubscribed(userInfoSubscription)) {
         userInfoSubscription.unsubscribe();
      }
   }
}
