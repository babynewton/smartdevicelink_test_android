package com.livio.sdl;

public final class SdlConstants {

	private SdlConstants() {}
	
	public static final class AddCommandConstants{
		private AddCommandConstants(){}
		
		public static final int INVALID_PARENT_ID = -1;
		public static final int ROOT_PARENT_ID = 0;
		public static final int DEFAULT_POSITION = 0;
		public static final int MINIMUM_COMMAND_ID = 0x00;
		public static final int MAXIMUM_COMMAND_ID = 2000000000;
	}
	
	public static final class AddSubmenuConstants{
		private AddSubmenuConstants(){}
		
		public static final int DEFAULT_POSITION = 0;
	}
	
	public static final class InteractionChoiceSetConstants{
		private InteractionChoiceSetConstants(){}
		
		public static final int MINIMUM_CHOICE_SET_ID = 0x00;
		public static final int MAXIMUM_CHOICE_SET_ID = 2000000000;
	}
	
	public static final class GetDtcsConstants{
		private GetDtcsConstants(){}
		
		public static final int MINIMUM_ECU_ID = 0x00;
		public static final int MAXIMUM_ECU_ID = 0xFFFF;
	}
	
	public static final class PerformInteractionConstants{
		private PerformInteractionConstants(){}
		
		public static final int MINIMUM_TIMEOUT = 5; // seconds
		public static final int MAXIMUM_TIMEOUT = 100; // seconds
		public static final int INVALID_TIMEOUT = -1; 
		
		public static final int EXPECTED_REPSONSE_TIME_OFFSET = 2000; // ms, or 2 s
	}
	
	public static final class ReadDidsConstants{
		private ReadDidsConstants(){}
		
		public static final int MINIMUM_ECU_ID = 0x00;
		public static final int MAXIMUM_ECU_ID = 0xFFFF;
		
		public static final int MINIMUM_DID_LOCATION = 0x00;
		public static final int MAXIMUM_DID_LOCATION = 0xFFFF;
	}
	
	public static final class ScrollableMessageConstants{
		private ScrollableMessageConstants(){}
		
		public static final int TIMEOUT_MINIMUM = 1; // seconds
		public static final int TIMEOUT_MAXIMUM = 65; // seconds
		
		public static final int MESSAGE_LENGTH_MAX = 500; // characters
		
		public static final int EXPECTED_REPSONSE_TIME_OFFSET = 2000; // ms, or 2 s
	}
	
	public static final class AlertConstants{
		private AlertConstants(){}
		
		public static final int ALERT_TIME_MINIMUM = 3; // seconds
		public static final int ALERT_TIME_MAXIMUM = 10; // seconds
		
		public static final int EXPECTED_REPSONSE_TIME_OFFSET = 2000; // ms, or 2 s
	}
	
	public static final class SetMediaClockTimerConstants{
		private SetMediaClockTimerConstants(){}

		public static final int HOURS_MINIMUM = 0; // hours
		public static final int HOURS_MAXIMUM = 59; // hours
		public static final int MINUTES_MINIMUM = 0; // minutes
		public static final int MINUTES_MAXIMUM = 59; // minutes
		public static final int SECONDS_MINIMUM = 0; // seconds
		public static final int SECONDS_MAXIMUM = 59; // seconds
	}
	
	public static final class SliderConstants{
		private SliderConstants(){}
		
		public static final int NUM_OF_TICKS_MIN = 2; // ticks
		public static final int NUM_OF_TICKS_MAX = 26; // ticks
		public static final int START_POSITION_MIN = 1;
		public static final int START_POSITION_MAX = NUM_OF_TICKS_MAX;
		public static final int TIMEOUT_MIN = 1; // second
		public static final int TIMEOUT_MAX = 65; // seconds
		
		public static final int EXPECTED_REPSONSE_TIME_OFFSET = 2000; // ms, or 2 s
	}

}
