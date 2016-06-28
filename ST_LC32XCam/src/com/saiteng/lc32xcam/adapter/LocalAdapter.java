package com.saiteng.lc32xcam.adapter;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import com.example.st_lc32xcam.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.saiteng.st_lc32xcam.utils.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 本地列表适配器.
 * 
 * @author cui_tao
 */
public class LocalAdapter extends BaseAdapter {
	
    /**
     * 布局.
     */
    private LayoutInflater mInflater;

    /**
     * 文件夹显示图片.
     */
    private Bitmap icon1;

    /**
     * 文件显示图片.
     */
    private Bitmap icon2;

    /**
     * 本地文件列表.
     */
    private List<File> list;

    /**
     * 构造函数.
     * @param context 当前环境
     * @param li 本地文件列表
     */
    public LocalAdapter(Context context, List<File> li) {
        this.mInflater = LayoutInflater.from(context);
        this.list = li;
        // 文件夹显示图片
        this.icon1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.folder);
        // 文件显示图片
        this.icon2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.video);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;

        if (view == null) {
            // 设置视图
            view = mInflater.inflate(R.layout.ftp_list, null);
            // 获取控件实例
            holder = new ViewHolder();
            holder.fileName = (TextView) view.findViewById(R.id.text_name);
            holder.fileSize = (TextView) view.findViewById(R.id.text_size);
            holder.icon = (ImageView) view.findViewById(R.id.image_icon);
            // 设置标签
            view.setTag(holder);
        } else {
            // 获取标签
            holder = (ViewHolder) view.getTag();
        }
        // 获取文件
        File file = list.get(position);
        // 判断是否为一个目录
        if (!file.isDirectory()) {
            try {
                // 创建输入流
                FileInputStream inputStream = new FileInputStream(file);
                // 获得流大小
                double size = (double) inputStream.available() / 1;
                // 获取文件大小
                holder.fileSize.setText(Util.getFormatSize(size));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
      
        if (file.isDirectory()) {
        	  // 获取文件名
            holder.fileName.setText(file.getName());
            // 获取显示文件夹图片
            holder.icon.setImageBitmap(icon1);
        } else {
        	  // 获取文件名
        	 holder.fileName.setText(file.getName().substring(5));
     
            // 获取显示文件图片
            holder.icon.setImageBitmap(icon2);
        }
        return view;
    }

    /**
     * 获取控件.
     */
    private class ViewHolder {
        /**
         * 图片.
         */
        private ImageView icon;

        /**
         * 文件名.
         */
        private TextView fileName;

        /**
         * 文件大小.
         */
        private TextView fileSize;
    }

}
