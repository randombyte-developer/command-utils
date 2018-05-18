package de.randombyte.commandutils.execute

import ninja.leaping.configurate.objectmapping.ObjectMappingException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

object TimeStampUtils {
    private val DATE_FORMAT = SimpleDateFormat("HH:mm:ss.SSS-dd.MM.yyyy")

    fun deserialize(timeStampString: String): Date {
        try {
            return DATE_FORMAT.parse(timeStampString)
        } catch (exception: ParseException) {
            throw ObjectMappingException("Invalid input value '$timeStampString' for a timestamp like this: '00:36:25.300-19.05.2018'", exception)
        }
    }

    fun now() = Date.from(Instant.now())
}