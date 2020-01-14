/*
MIT License

        Copyright (c) 2019-2020 Marco Antonio Anastacio Cintra <anastaciocintra@gmail.com>

        Permission is hereby granted, free of charge, to any person obtaining a copy
        of this software and associated documentation files (the "Software"), to deal
        in the Software without restriction, including without limitation the rights
        to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
        copies of the Software, and to permit persons to whom the Software is
        furnished to do so, subject to the following conditions:

        The above copyright notice and this permission notice shall be included in all
        copies or substantial portions of the Software.

        THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
        IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
        FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
        AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
        LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
        OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
        SOFTWARE.

        package com.github.anastaciocintra.escposcoffeesamples.androidimage;
*/

package com.github.anastaciocintra.escposcoffeesamples.androidimage;

import android.graphics.Bitmap;

import com.github.anastaciocintra.escpos.image.CoffeeImage;

/**
 * implements CoffeeImage using Java BufferedImage
 * @see CoffeeImage
 * @see Bitmap
 */
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
