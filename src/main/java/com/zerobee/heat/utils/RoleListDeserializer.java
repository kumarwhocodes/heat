package com.zerobee.heat.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobee.heat.dto.RoleDTO;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@JsonComponent
public class RoleListDeserializer extends JsonDeserializer<Set<RoleDTO>> {
    
    @Override
    public Set<RoleDTO> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        JsonNode node = mapper.readTree(p);
        Set<RoleDTO> roles = new HashSet<>();
        
        if (node.isArray()) {
            for (JsonNode element : node) {
                if (element.isTextual()) {
                    // Handle string roles (like ["USER", "ADMIN"])
                    roles.add(RoleDTO.fromString(element.asText()));
                } else if (element.isObject()) {
                    // Handle object roles (like [{"name": "USER"}, {"name": "ADMIN"}])
                    RoleDTO roleDTO = new RoleDTO();
                    
                    if (element.has("name")) {
                        roleDTO.setName(element.get("name").asText());
                    }
                    
                    if (element.has("id")) {
                        roleDTO.setId(String.valueOf(element.get("id").asInt()));
                    }
                    
                    roles.add(roleDTO);
                }
            }
        }
        
        return roles;
    }
}