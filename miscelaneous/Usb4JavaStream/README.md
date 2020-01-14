# Usb4JavaStream
This project is just to illustrate how to use Usb4Java with escpos-coffee library.

for more information, visit these sites:

http://usb4java.org/

http://github.com/usb4java/usb4java


##Read this before use
For general or usual use, I recommend the use of escpos-coffee.PrinterOutputStream, 
that runs properly on several platforms and access all the printers installed on your system.
Or you can use escpos-coffee.TcpIpOutputStream for printers with ethernet interface. 

The use of usb interface on java demand specific technical knowledge and is 
out of scope of escpos-coffee library. 

Because of that I'll not support issues from this project (Usb4JavaStream), 
and im my humble opinion you should use Usb4JavaStream only from studies purpose.

Why usb interface? 

I think that its a seed of how to read information from the printer, in other words, 
how to get online status information from the printer, it is because the Usb4Java have IN/OUT endpoints...
But for now, it isn't implemented here...  


## Using on Linux
##### 1. Discover  vendorId,  productId,  interfaceNumber and  endpointAddress

```shell script
lsusb -v
```

```
Bus 001 Device 005: ID 04b8:0e03 Seiko Epson Corp. TM-T20
Device Descriptor:
...
  idVendor           0x04b8 Seiko Epson Corp.
  idProduct          0x0e03 
  bcdDevice            1.00
  iManufacturer           1 EPSON
  iProduct                2 TM-T20
  iSerial                 3 xxxxx
  bNumConfigurations      1
  Configuration Descriptor:
    bLength                 9
    bDescriptorType         2
    wTotalLength       0x0020
    bNumInterfaces          1
    bConfigurationValue     1
    iConfiguration          0 
    bmAttributes         0xc0
      Self Powered
    MaxPower                2mA
    Interface Descriptor:
      bLength                 9
      bDescriptorType         4
      bInterfaceNumber        0
      bAlternateSetting       0
      bNumEndpoints           2
      bInterfaceClass         7 Printer
      bInterfaceSubClass      1 Printer
      bInterfaceProtocol      2 Bidirectional
      iInterface              0 
      Endpoint Descriptor:
        bLength                 7
        bDescriptorType         5
        bEndpointAddress     0x01  EP 1 OUT
        bmAttributes            2
          Transfer Type            Bulk
          Synch Type               None
          Usage Type               Data
        wMaxPacketSize     0x0040  1x 64 bytes
        bInterval               0
      Endpoint Descriptor:
        bLength                 7
        bDescriptorType         5
        bEndpointAddress     0x82  EP 2 IN
        bmAttributes            2
          Transfer Type            Bulk
          Synch Type               None
          Usage Type               Data
        wMaxPacketSize     0x0040  1x 64 bytes
        bInterval               0
```   

##### 2. Edit java file... 
Create the instance of  Usb4JavaStream passing the params 
```java
        Usb4JavaStream stream = new Usb4JavaStream((short) 0x04b8,(short)0x0e03, (byte) 0x00, (byte) 0x01);
```

##### 3. compile and run
```shell script
mvn clean package
java -jar  target/Usb4JavaStream-4.0.0-SNAPSHOT-jar-with-dependencies.jar
```
if you have permissions problems, then you can run with sudo...
```shell script
mvn clean package
sudo java -jar  target/Usb4JavaStream-4.0.0-SNAPSHOT-jar-with-dependencies.jar
```


