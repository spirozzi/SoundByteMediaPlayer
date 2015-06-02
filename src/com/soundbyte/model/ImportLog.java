package com.soundbyte.model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

class ImportLog
{
	private static final Logger log = Logger.getAnonymousLogger();

	// Singleton
	private static final ImportLog instance = new ImportLog();
	private final ImportLogHandler handler;

	private ImportLog()
	{
		handler = new ImportLogHandler();
		log.addHandler(handler);
	}

	public static ImportLog getInstance()
	{
		return instance;
	}

	public void logSuccess(String path)
	{
		log.log(Level.INFO, String.format("Added file %s to library",
				path));
	}

	public void logFailure(String path)
	{
		log.log(Level.INFO, String.format("Could not add file %s to library",
				path));
	}

	public int getNumReads()
	{
		return handler.getNumReads();
	}

	public int getNumErrors()
	{
		return handler.getNumErrors();
	}

	public List getMessages()
	{
		return handler.getMessages();
	}

	private class ImportLogHandler extends Handler
	{
		private List<String> messages;
		private int numReads;
		private int numErrors;

		ImportLogHandler()
		{
			initMessages();
		}

		private void initMessages()
		{
			messages = new ArrayList<>();
		}

		private void reset()
		{
			messages = null;
			numReads = 0;
			numErrors = 0;
		}

		@Override
		public void close() throws SecurityException
		{
		}

		@Override
		public void flush()
		{
			reset();
		}

		@Override
		public void publish(LogRecord record)
		{
			if (messages == null)
			{
				initMessages();
			}
			String msg = record.getMessage();
			if (msg.startsWith("Read: "))
			{
				// song was added to database successfully
				numReads++;
			}
			else
			{
				// song was not added to database
				numErrors++;
			}
			// save message
			messages.add(msg);
		}

		public int getNumReads()
		{
			return numReads;
		}

		public int getNumErrors()
		{
			return numErrors;
		}

		public List<String> getMessages()
		{
			return messages;
		}
	}
}
