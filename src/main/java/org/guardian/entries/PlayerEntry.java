package org.guardian.entries;

import java.util.List;
import org.bukkit.block.BlockState;
import org.guardian.ActionType;

public class PlayerEntry implements Entry {

    private int playerId;
    private String name, ip;
    private long firstLogin, lastLogin, onlineTime;

    public int getId() {
        return playerId;
    }

    public void setId(int id) {
        playerId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public long getFirstLogin() {
        return firstLogin;
    }

    public void setFirstLogin(long firstLogin) {
        this.firstLogin = firstLogin;
    }

    public long getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(long lastLogin) {
        this.lastLogin = lastLogin;
    }

    public long getOnlineTime() {
        return onlineTime;
    }

    public void setOnlineTime(long onlineTime) {
        this.onlineTime = onlineTime;
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
