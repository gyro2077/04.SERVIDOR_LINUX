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
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    // IMPORTANTE: Cambia esta IP por la IP REAL de tu máquina en la red local
    // Para emulador Android: usa 10.0.2.2 (equivale a localhost del host)
    // Para dispositivo físico: usa la IP de tu PC (ej: 192.168.100.171)
    // Para obtener tu IP: ejecuta 'hostname -I' en tu terminal
    private var baseUrl = "http://192.168.100.171:8080/04.SERVIDOR/conversion"

    private val namespace = "http://ws.grupo3.edu.ec/"

    fun updateBaseUrl(newIp: String) {
        baseUrl = "http://$newIp:8080/04.SERVIDOR/conversion"
    }

    suspend fun convert(category: String, value: Double, fromUnit: String, toUnit: String): Result<ConversionResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val methodName = "convert$category"

                // Envelope SOAP 1.1 CORREGIDO para JAX-WS/Payara
                // Los parámetros NO llevan prefijo de namespace, solo el elemento raíz
                val soapEnvelope = """<?xml version="1.0" encoding="utf-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
               xmlns:ns="$namespace">
  <soap:Body>
    <ns:$methodName>
      <value>$value</value>
      <fromUnit>$fromUnit</fromUnit>
      <toUnit>$toUnit</toUnit>
    </ns:$methodName>
  </soap:Body>
</soap:Envelope>"""

                val requestBody = soapEnvelope.toRequestBody("text/xml; charset=utf-8".toMediaType())

                // SOAPAction con el namespace completo
                val soapAction = "$namespace$methodName"

                val request = Request.Builder()
                    .url(baseUrl)
                    .post(requestBody)
                    .addHeader("Content-Type", "text/xml; charset=utf-8")
                    .addHeader("SOAPAction", soapAction)
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        val errorBody = response.body?.string() ?: "Sin detalles"
                        throw Exception("HTTP ${response.code}: $errorBody")
                    }

                    val responseBody = response.body?.string() ?: throw Exception("Respuesta vacía")
                    val result = parseSoapResponse(responseBody)
                    Result.success(result)
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    private fun parseSoapResponse(xml: String): ConversionResponse {
        val factory = XmlPullParserFactory.newInstance()
        factory.isNamespaceAware = true
        val xpp = factory.newPullParser()
        xpp.setInput(StringReader(xml))

        var eventType = xpp.eventType
        var currentTag = ""
        var inReturn = false

        var cat = ""
        var from = ""
        var to = ""
        var inputVal = 0.0
        var resultVal = 0.0

        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    currentTag = xpp.name
                    if (currentTag == "return") {
                        inReturn = true
                    }
                }
                XmlPullParser.TEXT -> {
                    val text = xpp.text.trim()
                    if (text.isNotEmpty() && inReturn) {
                        when (currentTag) {
                            "category" -> cat = text
                            "fromUnit" -> from = text
                            "toUnit" -> to = text
                            "inputValue" -> inputVal = text.toDoubleOrNull() ?: 0.0
                            "resultValue" -> resultVal = text.toDoubleOrNull() ?: 0.0
                        }
                    }
                }
                XmlPullParser.END_TAG -> {
                    if (xpp.name == "return") {
                        inReturn = false
                    }
                    currentTag = ""
                }
            }
            eventType = xpp.next()
        }

        return ConversionResponse(cat, from, to, inputVal, resultVal)
    }
}