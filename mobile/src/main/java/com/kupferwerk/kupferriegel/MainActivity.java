package com.kupferwerk.kupferriegel;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.games.Games;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.kupferwerk.kupferriegel.detection.DetectorResult;
import com.kupferwerk.kupferriegel.detection.HandshakeDetector;
import com.kupferwerk.kupferriegel.detection.NoiseOverDetector;
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

public class MainActivity extends Activity
      implements DataApi.DataListener, GoogleApiClient.ConnectionCallbacks,
      GoogleApiClient.OnConnectionFailedListener {

   public static final String SHAKES = "shakes";
   public static final String SHAKE_COUNT = "Shake_Count";
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
   NoiseOverDetector noiseOverDetector;
   TemperatureOverDetector temperatureOverDetector;
   @InjectView (R.id.toolbar_activity)
   Toolbar toolbar;
   UserController userController = new UserController(this);
   DeviceController deviceController = new DeviceController(this, userController);
   private AchievementController achievementController;
   private GoogleApiClient apiClient;
   private HandshakeDetector handshakeDector;
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
      //      deviceController.getDevice(DeviceModel.LIGHT_PROX_COLOR).subscribe(subscriber);
      //      deviceController.getDevice(DeviceModel.ACCELEROMETER_GYROSCOPE).subscribe(subscriber);
      //      deviceController.getDevice(DeviceModel.MICROPHONE).subscribe(subscriber);
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

   public void onConnected(Bundle bundle) {
      temperatureOverDetector.setGoogleApiClient(apiClient);
      noiseOverDetector.setGoogleApiClient(apiClient);
      achievementController = new AchievementController();
      achievementController.check(apiClient, this);
   }

   @Override
   public void onConnectionFailed(ConnectionResult connectionResult) {
      // nothing to do
   }

   @Override
   public void onConnectionSuspended(int i) {
      // noting to do here
   }

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
      this.apiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this).addApi(Wearable.API).addApi(Games.API).build();
      setContentView(R.layout.activity_main);
      ButterKnife.inject(this);
      setActionBar(toolbar);
      registrationController = new RegistrationController(this);
      registrationController.onCreate();
      this.apiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this).addApi(Wearable.API).build();
      userController.logIn(this, loginObserver);
   }

   @Override
   protected void onPause() {
      apiClient.disconnect();
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

   private void loadDevices() {
      temperatureOverDetector = new TemperatureOverDetector(deviceController);
      temperatureOverDetector.start();
      noiseOverDetector = new NoiseOverDetector(deviceController);
      noiseOverDetector.start();
      handshakeDector = new HandshakeDetector(deviceController);
      handshakeDector.start().subscribe(new Action1<DetectorResult>() {
                                           @Override
                                           public void call(DetectorResult detectorResult) {
                                              Log.d("Shake", "Handshake");
                                              final SharedPreferences preferences =
                                                    getSharedPreferences(SHAKES, MODE_PRIVATE);
                                              int count = preferences.getInt(SHAKE_COUNT, 0);
                                              preferences.edit().putInt(SHAKE_COUNT, count++)
                                                    .apply();
                                              Log.wtf("Shake", "Shake should be detected");
                                              achievementController
                                                    .check(apiClient, MainActivity.this);
                                              PutDataMapRequest request =
                                                    PutDataMapRequest.create("/hugs");
                                              request.getDataMap().putFloat("extra.hugs", count);

                                              PutDataRequest putDataRequest =
                                                    request.asPutDataRequest();
                                              PendingResult<DataApi.DataItemResult> pendingResult =
                                                    Wearable.DataApi
                                                          .putDataItem(apiClient, putDataRequest);
                                           }
                                        }

      );
      //      deviceController.getDevice(DeviceModel.LIGHT_PROX_COLOR).subscribe(subscriber);
      //      deviceController.getDevice(DeviceModel.ACCELEROMETER_GYROSCOPE).subscribe
      // (subscriber);
      //      deviceController.getDevice(DeviceModel.MICROPHONE).subscribe(subscriber);
   }
}

