package de.robert_heim.solitaire;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import de.robert_heim.solitaire.Card.CardGroup;

public class CardDrawer {

	public static final Paint CARD_BACK = Paint
			.valueOf("linear-gradient(from 25% 25% to 100% 100%, #dc143c, #661a33)");
	public static final Paint GAME_BACKGROUND = Paint
			.valueOf("linear-gradient(from 25% 25% to 100% 100%, #007700, #005500)");;

	public static final double CORNER_SIZE = 30;
	public static final double SPACER = 50;
	public static final double WIDTH = CORNER_SIZE * 4;
	public static final double HEIGHT = CORNER_SIZE * 5.6;

	public enum Pos {
		LEFT_TOP(0, 0), LEFT_BOTTOM(0, 1), RIGHT_TOP(1, 0), RIGHT_BOTTOM(1, 1);
		public final double x;
		public final double y;
		public final int logicPosX;
		public final int logicPosY;

		private Pos(int x, int y) {
			this.x = CORNER_SIZE / 2 + x * (WIDTH - CORNER_SIZE);
			this.y = CORNER_SIZE / 2 + y * (HEIGHT - CORNER_SIZE);
			this.logicPosX = x;
			this.logicPosY = y;
		}
	}

	public static void drawCard(Card card) {
		double yOffset = calcYOffset(card);

		CardGroup cardGroup = new CardGroup(card);
		cardGroup.setLayoutY(yOffset);

		Rectangle rec = new Rectangle();
		rec.setWidth(WIDTH);
		rec.setHeight(HEIGHT);
		rec.setFill(CARD_BACK);
		rec.setStroke(Color.BLACK);
		cardGroup.getChildren().add(rec);

		if (card.isFaceup()) {
			rec.setFill(Color.WHITE);

			// middle Text
			Font middleFont = new Font(12 * 3);
			Text textMiddle = new Text(card.getValue().str + "\n"
					+ card.getColor().str);
			textMiddle.setFont(middleFont);
			textMiddle.setTextAlignment(TextAlignment.CENTER);
			textMiddle.setFill(card.getColor().isBlack() ? Color.BLACK
					: Color.RED);
			textMiddle.relocate(WIDTH / 2
					- textMiddle.getBoundsInLocal().getWidth() / 2, HEIGHT / 2
					- textMiddle.getBoundsInLocal().getHeight() / 2);
			card.getGroup().getChildren().add(textMiddle);

			// corners
			for (Pos pos : Pos.values()) {
				Group corner = new Group();

				corner.relocate(pos.logicPosX * (WIDTH - CORNER_SIZE),
						pos.logicPosY * (HEIGHT - CORNER_SIZE));

				Text cornerText = new Text(card.getColor().str + " "
						+ card.getValue().str);
				cornerText
						.relocate(CORNER_SIZE / 2
								- cornerText.getBoundsInLocal().getWidth() / 2,
								CORNER_SIZE
										/ 2
										- cornerText.getBoundsInLocal()
												.getHeight() / 2);
				cornerText.setFill(card.getColor().isBlack() ? Color.BLACK
						: Color.RED);

				corner.getChildren().add(cornerText);
				card.getGroup().getChildren().add(corner);
			}
		}
	}

	private static void drawEmpty(Group container) {
		Rectangle r = new Rectangle(0, 0, WIDTH, HEIGHT);
		r.setFill(Color.TRANSPARENT);
		r.setStroke(Color.BLACK);
		container.getChildren().add(r);
	}

	public static void redrawStack(CardStack cardStack) {
		Group cardStackGroup = cardStack.getGroup();

		// auto faceup last of tableaus
		if (cardStack.isTableau() && !cardStack.isEmpty()) {
			if (!cardStack.getLast().isFaceup()) {
				cardStack.getLast().setFaceup(true);
			}
		}
		cardStackGroup.getChildren().clear();
		Group cardStackGroupParent = cardStack.getContainer();
		cardStackGroup.toBack();
		cardStackGroupParent.getChildren().remove(cardStackGroup);
		cardStack.setGroup(new Group());
		drawStack(cardStack, cardStackGroupParent);
		cardStack.getGroup().toBack();
	}

	public static void drawStack(CardStack stack, Group container) {
		Group cardStackGroup = new Group();
		stack.setGroup(cardStackGroup);
		stack.setContainer(container);
		double x = (1 + stack.getX()) * SPACER + stack.getX() * WIDTH;
		double y = (1 + stack.getY()) * SPACER + stack.getY() * HEIGHT;
		cardStackGroup.relocate(x, y);
		drawEmpty(cardStackGroup);
		for (Card c : stack) {
			drawCard(c);
			cardStackGroup.getChildren().add(c.getGroup());
			if (c.isFaceup()) {
				Main.makeDraggable(c);
			}
		}
		container.getChildren().add(cardStackGroup);
		if (stack.isTableau() || stack.isFoundation()) {
			Main.makeTarget(stack);
		}
	}

	private static double calcYOffset(Card card) {
		if (!card.getStack().isPresent()) {
			return 0;
		}
		CardStack stack = card.getStack().get();
		if (!stack.isTableau()) {
			return 0;
		}
		return stack.indexOf(card) * SPACER / 2;
	}

}
