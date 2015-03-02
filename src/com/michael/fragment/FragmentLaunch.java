package com.michael.fragment;

import java.util.ArrayList;

import android.content.Context;
import android.location.Address;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.michael.main.ListBookAdapter;
import com.michael.main.R;

public class FragmentLaunch extends Fragment
{
	ListView listView;
	ListBookAdapter adapter ;

	public FragmentLaunch()
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
	    View layout = myInflater.inflate(R.layout.frag_launch, container, false); 
	    listView = (ListView) layout.findViewById(R.id.listView1);
	    
		View footview = myInflater.inflate(R.layout.footview, null); 
		View headview = myInflater.inflate(R.layout.headview, null); 
		listView.addHeaderView(headview);
		listView.addFooterView(footview);
	    
	    ArrayList<String> list = new ArrayList<String>();
	    list.add("精品推荐");
	    list.add("养身");
	    list.add("童趣");
	    list.add("情感");
	    list.add("科教");
	    adapter = new ListBookAdapter(list, getActivity());
	    listView.setAdapter(adapter);
		return layout;
	}

	
	
}
