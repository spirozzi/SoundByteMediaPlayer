package com.soundbyte.util;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class ErrorHandler
{
	/**
	 * Displays the error message in a dialog box, then exits
	 *
	 * @param error the error message
	 */
	public static void error(String error, Exception ex)
	{
		// TODO: print error, exception message and exception stack trace to file
		String msg = "Fatal error: " + error;
		Logger.getGlobal().log(Level.SEVERE, msg);
		JOptionPane.showMessageDialog(null, "Fatal error: " + error);
		System.exit(1);
	}
}
