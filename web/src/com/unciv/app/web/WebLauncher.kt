package com.unciv.app.web

import com.badlogic.gdx.backends.web.WebApplication
import com.badlogic.gdx.backends.web.WebApplicationConfiguration
import com.unciv.UncivGame
import com.unciv.logic.files.UncivFiles
import com.unciv.utils.Display
import com.unciv.utils.PlatformDisplay

object WebLauncher {
    @JvmStatic
    fun main(args: Array<String>) {
        Display.platform = object : PlatformDisplay {}
        UncivFiles.preferExternalStorage = false

        val config = WebApplicationConfiguration().apply {
            width = 0
            height = 0
        }
        WebApplication(UncivGame(), config)
    }
}
