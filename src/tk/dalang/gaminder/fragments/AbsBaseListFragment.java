
package tk.dalang.gaminder.fragments;

import tk.dalang.gaminder.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * WoPlus
 * <p>
 * </p>
 * 
 * @author <a href="http://weibo.com/halzhang">Hal</a>
 * @version Feb 26, 2013
 */
public abstract class AbsBaseListFragment extends Fragment implements OnItemClickListener,
        OnScrollListener, OnLastItemVisibleListener, OnRefreshListener2<ListView> {

    private ListView mListView;

    private PullToRefreshListView mPullToRefreshListView;

    private ListAdapter mAdapter;

    private View mEmptyView;
    /**
     * when activity is recycled by system, isFirstTimeStartFlag will be reset to default true,
     * when activity is recreated because a configuration change for example screen rotate, isFirstTimeStartFlag will stay false
     */
    private boolean isFirstTimeStartFlag = true;

    protected final int FIRST_TIME_START = 0; //when activity is first time start
    protected final int SCREEN_ROTATE = 1;    //when activity is destroyed and recreated because a configuration change, see setRetainInstance(boolean retain)
    protected final int ACTIVITY_DESTROY_AND_CREATE = 2;  //when activity is destroyed because memory is too low, recycled by android system

    protected int getCurrentState(Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            isFirstTimeStartFlag = false;
            return ACTIVITY_DESTROY_AND_CREATE;
        }


        if (!isFirstTimeStartFlag) {
            return SCREEN_ROTATE;
        }

        isFirstTimeStartFlag = false;
        return FIRST_TIME_START;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getContentViewId(), null);
        mPullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.pull_refresh_list);
        if (mPullToRefreshListView == null) {
            throw new RuntimeException("Your content must have a ListView whose id attribute is "
                    + "'R.id.pull_refresh_list'");
        }
        mPullToRefreshListView.setOnRefreshListener(this);
        mPullToRefreshListView.setOnLastItemVisibleListener(this);
//        mPullToRefreshListView.setMode(Mode.BOTH);
        mPullToRefreshListView.setMode(Mode.PULL_FROM_START);
        mListView = mPullToRefreshListView.getRefreshableView();
        mListView.setOnItemClickListener(this);
        mListView.setOnScrollListener(this);
        mEmptyView = view.findViewById(android.R.id.empty);
        // if(mEmptyView != null && mAdapter == null/*第一次初始化*/){
        // mPullToRefreshListView.setVisibility(View.INVISIBLE);
        // mEmptyView.setVisibility(View.VISIBLE);
        // }else{
        // mPullToRefreshListView.setVisibility(View.VISIBLE);
        // mEmptyView.setVisibility(View.INVISIBLE);
        // }
        mListView.setEmptyView(mEmptyView);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Set Adapter here！
    }

    public void setListAdapter(ListAdapter adapter) {
        mAdapter = adapter;
        mListView.setAdapter(adapter);
    }

    /**
     * hide loading view,show listview
     */
    protected void onDataFirstLoadComplete() {
        // if(mEmptyView != null){
        // mEmptyView.setVisibility(View.GONE);
        // }
        // if(mPullToRefreshListView != null){
        // mPullToRefreshListView.setVisibility(View.VISIBLE);
        // }
    }

    @Override
    public void onLastItemVisible() {
        onPullListLastItemVisible();
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        onPullDownListViewRefresh((PullToRefreshListView) refreshView);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        onPullUpListViewRefresh((PullToRefreshListView) refreshView);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        onListItemClick((ListView) parent, view, position, id);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mListView == view && mAdapter != null) {
            // mAdapter.searchAsyncImageViews(view, scrollState ==
            // SCROLL_STATE_IDLE);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
            int totalItemCount) {
        // do nothing
    }

    /**
     * Get the fragment's list view widget.
     */
    public ListView getListView() {
        return mListView;
    }

    /**
     * Get the fragment's pull refresh list view widget
     * 
     * @return
     */
    public PullToRefreshListView getPullToRefreshListView() {
        return mPullToRefreshListView;
    }

    /**
     * Get the ListAdapter associated with this fragment's ListView.
     */
    public ListAdapter getListAdapter() {
        return mAdapter;
    }

    protected void onPullListLastItemVisible() {

    }

    /**
     * 向上拖动刷新
     * 
     * @param refreshListView
     */
    protected void onPullUpListViewRefresh(PullToRefreshListView refreshListView) {

    }

    /**
     * 向下拖动刷新
     * 
     * @param refreshListView
     */
    protected void onPullDownListViewRefresh(PullToRefreshListView refreshListView) {

    }

    /**
     * This method will be called when an item in the list is selected.
     * Subclasses should override. Subclasses can call
     * getListView().getItemAtPosition(position) if they need to access the data
     * associated with the selected item.
     * 
     * @param l The ListView where the click happened
     * @param v The view that was clicked within the ListView
     * @param position The position of the view in the list
     * @param id The row id of the item that was clicked
     */
    protected void onListItemClick(ListView l, View v, int position, long id) {

    }

    /**
     * Get layout res for Fragment
     * 
     * @return Fragment Content View Id
     * @see #onCreateView(LayoutInflater, ViewGroup, Bundle)
     */
    public abstract int getContentViewId();

}
