package com.github.anastaciocintra.escposcoffeesamples.androidimage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.EscPosConst;
import com.github.anastaciocintra.escpos.Style;
import com.github.anastaciocintra.escpos.image.Bitonal;
import com.github.anastaciocintra.escpos.image.BitonalOrderedDither;
import com.github.anastaciocintra.escpos.image.EscPosImage;
import com.github.anastaciocintra.escpos.image.RasterBitImageWrapper;
import com.github.anastaciocintra.output.TcpIpOutputStream;

import java.io.IOException;



public class Print extends Thread {
    private Context context;

    public Print(Context context){
        this.context = context;
    }

    public void run(){
        String host = context.getString(R.string.host);
        int port = Integer.parseInt(context.getString(R.string.port));

        try(TcpIpOutputStream stream = new TcpIpOutputStream(host, port)){
            EscPos escpos = new EscPos(stream);
            Style title = new Style()
                    .setFontSize(Style.FontSize._3, Style.FontSize._3)
                    .setJustification(EscPosConst.Justification.Center);

            Style subtitle = new Style(escpos.getStyle())
                    .setBold(true)
                    .setUnderline(Style.Underline.OneDotThick);
            Style bold = new Style(escpos.getStyle())
                    .setBold(true);

            escpos.writeLF(title,"My Market")
                    .feed(3)
                    .write("Client: ")
                    .writeLF(subtitle, "John Doe")
                    .feed(3)
                    .writeLF("Cup of coffee                      $1.00")
                    .writeLF("Botle of water                     $0.50")
                    .writeLF("----------------------------------------")
                    .feed(2)
                    .writeLF(bold,
                            "TOTAL                              $1.50")
                    .writeLF("----------------------------------------")
                    .feed(8)
                    .cut(EscPos.CutMode.FULL);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.dog, options);
            RasterBitImageWrapper imageWrapper = new RasterBitImageWrapper();
            escpos.writeLF("BitonalOrderedDither()");
            // using ordered dither for dithering algorithm with default values
            Bitonal algorithm = new BitonalOrderedDither();
            EscPosImage escposImage = new EscPosImage(new CoffeeImageAndroidImpl(bitmap), algorithm);
            escpos.write(imageWrapper, escposImage);
            escpos.feed(5).cut(EscPos.CutMode.FULL);

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
