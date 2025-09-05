---
description: Guide to setting up and configuring a SQL database for storage
---

# SQL

The options under the `sql` section of the main `config.yml` file allow the storage of player data in a SQL database instead of the default YAML flatfile format. Currently only MySQL (and MariaDB) databases work. Using SQL for data storage enables better performance at high player counts, allows syncing of data between multiple servers, and makes it easier for third-party applications to interact with data.

## Basic Setup

Before configuring the AuraSkills config, a SQL database must already be created separately (either through the server panel or terminal). This database should have a host (usually an IP address), database name, port, username, and password.

The options for configuring SQL are under the `sql` section of `config.yml`. To enable SQL, set the `enabled` option to true. Enter the host address under `host` and the name of your database under `database`. Then, enter the username and password of the database under their corresponding options. Enter the port number of the database under `port`. If desired, SSL can be enabled under the `ssl` option.

Once the server is restarted, SQL should be successfully working. AuraSkills will automatically create tables in the database. If SQL is not working, check the server console for any errors on startup and double check your database credentials are correct.&#x20;

## Migrating Data From YAML

Enabling SQL alone will not migrate player data from the default YAML storage method. To migrate data, a few extra steps are required:

1. While still on YAML, create a backup of the player data using `/skills backup save`
2. Shut down the server
3. Follow the instructions in the Basic Setup section and switch to MySQL (make sure you restart the server after)
4. Run `/skills backup load [fileName]`, with fileName being the name of the backup file you saved before (seen in the backups folder)
