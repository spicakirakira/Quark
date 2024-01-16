package vazkii.quark.base.client.config.screen;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public abstract class AbstractInputScreen<T> extends AbstractQScreen {

	protected Button resetButton, doneButton;
	protected boolean errored = false;

	protected T val;

	public AbstractInputScreen(Screen parent) {
		super(parent);
	}

	@Override
	public void render(@Nonnull PoseStack mstack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(mstack);

		super.render(mstack, mouseX, mouseY, partialTicks);
	}

	@Override
	protected final void init() {
		super.init();

		int pad = 3;
		int bWidth = 121;
		int left = (width - (bWidth + pad) * 3) / 2;
		int vStart = height - 30;

		addRenderableWidget(new Button(left, vStart, bWidth, 20, Component.translatable("quark.gui.config.default"), this::setDefault));
		addRenderableWidget(resetButton = new Button(left + bWidth + pad, vStart, bWidth, 20, Component.translatable("quark.gui.config.discard"), this::reset));
		addRenderableWidget(doneButton = new Button(left + (bWidth + pad) * 2, vStart, bWidth, 20, Component.translatable("gui.done"), this::save));

		onInit();
		update();
	}

	protected abstract void onInit();
	protected abstract T compute();
	protected abstract void setDefault();
	protected abstract void reset();
	protected abstract boolean isErrored();
	protected abstract boolean isDirty();
	protected abstract void commit();

	@Override
	public boolean keyPressed(int key, int mouseX, int mouseY) {
		switch(key) {
		case 256: // esc
			reset(null);
			return true;
		case 257: // enter
			if(!errored) {
				save(null);
				return true;
			}
		}

		return super.keyPressed(key, mouseX, mouseY);
	}

	protected final void setDefault(Button button) {
		setDefault();
		update();
		save(button);
	}

	protected final void reset(Button button) {
		reset();
		update();
		save(button);
	}

	protected final void save(Button button) {
		if(!errored) {
			commit();
			returnToParent(button);
		}
	}

	protected void update() {
		val = compute();
		errored = val == null || isErrored();

		resetButton.active = errored || isDirty();
		doneButton.active = !errored;
	}

}
