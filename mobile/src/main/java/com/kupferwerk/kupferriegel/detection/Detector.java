package com.kupferwerk.kupferriegel.detection;

import rx.Observable;

public interface Detector {

   Observable<DetectorResult> start();

   void stop();
}
