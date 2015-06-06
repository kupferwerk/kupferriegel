package com.kupferwerk.kupferriegel.sync;

import android.content.Intent;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;
import com.kupferwerk.kupferriegel.NoiseActivity;
import com.kupferwerk.kupferriegel.TemperatureActivity;

public class DataListenerService extends WearableListenerService {

   @Override
   public void onDataChanged(DataEventBuffer dataEvents) {
      super.onDataChanged(dataEvents);
      for (DataEvent dataEvent : dataEvents) {
         if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
            DataItem item = dataEvent.getDataItem();
            DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
            if (item.getUri().getPath().compareTo("/temperature") == 0) {
               handleTemperature(dataMap);
            } else if (item.getUri().getPath().compareTo("/noise") == 0) {
               handleNoise(dataMap);
            }
         }
      }
   }

   private void handleTemperature(DataMap dataMap) {
      float temperature = dataMap.getFloat("extra.temperature");
      String key = TemperatureActivity.class.getCanonicalName();

      if (Syncher.getInstance().isRegistered(key)) {
         Syncher.getInstance().getSynchable(key).syncData(new Temperature(temperature));
      } else {
         Intent intent = new Intent(this, TemperatureActivity.class);
         intent.putExtra("extra.temperature", temperature);
         intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
         startActivity(intent);
      }
   }

   private void handleNoise(DataMap dataMap) {
      float noise = dataMap.getFloat("extra.noise");
      String key = NoiseActivity.class.getCanonicalName();

      if (Syncher.getInstance().isRegistered(key)) {
         Syncher.getInstance().getSynchable(key).syncData(new Noise(noise));
      } else {
         Intent intent = new Intent(this, NoiseActivity.class);
         intent.putExtra("extra.noise", noise);
         intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
         startActivity(intent);
      }
   }
}
