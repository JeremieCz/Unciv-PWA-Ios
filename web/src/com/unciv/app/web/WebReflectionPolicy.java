package com.unciv.app.web;

import com.github.xpenatan.gdx.teavm.backends.shared.config.reflection.GdxReflectionHelper;
import org.teavm.extension.Autoregistered;
import org.teavm.extension.spi.reflection.SimpleReflectionPolicy;

@Autoregistered
public class WebReflectionPolicy extends SimpleReflectionPolicy {
    @Override
    protected void setup() {
        GdxReflectionHelper.applyDefaults(this::selectClasses);
        selectClasses(type -> type.name().startsWith("com.unciv."))
                .foundByName()
                .reflectableFields(field -> true)
                .reflectableMethods(method -> method.isConstructor());
    }
}
