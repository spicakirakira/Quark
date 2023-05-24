package vazkii.quark.addons.oddities.module;

import java.util.function.BiConsumer;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.extensions.IForgeMenuType;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.addons.oddities.block.CrateBlock;
import vazkii.quark.addons.oddities.block.be.CrateBlockEntity;
import vazkii.quark.addons.oddities.client.screen.CrateScreen;
import vazkii.quark.addons.oddities.inventory.CrateMenu;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;

@LoadModule(category = ModuleCategory.ODDITIES)
public class CrateModule extends QuarkModule {

	public static BlockEntityType<CrateBlockEntity> blockEntityType;
	public static MenuType<CrateMenu> menuType;
	
	public static Block crate;
	
	@Config public static int maxItems = 640;

	@Override
	public void register() {
		crate = new CrateBlock(this);
		
		menuType = IForgeMenuType.create(CrateMenu::fromNetwork);
		RegistryHelper.register(menuType, "crate", Registry.MENU_REGISTRY);
		
		blockEntityType = BlockEntityType.Builder.of(CrateBlockEntity::new, crate).build(null);
		RegistryHelper.register(blockEntityType, "crate", Registry.BLOCK_ENTITY_TYPE_REGISTRY);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientSetup() {
		MenuScreens.register(menuType, CrateScreen::new);
	}
	
	@Override
	public void addAdditionalHints(BiConsumer<Item, Component> consumer) {
		Component comp = Component.translatable("quark.jei.hint.crate", maxItems);
		consumer.accept(crate.asItem(), comp);
	}
	
}
