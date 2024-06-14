package com.namics.distrelec.b2b.core.timestampupdate.automatedtransfer;

public class TransportException extends RuntimeException {

	public TransportException(final String message, final Throwable e)
	{
		super(message, e);
	}

	public TransportException(final String message)
	{
		super(message);
	}

	public TransportException(final Throwable e)
	{
		super(e);
	}
}
