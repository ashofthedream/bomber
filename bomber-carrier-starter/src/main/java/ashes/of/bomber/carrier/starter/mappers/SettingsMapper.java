package ashes.of.bomber.carrier.starter.mappers;

import ashes.of.bomber.carrier.dto.SettingsDto;
import ashes.of.bomber.flight.Settings;

public class SettingsMapper {

    public static SettingsDto toDto(Settings settings) {
        return new SettingsDto()
                .setDisabled(settings.isDisabled())
                .setDuration(settings.getTime().toMillis())
                .setThreadsCount(settings.getThreadsCount())
                .setThreadIterationsCount(settings.getThreadIterationsCount())
                .setTotalIterationsCount(settings.getTotalIterationsCount());
    }
}
