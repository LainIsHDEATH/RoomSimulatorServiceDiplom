package diplom.work.roomsimulatorservice.service;

import diplom.work.roomsimulatorservice.model.room.Room;
import diplom.work.roomsimulatorservice.model.room.RoomParams;
import diplom.work.roomsimulatorservice.model.room.RoomState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

//@Component
//public class RoomRegistry {
//    private final Map<String, RoomParams> roomParamsMap = new ConcurrentHashMap<>();
//    private final Map<String, RoomState> roomStateMap = new ConcurrentHashMap<>();
//
//    // Зарегистрировать комнату (параметры + состояние)
//    public void registerRoom(RoomParams params, RoomState state) {
//        roomParamsMap.put(params.getRoomName(), params);
//        roomStateMap.put(params.getRoomName(), state);
//    }
//
//    public RoomParams getParams(String roomName) {
//        return roomParamsMap.get(roomName);
//    }
//
//    public RoomState getState(String roomName) {
//        return roomStateMap.get(roomName);
//    }
//
//    public Map<String, RoomParams> getAllParams() {
//        return roomParamsMap;
//    }
//
//    public Map<String, RoomState> getAllStates() {
//        return roomStateMap;
//    }
//
//    public boolean hasRoom(String name) {
//        return roomParamsMap.containsKey(name);
//    }
//}

/**
 * Хранит в памяти все активные комнаты и их состояние.
 * Поддерживает автоматическую очистку «старых» комнат.
 */
@Service
@RequiredArgsConstructor
public class RoomRegistryService {

    private final Map<String, Room> registry = new ConcurrentHashMap<>();

    /** Таймаут неактивности, после которого комната удаляется */
    private final Duration ttl = Duration.ofMinutes(30);

    public void register(Long id, String roomName, RoomParams roomParams, RoomState roomState) {
        roomState.initSurfaces(roomParams);
        registry.put(roomName, new Room(id, roomName, roomParams, roomState));
    }

    public Optional<Room> getByName(String roomName) {
        return Optional.ofNullable(registry.get(roomName))
                .filter(room -> !isExpired(room));
    }

    public boolean exists(String roomName) {
        return getByName(roomName).isPresent();
    }

    public void unregister(String roomName) {
        registry.remove(roomName);
    }

    /** Удаляет все устаревшие комнаты */
    public void cleanup() {
        Instant cutoff = Instant.now().minus(ttl);
        registry.entrySet().removeIf(e ->
                e.getValue().getRoomState().getLastUpdated().isBefore(cutoff)
        );
    }

    /**
     * Вспомогательный метод для проверки «просроченности» контекста.
     */
    private boolean isExpired(Room room) {
        Instant last = room.getRoomState().getLastUpdated();
        return last.isBefore(Instant.now().minus(ttl));
    }
}
