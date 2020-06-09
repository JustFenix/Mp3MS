/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.gries.mp3ms.connection;

import at.gries.mp3ms.Mp3MS;
import static at.gries.mp3ms.Mp3MS.myPlayerThread;
import static at.gries.mp3ms.Mp3MS.playList;
import static at.gries.mp3ms.Mp3MS.position;
import static at.gries.mp3ms.Mp3MS.songToPlay;
import static at.gries.mp3ms.Mp3MS.stopConRand;
import at.gries.mp3ms.PlayerThread;
import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Khom
 */
public class CommandConnection {

    private final static int DOWN = 0;
    private final static int UP = 1;

    public static ServerSocket ss = null;
    public static Socket soc = null;
    public static BufferedReader stringIn = null;
    public static PrintWriter stringOut = null;

    public static void restartConnection() {

        if (stringIn != null) {
            try {
                stringIn.close();
            } catch (IOException ex) {
                Logger.getLogger(CommandConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (stringOut != null) {
            stringOut.close();
        }

        if (!soc.isClosed()) {
            try {
                soc.close();
            } catch (IOException ex) {
                Logger.getLogger(CommandConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (!ss.isClosed()) {
            try {
                ss.close();
            } catch (IOException ex) {
                Logger.getLogger(CommandConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        startCommandConnection();
    }

    public static void startCommandConnection() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                String rawCommand = "";

                try {

                    ss = new ServerSocket(6969);
                    System.out.println("[PLAYER_COMMAND] Waiting for client....");
                    soc = ss.accept();
                    System.out.println("[PLAYER_COMMAND] Client connection from " + soc.getRemoteSocketAddress());

                    System.out.println("[PLAYER_COMMAND] Start ClientConnection getConnection with IP: " + Mp3MS.remoteIPAddress);

                    ClientConnection.getConnection(Mp3MS.remoteIPAddress);

                    stringIn = new BufferedReader(new InputStreamReader(soc.getInputStream()));
                    stringOut = new PrintWriter(soc.getOutputStream(), true);
                    stringOut.println("AFTER CONNECTION");

                    int command;

                    while (!rawCommand.equals("end")) {

                        String test = stringIn.readLine();

                        if (test == null) {
                            command = 10;
                            rawCommand = "end";
                        } else {
                            command = Integer.parseInt(test);
                        }

                        switch (command) {
                            case 0: // STOP
                                stopPlayMusic();
                                System.out.println("[PLAYER_COMMAND] || ");
                                if (!playList.isEmpty()) {
                                    stringOut.println("[PLAYER_COMMAND] ||" + playList.get(songToPlay).getArtist() + "  " + songToPlay + " " + playList.get(songToPlay).getTitle());
                                }
                                break;

                            case 1: // START
                                startPlayMusic();
                                System.out.println("[PLAYER_COMMAND] > ");
                                if (!playList.isEmpty()) {
                                    stringOut.println("[PLAYER_COMMAND] > " + playList.get(songToPlay).getArtist() + "  " + songToPlay + " " + playList.get(songToPlay).getTitle());
                                }
                                break;

                            case 2: // >>
                                if (myPlayerThread != null) {
                                    stopPlayMusic();
                                }
                                if (songToPlay == (playList.size() - 1)) {
                                    songToPlay = 0;
                                } else {
                                    songToPlay++;
                                }
                                Mp3MS.position = 0;
                                startPlayMusic();
                                System.out.println("[PLAYER_COMMAND] >> ");
                                stringOut.println("[PLAYER_COMMAND] || " + playList.get(songToPlay).getArtist() + "  " + songToPlay + " " + playList.get(songToPlay).getTitle());
                                break;

                            case 3: // <<
                                if (myPlayerThread != null) {
                                    stopPlayMusic();
                                }
                                if (songToPlay == 0) {
                                    songToPlay = (playList.size() - 1);
                                } else {
                                    songToPlay--;
                                }
                                Mp3MS.position = 0;
                                startPlayMusic();
                                System.out.println("[PLAYER_COMMAND] << ");
                                stringOut.println("[PLAYER_COMMAND] << " + playList.get(songToPlay).getArtist() + "  " + songToPlay + " " + playList.get(songToPlay).getTitle());
                                break;

                            case 4: // STOP
                                System.out.println("[PLAYER_COMMAND] stop");
                                stringOut.println("[PLAYER_COMMAND] stop");
                                stopConRand = 0;
                                break;

                            case 5: // CONTINUE
                                System.out.println("[PLAYER_COMMAND] continue");
                                stringOut.println("[PLAYER_COMMAND] continue");
                                stopConRand = 1;
                                break;

                            case 6: //SHUFFLE
                                System.out.println("[PLAYER_COMMAND] shuffle");
                                stringOut.println("[[PLAYER_COMMAND] shuffle");
                                stopConRand = 2;
                                break;

                            case 7: //setTo0
                                System.out.println("[PLAYER_COMMAND] set to 0");
                                stringOut.println("[[PLAYER_COMMAND] set to 0");
                                Mp3MS.position = 0;
                                Mp3MS.songToPlay = 0;
                                break;

                            case 9:
                                System.out.println("");
                                System.out.println("-------------------- THREAD INFO ------------------------------");
                                System.out.println("position: " + position);
                                if (myPlayerThread != null) {
                                    System.out.println(myPlayerThread.toString());
                                    System.out.println("myPlayerThread hash:" + myPlayerThread.hashCode());
                                } else {
                                    System.out.println("myPlayerThread == null");
                                }
                                getNumberOfThreads();
                                break;


                            case 11:
                                System.out.println("");
                                System.out.println("Volume Down");
                                changeVolume(DOWN);
                                break;

                            case 12:
                                System.out.println("");
                                System.out.println("Volume Up");
                                changeVolume(UP);
                                break;





                                
                                
                            case 10:
                                System.out.println("[PLAYER] CASE: 10 ");
                                rawCommand = "";
                                stringIn.close();
                                stringOut.close();
                                soc.close();
                                ss.close();
                                return;
                                
                            default:
                                System.out.println("[PLAYER_COMMAND] I don't know");
                        }
                    }
                } catch (IOException ex) {
                    System.out.println(" [PLAYER_COMMAND] EXCEPTION " + ex);
                    Logger.getLogger(Mp3MS.class
                            .getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        if (stringIn != null) {
                            stringIn.close();
                        }
                        if (stringOut != null) {
                            stringOut.close();
                        }
                        if (soc != null) {
                            soc.close();
                        }
                        if (ss != null) {
                            ss.close();
                        }
                    } catch (Exception e) {

                        System.out.println("[PLAYER_COMMAND] exception in finally " + e);
                    }
                    System.out.println("[PLAYER_COMMAND] DEBUG startUp() from finally");
                    rawCommand = "end";
                    startCommandConnection();
                }
            }
        }
        ).start();

    }

    public static void stopPlayMusic() {
        if (myPlayerThread != null) {
            songToPlay = myPlayerThread.getSongToPlay();
            myPlayerThread.stopPlayer();
//            myPlayerThread.setIsPaused(true);
            // myPlayerThread.setIsStopped(true);
            myPlayerThread = null;

        }
    }

    public static void startPlayMusic() {
        if (myPlayerThread == null) {
            myPlayerThread = new PlayerThread(playList, songToPlay, position);
            myPlayerThread.setIsStopped(false);
            Thread thread = new Thread(myPlayerThread);
            thread.setDaemon(true);
            thread.start();
//            int frameCount = myPlayerThread.getFrameCount();

        } else {
            System.out.println("[PLAYER_COMMAND] Player already running");
        }

    }

    public static void getNumberOfThreads() {
        ThreadGroup currentGroup = Thread.currentThread().getThreadGroup();
        int noThreads = currentGroup.activeCount();
        Thread[] lstThreads = new Thread[noThreads];
        currentGroup.enumerate(lstThreads);

        for (int i = 0; i < noThreads; i++) {
            System.out.println(" [PLAYER_COMMAND] Thread No:" + i + " = " + lstThreads[i].getName());
        }
        System.out.println("------------------------------------------------");
        System.out.println("");
    }

    public static void changeVolume(int direction) {
        StringBuffer output = new StringBuffer();
        String command = "";
        
        if (direction == DOWN) {
            command = "amixer set PCM 5db-";
        } else if (direction == UP) {
            command = "amixer set PCM 5db+";
        }
        try {
            Process p = Runtime.getRuntime().exec(command);
            try {
                p.waitFor();
            } catch (InterruptedException ex) {
                Logger.getLogger(CommandConnection.class.getName()).log(Level.SEVERE, null, ex);
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }

        } catch (IOException ex) {
            Logger.getLogger(CommandConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println(output.toString());
    }
}
