package com.engine.ecs;

import java.security.InvalidParameterException;
import java.util.*;

public class EcsRegister {

    private final List<Integer> entities = new ArrayList<>();
    private final Map<Class<?>, Map<Integer, Object>> components = new HashMap<>();

    private int nextEntityId = 1;

    public int createEntity() {
        var ent = nextEntityId++;
        entities.add(ent);
        return ent;
    }

    public void set(int entity, Object component) {
        ensureEntity(entity);

        components
                .computeIfAbsent(component.getClass(), _ -> new HashMap<>())
                .put(entity, component);
    }

    public boolean has(int entity, Class<?> type) {
        var map = components.get(type);
        if (map == null) {
            return false;
        }

        return map.containsKey(entity) && map.get(entity) != null;
    }

    public <T> T get(int entity, Class<T> type) {
        var map = components.get(type);
        if (map == null || map.isEmpty()) {
            return null;
        }

        return type.cast(map.get(entity));
    }

    /**
     * Gets all entities that have all the passed components
     *
     * @param types required component types
     * @return collection of entities
     */
    public Collection<Integer> view(Class<?>... types) {
        if (types.length == 0) return Collections.emptySet();

        var firstComponentMap = components.get(types[0]);
        if (firstComponentMap == null || firstComponentMap.isEmpty()) {
            return Collections.emptyList();
        }

        var result = new ArrayList<Integer>();
        for (var entity : firstComponentMap.keySet()) {
            boolean hasAll = true;
            for (int i = 1; i < types.length; i++) {
                if (!has(entity, types[i])) {
                    hasAll = false;
                    break;
                }
            }

            if (hasAll) result.add(entity);
        }

        return result;
    }

    public void destroy(int entity) {
        entities.remove(entity);
        components.values().forEach(map -> map.remove(entity));
    }

    private void ensureEntity(int entity) {
        if (!entities.contains(entity)) throw new InvalidParameterException("Entity: " + entity + " Does not exist");
    }
}
