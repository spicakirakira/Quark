package vazkii.quark.base.util;

import net.minecraft.world.level.material.MaterialColor;

public enum CorundumColor {
	RED("red", 0xff0000, MaterialColor.COLOR_RED),
	ORANGE("orange", 0xff8000, MaterialColor.COLOR_ORANGE),
	YELLOW("yellow", 0xffff00, MaterialColor.COLOR_YELLOW),
	GREEN("green", 0x00ff00, MaterialColor.COLOR_GREEN),
	BLUE("blue", 0x00ffff, MaterialColor.COLOR_LIGHT_BLUE),
	INDIGO("indigo", 0x0000ff, MaterialColor.COLOR_BLUE),
	VIOLET("violet", 0xff00ff, MaterialColor.COLOR_MAGENTA),
	WHITE("white", 0xffffff, MaterialColor.SNOW),
	BLACK("black", 0x000000, MaterialColor.COLOR_BLACK);

	public final String name;
	public final int beaconColor;
	public final MaterialColor materialColor;

	CorundumColor(String name, int beaconColor, MaterialColor materialColor) {
		this.name = name;
		this.beaconColor = beaconColor;
		this.materialColor = materialColor;
	}
}
