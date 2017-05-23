package com.quran.labs.androidquran.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.quran.labs.androidquran.R;
import com.quran.labs.androidquran.common.QuranText;
import com.quran.labs.androidquran.data.QuranDataProvider;
import com.quran.labs.androidquran.data.QuranInfo;
import com.quran.labs.androidquran.data.SuraAyah;
import com.quran.labs.androidquran.database.DatabaseHandler;
import com.quran.labs.androidquran.database.DatabaseUtils;
import com.quran.labs.androidquran.database.EnglishTextDBHelper;
import com.quran.labs.androidquran.database.SimilarityDBHelper;
import com.quran.labs.androidquran.database.TranslationsDBAdapter;
import com.quran.labs.androidquran.model.translation.ArabicDatabaseUtils;
import com.quran.labs.androidquran.presenter.translation.InlineTranslationPresenter;
import com.quran.labs.androidquran.ui.PagerActivity;
import com.quran.labs.androidquran.util.QuranSettings;
import com.quran.labs.androidquran.widgets.InlineTranslationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

//import com.quran.labs.androidquran.R;

public class AyahTranslationFragment extends AyahActionFragment {

  private View translationControls;
  private TextView text;
  private SeekBar similarRate;
  private TextView simTextView;
  ArrayList<String> item_data;
  ArrayList<String> allSimilarity;
  ArrayList<String> similarityWithRate;
  String currAyahText;
  SuraAyah start;
  PagerActivity activity;
  @Inject TranslationsDBAdapter translationsDBAdapter;
  @Inject ArabicDatabaseUtils arabicDatabaseUtils;
  @Inject QuranSettings quranSettings;
  @Inject InlineTranslationPresenter translationPresenter;
  DatabaseHandler dbHandler;
   View view=null;
  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    ((PagerActivity) getActivity()).getPagerActivityComponent().inject(this);
  }

  @Override
  public View onCreateView(LayoutInflater inflater,
                           ViewGroup container, Bundle savedInstanceState) {
    view = inflater.inflate(
        R.layout.translation_panel, container, false);

    similarRate = (SeekBar) view.findViewById(R.id.similarity);
    simTextView = (TextView) view.findViewById(R.id.similarRate);



    int rate = 10 + similarRate.getProgress();

    simTextView.setText(rate + "");

    translationControls = view.findViewById(R.id.controls);
    final View next = translationControls.findViewById(R.id.next_ayah);
    next.setOnClickListener(onClickListener);

    final View prev = translationControls.findViewById(R.id.previous_ayah);
    prev.setOnClickListener(onClickListener);
    text=(TextView) view.findViewById(R.id.ayah_text);

    try {
      EnglishTextDBHelper englishTextDBHelper = new EnglishTextDBHelper(getContext(), 1);
      SQLiteDatabase textDB = englishTextDBHelper.getReadableDatabase();
    } catch (Exception ex){
      Log.e("error", "englishText");
    }

    try {
      SimilarityDBHelper sim = new SimilarityDBHelper(getContext(), 2);
      SQLiteDatabase simDB = sim.getReadableDatabase();
    } catch (Exception ex){
      Log.e("error", "similarity");
    }
    activity = (PagerActivity) getActivity();
    start = activity.getSelectionStart();
    text.setText(handelVerses(start.sura, start.ayah));

    getSimilarities(start.sura, start.ayah);
    currAyahText = getEnglishText(start.sura, start.ayah);
    Log.d("", currAyahText);

    similarityWithRate = new ArrayList<>();
    item_data=new ArrayList<>();

    for(int i = 0; i < allSimilarity.size(); i++){
      Log.d(allSimilarity.size()+"","I'm in check similar rate");
      String temp = allSimilarity.get(i);
      String[] tempSimilar = temp.split(":");
      similarities(currAyahText, tempSimilar[2], rate, Integer.parseInt(tempSimilar[0]), Integer.parseInt(tempSimilar[1]));
    }

    for (int i = 0 ; i < similarityWithRate.size(); i++){
      Log.d(similarityWithRate.size()+"", similarityWithRate.get(i));
    }
    updateListView();

    similarRate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int rate = 10 + similarRate.getProgress();
        simTextView.setText(rate+"");
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {

      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {

        int rate = 10 + similarRate.getProgress();
        allSimilarity = new ArrayList<String>();
        getSimilarities(start.sura, start.ayah);
        similarityWithRate = new ArrayList<String>();

        item_data=new ArrayList<>();
        for(int i = 0; i < allSimilarity.size(); i++){
          Log.d("","I'm in check similar rate");
          String temp = allSimilarity.get(i);
          String[] tempSimilar = temp.split(":");
          similarities(currAyahText, tempSimilar[2], rate, Integer.parseInt(tempSimilar[0]), Integer.parseInt(tempSimilar[1]));
        }

        updateListView();

      }
    });

    return view;
  }

  private void updateListView() {
    ArrayAdapter<String> simiAdapter =
        new ArrayAdapter<String>(getActivity(), 0, similarityWithRate) {
          @Override
          public View getView(int position, View convertView, ViewGroup parent) {
            String currentSim="";
           if(position<similarityWithRate.size())
             currentSim=similarityWithRate.get(position);
            if(convertView == null)
              convertView = getActivity().getLayoutInflater().inflate(R.layout.exp_header, null, false);
            TextView ye=(TextView) convertView.findViewById(R.id.item);
            ye.setText(currentSim);
            TextView yee=(TextView) convertView.findViewById(R.id.sub_item);
           if(position<item_data.size())
            yee.setText(item_data.get(position));

            return convertView;
          }
        };
    final ListView listView=(ListView) view.findViewById(R.id.similar_list);
    listView.setAdapter(simiAdapter);
  }


  public void similarities(String ayahOrg, String ayahSim, int rate, int simIDSura, int simIDAyah) {

    int m = numberOfWords(ayahSim);
    int n = numberOfWords(ayahOrg);
    int k = longestCommonSub(ayahOrg, ayahSim);

    float x = 2 * k;
    float y = m + n;
    float z = x / y;

    Log.d("", z+"");

    int rateOfSimilarity = (int) (z * 100);
    Log.d("",rateOfSimilarity+"");
    if(rateOfSimilarity >= rate){

      Log.d("",QuranInfo.getAyahString(simIDSura,simIDAyah,getContext()));

      //put in list of similarities
      String text = handelVerses(simIDSura, simIDAyah);
      similarityWithRate.add(text);
      item_data.add(QuranInfo.getAyahString(simIDSura,simIDAyah,getContext()));
    }

  }

  private int numberOfWords(String str) {
    if (str.length() != 0) {
      Log.d("",str.split(" ").length + "");
      return str.split(" ").length;
    }

    return -1;
  }

  private int longestCommonSub(String ayahOrg, String ayahSim) {
    int length = 0;
    String[] ayahOrgTemp = ayahOrg.split(" ");
    String[] ayahSimTemp = ayahSim.split(" ");
    int m = ayahOrgTemp.length;
    int n = ayahSimTemp.length;
    int[][] cost = new int[m + 1][n + 1];
    for (int i = m - 1; i >= 0; i--) {
      for (int j = n - 1; j >= 0; j--) {
        if (ayahOrgTemp[i].equals(ayahSimTemp[j])) {
          cost[i][j] = cost[i + 1][j + 1] + 1;
        } else {
          cost[i][j] = Math.max(cost[i + 1][j], cost[i][j + 1]);
        }
      }
    }

    int i = 0, j = 0;
    while (i < m && j < n) {
      if (ayahOrgTemp[i].equals(ayahSimTemp[j])) {
        length++;
        i++;
        j++;
      } else if (cost[i + 1][j] >= cost[i][j + 1]) {
        i++;
      } else {
        j++;
      }
    }
    Log.d("",length +"");

    return length;

  }

  private void getSimilarities(int sura, int ayah) {
    SimilarityDBHelper simDB = new SimilarityDBHelper(getContext(), 2);
    Log.d("" , "getSimi");
    HashMap<Integer, Integer> similaritiesID = simDB.getSimilarities(sura, ayah);
    allSimilarity = new ArrayList<>();
    for(Map.Entry<Integer, Integer> entry: similaritiesID.entrySet()){
      Log.d("Sim", entry.getKey() + "  " + entry.getValue());
      allSimilarity.add(entry.getKey()+ ":" + entry.getValue() + ":" +
          getEnglishText(entry.getKey(), entry.getValue()));
    }
    Log.d(allSimilarity.size()+"","I'm in check similar rate");

  }

  private String getEnglishText(int sura, int ayah) {
    String text = "";
    EnglishTextDBHelper engDB = new EnglishTextDBHelper(getContext(), 2);
    text = engDB.getAyahText(sura, ayah);

    return text;
  }

  private String handelVerses(int sura, int ayah) {

    Cursor c=null;
    String textVerse = "";
    Log.d("ayah", sura + "   " + ayah);
    try {
      dbHandler= DatabaseHandler.getDatabaseHandler(getContext(),QuranDataProvider.QURAN_ARABIC_DATABASE);
      c=dbHandler.getVerses(sura,ayah,sura,ayah,DatabaseHandler.ARABIC_TEXT_TABLE);
      while (c.moveToNext()) {
        QuranText verse = new QuranText(c.getInt(1), c.getInt(2), c.getString(3));
        textVerse = verse.text;
        //  text.setText(verse.text);
      }
    } catch (Exception e) {
      // no op
    } finally {
      DatabaseUtils.closeCursor(c);
    }
    return textVerse;
  }

  @Override
  public void onResume() {
    super.onResume();
  }

  @Override
  public void onPause() {
    super.onPause();
  }

  private View.OnClickListener onClickListener = v -> {
    final Activity activity = getActivity();
    if (activity instanceof PagerActivity) {
      final PagerActivity pagerActivity = (PagerActivity) activity;

      switch (v.getId()) {
        case R.id.next_ayah:
          pagerActivity.nextAyah();
          break;
        case R.id.previous_ayah:
          pagerActivity.previousAyah();
          break;
      }
    }
  };

  public void refreshView() {
    start = activity.getSelectionStart();
    if (start == null || end == null) {
      return;
    } else
      text.setText(handelVerses(start.sura,start.ayah));
    allSimilarity = new ArrayList<>();
    getSimilarities(start.sura, start.ayah);

    currAyahText = getEnglishText(start.sura, start.ayah);
    int rate = 10 + similarRate.getProgress();
    similarityWithRate = new ArrayList<>();
    item_data=new ArrayList<>();
    for(int i = 0; i < allSimilarity.size(); i++){
      Log.d(allSimilarity.size()+"","I'm in check similar rate");
      String temp = allSimilarity.get(i);
      String[] tempSimilar = temp.split(":");
      similarities(currAyahText, tempSimilar[2], rate, Integer.parseInt(tempSimilar[0]), Integer.parseInt(tempSimilar[1]));
    }
    updateListView();
  }

}
