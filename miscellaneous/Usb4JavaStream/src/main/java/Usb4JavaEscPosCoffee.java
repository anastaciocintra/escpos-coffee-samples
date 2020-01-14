import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.image.*;
import lib.Usb4JavaStream;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class Usb4JavaEscPosCoffee {

    private URL getURL(String imageName){
        String strPath =  "images/" +  imageName;
        return getClass()
                .getClassLoader()
                .getResource(strPath);
    }
    public void fnc1() throws IOException {
        Usb4JavaStream stream = new Usb4JavaStream((short) 0x04b8,(short)0x0e03, (byte) 0x00, (byte) 0x01);
        EscPos escPos = new EscPos(stream);
        escPos.writeLF("Hello Usb4Java")
                .writeLF("Another Line")
                .feed(10);


        Bitonal algorithm = new BitonalOrderedDither();
        // creating the EscPosImage, need buffered image and algorithm.
        URL imageURL = getURL("dog.png");
        BufferedImage imageBufferedImage = ImageIO.read(imageURL);
        EscPosImage escposImage = new EscPosImage(new CoffeeImageImpl(imageBufferedImage), algorithm);
        RasterBitImageWrapper imageWrapper = new RasterBitImageWrapper();
        escPos.write(imageWrapper, escposImage);

        escPos.feed(10).cut(EscPos.CutMode.FULL);
        escPos.close();
    }

    public static void main(String[] args) throws IOException {
        Usb4JavaEscPosCoffee obj = new Usb4JavaEscPosCoffee();
        obj.fnc1();
    }
}
