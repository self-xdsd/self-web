/**
 * Copyright (c) 2020, Self XDSD Contributors
 * All rights reserved.
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"),
 * to read the Software only. Permission is hereby NOT GRANTED to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.selfxdsd.selfweb.api.output;

import javax.json.*;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Abstract JsonObject which can be extended to create JsonObject
 * out of any data.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @checkstyle FinalParameters (500 lines)
 * @checkstyle LineLength (500 lines)
 * @checkstyle ParameterName (500 lines)
 */
public abstract class AbstractJsonObject implements JsonObject {

    /**
     * Delegate JsonObject.
     */
    private final JsonObject delegate;

    /**
     * Ctor.
     * @param delegate Delegate.
     */
    public AbstractJsonObject(final Supplier<JsonObject> delegate) {
        this(delegate.get());
    }

    /**
     * Ctor.
     * @param delegate Delegate.
     */
    public AbstractJsonObject(final JsonObject delegate) {
        this.delegate = delegate;
    }

    @Override
    public JsonArray getJsonArray(String name) {
        return delegate.getJsonArray(name);
    }

    @Override
    public JsonObject getJsonObject(String name) {
        return delegate.getJsonObject(name);
    }

    @Override
    public JsonNumber getJsonNumber(String name) {
        return delegate.getJsonNumber(name);
    }

    @Override
    public JsonString getJsonString(String name) {
        return delegate.getJsonString(name);
    }

    @Override
    public String getString(String name) {
        return delegate.getString(name);
    }

    @Override
    public String getString(String name, String defaultValue) {
        return delegate.getString(name, defaultValue);
    }

    @Override
    public int getInt(String name) {
        return delegate.getInt(name);
    }

    @Override
    public int getInt(String name, int defaultValue) {
        return delegate.getInt(name, defaultValue);
    }

    @Override
    public boolean getBoolean(String name) {
        return delegate.getBoolean(name);
    }

    @Override
    public boolean getBoolean(String name, boolean defaultValue) {
        return delegate.getBoolean(name, defaultValue);
    }

    @Override
    public boolean isNull(String name) {
        return delegate.isNull(name);
    }

    @Override
    public JsonValue getValue(String jsonPointer) {
        return delegate.getValue(jsonPointer);
    }

    @Override
    public ValueType getValueType() {
        return delegate.getValueType();
    }

    @Override
    public JsonObject asJsonObject() {
        return delegate.asJsonObject();
    }

    @Override
    public JsonArray asJsonArray() {
        return delegate.asJsonArray();
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return delegate.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return delegate.containsValue(value);
    }

    @Override
    public JsonValue get(Object key) {
        return delegate.get(key);
    }

    @Override
    public JsonValue put(String key, JsonValue value) {
        return delegate.put(key, value);
    }

    @Override
    public JsonValue remove(Object key) {
        return delegate.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends JsonValue> m) {
        delegate.putAll(m);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public Set<String> keySet() {
        return delegate.keySet();
    }

    @Override
    public Collection<JsonValue> values() {
        return delegate.values();
    }

    @Override
    public Set<Entry<String, JsonValue>> entrySet() {
        return delegate.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        return delegate.equals(o);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public JsonValue getOrDefault(Object key, JsonValue defaultValue) {
        return delegate.getOrDefault(key, defaultValue);
    }

    @Override
    public void forEach(BiConsumer<? super String, ? super JsonValue> action) {
        delegate.forEach(action);
    }

    @Override
    public void replaceAll(BiFunction<? super String, ? super JsonValue, ? extends JsonValue> function) {
        delegate.replaceAll(function);
    }

    @Override
    public JsonValue putIfAbsent(String key, JsonValue value) {
        return delegate.putIfAbsent(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return delegate.remove(key, value);
    }

    @Override
    public boolean replace(String key, JsonValue oldValue, JsonValue newValue) {
        return delegate.replace(key, oldValue, newValue);
    }

    @Override
    public JsonValue replace(String key, JsonValue value) {
        return delegate.replace(key, value);
    }

    @Override
    public JsonValue computeIfAbsent(String key, Function<? super String, ? extends JsonValue> mappingFunction) {
        return delegate.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public JsonValue computeIfPresent(String key, BiFunction<? super String, ? super JsonValue, ? extends JsonValue> remappingFunction) {
        return delegate.computeIfPresent(key, remappingFunction);
    }

    @Override
    public JsonValue compute(String key, BiFunction<? super String, ? super JsonValue, ? extends JsonValue> remappingFunction) {
        return delegate.compute(key, remappingFunction);
    }

    @Override
    public JsonValue merge(String key, JsonValue value, BiFunction<? super JsonValue, ? super JsonValue, ? extends JsonValue> remappingFunction) {
        return delegate.merge(key, value, remappingFunction);
    }
}
