# escpos-coffee-samples

Samples projects to use escpos-coffee library
https://github.com/anastaciocintra/escpos-coffee

![GitHub](https://img.shields.io/github/license/anastaciocintra/escpos-coffee-samples)



## Running Samples 
### tip: build all together as below, otherwise you can get compilation errors. 

```shell script
cd usual
mvn clean package
java -jar [samplename]/target/[samplename]-jar-with-dependencies.jar 
# without argument to list the priter names
java -jar [samplename]/target/[samplename]-jar-with-dependencies.jar "printer name"
```

## Usual Directory
Have samples for most common functions of the escpos-coffee lib, for example, 
how to print an image or how to print a barcode. It is enough to make a good receipt print.
* [barcode](https://github.com/anastaciocintra/escpos-coffee-samples/tree/master/usual/barcode) - how to print bar-codes
* [charcode](https://github.com/anastaciocintra/escpos-coffee-samples/tree/master/usual/charcode) - how to print  other languages Strings
* [getstart](https://github.com/anastaciocintra/escpos-coffee-samples/tree/master/usual/getstart) - print escpos-coffee version 
* Image Printing - some samples of how to print an image.
    * [bitimage](https://github.com/anastaciocintra/escpos-coffee-samples/tree/master/usual/bitimage) - uses esc/pos sequence: "ESC '*'
    * [dithering](https://github.com/anastaciocintra/escpos-coffee-samples/tree/master/usual/dithering) - uses dithering algorithm to make image more "realistic"
    * [graphics-image](https://github.com/anastaciocintra/escpos-coffee-samples/tree/master/usual/graphics-image) - uses esc/pos sequence: "GS(L"
    * [raster-image](https://github.com/anastaciocintra/escpos-coffee-samples/tree/master/usual/raster-image) - uses esc/pos sequence: "GS 'v' '0'"
* [tcpip-stream](https://github.com/anastaciocintra/escpos-coffee-samples/tree/master/usual/tcpip-stream) - how to print on ethernet printer
* Text Style - how to style the text to print
    * [textstyle](https://github.com/anastaciocintra/escpos-coffee-samples/tree/master/usual/textstyle)
    * [textprintmodestyle](https://github.com/anastaciocintra/escpos-coffee-samples/tree/master/usual/textprintmodestyle)

## Miscellaneous Directory
More specific usages of the escpos-coffee:
* [AndroidImage](https://github.com/anastaciocintra/escpos-coffee-samples/tree/master/miscellaneous/AndroidImage) - (android only) How to print an image with Android Studio
* [SerialStream](https://github.com/anastaciocintra/escpos-coffee-samples/tree/master/miscellaneous/SerialStream) - (desktop only) How to use Serial Port   (com1, com2...) Stream
* [SerialStatus](https://github.com/anastaciocintra/escpos-coffee-samples/tree/master/miscellaneous/SerialStatus) - (desktop only) How to get online status of the printer using Serial port I/O
* [Usb4JavaStream](https://github.com/anastaciocintra/escpos-coffee-samples/tree/master/miscellaneous/Usb4JavaStream) - (desktop only) How to use a usb port I/O 
* [UsbStatus](https://github.com/anastaciocintra/escpos-coffee-samples/tree/master/miscellaneous/UsbStatus) - (desktop only) How to get on line status of the printer using usb port I/O 
* [SliceImage](https://github.com/anastaciocintra/escpos-coffee-samples/tree/master/miscellaneous/sliceimage)  - (android/desktop) How to print big images (beta version to be incorporated on escpos-coffee library)
* [CoffeeBitmap](https://github.com/anastaciocintra/escpos-coffee-samples/tree/master/miscellaneous/CoffeeBitmap) -   (desktop only) How to construct Graphics2D, jEditorPane and html/css receipts
* [PdfPrinting](https://github.com/anastaciocintra/escpos-coffee-samples/tree/master/miscellaneous/PdfPrinting) -  (desktop only) How to print pdf content
* [PdfPrintingAndroid](https://github.com/anastaciocintra/escpos-coffee-samples/tree/master/miscellaneous/pdfprintingandroid)  - (android only) How to print pdf content on Android
* [BarcodeGen](https://github.com/anastaciocintra/escpos-coffee-samples/tree/master/miscellaneous/BarcodeGen) - How to generate barcode image




## Versioning

The version of this project follow the versions of escpos-coffee library and each version have its own branch:
For example, the version 4.0.1 can be accessed on branch 4.0.1

The branch master have always the last stable version.


## Contributting 
Contributors are welcome, 
but before you do it its important to read and agree with [CODE_OF_CONDUCT.md](https://github.com/anastaciocintra/escpos-coffee-samples/blob/master/CODE_OF_CONDUCT.md) and [CONTRIBUTING.md](https://github.com/anastaciocintra/escpos-coffee-samples/blob/master/CONTRIBUTING.md).

