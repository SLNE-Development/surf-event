package dev.slne.surf.event.anarchy.config.serializer

import org.spongepowered.configurate.serialize.ScalarSerializer
import org.spongepowered.configurate.serialize.SerializationException
import java.lang.reflect.AnnotatedType
import java.time.ZonedDateTime
import java.time.format.DateTimeParseException
import java.util.function.Predicate

object ZonedDateTimeSerializer :
    ScalarSerializer.Annotated<ZonedDateTime>(ZonedDateTime::class.java) {

    override fun deserialize(type: AnnotatedType, obj: Any): ZonedDateTime {
        return try {
            ZonedDateTime.parse(obj.toString())
        } catch (e: DateTimeParseException) {
            throw SerializationException(
                ZonedDateTime::class.java,
                "$obj($type) is not a valid ZonedDateTime",
                e
            )
        }
    }

    override fun serialize(
        type: AnnotatedType,
        item: ZonedDateTime,
        typeSupported: Predicate<Class<*>>
    ): Any {
        return item.toString()
    }
}
