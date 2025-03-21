package com.Nxer.TwistSpaceTechnology.common.machine;

import static com.Nxer.TwistSpaceTechnology.common.machine.ValueEnum.Parallel_PerRing_MiracleTop;
import static com.Nxer.TwistSpaceTechnology.common.machine.ValueEnum.RingsAmount_EnablePerfectOverclock_MiracleTop;
import static com.Nxer.TwistSpaceTechnology.common.machine.ValueEnum.SpeedUpMultiplier_PerRing_MiracleTop;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.ExoticEnergy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.enums.HatchElement.OutputHatch;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_DTPF_OFF;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_DTPF_ON;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FUSION1_GLOW;
import static gregtech.api.enums.Textures.BlockIcons.casingTexturePages;
import static tectech.thing.casing.TTCasingsContainer.sBlockCasingsTT;

import java.util.Arrays;
import java.util.Collection;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.Nxer.TwistSpaceTechnology.common.machine.multiMachineClasses.GTCM_MultiMachineBase;
import com.Nxer.TwistSpaceTechnology.common.recipeMap.GTCMRecipe;
import com.Nxer.TwistSpaceTechnology.util.TextLocalization;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;

import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gtPlusPlus.core.block.ModBlocks;
import tectech.thing.block.BlockQuantumGlass;

public class GT_TileEntity_MiracleTop extends GTCM_MultiMachineBase<GT_TileEntity_MiracleTop> {

    // region Constructors
    public GT_TileEntity_MiracleTop(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GT_TileEntity_MiracleTop(String aName) {
        super(aName);
    }
    // endregion

    // region Structure
    private final int baseHorizontalOffSet = 10;
    private final int baseVerticalOffSet = 10;
    private final int baseDepthOffSet = 0;
    private static final String STRUCTURE_PIECE_MAIN = "mainMiracleTop";
    private static final String STRUCTURE_PIECE_MIDDLE = "middleMiracleTop";
    private static final String STRUCTURE_PIECE_END = "endMiracleTop";
    private static IStructureDefinition<GT_TileEntity_MiracleTop> STRUCTURE_DEFINITION = null;

    /*
     * A -> ofBlock...(gt.blockcasingsTT, 4, ...);
     * B -> ofBlock...(gt.blockcasingsTT, 7, ...);
     * C -> ofBlock...(gt.blockcasingsTT, 9, ...);
     * D -> ofBlock...(gtplusplus.blockcasings.4, 4, ...);
     * E -> ofBlock...(tile.quantumGlass, 0, ...);
     * H -> Hatches;
     * M -> Maintenance Hatch;
     */
    @Override
    public IStructureDefinition<GT_TileEntity_MiracleTop> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<GT_TileEntity_MiracleTop>builder()
                .addShape(STRUCTURE_PIECE_MAIN, shapeMain)
                .addShape(STRUCTURE_PIECE_MIDDLE, shapeMiddle)
                .addShape(STRUCTURE_PIECE_END, shapeEnd)
                .addElement('A', ofBlock(sBlockCasingsTT, 4))
                .addElement('B', ofBlock(sBlockCasingsTT, 7))
                .addElement('C', ofBlock(sBlockCasingsTT, 9))
                .addElement('D', ofBlock(ModBlocks.blockCasings4Misc, 4))
                .addElement('E', ofBlock(BlockQuantumGlass.INSTANCE, 0))
                .addElement(
                    'M',
                    GTStructureUtility.buildHatchAdder(GT_TileEntity_MiracleTop.class)
                        .atLeast(Maintenance)
                        .dot(1)
                        .casingIndex(1028)
                        .buildAndChain(sBlockCasingsTT, 4))
                .addElement(
                    'H',
                    GTStructureUtility.buildHatchAdder(GT_TileEntity_MiracleTop.class)
                        .atLeast(InputBus, InputHatch, OutputBus, OutputHatch, Energy.or(ExoticEnergy))
                        .dot(2)
                        .casingIndex(1028)
                        .buildAndChain(sBlockCasingsTT, 4))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        this.buildPiece(
            STRUCTURE_PIECE_MAIN,
            stackSize,
            hintsOnly,
            baseHorizontalOffSet,
            baseVerticalOffSet,
            baseDepthOffSet);
        int pointer = 1;
        for (; pointer < Math.min(15, stackSize.stackSize); pointer++) {

            buildPiece(
                STRUCTURE_PIECE_MIDDLE,
                stackSize,
                hintsOnly,
                baseHorizontalOffSet,
                baseVerticalOffSet,
                baseDepthOffSet - pointer * 8);
        }
        buildPiece(
            STRUCTURE_PIECE_END,
            stackSize,
            hintsOnly,
            baseHorizontalOffSet,
            baseVerticalOffSet,
            baseDepthOffSet - pointer * 8);
    }

    /**
     * Called when try auto construct in survival mode.
     *
     * @param stackSize     The StructureLib Blueprint item stack.
     * @param elementBudget The server configured element budget. The implementor can choose to tune this up a bit if
     *                      the structure is too big, but generally should not be a 4 digits number to not overwhelm the
     *                      server.
     */
    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {

        if (this.mMachine) return -1;

        int built = 0;

        built = survivialBuildPiece(
            STRUCTURE_PIECE_MAIN,
            stackSize,
            baseHorizontalOffSet,
            baseVerticalOffSet,
            baseDepthOffSet,
            elementBudget,
            env,
            false,
            true);

        if (built >= 0) return built;

        int rings = Math.min(14, stackSize.stackSize - 1);
        int pointer = 1;
        while (pointer <= rings) {
            built += survivialBuildPiece(
                STRUCTURE_PIECE_MIDDLE,
                stackSize,
                baseHorizontalOffSet,
                baseVerticalOffSet,
                baseDepthOffSet - pointer * 8,
                elementBudget,
                env,
                false,
                true);
            pointer++;
            if (built >= 0) return built;
        }

        built += survivialBuildPiece(
            STRUCTURE_PIECE_END,
            stackSize,
            baseHorizontalOffSet,
            baseVerticalOffSet,
            baseDepthOffSet - pointer * 8,
            elementBudget,
            env,
            false,
            true);

        return built;

    }

    /**
     * Checks the Machine. You have to assign the MetaTileEntities for the Hatches here.
     *
     * @param aBaseMetaTileEntity
     * @param aStack
     */
    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        repairMachine();

        // init the pointer, also the Properties.
        this.amountRings = 1;

        // check the Top layer.
        if (!checkPiece(STRUCTURE_PIECE_MAIN, baseHorizontalOffSet, baseVerticalOffSet, baseDepthOffSet)) {
            return false;
        }

        // check middle layer, increase speedBoost per layer.
        // 8 blocks depth per layer
        while (checkPiece(
            STRUCTURE_PIECE_MIDDLE,
            baseHorizontalOffSet,
            baseVerticalOffSet,
            baseDepthOffSet - this.amountRings * 8)) {
            this.amountRings++;
            if (amountRings > 15) {
                return false;
            }
        }

        // check the end layer
        if (!checkPiece(
            STRUCTURE_PIECE_END,
            baseHorizontalOffSet,
            baseVerticalOffSet,
            baseDepthOffSet - this.amountRings * 8)) {
            return false;
        }

        // basic two layers: the top and the end, means amountRings default is 2 .
        this.amountRings++;

        // calculate parameters
        enablePerfectOverclock = amountRings >= RingsAmount_EnablePerfectOverclock_MiracleTop;
        speedBonus = 1.0F / (amountRings * SpeedUpMultiplier_PerRing_MiracleTop);
        maxParallel = amountRings * Parallel_PerRing_MiracleTop;

        return true;
    }

    // endregion

    // region Processing Logic
    @Override
    public int totalMachineMode() {
        /*
         * 0 - Miracle Top Circuit Assembler
         * 1 - Gravitation Inversion
         */
        return 2;
    }

    @Override
    public void setMachineModeIcons() {
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_COMPRESSING);
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_BENDING);
    }

    @Override
    public String getMachineModeName(int mode) {
        return StatCollector.translateToLocal("MiracleTop.modeMsg." + mode);
    }

    public int amountRings = 1;

    @Override
    public RecipeMap<?> getRecipeMap() {
        return machineMode == 0 ? GTCMRecipe.MiracleTopRecipes : GTCMRecipe.QuantumInversionRecipes;
    }

    @NotNull
    @Override
    public Collection<RecipeMap<?>> getAvailableRecipeMaps() {
        return Arrays.asList(GTCMRecipe.MiracleTopRecipes, GTCMRecipe.QuantumInversionRecipes);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setByte("mode", (byte) machineMode);
        aNBT.setInteger("amountRings", amountRings);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        machineMode = aNBT.getByte("mode");
        amountRings = aNBT.getInteger("amountRings");
    }

    @Override
    public boolean addToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        return super.addToMachineList(aTileEntity, aBaseCasingIndex)
            || addExoticEnergyInputToMachineList(aTileEntity, aBaseCasingIndex);
    }

    // endregion

    // region Overrides

    @Override
    public String[] getInfoData() {
        String[] origin = super.getInfoData();
        String[] ret = new String[origin.length + 1];
        System.arraycopy(origin, 0, ret, 0, origin.length);
        ret[origin.length] = "Speed up multiplier: " + this.amountRings * SpeedUpMultiplier_PerRing_MiracleTop;
        return ret;
    }

    /**
     * @param aTileEntity is just because the internal Variable "mBaseMetaTileEntity" is set after this Call.
     * @return a newly created and ready MetaTileEntity
     */
    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_TileEntity_MiracleTop(this.mName);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {

            if (aActive) {
                return new ITexture[] { casingTexturePages[0][12], TextureFactory.builder()
                    .addIcon(OVERLAY_DTPF_ON)
                    .extFacing()
                    .build(),
                    TextureFactory.builder()
                        .addIcon(OVERLAY_FUSION1_GLOW)
                        .extFacing()
                        .glow()
                        .build() };
            }

            return new ITexture[] { casingTexturePages[0][12], TextureFactory.builder()
                .addIcon(OVERLAY_DTPF_OFF)
                .extFacing()
                .build() };
        }

        return new ITexture[] { casingTexturePages[0][12] };
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(TextLocalization.Tooltip_MiracleTop_MachineType)
            .addInfo(TextLocalization.Tooltip_MiracleTop_00)
            .addInfo(TextLocalization.Tooltip_MiracleTop_01)
            .addInfo(TextLocalization.Tooltip_MiracleTop_02)
            .addInfo(TextLocalization.Tooltip_MiracleTop_03)
            .addInfo(TextLocalization.Tooltip_MiracleTop_04)
            .addInfo(TextLocalization.Tooltip_MiracleTop_05)
            .addInfo(TextLocalization.Tooltip_MiracleTop_06)
            .addInfo(TextLocalization.Tooltip_MiracleTop_07)
            .addInfo(TextLocalization.StructureTooComplex)
            .addInfo(TextLocalization.BLUE_PRINT_INFO)
            .addSeparator()
            .addController(TextLocalization.textFrontCenter)
            .addInputHatch(TextLocalization.textMiracleTopHatchLocation, 2)
            .addOutputHatch(TextLocalization.textMiracleTopHatchLocation, 2)
            .addInputBus(TextLocalization.textMiracleTopHatchLocation, 2)
            .addOutputBus(TextLocalization.textMiracleTopHatchLocation, 2)
            .addEnergyHatch(TextLocalization.textMiracleTopHatchLocation, 2)
            .toolTipFinisher(TextLocalization.ModName);
        return tt;
    }
    // endregion

    // region Structures
    // spotless:off
    private final String[][] shapeMain = new String[][] {
        {
            "                     ",
            "         AAA         ",
            "       AA   AA       ",
            "                     ",
            "                     ",
            "                     ",
            "                     ",
            "  A               A  ",
            "  A      AAA      A  ",
            " A      AMMMA      A ",
            " A      AM~MA      A ",
            " A      AMMMA      A ",
            "  A      AAA      A  ",
            "  A               A  ",
            "                     ",
            "                     ",
            "                     ",
            "                     ",
            "       AA   AA       ",
            "         AAA         ",
            "                     " },
        {
            "         HHH         ",
            "       AADDDAA       ",
            "      ADDEEEDDA      ",
            "       EE   EE       ",
            "                     ",
            "                     ",
            "  A               A  ",
            " ADE             EDA ",
            " ADE     EEE     EDA ",
            "HDE     EBBBE     EDH",
            "HDE     EBCBE     EDH",
            "HDE     EBBBE     EDH",
            " ADE     EEE     EDA ",
            " ADE             EDA ",
            "  A               A  ",
            "                     ",
            "                     ",
            "       EE   EE       ",
            "      ADDEEEDDA      ",
            "       AADDDAA       ",
            "         HHH         " },
        {
            "                     ",
            "         HHH         ",
            "       AA   AA       ",
            "                     ",
            "                     ",
            "                     ",
            "                     ",
            "  A               A  ",
            "  A      AAA      A  ",
            " H      AAAAA      H ",
            " H      AACAA      H ",
            " H      AAAAA      H ",
            "  A      AAA      A  ",
            "  A               A  ",
            "                     ",
            "                     ",
            "                     ",
            "                     ",
            "       AA   AA       ",
            "         AAA         ",
            "                     " },
        { "                     ", "                     ", "                     ", "                     ",
            "                     ", "                     ", "                     ", "                     ",
            "                     ", "         EEE         ", "         ECE         ", "         EEE         ",
            "                     ", "                     ", "                     ", "                     ",
            "                     ", "                     ", "                     ", "                     ",
            "                     " },
        { "                     ", "                     ", "                     ", "                     ",
            "                     ", "                     ", "                     ", "                     ",
            "                     ", "         EEE         ", "         ECE         ", "         EEE         ",
            "                     ", "                     ", "                     ", "                     ",
            "                     ", "                     ", "                     ", "                     ",
            "                     " },
        { "                     ", "                     ", "                     ", "                     ",
            "                     ", "                     ", "                     ", "                     ",
            "                     ", "         EEE         ", "         ECE         ", "         EEE         ",
            "                     ", "                     ", "                     ", "                     ",
            "                     ", "                     ", "                     ", "                     ",
            "                     " },
        { "                     ", "                     ", "                     ", "                     ",
            "                     ", "                     ", "                     ", "                     ",
            "                     ", "         EEE         ", "         ECE         ", "         EEE         ",
            "                     ", "                     ", "                     ", "                     ",
            "                     ", "                     ", "                     ", "                     ",
            "                     " },
        { "                     ", "                     ", "                     ", "                     ",
            "                     ", "                     ", "                     ", "                     ",
            "                     ", "         EEE         ", "         ECE         ", "         EEE         ",
            "                     ", "                     ", "                     ", "                     ",
            "                     ", "                     ", "                     ", "                     ",
            "                     " } };
    /*
     * A -> ofBlock...(gt.blockcasingsTT, 4, ...);
     * B -> ofBlock...(gt.blockcasingsTT, 7, ...);
     * C -> ofBlock...(gt.blockcasingsTT, 9, ...);
     * D -> ofBlock...(gtplusplus.blockcasings.4, 4, ...);
     * E -> ofBlock...(tile.quantumGlass, 0, ...);
     */

    /*
     * Blocks:
     * A -> ofBlock...(gt.blockcasingsTT, 4, ...);
     * B -> ofBlock...(gt.blockcasingsTT, 7, ...);
     * C -> ofBlock...(gt.blockcasingsTT, 9, ...);
     * D -> ofBlock...(gtplusplus.blockcasings.4, 4, ...);
     * E -> ofBlock...(tile.quantumGlass, 0, ...);
     */
    private final String[][] shapeMiddle = new String[][]{{
        "                     ",
        "         AAA         ",
        "       AA   AA       ",
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "  A               A  ",
        "  A               A  ",
        " A       EEE       A ",
        " A       ECE       A ",
        " A       EEE       A ",
        "  A               A  ",
        "  A               A  ",
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "       AA   AA       ",
        "         AAA         ",
        "                     "
    },{
        "         HHH         ",
        "       AADDDAA       ",
        "      ADDEEEDDA      ",
        "       EE   EE       ",
        "                     ",
        "                     ",
        "  A               A  ",
        " ADE             EDA ",
        " ADE             EDA ",
        "HDE      EEE      EDH",
        "HDE      ECE      EDH",
        "HDE      EEE      EDH",
        " ADE             EDA ",
        " ADE             EDA ",
        "  A               A  ",
        "                     ",
        "                     ",
        "       EE   EE       ",
        "      ADDEEEDDA      ",
        "       AADDDAA       ",
        "         HHH         "
    },{
        "                     ",
        "         AAA         ",
        "       AA   AA       ",
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "  A               A  ",
        "  A               A  ",
        " A       EEE       A ",
        " A       ECE       A ",
        " A       EEE       A ",
        "  A               A  ",
        "  A               A  ",
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "       AA   AA       ",
        "         AAA         ",
        "                     "
    },{
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "         EEE         ",
        "         ECE         ",
        "         EEE         ",
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "                     "
    },{
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "         EEE         ",
        "         ECE         ",
        "         EEE         ",
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "                     "
    },{
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "         EEE         ",
        "         ECE         ",
        "         EEE         ",
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "                     "
    },{
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "         EEE         ",
        "         ECE         ",
        "         EEE         ",
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "                     "
    },{
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "         EEE         ",
        "         ECE         ",
        "         EEE         ",
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "                     "
    }} ;

    /*
     * Blocks:
     * A -> ofBlock...(gt.blockcasingsTT, 4, ...);
     * B -> ofBlock...(gt.blockcasingsTT, 7, ...);
     * C -> ofBlock...(gt.blockcasingsTT, 9, ...);
     * D -> ofBlock...(gtplusplus.blockcasings.4, 4, ...);
     * E -> ofBlock...(tile.quantumGlass, 0, ...);
     */
    private final String[][] shapeEnd = new String[][] {{
        "                     ",
        "         AAA         ",
        "       AA   AA       ",
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "  A               A  ",
        "  A      AAA      A  ",
        " A      AAAAA      A ",
        " A      AACAA      A ",
        " A      AAAAA      A ",
        "  A      AAA      A  ",
        "  A               A  ",
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "       AA   AA       ",
        "         AAA         ",
        "                     "
    },{
        "         HHH         ",
        "       AADDDAA       ",
        "      ADDEEEDDA      ",
        "       EE   EE       ",
        "                     ",
        "                     ",
        "  A               A  ",
        " ADE             EDA ",
        " ADE     EEE     EDA ",
        "HDE     EBBBE     EDH",
        "HDE     EBCBE     EDH",
        "HDE     EBBBE     EDH",
        " ADE     EEE     EDA ",
        " ADE             EDA ",
        "  A               A  ",
        "                     ",
        "                     ",
        "       EE   EE       ",
        "      ADDEEEDDA      ",
        "       AADDDAA       ",
        "         HHH         "
    },{
        "                     ",
        "         AAA         ",
        "       AA   AA       ",
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "  A               A  ",
        "  A      AAA      A  ",
        " A      AHHHA      A ",
        " A      AHHHA      A ",
        " A      AHHHA      A ",
        "  A      AAA      A  ",
        "  A               A  ",
        "                     ",
        "                     ",
        "                     ",
        "                     ",
        "       AA   AA       ",
        "         AAA         ",
        "                     "
    }};
    // spotless:on
    // endregion

}
