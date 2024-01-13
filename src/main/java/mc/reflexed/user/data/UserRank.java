package mc.reflexed.user.data;

import lombok.Getter;

public enum UserRank {
    ADMIN("§c[ADMIN]"),
    MODERATOR("§d[MODERATOR]"),
    DONATOR("§9[DONATOR]"),
    DEFAULT("§7[Default]");

    UserRank(String prefix) {
        this.prefix = prefix;
    }

    @Getter
    final String prefix;
}
