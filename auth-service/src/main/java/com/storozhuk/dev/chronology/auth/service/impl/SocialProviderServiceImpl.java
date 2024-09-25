package com.storozhuk.dev.chronology.auth.service.impl;

import com.storozhuk.dev.chronology.auth.entity.SocialProviderEntity;
import com.storozhuk.dev.chronology.auth.entity.UserEntity;
import com.storozhuk.dev.chronology.auth.repository.SocialProviderRepository;
import com.storozhuk.dev.chronology.auth.service.SocialProviderService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SocialProviderServiceImpl implements SocialProviderService {
  private final SocialProviderRepository socialProviderRepository;

  @Override
  public Optional<SocialProviderEntity> findBySocial(String provider, String providerUserId) {
    return socialProviderRepository.findByProviderAndProviderUserId(provider, providerUserId);
  }

  @Override
  public SocialProviderEntity createSocial(
      String provider, String providerUserId, UserEntity user) {
    SocialProviderEntity providerEntity = new SocialProviderEntity();
    providerEntity.setProvider(provider);
    providerEntity.setProviderUserId(providerUserId);
    providerEntity.setUser(user);
    return socialProviderRepository.save(providerEntity);
  }
}
