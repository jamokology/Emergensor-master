
//name the specific LoginActivity
package emergensor.sample002.myapplication;

//import: doesnt have to write the long name because of this

//180322addforFacebooklogin/Shohei
//import com.facebook.FacebookSdk;
//import com.facebook.appevents.AppEventsLogger;

//17/12/05追記＆コメントアウト
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.app.AlertDialog;
import android.os.Bundle;

//180324addforRadioButton/Shohei
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;


import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.Arrays;

import emergensor.sample002.myapplication.block.BlockBuilder;
import emergensor.sample002.myapplication.block.Message;
import emergensor.sample002.myapplication.block.drain.SimpleVectorConcatenateDrain;
import emergensor.sample002.myapplication.block.filter.BufferFilter;
import emergensor.sample002.myapplication.block.filter.FunctionFilter;
import emergensor.sample002.myapplication.block.filter.VectorPeriodicSampleFilter;
import emergensor.sample002.myapplication.block.sink.URLSenderSink;
import emergensor.sample002.myapplication.block.source.AccelerationSensorSource;
import emergensor.sample002.myapplication.block.source.IntervalSource;
import emergensor.sample002.myapplication.block.source.LocationSensorSource;
import emergensor.sample002.myapplication.functions.ComplexAbsoluteFunction;
import emergensor.sample002.myapplication.functions.FFTFunction;
import emergensor.sample002.myapplication.functions.HanningWindowFunction;
import emergensor.sample002.myapplication.functions.MapFunctionWrapper;
import emergensor.sample002.myapplication.functions.MeanFunction;
import emergensor.sample002.myapplication.functions.MessageFunctionWrapper;
import emergensor.sample002.myapplication.functions.NormFunction;
import emergensor.sample002.myapplication.functions.PassFrequencyFunction;
import emergensor.sample002.myapplication.functions.VarianceFunction;
import emergensor.sample002.myapplication.lib.Consumer;
import emergensor.sample002.myapplication.lib.Function;
import emergensor.sample002.myapplication.lib.Utils;
import emergensor.sample002.myapplication.lib.Vector;

//import static: field and method without the name of the class
import static emergensor.sample002.myapplication.block.BlockBuilder.build;

//17/12/05追記＆コメントアウト //180324EditforButton/Shohei
public class MapActivity extends AppCompatActivity {

    private UI ui;

    private IntervalSource intervalSource;
    private LocationSensorSource locationSensorSource;
    private AccelerationSensorSource accelerationSensorSource;

    private String userId;
    private double[] location = null;
    private String locationText = "undefined";
    private boolean allowAlert = false;
    private int index = 0;


    public void setText(String text){

        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(text);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //log
        Log.i("test", "test");

        // make instances
        try {

            ui = new UI(this, "http://mimi.f5.si:7030/", 500);
            intervalSource = build(new IntervalSource(10 * 1000)) //10秒に1回タイマーイベント発生(ミリセカンド)。これは、GPS位置情報が送られるタイミングの制御に使われる
                    .add(new Consumer<Message<Long>>() {
                        final URLSenderSink urlSenderSink = new URLSenderSink(
                                "a",
                                "gp-^45:w3v9]332c",
                                new URL("http://mimi.f5.si:7030/__api/send/alert"));

                        @Override
                        public void accept(Message<Long> m) {//10秒ごとに発生するイベントの受付処理
                            if (allowAlert) {
                                try {
                                    send(new JSONObject()
                                            .put("userId",userId)
                                            .put("lat", location != null ? location[0] :35.709104222649785)
                                            .put("lng", location != null ? location[1] :139.76200461387634)
                                            .put("text","App").toString());
                                } catch (JSONException e) {
                                    Log.i("test", "", e);
                                }
                                /*
                                if (location != null) {//位置情報をサーバに送る
                                    send(String.format("{userId:\"%s\",lat:\"%f\",lng:\"%f\",text:\"App\"}",
                                            userId,
                                            location[0],
                                            location[1]));
                                }
                                */
                            }
                        }

                        private void send(String string) {
                            try {
                                urlSenderSink.accept(string);
                            } catch (Throwable e) {
                                StringWriter out = new StringWriter();
                                e.printStackTrace(new PrintWriter(out));
                                new AlertDialog.Builder(MapActivity.this)
                                        .setTitle("title")
                                        .setMessage(out.toString())
                                        .setPositiveButton("OK", null)
                                        .show();
                            }
                        }
                    })
                    .get();
            locationSensorSource = build(new LocationSensorSource(this))//位置情報を受け取りブロックダイアグラムに渡す
                    .add(new Consumer<Message<Vector<Double>>>() {
                        @Override
                        public void accept(Message<Vector<Double>> m) {//ブロックダイアグラム内のacceptを使って位置情報を受け取る→変数と表示の更新
                            location = new double[]{m.value.get(0), m.value.get(1)};
                            locationText = "" + m.value.get(0) + " / " + m.value.get(1);
                            //ui.setText(locationText);
                        }
                    })
                    .get();
            final SimpleVectorConcatenateDrain<Double> drain = build(new SimpleVectorConcatenateDrain<Double>(6))
                    .add(new Consumer<Message<Vector<Double>>>() {
                        private Thread thread;

                        @Override
                        public void accept(Message<Vector<Double>> m) {//6つの特徴量をclassifyに入れ、
                            EnumState state = classify(m.value.get(0), m.value.get(1), m.value.get(2), m.value.get(3), m.value.get(4), m.value.get(5));

                            if (state == EnumState.RUN) {
                                allowAlert = true;

                                // タイマリセット
                                if (thread != null) thread.interrupt();
                                thread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Thread.sleep(60 * 1000);
                                        } catch (InterruptedException e) {
                                            return;
                                        }
                                        allowAlert = false;
                                    }
                                });
                                thread.start();
                            }

                            ui.setText2(state.name());//画面の下部にRUN/OTHER表示
                            ui.setEntry2(index,
                                    (float) (double) m.value.get(0),
                                    (float) (double) m.value.get(1),
                                    (float) (double) m.value.get(2) * 0.1f,
                                    (float) (double) m.value.get(3) * 0.1f,
                                    (float) (double) m.value.get(4) * 0.1f,
                                    (float) (double) m.value.get(5) * 0.1f);
                        }

                        private EnumState classify(Double... args) {
                            try {
                                return EnumState.values()[(int) Tree.classify(args)];
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    })
                    .get();
            accelerationSensorSource = build(new AccelerationSensorSource(this, false))//new
                    .add(build(new VectorPeriodicSampleFilter(1 * 1000 * 1000 / 100))
                            .add(new Consumer<Message<Vector<Double>>>() {
                                @Override
                                public void accept(Message<Vector<Double>> m) {
                                    ui.setEntry(index,
                                            (float) (double) m.value.get(0),
                                            (float) (double) m.value.get(1),
                                            (float) (double) m.value.get(2),
                                            (float) Utils.getNorm(m.value));
                                    index = (index + 1) % ui.getGraphSize();
                                }
                            })
                            .add(bfm(new NormFunction())
                                    .add(build(new BufferFilter<Double>(256))
                                            .add(bfm(new MeanFunction())
                                                    .add(drain.createDrain(0)))
                                            .add(bfm(new VarianceFunction())
                                                    .add(drain.createDrain(1)))
                                            .add(bfm(new HanningWindowFunction()
                                                    .andThen(new FFTFunction()))
                                                    .add(bfm(new PassFrequencyFunction(5, 8)
                                                            .andThen(new MapFunctionWrapper<>(new ComplexAbsoluteFunction()))
                                                            .andThen(new NormFunction()))
                                                            .add(drain.createDrain(2)))
                                                    .add(bfm(new PassFrequencyFunction(9, 16)
                                                            .andThen(new MapFunctionWrapper<>(new ComplexAbsoluteFunction()))
                                                            .andThen(new NormFunction()))
                                                            .add(drain.createDrain(3)))
                                                    .add(bfm(new PassFrequencyFunction(17, 32)
                                                            .andThen(new MapFunctionWrapper<>(new ComplexAbsoluteFunction()))
                                                            .andThen(new NormFunction()))
                                                            .add(drain.createDrain(4)))
                                                    .add(bfm(new PassFrequencyFunction(33, 64)
                                                            .andThen(new MapFunctionWrapper<>(new ComplexAbsoluteFunction()))
                                                            .andThen(new NormFunction()))
                                                            .add(drain.createDrain(5)))))))
                    .get();

            // pre init
            {
                ui.preInit();
                userId = createUserID();
            }

            // init
            {
                if (!locationSensorSource.checkPermission()) return; //利用許可
                if (!accelerationSensorSource.checkPermission()) return;

                intervalSource.init(); //初期化
                locationSensorSource.init();
                accelerationSensorSource.init();

                intervalSource.start(); //開始
                locationSensorSource.start();
                accelerationSensorSource.start();

                ui.init();
            }

            // post init
            {
                ui.setText(locationText);
            }




            //180331AddforButton/Shohei
            // ラジオグループのオブジェクトを取得
            RadioGroup rg = (RadioGroup)findViewById(R.id.radioGroup);

            // ラジオグループのチェック状態変更イベントを登録
            rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                // チェック状態変更時に呼び出されるメソッド
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    // チェック状態時の処理を記述
                    // チェックされたラジオボタンオブジェクトを取得
                    RadioButton radioButton = (RadioButton)findViewById(checkedId);

                    group.check(R.id.radio_map);

                    switch(radioButton.getId()) {
                        case R.id.radio_map:
                        {
                            //LoginManager.getInstance().logInWithPublishPermissions(LoginActivity.this, Arrays.<String>asList());
                            //setContentView(R.layout.activity_login);
                            break;
                        }
                        case R.id.radio_member:
                        {
                            //180407forMovetoMemberPage/Shohei
                            Intent intent = new Intent(MapActivity.this, MemberActivity.class);
                            startActivity(intent);
                            finish();
                            break;
                        }
                        case R.id.radio_setting:
                        {
                            Intent intent = new Intent(MapActivity.this, SettingActivity.class);
                            startActivity(intent);
                            finish();
                            break;
                        }
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        setText(LoginActivity.name);

    }

    private String createUserID() {
        try {
                return new JSONArray().put(Math.random()).put(LoginActivity.name).toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        intervalSource.stop();
        locationSensorSource.stop();
        accelerationSensorSource.stop();
    }


    @Override
    protected void onResume() {
        super.onResume();
        intervalSource.start();
        locationSensorSource.start();
        accelerationSensorSource.start();
    }

    private static enum EnumState {
        RUN,
        OTHER,
    }

    private <I, O> BlockBuilder<FunctionFilter<Message<I>, Message<O>>, Message<O>> bfm
            (Function<I, O> function) {
        return build(new FunctionFilter<>(new MessageFunctionWrapper<>(function)));
    }

}

