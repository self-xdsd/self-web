package com.selfxdsd.selfweb.api.output;

import javax.json.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;

/**
 * Abstract JsonArray which can be extended to create JsonArray
 * out of any data.
 * @author criske
 * @version $Id$
 * @since 0.0.1
 * @checkstyle FinalParameters (500 lines)
 * @checkstyle LineLength (500 lines)
 * @checkstyle ParameterName (500 lines)
 */
public abstract class AbstractJsonArray implements JsonArray {

    /**
     * JsonArray delegate.
     */
    private final JsonArray delegate;

    /**
     * Ctor.
     * @param delegate Delegate supplier.
     */
    public AbstractJsonArray(final Supplier<JsonArray> delegate) {
        this(delegate.get());
    }

    /**
     * Ctor.
     * @param delegate JsonArray delegate.
     */
    public AbstractJsonArray(final JsonArray delegate) {
        this.delegate = delegate;
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
    public boolean contains(final Object o) {
        return delegate.contains(o);
    }

    @Override
    public Iterator<JsonValue> iterator() {
        return delegate.iterator();
    }

    @Override
    public Object[] toArray() {
        return delegate.toArray();
    }

    @Override
    public <T> T[] toArray(final T[] a) {
        return delegate.toArray(a);
    }

    @Override
    public boolean add(final JsonValue jsonValue) {
        return delegate.add(jsonValue);
    }

    @Override
    public boolean remove(final Object o) {
        return delegate.remove(o);
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        return delegate.containsAll(c);
    }

    @Override
    public boolean addAll(final Collection<? extends JsonValue> c) {
        return delegate.addAll(c);
    }

    @Override
    public boolean addAll(final int index, final Collection<? extends JsonValue> c) {
        return delegate.addAll(index, c);
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        return delegate.removeAll(c);
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        return delegate.retainAll(c);
    }

    @Override
    public void replaceAll(final UnaryOperator<JsonValue> operator) {
        delegate.replaceAll(operator);
    }

    @Override
    public void sort(final Comparator<? super JsonValue> c) {
        delegate.sort(c);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public boolean equals(final Object o) {
        return delegate.equals(o);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public JsonValue get(final int index) {
        return delegate.get(index);
    }

    @Override
    public JsonValue set(final int index, final JsonValue element) {
        return delegate.set(index, element);
    }

    @Override
    public void add(final int index, final JsonValue element) {
        delegate.add(index, element);
    }

    @Override
    public JsonValue remove(final int index) {
        return delegate.remove(index);
    }

    @Override
    public int indexOf(final Object o) {
        return delegate.indexOf(o);
    }

    @Override
    public int lastIndexOf(final Object o) {
        return delegate.lastIndexOf(o);
    }

    @Override
    public ListIterator<JsonValue> listIterator() {
        return delegate.listIterator();
    }

    @Override
    public ListIterator<JsonValue> listIterator(final int index) {
        return delegate.listIterator(index);
    }

    @Override
    public List<JsonValue> subList(final int fromIndex, final int toIndex) {
        return delegate.subList(fromIndex, toIndex);
    }

    @Override
    public Spliterator<JsonValue> spliterator() {
        return delegate.spliterator();
    }

    @Override
    public <T> T[] toArray(final IntFunction<T[]> generator) {
        return delegate.toArray(generator);
    }

    @Override
    public boolean removeIf(final Predicate<? super JsonValue> filter) {
        return delegate.removeIf(filter);
    }

    @Override
    public Stream<JsonValue> stream() {
        return delegate.stream();
    }

    @Override
    public Stream<JsonValue> parallelStream() {
        return delegate.parallelStream();
    }

    @Override
    public void forEach(final Consumer<? super JsonValue> action) {
        delegate.forEach(action);
    }

    @Override
    public JsonObject getJsonObject(final int index) {
        return delegate.getJsonObject(index);
    }

    @Override
    public JsonArray getJsonArray(final int index) {
        return delegate.getJsonArray(index);
    }

    @Override
    public JsonNumber getJsonNumber(final int index) {
        return delegate.getJsonNumber(index);
    }

    @Override
    public JsonString getJsonString(final int index) {
        return delegate.getJsonString(index);
    }

    @Override
    public <T extends JsonValue> List<T> getValuesAs(final Class<T> clazz) {
        return delegate.getValuesAs(clazz);
    }

    @Override
    public <T, K extends JsonValue> List<T> getValuesAs(final Function<K, T> func) {
        return delegate.getValuesAs(func);
    }

    @Override
    public String getString(final int index) {
        return delegate.getString(index);
    }

    @Override
    public String getString(final int index, final String defaultValue) {
        return delegate.getString(index, defaultValue);
    }

    @Override
    public int getInt(final int index) {
        return delegate.getInt(index);
    }

    @Override
    public int getInt(final int index, final int defaultValue) {
        return delegate.getInt(index, defaultValue);
    }

    @Override
    public boolean getBoolean(final int index) {
        return delegate.getBoolean(index);
    }

    @Override
    public boolean getBoolean(final int index, final boolean defaultValue) {
        return delegate.getBoolean(index, defaultValue);
    }

    @Override
    public boolean isNull(final int index) {
        return delegate.isNull(index);
    }



}
