/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.gries.mp3ms.connection;

import at.gries.mp3ms.ConsoleColors;
import at.gries.mp3ms.Mp3MS;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Khom
 */
public class ListenForClient {

    /**
     * Listen to UDP trafic on port 8888 to detect remote client
     */
    public static void startListenForClient() {

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    //Keep a socket open to listen to all the UDP trafic that is destined for this port

                    DatagramSocket socket = new DatagramSocket(8888, InetAddress.getByName("0.0.0.0"));
                    socket.setBroadcast(true);

                    while (true) {
                        System.out.println(ConsoleColors.GREEN + "[PLAYER_LISTEN_FOR_CLIENT] start listening for client...." +ConsoleColors.RESET);
                        //Receive a packet
                        byte[] recvBuf = new byte[15000];
                        DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                        socket.receive(packet);

                        System.out.println(ConsoleColors.GREEN + "[PLAYER_LISTEN_FOR_CLIENT] received packet from " + packet.getAddress().getHostAddress() +ConsoleColors.RESET);
                        System.out.println(ConsoleColors.GREEN + "[PLAYER_LISTEN_FOR_CLIENT] received data " + new String(packet.getData())+ConsoleColors.RESET);

                        String message = new String(packet.getData()).trim();

                        if (message.equals("MP3EE_REQUEST")) {
                            byte[] sendData = "MP3EE_RESPONSE".getBytes();
                            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
                            socket.send(sendPacket);

                            System.out.println(ConsoleColors.GREEN + "[PLAYER_LISTEN_FOR_CLIENT] discovered client " + sendPacket.getAddress().getHostAddress()+ConsoleColors.RESET);
                            Mp3MS.remoteIPAddress = sendPacket.getAddress().getHostAddress();

////                           
////                               try {
////                                CommandConnection.restartConnection();
////                            } catch (Exception e) {
////                                System.out.println(ConsoleColors.GREEN + "[PLAYER_LISTEN_FOR_CLIENT] exception " + e +ConsoleColors.RESET);
////                            }
                                
                           

                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();
    }

}
