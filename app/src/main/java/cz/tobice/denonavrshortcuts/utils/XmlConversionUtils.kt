package cz.tobice.denonavrshortcuts.utils

import org.simpleframework.xml.core.Persister
import org.simpleframework.xml.stream.Format
import java.io.ByteArrayOutputStream

/**
 * Converts a SimpleXML annotated class instance into XML.
 *
 * @return a valid, unformatted XML string
 */
fun toXml(source: Any): String {
    val serializer = Persister(Format(/* indent = */ 0))
    val outputStream = ByteArrayOutputStream()
    serializer.write(source, outputStream)

    // The serializer by default "pretties" the XML output. We remove the indentation by setting
    // indent to zero (see above) and then we have to manually get rid of line ends.
    return outputStream.toString("UTF-8").replace("\n", "")
}

fun oneZeroToBoolean(value: String?): Boolean? = when (value) {
    "1" -> true
    "0" -> false
    else -> null
}

fun booleanToOneZero(value: Boolean) = if (value) "1" else "0"
