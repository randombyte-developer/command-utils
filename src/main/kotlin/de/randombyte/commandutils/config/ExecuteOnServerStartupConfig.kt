package de.randombyte.commandutils.config

import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable

@ConfigSerializable
class ExecuteOnServerStartupConfig(
        @Setting val commands: List<String> = listOf("say Server started!")
)