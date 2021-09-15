package ashes.of.bomber.carrier.mappers;

import ashes.of.bomber.carrier.dto.flight.ConfigurationDto;
import ashes.of.bomber.configuration.Configuration;

public class ConfigurationMapper {

    public static Configuration toConfiguration(ConfigurationDto dto) {
        return new Configuration(
                () -> {throw new RuntimeException("Not supported yet"); },
                () -> {throw new RuntimeException("Not supported yet"); },
                null,
                SettingsMapper.toSettingsOrNull(dto.getWarmUp()),
                SettingsMapper.toSettingsOrNull(dto.getSettings())
        );
    }


    public static ConfigurationDto toDto(Configuration config) {
        return new ConfigurationDto()
                .setSettings(SettingsMapper.toDto(config.getSettings()))
                .setWarmUp(SettingsMapper.toDto(config.getWarmUp()));
    }
}
