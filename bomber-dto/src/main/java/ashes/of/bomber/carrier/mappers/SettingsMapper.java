package ashes.of.bomber.carrier.mappers;

import ashes.of.bomber.configuration.dto.SettingsDto;
import ashes.of.bomber.configuration.Settings;

import javax.annotation.Nullable;
import java.time.Duration;

public class SettingsMapper {

    @Nullable
    public static Settings toSettingsOrNull(@Nullable SettingsDto settings) {
        if (settings == null)
            return null;

        return new Settings(
                Duration.ofMillis(settings.getTime()),
                settings.getThreads(),
                settings.getIterations()
        );
    }


    public static SettingsDto toDto(Settings settings) {
        if (settings == null)
            return null;

        return new SettingsDto()
                .setTime(settings.duration().toMillis())
                .setThreads(settings.threads())
                .setIterations(settings.iterations());
    }
}
