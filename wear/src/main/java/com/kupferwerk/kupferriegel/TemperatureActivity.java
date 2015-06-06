package com.kupferwerk.kupferriegel;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.TextView;

import com.kupferwerk.kupferriegel.status.StatusChecker;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class TemperatureActivity extends Activity {

   public static final String EXTRA_TEMPERATURE = "extra.temperature";

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
            float temperature = getIntent().getFloatExtra(EXTRA_TEMPERATURE, 27.5f);

            background.setBackgroundColor(
                  getResources().getColor(StatusChecker.getColorTemperatur(temperature)));
            value.setText(temperature + "");
         }
      });
   }
}
