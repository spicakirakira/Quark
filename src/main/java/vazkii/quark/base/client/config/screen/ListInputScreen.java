package vazkii.quark.base.client.config.screen;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import vazkii.quark.base.client.config.obj.ListObject;
import vazkii.quark.base.client.config.screen.widgets.ScrollableWidgetList;
import vazkii.quark.base.client.config.screen.widgets.StringElementList;

public class ListInputScreen extends AbstractScrollingWidgetScreen {

	private final ListObject object;
	public final List<String> initialList;

	public List<String> list;

	public ListInputScreen(Screen parent, ListObject object) {
		super(parent);
		this.object = object;

		initialList = new ArrayList<>(object.getCurrentObj());
		list = new ArrayList<>(initialList);
	}

	@Override
	public void render(@Nonnull PoseStack mstack, int mouseX, int mouseY, float partialTicks) {
		super.render(mstack, mouseX, mouseY, partialTicks);

		drawCenteredString(mstack, font, Component.literal(object.getGuiDisplayName()).withStyle(ChatFormatting.BOLD), width / 2, 20, 0xFFFFFF);
	}

	public void addNew() {
		list.add("");
		refresh();
	}

	public void remove(int index) {
		if (list.size() > index)
			list.remove(index);
		refresh();
	}

	@Override
	protected ScrollableWidgetList<?, ?> createWidgetList() {
		return new StringElementList(this);
	}

	@Override
	protected void onClickDone(Button b) {
		object.setCurrentObj(list);
		super.onClickDone(b);
	}

	@Override
	protected void onClickDefault(Button b) {
		list = new ArrayList<>(object.getCurrentObj());
		refresh();
	}

	@Override
	protected void onClickDiscard(Button b) {
		list = new ArrayList<>(object.defaultObj);
		refresh();
	}

	@Override
	protected boolean isDirty() {
		if(initialList.size() != list.size())
			return true;

		for(int i = 0; i < list.size(); i++) {
			String a = list.get(i);
			String b = initialList.get(i);
			if(!a.equals(b))
				return true;
		}

		return false;
	}

}
