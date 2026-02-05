package com.back.global.web

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.nio.file.Paths

@Configuration
class WebConfig(
    @Value("\${custom.genFile.dirPath:./gen_files}")
    private val genFileDirPath: String,
) : WebMvcConfigurer {

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        val absolutePath = Paths.get(genFileDirPath).toAbsolutePath().normalize().toString()

        registry
            .addResourceHandler("/gen/**")
            .addResourceLocations("file:$absolutePath/")
    }
}
