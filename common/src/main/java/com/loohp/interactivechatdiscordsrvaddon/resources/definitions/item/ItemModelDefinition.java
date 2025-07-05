/*
 * This file is part of InteractiveChatDiscordSrvAddon2.
 *
 * Copyright (C) 2020 - 2025. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2020 - 2025. Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.loohp.interactivechatdiscordsrvaddon.resources.definitions.item;

import com.ibm.icu.util.TimeZone;
import com.loohp.blockmodelrenderer.utils.ColorUtils;
import com.loohp.interactivechat.libs.net.kyori.adventure.key.Key;
import com.loohp.interactivechat.libs.org.json.simple.JSONArray;
import com.loohp.interactivechat.libs.org.json.simple.JSONObject;
import com.loohp.interactivechat.libs.org.json.simple.parser.ParseException;
import com.loohp.interactivechatdiscordsrvaddon.nms.NMSAddon;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.ChargeType;
import com.loohp.interactivechatdiscordsrvaddon.registry.ResourceRegistry;
import com.loohp.interactivechatdiscordsrvaddon.resources.models.ModelDisplay;
import com.loohp.interactivechatdiscordsrvaddon.utils.KeyUtils;
import com.loohp.interactivechatdiscordsrvaddon.utils.ThrowingFunction;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.bukkit.inventory.MainHand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class ItemModelDefinition {

    private final ItemModelDefinitionType<?> type;
    private final boolean handAnimationOnSwap;
    private final boolean oversizedInGui;

    public ItemModelDefinition(ItemModelDefinitionType<?> type, boolean handAnimationOnSwap, boolean oversizedInGui) {
        this.type = type;
        this.handAnimationOnSwap = handAnimationOnSwap;
        this.oversizedInGui = oversizedInGui;
    }

    public ItemModelDefinitionType<?> getType() {
        return type;
    }

    public boolean isHandAnimationOnSwap() {
        return handAnimationOnSwap;
    }

    public boolean isOversizedInGui() {
        return oversizedInGui;
    }

    public static ItemModelDefinition fromJson(JSONObject rootJson) throws ParseException {
        boolean handAnimationOnSwapString = (boolean) rootJson.getOrDefault("hand_animation_on_swap", true);
        boolean oversizedInGui = (boolean) rootJson.getOrDefault("oversized_in_gui", false);
        String typeKey = ensureNamespace((String) rootJson.get("type"));
        ItemModelDefinitionType<?> type = ItemModelDefinitionType.getItemModelDefinitionType(typeKey);

        if (type == ItemModelDefinitionType.MODEL) {
            String model = ensureNamespace((String) rootJson.get("model"));
            JSONArray tintsArray = (JSONArray) rootJson.get("tints");
            List<TintSource> tints = new ArrayList<>();
            if (tintsArray != null) {
                for (Object tintObj : tintsArray) {
                    JSONObject tintJson = (JSONObject) tintObj;
                    tints.add(TintSource.fromJson(tintJson));
                }
            }
            return new ItemModelDefinitionModel(handAnimationOnSwapString, oversizedInGui, model, tints);
        } else if (type == ItemModelDefinitionType.COMPOSITE) {
            JSONArray modelsArray = (JSONArray) rootJson.get("models");
            List<ItemModelDefinition> models = new ArrayList<>();
            for (Object modelObj : modelsArray) {
                models.add(ItemModelDefinition.fromJson((JSONObject) modelObj));
            }
            return new ItemModelDefinitionComposite(handAnimationOnSwapString, oversizedInGui, models);
        } else if (type == ItemModelDefinitionType.CONDITION) {
            String propertyKey = ensureNamespace((String) rootJson.get("property"));
            ConditionPropertyType<?> propertyType = ConditionPropertyType.getConditionPropertyType(propertyKey);
            JSONObject onTrueJson = (JSONObject) rootJson.get("on_true");
            JSONObject onFalseJson = (JSONObject) rootJson.get("on_false");
            ItemModelDefinition onTrue = onTrueJson != null ? ItemModelDefinition.fromJson(onTrueJson) : null;
            ItemModelDefinition onFalse = onFalseJson != null ? ItemModelDefinition.fromJson(onFalseJson) : null;
            if (propertyType.equals(ConditionPropertyType.USING_ITEM)) {
                return new UsingItemConditionProperty(handAnimationOnSwapString, oversizedInGui, propertyType, onTrue, onFalse);
            } else if (propertyType.equals(ConditionPropertyType.BROKEN)) {
                return new BrokenConditionProperty(handAnimationOnSwapString, oversizedInGui, propertyType, onTrue, onFalse);
            } else if (propertyType.equals(ConditionPropertyType.DAMAGED)) {
                return new DamagedConditionProperty(handAnimationOnSwapString, oversizedInGui, propertyType, onTrue, onFalse);
            } else if (propertyType.equals(ConditionPropertyType.HAS_COMPONENT)) {
                Key component = KeyUtils.toKey((String) rootJson.get("component"));
                boolean ignoreDefault = (boolean) rootJson.getOrDefault("ignore_default", false);
                return new HasComponentConditionProperty(handAnimationOnSwapString, oversizedInGui, propertyType, onTrue, onFalse, component, ignoreDefault);
            } else if (propertyType.equals(ConditionPropertyType.FISHING_ROD_CAST)) {
                return new FishingRodCastConditionProperty(handAnimationOnSwapString, oversizedInGui, propertyType, onTrue, onFalse);
            } else if (propertyType.equals(ConditionPropertyType.BUNDLE_SELECTED_ITEM)) {
                return new BundleSelectedItemConditionProperty(handAnimationOnSwapString, oversizedInGui, propertyType, onTrue, onFalse);
            } else if (propertyType.equals(ConditionPropertyType.SELECTED)) {
                return new SelectedConditionProperty(handAnimationOnSwapString, oversizedInGui, propertyType, onTrue, onFalse);
            } else if (propertyType.equals(ConditionPropertyType.CARRIED)) {
                return new CarriedConditionProperty(handAnimationOnSwapString, oversizedInGui, propertyType, onTrue, onFalse);
            } else if (propertyType.equals(ConditionPropertyType.EXTENDED_VIEW)) {
                return new ExtendedViewConditionProperty(handAnimationOnSwapString, oversizedInGui, propertyType, onTrue, onFalse);
            } else if (propertyType.equals(ConditionPropertyType.KEYBIND_DOWN)) {
                int keybind = ((Number) rootJson.get("keybind")).intValue();
                return new KeybindDownConditionProperty(handAnimationOnSwapString, oversizedInGui, propertyType, onTrue, onFalse, keybind);
            } else if (propertyType.equals(ConditionPropertyType.VIEW_ENTITY)) {
                return new ViewEntityConditionProperty(handAnimationOnSwapString, oversizedInGui, propertyType, onTrue, onFalse);
            } else if (propertyType.equals(ConditionPropertyType.COMPONENT)) {
                String predicate = (String) rootJson.get("predicate");
                String value = (String) rootJson.get("value");
                return new ComponentConditionProperty(handAnimationOnSwapString, oversizedInGui, propertyType, onTrue, onFalse, predicate, value);
            } else if (propertyType.equals(ConditionPropertyType.CUSTOM_MODEL_DATA)) {
                int index = ((Number) rootJson.getOrDefault("index", 0)).intValue();
                return new CustomModelDataConditionProperty(handAnimationOnSwapString, oversizedInGui, propertyType, onTrue, onFalse, index);
            } else {
                throw new IllegalArgumentException("Unknown condition property type: " + propertyKey);
            }
        } else if (type == ItemModelDefinitionType.SELECT) {
            String propertyKey = ensureNamespace((String) rootJson.get("property"));
            SelectPropertyType<?> propertyType = SelectPropertyType.getSelectPropertyType(propertyKey);
            List<UnparsedSelectCase> cases = new ArrayList<>();
            JSONArray casesJson = (JSONArray) rootJson.get("cases");
            for (Object caseObj : casesJson) {
                JSONObject caseModelJson = (JSONObject) caseObj;
                Object rawWhen = caseModelJson.get("when");
                List<String> whens;
                if (rawWhen instanceof JSONArray) {
                    whens = new ArrayList<>();
                    for (Object element : (JSONArray) rawWhen) {
                        whens.add((String) element);
                    }
                } else {
                    whens = Collections.singletonList((String) rawWhen);
                }
                cases.add(new UnparsedSelectCase(whens, ItemModelDefinition.fromJson((JSONObject) caseModelJson.get("model"))));
            }
            JSONObject fallbackJson = (JSONObject) rootJson.get("fallback");
            ItemModelDefinition fallback = fallbackJson != null ? ItemModelDefinition.fromJson(fallbackJson) : null;
            if (propertyType.equals(SelectPropertyType.MAIN_HAND)) {
                return new MainHandSelectProperty(handAnimationOnSwapString, oversizedInGui, propertyType, parse(cases, c -> MainHand.valueOf(c.toUpperCase())), fallback);
            } else if (propertyType.equals(SelectPropertyType.CHARGE_TYPE)) {
                return new ChargeTypeSelectProperty(handAnimationOnSwapString, oversizedInGui, propertyType, parse(cases, c -> ChargeType.fromName(c)), fallback);
            } else if (propertyType.equals(SelectPropertyType.COMPONENT)) {
                Key component = KeyUtils.toKey(ensureNamespace((String) rootJson.get("component")));
                return new ComponentSelectProperty(handAnimationOnSwapString, oversizedInGui, propertyType, parse(cases, c -> NMSAddon.getInstance().serializeDataComponent(component, c)), fallback, component);
            } else if (propertyType.equals(SelectPropertyType.TRIM_MATERIAL)) {
                return new TrimMaterialSelectProperty(handAnimationOnSwapString, oversizedInGui, propertyType, parse(cases, c -> KeyUtils.toKey(c)), fallback);
            } else if (propertyType.equals(SelectPropertyType.BLOCK_STATE)) {
                String blockStateProperty = (String) rootJson.get("block_state_property");
                return new BlockStateSelectProperty(handAnimationOnSwapString, oversizedInGui, propertyType, parse(cases, c -> c), fallback, blockStateProperty);
            } else if (propertyType.equals(SelectPropertyType.DISPLAY_CONTEXT)) {
                return new DisplayContextSelectProperty(handAnimationOnSwapString, oversizedInGui, propertyType, parse(cases, c -> ModelDisplay.ModelDisplayPosition.fromKey(c)), fallback);
            } else if (propertyType.equals(SelectPropertyType.LOCAL_TIME)) {
                String pattern = (String) rootJson.getOrDefault("pattern", "");
                String locale = (String) rootJson.getOrDefault("locale", "");
                TimeZone timeZone = TimeZone.getTimeZone((String) rootJson.getOrDefault("time_zone", ""));
                Optional<TimeZone> optTimeZone = timeZone == TimeZone.UNKNOWN_ZONE ? Optional.empty() : Optional.of(timeZone);
                return new LocalTimeSelectProperty(handAnimationOnSwapString, oversizedInGui, propertyType, parse(cases, c -> c), fallback, pattern, locale, optTimeZone);
            } else if (propertyType.equals(SelectPropertyType.CONTEXT_DIMENSION)) {
                return new ContextDimensionSelectProperty(handAnimationOnSwapString, oversizedInGui, propertyType, parse(cases, c -> KeyUtils.toKey(c)), fallback);
            } else if (propertyType.equals(SelectPropertyType.CONTEXT_ENTITY_TYPE)) {
                return new ContextEntityTypeSelectProperty(handAnimationOnSwapString, oversizedInGui, propertyType, parse(cases, c -> KeyUtils.toKey(c)), fallback);
            } else if (propertyType.equals(SelectPropertyType.CUSTOM_MODEL_DATA)) {
                int index = ((Number) rootJson.getOrDefault("index", 0)).intValue();
                return new CustomModelDataSelectProperty(handAnimationOnSwapString, oversizedInGui, propertyType, parse(cases, c -> c), fallback, index);
            } else {
                throw new IllegalArgumentException("Unknown select property type: " + propertyKey);
            }
        } else if (type == ItemModelDefinitionType.RANGE_DISPATCH) {
            String propertyKey = ensureNamespace((String) rootJson.get("property"));
            RangeDispatchPropertyType<?> propertyType = RangeDispatchPropertyType.getRangeDispatchPropertyType(propertyKey);
            float scale = ((Number) rootJson.getOrDefault("scale", 1.0)).floatValue();
            JSONArray entriesArray = (JSONArray) rootJson.get("entries");
            List<ItemModelDefinitionRangeDispatch.RangeEntry> entries = new ArrayList<>();
            for (Object entryObj : entriesArray) {
                JSONObject entryJson = (JSONObject) entryObj;
                float threshold = ((Number) entryJson.get("threshold")).floatValue();
                JSONObject modelJson = (JSONObject) entryJson.get("model");
                ItemModelDefinition model = ItemModelDefinition.fromJson(modelJson);
                entries.add(new ItemModelDefinitionRangeDispatch.RangeEntry(threshold, model));
            }
            JSONObject fallbackJson = (JSONObject) rootJson.get("fallback");
            ItemModelDefinition fallback = fallbackJson != null ? ItemModelDefinition.fromJson(fallbackJson) : null;
            if (propertyType.equals(RangeDispatchPropertyType.BUNDLE_FULLNESS)) {
                return new BundleFullnessRangeDispatchProperty(handAnimationOnSwapString, oversizedInGui, propertyType, scale, entries, fallback);
            } else if (propertyType.equals(RangeDispatchPropertyType.DAMAGE)) {
                boolean normalize = (boolean) rootJson.getOrDefault("normalize", true);
                return new DamageRangeDispatchProperty(handAnimationOnSwapString, oversizedInGui, propertyType, scale, entries, fallback, normalize);
            } else if (propertyType.equals(RangeDispatchPropertyType.COUNT)) {
                boolean normalize = (boolean) rootJson.getOrDefault("normalize", true);
                return new CountRangeDispatchProperty(handAnimationOnSwapString, oversizedInGui, propertyType, scale, entries, fallback, normalize);
            } else if (propertyType.equals(RangeDispatchPropertyType.COOLDOWN)) {
                return new CooldownRangeDispatchProperty(handAnimationOnSwapString, oversizedInGui, propertyType, scale, entries, fallback);
            } else if (propertyType.equals(RangeDispatchPropertyType.TIME)) {
                TimeRangeDispatchProperty.TimeSource timeSource = TimeRangeDispatchProperty.TimeSource.valueOf(((String) rootJson.get("source")).toUpperCase());
                boolean wobble = (boolean) rootJson.getOrDefault("wobble", true);
                return new TimeRangeDispatchProperty(handAnimationOnSwapString, oversizedInGui, propertyType, scale, entries, fallback, timeSource, wobble);
            } else if (propertyType.equals(RangeDispatchPropertyType.COMPASS)) {
                CompassRangeDispatchProperty.CompassTarget target = CompassRangeDispatchProperty.CompassTarget.valueOf(((String) rootJson.get("target")).toUpperCase());
                boolean wobble = (boolean) rootJson.getOrDefault("wobble", true);
                return new CompassRangeDispatchProperty(handAnimationOnSwapString, oversizedInGui, propertyType, scale, entries, fallback, target, wobble);
            } else if (propertyType.equals(RangeDispatchPropertyType.CROSSBOW_PULL)) {
                return new CrossbowPullRangeDispatchProperty(handAnimationOnSwapString, oversizedInGui, propertyType, scale, entries, fallback);
            } else if (propertyType.equals(RangeDispatchPropertyType.USE_DURATION)) {
                boolean remaining = (boolean) rootJson.getOrDefault("remaining", false);
                return new UseDurationRangeDispatchProperty(handAnimationOnSwapString, oversizedInGui, propertyType, scale, entries, fallback, remaining);
            } else if (propertyType.equals(RangeDispatchPropertyType.USE_CYCLE)) {
                float period = ((Number) rootJson.getOrDefault("period", 1.0)).floatValue();
                return new UseCycleRangeDispatchProperty(handAnimationOnSwapString, oversizedInGui, propertyType, scale, entries, fallback, period);
            } else if (propertyType.equals(RangeDispatchPropertyType.CUSTOM_MODEL_DATA)) {
                int index = ((Number) rootJson.getOrDefault("index", 0)).intValue();
                return new CustomModelDataRangeDispatchProperty(handAnimationOnSwapString, oversizedInGui, propertyType, scale, entries, fallback, index);
            } else {
                throw new IllegalArgumentException("Unknown range_dispatch property type: " + propertyKey);
            }
        } else if (type == ItemModelDefinitionType.EMPTY) {
            return new ItemModelDefinitionEmpty(handAnimationOnSwapString, oversizedInGui);
        } else if (type == ItemModelDefinitionType.BUNDLE_SELECTED_ITEM) {
            return new ItemModelDefinitionBundleSelectedItem(handAnimationOnSwapString, oversizedInGui);
        } else if (type == ItemModelDefinitionType.SPECIAL) {
            String base = (String) rootJson.get("base");
            JSONObject modelJson = (JSONObject) rootJson.get("model");
            String modelTypeKey = ensureNamespace((String) modelJson.get("type"));
            SpecialModelType<?> specialModelType = SpecialModelType.getSpecialModelType(modelTypeKey);
            if (specialModelType.equals(SpecialModelType.BANNER)) {
                String color = (String) modelJson.get("color");
                return new ItemModelDefinitionSpecial(handAnimationOnSwapString, oversizedInGui, base, new BannerSpecialModel(color));
            } else if (specialModelType.equals(SpecialModelType.BED)) {
                String texture = ensureNamespace((String) modelJson.get("texture"));
                return new ItemModelDefinitionSpecial(handAnimationOnSwapString, oversizedInGui, base, new BedSpecialModel(texture));
            } else if (specialModelType.equals(SpecialModelType.CONDUIT)) {
                return new ItemModelDefinitionSpecial(handAnimationOnSwapString, oversizedInGui, base, new ConduitSpecialModel());
            } else if (specialModelType.equals(SpecialModelType.DECORATED_POT)) {
                return new ItemModelDefinitionSpecial(handAnimationOnSwapString, oversizedInGui, base, new DecoratedPotSpecialModel());
            } else if (specialModelType.equals(SpecialModelType.CHEST)) {
                String texture = ensureNamespace((String) modelJson.get("texture"));
                float openness = ((Number) modelJson.getOrDefault("openness", 0.0)).floatValue();
                return new ItemModelDefinitionSpecial(handAnimationOnSwapString, oversizedInGui, base, new ChestSpecialModel(texture, openness));
            } else if (specialModelType.equals(SpecialModelType.HEAD)) {
                String kind = (String) modelJson.get("kind");
                String texture = ensureNamespace((String) modelJson.get("texture"));
                float animation = ((Number) modelJson.getOrDefault("animation", 0.0)).floatValue();
                return new ItemModelDefinitionSpecial(handAnimationOnSwapString, oversizedInGui, base, new HeadSpecialModel(kind, texture, animation));
            } else if (specialModelType.equals(SpecialModelType.PLAYER_HEAD)) {
                return new ItemModelDefinitionSpecial(handAnimationOnSwapString, oversizedInGui, base, new PlayerHeadSpecialModel());
            } else if (specialModelType.equals(SpecialModelType.SHULKER_BOX)) {
                String name = ensureNamespace((String) modelJson.get("name"));
                float openness = ((Number) modelJson.getOrDefault("openness", 0.0)).floatValue();
                String orientation = (String) modelJson.getOrDefault("orientation", "up");
                return new ItemModelDefinitionSpecial(handAnimationOnSwapString, oversizedInGui, base, new ShulkerBoxSpecialModel(name, openness, orientation));
            } else if (specialModelType.equals(SpecialModelType.SHIELD)) {
                return new ItemModelDefinitionSpecial(handAnimationOnSwapString, oversizedInGui, base, new ShieldSpecialModel());
            } else if (specialModelType.equals(SpecialModelType.STANDING_SIGN)) {
                String woodType = (String) modelJson.get("wood_type");
                String texture = ensureNamespace((String) modelJson.get("texture"));
                return new ItemModelDefinitionSpecial(handAnimationOnSwapString, oversizedInGui, base, new StandingSignSpecialModel(woodType, texture));
            } else if (specialModelType.equals(SpecialModelType.HANGING_SIGN)) {
                String woodType = (String) modelJson.get("wood_type");
                String texture = ensureNamespace((String) modelJson.get("texture"));
                return new ItemModelDefinitionSpecial(handAnimationOnSwapString, oversizedInGui, base, new HangingSignSpecialModel(woodType, texture));
            } else if (specialModelType.equals(SpecialModelType.TRIDENT)) {
                return new ItemModelDefinitionSpecial(handAnimationOnSwapString, oversizedInGui, base, new TridentSpecialModel());
            } else {
                throw new IllegalArgumentException("Unsupported special model type: " + modelTypeKey);
            }
        }

        throw new IllegalArgumentException("Unsupported item model definition type: " + typeKey);
    }

    private static String ensureNamespace(String key) {
        if (key == null) {
            return null;
        }
        if (!key.contains(":")) {
            return ResourceRegistry.DEFAULT_NAMESPACE + ":" + key;
        }
        return key;
    }

    @SuppressWarnings("rawtypes")
    public static class ItemModelDefinitionType<T extends ItemModelDefinition> {

        public static final ItemModelDefinitionType<ItemModelDefinitionModel> MODEL = new ItemModelDefinitionType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":model", ItemModelDefinitionModel.class);
        public static final ItemModelDefinitionType<ItemModelDefinitionComposite> COMPOSITE = new ItemModelDefinitionType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":composite", ItemModelDefinitionComposite.class);
        public static final ItemModelDefinitionType<ItemModelDefinitionCondition> CONDITION = new ItemModelDefinitionType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":condition", ItemModelDefinitionCondition.class);
        public static final ItemModelDefinitionType<ItemModelDefinitionSelect> SELECT = new ItemModelDefinitionType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":select", ItemModelDefinitionSelect.class);
        public static final ItemModelDefinitionType<ItemModelDefinitionRangeDispatch> RANGE_DISPATCH = new ItemModelDefinitionType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":range_dispatch", ItemModelDefinitionRangeDispatch.class);
        public static final ItemModelDefinitionType<ItemModelDefinitionEmpty> EMPTY = new ItemModelDefinitionType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":empty", ItemModelDefinitionEmpty.class);
        public static final ItemModelDefinitionType<ItemModelDefinitionBundleSelectedItem> BUNDLE_SELECTED_ITEM = new ItemModelDefinitionType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":bundle/selected_item", ItemModelDefinitionBundleSelectedItem.class);
        public static final ItemModelDefinitionType<ItemModelDefinitionSpecial> SPECIAL = new ItemModelDefinitionType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":special", ItemModelDefinitionSpecial.class);

        public static final ItemModelDefinitionType<ItemModelDefinitionEmpty> IC_LEGACY = new ItemModelDefinitionType<>(ResourceRegistry.ICD_PREFIX + "legacy", ItemModelDefinitionEmpty.class);

        private static final Map<Key, ItemModelDefinitionType<?>> TYPES_MAP = new HashMap<>();

        static {
            TYPES_MAP.put(MODEL.getKey(), MODEL);
            TYPES_MAP.put(COMPOSITE.getKey(), COMPOSITE);
            TYPES_MAP.put(CONDITION.getKey(), CONDITION);
            TYPES_MAP.put(SELECT.getKey(), SELECT);
            TYPES_MAP.put(RANGE_DISPATCH.getKey(), RANGE_DISPATCH);
            TYPES_MAP.put(EMPTY.getKey(), EMPTY);
            TYPES_MAP.put(BUNDLE_SELECTED_ITEM.getKey(), BUNDLE_SELECTED_ITEM);
            TYPES_MAP.put(SPECIAL.getKey(), SPECIAL);
        }

        private final Key key;
        private final Class<T> typeClass;

        public ItemModelDefinitionType(Key key, Class<T> typeClass) {
            this.key = key;
            this.typeClass = typeClass;
        }

        public ItemModelDefinitionType(String key, Class<T> typeClass) {
            this(KeyUtils.toKey(key), typeClass);
        }

        public Key getKey() {
            return key;
        }

        public Class<T> getTypeClass() {
            return typeClass;
        }

        @Override
        public String toString() {
            return key.asString();
        }

        public static ItemModelDefinitionType<?> getItemModelDefinitionType(String typeKey) {
            if (!typeKey.contains(":")) {
                typeKey = ResourceRegistry.DEFAULT_NAMESPACE + ":" + typeKey;
            }
            Key parsedKey = KeyUtils.toKey(typeKey);
            if (TYPES_MAP.containsKey(parsedKey)) {
                return TYPES_MAP.get(parsedKey);
            }
            throw new IllegalArgumentException("Unknown type: " + parsedKey.asString());
        }
    }

    public static class TintSourceType<T extends TintSource> {

        public static final TintSourceType<ConstantTintSource> CONSTANT = new TintSourceType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":constant", ConstantTintSource.class);
        public static final TintSourceType<DyeTintSource> DYE = new TintSourceType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":dye", DyeTintSource.class);
        public static final TintSourceType<GrassTintSource> GRASS = new TintSourceType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":grass", GrassTintSource.class);
        public static final TintSourceType<FireworkTintSource> FIREWORK = new TintSourceType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":firework", FireworkTintSource.class);
        public static final TintSourceType<PotionTintSource> POTION = new TintSourceType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":potion", PotionTintSource.class);
        public static final TintSourceType<MapColorTintSource> MAP_COLOR = new TintSourceType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":map_color", MapColorTintSource.class);
        public static final TintSourceType<TeamTintSource> TEAM = new TintSourceType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":team", TeamTintSource.class);
        public static final TintSourceType<CustomModelDataTintSource> CUSTOM_MODEL_DATA = new TintSourceType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":custom_model_data", CustomModelDataTintSource.class);

        private static final Map<String, TintSourceType<?>> TYPES_MAP = new HashMap<>();

        static {
            TYPES_MAP.put(CONSTANT.getNamespacedKey(), CONSTANT);
            TYPES_MAP.put(DYE.getNamespacedKey(), DYE);
            TYPES_MAP.put(GRASS.getNamespacedKey(), GRASS);
            TYPES_MAP.put(FIREWORK.getNamespacedKey(), FIREWORK);
            TYPES_MAP.put(POTION.getNamespacedKey(), POTION);
            TYPES_MAP.put(MAP_COLOR.getNamespacedKey(), MAP_COLOR);
            TYPES_MAP.put(TEAM.getNamespacedKey(), TEAM);
            TYPES_MAP.put(CUSTOM_MODEL_DATA.getNamespacedKey(), CUSTOM_MODEL_DATA);
        }

        private final String namespacedKey;
        private final Class<T> typeClass;

        public TintSourceType(String namespacedKey, Class<T> typeClass) {
            this.namespacedKey = namespacedKey;
            this.typeClass = typeClass;
        }

        public String getNamespacedKey() {
            return namespacedKey;
        }

        public Class<T> getTypeClass() {
            return typeClass;
        }

        @Override
        public String toString() {
            return getNamespacedKey();
        }

        public static TintSourceType<?> getTintSourceType(String typeKey) {
            if (TYPES_MAP.containsKey(typeKey)) {
                return TYPES_MAP.get(typeKey);
            }
            throw new IllegalArgumentException("Unknown tint source type: " + typeKey);
        }
    }

    // MODEL Definition
    public static class ItemModelDefinitionModel extends ItemModelDefinition {

        private final String model;
        private final List<TintSource> tints;

        public ItemModelDefinitionModel(boolean handAnimationOnSwapString, boolean oversizedInGui, String model, List<TintSource> tints) {
            super(ItemModelDefinitionType.MODEL, handAnimationOnSwapString, oversizedInGui);
            this.model = model;
            this.tints = tints;
        }

        public String getModel() {
            return model;
        }

        public List<TintSource> getTints() {
            return tints;
        }
    }

    // COMPOSITE Definition
    public static class ItemModelDefinitionComposite extends ItemModelDefinition {

        private final List<ItemModelDefinition> models;

        public ItemModelDefinitionComposite(boolean handAnimationOnSwapString, boolean oversizedInGui, List<ItemModelDefinition> models) {
            super(ItemModelDefinitionType.COMPOSITE, handAnimationOnSwapString, oversizedInGui);
            this.models = models;
        }

        public List<ItemModelDefinition> getModels() {
            return models;
        }
    }

    // CONDITION Definition
    public abstract static class ItemModelDefinitionCondition extends ItemModelDefinition {

        private final ConditionPropertyType<?> propertyType;
        private final ItemModelDefinition onTrue;
        private final ItemModelDefinition onFalse;

        public ItemModelDefinitionCondition(boolean handAnimationOnSwap, boolean oversizedInGui, ConditionPropertyType<?> propertyType, ItemModelDefinition onTrue, ItemModelDefinition onFalse) {
            super(ItemModelDefinitionType.CONDITION, handAnimationOnSwap, oversizedInGui);
            this.propertyType = propertyType;
            this.onTrue = onTrue;
            this.onFalse = onFalse;
        }

        public ConditionPropertyType<?> getPropertyType() {
            return propertyType;
        }

        public ItemModelDefinition getOnTrue() {
            return onTrue;
        }

        public ItemModelDefinition getOnFalse() {
            return onFalse;
        }

        public ItemModelDefinition getModel(boolean evaluation) {
            return evaluation ? getOnTrue() : getOnFalse();
        }
    }

    // SELECT Definition
    public abstract static class ItemModelDefinitionSelect<T> extends ItemModelDefinition {

        private final SelectPropertyType<?> property;
        private final List<SelectCase<T>> cases;
        private final ItemModelDefinition fallback;

        private final Object2ObjectMap<T, ItemModelDefinition> caseModels;

        public ItemModelDefinitionSelect(boolean handAnimationOnSwapString, boolean oversizedInGui, SelectPropertyType<?> propertyType, List<SelectCase<T>> cases, ItemModelDefinition fallback) {
            super(ItemModelDefinitionType.SELECT, handAnimationOnSwapString, oversizedInGui);
            this.property = propertyType;
            this.cases = cases;
            this.fallback = fallback;
            this.caseModels = bake();
        }

        private Object2ObjectMap<T, ItemModelDefinition> bake() {
            Object2ObjectMap<T, ItemModelDefinition> object2ObjectMap = new Object2ObjectOpenHashMap<>();
            for (SelectCase<T> switchCase : cases) {
                ItemModelDefinition itemModel = switchCase.getModel();
                for (T when : switchCase.getWhen()) {
                    object2ObjectMap.put(when, itemModel);
                }
            }
            return object2ObjectMap;
        }

        public SelectPropertyType<?> getPropertyType() {
            return property;
        }

        public List<SelectCase<T>> getCases() {
            return cases;
        }

        public boolean hasFallback() {
            return fallback != null;
        }

        public ItemModelDefinition getFallback() {
            return fallback;
        }

        public ItemModelDefinition getEntryCase(Object value) {
            return caseModels.get(value);
        }
    }

    // RANGE_DISPATCH Definition
    public abstract static class ItemModelDefinitionRangeDispatch extends ItemModelDefinition {

        private final RangeDispatchPropertyType<?> propertyType;
        private final float scale;
        private final List<RangeEntry> entries;
        private final ItemModelDefinition fallback;

        private final float[] thresholds;

        public ItemModelDefinitionRangeDispatch(boolean handAnimationOnSwapString, boolean oversizedInGui, RangeDispatchPropertyType<?> propertyType, float scale, List<RangeEntry> entries, ItemModelDefinition fallback) {
            super(ItemModelDefinitionType.RANGE_DISPATCH, handAnimationOnSwapString, oversizedInGui);
            this.propertyType = propertyType;
            this.scale = scale;
            this.entries = entries;
            this.fallback = fallback;

            this.thresholds = bake();
        }

        public float[] bake() {
            float[] thresholds = new float[entries.size()];
            for (int i = 0; i < thresholds.length; i++) {
                thresholds[i] = entries.get(i).getThreshold();
            }
            return thresholds;
        }

        public RangeDispatchPropertyType<?> getPropertyType() {
            return propertyType;
        }

        public float getScale() {
            return scale;
        }

        public List<RangeEntry> getEntries() {
            return entries;
        }

        public boolean hasFallback() {
            return fallback != null;
        }

        public ItemModelDefinition getFallback() {
            return fallback;
        }

        public int getEntryIndex(float value) {
            if (thresholds.length < 16) {
                for (int i = 0; i < thresholds.length; ++i) {
                    if (thresholds[i] > value) {
                        return i - 1;
                    }
                }
                return thresholds.length - 1;
            } else {
                int i = Arrays.binarySearch(thresholds, value);
                if (i < 0) {
                    int j = ~i;
                    return j - 1;
                } else {
                    return i;
                }
            }
        }
    }

    // EMPTY Definition
    public static class ItemModelDefinitionEmpty extends ItemModelDefinition {
        public ItemModelDefinitionEmpty(boolean handAnimationOnSwapString, boolean oversizedInGui) {
            super(ItemModelDefinitionType.EMPTY, handAnimationOnSwapString, oversizedInGui);
        }
    }

    // BUNDLE_SELECTED_ITEM Definition
    public static class ItemModelDefinitionBundleSelectedItem extends ItemModelDefinition {
        public ItemModelDefinitionBundleSelectedItem(boolean handAnimationOnSwapString, boolean oversizedInGui) {
            super(ItemModelDefinitionType.BUNDLE_SELECTED_ITEM, handAnimationOnSwapString, oversizedInGui);
        }
    }

    // SPECIAL Definition
    public static class ItemModelDefinitionSpecial extends ItemModelDefinition {

        private final String base;
        private final SpecialModel model;

        public ItemModelDefinitionSpecial(boolean handAnimationOnSwapString, boolean oversizedInGui, String base, SpecialModel model) {
            super(ItemModelDefinitionType.SPECIAL, handAnimationOnSwapString, oversizedInGui);
            this.base = base;
            this.model = model;
        }

        public String getBase() {
            return base;
        }

        public SpecialModel getModel() {
            return model;
        }
    }

    // IC LEGACY Definition
    public static class ItemModelDefinitionInteractiveChatDiscordSrvAddonLegacy extends ItemModelDefinition {
        public ItemModelDefinitionInteractiveChatDiscordSrvAddonLegacy(boolean handAnimationOnSwapString) {
            super(ItemModelDefinitionType.IC_LEGACY, handAnimationOnSwapString, true);
        }
    }

    private static class UnparsedSelectCase {

        private final List<String> when;
        private final ItemModelDefinition model;

        public UnparsedSelectCase(List<String> when, ItemModelDefinition model) {
            this.when = when;
            this.model = model;
        }

        public <T> SelectCase<T> parse(ThrowingFunction<String, T> parser) {
            return new SelectCase<>(when.stream().map(s -> {
                try {
                    return parser.apply(s);
                } catch (Throwable e) {
                    return null;
                }
            }).filter(c -> c != null).collect(Collectors.toList()), model);
        }

    }

    private static <T> List<SelectCase<T>> parse(List<UnparsedSelectCase> list, ThrowingFunction<String, T> parser) {
        return list.stream().map(c -> c.parse(parser)).collect(Collectors.toList());
    }

    public static class SelectCase<T> {

        private final List<T> when;
        private final ItemModelDefinition model;

        public SelectCase(List<T> when, ItemModelDefinition model) {
            this.when = when;
            this.model = model;
        }

        public List<T> getWhen() {
            return when;
        }

        public ItemModelDefinition getModel() {
            return model;
        }

    }

    // TintSource Definition
    public static abstract class TintSource {
        private final TintSourceType<?> type;

        public TintSource(TintSourceType<?> type) {
            this.type = type;
        }

        public TintSourceType<?> getType() {
            return type;
        }

        public static TintSource fromJson(JSONObject tintJson) {
            String typeKey = ensureNamespace((String) tintJson.get("type"));
            TintSourceType<?> type = TintSourceType.getTintSourceType(typeKey);

            if (type.equals(TintSourceType.CONSTANT)) {
                Object rawValue = tintJson.get("value");
                if (rawValue instanceof JSONArray) {
                    int[] value = ((JSONArray) tintJson.get("value")).stream().mapToInt(val -> ((Number) val).intValue()).toArray();
                    return new ConstantTintSource(value);
                } else {
                    int value = ((Number) rawValue).intValue();
                    return new ConstantTintSource(value);
                }
            } else if (type.equals(TintSourceType.DYE)) {
                Object rawDefaultValue = tintJson.get("default");
                if (rawDefaultValue instanceof JSONArray) {
                    int[] defaultValue = ((JSONArray) tintJson.get("default")).stream().mapToInt(val -> ((Number) val).intValue()).toArray();
                    return new DyeTintSource(defaultValue);
                } else {
                    int defaultValue = ((Number) rawDefaultValue).intValue();
                    return new DyeTintSource(defaultValue);
                }
            } else if (type.equals(TintSourceType.GRASS)) {
                float temperature = ((Number) tintJson.get("temperature")).floatValue();
                float downfall = ((Number) tintJson.get("downfall")).floatValue();
                return new GrassTintSource(temperature, downfall);
            } else if (type.equals(TintSourceType.FIREWORK)) {
                Object rawDefaultValue = tintJson.get("default");
                if (rawDefaultValue instanceof JSONArray) {
                    int[] defaultValue = ((JSONArray) tintJson.get("default")).stream().mapToInt(val -> ((Number) val).intValue()).toArray();
                    return new FireworkTintSource(defaultValue);
                } else {
                    int defaultValue = ((Number) rawDefaultValue).intValue();
                    return new FireworkTintSource(defaultValue);
                }
            } else if (type.equals(TintSourceType.POTION)) {
                Object rawDefaultValue = tintJson.get("default");
                if (rawDefaultValue instanceof JSONArray) {
                    int[] defaultValue = ((JSONArray) tintJson.get("default")).stream().mapToInt(val -> ((Number) val).intValue()).toArray();
                    return new PotionTintSource(defaultValue);
                } else {
                    int defaultValue = ((Number) rawDefaultValue).intValue();
                    return new PotionTintSource(defaultValue);
                }
            } else if (type.equals(TintSourceType.MAP_COLOR)) {
                Object rawDefaultValue = tintJson.get("default");
                if (rawDefaultValue instanceof JSONArray) {
                    int[] defaultValue = ((JSONArray) tintJson.get("default")).stream().mapToInt(val -> ((Number) val).intValue()).toArray();
                    return new MapColorTintSource(defaultValue);
                } else {
                    int defaultValue = ((Number) rawDefaultValue).intValue();
                    return new MapColorTintSource(defaultValue);
                }
            } else if (type.equals(TintSourceType.TEAM)) {
                Object rawDefaultValue = tintJson.get("default");
                if (rawDefaultValue instanceof JSONArray) {
                    int[] defaultValue = ((JSONArray) tintJson.get("default")).stream().mapToInt(val -> ((Number) val).intValue()).toArray();
                    return new TeamTintSource(defaultValue);
                } else {
                    int defaultValue = ((Number) rawDefaultValue).intValue();
                    return new TeamTintSource(defaultValue);
                }
            } else if (type.equals(TintSourceType.CUSTOM_MODEL_DATA)) {
                int index = ((Number) tintJson.getOrDefault("index", 0)).intValue();
                Object rawDefaultValue = tintJson.get("default");
                if (rawDefaultValue instanceof JSONArray) {
                    int[] defaultValue = ((JSONArray) tintJson.get("default")).stream().mapToInt(val -> ((Number) val).intValue()).toArray();
                    return new CustomModelDataTintSource(index, defaultValue);
                } else {
                    int defaultValue = ((Number) rawDefaultValue).intValue();
                    return new CustomModelDataTintSource(index, defaultValue);
                }
            }

            throw new IllegalArgumentException("Unsupported tint source type: " + typeKey);
        }
    }

    public static class ConstantTintSource extends TintSource {
        private final int value;

        public ConstantTintSource(int value) {
            super(TintSourceType.CONSTANT);
            this.value = value;
        }

        public ConstantTintSource(int[] rgb) {
            this(ColorUtils.getIntFromColor(rgb[0], rgb[1], rgb[2], 0));
        }

        public int getValue() {
            return value;
        }
    }

    public static class DyeTintSource extends TintSource {
        private final int defaultColor;

        public DyeTintSource(int defaultColor) {
            super(TintSourceType.DYE);
            this.defaultColor = defaultColor;
        }

        public DyeTintSource(int[] rgb) {
            this(ColorUtils.getIntFromColor(rgb[0], rgb[1], rgb[2], 0));
        }

        public int getDefaultColor() {
            return defaultColor;
        }
    }

    public static class GrassTintSource extends TintSource {
        private final float temperature;
        private final float downfall;

        public GrassTintSource(float temperature, float downfall) {
            super(TintSourceType.GRASS);
            this.temperature = temperature;
            this.downfall = downfall;
        }

        public float getTemperature() {
            return temperature;
        }

        public float getDownfall() {
            return downfall;
        }
    }

    public static class FireworkTintSource extends TintSource {
        private final int defaultColor;

        public FireworkTintSource(int defaultColor) {
            super(TintSourceType.FIREWORK);
            this.defaultColor = defaultColor;
        }

        public FireworkTintSource(int[] rgb) {
            this(ColorUtils.getIntFromColor(rgb[0], rgb[1], rgb[2], 0));
        }

        public int getDefaultColor() {
            return defaultColor;
        }
    }

    public static class PotionTintSource extends TintSource {
        private final int defaultColor;

        public PotionTintSource(int defaultColor) {
            super(TintSourceType.POTION);
            this.defaultColor = defaultColor;
        }

        public PotionTintSource(int[] rgb) {
            this(ColorUtils.getIntFromColor(rgb[0], rgb[1], rgb[2], 0));
        }

        public int getDefaultColor() {
            return defaultColor;
        }
    }

    public static class MapColorTintSource extends TintSource {
        private final int defaultColor;

        public MapColorTintSource(int defaultColor) {
            super(TintSourceType.MAP_COLOR);
            this.defaultColor = defaultColor;
        }

        public MapColorTintSource(int[] rgb) {
            this(ColorUtils.getIntFromColor(rgb[0], rgb[1], rgb[2], 0));
        }

        public int getDefaultColor() {
            return defaultColor;
        }
    }

    public static class TeamTintSource extends TintSource {
        private final int defaultColor;

        public TeamTintSource(int defaultColor) {
            super(TintSourceType.TEAM);
            this.defaultColor = defaultColor;
        }

        public TeamTintSource(int[] rgb) {
            this(ColorUtils.getIntFromColor(rgb[0], rgb[1], rgb[2], 0));
        }

        public int getDefaultColor() {
            return defaultColor;
        }
    }

    public static class CustomModelDataTintSource extends TintSource {
        private final int index;
        private final int defaultColor;

        public CustomModelDataTintSource(int index, int defaultColor) {
            super(TintSourceType.CUSTOM_MODEL_DATA);
            this.index = index;
            this.defaultColor = defaultColor;
        }

        public CustomModelDataTintSource(int index, int[] rgb) {
            this(index, ColorUtils.getIntFromColor(rgb[0], rgb[1], rgb[2], 0));
        }

        public int getIndex() {
            return index;
        }

        public int getDefaultColor() {
            return defaultColor;
        }
    }

    public static class ConditionPropertyType<T extends ItemModelDefinitionCondition> {

        public static final ConditionPropertyType<UsingItemConditionProperty> USING_ITEM = new ConditionPropertyType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":using_item", UsingItemConditionProperty.class);
        public static final ConditionPropertyType<BrokenConditionProperty> BROKEN = new ConditionPropertyType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":broken", BrokenConditionProperty.class);
        public static final ConditionPropertyType<HasComponentConditionProperty> HAS_COMPONENT = new ConditionPropertyType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":has_component", HasComponentConditionProperty.class);
        public static final ConditionPropertyType<DamagedConditionProperty> DAMAGED = new ConditionPropertyType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":damaged", DamagedConditionProperty.class);
        public static final ConditionPropertyType<FishingRodCastConditionProperty> FISHING_ROD_CAST = new ConditionPropertyType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":fishing_rod/cast", FishingRodCastConditionProperty.class);
        public static final ConditionPropertyType<BundleSelectedItemConditionProperty> BUNDLE_SELECTED_ITEM = new ConditionPropertyType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":bundle/has_selected_item", BundleSelectedItemConditionProperty.class);
        public static final ConditionPropertyType<SelectedConditionProperty> SELECTED = new ConditionPropertyType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":selected", SelectedConditionProperty.class);
        public static final ConditionPropertyType<CarriedConditionProperty> CARRIED = new ConditionPropertyType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":carried", CarriedConditionProperty.class);
        public static final ConditionPropertyType<ExtendedViewConditionProperty> EXTENDED_VIEW = new ConditionPropertyType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":extended_view", ExtendedViewConditionProperty.class);
        public static final ConditionPropertyType<KeybindDownConditionProperty> KEYBIND_DOWN = new ConditionPropertyType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":keybind_down", KeybindDownConditionProperty.class);
        public static final ConditionPropertyType<ViewEntityConditionProperty> VIEW_ENTITY = new ConditionPropertyType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":view_entity", ViewEntityConditionProperty.class);
        public static final ConditionPropertyType<ComponentConditionProperty> COMPONENT = new ConditionPropertyType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":component", ComponentConditionProperty.class);
        public static final ConditionPropertyType<CustomModelDataConditionProperty> CUSTOM_MODEL_DATA = new ConditionPropertyType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":custom_model_data", CustomModelDataConditionProperty.class);

        private static final Map<String, ConditionPropertyType<?>> TYPES_MAP = new HashMap<>();

        static {
            TYPES_MAP.put(USING_ITEM.getNamespacedKey(), USING_ITEM);
            TYPES_MAP.put(BROKEN.getNamespacedKey(), BROKEN);
            TYPES_MAP.put(HAS_COMPONENT.getNamespacedKey(), HAS_COMPONENT);
            TYPES_MAP.put(DAMAGED.getNamespacedKey(), DAMAGED);
            TYPES_MAP.put(FISHING_ROD_CAST.getNamespacedKey(), FISHING_ROD_CAST);
            TYPES_MAP.put(BUNDLE_SELECTED_ITEM.getNamespacedKey(), BUNDLE_SELECTED_ITEM);
            TYPES_MAP.put(SELECTED.getNamespacedKey(), SELECTED);
            TYPES_MAP.put(CARRIED.getNamespacedKey(), CARRIED);
            TYPES_MAP.put(EXTENDED_VIEW.getNamespacedKey(), EXTENDED_VIEW);
            TYPES_MAP.put(KEYBIND_DOWN.getNamespacedKey(), KEYBIND_DOWN);
            TYPES_MAP.put(VIEW_ENTITY.getNamespacedKey(), VIEW_ENTITY);
            TYPES_MAP.put(COMPONENT.getNamespacedKey(), COMPONENT);
            TYPES_MAP.put(CUSTOM_MODEL_DATA.getNamespacedKey(), CUSTOM_MODEL_DATA);
        }

        private final String namespacedKey;
        private final Class<T> typeClass;

        public ConditionPropertyType(String namespacedKey, Class<T> typeClass) {
            this.namespacedKey = namespacedKey;
            this.typeClass = typeClass;
        }

        public String getNamespacedKey() {
            return namespacedKey;
        }

        public Class<T> getTypeClass() {
            return typeClass;
        }

        public static ConditionPropertyType<?> getConditionPropertyType(String propertyKey) {
            if (TYPES_MAP.containsKey(propertyKey)) {
                return TYPES_MAP.get(propertyKey);
            }
            throw new IllegalArgumentException("Unknown condition property type: " + propertyKey);
        }
    }

    public static class UsingItemConditionProperty extends ItemModelDefinitionCondition {
        public UsingItemConditionProperty(boolean handAnimationOnSwap, boolean oversizedInGui, ConditionPropertyType<?> propertyType, ItemModelDefinition onTrue, ItemModelDefinition onFalse) {
            super(handAnimationOnSwap, oversizedInGui, propertyType, onTrue, onFalse);
        }
    }

    public static class BrokenConditionProperty extends ItemModelDefinitionCondition {
        public BrokenConditionProperty(boolean handAnimationOnSwap, boolean oversizedInGui, ConditionPropertyType<?> propertyType, ItemModelDefinition onTrue, ItemModelDefinition onFalse) {
            super(handAnimationOnSwap, oversizedInGui, propertyType, onTrue, onFalse);
        }
    }

    public static class HasComponentConditionProperty extends ItemModelDefinitionCondition {
        private final Key component;
        private final boolean ignoreDefault;

        public HasComponentConditionProperty(boolean handAnimationOnSwap, boolean oversizedInGui, ConditionPropertyType<?> propertyType, ItemModelDefinition onTrue, ItemModelDefinition onFalse, Key component, boolean ignoreDefault) {
            super(handAnimationOnSwap, oversizedInGui, propertyType, onTrue, onFalse);
            this.component = component;
            this.ignoreDefault = ignoreDefault;
        }

        public Key getComponent() {
            return component;
        }

        public boolean isIgnoreDefault() {
            return ignoreDefault;
        }
    }

    public static class DamagedConditionProperty extends ItemModelDefinitionCondition {
        public DamagedConditionProperty(boolean handAnimationOnSwap, boolean oversizedInGui, ConditionPropertyType<?> propertyType, ItemModelDefinition onTrue, ItemModelDefinition onFalse) {
            super(handAnimationOnSwap, oversizedInGui, propertyType, onTrue, onFalse);
        }
    }

    public static class FishingRodCastConditionProperty extends ItemModelDefinitionCondition {
        public FishingRodCastConditionProperty(boolean handAnimationOnSwap, boolean oversizedInGui, ConditionPropertyType<?> propertyType, ItemModelDefinition onTrue, ItemModelDefinition onFalse) {
            super(handAnimationOnSwap, oversizedInGui, propertyType, onTrue, onFalse);
        }
    }

    public static class BundleSelectedItemConditionProperty extends ItemModelDefinitionCondition {
        public BundleSelectedItemConditionProperty(boolean handAnimationOnSwap, boolean oversizedInGui, ConditionPropertyType<?> propertyType, ItemModelDefinition onTrue, ItemModelDefinition onFalse) {
            super(handAnimationOnSwap, oversizedInGui, propertyType, onTrue, onFalse);
        }
    }

    public static class SelectedConditionProperty extends ItemModelDefinitionCondition {
        public SelectedConditionProperty(boolean handAnimationOnSwap, boolean oversizedInGui, ConditionPropertyType<?> propertyType, ItemModelDefinition onTrue, ItemModelDefinition onFalse) {
            super(handAnimationOnSwap, oversizedInGui, propertyType, onTrue, onFalse);
        }
    }

    public static class CarriedConditionProperty extends ItemModelDefinitionCondition {
        public CarriedConditionProperty(boolean handAnimationOnSwap, boolean oversizedInGui, ConditionPropertyType<?> propertyType, ItemModelDefinition onTrue, ItemModelDefinition onFalse) {
            super(handAnimationOnSwap, oversizedInGui, propertyType, onTrue, onFalse);
        }
    }

    public static class ExtendedViewConditionProperty extends ItemModelDefinitionCondition {
        public ExtendedViewConditionProperty(boolean handAnimationOnSwap, boolean oversizedInGui, ConditionPropertyType<?> propertyType, ItemModelDefinition onTrue, ItemModelDefinition onFalse) {
            super(handAnimationOnSwap, oversizedInGui, propertyType, onTrue, onFalse);
        }
    }

    public static class KeybindDownConditionProperty extends ItemModelDefinitionCondition {
        private final int keybind;

        public KeybindDownConditionProperty(boolean handAnimationOnSwap, boolean oversizedInGui, ConditionPropertyType<?> propertyType, ItemModelDefinition onTrue, ItemModelDefinition onFalse, int keybind) {
            super(handAnimationOnSwap, oversizedInGui, propertyType, onTrue, onFalse);
            this.keybind = keybind;
        }

        public int getKeybind() {
            return keybind;
        }
    }

    public static class ViewEntityConditionProperty extends ItemModelDefinitionCondition {
        public ViewEntityConditionProperty(boolean handAnimationOnSwap, boolean oversizedInGui, ConditionPropertyType<?> propertyType, ItemModelDefinition onTrue, ItemModelDefinition onFalse) {
            super(handAnimationOnSwap, oversizedInGui, propertyType, onTrue, onFalse);
        }
    }

    public static class ComponentConditionProperty extends ItemModelDefinitionCondition {
        private final String predicate;
        private final String value;

        public ComponentConditionProperty(boolean handAnimationOnSwap, boolean oversizedInGui, ConditionPropertyType<?> propertyType, ItemModelDefinition onTrue, ItemModelDefinition onFalse, String predicate, String value) {
            super(handAnimationOnSwap, oversizedInGui, propertyType, onTrue, onFalse);
            this.predicate = predicate;
            this.value = value;
        }

        public String getPredicate() {
            return predicate;
        }

        public String getValue() {
            return value;
        }
    }

    public static class CustomModelDataConditionProperty extends ItemModelDefinitionCondition {
        private final int index;

        public CustomModelDataConditionProperty(boolean handAnimationOnSwap, boolean oversizedInGui, ConditionPropertyType<?> propertyType, ItemModelDefinition onTrue, ItemModelDefinition onFalse, int index) {
            super(handAnimationOnSwap, oversizedInGui, propertyType, onTrue, onFalse);
            this.index = index;
        }

        public int getIndex() {
            return index;
        }
    }

    public static class SelectPropertyType<T extends ItemModelDefinitionSelect<?>> {

        public static final SelectPropertyType<MainHandSelectProperty> MAIN_HAND = new SelectPropertyType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":main_hand", MainHandSelectProperty.class);
        public static final SelectPropertyType<ChargeTypeSelectProperty> CHARGE_TYPE = new SelectPropertyType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":charge_type", ChargeTypeSelectProperty.class);
        public static final SelectPropertyType<ComponentSelectProperty> COMPONENT = new SelectPropertyType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":component", ComponentSelectProperty.class);
        public static final SelectPropertyType<TrimMaterialSelectProperty> TRIM_MATERIAL = new SelectPropertyType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":trim_material", TrimMaterialSelectProperty.class);
        public static final SelectPropertyType<BlockStateSelectProperty> BLOCK_STATE = new SelectPropertyType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":block_state", BlockStateSelectProperty.class);
        public static final SelectPropertyType<DisplayContextSelectProperty> DISPLAY_CONTEXT = new SelectPropertyType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":display_context", DisplayContextSelectProperty.class);
        public static final SelectPropertyType<LocalTimeSelectProperty> LOCAL_TIME = new SelectPropertyType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":local_time", LocalTimeSelectProperty.class);
        public static final SelectPropertyType<ContextDimensionSelectProperty> CONTEXT_DIMENSION = new SelectPropertyType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":context_dimension", ContextDimensionSelectProperty.class);
        public static final SelectPropertyType<ContextEntityTypeSelectProperty> CONTEXT_ENTITY_TYPE = new SelectPropertyType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":context_entity_type", ContextEntityTypeSelectProperty.class);
        public static final SelectPropertyType<CustomModelDataSelectProperty> CUSTOM_MODEL_DATA = new SelectPropertyType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":custom_model_data", CustomModelDataSelectProperty.class);

        private static final Map<String, SelectPropertyType<?>> TYPES_MAP = new HashMap<>();

        static {
            TYPES_MAP.put(MAIN_HAND.getNamespacedKey(), MAIN_HAND);
            TYPES_MAP.put(CHARGE_TYPE.getNamespacedKey(), CHARGE_TYPE);
            TYPES_MAP.put(COMPONENT.getNamespacedKey(), COMPONENT);
            TYPES_MAP.put(TRIM_MATERIAL.getNamespacedKey(), TRIM_MATERIAL);
            TYPES_MAP.put(BLOCK_STATE.getNamespacedKey(), BLOCK_STATE);
            TYPES_MAP.put(DISPLAY_CONTEXT.getNamespacedKey(), DISPLAY_CONTEXT);
            TYPES_MAP.put(LOCAL_TIME.getNamespacedKey(), LOCAL_TIME);
            TYPES_MAP.put(CONTEXT_DIMENSION.getNamespacedKey(), CONTEXT_DIMENSION);
            TYPES_MAP.put(CONTEXT_ENTITY_TYPE.getNamespacedKey(), CONTEXT_ENTITY_TYPE);
            TYPES_MAP.put(CUSTOM_MODEL_DATA.getNamespacedKey(), CUSTOM_MODEL_DATA);
        }

        private final String namespacedKey;
        private final Class<T> typeClass;

        public SelectPropertyType(String namespacedKey, Class<T> typeClass) {
            this.namespacedKey = namespacedKey;
            this.typeClass = typeClass;
        }

        public String getNamespacedKey() {
            return namespacedKey;
        }

        public Class<T> getTypeClass() {
            return typeClass;
        }

        public static SelectPropertyType<?> getSelectPropertyType(String propertyKey) {
            if (TYPES_MAP.containsKey(propertyKey)) {
                return TYPES_MAP.get(propertyKey);
            }
            throw new IllegalArgumentException("Unknown select property type: " + propertyKey);
        }
    }

    public static class MainHandSelectProperty extends ItemModelDefinitionSelect<MainHand> {
        public MainHandSelectProperty(boolean handAnimationOnSwapString, boolean oversizedInGui, SelectPropertyType<?> propertyType, List<SelectCase<MainHand>> cases, ItemModelDefinition fallback) {
            super(handAnimationOnSwapString, oversizedInGui, propertyType, cases, fallback);
        }
    }

    public static class ChargeTypeSelectProperty extends ItemModelDefinitionSelect<ChargeType> {
        public ChargeTypeSelectProperty(boolean handAnimationOnSwapString, boolean oversizedInGui, SelectPropertyType<?> propertyType, List<SelectCase<ChargeType>> cases, ItemModelDefinition fallback) {
            super(handAnimationOnSwapString, oversizedInGui, propertyType, cases, fallback);
        }
    }

    public static class ComponentSelectProperty extends ItemModelDefinitionSelect<Object> {
        private final Key component;

        public ComponentSelectProperty(boolean handAnimationOnSwapString, boolean oversizedInGui, SelectPropertyType<?> propertyType, List<SelectCase<Object>> cases, ItemModelDefinition fallback, Key component) {
            super(handAnimationOnSwapString, oversizedInGui, propertyType, cases, fallback);
            this.component = component;
        }

        public Key getComponent() {
            return component;
        }
    }

    public static class TrimMaterialSelectProperty extends ItemModelDefinitionSelect<Key> {
        public TrimMaterialSelectProperty(boolean handAnimationOnSwapString, boolean oversizedInGui, SelectPropertyType<?> propertyType, List<SelectCase<Key>> cases, ItemModelDefinition fallback) {
            super(handAnimationOnSwapString, oversizedInGui, propertyType, cases, fallback);
        }
    }

    public static class BlockStateSelectProperty extends ItemModelDefinitionSelect<String> {
        private final String blockStateProperty;

        public BlockStateSelectProperty(boolean handAnimationOnSwapString, boolean oversizedInGui, SelectPropertyType<?> propertyType, List<SelectCase<String>> cases, ItemModelDefinition fallback, String blockStateProperty) {
            super(handAnimationOnSwapString, oversizedInGui, propertyType, cases, fallback);
            this.blockStateProperty = blockStateProperty;
        }

        public String getBlockStateProperty() {
            return blockStateProperty;
        }
    }

    public static class DisplayContextSelectProperty extends ItemModelDefinitionSelect<ModelDisplay.ModelDisplayPosition> {
        public DisplayContextSelectProperty(boolean handAnimationOnSwapString, boolean oversizedInGui, SelectPropertyType<?> propertyType, List<SelectCase<ModelDisplay.ModelDisplayPosition>> cases, ItemModelDefinition fallback) {
            super(handAnimationOnSwapString, oversizedInGui, propertyType, cases, fallback);
        }
    }

    public static class LocalTimeSelectProperty extends ItemModelDefinitionSelect<String> {
        private final String pattern;
        private final String locale;
        private final Optional<TimeZone> timeZone;

        public LocalTimeSelectProperty(boolean handAnimationOnSwapString, boolean oversizedInGui, SelectPropertyType<?> propertyType, List<SelectCase<String>> cases, ItemModelDefinition fallback, String pattern, String locale, Optional<TimeZone> timeZone) {
            super(handAnimationOnSwapString, oversizedInGui, propertyType, cases, fallback);
            this.pattern = pattern;
            this.locale = locale;
            this.timeZone = timeZone;
        }

        public String getPattern() {
            return pattern;
        }

        public String getLocale() {
            return locale;
        }

        public Optional<TimeZone> getTimeZone() {
            return timeZone;
        }
    }

    public static class ContextDimensionSelectProperty extends ItemModelDefinitionSelect<Key> {
        public ContextDimensionSelectProperty(boolean handAnimationOnSwapString, boolean oversizedInGui, SelectPropertyType<?> propertyType, List<SelectCase<Key>> cases, ItemModelDefinition fallback) {
            super(handAnimationOnSwapString, oversizedInGui, propertyType, cases, fallback);
        }
    }

    public static class ContextEntityTypeSelectProperty extends ItemModelDefinitionSelect<Key> {
        public ContextEntityTypeSelectProperty(boolean handAnimationOnSwapString, boolean oversizedInGui, SelectPropertyType<?> propertyType, List<SelectCase<Key>> cases, ItemModelDefinition fallback) {
            super(handAnimationOnSwapString, oversizedInGui, propertyType, cases, fallback);
        }
    }

    public static class CustomModelDataSelectProperty extends ItemModelDefinitionSelect<String> {
        private final int index;

        public CustomModelDataSelectProperty(boolean handAnimationOnSwapString, boolean oversizedInGui, SelectPropertyType<?> propertyType, List<SelectCase<String>> cases, ItemModelDefinition fallback, int index) {
            super(handAnimationOnSwapString, oversizedInGui, propertyType, cases, fallback);
            this.index = index;
        }

        public int getIndex() {
            return index;
        }
    }

    public static class RangeDispatchPropertyType<T extends ItemModelDefinitionRangeDispatch> {

        public static final RangeDispatchPropertyType<BundleFullnessRangeDispatchProperty> BUNDLE_FULLNESS = new RangeDispatchPropertyType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":bundle/fullness", BundleFullnessRangeDispatchProperty.class);
        public static final RangeDispatchPropertyType<DamageRangeDispatchProperty> DAMAGE = new RangeDispatchPropertyType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":damage", DamageRangeDispatchProperty.class);
        public static final RangeDispatchPropertyType<CountRangeDispatchProperty> COUNT = new RangeDispatchPropertyType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":count", CountRangeDispatchProperty.class);
        public static final RangeDispatchPropertyType<CooldownRangeDispatchProperty> COOLDOWN = new RangeDispatchPropertyType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":cooldown", CooldownRangeDispatchProperty.class);
        public static final RangeDispatchPropertyType<TimeRangeDispatchProperty> TIME = new RangeDispatchPropertyType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":time", TimeRangeDispatchProperty.class);
        public static final RangeDispatchPropertyType<CompassRangeDispatchProperty> COMPASS = new RangeDispatchPropertyType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":compass", CompassRangeDispatchProperty.class);
        public static final RangeDispatchPropertyType<CrossbowPullRangeDispatchProperty> CROSSBOW_PULL = new RangeDispatchPropertyType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":crossbow/pull", CrossbowPullRangeDispatchProperty.class);
        public static final RangeDispatchPropertyType<UseDurationRangeDispatchProperty> USE_DURATION = new RangeDispatchPropertyType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":use_duration", UseDurationRangeDispatchProperty.class);
        public static final RangeDispatchPropertyType<UseCycleRangeDispatchProperty> USE_CYCLE = new RangeDispatchPropertyType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":use_cycle", UseCycleRangeDispatchProperty.class);
        public static final RangeDispatchPropertyType<CustomModelDataRangeDispatchProperty> CUSTOM_MODEL_DATA = new RangeDispatchPropertyType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":custom_model_data", CustomModelDataRangeDispatchProperty.class);

        private static final Map<String, RangeDispatchPropertyType<?>> TYPES_MAP = new HashMap<>();

        static {
            TYPES_MAP.put(BUNDLE_FULLNESS.getNamespacedKey(), BUNDLE_FULLNESS);
            TYPES_MAP.put(DAMAGE.getNamespacedKey(), DAMAGE);
            TYPES_MAP.put(COUNT.getNamespacedKey(), COUNT);
            TYPES_MAP.put(COOLDOWN.getNamespacedKey(), COOLDOWN);
            TYPES_MAP.put(TIME.getNamespacedKey(), TIME);
            TYPES_MAP.put(COMPASS.getNamespacedKey(), COMPASS);
            TYPES_MAP.put(CROSSBOW_PULL.getNamespacedKey(), CROSSBOW_PULL);
            TYPES_MAP.put(USE_DURATION.getNamespacedKey(), USE_DURATION);
            TYPES_MAP.put(USE_CYCLE.getNamespacedKey(), USE_CYCLE);
            TYPES_MAP.put(CUSTOM_MODEL_DATA.getNamespacedKey(), CUSTOM_MODEL_DATA);
        }

        private final String namespacedKey;
        private final Class<T> typeClass;

        public RangeDispatchPropertyType(String namespacedKey, Class<T> typeClass) {
            this.namespacedKey = namespacedKey;
            this.typeClass = typeClass;
        }

        public String getNamespacedKey() {
            return namespacedKey;
        }

        public Class<T> getTypeClass() {
            return typeClass;
        }

        public static RangeDispatchPropertyType<?> getRangeDispatchPropertyType(String propertyKey) {
            if (TYPES_MAP.containsKey(propertyKey)) {
                return TYPES_MAP.get(propertyKey);
            }
            throw new IllegalArgumentException("Unknown range dispatch property type: " + propertyKey);
        }
    }

    public static class RangeEntry {
        private final float threshold;
        private final ItemModelDefinition model;

        public RangeEntry(float threshold, ItemModelDefinition model) {
            this.threshold = threshold;
            this.model = model;
        }

        public float getThreshold() {
            return threshold;
        }

        public ItemModelDefinition getModel() {
            return model;
        }
    }

    public static class BundleFullnessRangeDispatchProperty extends ItemModelDefinitionRangeDispatch {
        public BundleFullnessRangeDispatchProperty(boolean handAnimationOnSwapString, boolean oversizedInGui, RangeDispatchPropertyType<?> propertyType, float scale, List<RangeEntry> entries, ItemModelDefinition fallback) {
            super(handAnimationOnSwapString, oversizedInGui, propertyType, scale, entries, fallback);
        }
    }

    public static class DamageRangeDispatchProperty extends ItemModelDefinitionRangeDispatch {
        private final boolean normalize;

        public DamageRangeDispatchProperty(boolean handAnimationOnSwapString, boolean oversizedInGui, RangeDispatchPropertyType<?> propertyType, float scale, List<RangeEntry> entries, ItemModelDefinition fallback, boolean normalize) {
            super(handAnimationOnSwapString, oversizedInGui, propertyType, scale, entries, fallback);
            this.normalize = normalize;
        }

        public boolean isNormalize() {
            return normalize;
        }
    }

    public static class CountRangeDispatchProperty extends ItemModelDefinitionRangeDispatch {
        private final boolean normalize;

        public CountRangeDispatchProperty(boolean handAnimationOnSwapString, boolean oversizedInGui, RangeDispatchPropertyType<?> propertyType, float scale, List<RangeEntry> entries, ItemModelDefinition fallback, boolean normalize) {
            super(handAnimationOnSwapString, oversizedInGui, propertyType, scale, entries, fallback);
            this.normalize = normalize;
        }

        public boolean isNormalize() {
            return normalize;
        }
    }

    public static class CooldownRangeDispatchProperty extends ItemModelDefinitionRangeDispatch {
        public CooldownRangeDispatchProperty(boolean handAnimationOnSwapString, boolean oversizedInGui, RangeDispatchPropertyType<?> propertyType, float scale, List<RangeEntry> entries, ItemModelDefinition fallback) {
            super(handAnimationOnSwapString, oversizedInGui, propertyType, scale, entries, fallback);
        }
    }

    public static class TimeRangeDispatchProperty extends ItemModelDefinitionRangeDispatch {
        private final TimeSource source;
        private final boolean wobble;

        public TimeRangeDispatchProperty(boolean handAnimationOnSwapString, boolean oversizedInGui, RangeDispatchPropertyType<?> propertyType, float scale, List<RangeEntry> entries, ItemModelDefinition fallback, TimeSource source, boolean wobble) {
            super(handAnimationOnSwapString, oversizedInGui, propertyType, scale, entries, fallback);
            this.source = source;
            this.wobble = wobble;
        }

        public TimeSource getSource() {
            return source;
        }

        public boolean isWobble() {
            return wobble;
        }

        public enum TimeSource {
            DAYTIME, MOON_PHASE, RANDOM;
        }
    }

    public static class CompassRangeDispatchProperty extends ItemModelDefinitionRangeDispatch {
        private final CompassTarget target;
        private final boolean wobble;

        public CompassRangeDispatchProperty(boolean handAnimationOnSwapString, boolean oversizedInGui, RangeDispatchPropertyType<?> propertyType, float scale, List<RangeEntry> entries, ItemModelDefinition fallback, CompassTarget target, boolean wobble) {
            super(handAnimationOnSwapString, oversizedInGui, propertyType, scale, entries, fallback);
            this.target = target;
            this.wobble = wobble;
        }

        public CompassTarget getTarget() {
            return target;
        }

        public boolean isWobble() {
            return wobble;
        }

        public enum CompassTarget {
            SPAWN, LODESTONE, RECOVERY, NONE;
        }
    }

    public static class CrossbowPullRangeDispatchProperty extends ItemModelDefinitionRangeDispatch {
        public CrossbowPullRangeDispatchProperty(boolean handAnimationOnSwapString, boolean oversizedInGui, RangeDispatchPropertyType<?> propertyType, float scale, List<RangeEntry> entries, ItemModelDefinition fallback) {
            super(handAnimationOnSwapString, oversizedInGui, propertyType, scale, entries, fallback);
        }
    }

    public static class UseDurationRangeDispatchProperty extends ItemModelDefinitionRangeDispatch {
        private final boolean remaining;

        public UseDurationRangeDispatchProperty(boolean handAnimationOnSwapString, boolean oversizedInGui, RangeDispatchPropertyType<?> propertyType, float scale, List<RangeEntry> entries, ItemModelDefinition fallback, boolean remaining) {
            super(handAnimationOnSwapString, oversizedInGui, propertyType, scale, entries, fallback);
            this.remaining = remaining;
        }

        public boolean isRemaining() {
            return remaining;
        }
    }

    public static class UseCycleRangeDispatchProperty extends ItemModelDefinitionRangeDispatch {
        private final float period;

        public UseCycleRangeDispatchProperty(boolean handAnimationOnSwapString, boolean oversizedInGui, RangeDispatchPropertyType<?> propertyType, float scale, List<RangeEntry> entries, ItemModelDefinition fallback, float period) {
            super(handAnimationOnSwapString, oversizedInGui, propertyType, scale, entries, fallback);
            this.period = period;
        }

        public float getPeriod() {
            return period;
        }
    }

    public static class CustomModelDataRangeDispatchProperty extends ItemModelDefinitionRangeDispatch {
        private final int index;

        public CustomModelDataRangeDispatchProperty(boolean handAnimationOnSwapString, boolean oversizedInGui, RangeDispatchPropertyType<?> propertyType, float scale, List<RangeEntry> entries, ItemModelDefinition fallback, int index) {
            super(handAnimationOnSwapString, oversizedInGui, propertyType, scale, entries, fallback);
            this.index = index;
        }

        public int getIndex() {
            return index;
        }
    }

    public static class SpecialModelType<T extends SpecialModel> {

        public static final SpecialModelType<BannerSpecialModel> BANNER = new SpecialModelType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":banner", BannerSpecialModel.class);
        public static final SpecialModelType<BedSpecialModel> BED = new SpecialModelType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":bed", BedSpecialModel.class);
        public static final SpecialModelType<ChestSpecialModel> CHEST = new SpecialModelType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":chest", ChestSpecialModel.class);
        public static final SpecialModelType<ConduitSpecialModel> CONDUIT = new SpecialModelType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":conduit", ConduitSpecialModel.class);
        public static final SpecialModelType<DecoratedPotSpecialModel> DECORATED_POT = new SpecialModelType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":decorated_pot", DecoratedPotSpecialModel.class);
        public static final SpecialModelType<HeadSpecialModel> HEAD = new SpecialModelType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":head", HeadSpecialModel.class);
        public static final SpecialModelType<PlayerHeadSpecialModel> PLAYER_HEAD = new SpecialModelType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":player_head", PlayerHeadSpecialModel.class);
        public static final SpecialModelType<ShieldSpecialModel> SHIELD = new SpecialModelType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":shield", ShieldSpecialModel.class);
        public static final SpecialModelType<ShulkerBoxSpecialModel> SHULKER_BOX = new SpecialModelType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":shulker_box", ShulkerBoxSpecialModel.class);
        public static final SpecialModelType<StandingSignSpecialModel> STANDING_SIGN = new SpecialModelType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":standing_sign", StandingSignSpecialModel.class);
        public static final SpecialModelType<HangingSignSpecialModel> HANGING_SIGN = new SpecialModelType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":hanging_sign", HangingSignSpecialModel.class);
        public static final SpecialModelType<TridentSpecialModel> TRIDENT = new SpecialModelType<>(ResourceRegistry.DEFAULT_NAMESPACE + ":trident", TridentSpecialModel.class);

        private static final Map<String, SpecialModelType<?>> TYPES_MAP = new HashMap<>();

        static {
            TYPES_MAP.put(BANNER.getNamespacedKey(), BANNER);
            TYPES_MAP.put(BED.getNamespacedKey(), BED);
            TYPES_MAP.put(CHEST.getNamespacedKey(), CHEST);
            TYPES_MAP.put(CONDUIT.getNamespacedKey(), CONDUIT);
            TYPES_MAP.put(DECORATED_POT.getNamespacedKey(), DECORATED_POT);
            TYPES_MAP.put(HEAD.getNamespacedKey(), HEAD);
            TYPES_MAP.put(PLAYER_HEAD.getNamespacedKey(), PLAYER_HEAD);
            TYPES_MAP.put(SHIELD.getNamespacedKey(), SHIELD);
            TYPES_MAP.put(SHULKER_BOX.getNamespacedKey(), SHULKER_BOX);
            TYPES_MAP.put(STANDING_SIGN.getNamespacedKey(), STANDING_SIGN);
            TYPES_MAP.put(HANGING_SIGN.getNamespacedKey(), HANGING_SIGN);
            TYPES_MAP.put(TRIDENT.getNamespacedKey(), TRIDENT);
        }

        private final String namespacedKey;
        private final Class<T> typeClass;

        public SpecialModelType(String namespacedKey, Class<T> typeClass) {
            this.namespacedKey = namespacedKey;
            this.typeClass = typeClass;
        }

        public String getNamespacedKey() {
            return namespacedKey;
        }

        public Class<T> getTypeClass() {
            return typeClass;
        }

        public static SpecialModelType<?> getSpecialModelType(String modelKey) {
            if (TYPES_MAP.containsKey(modelKey)) {
                return TYPES_MAP.get(modelKey);
            }
            throw new IllegalArgumentException("Unknown special model type: " + modelKey);
        }
    }

    public abstract static class SpecialModel {
        private final SpecialModelType<?> modelType;

        public SpecialModel(SpecialModelType<?> modelType) {
            this.modelType = modelType;
        }

        public SpecialModelType<?> getModelType() {
            return modelType;
        }
    }

    public static class BannerSpecialModel extends SpecialModel {
        private final String color;

        public BannerSpecialModel(String color) {
            super(SpecialModelType.BANNER);
            this.color = color;
        }

        public String getColor() {
            return color;
        }
    }

    public static class BedSpecialModel extends SpecialModel {
        private final String texture;

        public BedSpecialModel(String texture) {
            super(SpecialModelType.BED);
            this.texture = texture;
        }

        public String getTexture() {
            return texture;
        }
    }

    public static class ChestSpecialModel extends SpecialModel {
        private final String texture;
        private final float openness;

        public ChestSpecialModel(String texture, float openness) {
            super(SpecialModelType.CHEST);
            this.texture = texture;
            this.openness = openness;
        }

        public String getTexture() {
            return texture;
        }

        public float getOpenness() {
            return openness;
        }
    }

    public static class ConduitSpecialModel extends SpecialModel {
        public ConduitSpecialModel() {
            super(SpecialModelType.CONDUIT);
        }
    }

    public static class DecoratedPotSpecialModel extends SpecialModel {
        public DecoratedPotSpecialModel() {
            super(SpecialModelType.DECORATED_POT);
        }
    }

    public static class HeadSpecialModel extends SpecialModel {
        private final String kind;
        private final String texture;
        private final float animation;

        public HeadSpecialModel(String kind, String texture, float animation) {
            super(SpecialModelType.HEAD);
            this.kind = kind;
            this.texture = texture;
            this.animation = animation;
        }

        public String getKind() {
            return kind;
        }

        public String getTexture() {
            return texture;
        }

        public float getAnimation() {
            return animation;
        }
    }

    public static class PlayerHeadSpecialModel extends SpecialModel {
        public PlayerHeadSpecialModel() {
            super(SpecialModelType.PLAYER_HEAD);
        }
    }

    public static class ShieldSpecialModel extends SpecialModel {
        public ShieldSpecialModel() {
            super(SpecialModelType.SHIELD);
        }
    }

    public static class ShulkerBoxSpecialModel extends SpecialModel {
        private final String name;
        private final float openness;
        private final String orientation;

        public ShulkerBoxSpecialModel(String name, float openness, String orientation) {
            super(SpecialModelType.SHULKER_BOX);
            this.name = name;
            this.openness = openness;
            this.orientation = orientation;
        }

        public String getName() {
            return name;
        }

        public float getOpenness() {
            return openness;
        }

        public String getOrientation() {
            return orientation;
        }
    }

    public static class StandingSignSpecialModel extends SpecialModel {
        private final String woodType;
        private final String texture;

        public StandingSignSpecialModel(String woodType, String texture) {
            super(SpecialModelType.STANDING_SIGN);
            this.woodType = woodType;
            this.texture = texture;
        }

        public String getWoodType() {
            return woodType;
        }

        public String getTexture() {
            return texture;
        }
    }

    public static class HangingSignSpecialModel extends SpecialModel {
        private final String woodType;
        private final String texture;

        public HangingSignSpecialModel(String woodType, String texture) {
            super(SpecialModelType.HANGING_SIGN);
            this.woodType = woodType;
            this.texture = texture;
        }

        public String getWoodType() {
            return woodType;
        }

        public String getTexture() {
            return texture;
        }
    }

    public static class TridentSpecialModel extends SpecialModel {
        public TridentSpecialModel() {
            super(SpecialModelType.TRIDENT);
        }
    }
}
