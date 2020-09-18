# CoffeeBitmap
Shows in one simple way how to construct bitmaps using 3 techniques.
In theory, you can write every language glyph Unicode that you want, but it depends on the font.

# Great benefits
Most of modern thermal printer can quickly print good quality images. So we can easily build 
more complex layouts with all benefits of HTML/CSS offering better solution architecture.
 

## Graphics2D
Is the simplest of the 3 samples - indicate to make small graphics, print one or two lines on printer or draw some shape.

## jEditorPane
Has simplicity but is richier than Graphics2D and you can work with html 

## FlyingSaucer
Third party html renderer that offer better renderer to work with css.
This library can be found at [https://github.com/flyingsaucerproject/flyingsaucer](https://github.com/flyingsaucerproject/flyingsaucer)
 and have LGPL licence. See [ThirdParty.md](https://github.com/anastaciocintra/escpos-coffee-samples/blob/master/miscellaneous/CoffeeBitmap/ThirdParty.md)
 
 ### maven configure for Flying Saucer
```
        <dependency>
            <groupId>org.xhtmlrenderer</groupId>
            <artifactId>flying-saucer-core</artifactId>
            <version>9.1.20</version>
        </dependency>
```


to run
```
mvn clean package
java -jar target\coffee-bitmap-4.0.1-jar-with-dependencies.jar
```
 
 
 ## Troubleshoot 
 In case of problem on printing the images, you can try to change the image wrapper, 
 image wrapper have 3 options and you can test between these 3 wich is better for you.

```
 BitImageWrapper imageWrapper = new BitImageWrapper();
 RasterBitImageWrapper imageWrapper = new RasterBitImageWrapper();
 GraphicsImageWrapper imageWrapper = new GraphicsImageWrapper();
 
 new ImageHelper().write(escpos, new CoffeeImageImpl(image),imageWrapper,new BitonalOrderedDither());
```
