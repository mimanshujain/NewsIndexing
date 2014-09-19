package edu.buffalo.cse.irf14.analysis;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class AccentFilter extends TokenFilter {

	public AccentFilter(TokenStream stream) {
		super(stream);
	}

	@Override
	public boolean increment() throws TokenizerException {

		if (tStream.hasNext()) {
			Token tk = tStream.next();
			String tempToken = tk.getTermText();
			if (tempToken!=null && !"".equals(tempToken)) {

				String nfdNormalizedString = Normalizer.normalize(tempToken,
						Normalizer.Form.NFKD);
				Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
				tk.setTermText(pattern.matcher(nfdNormalizedString).replaceAll(""));
				return true;
			}
		}
		return false;
	}

	@Override
	public TokenStream getStream() {
		// TODO Auto-generated method stub
		return tStream;
	}
//
//	public static void main(String[] args) {
//
//	}

}
