package com.github.anastaciocintra.escposcoffeesamples.androidimage;

import android.graphics.Bitmap;

import com.github.anastaciocintra.escpos.image.CoffeeImage;

public class CoffeeImageAndroidImpl implements CoffeeImage {
    private Bitmap bitmap;

    public CoffeeImageAndroidImpl(Bitmap bitmap) {
        this.bitmap = bitmap;
    }


    @Override
    public int getWidth() {
        return bitmap.getWidth();
    }

    @Override
    public int getHeight() {
        return bitmap.getHeight();
    }

    @Override
    public CoffeeImage getSubimage(int x, int y, int w, int h) {
        return new CoffeeImageAndroidImpl(bitmap.createBitmap(this.bitmap,x,y,w,h));
    }

    @Override
    public int getRGB(int x, int y) {
        return bitmap.getPixel(x, y);
    }
}
