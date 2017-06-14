package com.artifex.mupdfdemo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Author: HueYoung
 * E-mail: yangtaolue@xuechengjf.com
 * Date: 2016/12/19.
 * <p/>
 * Description : pdf文件解析
 * 使用方法：
 * （1）直接打开MainActivity
 * （2）通过Fragment添加
 */
public class PdfAnalyzeFragment extends Fragment {
    private ListView mListView;
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PARAM_FILE_PATH = "param1";

    // TODO: Rename and change types of parameters
    private String paramFilePath;
    PageScrollService pageScrollService = new PageScrollService();
    public PdfAnalyzeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param Parameter 1.
     * @return A new instance of fragment PdfAnalyzeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PdfAnalyzeFragment newInstance(String param) {
        PdfAnalyzeFragment fragment = new PdfAnalyzeFragment();
        Bundle args = new Bundle();
        args.putString(PARAM_FILE_PATH, param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            paramFilePath = getArguments().getString(PARAM_FILE_PATH);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mupdf, container, false);
        mListView = (ListView) view.findViewById(R.id.listview_main);
        pdfAnalyze(view);

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //停止滚动
                if (scrollState == SCROLL_STATE_IDLE) {
                    pageScrollService.onStop();
                   /* Animation an = AnimationUtils.loadAnimation(getActivity(), R.anim.index_out);
                    an.setFillAfter(true);*/
                } else if (scrollState == SCROLL_STATE_FLING) {
                    pageScrollService.onScroll();
                    /*Animation an = AnimationUtils.loadAnimation(getActivity(), R.anim.index_in);
                    an.setFillAfter(true);*/
                } else if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    pageScrollService.onScroll();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
        return view;
    }

    /**加载pdf文件*/
    private void pdfAnalyze(View view) {
        MuPDFCore mCore = null;

        try {
            mCore = new MuPDFCore(getContext(),paramFilePath);
        } catch (Exception e){
            e.printStackTrace();
        }

        if (mCore!=null&&mCore.countPages()==0){
            mCore = null;
        }
        if (null == mCore){
            Toast.makeText(getContext(),"文件已损坏，无法打开",Toast.LENGTH_LONG).show();
            return;
        }
        MuPDFPageAdapter mAdapter = new MuPDFPageAdapter(getContext(),mCore, pageScrollService);
        mListView.setAdapter(mAdapter);
    }
}
