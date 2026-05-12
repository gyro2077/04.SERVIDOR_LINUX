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

class SoapRepository {
    private val client = OkHttpClient()
    private val baseUrl = "http://192.168.100.171:8080/ConversionService.svc"

    suspend fun convert(category: String, value: Double, fromUnit: String, toUnit: String): Result<ConversionResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val methodName = "convert$category"

                val soapEnvelope = """
                    <?xml version="1.0" encoding="utf-8"?>
                    <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                      <soap:Body>
                        <$methodName xmlns="http://ws.grupo3.edu.ec/">
                          <value>$value</value>
                          <fromUnit>$fromUnit</fromUnit>
                          <toUnit>$toUnit</toUnit>
                        </$methodName>
                      </soap:Body>
                    </soap:Envelope>
                """.trimIndent()

                val requestBody = soapEnvelope.toRequestBody("text/xml; charset=utf-8".toMediaType())

                val request = Request.Builder()
                    .url(baseUrl)
                    .post(requestBody)
                    .addHeader("SOAPAction", "http://ws.grupo3.edu.ec/ConversionService/$methodName")
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        throw Exception("Error del servidor: ${response.code}")
                    }

                    val responseBody = response.body?.string() ?: throw Exception("Cuerpo de respuesta vacío")
                    val result = parseSoapResponse(responseBody, category)
                    Result.success(result)
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    private fun parseSoapResponse(xml: String, category: String): ConversionResponse {
        val factory = XmlPullParserFactory.newInstance()
        factory.isNamespaceAware = true
        val xpp = factory.newPullParser()
        xpp.setInput(StringReader(xml))

        var eventType = xpp.eventType
        var currentTag = ""

        var cat = ""
        var from = ""
        var to = ""
        var inputVal = 0.0
        var resultVal = 0.0

        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    currentTag = xpp.name
                }
                XmlPullParser.TEXT -> {
                    val text = xpp.text.trim()
                    if (text.isNotEmpty()) {
                        when (currentTag) {
                            "Category" -> cat = text
                            "FromUnit" -> from = text
                            "ToUnit" -> to = text
                            "InputValue" -> inputVal = text.toDoubleOrNull() ?: 0.0
                            "ResultValue" -> resultVal = text.toDoubleOrNull() ?: 0.0
                        }
                    }
                }
                XmlPullParser.END_TAG -> {
                    currentTag = ""
                }
            }
            eventType = xpp.next()
        }

        return ConversionResponse(cat, from, to, inputVal, resultVal)
    }
}