package de.randombyte.commandutils

interface ConfigAccessor {
    fun get(): Config
    fun save(newConfig: Config)
}