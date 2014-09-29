//Code taken from StakOverflow.

package edu.buffalo.cse.irf14.analysis;

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


		try {
			if (tStream.hasNext()) {

				Token tk = tStream.next();
				if (tk!=null)
				{
					String tempToken = tk.getTermText();
					if(!tempToken.equals(null) && !tempToken.equals(""))
					{
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

						return tStream.hasNext();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tStream.hasNext();
	}

	@Override
	public TokenStream getStream() {
		return tStream;
	}

}
