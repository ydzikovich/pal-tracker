package io.pivotal.pal.tracker;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class EnvController {


    private final String port;
    private final String mem_limit;
    private final String cf_instance_index;
    private final String cf_instance_addr;

    public EnvController(@Value("${PORT:NOT SET}") String port,
                         @Value("${MEMORY_LIMIT:NOT SET}") String mem_limit,
                         @Value("${CF_INSTANCE_INDEX:NOT SET}") String cf_instance_index,
                         @Value("${CF_INSTANCE_ADDR:NOT SET}") String cf_instance_addr) {

        this.port = port;
        this.mem_limit = mem_limit;
        this.cf_instance_index = cf_instance_index;
        this.cf_instance_addr = cf_instance_addr;
    }

    @GetMapping("/env")
    public Map<String, String> getEnv() {
        HashMap<String, String> env = new HashMap<>();
        env.put("PORT", port);
        env.put("MEMORY_LIMIT", mem_limit);
        env.put("CF_INSTANCE_INDEX", cf_instance_index);
        env.put("CF_INSTANCE_ADDR", cf_instance_addr);
        return env;
    }
}
