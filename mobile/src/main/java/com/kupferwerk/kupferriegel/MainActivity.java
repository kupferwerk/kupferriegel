package com.kupferwerk.kupferriegel;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Toolbar;

import com.kupferwerk.kupferriegel.detection.DetectorResult;
import com.kupferwerk.kupferriegel.detection.HandshakeDetector;
import com.kupferwerk.kupferriegel.detection.TemperatureOverDetector;
import com.kupferwerk.kupferriegel.device.DeviceController;
import com.kupferwerk.kupferriegel.device.ReadingInfo;
import com.kupferwerk.kupferriegel.registration.RegistrationController;
import com.kupferwerk.kupferriegel.user.UserController;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.relayr.RelayrSdk;
import io.relayr.model.User;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Action1;

public class MainActivity extends Activity {

   Observer<User> loginObserver = new Observer<User>() {
      @Override
      public void onCompleted() {

      }

      @Override
      public void onError(Throwable e) {
         Toast.makeText(MainActivity.this, "Could not Login", Toast.LENGTH_LONG).show();
         invalidateOptionsMenu();
      }

      @Override
      public void onNext(User user) {
         invalidateOptionsMenu();
      }
   };
   @InjectView (R.id.toolbar_activity)
   Toolbar toolbar;
   UserController userController = new UserController(this);
   DeviceController deviceController = new DeviceController(this, userController);
   private RegistrationController registrationController;

   public static void printReading(ReadingInfo readingInfo) {

      StringBuilder log = new StringBuilder();
      log.append("device " + readingInfo.getDeviceModel().name());
      log.append(";path=" + readingInfo.getReading().path);
      log.append(";meaning=" + readingInfo.getReading().meaning);
      log.append(";received=" + readingInfo.getReading().received);
      log.append(";recorded=" + readingInfo.getReading().recorded);
      log.append(";value=" + readingInfo.getReading().value);

      Log.d(MainActivity.class.getSimpleName(), log.toString());
   }

   @Override
   public void onBackPressed() {
      if (registrationController.isOverlayShown()) {
         registrationController.showRegistrationOverlay(false);
         setActionBar(toolbar);
      } else {
         super.onBackPressed();
      }
   }

   @OnClick (R.id.btn_register_sequence)
   public void onBtnRegisterSequenceClick() {
      registrationController.showRegistrationOverlay(true);
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      // Handle action bar item clicks here. The action bar will
      // automatically handle clicks on the Home/Up button, so long
      // as you specify a parent activity in AndroidManifest.xml.
      int id = item.getItemId();
      if (id == R.id.action_logout) {
         userController.logOut();
         finish();
         return true;
      } else if (id == R.id.action_login) {
         userController.logIn(this, loginObserver);
         finish();
         return true;
      }

      return super.onOptionsItemSelected(item);
   }

   @Override
   public boolean onPrepareOptionsMenu(Menu menu) {
      menu.clear();
      if (RelayrSdk.isUserLoggedIn()) {
         getMenuInflater().inflate(R.menu.menu_main_logout, menu);
      } else {
         getMenuInflater().inflate(R.menu.menu_main_login, menu);
      }

      return super.onPrepareOptionsMenu(menu);
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      ButterKnife.inject(this);
      setActionBar(toolbar);
      registrationController = new RegistrationController(this);
      registrationController.onCreate();
      userController.logIn(this, loginObserver);
   }

   @Override
   protected void onPause() {
      userController.onPause();
      registrationController.onPause();
      super.onPause();
   }

   @Override
   protected void onResume() {
      super.onResume();
      registrationController.onResume();
      userController.loadUserInfo().subscribe(new Subscriber<User>() {
         @Override
         public void onCompleted() {

         }

         @Override
         public void onError(Throwable e) {

         }

         @Override
         public void onNext(User user) {
            loadDevices();
         }
      });
   }

   private void loadDevices() {
      TemperatureOverDetector temperatureOverDetector =
            new TemperatureOverDetector(deviceController);
      temperatureOverDetector.start();
      HandshakeDetector handshakeDetector = new HandshakeDetector(deviceController);
      handshakeDetector.start().subscribe(new Action1<DetectorResult>() {
         @Override
         public void call(DetectorResult detectorResult) {
            Toast.makeText(MainActivity.this, "Shake your hands!", Toast.LENGTH_LONG).show();
         }
      }, new Action1<Throwable>() {
         @Override
         public void call(Throwable throwable) {
            Toast.makeText(MainActivity.this, "Error occured! " + throwable.getMessage(),
                  Toast.LENGTH_LONG).show();
         }
      });
      //      deviceController.getDevice(DeviceModel.LIGHT_PROX_COLOR).subscribe(subscriber);
      //      deviceController.getDevice(DeviceModel.ACCELEROMETER_GYROSCOPE).subscribe(subscriber);
      //      deviceController.getDevice(DeviceModel.MICROPHONE).subscribe(subscriber);
   }
}
