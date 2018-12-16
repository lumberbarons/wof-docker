package wof.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import wof.rest.model.BundleDTO;
import wof.rest.model.EnvelopeDTO;
import wof.service.BundleService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class BundleController {

    private final BundleService bundleService;

    public BundleController(BundleService bundleService) {
        this.bundleService = bundleService;
    }

    @PostMapping("/bundles")
    @PreAuthorize("hasRole('USER')")
    private ResponseEntity<?> createBundle(@RequestBody BundleDTO bundle) {
        BundleDTO response = bundleService.createBundle(bundle);
        return ResponseEntity.ok(new EnvelopeDTO<>(response));
    }

    @GetMapping("/bundles")
    @PreAuthorize("hasRole('USER')")
    private ResponseEntity<?> getBundles(Pageable pageable) {
        EnvelopeDTO<List<BundleDTO>> response = bundleService.getBundles(pageable);
        return ResponseEntity.ok(response);
    }
}
