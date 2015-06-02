package com.soundbyte.util;

import entagged.audioformats.AudioFile;
import entagged.audioformats.AudioFileIO;
import entagged.audioformats.Tag;
import entagged.audioformats.exceptions.CannotReadException;
import entagged.audioformats.generic.GenericTag;
import entagged.audioformats.mp3.util.id3frames.TextId3Frame;
import java.io.File;

public class MP3Tag
{
    private final String title;
    private final String artist;
    private final String album;
    private final String genre;
    private final String year;
    private final String trackNum;
    private final String length;
    private final int bitrate;
    private final String discNum;

    /**
     *
     * @param f
     *
     * @throws CannotReadException if file cannot be read or file has no tags
     */
    public MP3Tag(File f) throws CannotReadException
    {
        AudioFile song = AudioFileIO.read(f);
        final Tag tag = song.getTag();
        if (tag instanceof GenericTag)
        {
            // Song is valid but has no ID3 tags
            // Set title to filename
            title = f.getName();
            // Get bitrate and length
            bitrate = song.getBitrate();
            length = parseLength(song.getLength());
            // Leave all other fields blank
            artist = album = genre = year = trackNum = discNum = "";
            return;
        }
        title = tag.getFirstTitle();
        artist = tag.getFirstArtist();
        album = tag.getFirstAlbum();
        trackNum = tag.getFirstTrack();
        year = tag.getFirstYear();
        bitrate = song.getBitrate();
        length = parseLength(song.getLength());
        genre = tag.getFirstGenre();
        discNum = ((TextId3Frame) tag.get("TPOS").get(0)).getContent();
    }

    public String getTitle()
    {
        return title;
    }

    public String getArtist()
    {
        return artist;
    }

    public String getAlbum()
    {
        return album;
    }

    public String getGenre()
    {
        return genre;
    }

    public String getYear()
    {
        return year;
    }

    public String getTrackNum()
    {
        return trackNum;
    }

    public String getLength()
    {
        return length;
    }

    public int getBitrate()
    {
        return bitrate;
    }

    public String getDiscNum()
    {
        return discNum;
    }

    /**
     * Parses n seconds into HH:MM:SS format
     *
     * @param seconds
     * the number of seconds
     *
     * @return the number of seconds in HH:MM:SS format
     */
    private static String parseLength(final int seconds)
    {
        // if seconds <= 0 return 00:00:00
        if (seconds <= 0)
        {
            return "0:00:00";
        }
        int hours = seconds / 3600;
        int rem = seconds % 3600;
        int mins = rem / 60;
        int secs = rem % 60;
        // Add a zero in front of minutes and seconds < 10, but not hours
        String mnStr = (mins < 10 ? "0" : "") + mins;
        String secStr = (secs < 10 ? "0" : "") + secs;
        return hours + ":" + mnStr + ":" + secStr;
    }
}
