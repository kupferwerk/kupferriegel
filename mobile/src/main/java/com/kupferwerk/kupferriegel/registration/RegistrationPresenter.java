package com.kupferwerk.kupferriegel.registration;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.kupferwerk.kupferriegel.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class RegistrationPresenter {

   @InjectView (R.id.btn_startstop)
   Button btnStartStop;
   @InjectView (R.id.spinner)
   Spinner spinner;

   private final RegistrationController parentController;
   private int selectedTypeIndex = 0;

   public RegistrationPresenter(RegistrationController parentController) {
      this.parentController = parentController;
   }

   public void init() {
      ButterKnife.inject(this, getActivity());
      initSpinner();
      initButton();
   }

   public void onResume() {
      setBtnTextViaState(!parentController.isRecording());
   }

   private void initSpinner() {
      ArrayAdapter<CharSequence> adapter = ArrayAdapter
            .createFromResource(getActivity(), R.array.sequence_types,
                  android.R.layout.simple_spinner_item);
      adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
      spinner.setAdapter(adapter);
      spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
         @Override
         public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            selectedTypeIndex = position;
         }

         @Override
         public void onNothingSelected(AdapterView<?> parent) {
            // do nothing
         }
      });
   }

   private void initButton() {
      btnStartStop.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            handleBtnClick();
         }
      });
      setBtnTextViaState(!parentController.isRecording());
   }

   private void handleBtnClick() {
      triggerActionViaState(parentController.isRecording());
      setBtnTextViaState(!parentController.isRecording());
   }

   private void triggerActionViaState(boolean startState) {
      if (startState) {
         parentController.stopRecording();
         setBtnText(R.string.start_registering);
      } else {
         parentController.startRecording(mapIndexToUsableSequenceIndex());
         setBtnText(R.string.stop_registering);
      }
   }

   private void setBtnTextViaState(boolean recording) {
      if (recording) {
         setBtnText(R.string.start_registering);
      } else {
         setBtnText(R.string.stop_registering);
      }
   }

   private int mapIndexToUsableSequenceIndex() {
      return selectedTypeIndex + 1;
   }

   private void setBtnText(int textId) {
      btnStartStop.setText(textId);
   }

   private Activity getActivity() {
      return parentController.getActivity();
   }
}
