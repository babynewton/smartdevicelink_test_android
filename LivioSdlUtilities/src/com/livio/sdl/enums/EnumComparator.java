package com.livio.sdl.enums;

import java.util.Comparator;

/**
 * This class is simply a comparator for enum values using their toString()
 * method instead of the standard name() method.
 * 
 * This allows you to sort your enums by their friendly names, which is much
 * more useful than sorting by the enum name.
 * 
 * @author Mike Burke
 *
 */
public class EnumComparator<E extends Enum<E>> implements Comparator<E> {
	@Override
	public int compare(E lhs, E rhs) {
		return lhs.toString().compareTo(rhs.toString());
	}
}
