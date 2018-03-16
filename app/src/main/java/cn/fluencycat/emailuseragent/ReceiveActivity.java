package cn.fluencycat.emailuseragent;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * 接受邮件Activity,选择接受第几封邮件
 *
 * @author Fluency
 */

public class ReceiveActivity extends Activity {
    private EditText edit_num;//用户选择第几封邮件
    private TextView mailContent,number;//邮件内容,邮件数量
    private String username;
    private String password;
    private int flag;
    private int mailNumber;
    private ProgressDialog dialog;
    private Handler handler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);
        initView();//初始化控件
        initData();//初始化数据
    }

    private void initView() {
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("请稍后...");
        edit_num = findViewById(R.id.mail_number);
        mailContent = findViewById(R.id.text_content);
        number=findViewById(R.id.text_number);
    }

    private void initData() {
        handler = new Handler();
        username = MainActivity.username;
        password = MainActivity.password;
        flag = MainActivity.flag;
        dialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                mailNumber = getMailNumber();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mailNumber == -1) {
                            Tools.showShortToast("获取邮件数量失败,请检查网络",ReceiveActivity.this);
                            dialog.dismiss();
                            finish();
                        }
                        else{
                            dialog.dismiss();
                            number.setText("收件箱中共有"+mailNumber+"封邮件");
                        }
                    }
                });
            }
        }).start();
    }

    //得到用户邮件数量
    private int getMailNumber() {
        String pop3Url = "";
        int port = 110;//端口号
        if (flag == 0) {
            pop3Url = "pop3.163.com";
        } else {
            pop3Url = "pop3.aliyun.com";
            username = username + "@aliyun.com";//阿里云邮箱
        }
        try {
            Socket socket = new Socket(pop3Url, port);
            InputStream is = socket.getInputStream();//输入字节流
            OutputStream os = socket.getOutputStream();//输出字节流
            BufferedReader bf = new BufferedReader(new InputStreamReader(is, "UTF8"));//输入字符流
            String reply = "";
            reply = bf.readLine();
            Log.i("test", reply);
            if (!reply.contains("OK")) {
                bf.close();
                is.close();
                os.close();
                socket.close();
                return -1;
            }
            os.write(("user " + username + "\r\n").getBytes("utf8"));//用户名
            reply = bf.readLine();
            Log.i("test", reply);
            if (!reply.contains("OK")) {
                bf.close();
                is.close();
                os.close();
                socket.close();
                return -1;
            }
            os.write(("pass " + password + "\r\n").getBytes("utf8"));//密码
            reply = bf.readLine();
            Log.i("test", reply);
            if (!reply.contains("OK")) {
                bf.close();
                is.close();
                os.close();
                socket.close();
                return -1;
            }
            bf.close();
            is.close();
            os.close();
            socket.close();
            return Integer.parseInt(reply.substring(reply.indexOf("+OK ") + 4, reply.indexOf(" message")));//邮件数量
        } catch (Exception e) {
            e.printStackTrace();
            return -1;//获取邮件数量失败
        }
    }

    /**
     * 返回
     *
     * @param v
     */
    public void receive_back(View v) {
        finish();
    }

    /**
     * 确定
     *
     * @param v
     */
    public void receive_sure(View v) {
        if(mailNumber==0){
            Tools.showShortToast("邮箱里没有邮件哦~~~",this);
            return;
        }
        int selectNumber=0;
        try{
            selectNumber=Integer.parseInt(edit_num.getText().toString().trim());
        }catch (NumberFormatException e){
            e.printStackTrace();
            Tools.showShortToast("请输入正确的数字!",this);
            return;
        }
        if(selectNumber<1||selectNumber>mailNumber){
            Tools.showShortToast("请输入1-"+mailNumber+"之间的数值~",this);
            return;
        }
        final int num=selectNumber;
        dialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String content=getMailContent(num);
                if(content.length()==0){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Tools.showShortToast("获取邮件信息失败,请重试",ReceiveActivity.this);
                            dialog.dismiss();
                        }
                    });
                }
                else{
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mailContent.setText(content);
                            dialog.dismiss();
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * 返回邮件内容
     * @param num
     * @return
     */
    private String getMailContent(int num){
        String pop3Url = "";
        int port = 110;//端口号
        if (flag == 0) {
            pop3Url = "pop3.163.com";
        } else {
            pop3Url = "pop3.aliyun.com";
        }
        try {
            Socket socket = new Socket(pop3Url, port);
            InputStream is = socket.getInputStream();//输入字节流
            OutputStream os = socket.getOutputStream();//输出字节流
            BufferedReader bf = new BufferedReader(new InputStreamReader(is, "UTF8"));//输入字符流
            String reply = "";
            reply = bf.readLine();
            Log.i("test", reply);
            if (!reply.contains("OK")) {
                bf.close();
                is.close();
                os.close();
                socket.close();
                return "";
            }
            os.write(("user " + username + "\r\n").getBytes("utf8"));//用户名
            reply = bf.readLine();
            Log.i("test", reply);
            if (!reply.contains("OK")) {
                bf.close();
                is.close();
                os.close();
                socket.close();
                return "";
            }
            os.write(("pass " + password + "\r\n").getBytes("utf8"));//密码
            reply = bf.readLine();
            Log.i("test", reply);
            if (!reply.contains("OK")) {
                bf.close();
                is.close();
                os.close();
                socket.close();
                return "";
            }
            os.write(("retr "+num+"\r\n").getBytes("utf8"));//密码
            String content="";
            String temp="";
            temp=bf.readLine();
            while(!temp.equals(".")){
                content=content+"\n"+temp;
                temp=bf.readLine();
            }
            bf.close();
            is.close();
            os.close();
            socket.close();
            return content;
        } catch (Exception e) {
            e.printStackTrace();
            return "";//获取邮件数量失败
        }
    }
}
