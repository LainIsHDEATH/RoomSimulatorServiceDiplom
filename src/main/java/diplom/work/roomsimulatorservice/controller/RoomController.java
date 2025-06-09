package diplom.work.roomsimulatorservice.controller;

import diplom.work.roomsimulatorservice.model.room.RoomParams;
import diplom.work.roomsimulatorservice.model.room.RoomState;
import diplom.work.roomsimulatorservice.service.RoomRegistryService;
import diplom.work.roomsimulatorservice.service.RoomSimulationService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomSimulationService simulationService;
    private final RoomRegistryService roomRegistryService;

//    @PostMapping
//    public ResponseEntity<String> registerRoom(@RequestBody RoomConfigRequest request) {
//        List<SurfaceParams> surfaces = request.surfaces().stream()
//                .map(SurfaceParamsMapper::fromDTO)
//                .collect(Collectors.toList());
//
//        RoomParams roomParams = RoomParams.builder()
//                .roomName(request.roomName())
//                .volume(request.volume())
//                .airDensity(request.airDensity())
//                .airSpecificHeat(request.airSpecificHeat())
//                .surfaces(surfaces)
//                .build();
//
//        RoomState roomState = new RoomState(
//                roomParams,
//                request.initialAirTemperature(),
//                request.peopleCount()
//        );
//        roomState.setHeaterPower(request.heaterPower());
//
//        roomRegistry.registerRoom(roomParams, roomState);
//
//        boolean lstm = true; //////////TODO////////////////////////////////////////////////////////!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//
//        for (int step = 0; step < 20000; step++) {
//            //lstm= step % 60 == 0;
//            simulationService.simulateStep(roomParams, roomState, lstm);
//
//            System.out.printf(
//                    "Step %3d | Heater: %.2f W | Air: %.2f°C | NorthWall: %.2f°C | SouthWall: %.2f°C | EastWall: %.2f°C | WestWall: %.2f°C | Roof: %.2f°C | Door: %.2f°C | Window: %.2f°C%n",
//                    step + 1,
//                    roomState.getHeaterPower(),
//                    roomState.getAirTemperature(),
//                    roomState.getSurfaceTemperature("NorthWall"),
//                    roomState.getSurfaceTemperature("SouthWall"),
//                    roomState.getSurfaceTemperature("EastWall"),
//                    roomState.getSurfaceTemperature("WestWall"),
//                    roomState.getSurfaceTemperature("Roof"),
//                    roomState.getSurfaceTemperature("Door"),
//                    roomState.getSurfaceTemperature("Window")
//            );
//        }
//
//        return ResponseEntity.ok("Комната зарегистрирована: " + request.roomName());
//    }


//    @PostMapping("/step")
//    public RoomState step(@RequestBody ControlActionRequest action) {
//        RoomState roomState = roomRegistry.getState(action.getRoomName());
//        roomState.setHeaterPower(action.getHeaterPower());
//
//        simulationService.simulateStep(action.getRoomParams(), roomState, action.isUseLstm());
//
//        return roomState;
//    }

    @GetMapping("/state")
    public RoomState getState(@RequestParam String roomName) {
        return roomRegistryService.getByName(roomName).get().getRoomState();
    }

    @Getter
    public class ControlActionRequest {
        private String roomName;
        private double heaterPower;
        private RoomParams roomParams;
        private boolean useLstm;
    }

}
