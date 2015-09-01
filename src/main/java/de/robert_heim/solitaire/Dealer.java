package de.robert_heim.solitaire;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.robert_heim.solitaire.Card.Color;
import de.robert_heim.solitaire.Card.Value;

/**
 * Contains a shuffled decks of cards.
 * 
 * @author Robert
 *
 */
public class Dealer {
	private ArrayList<Card> all = new ArrayList<>();

	public Dealer() {
		for (Color c : Color.values()) {
			List<Value> vals = Arrays.asList(Value.values());
			Collections.reverse(vals);
			for (Value v : vals) {
				all.add(new Card(v, c));
			}
		}
	}

	public int size() {
		return all.size();
	}

	public Dealer shuffle() {
		Collections.shuffle(all);
		return this;
	}

	public static Dealer createSuffldedDeck() {
		return new Dealer().shuffle();
	}

	public List<Card> get(int start, int count) {
		List<Card> re = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			re.add(all.get(start + i));
		}
		return re;
	}
}
