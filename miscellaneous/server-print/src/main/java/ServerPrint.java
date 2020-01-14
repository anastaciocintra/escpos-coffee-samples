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

import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.output.PrinterOutputStream;

import javax.print.PrintService;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerPrint {

    public static void main(String[] args) throws IOException {

        if(args.length!=2){
            System.out.println("Usage: java -jar xyz.jar (\"printer name\") (\"port\")");
            System.out.println("Printer list to use:");
            String[] printServicesNames = PrinterOutputStream.getListPrintServicesNames();
            for(String printServiceName: printServicesNames){
                System.out.println(printServiceName);
            }

            System.exit(0);
        }
        PrintService printService = PrinterOutputStream.getPrintServiceByName(args[0]);



        ServerSocket ss = new ServerSocket(Integer.parseInt(args[1]));
        while(true){

            System.out.println("ServerSocket awaiting connections from " + Integer.parseInt(args[1]));
            Socket socket = ss.accept();
            System.out.println("Connection from " + socket + "!");

            EscPos escpos = new EscPos(new PrinterOutputStream(printService));

            InputStream inputStream = socket.getInputStream();

            byte[] buf = new byte[4096];
            while(true) {
                int n = inputStream.read(buf);
                if( n < 0 ) break;
                escpos.write(buf,0,n);
            }
            escpos.close();


            System.out.println("Closing sockets.");
            socket.close();
        }
    }
}
