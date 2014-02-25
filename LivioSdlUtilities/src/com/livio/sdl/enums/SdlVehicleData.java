package com.livio.sdl.enums;

public enum SdlVehicleData {

	GPS("GPS Data"),
	SPEED("Speed"),
	RPM("Engine RPM"),
	FUEL_LEVEL("Fuel Level"),
	FUEL_LEVEL_STATE("Fuel Level State"),
	FUEL_ECONOMY_INST("Fuel Economy (instant)"),
	EXTERNAL_TEMP("External Temperature"),
	VEHICLE_GEAR("Vehicle Gear State"),
	TIRE_PRESSURE("Tire Pressure"),
	ODOMETER("Odometer"),
	SEAT_BELT_STATUS("Seat Belt Status"),
	BODY_INFO("Body Information"),
	DEVICE_STATUS("Device Status"),
	DRIVER_BRAKING("Brake Status"),
	WIPER_STATUS("Windshield Wiper Status"),
	FUEL_ECONOMY_OVR("Fuel Economy (overall)"),
	ENGINE_OIL_LIFE("Engine Oil Life"),
	HEADLIGHT_STATUS("Headlight Status"),
	BATTERY_VOLTAGE("Battery Voltage"),
	BRAKE_TORQUE("Brake Torque"),
	ENGINE_TORQUE("Engine Torque"),
	TURBO_BOOST("Turbo Boost"),
	COOLANT_TEMP("Coolant Temp"),
	AIR_FUEL_RATIO("Air-Fuel Ratio"),
	COOLING_HEAD_TEMP("Cooling Head Temp"),
	ENGINE_OIL_TEMP("Engine Oil Temp"),
	AIR_INTAKE_TEMP("Air Intake Temp"),
	GEAR_SHIFT_ADVICE("Gear Shift Advice"),
	ACCELERATION("Acceleration"),
	ACC_PEDAL_POSITION("Acceleration Pedal Position"),
	CLUTCH_PEDAL_POSITION("Clutch Pedal Position"),
	REVERSE_GEAR_STATUS("Reverse Gear Status"),
	ACCELERATION_TORQUE("Acceleration Torque"),
	EV_INFO("EV Info"),
	AMBIENT_LIGHT_STATUS("Ambient Light Status"),
	
	
	;
	
	private final String friendlyName;
	private SdlVehicleData(String friendlyName) {
		this.friendlyName = friendlyName;
	}
	
	@Override
	public String toString(){
		return this.friendlyName;
	}

}
