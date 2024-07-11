package net.quepierts.lootreplacer.javascript;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;
import net.quepierts.lootreplacer.LootReplacer;
import org.openjdk.nashorn.api.scripting.NashornScriptEngine;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;

import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReplacerManager {
    private static final Path SCRIPT_DIR = Path.of("scripts/loot_replacer");
    private static final Path EXAMPLE_PATH = Path.of("scripts/loot_replacer/example.js");
    private static final Map<Item, JSFunction> REPLACER_MAP = new HashMap<>();
    private static final Pattern PATTERN_FUNCTION = Pattern.compile("function\\s+([a-zA-Z0-9]+\\$[a-zA-Z0-9_]+)\\s*\\(replacer\\)");
    private static final NashornScriptEngineFactory FACTORY = new NashornScriptEngineFactory();

    private static final String EXAMPLE = """
            function minecraft$cobblestone(replacer) {
                var text = utils.text("Hello").bold()
                    .text(" World");
                replacer.name(text);
            }
                        
            function minecraft$sandstone(replacer) {
                replacer
                    .name("§1R§2e§3d§4s§5t§6o§7n§8e §9C§ar§be§ca§dt§ei§fo§1n§2s")
                    .lore([
                        utils.text("text").color(colors.GREEN).bold(),
                        utils.text("lore")
                    ])
                    .enchant("minecraft:sharpness", 2)
                    .setBool("Unbreakable", true);
            }
            """;

    private static NashornScriptEngine engine;
    private static Invocable invoker;

    private static boolean prepareEngine() {
        LootReplacer.LOGGER.info("Preparing Script Engine...");
        ScriptEngine temp = FACTORY.getScriptEngine();

        if (temp instanceof NashornScriptEngine nashornScriptEngine) {
            engine = nashornScriptEngine;
        } else {
            LootReplacer.LOGGER.warn("Errors Occur During Preparing Script Engine!");
            return false;
        }

        engine.put("ItemStackReplacer", ItemStackReplacer.class);
        engine.put("ChatFormatting", ChatFormatting.class);
        engine.put("TextBuilder", TextBuilder.class);
        engine.put("AttributeModifierOperation", AttributeModifier.Operation.class);
        engine.put("EquipmentSlot", EquipmentSlot.class);
        engine.put("Attributes", Attributes.class);
        engine.put("NBT", CompoundTag.class);
        engine.put("colors", new Color());
        engine.put("utils", new ReplacerUtils());

        invoker = engine;
        return true;
    }

    public static void reload() {
        if (!prepareEngine()) {
            return;
        }

        REPLACER_MAP.clear();

        LootReplacer.LOGGER.info("Loading Scripts From Directory...");
        if (!Files.exists(SCRIPT_DIR)) {
            createExampleScript();
            return;
        }

        Set<String> set = new HashSet<>();
        File dir = SCRIPT_DIR.toFile();
        File[] files = dir.listFiles((dir1, name) -> name.endsWith(".js"));

        if (files == null)
            return;

        for (File file : files) {
            LootReplacer.LOGGER.info("Loading File: {}", file.getPath());

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
                String line;
                StringBuilder builder = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    builder.append(line).append("\n");
                    String replacer = getReplacer(line);
                    if (replacer != null)
                        set.add(replacer);
                }

                CompiledScript compiled = engine.compile(new StringReader(builder.toString()));
                compiled.eval();

                for (String replacer : set) {
                    ResourceLocation location = new ResourceLocation(replacer.replace('$', ':'));
                    Item item = ForgeRegistries.ITEMS.getValue(location);

                    LootReplacer.LOGGER.info("Loaded replacer: {} for item {}", replacer, location);

                    if (item != Items.AIR) {
                        JSFunction function = new JSFunction(replacer, compiled);

                        if (checkFunction(function)) {
                            REPLACER_MAP.put(item, function);
                        }
                    }
                }
            } catch (IOException | ScriptException e) {
                LootReplacer.LOGGER.warn(e.getMessage());
            }

            set.clear();
        }
    }

    public static void applyReplacer(ItemStack pStack) {
        if (invoker == null) {
            LootReplacer.LOGGER.warn("Unprepared Script Engine");
            return;
        }
        Item item = pStack.getItem();
        JSFunction function = REPLACER_MAP.get(item);

        if (function != null) {
            try {
                function.invoke(new ItemStackReplacer(pStack));
            } catch (Exception e) {
                LootReplacer.LOGGER.warn(e.getMessage());
            }
        }
    }

    private static boolean checkFunction(JSFunction function) {
        try {
            function.invoke(new ItemStackReplacer(new ItemStack(Items.AIR)));
            return true;
        } catch (Exception e) {
            LootReplacer.LOGGER.warn("Replacer {}: {}", function.name, e.getMessage());
            return false;
        }
    }

    private static String getReplacer(String line) {
        Matcher matcher = PATTERN_FUNCTION.matcher(line);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }

    private static void createExampleScript() {
        try {
            Files.createDirectories(SCRIPT_DIR);

            BufferedWriter writer = new BufferedWriter(new FileWriter(EXAMPLE_PATH.toFile(), StandardCharsets.UTF_8));
            writer.write(EXAMPLE);
            writer.close();

        } catch (IOException e) {
            LootReplacer.LOGGER.warn(e.getMessage());
        }
    }

    private record JSFunction(String name, CompiledScript script) {
        public void invoke(Object... objects) throws ScriptException, NoSuchMethodException {
            invoker.invokeFunction(name, objects);
        }
    }
}
