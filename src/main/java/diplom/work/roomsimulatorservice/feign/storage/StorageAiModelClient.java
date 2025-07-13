package diplom.work.roomsimulatorservice.feign.storage;

import diplom.work.roomsimulatorservice.dto.storage.storage_ai_client.AiModelDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "storage-ai-service", url = "${feign.client.config.storage-service.url}")
public interface StorageAiModelClient {

    @GetMapping("/api/models/{modelId}")
    ResponseEntity<AiModelDTO> getAiModelById(@PathVariable Integer modelId);
}
