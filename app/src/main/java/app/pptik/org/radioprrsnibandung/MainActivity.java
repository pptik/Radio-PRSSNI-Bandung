package app.pptik.org.radioprrsnibandung;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Movie;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import app.pptik.org.radioprrsnibandung.adapter.StasiunRadioAdapter;
import app.pptik.org.radioprrsnibandung.customview.DividerItemDecoration;
import app.pptik.org.radioprrsnibandung.customview.RecyclerTouchListener;
import app.pptik.org.radioprrsnibandung.model.StasiunRadio;
import app.pptik.org.radioprrsnibandung.utility.ConfigUrl;
import app.pptik.org.radioprrsnibandung.utility.RadioClient;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{
    private static String TAG = "[MainActivity]";

    List<StasiunRadio> stasiunRadioList = new ArrayList<>();

    private StasiunRadioAdapter stasiunRadioAdapter;

    @BindView(R.id.radio_list) RecyclerView recyclerView;
    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG,"Create Activity");
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        stasiunRadioAdapter = new StasiunRadioAdapter(getApplicationContext(), stasiunRadioList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(stasiunRadioAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                StasiunRadio stasiunRadio = (StasiunRadio) stasiunRadioAdapter.getItem(position);
                Toast.makeText(getApplicationContext(), stasiunRadio.getNama() + " is selected!", Toast.LENGTH_SHORT).show();
                Intent nextInt = new Intent(getApplicationContext(),RadioActivity.class);
                nextInt.putExtra("StasiunRadio",stasiunRadio);
                startActivity(nextInt);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        prepareMainListRadio();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_with_search, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Radio Station's Name...");
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    /*
    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }


    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            prepareMainListRadio(query);
        }
    }

    */

    public void prepareMainListRadio() {
        RadioClient.get(getApplicationContext(), ConfigUrl.radioBroadcast, new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                stasiunRadioList.clear();
                stasiunRadioAdapter.notifyDataSetChanged();
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    Toast.makeText(getApplicationContext(), R.string.con_timeout, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                stasiunRadioList.clear();
                stasiunRadioAdapter.notifyDataSetChanged();
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    Toast.makeText(getApplicationContext(), R.string.con_timeout, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                stasiunRadioList.clear();
                stasiunRadioAdapter.notifyDataSetChanged();
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    Toast.makeText(getApplicationContext(), R.string.con_timeout, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                stasiunRadioList.clear();
                try {
                    if (response.getBoolean("status")) {
                        JSONArray array = response.getJSONArray("broadcast");
                        for (int i = 0; i < array.length(); i++) {
                            StasiunRadio acr = new StasiunRadio();
                            acr.setId_broadcast(array.getJSONObject(i).getInt("id"));
                            acr.setId(array.getJSONObject(i).getInt("id_radio"));
                            acr.setNama(array.getJSONObject(i).getString("name"));
                            acr.setAlamat(array.getJSONObject(i).getString("address"));
                            acr.setBroadcast_path(array.getJSONObject(i).getString("url_stream_stereo"));
                            acr.setProf_pic_uri(array.getJSONObject(i).getString("pathlogo"));
                            acr.setAbout(array.getJSONObject(i).getString("about"));
                            acr.setFrekuensi(array.getJSONObject(i).getString("frekuensi"));
                            acr.setBand(array.getJSONObject(i).getString("band"));
                            acr.setSite_addr(array.getJSONObject(i).getString("sitea_dd"));
                            //acr.setRadio_url(ConfigUrl.BASE_URL+ConfigUrl.streamingChannel+stRadio.getNama()+"/"+acr.getTanggal()+"/"+acr.getFileName()+".mp3");
                            //acr.setRadio_url(ConfigUrl.BASE_URL + ConfigUrl.streamingChannel + acr.getId());
                            if (!acr.getBroadcast_path().equals(null) && !acr.getBroadcast_path().equals("") && !acr.getBroadcast_path().equals(" ")) {
                                stasiunRadioList.add(acr);
                                Log.i(TAG, acr.toString());
                            }
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.no_channel, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                stasiunRadioAdapter.notifyDataSetChanged();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                stasiunRadioList.clear();
                stasiunRadioAdapter.notifyDataSetChanged();
            }
        });
    }

    public void prepareMainListRadio(final String query) {
        RadioClient.get(getApplicationContext(), ConfigUrl.radioBroadcast, new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                stasiunRadioList.clear();
                stasiunRadioAdapter.notifyDataSetChanged();
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    Toast.makeText(getApplicationContext(), R.string.con_timeout, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                stasiunRadioList.clear();
                stasiunRadioAdapter.notifyDataSetChanged();
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    Toast.makeText(getApplicationContext(), R.string.con_timeout, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                stasiunRadioList.clear();
                stasiunRadioAdapter.notifyDataSetChanged();
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    Toast.makeText(getApplicationContext(), R.string.con_timeout, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                stasiunRadioList.clear();
                try {
                    if (response.getBoolean("status")) {
                        JSONArray array = response.getJSONArray("broadcast");
                        for (int i = 0; i < array.length(); i++) {
                            StasiunRadio acr = new StasiunRadio();
                            acr.setNama(array.getJSONObject(i).getString("name"));
                            acr.setAlamat(array.getJSONObject(i).getString("address"));
                            acr.setBroadcast_path(array.getJSONObject(i).getString("url_stream_stereo"));
                            acr.setAbout(array.getJSONObject(i).getString("about"));
                            //acr.setRadio_url(ConfigUrl.BASE_URL+ConfigUrl.streamingChannel+stRadio.getNama()+"/"+acr.getTanggal()+"/"+acr.getFileName()+".mp3");
                            //acr.setRadio_url(ConfigUrl.BASE_URL + ConfigUrl.streamingChannel + acr.getId());
                            if (!acr.getBroadcast_path().equals(null) && !acr.getBroadcast_path().equals("") && !acr.getBroadcast_path().equals(" ")) {
                                if (acr.getNama().toLowerCase().contains(query.toLowerCase())) {
                                    stasiunRadioList.add(acr);
                                    Log.i(TAG, acr.toString());
                                }
                            }
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.no_channel, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                stasiunRadioAdapter.notifyDataSetChanged();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                stasiunRadioList.clear();
                stasiunRadioAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        stasiunRadioAdapter.getFilter().filter(newText);
        Log.i(TAG,"Filter "+newText);
        return true;
    }
}
