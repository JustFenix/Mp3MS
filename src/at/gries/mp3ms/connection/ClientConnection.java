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
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Khom
 */
public class ClientConnection {

   public static Socket soc;
    static PrintWriter out;

    public static void getConnection(final String ipAddress) {

        System.out.println(ConsoleColors.PURPLE + "[CLIENT_CONNECTION] GetConnection started " + ConsoleColors.RESET);
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    soc = new Socket(ipAddress, 6971);
                    if (soc.isConnected()) {
                        System.out.println(ConsoleColors.PURPLE + "[CLIENT_CONNECTION] GetConnection connected to: " + soc.getInetAddress() + ConsoleColors.RESET);
                    }
                    out = new PrintWriter(soc.getOutputStream(), true);

                    if (!Mp3MS.playList.isEmpty()) {
                        String artist = Mp3MS.playList.get(Mp3MS.songToPlay).getArtist();
                        String title = Mp3MS.playList.get(Mp3MS.songToPlay).getTitle();
                        out.println(artist + ": " + title);
                        System.out.println(ConsoleColors.PURPLE + artist + ": " + title + soc.getInetAddress() + ConsoleColors.RESET);
                    } else {
                        out.println("PLAYLIST empty!");
                    }

                } catch (IOException ex) {
                    Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();

    }

    public static void sendInfo(String info) {
        out.println(info);
    }

}
