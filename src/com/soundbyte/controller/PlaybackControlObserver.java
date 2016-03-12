package com.soundbyte.controller;

public interface PlaybackControlObserver extends PlaybackControllerInterface
{
    public void playButtonPressed(int nowPlayingTableIndex);

    public void pauseButtonPressed();

    public void nextSongPressed();

    public void previousSongPressed();
}
