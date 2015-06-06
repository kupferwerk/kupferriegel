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

public class TemperatureOverDetector {

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
         PutDataMapRequest request = PutDataMapRequest.create("/temperature");
         request.getDataMap().putFloat("extra.temperature", value);

         PutDataRequest putDataRequest = request.asPutDataRequest();
         PendingResult<DataApi.DataItemResult> pendingResult =
               Wearable.DataApi.putDataItem(apiClient, putDataRequest);
      }
   };

   DeviceController deviceController;
   GoogleApiClient apiClient;

   public TemperatureOverDetector(DeviceController deviceController) {
      this.deviceController = deviceController;
   }

   public void setGoogleApiClient(GoogleApiClient apiClient) {
      this.apiClient = apiClient;
   }

   public void start() {
      deviceController.getDevice(DeviceModel.TEMPERATURE_HUMIDITY)
            .filter(new Func1<ReadingInfo, Boolean>() {

               @Override
               public Boolean call(ReadingInfo readingInfo) {
                  return readingInfo.getReading().meaning.equals("temperature");
               }
            }).map(new Func1<ReadingInfo, Object>() {

         @Override
         public Object call(ReadingInfo readingInfo) {
            return readingInfo.getReading().value;
         }
      }).cast(Double.class).filter(new Func1<Double, Boolean>() {
         @Override
         public Boolean call(Double d) {
            return d >= 32;
         }
      }).sample(5, TimeUnit.SECONDS).map(new Func1<Double, Float>() {
         @Override
         public Float call(Double aDouble) {
            float round = (float) Math.round(aDouble * 10f);
            return round / 10f;
         }
      }).distinctUntilChanged().subscribe(subscriber);
   }
}
