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

   private static final int MAX_RECORDED_ITEMS = 10;
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
            }).observeOn(Schedulers.io());
   }

   @Override
   public void stop() {
   }

   private void fillTemplateData() {
      templateData = new LinkedList<>();
      templateData.add(new Vector3DDTWModel(0.76, 0.59, -0.53));
      templateData.add(new Vector3DDTWModel(0.0, 1.0, -0.24));
      templateData.add(new Vector3DDTWModel(0.0, 0.84, -0.07));
      templateData.add(new Vector3DDTWModel(0.42, -0.74, -0.16));
      templateData.add(new Vector3DDTWModel(-0.21, -1.16, 0.7));
      templateData.add(new Vector3DDTWModel(0.66, -0.09, -0.29));
      templateData.add(new Vector3DDTWModel(0.0, 0.94, 0.0));
      templateData.add(new Vector3DDTWModel(0.14, 0.99, 0.32));
      templateData.add(new Vector3DDTWModel(0.26, 0.85, -0.36));
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
      recordedData.add(new Vector3DDTWModel(x, y, z));
      if (recordedData.size() > MAX_RECORDED_ITEMS) {
         recordedData.remove(recordedData.size() - 1);
      }
      double similarity = DynamicTimeWarping.dtw(recordedData, templateData);
      Log.d(TAG, "Similarity = " + similarity);
      if (similarity > 0.5f) {
         recordedData.clear();
         return true;
      }
      return false;
   }
}
