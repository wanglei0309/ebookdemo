package com.michael.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.michael.fragment.FragmentExecute;
import com.michael.fragment.FragmentSetting;
import com.michael.fragment.FragmentLaunch;
import com.michael.fragment.FragmentSearch;
import com.michael.fragment.FragmentTeam;
import com.michael.widget.BottomBar;
import com.michael.widget.BottomBar.OnItemChangedListener;


/**
 * This demo shows how to use FragmentActivity to build the frame of a common application.
 * To replace the deprecated class such as TabActivity, ActivityGroup,and so on.
 * 
 * ���demoչʾ�����ʹ��FragmentActivity������Ӧ�ó���Ŀ��
 * ����ʹ����������ԭ����TabActivity��ActivityGroup�ȵ�
 * 
 * @author MichaelYe
 * @since 2012-9-6
 * @see http://developer.android.com/reference/android/app/Fragment.html
 * @see http://developer.android.com/training/basics/fragments/index.html
 * @see http://developer.android.com/guide/components/fragments.html
 * */
public class MainActivity extends FragmentActivity 
{

	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final BottomBar bottomBar = (BottomBar)findViewById(R.id.ll_bottom_bar);
        bottomBar.setOnItemChangedListener(new OnItemChangedListener() 
        {
			
			@Override
			public void onItemChanged(int index) 
			{

				showDetails(index);
			}
		});
        bottomBar.setSelectedState(0);
        
//        bottomBar.hideIndicate();//�������ԭ�����ƺ�ɫСͼ��Ŀɼ���
//        bottomBar.showIndicate(12);
        
    }
	
	private void showDetails(int index)
	{
		Fragment details = (Fragment)
	            getSupportFragmentManager().findFragmentById(R.id.details);
		switch(index)
		{
		case 0:
			details = new FragmentSearch();
			break;
		case 1:
			details = new FragmentLaunch();
			break;
		case 2:
			details = new FragmentTeam();
			break;
		case 3:
			details = new FragmentExecute();
			break;
		}
		// Execute a transaction, replacing any existing
        // fragment with this one inside the frame.
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.details, details);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//        ft.addToBackStack(null);//���д�����Է���֮ǰ�Ĳ���������������£������߶���ʾ������£�
        ft.commit();
	}

    
}
