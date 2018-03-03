package de.randombyte.commandutils.alias

import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable
import java.time.Duration

@ConfigSerializable
class AliasConfig(
        @Setting val aliases: Map<String, Alias> = emptyMap()
) {
    @ConfigSerializable
    class Alias(
            @Setting val permission: String = "",
            @Setting val commands: List<String> = emptyList(),
            @Setting val cooldown: Duration? = null
    )

    constructor() : this(
            aliases = mapOf(
                    "example alias command {0}" to Alias(
                            permission = "example.permission",
                            commands = listOf(
                                    "*say Executed alias with argument {0}",
                                    "*give \$p minecraft:cookie 3"
                            ),
                            cooldown = null
                    )
            )
    )
}