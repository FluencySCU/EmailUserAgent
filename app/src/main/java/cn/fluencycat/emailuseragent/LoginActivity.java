package cn.fluencycat.emailuseragent;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * 邮箱登录Activity
 *
 * @author Fluency
 */

public class LoginActivity extends AppCompatActivity {
    private RadioGroup radioGroup;
    private int flag = 0;//初始化为163邮箱
    private TextView domain;
    private EditText edit_user, edit_pass;
    private Button btn_login;
    private Handler handler;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        handler = new Handler();
        initView();//初始化View
    }

    private void initView() {
        progressDialog = new ProgressDialog(this);//创建加载框
        progressDialog.setMessage("登录...");
        progressDialog.setCancelable(false);//不可点击取消
        domain = findViewById(R.id.domain);
        edit_user = findViewById(R.id.username);
        edit_pass = findViewById(R.id.password);
        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = edit_user.getText().toString().trim();
                final String password = edit_pass.getText().toString().trim();
                if (username.length() < 1 || password.length() < 1) {
                    Tools.showShortToast("账号或密码不能为空!", LoginActivity.this);
                } else {
                    progressDialog.show();//弹出加载框
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (login(username, password)) {
                                Log.i("test", "成功");
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        intent.putExtra("username", username);//添加传入的参数，将用户名和密码以及用户登录的邮箱类型传给下一个Ac
                                        intent.putExtra("password", password);
                                        intent.putExtra("flag",flag);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                            } else {
                                Log.i("test", "失败");
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                        Tools.showShortToast("登录失败,用户名或密码错误", LoginActivity.this);//提示用户登录失败
                                    }
                                });
                            }
                        }
                    }).start();
                }
            }
        });
        radioGroup = findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_163) {
                    domain.setText("@163.com");
                    edit_pass.setHint("使用授权码登录");
                    flag = 0;
                } else if (checkedId == R.id.radio_sina) {
                    domain.setText("@aliyun.com");
                    edit_pass.setHint("使用密码登录");
                    flag = 1;
                }
            }
        });
    }

    /**
     * 登录验证
     *
     * @param userName
     * @param password
     * @return 登录验证是否通过
     * @throws IOException
     */
    private boolean login(String userName, String password) {
        String smtpUrl = "";
        int port = 25;//端口号
        if (flag == 0) {
            smtpUrl = "smtp.163.com";
        }//163邮箱
        else {
            smtpUrl = "smtp.aliyun.com";
        }//sina邮箱
        try {
            Socket socket = new Socket(smtpUrl, port);
            //socket.setSoTimeout(5000);//5s超时
            if(flag==0)
                userName = new String(Base64.encode(userName.getBytes("utf-8"), Base64.NO_WRAP));//编码用户名
            else
                userName = new String(Base64.encode((userName+"@aliyun.com").getBytes("utf-8"), Base64.NO_WRAP));//编码用户名
            password = new String(Base64.encode(password.getBytes("utf-8"), Base64.NO_WRAP));//编码密码
            InputStream is = socket.getInputStream();//输入字节流
            OutputStream os = socket.getOutputStream();//输出字节流
            BufferedReader bf = new BufferedReader(new InputStreamReader(is));//输入字符流
            String reply = "";//用来存储返回的字符串
            reply = bf.readLine();
            Log.i("test", reply);
            os.write("HELO fluency\r\n".getBytes("utf8"));//启动服务
            reply = bf.readLine();
            Log.i("test", reply);
            if (!reply.contains("250")) {
                bf.close();
                is.close();
                os.close();
                socket.close();
                return false;
            }
            os.write("auth login\r\n".getBytes("utf8"));//告诉服务器我要登录
            reply = bf.readLine();
            Log.i("test", reply);
            if (!reply.contains("334")) {
                bf.close();
                is.close();
                os.close();
                socket.close();
                return false;
            }
            os.write((userName+"\r\n").getBytes("utf8"));//转码后的用户名
            reply = bf.readLine();
            if (!reply.contains("334")) {
                bf.close();
                is.close();
                os.close();
                socket.close();
                return false;
            }
            os.write((password+"\r\n").getBytes("utf8"));//转码后的密码
            reply = bf.readLine();
            Log.i("test", reply);
            if (!reply.contains("235")) {
                bf.close();
                is.close();
                os.close();
                socket.close();
                return false;
            }
            bf.close();
            is.close();
            os.close();
            socket.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}