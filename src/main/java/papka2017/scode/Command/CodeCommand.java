package papka2017.scode.Command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import papka2017.scode.SCode;
import papka2017.scode.File.ConfigManager;
import papka2017.scode.File.DataManager;
import papka2017.scode.Utils.Hex;

public class CodeCommand implements CommandExecutor {
    private final SCode plugin;
    private final ConfigManager config;
    private final DataManager data;

    public CodeCommand(SCode plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
        this.data = plugin.getDataManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Hex.color(config.getMessage("only_player")));
            return true;
        }

        Player player = (Player) sender;

        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (player.hasPermission("scode.reload")) {
                plugin.reloadConfig();
                config.reload();
                data.reload();
                player.sendMessage(Hex.color(config.getMessage("reload_success")));
            } else {
                player.sendMessage(Hex.color(config.getMessage("no_permission")));
            }
            return true;
        }

        String code;
        boolean isPromo = !command.getName().equalsIgnoreCase("code");

        if (isPromo) {
            code = command.getName();
        } else {
            if (args.length == 0) {
                player.sendMessage(Hex.color(config.getMessage("usage")));
                return true;
            }
            code = args[0];
        }

        String codeType = config.getCodeType(code);
        if ((isPromo && !codeType.equalsIgnoreCase("promo")) ||
                (!isPromo && !codeType.equalsIgnoreCase("code"))) {
            player.sendMessage(Hex.color(config.getMessage("wrong_code_type")));
            return true;
        }

        if (isPromo && !config.isPromoRegistered(code)) {
            player.sendMessage(Hex.color(config.getMessage("promo_not_registered")));
            return true;
        }

        if (!config.isCodeEnabled(code)) {
            player.sendMessage(Hex.color(config.getMessage("code_not_found")));
            return true;
        }

        if (data.getPlayerCodeUsage(player.getUniqueId(), code) >= config.getMaxUsage(code)) {
            player.sendMessage(Hex.color(config.getMessage("code_max_usage")));
            return true;
        }

        if (data.getCodeActivations(code) >= config.getActivations(code)) {
            player.sendMessage(Hex.color(config.getMessage("code_max_activations")));
            return true;
        }

        config.executeCommands(code, player);
        data.incrementPlayerCodeUsage(player.getUniqueId(), code);
        data.incrementCodeActivations(code);
        return true;
    }
}