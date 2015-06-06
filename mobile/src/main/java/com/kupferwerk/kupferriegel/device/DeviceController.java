package com.kupferwerk.kupferriegel.device;

import android.content.Context;

import com.kupferwerk.kupferriegel.user.UserController;

import java.util.ArrayList;
import java.util.List;

import io.relayr.RelayrSdk;
import io.relayr.model.DeviceModel;
import io.relayr.model.Reading;
import io.relayr.model.Transmitter;
import io.relayr.model.TransmitterDevice;
import io.relayr.model.User;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

public class DeviceController {

   private Context context;
   private UserController userController;

   public DeviceController(Context context, UserController userController) {
      this.context = context;
      this.userController = userController;
   }

   public Observable<ReadingInfo> getDevice(final DeviceModel deviceModel) {

      return Observable.create(new Observable.OnSubscribe<ReadingInfo>() {

         @Override
         public void call(final Subscriber<? super ReadingInfo> subscriber) {
            loadDevice(deviceModel).subscribe(new Subscriber<TransmitterDevice>() {
               @Override
               public void onCompleted() {
                  subscriber.onCompleted();
               }

               @Override
               public void onError(Throwable e) {
                  subscriber.onError(e);
               }

               @Override
               public void onNext(TransmitterDevice transmitterDevice) {
                  subscribeToCloudReadings(deviceModel, transmitterDevice, subscriber);
               }
            });
         }
      });
   }

   private void subscribeToCloudReadings(final DeviceModel deviceModel,
         TransmitterDevice transmitterDevice, final Subscriber<? super ReadingInfo> subscriber) {
      transmitterDevice.subscribeToCloudReadings().map(

            new Func1<Reading, ReadingInfo>() {
               @Override
               public ReadingInfo call(Reading reading) {
                  ReadingInfo readingInfo = new ReadingInfo();
                  readingInfo.setReading(reading);
                  readingInfo.setDeviceModel(deviceModel);
                  return readingInfo;
               }
            }).subscribe(subscriber);
   }

   private Observable<TransmitterDevice> loadDevice(final DeviceModel deviceModel) {

      return Observable.create(new Observable.OnSubscribe<TransmitterDevice>() {

         @Override
         public void call(final Subscriber<? super TransmitterDevice> subscriber) {
            User user = userController.getUser();
            user.getTransmitters()
                  .flatMap(new Func1<List<Transmitter>, Observable<List<TransmitterDevice>>>() {
                     @Override
                     public Observable<List<TransmitterDevice>> call(
                           List<Transmitter> transmitters) {
                        // This is a naive implementation. Users may own many WunderBars or other
                        // kinds of transmitter.
                        if (transmitters.isEmpty()) {
                           return Observable.from(new ArrayList<List<TransmitterDevice>>());
                        }
                        return RelayrSdk.getRelayrApi()
                              .getTransmitterDevices(transmitters.get(0).id);
                     }
                  }).observeOn(AndroidSchedulers.mainThread())
                  .subscribe(new Subscriber<List<TransmitterDevice>>() {
                     @Override
                     public void onCompleted() {
                        subscriber.onCompleted();
                     }

                     @Override
                     public void onError(Throwable e) {
                        subscriber.onError(e);
                     }

                     @Override
                     public void onNext(List<TransmitterDevice> devices) {
                        for (TransmitterDevice device : devices) {
                           if (device.model.equals(deviceModel.getId())) {
                              subscriber.onNext(device);
                              subscriber.onCompleted();
                              return;
                           }
                        }
                     }
                  });
         }
      });
   }
}
