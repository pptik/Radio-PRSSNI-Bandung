package app.pptik.org.radioprrsnibandung.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Objects;

import app.pptik.org.radioprrsnibandung.R;
import app.pptik.org.radioprrsnibandung.model.Acara;
import app.pptik.org.radioprrsnibandung.model.StasiunRadio;
import app.pptik.org.radioprrsnibandung.utility.DownloadRadioTask;

/**
 * Created by Hafid on 5/17/2017.
 */

public class AcaraAdapter extends RecyclerView.Adapter<AcaraAdapter.MyViewHolder> {
    private static String TAG = "[StasiunRadioAdapter]";

    private List<Acara> acaraList;
    private Context context;
    private int lastPosition = -1;
    private boolean isAnyDownload[];

    public AcaraAdapter(Context context, List<Acara> acaraList){
        this.context = context;
        this.acaraList = acaraList;
        this.isAnyDownload = new boolean[acaraList.size()];
    }

    public void notifyAdapterDataSetChanged() {
        notifyDataSetChanged();
        this.isAnyDownload = new boolean[acaraList.size()];
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_acara_podcast, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Acara acara = acaraList.get(position);
        holder.agenda_name.setText(acara.getNama());
        holder.date.setText(acara.getTanggal());
        holder.time.setText(acara.getWaktu());
        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (acara.getDownloader() != null && acara.getDownloader().getStatus() != AsyncTask.Status.FINISHED) {
                    Log.i(TAG, "Download canceled now..");
                    acara.getDownloader().cancel(true);
                } else {
                    acara.setDownloader(new DownloadRadioTask(context, acara, holder.progressBar, holder.download_size, holder.download));
                    acara.getDownloader().execute(acara.getDownload_url());
                    Log.i(TAG, "Download starting now..");
                }
                isAnyDownload[position] = !acara.getDownloader().isCancelled();
            }
        });
        if(position > lastPosition) {

            Animation animation = AnimationUtils.loadAnimation(context,
                    R.anim.up_from_bottom);
            holder.itemView.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return acaraList.size();
    }

    public Acara getAcara(int position){
        return acaraList.get(position);
    }

    public boolean[] getIsAnyDownload(){
        return isAnyDownload;
    }

    public boolean checkIfAnyDownloadProgress() {
        boolean check = false;
        for (int i = 0; i < acaraList.size(); i++) {
            if (isAnyDownload[i]) {
                check = true;
            }
        }
        if (check)
            Toast.makeText(context, R.string.notif_stop_download, Toast.LENGTH_LONG).show();
        return check;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView agenda_name, date, time, download_size;
        public ImageView profpic;
        public ProgressBar progressBar;
        public ImageButton download;

        public MyViewHolder(View view) {
            super(view);
            agenda_name = (TextView) view.findViewById(R.id.tx_name);
            date = (TextView) view.findViewById(R.id.tx_tanggal);
            time = (TextView) view.findViewById(R.id.tx_waktu);
            download_size = (TextView) view.findViewById(R.id.id_download_size);
            progressBar = (ProgressBar) view.findViewById(R.id.id_download_progress);
            progressBar.setVisibility(View.GONE);
            profpic = (ImageView) view.findViewById(R.id.prof_pic_lst);
            download = (ImageButton) view.findViewById(R.id.id_download);
        }
    }

}
