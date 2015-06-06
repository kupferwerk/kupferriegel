package com.kupferwerk.kupferriegel.sync;

import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.Wearable;

public class SyncController implements DataApi.DataListener, GoogleApiClient.ConnectionCallbacks,
      GoogleApiClient.OnConnectionFailedListener {

   private GoogleApiClient apiClient;

   public SyncController(Context context) {
      this.apiClient = new GoogleApiClient.Builder(context).addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this).addApi(Wearable.API).build();
   }

   public void connect() {
      apiClient.connect();
   }

   public void disconnect() {
      apiClient.disconnect();
   }

   @Override
   public void onConnected(Bundle bundle) {
      Wearable.DataApi.addListener(apiClient, this);
   }

   @Override
   public void onConnectionFailed(ConnectionResult connectionResult) {

   }

   @Override
   public void onConnectionSuspended(int i) {

   }

   @Override
   public void onDataChanged(DataEventBuffer dataEventBuffer) {

   }
}
