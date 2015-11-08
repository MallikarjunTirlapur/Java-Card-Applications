/*
 * The MIT License
 *
 * Copyright 2015 Mallikarjun Tirlapur.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package machine;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import com.sun.javacard.apduio.*;

/**
 *
 * @author mallik
 */
public class JavaCardHostApp {

    private Socket sock;
    private OutputStream os;
    private InputStream is;
    private Apdu apdu;
    private CadClientInterface cad;

    public JavaCardHostApp() {
        apdu = new Apdu();
    }

    public void establishConnectionToSimulator() {
        try {

            sock = new Socket("localhost", 9025);
            os = sock.getOutputStream();
            is = sock.getInputStream();
            cad = CadDevice.getCadClientInstance(CadDevice.PROTOCOL_T1, is, os);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void closeConnection() {
        try {
            sock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pwrUp() {
        try {
            if (cad != null) {
                cad.powerUp();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pwrDown() {
        try {
            if (cad != null) {
                cad.powerDown();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTheAPDUCommands(byte[] cmnds) {
        if (cmnds.length > 4 || cmnds.length == 0) {
            System.err.println("inavlid commands");
        } else {
            apdu.command = cmnds;
            System.out.println("CLA: " + atrToHex(cmnds[0]));
            System.out.println("INS: " + atrToHex(cmnds[1]));
            System.out.println("P1: " + atrToHex(cmnds[2]));
            System.out.println("P2: " + atrToHex(cmnds[3]));
        }
    }

    public void setTheDataLength(byte ln) {
        apdu.Lc = ln;
        System.out.println("Lc: " + atrToHex(ln));
    }

    public void setTheDataIn(byte[] data) {
        if (data.length != apdu.Lc) {
            System.err.println("The number of data in the array are more than expected");
        } else {
            apdu.dataIn = data;
            for (int dataIndx = 0; dataIndx < data.length; dataIndx++) {
                System.out.println("dataIn" + dataIndx + ": " + atrToHex(data[dataIndx]));
            }

        }
    }

    public void setExpctdByteLength(byte ln) {
        apdu.Le = ln;
        System.out.println("Le: " + atrToHex(ln));
    }

    public void exchangeTheAPDUWithSimulator() {

        try {
            apdu.setDataIn(apdu.dataIn, apdu.Lc);
            cad.exchangeApdu(apdu);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] decodeDataOut() {

        byte[] dOut = apdu.dataOut;
        for (int dataIndx = 0; dataIndx < dOut.length; dataIndx++) {
            System.out.println("dataOut" + dataIndx + ": " + atrToHex(dOut[dataIndx]));
        }
        return dOut;

    }

    public byte[] decodeStatus() {
        byte[] statByte = apdu.getSw1Sw2();
        System.out.println("SW1: " + atrToHex(statByte[0]));
        System.out.println("SW2: " + atrToHex(statByte[1]));
        return statByte;
    }


    public String atrToHex(byte atCode) {
        char hex[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        String str2 = "";
        int num = atCode & 0xff;
        int rem;
        while (num > 0) {
            rem = num % 16;
            str2 = hex[rem] + str2;
            num = num / 16;
        }
        if (str2 != "") {
            return str2;
        } else {
            return "0";
        }
    }

}
