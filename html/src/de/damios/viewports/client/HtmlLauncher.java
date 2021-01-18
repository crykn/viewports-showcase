package de.damios.viewports.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;

import de.damios.viewports.ViewportTest;

public class HtmlLauncher extends GwtApplication {

	@Override
	public GwtApplicationConfiguration getConfig() {
		// Resizable application, uses available space in browser
		return new GwtApplicationConfiguration(GwtApplication.isMobileDevice());
		// Fixed size application:
		// return new GwtApplicationConfiguration(480, 320);
	}

	@Override
	public ApplicationListener createApplicationListener() {
		return new ViewportTest();
	}
}