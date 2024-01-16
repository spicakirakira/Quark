package vazkii.quark.base.client.config.screen;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import vazkii.quark.base.client.config.obj.AbstractStringInputObject;

public class StringInputScreen<T> extends AbstractInputScreen<T> {

	private final AbstractStringInputObject<T> object;

	private EditBox input;

	public StringInputScreen(Screen parent, AbstractStringInputObject<T> object) {
		super(parent);
		this.object = object;
	}

	@Override
	public void render(@Nonnull PoseStack mstack, int mouseX, int mouseY, float partialTicks) {
		super.render(mstack, mouseX, mouseY, partialTicks);

		drawCenteredString(mstack, font, Component.literal(object.getGuiDisplayName()).withStyle(ChatFormatting.BOLD), width / 2, 20, 0xFFFFFF);
		drawCenteredString(mstack, font, I18n.get("quark.gui.config.defaultvalue", object.defaultObj), width / 2, 30, 0xFFFFFF);

		input.render(mstack, mouseX, mouseY, partialTicks);
	}

	@Override
	protected void onInit() {
		input = new EditBox(font, width / 2 - 100, 60, 200, 20, Component.literal(""));
		input.setFilter(object::isStringValid);
		input.setMaxLength(object.getMaxStringLength());
		input.setResponder(this::update);
		setInitialFocus(input);
		addWidget(input);
	}

	@Override
	public void tick() {
		input.tick();
	}

	@Override
	public boolean mouseClicked(double x, double y, int button) {
		if(input.mouseClicked(x, y, button))
			return true;

		return super.mouseClicked(x, y, button);
	}

	@Override
	protected void setDefault() {
		object.reset(true);
	}

	@Override
	protected void reset() {
		object.reset(false);
	}

	@Override
	protected void update() {
		input.setValue(object.getCurrentObj().toString());
		super.update();
	}

	private void update(String s) {
		super.update();
		input.setTextColor(errored ? 0xFF0000 : 0xFFFFFF);
	}

	@Override
	protected T compute() {
		return object.fromString(input.getValue());
	}

	@Override
	protected boolean isErrored() {
		return object.restriction != null && !object.restriction.test(val);
	}

	@Override
	protected boolean isDirty() {
		return object.wouldBeDirty(val);
	}

	@Override
	protected void commit() {
		object.setCurrentObj(object.fromString(input.getValue()));
	}

}
