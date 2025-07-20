package com.vicgroup.veterinaria.modules.record.controller;

import com.vicgroup.veterinaria.modules.auth.service.AuthService;
import com.vicgroup.veterinaria.modules.pet.service.PetService;
import com.vicgroup.veterinaria.modules.record.dto.request.UpdateAccessLevelRequest;
import com.vicgroup.veterinaria.modules.record.dto.response.HistoricalRecordDto;
import com.vicgroup.veterinaria.modules.record.dto.response.RecordAccessLevelResponse;
import com.vicgroup.veterinaria.modules.record.service.RecordService;
import com.vicgroup.veterinaria.modules.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.Authenticator;
import java.util.List;

@RestController
@RequestMapping("/api/records/")
@RequiredArgsConstructor
public class RecordController {

    private final RecordService recordService;

    @GetMapping("/by-pet/{petId}")
    @PreAuthorize("hasAnyRole('VET', 'PET_OWNER')") // o el rol que necesites
    public ResponseEntity<List<HistoricalRecordDto>> getFullHistory(
            @PathVariable Long petId
    ) {
        List<HistoricalRecordDto> history = recordService.getFullHistoryForPet(petId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/access/{petId}")
    @PreAuthorize("hasRole('PET_OWNER')")
    public List<RecordAccessLevelResponse> getAccessLevels(@PathVariable Long petId,
                                                           @AuthenticationPrincipal User ownerUser) {
        return recordService.getAccessesForPet(petId, ownerUser.getId());
    }

    @PatchMapping("/access/{petId}")
    @PreAuthorize("hasRole('PET_OWNER')")
    public ResponseEntity<Void> updateAccessLevel(@PathVariable Long petId,
                                                  @RequestBody UpdateAccessLevelRequest request,
                                                  @AuthenticationPrincipal User user) {
        recordService.updateAccessLevelForClinic(petId, user.getId(), request);
        return ResponseEntity.ok().build();
    }


}

