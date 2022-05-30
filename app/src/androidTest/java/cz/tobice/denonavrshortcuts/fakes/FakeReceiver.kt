package cz.tobice.denonavrshortcuts.fakes

import cz.tobice.denonavrshortcuts.settings.enums.audio.AudysseyDynamicVolume
import cz.tobice.denonavrshortcuts.utils.booleanToOneZero
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

/**
 * A fake that uses [MockWebServer] to simulate behavior of an actual, real-life receiver.
 *
 * This allows us to build true end-to-end instrumentation tests without the brittleness of usual
 * mocking. The fake has its own state that can be arranged and asserted.
 */
class FakeReceiver {
    var centerSpread: Boolean? = null
    var dynamicVolume: AudysseyDynamicVolume? = null

    private val server = MockWebServer()

    fun startHttpServer() {
        server.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                return handleRequest(request)
            }
        }
        server.start(PORT)
    }

    fun stopHttpServer() {
        server.shutdown()
    }

    private fun handleRequest(request: RecordedRequest): MockResponse {
        return when (request.path) {
            // This is ugly but very much intentionally. Following the best testing practices, we
            // are avoiding unnecessary complexities and levels of indirection. This is a simple yet
            // descriptive way of defining how the receiver should handle different operations.
            "/ajax/audio/get_config?type=4" -> getSurroundSettingsConfig()
            "/ajax/audio/get_config?type=9" -> getAudysseySettingsConfig()
            "/ajax/audio/set_config?type=4&data=${encode("<SurroundParameter><CenterSpread>0</CenterSpread></SurroundParameter>")}" -> setSettingValue { centerSpread = false }
            "/ajax/audio/set_config?type=4&data=${encode("<SurroundParameter><CenterSpread>1</CenterSpread></SurroundParameter>")}" -> setSettingValue { centerSpread = true }
            "/ajax/audio/set_config?type=9&data=${encode("<DynamicVolume>1</DynamicVolume>")}" -> setSettingValue { dynamicVolume = AudysseyDynamicVolume.HEAVY }
            "/ajax/audio/set_config?type=9&data=${encode("<DynamicVolume>2</DynamicVolume>")}" -> setSettingValue { dynamicVolume = AudysseyDynamicVolume.MEDIUM }
            "/ajax/audio/set_config?type=9&data=${encode("<DynamicVolume>3</DynamicVolume>")}" -> setSettingValue { dynamicVolume = AudysseyDynamicVolume.LIGHT }
            "/ajax/audio/set_config?type=9&data=${encode("<DynamicVolume>4</DynamicVolume>")}" -> setSettingValue { dynamicVolume = AudysseyDynamicVolume.OFF }
            else -> MockResponse().setResponseCode(400)
        }
    }

    private fun getSurroundSettingsConfig() = asXmlResponse {
        buildString {
            append("<SurroundParameter>")

            if (centerSpread == null) {
                append("<CenterSpread display=\"1\"/>")
            } else {
                append("<CenterSpread display=\"3\">${booleanToOneZero(centerSpread!!)}</CenterSpread>")
            }

            append("</SurroundParameter>")
        }
    }

    private fun getAudysseySettingsConfig() = asXmlResponse {
        buildString {
            append("<Audyssey>")

            if (dynamicVolume == null) {
                append("<DynamicVolume display=\"1\"/>")
            } else {
                append("<DynamicVolume display=\"3\">${dynamicVolume!!.receiverValue}</DynamicVolume>")
            }

            append("</Audyssey>")
        }
    }

    private fun setSettingValue(setter: () -> Unit): MockResponse {
        setter()
        return createResponse().setResponseCode(200)
    }

    companion object {
        /** The port on which the server should run. */
        const val PORT = 8080

        /** A small delay in milliseconds for all responses to make the tests more realistic */
        private const val DEFAULT_DELAY_MS: Long = 100

        private fun asXmlResponse(makeXmlBody: () -> String): MockResponse {
            return createResponse()
                .setResponseCode(200)
                .setBody("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" + makeXmlBody())
        }

        private fun createResponse() =
            MockResponse().setBodyDelay(DEFAULT_DELAY_MS, TimeUnit.MILLISECONDS)

        private fun encode(urlParam: String): String {
            return URLEncoder.encode(urlParam, "UTF-8")
        }
    }
}
