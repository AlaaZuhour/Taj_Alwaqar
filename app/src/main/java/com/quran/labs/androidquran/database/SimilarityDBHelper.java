package com.quran.labs.androidquran.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;

import com.quran.labs.androidquran.ui.fragment.AyahTranslationFragment;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import timber.log.Timber;

/**
 * Created by AlaaZuhour on 4/8/2017.
 */

public class SimilarityDBHelper extends SQLiteOpenHelper {
  private static final String DB_NAME="tajalwaqar";

  String myJSON;
  JSONArray quranData = null;

  static final String TABLE_NAME = "similarities";
  static final String SURAID = "SuraID";
  static final String VERSEID = "VerseID";
  static final String SIMSURAID = "SimSuraID";
  static final String SIMVERSEID = "SimVerseID";


  private static final String CREATE_SIMILAR_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
      SIMSURAID + " integer not null," +
      SIMVERSEID + " integer not null," +
      VERSEID + " integer not null," +
      SURAID + " integer not null);";


  public SimilarityDBHelper(Context context, int version) {
    super(context, DB_NAME, null, version);
    Log.d("fdfd", "hi i'm in constructor");
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    Log.d("fdfd", "hi i'm in on create");
    db.execSQL(CREATE_SIMILAR_TABLE);
    // db.execSQL(CREATE_SIMILAR_TABLE);
    Log.d("fdfd", "hi i'm in on create222");
    getData("http://tajalwaqar.atwebpages.com/getSimilarVerse.php",db);

  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    Log.d("fdfd", "hi i'm in onUpgrade");
    db.execSQL(CREATE_SIMILAR_TABLE);
    getData("http://tajalwaqar.atwebpages.com/getSimilarVerse.php",db);



  }

  public void getData(String url, SQLiteDatabase db){
    class GetDataJSON extends AsyncTask<String, Void, String> {

      @Override
      protected String doInBackground(String... params) {
        Log.d("fdfd", "hi i'm in getData");
        DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
        HttpPost httppost = new HttpPost(url);

        // Depends on your web service
        httppost.setHeader("Content-type", "application/json");

        InputStream inputStream = null;
        String result = null;
        try {
          HttpResponse response = httpclient.execute(httppost);
          HttpEntity entity = response.getEntity();

          inputStream = entity.getContent();
          // json is UTF-8 by default
          BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
          StringBuilder sb = new StringBuilder();

          String line = null;
          char cr;
          while((cr = (char) reader.read()) != '{'){

          }
          while ((line = reader.readLine()) != null)
          {
            sb.append(line + "\n");
          }
          result = sb.toString();
        } catch (Exception e) {
          // Oops
          Log.d("error error error","error");
        }
        finally {
          try{if(inputStream != null)inputStream.close();}catch(Exception squish){}
        }
        Log.d("fd", result);

        return "{" + result;
      }

      @Override
      protected void onPostExecute(String result){
        myJSON=result;
        try {
          JSONObject jobjQuran = new JSONObject(myJSON);
          Log.d("fdfd", "hi i'm in onPostExecute");
          quranData = jobjQuran.getJSONArray("result");
          for(int i=0;i<quranData.length();i++){
            JSONObject c = quranData.getJSONObject(i);
            int idSura = c.getInt("id_surah");
            int idAyah = c.getInt("id_ayah");
            int idSimSura = c.getInt("id_similarSurah");
            int idSimAyah = c.getInt("id_similarAyah");
            db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (" + idSimSura + "," + idSimAyah
                + "," + idAyah  + ", " + idSura +")");
            Log.d("add success", "success");

          }
        } catch (JSONException e) {
          Log.d("errorrr", "errrrrorrrrr");
          e.printStackTrace();
        }

      }
    }
    GetDataJSON g = new GetDataJSON();
    g.execute();
  }


  public HashMap<Integer, Integer> getSimilarities(int sura, int ayah) {
    HashMap<Integer, Integer> sim = new HashMap<>();

    Log.d("sg", "getSimilarities");

    SQLiteDatabase db = this.getReadableDatabase();
    Cursor temp = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + SURAID + "="+ sura +
        " AND "+ VERSEID + "=" + ayah,null);
    if(temp.moveToFirst()){
      do{
        int simSuraIndex = temp.getColumnIndex(SIMSURAID);
        int simAyahIndex = temp.getColumnIndex(SIMVERSEID);
        int simSura = temp.getInt(simSuraIndex);
        int simAyah = temp.getInt(simAyahIndex);
        Log.d("hi", "here");
        sim.put(simSura, simAyah);
      } while(temp.moveToNext());
    } else{
      Log.d("df", "fdlk");
    }

    temp.close();

    return sim;
  }
}
