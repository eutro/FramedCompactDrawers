package eutros.framedcompactdrawers.render.model;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawers;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.RenderTypeGroup;
import net.minecraftforge.client.model.IDynamicBakedModel;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class FrameableModel implements IUnbakedGeometry<FrameableModel> {

    public static final Logger LOGGER = LogManager.getLogger();

    public ResourceLocation parent = null;
    public Multimap<MaterialSide, FramingCandidate> materials;
    public List<ResourceLocation> inherits = Collections.emptyList();

    public enum MaterialSide {
        SIDE(RenderType.cutoutMipped()),
        FRONT(RenderType.cutoutMipped()),
        TRIM(RenderType.cutoutMipped()),
        OVERLAY(RenderType.translucent());

        public final ModelProperty<ItemStack> property = new ModelProperty<>();
        private final RenderType type;

        MaterialSide(RenderType type) {
            this.type = type;
        }

        public static List<RenderType> getRenderTypes() {
            return List.of(SIDE.type, OVERLAY.type);
        }

        @Nullable
        public String getKey() {
            if (this != OVERLAY) {
                return "Mat" + name().charAt(0);
            }
            return null;
        }
    }

    public static class FramingCandidate {

        public static final FaceBakery FACE_BAKERY = new FaceBakery();

        public Vector3f start;
        public Vector3f end;
        public Direction direction;
        public BlockElementFace face;
        public Condition condition = Condition.ALWAYS;

        public Baked baked(IGeometryBakingContext context,
                           Function<Material, TextureAtlasSprite> spriteGetter,
                           ModelState state,
                           ResourceLocation modelLocation) {
            return new Baked(context, spriteGetter, state, modelLocation);
        }

        public class Baked {

            private final Function<TextureAtlasSprite, BakedQuad> quadSupplier;
            private final Cache<TextureAtlasSprite, BakedQuad> quadCache =
                    CacheBuilder.newBuilder()
                            .expireAfterAccess(60, TimeUnit.SECONDS)
                            .build();
            private final TextureAtlasSprite rawSprite;

            public FramingCandidate getEnclosing() {
                return FramingCandidate.this;
            }

            private Baked(IGeometryBakingContext context,
                          Function<Material, TextureAtlasSprite> spriteGetter,
                          ModelState state,
                          ResourceLocation modelLocation) {
                Material rawMaterial = context.getMaterial(face.texture);
                rawSprite = spriteGetter.apply(rawMaterial);
                face.uv.setMissingUv(getFaceUvs(direction));
                quadSupplier = sprite -> FACE_BAKERY.bakeQuad(start, end, face, sprite, direction, state, null, true, modelLocation);
            }

            /**
             * BlockPart#getFaceUvs(Direction)
             */
            public float[] getFaceUvs(Direction facing) {
                return switch (facing) {
                    case DOWN -> new float[]{start.x(), 16.0F - end.z(), end.x(), 16.0F - start.z()};
                    case UP -> new float[]{start.x(), start.z(), end.x(), end.z()};
                    case NORTH -> new float[]{16.0F - end.x(), 16.0F - end.y(), 16.0F - start.x(), 16.0F - start.y()};
                    case SOUTH -> new float[]{start.x(), 16.0F - end.y(), end.x(), 16.0F - start.y()};
                    case WEST -> new float[]{start.z(), 16.0F - end.y(), end.z(), 16.0F - start.y()};
                    case EAST -> new float[]{16.0F - end.z(), 16.0F - end.y(), 16.0F - start.z(), 16.0F - start.y()};
                };
            }

            public BakedQuad getQuad(@Nullable ItemStack stack) {
                TextureAtlasSprite sprite = getSpriteOrRaw(stack);
                try {
                    return quadCache.get(sprite, () -> quadSupplier.apply(sprite));
                } catch (ExecutionException e) {
                    return quadSupplier.apply(sprite);
                }
            }

            public TextureAtlasSprite getSpriteOrRaw(@Nullable ItemStack stack) {
                return stack == null || stack.isEmpty() ?
                        rawSprite :
                        getSprite(stack);
            }

            private TextureAtlasSprite getSprite(ItemStack stack) {
                return Minecraft.getInstance()
                        .getItemRenderer()
                        .getModel(stack, null, null, 0)
                        .getParticleIcon(ModelData.EMPTY);
            }

        }

        @SuppressWarnings("unused")
        public enum Condition implements Predicate<ModelData> {
            LOCKED(data -> {
                IDrawerAttributes attr = data.get(BlockEntityDrawers.ATTRIBUTES);
                return attr != null &&
                        (attr.isItemLocked(LockAttribute.LOCK_EMPTY) ||
                                attr.isItemLocked(LockAttribute.LOCK_POPULATED));
            }),
            ALWAYS(data -> true);

            private final Predicate<ModelData> predicate;

            Condition(Predicate<ModelData> predicate) {
                this.predicate = predicate;
            }

            @Override
            public boolean test(ModelData data) {
                return predicate.test(data);
            }
        }

    }

    @Override
    public BakedModel bake(IGeometryBakingContext context,
                           ModelBaker baker,
                           Function<Material, TextureAtlasSprite> spriteGetter,
                           ModelState modelState,
                           ItemOverrides overrides,
                           ResourceLocation modelLocation) {
        BakedModel parent = null;
        if (this.parent != null) {
            parent = baker.bake(this.parent, modelState, spriteGetter);
        }
        HashMultimap<MaterialSide, FramingCandidate.Baked> bakedSides = materials.entries()
                .stream()
                .collect(Collector.of(HashMultimap::create,
                        (map, entry) ->
                                map.put(entry.getKey(),
                                        entry.getValue()
                                                .baked(context, spriteGetter, modelState, modelLocation)),
                        (first, second) -> {
                            first.putAll(second);
                            return first;
                        },
                        Collector.Characteristics.UNORDERED));
        for (ResourceLocation rl : inherits) {
            BakedModel baked = baker.bake(rl, modelState, spriteGetter);
            if (baked instanceof Baked) {
                for (Map.Entry<MaterialSide, FramingCandidate.Baked> entry : ((Baked) baked).bakedSides.entries()) {
                    bakedSides.put(entry.getKey(), entry.getValue());
                }
            } else {
                LOGGER.warn("Inherited model must be a frameable model! Got: " + (baked == null ?
                        "null" :
                        baked.getClass()));
            }
        }
        RenderTypeGroup rtg = context.getRenderType(new ResourceLocation("translucent"));
        return new Baked(parent, bakedSides, List.of(rtg.entity()), List.of(rtg.entityFabulous()));
    }

    private static class Baked implements IDynamicBakedModel {

        final Multimap<MaterialSide, FramingCandidate.Baked> bakedSides;
        @Nullable
        private final BakedModel parent;
        private final ItemOverrides overrides;
        private final List<RenderType> itemRenderTypes, itemRenderTypesFabulous;

        public Baked(@Nullable BakedModel parent,
                     Multimap<MaterialSide, FramingCandidate.Baked> bakedSides,
                     List<RenderType> itemRenderTypes,
                     List<RenderType> itemRenderTypesFabulous) {
            this.bakedSides = bakedSides;
            this.parent = parent;
            this.itemRenderTypes = itemRenderTypes;
            this.itemRenderTypesFabulous = itemRenderTypesFabulous;
            overrides = new ItemOverrides() {
                @Nonnull
                @Override
                @ParametersAreNonnullByDefault
                public BakedModel resolve(BakedModel model,
                                          ItemStack stack,
                                          @Nullable ClientLevel world,
                                          @Nullable LivingEntity entity,
                                          int idkMan) {
                    return new Baked(parent, bakedSides, itemRenderTypes, itemRenderTypesFabulous) {
                        @Nonnull
                        @Override
                        public List<BakedQuad> getQuads(@Nullable BlockState state,
                                                        @Nullable Direction side,
                                                        @Nonnull RandomSource rand,
                                                        @Nonnull ModelData extraData,
                                                        @Nullable RenderType layer) {
                            if (stack.hasTag()) {
                                CompoundTag tag = Objects.requireNonNull(stack.getTag());
                                ModelData.Builder builder = ModelData.builder();
                                for (MaterialSide material : MaterialSide.values()) {
                                    String key = material.getKey();
                                    if (key != null && tag.contains(key)) {
                                        builder.with(material.property, ItemStack.of(tag.getCompound(key)));
                                    }
                                }
                                extraData = builder.build();
                            }
                            return super.getQuads(state, side, rand, extraData, layer);
                        }

                        @Nonnull
                        @Override
                        public ItemOverrides getOverrides() {
                            return EMPTY;
                        }
                    };
                }
            };
        }

        @Nonnull
        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state,
                                        @Nullable Direction side,
                                        @Nonnull RandomSource rand,
                                        @Nonnull ModelData extraData,
                                        @Nullable RenderType layer) {
            List<BakedQuad> quads = new ArrayList<>();
            for (MaterialSide material : MaterialSide.values()) {
                if (layer != null && material.type != layer) continue;
                for (FramingCandidate.Baked baked : bakedSides.get(material)) {
                    if (baked.getEnclosing().face.cullForDirection == side &&
                            baked.getEnclosing().condition.test(extraData))
                        quads.add(baked.getQuad(resolve(extraData, material)));
                }
            }
            return quads;
        }

        @Nullable
        private ItemStack resolve(ModelData data, MaterialSide material) {
            if (material == MaterialSide.OVERLAY) {
                return null;
            }
            ItemStack stack = data.get(material.property);
            if (material != MaterialSide.SIDE && (stack == null || stack.isEmpty())) {
                return resolve(data, MaterialSide.SIDE);
            }
            return stack;
        }

        @Override
        public boolean useAmbientOcclusion() {
            return parent != null && parent.useAmbientOcclusion();
        }

        @Override
        public boolean isGui3d() {
            return parent != null && parent.isGui3d();
        }

        @Override
        public boolean usesBlockLight() {
            return parent != null && parent.usesBlockLight();
        }

        @Override
        public boolean isCustomRenderer() {
            return false;
        }

        @SuppressWarnings("deprecated")
        @Override
        public TextureAtlasSprite getParticleIcon() {
            return getParticleIcon(ModelData.EMPTY);
        }

        @Override
        public @NotNull TextureAtlasSprite getParticleIcon(@NotNull ModelData data) {
            return bakedSides.get(MaterialSide.SIDE)
                    .stream()
                    .findFirst()
                    .map(mat -> mat.getSpriteOrRaw(resolve(data, MaterialSide.SIDE)))
                    .orElseGet(() -> parent == null ?
                            Minecraft.getInstance()
                                    .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                                    .apply(MissingTextureAtlasSprite.getLocation()) :
                            parent.getParticleIcon(ModelData.EMPTY));
        }

        @Override
        public ItemOverrides getOverrides() {
            return overrides;
        }

        @SuppressWarnings("deprecation") // shut UP
        @Override
        public ItemTransforms getTransforms() {
            if (parent != null) return parent.getTransforms();
            return ItemTransforms.NO_TRANSFORMS;
        }

        @Override
        public ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data) {
            return ChunkRenderTypeSet.of(MaterialSide.getRenderTypes());
        }

        @Override
        public List<RenderType> getRenderTypes(ItemStack itemStack, boolean fabulous) {
            return fabulous ? itemRenderTypesFabulous : itemRenderTypes;
        }
    }
}
