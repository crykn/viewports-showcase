package de.damios.viewports;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class ViewportTest extends InputAdapter implements ApplicationListener {

	private Array<Viewport> viewports;
	private Array<String> names;
	private Array<String> descriptions;

	private Viewport activeViewport;

	private ShapeRenderer shapeRenderer;
	private Stage stage;
	private Label currentViewportLabel, descriptionLabel;
	private TextButton previousButton, nextButton;

	public void create() {
		stage = new Stage();
		Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
		skin.getFont("default-font").getData().markupEnabled = true;

		currentViewportLabel = new Label("", skin);
		descriptionLabel = new Label("", skin);
		descriptionLabel.setWrap(true);
		previousButton = new TextButton("Previous", skin);
		previousButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				switchToNewViewport(false);
			}
		});
		nextButton = new TextButton("Next", skin);
		nextButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				switchToNewViewport(true);
			}
		});

		Table root = new Table(skin);
		root.setFillParent(true);
		// root.setBackground(skin.getDrawable("default-pane"));
		root.defaults().space(6);
		root.add("[#FFD505]Resize this window to see the effect of the different viewports!").colspan(2).row();
		root.add("Current viewport:");
		root.add(currentViewportLabel).minWidth(140).row();
		root.add(previousButton);
		root.add(nextButton).row();

		Table info = new Table(skin);
		info.defaults().space(6);
		info.add(descriptionLabel).prefWidth(370).prefHeight(70).center().top().row();

		root.add(info).colspan(2).row();

//		ImageButton githubRepoButton = new ImageButton(
//				new TextureRegionDrawable(new TextureRegion(new Texture("github.png"))));
//		githubRepoButton.addListener(new ClickListener() {
//			@Override
//			public void clicked(InputEvent event, float x, float y) {
//				Gdx.net.openURI("https://github.com/libgdx/libgdx/wiki/Viewports");
//			}
//		});
//		githubRepoButton.addListener(new InputListener() {
//			@Override
//			public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
//				Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand);
//			}
//
//			public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
//				Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
//			};
//		});
//
//		githubRepoButton.padLeft(3).padBottom(3).bottom().left();

		stage.addActor(root);
//		stage.addActor(githubRepoButton);

		viewports = getViewports(new OrthographicCamera());
		names = getViewportNames();
		descriptions = getViewportDescriptions();

		activeViewport = viewports.first();

		stage.setViewport(new FitViewport(800, 600, stage.getCamera()));
		currentViewportLabel.setText("[#ADD8E6]" + names.first());
		descriptionLabel.setText("Description: " + descriptions.first());

		Gdx.input.setInputProcessor(new InputMultiplexer(new InputAdapter() {
			public boolean keyDown(int keycode) {
				if (keycode == Input.Keys.RIGHT) {
					switchToNewViewport(true);
					return true;
				} else if (keycode == Input.Keys.LEFT) {
					switchToNewViewport(false);
					return true;
				}
				return false;
			}
		}, stage));

		shapeRenderer = new ShapeRenderer();
	}

	private int WORLD_WIDTH = 4000;
	private int WORLD_HEIGHT = 2000;

	public void render() {
		stage.act();

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		activeViewport.apply();
		shapeRenderer.setProjectionMatrix(activeViewport.getCamera().combined);
		shapeRenderer.begin(ShapeType.Filled);
		/* Background */
		shapeRenderer.setColor(Color.DARK_GRAY);
		shapeRenderer.rect(0, 0, WORLD_WIDTH, WORLD_HEIGHT);
		/* Grid */
		shapeRenderer.setColor(Color.RED);
		for (int i = 0; i < WORLD_WIDTH; i += 200) {
			shapeRenderer.rectLine(i, 0, i, WORLD_HEIGHT, 3);
		}
		for (int i = 0; i < WORLD_HEIGHT; i += 200) {
			shapeRenderer.rectLine(0, i, WORLD_WIDTH, i, 3);
		}
		shapeRenderer.end();

		/* Stage */
		stage.getViewport().apply();
		stage.draw();
	}

	public void resize(int width, int height) {
		activeViewport.update(width, height, true);
		stage.getViewport().update(width, height, true);
	}

	public void dispose() {
		stage.dispose();
	}

	public void switchToNewViewport(boolean forward) {
		int index = (viewports.size + viewports.indexOf(activeViewport, true) + (forward ? 1 : -1)) % viewports.size;
		currentViewportLabel.setText("[#ADD8E6]" + names.get(index));
		descriptionLabel.setText("Description: " + descriptions.get(index));
		activeViewport = viewports.get(index);
		// stage.setViewport(viewport);
		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	static public Array<String> getViewportNames() {
		Array<String> names = new Array<>();
		names.add("FitViewport");
		names.add("StretchViewport");
		names.add("FillViewport");
		names.add("ExtendViewport (no max)");
		names.add("ExtendViewport (max)");
		names.add("ScreenViewport (1:1)");
		names.add("ScreenViewport (0.75:1)");
		names.add("ScalingViewport (mode: none)");
		return names;
	}

	static public Array<String> getViewportDescriptions() {
		Array<String> desc = new Array<>();
		desc.add("scales the world to keep the aspect ratio; adds black bars to the sides");
		desc.add("stretches the world to fit the screen; aspect ratio is not kept");
		desc.add(
				"scales the world to keep the aspect ratio, but will always fill the screen -> parts might be cut off");
		desc.add("scales the world to keep the aspect ratio; then extends in one direction");
		desc.add(
				"scales the world to keep the aspect ratio; then extends in one direction; there is a maximum set of dimensions");
		desc.add("default; the world size matches the window size");
		desc.add("0.75 screen units match 1 screen pixel");
		desc.add("scales the world using different scale modes");
		return desc;
	}

	static public Array<Viewport> getViewports(Camera camera) {
		int minWorldWidth = 800;
		int minWorldHeight = 600;
		int maxWorldWidth = 1000;
		int maxWorldHeight = 600;

		Array<Viewport> viewports = new Array<>();
		viewports.add(new FitViewport(minWorldWidth, minWorldHeight, camera));
		viewports.add(new StretchViewport(minWorldWidth, minWorldHeight, camera));
		viewports.add(new FillViewport(minWorldWidth, minWorldHeight, camera));
		viewports.add(new ExtendViewport(minWorldWidth, minWorldHeight, camera));
		viewports.add(new ExtendViewport(minWorldWidth, minWorldHeight, maxWorldWidth, maxWorldHeight, camera));
		viewports.add(new ScreenViewport(camera));

		ScreenViewport screenViewport = new ScreenViewport(camera);
		screenViewport.setUnitsPerPixel(0.75f);
		viewports.add(screenViewport);

		viewports.add(new ScalingViewport(Scaling.none, minWorldWidth, minWorldHeight, camera));
		return viewports;
	}

	@Override
	public void pause() {
		// unused
	}

	@Override
	public void resume() {
		// unused
	}
}
