package com.kupferwerk.kupferriegel;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ShowDataActivity extends Activity {

   @InjectView (R.id.text)
   TextView dataView;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.act_show_data);
      final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
      stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
         @Override
         public void onLayoutInflated(WatchViewStub stub) {
            ButterKnife.inject(ShowDataActivity.this);
            dataView.setText("Waiting for data");
         }
      });
   }
}
