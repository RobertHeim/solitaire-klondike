package de.robert_heim.solitaire;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.DataFormat;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import com.google.common.collect.ImmutableList;

import de.robert_heim.solitaire.Card.CardGroup;

public class Main extends Application {

	public static MenuItem menuNew = new MenuItem("New");

	public static final double SCENE_WIDTH = CardDrawer.WIDTH * 7
			+ CardDrawer.SPACER * 8;
	public static final double SCENE_HEIGHT = CardDrawer.HEIGHT * 3
			+ CardDrawer.SPACER * 3;
	private static List<Card> inDrag = new ArrayList<>();

	public static CardStack stock = new CardStack(0, 0);

	public static boolean running = false;
	public static CardStack waste = new CardStack(1, 0);

	private static Collection<CardStack> stacks = Arrays.asList(
			stock,
			waste,
			// foundations
			new CardStack(3, 0), new CardStack(4, 0),
			new CardStack(5, 0),
			new CardStack(6, 0),
			// tableaus
			new CardStack(0, 1), new CardStack(1, 1), new CardStack(2, 1),
			new CardStack(3, 1), new CardStack(4, 1), new CardStack(5, 1),
			new CardStack(6, 1));

	private static List<CardStack> foundations = stacks.stream()
			.filter(s -> s.isFoundation()).collect(Collectors.toList());

	private static List<CardStack> tableaus = stacks.stream()
			.filter(s -> s.isTableau()).collect(Collectors.toList());

	private static Optional<LocalDateTime> startTime = Optional.empty();
	private static Optional<LocalDateTime> endTime = Optional.empty();

	@Override
	public void start(Stage primaryStage) throws Exception {

		double width = SCENE_WIDTH;
		double height = SCENE_HEIGHT;

		BorderPane rootBorderPane = new BorderPane();
		Group gameArea = new Group();

		rootBorderPane.setCenter(gameArea);
		StackPane parentGroup = new StackPane();
		parentGroup.relocate(0, 0);

		rootBorderPane.setBackground(new Background(new BackgroundFill(
				CardDrawer.GAME_BACKGROUND, CornerRadii.EMPTY, Insets.EMPTY)));

		MenuBar menuBar = new MenuBar();

		// Menu

		MenuItem menuUndo = new MenuItem("Undo");
		MenuItem menuRedo = new MenuItem("Redo");

		menuNew.setOnAction(e -> deal(gameArea));
		MenuItem menuQuit = new MenuItem("Quit");
		menuQuit.setOnAction(e -> Platform.exit());

		Menu menuFile = new Menu("File");
		menuFile.getItems().addAll(menuNew, new SeparatorMenuItem(), menuQuit);

		Menu menuEdit = new Menu("Edit");
		menuEdit.getItems().addAll(menuUndo, menuRedo);

		menuBar.getMenus().addAll(menuFile);//, menuEdit);

		rootBorderPane.setTop(menuBar);

		// footer

		HBox statusBar = new HBox();
		statusBar.setBackground(new Background(new BackgroundFill(
				Color.LIGHTGREY, CornerRadii.EMPTY, Insets.EMPTY)));
		Text durationStatusText = new Text("Dauer:");
		statusBar.getChildren().add(durationStatusText);

		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				Platform.runLater(() -> durationStatusText.setText("Dauer: "
						+ calcDurationAndFormat()));
			}
		}, 0, 1000);

		rootBorderPane.setBottom(statusBar);

		gameArea.getChildren().add(parentGroup);

		menuNew.fire();

		Scene scene = new Scene(rootBorderPane, width, height);

		primaryStage.setTitle("Solitaire");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		new DataFormat("card");
		Main.launch(args);
	}

	public void deal(Group parentGroup) {
		running = true;
		startTime = Optional.of(LocalDateTime.now());
		endTime = Optional.empty();
		parentGroup.getChildren().clear();
		stacks.forEach(s -> s.clear());
		Dealer dealer = Dealer.createSuffldedDeck();
		int current = 0;
		for (CardStack tableau : tableaus) {
			int countToDraw = tableau.getX() + 1;
			tableau.addAll(dealer.get(current, countToDraw));
			tableau.getLast().setFaceup(true);
			current += countToDraw;
		}

		stock.addAll(dealer.get(current, dealer.size() - current));

		// set link from card to its stack
		stacks.forEach(s -> s.forEach(c -> c.setStack(Optional.of(s))));

		tableaus.forEach(t -> CardDrawer.drawStack(t, parentGroup));
		foundations.forEach(f -> CardDrawer.drawStack(f, parentGroup));
		CardDrawer.drawStock(stock, parentGroup);
		CardDrawer.drawWaste(waste, parentGroup);
	}

	public static class MouseLocation {
		public double x;
		public double y;
	}

	public static void moveCards(List<Card> cards, CardStack targetStack) {
		if (cards.isEmpty())
			return;

		CardStack sourceStack = cards.get(0).getStack().get();
		for (Card card : cards) {
			System.out.println("removing from " + sourceStack + " put on "
					+ targetStack);
			sourceStack.remove(card);
			targetStack.add(card);
			CardGroup cardGroup = new CardGroup(card);
			targetStack.getGroup().getChildren().add(cardGroup);
			makeDraggable(card);
			card.setStack(Optional.of(targetStack));
		}
		CardDrawer.redrawStack(sourceStack);
		CardDrawer.redrawStack(targetStack);

		checkFinish();
	}

	public static String calcDurationAndFormat() {
		Duration duration = (Duration.between(
				startTime.orElse(LocalDateTime.now()),
				endTime.orElse(LocalDateTime.now())));
		long minutes = duration.toMinutes();
		long totalSeconds = duration.getSeconds();
		long points = 1000 - totalSeconds;
		if (points<0) {
			points = 0;
		}
		long seconds = duration.minusMinutes(minutes).getSeconds();
		return minutes + " Minute" + (minutes != 1 ? "n" : "") + ", " + seconds
				+ " Sekunde" + (seconds != 1 ? "n" : "") + ",  \tPunkte: " + points;
	}

	public static void checkFinish() {
		boolean foundationsFull = foundations.stream().map(f -> f.size())
				.allMatch(s -> 13 == s);
		if (foundationsFull) {
			running = false;
			endTime = Optional.of(LocalDateTime.now());
			Stage dialogStage = new Stage();
			dialogStage.initModality(Modality.WINDOW_MODAL);

			Alert alert = new Alert(AlertType.NONE,
					"Du hast gewonnen!\n\nZeit: " + calcDurationAndFormat()
							+ "\n\nNeu austeilen?", ButtonType.OK,
					ButtonType.CLOSE);
			alert.showAndWait().filter(response -> response == ButtonType.OK)
					.ifPresent(response -> menuNew.fire());
		}
	}

	public static void makeTarget(final CardStack cardStack) {
		Group source = cardStack.getGroup();
		source.setOnMouseDragReleased((final MouseDragEvent e) -> {
			if (!inDrag.isEmpty()) {
				CardStack targetStack = cardStack;
				Card sourceCard = inDrag.get(0);
				CardStack sourceStack = sourceCard.getStack().get();
				if (targetStack != sourceStack) {
					if (targetStack.isApplyable(ImmutableList.copyOf(inDrag))) {
						moveCards(ImmutableList.copyOf(inDrag), targetStack);
						inDrag.clear();
					}
				}
				e.consume();
			}
		});
	}

	public static void makeDraggable(final Card card) {
		final MouseLocation lastMouseLocation = new MouseLocation();

		CardGroup source = card.getGroup();
		if (card != Card.EMPTY) {
			source.setOnDragDetected((final MouseEvent e) -> {
				source.startFullDrag();
				e.consume();
			});
			source.setOnMousePressed(e -> {
				if (2 == e.getClickCount()) {
					Card sourceCard = source.getCard();
					if (sourceCard.isTop()) {
						List<Card> toPut = new ArrayList<>();
						toPut.add(sourceCard);
						// try bring to foundation
						for (CardStack f : foundations) {
							if (f.isApplyable(toPut)) {
								moveCards(toPut, f);
								break;
							}
						}
					}
				} else {
					Card sourceCard = source.getCard();
					CardStack sourceStack = sourceCard.getStack().get();
					lastMouseLocation.x = e.getSceneX();
					lastMouseLocation.y = e.getSceneY();

					inDrag.add(sourceCard);
					if (sourceCard.getStack().get().isTableau()) {
						sourceStack.getRestAfter(sourceCard).forEach(c -> {
							inDrag.add(c);
						});
					}
					source.setCursor(Cursor.CLOSED_HAND);
					source.setMouseTransparent(true);
					sourceStack.getGroup().toFront();
					System.out.println("pressed" + sourceCard);
				}
				e.consume();
			});
			source.setOnMouseDragged(mouseEvent -> {
				double deltaX = mouseEvent.getSceneX() - lastMouseLocation.x;
				double deltaY = mouseEvent.getSceneY() - lastMouseLocation.y;
				for (Card c : inDrag) {
					double newX = c.getGroup().getLayoutX() + deltaX;
					double newY = c.getGroup().getLayoutY() + deltaY;
					c.getGroup().setLayoutX(newX);
					c.getGroup().setLayoutY(newY);
					lastMouseLocation.x = mouseEvent.getSceneX();
					lastMouseLocation.y = mouseEvent.getSceneY();
				}
				mouseEvent.consume();
			});
			source.setOnMouseEntered(e -> {
				source.setCursor(Cursor.HAND);
			});
			source.setOnMouseReleased(e -> {
				source.setMouseTransparent(false);
				CardStack sourceStack = source.getCard().getStack().get();
				sourceStack.getGroup().toBack();
				if (!inDrag.isEmpty()) {
					Card c = inDrag.get(0);
					inDrag.clear();
					CardDrawer.redrawStack(c.getStack().get());
				}
				e.consume();
			});
		}
	}
}
