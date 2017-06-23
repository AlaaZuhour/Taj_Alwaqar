package com.quran.labs.androidquran.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.CountDownTimer;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.quran.labs.androidquran.R;
import com.quran.labs.androidquran.common.QuranText;
import com.quran.labs.androidquran.data.QuranDataProvider;
import com.quran.labs.androidquran.database.DatabaseHandler;
import com.quran.labs.androidquran.database.DatabaseUtils;

import java.util.ArrayList;
import com.quran.labs.androidquran.ui.TestFragment;

import it.sephiroth.android.library.tooltip.Tooltip;

public class AyahEndFragment extends Fragment implements TestFragment {

  private OnEndFragmentListener mListener;
  View rootView;
 public int suraID,ayah=1, tID;
  ArrayList<Integer[]> ends;
  DatabaseHandler dbHandler;
  String[] ayahEnds1,ayahEnds2,ayahEnds;
  ImageButton change;
  public AyahEndFragment() {
    // Required empty public constructor
  }
  public void onStart(){
    super.onStart();
    ImageButton change =(ImageButton) getActivity().findViewById(R.id.change);
    Tooltip.make(getActivity(),
        new Tooltip.Builder(500)
            .anchor(change, Tooltip.Gravity.TOP)
            .closePolicy(new Tooltip.ClosePolicy()
                .insidePolicy(true, false)
                .outsidePolicy(true, false), 4000)
            .activateDelay(2000)
            .showDelay(2000)
            .text("بإمكانك تبديل السؤال 3 مرات فقط خلال الإختبار")
            .maxWidth(600)
            .withArrow(true)
            .withOverlay(true)
            .withStyleId(R.style.tooltip).build()
    ).show();
    change.setOnClickListener(new View.OnClickListener(){

      @Override
      public void onClick(View v) {
        mListener.onChangeClick(v);
      }
    });
    ImageButton reset =(ImageButton) getActivity().findViewById(R.id.reset);
    CountDownTimer c=new CountDownTimer(4000,1000) {
      @Override
      public void onTick(long millisUntilFinished) {
      }
      @Override
      public void onFinish() {
        Tooltip.make(getActivity(),
            new Tooltip.Builder(500)
                .anchor(reset, Tooltip.Gravity.TOP)
                .closePolicy(new Tooltip.ClosePolicy()
                    .insidePolicy(true, false)
                    .outsidePolicy(true, false), 4000)
                .activateDelay(2000)
                .showDelay(2000)
                .text("يمكنك إعادة الإختبار من البداية")
                .maxWidth(600)
                .withArrow(true)
                .withOverlay(true)
                .withStyleId(R.style.tooltip).build()
        ).show();
      }
    }.start();

    reset.setOnClickListener(new View.OnClickListener(){

      @Override
      public void onClick(View v) {
        mListener.onResetClick(v);
      }
    });

  }
  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    ends=new ArrayList<>();
    ends.add(new Integer[1]);
    ends.add(new Integer[1]);
    Integer[] sra1=new Integer[]{54,115,127,128,129,137,143,158,182,199,209,218,224,225,240,244,261,263,267,160};
    ends.add(sra1);
    Integer[] sra2=new Integer[]{6,18,35,62,73,126,129,155,121,89,31,34};
    ends.add(sra2);
    Integer[] sra3=new Integer[]{16,17,24,26,35,56,58,64,96,99,100,104,110,111,129,130,131,134,147,148,149,152,170};
    ends.add(sra3);
    Integer[] sra4=new Integer[]{34,38,39,76,101,118,98,74};
    ends.add(sra4);
    Integer[] sra5=new Integer[]{13,18,54,83,96,103,115,128,139,145,165};
    ends.add(sra5);
    ayahEnds1=new String[]{"الرحمن الرحيم","العليم القدير","التواب الرحيم"," العزيز الحميد","الواحد القهار","الغفور الودود",
        "العزيز الحكيم","العزيز العليم","الغفور الرحيم","العليم الحكيم","الحكيم الخبير","السميع العليم","الغني الحميد",
        "العليم الخبير","الفتاح العليم","الخلاق العليم","الرحيم الغفور","الكبير المتعال","العزيز الرحيم","العلي العظيم"};
    ayahEnds2=new String[]{"غني حليم","غفورا شكورا","واسع حكيم","واسع عليم","عفوا قديرا","رؤوف رحيم","قوي عزيز","عفوا غفورا",
        "غفورا حليم","سميع عليم","غفور رحيم","عزيز حكيم","عليم حليم"};
    Intent i =getActivity().getIntent();
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

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    rootView=inflater.inflate(R.layout.fragment_ayah_end, container, false);
    change=(ImageButton) rootView.findViewById(R.id.change);
    //
    return rootView;
  }
  @Override
  public void viewQuestion(int suraID, int ayah) {
   try {
     Integer[] current = ends.get(suraID);
     int ran = (int) Math.floor(Math.random() * current.length);
     ayah = current[ran];
   }
   catch (Exception e){

   }
    String ayahText=handelVerses(suraID,ayah);
    Log.d("ayahText",ayahText);
    String sb="";
    String sb1="";
    String[] word=ayahText.split(" ");
    for(int i=0; i<word.length;i++){
      if(i<word.length-2)
        sb+=word[i]+" ";
      else
        sb1+=word[i]+" ";
    }
    String endTrue=sb1;
    Log.d("endText",endTrue);
    ArrayList<TextView> option=new ArrayList<>();
    TextView ayahQuestion=(TextView) rootView.findViewById(R.id.textView10);
    ayahQuestion.setText(sb);
    ayahQuestion.setMovementMethod(new ScrollingMovementMethod());
    TextView option1=(TextView) rootView.findViewById(R.id.textView6);
    TextView option2=(TextView) rootView.findViewById(R.id.textView7);
    TextView option3=(TextView) rootView.findViewById(R.id.textView8);
    TextView option4=(TextView) rootView.findViewById(R.id.textView9);
    option.add(option1);
    option.add(option2);
    option.add(option3);
    option.add(option4);

    int num=(int)Math.floor(Math.random()*4);
    option.get(num).setText(endTrue);
    tID=option.get(num).getId();
    option.get(num).setOnClickListener(new View.OnClickListener(){

      @Override
      public void onClick(View v) {
        mListener.onOptionClick(v);
      }
    });
    String al="ال";
    int j=0;
    option.remove(num);
    if(endTrue.substring(0,2).equals(al))
      ayahEnds = ayahEnds1;
    else
    ayahEnds=ayahEnds2;
      j = (int) Math.floor(Math.random() * ayahEnds.length);
      for (int i = 0; i < option.size(); i++) {
        if (!ayahEnds[j].equals(endTrue))
          option.get(i).setText(ayahEnds[j]);
        option.get(i).setOnClickListener(new View.OnClickListener() {

          @Override
          public void onClick(View v) {
            mListener.onOptionClick(v);
          }
        });
        j = (int) Math.floor(Math.random() * ayahEnds.length);
      }


  }
  @Override
  public TextView getTimeView() {
    return (TextView)rootView.findViewById(R.id.time);
  }

  @Override
  public TextView getScoreView() {
    return (TextView)rootView.findViewById(R.id.score);
  }


  private View.OnClickListener onOptionClick = v -> {

    if(v.getId()==tID)
      Toast.makeText(getActivity(), "احسنت اجابة صحيحة", Toast.LENGTH_SHORT).show();
    else
      Toast.makeText(getActivity(), "إجابة خاطئة حاول مرة أخرى", Toast.LENGTH_SHORT).show();


  };

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof OnEndFragmentListener) {
      mListener = (OnEndFragmentListener) context;
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

  public interface OnEndFragmentListener {
    // TODO: Update argument type and name
    void onOptionClick(View v);

    void onChangeClick(View v);

    void onResetClick(View v);
  }
  private String handelVerses(int sura, int ayah) {

    Cursor c=null;
    String textVerse = "";
    //Log.d("ayah", sura + "   " + ayah);
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
}
