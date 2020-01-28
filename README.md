# escpos-coffee-samples

Samples projects to use escpos-coffee library
https://github.com/anastaciocintra/escpos-coffee

![GitHub](https://img.shields.io/github/license/anastaciocintra/escpos-coffee-samples)



## Running Samples
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
* barcode - how to print bar-codes
* charcode - how to print  other languages Strings
* getstart - print escpos-coffee version 
* Image Printing - some samples of how to print an image.
    * bitimage - uses esc/pos sequence: "ESC '*'
    * dithering - uses dithering algorithm to make image more "realistic"
    * graphics-image - uses esc/pos sequence: "GS(L"
    * raster-image - uses esc/pos sequence: "GS 'v' '0'"
* tcpip-stream - how to print on ethernet printer
* Text Style - how to style the text to print
    * textstyle
    * textprintmodestyle

## Miscellaneous Directory
More specific usages of the escpos-coffee:
* AndroidImage - How to print an image with Android Studio
* SerialStream - How to use Serial Port   (com1, com2...) Stream
* SerialStatus - How to get online status of the printer using Serial port I/O
* Usb4JavaStream - How to use a usb port I/O 
* UsbStatus - How to get on line status of the printer using usb port I/O 



## Versioning

The version of this project follow the versions of escpos-coffee library and each version have its own branch:
For example, the version 4.0.1 can be accessed on branch 4.0.1

The branch master have always the last stable version.


## Contributting 
Contributors are welcome, 
but before you do it its important to read and agree with [CODE_OF_CONDUCT.md](https://github.com/anastaciocintra/escpos-coffee-samples/blob/master/CODE_OF_CONDUCT.md) and [CONTRIBUTING.md](https://github.com/anastaciocintra/escpos-coffee-samples/blob/master/CONTRIBUTING.md).

