package com.quran.labs.androidquran.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.quran.labs.androidquran.R;
import com.quran.labs.androidquran.data.Constants;
import com.quran.labs.androidquran.data.QuranInfo;
import com.quran.labs.androidquran.ui.PagerActivity;
import com.quran.labs.androidquran.ui.QuranActivity;
import com.quran.labs.androidquran.ui.SuraTestActivity;
import com.quran.labs.androidquran.ui.helpers.QuranListAdapter;
import com.quran.labs.androidquran.ui.helpers.QuranRow;
import com.quran.labs.androidquran.util.QuranSettings;
import com.quran.labs.androidquran.util.QuranUtils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;

import static com.quran.labs.androidquran.data.Constants.JUZ2_COUNT;
import static com.quran.labs.androidquran.data.Constants.PAGES_LAST;
import static com.quran.labs.androidquran.data.Constants.SURAS_COUNT;

public class SuraListFragment extends Fragment implements QuranListAdapter.QuranTouchListener {

  private RecyclerView mRecyclerView;
  private Disposable disposable;
  Intent i;
 static Context context;
  QuranListAdapter adapter;
  static int suraID;
  public static SuraListFragment newInstance() {
    return new SuraListFragment();
  }

  public void onCreate(Bundle sBundle){
    super.onCreate(sBundle);
    context = getActivity();
  }
  @Override
  public View onCreateView(LayoutInflater inflater,
      ViewGroup container, Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.quran_list, container, false);
    i=new Intent(getActivity(),SuraTestActivity.class);

    mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
    mRecyclerView.setHasFixedSize(true);
    mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
    mRecyclerView.setItemAnimator(new DefaultItemAnimator());

     adapter = new QuranListAdapter(context, mRecyclerView, getSuraList(), true);
    QuranListAdapter.QuranTouchListener q=newInstance();
    adapter.setQuranTouchListener(q);
    mRecyclerView.setAdapter(adapter);
    registerForContextMenu(mRecyclerView);
    return view;

  }
  @Override
  public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
    super.onCreateContextMenu(menu, v, menuInfo);
    menu.setHeaderTitle("قائمة السورة");
    int sura=suraID;
    Log.d("on create context",sura+"");
    i.putExtra("sura",sura);
    menu.add(0, v.getId(), 0, "إختبار السورة ");

  }
  @Override
  public boolean onContextItemSelected(MenuItem item) {
    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

    startActivity(i);

    return super.onContextItemSelected(item);
  }
  @Override
  public void onPause() {
    if (disposable != null) {
      disposable.dispose();
    }
    super.onPause();
  }

  @Override
  public void onResume() {
    final Activity activity = getActivity();
    QuranSettings settings = QuranSettings.getInstance(activity);
    if (activity instanceof QuranActivity) {
      disposable = ((QuranActivity) activity).getLatestPageObservable()
          .first(Constants.NO_PAGE)
          .observeOn(AndroidSchedulers.mainThread())
          .subscribeWith(new DisposableSingleObserver<Integer>() {
            @Override
            public void onSuccess(Integer recentPage) {
              if (recentPage != Constants.NO_PAGE) {
                int sura = QuranInfo.safelyGetSuraOnPage(recentPage);
                int juz = QuranInfo.getJuzFromPage(recentPage);
                int position = sura + juz - 1;
                mRecyclerView.scrollToPosition(position);
              }
            }

            @Override
            public void onError(Throwable e) {
            }
          });
    }

    if (settings.isArabicNames()) {
      updateScrollBarPositionHoneycomb();
    }

    super.onResume();
  }

  private void updateScrollBarPositionHoneycomb() {
    mRecyclerView.setVerticalScrollbarPosition(View.SCROLLBAR_POSITION_LEFT);
  }

  private QuranRow[] getSuraList() {
    int next;
    int pos = 0;
    int sura = 1;
    QuranRow[] elements = new QuranRow[SURAS_COUNT + JUZ2_COUNT];

    Activity activity = getActivity();
    boolean wantPrefix = activity.getResources().getBoolean(R.bool.show_surat_prefix);
    boolean wantTranslation = activity.getResources().getBoolean(R.bool.show_sura_names_translation);
    for (int juz = 1; juz <= JUZ2_COUNT; juz++) {
      final String headerTitle = activity.getString(R.string.juz2_description,
          QuranUtils.getLocalizedNumber(activity, juz));
      final QuranRow.Builder headerBuilder = new QuranRow.Builder()
          .withType(QuranRow.HEADER)
          .withText(headerTitle)
          .withPage(QuranInfo.JUZ_PAGE_START[juz - 1]);
      elements[pos++] = headerBuilder.build();
      next = (juz == JUZ2_COUNT) ? PAGES_LAST + 1 :
          QuranInfo.JUZ_PAGE_START[juz];

      while ((sura <= SURAS_COUNT) &&
          (QuranInfo.SURA_PAGE_START[sura - 1] < next)) {
        final QuranRow.Builder builder = new QuranRow.Builder()
            .withText(QuranInfo.getSuraName(activity, sura, wantPrefix, wantTranslation))
            .withMetadata(QuranInfo.getSuraListMetaString(activity, sura))
            .withSura(sura)
            .withPage(QuranInfo.SURA_PAGE_START[sura - 1]);
        elements[pos++] = builder.build();
        sura++;
      }
    }

    return elements;
  }

  @Override
  public void onClick(QuranRow row, int position) {
    ((QuranActivity) context).jumpTo(row.page);
  }

  @Override
  public boolean onLongClick(QuranRow row, int position) {
       suraID=row.sura;
    Log.d("Menu context",suraID+"");
    return false;
  }
}
