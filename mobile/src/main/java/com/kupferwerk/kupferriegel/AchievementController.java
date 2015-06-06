package com.kupferwerk.kupferriegel;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

public class AchievementController {

   public static final int ADDICTED = 100;
   public static final int BEGINNER = 1;
   public static final int DEALER = 15;

   public void check(GoogleApiClient apiClient, Context context) {
      final SharedPreferences prefs =
            context.getSharedPreferences(MainActivity.SHAKES, Context.MODE_PRIVATE);
      final int shakes = prefs.getInt(MainActivity.SHAKE_COUNT, 0);
      if (shakes >= BEGINNER) {
         Games.Achievements.unlock(apiClient, context.getString(R.string.achievement_beginner));
      }
      if (shakes >= DEALER) {
         Games.Achievements.unlock(apiClient, context.getString(R.string.achievement_hug_dealer));
      }
      if (shakes >= ADDICTED) {
         Games.Achievements
               .unlock(apiClient, context.getString(R.string.achievement_cuddle_addicted));
      }
   }
}
