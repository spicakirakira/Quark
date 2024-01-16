package vazkii.quark.base.client.config.obj;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import vazkii.quark.api.config.IConfigElement;
import vazkii.quark.base.client.config.ConfigCategory;
import vazkii.quark.base.client.config.ConfigObject;
import vazkii.quark.base.client.config.screen.CategoryScreen;
import vazkii.quark.base.client.config.screen.WidgetWrapper;
import vazkii.quark.base.client.config.screen.widgets.PencilButton;

public class ListObject extends ConfigObject<List<String>> {

	public ListObject(ConfigValue<List<String>> value, String comment, List<String> defaultObj, Supplier<List<String>> objGetter, Predicate<Object> restriction, ConfigCategory parent) {
		super(value, comment, defaultObj, objGetter, restriction, parent);
	}

	@Override
	public void addWidgets(CategoryScreen parent, IConfigElement element, List<WidgetWrapper> widgets) {
		widgets.add(new WidgetWrapper(new PencilButton(230, 3, parent.listInput(this))));
	}
	
	@Override
	protected String computeObjectString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		
		boolean first = true;
		for(String obj : currentObj) {
			if(!first)
				builder.append(", ");
			
			builder.append("\"");
			builder.append(obj.replaceAll("\"", "\\\""));
			builder.append("\"");
			
			first = false;
		}
		
		builder.append("]");
		return builder.toString();
	}

}
