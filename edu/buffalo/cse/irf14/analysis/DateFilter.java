package edu.buffalo.cse.irf14.analysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateFilter extends TokenFilter {

	public DateFilter(TokenStream stream) {
		super(stream);
		// TODO Auto-generated constructor stub
	}
	
	String dateFormat="yyyyMMdd";
	String year="1900", month="01", day="01";
	int monthIndex, dayIndex, yearIndex;
	boolean ad, bc;
	
	private static final String fullMonth = "(january)|(february)|(march)|(april)|(may)|\" + \"(june)|(july)|(august)|(september)|(october)|(november)|(december)";
	private static final String yearRange="(([1-2][0-9]{3})(..))|([1-2][0-9]{3})";
	private static final String shortMonth="^(?i:(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec))";
	private static final String dayRegex="((\\d{1,2})(..))|((\\d{1,2}))";//([0-2][0-9]|[3][0-1])|([0-9])";
	private static final String dayWithString="(\\d{1,2})(..)";//"(([0-2][0-9]|[3][0-1])|([0-9]))(..)";
	private static final String checkAdBc="([B].*[C].*)|([A].*[D].*)";
	private static final String yearNum="([0-9]+)(.*)";

	private Pattern checkPat= null;
	private Matcher matchText = null;

	@Override
	public boolean increment() throws TokenizerException {

		try
		{
			if (tStream.hasNext()) {
				Token tk = tStream.next();

				if (tk.getTermText() != "" || tk.getTermText() != null) {
					String tempToken = tk.getTermText();

					checkPat=Pattern.compile(fullMonth,Pattern.CASE_INSENSITIVE);
					matchText=checkPat.matcher(tempToken);

					if(matchText.matches())
					{
						//To get the month number
						int i=1;
						//Check if required
						monthIndex=tStream.getIndex();
						while(i<13)
						{
							if(	matchText.group(i)!=null)
							{
								break;
							}
							i++;
						}
						month=String.format("%02d", i);

						if(!tStream.isFirst())
						{
							int index=tStream.getIndex();
							
							String yearString=getYear();
							String dayString=getDay();
							
							if(yearString!=null)
							{
								year=yearString;
								tStream.remove(yearIndex);
							}
							
							if(yearString!=null)
							{
								int d=Integer.parseInt(dayString);
								day=String.format("%02d", d);
								tStream.remove(dayIndex);
							}
							tempToken=year+month+day;
							tk.setTermText(tempToken);
//							return true;
						}
					}
//					else
//						return true;
				}
			}
			else
				return false;
		}
		
		catch(NumberFormatException e)
		{
			throw new TokenizerException();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw new TokenizerException();
		}
		return true;
	}

	//Get the year if present
	private String getYear()
	{
		return matchAndReturn(yearRange, false, 0);
	}

	//Get the day if present
	private String getDay(){
		 String day=matchAndReturn(dayRegex, true, 1);
//		 if(day==null)
//		 {
//			 return matchAndReturn(dayWithString, true, 1);
//		 }
		 return day;
	}	

	//Process to find day and year across the current Token
	private String matchAndReturn(String regex, boolean checkDay, int type)
	{
		int i=2;
		int index=tStream.getIndex();
		checkPat=Pattern.compile(regex,Pattern.CASE_INSENSITIVE);

		if(checkDay)
		{
			while(i<5 && index!=0)
			{
				Token previous=tStream.getPrevious((index-i));
				matchText=checkPat.matcher(previous.getTermText());

				if(matchText.matches())
				{
					int position=2;
					while(position<5)
					{
						if(matchText.group(position)!=null)
							break;
						position=position+1;
				}
					dayIndex=index-i+1;
					return matchText.group(position);
				}
				i++;
			}
		}

		i=0;

		while(i<2)
		{
			Token next=tStream.getNext(index+i);			
			matchText=checkPat.matcher(next.getTermText());
			int position=2;
			if(matchText.matches())
			{
				if(type==0)
				{
					yearIndex=index-i+1;
					while(position<5)
					{
						if(matchText.group(position)!=null)
							break;
						position=position+2;
					}
				}
				else
				{
					while(position<5)
					{
						if(matchText.group(position)!=null)
							break;
						position=position+1;
					}
					dayIndex=index-i+1;
				}
				return matchText.group(position);
			}
			i++;
		}
		return null;
	}

	@Override
	public TokenStream getStream() {

		return tStream;
	}

}
