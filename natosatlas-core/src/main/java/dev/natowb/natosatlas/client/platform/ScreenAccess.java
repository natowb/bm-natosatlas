package dev.natowb.natosatlas.client.platform;

import dev.natowb.natosatlas.client.ui.elements.UIScreen;

public abstract class ScreenAccess {

    public abstract void openNacScreen(UIScreen screen);

    public abstract void playSound(String sound, float volume, float pitch);
}
