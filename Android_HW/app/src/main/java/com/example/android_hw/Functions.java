package com.example.android_hw;

import android.content.res.Resources;

public class Functions {

    public int getScreenHeight(){
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public int getScreenWidth(){
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public int getLegoHeight(GameActivity context){
        return context.getResources().getDrawable(R.drawable.blue_lego).getMinimumHeight();
    }

    public int getLegoWidth(GameActivity context){
        return context.getResources().getDrawable(R.drawable.player1).getMinimumWidth();
    }
}
