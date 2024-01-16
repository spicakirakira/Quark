package vazkii.quark.api.config;

import java.util.Map;
import java.util.function.Consumer;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IExternalCategory extends IConfigCategory {

	void commit();
	Map<String, IConfigCategory> getTopLevelCategories();

	IExternalCategory addTopLevelCategory(String name, Consumer<IExternalCategory> onChangedCallback);

}
