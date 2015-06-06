package com.kupferwerk.kupferriegel.detection;

import android.text.TextUtils;
import android.util.Log;

import com.kupferwerk.kupferriegel.MainActivity;
import com.kupferwerk.kupferriegel.device.DeviceController;
import com.kupferwerk.kupferriegel.device.ReadingInfo;
import com.kupferwerk.kupferriegel.utils.dtw.DTWModel;
import com.kupferwerk.kupferriegel.utils.dtw.DynamicTimeWarping;
import com.kupferwerk.kupferriegel.utils.dtw.Vector3DDTWModel;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.relayr.model.DeviceModel;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class HandshakeDetector implements Detector {

   private static final String TAG = HandshakeDetector.class.getSimpleName();
   private final DeviceController deviceController;
   private List<DTWModel> recordedData;
   private List<DTWModel> templateData;

   public HandshakeDetector(DeviceController deviceController) {
      this.deviceController = deviceController;
      recordedData = new LinkedList<>();
      fillTemplateData();
   }

   @Override
   public Observable<DetectorResult> start() {
      return deviceController.getDevice(DeviceModel.ACCELEROMETER_GYROSCOPE)
            .filter(new Func1<ReadingInfo, Boolean>() {
               @Override
               public Boolean call(ReadingInfo readingInfo) {
                  return handleData(readingInfo);
               }
            }).map(new Func1<ReadingInfo, DetectorResult>() {
               @Override
               public DetectorResult call(ReadingInfo aBoolean) {
                  return new DetectorResult().setDetectorType(DetectorManager.DETECTOR_HANDSHAKE);
               }
            }).subscribeOn(Schedulers.io());
   }

   @Override
   public void stop() {
   }

   private void fillTemplateData() {
      templateData = new LinkedList<>();
      //      templateData.add(new Vector3DDTWModel(0.25, -0.95, -0.95));
      //      templateData.add(new Vector3DDTWModel(-0.13, -0.82, -0.82));
      //      templateData.add(new Vector3DDTWModel(-0.61, -0.76, -0.76));
      //      templateData.add(new Vector3DDTWModel(-0.6, -0.8, -0.8));
      //      templateData.add(new Vector3DDTWModel(-0.33, -0.93, -0.93));
      //      templateData.add(new Vector3DDTWModel(0.09, -0.98, -0.98));
      //      templateData.add(new Vector3DDTWModel(0.42, -0.85, -0.85));
      //      templateData.add(new Vector3DDTWModel(0.2, -0.93, -0.93));
      //      templateData.add(new Vector3DDTWModel(-0.16, -0.96, -0.96));
      //      templateData.add(new Vector3DDTWModel(-0.52, -0.92, -0.92));
      //      templateData.add(new Vector3DDTWModel(-0.67, -0.74, -0.74));
      //      templateData.add(new Vector3DDTWModel(-0.4, -0.91, -0.91));
      //      templateData.add(new Vector3DDTWModel(0.0, -0.98, -0.98));
      //      templateData.add(new Vector3DDTWModel(0.38, -0.81, -0.81));
      //      templateData.add(new Vector3DDTWModel(0.51, -0.81, -0.81));
      templateData.add(new Vector3DDTWModel(0.25, 0, 0));
      templateData.add(new Vector3DDTWModel(-0.6, 0, 0));
      templateData.add(new Vector3DDTWModel(-0.33, 0, 0));
      templateData.add(new Vector3DDTWModel(0.0, 0, 0));
      templateData.add(new Vector3DDTWModel(0.42, 0, 0));
      templateData.add(new Vector3DDTWModel(0.2, 0, 0));
      templateData.add(new Vector3DDTWModel(-0.16, 0, 0));
      templateData.add(new Vector3DDTWModel(-0.67, 0, 0));
      templateData.add(new Vector3DDTWModel(0.0, 0, 0));
      templateData.add(new Vector3DDTWModel(0.38, 0, 0));
      templateData.add(new Vector3DDTWModel(0.51, 0, 0));
   }

   private boolean handleData(final ReadingInfo readingInfo) {
      MainActivity.printReading(readingInfo);
      if (!TextUtils.equals(readingInfo.getReading().meaning, "acceleration")) {
         return false;
      }
      final Map<String, Double> currentAcceleration =
            (Map<String, Double>) readingInfo.getReading().value;
      double x = currentAcceleration.get("x");
      double y = currentAcceleration.get("y");
      double z = currentAcceleration.get("z");
      Log.d(TAG, "templateData.add(new Vector3DDTWModel(" + x + ", " + y + ", " + y + "));");
      recordedData.add(new Vector3DDTWModel(x, 0, 0));
      if (recordedData.size() < templateData.size() / 0.75) {
         return false;
      }
      while (recordedData.size() > templateData.size() * 1.1) {
         recordedData.remove(0);
      }

      double similarity = DynamicTimeWarping.dtw(recordedData, templateData);
      Log.d(TAG, "RecordedData=" + recordedData.size() + "; Similarity = " + similarity);
      if (similarity < 4.0f) {
         recordedData.clear();
         return true;
      }
      return false;
   }
}
