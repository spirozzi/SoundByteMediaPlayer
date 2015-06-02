package com.soundbyte.model.table;

public final class NowPlayingEntry
{
	private final String titleAndArtist;
	private final String path;

	public NowPlayingEntry(String title, String artist, String path)
	{
		titleAndArtist = title + " - " + artist;
		this.path = path;
	}

	public String getTitleAndArtist()
	{
		return titleAndArtist;
	}

	public String getPath()
	{
		return path;
	}

	@Override
	public String toString()
	{
		return titleAndArtist;
	}
}
