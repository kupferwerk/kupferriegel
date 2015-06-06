package com.kupferwerk.kupferriegel.detection;

import java.io.Serializable;

import rx.Observable;
import rx.functions.Func1;

public class HugDetector implements Detector {

   public HugDetector() {

   }

   @Override
   public Observable<DetectorResult> start() {
      Observable<String> a = Observable.just("abc");
      Observable<Integer> b = Observable.just(1, 2, 4);
      return Observable.merge(a, b).map(new Func1<Serializable, DetectorResult>() {
         @Override
         public DetectorResult call(Serializable serializable) {

            return null;
         }
      });
   }

   @Override
   public void stop() {
   }
}
