package org.guardian.entries;

import java.util.List;
import org.bukkit.block.BlockState;
import org.guardian.ActionType;

/*
 * Used for plugins and worlds -md_5
 */
public class KeyEntry implements Entry {

    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getMessage() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ActionType getAction() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<BlockState> getRollbackBlockStates() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<BlockState> getRebuildBlockStates() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isRollbacked() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
