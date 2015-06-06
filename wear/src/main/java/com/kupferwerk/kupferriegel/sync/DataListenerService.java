package com.kupferwerk.kupferriegel.sync;

import android.content.Intent;

import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.WearableListenerService;
import com.kupferwerk.kupferriegel.ShowDataActivity;

public class DataListenerService extends WearableListenerService {

   @Override
   public void onDataChanged(DataEventBuffer dataEvents) {
      super.onDataChanged(dataEvents);
      final Intent intent = new Intent(this, ShowDataActivity.class);
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      startActivity(intent);
   }
}
