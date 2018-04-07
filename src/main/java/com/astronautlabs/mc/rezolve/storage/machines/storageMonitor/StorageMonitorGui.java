package com.astronautlabs.mc.rezolve.storage.machines.storageMonitor;

import com.astronautlabs.mc.rezolve.common.BuildableContainer;
import com.astronautlabs.mc.rezolve.common.ContainerBase;
import com.astronautlabs.mc.rezolve.storage.gui.IStorageViewContainer;
import com.astronautlabs.mc.rezolve.storage.gui.StorageView;
import com.astronautlabs.mc.rezolve.machines.MachineEntity;
import com.astronautlabs.mc.rezolve.machines.MachineGui;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.util.EnumFacing;

public class StorageMonitorGui extends MachineGui<StorageMonitorEntity> implements IStorageViewContainer {
	public static ContainerBase<?> createContainerFor(EntityPlayer player, MachineEntity entity) {
		StorageMonitorEntity storageShellEntity = (StorageMonitorEntity)entity;

		return BuildableContainer
			.withEntity(entity)
			.slotSize(18)
			.addSlotGrid(0, 7, 131, 3, 3)
			//.addOutputSlot(9, 64, 149)
			.addSlot(new SlotCrafting(player, storageShellEntity.getCraftMatrix(), storageShellEntity.getCraftResult(), 0, 64, 149))
			.addPlayerSlots(player.inventory, 88, 131)
			.build();
	}

	@Override
	public StorageView getStorageView() {
		return storageView;
	}

	public int SEARCH_FIELD = 1;

	@Override
	public void setup() {
		this.guiBackgroundResource = "rezolve:textures/gui/container/storage_monitor_gui.png";
		this.xSize = 255;
		this.ySize = 212;
	}

	@Override
	public void initGui() {
		super.initGui();

		this.searchField = new GuiTextField(SEARCH_FIELD, this.fontRendererObj, this.guiLeft + 7, this.guiTop + 5, 240, 13);
		this.searchField.setVisible(true);
		this.searchField.setText("");
		this.searchField.setEnableBackgroundDrawing(true);
		this.searchField.setFocused(true);
		//this.searchField.setTextColor(0x000000);
		this.addControl(this.searchField);

		this.storageView = new StorageView(this, this.guiLeft + 7, this.guiTop + 20, 240, 90);
		this.addControl(this.storageView);
	}

	private GuiTextField searchField;
	private StorageView storageView;

	@Override
	protected void render(int mouseX, int mouseY) {

		//String s = this.entity.getDisplayName().getUnformattedText();
		//this.fontRendererObj.drawString(s, 88 - this.fontRendererObj.getStringWidth(s) / 2, 6, 4210752);            //#404040
		//this.fontRendererObj.drawString(this.playerInv.getDisplayName().getUnformattedText(), 8, 72, 4210752);      //#404040

		int rfBarX = 250;
		int rfBarY = 3;
		int rfBarHeight = 111;
		int rfBarWidth = 3;

		int usedHeight = (int)(this.entity.getEnergyStored(EnumFacing.DOWN) / (double)this.entity.getMaxEnergyStored(EnumFacing.DOWN) * rfBarHeight);
		Gui.drawRect(rfBarX, rfBarY, rfBarX + rfBarWidth, rfBarY + rfBarHeight - usedHeight, 0xFF000000);


		this.storageView.setQuery(this.searchField.getText());

	}
}