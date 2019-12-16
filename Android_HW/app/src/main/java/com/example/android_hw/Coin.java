package com.example.android_hw;

public class Coin extends Lego {
    public Coin(GameActivity context) {
        super(context);
    }

    @Override
    protected void setColor() {
        imageID = R.drawable.coin;
        this.setImageResource(imageID);
    }
}
