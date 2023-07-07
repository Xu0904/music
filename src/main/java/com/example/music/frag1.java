package com.example.music;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class frag1 extends Fragment {
    private View view;
    //创建歌曲的String数组和歌手图片的int数组
    public String[] name={"青蛙乐队——小跳蛙","夏日入侵企画——想去海边","薛飞——达尔文","林宥嘉——突然好想你","赵雷——成都"};
    public static int[] icons={R.drawable.img2,R.drawable.img2,R.drawable.img2,R.drawable.img2,R.drawable.img2};
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        //绑定布局，只不过这里是用inflate()方法
        view=inflater.inflate(R.layout.music_list,null);
        //创建listView列表并且绑定控件
        ListView listView=view.findViewById(R.id.lv);
        //实例化一个适配器
        MyBaseAdapter adapter=new MyBaseAdapter();
        //列表设置适配器
        listView.setAdapter(adapter);
        //列表元素的点击监听器
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //创建Intent对象，启动音乐播放界面
                Intent intent=new Intent(frag1.this.getContext(), MusicActivity.class);
                //将数据存入Intent对象，利用键值对
                intent.putExtra("name",name[position]);
                intent.putExtra("position",String.valueOf(position));
                //开启意图
                startActivity(intent);
            }
        });
        return view;
    }
    //这里是创建一个自定义适配器，可以作为模板
    class MyBaseAdapter extends BaseAdapter{
        public int getCount(){return  name.length;}
        public Object getItem(int i){return name[i];}
        public long getItemId(int i){return i;}
        public View getView(int i ,View convertView, ViewGroup parent) {
            //绑定好VIew，然后绑定控件
            View view=View.inflate(frag1.this.getContext(),R.layout.item_layout,null);
            TextView tv_name=view.findViewById(R.id.item_name);
            ImageView iv=view.findViewById(R.id.iv);
            //设置控件显示的内容，就是获取的歌曲名和歌手图片
            tv_name.setText(name[i]);
            iv.setImageResource(icons[i]);
            return view;
        }
    }

}

