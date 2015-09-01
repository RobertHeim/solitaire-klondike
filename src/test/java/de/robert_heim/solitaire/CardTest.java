package de.robert_heim.solitaire;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.robert_heim.solitaire.Card.Color;
import de.robert_heim.solitaire.Card.Value;

public class CardTest {

	@Test
	public void testTableau() {

		List<Card> source = new ArrayList<>();
		CardStack targetStack = new CardStack(1, 1);
		targetStack.add(new Card(Value.SEVEN, Color.CLUBS));

		// valid
		source.clear();
		source.add(new Card(Value.SIX, Color.HEART));
		assertTrue(targetStack.isApplyable(source));

		source.clear();
		source.add(new Card(Value.SIX, Color.DIAMONDS));
		assertTrue(targetStack.isApplyable(source));

		source.clear();
		source.add(new Card(Value.SIX, Color.DIAMONDS));
		assertTrue(targetStack.isApplyable(source));

		// bad color

		source.clear();
		source.add(new Card(Value.SIX, Color.CLUBS));
		assertFalse(targetStack.isApplyable(source));

		source.clear();
		source.add(new Card(Value.SIX, Color.SPADES));
		assertFalse(targetStack.isApplyable(source));

		// bad value
		source.clear();
		source.add(new Card(Value.SEVEN, Color.DIAMONDS));
		assertFalse(targetStack.isApplyable(source));

		source.clear();
		source.add(new Card(Value.EIGHT, Color.DIAMONDS));
		assertFalse(targetStack.isApplyable(source));

		source.clear();
		source.add(new Card(Value.ACE, Color.DIAMONDS));
		assertFalse(targetStack.isApplyable(source));

		source.clear();
		source.add(new Card(Value.TWO, Color.DIAMONDS));
		assertFalse(targetStack.isApplyable(source));

		// bad color and bad value
		source.clear();
		source.add(new Card(Value.TWO, Color.SPADES));
		assertFalse(targetStack.isApplyable(source));

	}

	@Test
	public void testFoundation() {

		List<Card> toPut = new ArrayList<>();
		CardStack targetStack = new CardStack(3, 0);

		// valid

		// -- aces
		toPut.add(new Card(Value.ACE, Color.CLUBS));
		assertTrue(targetStack.isApplyable(toPut));

		toPut.clear();
		toPut.add(new Card(Value.ACE, Color.SPADES));
		assertTrue(targetStack.isApplyable(toPut));

		toPut.clear();
		toPut.add(new Card(Value.ACE, Color.HEART));
		assertTrue(targetStack.isApplyable(toPut));

		toPut.clear();
		toPut.add(new Card(Value.ACE, Color.DIAMONDS));
		assertTrue(targetStack.isApplyable(toPut));

		// -- stack
		targetStack.add(new Card(Value.SEVEN, Color.CLUBS));

		toPut.clear();
		toPut.add(new Card(Value.SIX, Color.CLUBS));
		assertTrue(targetStack.isApplyable(toPut));

		// bad color

		toPut.clear();
		toPut.add(new Card(Value.SIX, Color.SPADES));
		assertFalse(targetStack.isApplyable(toPut));

		toPut.clear();
		toPut.add(new Card(Value.SIX, Color.HEART));
		assertFalse(targetStack.isApplyable(toPut));

		toPut.clear();
		toPut.add(new Card(Value.SIX, Color.DIAMONDS));
		assertFalse(targetStack.isApplyable(toPut));

		// bad value

		toPut.clear();
		toPut.add(new Card(Value.FIVE, Color.CLUBS));
		assertFalse(targetStack.isApplyable(toPut));

		toPut.clear();
		toPut.add(new Card(Value.ACE, Color.CLUBS));
		assertFalse(targetStack.isApplyable(toPut));

		toPut.clear();
		toPut.add(new Card(Value.KING, Color.CLUBS));
		assertFalse(targetStack.isApplyable(toPut));

		// bad color and bad value

		toPut.clear();
		toPut.add(new Card(Value.FIVE, Color.SPADES));
		assertFalse(targetStack.isApplyable(toPut));
	}
}
