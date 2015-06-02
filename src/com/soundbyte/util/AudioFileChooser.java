package com.soundbyte.util;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class AudioFileChooser extends JFileChooser
{
	private static final FileFilter supportedFilter
			= new FileNameExtensionFilter("All Supported Files (*.mp3, *.flac)",
					"mp3", "flac");
	private static final FileFilter mp3Filter = new FileNameExtensionFilter(
			"MP3 Audio Files (*.mp3)", "mp3");
	private static final FileFilter flacFilter = new FileNameExtensionFilter(
			"FLAC Audio Files (*.flac)", "flac");

	public AudioFileChooser(boolean foldersOnly)
	{
		setMultiSelectionEnabled(true);
		if (foldersOnly)
		{
			setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		}
		else
		{
			setFileSelectionMode(FILES_ONLY);
			addChoosableFileFilter(supportedFilter);
			addChoosableFileFilter(mp3Filter);
			addChoosableFileFilter(flacFilter);
			setAcceptAllFileFilterUsed(false);
		}
	}
}
