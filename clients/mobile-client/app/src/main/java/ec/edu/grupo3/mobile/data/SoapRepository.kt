package ec.edu.grupo3.mobile.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader
import java.util.concurrent.TimeUnit

class SoapRepository {

    companion object {
        private const val BASE_URL = "http://209.145.48.25:8081/ROOT/Conversion"

        private const val SESSION_TOKEN = "TU9OU1RFUjoxNzc4Njc3MDM0ODMy"

        private const val SOAP_NS = "http://ws.grupo3.edu.ec/"

        private const val CONNECT_TIMEOUT_SEC = 15L
        private const val READ_TIMEOUT_SEC    = 30L
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(CONNECT_TIMEOUT_SEC, TimeUnit.SECONDS)
        .readTimeout(READ_TIMEOUT_SEC, TimeUnit.SECONDS)
        .build()

    suspend fun convert(
        category: String,
        value: Double,
        fromUnit: String,
        toUnit: String
    ): Result<ConversionResponse> = withContext(Dispatchers.IO) {
        try {
            val methodName = "convert${category.replaceFirstChar { it.uppercase() }}"

            val soapEnvelope = buildSoapEnvelope(methodName, value, fromUnit, toUnit)

            val requestBody = soapEnvelope
                .toRequestBody("text/xml; charset=UTF-8".toMediaType())

            val request = Request.Builder()
                .url(BASE_URL)
                .post(requestBody)
                .addHeader("Content-Type", "text/xml; charset=UTF-8")
                .build()

            client.newCall(request).execute().use { response ->
                val bodyStr = response.body?.string()
                    ?: throw Exception("Cuerpo de respuesta vacío del servidor.")

                if (!response.isSuccessful) {
                    throw Exception("Error del servidor [${response.code}]: $bodyStr")
                }

                Result.success(parseSoapResponse(bodyStr))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun buildSoapEnvelope(
        methodName: String,
        value: Double,
        fromUnit: String,
        toUnit: String
    ): String = """
        <?xml version="1.0" encoding="UTF-8"?>
        <soap:Envelope
            xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
            xmlns:con="$SOAP_NS">
          <soap:Body>
            <con:$methodName>
              <token>$SESSION_TOKEN</token>
              <value>$value</value>
              <fromUnit>$fromUnit</fromUnit>
              <toUnit>$toUnit</toUnit>
            </con:$methodName>
          </soap:Body>
        </soap:Envelope>
    """.trimIndent()

    private fun parseSoapResponse(xml: String): ConversionResponse {
        val factory = XmlPullParserFactory.newInstance().apply {
            isNamespaceAware = true
        }
        val xpp = factory.newPullParser()
        xpp.setInput(StringReader(xml))

        var currentTag = ""
        var category   = ""
        var fromUnit   = ""
        var toUnit     = ""
        var inputVal   = 0.0
        var resultVal  = 0.0
        var message    = ""

        var eventType = xpp.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> currentTag = xpp.name ?: ""
                XmlPullParser.TEXT -> {
                    val text = xpp.text.trim()
                    if (text.isNotEmpty()) {
                        when (currentTag) {
                            "category"   -> category  = text
                            "fromUnit"   -> fromUnit   = text
                            "toUnit"     -> toUnit     = text
                            "inputValue" -> inputVal   = text.toDoubleOrNull() ?: 0.0
                            "resultValue"-> resultVal  = text.toDoubleOrNull() ?: 0.0
                            "message"    -> message    = text
                        }
                    }
                }
                XmlPullParser.END_TAG -> currentTag = ""
            }
            eventType = xpp.next()
        }

        if (category.isEmpty() && resultVal == 0.0) {
            throw Exception("Respuesta SOAP inesperada del servidor. Verifique el token o el endpoint.")
        }

        return ConversionResponse(category, fromUnit, toUnit, inputVal, resultVal, message)
    }
}