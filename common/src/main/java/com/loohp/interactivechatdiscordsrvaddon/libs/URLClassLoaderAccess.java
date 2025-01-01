/*
 * This file is part of InteractiveChatDiscordSrvAddon.
 *
 * Copyright (C) 2020 - 2025. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2020 - 2025. Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.loohp.interactivechatdiscordsrvaddon.libs;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;

/**
 * Provides access to {@link URLClassLoader}#addURL.
 */
public abstract class URLClassLoaderAccess {

    /**
     * Creates a {@link URLClassLoaderAccess} for the given class loader.
     *
     * @param classLoader the class loader
     * @return the access object
     */
    public static URLClassLoaderAccess create(URLClassLoader classLoader) {
        if (Reflection.isSupported()) {
            return new Reflection(classLoader);
        } else if (Unsafe.isSupported()) {
            return new Unsafe(classLoader);
        } else {
            return Noop.INSTANCE;
        }
    }

    private static void throwError(Throwable cause) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("InteractiveChatDiscordSRVAddon is unable to inject into the plugin URLClassLoader.\n" +
                                                    "You may be able to fix this problem by adding the following command-line argument " +
                                                    "directly after the 'java' command in your start script: \n'--add-opens java.base/java.lang=ALL-UNNAMED'", cause);
    }

    private final URLClassLoader classLoader;


    protected URLClassLoaderAccess(URLClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * Adds the given URL to the class loader.
     *
     * @param url the URL to add
     */
    public abstract void addURL(@NonNull URL url);

    /**
     * Accesses using reflection, not supported on Java 9+.
     */
    private static class Reflection extends URLClassLoaderAccess {

        private static final Method ADD_URL_METHOD;

        static {
            Method addUrlMethod;
            try {
                addUrlMethod = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                addUrlMethod.setAccessible(true);
            } catch (Exception e) {
                addUrlMethod = null;
            }
            ADD_URL_METHOD = addUrlMethod;
        }

        private static boolean isSupported() {
            return ADD_URL_METHOD != null;
        }

        Reflection(URLClassLoader classLoader) {
            super(classLoader);
        }

        @Override
        public void addURL(@NonNull URL url) {
            try {
                ADD_URL_METHOD.invoke(super.classLoader, url);
            } catch (ReflectiveOperationException e) {
                URLClassLoaderAccess.throwError(e);
            }
        }

    }

    /**
     * Accesses using sun.misc.Unsafe, supported on Java 9+.
     *
     * @author Vaishnav Anil (https://github.com/slimjar/slimjar)
     */
    private static class Unsafe extends URLClassLoaderAccess {

        private static final Object UNSAFE;
        private static final Method OBJECT_FIELD_OFFSET_METHOD;
        private static final Method GET_OBJECT_METHOD;

        static {
            Object unsafe;
            Method objectFieldOffsetMethod;
            Method getObjectMethod;
            try {
                Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
                Field unsafeField = unsafeClass.getDeclaredField("theUnsafe");
                unsafeField.setAccessible(true);
                unsafe = unsafeField.get(null);
                objectFieldOffsetMethod = unsafe.getClass().getMethod("objectFieldOffset", Field.class);
                getObjectMethod = unsafe.getClass().getMethod("getObject", Object.class, long.class);
            } catch (Throwable t) {
                unsafe = null;
                objectFieldOffsetMethod = null;
                getObjectMethod = null;
            }
            UNSAFE = unsafe;
            OBJECT_FIELD_OFFSET_METHOD = objectFieldOffsetMethod;
            GET_OBJECT_METHOD = getObjectMethod;
        }

        private static boolean isSupported() {
            return UNSAFE != null;
        }

        private static Object fetchField(final Class<?> clazz, final Object object, final String name) throws NoSuchFieldException, InvocationTargetException, IllegalAccessException {
            Field field = clazz.getDeclaredField(name);
            long offset = (long) OBJECT_FIELD_OFFSET_METHOD.invoke(UNSAFE, field);
            return GET_OBJECT_METHOD.invoke(UNSAFE, object, offset);
        }

        private final Collection<URL> unopenedURLs;
        private final Collection<URL> pathURLs;

        Unsafe(URLClassLoader classLoader) {
            super(classLoader);

            Collection<URL> unopenedURLs;
            Collection<URL> pathURLs;
            try {
                Object ucp = fetchField(URLClassLoader.class, classLoader, "ucp");
                unopenedURLs = (Collection<URL>) fetchField(ucp.getClass(), ucp, "unopenedUrls");
                pathURLs = (Collection<URL>) fetchField(ucp.getClass(), ucp, "path");
            } catch (Throwable e) {
                unopenedURLs = null;
                pathURLs = null;
            }

            this.unopenedURLs = unopenedURLs;
            this.pathURLs = pathURLs;
        }

        @Override
        public void addURL(@NonNull URL url) {
            if (this.unopenedURLs == null || this.pathURLs == null) {
                URLClassLoaderAccess.throwError(new NullPointerException("unopenedURLs or pathURLs"));
            }

            this.unopenedURLs.add(url);
            this.pathURLs.add(url);
        }

    }

    private static class Noop extends URLClassLoaderAccess {

        private static final Noop INSTANCE = new Noop();

        private Noop() {
            super(null);
        }

        @Override
        public void addURL(@NonNull URL url) {
            URLClassLoaderAccess.throwError(null);
        }

    }

}