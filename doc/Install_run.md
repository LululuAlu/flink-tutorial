## 下载安装包

[访问官网](https://flink.apache.org/),[下载](https://flink.apache.org/downloads.html)最新版本。本教程使用Flink1.9版本。
![download](http://lgwen.cn/upload/2019/11/download-8c1f83db1c7f43958c170ab76b180a4c.png)

## 目录说明
![dir](http://lgwen.cn/upload/2019/11/dir-343c84bf0ce44057a53d3f49435bec27.png)
* bin flink 启动的脚本文件
* lib flink 运行时jar包依赖
* conf flink 启动的配置，如web端口，日志配置等
* example flink 提供了很多例子，不但有流和批的还有python接口的

## 执行例子
* 启动cluster单机版  
  进入bin执行命令 windows:`.\start-cluster.bat`、linux：`.\start-cluster.bat`
* 介绍web界面  
  启动完成后打开`localhost:8081`查看flink web页面 
![dashboard](http://lgwen.cn/upload/2019/11/dashboard-f65dfe51b76f420cabdff6b0af0da4af.png)
    1. solt 槽位，flink资源单位
    2. 当前运行的job数量
    3. TaskManager 任务都是在TaskManager上运行
    4. JobManager 用来管理TaskManager

### 关注我的公众号
了解我的最新动向  
![qrcode_for_gh_eac3d4651e58_344](http://qiniu.lgwen.cn/wechat/qrcode_for_gh_eac3d4651e58_344-416a7124e3704fdf96d24e5c87246e86.jpg)
### 收藏我的个人[博客](http://lgwen.cn)
