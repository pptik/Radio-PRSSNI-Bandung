package app.pptik.org.radioprrsnibandung;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.siyamed.shapeimageview.CircularImageView;

import java.io.IOException;

import app.pptik.org.radioprrsnibandung.model.StasiunRadio;
import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by Hafid on 5/17/2017.
 */

public class BroadCastFragment extends Fragment {
    private static String TAG = "[BroadCastFragment]";
    private BroadCastHolder holder;
    private StasiunRadio stasiunRadio = new StasiunRadio();
    private MediaPlayer mediaPlayer;


    public BroadCastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Created");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_broadcast, container, false);
        holder = new BroadCastHolder(view);

        final Animation rotateAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.infinite_rotation);
        holder.button_play.startAnimation(rotateAnimation);

        stasiunRadio = (StasiunRadio) this.getArguments().getSerializable("StasiunRadio");
        if (!stasiunRadio.getAbout().equals("null")) {
            holder.about.setText(stasiunRadio.getAbout());
        } else {
            holder.about.setText("");
        }
        holder.radioAddress.setText(stasiunRadio.getAlamat());
        holder.radioName.setText(stasiunRadio.getNama());

        String uri = stasiunRadio.getProf_pic_uri();
        if (!uri.equals("")) {
            Glide.with(getContext()).load(uri)
                    .bitmapTransform(new CropCircleTransformation(getContext()))
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.logo_radio);
        }

        try {
            holder.syncIconOn();
            mediaPlayer = (new MediaPlayer());
            mediaPlayer.setDataSource(stasiunRadio.getBroadcast_path());
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(final MediaPlayer mp) {
                holder.button_play.clearAnimation();
                holder.playIconOn();
                holder.button_play.setOnClickListener(new View.OnClickListener() {
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

    public BroadCastHolder getHolder() {
        return holder;
    }

    public void stopMediaPlayer() {
        mediaPlayer.reset();
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }

    public class BroadCastHolder extends View {
        public TextView about;
        public TextView radioName;
        public TextView radioAddress;
        public CircularImageView logo;
        public ImageView logo_radio;
        public ImageButton button_play;

        public BroadCastHolder(View view) {
            super(view.getContext());
            about = (TextView) view.findViewById(R.id.about);
            radioName = (TextView) view.findViewById(R.id.radiosName);
            radioAddress = (TextView) view.findViewById(R.id.radiosAddress);
            logo = (CircularImageView) view.findViewById(R.id.logo_radio);
            logo_radio = (ImageView) view.findViewById(R.id.logo_stasiun_radio);
            button_play = (ImageButton) view.findViewById(R.id.control_play);
            about.setMovementMethod(new ScrollingMovementMethod());
        }

        public void syncIconOn() {
            button_play.setImageResource(R.drawable.ic_action_syncdata);
            button_play.setEnabled(false);
        }

        public void playIconOn() {
            button_play.setImageResource(R.drawable.ic_play);
            button_play.setEnabled(true);
        }

        public void pauseIconOn() {
            button_play.setImageResource(R.drawable.ic_pause);
            button_play.setEnabled(true);
        }
    }
}
