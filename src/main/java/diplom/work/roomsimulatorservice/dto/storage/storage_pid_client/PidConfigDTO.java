package diplom.work.roomsimulatorservice.dto.storage.storage_pid_client;

public record PidConfigDTO(
        Long   id,
        Double kp,
        Double ki,
        Double kd,
        String tunedMethod,
        Boolean active
) {
}
