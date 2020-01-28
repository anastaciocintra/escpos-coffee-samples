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

import com.github.anastaciocintra.escpos.EscPos;
import lib.SerialStatus;
import lib.PrinterStatusEvent;


import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class Principal {

    SerialStatus serialStatus;

    private JFrame frame;



    public void showWindow() throws IOException{

        serialStatus = new SerialStatusTMT20("com1");

        frame = new JFrame("Printer Coffee");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        WindowListener wls = new WindowListener(){
            @Override
            public void windowOpened(WindowEvent windowEvent) {

            }

            @Override
            public void windowClosing(WindowEvent windowEvent) {
                serialStatus.finish();
                frame.dispose();
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
                serialStatus.finish();
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


        serialStatus.addEventListener(new PrinterStatusEvent() {
            @Override
            public void onStatusChanged() {
                textArea.setText("");

                Map<Integer, SerialStatus.Status> mapErrors = serialStatus.getMapErrors();
                for (SerialStatus.Status error: mapErrors.values()) {
                    textArea.append(error.value + "\n");
                }
                Map<Integer, SerialStatus.Status> mapInfo = serialStatus.getMapInfo();
                for (SerialStatus.Status info: mapInfo.values()) {
                    textArea.append(info.value + "\n");
                }

            }
        });
        // just change color
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


        frame.pack();
        frame.setMinimumSize(frame.getPreferredSize());
        frame.setVisible(true);

    }


    private void print() throws IOException {
        OutputStream outputStream = serialStatus.getOutputStream();
        EscPos escPos = new EscPos(outputStream);
        escPos.info();

        escPos.close();
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Principal principal = new Principal();
                try {
                    principal.showWindow();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

    }
}
