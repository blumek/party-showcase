package dev.blumek.party.shared;

public record Version(long number) {

    public Version {
        if (number < 0) {
            throw new IllegalArgumentException("Version number cannot be negative");
        }
    }

    public static Version initial() {
        return new Version(0);
    }

    public Version next() {
        return new Version(number + 1);
    }
}
