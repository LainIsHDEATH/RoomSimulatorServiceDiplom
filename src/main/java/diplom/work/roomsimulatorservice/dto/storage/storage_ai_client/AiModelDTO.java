package diplom.work.roomsimulatorservice.dto.storage.storage_ai_client;

public record AiModelDTO(
        Long id,
        String type,
        String path,
        String description
) {
}