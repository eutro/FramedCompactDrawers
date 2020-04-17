package eutros.fcd.model;

import com.google.common.collect.ImmutableList;
import com.jaquadro.minecraft.chameleon.Chameleon;
import com.jaquadro.minecraft.chameleon.model.CachedBuilderModel;
import com.jaquadro.minecraft.chameleon.model.ChamModel;
import com.jaquadro.minecraft.chameleon.model.ProxyBuilderModel;
import com.jaquadro.minecraft.chameleon.render.ChamRender;
import com.jaquadro.minecraft.chameleon.resources.IconUtil;
import com.jaquadro.minecraft.chameleon.resources.register.DefaultRegister;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawersCustom;
import com.jaquadro.minecraft.storagedrawers.block.modeldata.MaterialModelData;
import com.jaquadro.minecraft.storagedrawers.client.model.component.DrawerSealedModel;
import eutros.fcd.block.BlockControllerCustom;
import eutros.fcd.registry.ModBlocks;
import eutros.fcd.utils.Reference;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class CustomControllerModel extends ChamModel {

    private static final ItemHandler itemHandler = new ItemHandler();
    private TextureAtlasSprite iconParticle;

    private CustomControllerModel(IBlockState state, boolean mergeLayers) {
        this(state, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY,
                ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, mergeLayers);
    }

    private CustomControllerModel(IBlockState state, @Nonnull ItemStack effMatFront, @Nonnull ItemStack effMatSide, @Nonnull ItemStack effMatTrim,
                                  @Nonnull ItemStack matFront, @Nonnull ItemStack matSide, @Nonnull ItemStack matTrim, boolean mergeLayers) {
        super(state, mergeLayers, effMatFront, effMatSide, effMatTrim, matFront, matSide, matTrim);
    }

    public static IBakedModel fromBlock(IBlockState state) {
        if(!(state instanceof IExtendedBlockState))
            return new CustomControllerModel(state, false);

        IExtendedBlockState xState = (IExtendedBlockState) state;
        MaterialModelData matModel = xState.getValue(BlockDrawersCustom.MAT_MODEL);
        if(matModel == null)
            return new CustomControllerModel(state, false);

        ItemStack effMatFront = matModel.getEffectiveMaterialFront();
        ItemStack effMatSide = matModel.getEffectiveMaterialSide();
        ItemStack effMatTrim = matModel.getEffectiveMaterialTrim();

        ItemStack matFront = matModel.getMaterialFront();
        ItemStack matSide = matModel.getMaterialSide();
        ItemStack matTrim = matModel.getMaterialTrim();

        return new CustomControllerModel(state, effMatFront, effMatSide, effMatTrim, matFront, matSide, matTrim, false);
    }

    public static IBakedModel fromItem(@Nonnull ItemStack stack) {
        IBlockState state = ModBlocks.framedDrawerController.getDefaultState();
        if(!stack.hasTagCompound())
            return new CustomControllerModel(state, true);

        NBTTagCompound tag = stack.getTagCompound();
        ItemStack matFront = ItemStack.EMPTY;
        ItemStack matSide = ItemStack.EMPTY;
        ItemStack matTrim = ItemStack.EMPTY;

        assert tag != null;

        if(tag.hasKey("MatF", Constants.NBT.TAG_COMPOUND))
            matFront = new ItemStack(tag.getCompoundTag("MatF"));
        if(tag.hasKey("MatS", Constants.NBT.TAG_COMPOUND))
            matSide = new ItemStack(tag.getCompoundTag("MatS"));
        if(tag.hasKey("MatT", Constants.NBT.TAG_COMPOUND))
            matTrim = new ItemStack(tag.getCompoundTag("MatT"));

        ItemStack effMatFront = !matFront.isEmpty() ? matFront : matSide;
        ItemStack effMatTrim = !matTrim.isEmpty() ? matTrim : matSide;
        ItemStack effMatSide = matSide;

        IBakedModel model = new CustomControllerModel(state, effMatFront, effMatSide, effMatTrim, matFront, matSide, matTrim, true);
        if(!stack.getTagCompound().hasKey("tile", Constants.NBT.TAG_COMPOUND))
            return model;

        return new DrawerSealedModel(model, state, true);
    }

    @Override
    protected void renderMippedLayer(ChamRender renderer, IBlockState state, Object... args) {
        ItemStack itemFront = (ItemStack) args[0];
        ItemStack itemSide = (ItemStack) args[1];
        ItemStack itemTrim = (ItemStack) args[2];

        TextureAtlasSprite iconFront = !itemFront.isEmpty() ? IconUtil.getIconFromStack(itemFront) : null;
        TextureAtlasSprite iconSide = !itemSide.isEmpty() ? IconUtil.getIconFromStack(itemSide) : null;
        TextureAtlasSprite iconTrim = !itemTrim.isEmpty() ? IconUtil.getIconFromStack(itemTrim) : null;

        if(iconFront == null)
            iconFront = iconSide;
        if(iconTrim == null)
            iconTrim = iconSide;

        if(iconFront == null)
            iconFront = Chameleon.instance.iconRegistry.getIcon(Register.iconDefaultFront);
        if(iconSide == null)
            iconSide = Chameleon.instance.iconRegistry.getIcon(Register.iconDefaultSide);
        if(iconTrim == null)
            iconTrim = Chameleon.instance.iconRegistry.getIcon(Register.iconDefaultSide);

        iconParticle = iconSide;

        ControllerRenderer controllerRenderer = new ControllerRenderer(renderer);
        controllerRenderer.renderBasePass(null, state, BlockPos.ORIGIN, state.getValue(BlockDrawers.FACING), iconSide, iconTrim, iconFront);
    }

    @Override
    protected void renderTransLayer(ChamRender renderer, IBlockState state, Object... args) {

        TextureAtlasSprite iconOverlayHandle = Chameleon.instance.iconRegistry.getIcon(Register.iconOverlayHandle);

        ItemStack matFront = (ItemStack) args[0];
        ItemStack itemTrim = (ItemStack) args[5];

        TextureAtlasSprite iconOverlayFace = matFront.isEmpty() ?
                                             null :
                                             Chameleon.instance.iconRegistry.getIcon(Register.iconOverlayFace);
        TextureAtlasSprite iconTrim = !itemTrim.isEmpty() ? IconUtil.getIconFromStack(itemTrim) : null;
        TextureAtlasSprite iconOverlayTrim;

        if(iconTrim == null)
            iconOverlayTrim = Chameleon.instance.iconRegistry.getIcon(Register.iconOverlayBoldTrim);
        else
            iconOverlayTrim = Chameleon.instance.iconRegistry.getIcon(Register.iconOverlayTrim);

        ControllerRenderer controllerRenderer = new ControllerRenderer(renderer);
        controllerRenderer.renderOverlayPass(null, state, BlockPos.ORIGIN, state.getValue(BlockDrawers.FACING), iconOverlayTrim, iconOverlayHandle, iconOverlayFace);
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return iconParticle;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return null;
    }

    public static class Register extends DefaultRegister<BlockControllerCustom> {

        public static final ResourceLocation iconDefaultSide = new ResourceLocation(Reference.MOD_ID + ":blocks/drawers_comp_raw_side");

        public static final ResourceLocation iconDefaultFront =
                new ResourceLocation(Reference.MOD_ID + ":blocks/drawer_controller_raw_front");

        public static final ResourceLocation iconOverlayTrim =
                new ResourceLocation(Reference.MOD_ID + ":blocks/overlay/shading_controller_trim");

        public static final ResourceLocation iconOverlayBoldTrim =
                new ResourceLocation(Reference.MOD_ID + ":blocks/overlay/shading_controller_bold_trim");

        public static final ResourceLocation iconOverlayFace =
                new ResourceLocation(Reference.MOD_ID + ":blocks/overlay/shading_controller_face");

        public static final ResourceLocation iconOverlayHandle =
                new ResourceLocation(Reference.MOD_ID + ":blocks/overlay/handle");

        public Register() {
            super(ModBlocks.framedDrawerController);
        }

        @Override
        public List<IBlockState> getBlockStates() {
            List<IBlockState> states = new ArrayList<>();

            for(EnumFacing dir : EnumFacing.HORIZONTALS)
                states.add(ModBlocks.framedDrawerController.getDefaultState()
                        .withProperty(BlockDrawers.FACING, dir));

            return states;
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
            resource.add(iconDefaultFront);
            resource.add(iconDefaultSide);
            resource.add(iconOverlayTrim);
            resource.add(iconOverlayBoldTrim);
            resource.add(iconOverlayFace);
            resource.add(iconOverlayHandle);
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
                return CustomControllerModel.fromBlock(state);
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

        @Override
        public IBakedModel handleItemState(IBakedModel originalModel, @Nonnull ItemStack stack, World world, EntityLivingBase entity) {
            return fromItem(stack);
        }

    }

}
