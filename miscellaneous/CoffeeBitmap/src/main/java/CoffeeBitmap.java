/*
MIT License

Copyright (c) 2020 Marco Antonio Anastacio Cintra <anastaciocintra@gmail.com>

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

import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.EscPosConst;
import com.github.anastaciocintra.escpos.Style;
import com.github.anastaciocintra.escpos.image.*;
import com.github.anastaciocintra.output.PrinterOutputStream;
import lib.ImageHelper;
import org.xhtmlrenderer.swing.Java2DRenderer;

import javax.imageio.ImageIO;
import javax.print.PrintService;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;

public class CoffeeBitmap {

    public void Graphics2D(PrinterOutputStream outputStream) throws IOException {
        EscPos escpos = new EscPos(outputStream);
        final int FontSize = 30;
        final String arabicText = "الصفحة الرئيسية";
        // 1 - create one buffered image with width and height
        BufferedImage image = new BufferedImage(576, 150, TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // change background and foregroud colors
        g.setColor(Color.white);
        g.fillRect(0, 0, g.getDeviceConfiguration().getBounds().width, g.getDeviceConfiguration().getBounds().height);
        g.setColor(Color.BLACK);

        // choose your font
        //OBS: not all fonts work well with Arabic language
        // read more on https://docs.oracle.com/javase/tutorial/2d/text/advanced.html
        //
        // choose the font... below you have some combinations
        Font fontMonoSpacePlan = new Font (Font.MONOSPACED, Font.PLAIN, FontSize);
        Font fontMonoSpaceBold = new Font (Font.MONOSPACED, Font.BOLD, FontSize);
        Font fontMonoSpaceBoldItalic = new Font (Font.MONOSPACED, Font.ITALIC|Font.BOLD, FontSize);
        Font fontSerifPlan = new Font (Font.SERIF, Font.PLAIN, FontSize);
        Font fontSerifBold = new Font (Font.SERIF, Font.BOLD, FontSize);
        Font fontSansSerif = new Font (Font.SANS_SERIF, Font.PLAIN, FontSize);
        Font fontSansSerifBold = new Font (Font.SANS_SERIF, Font.BOLD, FontSize);
        // doc about canDisplayUpTo
        /**
         * Indicates whether or not this <code>Font</code> can display a
         * specified <code>String</code>.  For strings with Unicode encoding,
         * it is important to know if a particular font can display the
         * string. This method returns an offset into the <code>String</code>
         * <code>str</code> which is the first character this
         * <code>Font</code> cannot display without using the missing glyph
         * code. If the <code>Font</code> can display all characters, -1 is
         * returned.
         * @param str a <code>String</code> object
         * @return an offset into <code>str</code> that points
         *          to the first character in <code>str</code> that this
         *          <code>Font</code> cannot display; or <code>-1</code> if
         *          this <code>Font</code> can display all characters in
         *          <code>str</code>.
         * @since 1.2
         */

        // ..
        // you can test the font
        if(fontMonoSpaceBold.canDisplayUpTo(arabicText) != -1){
            throw new CharConversionException("the font doesn't work with these glyphs");
        }

        // set the font to be used
        g.setFont(fontMonoSpaceBold);

        // write your text
        g.drawString("hello Arabian Character", 1, 90);

        // other line ...
        g.drawString("الصفحة الرئيسية", 1, 140);

        // send the graphic to the escpos printer...
        escpos.write(new GraphicsImageWrapper(),new EscPosImage(new CoffeeImageImpl(image),new BitonalThreshold()));

        // send the graphic to the escpos printer...
        new ImageHelper().write(escpos, new CoffeeImageImpl(image),new RasterBitImageWrapper(),new BitonalThreshold());

        escpos.feed(5).cut(EscPos.CutMode.FULL);
        escpos.close();

        // for debug purposes, you can save the graphic
        /* DEBUG
        File output = new File("C:\\Users\\macin\\desenv\\arabic.png");
        ImageIO.write(image, "png", output);
        DEBUG */

    }

    //
    // this peace of code was inspired reading questions on stackoverflow, thanks to johnchen902
    // https://stackoverflow.com/questions/17061682/java-html-rendering-engine?rq=1
    public void jEditorPane(PrinterOutputStream outputStream) throws IOException {
        EscPos escpos = new EscPos(outputStream);

        String html = "" +
                "<html>\n" +
                "<head>\n" +
                "<style>\n" +
                "body {\n" +
                "  background-color: lightblue;\n" +
                "}\n" +
                "\n" +
                "h1 {\n" +
                "  text-align: center;\n" +
                "}\n" +
                "\n" +
                "p {\n" +
                "  font-family: verdana;\n" +
                "  font-size: 20px;\n" +
                "}\n" +
                "p.korean {\n" +
                "  font-family: Single Day;\n" +
                "  font-size: 20px;\n" +
                "}\n" +
                "</style>\n" +
                "</head>" +
                "<body>" +
                "<h1>Hello, world.</h1>" +
                "<p>الصفحة الرئيسية \n" + // Arabiac
                "<br>你好，世界 \n" + // Chinese
                "<br>こんにちは世界 \n" + // Japanese
                "<br>Привет мир \n" + // Russian
                "<br>नमस्ते दुनिया \n" + //  Hindi
                "<p class=\"korean\"><br>안녕하세요 세계</p>" + // if necessary, you can download and install on your environment the Single Day from fonts.google...
                "</body>"
                ;

        int width = 576, height = 700;
        // Create a `BufferedImage` and create the its `Graphics`
        BufferedImage image = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice().getDefaultConfiguration()
                .createCompatibleImage(width, height);
        Graphics graphics = image.createGraphics();
        // Create an `JEditorPane` and invoke `print(Graphics)`

//        JEditorPane jep = new JEditorPane("text/html", html);
        JEditorPane jep = new JEditorPane();
        jep.setContentType("text/html");

        jep.setText(html);
        jep.setSize(width, height);
        jep.print(graphics);

        // send the graphic to the escpos printer...
        new ImageHelper().write(escpos, new CoffeeImageImpl(image),new RasterBitImageWrapper(),new BitonalThreshold());
        escpos.feed(5).cut(EscPos.CutMode.FULL);
        escpos.close();

        /* DEBUG, you can save the image */
        File output = new File("C:\\Users\\macin\\desenv\\html.png");
        ImageIO.write(image, "png", output);


    }


    // Flying Sauer is better html/css renderer,
    // but you can use others libs like javafx WebView
    public void FlyingSauer(PrinterOutputStream outputStream) throws IOException {
        EscPos escpos = new EscPos(outputStream);


        File temp = File.createTempFile("tmp_espos_sample", ".xhtml");
        temp.deleteOnExit();
        OutputStream tmpStreamxhtml = new FileOutputStream(temp);




        String html = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<style>\n" +
                "#customers {\n" +
                "  font-family: \"Trebuchet MS\", Arial, Helvetica, sans-serif;\n" +
                "  border-collapse: collapse;\n" +
                "  width: 100%;\n" +
                "}\n" +
                "\n" +
                "#customers td, #customers th {\n" +
                "  border: 1px solid #ddd;\n" +
                "  padding: 8px;\n" +
                "}\n" +
                "\n" +
                "#customers tr:nth-child(even){background-color: #616161;}\n" +
                "\n" +
                "\n" +
                "#customers th {\n" +
                "  padding-top: 12px;\n" +
                "  padding-bottom: 12px;\n" +
                "  text-align: left;\n" +
                "  background-color: #424242;\n" +
                "  color: white;\n" +
                "}\n" +
                "body {\n" +
                "  font-family: monospace;\n" +
                "  font-size: 25px;\n" +
                "}\n" +
                "</style>\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "<table id=\"customers\">\n" +
                "  <tr>\n" +
                "    <th>Company</th>\n" +
                "    <th>Contact</th>\n" +
                "    <th>Country</th>\n" +
                "  </tr>\n" +
                "  <tr>\n" +
                "    <td>Alfreds Futterkiste</td>\n" +
                "    <td>Maria Anders</td>\n" +
                "    <td>Germany</td>\n" +
                "  </tr>\n" +
                "  <tr>\n" +
                "    <td>Berglunds snabbköp</td>\n" +
                "    <td>Christina Berglund</td>\n" +
                "    <td>Sweden</td>\n" +
                "  </tr>\n" +
                "  <tr>\n" +
                "    <td>Centro comercial Moctezuma</td>\n" +
                "    <td>Francisco Chang</td>\n" +
                "    <td>Mexico</td>\n" +
                "  </tr>\n" +
                "  <tr>\n" +
                "    <td>Ernst Handel</td>\n" +
                "    <td>Roland Mendel</td>\n" +
                "    <td>Austria</td>\n" +
                "  </tr>\n" +
                "  <tr>\n" +
                "    <td>Island Trading</td>\n" +
                "    <td>Helen Bennett</td>\n" +
                "    <td>UK</td>\n" +
                "  </tr>\n" +
                "  <tr>\n" +
                "    <td>Königlich Essen</td>\n" +
                "    <td>Philip Cramer</td>\n" +
                "    <td>Germany</td>\n" +
                "  </tr>\n" +
                "  <tr>\n" +
                "    <td>Laughing Bacchus Winecellars</td>\n" +
                "    <td>Yoshi Tannamuri</td>\n" +
                "    <td>Canada</td>\n" +
                "  </tr>\n" +
                "  <tr>\n" +
                "    <td>Magazzini Alimentari Riuniti</td>\n" +
                "    <td>Giovanni Rovelli</td>\n" +
                "    <td>Italy</td>\n" +
                "  </tr>\n" +
                "  <tr>\n" +
                "    <td>North/South</td>\n" +
                "    <td>Simon Crowther</td>\n" +
                "    <td>UK</td>\n" +
                "  </tr>\n" +
                "  <tr>\n" +
                "    <td>Paris spécialités</td>\n" +
                "    <td>Marie Bertrand</td>\n" +
                "    <td>France</td>\n" +
                "  </tr>\n" +
                "</table>\n" +
                "\n" +
                "</body>\n" +
                "</html>";

        tmpStreamxhtml.write(html.getBytes("utf-8"));
        tmpStreamxhtml.close();
        String tmpFileURL = temp.toURI().toURL().toExternalForm();

        // if you want to include image or other external, include at tmpFileURL directory...
        String tmpBaseURL =  temp.getParent();
        Java2DRenderer render = new Java2DRenderer(tmpFileURL, tmpBaseURL, 576); // 576 is max printer area of the printer, you can configure to your
        BufferedImage image = render.getImage();

        escpos.getStyle()
                .setJustification(EscPosConst.Justification.Center)
                .setFontSize(Style.FontSize._2, Style.FontSize._2);
        escpos.writeLF("Flying Sauer\ncan understand\ncss 2.1 spec.\n");
        // send the graphic to the escpos printer...
        new ImageHelper().write(escpos, new CoffeeImageImpl(image),new RasterBitImageWrapper(),new BitonalOrderedDither());

        escpos.feed(5).cut(EscPos.CutMode.FULL);
        escpos.close();



//        /* DEBUG
//        File output = new File("C:\\Users\\macin\\desenv\\css.png");
//        ImageIO.write(image, "png", output);

//         */


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
        CoffeeBitmap coffeeBitmap = new CoffeeBitmap();
        coffeeBitmap.Graphics2D(new PrinterOutputStream(printService));
        coffeeBitmap.jEditorPane(new PrinterOutputStream(printService));
        coffeeBitmap.FlyingSauer(new PrinterOutputStream(printService));

    }
}