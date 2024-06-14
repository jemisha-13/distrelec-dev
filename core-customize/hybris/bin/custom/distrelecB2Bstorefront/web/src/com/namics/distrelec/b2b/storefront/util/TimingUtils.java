package com.namics.distrelec.b2b.storefront.util;

import java.util.concurrent.TimeUnit;

/**
 * @author mab@foryouandyourcustomers.com - 24 Mar 2017
 */
public final class TimingUtils {
	
	private TimingUtils() {}
	
	public final static long startTimer(final boolean inNanos) {
		
		if (inNanos) {
			return System.nanoTime();
		} else {
			return System.currentTimeMillis();
		}
	}
	
	public final static long stopTimer(final long start, final boolean inNanos) {
		
		if (inNanos) {
			return System.nanoTime() - start;
		} else {
			return System.currentTimeMillis() - start;
		}
	}
	
	// NANOS
	
	public final static long nanosToMicros(final long nanos) {
		
		return TimeUnit.NANOSECONDS.toMicros(nanos);
	}
	
	public final static long nanosToMillis(final long nanos) {
		
		return TimeUnit.NANOSECONDS.toMillis(nanos);
	}
	
	public final static long nanosToSecs(final long nanos) {
		
		return TimeUnit.NANOSECONDS.toSeconds(nanos);
	}
	
	public final static long nanosToMins(final long nanos) {
		
		return TimeUnit.NANOSECONDS.toMinutes(nanos);
	}
	
	// MICROS
	
	public final static long microsToNanos(final long micros) {
		
		return TimeUnit.MICROSECONDS.toNanos(micros);
	}
	
	public final static long microsToMillis(final long micros) {
		
		return TimeUnit.MICROSECONDS.toMillis(micros);
	}
	
	public final static long microsToSecs(final long micros) {
		
		return TimeUnit.MICROSECONDS.toSeconds(micros);
	}
	
	public final static long microsToMins(final long micros) {
		
		return TimeUnit.MICROSECONDS.toMinutes(micros);
	}
	
	// MILLIS
	
	public final static long millisToNanos(final long millis) {
		
		return TimeUnit.MILLISECONDS.toNanos(millis);
	}
	
	public final static long millisToMicros(final long millis) {
		
		return TimeUnit.MILLISECONDS.toMicros(millis);
	}
	
	public final static long millisToSec(final long millis) {
		
		return TimeUnit.MILLISECONDS.toSeconds(millis);
	}
	
	public final static long millisToMins(final long millis) {
		
		return TimeUnit.MILLISECONDS.toMinutes(millis);
	}
	
}
