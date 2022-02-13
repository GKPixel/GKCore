package com.gkpixel.core.modules;

import com.gkpixel.core.GKCore;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.bukkit.*;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Parse {@link ItemStack} to JSON
 *
 * @author DevSrSouza
 * @version 1.0
 * <p>
 * https://github.com/DevSrSouza/
 * You can find updates here https://gist.github.com/DevSrSouza
 */
public class JsonItemStack {

    private static final String[] BYPASS_CLASS = {"CraftMetaBlockState", "CraftMetaItem"
            /*Glowstone Support*/, "GlowMetaItem"};

    /**
     * Parse the {@link ItemStack} to JSON
     *
     * @param itemStack The {@link ItemStack} instance
     * @return The JSON string
     */
    public static JsonObject toJson(ItemStack itemStack) {
        JsonParser parser = new JsonParser();
        Gson gson = new Gson();
        JsonObject itemJson = new JsonObject();


        itemJson.addProperty("type", itemStack.getType().name());
        if (itemStack.getDurability() > 0) itemJson.addProperty("damage", itemStack.getDurability());
        if (itemStack.getAmount() != 1) itemJson.addProperty("amount", itemStack.getAmount());


        if (itemStack.hasItemMeta()) {
            JsonObject metaJson = new JsonObject();

            ItemMeta meta = itemStack.getItemMeta();


            if (meta.hasDisplayName()) {
                metaJson.addProperty("display-name", meta.getDisplayName());
            }
            if (meta.hasLore()) {
                JsonArray lore = new JsonArray();
                meta.getLore().forEach(str -> lore.add(new JsonPrimitive(str)));
                metaJson.add("lore", lore);
            }
            if (meta.hasEnchants()) {
                JsonArray enchants = new JsonArray();
                meta.getEnchants().forEach((enchantment, integer) -> {
                    enchants.add(new JsonPrimitive(enchantment.getName() + ":" + integer));
                });
                metaJson.add("enchants", enchants);
            }
            if (!meta.getItemFlags().isEmpty()) {
                JsonArray flags = new JsonArray();
                meta.getItemFlags().stream().map(ItemFlag::name).forEach(str -> flags.add(new JsonPrimitive(str)));
                metaJson.add("flags", flags);
            }

            for (String clazz : BYPASS_CLASS) {
                if (meta.getClass().getSimpleName().equals(clazz)) {
                    itemJson.add("meta", metaJson);
                    return itemJson;
                }
            }

            if (meta instanceof SkullMeta) {
                metaJson = parser.parse(GKCore.instance.jsonSystem.gson.toJson(meta)).getAsJsonObject();
            } else if (meta instanceof BannerMeta) {
                BannerMeta bannerMeta = (BannerMeta) meta;
                JsonObject extraMeta = new JsonObject();
                DyeColor baseColor = bannerMeta.getBaseColor();
                if (baseColor != null)
                    extraMeta.addProperty("base-color", baseColor.name());

                if (bannerMeta.numberOfPatterns() > 0) {
                    JsonArray patterns = new JsonArray();
                    bannerMeta.getPatterns()
                            .stream()
                            .map(pattern ->
                                    pattern.getColor().name() + ":" + pattern.getPattern().getIdentifier())
                            .forEach(str -> patterns.add(new JsonPrimitive(str)));
                    extraMeta.add("patterns", patterns);
                }

                metaJson.add("extra-meta", extraMeta);
            } else if (meta instanceof EnchantmentStorageMeta) {
                EnchantmentStorageMeta esmeta = (EnchantmentStorageMeta) meta;
                if (esmeta.hasStoredEnchants()) {
                    JsonObject extraMeta = new JsonObject();
                    JsonArray storedEnchants = new JsonArray();
                    esmeta.getStoredEnchants().forEach((enchantment, integer) -> {
                        storedEnchants.add(new JsonPrimitive(enchantment.getName() + ":" + integer));
                    });
                    extraMeta.add("stored-enchants", storedEnchants);
                    metaJson.add("extra-meta", extraMeta);
                }
            } else if (meta instanceof LeatherArmorMeta) {
                LeatherArmorMeta lameta = (LeatherArmorMeta) meta;
                JsonObject extraMeta = new JsonObject();
                extraMeta.addProperty("color", Integer.toHexString(lameta.getColor().asRGB()));
                metaJson.add("extra-meta", extraMeta);
            } else if (meta instanceof BookMeta) {
                BookMeta bmeta = (BookMeta) meta;
                if (bmeta.hasAuthor() || bmeta.hasPages() || bmeta.hasTitle()) {
                    JsonObject extraMeta = new JsonObject();
                    if (bmeta.hasTitle()) {
                        extraMeta.addProperty("title", bmeta.getTitle());
                    }
                    if (bmeta.hasAuthor()) {
                        extraMeta.addProperty("author", bmeta.getAuthor());
                    }
                    if (bmeta.hasPages()) {
                        JsonArray pages = new JsonArray();
                        bmeta.getPages().forEach(str -> pages.add(new JsonPrimitive(str)));
                        extraMeta.add("pages", pages);
                    }
                    metaJson.add("extra-meta", extraMeta);
                }
            } else if (meta instanceof PotionMeta) {
                PotionMeta pmeta = (PotionMeta) meta;
                if (pmeta.hasCustomEffects()) {
                    JsonObject extraMeta = new JsonObject();

                    JsonArray customEffects = new JsonArray();
                    pmeta.getCustomEffects().forEach(potionEffect -> {
                        customEffects.add(new JsonPrimitive(potionEffect.getType().getName()
                                + ":" + potionEffect.getAmplifier()
                                + ":" + potionEffect.getDuration() / 20));
                    });
                    extraMeta.add("custom-effects", customEffects);

                    metaJson.add("extra-meta", extraMeta);
                }
            } else if (meta instanceof FireworkEffectMeta) {
                FireworkEffectMeta femeta = (FireworkEffectMeta) meta;
                if (femeta.hasEffect()) {
                    FireworkEffect effect = femeta.getEffect();
                    JsonObject extraMeta = new JsonObject();

                    extraMeta.addProperty("type", effect.getType().name());
                    if (effect.hasFlicker()) extraMeta.addProperty("flicker", true);
                    if (effect.hasTrail()) extraMeta.addProperty("trail", true);

                    if (!effect.getColors().isEmpty()) {
                        JsonArray colors = new JsonArray();
                        effect.getColors().forEach(color ->
                                colors.add(new JsonPrimitive(Integer.toHexString(color.asRGB()))));
                        extraMeta.add("colors", colors);
                    }

                    if (!effect.getFadeColors().isEmpty()) {
                        JsonArray fadeColors = new JsonArray();
                        effect.getFadeColors().forEach(color ->
                                fadeColors.add(new JsonPrimitive(Integer.toHexString(color.asRGB()))));
                        extraMeta.add("fade-colors", fadeColors);
                    }

                    metaJson.add("extra-meta", extraMeta);
                }
            } else if (meta instanceof FireworkMeta) {
                FireworkMeta fmeta = (FireworkMeta) meta;

                JsonObject extraMeta = new JsonObject();

                extraMeta.addProperty("power", fmeta.getPower());

                if (fmeta.hasEffects()) {
                    JsonArray effects = new JsonArray();
                    fmeta.getEffects().forEach(effect -> {
                        JsonObject jsonObject = new JsonObject();

                        jsonObject.addProperty("type", effect.getType().name());
                        if (effect.hasFlicker()) jsonObject.addProperty("flicker", true);
                        if (effect.hasTrail()) jsonObject.addProperty("trail", true);

                        if (!effect.getColors().isEmpty()) {
                            JsonArray colors = new JsonArray();
                            effect.getColors().forEach(color ->
                                    colors.add(new JsonPrimitive(Integer.toHexString(color.asRGB()))));
                            jsonObject.add("colors", colors);
                        }

                        if (!effect.getFadeColors().isEmpty()) {
                            JsonArray fadeColors = new JsonArray();
                            effect.getFadeColors().forEach(color ->
                                    fadeColors.add(new JsonPrimitive(Integer.toHexString(color.asRGB()))));
                            jsonObject.add("fade-colors", fadeColors);
                        }

                        effects.add(jsonObject);
                    });
                    extraMeta.add("effects", effects);
                }
                metaJson.add("extra-meta", extraMeta);
            } else if (meta instanceof MapMeta) {
                MapMeta mmeta = (MapMeta) meta;
                JsonObject extraMeta = new JsonObject();

                /* 1.11
                if(mmeta.hasLocationName()) {
                    extraMeta.addProperty("location-name", mmeta.getLocationName());
                }
                if(mmeta.hasColor()) {
                    extraMeta.addProperty("color", Integer.toHexString(mmeta.getColor().asRGB()));
                }*/
                extraMeta.addProperty("scaling", mmeta.isScaling());

                metaJson.add("extra-meta", extraMeta);
            }

            itemJson.add("meta", metaJson);
        }
        return itemJson;
    }

    /**
     * Parse a JSON to {@link ItemStack}
     *
     * @param itemJson The JSON string
     * @return The {@link ItemStack} or null if not succeed
     */
    public static ItemStack fromJson(JsonObject itemJson) {
        if (true) {

            JsonElement typeElement = itemJson.get("type");
            JsonElement dataElement = itemJson.get("damage");
            JsonElement amountElement = itemJson.get("amount");

            if (typeElement.isJsonPrimitive()) {

                String type = typeElement.getAsString();
                short data = dataElement != null ? dataElement.getAsShort() : 0;
                int amount = amountElement != null ? amountElement.getAsInt() : 1;

                ItemStack itemStack = new ItemStack(Material.getMaterial(type));
                itemStack.setDurability(data);
                itemStack.setAmount(amount);

                JsonElement itemMetaElement = itemJson.get("meta");
                if (itemMetaElement != null && itemMetaElement.isJsonObject()) {

                    ItemMeta meta = itemStack.getItemMeta();
                    JsonObject metaJson = itemMetaElement.getAsJsonObject();

                    JsonElement displaynameElement = metaJson.get("display-name");
                    JsonElement loreElement = metaJson.get("lore");
                    JsonElement enchants = metaJson.get("enchants");
                    JsonElement flagsElement = metaJson.get("flags");
                    if (displaynameElement != null && displaynameElement.isJsonPrimitive()) {
                        meta.setDisplayName(displaynameElement.getAsString());
                    }
                    if (loreElement != null && loreElement.isJsonArray()) {
                        JsonArray jarray = loreElement.getAsJsonArray();
                        List<String> lore = new ArrayList<>(jarray.size());
                        jarray.forEach(jsonElement -> {
                            if (jsonElement.isJsonPrimitive()) lore.add(jsonElement.getAsString());
                        });
                        meta.setLore(lore);
                    }
                    if (enchants != null && enchants.isJsonArray()) {
                        JsonArray jarray = enchants.getAsJsonArray();
                        jarray.forEach(jsonElement -> {
                            if (jsonElement.isJsonPrimitive()) {
                                String enchantString = jsonElement.getAsString();
                                if (enchantString.contains(":")) {
                                    try {
                                        String[] splitEnchant = enchantString.split(":");
                                        Enchantment enchantment = Enchantment.getByName(splitEnchant[0]);
                                        int level = Integer.parseInt(splitEnchant[1]);
                                        if (enchantment != null && level > 0) {
                                            meta.addEnchant(enchantment, level, true);
                                        }
                                    } catch (NumberFormatException ex) {
                                    }
                                }
                            }
                        });
                    }
                    if (flagsElement != null && flagsElement.isJsonArray()) {
                        JsonArray jarray = flagsElement.getAsJsonArray();
                        jarray.forEach(jsonElement -> {
                            if (jsonElement.isJsonPrimitive()) {
                                for (ItemFlag flag : ItemFlag.values()) {
                                    if (flag.name().equalsIgnoreCase(jsonElement.getAsString())) {
                                        meta.addItemFlags(flag);
                                        break;
                                    }
                                }
                            }
                        });
                    }

                    for (String clazz : BYPASS_CLASS) {
                        if (meta.getClass().getSimpleName().equals(clazz)) {
                            return itemStack;
                        }
                    }

                    JsonElement extrametaElement = metaJson.get("extra-meta");

                    if (true) {

                        try {
                            JsonObject extraJson = null;//extrametaElement.getAsJsonObject();
                            if (meta instanceof SkullMeta) {
                                SkullMeta sm = (SkullMeta) meta;
                                sm = GKCore.instance.jsonSystem.gson.fromJson(metaJson, SkullMeta.class);
                                //GKCore.G7_log(GKCore.instance.jsonSystem.gson.toJson(sm));

                            } else if (meta instanceof BannerMeta) {
                                JsonElement baseColorElement = extraJson.get("base-color");
                                JsonElement patternsElement = extraJson.get("patterns");
                                BannerMeta bmeta = (BannerMeta) meta;
                                if (baseColorElement != null && baseColorElement.isJsonPrimitive()) {
                                    try {
                                        Optional<DyeColor> color = Arrays.stream(DyeColor.values())
                                                .filter(dyeColor -> dyeColor.name().equalsIgnoreCase(baseColorElement.getAsString()))
                                                .findFirst();
                                        if (color.isPresent()) {
                                            bmeta.setBaseColor(color.get());
                                        }
                                    } catch (NumberFormatException ex) {
                                    }
                                }
                                if (patternsElement != null && patternsElement.isJsonArray()) {
                                    JsonArray jarray = patternsElement.getAsJsonArray();
                                    List<Pattern> patterns = new ArrayList<>(jarray.size());
                                    jarray.forEach(jsonElement -> {
                                        String patternString = jsonElement.getAsString();
                                        if (patternString.contains(":")) {
                                            String[] splitPattern = patternString.split(":");
                                            Optional<DyeColor> color = Arrays.stream(DyeColor.values())
                                                    .filter(dyeColor -> dyeColor.name().equalsIgnoreCase(splitPattern[0]))
                                                    .findFirst();
                                            PatternType patternType = PatternType.getByIdentifier(splitPattern[1]);
                                            if (color.isPresent() && patternType != null) {
                                                patterns.add(new Pattern(color.get(), patternType));
                                            }
                                        }
                                    });
                                    if (!patterns.isEmpty()) bmeta.setPatterns(patterns);
                                }
                            } else if (meta instanceof EnchantmentStorageMeta) {
                                JsonElement storedEnchantsElement = extraJson.get("stored-enchants");
                                if (storedEnchantsElement != null && storedEnchantsElement.isJsonArray()) {
                                    EnchantmentStorageMeta esmeta = (EnchantmentStorageMeta) meta;
                                    JsonArray jarray = storedEnchantsElement.getAsJsonArray();
                                    jarray.forEach(jsonElement -> {
                                        if (jsonElement.isJsonPrimitive()) {
                                            String enchantString = jsonElement.getAsString();
                                            if (enchantString.contains(":")) {
                                                try {
                                                    String[] splitEnchant = enchantString.split(":");
                                                    Enchantment enchantment = Enchantment.getByName(splitEnchant[0]);
                                                    int level = Integer.parseInt(splitEnchant[1]);
                                                    if (enchantment != null && level > 0) {
                                                        esmeta.addStoredEnchant(enchantment, level, true);
                                                    }
                                                } catch (NumberFormatException ex) {
                                                }
                                            }
                                        }
                                    });
                                }
                            } else if (meta instanceof LeatherArmorMeta) {
                                JsonElement colorElement = extraJson.get("color");
                                if (colorElement != null && colorElement.isJsonPrimitive()) {
                                    LeatherArmorMeta lameta = (LeatherArmorMeta) meta;
                                    try {
                                        lameta.setColor(Color.fromRGB(Integer.parseInt(colorElement.getAsString(), 16)));
                                    } catch (NumberFormatException ex) {
                                    }
                                }
                            } else if (meta instanceof BookMeta) {
                                JsonElement titleElement = extraJson.get("title");
                                JsonElement authorElement = extraJson.get("author");
                                JsonElement pagesElement = extraJson.get("pages");

                                BookMeta bmeta = (BookMeta) meta;
                                if (titleElement != null && titleElement.isJsonPrimitive()) {
                                    bmeta.setTitle(titleElement.getAsString());
                                }
                                if (authorElement != null && authorElement.isJsonPrimitive()) {
                                    bmeta.setAuthor(authorElement.getAsString());
                                }
                                if (pagesElement != null && pagesElement.isJsonArray()) {
                                    JsonArray jarray = pagesElement.getAsJsonArray();
                                    List<String> pages = new ArrayList<>(jarray.size());
                                    jarray.forEach(jsonElement -> {
                                        if (jsonElement.isJsonPrimitive()) pages.add(jsonElement.getAsString());
                                    });
                                    bmeta.setPages(pages);
                                }

                            } else if (meta instanceof PotionMeta) {
                                JsonElement customEffectsElement = extraJson.get("custom-effects");
                                if (customEffectsElement != null && customEffectsElement.isJsonArray()) {
                                    PotionMeta pmeta = (PotionMeta) meta;
                                    JsonArray jarray = customEffectsElement.getAsJsonArray();
                                    jarray.forEach(jsonElement -> {
                                        if (jsonElement.isJsonPrimitive()) {
                                            String enchantString = jsonElement.getAsString();
                                            if (enchantString.contains(":")) {
                                                try {
                                                    String[] splitPotions = enchantString.split(":");
                                                    PotionEffectType potionType = PotionEffectType.getByName(splitPotions[0]);
                                                    int amplifier = Integer.parseInt(splitPotions[1]);
                                                    int duration = Integer.parseInt(splitPotions[2]) * 20;
                                                    if (potionType != null) {
                                                        pmeta.addCustomEffect(new PotionEffect(potionType, amplifier, duration), true);
                                                    }
                                                } catch (NumberFormatException ex) {
                                                }
                                            }
                                        }
                                    });
                                }
                            } else if (meta instanceof FireworkEffectMeta) {
                                JsonElement effectTypeElement = extraJson.get("type");
                                JsonElement flickerElement = extraJson.get("flicker");
                                JsonElement trailElement = extraJson.get("trail");
                                JsonElement colorsElement = extraJson.get("colors");
                                JsonElement fadeColorsElement = extraJson.get("fade-colors");

                                if (effectTypeElement != null && effectTypeElement.isJsonPrimitive()) {
                                    FireworkEffectMeta femeta = (FireworkEffectMeta) meta;

                                    FireworkEffect.Type effectType = FireworkEffect.Type.valueOf(effectTypeElement.getAsString());

                                    if (effectType != null) {
                                        List<Color> colors = new ArrayList<>();
                                        if (colorsElement != null && colorsElement.isJsonArray())
                                            colorsElement.getAsJsonArray().forEach(colorElement -> {
                                                if (colorElement.isJsonPrimitive())
                                                    colors.add(Color.fromRGB(Integer.parseInt(colorElement.getAsString(), 16)));
                                            });

                                        List<Color> fadeColors = new ArrayList<>();
                                        if (fadeColorsElement != null && fadeColorsElement.isJsonArray())
                                            fadeColorsElement.getAsJsonArray().forEach(colorElement -> {
                                                if (colorElement.isJsonPrimitive())
                                                    fadeColors.add(Color.fromRGB(Integer.parseInt(colorElement.getAsString(), 16)));
                                            });

                                        FireworkEffect.Builder builder = FireworkEffect.builder().with(effectType);

                                        if (flickerElement != null && flickerElement.isJsonPrimitive())
                                            builder.flicker(flickerElement.getAsBoolean());
                                        if (trailElement != null && trailElement.isJsonPrimitive())
                                            builder.trail(trailElement.getAsBoolean());

                                        if (!colors.isEmpty()) builder.withColor(colors);
                                        if (!fadeColors.isEmpty()) builder.withFade(fadeColors);

                                        femeta.setEffect(builder.build());
                                    }
                                }
                            } else if (meta instanceof FireworkMeta) {
                                FireworkMeta fmeta = (FireworkMeta) meta;

                                JsonElement effectArrayElement = extraJson.get("effects");
                                JsonElement powerElement = extraJson.get("power");

                                if (powerElement != null && powerElement.isJsonPrimitive()) {
                                    fmeta.setPower(powerElement.getAsInt());
                                }

                                if (effectArrayElement != null && effectArrayElement.isJsonArray()) {

                                    effectArrayElement.getAsJsonArray().forEach(jsonElement -> {
                                        if (jsonElement.isJsonObject()) {

                                            JsonObject jsonObject = jsonElement.getAsJsonObject();

                                            JsonElement effectTypeElement = jsonObject.get("type");
                                            JsonElement flickerElement = jsonObject.get("flicker");
                                            JsonElement trailElement = jsonObject.get("trail");
                                            JsonElement colorsElement = jsonObject.get("colors");
                                            JsonElement fadeColorsElement = jsonObject.get("fade-colors");

                                            if (effectTypeElement != null && effectTypeElement.isJsonPrimitive()) {

                                                FireworkEffect.Type effectType = FireworkEffect.Type.valueOf(effectTypeElement.getAsString());

                                                if (effectType != null) {
                                                    List<Color> colors = new ArrayList<>();
                                                    if (colorsElement != null && colorsElement.isJsonArray())
                                                        colorsElement.getAsJsonArray().forEach(colorElement -> {
                                                            if (colorElement.isJsonPrimitive())
                                                                colors.add(Color.fromRGB(Integer.parseInt(colorElement.getAsString(), 16)));
                                                        });

                                                    List<Color> fadeColors = new ArrayList<>();
                                                    if (fadeColorsElement != null && fadeColorsElement.isJsonArray())
                                                        fadeColorsElement.getAsJsonArray().forEach(colorElement -> {
                                                            if (colorElement.isJsonPrimitive())
                                                                fadeColors.add(Color.fromRGB(Integer.parseInt(colorElement.getAsString(), 16)));
                                                        });

                                                    FireworkEffect.Builder builder = FireworkEffect.builder().with(effectType);

                                                    if (flickerElement != null && flickerElement.isJsonPrimitive())
                                                        builder.flicker(flickerElement.getAsBoolean());
                                                    if (trailElement != null && trailElement.isJsonPrimitive())
                                                        builder.trail(trailElement.getAsBoolean());

                                                    if (!colors.isEmpty()) builder.withColor(colors);
                                                    if (!fadeColors.isEmpty()) builder.withFade(fadeColors);

                                                    fmeta.addEffect(builder.build());
                                                }
                                            }
                                        }
                                    });
                                }
                            } else if (meta instanceof MapMeta) {
                                MapMeta mmeta = (MapMeta) meta;

                                JsonElement scalingElement = extraJson.get("scaling");
                                if (scalingElement != null && scalingElement.isJsonPrimitive()) {
                                    mmeta.setScaling(scalingElement.getAsBoolean());
                                }

                                /* 1.11
                                JsonElement locationNameElement = extraJson.get("location-name");
                                if(locationNameElement != null && locationNameElement.isJsonPrimitive()) {
                                    mmeta.setLocationName(locationNameElement.getAsString());
                                }
                                JsonElement colorElement = extraJson.get("color");
                                if(colorElement != null && colorElement.isJsonPrimitive()) {
                                    mmeta.setColor(Color.fromRGB(Integer.parseInt(colorElement.getAsString(), 16)));
                                }*/
                            }
                        } catch (Exception e) {
                            return null;
                        }
                    }
                    itemStack.setItemMeta(meta);
                }
                return itemStack;
            } else return null;
        } else return null;
    }

    public static ItemStack fromJson(String json) {
        if (true) {
            JsonParser parser = new JsonParser();
            JsonObject itemJson = parser.parse(json).getAsJsonObject();

            JsonElement typeElement = itemJson.get("type");
            JsonElement dataElement = itemJson.get("damage");
            JsonElement amountElement = itemJson.get("amount");

            if (typeElement.isJsonPrimitive()) {

                String type = typeElement.getAsString();
                short data = dataElement != null ? dataElement.getAsShort() : 0;
                int amount = amountElement != null ? amountElement.getAsInt() : 1;

                ItemStack itemStack = new ItemStack(Material.getMaterial(type));
                itemStack.setDurability(data);
                itemStack.setAmount(amount);

                JsonElement itemMetaElement = itemJson.get("meta");
                if (itemMetaElement != null && itemMetaElement.isJsonObject()) {

                    ItemMeta meta = itemStack.getItemMeta();
                    JsonObject metaJson = itemMetaElement.getAsJsonObject();

                    JsonElement displaynameElement = metaJson.get("display-name");
                    JsonElement loreElement = metaJson.get("lore");
                    JsonElement enchants = metaJson.get("enchants");
                    JsonElement flagsElement = metaJson.get("flags");
                    if (displaynameElement != null && displaynameElement.isJsonPrimitive()) {
                        meta.setDisplayName(displaynameElement.getAsString());
                    }
                    GKCore.G7_log("fuck 591: " + meta);
                    if (loreElement != null && loreElement.isJsonArray()) {
                        JsonArray jarray = loreElement.getAsJsonArray();
                        List<String> lore = new ArrayList<>(jarray.size());
                        jarray.forEach(jsonElement -> {
                            if (jsonElement.isJsonPrimitive()) lore.add(jsonElement.getAsString());
                        });
                        meta.setLore(lore);
                    }
                    if (enchants != null && enchants.isJsonArray()) {
                        JsonArray jarray = enchants.getAsJsonArray();
                        jarray.forEach(jsonElement -> {
                            if (jsonElement.isJsonPrimitive()) {
                                String enchantString = jsonElement.getAsString();
                                if (enchantString.contains(":")) {
                                    try {
                                        String[] splitEnchant = enchantString.split(":");
                                        Enchantment enchantment = Enchantment.getByName(splitEnchant[0]);
                                        int level = Integer.parseInt(splitEnchant[1]);
                                        if (enchantment != null && level > 0) {
                                            meta.addEnchant(enchantment, level, true);
                                        }
                                    } catch (NumberFormatException ex) {
                                    }
                                }
                            }
                        });
                    }
                    if (flagsElement != null && flagsElement.isJsonArray()) {
                        JsonArray jarray = flagsElement.getAsJsonArray();
                        jarray.forEach(jsonElement -> {
                            if (jsonElement.isJsonPrimitive()) {
                                for (ItemFlag flag : ItemFlag.values()) {
                                    if (flag.name().equalsIgnoreCase(jsonElement.getAsString())) {
                                        meta.addItemFlags(flag);
                                        break;
                                    }
                                }
                            }
                        });
                    }
                    for (String clazz : BYPASS_CLASS) {
                        if (meta.getClass().getSimpleName().equals(clazz)) {
                            return itemStack;
                        }
                    }

                    JsonElement extrametaElement = metaJson.get("extra-meta");

                    if (extrametaElement != null) {
                        try {
                            JsonObject extraJson = extrametaElement.getAsJsonObject();
                            if (meta instanceof SkullMeta) {
                                SkullMeta sm = (SkullMeta) meta;
                                sm = GKCore.instance.jsonSystem.gson.fromJson(metaJson, SkullMeta.class);
                                //GKCore.G7_log(GKCore.instance.jsonSystem.gson.toJson(sm));

                            } else if (meta instanceof BannerMeta) {
                                JsonElement baseColorElement = extraJson.get("base-color");
                                JsonElement patternsElement = extraJson.get("patterns");
                                BannerMeta bmeta = (BannerMeta) meta;
                                if (baseColorElement != null && baseColorElement.isJsonPrimitive()) {
                                    try {
                                        Optional<DyeColor> color = Arrays.stream(DyeColor.values())
                                                .filter(dyeColor -> dyeColor.name().equalsIgnoreCase(baseColorElement.getAsString()))
                                                .findFirst();
                                        if (color.isPresent()) {
                                            bmeta.setBaseColor(color.get());
                                        }
                                    } catch (NumberFormatException ex) {
                                    }
                                }
                                if (patternsElement != null && patternsElement.isJsonArray()) {
                                    JsonArray jarray = patternsElement.getAsJsonArray();
                                    List<Pattern> patterns = new ArrayList<>(jarray.size());
                                    jarray.forEach(jsonElement -> {
                                        String patternString = jsonElement.getAsString();
                                        if (patternString.contains(":")) {
                                            String[] splitPattern = patternString.split(":");
                                            Optional<DyeColor> color = Arrays.stream(DyeColor.values())
                                                    .filter(dyeColor -> dyeColor.name().equalsIgnoreCase(splitPattern[0]))
                                                    .findFirst();
                                            PatternType patternType = PatternType.getByIdentifier(splitPattern[1]);
                                            if (color.isPresent() && patternType != null) {
                                                patterns.add(new Pattern(color.get(), patternType));
                                            }
                                        }
                                    });
                                    if (!patterns.isEmpty()) bmeta.setPatterns(patterns);
                                }
                            } else if (meta instanceof EnchantmentStorageMeta) {
                                JsonElement storedEnchantsElement = extraJson.get("stored-enchants");
                                if (storedEnchantsElement != null && storedEnchantsElement.isJsonArray()) {
                                    EnchantmentStorageMeta esmeta = (EnchantmentStorageMeta) meta;
                                    JsonArray jarray = storedEnchantsElement.getAsJsonArray();
                                    jarray.forEach(jsonElement -> {
                                        if (jsonElement.isJsonPrimitive()) {
                                            String enchantString = jsonElement.getAsString();
                                            if (enchantString.contains(":")) {
                                                try {
                                                    String[] splitEnchant = enchantString.split(":");
                                                    Enchantment enchantment = Enchantment.getByName(splitEnchant[0]);
                                                    int level = Integer.parseInt(splitEnchant[1]);
                                                    if (enchantment != null && level > 0) {
                                                        esmeta.addStoredEnchant(enchantment, level, true);
                                                    }
                                                } catch (NumberFormatException ex) {
                                                }
                                            }
                                        }
                                    });
                                }
                            } else if (meta instanceof LeatherArmorMeta) {
                                JsonElement colorElement = extraJson.get("color");
                                if (colorElement != null && colorElement.isJsonPrimitive()) {
                                    LeatherArmorMeta lameta = (LeatherArmorMeta) meta;
                                    try {
                                        lameta.setColor(Color.fromRGB(Integer.parseInt(colorElement.getAsString(), 16)));
                                    } catch (NumberFormatException ex) {
                                    }
                                }
                            } else if (meta instanceof BookMeta) {
                                JsonElement titleElement = extraJson.get("title");
                                JsonElement authorElement = extraJson.get("author");
                                JsonElement pagesElement = extraJson.get("pages");

                                BookMeta bmeta = (BookMeta) meta;
                                if (titleElement != null && titleElement.isJsonPrimitive()) {
                                    bmeta.setTitle(titleElement.getAsString());
                                }
                                if (authorElement != null && authorElement.isJsonPrimitive()) {
                                    bmeta.setAuthor(authorElement.getAsString());
                                }
                                if (pagesElement != null && pagesElement.isJsonArray()) {
                                    JsonArray jarray = pagesElement.getAsJsonArray();
                                    List<String> pages = new ArrayList<>(jarray.size());
                                    jarray.forEach(jsonElement -> {
                                        if (jsonElement.isJsonPrimitive()) pages.add(jsonElement.getAsString());
                                    });
                                    bmeta.setPages(pages);
                                }

                            } else if (meta instanceof PotionMeta) {
                                JsonElement customEffectsElement = extraJson.get("custom-effects");
                                if (customEffectsElement != null && customEffectsElement.isJsonArray()) {
                                    PotionMeta pmeta = (PotionMeta) meta;
                                    JsonArray jarray = customEffectsElement.getAsJsonArray();
                                    jarray.forEach(jsonElement -> {
                                        if (jsonElement.isJsonPrimitive()) {
                                            String enchantString = jsonElement.getAsString();
                                            if (enchantString.contains(":")) {
                                                try {
                                                    String[] splitPotions = enchantString.split(":");
                                                    PotionEffectType potionType = PotionEffectType.getByName(splitPotions[0]);
                                                    int amplifier = Integer.parseInt(splitPotions[1]);
                                                    int duration = Integer.parseInt(splitPotions[2]) * 20;
                                                    if (potionType != null) {
                                                        pmeta.addCustomEffect(new PotionEffect(potionType, amplifier, duration), true);
                                                    }
                                                } catch (NumberFormatException ex) {
                                                }
                                            }
                                        }
                                    });
                                }
                            } else if (meta instanceof FireworkEffectMeta) {
                                JsonElement effectTypeElement = extraJson.get("type");
                                JsonElement flickerElement = extraJson.get("flicker");
                                JsonElement trailElement = extraJson.get("trail");
                                JsonElement colorsElement = extraJson.get("colors");
                                JsonElement fadeColorsElement = extraJson.get("fade-colors");

                                if (effectTypeElement != null && effectTypeElement.isJsonPrimitive()) {
                                    FireworkEffectMeta femeta = (FireworkEffectMeta) meta;

                                    FireworkEffect.Type effectType = FireworkEffect.Type.valueOf(effectTypeElement.getAsString());

                                    if (effectType != null) {
                                        List<Color> colors = new ArrayList<>();
                                        if (colorsElement != null && colorsElement.isJsonArray())
                                            colorsElement.getAsJsonArray().forEach(colorElement -> {
                                                if (colorElement.isJsonPrimitive())
                                                    colors.add(Color.fromRGB(Integer.parseInt(colorElement.getAsString(), 16)));
                                            });

                                        List<Color> fadeColors = new ArrayList<>();
                                        if (fadeColorsElement != null && fadeColorsElement.isJsonArray())
                                            fadeColorsElement.getAsJsonArray().forEach(colorElement -> {
                                                if (colorElement.isJsonPrimitive())
                                                    fadeColors.add(Color.fromRGB(Integer.parseInt(colorElement.getAsString(), 16)));
                                            });

                                        FireworkEffect.Builder builder = FireworkEffect.builder().with(effectType);

                                        if (flickerElement != null && flickerElement.isJsonPrimitive())
                                            builder.flicker(flickerElement.getAsBoolean());
                                        if (trailElement != null && trailElement.isJsonPrimitive())
                                            builder.trail(trailElement.getAsBoolean());

                                        if (!colors.isEmpty()) builder.withColor(colors);
                                        if (!fadeColors.isEmpty()) builder.withFade(fadeColors);

                                        femeta.setEffect(builder.build());
                                    }
                                }
                            } else if (meta instanceof FireworkMeta) {
                                FireworkMeta fmeta = (FireworkMeta) meta;

                                JsonElement effectArrayElement = extraJson.get("effects");
                                JsonElement powerElement = extraJson.get("power");

                                if (powerElement != null && powerElement.isJsonPrimitive()) {
                                    fmeta.setPower(powerElement.getAsInt());
                                }

                                if (effectArrayElement != null && effectArrayElement.isJsonArray()) {

                                    effectArrayElement.getAsJsonArray().forEach(jsonElement -> {
                                        if (jsonElement.isJsonObject()) {

                                            JsonObject jsonObject = jsonElement.getAsJsonObject();

                                            JsonElement effectTypeElement = jsonObject.get("type");
                                            JsonElement flickerElement = jsonObject.get("flicker");
                                            JsonElement trailElement = jsonObject.get("trail");
                                            JsonElement colorsElement = jsonObject.get("colors");
                                            JsonElement fadeColorsElement = jsonObject.get("fade-colors");

                                            if (effectTypeElement != null && effectTypeElement.isJsonPrimitive()) {

                                                FireworkEffect.Type effectType = FireworkEffect.Type.valueOf(effectTypeElement.getAsString());

                                                if (effectType != null) {
                                                    List<Color> colors = new ArrayList<>();
                                                    if (colorsElement != null && colorsElement.isJsonArray())
                                                        colorsElement.getAsJsonArray().forEach(colorElement -> {
                                                            if (colorElement.isJsonPrimitive())
                                                                colors.add(Color.fromRGB(Integer.parseInt(colorElement.getAsString(), 16)));
                                                        });

                                                    List<Color> fadeColors = new ArrayList<>();
                                                    if (fadeColorsElement != null && fadeColorsElement.isJsonArray())
                                                        fadeColorsElement.getAsJsonArray().forEach(colorElement -> {
                                                            if (colorElement.isJsonPrimitive())
                                                                fadeColors.add(Color.fromRGB(Integer.parseInt(colorElement.getAsString(), 16)));
                                                        });

                                                    FireworkEffect.Builder builder = FireworkEffect.builder().with(effectType);

                                                    if (flickerElement != null && flickerElement.isJsonPrimitive())
                                                        builder.flicker(flickerElement.getAsBoolean());
                                                    if (trailElement != null && trailElement.isJsonPrimitive())
                                                        builder.trail(trailElement.getAsBoolean());

                                                    if (!colors.isEmpty()) builder.withColor(colors);
                                                    if (!fadeColors.isEmpty()) builder.withFade(fadeColors);

                                                    fmeta.addEffect(builder.build());
                                                }
                                            }
                                        }
                                    });
                                }
                            } else if (meta instanceof MapMeta) {
                                MapMeta mmeta = (MapMeta) meta;

                                JsonElement scalingElement = extraJson.get("scaling");
                                if (scalingElement != null && scalingElement.isJsonPrimitive()) {
                                    mmeta.setScaling(scalingElement.getAsBoolean());
                                }

                                /* 1.11
                                JsonElement locationNameElement = extraJson.get("location-name");
                                if(locationNameElement != null && locationNameElement.isJsonPrimitive()) {
                                    mmeta.setLocationName(locationNameElement.getAsString());
                                }
                                JsonElement colorElement = extraJson.get("color");
                                if(colorElement != null && colorElement.isJsonPrimitive()) {
                                    mmeta.setColor(Color.fromRGB(Integer.parseInt(colorElement.getAsString(), 16)));
                                }*/
                            }
                        } catch (Exception e) {
                            return null;
                        }
                    }
                    itemStack.setItemMeta(meta);
                }
                return itemStack;
            } else return null;
        } else return null;
    }
}


class PatternAdaptor implements JsonSerializer<Pattern>, JsonDeserializer<Pattern> {
    @Override
    public JsonElement serialize(Pattern pattern, Type typeOfSrc, JsonSerializationContext context) {
        //GKCore.G7_log("test 1");
        JsonObject patternJson = new JsonObject();
        patternJson.addProperty("color", pattern.getColor().toString());
        patternJson.addProperty("pattern", pattern.getPattern().toString());
        return patternJson;
    }

    @Override
    public Pattern deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        //GKCore.G7_log("test 2");
        JsonObject patternJson = json.getAsJsonObject();
        String colorStr = patternJson.get("color").getAsString();
        String patternStr = patternJson.get("pattern").getAsString();
        Pattern pattern = new Pattern(DyeColor.valueOf(colorStr), PatternType.valueOf(patternStr));

        return pattern;
    }

}

class BannerMetaAdaptor implements JsonSerializer<BannerMeta>, JsonDeserializer<BannerMeta> {

    @Override
    public JsonElement serialize(BannerMeta bannerMeta, Type typeOfSrc, JsonSerializationContext context) {
        //GKCore.G7_log("test 1");
        JsonObject metaJson = new JsonObject();

        if (bannerMeta.hasDisplayName()) {
            metaJson.addProperty("display-name", bannerMeta.getDisplayName());
        }
        if (bannerMeta.hasLore()) {
            JsonArray lore = new JsonArray();
            bannerMeta.getLore().forEach(str -> lore.add(new JsonPrimitive(str)));
            metaJson.add("lore", lore);
        }
        if (bannerMeta.hasEnchants()) {
            JsonArray enchants = new JsonArray();
            bannerMeta.getEnchants().forEach((enchantment, integer) -> {
                enchants.add(new JsonPrimitive(enchantment.getName() + ":" + integer));
            });
            metaJson.add("enchants", enchants);
        }
        if (!bannerMeta.getItemFlags().isEmpty()) {
            JsonArray flags = new JsonArray();
            bannerMeta.getItemFlags().stream().map(ItemFlag::name).forEach(str -> flags.add(new JsonPrimitive(str)));
            metaJson.add("flags", flags);
        }

        JsonObject extraMeta = new JsonObject();
        DyeColor baseColor = bannerMeta.getBaseColor();
        if (baseColor != null)
            extraMeta.addProperty("base-color", baseColor.name());

        if (bannerMeta.numberOfPatterns() > 0) {
            JsonArray patterns = new JsonArray();
            bannerMeta.getPatterns()
                    .stream()
                    .map(pattern ->
                            pattern.getColor().name() + ":" + pattern.getPattern().getIdentifier())
                    .forEach(str -> patterns.add(new JsonPrimitive(str)));
            extraMeta.add("patterns", patterns);
        }

        metaJson.add("extra-meta", extraMeta);
        return metaJson;
    }

    @Override
    public BannerMeta deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        //GKCore.G7_log("test 2" + json.toString());
        ItemStack itemStack = new ItemStack(Material.WHITE_BANNER, 1);
        BannerMeta meta = (BannerMeta) itemStack.getItemMeta();
        JsonObject metaJson = json.getAsJsonObject();

        JsonElement displaynameElement = metaJson.get("display-name");
        JsonElement loreElement = metaJson.get("lore");
        JsonElement enchants = metaJson.get("enchants");
        JsonElement flagsElement = metaJson.get("flags");
        if (displaynameElement != null && displaynameElement.isJsonPrimitive()) {
            meta.setDisplayName(displaynameElement.getAsString());
        }
        if (loreElement != null && loreElement.isJsonArray()) {
            JsonArray jarray = loreElement.getAsJsonArray();
            List<String> lore = new ArrayList<>(jarray.size());
            jarray.forEach(jsonElement -> {
                if (jsonElement.isJsonPrimitive()) lore.add(jsonElement.getAsString());
            });
            meta.setLore(lore);
        }
        if (enchants != null && enchants.isJsonArray()) {
            JsonArray jarray = enchants.getAsJsonArray();
            jarray.forEach(jsonElement -> {
                if (jsonElement.isJsonPrimitive()) {
                    String enchantString = jsonElement.getAsString();
                    if (enchantString.contains(":")) {
                        try {
                            String[] splitEnchant = enchantString.split(":");
                            Enchantment enchantment = Enchantment.getByName(splitEnchant[0]);
                            int level = Integer.parseInt(splitEnchant[1]);
                            if (enchantment != null && level > 0) {
                                meta.addEnchant(enchantment, level, true);
                            }
                        } catch (NumberFormatException ex) {
                        }
                    }
                }
            });
        }
        if (flagsElement != null && flagsElement.isJsonArray()) {
            JsonArray jarray = flagsElement.getAsJsonArray();
            jarray.forEach(jsonElement -> {
                if (jsonElement.isJsonPrimitive()) {
                    for (ItemFlag flag : ItemFlag.values()) {
                        if (flag.name().equalsIgnoreCase(jsonElement.getAsString())) {
                            meta.addItemFlags(flag);
                            break;
                        }
                    }
                }
            });
        }


        JsonElement extrametaElement = metaJson.get("extra-meta");
        JsonElement baseColorElement = extrametaElement.getAsJsonObject().get("base-color");
        JsonElement patternsElement = extrametaElement.getAsJsonObject().get("patterns");
        BannerMeta bmeta = meta;

        if (baseColorElement != null) {
            if (baseColorElement.isJsonPrimitive()) {
                try {
                    Optional<DyeColor> color = Arrays.stream(DyeColor.values())
                            .filter(dyeColor -> dyeColor.name().equalsIgnoreCase(baseColorElement.getAsString()))
                            .findFirst();
                    if (color.isPresent()) {
                        bmeta.setBaseColor(color.get());
                    }
                } catch (NumberFormatException ex) {
                }
            }
        }
        if (patternsElement != null && patternsElement.isJsonArray()) {
            JsonArray jarray = patternsElement.getAsJsonArray();
            List<Pattern> patterns = new ArrayList<>(jarray.size());
            jarray.forEach(jsonElement -> {
                String patternString = jsonElement.getAsString();
                if (patternString.contains(":")) {
                    String[] splitPattern = patternString.split(":");
                    Optional<DyeColor> color = Arrays.stream(DyeColor.values())
                            .filter(dyeColor -> dyeColor.name().equalsIgnoreCase(splitPattern[0]))
                            .findFirst();
                    PatternType patternType = PatternType.getByIdentifier(splitPattern[1]);
                    if (color.isPresent() && patternType != null) {
                        patterns.add(new Pattern(color.get(), patternType));
                    }
                }
            });
            if (!patterns.isEmpty()) bmeta.setPatterns(patterns);
        }
        GKCore.G7_log("deserializing BannerMeta: " + bmeta.getPatterns().size());
        return bmeta;
    }

}

class ItemMetaAdaptor implements JsonSerializer<ItemMeta>, JsonDeserializer<ItemMeta> {
    @Override
    public JsonElement serialize(ItemMeta meta, Type typeOfSrc, JsonSerializationContext context) {
        //GKCore.G7_log("test 1");
        JsonObject metaJson = new JsonObject();

        if (meta.hasDisplayName()) {
            metaJson.addProperty("display-name", meta.getDisplayName());
        }
        if (meta.hasLore()) {
            JsonArray lore = new JsonArray();
            meta.getLore().forEach(str -> lore.add(new JsonPrimitive(str)));
            metaJson.add("lore", lore);
        }
        if (meta.hasEnchants()) {
            JsonArray enchants = new JsonArray();
            meta.getEnchants().forEach((enchantment, integer) -> {
                enchants.add(new JsonPrimitive(enchantment.getName() + ":" + integer));
            });
            metaJson.add("enchants", enchants);
        }
        if (!meta.getItemFlags().isEmpty()) {
            JsonArray flags = new JsonArray();
            meta.getItemFlags().stream().map(ItemFlag::name).forEach(str -> flags.add(new JsonPrimitive(str)));
            metaJson.add("flags", flags);
        }

        if (meta instanceof BannerMeta) {
            BannerMeta bannerMeta = (BannerMeta) meta;
            JsonObject extraMeta = new JsonObject();
            DyeColor baseColor = bannerMeta.getBaseColor();
            if (baseColor != null)
                extraMeta.addProperty("base-color", baseColor.name());

            if (bannerMeta.numberOfPatterns() > 0) {
                JsonArray patterns = new JsonArray();
                bannerMeta.getPatterns()
                        .stream()
                        .map(pattern ->
                                pattern.getColor().name() + ":" + pattern.getPattern().getIdentifier())
                        .forEach(str -> patterns.add(new JsonPrimitive(str)));
                extraMeta.add("patterns", patterns);
            }

            metaJson.add("extra-meta", extraMeta);
        }
        return metaJson;
    }

    @Override
    public ItemMeta deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        ItemStack itemStack = new ItemStack(Material.WHITE_BANNER);
        BannerMeta meta = (BannerMeta) itemStack.getItemMeta();
        JsonObject metaJson = json.getAsJsonObject();
        JsonElement displaynameElement = metaJson.get("display-name");
        JsonElement loreElement = metaJson.get("lore");
        JsonElement enchants = metaJson.get("enchants");
        JsonElement flagsElement = metaJson.get("flags");
        if (displaynameElement != null && displaynameElement.isJsonPrimitive()) {
            meta.setDisplayName(displaynameElement.getAsString());
        }
        if (loreElement != null && loreElement.isJsonArray()) {
            JsonArray jarray = loreElement.getAsJsonArray();
            List<String> lore = new ArrayList<>(jarray.size());
            jarray.forEach(jsonElement -> {
                if (jsonElement.isJsonPrimitive()) lore.add(jsonElement.getAsString());
            });
            meta.setLore(lore);
        }
        if (enchants != null && enchants.isJsonArray()) {
            JsonArray jarray = enchants.getAsJsonArray();
            jarray.forEach(jsonElement -> {
                if (jsonElement.isJsonPrimitive()) {
                    String enchantString = jsonElement.getAsString();
                    if (enchantString.contains(":")) {
                        try {
                            String[] splitEnchant = enchantString.split(":");
                            Enchantment enchantment = Enchantment.getByName(splitEnchant[0]);
                            int level = Integer.parseInt(splitEnchant[1]);
                            if (enchantment != null && level > 0) {
                                meta.addEnchant(enchantment, level, true);
                            }
                        } catch (NumberFormatException ex) {
                        }
                    }
                }
            });
        }
        if (flagsElement != null && flagsElement.isJsonArray()) {
            JsonArray jarray = flagsElement.getAsJsonArray();
            jarray.forEach(jsonElement -> {
                if (jsonElement.isJsonPrimitive()) {
                    for (ItemFlag flag : ItemFlag.values()) {
                        if (flag.name().equalsIgnoreCase(jsonElement.getAsString())) {
                            meta.addItemFlags(flag);
                            break;
                        }
                    }
                }
            });
        }


        JsonElement baseColorElement = json.getAsJsonObject().get("base-color");
        JsonElement patternsElement = json.getAsJsonObject().get("patterns");
        BannerMeta bmeta = (BannerMeta) itemStack.getItemMeta();
        if (baseColorElement != null && baseColorElement.isJsonPrimitive()) {
            try {
                Optional<DyeColor> color = Arrays.stream(DyeColor.values())
                        .filter(dyeColor -> dyeColor.name().equalsIgnoreCase(baseColorElement.getAsString()))
                        .findFirst();
                if (color.isPresent()) {
                    bmeta.setBaseColor(color.get());
                }
            } catch (NumberFormatException ex) {
            }
        }
        if (patternsElement != null && patternsElement.isJsonArray()) {
            JsonArray jarray = patternsElement.getAsJsonArray();
            List<Pattern> patterns = new ArrayList<>(jarray.size());
            jarray.forEach(jsonElement -> {
                String patternString = jsonElement.getAsString();
                if (patternString.contains(":")) {
                    String[] splitPattern = patternString.split(":");
                    Optional<DyeColor> color = Arrays.stream(DyeColor.values())
                            .filter(dyeColor -> dyeColor.name().equalsIgnoreCase(splitPattern[0]))
                            .findFirst();
                    PatternType patternType = PatternType.getByIdentifier(splitPattern[1]);
                    if (color.isPresent() && patternType != null) {
                        patterns.add(new Pattern(color.get(), patternType));
                    }
                }
            });
            if (!patterns.isEmpty()) bmeta.setPatterns(patterns);
        }
        return bmeta;
    }

}

class ItemStackAdaptor implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {
    @Override
    public JsonElement serialize(ItemStack bannerMeta, Type typeOfSrc, JsonSerializationContext context) {
        return JsonItemStack.toJson(bannerMeta);
    }

    @Override
    public ItemStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        return JsonItemStack.fromJson(json.getAsJsonObject());
    }

}

final class InterfaceAdapter<T> implements JsonSerializer<T>, JsonDeserializer<T> {
    public JsonElement serialize(T object, Type interfaceType, JsonSerializationContext context) {
        final JsonObject wrapper = new JsonObject();
        wrapper.addProperty("type", object.getClass().getName());
        wrapper.add("data", context.serialize(object));
        return wrapper;
    }

    public T deserialize(JsonElement elem, Type interfaceType, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject wrapper = (JsonObject) elem;
        final JsonElement typeName = get(wrapper, "type");
        final JsonElement data = get(wrapper, "data");
        final Type actualType = typeForName(typeName);
        return context.deserialize(data, actualType);
    }

    private Type typeForName(final JsonElement typeElem) {
        try {
            return Class.forName(typeElem.getAsString());
        } catch (ClassNotFoundException e) {
            throw new JsonParseException(e);
        }
    }

    private JsonElement get(final JsonObject wrapper, String memberName) {
        final JsonElement elem = wrapper.get(memberName);
        if (elem == null)
            throw new JsonParseException("no '" + memberName + "' member found in what was expected to be an interface wrapper");
        return elem;
    }
}