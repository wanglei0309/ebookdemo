package com.michael.fragment;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.michael.main.ListBookTopAdapter;
import com.michael.main.PullToRefreshListView;
import com.michael.main.R;

public class FragmentTeam extends Fragment
{
    PullToRefreshListView listView; 
    ListBookTopAdapter adapter;
	public FragmentTeam()
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
	    View layout = myInflater.inflate(R.layout.frag_team, container, false); 
	    listView = (PullToRefreshListView) layout.findViewById(R.id.listView1);
	    listView.setIsNeedHeadRefresh(false);
	    
	    ArrayList<String> list = new ArrayList<String>();
	    list.add("中国姓氏文化");
	    list.add("厚黑学");
	    list.add("增广贤文");
	    list.add("医事困学录");
	    list.add("中医五脏养生");
		adapter = new ListBookTopAdapter(list, getActivity());
		
		listView.setAdapter(adapter);
	    
		return layout;
	}

	
	
}
