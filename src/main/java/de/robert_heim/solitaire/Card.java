package de.robert_heim.solitaire;

import java.util.Optional;

import javafx.scene.Group;

public class Card {
	public static final Card EMPTY = new Card();
	private Value value;
	private Color color;

	private Optional<CardStack> stack = Optional.empty();

	private boolean faceup = false;

	private Card() {
		new CardGroup(this);
		Main.makeDraggable(this);
	}

	public Card(Value value, Color color) {
		this.value = value;
		this.color = color;
	}

	public void setFaceup(boolean faceup) {
		this.faceup = faceup;
	}

	public boolean isFaceup() {
		return faceup;
	}

	public Color getColor() {
		return color;
	}

	public Value getValue() {
		return value;
	}

	public void setStack(Optional<CardStack> stack) {
		this.stack = stack;
	}

	public Optional<CardStack> getStack() {
		return stack;
	}

	public enum Value {
		ACE("A"), TWO("2"), THREE("3"), FOUR("4"), FIVE("5"), SIX("6"), SEVEN(
				"7"), EIGHT("8"), NINE("9"), TEN("10"), JACK("J"), QUEEN("Q"), KING(
				"K");
		public String str;

		private Value(String str) {
			this.str = str;
		}

	}

	public enum Color {
		CLUBS("♣"), SPADES("♠"), HEART("♥"), DIAMONDS("♦");
		public String str;

		private Color(String str) {
			this.str = str;
		}

		public boolean isBlack() {
			return this == CLUBS || this == SPADES;
		}

	}

	@Override
	public String toString() {
		return "Card [" + color + " " + value + " " + (faceup ? "up" : "down")
				+ "]";
	}

	private CardGroup group;

	public CardGroup getGroup() {
		return group;
	}

	public void setGroup(CardGroup group) {
		this.group = group;
	}

	public static class CardGroup extends Group {

		public CardGroup(Card card) {
			this.card = card;
			card.setGroup(this);
		}

		private Card card;

		public Card getCard() {
			return card;
		}

		public void setCard(Card card) {
			this.card = card;
		}
	}

	/**
	 * Checks whether the card is on top of a stack
	 * 
	 * @return true if the card is on top of a stack, false otherwise.
	 */
	public boolean isTop() {
		if (stack.isPresent()) {
			return stack.get().getLast() == this;
		}
		return false;
	}
}
