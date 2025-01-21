package com.astroverse.backend.service;

import com.astroverse.backend.model.Preference;
import com.astroverse.backend.repository.PreferenceRepository;
import org.springframework.stereotype.Service;

@Service
public class PreferenceService {
    private final PreferenceRepository preferenceRepository;

    public PreferenceService(PreferenceRepository preferenceRepository) {
        this.preferenceRepository = preferenceRepository;
    }

    public Preference savePreference(Preference preference) {
        return preferenceRepository.save(preference);
    }
}
