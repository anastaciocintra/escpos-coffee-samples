# BarcodeGen
Show How to generate barcode image and print it.



## Third Party library
This project use zxing library that can be found at [https://github.com/zxing/zxing](https://github.com/zxing/zxing). 

License details on [ThirdParty.txt](https://github.com/anastaciocintra/escpos-coffee-samples/blob/master/miscellaneous/BarcodeGen/ThirdParty.txt)
 
 ### maven configure 
```
<dependency>
    <groupId>com.google.zxing</groupId>
    <artifactId>core</artifactId>
    <version>3.4.1</version>
</dependency>
<dependency>
    <groupId>com.google.zxing</groupId>
    <artifactId>javase</artifactId>
    <version>3.4.1</version>
</dependency>
```


to run
```
mvn clean package
java -jar target\BarcodeGen-4.0.1-jar-with-dependencies.jar
```

## usages
```java
// printing DataMatrix
BufferedImage image = MatrixToImageWriter.toBufferedImage(
        multiFormatWriter.encode(
                "Hello DataMatrix",
                BarcodeFormat.DATA_MATRIX,
                200,200));

escpos.writeLF("zxing DataMatrix");
new ImageHelper().write(escpos
        ,new CoffeeImageImpl(image)
        ,new GraphicsImageWrapper().setJustification(EscPosConst.Justification.Center)
        , new BitonalThreshold());
``` 
you can generate a bunch of barcodes:
```java
/**
 * Enumerates barcode formats known to this package. Please keep alphabetized.
 *
 * @author Sean Owen
 */
public enum BarcodeFormat {

  /** Aztec 2D barcode format. */
  AZTEC,

  /** CODABAR 1D format. */
  CODABAR,

  /** Code 39 1D format. */
  CODE_39,

  /** Code 93 1D format. */
  CODE_93,

  /** Code 128 1D format. */
  CODE_128,

  /** Data Matrix 2D barcode format. */
  DATA_MATRIX,

  /** EAN-8 1D format. */
  EAN_8,

  /** EAN-13 1D format. */
  EAN_13,

  /** ITF (Interleaved Two of Five) 1D format. */
  ITF,

  /** MaxiCode 2D barcode format. */
  MAXICODE,

  /** PDF417 format. */
  PDF_417,

  /** QR Code 2D barcode format. */
  QR_CODE,

  /** RSS 14 */
  RSS_14,

  /** RSS EXPANDED */
  RSS_EXPANDED,

  /** UPC-A 1D format. */
  UPC_A,

  /** UPC-E 1D format. */
  UPC_E,

  /** UPC/EAN extension format. Not a stand-alone format. */
  UPC_EAN_EXTENSION

}

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
