package de.randombyte.commandutils

import de.randombyte.kosp.extensions.*

object Messages {
    val motivationalSpeech = listOf(
            "[${CommandUtils.NAME}] ".yellow() + "Metrics are disabled for this plugin or globally! Please consider enabling metrics.".aqua(),
            "Metrics are anonymous usage data (how many players are on the server, which minecraft version the server is on, etc.)".green(),
            "With that data the developer can check how many servers use the plugin. Plugins with many users motivate me more to release new updates. :)".gold(),
            "To disable this message, go to the Sponge global config, and enable metrics collection for at least this plugin, thanks! ;)".lightPurple())
}