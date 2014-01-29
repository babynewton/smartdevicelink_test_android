package com.livio.sdl.enums;

/**
 * Defines a callback for click events that occur on an enumerated type.
 *
 * @author Mike Burke
 *
 */
public interface EnumClickListener {
	<E extends Enum<E>> void OnEnumItemClicked(E selection);
}
