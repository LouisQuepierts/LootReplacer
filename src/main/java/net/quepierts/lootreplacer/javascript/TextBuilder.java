package net.quepierts.lootreplacer.javascript;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;

import java.util.ArrayDeque;
import java.util.Deque;

public class TextBuilder {
    private final Deque<BaseComponent> queue;

    public TextBuilder(String text) {
        queue = new ArrayDeque<>();
        queue.add(new TextComponent(text));
    }

    public TextBuilder(Component component) {
        queue = new ArrayDeque<>();
        if (component instanceof BaseComponent baseComponent)
            queue.add(baseComponent);
    }

    public TextBuilder text(String str) {
        TextComponent component = new TextComponent(str);
        queue.add(component);
        return this;
    }

    public TextBuilder color(int rgb) {
        BaseComponent last = queue.getLast();
        last.setStyle(last.getStyle().withColor(TextColor.fromRgb(rgb)));
        return this;
    }

    public TextBuilder color(int red, int green, int blue) {
        BaseComponent last = queue.getLast();
        last.setStyle(last.getStyle().withColor(TextColor.fromRgb(red << 16 | green << 8 | blue)));
        return this;
    }

    public TextBuilder format(ChatFormatting... formatting) {
        queue.getLast().withStyle(formatting);
        return this;
    }

    public TextBuilder bold() {
        BaseComponent last = queue.getLast();
        last.setStyle(last.getStyle().withBold(true));
        return this;
    }

    public TextBuilder italic() {
        BaseComponent last = queue.getLast();
        last.setStyle(last.getStyle().withItalic(true));
        return this;
    }

    public TextBuilder underline() {
        BaseComponent last = queue.getLast();
        last.setStyle(last.getStyle().withUnderlined(true));
        return this;
    }

    public TextBuilder strikethrough() {
        BaseComponent last = queue.getLast();
        last.setStyle(last.getStyle().withStrikethrough(true));
        return this;
    }

    public TextBuilder obfuscated() {
        BaseComponent last = queue.getLast();
        last.setStyle(last.getStyle().withObfuscated(true));
        return this;
    }

    public BaseComponent build() {
        if (queue.size() == 1) {
            return queue.poll();
        } else {
            TextComponent base = new TextComponent("");
            while (!queue.isEmpty()) {
                base.append(queue.poll());
            }
            return base;
        }
    }
}
