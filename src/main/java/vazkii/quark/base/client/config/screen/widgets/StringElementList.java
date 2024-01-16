package vazkii.quark.base.client.config.screen.widgets;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import vazkii.quark.base.client.config.screen.ListInputScreen;
import vazkii.quark.base.client.config.screen.WidgetWrapper;

public class StringElementList extends ScrollableWidgetList<ListInputScreen, StringElementList.Entry>{

	public StringElementList(ListInputScreen parent) {
		super(parent);
	}

	@Override
	protected void findEntries() {
		int i = 0;
		for(String s : parent.list) {
			addEntry(new Entry(parent, s, i));
			i++;
		}

		addEntry(new Entry(parent, null, 0));
	}

	public static final class Entry extends ScrollableWidgetList.Entry<Entry> {

		public final String initialString;

		public String string;

		public Entry(ListInputScreen parent, String s, int index) {
			initialString = string = s;

			if(s != null) {
				Minecraft mc = Minecraft.getInstance();
				EditBox field = new EditBox(mc.font, 10, 3, 210, 20, Component.literal(""));
				field.setMaxLength(256);
				field.setValue(initialString);
				field.moveCursorTo(0);
				field.setResponder(str -> {
					if(parent.list.isEmpty())
						parent.list.add(str);
					else 
						parent.list.set(index, str);
				});
				children.add(new WidgetWrapper(field));

				children.add(new WidgetWrapper(new Button(230, 3, 20, 20, Component.literal("-").withStyle(ChatFormatting.RED), b -> parent.remove(index))));
			} else {
				children.add(new WidgetWrapper(new Button(10, 3, 20, 20, Component.literal("+").withStyle(ChatFormatting.GREEN), b -> parent.addNew())));
			}
		}

		@Override
		public void render(@Nonnull PoseStack mstack, int index, int rowTop, int rowLeft, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean hovered, float partialTicks) {
			super.render(mstack, index, rowTop, rowLeft, rowWidth, rowHeight, mouseX, mouseY, hovered, partialTicks);

			if(initialString != null)
				drawBackground(mstack, index, rowTop, rowLeft, rowWidth, rowHeight, mouseX, mouseY, hovered);
		}

		@Nonnull
		@Override
		public Component getNarration() {
			return Component.literal(string);
		}

	}


}
