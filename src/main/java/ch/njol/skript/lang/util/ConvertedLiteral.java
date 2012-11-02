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

package ch.njol.skript.lang.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;

import org.bukkit.event.Event;

import ch.njol.skript.SkriptAPIException;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.Utils;
import ch.njol.util.Checker;

/**
 * @author Peter Güttinger
 * @see SimpleLiteral
 */
public class ConvertedLiteral<F, T> extends ConvertedExpression<F, T> implements Literal<T> {
	private static final long serialVersionUID = 2640112540190230558L;
	
	protected transient T[] data;
	
	private void writeObject(final ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
		final String codeName = Classes.getExactClassName(data.getClass().getComponentType());
		if (codeName == null)
			throw new SkriptAPIException(data.getClass().getComponentType().getName() + " is not registered");
		out.writeUTF(codeName);
		final String[][] d = new String[data.length][];
		for (int i = 0; i < data.length; i++) {
			if ((d[i] = Classes.serialize(data[i])) == null) {
				throw new SkriptAPIException("Parsed class cannot be serialized: " + data[i].getClass().getName());
			}
		}
		out.writeObject(d);
	}
	
	private void readObject(final ObjectInputStream in) throws ClassNotFoundException, IOException {
		in.defaultReadObject();
		final String codeName = in.readUTF();
		final String[][] d = (String[][]) in.readObject();
		final ClassInfo<?> ci = Classes.getClassInfo(codeName);
		data = (T[]) Array.newInstance(ci.getC(), d.length);
		for (int i = 0; i < data.length; i++) {
			data[i] = (T) Classes.deserialize(d[i][0], d[i][1]);
		}
	}
	
	public ConvertedLiteral(final Literal<F> source, final T[] data, final Class<T> to) {
		super(source, to, null);
		this.data = data;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <R> Literal<? extends R> getConvertedExpression(final Class<R> to) {
		if (to.isAssignableFrom(this.to))
			return (Literal<? extends R>) this;
		return ((Literal<F>) source).getConvertedExpression(to);
	}
	
	@Override
	public String toString(final Event e, final boolean debug) {
		return Classes.toString(data, getAnd());
	}
	
	@Override
	public T[] getArray() {
		return data;
	}
	
	@Override
	public T[] getAll() {
		return data;
	}
	
	@Override
	public T[] getArray(final Event e) {
		return getArray();
	}
	
	@Override
	public T getSingle() {
		if (getAnd() && data.length > 1)
			throw new SkriptAPIException("Call to getSingle on a non-single expression");
		return Utils.random(data);
	}
	
	@Override
	public T getSingle(final Event e) {
		return getSingle();
	}
	
	@Override
	public boolean check(final Event e, final Checker<? super T> c) {
		return SimpleExpression.check(data, c, false, getAnd());
	}
	
	@Override
	public boolean check(final Event e, final Checker<? super T> c, final Condition cond) {
		return SimpleExpression.check(data, c, cond.isNegated(), getAnd());
	}
	
}
