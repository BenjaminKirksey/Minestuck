package com.mraof.minestuck.block;

import com.mraof.minestuck.tileentity.TileEntityPunchDesignix;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPunchDesignix extends BlockLargeMachine
{
	
	public static final PropertyEnum<EnumParts> PART = PropertyEnum.create("part", EnumParts.class);
	public static final PropertyDirection DIRECTION = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public BlockPunchDesignix()
	{
		setUnlocalizedName("punch_designix");
		setDefaultState(blockState.getBaseState());
	} 
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		EnumParts parts = state.getValue(PART);
		EnumFacing facing = state.getValue(DIRECTION);
		
		return parts.BOUNDING_BOX[facing.getHorizontalIndex()];
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if (worldIn.isRemote)
			return true;
		BlockPos mainPos = getMainPos(state, pos);
		TileEntity te = worldIn.getTileEntity(mainPos);
		if (te != null && te instanceof TileEntityPunchDesignix)
			((TileEntityPunchDesignix) te).onRightClick(playerIn, state);
		return true;
	}
	
	@Override
	public boolean hasTileEntity(IBlockState state)
	{
		return state.getValue(PART) == EnumParts.TOP_LEFT || state.getValue(PART) == EnumParts.TOP_LEFT_CARD;
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		if(meta % 4 == EnumParts.TOP_LEFT.ordinal())
			return new TileEntityPunchDesignix();
		else return null;
	}
	
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
	{
	
		BlockPos mainPos = getMainPos(state, pos);
		TileEntity te = worldIn.getTileEntity(mainPos);
		if(te != null && te instanceof TileEntityPunchDesignix)
		{
			TileEntityPunchDesignix designix = (TileEntityPunchDesignix) te;
			designix.broken = true;
			if(hasTileEntity(state))
				designix.dropItem(true);
		}
		
		super.breakBlock(worldIn, pos, state);
	}
	
	//Block state handling
	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, PART, DIRECTION);
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		IBlockState defaultState = getDefaultState();
		EnumParts part = EnumParts.values()[meta % 4];
		EnumFacing facing = EnumFacing.getHorizontal(meta/4);
		
		return defaultState.withProperty(PART, part).withProperty(DIRECTION, facing);
	}
	@Override
	public int getMetaFromState(IBlockState state)
	{
		EnumParts part = state.getValue(PART);
		EnumFacing facing = state.getValue(DIRECTION);
		if(part == EnumParts.TOP_LEFT_CARD)
			part = EnumParts.TOP_LEFT;
		
		return part.ordinal() + facing.getHorizontalIndex()*4;
	}
	
	
    /**
     *returns the block position of the "Main" block
     *aka the block with the TileEntity for the machine
     *@pram the state of the block
     *@pram the position the block 
     */
	public BlockPos getMainPos(IBlockState state, BlockPos pos)
	{
		EnumFacing facing = state.getValue(DIRECTION);
		switch(state.getValue(PART))
		{
			case TOP_LEFT: case TOP_LEFT_CARD: return pos;
			case TOP_RIGHT: return pos.offset(facing.rotateY());
			case BOTTOM_LEFT: return pos.up();
			case BOTTOM_RIGHT: return pos.up().offset(facing.rotateY());
		}
		return pos;
	}
	
	@Override
	public IBlockState getActualState(IBlockState state,IBlockAccess worldIn,BlockPos pos)
	{
		if (hasTileEntity(state))
		{
			BlockPos mainPos = getMainPos(state, pos);
			TileEntity te = worldIn.getTileEntity(mainPos);
			if (te instanceof TileEntityPunchDesignix)
				return state.withProperty(PART, ((TileEntityPunchDesignix) te).getCard().isEmpty() ? EnumParts.TOP_LEFT : EnumParts.TOP_LEFT_CARD);
		}
		return state;
	}
	
	public enum EnumParts implements IStringSerializable
	{
		BOTTOM_LEFT(new AxisAlignedBB(5/16D, 0.0D, 0.0D, 1.0D, 1.0D, 11/16D), new AxisAlignedBB(5/16D, 0.0D, 5/16D, 1.0D, 1.0D, 1.0D),
				new AxisAlignedBB(0.0D, 0.0D, 5/16D, 11/16D, 1.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 11/16D, 1.0D, 11/16D)),
		BOTTOM_RIGHT(new AxisAlignedBB(0.0D, 0.0D, 0.0D, 13/16D, 1.0D, 11/16D), new AxisAlignedBB(5/16D, 0.0D, 0.0D, 1.0D, 1.0D, 13/16D),
				new AxisAlignedBB(3/16D, 0.0D, 5/16D, 1.0D, 1.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 3/16D, 11/16D, 1.0D, 1.0D)),
		TOP_LEFT(new AxisAlignedBB(5/16D, 0.0D, 0.0D, 1.0D, 6/16D, 6/16D), new AxisAlignedBB(10/16D, 0.0D, 5/16D, 1.0D, 6/16D, 1.0D),
				new AxisAlignedBB(0.0D, 0.0D, 10/16D, 11/16D, 6/16D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 6/16D, 6/16D, 11/16D)),
		TOP_RIGHT(new AxisAlignedBB(0.0D, 0.0D, 0.0D, 13/16D, 6/16D, 6/16D), new AxisAlignedBB(10/16D, 0.0D, 0.0D, 1.0D, 6/16D, 13/16D),
				new AxisAlignedBB(3/16D, 0.0D, 10/16D, 1.0D, 6/16D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 3/16D, 6/16D, 6/16D, 1.0D)),
		TOP_LEFT_CARD(new AxisAlignedBB(5/16D, 0.0D, 0.0D, 1.0D, 6/16D, 6/16D), new AxisAlignedBB(10/16D, 0.0D, 5/16D, 1.0D, 6/16D, 1.0D),
				new AxisAlignedBB(0.0D, 0.0D, 10/16D, 11/16D, 6/16D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 6/16D, 6/16D, 11/16D));
		
		private final AxisAlignedBB[] BOUNDING_BOX;
		
		EnumParts(AxisAlignedBB... bb)
		{
			BOUNDING_BOX = bb;
		}
		
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