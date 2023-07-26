package dev.aurelium.auraskills.api.item;

public interface ItemHolder {

    <T> T get(Class<T> itemClass);

}
