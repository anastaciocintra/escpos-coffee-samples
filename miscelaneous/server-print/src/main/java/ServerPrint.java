
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


        // don't need to specify a hostname, it will be the current machine
        ServerSocket ss = new ServerSocket(Integer.parseInt(args[1]));
        while(true){

            System.out.println("ServerSocket awaiting connections from " + Integer.parseInt(args[1]));
            Socket socket = ss.accept(); // blocking call, this will wait until a connection is attempted on this port.
            System.out.println("Connection from " + socket + "!");

            EscPos escpos = new EscPos(new PrinterOutputStream(printService));
            // get the input stream from the connected socket
            InputStream inputStream = socket.getInputStream();
            // create a DataInputStream so we can read data from it.
            // read the message from the socket
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
//        ss.close();
    }
}
