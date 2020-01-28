# Serial Port Printer Status


If you want to get status from the escpos printer, then take a look at this project.

It uses jSerialComm library: 


https://fazecast.github.io/jSerialComm/

https://github.com/Fazecast/jSerialComm






##Read this before use

For me, the printer itself have its status led indicator, and its enough.
The escpos-coffee have the PrinterOutputStream that works properly to print.

But if you really want to get online status of the printer, go ahead and have fun.

About my environment: windows 10 and Epson-TM-T20. 

I hope that this project can guide you on getting online status printer.


## Screenshots

![output](screenshots/frame1.png?raw=true "frame1")

![output](screenshots/frame2.png?raw=true "frame2")



##### 1. Configure your serialStatus instance
Create the instance of  serialStatus passing the param 
```java
        SerialStatusTMT20 serialStatus = new SerialStatusTMT20("com1");
```


##### 2. If you want, write your own implementation of SerialStatus
Its not difficult, but you need to get your printer documentation and find something like
"Enables or disables basic ASB" (Automatic Status Back) (GS a n) 

Based on your printer documentation, implement your processData method similar on SerialStatusTMT20.java

Even if your printer is the same of mine (TM-T20), you can customize your processData method for your needs.

Note that all information about status of the printer was produced by processData method. The base class read bytes, and
call this processData always that printer send data, 
but you need change the status at this method by calling addError or addInfo, then this function is the most important to understand.

```java 
    /**
     * The method is implemented accordingly of follow lines (documentation)
     * ASB status binary (x=0 or 1)
     * first byte
     * 0xx1 xx00
     * bit 2 = 1: Drawer kick-out connector pin 3: High
     * bit 2 = 0: Drawer kick-out connector pin 3: Low
     * bit 3 = 1: in Offline, 0: in Online
     * bit 5 = 1: Cover is open, 0: closed
     * bit 6 = 1: on feeding paper by switch, 0: not
     *
     * 2nd byte
     * 0xx0 x000
     * bit 3 = 1: Autocutter error, 0: not
     * bit 5 = 1: Unrecoverable error, 0: not
     * bit 6 = 1: Automatically recoverable error, 0: not
     *
     * 3rd byte
     * 0110 xx00
     * bit 2, 3 = 1: Paper end, 0: paper present
     *
     * 4th byte
     * 0110 1111
     *
     * @param data - bytes with status of the printer
     * @param size - size of the byte[] data.
     */
    @Override
    protected void processData(byte[] data, int size){
        // continue if # of bytes received is not multiple of 4
        if (((size % 4) != 0) || size < 4) {
            return;
        }

        int byte3 = data[size - 2];
        int byte2 = data[size - 3];
        int byte1 = data[size - 4];

        mapErrors.clear();
        mapInfo.clear();


        if (bitAndCompare(byte1, bit3)){
            addError(new Status(1,"Offline"));
        }else{
            addInfo(new Status(50, "Online"));
        }
        if (bitAndCompare(byte1, bit5)) addError(new Status(2,"Cover is open"));
        if (bitAndCompare(byte1, bit6)) addError(new Status(3,"Feeding paper by switch"));

        if (bitAndCompare(byte2, bit3)) addError(new Status(4,"Autocutter error"));
        if (bitAndCompare(byte2, bit5)) addError(new Status(5,"Unrecoverable error"));
        if (bitAndCompare(byte2, bit6)) addError(new Status(6,"Automatically recoverable error"));

        if (bitAndCompare(byte3, bit2) || bitAndCompare(byte3, bit3)) addError(new Status(7,"Paper end"));


    }
``` 


##### 3. Configure your events
You can work with getMapErrors(), getMapInfo(), isFinished() and haveAnyError()...

 

```java
        serialStatus.addEventListener(new PrinterStatusEvent() {
            @Override
            public void onStatusChanged() {
                if(serialStatus.haveAnyError()){
                    labelStatus.setBackground(Color.red);
                }else{
                    labelStatus.setBackground(Color.green);
                }
            }
        });

``` 

##### 4. Before the program exit, you need to call finish()
This is necessary to release resources
```java
    serialStatus.finish()
``` 

##### 5. Compile and run
```shell script
mvn clean package
java -jar target/SerialStatus-4.0.1-jar-with-dependencies.jar
```


