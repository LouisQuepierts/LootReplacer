package net.quepierts.lootreplacer.javascript;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;

import java.util.ArrayDeque;
import java.util.Deque;

public class TextBuilder {
    private final Deque<TextComponent> queue;

    public TextBuilder(String text) {
        queue = new ArrayDeque<>();
        queue.add(new TextComponent(text));
    }

    public TextBuilder text(String str) {
        TextComponent component = new TextComponent(str);
        queue.add(component);
        return this;
    }

    public TextBuilder color(int rgb) {
        TextComponent last = queue.getLast();
        last.setStyle(last.getStyle().withColor(TextColor.fromRgb(rgb)));
        return this;
    }

    public TextBuilder color(int red, int green, int blue) {
        TextComponent last = queue.getLast();
        last.setStyle(last.getStyle().withColor(TextColor.fromRgb(red << 16 | green << 8 | blue)));
        return this;
    }

    public TextBuilder format(ChatFormatting... formatting) {
        queue.getLast().withStyle(formatting);
        return this;
    }

    public TextBuilder bold() {
        TextComponent last = queue.getLast();
        last.setStyle(last.getStyle().withBold(true));
        return this;
    }

    public TextBuilder italic() {
        TextComponent last = queue.getLast();
        last.setStyle(last.getStyle().withItalic(true));
        return this;
    }

    public TextBuilder underline() {
        TextComponent last = queue.getLast();
        last.setStyle(last.getStyle().withUnderlined(true));
        return this;
    }

    public TextBuilder strikethrough() {
        TextComponent last = queue.getLast();
        last.setStyle(last.getStyle().withStrikethrough(true));
        return this;
    }

    public TextBuilder obfuscated() {
        TextComponent last = queue.getLast();
        last.setStyle(last.getStyle().withObfuscated(true));
        return this;
    }

    public TextComponent build() {
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
