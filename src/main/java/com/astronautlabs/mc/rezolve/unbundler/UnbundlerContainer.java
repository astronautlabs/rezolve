package com.astronautlabs.mc.rezolve.unbundler;

import com.astronautlabs.mc.rezolve.bundleBuilder.BundlePatternSlot;
import com.astronautlabs.mc.rezolve.bundler.BundleSlot;
import com.astronautlabs.mc.rezolve.common.ContainerBase;
import com.astronautlabs.mc.rezolve.common.MachineOutputSlot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class UnbundlerContainer extends ContainerBase<UnbundlerEntity> {
	
	public UnbundlerContainer(IInventory playerInv, UnbundlerEntity te) {

		super(te);
		
		this.entity = te;
		int invSlotSize = 18;

		// Input bundles slots (0-8)
		int inputItemsOffsetX = 47;
		int inputItemsOffsetY = 55;
		int inputItemsWidth = 3;
		int inputItemsHeight = 3;
		int firstInputItemSlot = 0;
		
	    for (int y = 0; y < inputItemsHeight; ++y) {
	        for (int x = 0; x < inputItemsWidth; ++x) {
	            this.addSlotToContainer(new BundleSlot(te, firstInputItemSlot + x + y * inputItemsWidth, inputItemsOffsetX + x * invSlotSize, inputItemsOffsetY + y * invSlotSize));
	        }
	    }
	    
	    // Output items slots 10-25
		
		int patternsOffsetX = 137;
		int patternsOffsetY = 37;
		int patternsWidth = 4;
		int patternsHeight = 4;
		int firstPatternSlot = 9;
		
	    for (int y = 0; y < patternsHeight; ++y) {
	        for (int x = 0; x < patternsWidth; ++x) {
	            this.addSlotToContainer(new MachineOutputSlot(te, firstPatternSlot + x + y * patternsWidth, patternsOffsetX + x * invSlotSize, patternsOffsetY + y * invSlotSize));
	        }
	    }
	    
		int playerInvOffsetX = 47;
		int playerInvOffsetY = 131;
		
	    // Player Inventory, slots 9-35
		
	    for (int y = 0; y < 3; ++y) {
	        for (int x = 0; x < 9; ++x) {
	            this.addSlotToContainer(new Slot(playerInv, 9 + x + y * 9, playerInvOffsetX + x * invSlotSize, playerInvOffsetY + y * invSlotSize));
	        }
	    }

	    int playerHotbarOffsetX = 47;
	    int playerHotbarOffsetY = 189;
	    
	    // Player Hotbar, slots 0-8
	    
	    for (int x = 0; x < 9; ++x) {
	        this.addSlotToContainer(new Slot(playerInv, x, playerHotbarOffsetX + x * 18, playerHotbarOffsetY));
	    }
	}
}
