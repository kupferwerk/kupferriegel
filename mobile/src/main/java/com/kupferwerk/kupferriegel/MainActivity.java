package com.kupferwerk.kupferriegel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.Wearable;
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

public class MainActivity extends Activity
      implements DataApi.DataListener, GoogleApiClient.ConnectionCallbacks,
      GoogleApiClient.OnConnectionFailedListener {

   private GoogleApiClient apiClient;
   private boolean connected;
   private static float count = 22.0f;

   @Override
   public void onConnected(Bundle bundle) {
      this.connected = true;
      temperatureOverDetector.setGoogleApiClient(apiClient);
   }

   @Override
   public void onConnectionFailed(ConnectionResult connectionResult) {

   }

   @Override
   public void onConnectionSuspended(int i) {
      this.connected = false;
   }

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
   public void onDataChanged(DataEventBuffer dataEventBuffer) {

   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      int id = item.getItemId();
      if (id == R.id.games_services) {
         startActivity(new Intent(this, GamesLoginActivity.class));
         return true;
      } else if (id == R.id.action_logout) {
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
         setActionBar(toolbar);
      } else {
         super.onBackPressed();
      }
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      this.apiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this).addApi(Wearable.API).build();
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
      apiClient.disconnect();
      super.onPause();
   }

   @Override
   protected void onResume() {
      super.onResume();
      apiClient.connect();
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

   TemperatureOverDetector temperatureOverDetector;

   private void loadDevices() {
      temperatureOverDetector = new TemperatureOverDetector(deviceController);
      temperatureOverDetector.start();
      //      deviceController.getDevice(DeviceModel.LIGHT_PROX_COLOR).subscribe(subscriber);
      //      deviceController.getDevice(DeviceModel.ACCELEROMETER_GYROSCOPE).subscribe(subscriber);
      //      deviceController.getDevice(DeviceModel.MICROPHONE).subscribe(subscriber);
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
