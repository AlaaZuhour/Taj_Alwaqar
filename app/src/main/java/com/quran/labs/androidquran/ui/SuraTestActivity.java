package com.quran.labs.androidquran.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.quran.labs.androidquran.QuranDataActivity;
import com.quran.labs.androidquran.R;
import com.quran.labs.androidquran.data.QuranInfo;
import com.quran.labs.androidquran.ui.fragment.AyahEndFragment;
import com.quran.labs.androidquran.ui.fragment.AyahNextFragment;
import com.quran.labs.androidquran.ui.fragment.AyahOrderFragment;
import com.quran.labs.androidquran.ui.fragment.DialogPop;
import com.quran.labs.androidquran.ui.fragment.SuraListFragment;

public class SuraTestActivity extends Activity implements AyahEndFragment.OnEndFragmentListener,
                  AyahOrderFragment.OnOrderFragmentListener, AyahNextFragment.OnNextFragmentListener,DialogPop.PopupDialog {
  int ayah,sura;
  CountDownTimer countDownTimer;
  com.quran.labs.androidquran.ui.TestFragment fm;
  boolean count=true;
  public long mil=30000;
  DialogPop p;
  SharedPreferences sharedPref;
  public int fragmentCount = 0, questionCount = 0,score=0, len=0;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sura_test);
    sharedPref =this.getPreferences(Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPref.edit();
    fragmentCount = 0; questionCount = 0; score=0; len=0;
      if(getIntent().getExtras()!=null) {
        sura = getIntent().getExtras().getInt("sura");
        editor.putInt("sura",sura);
      }
      else
        sura=sharedPref.getInt("sura",1);

      }
  @Override
  public void onSaveInstanceState(Bundle onState){
   onState.putInt("sura",sura);
    fragmentCount = 0; questionCount = 0; score=0; len=0;
    super.onSaveInstanceState(onState);
  }
  private void createCountDownTimer(long mill) {
    countDownTimer = new CountDownTimer(mill, 1000) {
      public void onTick(long millisUntilFinished) {
        if(fm.getTimeView()!=null)
          fm.getTimeView().setText("" + millisUntilFinished / 1000);

      }

      public void onFinish() {
        count=true;

        if(fragmentCount==2 && questionCount==5){
          p = new DialogPop();
          p.show(getFragmentManager(), " ");
        }
        if(questionCount == 5)
          replaceFragment(++fragmentCount);
        else
          questionCount++;
        Log.d("question count",questionCount+"");
        fm.viewQuestion(sura, ayah);
        if(fragmentCount==2) {
          mil=((AyahOrderFragment)fm).mil;
        }
        countDownTimer.cancel();
        createCountDownTimer(mil);
      }
    }.start();

  }
  public void onStartClick(View v) {


    replaceFragment(fragmentCount);
  }
  private void replaceFragment(int fragmentCount) {
    switch (fragmentCount){
      case 0:
        fm=new AyahEndFragment();
        break;
      case 1:
        fm=new AyahNextFragment();
        break;
      case 2:
        fm=new AyahOrderFragment();
        break;

    }
    questionCount = 0;
    android.app.FragmentManager fragmentmanager;
    FragmentTransaction fragmentTransaction ;
    fragmentmanager= getFragmentManager();
    fragmentTransaction = fragmentmanager.beginTransaction();
    fragmentTransaction.replace(android.R.id.content ,(Fragment) fm);
    fragmentTransaction.commit();
    mil=40;

    CountDownTimer c=new CountDownTimer(40,1000) {
      @Override
      public void onTick(long millisUntilFinished) {
      }
      @Override
      public void onFinish() {

        questionCount++;
        fm.viewQuestion(sura, ayah);
        if(fragmentCount==2) {
          mil=((AyahOrderFragment)fm).mil;
        }
        else
          mil=30000;
        if(countDownTimer!=null)
        countDownTimer.cancel();
        createCountDownTimer(mil);
      }
    }.start();

  }
  @Override
  public void onOptionClick(View v) {
    if(v.getId()== ((AyahEndFragment)fm).tID) {
      Toast.makeText(this, "احسنت اجابة صحيحة", Toast.LENGTH_SHORT).show();
      score+=10;
      fm.getScoreView().setText(score+"");

      if(questionCount == 5)
        replaceFragment(++fragmentCount);
      else {
        questionCount++;
        fm.viewQuestion(sura, ayah);
        mil=30000;
        countDownTimer.cancel();
        createCountDownTimer(mil);
      }

    }
    else {
      Toast.makeText(this, "إجابة خاطئة", Toast.LENGTH_SHORT).show();
      if (questionCount == 5)
        replaceFragment(++fragmentCount);
      else {
        questionCount++;
        fm.viewQuestion(sura, ayah);
        mil=30000;
        countDownTimer.cancel();
        createCountDownTimer(mil);
      }
    }

  }

  @Override
  public void onChangeClick(View v) {
    questionCount--;
    score-=3;
    fm.getScoreView().setText(score+"");
    questionCount++;
    fm.viewQuestion(sura, ayah);
    if(fragmentCount==2) {
      mil=((AyahOrderFragment)fm).mil;
    }
    if(fragmentCount==2 && questionCount==5){
      p = new DialogPop();
      p.show(getFragmentManager(), " ");
    }
    countDownTimer.cancel();
    createCountDownTimer(mil);
  }

  @Override
  public void onResetClick(View v) {
    Intent i =new Intent(SuraTestActivity.this,SuraTestActivity.class);
    i.putExtra("sura",sura);
    startActivity(i);
  }

  @Override
  public void onOrderEnd() {

    score+=10;
    fm.getScoreView().setText(score+"");
    if(questionCount == 5)
      replaceFragment(++fragmentCount);
    else {
      questionCount++;
      fm.viewQuestion(sura, ayah);
      if(fragmentCount==2) {
        mil=((AyahOrderFragment)fm).mil;
      }
      if(fragmentCount==2 && questionCount==5){
        p = new DialogPop();
        p.show(getFragmentManager(), " ");
      }
      countDownTimer.cancel();
      createCountDownTimer(mil);
    }
  }

  @Override
  public void onRadioChecked(int v) {
    if(v ==((AyahNextFragment)fm).rID) {
      Toast.makeText(this, "احسنت اجابة صحيحة", Toast.LENGTH_SHORT).show();
      score+=10;
      fm.getScoreView().setText(score+"");
      if(questionCount == 5)
        replaceFragment(++fragmentCount);
      else {
        questionCount++;
        fm.viewQuestion(sura, ayah);
        mil=30000;
        countDownTimer.cancel();
        createCountDownTimer(mil);
      }
    }
    else {
      Toast.makeText(this, "اجابة خاطئة", Toast.LENGTH_SHORT).show();
      if (questionCount == 5)
        replaceFragment(++fragmentCount);
      else {
        questionCount++;
        fm.viewQuestion(sura, ayah);
        mil=30000;
        countDownTimer.cancel();
        createCountDownTimer(mil);
      }
    }

  }

  @Override
  public Dialog createDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage("رائع! لقد أنهيت إختبار "+ QuranInfo.getSuraName(this,sura,true)+" نتيجتك هي: "+score)
        .setPositiveButton("قائمة السور", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
           Intent i=new Intent(SuraTestActivity.this,QuranDataActivity.class);
            startActivity(i);
          }
        });

    return builder.create();
  }
}

