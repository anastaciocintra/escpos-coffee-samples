# AndroidImage

This project show how to use escpos-coffee library on Android Studio and
how to use CoffeeImageAndroidImpl class

## Points of attention:
1. **Min api level 19 (KitKat) Android 4.4**
1. the class com.github.anastaciocintra.output.PrinterOutputStream
   isn't compatible with android sdk. You need to use:
    1. com.github.anastaciocintra.output.TcpIpOutputStream. 
    Used on this sample code.
    1. another printer outputstream for android sdk.
1. About ip address and port
    1. getting ip address and port to use
        1. if you have one Ethernet / wifi printer, then you 
    need to discover the ip address and port of the printer. 
        1. if you have only a local printer. Then you need to run one auxiliary program 
        [server-print](../server-print)
    1. configure res.values.strings.xml with the values obtained on step before.
        1. host value
        1. port value 

## If you want to write your own android app intending for  to copy important pieces of code, follow some tips bellow:  
Configure dependencies an compatibility on app build.gradle
```
dependencies {
    ...
    implementation 'com.github.anastaciocintra:escpos-coffee:4.1.0'

}

repositories {
    mavenCentral()

}

android {
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}

```     

If you want to work with escpos-coffee SNAPSHOTS (unstable[CAUTION]), add on app build.gradle:
```
repositories {
    mavenCentral()

    maven {
        url "https://oss.sonatype.org/content/repositories/snapshots"
    }
}
```

Configure permissions on manifests.AndroidManifest.xml
```xml
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

Copy java implementation of com.github.anastaciocintra.escpos.image.CoffeeImage for android
```java
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

*/


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

```