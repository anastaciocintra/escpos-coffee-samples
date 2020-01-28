/*
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
package lib;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortPacketListener;


import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * SerialStatus - framework to get status of the Serial/Com thermal printer
 * this can be used with escpos-coffee lib
 */

public abstract class SerialStatus {

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
    private volatile boolean finished;
    private final SerialPort comPort;
    private volatile Object lock = new Object();



    private final List<PrinterStatusEvent> listPrinterStatusEvent;

    public final int GS = 29;
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
     * @param portDescriptor  object corresponding to the user-specified port descriptor
     * On Windows machines, this descriptor should be in the form of "COM[*]".<br>
     * On Linux machines, the descriptor will look similar to "/dev/tty[*]".
     * @see SerialPort#getCommPort(String)
     */
    public SerialStatus(String portDescriptor) throws IOException {
        comPort = SerialPort.getCommPort(portDescriptor);
        if(!comPort.openPort()){
            throw new IOException("Error on comPort.openPort call");
        }


        comPort.addDataListener(new SerialPortPacketListener() {
            @Override
            public int getPacketSize() {
                return 4;
            }

            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
            }

            @Override
            public void serialEvent(SerialPortEvent event) {
                byte[] newData = event.getReceivedData();
                processData(newData,newData.length);
                statusChanged();
            }
        });

        Executors.newScheduledThreadPool(1).schedule(new Runnable() {
            @Override
            public void run() {
                synchronized (lock) {
                    java.io.OutputStream out = comPort.getOutputStream();
                    try {
                        out.write(GS);
                        out.write('a');
                        out.write(255); /// 1111 1111
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }, 1, TimeUnit.SECONDS);




        mapErrors = new HashMap<>();
        mapInfo = new HashMap<>();
        listPrinterStatusEvent = new ArrayList<>();



        finished = false;





    }

    /**
     * Finish the SerialStatus class, release comPort resource
     * to be used on another program.
     * You should call this method before exit.
     * After finish is called, you cannot use anymore this instance.
     */

    public void finish()  {
        if(finished) return;
        finished = true;
        mapErrors.clear();
        mapInfo.clear();

        comPort.removeDataListener();
        comPort.closePort();

        addError(new Status(8,"The SerialStatus is finished"));
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
     * Provide one instance of  OutputStream of SerialStatus
     * @return one instance of outputstream
     * @throws IOException
     */

    public OutputStream getOutputStream() throws IOException {
        if(isFinished()){
            throw new IOException("The SerialStatus is finished");
        }
        if(haveAnyError()) {
            throw new IOException("The status of the printer is not ok");
        };
        return new OutputStream();
    }

    /**
     * Supply OutputStream to the serial port.
     * <p>
     * Send data directing to the printer. The instance cannot
     * be reused and the last command should be <code>close()</code>, after that,
     * you need to create another instance to send data to the printer.
     * @see #getOutputStream()
     */
    private class OutputStream extends PipedOutputStream {

        private final PipedInputStream pipedInputStream;
        private final Thread threadPrint;

        public OutputStream() throws IOException {
            pipedInputStream = new PipedInputStream();
            super.connect(pipedInputStream);
            Thread.UncaughtExceptionHandler uncaughtException = (Thread t, Throwable e) -> {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            };


            Runnable runnablePrint = () -> {
                synchronized (lock){
                    try {
                        java.io.OutputStream outputStream = comPort.getOutputStream();
                        try{
                            byte[] buf = new byte[1024];
                            while(true) {
                                int n = pipedInputStream.read(buf);
                                if( n < 0 ){
                                    outputStream.write(GS);
                                    outputStream.write('a');
                                    outputStream.write(255); /// 1111 1111
                                    break;

                                }
                                outputStream.write(buf,0,n);
                            }

                        }finally {
                            outputStream.close();
                        }
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }

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
     * Add printerStatusEventListener to list of events
     * triggered on status changed
     * @see #statusChanged()
     * @param printerStatusEventListener - implementation of PrinterStatusEvent
     */
    public void addEventListener(PrinterStatusEvent printerStatusEventListener){
        listPrinterStatusEvent.add(printerStatusEventListener);
    }

    /**
     * Call all the PrinterStatusEvent functions added on event listener.
     * This method is internally called whenever  the printer status changed
     * @see #addEventListener(PrinterStatusEvent)
     */
    private void statusChanged(){
        for (PrinterStatusEvent printerStatusEvent : listPrinterStatusEvent) {
            printerStatusEvent.onStatusChanged();
        }
    }
}
