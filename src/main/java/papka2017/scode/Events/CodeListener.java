package papka2017.scode.Events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import papka2017.scode.File.DataManager;
import papka2017.scode.SCode;
import papka2017.scode.Utils.Hex;

import java.util.List;

public class CodeListener implements Listener {
    private final SCode plugin;
    private final DataManager data;

    public CodeListener(SCode plugin) {
        this.plugin = plugin;
        this.data = plugin.getDataManager();
    }

    public void triggerListeners(Player player, String code) {
        List<String> listeners = plugin.getConfigManager().getListeners(code);
        String title = "";
        String subtitle = "";

        for (String listener : listeners) {
            String message = listener.replace("{user}", player.getName())
                    .replace("{usage}", String.valueOf(data.getCodeActivations(code)));

            if (listener.startsWith("[bc]")) {
                Bukkit.broadcastMessage(Hex.color(message.substring(4)));
            }
            else if (listener.startsWith("[chat]")) {
                player.sendMessage(Hex.color(message.substring(6)));
            }
            else if (listener.startsWith("[title]")) {
                title = message.substring(7);
            }
            else if (listener.startsWith("[subtitle]")) {
                subtitle = message.substring(10);
            }
            else if (listener.startsWith("[alltitle]")) {
                String allTitle = message.substring(10);
                Bukkit.getOnlinePlayers().forEach(p ->
                        p.sendTitle(Hex.color(allTitle), "", 20, 60, 20));
            }
            else if (listener.startsWith("[allsubtitle]")) {
                String allSubtitle = message.substring(14);
                Bukkit.getOnlinePlayers().forEach(p ->
                        p.sendTitle("", Hex.color(allSubtitle), 20, 60, 20));
            }
        }

        if (!title.isEmpty() || !subtitle.isEmpty()) {
            player.sendTitle(
                    Hex.color(title),
                    Hex.color(subtitle),
                    20,
                    60,
                    20
            );
        }
    }
}