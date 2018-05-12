package emergensor.sample002.myapplication;

/**
 * Created by yoshi on 2018/04/09.
 */
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.internal.ImageDownloader;

import java.util.List;

public class MemberListAdapter extends ArrayAdapter<MemberListItem> {

    private Context context;
    private int mResource;
    private List<MemberListItem> mItems;
    private LayoutInflater mInflater;

    /**
     * コンストラクタ
     * @param context コンテキスト
     * @param resource リソースID
     * @param items リストビューの要素
     */
    public MemberListAdapter(Context context, int resource, List<MemberListItem> items) {
        super(context, resource, items);
        this.context = context;
        mResource = resource;
        mItems = items;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView != null) {
            view = convertView;
        }
        else {
            view = mInflater.inflate(mResource, null);
        }

        // リストビューに表示する要素を取得
        MemberListItem item = mItems.get(position);

        // サムネイル画像を設定
        ImageView thumbnail = (ImageView)view.findViewById(R.id.thumbnail);
        new DownloadImageTask(thumbnail, context, item.url).execute();

        // タイトルを設定
        TextView title = (TextView)view.findViewById(R.id.title);
        title.setText(item.title);

        return view;
    }
}
