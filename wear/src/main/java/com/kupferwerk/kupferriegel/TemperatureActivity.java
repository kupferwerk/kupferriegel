package com.kupferwerk.kupferriegel;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
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

   private long lastUpdate;
   private Handler timer = new Handler() {
      @Override
      public void handleMessage(Message msg) {
         super.handleMessage(msg);
         long now = System.currentTimeMillis();

         if (now - lastUpdate > 15000) {
            Syncher.getInstance().unregister(TemperatureActivity.class.getCanonicalName());
            TemperatureActivity.this.finish();
         } else {
            sendEmptyMessageDelayed(0, 5000);
         }
      }
   };

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      new Handler().postDelayed(new Runnable() {
         @Override
         public void run() {

         }
      }, 5000);

      setContentView(R.layout.act_temperature);
      final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
      stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
         @Override
         public void onLayoutInflated(WatchViewStub stub) {
            ButterKnife.inject(TemperatureActivity.this);
            float temperature = getIntent().getFloatExtra("extra.temperature", -372f);
            setTemperature(temperature);
            timer.sendEmptyMessageDelayed(0, 5000);
            Vibrator v =
                  (Vibrator) TemperatureActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            v.vibrate(500);
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
      lastUpdate = System.currentTimeMillis();
      new Handler(this.getMainLooper()).post(new Runnable() {
         @Override
         public void run() {
            background.setBackgroundColor(
                  getResources().getColor(StatusChecker.getColorTemperatur(temperature)));
            value.setText("Cool down!\n" + temperature + "°C");
         }
      });
   }
}
