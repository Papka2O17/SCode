package papka2017.scode.File;

import org.bukkit.configuration.file.YamlConfiguration;
import papka2017.scode.SCode;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class DataManager {

    public final SCode plugin;
    private final File dataFile;
    private YamlConfiguration dataConfig;

    public DataManager(SCode plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "data.yml");
        if (!dataFile.exists()) {
            plugin.saveResource("data.yml", false);
        }
        this.dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    public int getPlayerCodeUsage(UUID playerUUID, String code) {
        return dataConfig.getInt(playerUUID + ".codes." + code, 0);
    }

    public void incrementPlayerCodeUsage(UUID playerUUID, String code) {
        int usage = getPlayerCodeUsage(playerUUID, code) + 1;
        dataConfig.set(playerUUID + ".codes." + code, usage);
        saveData();
    }

    public int getCodeActivations(String code) {
        return dataConfig.getInt("codes." + code + ".activations", 1);
    }

    public void incrementCodeActivations(String code) {
        int activations = getCodeActivations(code) + 1;
        dataConfig.set("codes." + code + ".activations", activations);
        saveData();
    }

    private void saveData() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void reload() {
        this.dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }
}
