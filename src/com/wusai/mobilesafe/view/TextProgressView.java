package com.wusai.mobilesafe.view;

import com.itheima.mobilesafe74.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TextProgressView extends RelativeLayout {

	private ProgressBar pb_progress_view;
	private TextView tv_memory_view;
	public TextProgressView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		View view=View.inflate(context,R.layout.view_progress,this);
		pb_progress_view = (ProgressBar) view.findViewById(R.id.pb_progress_view);
		tv_memory_view = (TextView) view.findViewById(R.id.tv_memory_view);
	}

	public TextProgressView(Context context, AttributeSet attrs) {
		this(context, attrs,0);
		// TODO Auto-generated constructor stub
	}

	public TextProgressView(Context context) {
		this(context,null);
		// TODO Auto-generated constructor stub
	}
	public void setProgress(int progress){
		pb_progress_view.setProgress(progress);
	}
	public void setText(String text){
		tv_memory_view.setText(text);
	}


}
