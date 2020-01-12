import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.output.TcpIpOutputStream;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TcpIpSample {
    public void printInfo(String host, int port){
        try(TcpIpOutputStream outputStream = new TcpIpOutputStream(host,port)) {
        EscPos escpos = new EscPos(outputStream);
        escpos.info();

        } catch (
        IOException ex) {
            Logger.getLogger(TcpIpSample.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void main(String[] args) {
        if(args.length!=2){
            System.out.println("Usage: java -jar tcpip-stream (\"ip\") (\"port\") ");

            System.exit(0);
        }
        TcpIpSample obj = new TcpIpSample();
        obj.printInfo(args[0], Integer.parseInt(args[1]));


    }

}
