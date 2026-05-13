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

    private val VPS_ENDPOINT = "http://209.145.48.25:8081/ROOT/Conversion"
    private val SESSION_TOKEN = "TU9OU1RFUjoxNzc4Njc3MDM0ODMy"

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    suspend fun convert(
        category: String,
        value: Double,
        fromUnit: String,
        toUnit: String
    ): Result<ConversionResponse> = withContext(Dispatchers.IO) {
        try {
            val methodName = "convert${category}"
            val envelope = buildSoapEnvelope(methodName, value, fromUnit, toUnit)
            val response = executeRequest(envelope)
            Result.success(parseSoapResponse(response))
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
            xmlns:con="http://ws.grupo3.edu.ec/">
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

    private fun executeRequest(soapEnvelope: String): String {
        val body = soapEnvelope.toRequestBody("text/xml; charset=UTF-8".toMediaType())

        val request = Request.Builder()
            .url(VPS_ENDPOINT)
            .post(body)
            .addHeader("Content-Type", "text/xml; charset=UTF-8")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("Error HTTP ${response.code}: ${response.message}")
            }
            return response.body?.string()
                ?: throw Exception("El VPS retornó un cuerpo de respuesta vacío.")
        }
    }

    private fun parseSoapResponse(xml: String): ConversionResponse {
        val factory = XmlPullParserFactory.newInstance().apply {
            isNamespaceAware = true
        }
        val xpp = factory.newPullParser()
        xpp.setInput(StringReader(xml))

        var currentTag = ""
        var category = ""
        var fromUnit = ""
        var toUnit = ""
        var inputValue = 0.0
        var resultValue = 0.0
        var message = ""

        var eventType = xpp.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    currentTag = xpp.name ?: ""
                }
                XmlPullParser.TEXT -> {
                    val text = xpp.text?.trim() ?: ""
                    if (text.isNotEmpty()) {
                        when (currentTag) {
                            "category" -> category = text
                            "fromUnit" -> fromUnit = text
                            "toUnit" -> toUnit = text
                            "inputValue" -> inputValue = text.toDoubleOrNull() ?: 0.0
                            "resultValue" -> resultValue = text.toDoubleOrNull() ?: 0.0
                            "message" -> message = text
                        }
                    }
                }
                XmlPullParser.END_TAG -> {
                    currentTag = ""
                }
            }
            eventType = xpp.next()
        }

        if (resultValue == 0.0 && message.isNotEmpty() && message != "OK") {
            throw Exception("El VPS reportó un error: $message")
        }

        return ConversionResponse(
            category = category,
            fromUnit = fromUnit,
            toUnit = toUnit,
            inputValue = inputValue,
            resultValue = resultValue,
            message = message
        )
    }
}