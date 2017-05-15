package com.quran.labs.androidquran.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.quran.labs.androidquran.R;
import com.quran.labs.androidquran.common.QuranText;
import com.quran.labs.androidquran.data.QuranDataProvider;
import com.quran.labs.androidquran.data.QuranInfo;
import com.quran.labs.androidquran.database.DatabaseHandler;
import com.quran.labs.androidquran.database.DatabaseUtils;
import com.quran.labs.androidquran.ui.TestFragment;

import java.util.ArrayList;


public class AyahNextFragment extends Fragment implements TestFragment{

  private OnNextFragmentListener mListener;
  View rootView;
  int suraID;
  int ayah=1;
  String ayahTrue="";
  DatabaseHandler dbHandler;
  public int rID;
  TextView tim;
  public AyahNextFragment() {
    // Required empty public constructor
  }
  public void onStart(){
    super.onStart();
    ImageButton change =(ImageButton) getActivity().findViewById(R.id.change);
    change.setOnClickListener(new View.OnClickListener(){

      @Override
      public void onClick(View v) {
        mListener.onChangeClick(v);
      }
    });
    ImageButton reset =(ImageButton) getActivity().findViewById(R.id.reset);
    reset.setOnClickListener(new View.OnClickListener(){

      @Override
      public void onClick(View v) {
        mListener.onResetClick(v);
      }
    });

  }
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Intent i =getActivity().getIntent();
    suraID=i.getExtras().getInt("sura");
    Log.d("on create",suraID+"");
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    rootView=inflater.inflate(R.layout.fragment_ayah_next, container, false);
    tim=(TextView) rootView.findViewById(R.id.time);
    //viewQuestion(suraID,ayah);
    return rootView;
  }
  @Override
  public void viewQuestion(int suraID, int ayah) {
    int ayat= QuranInfo.getNumAyahs(suraID);
    Log.d("ayahNumber",ayat+"");
    Log.d("surahhhh",suraID+"");

    String ayahTextQ="";
    ayah= (int) Math.floor(Math.random() * ayat+1);
    ArrayList<String> option=new ArrayList<>();

    Log.d("ayahhhh",ayah+"");
    while (ayah==ayat-3 || ayah==ayat || ayah==1 ||ayah==0) {
      ayah = (int) Math.floor(Math.random() * ayat+1);
      Log.d("ayahhhkkkk", ayah + "");
    }
    if(ayah!=ayat-3 && ayah!=ayat && ayah!=1){
       ayahTextQ=handelVerses(suraID,ayah);
      ayahTrue=handelVerses(suraID, ayah+1);
      option.add(handelVerses(suraID, ayah+2));
      option.add(handelVerses(suraID, ayah-1));
      option.add(handelVerses(suraID, ayah+3));
    }
    ArrayList<RadioButton> rad=new ArrayList<>();
    TextView queation=(TextView) rootView.findViewById(R.id.ayah);
    queation.setText(ayahTextQ);
    RadioButton radio1 = (RadioButton) rootView.findViewById(R.id.radioButton1);
    RadioButton radio2 = (RadioButton) rootView.findViewById(R.id.radioButton2);
    RadioButton radio3 = (RadioButton) rootView.findViewById(R.id.radioButton3);
    RadioButton radio4 = (RadioButton) rootView.findViewById(R.id.radioButton4);
    rad.add(radio1);
    rad.add(radio2);
    rad.add(radio3);
    rad.add(radio4);
    int num=(int)Math.floor(Math.random()*4);
    rad.get(num).setText(ayahTrue);
    rID=rad.get(num).getId();
      rad.remove(num);
    for(int i=0;i<rad.size();i++){
          rad.get(i).setText(option.get(i));
    }
    RadioGroup radioGroup = (RadioGroup) rootView.findViewById(R.id.radio_group);

    radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
    {
      public void onCheckedChanged(RadioGroup group, int checkedId) {
        // checkedId is the RadioButton selected
       mListener.onRadioChecked(checkedId);
      }
    });
  }

  private String handelVerses(int sura, int ayah) {

    Cursor c=null;
    String textVerse = "";
    //Log.d("ayah", sura + "   " + ayah);
    try {
      dbHandler= DatabaseHandler.getDatabaseHandler(getActivity(), QuranDataProvider.QURAN_ARABIC_DATABASE);
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
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof OnNextFragmentListener) {
      mListener = (OnNextFragmentListener) context;
    } else {
      throw new RuntimeException(context.toString()
          + " must implement OnFragmentInteractionListener");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mListener = null;
  }
  @Override
  public TextView getTimeView() {
    return tim;
  }

  @Override
  public TextView getScoreView() {
    return (TextView)rootView.findViewById(R.id.score);
  }

  public interface OnNextFragmentListener {
    void onRadioChecked(int v);

    void onChangeClick(View v);
    void onResetClick(View v);
  }

//  public void onRadioButtonClicked(View view) {
//    // Is the button now checked?
//    boolean checked = ((RadioButton) view).isChecked();
//
//    // Check which radio button was clicked
//    switch (view.getId()) {
//      case R.id.radioButton2:
//        if (checked)
//          Toast.makeText(getActivity(), "احسنت اجابة صحيحة" , Toast.LENGTH_SHORT).show();
//
//        break;
//      default:
//    }
//  }
}
