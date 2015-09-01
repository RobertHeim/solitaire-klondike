package de.robert_heim.solitaire;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javafx.scene.Group;

import com.google.common.collect.ImmutableList;

import de.robert_heim.solitaire.Card.Value;

public class CardStack extends LinkedList<Card> {

	private static final long serialVersionUID = 1L;

	private int x = 0;
	private int y = 0;

	public CardStack(int x, int y) {
		super();
		this.group = new Group();
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public boolean isWaste() {
		return 1 == x && 0 == y;
	}

	public boolean isStock() {
		return 0 == x && 0 == y;
	}

	public boolean isFoundation() {
		return 3 <= x && 0 == y;
	}

	public boolean isTableau() {
		return 1 == y;
	}

	public List<Card> getRestAfter(Card card) {
		int start = this.indexOf(card) + 1;
		List<Card> re = new ArrayList<>();
		for (int i = start; i < size(); i++) {
			re.add(get(i));
		}
		return ImmutableList.copyOf(re);
	}

	/**
	 * Checks whether source can be put on target by meanings of a tableau.
	 * 
	 * @param source
	 * @param target
	 * @return true if is applyable, false otherwise.
	 */
	public boolean isApplyable(List<Card> toPut) {
		if (toPut.isEmpty()) {
			return false;
		}
		Card source = toPut.get(0);
		// tableau
		if (isTableau()) {
			if (isEmpty()) {
				return source.getValue().equals(Value.KING);
			}
			Card target = getLast();
			if (source.getColor().isBlack() == target.getColor().isBlack()) {
				return false;
			}
			// source value must be exactly one smaller than target
			if (-1 == source.getValue().compareTo(target.getValue())) {
				return true;
			}
		}
		// foundation
		else if (isFoundation()) {
			if (toPut.size() > 1) {
				return false;
			}
			if (isEmpty()) {
				return source.getValue().equals(Value.ACE);
			}
			Card target = getLast();
			if (target.getColor().equals(source.getColor())) {
				if (1 == source.getValue().compareTo(target.getValue())) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		CardStack other = (CardStack) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

	@Override
	public String toString() {
		Card last = isEmpty() ? null : getLast();
		if (null != last && last.getStack().get() != this) {
			System.err.println("last of stack " + "[x=" + x + ", y=" + y
					+ ", size=" + size() + ", top=" + last + "]"
					+ " has not me as stack, but " + last.getStack().get());
		}
		return "CardStack [x=" + x + ", y=" + y + ", size=" + size() + ", top="
				+ last + "]";
	}

	private Group group;

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	private Group container;

	public void setContainer(Group container) {
		this.container = container;
	}

	public Group getContainer() {
		return container;
	}
}
