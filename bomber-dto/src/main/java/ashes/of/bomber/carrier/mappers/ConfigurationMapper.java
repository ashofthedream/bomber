package ashes.of.bomber.carrier.mappers;

import ashes.of.bomber.configuration.Configuration;
import ashes.of.bomber.configuration.dto.ConfigurationDto;
import ashes.of.bomber.squadron.NoBarrier;

public class ConfigurationMapper {

    public static Configuration toConfiguration(ConfigurationDto dto) {
        return new Configuration(
                () -> { throw new RuntimeException("Not supported yet"); },
                () -> { throw new RuntimeException("Not supported yet"); },
                new NoBarrier(),
                SettingsMapper.toSettingsOrNull(dto.getSettings())
        );
    }


    public static ConfigurationDto toDto(Configuration config) {
        return new ConfigurationDto()
                .setSettings(SettingsMapper.toDto(config.settings()));
    }
}
