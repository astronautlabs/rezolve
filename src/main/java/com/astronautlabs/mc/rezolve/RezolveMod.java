package com.astronautlabs.mc.rezolve;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.astronautlabs.mc.rezolve.bundleBuilder.BlankBundlePatternItem;
import com.astronautlabs.mc.rezolve.bundleBuilder.BundleBuilderBlock;
import com.astronautlabs.mc.rezolve.bundleBuilder.BundlePatternItem;
import com.astronautlabs.mc.rezolve.bundler.BundlerBlock;
import com.astronautlabs.mc.rezolve.common.BlockBase;
import com.astronautlabs.mc.rezolve.common.GhostSlotUpdateMessageHandler;
import com.astronautlabs.mc.rezolve.common.ItemBase;
import com.astronautlabs.mc.rezolve.common.TileEntityBase;
import com.astronautlabs.mc.rezolve.unbundler.UnbundlerBlock;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = RezolveMod.MODID, version = RezolveMod.VERSION, name = "Rezolve", dependencies = "after:Waila;after:EnderIO")
public class RezolveMod {
	public RezolveMod() {
		_instance = this;
	}

	public static final String MODID = "rezolve";
	public static final String VERSION = "1.0";

	public static BundlerBlock bundlerBlock;
	public static UnbundlerBlock unbundlerBlock;
	public static BundleBuilderBlock bundleBuilderBlock;
	public static ItemBlock bundlerItem;
	public static BundlePatternItem bundlePatternItem;
	public static BundleItem bundleItem;
	public static ArrayList<BundleItem> coloredBundleItems = new ArrayList<BundleItem>();

	public static final Logger logger = LogManager.getLogger(RezolveMod.MODID);
	private static RezolveMod _instance = null;
	
	public static RezolveMod instance() {
		return _instance;
	}

	/**
	 * Offers much better error messages compared to the standard recipe registration method 
	 * while also being a bit less verbose. If a string is passed instead of an Item/Block, it will 
	 * be looked up using the appropriate registry (Item.REGISTRY for a prefix of "item|" and Block.REGISTRY
	 * for a prefix of "block|"). If a null value is encountered, an explanatory exception will be thrown to crash 
	 * the game. Also helpful during development if you aren't sure what the IDs are for a mod's item or block.
	 * 
	 * I suspect this will be great for getting to the root of crash reports from users as well.
	 * 
	 * @param output
	 * @param params
	 */
	public static void addRecipe(ItemStack output, Object... params) {
		
		Character lastChar = null;
		Object[] resolvedParams = new Object[params.length];
		int index = 0;
		
		for (Object param : params) {
			int thisIndex = index;
			resolvedParams[index++] = param;
			
			if (param instanceof String && lastChar == null)
				continue;
			
			if (param instanceof Character) {
				lastChar = (Character)param;
				continue;
			}
			
			if (param == null) {
				if (lastChar != null) {
					throw new RuntimeException(
						"The recipe ingredient labelled '"+lastChar+"' used in '"+output.getItem().getRegistryName()+"' could not be loaded, "
						+ "this indicates that a mod has removed/renamed an item or block " 
						+ "and Rezolve has not been updated to match yet :-(. Please file a bug " 
						+ "and include the versions of Rezolve and the other mod."
					);
				}
			}
			
			if (param instanceof String) {
				Object resolvedParam = null;
				String identifier = (String)param;
				ResourceLocation resloc = null;
				String[] parts = identifier.split("\\|");
				
				if ("item".equals(parts[0]))
					resolvedParam = Item.REGISTRY.getObject(resloc = new ResourceLocation(parts[1]));
				else if ("block".equals(parts[0]))
					resolvedParam = Block.REGISTRY.getObject(resloc = new ResourceLocation(parts[1]));
				else 
					throw new RuntimeException("Invalid recipe identifier: "+identifier);
				
				if (parts.length > 2) {
					// Metadata 
					
					if (resolvedParam instanceof Item)
						resolvedParam = new ItemStack((Item)resolvedParam, 1, Integer.parseInt(parts[2]));
					else if (resolvedParam instanceof Block)
						resolvedParam = new ItemStack((Block)resolvedParam, 1, Integer.parseInt(parts[2]));
					else 
						throw new RuntimeException("Resolved parameter is not a block or item, cannot create an ItemStack from it.");
				}
				
				if (resolvedParam == null) {
					
					System.out.println("Cannot find "+identifier);
					System.out.println("Registered items in mod "+resloc.getResourceDomain()+" are:");
					for (ResourceLocation loc : Item.REGISTRY.getKeys()) {
						if (!loc.getResourceDomain().equals(resloc.getResourceDomain()))
							continue;
						
						System.out.println(" - "+loc.getResourcePath());
					}
					
					System.out.println("Registered blocks in mod "+resloc.getResourceDomain()+" are:");
					for (ResourceLocation loc : Block.REGISTRY.getKeys()) {
						if (!loc.getResourceDomain().equals(resloc.getResourceDomain()))
							continue;
						
						System.out.println(" - "+loc.getResourcePath());
					}
					
					throw new RuntimeException(
						"The recipe ingredient '"+identifier+"' used in '"+output.getItem().getRegistryName()+"' could not be loaded, "
						+ "this indicates that a mod has removed/renamed an item or block " 
						+ "and Rezolve has not been updated to match yet :-(. Please file a bug " 
						+ "and include the versions of Rezolve and the other mod."
					);
				}
				
				resolvedParams[thisIndex] = resolvedParam;
			}
		}
		
		GameRegistry.addRecipe(output, resolvedParams);
	}
	
	public String getColorName(int dye) {
		if (dye < 0 || dye >= DYE_NAMES.length)
			return "";

		return DYE_NAMES[dye];
	}

	public static boolean areStacksSame(ItemStack stackA, ItemStack stackB) {
		if (stackA == stackB)
			return true;
		
		if (stackA == null || stackB == null)
			return false;
		
		return (stackA.isItemEqual(stackB) && ItemStack.areItemStackTagsEqual(stackA, stackB));
	}

	@SidedProxy(clientSide = "com.astronautlabs.mc.rezolve.ClientProxy", serverSide = "com.astronautlabs.mc.rezolve.ServerProxy")
	public static CommonProxy proxy;

	private ArrayList<BlockBase> registeredBlocks = new ArrayList<BlockBase>();

	private void registerBlock(BlockBase block) {
		GameRegistry.register(block);
		this.registeredBlocks.add(block);

		block.init(this);
	}

	public void registerBlockRecipes() {
		this.log("Registering block recipes...");
		for (BlockBase block : this.registeredBlocks) {
			this.log("Registering recipes for: " + block.getRegistryName());
			block.registerRecipes();
		}
	}

	public void registerItemRecipes() {
		this.log("Registering item recipes...");
		for (ItemBase item : this.registeredItems) {
			this.log("Registering recipes for: " + item.getRegistryName());
			item.registerRecipes();
		}
	}

	public void registerBlockRenderers() {
		this.log("Registering block renderers...");
		for (BlockBase block : this.registeredBlocks) {
			this.log("Registering renderer for: " + block.getRegistryName());
			block.registerRenderer();
		}
	}

	public void registerItemRenderers() {
		this.log("Registering item renderers...");
		for (ItemBase item : this.registeredItems) {
			this.log("Registering renderer for: " + item.getRegistryName());
			item.registerRenderer();
		}
	}

	private ArrayList<ItemBase> registeredItems = new ArrayList<ItemBase>();

	public void registerItem(ItemBase item) {
		this.log("Registering item " + item.getRegistryName().toString());
		this.registeredItems.add(item);
		GameRegistry.register(item);
	}

	public void registerItemBlock(BlockBase block) {
		this.registerBlock(block);

		ItemBlock item = new ItemBlock(block);
		item.setRegistryName(block.getRegistryName());
		GameRegistry.register(item);

		block.itemBlock = item;
	}
	
	public void registerTileEntity(Class<? extends TileEntityBase> entityClass) {
		try {
			Constructor<? extends TileEntityBase> x = entityClass.getConstructor();
			
			TileEntityBase instance = x.newInstance();
			String registryName = instance.getRegistryName();
			
			GameRegistry.registerTileEntity(entityClass, registryName);
			
		} catch (Exception e) {
			 System.err.println("Cannot register tile entity class "+entityClass.getCanonicalName()+": Caught exception");
			 System.err.println(e.toString());
			 return;
		}
	}

	public boolean isDye(Item item) {
		return "minecraft:dye".equals(item.getRegistryName().toString());
	}

	/**
	 * Register blocks!
	 */
	private void registerBlocks() {
		this.log("Registering blocks...");

		this.registerItemBlock(bundlerBlock = new BundlerBlock());
		this.registerItemBlock(unbundlerBlock = new UnbundlerBlock());
		this.registerItemBlock(bundleBuilderBlock = new BundleBuilderBlock());
	}

	/**
	 * Register items!
	 */
	private void registerItems() {
		this.log("Registering items...");

		this.registerItem(bundlePatternItem = new BundlePatternItem());

		this.registerItem(bundleItem = new BundleItem());
		//for (String color : DYES)
		//	this.registerColoredBundleItem(color);
	}
	
	public static final String[] DYES = new String[] { "black", "red", "green", "brown", "blue", "purple", "cyan",
			"light_gray", "gray", "pink", "lime", "yellow", "light_blue", "magenta", "orange", "white" };

	public static final String[] DYE_NAMES = new String[] { "Black", "Red", "Green", "Brown", "Blue", "Purple", "Cyan",
			"Light Gray", "Gray", "Pink", "Lime", "Yellow", "Light Blue", "Magenta", "Orange", "White" };

	private void registerColoredBundleItem(String color) {
		BundleItem item = new BundleItem(color);
		coloredBundleItems.add(item);
		this.registerItem(item);
	}

	RezolveGuiHandler guiHandler;

	public RezolveGuiHandler getGuiHandler() {
		return this.guiHandler;
	}

	@EventHandler
	public void preinit(FMLPreInitializationEvent event) {
		this.guiHandler = new RezolveGuiHandler();
		this.registerBlocks();
		this.registerItems();

		this.registerBlockRecipes();
		this.registerItemRecipes();

		GhostSlotUpdateMessageHandler.register();
		
		proxy.init(this);

		FMLInterModComms.sendMessage("Waila", "register", "com.astronautlabs.mc.rezolve.waila.WailaCompat.load");

	}

	public void log(String message) {
		System.out.println(message);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		this.log("Initialized!");
	}
}
