package nl.hu.zrb.btclient;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by JZuurbier on 26-9-2015.
 */
public class RobotConnection {
    private UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private boolean connected = false;
    private boolean programReady = false;
    BluetoothSocket theSocket;
    private OutputStream out;
    private InputStream in;

    public boolean isConnected(){
        return connected;
    }

    public boolean isProgramReady(){
        return programReady;
    }

    public void connectToDevice(BluetoothDevice device) throws IOException {
        theSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
        theSocket.connect();
        out = theSocket.getOutputStream();
        in = theSocket.getInputStream();
        connected = true;
    }

    public void startProgram(String name)throws IOException {
        byte[] command = createStartCommand(name);
        sendCommand(command);
        programReady = true;
    }

    public void closeConnection(){
        if(!connected) return;
        programReady = false;
        connected = false;
        try {
            theSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendCommand(byte[] command) throws IOException {
        out.write(command.length & 0xFF);
        out.write(command.length >> 8);
        out.write(command);
        int i = in.read(command);
        if(i == 5 && command[3] == 0x00 && command[4] != 0 )
            throw new IOException(getErrorMessage(command[4]));
    }

    public byte[] createWriteCommand(String s){
        byte[] bytes = s.getBytes();
        byte[] command = new byte[bytes.length + 5];
        command[0] = 0x00; //reply
        command[1] = 0x09; //write
        command[2] = 5;    //mailboxnumber
        command[3] = (byte) (bytes.length + 1);
        for(int i = 0; i < bytes.length; i++ )
            command[i+4] = bytes[i];
        command[bytes.length + 4] = 0x00; //terminator
        return command;
    }

    public static String getErrorMessage(byte x){
        switch(x) {
            case 0x20: return "Pending communication transaction in progress";
            case 0x40: return "Specified mailbox queue is empty";
            case  (byte)0xBD: return "Request failed (i.e. specified file not found)";
            case (byte)0xBE: return "Unknown command opcode";
            case (byte)0xBF: return "Insane packet";
            case (byte)0xC0: return "Data contains out-of-range values";
            case (byte)0xDD: return "Communication bus error";
            case (byte)0xDE: return "No free memory in communication buffer";
            case (byte)0xDF: return "Specified channel/connection is not valid";
            case (byte)0xE0: return "Specified channel/connection not configured or busy";
            case (byte)0xEC: return "No active program";
            case (byte)0xED: return "Illegal size specified";
            case (byte)0xEE: return "Illegal mailbox queue ID specified";
            case (byte)0xEF: return "Attempted to access invalid field of a structure";
            case (byte)0xF0: return "Bad input or output specified";
            case (byte)0xFB: return "Insufficient memory available";
            case (byte)0xFF: return "Bad arguments";
            default: return "Unknown error";
        }
    }

    public static byte[] createStartCommand(String s){
        byte[] bytes = s.getBytes();
        byte[] command = new byte[bytes.length + 3];
        command[0] = 0x00; //reply
        command[1] = 0x00; //start program
        for(int i = 0; i < bytes.length; i++ )
            command[i+2] = bytes[i];
        command[bytes.length + 2] = 0x00; //terminator
        return command;
    }

}
