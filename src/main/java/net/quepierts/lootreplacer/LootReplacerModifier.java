package net.quepierts.lootreplacer;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.quepierts.lootreplacer.javascript.ReplacerManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LootReplacerModifier implements IGlobalLootModifier {
    protected LootReplacerModifier() {
    }


    @NotNull
    @Override
    public List<ItemStack> apply(List<ItemStack> generatedLoot, LootContext context) {
        for (ItemStack pStack : generatedLoot) {
            ReplacerManager.applyReplacer(pStack);
        }
        return generatedLoot;
    }

    public static class Serializer extends GlobalLootModifierSerializer<LootReplacerModifier> {
        @Override
        public LootReplacerModifier read(ResourceLocation location, JsonObject object, LootItemCondition[] ailootcondition) {
            return new LootReplacerModifier();
        }

        @Override
        public JsonObject write(LootReplacerModifier instance) {
            return new JsonObject();
        }
    }
}
