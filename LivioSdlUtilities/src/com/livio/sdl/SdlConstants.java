package com.livio.sdl;

public final class SdlConstants {

	private SdlConstants() {}
	
	public static class AddCommand{
		public static int INVALID_PARENT_ID = -1;
		public static int ROOT_PARENT_ID = 0;
		public static int DEFAULT_POSITION = 0;
		public static int MINIMUM_COMMAND_ID = 0;
		public static int MAXIMUM_COMMAND_ID = 65535; // TODO - is this true or is there another limit?
	}
	
	public static class AddSubmenu{
		public static int DEFAULT_POSITION = 0;
	}
	
	public static class InteractionChoiceSet{
		public static int MINIMUM_CHOICE_SET_ID = 0;
		public static int MAXIMUM_CHOICE_SET_ID = 65535; // TODO - is this true or is there another limit?
	}

}
