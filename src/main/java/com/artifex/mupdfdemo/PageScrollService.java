package com.artifex.mupdfdemo;

import java.util.HashSet;

/**
 * Author: HueYoung
 * E-mail: yangtaolue@xuechengjf.com
 * Date: 2017/6/8
 * <p/>
 * Description : 滚动通知
 */
public class PageScrollService {
    HashSet<PageScrollListener> hash = new HashSet<>();

    public void setPageScrollListener(PageScrollListener pageScrollListener) {
        hash.add(pageScrollListener);
    }

    public void onScroll() {
        for (PageScrollListener pageScrollListener : hash) {
            pageScrollListener.onScroll();
        }
    }

    public void onStop() {
        for (PageScrollListener pageScrollListener : hash) {
            pageScrollListener.onStop();
        }
    }
}
