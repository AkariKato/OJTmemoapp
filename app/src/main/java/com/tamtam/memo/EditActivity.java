package com.tamtam.memo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by akari_kato on 2017/07/20.
 */

public class EditActivity extends AppCompatActivity {


    String mFileName = "";   //ファイル名
    boolean mNotSave = false;   //保存しないフラグ


    //画面生成
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // タイトルと内容入力用の EditText を取得
        final EditText editTitle = (EditText)findViewById(R.id.editTitle);
        final EditText editBody = (EditText)findViewById(R.id.editBody);

        // メイン画面からの情報があればセット
        final Intent intent = getIntent();
        final String name = intent.getStringExtra("NAME");
        if (name != "" || name != null) {
            mFileName = name;
            editTitle.setText(intent.getStringExtra("TITLE"));
            editBody.setText(intent.getStringExtra("BODY"));
        }

        //save_btn 読み込み
        Button save_btn = (Button) findViewById(R.id.save_btn);
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 保存して戻る
                mNotSave = false;
                finish();
            }
        });

        //reset_btn 読み込み
        Button reset_btn = (Button) findViewById(R.id.reset_btn);
        reset_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // タイトルと本文があればリセット
                if (name != null) {
                    mFileName = name;
                    editTitle.setText(intent.getStringExtra("TITLE"));
                    editBody.setText(intent.getStringExtra("BODY"));
                }
            }
        });

        //clear_btn 読み込み
        Button clear_btn = (Button) findViewById(R.id.clear_btn);
        clear_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // タイトルと本文を空に
                editTitle.setText("");
                editBody.setText("");
            }
        });


    }

    //menu 位置の del_btn 生成
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }


    //memo内容処理
    //バッググラウンドへ回る時
    @Override
    protected void onPause() {
        super.onPause();

        //保存しない
        if (mNotSave) {
            return;
        }

        //title body を取得
        EditText editTitle = (EditText)findViewById(R.id.editTitle);
        EditText editBody = (EditText)findViewById(R.id.editBody);
        String title = editTitle.getText().toString();
        String body = editBody.getText().toString();

        // title body ともに空白の場合　保存しない
        if (title.isEmpty() && body.isEmpty()) {
            Toast.makeText(this, R.string.msg_destruction, Toast.LENGTH_SHORT).show();
            return;
        }

        //ファイル名生成 yyyyMMdd_HHmmssSSS.txt  ※既に保存されているファイル名はそのまま
        if (mFileName == null) {
            Date date = new Date(System.currentTimeMillis());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmssSSS", Locale.JAPAN);
            mFileName = sdf.format(date) + ".txt";
        }

        // 保存
        OutputStream out = null;
        PrintWriter writer = null;
        try{
            out = this.openFileOutput(mFileName, Context.MODE_PRIVATE);
            writer = new PrintWriter(new OutputStreamWriter(out,"UTF-8"));
            // タイトル書き込み　一行目
            writer.println(title);
            // 内容書き込み　二行目以降
            writer.print(body);
            writer.close();
            out.close();
        }catch(Exception e){
            Toast.makeText(this, "File save error!", Toast.LENGTH_LONG).show();
        }

    }

    //削除選択時の処理
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_del:
                //削除処理
                //ファイル削除
                if (!mFileName.isEmpty()) {
                    this.deleteFile(mFileName);
                }
                //保存せずに画面を閉じる
                mNotSave = true;
                this.finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
