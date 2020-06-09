/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.gries.mp3ms;

import at.gries.mp3ms.connection.CommandConnection;
import at.gries.mp3ms.connection.DataConnection;
import at.gries.mp3ms.connection.ListenForClient;
import java.util.ArrayList;

import at.gries.mp3ms.persist.SongDao;

public class Mp3MS {

    public static String remoteIPAddress;
    public static  PlayerThread myPlayerThread;
    public static int stopConRand = 1;
    public static int position = 0;
    public static int songToPlay;

    public static ArrayList<Song> playList = new ArrayList<>();

    public static void main(String[] args) {

        SongDao songDao = new SongDao();

        ListenForClient.startListenForClient();
        
        CommandConnection.startCommandConnection();
        
        DataConnection.startDataConnection();

    }
}
