package com.kupferwerk.kupferriegel;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.kupferwerk.kupferriegel.device.DeviceController;
import com.kupferwerk.kupferriegel.device.ReadingInfo;
import com.kupferwerk.kupferriegel.registration.RegistrationController;
import com.kupferwerk.kupferriegel.user.UserController;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.relayr.RelayrSdk;
import io.relayr.model.DeviceModel;
import io.relayr.model.User;
import rx.Observer;
import rx.Subscriber;

public class MainActivity extends ActionBarActivity {

   @InjectView (R.id.toolbar_activity)
   Toolbar toolbar;
   private RegistrationController registrationController;

   UserController userController = new UserController(this);
   DeviceController deviceController = new DeviceController(this, userController);

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

   @OnClick (R.id.btn_register_sequence)
   public void onBtnRegisterSequenceClick() {
      registrationController.showRegistrationOverlay(true);
   }

   @Override
   public void onBackPressed() {
      if (registrationController.isOverlayShown()) {
         registrationController.showRegistrationOverlay(false);
         setSupportActionBar(toolbar);
      } else {
         super.onBackPressed();
      }
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      ButterKnife.inject(this);
      setSupportActionBar(toolbar);
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

      Subscriber subscriber = new Subscriber<ReadingInfo>() {

         @Override
         public void onCompleted() {

         }

         @Override
         public void onError(Throwable e) {

         }

         @Override
         public void onNext(ReadingInfo readingInfo) {
            printReading(readingInfo);
         }
      };

      deviceController.getDevice(DeviceModel.TEMPERATURE_HUMIDITY).subscribe(subscriber);
      deviceController.getDevice(DeviceModel.LIGHT_PROX_COLOR).subscribe(subscriber);
      deviceController.getDevice(DeviceModel.ACCELEROMETER_GYROSCOPE).subscribe(subscriber);
      deviceController.getDevice(DeviceModel.MICROPHONE).subscribe(subscriber);
   }

   private void printReading(ReadingInfo readingInfo) {

      StringBuilder log = new StringBuilder();
      log.append("device " + readingInfo.getDeviceModel().name());
      log.append(";path=" + readingInfo.getReading().path);
      log.append(";meaning=" + readingInfo.getReading().meaning);
      log.append(";received=" + readingInfo.getReading().received);
      log.append(";recorded=" + readingInfo.getReading().recorded);
      log.append(";value=" + readingInfo.getReading().value);

      Log.d(getClass().getSimpleName(), log.toString());
   }
}
