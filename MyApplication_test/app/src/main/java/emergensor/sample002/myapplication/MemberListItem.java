package emergensor.sample002.myapplication;

/**
 * Created by yoshi on 2018/04/09.
 */

import android.graphics.Bitmap;

import java.net.URL;

public class MemberListItem {
    public URL url;
    public String title;

    public MemberListItem() {

    }

    public MemberListItem(URL url, String title) {
        this.url = url;
        this.title = title;
    }

}