package com.mraof.minestuck.block;

import com.mraof.minestuck.Minestuck;
import com.mraof.minestuck.client.gui.GuiHandler;
import com.mraof.minestuck.tileentity.TileEntityAlchemiter;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class BlockAlchemiter extends BlockLargeMachine
{
	public static final PropertyEnum<EnumParts> PART1 = PropertyEnum.create("part",EnumParts.class, EnumParts.TOTEM_CORNER, EnumParts.TOTEM_PAD, EnumParts.LOWER_ROD, EnumParts.UPPER_ROD);
	public static final PropertyEnum<EnumParts> PART2 = PropertyEnum.create("part",EnumParts.class, EnumParts.EDGE_LEFT, EnumParts.EDGE_RIGHT, EnumParts.CORNER, EnumParts.CENTER_PAD);
	public final PropertyEnum<EnumParts> PART;
	public static final PropertyDirection DIRECTION = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	
	public final int index;
	
	public BlockAlchemiter()
	{
		this(0, PART1);
		
	}
	public BlockAlchemiter(int index, PropertyEnum<EnumParts> property)
	{
		super();
		this.index = index;
		this.PART = property;
		
		setUnlocalizedName("alchemiter");
		setDefaultState(getStateFromMeta(0));
	}
	
	//not sure how to do this.
	//@Override
	//public AxisAlignedBB getBoundingBox(IBlockState state,IBlockAccess source,BlockPos pos){
		
	//}


	@Override
	public boolean onBlockActivated(World worldIn,BlockPos pos,IBlockState state,EntityPlayer playerIn,EnumHand hand,EnumFacing facing,float hitX,float hitY,float hitZ)
	{
		BlockPos mainPos = getMainPos(state, pos, worldIn);
		TileEntity te = worldIn.getTileEntity(mainPos);
		if(!worldIn.isRemote && te != null && te instanceof TileEntityAlchemiter && !((TileEntityAlchemiter)te).isBroken())
		{
			playerIn.openGui(Minestuck.instance, GuiHandler.GuiId.MACHINE.ordinal(), worldIn, mainPos.getX(), mainPos.getY(), mainPos.getZ());	
		}
		return true;
	}
	@Override
	public boolean hasTileEntity(IBlockState state)
	{
		return state.getValue(PART) == EnumParts.TOTEM_CORNER;
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		if(index == 0 && meta % 4 == EnumParts.TOTEM_CORNER.ordinal())
			return new TileEntityAlchemiter();
		return null;
	}
	
	@Override
	public void onBlockPlacedBy(World worldIn,BlockPos pos,IBlockState state,EntityLivingBase placer, ItemStack stack)
	{
		EnumFacing facing = EnumFacing.getHorizontal(MathHelper.floor((double)(placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3);
		if(placer!=null && !(worldIn.isRemote)){
			worldIn.setBlockState(pos.up(0), getBlockState(EnumParts.TOTEM_CORNER, facing));
			worldIn.setBlockState(pos.up(1), getBlockState(EnumParts.TOTEM_PAD, facing));
			worldIn.setBlockState(pos.up(2), getBlockState(EnumParts.LOWER_ROD, facing));
			worldIn.setBlockState(pos.up(3), getBlockState(EnumParts.UPPER_ROD, facing));
			
			worldIn.setBlockState(pos.offset(facing,0).offset(facing.rotateY(),1), getBlockState(EnumParts.EDGE_LEFT, facing));
			worldIn.setBlockState(pos.offset(facing,0).offset(facing.rotateY(),2), getBlockState(EnumParts.EDGE_RIGHT, facing));
			worldIn.setBlockState(pos.offset(facing,0).offset(facing.rotateY(),3), getBlockState(EnumParts.CORNER, facing));
			worldIn.setBlockState(pos.offset(facing,1).offset(facing.rotateY(),1), getBlockState(EnumParts.CENTER_PAD, facing));
			worldIn.setBlockState(pos.offset(facing,1).offset(facing.rotateY(),0), getBlockState(EnumParts.EDGE_RIGHT, facing.rotateY()));
			worldIn.setBlockState(pos.offset(facing,1).offset(facing.rotateY(),2), getBlockState(EnumParts.CENTER_PAD, facing.rotateYCCW()));
			worldIn.setBlockState(pos.offset(facing,1).offset(facing.rotateY(),3), getBlockState(EnumParts.EDGE_LEFT, facing.rotateYCCW()));
			worldIn.setBlockState(pos.offset(facing,2).offset(facing.rotateY(),0), getBlockState(EnumParts.EDGE_RIGHT, facing.rotateY()));
			worldIn.setBlockState(pos.offset(facing,2).offset(facing.rotateY(),1), getBlockState(EnumParts.CENTER_PAD, facing.rotateY()));
			worldIn.setBlockState(pos.offset(facing,2).offset(facing.rotateY(),2), getBlockState(EnumParts.CENTER_PAD, facing.getOpposite()));
			worldIn.setBlockState(pos.offset(facing,2).offset(facing.rotateY(),3), getBlockState(EnumParts.EDGE_RIGHT, facing.rotateYCCW()));
			worldIn.setBlockState(pos.offset(facing,3).offset(facing.rotateY(),0), getBlockState(EnumParts.CORNER, facing.getOpposite()));
			worldIn.setBlockState(pos.offset(facing,3).offset(facing.rotateY(),1), getBlockState(EnumParts.EDGE_RIGHT, facing.getOpposite()));
			worldIn.setBlockState(pos.offset(facing,3).offset(facing.rotateY(),2), getBlockState(EnumParts.EDGE_LEFT, facing.getOpposite()));
			worldIn.setBlockState(pos.offset(facing,3).offset(facing.rotateY(),3), getBlockState(EnumParts.CORNER, facing.rotateYCCW()));
		}
	}
	
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
	{
		BlockPos mainPos=getMainPos(state,pos, worldIn);
		TileEntity te = worldIn.getTileEntity(mainPos);
		if (te instanceof TileEntityAlchemiter)
		{
			TileEntityAlchemiter alchemiter = (TileEntityAlchemiter) te;
			alchemiter.Break();
			InventoryHelper.dropInventoryItems(worldIn, pos, alchemiter);
		}	
		super.breakBlock(worldIn, pos, state);
	}
	
	//Block state handling
	@Override
	protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this,PART1,DIRECTION);
    }
	
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		IBlockState defaultState=getDefaultState();		
		EnumParts part = EnumParts.values()[index*4 + meta % 4];
		EnumFacing facing = EnumFacing.getHorizontal(meta/4);

		return defaultState.withProperty(PART, part).withProperty(DIRECTION, facing);
	}
	
	@Override
	public int getMetaFromState(IBlockState state)
	{
		EnumParts part = state.getValue(PART);
		EnumFacing facing = state.getValue(DIRECTION);
		return (part.ordinal()%4) + facing.getHorizontalIndex()*4;
	}
	
    /**
     *returns the block position of the "Main" block
     *aka the block with the TileEntity for the machine
     */
	public BlockPos getMainPos(IBlockState state, BlockPos pos, World world)
	{
		return getMainPos(state, pos, world, 4);
	}
	public BlockPos getMainPos(IBlockState state, BlockPos pos, World world, int count)
	{
		EnumParts part = state.getValue(PART);
		EnumFacing facing = state.getValue(DIRECTION);
		switch(part)
		{
			case TOTEM_CORNER: return pos;
			case TOTEM_PAD:	return pos.down(1);
			case LOWER_ROD: return pos.down(2);
			case UPPER_ROD: return pos.down(3);
			default:
				if(count == 0)	//Prevents potential recursion crashes
					return new BlockPos(0, -1, 0);
				if(part == EnumParts.CENTER_PAD)
					pos = pos.offset(facing.rotateYCCW()).offset(facing.getOpposite());
				else pos = pos.offset(facing.rotateYCCW(), part == EnumParts.EDGE_LEFT ? 1 : part == EnumParts.EDGE_RIGHT ? 2 : 3);
				IBlockState newState = world.getBlockState(pos);
				if(newState.equals(getBlockState(EnumParts.TOTEM_CORNER, facing))
						|| newState.equals(getBlockState(EnumParts.CORNER, facing.rotateY())))
					return ((BlockAlchemiter) newState.getBlock()).getMainPos(newState, pos, world, count - 1);
				else return new BlockPos(0, -1, 0);
		}
	}
	
	public static IBlockState getBlockState(EnumParts parts, EnumFacing direction)
	{
		BlockAlchemiter block = MinestuckBlocks.alchemiter[parts.ordinal() < 4 ? 0 : 1];
		IBlockState state = block.getDefaultState();
		return state.withProperty(block.PART, parts).withProperty(DIRECTION, direction);
	}
	
	public static class BlockAlchemiter2 extends BlockAlchemiter
	{
		public BlockAlchemiter2()
		{
			super(1, PART2);
		}
		
		@Override
		protected BlockStateContainer createBlockState()
		{
			return new BlockStateContainer(this,PART2,DIRECTION);
		}
	}
	
	public static BlockAlchemiter[] createBlocks()
	{
		return new BlockAlchemiter[] {(BlockAlchemiter) new BlockAlchemiter().setRegistryName("alchemiter"),
				(BlockAlchemiter) new BlockAlchemiter2().setRegistryName("alchemiter2")};
	}
	
	public enum EnumParts implements IStringSerializable
	{
		TOTEM_CORNER,
		TOTEM_PAD,
		LOWER_ROD,
		UPPER_ROD,
		EDGE_LEFT,
		EDGE_RIGHT,
		CORNER,
		CENTER_PAD;
		
		@Override
		public String toString()
		{
			return getName();
		}
		@Override
		public String getName()
		{
			return name().toLowerCase();
		}
	}
}