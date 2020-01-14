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

package lib;

import javax.usb.*;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Usb4JavaStream extends PipedOutputStream {

    private final PipedInputStream pipedInputStream;
    private final Thread threadPrint;

    public Usb4JavaStream(short vendorId, short productId, byte interfaceNumber, byte endpointAddress) throws IOException {
        pipedInputStream = new PipedInputStream();
        super.connect(pipedInputStream);
        Thread.UncaughtExceptionHandler uncaughtException = (Thread t, Throwable e) -> {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage(),e);
        };


        Runnable runnablePrint = () -> {

            try  {
                UsbDevice device = findDevice(UsbHostManager.getUsbServices().getRootUsbHub(),vendorId,productId);
                if(device == null){
                    throw new RuntimeException("Device is not found");
                }

                UsbConfiguration configuration = device.getActiveUsbConfiguration();
                UsbInterface iface = configuration.getUsbInterface(interfaceNumber);
                if(iface == null){
                    throw new RuntimeException("configuration.getUsbInterface failed");

                }
                iface.claim(new UsbInterfacePolicy()
                {
                    @Override
                    public boolean forceClaim(UsbInterface usbInterface)
                    {
                        return true;
                    }
                });
                try
                {
                    UsbEndpoint endpoint = iface.getUsbEndpoint(endpointAddress);
                    UsbPipe pipe = endpoint.getUsbPipe();
                    pipe.open();
                    try
                    {
                        byte[] buf = new byte[1];
                        while(true) {
                            int n = pipedInputStream.read(buf);
                            if( n < 0 ) break;
                            int sent = pipe.syncSubmit(buf);
                        }
                    }
                    finally
                    {
                        pipe.close();
                    }
                }
                finally
                {
                    iface.release();
                }


            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

        };

        threadPrint = new Thread(runnablePrint);
        threadPrint.setUncaughtExceptionHandler(uncaughtException);
        threadPrint.start();




    }


    public UsbDevice findDevice(UsbHub hub, short vendorId, short productId)
    {
        for (UsbDevice device : (List<UsbDevice>) hub.getAttachedUsbDevices())
        {
            UsbDeviceDescriptor desc = device.getUsbDeviceDescriptor();
            if (desc.idVendor() == vendorId && desc.idProduct() == productId) return device;
            if (device.isUsbHub())
            {
                device = findDevice((UsbHub) device, vendorId, productId);
                if (device != null) return device;
            }
        }
        return null;
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

