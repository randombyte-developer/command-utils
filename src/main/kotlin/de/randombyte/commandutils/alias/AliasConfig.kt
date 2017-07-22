package de.randombyte.commandutils.alias

import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable

@ConfigSerializable
class AliasConfig(
        @Setting val aliases: Map<String, Alias> = emptyMap()
) {
    @ConfigSerializable
    class Alias(
            @Setting val permission: String = "",
            @Setting val commands: List<String> = emptyList()
    )

    constructor() : this(
            aliases = mapOf(
                    "example alias command {0}" to Alias(
                            permission = "example.permission",
                            commands = listOf(
                                    "*say Executed alias with argument {0}",
                                    "*give \$s minecraft:cookie 3"
                            )
                    )
            )
    )
}