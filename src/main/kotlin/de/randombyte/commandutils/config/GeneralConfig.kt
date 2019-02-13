package de.randombyte.commandutils.config

import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable

@ConfigSerializable
class GeneralConfig (
        @Setting("enable-metrics-messages", comment =
                "Since you are already editing configs, how about enabling metrics for at least this plugin? ;)\n" +
                "Go to the 'config/sponge/global.conf', scroll to the 'metrics' section and enable metrics.\n" +
                "Anonymous metrics data collection enables the developer to see how many people and servers are using this plugin.\n" +
                "Seeing that my plugin is being used is a big factor in motivating me to provide future support and updates.\n" +
                "If you really don't want to enable metrics and don't want to receive any messages anymore, you can disable this config option ;("
        ) val enableMetricsMessages: Boolean = true
)