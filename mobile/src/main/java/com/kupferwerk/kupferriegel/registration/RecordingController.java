package com.kupferwerk.kupferriegel.registration;

public class RecordingController {

   private final RegistrationController parentController;
   private boolean isRecording = false;

   public RecordingController(RegistrationController controller) {
      this.parentController = controller;
   }

   public boolean isRecording() {
      return isRecording;
   }

   public void startRecording(int index) {
      isRecording = true;
      // TODO trigger recording of values (with timestamps?)
   }

   public void stopRecording() {
      isRecording = false;
      // TODO stop recording and save stuff?
   }
}
