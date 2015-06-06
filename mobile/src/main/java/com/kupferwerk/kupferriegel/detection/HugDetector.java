package com.kupferwerk.kupferriegel.detection;

import com.kupferwerk.kupferriegel.device.DeviceController;
import com.kupferwerk.kupferriegel.device.ReadingInfo;

import io.relayr.model.DeviceModel;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class HugDetector implements Detector {

   private final DeviceController deviceController;

   public HugDetector(DeviceController deviceController) {
      this.deviceController = deviceController;
   }

   @Override
   public Observable<DetectorResult> start() {
      return Observable.merge(deviceController.getDevice(DeviceModel.ACCELEROMETER_GYROSCOPE),
            deviceController.getDevice(DeviceModel.TEMPERATURE_HUMIDITY))
            .filter(new Func1<ReadingInfo, Boolean>() {
               @Override
               public Boolean call(ReadingInfo readingInfo) {
                  return handleHugData(readingInfo);
               }
            }).map(new Func1<ReadingInfo, DetectorResult>() {
               @Override
               public DetectorResult call(ReadingInfo aBoolean) {
                  return new DetectorResult().setDetectorType(DetectorManager.DETECTOR_HUG);
               }
            }).observeOn(Schedulers.io());
   }

   @Override
   public void stop() {
   }

   private boolean handleHugData(final ReadingInfo readingInfo) {
      return false;
   }
}
