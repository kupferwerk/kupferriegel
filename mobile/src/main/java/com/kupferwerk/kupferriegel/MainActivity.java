package com.kupferwerk.kupferriegel;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

public class MainActivity extends Activity
      implements DataApi.DataListener, GoogleApiClient.ConnectionCallbacks,
      GoogleApiClient.OnConnectionFailedListener {

   private GoogleApiClient apiClient;

   @Override
   public void onConnected(Bundle bundle) {
      PutDataMapRequest mapRequest = PutDataMapRequest.create("/wunderbar");
      mapRequest.getDataMap().putString("Hello", "Hello from my phone");

      PutDataRequest putDataReq = mapRequest.asPutDataRequest();
      PendingResult<DataApi.DataItemResult> pendingResult =
            Wearable.DataApi.putDataItem(apiClient, putDataReq);
   }

   @Override
   public void onConnectionFailed(ConnectionResult connectionResult) {

   }

   @Override
   public void onConnectionSuspended(int i) {

   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.menu_main, menu);
      return true;
   }

   @Override
   public void onDataChanged(DataEventBuffer dataEventBuffer) {

   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      int id = item.getItemId();
      if (id == R.id.action_settings) {
         return true;
      }
      return super.onOptionsItemSelected(item);
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      this.apiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this).addApi(Wearable.API).build();
   }

   @Override
   protected void onResume() {
      super.onResume();
      apiClient.connect();
   }

   @Override
   protected void onPause() {
      super.onPause();
      apiClient.disconnect();
   }
}
