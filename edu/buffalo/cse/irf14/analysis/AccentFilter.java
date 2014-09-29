package edu.buffalo.cse.irf14.analysis;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class AccentFilter extends TokenFilter {

	private static final String tab00c0 = "AAAAAAACEEEEIIII"
			+ "DNOOOOO\u00d7\u00d8UUUUYI\u00df" + "aaaaaaaceeeeiiii"
			+ "\u00f0nooooo\u00f7\u00f8uuuuy\u00fey" + "AaAaAaCcCcCcCcDd"
			+ "DdEeEeEeEeEeGgGg" + "GgGgHhHhIiIiIiIi" + "IiJjJjKkkLlLlLlL"
			+ "lLlNnNnNnnNnOoOo" + "OoOoRrRrRrSsSsSs" + "SsTtTtTtUuUuUuUu"
			+ "UuUuWwYyYZzZzZzF";

	public AccentFilter(TokenStream stream) {
		super(stream);
	}

	@Override
	public boolean increment() throws TokenizerException {

		//
		//
		// if (tStream.hasNext()) {
		//
		//
		// Token tk = tStream.next();
		// String tempToken = tk.getTermText();
		// if (tempToken!=null && !"".equals(tempToken)) {
		//
		// String nfdNormalizedString = Normalizer.normalize(tempToken,
		// Normalizer.Form.NFKD);
		// //System.out.println(nfdNormalizedString);
		// Pattern pattern =
		// Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
		// tk.setTermText(pattern.matcher(nfdNormalizedString).replaceAll(""));
		// return true;
		// }
		// }
		//
		// return false;

		try {
			if (tStream.hasNext()) {

				Token tk = tStream.next();

				String tempToken = tk.getTermText();
				String tempToken1 = "";

				char[] vysl = new char[tempToken.length()];
				
				char one;
				
				for (int i = 0; i < tempToken.length(); i++) {
					one = tempToken.charAt(i);
					if (one >= '\u00c0' && one <= '\u017f') {
						one = tab00c0.charAt((int) one - '\u00c0');

					}
					vysl[i] = one;
					String b = String.valueOf(vysl);
					tk.setTermText(b.trim());
				}

				return true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public TokenStream getStream() {
		// TODO Auto-generated method stub
		return tStream;
	}

}
