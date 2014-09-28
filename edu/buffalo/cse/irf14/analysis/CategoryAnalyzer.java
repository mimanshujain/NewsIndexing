package edu.buffalo.cse.irf14.analysis;

public class CategoryAnalyzer implements Analyzer {

	TokenStream tStream;
	TokenFilter filtering;

	public CategoryAnalyzer(TokenStream tStream) {
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
					tStream.reset();
					return false;
				}
				else
					return tStream.hasNext();
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

			if(tFilterFactory!=null && tStream!=null)
			{
//				filtering=tFilterFactory.getFilterByType(TokenFilterType.SPECIALCHARS, tStream);
//				applyFilters(filtering);
//				filtering=tFilterFactory.getFilterByType(TokenFilterType.STOPWORD, tStream);
//				applyFilters(filtering);
				filtering=tFilterFactory.getFilterByType(TokenFilterType.CAPITALIZATION, tStream);
				applyFilters(filtering);
//				filtering=tFilterFactory.getFilterByType(TokenFilterType.SYMBOL, tStream);
//				applyFilters(filtering);			
//				filtering=tFilterFactory.getFilterByType(TokenFilterType.DATE, tStream);
//				applyFilters(filtering);
//	//			filtering=tFilterFactory.getFilterByType(TokenFilterType.NUMERIC, tStream);
//	//			applyFilters(filtering);
//				filtering=tFilterFactory.getFilterByType(TokenFilterType.STEMMER, tStream);
//				applyFilters(filtering);
//				filtering=tFilterFactory.getFilterByType(TokenFilterType.ACCENT, tStream);
//				applyFilters(filtering);
			}
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
		return tStream;
	}

}
