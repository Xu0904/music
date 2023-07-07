package com.example.music;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import static java.lang.Integer.parseInt;

public class MusicActivity extends AppCompatActivity implements View.OnClickListener{

    public String[] musicName={"青蛙乐队——小跳蛙","夏日入侵企画——想去海边","薛飞——达尔文","林宥嘉——突然好想你","赵雷——成都"};
    //进度条
    private static SeekBar sb;
    private static TextView tv_progress,tv_total,name_song;
    //动画
    private ObjectAnimator animator;
    private MainService.MusicControl musicControl;
    private String name;
    private Intent intent1,intent2;
    private MyServiceConn conn;
    private ImageButton play;//播放按钮
    private ImageButton pause;//暂停按钮
    private ImageButton con;//继续播放按钮
    private ImageButton pre;//上一首按钮
    private ImageButton next;//下一首按钮
    private ImageButton exit;//退出按钮
    public int change=0;//记录下标的变化值
    private ImageView iv_music;//歌手图片框

    //记录服务是否被解绑，默认没有
    private boolean isUnbind =false;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        //获取从frag1传来的信息
        intent1=getIntent();
        init();
    }
    private void init(){
        //进度条上小绿点的位置，也就是当前已播放时间
        tv_progress=(TextView)findViewById(R.id.tv_progress);
        //进度条的总长度，就是总时间
        tv_total=(TextView)findViewById(R.id.tv_total);
        //进度条的控件
        sb=(SeekBar)findViewById(R.id.sb);
        //歌曲名显示的控件
        name_song=(TextView)findViewById(R.id.song_name);
        //绑定控件的同时设置点击事件监听器
        //依次绑定控件
        play=findViewById(R.id.btn_play);
        pause=findViewById(R.id.btn_pause);
        con=findViewById(R.id.btn_continue_play);
        pre=findViewById(R.id.btn_pre);
        next=findViewById(R.id.btn_next);
        exit=findViewById(R.id.btn_exit);

        //依次设置监听器
        play.setOnClickListener(this);
        pause.setOnClickListener(this);
        con.setOnClickListener(this);
        pre.setOnClickListener(this);
        next.setOnClickListener(this);
        exit.setOnClickListener(this);

        name=intent1.getStringExtra("name");
        name_song.setText(name);
        //创建一个意图对象，是从当前的Activity跳转到Service
        intent2=new Intent(this,MainService.class);
        conn=new MyServiceConn();//创建服务连接对象
        bindService(intent2,conn,BIND_AUTO_CREATE);//绑定服务
        //为滑动条添加事件监听，每个控件不同果然点击事件方法名都不同
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            //这一行注解是保证API在KITKAT以上的模拟器才能顺利运行，也就是19以上
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //进当滑动条到末端时，结束动画
                if (progress==seekBar.getMax()){
                    animator.pause();//停止播放动画
                }
            }
            //滑动条开始滑动时调用
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            //滑动条停止滑动时调用
            public void onStopTrackingTouch(SeekBar seekBar) {
                //根据拖动的进度改变音乐播放进度
                int progress=seekBar.getProgress();//获取seekBar的进度
                musicControl.seekTo(progress);//改变播放进度
            }
        });
        //声明并绑定音乐播放器的iv_music控件
        ImageView iv_music=(ImageView)findViewById(R.id.iv_music);

        String position= intent1.getStringExtra("position");
        //praseInt()就是将字符串变成整数类型
        int i=parseInt(position);
        iv_music.setImageResource(frag1.icons[i]);
        //rotation和0f,360.0f就设置了动画是从0°旋转到360°
        animator=ObjectAnimator.ofFloat(iv_music,"rotation",0f,0f);
        animator.setDuration(10000);//动画旋转一周的时间为10秒
        animator.setInterpolator(new LinearInterpolator());//匀速
        animator.setRepeatCount(-1);//-1表示设置动画无限循环
    }
    //handler机制，可以理解为线程间的通信，我获取到一个信息，然后把这个信息告诉你，就这么简单
    public static Handler handler=new Handler(){//创建消息处理器对象
        //在主线程中处理从子线程发送过来的消息
        @Override
        public void handleMessage(Message msg){
            Bundle bundle=msg.getData();//获取从子线程发送过来的音乐播放进度
            //获取当前进度currentPosition和总时长duration
            int duration=bundle.getInt("duration");
            int currentPosition=bundle.getInt("currentPosition");
            //对进度条进行设置
            sb.setMax(duration);
            sb.setProgress(currentPosition);
            //歌曲是多少分钟多少秒钟
            int minute=duration/1000/60;
            int second=duration/1000%60;
            String strMinute=null;
            String strSecond=null;
            if(minute<10){//如果歌曲的时间中的分钟小于10
                strMinute="0"+minute;//在分钟的前面加一个0
            }else{
                strMinute=minute+"";
            }
            if (second<10){//如果歌曲中的秒钟小于10
                strSecond="0"+second;//在秒钟前面加一个0
            }else{
                strSecond=second+"";
            }
            //这里就显示了歌曲总时长
            tv_total.setText(strMinute+":"+strSecond);
            //歌曲当前播放时长
            minute=currentPosition/1000/60;
            second=currentPosition/1000%60;
            if(minute<10){//如果歌曲的时间中的分钟小于10
                strMinute="0"+minute;//在分钟的前面加一个0
            }else{
                strMinute=minute+" ";
            }
            if (second<10){//如果歌曲中的秒钟小于10
                strSecond="0"+second;//在秒钟前面加一个0
            }else{
                strSecond=second+" ";
            }
            //显示当前歌曲已经播放的时间
            tv_progress.setText(strMinute+":"+strSecond);
        }
    };
    //用于实现连接服务，比较模板化，不需要详细知道内容
    class MyServiceConn implements ServiceConnection{
        @Override
        public void onServiceConnected(ComponentName name, IBinder service){
            musicControl=(MainService.MusicControl) service;
        }
        @Override
        public void onServiceDisconnected(ComponentName name){

        }
    }
    //判断服务是否被解绑
    private void unbind(boolean isUnbind){
        //如果解绑了
        if(!isUnbind){
            musicControl.pausePlay();//音乐暂停播放
            unbindService(conn);//解绑服务
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        //定义歌曲列表传过来的下标position
        String position= intent1.getStringExtra("position");
        //将字符串转化为整型i
        int i=parseInt(position);
        if (v.getId() == R.id.btn_play) {
            //播放按钮点击事件
            musicControl.play(i);
            animator.start();
        } else if (v.getId() == R.id.btn_pause) {
            musicControl.pausePlay();
            animator.pause();
        } else if (v.getId() == R.id.btn_continue_play) {
            musicControl.continuePlay();
            animator.start();
        } else if (v.getId() == R.id.btn_exit) {
            unbind(isUnbind);
            isUnbind=true;
            finish();
        } else if (v.getId() == R.id.btn_pre) {
            if((i+change)<1) {
                Toast.makeText(MusicActivity.this, "已经是第一首啦", Toast.LENGTH_SHORT).show();
            }
            else {
                change--;
                name_song.setText(musicName[i+change]);
                musicControl.play(i+change);
                pause.setVisibility(View.VISIBLE);
                animator.start();
            }
        } else if (v.getId() == R.id.btn_next) {
            if((i+change)==musicName.length-1) {//这里musicName.length-1表示的最后一首歌的下标，即歌曲总数-1
                Toast.makeText(MusicActivity.this, "已经是最后一首啦", Toast.LENGTH_SHORT).show();
            }
            else {
                change++;
                name_song.setText(musicName[i+change]);
                musicControl.play(i+change);
                pause.setVisibility(View.VISIBLE);
                animator.start();
            }
        }
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        unbind(isUnbind);//解绑服务
    }
}

