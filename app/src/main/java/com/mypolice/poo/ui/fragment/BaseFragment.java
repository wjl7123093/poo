package com.mypolice.poo.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mypolice.poo.application.PooApplication;
import com.mypolice.poo.util.filecache.DataLoader;

/**
 * Created by wangjl on 2017/8/30.
 */

public class BaseFragment extends Fragment {

    public PooApplication mApplication;
    public DataLoader mDataLoader;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mApplication = (PooApplication) getActivity().getApplication();
        mDataLoader = DataLoader.getInstance(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void initView() {

    }

    public void initData() {

    }

    public void loadData() {

    }
}
