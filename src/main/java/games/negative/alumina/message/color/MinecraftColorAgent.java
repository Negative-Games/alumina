package games.negative.alumina.message.color;

import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;

/**
 * Minecraft's default color agent.
 */
public class MinecraftColorAgent implements ColorAgent {
    @Override
    public String translate(@NotNull String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }
}
