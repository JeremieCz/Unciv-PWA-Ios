plugins {
    id("com.github.xpenatan.gdx-teavm") version "1.6.1"
    id("kotlin")
}

sourceSets {
    main {
        java.srcDir("src/")
    }
}

gdxTeaVM {
    assets("../android/assets")

    webDefaults {
        mainClass = "com.unciv.app.web.WebLauncher"
        htmlTitle = "Unciv"
        htmlWidth = 1280
        htmlHeight = 720
        obfuscated = false
        optimization = org.teavm.gradle.api.OptimizationLevel.NONE
    }

    js {
        devServer {
            enabled = true
            autoReload = true
        }
    }
}

tasks.register<Copy>("copyPwaAssets") {
    dependsOn("gdx_teavm_web_js_build")
    from("src/pwa")
    into(layout.buildDirectory.dir("dist/js/webapp"))
    doLast {
        val index = layout.buildDirectory.file("dist/js/webapp/index.html").get().asFile
        if (index.isFile) {
            val html = index.readText()
            val head = """
                <link rel="manifest" href="manifest.webmanifest">
                <meta name="theme-color" content="#263238">
                <meta name="apple-mobile-web-app-capable" content="yes">
                <meta name="apple-mobile-web-app-status-bar-style" content="black-translucent">
                <meta name="apple-mobile-web-app-title" content="Unciv">
                <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, viewport-fit=cover">
                <script>if ('serviceWorker' in navigator) window.addEventListener('load', () => navigator.serviceWorker.register('./service-worker.js'));</script>
            """.trimIndent()
            index.writeText(html.replace("</head>", "$head\n</head>"))
        }
    }
}

tasks.named("gdx_teavm_web_js_build") {
    finalizedBy("copyPwaAssets")
}
