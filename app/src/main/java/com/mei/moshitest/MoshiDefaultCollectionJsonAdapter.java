package com.mei.moshitest;

/**
 * Created by mei on 2022/10/19.
 * Description:
 * 参考：https://blog.csdn.net/yuzhiqiang_1993/article/details/124076400
 */

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author greensun
 * @date 2021/6/2
 * @desc null 转换成空collection  更改自{@link com.squareup.moshi.CollectionJsonAdapter}
 * <p>
 * 如果字段声明为Collection, json中值为null，kotlin下在声明类型为非空情况下会抛异常，这里给一个空Collection填充
 */
public abstract class MoshiDefaultCollectionJsonAdapter<C extends Collection<T>, T> extends JsonAdapter<C> {

    public static final Factory FACTORY = new Factory() {
                @Override
                public JsonAdapter<?> create(
                        Type type, Set<? extends Annotation> annotations, Moshi moshi) {
                    Class<?> rawType = Types.getRawType(type);
                    if (!annotations.isEmpty()) return null;
                    if (rawType == List.class || rawType == Collection.class) {
                        return newArrayListAdapter(type, moshi);
                    } else if (rawType == Set.class) {
                        return newLinkedHashSetAdapter(type, moshi);
                    }
                    return null;
                }
            };

    private final JsonAdapter<T> elementAdapter;

    private MoshiDefaultCollectionJsonAdapter(JsonAdapter<T> elementAdapter) {
        this.elementAdapter = elementAdapter;
    }

    static <T> JsonAdapter<Collection<T>> newArrayListAdapter(Type type, Moshi moshi) {
        Type elementType = Types.collectionElementType(type, Collection.class);
        JsonAdapter<T> elementAdapter = moshi.adapter(elementType);
        return new MoshiDefaultCollectionJsonAdapter<Collection<T>, T>(elementAdapter) {
            @Override
            Collection<T> newCollection() {
                return new ArrayList<>();
            }
        };
    }

    static <T> JsonAdapter<Set<T>> newLinkedHashSetAdapter(Type type, Moshi moshi) {
        Type elementType = Types.collectionElementType(type, Collection.class);
        JsonAdapter<T> elementAdapter = moshi.adapter(elementType);
        return new MoshiDefaultCollectionJsonAdapter<Set<T>, T>(elementAdapter) {
            @Override
            Set<T> newCollection() {
                return new LinkedHashSet<>();
            }
        };
    }

    abstract C newCollection();

    @Override
    public C fromJson(JsonReader reader) throws IOException {
        C result = newCollection();
        if (reader.peek() == JsonReader.Token.NULL) {
            // null 直接返回空collection
            reader.nextNull();
            return result;
        }
        reader.beginArray();
        while (reader.hasNext()) {
            result.add(elementAdapter.fromJson(reader));
        }
        reader.endArray();
        return result;
    }

    @Override
    public void toJson(JsonWriter writer, C value) throws IOException {
        writer.beginArray();
        if(value != null) {
            for (T element : value) {
                elementAdapter.toJson(writer, element);
            }
        }
        writer.endArray();
    }

    @Override
    public String toString() {
        return elementAdapter + ".collection()";
    }
}
