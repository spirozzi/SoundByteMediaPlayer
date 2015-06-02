package com.soundbyte.controller;

import com.soundbyte.model.DataModelInterface;
import com.soundbyte.model.table.NowPlayingEntry;
import com.soundbyte.util.FlacPlayer;
import javax.swing.table.DefaultTableModel;

public class PlaybackController implements PlaybackControlObserver
{
	// TODO: replace with seek table flac player
	private final FlacPlayer flacPlayer;
	// TODO: add mp3 player
	private final DataModelInterface model;
	/**
	 * Shared by main thread and player thread
	 */
	private volatile boolean isPlaying;

	public PlaybackController(DataModelInterface model)
	{
		this.model = model;
		flacPlayer = new FlacPlayer();
		isPlaying = false;
	}

	public synchronized boolean isPlaying()
	{
		return isPlaying;
	}

	@Override
	public void playFromNowPlayingTable(int rowIndex)
	{
		if (rowIndex == -1)
		{
			// no song selected in table
			return;
		}
		final DefaultTableModel tableModel = model.getNowPlayingTableModel();
		NowPlayingEntry song = (NowPlayingEntry) tableModel.getDataVector().get(
				rowIndex);
		String path = song.getPath();
	}

	@Override
	public void nextSongPressed()
	{
	}

	@Override
	public void pauseButtonPressed()
	{
	}

	@Override
	public void playButtonPressed(int nowPlayingTableIndex)
	{
	}

	@Override
	public void previousSongPressed()
	{
	}

}
