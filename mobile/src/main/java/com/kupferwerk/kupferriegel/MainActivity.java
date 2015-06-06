package com.kupferwerk.kupferriegel;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.kupferwerk.kupferriegel.registration.RegistrationController;
import com.kupferwerk.kupferriegel.user.UserController;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.relayr.RelayrSdk;
import io.relayr.model.User;
import rx.Observer;

public class MainActivity extends ActionBarActivity {

   @InjectView (R.id.toolbar_activity)
   Toolbar toolbar;
   private RegistrationController registrationController;

   UserController userController = new UserController(this);
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
      userController.onResume();
      registrationController.onResume();
   }
}
