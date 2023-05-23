package vazkii.quark.base.module.hint;

import java.util.Collection;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.ConfigFlagManager;

public class HintObject {

	private final ConfigFlagManager flagManager;
	private final QuarkModule module;
	private final String flag;
	private boolean negateFlag;
	private final Supplier<Optional<Object>> fieldGetter;
	
	private String key;

	public HintObject(ConfigFlagManager flagManager, QuarkModule module, Hint hint, Supplier<Optional<Object>> fieldGetter) {
		this.flagManager = flagManager;
		this.module = module;
		this.flag = hint.value();
		this.negateFlag = hint.negate();
		this.fieldGetter = fieldGetter;

		this.key = hint.key();
	}

	public boolean isEnabled() {
		return module.enabled && (flag.isEmpty() || flagManager.getFlag(flag) != negateFlag);
	}

	public void apply(BiConsumer<Item, Component> consumer) {
		Optional<Object> optional = fieldGetter.get();
		if(optional.isPresent()) {
			Object obj = optional.get();
			if(obj instanceof Collection<?> coll) {
				if(key.isEmpty())
					throw new RuntimeException("Multi-item Hints need a defined key (Module " + module.displayName + ")");
				
				for(Object inObj : coll)
					if(inObj instanceof ItemLike itemLike)
						applyTo(consumer, itemLike);
			}

			else if(obj instanceof ItemLike itemLike)
				applyTo(consumer, itemLike);


			else throw new RuntimeException("Invalid hint object on Module " + module.displayName + " - " + obj);
		}
	}

	private void applyTo(BiConsumer<Item, Component> consumer, ItemLike itemLike) {
		if(key.isEmpty())
			HintManager.hintItem(consumer, itemLike);
		else 
			HintManager.hintItem(consumer, itemLike, key);
	}

}
