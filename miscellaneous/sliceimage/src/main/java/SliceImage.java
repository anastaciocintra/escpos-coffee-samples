import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.image.*;
import com.github.anastaciocintra.output.PrinterOutputStream;
import lib.ImageHelper;

import javax.imageio.ImageIO;
import javax.print.PrintService;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class SliceImage {
    public void run(PrinterOutputStream outputStream) throws IOException {
        EscPos escPos = new EscPos(outputStream);

        BufferedImage  image = SliceImage.getImage(sampleImages.big_image_576);

        ImageHelper helper = new ImageHelper();
        Bitonal algorithm = new BitonalThreshold();
        GraphicsImageWrapper imageWrapper = new GraphicsImageWrapper();
        helper.write(escPos, new CoffeeImageImpl(image),imageWrapper,algorithm);

        escPos.feed(5).cut(EscPos.CutMode.FULL);
        escPos.close();
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
        SliceImage sliceImage = new SliceImage();
        sliceImage.run(new PrinterOutputStream(printService));
    }

    public enum sampleImages {
        big_image_576("big-image_576.png")
        ;
        public String imageName;
        sampleImages(String imageName){
            this.imageName = imageName;
        }
    }


    public static BufferedImage getImage(sampleImages image) throws IOException {
        URL url = getURL(image.imageName);
        return ImageIO.read(url);

    }


    private static URL getURL(String imageName){
        String strPath =  "images/" +  imageName;
        return SliceImage.class
                .getClassLoader()
                .getResource(strPath);
    }

}
