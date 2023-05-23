package vazkii.quark.content.tweaks.module;

import java.util.function.BiConsumer;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;

/**
 * @author WireSegal
 * Created at 8:40 AM on 8/27/19.
 */
@LoadModule(category = ModuleCategory.TWEAKS, hasSubscriptions = true)
public class ArmedArmorStandsModule extends QuarkModule {

	@Override
	public void addAdditionalHints(BiConsumer<Item, Component> consumer) {
		hintItem(consumer, Items.ARMOR_STAND);
	}

	@SubscribeEvent
	public void entityConstruct(EntityEvent.EntityConstructing event) {
		if(event.getEntity() instanceof ArmorStand stand) {
			if(!stand.isShowArms())
				setShowArms(stand, true);
		}
	}

	private void setShowArms(ArmorStand e, boolean showArms) {
		e.getEntityData().set(ArmorStand.DATA_CLIENT_FLAGS, setBit(e.getEntityData().get(ArmorStand.DATA_CLIENT_FLAGS), 4, showArms));
	}

	private byte setBit(byte status, int bitFlag, boolean value) {
		if (value)
			status = (byte)(status | bitFlag);
		else
			status = (byte)(status & ~bitFlag);

		return status;
	}
}
