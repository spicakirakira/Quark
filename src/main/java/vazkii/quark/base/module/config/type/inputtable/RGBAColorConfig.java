package vazkii.quark.base.module.config.type.inputtable;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.quark.base.module.config.Config;

import java.util.Objects;

public class RGBAColorConfig extends RGBColorConfig {

	@Config public double a;

	protected double da;

	private RGBAColorConfig(double r, double g, double b, double a) {
		super(r, g, b, a);
		this.a = a;
	}

	public static RGBAColorConfig forColor(double r, double g, double b, double a) {
		RGBAColorConfig config = new RGBAColorConfig(r, g, b, a);
		config.color = config.calculateColor();
		config.dr = r;
		config.dg = g;
		config.db = b;
		config.da = a;

		return config;
	}

	@Override
	public double getAlphaComponent() {
		return a;
	}

	@Override
	void setAlphaComponent(double c) {
		a = c;
	}

	@Override
	public void inherit(RGBColorConfig other, boolean committing) {
		if(other instanceof RGBAColorConfig rgba) {
			a = rgba.a;

			if(!committing)
				da = rgba.a;
		}

		super.inherit(other, committing);
	}

	@Override
	public void inheritDefaults(RGBColorConfig target) {
		a = (target instanceof RGBAColorConfig rgba) ? rgba.da : 1F;
		super.inheritDefaults(target);
	}

	@Override
	public RGBAColorConfig copy() {
		RGBAColorConfig newMatrix = new RGBAColorConfig(r, g, b, a);
		newMatrix.inherit(this, false);
		return newMatrix;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		RGBAColorConfig that = (RGBAColorConfig) o;
		return Double.compare(that.r, r) == 0 && Double.compare(that.g, g) == 0 && Double.compare(that.b, b) == 0 && Double.compare(that.a, a) == 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(r, g, b, a);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public String getSubtitle() {
		return String.format("[%.1f, %.1f, %.1f, %.1f]", r, g, b, a);
	}

}
