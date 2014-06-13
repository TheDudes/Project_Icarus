import java.io.*;
import IOInterface.*;
import java.net.*;
import java.util.*;
public class testServer{

    public static void main(String[] args) throws IOException{
        ServerSocket servSocket = new ServerSocket(5021);
        Socket clientSocket;
        IO_Packet packet;
        while(true){
            clientSocket = servSocket.accept();
            PacketReader reader = new PacketReader(clientSocket.getInputStream());
            PacketWriter writer = new PacketWriter(clientSocket.getOutputStream());
            packet = reader.readPacket();
            System.out.println(packet.getGeraeteId() + " "  + packet.getPin() + " " + packet.getNamespaceId() + " " + packet.getCount() + " " + packet.getRWFlag() + " " + packet.getValue());
            writer.write(packet);
            writer.flush();
            System.out.println("sent back");
            writer.flush();
            reader.close();
            writer.close();
            clientSocket.close();
        }
//        servSocket.close();
    }
}
