package app.pptik.org.radioprrsnibandung.utility;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import app.pptik.org.radioprrsnibandung.R;
import app.pptik.org.radioprrsnibandung.model.Acara;

import static java.lang.Math.ceil;

/**
 * Created by Hafid on 5/16/2017.
 */

public class DownloadRadioTask extends AsyncTask<String, Integer, Long> {
    public static String TAG = "[DownloadRadioTask]";
    private ProgressBar loading;
    private TextView download_size;
    private Context ctx;
    private Acara acara;
    private HttpURLConnection conection = null;
    private String path = "";
    private ImageButton button;

    public DownloadRadioTask(Context ctx, Acara acara, final ProgressBar loading, final TextView download_size, final ImageButton button) {
        this.acara = acara;
        if (loading != null) {
            this.loading = loading;
        }
        if (download_size != null) {
            this.download_size = download_size;
        }
        if (button!=null){
            this.button = button;
        }
        this.ctx = ctx;
    }

    protected Long doInBackground(String... urls) {
        int count;
        long total = 0;

        try {
            URL url = new URL(urls[0]);
            conection = (HttpURLConnection) url.openConnection();
            conection.connect();
            int lenghtOfFile = conection.getContentLength();
            // download file
            InputStream input = new BufferedInputStream(url.openStream());

            //Jika tersedia memory external simpan ke memory external, jika tidak tersedia simpan ke internal
            if (DiskSpace.externalMemoryAvailable()) {
                path = "/sdcard/" + acara.getFileName() + "_" + acara.getTanggal() + ".mp3";
            } else {
                PackageManager m = ctx.getPackageManager();
                String s = ctx.getPackageName();
                try {
                    PackageInfo p = m.getPackageInfo(s, 0);
                    s = p.applicationInfo.dataDir;
                } catch (PackageManager.NameNotFoundException e) {
                    Log.w("yourtag", "Error Package name not found ", e);
                }

                path = s + acara.getFileName() + "_" + acara.getTanggal() + ".mp3";
            }
            // Output stream
            OutputStream output = new FileOutputStream(path);

            byte data[] = new byte[1024];

            total = 0;

            while (((count = input.read(data)) != -1) && this.isCancelled() == false && lenghtOfFile!=0) {
                total += count;
                int fLeft = (int) ceil((lenghtOfFile - total) / 1048576);
                publishProgress((int) ((total * 100) / lenghtOfFile), fLeft);
                output.write(data, 0, count);
                Thread.sleep(5);
                Log.i(TAG, "IsCanceled = " + isCancelled() + " lengthoffile = " + lenghtOfFile);
            }
            // flushing output
            output.flush();
            // closing streams
            output.close();
            input.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return total;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        loading.setProgress(values[0]);
        download_size.setText("" + values[1] + " Mb");
        Log.i(TAG, "progress " + values[0]);
        if (isCancelled()) {
            loading.setVisibility(View.GONE);
            download_size.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        loading.setProgress(0);
        loading.setVisibility(View.VISIBLE);
        download_size.setVisibility(View.VISIBLE);
        button.setImageResource(R.drawable.ic_action_cancel);
        Toast.makeText(ctx, "Download " + acara.getNama() + " in progress!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPostExecute(Long aLong) {
        super.onPostExecute(aLong);
        loading.setVisibility(View.GONE);
        download_size.setVisibility(View.GONE);
        button.setImageResource(R.drawable.ic_action_download);
        Toast.makeText(ctx,"The downloaded file has been saved in "+path+" !",Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        loading.setVisibility(View.GONE);
        download_size.setVisibility(View.GONE);
        button.setImageResource(R.drawable.ic_action_download);
        loading.setProgress(0);
        Toast.makeText(ctx, "Download " + acara.getNama() + " canceled!", Toast.LENGTH_SHORT).show();
        Toast.makeText(ctx,"The downloaded file has been saved in "+path+" !",Toast.LENGTH_LONG).show();
    }

}