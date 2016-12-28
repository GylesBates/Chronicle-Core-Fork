/*
 * Copyright 2016 higherfrequencytrading.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.openhft.chronicle.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;

/**
 * Created by peter on 13/12/16.
 */
public enum Mocker {
    ;

    @NotNull
    public static <T> T logging(@NotNull Class<T> tClass, String description, @NotNull PrintStream out) {
        return intercepting(tClass, description, out::println);
    }

    @NotNull
    public static <T> T logging(@NotNull Class<T> tClass, String description, @NotNull PrintWriter out) {
        return intercepting(tClass, description, out::println);
    }

    @NotNull
    public static <T> T logging(@NotNull Class<T> tClass, String description, @NotNull StringWriter out) {
        return logging(tClass, description, new PrintWriter(out));
    }

    @NotNull
    public static <T> T queuing(@NotNull Class<T> tClass, String description, @NotNull BlockingQueue<String> queue) {
        return intercepting(tClass, description, queue::add);
    }

    @NotNull
    public static <T> T intercepting(@NotNull Class<T> tClass, String description, @NotNull Consumer<String> consumer) {
        //noinspection unchecked
        return (T) Proxy.newProxyInstance(tClass.getClassLoader(), new Class[]{tClass}, new InvocationHandler() {
            @Nullable
            @Override
            public Object invoke(Object proxy, @NotNull Method method, @Nullable Object[] args) throws Throwable {
                if (method.getDeclaringClass() == Object.class)
                    return method.invoke(this, args);
                consumer.accept(description + method.getName() + (args == null ? "()" : Arrays.toString(args)));
                return null;
            }
        });
    }

    @NotNull
    public static <T> T ignored(@NotNull Class<T> tClass) {
        //noinspection unchecked
        return (T) Proxy.newProxyInstance(tClass.getClassLoader(), new Class[]{tClass}, new InvocationHandler() {
            @Nullable
            @Override
            public Object invoke(Object proxy, @NotNull Method method, Object[] args) throws Throwable {
                if (method.getDeclaringClass() == Object.class)
                    return method.invoke(this, args);
                return null;
            }
        });
    }
}
