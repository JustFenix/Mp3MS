/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.gries.mp3ms.persist;

import at.gries.mp3ms.Song;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Khom
 */
public class SongDao {
    
    public String addOneSong(String title, String artist, String year, 
            String album, long lenght, int bitrate,String path, String track) {

        String SQL = "INSERT into mp3ee.songs (title, artist, year, album, "
                + "lenght, bitrate, path, track) VALUES (?,?,?,?,?,?,?,?)";

        try {
                try (Connection con = DbUtil.getConnection();
                        PreparedStatement stmt = con.prepareStatement(SQL)) {
                    stmt.setString(1, title);
                    stmt.setString(2, artist);
                    stmt.setString(3, year);
                    stmt.setString(4, album);
                    stmt.setLong(5, lenght);
                    stmt.setInt(6, bitrate);
                    stmt.setString(7,path);
                    stmt.setString(8, track);
                    stmt.executeUpdate();
                }

            return "success";
            
        } catch (SQLException ex) {
            DbUtil.processException(ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SongDao.class.getName()).log(Level.SEVERE, null, ex);
        }

        return "fail";
    }

    public String addSongs(ArrayList<Song> songs) {

        String SQL = "INSERT into mp3ee.songs (title, artist, year, album, "
                + "lenght, bitrate, path, track) VALUES (?,?,?,?,?,?,?,?)";

        try {
            try (Connection con = DbUtil.getConnection()) {
                for (Song song : songs) {
                    
                    try (PreparedStatement stmt = con.prepareStatement(SQL)) {
                        stmt.setString(1, song.getTitle());
                        stmt.setString(2, song.getArtist());
                        stmt.setString(3, song.getYear());
                        stmt.setString(4, song.getAlbum());
                        stmt.setLong(5, song.getLenght());
                        stmt.setInt(6, song.getBitrate());
                        stmt.setString(7, song.getPath());
                        stmt.setString(8, song.getTrack());
                        
                        System.out.println("Try to execute: " + song.toString());
                        stmt.executeUpdate();
                        
                    }
                    
                }
            }

            return "success";
            
        } catch (SQLException ex) {
            DbUtil.processException(ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SongDao.class.getName()).log(Level.SEVERE, null, ex);
            
        }

        return "fail";
    }
    
    public ArrayList<Song> getSongList(String artist, String album){
        
        System.out.println("Start DAO");
        System.out.println("ASTIST " + artist);
        System.out.println("ALBUM " + album);
        
        ArrayList<Song> songList = new ArrayList();
        
        String SQL = "SELECT  title, artist, year, album, lenght, bitrate, "
                + "path, track FROM mp3ee.songs WHERE artist = ? AND album = ?";
        
        ResultSet rs = null;
             
        
         try (Connection conn = DbUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(SQL, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);) {
            stmt.setString(1, artist);
            stmt.setString(2, album);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Song mySong =  new Song();
                
                mySong.setTitle(rs.getString("title"));
                mySong.setArtist(rs.getString("artist"));
                mySong.setYear(rs.getString("year"));
                mySong.setAlbum(rs.getString("album"));
                mySong.setLenght(rs.getLong("lenght"));
                mySong.setBitrate(rs.getInt("bitrate"));
                mySong.setPath(rs.getString("path"));
                mySong.setTrack(rs.getString("track"));
                
                songList.add(mySong);
            }
        } catch (Exception e) {
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    DbUtil.processException(ex);
                }
            }
        }
         System.out.println("SONGLIST " +songList.toString());
        return songList;
    }
}
