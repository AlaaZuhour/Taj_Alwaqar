package com.quran.labs.androidquran.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.quran.labs.androidquran.R;
import com.quran.labs.androidquran.common.QuranText;
import com.quran.labs.androidquran.data.QuranDataProvider;
import com.quran.labs.androidquran.data.QuranInfo;
import com.quran.labs.androidquran.data.SuraAyah;
import com.quran.labs.androidquran.database.DatabaseHandler;
import com.quran.labs.androidquran.database.DatabaseUtils;
import com.quran.labs.androidquran.ui.PagerActivity;
import com.quran.labs.androidquran.ui.TestFragment;
import it.sephiroth.android.library.tooltip.Tooltip;
import java.util.ArrayList;
import java.util.Collections;


public class AyahOrderFragment extends Fragment implements TestFragment , DialogPop.PopupDialog{

  String ayahText;
  DatabaseHandler dbHandler;
  Intent i;
 public int orgAyah = 0,sura,ayah;
 public TextView view;
  public long mil;
  DialogPop p;
  View rootView;
  boolean isAyahTest=false;
  int suraID;
  public String[] ayahWord;
  TextView tim,score;
  private OnOrderFragmentListener mListener;
  public AyahOrderFragment(){

  }
  public void onStart(){
    super.onStart();
    Button nex=(Button) getActivity().findViewById(R.id.button);

    nex.setOnClickListener(new View.OnClickListener(){

      @Override
      public void onClick(View v) {
        onNextClick(v);
      }
    });
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
    if(getActivity() instanceof PagerActivity) {
      isAyahTest=true;
      sura = ((PagerActivity) getActivity()).getSelectionStart().sura;
      ayah = ((PagerActivity) getActivity()).getSelectionStart().ayah;

    }
    else {
      isAyahTest=false;
      Intent i = getActivity().getIntent();
      SharedPreferences sharedPref;
      sharedPref =getActivity().getPreferences(Context.MODE_PRIVATE);
      SharedPreferences.Editor editor = sharedPref.edit();
      if(i.getExtras()!=null) {
        suraID = i.getExtras().getInt("sura");
        editor.putInt("sura",suraID);

      }
      else
        suraID=sharedPref.getInt("sura",1);


    }

  }
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    rootView =inflater.inflate(R.layout.fragment_ayah_test, container, false);
    tim=(TextView) rootView.findViewById(R.id.time);
    score=(TextView) rootView.findViewById(R.id.score);

    if(isAyahTest){
     LinearLayout time=(LinearLayout) rootView.findViewById(R.id.time_layout);
     LinearLayout button=(LinearLayout) rootView.findViewById(R.id.button_layout);
     time.setVisibility(View.GONE);
     button.setVisibility(View.GONE);
     viewQuestion(sura,ayah);
   }
   else{
     LinearLayout next=(LinearLayout) rootView.findViewById(R.id.next_layout);
     next.setVisibility(View.GONE);
     //viewQuestion(suraID,ayah);
   }
    return rootView;


  }
  @Override
  public void viewQuestion(int sura, int ayah) {
    orgAyah=0;
    long len = 0;
    if(view!=null)
      view.setText("");
    if(!isAyahTest){
      int ayat= QuranInfo.getNumAyahs(suraID);
      ayah= (int) Math.floor(Math.random() * ayat+1);
    }
    ayahText=handelVerses(sura, ayah);
    ayahWord = ayahText.split(" ");
    if(ayahWord != null)
      len=ayahWord.length;
    if(len<8)
      mil=20000;
    else if(8<len && len<20)
      mil=30000;
    else if(20<len && len<40)
      mil=50000;
    else if(40<len && len<60 )
      mil=70000;
    else
      mil=100000;
    ArrayList<String> ayahList=new ArrayList<>();
    for(int i=0;i<ayahWord.length;i++)
      ayahList.add(ayahWord[i]);
    Collections.shuffle(ayahList);

    ArrayAdapter<String> ayahAdapter =
        new ArrayAdapter<String>(getActivity(), 0, ayahList) {
          @Override
          public View getView(int position, View convertView, ViewGroup parent) {
            String currentWord = ayahList.get(position);
            if (convertView == null)
              convertView = getActivity().getLayoutInflater().inflate(R.layout.ayah_word, null, false);
            TextView ye = (TextView) convertView.findViewById(R.id.textView2);
            ye.setText(currentWord);
            return convertView;
          }
          @Override
          public String getItem(int position) {
            return ayahList.get(position);
          }

        };
    final GridView gridView = (GridView) rootView.findViewById(R.id.ayah_listview);
    gridView.setAdapter(ayahAdapter);
   StringBuilder ayha=new StringBuilder();
    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      public void onItemClick(AdapterView<?> parent, View v,int position, long id) {
        String currAyah= (String) parent.getItemAtPosition(position);
         Log.d("POSTIONN","this is postion: "+id);
        if(currAyah.equals(ayahWord[orgAyah])) {
          if(orgAyah<=ayahWord.length) {
            orgAyah++;
          }

          ayha.append(currAyah + " ");
          ayahList.remove(position);
          ayahAdapter.notifyDataSetChanged();

          //v.setVisibility(View.GONE);
          view = (TextView) rootView.findViewById(R.id.ayah);
          view.setText(ayha);
          view.setMovementMethod(new ScrollingMovementMethod());
        }
        else
          Toast.makeText(getActivity(), "جواب خاطئ، حاول مرة أخرى" , Toast.LENGTH_SHORT).show();
        if(orgAyah == ayahWord.length)
          mListener.onOrderEnd();
        if(orgAyah == ayahWord.length && isAyahTest) {
          p = new DialogPop();
          p.show(getFragmentManager(), " ");
        }

      }
    });




  }

  @Override
  public TextView getTimeView() {
    return tim;
  }

  @Override
  public TextView getScoreView() {
    return score;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof OnOrderFragmentListener) {
      mListener = (OnOrderFragmentListener) context;
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
  public void onNextClick(View v){
    view.setText("");
    orgAyah=0;
        final int ayat = QuranInfo.getNumAyahs(sura);
         SuraAyah s;
        if (ayah + 1 <= ayat) {
          s = new SuraAyah(sura, ayah + 1);
        } else if (sura < 114) {
          s = new SuraAyah(sura + 1, 1);
        } else {
          return;
      }
      sura=s.sura;
      ayah=s.ayah;
      viewQuestion(sura,ayah);

  }
  private String handelVerses(int sura, int ayah) {

    Cursor c=null;
    String textVerse = "";
    Log.d("ayah", sura + "   " + ayah);
    try {
      dbHandler= DatabaseHandler.getDatabaseHandler(getActivity(), QuranDataProvider.QURAN_ARABIC_DATABASE);
      c=dbHandler.getVerses(sura,ayah,sura,ayah,DatabaseHandler.VERSE_TABLE);
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
  public Dialog createDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setMessage(R.string.dialog)
        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            dialog.dismiss();
          }
        });

    return builder.create();
  }

  public interface OnOrderFragmentListener{
    void onOrderEnd();

    void onChangeClick(View v);

    void onResetClick(View v);
  }
}


