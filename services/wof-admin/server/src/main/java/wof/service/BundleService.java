package wof.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import wof.repository.BundleRepository;
import wof.repository.model.BundleEntity;
import wof.exception.AlreadyExistsException;
import wof.rest.model.BundleDTO;
import wof.rest.model.EnvelopeDTO;
import wof.rest.model.mapper.ModelMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BundleService {

    private final ModelMapper modelMapper;
    private final BundleRepository bundleRepository;

    public BundleService(BundleRepository bundleRepository, ModelMapper modelMapper) {
        this.bundleRepository = bundleRepository;
        this.modelMapper = modelMapper;
    }

    public BundleDTO createBundle(BundleDTO bundle) {
        if(bundleRepository.existsById(bundle.getId())) {
            throw new AlreadyExistsException("Bundle already exists!");
        }

        BundleEntity response = bundleRepository.save(modelMapper.map(bundle));

        return modelMapper.map(response);
    }

    public EnvelopeDTO<List<BundleDTO>> getBundles(Pageable pageable) {
        Page<BundleEntity> page = bundleRepository.findAll(pageable);
        List<BundleDTO> bundles = page.get().map(modelMapper::map).collect(Collectors.toList());
        return new EnvelopeDTO<List<BundleDTO>>(bundles, page);
    }
}
