package com.quran.labs.androidquran.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.quran.labs.androidquran.QuranDataActivity;
import com.quran.labs.androidquran.R;
import com.quran.labs.androidquran.data.Constants;
import com.quran.labs.androidquran.data.QuranInfo;
import com.quran.labs.androidquran.data.SuraAyah;
import com.quran.labs.androidquran.database.EnglishTextDBHelper;
import com.quran.labs.androidquran.ui.helpers.FragmentStatePagerAdapter;
import com.quran.labs.androidquran.ui.helpers.HighlightType;
import com.quran.labs.androidquran.ui.helpers.QuranPage;
import com.quran.labs.androidquran.ui.helpers.QuranPageAdapter;

import timber.log.Timber;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import static com.quran.labs.androidquran.data.Constants.PAGES_LAST;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity {


  // UI references.
  private EditText username;
  private EditText password;
  private View mProgressView;
  private View mLoginFormView;
  JSONObject json;
  public static boolean CORRPASS = false;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    // Set up the login form.
    username = (EditText) findViewById(R.id.username);

    password = (EditText) findViewById(R.id.password);

    Button login = (Button) findViewById(R.id.signin);

    login.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        json = new JSONObject();
        Log.d("fdf", "click button");
        try {
          json.put("username", username.getText());
          json.put("password", password.getText());
          Log.d("ds", "pass");
          Log.d("fd", String.valueOf(new TheTask().execute("http://tajalwaqar.atwebpages.com/login.php")));

          if(CORRPASS){

          } else {
          }

        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    });

    //mLoginFormView = findViewById(R.id.login_form);
    mProgressView = findViewById(R.id.login_progress);
  }



  class TheTask extends AsyncTask<String, String, String> {

    String myJSON;
    JSONArray counterData = null;

    @Override
    protected void onPreExecute() {
      // TODO Auto-generated method stub
      Log.d("d", "here");

      super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
      try {
        Log.d("", "I'm in do in background");
        HttpParams httpParams = new BasicHttpParams();
        HttpClient client = new DefaultHttpClient(httpParams);

        HttpPost request = new HttpPost(params[0]);
        request.setEntity(new ByteArrayEntity(json.toString().getBytes(
            "UTF8")));
        request.setHeader("json", json.toString());
        HttpResponse response = client.execute(request);
        HttpEntity entity = response.getEntity();
        // If the response does not enclose an entity, there is no need
        if (entity != null) {
          InputStream instream = entity.getContent();
          String result = null;
          BufferedReader reader = new BufferedReader(new InputStreamReader(instream, "UTF-8"), 8);
          StringBuilder sb = new StringBuilder();
          String line = null;
          char cr;
          while ((cr = (char) reader.read()) != '{') {

          }
          while ((line = reader.readLine()) != null) {
            sb.append(line + "\n");
          }
          result = "{" + sb.toString();
          Log.i("Read from server", result);
          // Log.d("back", EntityUtils.toString(entity));

          getCounters(result);

          return EntityUtils.toString(entity);
        } else {
          return "no String";
        }
      } catch (Exception e) {
        return "Network problem";
      }


    }

    private void getCounters(String result) {
      myJSON = result;
      try {
        JSONObject jobjQuran = new JSONObject(myJSON);
        Log.d("fdfd", "hi i'm in onPostExecute");
        counterData = jobjQuran.getJSONArray("posts");

        if (counterData.length() == 1) {
          LoginActivity.CORRPASS = false;
          runOnUiThread(new Runnable() {
            public void run() {
              // change text
              Toast.makeText(getApplicationContext(), "كلمة المرور خاطئة، إما بسبب وجود شخص آخر سجل بهذا الاسم أو أنك نسيت كلمة المرور الخاصة بك!!", Toast.LENGTH_LONG).show();
            }
          });

        } else {
          LoginActivity.CORRPASS = true;


          //  EnglishTextDBHelper db = new EnglishTextDBHelper(getApplicationContext(), 2);
          for (int i = 0; i < counterData.length(); i++) {
            JSONObject c = counterData.getJSONObject(i);
            int idSura = c.getInt("suraID");
            int counter = c.getInt("counter");
            //  Log.d("", idSura + "  " + counter);
          // Constants.counters.put(idSura, counter);
            //   db.ayahSuraId(idSura);
          }

          EnglishTextDBHelper dbHelper = new EnglishTextDBHelper(getApplicationContext(), 2);

//          for(Map.Entry<Integer, Integer> entry: Constants.counters.entrySet()){
//            dbHelper.ayahSuraId(entry.getKey());
//          }


         /* MenuItem mItem = (MenuItem) findViewById(R.id.login);
          mItem.setTitle(username.getText().toString());
          finish();*/

          runOnUiThread(new Runnable() {
            public void run() {
              // change text
              Toast.makeText(getApplicationContext(), "تم تسجيل الدخول بنجاح", Toast.LENGTH_LONG).show();
            }
          });

          Intent intObj = new Intent(LoginActivity.this, QuranDataActivity.class);
          startActivity(intObj);



        }

      } catch (JSONException e) {
        Log.d("errorrr", "errrrrorrrrr");
        e.printStackTrace();
      }
    }

 /*   private void highlightAyah(int sura, int ayah,
                               boolean force, HighlightType type) {
      Timber.d("highlightAyah() - %s:%s", sura, ayah);
      int page = QuranInfo.getPageFromSuraAyah(sura, ayah);
      if (page < Constants.PAGES_FIRST ||
          PAGES_LAST < page) {
        return;
      }

      int position = QuranInfo.getPosFromPage(page, false);

      QuranPageAdapter pagerAdapter = new QuranPageAdapter(
          this.getSupportFragmentManager(), false, false);

      Fragment f = pagerAdapter.getFragmentIfExists(position);
      if (f instanceof QuranPage && f.isAdded()) {
        ((QuranPage) f).getAyahTracker().highlightAyah(sura, ayah, type, true);
      }
    }
*/
  }
}

