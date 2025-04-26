package papka2017.scode.File;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import papka2017.scode.SCode;

import java.util.List;

public class ConfigManager {
    private final SCode plugin;
    private FileConfiguration config;

    public ConfigManager(SCode plugin) {
        this.plugin = plugin;
        this.plugin.saveDefaultConfig();
        this.config = plugin.getConfig();
    }

    public boolean isCodeEnabled(String code) {
        return config.getBoolean("codes." + code + ".enable", false);
    }

    public String getMessage(String key) {
        String prefix = getPrefix();
        return prefix + config.getString("messages." + key, "Сообщение не найдено: " + key);
    }

    public int getActivations(String code) {
        return config.getInt("codes." + code + ".activations", 0);
    }

    public String getPrefix() {
        return config.getString("prefix", "");
    }

    public int getMaxUsage(String code) {
        return config.getInt("codes." + code + ".maxusage", 1);
    }

    public String getCodeType(String code) {
        return config.getString("codes." + code + ".type", "code");
    }

    public boolean isPromoRegistered(String code) {
        return config.getBoolean("codes." + code + ".register", true);
    }

    public void reload() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }

    public List<String> getListeners(String code) {
        return config.getStringList("codes." + code + ".listener");
    }

    public void executeCommands(String code, Player player) {
        List<String> consoleCommands = config.getStringList("codes." + code + ".commands.Console");
        for (String command : consoleCommands) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("{user}", player.getName()));
        }

        List<String> playerCommands = config.getStringList("codes." + code + ".commands.Player");
        for (String command : playerCommands) {
            player.performCommand(command);
        }

        plugin.getCodeListener().triggerListeners(player, code);
    }

    public FileConfiguration getConfig() {
        return this.config;
    }
}