package com.windanesz.ancientspellcraft.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WeightedRandom<T extends Object> {

	private class Entry {
		double accumulatedWeight;
		T object;
	}

	public WeightedRandom(Random rand) {
		this.rand = rand;
	}

	private List<Entry> entries = new ArrayList<>();
	private double accumulatedWeight;
	private Random rand = new Random();

	public void addEntry(T object, double weight) {
		accumulatedWeight += weight;
		Entry e = new Entry();
		e.object = object;
		e.accumulatedWeight = accumulatedWeight;
		entries.add(e);
	}

	public T getRandom() {
		double r = rand.nextDouble() * accumulatedWeight;

		for (Entry entry: entries) {
			if (entry.accumulatedWeight >= r) {
				return entry.object;
			}
		}
		return null; //should only happen when there are no entries
	}
}

//package com.windanesz.ancientspellcraft.misc;
//
//import javax.annotation.Nullable;
//import java.util.List;
//import java.util.Random;
//
//public class WeightedRandom {
//
//	public static class Entry {
//		double weight;
//		String object;
//
//		public Entry(String name, double weight) {
//			this.weight = this.weight;
//			this.object = object;
//		}
//	}
//
//	private final List<Entry> entries;
//	private double accumulatedWeight = 0;
//	private final Random rand;
//
//	public WeightedRandom(List<Entry> entries, Random rand) {
//		this.entries = entries;
//		this.rand = rand;
//		entries.forEach(e -> accumulatedWeight += e.weight);
//	}
//
//	@Nullable
//	public String getRandom() {
//		double r = rand.nextDouble() * accumulatedWeight;
//
//		for (Entry entry: entries) {
//			if (entry.weight >= r) {
//				return entry.object;
//			}
//		}
//		return null; //should only happen when there are no entries
//	}
//}