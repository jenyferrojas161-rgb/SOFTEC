package com.softech.softech.security;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Límite de intentos fallidos (anti fuerza bruta) para login y recuperación de contraseña.
 * Guarda en memoria: [cantidad de fallos, inicio de la ventana]. Pasados 15 minutos se reinicia.
 * Al reiniciar el servidor los contadores se limpian.
 */
@Component
public class LimitadorIntentos {
    private static final long VENTANA_MS = 15 * 60 * 1000L;
    private final Map<String, long[]> intentos = new ConcurrentHashMap<>();

    public boolean bloqueado(String clave, int maximo) {
        long[] e = intentos.get(clave);
        if (e == null) return false;
        if (System.currentTimeMillis() - e[1] > VENTANA_MS) {
            intentos.remove(clave);
            return false;
        }
        return e[0] >= maximo;
    }

    public void registrarFallo(String clave) {
        long ahora = System.currentTimeMillis();
        if (intentos.size() > 10000) intentos.clear(); // evita crecer sin límite
        intentos.compute(clave, (k, e) ->
                (e == null || ahora - e[1] > VENTANA_MS) ? new long[]{1, ahora} : new long[]{e[0] + 1, e[1]});
    }

    public void limpiar(String clave) {
        intentos.remove(clave);
    }
}
