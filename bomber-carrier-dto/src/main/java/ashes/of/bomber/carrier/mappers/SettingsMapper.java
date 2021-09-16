package ashes.of.bomber.carrier.mappers;

import ashes.of.bomber.carrier.dto.flight.SettingsDto;
import ashes.of.bomber.configuration.Settings;
import ashes.of.bomber.configuration.SettingsBuilder;

import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;

public class SettingsMapper {

    @Nullable
    public static Settings toSettingsOrNull(@Nullable SettingsDto settings) {
        if (settings == null)
            return null;

        return new SettingsBuilder()
                .setTime(settings.getDuration(), TimeUnit.MILLISECONDS)
                .setThreadsCount(settings.getThreadsCount())
                .setThreadIterationsCount(settings.getThreadIterationsCount())
                .setTotalIterationsCount(settings.getTotalIterationsCount())
                .build();
    }


    public static SettingsDto toDto(Settings settings) {
        if (settings == null)
            return null;

        return new SettingsDto()
                .setDuration(settings.getDuration().toMillis())
                .setThreadsCount(settings.getThreadsCount())
                .setThreadIterationsCount(settings.getThreadIterationsCount())
                .setTotalIterationsCount(settings.getTotalIterationsCount());
    }
}
