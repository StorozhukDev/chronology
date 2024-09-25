package com.storozhuk.dev.chronology.auth.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class UuidMapperTest {

  private final UuidMapper mapper = Mappers.getMapper(UuidMapper.class);

  @Test
  public void testUuidToString() {
    UUID uuid = UUID.randomUUID();
    String uuidString = mapper.uuidToString(uuid);
    assertEquals(uuid.toString(), uuidString);
  }

  @Test
  public void testUuidToString_Null() {
    String uuidString = mapper.uuidToString(null);
    assertNull(uuidString);
  }
}
