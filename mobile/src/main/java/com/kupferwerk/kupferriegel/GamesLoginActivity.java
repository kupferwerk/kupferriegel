package com.kupferwerk.kupferriegel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class GamesLoginActivity extends Activity
      implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

   private static int RC_SIGN_IN = 9001;
   @InjectView (R.id.login)
   View login;
   @InjectView (R.id.logout)
   View logout;
   @InjectView (R.id.show_achievements)
   View showAchievements;
   private GoogleApiClient googleApiClient;
   private boolean mAutoStartSignInFlow = true;
   private boolean mResolvingConnectionFailure;
   private boolean mSignInClicked;

   @OnClick (R.id.login)
   public void login() {
      mSignInClicked = true;
      googleApiClient.connect();
   }

   @OnClick (R.id.logout)
   public void logout() {
      mSignInClicked = false;
      Games.signOut(googleApiClient);
      finish();
   }

   @Override
   public void onConnected(Bundle bundle) {
      if (mAutoStartSignInFlow) {
         mAutoStartSignInFlow = false;
         showLogout();
      } else {
         finish();
      }
   }

   @Override
   public void onConnectionFailed(ConnectionResult connectionResult) {
      if (mResolvingConnectionFailure) {
         // already resolving
         return;
      }

      // if the sign-in button was clicked or if auto sign-in is enabled,
      // launch the sign-in flow
      if (mSignInClicked || mAutoStartSignInFlow) {
         mAutoStartSignInFlow = false;
         mSignInClicked = false;
         mResolvingConnectionFailure = true;

         // Attempt to resolve the connection failure using BaseGameUtils.
         // The R.string.signin_other_error value should reference a generic
         // error string in your strings.xml file, such as "There was
         // an issue with sign-in, please try again later."
         if (!BaseGameUtils
               .resolveConnectionFailure(this, googleApiClient, connectionResult, RC_SIGN_IN,
                     getString(R.string.signin_error))) {
            mResolvingConnectionFailure = false;
         }
         showLogin();
      }
   }

   @Override
   public void onConnectionSuspended(int i) {
      googleApiClient.connect();
   }

   @OnClick (R.id.show_achievements)
   public void showAchievements() {
      startActivityForResult(Games.Achievements.getAchievementsIntent(googleApiClient), 42);
   }

   protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
      if (requestCode == RC_SIGN_IN) {
         mSignInClicked = false;
         mResolvingConnectionFailure = false;
         if (resultCode == RESULT_OK) {
            googleApiClient.connect();
         } else {
            BaseGameUtils
                  .showActivityResultError(this, requestCode, resultCode, R.string.signin_error);
         }
      }
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.signin);
      ButterKnife.inject(this);
      googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this).addApi(Games.API).addScope(Games.SCOPE_GAMES)
            .build();
   }

   @Override
   protected void onStart() {
      super.onStart();
      googleApiClient.connect();
   }

   @Override
   protected void onStop() {
      super.onStop();
      googleApiClient.disconnect();
   }

   private void showLogin() {
      login.setVisibility(View.VISIBLE);
      logout.setVisibility(View.GONE);
      showAchievements.setVisibility(View.GONE);
   }

   private void showLogout() {
      login.setVisibility(View.GONE);
      showAchievements.setVisibility(View.VISIBLE);
      logout.setVisibility(View.VISIBLE);
   }
}
