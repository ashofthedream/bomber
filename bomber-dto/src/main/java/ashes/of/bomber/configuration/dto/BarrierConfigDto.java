package ashes.of.bomber.configuration.dto;

public class BarrierConfigDto {
    private boolean enabled;

    public boolean isEnabled() {
        return enabled;
    }

    public BarrierConfigDto setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }
}
