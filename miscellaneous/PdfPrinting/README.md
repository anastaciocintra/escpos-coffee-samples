# PdfPrinting
Shows how to print pdf documents on escpos-coffee

## Third Party library
This project use PDFBox library, it can be used on comercial applications.
This library can be found at [https://github.com/apache/pdfbox](https://github.com/apache/pdfbox). 

Read about license on [ThirdParty.txt](https://github.com/anastaciocintra/escpos-coffee-samples/blob/master/miscellaneous/PdfPrinting/ThirdParty.txt)
 
 ### maven configure 
```
<dependency>
    <groupId>org.apache.pdfbox</groupId>
    <artifactId>pdfbox</artifactId>
    <version>2.0.19</version>
</dependency>
```


to run
```
mvn clean package
java -jar target\PdfPrinting-4.0.1-jar-with-dependencies.jar
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
