package it.hurts.sskirillss.rbocompat.client;

public interface IScrollingScreen {
    default String message() {
        return "";
    }

    default int width() {
        return 0;
    }

    default int height() {
        return 0;
    }

    default int x() {
        return 0;
    }

    default int y() {
        return 0;
    }
}
