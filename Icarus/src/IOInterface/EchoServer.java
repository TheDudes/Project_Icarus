/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IOInterface;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

/**
 *
 * @author Jonas Huber <Jonas_Huber2@gmx.de>
 */
public class EchoServer {

    public static void main(String... args) throws Exception {

        int port = 5021;
        byte[] array = new byte[20];
        ServerSocket serverSocket = new ServerSocket(port);
        System.err.println("Started server on port " + port);

        // repeatedly wait for connections, and process

        while (true) {

            // open up IO streams
            Socket clientSocket = serverSocket.accept();
            BufferedInputStream in = new BufferedInputStream(clientSocket.getInputStream());
            in.read(array, 0, 8);
            BufferedOutputStream out = new BufferedOutputStream(clientSocket.getOutputStream());
            out.write(array, 0, 8);
            out.flush();

            System.out.println(Arrays.toString(array));
        }

    }

}
