package mc.reflexed.user.data;

import lombok.Getter;

@Getter
public enum UserRank {
    ADMIN("§c[ADMIN]", 4),
    MODERATOR("§d[MODERATOR]", 3),
    DONATOR("§9[DONATOR]", 2),
    DEFAULT("§7[Default]", 1);

    UserRank(String prefix, int level) {
        this.prefix = prefix;
        this.level = level;
    }

    final String prefix;
    final int level;

    public static UserRank forName(String name) {
        try {
            return UserRank.valueOf(name.toUpperCase());
        } catch (Exception e) {
            return UserRank.DEFAULT;
        }
    }
}
