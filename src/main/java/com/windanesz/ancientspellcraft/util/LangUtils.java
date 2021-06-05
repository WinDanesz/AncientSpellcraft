package com.windanesz.ancientspellcraft.util;

public class LangUtils {

	public static String toElderFuthark(String string) {
		return ElderFuthark.getFuthark(string);
	}

	public enum ElderFuthark {

		F("F", "\u16A1", "Feoh"),
		U("U", "\u16A2", "Uruz"),
		V("V", "\u16A2", "Uruz"),
		TH("TH", "\u16A6", "Thurisaz"),
		A("A", "\u16A8", "Ansuz"),
		R("R", "\u16B1", "Raido"),
		K("K", "\u16B2", "Kaunan"),
		C("C", "\u16B2", "Kaunan"),
		G("G", "\u16B7", "Gyfu"),
		W("W", "\u16B9", "Wynn"),
		H("H", "\u16BA", "Haglaz"),
		N("N", "\u16BE", "Naudiz"),
		I("I", "\u16C1", "Isaz"),
		J("J", "\u16C3", "Jera"),
		Y("Y", "\u16C3", "Jera"),
		AE("AE", "\u16C7", "Ihwaz"),
		P("P", "\u16C8", "Peorth"),
		Z("Z", "\u16C9", "Algiz"),
		S("S", "\u16CA", "Sowilo"),
		T("T", "\u16CF", "Tiwaz"),
		B("B", "\u16D2", "Berkanan"),
		E("E", "\u16D6", "Ehwaz"),
		M("M", "\u16D7", "Mannaz"),
		L("L", "\u16DA", "Laguz"),
		Q("Q", "\u16DC", "Yngvi"),
		O("O", "\u16DF", "Odal"),
		D("D", "\u16DE", "Dagaz"),
		COLON(":", "\u1365", ":");

		private final String latin;
		private String uniRune;
		private String runeName;

		ElderFuthark(String latin, String uniRune, String runeName) {
			this.latin = latin;
			this.uniRune = uniRune;
		}

		private static String getUniRuneForChar(String string) {
			for (ElderFuthark f : ElderFuthark.values()) {
				if (f.latin.equals(string.toUpperCase())) {
					return f.uniRune;
				}
			}
			return string;
		}

		public static String getFuthark(String string) {
			StringBuilder target = new StringBuilder();
			for (int i = 0; i < string.length(); i++) {
				target.append(getUniRuneForChar(string.substring(i,i +1)));
			}
			return target.toString();
		}
	}

}
