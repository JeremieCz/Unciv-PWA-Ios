plugins {
    id("com.github.xpenatan.gdx-teavm") version "1.6.1"
    id("kotlin")
}

gdxTeaVM {
    assets("../android/assets")

    webDefaults {
        mainClass = "com.unciv.app.web.WebLauncher"
        htmlTitle = "Unciv"
        htmlWidth = 1280
        htmlHeight = 720
        obfuscated = false
    }

    js {}
}
