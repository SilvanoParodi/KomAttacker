package com.inandio.komattacker.progressDialog;

import android.content.Context;
import android.view.View;
/**
 * Created by parodi on 11/12/2015. Inspirated by https://github.com/d-max
 */

class AnimatedView extends View {

    private int target;

    public AnimatedView(Context context) {
        super(context);
    }

    public float getXFactor() {
        return getX() / target;
    }

    public void setXFactor(float xFactor) {
        setX(target * xFactor);
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public int getTarget() {
        return target;
    }
}