package com.attila.horvath.config;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

public class Config {

	// Static values
	public static final float WIDTH = Gdx.graphics.getWidth();
	public static final float HEIGHT = Gdx.graphics.getHeight();
	public static final int BWIDTH = 170;
	public static final int BHEIGHT = 70;
	public static final int CWIDTH = 150;
	public static final int CHEIGHT = 60;
	
	public static final float MOVE = (float)Math.sqrt(12*12*2);
	public static final float STEP = 12.1f;
	
	public static final int X = 3;
	public static final int Y = 3;
	public static final int Z = 3;
	
	public static final int WXE = 5;
	public static final int WYE = 18;
	public static final int WZE = 5;
	
	public static final int WXM = 7;
	public static final int WYM = 18;
	public static final int WZM = 7;
	
	public static final int WXH = 9;
	public static final int WYH = 18;
	public static final int WZH = 9;

	public static final Color colors[] = {Color.CYAN, Color.RED, Color.GREEN, Color.ORANGE, Color.YELLOW,
		Color.CYAN, Color.RED, Color.GREEN, Color.ORANGE, Color.YELLOW};
}
