/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.gries.mp3ms.connection;

import at.gries.mp3ms.ConsoleColors;
import at.gries.mp3ms.Mp3MS;
import at.gries.mp3ms.PlayerThread;
import at.gries.mp3ms.Song;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Khom
 */
public class DataConnection {

    public static ServerSocket ss = null;
    public static Socket soc = null;
    public static ObjectOutputStream oos = null;
    public static ObjectInputStream ois = null;
    public static ArrayList<Song> songList = new ArrayList<>();

    public static void startDataConnection() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    ss = new ServerSocket(6970);
                    System.out.println(ConsoleColors.BLUE + "[PLAYER_DATA] Waiting for client...." + ConsoleColors.RESET);
                    soc = ss.accept();

                    System.out.println(ConsoleColors.BLUE + "[PLAYER_DATA] Client connection from " + soc.getRemoteSocketAddress() + ConsoleColors.RESET);

                    ois = new ObjectInputStream(soc.getInputStream());
                    oos = new ObjectOutputStream(soc.getOutputStream());

                    if (Mp3MS.playList != null) {
                        oos.writeObject(Mp3MS.playList);
                        oos.flush();
                    }

                    Mp3MS.playList = (ArrayList<Song>) ois.readObject();
                    if (!Mp3MS.playList.isEmpty()) {
                        ClientConnection.sendInfo(Mp3MS.playList.get(Mp3MS.songToPlay).getArtist() + ": " + Mp3MS.playList.get(Mp3MS.songToPlay).getTitle());
                        System.out.println(ConsoleColors.BLUE + "[PLAYER_DATA] " + Mp3MS.playList.get(0).getArtist() + " <> " + Mp3MS.playList.get(0).getTitle()+ ConsoleColors.RESET);
                    } else {
                        System.out.println(ConsoleColors.BLUE + "[PLAYER_DATA] playlist empty"  + ConsoleColors.RESET);
                        ClientConnection.sendInfo("PLAYLIST empty");

                    }
                    for (Song song : Mp3MS.playList) {
                        System.out.println(ConsoleColors.BLUE + "              " + song.getTitle() + ConsoleColors.RESET);
                    }
//                    CommandConnection.stopPlayMusic();
//                    Mp3MS.position = 0;
//                    Mp3MS.songToPlay = 0;
                    oos.close();
                    ois.close();
                    soc.close();
                    ss.close();

                    //System.out.println("[PLAYER_DATA] DEBUG bottom of loop");
                } catch (IOException | ClassNotFoundException ex) {
                    // System.out.println("[PLAYER_DATA] DEBUG DataConnection catch " + ex);
                    Logger.getLogger(DataConnection.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        if (oos != null) {
                            oos.close();
                        }
                        if (ois != null) {
                            ois.close();
                        }
                        if (soc != null) {
                            soc.close();
                        }
                        if (ss != null) {
                            ss.close();
                        }
                    } catch (Exception e) {
                        //System.out.println("[PLAYER_DATA] DataConnection finally: " + e);
                    }
                    DataConnection.startDataConnection();
                    //System.out.println("[PLAYER_DATA] startUpData() from finally");
                }
            }
        }).start();
    }
}
