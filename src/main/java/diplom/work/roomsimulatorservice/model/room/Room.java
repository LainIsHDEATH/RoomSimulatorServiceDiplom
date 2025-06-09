package diplom.work.roomsimulatorservice.model.room;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Room implements Serializable {
    private Long id;
    private String name;
    private RoomParams roomParams;
    private RoomState roomState;
}