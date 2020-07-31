package eutros.framedcompactdrawers.model;

import com.google.common.base.Suppliers;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.client.model.geometry.IModelGeometry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class FrameableModel implements IModelGeometry<FrameableModel> {

    public Multimap<MaterialSide, FramingCandidate> materials;

    public enum MaterialSide {
        SIDE,
        FRONT,
        TRIM,
        OVERLAY;

        public final ModelProperty<ItemStack> property = new ModelProperty<>();
    }

    public static class FramingCandidate {

        public static final FaceBakery FACE_BAKERY = new FaceBakery();

        public Vector3f start;
        public Vector3f end;
        public Direction direction;
        public BlockPartFace face;

        public String getRaw() {
            return face.texture;
        }

        public Baked baked(IModelConfiguration owner, IModelTransform transform, ResourceLocation modelLocation) {
            return new Baked(owner, transform, modelLocation);
        }

        public class Baked {

            public RenderMaterial rawMaterial;
            public Supplier<BakedQuad> defaultQuad;

            private Baked(IModelConfiguration owner, IModelTransform transform, ResourceLocation modelLocation) {
                rawMaterial = owner.resolveTexture(face.texture);
                face.blockFaceUV.setUvs(getFaceUvs(direction));
                defaultQuad = Suppliers.memoize(() -> FACE_BAKERY.bakeQuad(start, end, face, rawMaterial.getSprite(), direction, transform, null, true, modelLocation));
            }

            /**
             * @see BlockPart#getFaceUvs(Direction)
             */
            public float[] getFaceUvs(Direction facing) {
                switch(facing) {
                    case DOWN:
                        return new float[] {start.getX(), 16.0F - end.getZ(), end.getX(), 16.0F - start.getZ()};
                    case UP:
                        return new float[] {start.getX(), start.getZ(), end.getX(), end.getZ()};
                    case NORTH:
                    default:
                        return new float[] {16.0F - end.getX(), 16.0F - end.getY(), 16.0F - start.getX(), 16.0F - start.getY()};
                    case SOUTH:
                        return new float[] {start.getX(), 16.0F - end.getY(), end.getX(), 16.0F - start.getY()};
                    case WEST:
                        return new float[] {start.getZ(), 16.0F - end.getY(), end.getZ(), 16.0F - start.getY()};
                    case EAST:
                        return new float[] {16.0F - end.getZ(), 16.0F - end.getY(), 16.0F - start.getZ(), 16.0F - start.getY()};
                }
            }

            public BakedQuad getQuad(ItemStack stack) {
                BakedQuad base = defaultQuad.get();
                if(stack == null || stack.isEmpty()) {
                    return base;
                }

                return new BakedQuad(base.getVertexData(), base.getTintIndex(), base.getFace(), getSprite(stack), base.func_239287_f_());
            }

            private TextureAtlasSprite getSprite(ItemStack stack) {
                return Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(stack, null, null).getParticleTexture(EmptyModelData.INSTANCE);
            }

        }

    }

    @Override
    public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
        return materials.values()
                .stream()
                .map(FramingCandidate::getRaw)
                .map(owner::resolveTexture)
                .collect(Collectors.toList());
    }

    @Override
    public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation) {
        IBakedModel parent = null;
        IUnbakedModel ownerModel = owner.getOwnerModel();
        if(ownerModel instanceof BlockModel) {
            ResourceLocation parentLoc = ((BlockModel) ownerModel).getParentLocation();
            if(parentLoc != null) {
                parent = bakery.getBakedModel(parentLoc, modelTransform, spriteGetter);
            }
        }
        return new Baked(owner,
                modelTransform,
                modelLocation,
                parent);
    }

    private class Baked implements IDynamicBakedModel {

        private final Multimap<MaterialSide, FramingCandidate.Baked> bakedSides;
        @Nullable
        private final IBakedModel parent;

        public Baked(IModelConfiguration owner, IModelTransform modelTransform, ResourceLocation modelLocation, @Nullable IBakedModel parent) {
            bakedSides = materials.entries()
                    .stream()
                    .collect(Collector.of(HashMultimap::create,
                            (map, entry) ->
                                    map.put(entry.getKey(),
                                            entry.getValue()
                                                    .baked(owner, modelTransform, modelLocation)),
                            (first, second) -> {
                                first.putAll(second);
                                return first;
                            },
                            Collector.Characteristics.UNORDERED));
            this.parent = parent;
        }

        @Nonnull
        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
            List<BakedQuad> quads = new ArrayList<>();
            for(MaterialSide material : MaterialSide.values()) {
                for(FramingCandidate.Baked baked : bakedSides.get(material)) {
                    quads.add(baked.getQuad(extraData.getData(material.property)));
                }
            }
            return quads;
        }

        @Override
        public boolean isAmbientOcclusion() {
            return parent != null && parent.isAmbientOcclusion();
        }

        @Override
        public boolean isGui3d() {
            return parent != null && parent.isGui3d();
        }

        @Override
        public boolean func_230044_c_() {
            return parent != null && parent.func_230044_c_();
        }

        @Override
        public boolean isBuiltInRenderer() {
            return false;
        }

        @Override
        public TextureAtlasSprite getParticleTexture() {
            return bakedSides.get(MaterialSide.SIDE)
                    .stream()
                    .findFirst()
                    .map(mat -> mat.rawMaterial.getSprite())
                    .orElseGet(() -> parent == null ?
                                     Minecraft.getInstance()
                                             .getAtlasSpriteGetter(PlayerContainer.LOCATION_BLOCKS_TEXTURE)
                                             .apply(MissingTextureSprite.getLocation()) :
                                     parent.getParticleTexture(EmptyModelData.INSTANCE));
        }

        @Override
        public ItemOverrideList getOverrides() {
            return parent == null ? ItemOverrideList.EMPTY : parent.getOverrides();
        }

        @Override
        public IBakedModel handlePerspective(ItemCameraTransforms.TransformType cameraTransformType, MatrixStack mat) {
            if(parent != null) parent.handlePerspective(cameraTransformType, mat);
            return this;
        }

    }

}
