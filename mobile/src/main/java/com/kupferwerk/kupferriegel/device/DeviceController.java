package com.kupferwerk.kupferriegel.device;

import android.app.Activity;
import android.util.Log;

import com.google.gson.Gson;

import java.util.List;

import io.relayr.RelayrSdk;
import io.relayr.model.Device;
import io.relayr.model.Reading;
import io.relayr.model.TransmitterDevice;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;

public class DeviceController {

   private final Activity activity;
   private List<Device> devices;
   private TransmitterDevice transmitterDevice;

   public DeviceController(Activity activity) {
      this.activity = activity;
   }

   public void loadDevices(String userId, final Observer<Reading> observer) {
      Log.i("HTH", "Loading devices...");

      RelayrSdk.getRelayrApi().getUserDevices(userId).observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<List<Device>>() {
               @Override
               public void onCompleted() {
               }

               @Override
               public void onError(Throwable e) {
                  e.printStackTrace();
               }

               @Override
               public void onNext(List<Device> devices) {
                  DeviceController.this.devices = devices;
                  for (Device device : devices) {
                     subscribeForUpdates(device.toTransmitterDevice(), observer);
                     break;
                  }
               }
            });
   }

   private void subscribeForUpdates(final TransmitterDevice transmitterDevice,
         final Observer<Reading> readingObserver) {
      this.transmitterDevice = transmitterDevice;
      RelayrSdk.getWebSocketClient().subscribe(transmitterDevice)
            .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Object>() {
         @Override
         public void onCompleted() {
         }

         @Override
         public void onError(Throwable e) {
         }

         @Override
         public void onNext(Object o) {
            final Reading reading = new Gson().fromJson(o.toString(), Reading.class);
            readingObserver.onNext(reading);
            Log.i(DeviceController.class.getSimpleName(), "Device update: path:" + reading.path +
                  "\nmeaning: " + reading.meaning +
                  "\ndata: " + reading.value);
         }
      });
   }
}
