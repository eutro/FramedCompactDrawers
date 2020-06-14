package eutros.framedcompactdrawers.model;

import com.google.common.collect.ImmutableList;
import com.jaquadro.minecraft.chameleon.Chameleon;
import com.jaquadro.minecraft.chameleon.model.CachedBuilderModel;
import com.jaquadro.minecraft.chameleon.model.ChamModel;
import com.jaquadro.minecraft.chameleon.model.ProxyBuilderModel;
import com.jaquadro.minecraft.chameleon.render.ChamRender;
import com.jaquadro.minecraft.chameleon.resources.IconUtil;
import com.jaquadro.minecraft.chameleon.resources.register.DefaultRegister;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawersCustom;
import com.jaquadro.minecraft.storagedrawers.block.modeldata.MaterialModelData;
import com.jaquadro.minecraft.storagedrawers.client.model.component.DrawerSealedModel;
import eutros.framedcompactdrawers.block.BlockSlaveCustom;
import eutros.framedcompactdrawers.registry.ModBlocks;
import eutros.framedcompactdrawers.utils.Reference;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomSlaveModel extends ChamModel {

    private static final ItemHandler itemHandler = new ItemHandler();
    private TextureAtlasSprite iconParticle;

    private CustomSlaveModel(IBlockState state, boolean mergeLayers) {
        this(state, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY,
                ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, mergeLayers);
    }

    private CustomSlaveModel(IBlockState state, @Nonnull ItemStack effMatSide, @Nonnull ItemStack effMatTrim, @Nonnull ItemStack effMatTopB,
                             @Nonnull ItemStack matSide, @Nonnull ItemStack matTrim, @Nonnull ItemStack matTopB, boolean mergeLayers) {
        super(state, mergeLayers, effMatSide, effMatTrim, effMatTopB, matSide, matTrim, matTopB);
    }

    public static IBakedModel fromBlock(IBlockState state) {
        if(!(state instanceof IExtendedBlockState))
            return new CustomSlaveModel(state, false);

        IExtendedBlockState xState = (IExtendedBlockState) state;
        MaterialModelData matModel = xState.getValue(BlockDrawersCustom.MAT_MODEL);
        if(matModel == null)
            return new CustomSlaveModel(state, false);

        ItemStack effMatSide = matModel.getEffectiveMaterialSide();
        ItemStack effMatTrim = matModel.getEffectiveMaterialTrim();
        ItemStack effMatTopB = matModel.getEffectiveMaterialFront();

        ItemStack matSide = matModel.getMaterialSide();
        ItemStack matTrim = matModel.getMaterialTrim();
        ItemStack matTopB = matModel.getMaterialFront();

        return new CustomSlaveModel(state, effMatSide, effMatTrim, effMatTopB, matSide, matTrim, matTopB, false);
    }

    public static IBakedModel fromItem(@Nonnull ItemStack stack) {
        IBlockState state = ModBlocks.framedSlave.getDefaultState();
        if(!stack.hasTagCompound())
            return new CustomSlaveModel(state, true);

        NBTTagCompound tag = stack.getTagCompound();
        ItemStack matSide = ItemStack.EMPTY;
        ItemStack matTrim = ItemStack.EMPTY;
        ItemStack matTopB = ItemStack.EMPTY;

        assert tag != null;

        if(tag.hasKey("MatS", Constants.NBT.TAG_COMPOUND))
            matSide = new ItemStack(tag.getCompoundTag("MatS"));
        if(tag.hasKey("MatT", Constants.NBT.TAG_COMPOUND))
            matTrim = new ItemStack(tag.getCompoundTag("MatT"));
        if(tag.hasKey("MatF", Constants.NBT.TAG_COMPOUND))
            matTopB = new ItemStack(tag.getCompoundTag("MatF"));

        ItemStack effMatTopB = !matTopB.isEmpty() ? matTopB : matSide;
        ItemStack effMatTrim = !matTrim.isEmpty() ? matTrim : matSide;
        ItemStack effMatSide = matSide;

        IBakedModel model = new CustomSlaveModel(state, effMatSide, effMatTrim, effMatTopB, matSide, matTrim, matTopB, true);
        if(!stack.getTagCompound().hasKey("tile", Constants.NBT.TAG_COMPOUND))
            return model;

        return new DrawerSealedModel(model, state, true);
    }

    @Override
    protected void renderMippedLayer(ChamRender renderer, IBlockState state, Object... args) {
        ItemStack itemSide = (ItemStack) args[0];
        ItemStack itemTrim = (ItemStack) args[1];
        ItemStack itemTopB = (ItemStack) args[2];

        TextureAtlasSprite iconSide = !itemSide.isEmpty() ? IconUtil.getIconFromStack(itemSide) : null;
        TextureAtlasSprite iconTrim = !itemTrim.isEmpty() ? IconUtil.getIconFromStack(itemTrim) : null;
        TextureAtlasSprite iconTopB = !itemTopB.isEmpty() ? IconUtil.getIconFromStack(itemTopB) : null;

        if(iconTrim == null)
            iconTrim = iconSide;

        if(iconSide == null)
            iconSide = Chameleon.instance.iconRegistry.getIcon(Register.iconDefaultSide);
        if(iconTrim == null)
            iconTrim = Chameleon.instance.iconRegistry.getIcon(Register.iconDefaultSide);
        if(iconTopB == null)
            iconTopB = Chameleon.instance.iconRegistry.getIcon(Register.iconDefaultTopBottom);

        iconParticle = iconSide;

        SlaveRenderer slaveRenderer = new SlaveRenderer(renderer);
        slaveRenderer.renderBasePass(null, state, BlockPos.ORIGIN, iconSide, iconTrim, iconTopB);
    }

    @Override
    protected void renderTransLayer(ChamRender renderer, IBlockState state, Object... args) {

        TextureAtlasSprite iconOverlaySide = Chameleon.instance.iconRegistry.getIcon(Register.iconOverlaySide);

        SlaveRenderer slaveRenderer = new SlaveRenderer(renderer);
        slaveRenderer.renderOverlayPass(null, state, BlockPos.ORIGIN, iconOverlaySide);
    }

    @Nonnull
    @Override
    public TextureAtlasSprite getParticleTexture() {
        return iconParticle;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return null;
    }

    public static class Register extends DefaultRegister<BlockSlaveCustom> {

        public static final ResourceLocation iconDefaultSide = new ResourceLocation(Reference.MOD_ID + ":blocks/raw_side");

        public static final ResourceLocation iconDefaultTopBottom = new ResourceLocation(Reference.MOD_ID + ":blocks/slave_raw_top_bottom");

        public static final ResourceLocation iconOverlaySide =
                new ResourceLocation(Reference.MOD_ID, "blocks/overlay/shading_side");

        public Register() {
            super(ModBlocks.framedSlave);
        }

        @Override
        public List<IBlockState> getBlockStates() {
            return Collections.singletonList(ModBlocks.framedSlave.getDefaultState());
        }

        @Override
        public IBakedModel getModel(IBlockState state, IBakedModel existingModel) {
            return new CachedBuilderModel(new Model());
        }

        @Override
        public IBakedModel getModel(ItemStack stack, IBakedModel existingModel) {
            return new CachedBuilderModel(new Model());
        }

        @Override
        public List<ResourceLocation> getTextureResources() {
            List<ResourceLocation> resource = new ArrayList<>();
            resource.add(iconDefaultSide);
            resource.add(iconOverlaySide);
            resource.add(iconDefaultTopBottom);
            return resource;
        }

    }

    public static class Model extends ProxyBuilderModel {

        public Model() {
            super(Chameleon.instance.iconRegistry.getIcon(Register.iconDefaultSide));
        }

        @Override
        protected IBakedModel buildModel(IBlockState state, IBakedModel parent) {
            try {
                return CustomSlaveModel.fromBlock(state);
            } catch(Throwable t) {
                return parent;
            }
        }

        @Override
        public ItemOverrideList getOverrides() {
            return itemHandler;
        }

        @Override
        public List<Object> getKey(IBlockState state) {
            try {
                List<Object> key = new ArrayList<>();
                IExtendedBlockState xState = (IExtendedBlockState) state;
                key.add(xState.getValue(BlockDrawersCustom.MAT_MODEL));

                return key;
            } catch(Throwable t) {
                return super.getKey(state);
            }
        }

    }

    private static class ItemHandler extends ItemOverrideList {

        public ItemHandler() {
            super(ImmutableList.of());
        }

        @Nonnull
        @Override
        public IBakedModel handleItemState(@Nonnull IBakedModel originalModel, @Nonnull ItemStack stack, World world, EntityLivingBase entity) {
            return fromItem(stack);
        }

    }

}
