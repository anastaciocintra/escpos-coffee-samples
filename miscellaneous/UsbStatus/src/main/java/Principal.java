import com.github.anastaciocintra.escpos.EscPos;
import lib.UsbPrinterEvent;
import lib.UsbStatus;

import javax.swing.*;
import javax.usb.UsbException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class Principal {

    UsbStatus usbStatus;

    private JFrame frame;



    public void showWindow() throws IOException, UsbException {
        usbStatus = new UsbStatusTMT20((short) 0x04b8,(short)0x0e03, (byte) 0x00, (byte) 0x01, (byte) 0x82);

        frame = new JFrame("Printer Coffee");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        WindowListener wls = new WindowListener(){
            @Override
            public void windowOpened(WindowEvent windowEvent) {

            }

            @Override
            public void windowClosing(WindowEvent windowEvent) {
                try {
                    usbStatus.finish();
                    frame.dispose();
                } catch (UsbException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void windowClosed(WindowEvent windowEvent) {

            }

            @Override
            public void windowIconified(WindowEvent windowEvent) {

            }

            @Override
            public void windowDeiconified(WindowEvent windowEvent) {

            }

            @Override
            public void windowActivated(WindowEvent windowEvent) {

            }

            @Override
            public void windowDeactivated(WindowEvent windowEvent) {

            }
        };
        frame.addWindowListener(wls);

        frame.setLayout(new FlowLayout());

        JButton buttonPrint = new JButton("Print");
        buttonPrint.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    print();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        JButton buttonFinish = new JButton("Finish");
        buttonFinish.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    usbStatus.finish();
                } catch (UsbException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });



        frame.add(buttonPrint);
        frame.add(buttonFinish);

        JLabel labelStatus = new JLabel("Status");
        labelStatus.setOpaque(true);
        frame.add(labelStatus);

        JTextArea textArea = new JTextArea();
        textArea.setColumns(25);
        textArea.setRows(7);
        frame.add(textArea);


        usbStatus.addEventListener(new UsbPrinterEvent() {
            @Override
            public void onStatusChanged() {
                textArea.setText("");

                Map<Integer, UsbStatus.Status> mapErrors = usbStatus.getMapErrors();
                for (UsbStatus.Status error: mapErrors.values()) {
                    textArea.append(error.value + "\n");
                }
                Map<Integer, UsbStatus.Status> mapInfo = usbStatus.getMapInfo();
                for (UsbStatus.Status info: mapInfo.values()) {
                    textArea.append(info.value + "\n");
                }

            }
        });
        // just change color
        usbStatus.addEventListener(new UsbPrinterEvent() {
            @Override
            public void onStatusChanged() {
                if(usbStatus.haveAnyError()){
                    labelStatus.setBackground(Color.red);
                }else{
                    labelStatus.setBackground(Color.green);
                }
            }
        });


        frame.pack();
        frame.setMinimumSize(frame.getPreferredSize());
        frame.setVisible(true);
    }


    private void print() throws IOException {
        OutputStream outputStream = usbStatus.getUsbStream();
        EscPos escPos = new EscPos(outputStream);
        escPos.info();

        escPos.close();
    }

    public static void main(String[] args) throws IOException, UsbException, InterruptedException {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Principal principal = new Principal();
                try {
                    principal.showWindow();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (UsbException e) {
                    e.printStackTrace();
                }

            }
        });

    }
}
