package com.kupferwerk.kupferriegel;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.view.SimpleAnimatorListener;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.kupferwerk.kupferriegel.sync.Hugs;
import com.kupferwerk.kupferriegel.sync.SyncContent;
import com.kupferwerk.kupferriegel.sync.Synchable;
import com.kupferwerk.kupferriegel.sync.Syncher;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class HugActivity extends Activity implements Synchable {

   private static final String KEY_HUGS = "extras.hugs";
   @InjectView (R.id.heart)
   ImageView imgHeart;
   @InjectView (R.id.hugs)
   TextView txtHugs;

   @Override
   public void syncData(final SyncContent content) {
      new Handler(getMainLooper()).post(new Runnable() {
         @Override
         public void run() {
            updateHugs(((Hugs) content).getHugs());
         }
      });
   }

   @Override

   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.act_hug);
      final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
      stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
         @Override
         public void onLayoutInflated(WatchViewStub stub) {
            ButterKnife.inject(HugActivity.this);
            int hugs = getIntent().getIntExtra(KEY_HUGS, 0);
            hugs = 100;
            if (hugs > 0) {
               updateHugs(hugs);
            }
         }
      });
   }

   private void updateHugs(final int count) {
      ObjectAnimator scaleXUp = ObjectAnimator.ofFloat(imgHeart, View.SCALE_X, 0.5f, 1.1f);
      ObjectAnimator scaleYUp = ObjectAnimator.ofFloat(imgHeart, View.SCALE_Y, 0.5f, 1.1f);

      AnimatorSet scaleUp = new AnimatorSet();
      scaleUp.playTogether(scaleXUp, scaleYUp);
      scaleUp.setDuration(4000);
      scaleUp.setInterpolator(new AccelerateDecelerateInterpolator());

      TextAnimator text = new TextAnimator(txtHugs);
      ObjectAnimator setHugs = ObjectAnimator.ofInt(text, "text", 0, count);
      setHugs.setDuration(4000);
      setHugs.setInterpolator(new DecelerateInterpolator());

      AnimatorSet all = new AnimatorSet();

      all.playTogether(scaleUp, setHugs);
      all.start();

      all.addListener(new SimpleAnimatorListener() {
         @Override
         public void onAnimationEnd(Animator animator) {
            txtHugs.setText(count + "");
            startPulseAnimation();
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
      if (pulse != null && pulse.isRunning()) {
         pulse.cancel();
      }
   }

   private AnimatorSet pulse;

   private void startPulseAnimation() {
      ObjectAnimator scaleXUp = ObjectAnimator.ofFloat(imgHeart, View.SCALE_X, 1.1f, 0.99f);
      scaleXUp.setDuration(750);
      ObjectAnimator scaleYUp = ObjectAnimator.ofFloat(imgHeart, View.SCALE_Y, 1.1f, 0.99f);
      scaleYUp.setDuration(750);
      ObjectAnimator scaleXDown = ObjectAnimator.ofFloat(imgHeart, View.SCALE_X, 0.99f, 1.1f);
      scaleXUp.setDuration(750);
      ObjectAnimator scaleYDown = ObjectAnimator.ofFloat(imgHeart, View.SCALE_Y, 0.99f, 1.1f);
      scaleYUp.setDuration(750);

      AnimatorSet pulseUp = new AnimatorSet();
      pulseUp.playTogether(scaleXUp, scaleYUp);
      AnimatorSet pulseDown = new AnimatorSet();
      pulseDown.playTogether(scaleXDown, scaleYDown);

      pulse = new AnimatorSet();
      pulse.playSequentially(pulseUp, pulseDown);
      pulse.addListener(new SimpleAnimatorListener() {
         @Override
         public void onAnimationEnd(Animator animator) {
            super.onAnimationEnd(animator);
            pulse.start();
         }
      });

      pulse.start();
   }

   public class TextAnimator {
      TextView txtHugs;

      public TextAnimator(TextView txtHugs) {
         this.txtHugs = txtHugs;
      }

      public void setText(int count) {
         txtHugs.setText(count + "");
      }
   }
}
