
import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.image.BitonalOrderedDither;
import com.github.anastaciocintra.escpos.image.CoffeeImageImpl;
import com.github.anastaciocintra.escpos.image.RasterBitImageWrapper;
import com.github.anastaciocintra.output.PrinterOutputStream;
import lib.ImageHelper;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.print.PrintService;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * make pdf rendering and send to escpos_coffee
 */
public class PdfPrinting {

    public void printPdf(PrinterOutputStream outputStream) throws IOException {
        try (EscPos escpos = new EscPos(outputStream)){


            PDDocument document = PDDocument.load(getURL("Document.pdf"));
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            int numberOfPages = document.getNumberOfPages();
            System.out.println("Total files to be converting -> "+ numberOfPages);
            //
            // each page of pdf is rendered in one image
            for (int i = 0; i < numberOfPages; ++i) {
                BufferedImage image = pdfRenderer.renderImage(i);
//        /* DEBUG
//        File output = new File("C:\\Users\\macin\\desenv\\pdf_pag_x.png");
//        ImageIO.write(image, "png", output);

//         */

                // print the rendered image...
                new ImageHelper().write(escpos, new CoffeeImageImpl(image),new RasterBitImageWrapper(),new BitonalOrderedDither());
                escpos.feed(3).cut(EscPos.CutMode.PART);


            }
//            escpos.feed(3).cut(EscPos.CutMode.FULL);
            document.close();
        }

    }

    // get file from resource file
    private  InputStream getURL(String fileName) {

        String strPath = "docs/" + fileName;
        return getClass()
                .getClassLoader()
                .getResourceAsStream(strPath);
    }


    public static void main(String[] args) throws IOException {
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
        PdfPrinting printing = new PdfPrinting();
        printing.printPdf(new PrinterOutputStream(printService));


    }

}
