package com.isoview;

import com.isoview.core.BaseController;
import com.isoview.core.EngineLoopStats;
import com.isoview.core.Mouse;
import com.isoview.datastructures.Vector2D;
import com.isoview.datastructures.map.IsometricMap;
import com.isoview.datastructures.tile.Tile;
import com.isoview.scripting.ScriptObject;
import com.isoview.scripting.Scripting;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.Effect;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Controller extends BaseController {
	private Vector2D<Integer> tileSize;
	private IsometricMap map;
	private Tile[] tiles;

	private BufferedImage selectedTile;
	private BufferedImage cheatingTile;

	private boolean showGrid;

	@FXML
	private Pane root;
	@FXML
	private Label screenPosLabel;
	@FXML
	private Label worldPosLabel;
	@FXML
	private Label selectedMapCellPosLabel;
	@FXML
	private Label posOffsetInSelectedCellLabel;
	@FXML
	private Label fpsLabel;
	@FXML
	private VBox tileOptionsContainer;

	private Vector2D<Integer> screenTile;
	private Vector2D<Integer> offsetInScreenTile;
	private Vector2D<Integer> mapTile;

	private AtomicInteger selectedOption;

	@Override
	protected void onCreated() throws Exception {
		this.screenTile = new Vector2D<>(0, 0);
		this.offsetInScreenTile = new Vector2D<>(0, 0);
		this.mapTile = new Vector2D<>(0, 0);

		this.selectedOption = new AtomicInteger(0);

		this.showGrid = false;

		ScriptObject config = Scripting.runScript(getClass().getResource("/scripts/config.editor.js"));
		ScriptObject tilesetConfig = config.get("tilesets");
		ScriptObject mapConfig = config.get("map");

		this.tileSize = new Vector2D<>(
				tilesetConfig.get(0).get("metadata").get("tile_size").getAsInteger("width"),
				tilesetConfig.get(0).get("metadata").get("tile_size").getAsInteger("height")
		);

		this.map = new IsometricMap(
				new Vector2D<>(
						mapConfig.get("metadata").get("size").getAsInteger("width"),
						mapConfig.get("metadata").get("size").getAsInteger("height")
				),
				new Vector2D<>(
						mapConfig.get("metadata").get("origin").getAsInteger("x"),
						mapConfig.get("metadata").get("origin").getAsInteger("y")
				)
		);

		BufferedImage serviceTileset = ImageIO.read(getClass().getResource(tilesetConfig.get(0).get("metadata").getAsString("path")));
		BufferedImage tileset = ImageIO.read(getClass().getResource(tilesetConfig.get(1).get("metadata").getAsString("path")));

		ScriptObject serviceTilesetContent = tilesetConfig.get(0).get("content");
		ScriptObject tilesetContent = tilesetConfig.get(1).get("content");

		ScriptObject tileConfig = serviceTilesetContent.get(0);
		this.selectedTile = getTile(serviceTileset, tileConfig.getAsInteger("x"), tileConfig.getAsInteger("y"));

		tileConfig = serviceTilesetContent.get(1);
		this.cheatingTile = getTile(serviceTileset, tileConfig.getAsInteger("x"), tileConfig.getAsInteger("y"));

		this.tiles = new Tile[tilesetContent.size()];
		for (int i = 0; i < this.tiles.length; i++) {
			tileConfig = tilesetContent.get(i);
			this.tiles[i] = new Tile(
					tileConfig.getAsString("name"),
					getTile(tileset, tileConfig.getAsInteger("x"), tileConfig.getAsInteger("y"))
			);
		}

		ToggleGroup group = new ToggleGroup();
		for (int i = 0; i < this.tiles.length; i++) {
			Tile option = this.tiles[i];

			ToggleButton toggle = new ToggleButton(
					null,
					new ImageView(
							SwingFXUtils.toFXImage(
									(BufferedImage) option.getTile(),
									null
							)
					)
			);
			toggle.setToggleGroup(group);
			toggle.setUserData(i);
			toggle.setOnAction(event -> {
				event.consume();
				this.selectedOption.set((Integer) toggle.getUserData());
			});

			this.tileOptionsContainer.getChildren().add(toggle);

			if (i == 0) {
				toggle.setSelected(true);
			}
		}
	}

	@Override
	protected void onUpdate() {
		// find out what tile on screen is under the mouse cursor
		this.screenTile.setX(Mouse.xScreen / this.tileSize.getX());
		this.screenTile.setY(Mouse.yScreen / this.tileSize.getY());

		// find out what mouse offset is inside of the active screen
		this.offsetInScreenTile.setX(Mouse.xScreen % this.tileSize.getX());
		this.offsetInScreenTile.setY(Mouse.yScreen % this.tileSize.getY());

		// convert screen tile position to map tile position
		this.mapTile.setX((this.screenTile.getY() - this.map.getOrigin().getY()) + (this.screenTile.getX() - this.map.getOrigin().getX()));
		this.mapTile.setY((this.screenTile.getY() - this.map.getOrigin().getY()) - (this.screenTile.getX() - this.map.getOrigin().getX()));

		int argb = this.cheatingTile.getRGB(this.offsetInScreenTile.getX(), this.offsetInScreenTile.getY());
		if ((argb ^ 0xffff0000) == 0) {
			this.mapTile.setX(this.mapTile.getX() - 1);
		} else if ((argb ^ 0xff00ff00) == 0) {
			this.mapTile.setY(this.mapTile.getY() - 1);
		} else if ((argb ^ 0xff0000ff) == 0) {
			this.mapTile.setX(this.mapTile.getX() + 1);
		} else if ((argb ^ 0xffffff00) == 0) {
			this.mapTile.setY(this.mapTile.getY() + 1);
		}

		this.worldPosLabel.setText(String.format("%s, %s", this.mapTile.getX(), this.mapTile.getY()));
		this.screenPosLabel.setText(String.format("%s, %s", Mouse.xScreen, Mouse.yScreen));
		this.selectedMapCellPosLabel.setText(String.format("%s, %s", this.screenTile.getX(), this.screenTile.getY()));
		this.posOffsetInSelectedCellLabel.setText(String.format("%s, %s", this.offsetInScreenTile.getX(), this.offsetInScreenTile.getY()));
		this.fpsLabel.setText(String.format("%s", EngineLoopStats.fps));
	}

	@Override
	protected void onRender() {
		for (int y = 0, h = this.map.getHeight(); y < h; y++) {
			for (int x = 0, w = this.map.getWidth(); x < w; x++) {
				// convert map tile pos to screen tile pos
				Vector2D<Integer> screenTilePos = toScreen(x, y);

				drawTile(screenTilePos.getX(), screenTilePos.getY(), this.tiles[this.map.getTileAt(x, y)].getTile());
			}
		}

		if (this.showGrid) {
			drawGridLines();
		}

		highlightCurrentMapTile();
	}

	@Override
	protected void onEvent() {
		if (Mouse.isLeftButtonPressed) {
			if (this.mapTile.getX() < 0 || this.mapTile.getX() > this.map.getWidth()
					|| this.mapTile.getY() < 0 || this.mapTile.getY() > this.map.getHeight()) {
				return;
			}

			this.map.setTileAt(
					this.mapTile.getX(),
					this.mapTile.getY(),
					this.selectedOption.get()
			);
		}
	}

	private Vector2D<Integer> toScreen(int cx, int cy) {
		return new Vector2D<>(
				(this.map.getOrigin().getX() * this.tileSize.getX()) + (cx - cy) * (this.tileSize.getX() / 2),
				(this.map.getOrigin().getY() * this.tileSize.getY()) + (cx + cy) * (this.tileSize.getY() / 2)
		);
	}

	@FXML
	private void toggleGrid(ActionEvent event) {
		this.showGrid = !this.showGrid;
	}

	private void drawGridLines() {
		Color initialColor = getGraphics().getColor();
		getGraphics().setColor(Color.GRAY);

		for (int y = 0, oy = getRaster().getHeight() / this.map.getHeight(); y < this.map.getHeight(); y++) {
			getGraphics().drawLine(0, y * oy, getRaster().getWidth(), y * oy);
		}

		for (int x = 0, ox = getRaster().getWidth() / this.map.getWidth(); x < this.map.getWidth(); x++) {
			getGraphics().drawLine(x * ox, 0, x * ox, getRaster().getHeight());
		}

		getGraphics().setColor(initialColor);
	}

	private void highlightCurrentMapTile() {
		Vector2D<Integer> screenPos = toScreen(this.mapTile.getX(), this.mapTile.getY());

		drawTile(screenPos.getX(), screenPos.getY(), this.selectedTile);
	}

	private void drawTile(int sx, int sy, Image tile) {
		getGraphics().drawImage(
				tile,
				sx,
				sy,
				sx + tile.getWidth(null),
				sy + tile.getHeight(null),
				0,
				0,
				tile.getWidth(null),
				tile.getHeight(null),
				null
		);
	}

	private BufferedImage getTile(BufferedImage sprites, int x, int y) {
		return sprites.getSubimage(x * this.tileSize.getX(), y * this.tileSize.getY(), this.tileSize.getX(), this.tileSize.getY());
	}

	public void saveAs(ActionEvent event) throws IOException {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialFileName("New_Level.ivl");
		File file = fileChooser.showSaveDialog(new Stage().getOwner());

		if (file == null) {
			return;
		}

		if (!file.exists()) {
			file.createNewFile();
		}

		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
		oos.writeObject(this.map);
		oos.flush();
		oos.close();
	}

	public void open(ActionEvent event) throws IOException, ClassNotFoundException {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("IsoLevel", "*.ilv"));
		File file = fileChooser.showOpenDialog(new Stage().getOwner());

		if (file == null) {
			return;
		}

		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
		this.map = (IsometricMap) ois.readObject();
		ois.close();
	}

	public void about(ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("/about.fxml"));
		Window parent = this.root.getScene().getWindow();
		Stage stage = new Stage();
		stage.setTitle("About");
		stage.setAlwaysOnTop(true);
		stage.setResizable(false);
		stage.setScene(new Scene(root));
		stage.setX(parent.getX() + parent.getWidth() / 2 - parent.getWidth() / 4);
		stage.setY(parent.getY() + parent.getHeight() / 2 - parent.getHeight() / 4);
		stage.initModality(Modality.WINDOW_MODAL);
		stage.initOwner(parent);
		stage.show();
		root.autosize();

		parent.getScene().getRoot().setEffect(new BoxBlur());

		stage.setOnCloseRequest(e -> {
			parent.getScene().getRoot().setEffect(null);
		});
	}
}
