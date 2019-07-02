package com.mraof.minestuck.modSupport;

import com.mraof.minestuck.alchemy.CombinationRegistry;
import com.mraof.minestuck.alchemy.GristRegistry;
import com.mraof.minestuck.alchemy.GristSet;
import com.mraof.minestuck.alchemy.GristType;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.event.FMLInterModComms;

public class TinkersConstructSupport extends ModSupport
{
	
	@Override
	public void registerRecipes() throws Exception
	{
		Block oreBush1 = ((Block) Class.forName("tconstruct.world.TinkerWorld").getField("oreBerry").get(null));
		Block oreBush2 = ((Block) Class.forName("tconstruct.world.TinkerWorld").getField("oreBerrySecond").get(null));
		
		String[] items1 = {"ingotIron", "ingotGold", "ingotCopper", "ingotTin"};
		
		GristRegistry.addGristConversion(new ItemStack(oreBush1, 1, 0), new GristSet(new GristType[]{GristType.Rust, GristType.Build, GristType.Amber}, new int[]{16, 1, 1}));
		GristRegistry.addGristConversion(new ItemStack(oreBush1, 1, 1), new GristSet(new GristType[]{GristType.Gold, GristType.Build, GristType.Amber}, new int[]{16, 1, 1}));
		GristRegistry.addGristConversion(new ItemStack(oreBush1, 1, 2), new GristSet(new GristType[]{GristType.Rust, GristType.Cobalt, GristType.Build, GristType.Amber}, new int[]{16, 3, 1, 1}));
		GristRegistry.addGristConversion(new ItemStack(oreBush1, 1, 3), new GristSet(new GristType[]{GristType.Rust, GristType.Caulk, GristType.Build, GristType.Amber}, new int[]{16, 8, 1, 1}));
		GristRegistry.addGristConversion(new ItemStack(oreBush2, 1, 0), new GristSet(new GristType[]{GristType.Rust, GristType.Chalk, GristType.Build, GristType.Amber}, new int[]{16, 6, 1, 1}));
		GristRegistry.addGristConversion(new ItemStack(oreBush2, 1, 1), new GristSet(new GristType[]{GristType.Uranium, GristType.Quartz, GristType.Diamond, GristType.Ruby, GristType.Build, GristType.Amber}, new int[]{8, 1, 4, 4, 1, 1}));
		
		for(int i = 0; i < items1.length; i++)
			CombinationRegistry.addCombination("treeLeaves", items1[i], CombinationRegistry.Mode.MODE_AND, new ItemStack(oreBush1, 1, i));
		
		CombinationRegistry.addCombination("treeLeaves", "ingotAluminium", CombinationRegistry.Mode.MODE_AND, new ItemStack(oreBush2, 1, 0));
		CombinationRegistry.addCombination("treeLeaves", Items.EXPERIENCE_BOTTLE, 0, CombinationRegistry.Mode.MODE_AND, new ItemStack(oreBush2, 1, 1));
	}
	
	@Override
	public void interModComms() throws Exception
	{
		// Tell TiCon that there is a gemUranium that can be cast
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("gem", "Uranium");
		FMLInterModComms.sendMessage("tconstruct", "integrateSmeltery", tag);
	}
	
}
