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
	int monthIndex=0, dayIndex=0, yearIndex=0;
	boolean ad, bc;
	String extra="";

	private static final String fullMonth = "(january)|(february)|(march)|(april)|(may)|\" + \"(june)|(july)|(august)|(september)|(october)|(november)|(december)";
	private static final String yearRange="([1-2][0-9]{3})([a-zA-Z,']+)|([1-2][0-9]{3})";//"(([1-2][0-9]{3})(..))|([1-2][0-9]{3})";
	private static final String shortMonth="^(?i:(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec))";
	private static final String dayRegex="(\\d{1,2})([',a-z]+)|^(\\d{1,2})";//"((\\d{1,2})(..))|((\\d{1,2}))|(\\d{1,2},)|((\\d{1,2})(...))";	//"((\\d{1,2})(..))|((\\d{1,2}))";//([0-2][0-9]|[3][0-1])|([0-9])";
	private static final String dayWithString="(\\d{1,2})(..)";//"(([0-2][0-9]|[3][0-1])|([0-9]))(..)";
	private static final String checkAdBc="([B].[C].)|([B][C])|([B].[C])|([A].[D].)|([A][D])|([A].[D])";//"([B].*[C].*)|([A].*[D].*)";
	private static final String yearNum="([0-9]+)(.*)";
	private static final String bcAdWithNum="(\\d+)(([B].*[C].*)|([A].*[D].*))";

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

					//Match 1 with Month Name
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

						//						if(!tStream.isFirst())
						//						{
						//int index=tStream.getIndex();

						String yearString=getYear();
						String dayString=getDay();
						appendExtra();
						if(yearString!=null)
						{
							year=yearString;
							tStream.remove(yearIndex);
						}

						if(dayString!=null)
						{
							int d=Integer.parseInt(dayString);
							if(d>0 &&d<32)
							{
								day=String.format("%02d", d);
								tStream.remove(dayIndex);
							}
						}

						tempToken=year+month+day+extra;
						tk.setTermText(tempToken);
						return true;
						//						}
					}

					checkPat=Pattern.compile(checkAdBc,Pattern.CASE_INSENSITIVE);
					matchText=checkPat.matcher(tempToken);

					if(matchText.matches())
					{
						int index=tStream.getIndex();
						int k=2;
						while(k<4)
						{
							Token previous=tStream.getPrevious((index-k));
							if(previous!=null)
							{
								checkPat=Pattern.compile("^(\\d+)(.*)",Pattern.CASE_INSENSITIVE);
								matchText=checkPat.matcher(previous.getTermText());

								if(matchText.matches())
								{
									int numYear=Integer.parseInt(matchText.group(1));
									year=String.format("%04d", numYear);
									tStream.remove(index-k+1);
									if(tempToken.contains(","))
										extra=",";
									if(tempToken.equals("BC")||tempToken.equals("B.C."))
										tempToken="-"+year+month+day+extra;
									else
										tempToken=year+month+day+extra;
									tk.setTermText(tempToken);
									return true;
								}
							}
							k++;
						}
					}
					
					checkPat=Pattern.compile(bcAdWithNum,Pattern.CASE_INSENSITIVE);
					matchText=checkPat.matcher(tempToken);

					if(matchText.matches())
					{
						int index=tStream.getIndex();
//						int k=2;
//						while(k<4)
//						{
							//Token previous=tStream.getPrevious((index-k));
//							if(previous!=null)
//							{
//								checkPat=Pattern.compile("^(\\d+)(.*)",Pattern.CASE_INSENSITIVE);
//								matchText=checkPat.matcher(previous.getTermText());

//								if(matchText.matches())
//								{
									int numYear=Integer.parseInt(matchText.group(1));
									year=String.format("%04d", numYear);
									//tStream.remove(index-k+1);
									if(tempToken.contains(","))
										extra=",";
									if(tempToken.contains("BC")||tempToken.contains("B.C."))
										tempToken="-"+year+month+day+extra;
									else
										tempToken=year+month+day+extra;
									tk.setTermText(tempToken);
									return true;
//								}
//							}
//							k++;
//						}
					}
					
					checkPat=Pattern.compile(yearRange,Pattern.CASE_INSENSITIVE);
					matchText=checkPat.matcher(tempToken);

					if(matchText.matches())
					{
						yearIndex=tStream.getIndex();
						year=tempToken;
						String monthString=getMonth();
						String dayString=getDay();
						appendExtra();
						
						if(monthString!=null)
						{
							tStream.remove(monthIndex);
						}

						if(dayString!=null)
						{
							int d=Integer.parseInt(dayString);
							if(d>0 &&d<32)
							{
								day=String.format("%02d", d);
								tStream.remove(dayIndex);
							}
						}
						
						tempToken=year+month+day+extra;
						tk.setTermText(tempToken);
						return true;
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
	private String getMonth()
	{
		return matchAndReturn(fullMonth, false, 2);
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
				if(previous==null) return null;
				matchText=checkPat.matcher(previous.getTermText());

				if(matchText.matches())
				{
					int position=1;
					while(position<4)
					{
						if(matchText.group(position)!=null)
							break;
						position=position+2;
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
			if(next==null) return null;
			matchText=checkPat.matcher(next.getTermText());
			int position=0;
			if(matchText.matches())
			{
				if(type==0)
				{
					position=1;
					yearIndex=index+i+1;
					while(position<4)
					{
						if(matchText.group(position)!=null)
							break;
						position=position+2;
					}
				}
				else if(type==1)
				{
					position=1;
					while(position<4)
					{
						if(matchText.group(position)!=null)
							break;
						position=position+2;
					}
					dayIndex=index-i+1;
				}
				else
				{
					int k=1;
					//Check if required
					monthIndex=index+i+1;
					while(k<13)
					{
						if(	matchText.group(k)!=null)
						{
							break;
						}
						k++;
					}
					month=String.format("%02d", k);
					return month;
				}
				return matchText.group(position);
			}
			i++;
		}
		return null;
	}

	private void appendExtra()
	{
		if(yearIndex>monthIndex && yearIndex>dayIndex)
		{
			Token tk=tStream.getNext((yearIndex-1));
			if(tk.getTermText().contains(","))
				extra=",";

		}
		else if(monthIndex>yearIndex && monthIndex>dayIndex)
		{
			Token tk=tStream.getNext((monthIndex-1));
			if(tk.getTermText().contains(","))
				extra=",";
		}
		else
		{
			Token tk=tStream.getNext((dayIndex-1));
			if(tk.getTermText().contains(","))
				extra=",";
		}
	}

	@Override
	public TokenStream getStream() {

		return tStream;
	}

}
