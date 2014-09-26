package edu.buffalo.cse.irf14.analysis;

public class TermAnalyer implements Analyzer {

	TokenStream tStream;
	TokenFilter filtering;

	public TermAnalyer(TokenStream tStream) {
		this.tStream=tStream;
	}

	@Override
	public boolean increment() throws TokenizerException {
		try
		{
			if(tStream!=null)
			{
				if(tStream.hasNext())
				{
					doAnalysisOnStream();
					return tStream.hasNext();
				}
				else
					return false;
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw new TokenizerException();
		}
		return false;
	}

	private void doAnalysisOnStream() throws TokenizerException {
		try
		{
			TokenFilterFactory tFilterFactory=TokenFilterFactory.getInstance();

			filtering=tFilterFactory.getFilterByType(TokenFilterType.SYMBOL, tStream);
			applyFilters(filtering);			
			filtering=tFilterFactory.getFilterByType(TokenFilterType.SPECIALCHARS, tStream);
			applyFilters(filtering);
			filtering=tFilterFactory.getFilterByType(TokenFilterType.STOPWORD, tStream);
			applyFilters(filtering);
			filtering=tFilterFactory.getFilterByType(TokenFilterType.CAPITALIZATION, tStream);
			applyFilters(filtering);
			filtering=tFilterFactory.getFilterByType(TokenFilterType.DATE, tStream);
			applyFilters(filtering);
			filtering=tFilterFactory.getFilterByType(TokenFilterType.NUMERIC, tStream);
			applyFilters(filtering);
			filtering=tFilterFactory.getFilterByType(TokenFilterType.STEMMER, tStream);
			applyFilters(filtering);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw new TokenizerException();
		}

	}


	private void applyFilters(TokenFilter filterObject)
	{
		if(filterObject!=null)
		{
			try {
				tStream.reset();
				while(filterObject.increment())
				{					
				}

				filtering=null;
			} catch (TokenizerException e) {
				e.printStackTrace();
			}
		}
	}
	@Override
	public TokenStream getStream() {
		// TODO Auto-generated method stub
		return tStream;
	}

}
