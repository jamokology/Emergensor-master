package emergensor.sample002.myapplication;

/**
 * Created by yoshi on 2018/04/07.
 */
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.facebook.login.LoginManager;

public class SettingActivity extends AppCompatActivity {

    public void setText(String text){

        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(text);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        findViewById(R.id.logoutButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this,
                        LoginActivity.class );
                startActivity(intent);
                finish();
            }
        });


        RadioGroup rg = (RadioGroup)findViewById(R.id.radioGroup);
        rg.check(R.id.radio_setting);

        // ラジオグループのチェック状態変更イベントを登録
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            // チェック状態変更時に呼び出されるメソッド
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // チェック状態時の処理を記述

                // チェックされたラジオボタンオブジェクトを取得
                RadioButton radioButton = (RadioButton) findViewById(checkedId);

                group.check(R.id.radio_setting);

                switch (radioButton.getId()) {
                    case R.id.radio_map:
                        {
                        Intent intent = new Intent(SettingActivity.this,
                                MapActivity.class );
                        startActivity(intent);
                        //setContentView(R.layout.activity_login);
                        finish();
                        break;
                    }
                    case R.id.radio_member:
                        {
                        Intent intent = new Intent(SettingActivity.this,
                                MemberActivity.class );
                            startActivity(intent);
                            //setContentView(R.layout.activity_login);
                        finish();
                        break;
                        }
                    case R.id.radio_setting: {
                        break;
                    }
                }
            }
        });

        setText(LoginActivity.name);
    }

}