package com.example.calendar.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class EmitterRepository implements SseRepository {
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<String, Object> eventCache = new ConcurrentHashMap<>();

    @Override
    public SseEmitter save(String emitterId, SseEmitter sseEmitter) {
        emitters.put(emitterId, sseEmitter);
        return sseEmitter;
    }

    @Override
    public void saveEventCache(String eventCacheId, Object event) {
        eventCache.put(eventCacheId, event);
    }

    @Override
    public Map<String, SseEmitter> findAllEmitterStartsWithEmail(String email) {
        return emitters.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(email))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Map<String, Object> findAllEventCacheStartsWithEmail(String email) {
        return eventCache.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(email))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public void deleteEmitterById(String id) {
        emitters.remove(id);
    }

    @Override
    public void deleteAllEmitterStartsWithId(String id) {
        emitters.keySet().removeIf(key -> key.startsWith(id));
    }

    @Override
    public void deleteAllEventCacheStartsWithId(String id) {
        eventCache.keySet().removeIf(key -> key.startsWith(id));
    }
}
