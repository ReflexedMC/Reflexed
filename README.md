# Reflexed Minecraft Plugin

Reflexed is a Minecraft plugin that provides a variety of features to enhance the gameplay experience.

## Features

- Combat Tagging: Prevents players from escaping combat through certain commands or teleportation. See [`CombatTag.java`](src/main/java/mc/reflexed/combat/CombatTag.java).
- Commands: A variety of commands for players and administrators. See the [`commands`](src/main/java/mc/reflexed/command/commands) directory.
- Game Maps: Manage game maps and related data. See [`GameMap.java`](src/main/java/mc/reflexed/map/GameMap.java) and [`MapDatabase.java`](src/main/java/mc/reflexed/map/MapDatabase.java).
- User Management: Manage user data and sidebars. See [`User.java`](/src/main/java/mc/reflexed/user/User.java), [`UserDatabase.java`](src/main/java/mc/reflexed/user/UserDatabase.java), and [`UserSidebar.java`](src/main/java/mc/reflexed/user/UserSidebar.java).
- Utilities: Various utility classes for chat, math, and other functions. See the [`util`](src/main/java/mc/reflexed/util) directory.

## Installation

1. Build the plugin using a Maven clean install.
2. Place the built .jar file into your server's `plugins` directory.
3. Restart your server.