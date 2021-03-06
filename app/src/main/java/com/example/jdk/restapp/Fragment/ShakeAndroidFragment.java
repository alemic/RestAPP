package com.example.jdk.restapp.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jdk.restapp.Activity.ShowWebViewActivity;
import com.example.jdk.restapp.Adapter.RecyclerViewDataBindingAdapter;
import com.example.jdk.restapp.CustomizedWidget.ProgressSubscriber;
import com.example.jdk.restapp.HttpUtils.ReturnRetrofit;
import com.example.jdk.restapp.ModelData.Constant;
import com.example.jdk.restapp.ModelData.MyAndroidData;
import com.example.jdk.restapp.ModelData.entity.Base;
import com.example.jdk.restapp.ModelData.entity.URLTableData;
import com.example.jdk.restapp.R;
import com.example.jdk.restapp.Utils.MyDecoration;
import com.example.jdk.restapp.Utils.SnackBarUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by JDK on 2016/8/4.
 */
public class ShakeAndroidFragment extends BaseFragment{
    private List<Base> baseList;
    RecyclerViewDataBindingAdapter recyclerViewDataBindingAdapter;
    static Context mContext;
    public ShakeAndroidFragment() {
        super(R.layout.fragment_shake_android);
    }
    public static ShakeAndroidFragment newInstance(Context context){
        ShakeAndroidFragment androidFragment=new ShakeAndroidFragment();
        mContext = context;
        return androidFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        baseList=new ArrayList<>();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void getData(int page) {
       setProgressSubscribler(page);
    }
    public void setSubscriber(int page){
        if(page>1) {
            SnackBarUtils.makeLong(getActivity().getWindow().getDecorView(), getResources().getString(R.string.shake_loadmore_footer)).danger();
        }else{
            SnackBarUtils.makeLong(getActivity().getWindow().getDecorView(), getResources().getString(R.string.shake_refresh_header)).danger();
        }
    }
    public void setProgressSubscribler(int page){
        baseList=new ArrayList<>();
        ReturnRetrofit.getInstance().getMyGankApiRetrofit().getShakeAndroidData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ProgressSubscriber<MyAndroidData>(mContext) {

                    @Override
                    public void onNext(MyAndroidData myAndroidData) {
                        for (int i = 0; i < myAndroidData.results.size(); i++) {
                            baseList.add(myAndroidData.results.get(i));
                        }
                        //在这里设置这个标志位是为了加载后是直接在尾部加载数据，不会造成加载后整个数据又滑到了最开始。
                        if (isFirst()) {
                            setListWise(baseList,8);
                            if(baseList.size()==8) {
                                recyclerViewDataBindingAdapter = new RecyclerViewDataBindingAdapter(mContext, baseList, Constant.IsAndroid);
                                getMyRecyclerView().setAdapter(recyclerViewDataBindingAdapter);
                            }
                        }
                        recyclerViewDataBindingAdapter.setRecyclerViewItemOnClickListener(new RecyclerViewDataBindingAdapter.recyclerViewDataBindingItemOnClickListener() {
                            @Override
                            public void recyclerViewDataBindingItemOnClick(String url, String desc, String who, Date CreateAt, String type, int position) {
                                Intent intent = new Intent();
                                intent.setClass(mContext, ShowWebViewActivity.class);
                                URLTableData urlTableData = new URLTableData(url, who, desc, CreateAt);
                                urlTableData.setType(type);
                                urlTableData.setIsCollected(false);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("urlTableData", urlTableData);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        });
                        if(baseList.size()==8) {
                            recyclerViewDataBindingAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getMyRecyclerView().setLayoutManager(new LinearLayoutManager(mContext));
        getMyRecyclerView().addItemDecoration(new MyDecoration(getActivity(),MyDecoration.VERTICAL_LIST));
        getData(1);
        InitListener();
    }

}
