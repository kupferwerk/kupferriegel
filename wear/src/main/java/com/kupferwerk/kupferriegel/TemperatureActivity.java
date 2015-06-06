package com.kupferwerk.kupferriegel;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.TextView;

import com.kupferwerk.kupferriegel.status.StatusChecker;
import com.kupferwerk.kupferriegel.sync.SyncContent;
import com.kupferwerk.kupferriegel.sync.Synchable;
import com.kupferwerk.kupferriegel.sync.Syncher;
import com.kupferwerk.kupferriegel.sync.Temperature;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class TemperatureActivity extends Activity implements Synchable {

   @InjectView (R.id.background)
   View background;
   @InjectView (R.id.value)
   TextView value;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.act_temperature);
      final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
      stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
         @Override
         public void onLayoutInflated(WatchViewStub stub) {
            ButterKnife.inject(TemperatureActivity.this);
            float temperature = getIntent().getFloatExtra("extra.temperature", -372f);
            setTemperature(temperature);
         }
      });
   }

   @Override
   protected void onResume() {
      super.onResume();
      Syncher.getInstance().register(getClass().getCanonicalName(), this);
   }

   @Override
   protected void onPause() {
      super.onPause();
      Syncher.getInstance().unregister(getClass().getCanonicalName());
   }

   @Override
   public void syncData(SyncContent content) {
      setTemperature(((Temperature) content).getTemperature());
   }

   private void setTemperature(final float temperature) {
      new Handler(this.getMainLooper()).post(new Runnable() {
         @Override
         public void run() {
            background.setBackgroundColor(
                  getResources().getColor(StatusChecker.getColorTemperatur(temperature)));
            value.setText(temperature + "");
         }
      });
   }
}