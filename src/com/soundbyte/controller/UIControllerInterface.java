package com.soundbyte.controller;

import java.io.File;

public interface UIControllerInterface
{
    public void addSongsToLibrary(File[] files);

    public void addFoldersToLibrary(File[] folders);

    public void openOptions();

    public void search(String text);

    public void toggleTitleSearchFlag();

    public void toggleArtistSearchFlag();

    public void toggleAlbumSearchFlag();

    public void toggleGenreSearchFlag();

    public void toggleYearSearchFlag();

    public void reloadSongTable();

    public int addToNowPlaying(int[] rowIndexes);

    public void clearNowPlayingTable();
}
