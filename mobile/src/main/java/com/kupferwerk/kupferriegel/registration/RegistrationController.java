package com.kupferwerk.kupferriegel.registration;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.kupferwerk.kupferriegel.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class RegistrationController {

   @InjectView (R.id.overlay_registration)
   View overlayRegistrationView;
   @InjectView (R.id.toolbar_overlay)
   Toolbar overlayToolbar;

   private final ActionBarActivity activity;
   private final RegistrationPresenter registrationPresenter;
   private final RecordingController recordingController;

   public RegistrationController(ActionBarActivity activity) {
      this.activity = activity;
      registrationPresenter = new RegistrationPresenter(this);
      recordingController = new RecordingController(this);
   }

   public boolean isRecording() {
      return recordingController.isRecording();
   }

   public void startRecording(int selectedTypeIndex) {
      recordingController.startRecording(selectedTypeIndex);
   }

   public void stopRecording() {
      recordingController.stopRecording();
   }

   public void onCreate() {
      ButterKnife.inject(this, activity);
      registrationPresenter.init();
   }

   public void onPause() {
      stopRecording();
   }

   public void onResume() {
      registrationPresenter.onResume();
   }

   public boolean isOverlayShown() {
      return overlayRegistrationView.getVisibility() == View.VISIBLE;
   }

   public void showRegistrationOverlay(boolean show) {
      if (show) {
         overlayRegistrationView.setVisibility(View.VISIBLE);
         activity.setSupportActionBar(overlayToolbar);
      } else {
         recordingController.stopRecording();
         overlayRegistrationView.setVisibility(View.GONE);
      }
   }

   public Activity getActivity() {
      return activity;
   }
}
