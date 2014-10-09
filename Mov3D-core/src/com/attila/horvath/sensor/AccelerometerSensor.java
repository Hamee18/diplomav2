package com.attila.horvath.sensor;

import com.badlogic.gdx.Gdx;

public class AccelerometerSensor{
	
	private float x, y, z;
	
	public AccelerometerSensor() {
		x = Gdx.input.getAccelerometerX();
		y = Gdx.input.getAccelerometerY();
		z = Gdx.input.getAccelerometerZ();
		
		
	}

	public String toString() {
		return "X: " + x + "\nY: " + y + "\nZ: " + z;  
	}
}
