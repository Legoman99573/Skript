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

package ch.njol.skript.entity;

import java.util.Arrays;

import org.bukkit.entity.Enderman;

import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.ItemType;
import ch.njol.skript.util.Utils;
import ch.njol.util.Checker;
import ch.njol.util.StringUtils;

/**
 * @author Peter Güttinger
 */
public class EndermanData extends EntityData<Enderman> {
	private static final long serialVersionUID = -6712988549478058004L;
	
	static {
		EntityData.register(EndermanData.class, "enderman", Enderman.class, "enderm(a|e)n [(carrying|holding) %-itemtypes%]");
	}
	
	private ItemType[] hand = null;
	
	private boolean plural;
	
	@SuppressWarnings("unchecked")
	@Override
	protected boolean init(final Literal<?>[] exprs, final int matchedPattern, final ParseResult parseResult) {
		if (exprs[0] != null)
			hand = ((Literal<ItemType>) exprs[0]).getAll();
		plural = StringUtils.startsWithIgnoreCase(parseResult.expr, "endermen");
		return true;
	}
	
	@Override
	public void set(final Enderman entity) {
		if (hand != null)
			entity.setCarriedMaterial(Utils.random(hand).getBlock().getRandom().getData());
	}
	
	@Override
	public boolean match(final Enderman entity) {
		return hand == null || SimpleExpression.check(hand, new Checker<ItemType>() {
			@Override
			public boolean check(final ItemType t) {
				return t.isOfType(entity.getCarriedMaterial().getItemTypeId(), entity.getCarriedMaterial().getData());
			}
		}, false, false);
	}
	
	@Override
	public Class<Enderman> getType() {
		return Enderman.class;
	}
	
	@Override
	public String toString() {
		return "enderman carrying " + Classes.toString(hand, false);
	}
	
	@Override
	public boolean isPlural() {
		return plural;
	}
	
	@Override
	public int hashCode() {
		return Arrays.hashCode(hand);
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof EndermanData))
			return false;
		final EndermanData other = (EndermanData) obj;
		return Arrays.equals(hand, other.hand);
	}
	
	@Override
	public String serialize() {
		if (hand == null)
			return "";
		final StringBuilder b = new StringBuilder();
		for (final ItemType h : hand) {
			final String[] s = Classes.serialize(h);
			if (s == null)
				return null;
			if (b.length() != 0)
				b.append(",");
			b.append(s[0]);
			b.append(":");
			b.append(s[1].replace(",", ",,").replace(":", "::"));
		}
		return b.toString();
	}
	
	@Override
	protected boolean deserialize(final String s) {
		if (s.isEmpty())
			return true;
		final String[] split = s.split("(?<!,),(?!,)");
		hand = new ItemType[split.length];
		for (int i = 0; i < hand.length; i++) {
			final String[] t = split[i].split("(?<!:):(?::)");
			if (t.length != 2)
				return false;
			final Object o = Classes.deserialize(t[0], t[1].replace(",,", ",").replace("::", ":"));
			if (o == null || !(o instanceof ItemType))
				return false;
			hand[i] = (ItemType) o;
		}
		return false;
	}
	
}
