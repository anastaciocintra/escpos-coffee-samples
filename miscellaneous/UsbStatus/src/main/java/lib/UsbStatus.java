package lib;/*
MIT License

Copyright (c) 2020 Marco Antonio Anastacio Cintra <anastaciocintra@gmail.com>.

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

import javax.usb.*;
import javax.usb.event.UsbPipeDataEvent;
import javax.usb.event.UsbPipeErrorEvent;
import javax.usb.event.UsbPipeListener;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.github.anastaciocintra.escpos.EscPosConst.GS;

/**
 * UsbStatus - framework to get status of the USB thermal printer
 * this can be used with escpos-coffee lib
 */

public abstract class UsbStatus {

    /**
     * Structure to store errors and info of the printer.
     * @see #addError(Status)
     * @see #addInfo(Status)
     * @see #getMapErrors()
     * @see #getMapInfo()
     */
    public class Status{
        public final int  key;
        public final String  value;
        public Status(int key, String value){
            this.key = key;
            this.value = value;
        }
    }




    protected final Map<Integer, Status> mapErrors;
    protected final Map<Integer, Status> mapInfo;
    private final byte endpointAddressOut;
    private final byte endpointAddressIn;
    private final UsbInterface iface;
    private volatile boolean finished;

    private volatile Object lock = new Object();


    private ExecutorService pool;


    private final List<UsbPrinterEvent> listUsbPrinterEvent;


    protected final int bit0 = 1;
    protected final  int bit1 = 2;
    protected final  int bit2 = 4;
    protected final  int bit3 = 8;
    protected final  int bit4 = 16;
    protected final  int bit5 = 32;
    protected final  int bit6 = 64;
    protected final  int bit7 = 128;

    /**
     * Test if the printer have any error
     * @return true if the printer have any error
     */
    public boolean haveAnyError() {
        return !mapErrors.isEmpty();
    }



    /**
     * Get the map of all errors returned by the printer.
     * @return Status map
     */
    public Map<Integer, Status> getMapErrors() {
        return mapErrors;
    }

    /**
     * Get the map of all info returned by the printer.
     * @return Status map
     */
    public Map<Integer, Status> getMapInfo() {
        return mapInfo;
    }


    /**
     * Add one error status, that is impeding to work
     * @param status can be, for example (paper is end)
     */
    protected void addError(Status status){
        mapErrors.put(status.key,status);
    }

    /**
     * used to add one info status
     * @param status can be, for example (printer is ok)
     * @see #processData(byte[], int)
     */
    protected void addInfo(Status status){
        mapInfo.put(status.key,status);
    }


    /**
     * Instantiate this class and init the "getting status" on separate thread.
     * It will work until you turn off your printer / disconnect or call the finish method
     *
     * @param vendorId
     * @param productId
     * @param interfaceNumber
     * @param endpointAddressOut
     * @param endpointAddressIn
     * @throws IOException
     * @throws UsbException
     */
    public UsbStatus(short vendorId, short productId, byte interfaceNumber, byte endpointAddressOut, byte endpointAddressIn) throws IOException, UsbException{

        this.endpointAddressOut = endpointAddressOut;
        this.endpointAddressIn = endpointAddressIn;
        mapErrors = new HashMap<>();
        mapInfo = new HashMap<>();
        listUsbPrinterEvent = new ArrayList<>();



        finished = false;

        UsbDevice device = findDevice(UsbHostManager.getUsbServices().getRootUsbHub(), vendorId, productId);
        if (device == null) {
            throw new RuntimeException("Device is not found");
        }

        UsbConfiguration configuration = device.getActiveUsbConfiguration();
        iface = configuration.getUsbInterface(interfaceNumber);
        if (iface == null) {
            throw new RuntimeException("configuration.getUsbInterface failed");

        }
        iface.claim(new UsbInterfacePolicy() {
            @Override
            public boolean forceClaim(UsbInterface usbInterface) {
                return true;
            }
        });



        UsbEndpoint endpointOut = iface.getUsbEndpoint(endpointAddressOut);
        UsbPipe pipeOut = endpointOut.getUsbPipe();
        pipeOut.open();



        try {

            byte[] data = new byte[1024];
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();


            outputStream.write(GS);
            outputStream.write('a');
            outputStream.write(255); /// 1111 1111

            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

            while (true) {
                int n = inputStream.read(data);
                if (n < 0) break;
                pipeOut.syncSubmit(data);
            }

        } finally {
            pipeOut.close();
        }

        Runnable runnableStatus = new Runnable() {
            @Override
            public void run() {
                byte[] data = new byte[1024];
                try {
                    UsbEndpoint endpointIn = iface.getUsbEndpoint(endpointAddressIn);
                    UsbPipe pipeIn = endpointIn.getUsbPipe();
                    pipeIn.open();

                    pipeIn.addUsbPipeListener(new UsbPipeListener()
                    {

                        @Override
                        public void errorEventOccurred(UsbPipeErrorEvent usbPipeErrorEvent) {

                        }

                        @Override
                        public void dataEventOccurred(UsbPipeDataEvent event)
                        {
                            synchronized (lock) {
                                byte[] dataAsync = event.getData();
                                processData(dataAsync,dataAsync.length);
                                if(dataAsync.length > 0) {
                                    statusChanged();
                                }
                            }
                        }
                    });


                    try {


                        while (!finished) {

                            Thread.sleep(150);
                            // read status
                            int received;
                            synchronized (lock){
                                received = pipeIn.syncSubmit(data);
                                processData(data,received);
                                if(received > 0) {
                                    statusChanged();
                                }

                            }

                        }

                    } finally {
                        pipeIn.close();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (UsbException e) {
                    e.printStackTrace();
                }

            }
        };

        pool = Executors.newFixedThreadPool(1);
        pool.execute(runnableStatus);


    }

    /**
     * Finish the UsbStatus class, release usb resource
     * to be used on another program.
     * You should call this method before exit.
     * After finish is called, you cannot use anymore this instance.
     * @throws UsbException
     * @throws InterruptedException
     */

    public void finish() throws UsbException, InterruptedException {
        if(finished) return;
        finished = true;
        // for thread finish
        pool.shutdown();
        pool.awaitTermination(3, TimeUnit.SECONDS);
        iface.release();
        mapErrors.clear();
        mapInfo.clear();
        addError(new Status(8,"The UsbStream is finished"));
        statusChanged();
    }


    public boolean isFinished(){
        return finished;
    }

    /**
     * This abstract method is called after bytes read from printer.
     * the objective of implementation is to populate the list of errors
     * according to bytes.
     * Each printer have its own documentation for this. Then you need
     * to implement yours.
     * You need to find on your printer documentation about
     * "Enables or disables basic ASB" (Automatic Status Back) (GS a n)
     *
     * @param data - bytes with status of the printer
     * @param size - size of the byte[] data.
     * @see #addInfo(Status)
     * @see #addError(Status)
     * @see #bitAndCompare(int, int)
     */
    protected abstract void  processData(byte[] data, int size);


    /**
     * Return true if bit set configured on mask is ok
     * @param n - bit set to be compared
     * @param toCompare - mask the bit compare
     * @return boolean
     */

    protected boolean bitAndCompare(int n, int toCompare) {
        return ((n & toCompare) == toCompare);
    }

    /**
     * Find Usb4java device
     * @param hub
     * @param vendorId can be get on lsusb command
     * @param productId can be get on lsusb command
     * @return Usb device used on Usb4Java lib
     */
    private UsbDevice findDevice(UsbHub hub, short vendorId, short productId) {
        for (UsbDevice device : (List<UsbDevice>) hub.getAttachedUsbDevices()) {
            UsbDeviceDescriptor desc = device.getUsbDeviceDescriptor();
            if (desc.idVendor() == vendorId && desc.idProduct() == productId) return device;
            if (device.isUsbHub()) {
                device = findDevice((UsbHub) device, vendorId, productId);
                if (device != null) return device;
            }
        }
        return null;
    }


    /**
     * Provide one instance of UsbStream
     * @return one instance of outputstream
     * @throws IOException
     */

    public UsbStream getUsbStream() throws IOException {
        if(isFinished()){
            throw new IOException("The UsbStream is finished");
        }
        if(haveAnyError()) {
            throw new IOException("The status of the printer is not ok");
        };
        return new UsbStream();
    }

    /**
     * Supply OutputStream to the Usb printer.
     * <p>
     * Send data directing to the printer. The instance cannot
     * be reused and the last command should be <code>close()</code>, after that,
     * you need to create another instance to send data to the printer.
     * @see #getUsbStream()
     */
    private class UsbStream extends PipedOutputStream {

        private final PipedInputStream pipedInputStream;
        private final Thread threadPrint;

        public UsbStream() throws IOException {
            pipedInputStream = new PipedInputStream();
            super.connect(pipedInputStream);
            Thread.UncaughtExceptionHandler uncaughtException = (Thread t, Throwable e) -> {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            };


            Runnable runnablePrint = () -> {

                try {
                    UsbEndpoint endpoint = iface.getUsbEndpoint(endpointAddressOut);
                    UsbPipe pipe = endpoint.getUsbPipe();
                    pipe.open();
                    try {
                        byte[] buf = new byte[1];
                        while (true) {
                            int n = pipedInputStream.read(buf);
                            if (n < 0){
                                // enable Enable/disable Automatic Status Back (ASB)
                                pipe.syncSubmit(new byte[]{GS});
                                pipe.syncSubmit(new byte[]{'a'});
                                pipe.syncSubmit(new byte[]{(byte)255});

                                break;
                            }
//                            if(haveAnyError()){
//                                throw new IOException("The status of the printer is not ok");
//                            }
                            pipe.syncSubmit(buf);
                        }
                    } finally {
                        pipe.close();
                    }

                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }

            };

            threadPrint = new Thread(runnablePrint);
            threadPrint.setUncaughtExceptionHandler(uncaughtException);
            threadPrint.start();
        }
        /**
         * Set UncaughtExceptionHandler to make special error treatment.
         * <p>
         * Make special treatment of errors on your code.
         *
         * @param uncaughtException used on (another thread) print.
         */
        public void setUncaughtException(Thread.UncaughtExceptionHandler uncaughtException) {
            threadPrint.setUncaughtExceptionHandler(uncaughtException);
        }
    }

    /**
     * Add usbPrinterEventListener to list of events
     * triggered on status changed
     * @see #statusChanged()
     * @param usbPrinterEventListener - implementation of UsbPrinterEvent
     */
    public void addEventListener(UsbPrinterEvent usbPrinterEventListener){
        listUsbPrinterEvent.add(usbPrinterEventListener);
    }

    /**
     * Call all the UsbPrinterEvent functions added on event listener.
     * This method is internally called whenever  the printer status changed
     * @see #addEventListener(UsbPrinterEvent)
     */
    private void statusChanged(){
        for (UsbPrinterEvent usbPrinterEvent : listUsbPrinterEvent) {
            usbPrinterEvent.onStatusChanged();
        }
    }
}
