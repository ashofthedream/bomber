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
                .setThreads(settings.getThreads())
                .setIterations(settings.getIterations())
                .build();
    }


    public static SettingsDto toDto(Settings settings) {
        if (settings == null)
            return null;

        return new SettingsDto()
                .setDuration(settings.duration().toMillis())
                .setThreads(settings.threads())
                .setIterations(settings.iterations());
    }
}
