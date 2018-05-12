package emergensor.sample002.myapplication;

/**
 * Created by yoshi on 2018/04/07.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

//180409forMemberlist/Shohei
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;


public class MemberActivity extends AppCompatActivity {

    private ArrayList<MemberListItem> listItems;
    private MemberListAdapter adapter;



    public void setText(String text){

        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(text);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);

        ///180409forMemberlist/Shohei

        // レイアウトからリストビューを取得
        ListView listView = (ListView) findViewById(R.id.memberList);

        // リストビューに表示する要素を設定
        listItems = new ArrayList<>();

        // 出力結果をリストビューに表示
        adapter = new MemberListAdapter(this, R.layout.memberlist, listItems);
        listView.setAdapter(adapter);


        RadioGroup rg = (RadioGroup) findViewById(R.id.radioGroup);
        rg.check(R.id.radio_member);

        // ラジオグループのチェック状態変更イベントを登録
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            // チェック状態変更時に呼び出されるメソッド
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // チェック状態時の処理を記述

                // チェックされたラジオボタンオブジェクトを取得
                RadioButton radioButton = (RadioButton) findViewById(checkedId);

                group.check(R.id.radio_member);

                switch (radioButton.getId()) {
                    case R.id.radio_map: {
                        Intent intent = new Intent(MemberActivity.this,
                                MapActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    }
                    case R.id.radio_member: {
                        break;
                    }
                    case R.id.radio_setting: {
                        Intent intent = new Intent(MemberActivity.this,
                                SettingActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    }
                }
            }
        });


        {
            GraphRequest request = GraphRequest.newGraphPathRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/me/friends",
                    new GraphRequest.Callback() {
                        @Override
                        public void onCompleted(GraphResponse response) {
                            // Insert your code here

                            //180409forJASONtoHandleMemberlist/Shohei
                            ObjectMapper mapper = new ObjectMapper();
                            try {
                                JsonNode node = mapper.readTree(response.getRawResponse());

                                //int age = node.get("age").asInt();
                                //System.out.println(age);

                                JsonNode data = node.get("data");

                                for (int i = 0; i < data.size(); i++) {
                                    processFriend(data.get(i));
                                }

                                //String number = node.get("phoneNumbers").get(0).get("number").asText();
                                //System.out.println(number);
                            } catch (JsonParseException e) {
                                Log.i("test", "", e);
                            } catch (NullPointerException e) {
                                Log.i("test", "", e);
                            } catch (IOException e) {
                                Log.i("test", "", e);
                            }

                        }

                        private void processFriend(JsonNode friend) {
                            try {
                                String name = friend.get("name").asText();
                                URL url = new URL(friend.get("picture").get("data").get("url").asText());
                                Log.i("test", "" + name);//TODO
                                addMember(url, name);
                            } catch (NullPointerException e) {
                                Log.i("test", "", e);
                            } catch (MalformedURLException e) {
                                Log.i("test", "", e);
                            }
                        }

                    });

            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,gender,picture.type(large)");
            request.setParameters(parameters);

            request.executeAsync();
        }


        setText(LoginActivity.name);
    }

    private void addMember(URL url, String title) {

        MemberListItem item = new MemberListItem(url, title);
        listItems.add(item);

        adapter.notifyDataSetChanged();
    }
}