# EmailUserAgent
## 简介
A simple EmailUserAgent on Android platform<br><br>
《计算机网络》课程项目，利用SOCKET通过SMTP/POP3协议实现了向用户邮件SMTP Server上传需要发送的邮件和从用户POP3 Server下载读取收件箱的邮件的基础功能。
<br><br>
    项目中实现的用户邮件代理支持[163邮箱](https://mail.163.com/)和[aliyun邮箱](https://mail.aliyun.com/)

## 效果图
![login](/img/login.jpg "loginPage")
![main](/img/main.jpg "mainPage")
![send](/img/send.jpg "sendEmailPage")
![receive](/img/receive.jpg "receiveEmailPage")
## Tips

* 支持的用户邮件代理可以自己随意替换，当然，不同的邮箱有不同的代理登录方式要求，比如上面的163邮箱就需要使用授权码登录而aliyun邮箱不需要使用授权码直接使用登录密码登录
* 由于要求中没有要求解析MIME格式的邮件，所以项目中未对从POP3邮件服务器获取到的MIME格式的邮件进行解析，如果有要求可以参考相关资料实现
* 附件的功能同上
