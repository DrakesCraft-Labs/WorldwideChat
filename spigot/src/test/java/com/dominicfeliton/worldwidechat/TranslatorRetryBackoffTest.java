package com.dominicfeliton.worldwidechat;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Regresion del ticket 241: un traductor inalcanzable durante el arranque dejaba el
 * plugin apagado hasta el siguiente reinicio del servidor. Ahora el fallo se marca
 * como fallo de conexion para que el plugin programe una recarga diferida.
 */
class TranslatorRetryBackoffTest extends WWCIntegrationTest {

    @Test
    void unreachableTranslatorIsMarkedAsConnectionFailure() {
        YamlConfiguration config = plugin().getConfigManager().getMainConfig();
        config.set("Translator.testModeTranslator", false);
        config.set("Translator.useLibreTranslate", true);
        config.set("Translator.libreURL", "http://127.0.0.1:1");
        config.set("Translator.libreAPIKey", "");

        String result = plugin().getConfigManager().loadTranslatorSettings();

        assertEquals("Invalid", result);
        assertTrue(plugin().getConfigManager().translatorConnectionFailed(),
                "Un traductor habilitado que no responde debe quedar marcado como fallo de conexion.");
    }

    @Test
    void translatorDisabledIsNotAConnectionFailure() {
        YamlConfiguration config = plugin().getConfigManager().getMainConfig();
        config.set("Translator.testModeTranslator", false);

        String result = plugin().getConfigManager().loadTranslatorSettings();

        assertEquals("Invalid", result);
        assertFalse(plugin().getConfigManager().translatorConnectionFailed(),
                "Sin traductor habilitado el fallo es de configuracion, no de conexion: no debe reintentarse.");
    }
}
