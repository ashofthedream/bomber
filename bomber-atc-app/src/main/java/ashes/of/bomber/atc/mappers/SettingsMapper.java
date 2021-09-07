package ashes.of.bomber.atc.mappers;

import ashes.of.bomber.carrier.dto.SettingsDto;
import ashes.of.bomber.flight.Settings;
import ashes.of.bomber.flight.SettingsBuilder;

import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;

public class SettingsMapper {

    @Nullable
    public static Settings toSettingsOrNull(@Nullable SettingsDto settings) {
        if (settings == null)
            return null;

        return new SettingsBuilder()
                .setDisabled(settings.isDisabled())
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
                .setDisabled(settings.isDisabled())
                .setDuration(settings.getTime().toMillis())
                .setThreadsCount(settings.getThreadsCount())
                .setThreadIterationsCount(settings.getThreadIterationsCount())
                .setTotalIterationsCount(settings.getTotalIterationsCount());
    }
}
