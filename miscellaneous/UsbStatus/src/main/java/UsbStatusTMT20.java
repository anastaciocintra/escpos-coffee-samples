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

import lib.UsbStatus;

import javax.usb.UsbException;
import java.io.IOException;

/**
 * Implementation of UsbStatus specific to TM-T20 Epson usb printer
 */
public class UsbStatusTMT20 extends UsbStatus {
    public UsbStatusTMT20(short vendorId, short productId, byte interfaceNumber, byte endpointAddressOut, byte endpointAddressIn) throws IOException, UsbException {
        super(vendorId, productId, interfaceNumber, endpointAddressOut, endpointAddressIn);
    }

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
}
