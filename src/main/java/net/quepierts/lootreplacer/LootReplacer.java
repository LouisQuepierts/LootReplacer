package net.quepierts.lootreplacer;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.quepierts.lootreplacer.javascript.ReplacerManager;
import org.slf4j.Logger;

@Mod(LootReplacer.MODID)
public class LootReplacer {
    public static final String MODID = "lootreplacer";
    public static final DeferredRegister<GlobalLootModifierSerializer<?>> GLM =
            DeferredRegister.create(ForgeRegistries.Keys.LOOT_MODIFIER_SERIALIZERS, MODID);

    public static final RegistryObject<LootReplacerModifier.Serializer> REPLACER =
            GLM.register("replacer", LootReplacerModifier.Serializer::new);

    public static final Logger LOGGER = LogUtils.getLogger();

    public LootReplacer() {
        GLM.register(FMLJavaModLoadingContext.get().getModEventBus());
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onLoadConfig(AddReloadListenerEvent event) {
        ReplacerManager.reload();
    }
}
