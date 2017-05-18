package app.pptik.org.radioprrsnibandung.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import app.pptik.org.radioprrsnibandung.R;
import app.pptik.org.radioprrsnibandung.model.StasiunRadio;

/**
 * Created by Hafid on 5/16/2017.
 */

public class StasiunRadioAdapter extends RecyclerView.Adapter<StasiunRadioAdapter.MyViewHolder> implements Filterable{
    private static String TAG = "[StasiunRadioAdapter]";

    private List<StasiunRadio> radioList;
    private List<StasiunRadio> filteredRadio;
    private Context context;
    private int lastPosition = -1;
    private StasiunRadioFilter radioFilter = new StasiunRadioFilter();

    public StasiunRadioAdapter(Context context, List<StasiunRadio> radioList){
        this.radioList = radioList;
        this.filteredRadio = radioList;
        this.context = context;
        getFilter();
    }

    @Override
    public Filter getFilter() {
        return radioFilter;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView nama, alamat;
        public ImageView profpic;

        public MyViewHolder(View view) {
            super(view);
            nama = (TextView) view.findViewById(R.id.nama);
            alamat = (TextView) view.findViewById(R.id.alamat);
            profpic = (ImageView) view.findViewById(R.id.prof_pic_strd);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_stasiun_radio, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        StasiunRadio stasiunRadio = filteredRadio.get(position);
        holder.nama.setText(stasiunRadio.getNama());
        holder.alamat.setText(stasiunRadio.getAlamat());
        String uri = stasiunRadio.getProf_pic_uri();
        Log.i(TAG,"Pic Uri : "+uri+" "+stasiunRadio.getNama());
        if (!uri.equals("")) {
            Glide.with(context).load(uri)
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.profpic);
        }
        if(position > lastPosition) {

            Animation animation = AnimationUtils.loadAnimation(context,
                    R.anim.up_from_bottom);
            holder.itemView.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return filteredRadio.size();
    }

    public void setRadioList(List<StasiunRadio> stasiunRadios){
        this.radioList = stasiunRadios;
    }

    public Object getItem(int i) {
        return filteredRadio.get(i);
    }

    private class StasiunRadioFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint!=null && constraint.length()>0) {
                List<StasiunRadio> tempList = new ArrayList<>();
                for (int i = 0; i < radioList.size(); i++){
                    StasiunRadio rdo = radioList.get(i);
                    if (rdo.getNama().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        tempList.add(rdo);
                    }
                }

                filterResults.count = tempList.size();
                filterResults.values = tempList;
            } else {
                filterResults.count = radioList.size();
                filterResults.values = radioList;
            }

            return filterResults;
        }

        /**
         * Notify about filtered list to ui
         * @param constraint text
         * @param results filtered result
         */
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredRadio = (ArrayList<StasiunRadio>) results.values;
            notifyDataSetChanged();
        }
    }
}
