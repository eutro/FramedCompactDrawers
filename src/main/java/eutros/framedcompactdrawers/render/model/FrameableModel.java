package eutros.framedcompactdrawers.render.model;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.data.*;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class FrameableModel implements IModelGeometry<FrameableModel> {

    public static final Logger LOGGER = LogManager.getLogger();

    public Multimap<MaterialSide, FramingCandidate> materials;
    public List<ResourceLocation> inherits = Collections.emptyList();

    public enum MaterialSide {
        SIDE(RenderType.cutout()),
        FRONT(RenderType.cutout()),
        TRIM(RenderType.cutout()),
        OVERLAY(RenderType.translucent());

        public final ModelProperty<ItemStack> property = new ModelProperty<>();
        private final RenderType type;

        MaterialSide(RenderType type) {
            this.type = type;
        }

        @Nullable
        public String getKey() {
            if(this != OVERLAY) {
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

        public String getRaw() {
            return face.texture;
        }

        public Baked baked(IModelConfiguration owner, ModelState transform, ResourceLocation modelLocation) {
            return new Baked(owner, transform, modelLocation);
        }

        public class Baked {

            private final Function<TextureAtlasSprite, BakedQuad> quadSupplier;
            private final Cache<TextureAtlasSprite, BakedQuad> quadCache =
                    CacheBuilder.newBuilder()
                            .expireAfterAccess(60, TimeUnit.SECONDS)
                            .build();
            public Material rawMaterial;

            public FramingCandidate getEnclosing() {
                return FramingCandidate.this;
            }

            private Baked(IModelConfiguration owner, ModelState transform, ResourceLocation modelLocation) {
                rawMaterial = owner.resolveTexture(face.texture);
                face.uv.setMissingUv(getFaceUvs(direction));
                quadSupplier = sprite -> FACE_BAKERY.bakeQuad(start, end, face, sprite, direction, transform, null, true, modelLocation);
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

            public BakedQuad getQuad(ItemStack stack) {
                TextureAtlasSprite sprite = stack == null || stack.isEmpty() ?
                                            rawMaterial.sprite() :
                                            getSprite(stack);
                try {
                    return quadCache.get(sprite, () -> quadSupplier.apply(sprite));
                } catch(ExecutionException e) {
                    return quadSupplier.apply(sprite);
                }
            }

            private TextureAtlasSprite getSprite(ItemStack stack) {
                return Minecraft.getInstance()
                        .getItemRenderer()
                        .getModel(stack, null, null, 0)
                        .getParticleIcon(EmptyModelData.INSTANCE);
            }

        }

        @SuppressWarnings("unused")
        public enum Condition implements Predicate<IModelData> {
            LOCKED(data -> {
                IDrawerAttributes attr = data.getData(TileEntityDrawers.ATTRIBUTES);
                return attr != null &&
                        (attr.isItemLocked(LockAttribute.LOCK_EMPTY) ||
                                attr.isItemLocked(LockAttribute.LOCK_POPULATED));
            }),
            ALWAYS(data -> true);

            private final Predicate<IModelData> predicate;

            Condition(Predicate<IModelData> predicate) {
                this.predicate = predicate;
            }

            @Override
            public boolean test(IModelData data) {
                return predicate.test(data);
            }
        }

    }

    @Override
    public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
        return materials.values()
                .stream()
                .map(FramingCandidate::getRaw)
                .map(owner::resolveTexture)
                .collect(Collectors.toList());
    }

    @Override
    public BakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation) {
        BakedModel parent = null;
        UnbakedModel ownerModel = owner.getOwnerModel();
        if(ownerModel instanceof BlockModel) {
            ResourceLocation parentLoc = ((BlockModel) ownerModel).getParentLocation();
            if(parentLoc != null) {
                parent = bakery.bake(parentLoc, modelTransform, spriteGetter);
            }
        }
        HashMultimap<MaterialSide, FramingCandidate.Baked> bakedSides = materials.entries()
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
        for(ResourceLocation rl : inherits) {
            BakedModel baked = bakery.bake(rl, modelTransform, spriteGetter);
            if(baked instanceof Baked) {
                for(Map.Entry<MaterialSide, FramingCandidate.Baked> entry : ((Baked) baked).bakedSides.entries()) {
                    bakedSides.put(entry.getKey(), entry.getValue());
                }
            } else {
                LOGGER.warn("Inherited model must be a frameable model! Got: " + (baked == null ?
                                                                                  "null" :
                                                                                  baked.getClass()));
            }
        }
        return new Baked(parent, bakedSides);
    }

    private static class Baked implements IDynamicBakedModel {

        final Multimap<MaterialSide, FramingCandidate.Baked> bakedSides;
        @Nullable
        private final BakedModel parent;
        private final ItemOverrides overrides;

        public Baked(@Nullable BakedModel parent, Multimap<MaterialSide, FramingCandidate.Baked> bakedSides) {
            this.bakedSides = bakedSides;
            this.parent = parent;
            overrides = new ItemOverrides() {
                @Nonnull
                @Override
                @ParametersAreNonnullByDefault
                public BakedModel resolve(BakedModel model, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int idkMan) {
                    return new Baked(parent, bakedSides) {
                        @Nonnull
                        @Override
                        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
                            if(stack.hasTag()) {
                                CompoundTag tag = Objects.requireNonNull(stack.getTag());
                                ModelDataMap.Builder builder = new ModelDataMap.Builder();
                                for(MaterialSide material : MaterialSide.values()) {
                                    String key = material.getKey();
                                    if(key != null && tag.contains(key))
                                        builder.withInitial(material.property, ItemStack.of(tag.getCompound(key)));
                                }
                                extraData = builder.build();
                            }
                            return super.getQuads(state, side, rand, extraData);
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
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
            List<BakedQuad> quads = new ArrayList<>();
            RenderType layer = MinecraftForgeClient.getRenderLayer();
            for(MaterialSide material : MaterialSide.values()) {
                if(layer != null && material.type != layer) continue;
                for(FramingCandidate.Baked baked : bakedSides.get(material)) {
                    if(baked.getEnclosing().face.cullForDirection == side &&
                            baked.getEnclosing().condition.test(extraData))
                        quads.add(baked.getQuad(resolve(extraData, material)));
                }
            }
            return quads;
        }

        private ItemStack resolve(IModelData data, MaterialSide material) {
            if(material == MaterialSide.OVERLAY) {
                return null;
            }
            ItemStack stack = data.getData(material.property);
            if(material != MaterialSide.SIDE && (stack == null || stack.isEmpty())) {
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

        @Nonnull
        @Override
        public TextureAtlasSprite getParticleIcon() {
            return bakedSides.get(MaterialSide.SIDE)
                    .stream()
                    .findFirst()
                    .map(mat -> mat.rawMaterial.sprite())
                    .orElseGet(() -> parent == null ?
                                     Minecraft.getInstance()
                                             .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                                             .apply(MissingTextureAtlasSprite.getLocation()) :
                                     parent.getParticleIcon(EmptyModelData.INSTANCE));
        }

        @Nonnull
        @Override
        public ItemOverrides getOverrides() {
            return overrides;
        }

        @Override
        public BakedModel handlePerspective(ItemTransforms.TransformType cameraTransformType, PoseStack mat) {
            if(parent != null) parent.handlePerspective(cameraTransformType, mat);
            return this;
        }

    }

}
