package com.edmebank.clientmanagement.service;

import com.edmebank.clientmanagement.dto.AddressValidationResultDto;
import com.pullenti.address.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class AddressValidationService {
    public AddressValidationResultDto validate(String rawAddress) {
        try {
            TextAddress parsed = AddressService.processSingleAddressText(rawAddress, null);
            if (parsed == null) throw new RuntimeException("Failed to parse address");
            if (parsed.coef < 80) {
                log.warn("Address parsing returned low confidence: {}, {}", parsed.coef, parsed.errorMessage);
            }
            return toResult(parsed);
        } catch (Exception e) {
            log.error("Address parsing failed: {}", e.getMessage(), e);
            throw new RuntimeException("Address parsing failed", e);
        }
    }
    
    private AddressValidationResultDto toResult(TextAddress parsed) throws Exception {
        AddressValidationResultDto result = new AddressValidationResultDto();
        result.setConfidence(parsed.coef);
        result.setError(parsed.errorMessage);
        result.setCountryCode(parsed.alpha2);
        
        Map<String, String> levelMap = new LinkedHashMap<>();
        Map<String, String> paramMap = new LinkedHashMap<>();
        List<String> garGuids = new ArrayList<>();
        
        // isolate deepest GarObject (highest GarLevel)
        GarObject deepest = null;
        for (AddrObject item : parsed.items) {
            if (item.gars != null && !item.gars.isEmpty()) {
                GarObject gar = item.gars.getFirst();
                if (deepest == null || gar.level.value() > deepest.level.value()) {
                    deepest = gar;
                }
            }
        }
        
        // collect levels and params
        for (AddrObject item : parsed.items) {
            if (item.gars == null || item.gars.isEmpty()) continue;
            
            GarObject gar = item.gars.getFirst();
            garGuids.add(gar.guid);
            levelMap.putIfAbsent(gar.level.toString(), gar.toString());
            
            gar.getParams().forEach((param, value) -> {
                if (value != null && !value.isBlank()) {
                    paramMap.putIfAbsent(param.toString(), value);
                }
            });
        }
        
        // enrich hierarchy by walking up from deepest
        if (deepest != null) {
            enrichGarLevelsFromParents(deepest)
                    .forEach(levelMap::putIfAbsent);
        }
        
        String rawFull = parsed.getFullPath(", ", false, AddrLevel.UNDEFINED);
        String zip = paramMap.get("POSTINDEX");
        if (zip != null && !zip.isBlank()) {
            int firstComma = rawFull.indexOf(",");
            if (firstComma != -1) {
                rawFull = rawFull.substring(0, firstComma + 1) + " " + zip + ", " + rawFull.substring(firstComma + 1).trim();
            } else {
                rawFull = rawFull + ", " + zip; // fallback if there's no comma at all
            }
        }
        result.setFullAddress(rawFull);
        
        result.setGarGuids(garGuids);
        result.setGarLevels(levelMap);
        result.setGarParams(paramMap);
        return result;
    }
    
    
    private Map<String, String> enrichGarLevelsFromParents(GarObject start) {
        Map<String, String> levels = new LinkedHashMap<>();
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>(start.parentIds);
        
        while (!queue.isEmpty()) {
            String parentId = queue.poll();
            if (!visited.add(parentId)) continue;
            
            GarObject parent = AddressService.getObject(parentId);
            if (parent == null || parent.level == null) continue;
            
            levels.putIfAbsent(parent.level.toString(), parent.toString());
            
            if (parent.parentIds != null) {
                queue.addAll(parent.parentIds);
            }
        }
        
        return levels;
    }
}
