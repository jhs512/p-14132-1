package com.back.standard.lib

import org.springframework.http.HttpHeaders
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerAdapter
import org.springframework.web.servlet.HandlerMapping
import jakarta.servlet.http.HttpServletResponse

@Component
class InternalRestClient(
    private val handlerMappings: List<HandlerMapping>,
    private val handlerAdapters: List<HandlerAdapter>
) {
    fun get(uri: String, headers: Map<String, String> = emptyMap()): Response {
        return execute("GET", uri, headers)
    }

    fun post(uri: String, headers: Map<String, String> = emptyMap(), body: String? = null): Response {
        return execute("POST", uri, headers, body)
    }

    fun put(uri: String, headers: Map<String, String> = emptyMap(), body: String? = null): Response {
        return execute("PUT", uri, headers, body)
    }

    fun delete(uri: String, headers: Map<String, String> = emptyMap()): Response {
        return execute("DELETE", uri, headers)
    }

    private fun execute(method: String, uri: String, headers: Map<String, String>, body: String? = null): Response {
        val request = MockHttpServletRequest(method, uri).apply {
            headers.forEach { (key, value) -> addHeader(key, value) }
            body?.let {
                setContent(it.toByteArray())
                contentType = "application/json"
            }
        }
        val response = MockHttpServletResponse()

        val handler = handlerMappings
            .firstNotNullOfOrNull { it.getHandler(request) }
            ?: return Response(HttpServletResponse.SC_NOT_FOUND, "No handler found")

        val adapter = handlerAdapters
            .firstOrNull { it.supports(handler.handler!!) }
            ?: return Response(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "No adapter found")

        adapter.handle(request, response, handler.handler!!)

        return Response(response.status, response.contentAsString)
    }

    data class Response(
        val status: Int,
        val body: String
    ) {
        val isOk: Boolean get() = status == HttpServletResponse.SC_OK
    }
}
