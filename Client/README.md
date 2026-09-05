# Client

Use a legally obtained Minecraft 1.21.11 client installation and connect to
`127.0.0.1:25565`. The test server is intentionally configured with
`online-mode=false`; do not expose it to the public internet.

Start the server from the repository root with
`EULA_ACCEPTED=true ./Server/scripts/start.sh`, then launch the client from a
terminal with `MINECRAFT_CLIENT_DIR=/path/to/.minecraft ./Client/scripts/launch.sh`.
The script directly executes the locally installed client JAR in offline mode;
it does not use Prism Launcher. When finished, run `./Server/scripts/stop.sh`.
