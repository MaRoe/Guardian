package org.guardian.params;

import com.sk89q.worldedit.bukkit.selections.Selection;
import org.bukkit.Location;
import org.bukkit.World;
import org.guardian.ActionType;

import java.util.ArrayList;
import java.util.List;

public class QueryParams implements Cloneable {

    public List<ActionType> actions = new ArrayList<ActionType>();
    public long before, since;
    public List<Integer> blocks = new ArrayList<Integer>();
    public boolean excludePlayersMode = false;
    public int limit = -1;
    public Location loc = null;
    public Order order = Order.DESC;
    public List<String> players = new ArrayList<String>();
    public int radius = 0;
    public Selection sel = null;
    public boolean silent = false;
    public SummarizationMode sum = SummarizationMode.NONE;
    public String textMatch;
    public List<World> worlds = new ArrayList<World>();
    public boolean needId = false, needDate = false, needPlayer = false, needCoords = false, needSubTableText = false,
            needSubTableItem = false, needSubTableBlock = false, needSubTableDeath = false;

    /**
     * Constructs a new QueryParams with default values
     */
    public QueryParams() {
        // TODO Set default values
    }

    @Override
    public QueryParams clone() throws CloneNotSupportedException {
        return this;
    }
}
