/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.Style;
import com.github.anastaciocintra.escpos.image.*;
import com.github.anastaciocintra.output.PrinterOutputStream;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.print.PrintService;

public class DitheringSample {
    public void GraphcsImageWrapper(String printerName){

        // get the printer service by name passed on command line...
        //this call is slow, try to use it only once and reuse the PrintService variable.
        PrintService printService = PrinterOutputStream.getPrintServiceByName(printerName);
        EscPos escpos;
        try {
            /*
             * to print one image we need to have:
             * - one BufferedImage.
             * - one bitonal algorithm to define what and how print on image.
             * - one image wrapper to determine the command set to be used on 
             * image printing and how to customize it.
             */
            
            // creating the EscPosImage, need buffered image and algorithm.
            BufferedImage  imageBufferedImage = SamplesCommon.getImage(SamplesCommon.sampleImages.dog);

            
            // this wrapper uses esc/pos sequence: "GS 'v' '0'"
            RasterBitImageWrapper imageWrapper = new RasterBitImageWrapper();

            
            
            escpos = new EscPos(new PrinterOutputStream(printService));
            
            Style title = new Style().setFontSize(Style.FontSize._2, Style.FontSize._2);
            escpos.writeLF(title
                    ,"Dithering BitonalThreshold");
            
            escpos.feed(5);
            escpos.writeLF("BitonalThreshold()");
            // using bitonal threshold for dithering
            Bitonal algorithm = new BitonalThreshold(); 
            EscPosImage escposImage = new EscPosImage(new CoffeeImageImpl(imageBufferedImage), algorithm);
            escpos.write(imageWrapper, escposImage);
            escpos.feed(5);

            escpos.writeLF("BitonalThreshold(60) (clearing)");
            // using bitonal threshold for dithering with threshold value 60 (clearing)
            algorithm = new BitonalThreshold(100); 
            escposImage = new EscPosImage(new CoffeeImageImpl(imageBufferedImage), algorithm);
            escpos.write(imageWrapper, escposImage);
            escpos.feed(5);

            escpos.writeLF("BitonalThreshold(150) (darkening)");
            // using bitonal threshold for dithering with threshold value 60 (darkening)
            algorithm = new BitonalThreshold(150); 
            escposImage = new EscPosImage(new CoffeeImageImpl(imageBufferedImage), algorithm);
            escpos.write(imageWrapper, escposImage);
            escpos.feed(5);

            escpos.cut(EscPos.CutMode.PART);
            
            escpos.writeLF(title
                    ,"Dithering");
            escpos.writeLF(title
                    ,"BitonalOrderedDither");
            
            escpos.feed(5);
            escpos.writeLF("BitonalOrderedDither()");
            // using ordered dither for dithering algorithm with default values
            algorithm = new BitonalOrderedDither();
            escposImage = new EscPosImage(new CoffeeImageImpl(imageBufferedImage), algorithm);
            escpos.write(imageWrapper, escposImage);
            escpos.feed(5);

            escpos.writeLF("BitonalOrderedDither (clearing) values");
            // using ordered dither for dithering algorithm with (clearing) values
            algorithm = new BitonalOrderedDither(2,2,60,100);
            escposImage = new EscPosImage(new CoffeeImageImpl(imageBufferedImage), algorithm);
            escpos.write(imageWrapper, escposImage);
            escpos.feed(5);

            escpos.writeLF("BitonalOrderedDither (darkening) values");
            // using ordered dither for dithering algorithm with (clearing) values
            algorithm = new BitonalOrderedDither(2,2,120,170);
            escposImage = new EscPosImage(new CoffeeImageImpl(imageBufferedImage), algorithm);
            escpos.write(imageWrapper, escposImage);
            escpos.feed(5);

            escpos.writeLF("BitonalOrderedDither  3x3 matrix");
            escpos.writeLF("quadruple sized to better see effects..");
            // using ordered dither for dithering algorithm with (clearing) values
            algorithm = new BitonalOrderedDither(3,3,100,130);
            escposImage = new EscPosImage(new CoffeeImageImpl(imageBufferedImage), algorithm);
            imageWrapper.setRasterBitImageMode(RasterBitImageWrapper.RasterBitImageMode.Quadruple);
            escpos.write(imageWrapper, escposImage);
            escpos.feed(5);
            
            escpos.writeLF("BitonalOrderedDither customized matrix");
            // create 3x3 matrix
            int[][] ditherMatrix = new int[][] 
            {
                {100,130,100},
                {130,  0,130},
                {100,130,100},
            };
            // create BitonalOrderedDither object with 3x3 matrix filled with zeroes.
            BitonalOrderedDither customAlgorithm = new BitonalOrderedDither(3,3);
            // set custom matrix to be used on dithering
            customAlgorithm.setDitherMatrix(ditherMatrix);
            escposImage = new EscPosImage(new CoffeeImageImpl(imageBufferedImage), customAlgorithm);
            // make image bigger to see better the effects.
            escpos.write(imageWrapper, escposImage);
            escpos.feed(5);
            
            
            
            
            escpos.feed(5);
            escpos.cut(EscPos.CutMode.FULL);
            
            
            escpos.close();
            
        } catch (IOException ex) {
            Logger.getLogger(DitheringSample.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java -jar xyz.jar (\"printer name\")");
            System.out.println("Printer list to use:");
            String[] printServicesNames = PrinterOutputStream.getListPrintServicesNames();
            for (String printServiceName : printServicesNames) {
                System.out.println(printServiceName);
            }

            System.exit(0);
        }
        DitheringSample obj = new DitheringSample();
        obj.GraphcsImageWrapper(args[0]);
        
    }
    
}
