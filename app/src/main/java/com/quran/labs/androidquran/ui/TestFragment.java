package com.quran.labs.androidquran.ui;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by AlaaZuhour on 5/5/2017.
 */

public interface TestFragment {
  void viewQuestion(int s,int a);
  TextView getTimeView();
  TextView getScoreView();

}
