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

package ch.njol.skript.lang;

import org.bukkit.event.Event;

import ch.njol.skript.config.SectionNode;

/**
 * @author Peter Güttinger
 */
public class While extends TriggerSection {
	private static final long serialVersionUID = 7418569480021905083L;
	
	private final Condition c;
	
	public While(final Condition c, final SectionNode n) {
		super(n);
		this.c = c;
	}
	
	@Override
	public String toString(final Event e, final boolean debug) {
		return "while " + c.toString(e, debug);
	}
	
	@Override
	protected TriggerItem walk(final Event e) {
		return super.walk(e, c.check(e));
	}
	
}
