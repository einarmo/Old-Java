import java.awt.Color;

public class OrgKjemi {
	public static int sel1, sel2;
	public static String[] bindings = {"Single", "Double", "Triple"};
	public static String[] others = {"O", "Cl", "Br", "N"}; //IMPORTANT: make sure the significant groups come first!
	public static String[] othersName = {"amino","imino", "amin", "imin", "hydroksy", "okso", "okso", "cyano","oxy", "oxykarbonyl", "karboksyl", "klor", "brom"};
	public static String[] signSuf = {"amin", "imin", "amin", "imin", "ol", "on", "al", "nitril", "eter", "at", "syre"};
	public static String[] types = {"amin", "imin", "amino", "imino", "hydroxy", "keton", "aldehyd", "cyan", "eter", "ester", "syre", "klor", "brom"};
	public static int[] slots = {4, 2, 1, 1, 3};
	public static MainPanel m;
	public static String[] prefixC = {"met", "et", "prop", "but", "pent", "heks", "hept", "okt", "non", "dek"};
	public static String[] greekPref = {"", "un", "do", "tri"};
	public static String[] greekBig = {"cos", "docos", "triacos"};
	public static String[] prefixO = {"", "di", "tri", "tetra", "penta", "heksa", "hepta", "okta", "enna", "deka"};
	public static Color bgc = Color.white;
	//Array of base names to be translated:
	public static String[] baseName = {
		"2-amino-propansyre",
		"2-amino-3-metyl-butansyre",
		"2-amino-3-metyl-pentansyre",
		"2-amino-4-metyl-pentansyre",
		"2-amino-etansyre",
		"2-amino-3-hydroksy-butansyre",
		"2-amino-4-hydroksy-butansyre",
		"2, 4-diamino-4-okso-butansyre",
		"2, 5-diamino-5-okso-pentansyre",
		"metanal",
		"propan-1, 2, 3-triol"
	};
	public static String[] commonName = {
		"Alanin",
		"Valin",
		"Isoleucin",
		"Leucin",
		"Glycin",
		"Serin",
		"Treonin",
		"Asparagin",
		"Glutamin",
		"Formaldehyd",
		"Glyserol"
	};
	
	public static void main(String[] args) {
		new OrgKjemi();
	}
	OrgKjemi() {
		new MainPanel();
	}
}
