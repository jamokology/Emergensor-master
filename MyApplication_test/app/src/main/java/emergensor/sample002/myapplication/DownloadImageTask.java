package emergensor.sample002.myapplication;

import java.io.InputStream;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

/**
 * Web上から画像を読み込むタスク
 *
 */
public class DownloadImageTask extends AsyncTask<Void,Void,Bitmap> {
    private ImageView imageView;
    private Context context;
    private URL url;

    // 初期化
    public DownloadImageTask(ImageView imageView, Context context, URL url) {
        this.imageView = imageView;
        this.context = context;
        this.url = url;
    }

    // execute時のタスク本体。画像をビットマップとして読み込んで返す
    @Override
    protected Bitmap doInBackground(Void... params) {
        synchronized (context){
            try {

                // 読み込み実行
                InputStream imageIs = url.openStream();
                Bitmap bm = BitmapFactory.decodeStream(imageIs);
                return bm;
            } catch (Exception e) {
                Log.d("DownloadImageTask", "画像読み込みタスクで例外発生：",e);
                return null;
            }
        }
    }

    // タスク完了時
    @Override
    protected void onPostExecute(Bitmap result) {
        if(result != null){
            imageView.setImageBitmap(result);
        }
    }
}