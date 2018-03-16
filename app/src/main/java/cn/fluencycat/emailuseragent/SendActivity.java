package cn.fluencycat.emailuseragent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * 发送邮件Activity
 *
 * @author Fluency
 */

public class SendActivity extends Activity {
    private EditText receiver, title, content;//收件人，邮件主题，邮件内容
    private TextView sender;//发件人
    private String sender_str = "";//发件人邮箱地址
    private String username = MainActivity.username;
    private String password = MainActivity.password;
    private int flag = MainActivity.flag;
    private Handler handler = null;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        initView();//初始化View
    }

    private void initView() {
        progressDialog = new ProgressDialog(this);//创建加载框
        progressDialog.setMessage("发送中...");
        progressDialog.setCancelable(false);//不可点击取消
        handler = new Handler();
        receiver = findViewById(R.id.send_receiver);
        title = findViewById(R.id.send_title);
        content = findViewById(R.id.send_content);
        sender = findViewById(R.id.send_sender);
        if (flag == 0)
            sender_str = username + "@163.com";
        else
            sender_str = username + "@aliyun.com";
        sender.setText(sender_str);
    }

    /**
     * 取消按钮
     *
     * @param v
     */
    public void send_cancel(View v) {
        back();
    }

    /**
     * 退出弹出提示框
     */
    private void back() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("邮件还未发送，返回将不会保存邮件内容，确定返回吗?")
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

    /**
     * 按下返回键
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
            back();
        return false;
    }

    /**
     * 发送按钮
     *
     * @param v
     */
    public void send_sure(View v) {
        final String receiver_str = receiver.getText().toString();
        if (receiver_str.length() < 1) {
            Tools.showShortToast("请输入收件人", SendActivity.this);
            return;
        }
        final String title_str = title.getText().toString().trim();
        if (title_str.length() < 1) {
            Tools.showShortToast("请输入邮件主题", SendActivity.this);
            return;
        }
        final String content_str = content.getText().toString();
        if (content_str.length() < 1) {
            Tools.showShortToast("请输入邮件内容", SendActivity.this);
            return;
        }
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (send_mail(receiver_str, title_str, content_str))
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            Tools.showShortToast("发送成功", SendActivity.this);
                            finish();
                        }
                    });
                else
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            Tools.showShortToast("发送失败，请重试", SendActivity.this);
                        }
                    });
            }
        }).start();
    }

    private boolean send_mail(String receiver_str, String mail_title, String mail_content) {
        String smtpUrl = "";
        int port = 25;
        if (flag == 0)
            smtpUrl = "smtp.163.com";
        else
            smtpUrl = "smtp.aliyun.com";
        try {
            String user=null;
            if(flag==0)
                user = new String(Base64.encode(username.getBytes("utf-8"), Base64.NO_WRAP));//编码用户名
            else
                user = new String(Base64.encode(sender_str.getBytes("utf-8"), Base64.NO_WRAP));//编码用户名
            String pass = new String(Base64.encode(password.getBytes("utf-8"), Base64.NO_WRAP));
            Socket socket = new Socket(smtpUrl, port);
            InputStream is = socket.getInputStream();//输入字节流
            OutputStream os = socket.getOutputStream();//输出字节流
            BufferedReader bf = new BufferedReader(new InputStreamReader(is));//输入字符流
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(os,"utf-8"), true);//输出字符流
            String reply = "";
            reply = bf.readLine();
            os.write("HELO fluency\r\n".getBytes("utf8"));//打招呼
            reply = bf.readLine();
            os.write("auth login\r\n".getBytes("utf8"));//告诉服务器我要登录
            reply = bf.readLine();
            os.write((user+"\r\n").getBytes("utf8"));//用户名
            reply = bf.readLine();
            os.write((pass+"\r\n").getBytes("utf8"));//密码
            reply = bf.readLine();
            Log.i("test", reply);
            if (!reply.contains("235")) {
                pw.close();
                bf.close();
                is.close();
                os.close();
                socket.close();
                return false;
            }
            os.write(("mail from:<" + sender_str + ">\r\n").getBytes("utf8"));//发件人
            reply = bf.readLine();
            Log.i("test", reply);
            if (!reply.contains("250")) {
                pw.close();
                bf.close();
                is.close();
                os.close();
                socket.close();
                return false;
            }
            os.write(("rcpt to:<" + receiver_str + ">\r\n").getBytes("utf8"));//收件人
            reply = bf.readLine();
            Log.i("test", reply);
            if (!reply.contains("250")) {
                pw.close();
                bf.close();
                is.close();
                os.close();
                socket.close();
                return false;
            }
            os.write("data\r\n".getBytes("utf8"));//开始写信
            reply = bf.readLine();
            Log.i("test", reply);
            if (!reply.contains("354")) {
                pw.close();
                bf.close();
                is.close();
                os.close();
                socket.close();
                return false;
            }
            //邮件详细信息
            os.write(("from:"+sender_str+"\r\n").getBytes("utf8"));//发送方
            os.write(("to:"+receiver_str+"\r\n").getBytes("utf8"));//接收方
            os.write(("subject:"+mail_title+"\r\n").getBytes("utf8"));//主题
            os.write(("Content-Type:text/plain;charset=utf8"+"\r\n\r\n").getBytes("utf8"));//编码和字体设置,再加一个空行
            os.write((mail_content+"\r\n").getBytes("utf8"));//内容
            os.write((".\r\n").getBytes("utf8"));//结束标志
            reply = bf.readLine();
            Log.i("test", reply);
            if (!reply.contains("250")) {
                pw.close();
                bf.close();
                is.close();
                os.close();
                socket.close();
                return false;
            }
            os.write("rset\r\n".getBytes("utf8"));//重置
            reply = bf.readLine();
            Log.i("test", reply);
            os.write("quit\r\n".getBytes("utf8"));//退出smtp服务
            reply = bf.readLine();
            Log.i("test", reply);
            //发送结束关闭各种流和socket
            pw.close();
            bf.close();
            is.close();
            os.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
