package com.michael.main;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 
 * 〈一句话功能简述〉<br>
 * 〈功能详细描述〉上拉刷新、下拉加载更多控件
 * 
 * @author 12100128
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class PullToRefreshListView extends ListView implements OnScrollListener
{
    private static final String TAG = "PullToRefreshListView";
    public static int HEAD_REFRESH = 1010100;
    public static int FOOT_REFRESH = 1010101;

    public static final int RELEASE_TO_REFRESH = 0;
    public static final int PULL_TO_REFRESH = 1;
    public static final int REFRESHING = 2;
    public static final int DONE = 3;
    public static final int LOADING = 4;

    /**
     * 比率，触摸滑动的距离与list实际滑动的距离比
     */
    private static final int RATIO = 1;

    public Context context;
    public LayoutInflater inflater;

    public LinearLayout headView;
    public ImageView headArrowImageView;
    public ProgressBar headProgressBar;
    public TextView headTipsTextview;
    public TextView headLastUpdatedTextView;
    public int headHight;

    public LinearLayout footView;
    public ImageView footArrowImageView;
    public ProgressBar footProgressBar;
    public TextView footTipsTextview;
    public TextView footLastUpdatedTextView;
    public int footHight;

    private OnPullRefreshListener refreshListener;
    private OnSearchViewListener searchViewListener;
    private RotateAnimation reverseAnimation;
    private RotateAnimation animation;
    /**
     * 是否由RELEASE_To_REFRESH转到PULL_TO_REFRESH状态
     */
    private boolean isBack = false;

    /**
     * list第一条可见的下标
     */
    private int firstVisiableItem = 0;

    /**
     * 可见的ListItem总数
     */
    private int visibleItemCount = 0;

    /**
     * footview是否在上拉时完全可见
     */
    private boolean isFootVisible = false;

    /**
     * 可见的最上面item是否为list第一条
     */
    private boolean isFirstIndex = false;

    /**
     * 可见的最下面item是否为list最后一条
     */
    private boolean isLastIndex = true;

    private int startY;
    /**
     * 上拉、下拉刷新时的状态
     */
    private int headState;

    /**
     * 上拉、下拉刷新时的状态
     */
    private int footState;
    /**
     * 是否已标记拖动刷新时的起始点
     */
    private boolean isHeadRecord = false;
    /**
     * 是否已标记拖动刷新时的起始点
     */
    private boolean isFootRecord = false;

    private int tempY;

    /**
     * 网络中是否还有更多页的数据
     */
    private boolean isHaveMorePage = true;

    private boolean isNeedHeadRefresh = true;

    public PullToRefreshListView(Context context, AttributeSet attrs,
            int defStyle)
    {
        super(context, attrs, defStyle);
        this.context = context;
        init();
    }

    public PullToRefreshListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.context = context;
        init();
    }

    public PullToRefreshListView(Context context)
    {
        super(context);
        this.context = context;
        init();
    }

    public void Log(String s)
    {
        android.util.Log.i("PullToRefreshView==>", s);
    };
    
    public void setHeadState(int state){
    	this.headState = state;
    }

    public void onHeadRefresh()
    {
        if (refreshListener != null)
        {
            refreshListener.onHeadRefresh();
        }
    }

    private void onFootRefresh()
    {
        if (refreshListener != null)
        {
            refreshListener.onFootRefresh();
        }
    }

    private void onMoveRefresh()
    {
        if (refreshListener != null)
        {
            refreshListener.onMoveRefresh();
        }
    }

    public void setOnRefreshListener(OnPullRefreshListener refreshListener)
    {
        this.refreshListener = refreshListener;
    }

    public void setOnSearchShowListener(OnSearchViewListener searchViewListener)
    {
        this.searchViewListener = searchViewListener;
    }
    
    public void onSearchViewShow()
    {
        if (searchViewListener != null)
        {
            searchViewListener.onSearchViewShow();
        }
    }
    
    private void onSearchViewHide()
    {
        if (searchViewListener != null)
        {
            searchViewListener.onSearchViewHide();
        }
    }
    
    public void init()
    {
        inflater = LayoutInflater.from(context);
        headView = (LinearLayout) inflater.inflate(R.layout.list_head_view,
                null);
        headArrowImageView = (ImageView) headView
                .findViewById(R.id.head_arrowImageView);
        headProgressBar = (ProgressBar) headView
                .findViewById(R.id.head_progressBar);
        headTipsTextview = (TextView) headView
                .findViewById(R.id.head_tipsTextView);
        headLastUpdatedTextView = (TextView) headView
                .findViewById(R.id.head_lastUpdatedTextView);
        measureView(headView);
        headHight = headView.getMeasuredHeight();
        Log("headHight=" + headHight);
        headView.setPadding(0, -1 * headHight, 0, 0);
        addHeaderView(headView, null, false);

        footView = (LinearLayout) inflater.inflate(R.layout.list_foot_view,
                null);
        footArrowImageView = (ImageView) footView
                .findViewById(R.id.foot_arrowImageView);
        footProgressBar = (ProgressBar) footView
                .findViewById(R.id.foot_progressBar);
        footTipsTextview = (TextView) footView
                .findViewById(R.id.foot_tipsTextView);
        footLastUpdatedTextView = (TextView) footView
                .findViewById(R.id.foot_lastUpdatedTextView);
        measureView(footView);
        footHight = footView.getMeasuredHeight();

        DisplayMetrics metric = new DisplayMetrics();

        ((Activity) context).getWindowManager().getDefaultDisplay()
                .getMetrics(metric);
        int width = metric.widthPixels; // 屏幕宽度（像素）
        Log("metric.width=" + width);
        LayoutParams footParams = (LayoutParams) footView.getLayoutParams();
        Log("footParams =" + footParams);
        if (footParams == null)
        {
            footParams = new LayoutParams(LayoutParams.FILL_PARENT,
                    LayoutParams.WRAP_CONTENT);
        }
        footParams.width = width;
        footView.setLayoutParams(footParams);
        footView.setPadding(0, 0, 0, -footHight);

        addFooterView(footView, null, false);

        setOnScrollListener(this);
        headState = DONE;
        footState = DONE;

        animation = new RotateAnimation(0, -180,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(250);
        animation.setFillAfter(true);

        reverseAnimation = new RotateAnimation(-180, 0,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        reverseAnimation.setInterpolator(new LinearInterpolator());
        reverseAnimation.setDuration(200);
        reverseAnimation.setFillAfter(true);

    }

    private void measureView(View child)
    {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null)
        {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0)
        {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
                    MeasureSpec.EXACTLY);
        }
        else
        {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0,
                    MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev)
    {

        int action = ev.getAction();
        int dY = 0;
        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
                if (firstVisiableItem == 0 && !isHeadRecord)
                {
                    startY = (int) ev.getY();
                    isHeadRecord = true;

                }
                if (isLastIndex && !isFootRecord)
                {
                    startY = (int) ev.getY();
                    isFootRecord = true;
                }
                tempY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                onMoveRefresh();
                int curY = (int) ev.getY();
                // LogX.e(this, "firstVisiableItem: " + firstVisiableItem
                // + " and isNeedHeadRefresh: " + isNeedHeadRefresh);
                // LogX.e(this, "isLastIndex: " + isLastIndex
                // + " and isHaveMorePage: " + isHaveMorePage);
//                LogUtil.d("wl", "下拉刷新："+firstVisiableItem);
                if (firstVisiableItem == 0 && isNeedHeadRefresh)
                {
                    if (!isHeadRecord)
                    {
                        startY = curY;
                        isHeadRecord = true;
                    }

                    dY = (curY - startY) / RATIO;
                    if (isHeadRecord && headState != REFRESHING)
                    {
                        if (headState == PULL_TO_REFRESH)
                        {
                            if (dY >= headHight)
                            {
                                headState = RELEASE_TO_REFRESH;
                                isBack = true;
                                changeHeaderViewByState();
                                onSearchViewShow();
                            }
                            else if (dY < 0)
                            {
                                headState = DONE;
                                changeHeaderViewByState();
                                onSearchViewHide();
                            }
                        }
                        if (headState == RELEASE_TO_REFRESH)
                        {
                            setSelection(0);
                            // Log("dY=" + dY + " headHight=" + headHight);
                            if (dY < headHight && dY > 0)
                            {
                                headState = PULL_TO_REFRESH;
                                changeHeaderViewByState();
                            }
                            else if (dY <= 0)
                            {
                                headState = DONE;
                                changeHeaderViewByState();
                            }
                        }
                        if (headState == DONE && dY > 0)
                        {
                            headState = PULL_TO_REFRESH;
                            changeHeaderViewByState();
                        }
                        int upPadding = dY - headHight;
                        int topPadding = (int) (1.5 * headHight);
                        headView.setPadding(
                                0,
                                upPadding > topPadding ? topPadding : upPadding,
                                0, 0);
                    }
                }

                else if (isLastIndex && isHaveMorePage)
                {

                    if (!isFootRecord)
                    {
                        startY = curY;
                        isFootRecord = true;
                    }
                    dY = (curY - startY) / RATIO;

                    Log("---dY---:" + dY + "   footHight : " + footHight);
                    if (isFootRecord && footState != REFRESHING)
                    {
                        if (footState == DONE && dY <= 0)
                        {
                            footState = PULL_TO_REFRESH;
                            changeFooterViewByState();
                        }
                        if (footState == PULL_TO_REFRESH)
                        {
                            // Log("PULL_TO_REFRESH setSelection="
                            // + (getCount() - 1));
                            setSelection(getCount() - 1);
                            if (dY < -footHight)
                            {
                                footState = RELEASE_TO_REFRESH;
                                isBack = true;
                                changeFooterViewByState();
                            }
                            else if (dY >= 0)
                            {
                                footState = DONE;
                                changeFooterViewByState();
                            }
                        }
                        if (footState == RELEASE_TO_REFRESH)
                        {
                            // Log("RELEASE_TO_REFRESH setSelection="
                            // + (getCount() - 1));
                            setSelection(getCount() - 1);
                            if (dY < 0 && dY > -footHight)
                            {
                                footState = PULL_TO_REFRESH;
                                changeFooterViewByState();
                            }
                            else if (dY >= 0)
                            {
                                footState = DONE;
                                changeFooterViewByState();
                            }
                        }
                        if (!isFootVisible)
                        {
                            footView.setPadding(0, 0, 0, 0);
                        }
                        else
                        {
                            int bottomPadding = -footHight - dY;
                            int topPadding = (int) (1.5 * footHight);
                            footView.setPadding(0, 0, 0,
                                    bottomPadding > topPadding ? topPadding
                                            : bottomPadding);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (isFootRecord || isHeadRecord)
                {
                    if (headState == RELEASE_TO_REFRESH)
                    {
                        headState = REFRESHING;
                        changeHeaderViewByState();
                        onHeadRefresh();
                    }
                    else
                    {
                        headState = DONE;
                        changeHeaderViewByState();
                    }

                    if (footState == RELEASE_TO_REFRESH)
                    {
                        footState = REFRESHING;
                        changeFooterViewByState();
                        onFootRefresh();
                    }
                    else
                    {
                        footState = DONE;
                        changeFooterViewByState();
                    }
                    isBack = false;
                    isFootRecord = false;
                    isHeadRecord = false;
                }
                break;
        }

        return super.onTouchEvent(ev);
    }
    
    /**
     * @Description:手动调用listview头部刷新
     * @Author Administrator
     * @Date 2013-10-27
     */
    public void setHeadRefreshed(){
        headState = REFRESHING;
        changeHeaderViewByState();
        onHeadRefresh();
    }

    // 当状态改变时候，调用该方法，以更新界面
    public void changeHeaderViewByState()
    {
        switch (headState)
        {
            case RELEASE_TO_REFRESH:
                headArrowImageView.setVisibility(View.VISIBLE);
                headProgressBar.setVisibility(View.GONE);
                headTipsTextview.setVisibility(View.VISIBLE);
                headLastUpdatedTextView.setVisibility(View.VISIBLE);

                headArrowImageView.clearAnimation();
                headArrowImageView.startAnimation(animation);

                headTipsTextview.setText("松开刷新");

                break;
            case PULL_TO_REFRESH:
                headProgressBar.setVisibility(View.GONE);
                headTipsTextview.setVisibility(View.VISIBLE);
                headLastUpdatedTextView.setVisibility(View.VISIBLE);
                headArrowImageView.clearAnimation();
                headArrowImageView.setVisibility(View.VISIBLE);
                // 是由RELEASE_To_REFRESH状态转变来的
                if (isBack)
                {
                    isBack = false;
                    headArrowImageView.clearAnimation();
                    headArrowImageView.startAnimation(reverseAnimation);
                }
                headTipsTextview.setText("下拉刷新");
                break;

            case REFRESHING:

                headView.setPadding(0, 0, 0, 0);

                headProgressBar.setVisibility(View.VISIBLE);
                headArrowImageView.clearAnimation();
                headArrowImageView.setVisibility(View.GONE);
                headTipsTextview.setText("正在刷新...");
                headLastUpdatedTextView.setVisibility(View.VISIBLE);

                break;
            case DONE:
                headView.setPadding(0, -1 * headHight, 0, 0);

                headProgressBar.setVisibility(View.GONE);
                headArrowImageView.clearAnimation();
                headArrowImageView.setImageResource(R.drawable.goicon);
                headTipsTextview.setText("下拉刷新");
                headLastUpdatedTextView.setVisibility(View.VISIBLE);

                break;
            default:
                break;
        }
    }

    private void changeFooterViewByState()
    {
        switch (footState)
        {
            case DONE:
                footView.setPadding(0, 0, 0, -footHight);
                footArrowImageView.clearAnimation();
                footArrowImageView.setVisibility(View.VISIBLE);
                footProgressBar.setVisibility(View.GONE);
                footTipsTextview.setText("上拉加载更多");
                break;
            case PULL_TO_REFRESH:
                footArrowImageView.setVisibility(View.VISIBLE);
                footProgressBar.setVisibility(View.GONE);
                footTipsTextview.setText("上拉加载更多");
                if (isBack)
                {
                    isBack = false;
                    // Log("isBack=" + isBack);
                    footArrowImageView.clearAnimation();
                    footArrowImageView.startAnimation(reverseAnimation);
                }
                break;
            case RELEASE_TO_REFRESH:
                footArrowImageView.setVisibility(View.VISIBLE);
                footProgressBar.setVisibility(View.GONE);
                footArrowImageView.clearAnimation();
                footArrowImageView.startAnimation(animation);
                footTipsTextview.setText("松开加载更多");
                break;
            case REFRESHING:
                footArrowImageView.setVisibility(View.GONE);
                footProgressBar.setVisibility(VISIBLE);
                footView.setPadding(0, 0, 0, 0);
                footTipsTextview.setText("正在加载更多...");
                footArrowImageView.clearAnimation();
                break;
            case LOADING:
                break;
        }
    }

    /**
     * 
     * 方法描述：刷新完成后调用此方法
     * 
     * @param:
     * @return:
     * @version: 1.0
     * @author: 12100128
     * @version: 2012-12-27 下午06:56:59
     */
    public void onRefreshComplete()
    {

        onHeadRefreshComplete();
        onFooterRefreshComplete();
    }

    private void onHeadRefreshComplete()
    {
        headState = DONE;
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日  HH:mm");
        String date = format.format(new Date());
        headLastUpdatedTextView.setText("最近更新:" + date);
        changeHeaderViewByState();
    }

    private void onFooterRefreshComplete()
    {
        footState = DONE;
        isLastIndex = false;
        isFootVisible = false;
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日  HH:mm");
        String date = format.format(new Date());
        footLastUpdatedTextView.setText("最近更新:" + date);
        Log.e("", "selection=" + firstVisiableItem + " visibleItemCount="
                + visibleItemCount + " getCount()" + getCount());
        changeFooterViewByState();

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
            int visibleItemCount, int totalItemCount)
    {
        // LogX.e(TAG, "onScroll() enter : totalItemCount" + totalItemCount
        // + " firstVisibleItem:" + firstVisibleItem);
        firstVisiableItem = firstVisibleItem;
//  	    LogUtil.d("wl", "下拉刷新aaaaaaaaaaaaaaa："+firstVisiableItem);
        if (firstVisibleItem == 0)
        {
            isFirstIndex = true;
            isLastIndex = false;
        }
        if (firstVisibleItem + visibleItemCount >= totalItemCount - 1)
        {
            isFootVisible = true;
            isLastIndex = true;
        }
        else
        {
            isFootVisible = false;
            isLastIndex = false;
        }
        this.visibleItemCount = visibleItemCount;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState)
    {
        // LogX.e(TAG, "onScrollStateChanged() enter : scrollState" +
        // scrollState);
//  	    LogUtil.d("wl", "下拉刷新bbbbbbbbbbbbbbb："+firstVisiableItem);
        if (firstVisiableItem > 0)
        {
            isFirstIndex = false;
        }
        if (view.getLastVisiblePosition() == view.getCount() - 1)
        {
            isLastIndex = true;
            isFirstIndex = false;
        }
        // LogX.e(TAG, "view.getLastVisiblePosition(): " +
        // view.getLastVisiblePosition());
        // LogX.e(TAG, "view.getCount(): " + view.getCount());
        // LogX.e(TAG, "isLastIndex: " + isLastIndex);
        // LogX.e(TAG, "isFirstIndex: " + isFirstIndex);
    }

    /**
     * @param mPerNum
     *            每页包含的条数
     * @param mTotalPageNum
     *            总页数
     * @Description:设置是否到达最后一页,需在设置好Adapter后调用
     * @Author 12100128
     * @Date 2013-1-14
     */
    public void setIsLastPage(int mPerNum, int mTotalPageNum)
    {
        int itemSize = getCount() - 2;
        isHaveMorePage = mTotalPageNum > 0
                && (itemSize / mPerNum + itemSize % mPerNum) < mTotalPageNum ? true
                : false;
        // System.out.println("isHaveMorePage=" + isHaveMorePage
        // + " setIsLastPage getCount()=" + getCount() + " mTotalPageNum="
        // + mTotalPageNum);
    }

    /**
     * @param mTotalCount
     *            总条数
     * @Description:设置是否到达最后一页,需在设置好Adapter后调用
     * @Author 12100128
     * @Date 2013-1-14
     */
    public void setIsLastPage(int mTotalCount)
    {
        int itemSize = getCount() - 2;
        isHaveMorePage = itemSize < mTotalCount ? true : false;
    }

    /**
     * @param isHaveMorePage true 已到最后一页; false 可以继续上拉刷新
     * @Description:设置是否到达最后一页,需在设置好Adapter后调用
     * @Author 12100128
     * @Date 2013-1-14
     */
    public void setIsLastPage(boolean isHaveMorePage)
    {
        this.isHaveMorePage = !isHaveMorePage;
    }

    @Override
    public void setAdapter(ListAdapter adapter)
    {
        super.setAdapter(adapter);
    }

    /**
     * @param isNeedHeadRefresh
     *            头部是否需要下拉刷新，默认为true
     * @Description:
     * @Author 12100128
     * @Date 2013-3-19
     */
    public void setIsNeedHeadRefresh(boolean isNeedHeadRefresh)
    {
        this.isNeedHeadRefresh = isNeedHeadRefresh;
    }

    /**
     * 
     * 〈一句话功能简述〉<br>
     * 〈功能详细描述〉上拉下拉刷新监听事件
     * 
     * @author 12100128
     */
    public interface OnPullRefreshListener
    {
        public abstract void onHeadRefresh();

        public abstract void onFootRefresh();

        public abstract void onMoveRefresh();
    }
    
    public interface OnSearchViewListener
    {
        public abstract void onSearchViewShow();
        public abstract void onSearchViewHide();
    }

}
