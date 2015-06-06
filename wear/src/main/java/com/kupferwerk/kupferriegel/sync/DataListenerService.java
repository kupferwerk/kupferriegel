package com.kupferwerk.kupferriegel.sync;

import android.content.Intent;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;
import com.kupferwerk.kupferriegel.ShowDataActivity;
import com.kupferwerk.kupferriegel.TemperatureActivity;

public class DataListenerService extends WearableListenerService {

   public static String TEMPERATURE = "temperature";

   @Override
   public void onDataChanged(DataEventBuffer dataEvents) {
      super.onDataChanged(dataEvents);
      

      for (DataEvent dataEvent : dataEvents) {
         if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
            DataItem item = dataEvent.getDataItem();

            if (item.getUri().getPath().compareTo("/temperature") == 0) {
               DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
               handleTemperature(dataMap);
            }
         }
      }
   }

   private void handleTemperature(DataMap dataMap) {
      Intent intent = new Intent(this, TemperatureActivity.class);
      float temperature = dataMap.getFloat(TEMPERATURE);
      intent.putExtra(TemperatureActivity.EXTRA_TEMPERATURE, temperature);

      startActivity(intent);
   }
}
