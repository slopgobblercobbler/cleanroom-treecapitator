package com.cleanroom.treecapitator;

import java.util.List;

public record TreeFellResult(
        boolean success,
        int felledLogs,
        int decayedLeaves,
        int damageDealt,
        double exhaustionAdded,
        String reason,
        List<String> gatheredDrops
) {
    public static TreeFellResult failure(String reason, int felledLogs, int damageDealt, double exhaustion, List<String> drops) {
        return new TreeFellResult(false, felledLogs, 0, damageDealt, exhaustion, reason, drops);
    }

    public static TreeFellResult singleLog(String reason, double exhaustion, List<String> drops) {
        return new TreeFellResult(true, 1, 0, 1, exhaustion, reason, drops);
    }
}
