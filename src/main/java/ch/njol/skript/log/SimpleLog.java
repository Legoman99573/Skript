/*
 *   This file is part of Skript.
 *
 *  Skript is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Skript is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Skript.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * 
 * Copyright 2011, 2012 Peter Güttinger
 * 
 */

package ch.njol.skript.log;

import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.logging.Level;

import org.bukkit.command.CommandSender;

public class SimpleLog implements SubLog {
	
	private final Deque<LogEntry> log = new LinkedList<LogEntry>();
	private int numErrors = 0;
	
	@Override
	public void log(final LogEntry entry) {
		log.add(entry);
		if (entry.getLevel() == Level.SEVERE)
			numErrors++;
	}
	
	/**
	 * Print all retained errors or the given one if no errors were retained.<br>
	 * This sublog is stopped if not already done
	 * 
	 * @param def error to print if no errors were logged, can be null to not print any error if there are none
	 * @return whether there were any errors
	 */
	public final boolean printErrors(final String def) {
		stop();
		boolean hasError = false;
		for (final LogEntry e : log) {
			if (e.getLevel() == Level.SEVERE) {
				SkriptLogger.log(e);
				hasError = true;
			}
		}
		if (!hasError && def != null)
			SkriptLogger.log(Level.SEVERE, def);
		return hasError;
	}
	
	/**
	 * Sends all retained error messages to the given recipient<br>
	 * This sublog is stopped if not already done
	 * 
	 * @param recipient
	 * @param def error to send if no errors were logged, can be null to not print any error if there are none
	 * @return whether there were any errors to send
	 */
	public final boolean printErrors(final CommandSender recipient, final String def) {
		stop();
		boolean hasError = false;
		for (final LogEntry e : log) {
			if (e.getLevel() == Level.SEVERE) {
				recipient.sendMessage(e.getMessage());
				hasError = true;
			}
		}
		if (!hasError && def != null)
			recipient.sendMessage(def);
		return hasError;
	}
	
	/**
	 * Prints all retained log messages<br>
	 * This sublog is stopped if not already done
	 * 
	 * @return
	 */
	@Override
	public final void printLog() {
		stop();
		SkriptLogger.logAll(log);
	}
	
	public boolean hasErrors() {
		return numErrors != 0;
	}
	
	public LogEntry getFirstError() {
		for (final LogEntry e : log) {
			if (e.getLevel() == Level.SEVERE)
				return e;
		}
		return null;
	}
	
	public LogEntry getFirstError(final String def) {
		for (final LogEntry e : log) {
			if (e.getLevel() == Level.SEVERE)
				return e;
		}
		return new LogEntry(Level.SEVERE, def);
	}
	
	/**
	 * Clears the list of retained log messages.
	 */
	@Override
	public void clear() {
		log.clear();
		numErrors = 0;
	}
	
	public int size() {
		return log.size();
	}
	
	@Override
	public void stop() {
		SkriptLogger.stopSubLog(this);
	}
	
	public Collection<LogEntry> getLog() {
		return Collections.unmodifiableCollection(log);
	}
	
	@Override
	public int getNumErrors() {
		return numErrors;
	}
	
}
