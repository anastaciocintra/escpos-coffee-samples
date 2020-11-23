package com.github.anastaciocintra.escposcoffeesamples.pdfprintingandroid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;
import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.image.Bitonal;
import com.github.anastaciocintra.escpos.image.BitonalThreshold;
import com.github.anastaciocintra.escpos.image.GraphicsImageWrapper;
import com.github.anastaciocintra.output.TcpIpOutputStream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


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

            // begin copy resource to a regular file ...
            try (InputStream is = context.getResources().openRawResource(R.raw.document);
                 FileOutputStream os = context.openFileOutput("document.pdf", context.MODE_PRIVATE)){

                byte[] buf = new byte[1024];
                while(true) {
                    int n = is.read(buf);
                    if( n < 0 ) break;
                    os.write(buf,0,n);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            // end copy resource to a regular file


            // steps from https://developer.android.com/reference/android/graphics/pdf/PdfRenderer
            // create a new renderer
            File file = new File(context.getFilesDir() + "/document.pdf");
                PdfRenderer renderer = new PdfRenderer(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY));


            // let us just render all pages
            final int pageCount = renderer.getPageCount();
            for (int i = 0; i < pageCount; i++) {
                PdfRenderer.Page page = renderer.openPage(i);

                // say we render for showing on the screen
                Bitmap mBitmap = Bitmap.createBitmap(page.getWidth(),page.getHeight(), Bitmap.Config.ARGB_8888);
                page.render(mBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_PRINT);


                // do stuff with the bitmap

                // send the image to printer
                ImageHelper helper = new ImageHelper();
                Bitonal algorithm = new BitonalThreshold();
                GraphicsImageWrapper imageWrapper = new GraphicsImageWrapper();
                helper.write(escpos, new CoffeeImageAndroidImpl(mBitmap),imageWrapper,algorithm);
                escpos.feed(5).cut(EscPos.CutMode.FULL);


                // close the page
                page.close();
            }

            // close the renderer
            renderer.close();




        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
