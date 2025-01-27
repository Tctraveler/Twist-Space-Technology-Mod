package com.Nxer.TwistSpaceTechnology.common.machine.singleBlock.hatch;

import static gregtech.api.enums.Dyes.MACHINE_METAL;
import static tectech.thing.metaTileEntity.Textures.OVERLAYS_ENERGY_IN_LASER_TT;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import com.Nxer.TwistSpaceTechnology.util.TextEnums;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.Dyes;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTETieredMachineBlock;
import gregtech.api.objects.GTRenderedTexture;
import tectech.mechanics.pipe.IConnectsToEnergyTunnel;
import tectech.thing.metaTileEntity.hatch.MTEHatchDynamoTunnel;
import tectech.thing.metaTileEntity.hatch.MTEHatchEnergyTunnel;
import tectech.thing.metaTileEntity.pipe.MTEPipeEnergy;
import tectech.util.CommonValues;

public class GT_MetaTileEntity_Pipe_EnergySmart extends MTETieredMachineBlock implements IConnectsToEnergyTunnel {

    public long Voltage;
    public long Amperes;

    private static Textures.BlockIcons.CustomIcon EMCandyActive, EMpipe;

    public GT_MetaTileEntity_Pipe_EnergySmart(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional, 10, 0, (String) null);
    }

    public GT_MetaTileEntity_Pipe_EnergySmart(String aName, int aTier, String[] aDescription,
        ITexture[][][] aTextures) {
        super(aName, aTier, 0, aDescription, aTextures);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister aBlockIconRegister) {
        EMCandyActive = new Textures.BlockIcons.CustomIcon("iconsets/EM_CANDY_ACTIVE");
        EMpipe = new Textures.BlockIcons.CustomIcon("iconsets/EM_LASER");
        super.registerIcons(aBlockIconRegister);
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new GT_MetaTileEntity_Pipe_EnergySmart(mName, mTier, mDescriptionArray, mTextures);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        aNBT.setLong("Voltage", Voltage);
        aNBT.setLong("Amperes", Amperes);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        Voltage = aNBT.getLong("Voltage");
        Amperes = aNBT.getLong("Amperes");
    }

    @Override
    public String[] getDescription() {
        return new String[] { CommonValues.TEC_MARK_EM,
            // #tr LaserSmartNode.getDescription.01
            // # Reflect after precise calculation.
            // #zh_CN 精密计算, 然后, 反射！
            TextEnums.tr("LaserSmartNode.getDescription.01"),
            // #tr LaserSmartNode.getDescription.02
            // # {\AQUA}Connects devices in different directions.
            // #zh_CN {\AQUA}连接不同方向的设备.
            TextEnums.tr("LaserSmartNode.getDescription.02"),
            // #tr LaserSmartNode.getDescription.03
            // # {\AQUA}Input energy from the front and output from other sides.
            // #zh_CN {\AQUA}从正面输入能量, 从其他面输出.
            TextEnums.tr("LaserSmartNode.getDescription.03"), TextEnums.AddByTwistSpaceTechnology.getText() };
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            return new ITexture[] { new GTRenderedTexture(EMpipe), OVERLAYS_ENERGY_IN_LASER_TT[mTier],
                new GTRenderedTexture(EMCandyActive, Dyes.getModulation(colorIndex, MACHINE_METAL.getRGBA())) };
        } else {
            return new ITexture[] { new GTRenderedTexture(EMpipe),
                new GTRenderedTexture(EMCandyActive, Dyes.getModulation(colorIndex, MACHINE_METAL.getRGBA())) };
        }
    }

    @Override
    public ITexture[][][] getTextureSet(ITexture[] aTextures) {
        return new ITexture[0][0][0];
    }

    @Override
    public long maxEUInput() {
        return Voltage;
    }

    @Override
    public long maxEUOutput() {
        return Voltage;
    }

    @Override
    public long maxEUStore() {
        return 24 * Voltage * Amperes;
    }

    @Override
    public long maxAmperesOut() {
        return Amperes;
    }

    @Override
    public long maxAmperesIn() {
        return Amperes;
    }

    @Override
    public long getMinimumStoredEU() {
        return Voltage;
    }

    @Override
    public boolean isAccessAllowed(EntityPlayer aPlayer) {
        return true;
    }

    @Override
    public boolean isSimpleMachine() {
        return true;
    }

    @Override
    public boolean isFacingValid(ForgeDirection facing) {
        return true;
    }

    @Override
    public boolean isInputFacing(ForgeDirection side) {
        return side == getBaseMetaTileEntity().getFrontFacing();
    }

    @Override
    public boolean isOutputFacing(ForgeDirection side) {
        return !isInputFacing(side);
    }

    @Override
    public void doExplosion(long aExplosionPower) {
        // no-op
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        if (aBaseMetaTileEntity.isServerSide() && aTick % 20 == 0) {
            byte color = getBaseMetaTileEntity().getColorization();
            if (color < 0) {
                return;
            }
            MetaTileEntity dynamo = null;
            List<MetaTileEntity> energies = new ArrayList<>();
            for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
                ForgeDirection opposite = side.getOpposite();
                if (side == aBaseMetaTileEntity.getFrontFacing()) {
                    // Search for energy provider
                    for (short dist = 1; dist < 1000; dist++) {
                        IGregTechTileEntity tGTTileEntity = aBaseMetaTileEntity
                            .getIGregTechTileEntityAtSideAndDistance(side, dist);
                        if (tGTTileEntity != null && tGTTileEntity.getColorization() == color) {
                            IMetaTileEntity aMetaTileEntity = tGTTileEntity.getMetaTileEntity();
                            if (aMetaTileEntity instanceof MTEHatchDynamoTunnel dynamoTunnel
                                && opposite == tGTTileEntity.getFrontFacing()) {
                                dynamo = dynamoTunnel;
                                Voltage = dynamoTunnel.maxEUOutput();
                                Amperes = dynamoTunnel.Amperes;
                                break;
                            } else if (aMetaTileEntity instanceof GT_MetaTileEntity_Pipe_EnergySmart energySmart
                                && opposite != tGTTileEntity.getFrontFacing()) {
                                    Voltage = energySmart.maxEUOutput();
                                    Amperes = energySmart.Amperes;
                                    break;
                                } else if (aMetaTileEntity instanceof MTEPipeEnergy pipe) {
                                    if (pipe.connectionCount < 2) {
                                        break;
                                    } else {
                                        pipe.markUsed();
                                    }
                                } else break;
                        } else break;
                    }
                } else {
                    // Search for energy receiver
                    for (short dist = 1; dist < 1000; dist++) {
                        IGregTechTileEntity tGTTileEntity = aBaseMetaTileEntity
                            .getIGregTechTileEntityAtSideAndDistance(side, dist);
                        if (tGTTileEntity != null && tGTTileEntity.getColorization() == color) {
                            IMetaTileEntity aMetaTileEntity = tGTTileEntity.getMetaTileEntity();
                            if ((aMetaTileEntity instanceof MTEHatchEnergyTunnel
                                || aMetaTileEntity instanceof GT_MetaTileEntity_Pipe_EnergySmart)
                                && opposite == tGTTileEntity.getFrontFacing()) {
                                energies.add((MetaTileEntity) aMetaTileEntity);
                                break;
                            } else if (aMetaTileEntity instanceof MTEPipeEnergy pipe) {
                                if (pipe.connectionCount < 2) {
                                    break;
                                } else {
                                    pipe.markUsed();
                                }
                            } else break;
                        } else break;
                    }
                }
            }
            if (dynamo != null) moveEnergy(dynamo, this);
            for (MetaTileEntity energy : energies) {
                if (aBaseMetaTileEntity.getStoredEU() > getMinimumStoredEU()) moveEnergy(this, energy);
            }
        }
    }

    @Override
    public boolean allowPullStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, ForgeDirection side,
        ItemStack aStack) {
        return false;
    }

    @Override
    public boolean allowPutStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, ForgeDirection side,
        ItemStack aStack) {
        return false;
    }

    private void moveEnergy(MetaTileEntity dynamo, MetaTileEntity energy) {
        if (dynamo.maxEUOutput() > energy.maxEUInput()) {
            energy.doExplosion(dynamo.maxEUOutput());
            dynamo.setEUVar(
                dynamo.getBaseMetaTileEntity()
                    .getStoredEU() - dynamo.maxEUOutput());
        } else if (dynamo.maxEUOutput() == energy.maxEUInput()) {
            long diff = Math.min(
                dynamo.maxAmperesOut() * 20L * dynamo.maxEUOutput(),
                Math.min(
                    energy.maxEUStore() - energy.getBaseMetaTileEntity()
                        .getStoredEU(),
                    dynamo.getBaseMetaTileEntity()
                        .getStoredEU()));

            dynamo.setEUVar(
                dynamo.getBaseMetaTileEntity()
                    .getStoredEU() - diff);

            energy.setEUVar(
                energy.getBaseMetaTileEntity()
                    .getStoredEU() + diff);
        }

    }

    @Override
    public boolean canConnect(ForgeDirection side) {
        return true;
    }
}
