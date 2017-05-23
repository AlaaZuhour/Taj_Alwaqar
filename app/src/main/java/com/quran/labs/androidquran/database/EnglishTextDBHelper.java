package com.quran.labs.androidquran.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;

import com.quran.labs.androidquran.data.Constants;
import com.quran.labs.androidquran.data.SuraAyah;
import com.quran.labs.androidquran.ui.fragment.AyahTranslationFragment;

import junit.runner.Version;

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

public class EnglishTextDBHelper extends SQLiteOpenHelper {
  private static final String DB_NAME="tajalwaqar";

  String myJSON;
  JSONArray quranData = null;

  static final String TABLE_NAME = "english_quran";
  static final String ID = "ID";
  static final String SURAID = "SuraID";
  static final String VERSEID = "VerseID";
  static final String AYAHTEXT = "AyahText";


  private static final String CREATE_QURANDATA_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
      ID + " integer primary key," +
      AYAHTEXT + " TEXT," +
      VERSEID + " integer not null," +
      SURAID + " integer not null);";

/*  private static final String CREATE_SIMILAR_TABLE = "CREATE TABLE " + SimilarTable.TABLE_NAME + "(" +
      SimilarTable.IDSURA + " integer not null, " +
      SimilarTable.IDAYAH + " integer not null, " +
      SimilarTable.IDSIMSURA + " integer not null, " +
      SimilarTable.IDSIMAYAH + " integer not null);";
*/

  public EnglishTextDBHelper(Context context, int version) {
    super(context, DB_NAME, null, version);
    Log.d("fdfd", "hi i'm in constructor");
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    Log.d("fdfd", "hi i'm in on create");
    db.execSQL(CREATE_QURANDATA_TABLE);
    getData("http://tajalwaqar.atwebpages.com/getQuranData.php",db);

  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    Log.d("fdfd", "hi i'm in on upgrade");
    db.execSQL(CREATE_QURANDATA_TABLE);
    getData("http://tajalwaqar.atwebpages.com/getQuranData.php",db);

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
            String id = c.getString("id");
            String idSura = c.getString("suraID");
            String idAyah = c.getString("verseID");
            String ayahText = c.getString("AyahText");
            db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (" + Integer.parseInt(id) + ", '" + ayahText
                + "'," + Integer.parseInt(idAyah)  + ", " + Integer.parseInt(idSura) +")");
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

  public String getAyahText(int sura, int ayah) {
    String text = "";
    SQLiteDatabase db = this.getReadableDatabase();
    String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE "+ SURAID + "=" + sura + " AND " + VERSEID + "="+ ayah;
    Cursor temp = db.rawQuery(selectQuery, null);
    if(temp.moveToNext()){
      do{
        int index = temp.getColumnIndex(AYAHTEXT);
        text = temp.getString(index);
      } while(temp.moveToNext());
    }
    temp.close();
    return text;
  }

  public void ayahSuraId(int id){
    Log.d("dd", "I'm here");
    String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE "+ ID + "=" + id;
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor temp = db.rawQuery(selectQuery, null);
    if(temp.moveToNext()){
      do{

        int idAyahIndex = temp.getColumnIndex(VERSEID);
        int idAyah = temp.getInt(idAyahIndex);
        int idSuraIndex = temp.getColumnIndex(SURAID);
        int idSura = temp.getInt(idSuraIndex);

        SuraAyah suraAyah = new SuraAyah(idSura, idAyah);

        Constants.suraAyahHashMap.put(id, suraAyah);
      } while(temp.moveToNext());
    }
    temp.close();

  }
}
