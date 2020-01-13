/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.EscPosConst;
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

public class GraphicsImageSample {
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
            
             // specify the algorithm that defines what and how "print or not print" on each coordinate of the BufferedImage.
             // in this case, threshold 127
            Bitonal algorithm = new BitonalThreshold(127); 
            // creating the EscPosImage, need buffered image and algorithm.
            BufferedImage  githubBufferedImage = SamplesCommon.getImage(SamplesCommon.sampleImages.github);
            EscPosImage escposImage = new EscPosImage(new CoffeeImageImpl(githubBufferedImage), algorithm);
            
            // this wrapper uses esc/pos sequence: "GS(L"
            GraphicsImageWrapper imageWrapper = new GraphicsImageWrapper();

            
            
            escpos = new EscPos(new PrinterOutputStream(printService));
            
            escpos.writeLF(new Style().setFontSize(Style.FontSize._2, Style.FontSize._2)
                    ,"GraphicsImageWrapper");

            escpos.writeLF("default size");
            escpos.write(imageWrapper, escposImage);
            
            escpos.feed(5);
            escpos.writeLF("Double Height");
            imageWrapper.setGraphicsImageBxBy(GraphicsImageWrapper.GraphicsImageBxBy.DoubleHeight);
            escpos.write(imageWrapper, escposImage);

            escpos.feed(5);
            escpos.writeLF("Double Width");
            imageWrapper.setGraphicsImageBxBy(GraphicsImageWrapper.GraphicsImageBxBy.DoubleWidth);
            escpos.write(imageWrapper, escposImage);

            escpos.feed(5);
            escpos.writeLF("Quadruple size");
            imageWrapper.setGraphicsImageBxBy(GraphicsImageWrapper.GraphicsImageBxBy.Quadruple);
            escpos.write(imageWrapper, escposImage);

            escpos.feed(5);
            escpos.writeLF("print on Left");
            imageWrapper.setGraphicsImageBxBy(GraphicsImageWrapper.GraphicsImageBxBy.Normal_Default);
            imageWrapper.setJustification(EscPosConst.Justification.Left_Default);
            escpos.write(imageWrapper, escposImage);
            escpos.feed(5);
            escpos.writeLF("print on Right");
            imageWrapper.setJustification(EscPosConst.Justification.Right);
            escpos.write(imageWrapper, escposImage);
            escpos.feed(5);
            escpos.writeLF("print on Center");
            imageWrapper.setJustification(EscPosConst.Justification.Center);
            escpos.write(imageWrapper, escposImage);
            
            escpos.feed(5);
            escpos.cut(EscPos.CutMode.FULL);
            
            
            escpos.close();
            
        } catch (IOException ex) {
            Logger.getLogger(GraphicsImageSample.class.getName()).log(Level.SEVERE, null, ex);
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
        GraphicsImageSample obj = new GraphicsImageSample();
        obj.GraphcsImageWrapper(args[0]);
        
    }
    
}
