package com.kupferwerk.kupferriegel.detection;

import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.kupferwerk.kupferriegel.device.DeviceController;
import com.kupferwerk.kupferriegel.device.ReadingInfo;

import java.util.concurrent.TimeUnit;

import io.relayr.model.DeviceModel;
import rx.Subscriber;
import rx.functions.Func1;

public class NoiseOverDetector {

   Subscriber subscriber = new Subscriber<Float>() {

      @Override
      public void onCompleted() {

      }

      @Override
      public void onError(Throwable e) {
         Log.e("Error", e.getMessage(), e);
      }

      @Override
      public void onNext(Float value) {
         PutDataMapRequest request = PutDataMapRequest.create("/noise");
         request.getDataMap().putFloat("extra.noise", value);

         PutDataRequest putDataRequest = request.asPutDataRequest();
         PendingResult<DataApi.DataItemResult> pendingResult =
               Wearable.DataApi.putDataItem(apiClient, putDataRequest);
      }
   };

   GoogleApiClient apiClient;
   DeviceController deviceController;

   public NoiseOverDetector(DeviceController deviceController) {
      this.deviceController = deviceController;
   }

   public void setGoogleApiClient(GoogleApiClient apiClient) {
      this.apiClient = apiClient;
   }

   public void start() {
      deviceController.getDevice(DeviceModel.MICROPHONE).filter(new Func1<ReadingInfo, Boolean>() {

         @Override
         public Boolean call(ReadingInfo readingInfo) {
            return readingInfo.getReading().meaning.equals("noiseLevel");
         }
      }).map(new Func1<ReadingInfo, Object>() {

         @Override
         public Object call(ReadingInfo readingInfo) {
            return readingInfo.getReading().value;
         }
      }).cast(Double.class).filter(new Func1<Double, Boolean>() {
         @Override
         public Boolean call(Double d) {
            return d >= 100;
         }
      }).sample(3, TimeUnit.SECONDS).map(new Func1<Double, Float>() {
         @Override
         public Float call(Double aDouble) {
            return (float) Math.round(aDouble);
         }
      }).distinctUntilChanged().subscribe(subscriber);
   }
}
