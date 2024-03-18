package dev.aurelium.auraskills.api.item;

import java.util.HashMap;
import java.util.Map;

public class ItemContext {

    private final Map<String, Object> map;

    public ItemContext(Map<String, Object> map) {
        this.map = map;
    }

    public static ItemContextBuilder builder() {
        return new ItemContextBuilder();
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public static class ItemContextBuilder {

        private final Map<String, Object> map = new HashMap<>();

        /**
         * Defines the base material of the context item. This should be a valid Minecraft item name
         * such as diamond_block.
         *
         * @param material the material
         * @return the builder
         */
        public ItemContextBuilder material(String material) {
            map.put("material", material);
            return this;
        }

        /**
         * Defines the exact position/slot of the context in the menu. Two formats are possible:
         * 1. A comma-separated "row,column" format where the row and columns start at 0,0 at the top left of the menu.
         * 2. A single integer (as a String) that defines the slot number from 0-54 going left to right, top to bottom.
         *
         * @param pos the pos value in either format
         * @return the builder
         */
        public ItemContextBuilder pos(String pos) {
            map.put("pos", pos);
            return this;
        }

        /**
         * Defines the context group this context is part of. Together with {@link #order(int) order}, it is
         * an alternative to specifying a static {@link #pos(String) pos}. When a group and order is specified,
         * items are positioned within the rectangle created by the start and end of the group defined in the menu
         * configuration file and positioned based on their order and align.
         *
         * @param group the group name as defined in the menu file
         * @return the builder
         */
        public ItemContextBuilder group(String group) {
            map.put("group", group);
            return this;
        }

        /**
         * Defines the order within the context group this context is part of. You must also use {@link #group(String) group}
         * to define the group name. Lower order numbers will be positioned to the left and top of contexts with higher order
         * numbers but the exact position is based on the "align" of the group.
         *
         * @param order the order of the context
         * @return the builder
         */
        public ItemContextBuilder order(int order) {
            map.put("order", order);
            return this;
        }

        /**
         * Adds any custom key-value pair to the context in order to specify special item metas
         * like enchantments, nbt, etc.
         *
         * @param key the key of the entry to set, should be a valid Slate menu file item context key
         * @param value the value of the entry
         * @return the builder
         */
        public ItemContextBuilder set(String key, Object value) {
            map.put(key, value);
            return this;
        }

        /**
         * Builds the context.
         *
         * @return the context
         */
        public ItemContext build() {
            return new ItemContext(map);
        }

    }

}
