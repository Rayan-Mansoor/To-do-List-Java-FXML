package com.mycompany.todolistgui;

import io.github.cdimascio.dotenv.Dotenv;

public final class Env {
    private static final Dotenv DOTENV = Dotenv.configure()
            .directory(System.getProperty("user.dir")) // project root
            .ignoreIfMalformed()
            .ignoreIfMissing() // allows CI/prod to use real OS env vars
            .load();

    private Env() {}

    /** Prefer OS env, then .env file, else default (may be null). */
    public static String get(String key, String def) {
        String v = System.getenv(key);
        if (v != null && !v.isEmpty()) return v;
        v = DOTENV.get(key);
        return (v != null && !v.isEmpty()) ? v : def;
    }

    /** Require a value; throws if not found in OS env or .env. */
    public static String require(String key) {
        String v = get(key, null);
        if (v == null) throw new IllegalStateException("Missing env: " + key);
        return v;
    }
}
