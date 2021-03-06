package com.astronautlabs.mc.rezolve.bundler;

import com.astronautlabs.mc.rezolve.bundleBuilder.BundlePatternSlot;
import com.astronautlabs.mc.rezolve.common.ContainerBase;
import com.astronautlabs.mc.rezolve.common.MachineOutputSlot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class BundlerContainer extends ContainerBase<BundlerEntity> {
	
	public BundlerContainer(IInventory playerInv, BundlerEntity te) {
		super(te);
		int invSlotSize = 18;

		// Input item slots (0-8)
		int inputItemsOffsetX = 20;
		int inputItemsOffsetY = 45;
		int inputItemsWidth = 3;
		int inputItemsHeight = 3;
		int firstInputItemSlot = 0;
		
	    for (int y = 0; y < inputItemsHeight; ++y) {
	        for (int x = 0; x < inputItemsWidth; ++x) {
	            this.addSlotToContainer(new Slot(te, firstInputItemSlot + x + y * inputItemsWidth, inputItemsOffsetX + x * invSlotSize, inputItemsOffsetY + y * invSlotSize));
	        }
	    }
	    
	    // Pattern slots 9-17
		
		int patternsOffsetX = 81;
		int patternsOffsetY = 45;
		int patternsWidth = 3;
		int patternsHeight = 3;
		int firstPatternSlot = 9;
		
	    for (int y = 0; y < patternsHeight; ++y) {
	        for (int x = 0; x < patternsWidth; ++x) {
	            this.addSlotToContainer(new BundlePatternSlot(te, firstPatternSlot + x + y * patternsWidth, patternsOffsetX + x * invSlotSize, patternsOffsetY + y * invSlotSize));
	        }
	    }
	    
	    // Output slots 18-26
		
		int invOffsetX = 165;
		int invOffsetY = 45;
		int invWidth = 3;
		int invHeight = 3;
		int firstInvSlot = 18;
		
	    for (int y = 0; y < invHeight; ++y) {
	        for (int x = 0; x < invWidth; ++x) {
	            this.addSlotToContainer(new MachineOutputSlot(te, firstInvSlot + x + y * invWidth, invOffsetX + x * invSlotSize, invOffsetY + y * invSlotSize));
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
