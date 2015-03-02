package com.michael.fragment;

import java.util.List;

import android.content.Context;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import com.michael.main.R;

public class FragmentSearch extends Fragment
{
    private GridView bookShelf;
    private int[] data = {
			R.drawable.cover_txt,R.drawable.cover_txt,R.drawable.cover_txt,R.drawable.cover_txt,R.drawable.cover_txt,
			R.drawable.cover_txt,R.drawable.cover_txt,R.drawable.cover_txt,R.drawable.cover_txt,R.drawable.cover_txt,
			R.drawable.cover_txt,R.drawable.cover_txt,R.drawable.cover_txt,R.drawable.cover_txt,
			R.drawable.cover_txt,R.drawable.cover_txt,R.drawable.cover_txt,R.drawable.cover_txt,
			R.drawable.cover_txt,R.drawable.cover_txt,R.drawable.cover_txt,R.drawable.cover_txt,
			R.drawable.cover_txt,R.drawable.cover_txt,R.drawable.cover_txt,R.drawable.cover_txt,
			R.drawable.cover_txt,R.drawable.cover_txt,R.drawable.cover_txt,R.drawable.cover_txt,
			R.drawable.cover_txt,R.drawable.cover_txt,R.drawable.cover_txt,R.drawable.cover_txt
			
	};
    private String[] name={
    		"天龙八部","搜神记","水浒传","黑道悲情"
    };
    
    private GridView gv;  
    private SlidingDrawer sd;  
    private Button iv;  
    private List<ResolveInfo> apps;  

	public FragmentSearch()
	{
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{

		if (container == null) 
		{
            // Currently in a layout without a container, so no
            // reason to create our view.
            return null;
        }
		
		LayoutInflater myInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
	    View layout = myInflater.inflate(R.layout.frag_search, container, false); 
	    
	    bookShelf = (GridView)layout.findViewById(R.id.bookShelf);
        ShlefAdapter adapter=new ShlefAdapter();
        bookShelf.setAdapter(adapter);
        bookShelf.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if(arg2>=data.length){
					
				}else{
				   Toast.makeText(getActivity(), ""+arg2, Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		return layout;
	}

	
	 class ShlefAdapter extends BaseAdapter{

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return data.length+5;
			}

			@Override
			public Object getItem(int arg0) {
				// TODO Auto-generated method stub
				return arg0;
			}

			@Override
			public long getItemId(int arg0) {
				// TODO Auto-generated method stub
				return arg0;
			}

			@Override
			public View getView(int position, View contentView, ViewGroup arg2) {
				// TODO Auto-generated method stub
				
				contentView=LayoutInflater.from(getActivity()).inflate(R.layout.item1, null);
				
				TextView view=(TextView) contentView.findViewById(R.id.imageView1);
				if(data.length>position){
					if(position<name.length){
					   view.setText(name[position]);
					}
					view.setBackgroundResource(data[position]);
				}else{
					view.setBackgroundResource(data[0]);
					view.setClickable(false);
					view.setVisibility(View.INVISIBLE);
				}
				return contentView;
			}
	    	
	    }
}
