package com.artifex.mupdfdemo;

import android.content.Context;
import android.widget.ImageView;

/**
 * Created by jcman on 16-6-22.
 */
public class OpaqueImageView extends ImageView {

    public OpaqueImageView(Context context){
        super(context);
        setBackgroundResource(R.drawable.bg_shadow);
        setPadding(8, 8, 8, 8);
    }
    @Override
    public boolean isOpaque() {
        return true;
    }
}
