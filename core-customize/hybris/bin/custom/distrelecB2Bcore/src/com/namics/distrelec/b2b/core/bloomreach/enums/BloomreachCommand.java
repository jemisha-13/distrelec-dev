package com.namics.distrelec.b2b.core.bloomreach.enums;

import java.util.HashMap;
import java.util.Map;

public enum BloomreachCommand {

    CUSTOMER_COMMAND("customers", "customer_change"),
    CONSENT_COMMAND("customers/events", "consent_change");

    private static final Map<String, BloomreachCommand> BY_COMMANDNAME = new HashMap<>();

    private static final Map<String, BloomreachCommand> BY_COMMANDID = new HashMap<>();

    static {
        for (BloomreachCommand e : values()) {
            BY_COMMANDNAME.put(e.commandName, e);
            BY_COMMANDID.put(e.commandId, e);
        }
    }

    public final String commandName;

    public final String commandId;

    private BloomreachCommand(String commandName, String commandId) {
        this.commandName = commandName;
        this.commandId = commandId;
    }

    public static BloomreachCommand valueOfCommandName(String name) {
        return BY_COMMANDNAME.get(name);
    }

    public static BloomreachCommand valueOfCommandId(String id) {
        return BY_COMMANDID.get(id);
    }

}
