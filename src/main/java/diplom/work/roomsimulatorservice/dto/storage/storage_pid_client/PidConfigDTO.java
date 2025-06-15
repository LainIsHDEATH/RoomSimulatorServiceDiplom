package diplom.work.roomsimulatorservice.dto.storage.storage_pid_client;

public record PidConfigDTO(
        Integer   id,
        Double kp,
        Double ki,
        Double kd,
        String tunedMethod,
        Boolean active
) {
}
