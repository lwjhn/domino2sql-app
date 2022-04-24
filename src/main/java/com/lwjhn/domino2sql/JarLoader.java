package com.lwjhn.domino2sql;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Objects;

class JarLoader {
    private final URLClassLoader urlClassLoader;

    public JarLoader() {
        this.urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
    }

    public JarLoader(URLClassLoader urlClassLoader) {
        this.urlClassLoader = urlClassLoader;
    }

    public void load(URL url) throws Exception {
        Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        addURL.setAccessible(true);
        addURL.invoke(urlClassLoader, url);
    }

    public void load(File lib) throws Exception {
        if (!lib.exists())
            return;
        if (lib.isDirectory()) {
            for (File file : Objects.requireNonNull(lib.listFiles(file -> file.exists() && file.isFile() && file.getName().endsWith(".jar")))) {
                this.load(file.toURI().toURL());
            }
        } else if (lib.isFile()) {
            this.load(lib.toURI().toURL());
        }
    }

    public static void load(String path) throws Exception {
        (new JarLoader()).load(new File(path));
    }
}