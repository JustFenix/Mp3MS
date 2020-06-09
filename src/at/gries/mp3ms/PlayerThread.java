/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.gries.mp3ms;

import at.gries.mp3ms.connection.ClientConnection;
import at.gries.mp3ms.connection.CommandConnection;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.FactoryRegistry;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

/**
 *
 * @author Khom
 */
public class PlayerThread implements Runnable {

    private ArrayList<Song> playList;
    private int position = 0;
    private int songToPlay;
    public static volatile boolean isStopped;
    public volatile boolean isPaused;
    private static volatile AdvancedPlayer amp;

    private Bitstream bitstream;
    private AudioDevice audio;
    private Decoder decoder;
    private int frameNumber;
    private int frameCount;

    private static PlayerThread instance;

//    public static PlayerThread getInstance() {
//        if (instance == null) {
//            instance = new PlayerThread();
//            
//            if (amp != null) {
//                System.out.println(ConsoleColors.RED_BACKGROUND + "<PLayer> isStopped " + isStopped + ConsoleColors.RESET);
//                killPlayer();
//            }
//        }
//        if (amp != null) {
//            System.out.println(ConsoleColors.RED_BACKGROUND + "<PLayer> isStopped " + isStopped + ConsoleColors.RESET);
//            killPlayer();
//        }
//        return instance;
//           
//    }



    public PlayerThread(ArrayList<Song> playList, int songToPlay, int position) {
        this.playList = playList;
        this.songToPlay = songToPlay;
        this.position = position;
//        try {
//            openAudio();
//        } catch (JavaLayerException ex) {
//            Logger.getLogger(PlayerThread.class.getName()).log(Level.SEVERE, null, ex);
//        }
        System.out.println(ConsoleColors.YELLOW_BACKGROUND + "<PLayer> Constructor " + this.toString() + ConsoleColors.RESET);
    }
    
    @Override
    public void run() {
        System.out.println(ConsoleColors.YELLOW_BACKGROUND + "<PLAYER> called run" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.YELLOW_BACKGROUND + "<PLAYER> isStopped " + isStopped + ConsoleColors.RESET);

        while (!this.isStopped) {
            try {
                if (!playList.isEmpty()) {
                    System.out.println(ConsoleColors.YELLOW_BACKGROUND + "<PLAYER> runs start.amp() " + ConsoleColors.RESET);
                    ClientConnection.sendInfo(this.playList.get(Mp3MS.songToPlay).getArtist() + ": " + this.playList.get(Mp3MS.songToPlay).getTitle());
                    startAmp();
                } else {
                    ClientConnection.sendInfo("Playliste empty!");
                    this.isStopped = true;
                }
            } catch (FileNotFoundException | JavaLayerException ex) {
                Logger.getLogger(PlayerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void startAmp() throws FileNotFoundException, JavaLayerException {
        System.out.println(ConsoleColors.YELLOW_BACKGROUND + "<PLAYER> start amp: song to play  " + songToPlay + " position " + position + ConsoleColors.RESET);
        Random rand = new Random();
        for (int i = songToPlay; i < this.playList.size(); i++) {
            System.out.println(ConsoleColors.YELLOW_BACKGROUND + "<PLAYER> plays Track: " + (i + 1) + " Title: " + this.playList.get(i).getTitle() + ConsoleColors.RESET);
            ClientConnection.sendInfo(this.playList.get(i).getArtist() + ": " + this.playList.get(i).getTitle());
            //TODO commandconnection entfernen wird nicht gebraucht
            //CommandConnection.stringOut.println(this.playList.get(i).getTitle());
            FileInputStream is = new FileInputStream(this.playList.get(i).getPath());
            
         
            amp = new AdvancedPlayer(is);
            
            System.out.println(ConsoleColors.YELLOW_BACKGROUND + "<PLAYER> startAmp amp.play() hash: " +amp.hashCode()+" : " +this.playList.get(i) + ConsoleColors.RESET);
            amp.play(position, Integer.MAX_VALUE);

            position = 0;
            songToPlay = i + 1;

            if (songToPlay == this.playList.size() & Mp3MS.stopConRand == 1) {
                songToPlay = 0;

            } else if (songToPlay == this.playList.size() & Mp3MS.stopConRand == 0) {
                Mp3MS.position = 0;
                Mp3MS.songToPlay = 0;
                this.isStopped = true;
                Mp3MS.myPlayerThread = null;

            } else if (Mp3MS.stopConRand == 2) {
                int r = rand.nextInt(this.playList.size() - 1);
                i = r;
            }

            if (this.isStopped) {
                return;
            }
        }

    }

    public void stopPlayer() {
        if (amp != null) {
            amp.setPlayBackListener(new PlaybackListener() {
                @Override
                public void playbackFinished(PlaybackEvent event) {
//                    Mp3MS.position = event.getFrame();
                    Mp3MS.position = position + (44 * event.getFrame() / 1000);
                    //Mp3EE.nativPosition = event.getFrame();
                }
            });
            this.isStopped = true;
            amp.stop();
            System.out.println(ConsoleColors.RED_BACKGROUND + "<PLayer> stopPlayer amp.stop() " + ConsoleColors.RESET);
            amp.close();
            System.out.println(ConsoleColors.RED_BACKGROUND + "<PLayer> stopPlayer amp.close() " + ConsoleColors.RESET);
            amp = null;
            System.out.println(ConsoleColors.RED_BACKGROUND + "<PLayer> stopPlayer amp = null " + ConsoleColors.RESET);
            if(amp== null){
                System.out.println(ConsoleColors.RED_BACKGROUND + "<PLayer> amp destroyed " + ConsoleColors.RESET);
            }else{
                System.out.println(ConsoleColors.RED_BACKGROUND + "<PLayer> amp not null something is gone wrong " + ConsoleColors.RESET);
            }
        }
    }

    public static void killPlayer() {
        if (amp != null) {
            System.out.println(ConsoleColors.YELLOW_BACKGROUND + "<PLayer> killPlayer amp.close() " + ConsoleColors.RESET);
            amp.close();
            amp = null;

        }
    }

    public ArrayList<Song> getPlayList() {
        return playList;
    }

    public void setPlayList(ArrayList<Song> playList) {
        this.playList = playList;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getSongToPlay() {
        return songToPlay;
    }

    public void setSongToPlay(int songToPlay) {
        this.songToPlay = songToPlay;
    }

    @Override
    public String toString() {
        return "PlayerThread{" + "position=" + position + ", songToPlay=" + songToPlay + ", isStopped=" + isStopped + ", isPaused=" + isPaused + '}';
    }

    public boolean isIsPaused() {
        return isPaused;
    }

    public void setIsPaused(boolean isPaused) {
        this.isPaused = isPaused;
    }

    public boolean isIsStopped() {
        return isStopped;
    }

    public void setIsStopped(boolean isStopped) {
        this.isStopped = isStopped;
    }

//    /**
//     * Open an audio device.
//     */
//    protected void openAudio() throws JavaLayerException {
//        audio = FactoryRegistry.systemRegistry().createAudioDevice();
//        decoder = new Decoder();
//        audio.open(decoder);
//    }
//
//    /**
//     * Read a frame.
//     *
//     * @return The frame read.
//     */
//    protected Header readFrame() throws JavaLayerException {
//        if (audio != null) {
//            return bitstream.readFrame();
//        } else {
//            return null;
//        }
//    }
//
//    /**
//     * skips over a single frame
//     *
//     * @return false if there are no more frames to decode, true otherwise.
//     */
//    protected boolean skipFrame() throws JavaLayerException {
//        Header h = readFrame();
//        if (h == null) {
//            return false;
//        }
//        frameNumber++;
//        bitstream.closeFrame();
//        return true;
//    }
//
//    /**
//     * Count the number of frames in the file. This can be used for positioning.
//     *
//     * @param filename The file to be measured.
//     * @return The number of frames.
//     */
//    protected int getFrameCount(String filename) throws JavaLayerException {
//        openBitstream(filename);
//        int count = 0;
//        while (skipFrame()) {
//            count++;
//        }
//        bitstream.close();
//        return count;
//    }
//
//    /**
//     * Open a BitStream for the given file.
//     *
//     * @param filename The file to be opened.
//     * @throws IOException If the file cannot be opened.
//     */
//    protected void openBitstream(String filename)
//            throws JavaLayerException {
//        try {
//            bitstream = new Bitstream(new BufferedInputStream(
//                    new FileInputStream(filename)));
//        } catch (java.io.IOException ex) {
//            throw new JavaLayerException(ex.getMessage(), ex);
//        }
//
//    }
//
//    public int getFrameCount() {
//        return frameCount;
//    }

}
