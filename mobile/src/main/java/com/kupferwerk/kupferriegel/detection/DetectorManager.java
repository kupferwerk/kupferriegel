package com.kupferwerk.kupferriegel.detection;

import com.kupferwerk.kupferriegel.device.DeviceController;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

public class DetectorManager {

   public static class Builder {
      private DeviceController deviceController;
      private int flags;

      public DetectorManager build() {
         if (deviceController == null) {
            throw new IllegalArgumentException("DeviceController has to be set.");
         }
         return new DetectorManager(flags, deviceController);
      }

      public void enableHandShakeDetection() {
         flags = flags | DETECTOR_HANDSHAKE;
      }

      public void enableHugDetection() {
         flags = flags | DETECTOR_HUG;
      }

      public void setDeviceController(DeviceController deviceController) {
         this.deviceController = deviceController;
      }
   }

   public static int DETECTOR_HANDSHAKE = 2;
   public static int DETECTOR_HUG = 1;
   private List<Detector> detectors;

   private DetectorManager(int flags, final DeviceController deviceController) {
      this.detectors = new ArrayList<>();
      if ((flags & DETECTOR_HANDSHAKE) > 0) {
         // TODO Add handshake detector
      } else if ((flags & DETECTOR_HUG) > 0) {
         detectors.add(new HugDetector(deviceController));
      }
   }

   public Observable<DetectorResult> startDetection() {
      List<Observable<DetectorResult>> observables = new ArrayList<>(detectors.size());
      for (Detector detector : detectors) {
         observables.add(detector.start());
      }
      return Observable.merge(observables);
   }

   public void stopDetection() {
      for (Detector detector : detectors) {
         detector.stop();
      }
   }
}
