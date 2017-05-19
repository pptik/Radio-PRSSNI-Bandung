package app.pptik.org.radioprrsnibandung;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import app.pptik.org.radioprrsnibandung.adapter.AcaraAdapter;
import app.pptik.org.radioprrsnibandung.adapter.StasiunRadioAdapter;
import app.pptik.org.radioprrsnibandung.customview.DateDialog;
import app.pptik.org.radioprrsnibandung.customview.DividerItemDecoration;
import app.pptik.org.radioprrsnibandung.customview.RecyclerTouchListener;
import app.pptik.org.radioprrsnibandung.model.Acara;
import app.pptik.org.radioprrsnibandung.model.StasiunRadio;
import app.pptik.org.radioprrsnibandung.utility.ConfigUrl;
import app.pptik.org.radioprrsnibandung.utility.RadioClient;

/**
 * Created by Hafid on 5/17/2017.
 */

public class PodcastFragment extends Fragment {
    private static String TAG = "[PodcastFragment]";

    List<Acara> acaraList = new ArrayList<>();
    private AcaraAdapter acaraAdapter;
    private StasiunRadio stasiunRadio;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private Acara selectedAcara;

    public PodcastFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG,"Created");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_podcast, container, false);
        final PodCastHolder holder = new PodCastHolder(view);
        final Animation rotateAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.infinite_rotation);

        stasiunRadio = (StasiunRadio) this.getArguments().getSerializable("StasiunRadio");

        holder.radioStationsName.setText(stasiunRadio.getNama());
        holder.selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DateDialog dialog = new DateDialog(holder.selectDateText);
                dialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                        selectedAcara = null;
                        holder.channelTitle.setText("Podcast Play List");

                        int day = datePicker.getDayOfMonth();
                        int month = datePicker.getMonth();
                        int year = datePicker.getYear();

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, day);

                        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
                        String date = dateFormat.format(calendar.getTime());

                        if (!acaraAdapter.checkIfAnyDownloadProgress()) {
                            prepareChannelAcara(stasiunRadio, date);
                            dialog.setDate(i, i1 + 1, i2);
                        }

                    }
                });

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                dialog.show(ft, "Tanggal Siaran");
            }
        });
        acaraAdapter = new AcaraAdapter(getContext(), acaraList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        holder.recyclerView.setLayoutManager(mLayoutManager);
        holder.recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        holder.recyclerView.setItemAnimator(new DefaultItemAnimator());
        holder.recyclerView.setAdapter(acaraAdapter);
        holder.recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), holder.recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                selectedAcara = acaraAdapter.getAcara(position);
                holder.channelTitle.setText(selectedAcara.getNama());
                try {
                    holder.syncIconOn();
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(getContext(), Uri.parse(selectedAcara.getRadio_url()));
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.prepareAsync();
                    holder.play_btn.startAnimation(rotateAnimation);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(final MediaPlayer mp) {
                holder.play_btn.clearAnimation();
                holder.playIconOn();
                holder.play_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mp.isPlaying()) {
                            mp.pause();
                            holder.playIconOn();
                        } else {
                            mp.start();
                            holder.pauseIconOn();
                        }
                    }
                });
            }
        });
        return view;
    }

    public void prepareChannelAcara(final StasiunRadio stRadio, String tanggal){
        String requested = ConfigUrl.channelList + stRadio.getId() + "/" + tanggal;
        RadioClient.get(getContext(), requested, new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                acaraList.clear();
                acaraAdapter.notifyAdapterDataSetChanged();
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    Toast.makeText(getContext(), R.string.con_timeout, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                acaraList.clear();
                acaraAdapter.notifyAdapterDataSetChanged();
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    Toast.makeText(getContext(), R.string.con_timeout, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                acaraList.clear();
                acaraAdapter.notifyAdapterDataSetChanged();
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    Toast.makeText(getContext(), R.string.con_timeout, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                acaraList.clear();
                try {
                    if (response.getBoolean("status")) {
                        JSONArray array = response.getJSONArray("channel");
                        for (int i = 0; i < array.length(); i++) {
                            Acara acr = new Acara();
                            acr.setId(array.getJSONObject(i).getInt("id"));
                            acr.setNama(array.getJSONObject(i).getString("nameChannel"));
                            acr.setTanggal(array.getJSONObject(i).getString("date"));
                            acr.setWaktu(array.getJSONObject(i).getString("time"));
                            acr.setFileName(array.getJSONObject(i).getString("nameFile"));
                            acr.setDownload_url(ConfigUrl.BASE_URL + ConfigUrl.downloadChannel + acr.getId());
                            acr.setRadio_url(ConfigUrl.BASE_URL_MIN + array.getJSONObject(i).getString("file_path"));
                            //acr.setRadio_url(ConfigUrl.BASE_URL+ConfigUrl.streamingChannel+stRadio.getNama()+"/"+acr.getTanggal()+"/"+acr.getFileName()+".mp3");
                            //acr.setRadio_url(ConfigUrl.BASE_URL + ConfigUrl.streamingChannel + acr.getId());
                            acaraList.add(acr);
                            Log.i(TAG, " Acara " + acr.getNama() + " " + acr.getRadio_url() + " " + acr.getDownload_url());
                        }
                    } else {
                        Toast.makeText(getContext(), R.string.no_channel, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                acaraAdapter.notifyAdapterDataSetChanged();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                acaraList.clear();
                acaraAdapter.notifyAdapterDataSetChanged();
            }
        });
    }

    public void stopMediaPlayer() {
        mediaPlayer.reset();
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }

    public boolean isAnyDownload(){
        return acaraAdapter.checkIfAnyDownloadProgress();
    }

    public class PodCastHolder extends View{
        public ImageButton selectDate, play_btn;
        public EditText selectDateText;
        public TextView radioStationsName;
        public TextView channelTitle;
        public RecyclerView recyclerView;

        public PodCastHolder(View view) {
            super(view.getContext());
            selectDateText = (EditText) view.findViewById(R.id.tanggal_radio);
            selectDate = (ImageButton)view.findViewById(R.id.id_select_date);
            radioStationsName = (TextView)view.findViewById(R.id.radiosName);
            channelTitle = (TextView)view.findViewById(R.id.radiosChannel);
            recyclerView = (RecyclerView) view.findViewById(R.id.acara_list);
            play_btn = (ImageButton) view.findViewById(R.id.control_play);
        }

        public void syncIconOn() {
            play_btn.setImageResource(R.drawable.ic_action_syncdata);
            play_btn.setEnabled(false);
        }

        public void playIconOn() {
            play_btn.setImageResource(R.drawable.ic_play);
            play_btn.setEnabled(true);
        }

        public void pauseIconOn() {
            play_btn.setImageResource(R.drawable.ic_pause);
            play_btn.setEnabled(true);
        }
    }
}
