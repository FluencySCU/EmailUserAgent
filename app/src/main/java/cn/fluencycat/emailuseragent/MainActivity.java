package cn.fluencycat.emailuseragent;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

/**
 * 主界面Activity,选择发邮件或是收邮件
 *
 * @author Fluency
 */

public class MainActivity extends AppCompatActivity {

    private Button send,receive;//发送和接受按钮

    //讲道理这里可能会leaks但是就这样吧
    public static String username = "";//用户名
    public static String password = "";//用户密码
    public static int flag = 0;//用户使用的哪种邮箱

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();//获取上一个Ac的意图
        Bundle bundle = intent.getExtras();
        /*
        获取三个参数
         */
        username = bundle.getString("username");
        password = bundle.getString("password");
        flag = bundle.getInt("flag");
        initView();//初始化View
    }

    private void initView(){
        send=findViewById(R.id.btn_send);
        receive=findViewById(R.id.btn_receive);
        send.setOnClickListener(listener);
        receive.setOnClickListener(listener);
    }


    /**
     * 返回事件监听，弹出对话框提醒用户是否退出?
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode== KeyEvent.KEYCODE_BACK){
            AlertDialog dialog=new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("确定退出程序?")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ;
                        }
                    }).create();
            dialog.show();
        }
        return false;
    }

    /**
     * 点击监听
     */
    private View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_send:
                    //发送
                    Intent intent=new Intent(MainActivity.this,SendActivity.class);
                    startActivity(intent);
                    break;
                case R.id.btn_receive:
                    //接受
                    Intent intent1=new Intent(MainActivity.this,ReceiveActivity.class);
                    startActivity(intent1);
                    break;
            }
        }
    };
}
