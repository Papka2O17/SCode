package papka2017.scode;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import papka2017.scode.Command.CodeCommand;
import papka2017.scode.Events.CodeListener;
import papka2017.scode.File.ConfigManager;
import papka2017.scode.File.DataManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public final class SCode extends JavaPlugin {
    private ConfigManager configManager;
    private DataManager dataManager;
    private CodeListener codeListener;

    @Override
    public void onEnable() {
        try {
            configManager = new ConfigManager(this);
            dataManager = new DataManager(this);
            codeListener = new CodeListener(this);

            registerCommands();
            getServer().getPluginManager().registerEvents(codeListener, this);
            getLogger().info("SCode успешно запущен!");
        } catch (Exception e) {
            getLogger().severe("Ошибка при запуске: " + e.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    private void registerCommands() throws Exception {
        CommandMap commandMap = getCommandMap();

        PluginCommand codeCmd = getCommand("code");
        if (codeCmd != null) {
            codeCmd.setExecutor(new CodeCommand(this));
        }

        for (String code : getPromoCodes()) {
            PluginCommand cmd = createPluginCommand(code);
            cmd.setExecutor(new CodeCommand(this));
            commandMap.register(this.getName(), cmd);
        }
    }

    private CommandMap getCommandMap() throws Exception {
        Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
        commandMapField.setAccessible(true);
        return (CommandMap) commandMapField.get(Bukkit.getServer());
    }

    private PluginCommand createPluginCommand(String name) throws Exception {
        Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
        constructor.setAccessible(true);
        return constructor.newInstance(name, this);
    }

    private List<String> getPromoCodes() {
        List<String> promoCodes = new ArrayList<>();
        if (configManager.getConfig().contains("codes")) {
            for (String code : configManager.getConfig().getConfigurationSection("codes").getKeys(false)) {
                if ("promo".equalsIgnoreCase(configManager.getCodeType(code))
                        && configManager.isPromoRegistered(code)) {
                    promoCodes.add(code);
                }
            }
        }
        return promoCodes;
    }

    @Override
    public void onDisable() {
        getLogger().info("SCode отключен");
    }

    public CodeListener getCodeListener() {
        return codeListener;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public DataManager getDataManager() {
        return dataManager;
    }
}