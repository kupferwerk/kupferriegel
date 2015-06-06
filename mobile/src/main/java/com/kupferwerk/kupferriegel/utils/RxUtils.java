package com.kupferwerk.kupferriegel.utils;

import rx.Subscription;

public class RxUtils {

   public static boolean isSubscribed(Subscription subscription) {
      return subscription != null && !subscription.isUnsubscribed();
   }

   public static void unsubscribe(final Subscription subscription) {
      if (subscription != null && !subscription.isUnsubscribed()) {
         subscription.unsubscribe();
      }
   }
}
