/*
	Gilgamesh Artificial Intelligence Project
    Copyright (C) 2014  Eduardo Alevi

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
    
*/


package org.gilgamesh.core;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Gilgamesh.class})
public class GilgameshDeepAnswerTest
{
	@Test
	public void testDeepAnswer() throws Exception
	{
		// TODO:
		/*
		Core<String> core = new Core<String>();
		
		core.reinforce("cat", "eats", "ration", "tasty");
		core.reinforce("cat", "eats", "ration", "tasty");
		core.reinforce("cat", "eats", "ration", "tasty");
		core.reinforce("cat", "eats", "ration", "tasty");
		core.reinforce("cat", "eats", "ration", "tasty");
		core.reinforce("rat", "eats", "food", "tasty");
		core.reinforce("rat", "eats", "food", "bad");
		core.reinforce("rat", "eats", "food", "bad");
		core.reinforce("rat", "eats", "food", "bad");
		core.reinforce("dog", "eats", "food", "bad");
		core.reinforce("dog", "eats", "food", "tasty");
		core.reinforce("dog", "eats", "food", "tasty");
		core.reinforce("cat", "died");
		core.punish("cat", "eats", "ration", "tasty");
		core.punish("cat", "eats", "ration", "tasty");
		core.punish("rat", "eats", "food", "tasty");
		core.punish("rat", "eats", "food", "bad");

		for(Fact<String> fact : core.getFacts())
			System.out.println(fact);
		
		final Atom<String> values[] = core.convert("eats");
		
		Answer<Fact<String>> answer = core.getDeepAnswer(true, new Predicate<String>() {
			@SuppressWarnings("unchecked")
			public boolean match(Atom<String> ... atoms)
			{
				loop:
				for(Atom<String> value : values)
				{
					for(Atom<String> atom : atoms)
						if(atom.equals(value))
							continue loop;
					return false;
				}

				return true;
			}

			@Override
			public double calc(Fact<String> fact, double force)
			{
				return force / values.length;
			}
		}, (Factor<String>) null, values);
		
		System.out.println();
		System.out.println(answer);
		*/
	}
}


































