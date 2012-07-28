package org.guardian.params;

import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.guardian.ActionType;
import org.guardian.Guardian;
import org.guardian.util.Utils;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class QueryParamsFactory {

    private final Guardian plugin = Guardian.getInstance();
    private final ParamsParser parser;

    public QueryParamsFactory() {
        parser = new LogBlockParamsParser(); // TODO This has to be selectable
        // in config
    }

    public QueryParams create(CommandSender sender, List<String> args) throws IllegalArgumentException {
        final Map<Param, List<String>> paramsMap = parser.parse(args);
        final QueryParams params = new QueryParams();
        for (final Entry<Param, List<String>> e : paramsMap.entrySet()) {
            final List<String> paramArgs = e.getValue();
            switch (e.getKey()) {
                case ACTION:
                    for (final String action : paramArgs) {
                        try {
                            params.actions.add(ActionType.valueOf(action.toUpperCase()));
                        } catch (final IllegalArgumentException ex) {
                            throw new IllegalArgumentException("There is no action '" + action + "'");
                        }
                    }
                    break;
                case PLAYER:
                    for (String player : paramArgs) {
                        params.players.add(player);
                    }
                    break;
                case AREA:
                    if (sender instanceof Player) {
                        params.loc = ((Player) sender).getLocation();
                        params.sel = null;
                        if (paramArgs.isEmpty()) {
                            params.radius = 20; // TODO Get default value from
                        } // config
                        else if (Utils.isInt(paramArgs.get(0))) {
                            params.radius = Integer.valueOf(paramArgs.get(0));
                        } else {
                            throw new IllegalArgumentException("Not a number '" + paramArgs.get(0) + "'");
                        }
                    } else {
                        throw new IllegalArgumentException("You must be a player to use area");
                    }
                    break;
                case SELECTION:
                    if (sender instanceof Player) {
                        if (!plugin.getServer().getPluginManager().isPluginEnabled("WorldEdit")) {
                            throw new IllegalArgumentException("WorldEdit is not loaded");
                        }
                        final Selection sel = plugin.getWorldEdit().getSelection((Player) sender);
                        if (sel != null && sel instanceof CuboidSelection) {
                            params.sel = sel;
                            params.loc = null;
                            params.radius = -1;
                        } else {
                            throw new IllegalArgumentException("No selection defined ");
                        }
                    } else {
                        throw new IllegalArgumentException("You must be a player to use area");
                    }
                    break;
                case BLOCK:
                    for (final String block : paramArgs) {
                        final Material mat = Material.matchMaterial(block);
                        if (mat != null) {
                            params.blocks.add(mat.getId());
                        } else {
                            throw new IllegalArgumentException("There is no material '" + block + "'");
                        }
                    }
                    break;
                case WORLD:
                    for (final String worldName : paramArgs) {
                        final World world = Bukkit.getServer().getWorld(worldName);
                        if (world != null) {
                            params.worlds.add(world);
                        } else {
                            throw new IllegalArgumentException("There is no world '" + worldName + "'");
                        }
                    }
                    break;
                case SINCE:
                    final String since = Utils.join(paramArgs, " ");
                    final int minutessince = Utils.parseTimeSpec(since);
                    if (minutessince > 0) {
                        params.since = minutessince;
                    } else {
                        throw new IllegalArgumentException("Not a valid time spec '" + since + "'");
                    }
                    break;
                case BEFORE:
                    final String before = Utils.join(paramArgs, " ");
                    final int minutesbefore = Utils.parseTimeSpec(before);
                    if (minutesbefore > 0) {
                        params.before = minutesbefore;
                    } else {
                        throw new IllegalArgumentException("Not a valid time spec '" + before + "'");
                    }
                    break;
                case SUM:
                    try {
                        params.sum = SummarizationMode.valueOf(paramArgs.get(0).toUpperCase());
                    } catch (final IllegalArgumentException ex) {
                        throw new IllegalArgumentException("There is no summarization mode '" + paramArgs.get(0) + "'");
                    }
                    break;
                case LIMIT:
                    if (Utils.isInt(paramArgs.get(0))) {
                        params.limit = Integer.valueOf(paramArgs.get(0));
                    } else {
                        throw new IllegalArgumentException("Not a number '" + paramArgs.get(0) + "'");
                    }
                    break;
                case SILENT:
                    params.silent = true;
                    break;
                case COORDS:
                    params.needCoords = true;
                    break;
                case MATCH:
                    params.textMatch = Utils.join(paramArgs, " ");
                    break;
                case ASC:
                    params.order = Order.ASC;
                    break;
                case DESC:
                    params.order = Order.DESC;
                    break;
                case LAST:
                    // TODO Get last query params from session and use a close of
                    // it.
                    break;
                case DESTROYED:
                    params.actions.clear();
                    params.actions.add(ActionType.BLOCK_BREAK);
                    break;
                case CREATED:
                    params.actions.clear();
                    params.actions.add(ActionType.BLOCK_PLACE);
                    break;
            }
        }
        if (params.worlds.isEmpty()) {
            params.worlds = Bukkit.getServer().getWorlds();
        }
        return params;
    }

    public String parse(QueryParams params) {
        StringBuilder out = new StringBuilder();
        if (!params.actions.isEmpty()) {
            out.append("ACTION ");
            for (ActionType a : params.actions) {
                out.append(a.name()).append(" ");
            }
        }
        if (!params.actions.isEmpty()) {
            out.append("PLAYER ");
            for (String s : params.players) {
                out.append(s).append(" ");
            }
        }
        if (!params.blocks.isEmpty()) {
            out.append("BLOCK ");
            for (int b : params.blocks) {
                out.append(b).append(" ");
            }
        }
        if (!params.worlds.isEmpty()) {
            out.append("WORLD ");
            for (World w : params.worlds) {
                out.append(w.getName()).append(" ");
            }
        }
        if (params.since != 0) {
            out.append("SINCE ");
            out.append(params.since).append(" ");
        }
        if (params.before != 0) {
            out.append("BEFORE ");
            out.append(params.before).append(" ");
        }
        return out.toString();
    }
}
