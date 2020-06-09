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

//        playList = songDao.getSongList("Bob Dylan", "A Fool Such As I");

        ListenForClient.startListenForClient();

        
        CommandConnection.startCommandConnection();

        
        DataConnection.startDataConnection();

    }

//    private static void startUpCommand() {
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                ServerSocket ss = null;
//                Socket soc = null;
//                BufferedReader stringIn = null;
//                PrintWriter stringOut = null;
//
//                try {
//
//                    ss = new ServerSocket(6969);
//                    System.out.println("[PLAYER] Waiting for client....");
//                    soc = ss.accept();
//                    System.out.println("[PLAYER] Client connection from " + soc.getRemoteSocketAddress());
//
//                    stringIn = new BufferedReader(new InputStreamReader(soc.getInputStream()));
//                    stringOut = new PrintWriter(soc.getOutputStream(), true);
//
//                    String rawCommand = "";
//                    int command;
//
//                    while (!rawCommand.equals("end")) {
//
//                        String test = stringIn.readLine();
//
//                        if (test == null) {
//                            command = 10;
//                            rawCommand = "end";
//                        } else {
//                            command = Integer.parseInt(test);
//                        }
//
//                        switch (command) {
//                            case 0: // STOP
//                                stopPlayMusic();
//                                System.out.println("PLAYER: stop " + playList.get(songToPlay).getArtist() + "  " + songToPlay + " " + playList.get(songToPlay).getTitle());
//                                stringOut.println("PLAYER: stop " + playList.get(songToPlay).getArtist() + "  " + songToPlay + " " + playList.get(songToPlay).getTitle());
//                                break;
//
//                            case 1: // START
//                                startPlayMusic();
//                                System.out.println("PLAYER: start " + playList.get(songToPlay).getArtist() + "  " + songToPlay + " " + playList.get(songToPlay).getTitle());
//                                stringOut.println("PLAYER: start " + playList.get(songToPlay).getArtist() + "  " + songToPlay + " " + playList.get(songToPlay).getTitle());
//                                break;
//
//                            case 2: // >>
//                                if (myPlayerThread != null) {
//                                    stopPlayMusic();
//                                }
//                                if (songToPlay == (playList.size() - 1)) {
//                                    songToPlay = 0;
//                                } else {
//                                    songToPlay++;
//                                }
//                                position = 0;
//                                startPlayMusic();
//                                System.out.println("PLAYER: >> " + playList.get(songToPlay).getArtist() + "  " + songToPlay + " " + playList.get(songToPlay).getTitle());
//                                stringOut.println("PLAYER: >> " + playList.get(songToPlay).getArtist() + "  " + songToPlay + " " + playList.get(songToPlay).getTitle());
//                                break;
//
//                            case 3: // <<
//                                if (myPlayerThread != null) {
//                                    stopPlayMusic();
//                                }
//                                if (songToPlay == 0) {
//                                    songToPlay = (playList.size() - 1);
//                                } else {
//                                    songToPlay--;
//                                }
//                                position = 0;
//                                startPlayMusic();
//                                System.out.println("PLAYER: << " + playList.get(songToPlay).getArtist() + "  " + songToPlay + " " + playList.get(songToPlay).getTitle());
//                                stringOut.println("PLAYER: << " + playList.get(songToPlay).getArtist() + "  " + songToPlay + " " + playList.get(songToPlay).getTitle());
//                                break;
//                            case 4:
//                                System.out.println("PLAYER: play to end");
//                                stringOut.println("PLAYER: play to end");
//                                stopConRand = 0;
//                                break;
//
//                            case 5:
//                                System.out.println("PLAYER: loop");
//                                stringOut.println("PLAYER: loop");
//                                stopConRand = 1;
//                                break;
//
//                            case 6:
//                                System.out.println("PLAYER: random");
//                                stringOut.println("PLAYER: random");
//                                stopConRand = 2;
//                                break;
//
//                            case 9:
//                                System.out.println("INFO");
//                                System.out.println("position: " + position);
//                                if (myPlayerThread != null) {
//                                    System.out.println(myPlayerThread.toString());
//                                } else {
//                                    System.out.println("myPlayerThread == null");
//                                }
//                                getNumberOfThreads();
//                                break;
//                            case 10:
//                                System.out.println("CASE: 10 ");
//                                rawCommand = "";
//                                stringIn.close();
//                                stringOut.close();
//                                soc.close();
//                                ss.close();
//                                //System.out.println("DEBUG startUp() from Case 10");
//                                //startUp();
//                                return;
//
//                            default:
//                                System.out.println("I don't know");
//                        }
//                    }
//                } catch (IOException ex) {
//                    System.out.println("EXCEPTION " + ex);
//                    Logger.getLogger(Mp3MS.class.getName()).log(Level.SEVERE, null, ex);
//                } finally {
//                    try {
//                        if (stringIn != null) {
//                            stringIn.close();
//                        }
//                        if (stringOut != null) {
//                            stringOut.close();
//                        }
//                        if (soc != null) {
//                            soc.close();
//                        }
//                        if (ss != null) {
//                            ss.close();
//                        }
//                    } catch (Exception e) {
//                    }
//                    System.out.println("DEBUG startUp() from finally");
//                    startUpCommand();
//                }
//            }
//        }).start();
//
//    }
//
//    private static void stopPlayMusic() {
//        if (myPlayerThread != null) {
//            songToPlay = myPlayerThread.getSongToPlay();
//            myPlayerThread.stopPlayer();
////            myPlayerThread.setIsPaused(true);
//            myPlayerThread.setIsStopped(true);
//            myPlayerThread = null;
//
//            System.out.println("[STOP_PLAY position " + position);
//            System.out.println("[STOP_PLAY song " + songToPlay);
//            
//            getNumberOfThreads();
//        }
//    }
//
//    private static void startPlayMusic() {
//        System.out.println("[START_PLAY position " + position);
//        System.out.println("[START_PLAY song " + songToPlay);
//
//        if (myPlayerThread == null) {
//            myPlayerThread = new PlayerThread(playList, songToPlay, position);
//            Thread thread = new Thread(myPlayerThread);
//            thread.setDaemon(true);
//            thread.start();
//        } else {
//            System.out.println("Player already running");
//        }
//    }
//
//    private static void getNumberOfThreads() {
//        ThreadGroup currentGroup = Thread.currentThread().getThreadGroup();
//        int noThreads = currentGroup.activeCount();
//        Thread[] lstThreads = new Thread[noThreads];
//        currentGroup.enumerate(lstThreads);
//
//        for (int i = 0; i < noThreads; i++) {
//            System.out.println("Thread No:" + i + " = " + lstThreads[i].getName());
//        }
//    }
}
