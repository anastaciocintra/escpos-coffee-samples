import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.EscPosConst;
import com.github.anastaciocintra.escpos.image.BitonalThreshold;
import com.github.anastaciocintra.escpos.image.CoffeeImageImpl;
import com.github.anastaciocintra.escpos.image.GraphicsImageWrapper;
import com.github.anastaciocintra.output.PrinterOutputStream;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import lib.ImageHelper;

import javax.imageio.ImageIO;
import javax.print.PrintService;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * generate barcode image and print on  escpos_coffee
 */
public class BarcodeGen {

    public  void generateMulti(String text) throws Exception {
        BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.DATA_MATRIX, 200,200);

        BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
        File output = new File("C:\\Users\\macin\\desenv\\datamatrix.png");
        ImageIO.write(image, "png", output);

    }
    public void printzxing(EscPos escpos) throws IOException, WriterException {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

        // printing DataMatrix
        BufferedImage image = MatrixToImageWriter.toBufferedImage(
                multiFormatWriter.encode(
                        "Hello DataMatrix",
                        BarcodeFormat.DATA_MATRIX,
                        200,200));
        // for debug
        //File output = new File("C:\\Users\\macin\\desenv\\datamatrix.png");
        //ImageIO.write(image, "png", output);

        escpos.writeLF("zxing DataMatrix");
        new ImageHelper().write(escpos
                ,new CoffeeImageImpl(image)
                ,new GraphicsImageWrapper().setJustification(EscPosConst.Justification.Center)
                , new BitonalThreshold());
        escpos.feed(3);

        // printing qrcode
        image = MatrixToImageWriter.toBufferedImage(
                multiFormatWriter.encode(
                        "https://anastaciocintra.github.io/escpos-coffee/",
                        BarcodeFormat.QR_CODE,
                        200,200));
        // for debug
//        File output = new File("C:\\Users\\macin\\desenv\\qrcode.png");
//        ImageIO.write(image, "png", output);

        escpos.writeLF("zxing QrCode");
        new ImageHelper().write(escpos
                ,new CoffeeImageImpl(image)
                ,new GraphicsImageWrapper().setJustification(EscPosConst.Justification.Center)
                , new BitonalThreshold());
        escpos.feed(5).cut(EscPos.CutMode.FULL);

        escpos.close();

    }



    public static void main(String[] args) throws IOException, WriterException {
        if(args.length!=1){
            System.out.println("Usage: java -jar xyz.jar (\"printer name\")");
            System.out.println("Printer list to use:");
            String[] printServicesNames = PrinterOutputStream.getListPrintServicesNames();
            for(String printServiceName: printServicesNames){
                System.out.println(printServiceName);
            }

            System.exit(0);
        }
        PrintService printService = PrinterOutputStream.getPrintServiceByName(args[0]);
        EscPos escpos = new EscPos(new PrinterOutputStream(printService));
        BarcodeGen obj = new BarcodeGen();

        obj.printzxing(escpos);


    }

}
