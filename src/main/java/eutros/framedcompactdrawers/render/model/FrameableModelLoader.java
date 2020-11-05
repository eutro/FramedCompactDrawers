package eutros.framedcompactdrawers.render.model;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.*;
import eutros.framedcompactdrawers.render.model.FrameableModel.FramingCandidate;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import net.minecraft.client.renderer.model.BlockFaceUV;
import net.minecraft.client.renderer.model.BlockPartFace;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Direction;
import net.minecraft.util.EnumTypeAdapterFactory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.model.IModelLoader;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FrameableModelLoader implements IModelLoader<FrameableModel> {

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
    }

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapterFactory(new EnumTypeAdapterFactory())
            .registerTypeAdapter(Multimap.class, (JsonDeserializer<Multimap<?, ?>>) (json, typeOfT, context) -> {
                if(!json.isJsonObject()) {
                    throw new JsonSyntaxException("Not an object: " + json);
                }
                JsonObject obj = json.getAsJsonObject();
                HashMultimap<Object, Object> map = HashMultimap.create();
                Type keyType;
                Type valueType;
                if(typeOfT instanceof ParameterizedType) {
                    Type[] typeArguments = ((ParameterizedType) typeOfT).getActualTypeArguments();
                    keyType = typeArguments[0];
                    valueType = typeArguments[1];
                } else {
                    keyType = valueType = Object.class;
                }
                for(Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                    Object key = context.deserialize(new JsonPrimitive(entry.getKey()), keyType);
                    if(!entry.getValue().isJsonArray()) {
                        throw new JsonSyntaxException("Not an array: " + entry.getValue().toString());
                    }
                    List<Object> values = StreamSupport.stream(entry.getValue().getAsJsonArray().spliterator(), false)
                            .map(element -> context.deserialize(element, valueType))
                            .collect(Collectors.toList());
                    map.putAll(key, values);
                }
                return map;
            })
            .registerTypeAdapter(Vector3f.class, (JsonDeserializer<Vector3f>) (json, typeOfT, context) -> {
                if(!json.isJsonArray()) {
                    throw new JsonSyntaxException("Not an array: " + json);
                }
                JsonArray array = json.getAsJsonArray();
                if(array.size() != 3) {
                    throw new JsonSyntaxException("3D vector doesn't have 3 elements.");
                }
                return new Vector3f(
                        StreamSupport.stream(array.spliterator(), false)
                                .mapToDouble(el -> {
                                    if(!el.isJsonPrimitive() || !el.getAsJsonPrimitive().isNumber()) {
                                        throw new JsonSyntaxException("Not a number: " + json.toString());
                                    }
                                    return el.getAsDouble();
                                })
                                .collect(FloatArrayList::new, (fl, value) -> fl.add((float) value), FloatList::addAll)
                                .toFloatArray()
                );
            })
            .registerTypeAdapter(BlockPartFace.class, new BlockPartFace.Deserializer())
            .registerTypeAdapter(BlockFaceUV.class, new BlockFaceUV.Deserializer())
            .registerTypeAdapter(FramingCandidate.class, (JsonDeserializer<FramingCandidate>) (json, typeOfT, context) -> {
                if(!json.isJsonObject()) {
                    throw new JsonSyntaxException("Not an object: " + json.toString());
                }
                JsonObject obj = json.getAsJsonObject();
                FramingCandidate candidate = new FramingCandidate();
                FramingCandidate.Condition condition = context.deserialize(obj.get("condition"), FramingCandidate.Condition.class);
                if(condition != null)
                    candidate.condition = condition;
                candidate.face = context.deserialize(json, BlockPartFace.class);
                candidate.direction = context.deserialize(obj.get("face"), Direction.class);
                candidate.start = context.deserialize(obj.get("start"), Vector3f.class);
                candidate.end = context.deserialize(obj.get("end"), Vector3f.class);
                if(candidate.start.getX() != candidate.end.getX() &&
                        candidate.start.getY() != candidate.end.getY() &&
                        candidate.start.getZ() != candidate.end.getZ()) {
                    throw new JsonSyntaxException(String.format("Start and end points %s and %s aren't aligned on any axis!", candidate.start, candidate.end));
                }
                return candidate;
            })
            .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
            .create();

    @Override
    public FrameableModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
        return gson.fromJson(modelContents.get("frameable"), FrameableModel.class);
    }

}
