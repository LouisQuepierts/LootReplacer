package net.quepierts.lootreplacer.javascript;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.ForgeRegistries;
import net.quepierts.lootreplacer.LootReplacer;

public class ItemStackReplacer {
    private ItemStack pStack;

    public ItemStackReplacer(ItemStack pStack) {
        this.pStack = pStack;
    }

    public ItemStackReplacer debug() {
        LootReplacer.LOGGER.info(pStack.toString());
        return this;
    }

    public ItemStackReplacer name(TextBuilder builder) {
        pStack.setHoverName(builder.build());
        return this;
    }

    public TextBuilder name() {
        return new TextBuilder(pStack.getHoverName());
    }

    public ItemStackReplacer lore(TextBuilder[] builders) {
        ListTag lore = new ListTag();
        for (TextBuilder builder : builders) {
            lore.add(StringTag.valueOf(Component.Serializer.toJson(builder.build())));
        }
        CompoundTag compoundtag = pStack.getOrCreateTagElement("display");
        compoundtag.put("Lore", lore);
        return this;
    }

    public ItemStackReplacer enchant(String enchantment, int level) {
        Enchantment pEnchantment = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(enchantment));

        if (pEnchantment != null) {
            pStack.enchant(pEnchantment, level);
        }

        return this;
    }

    public ItemStackReplacer attribute(String attribute, double amount, AttributeModifier.Operation operation, EquipmentSlot slot) {
        Attribute pAttribute = ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(attribute));
        if (pAttribute != null)
            pStack.addAttributeModifier(pAttribute, new AttributeModifier(attribute, amount, operation), slot);
        return this;
    }

    public ItemStackReplacer attribute(String attribute, double amount, AttributeModifier.Operation operation) {
        Attribute pAttribute = ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(attribute));
        if (pAttribute != null)
            pStack.addAttributeModifier(pAttribute, new AttributeModifier(attribute, amount, operation), null);
        return this;
    }

    public ItemStackReplacer setString(String key, String value) {
        pStack.getOrCreateTag().putString(key, value);
        return this;
    }

    public ItemStackReplacer setNBT(String key, CompoundTag tag) {
        pStack.getOrCreateTag().put(key, tag);
        return this;
    }

    public ItemStackReplacer setBool(String key, boolean b) {
        pStack.getOrCreateTag().putBoolean(key, b);
        return this;
    }

    public ItemStackReplacer setInt(String key, int i) {
        pStack.getOrCreateTag().putInt(key, i);
        return this;
    }

    public ItemStackReplacer setFloat(String key, float f) {
        pStack.getOrCreateTag().putFloat(key, f);
        return this;
    }

    public CompoundTag getNBT() {
        return pStack.getOrCreateTag();
    }

    public ItemStackReplacer amount(int count) {
        pStack.setCount(count);
        return this;
    }

    public int amount() {
        return pStack.getCount();
    }

    public ItemStackReplacer damage(int damage) {
        pStack.setDamageValue(damage);
        return this;
    }

    public int damage() {
        return pStack.getDamageValue();
    }
}
