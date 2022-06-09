package com.dfsek.terra.api.util;

import com.dfsek.terra.api.util.function.IntObjConsumer;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


public interface Column<T> {
    int getMinY();
    
    int getMaxY();
    
    T get(int y);
    
    default void forEach(Consumer<T> consumer) {
        for(int y = getMinY(); y < getMaxY(); y++) {
            consumer.accept(get(y));
        }
    }
    
    default void forEach(IntObjConsumer<T> consumer) {
        for(int y = getMinY(); y < getMaxY(); y++) {
            consumer.accept(y, get(y));
        }
    }
    
    default List<? extends T> asList() {
        List<T> list = new ArrayList<>();
        forEach((Consumer<T>) list::add);
        return list;
    }
}
