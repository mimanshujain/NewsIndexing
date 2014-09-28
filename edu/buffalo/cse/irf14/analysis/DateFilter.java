//References might be from tutorialspoint.com
package edu.buffalo.cse.irf14.analysis;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.DateFormatSymbols;

public class DateFilter extends TokenFilter {

	public DateFilter(TokenStream stream) {
		super(stream);

		//All the regex initialized
		fullMonth = "(january)|(february)|(march)|(april)|(may)|\" + \"(june)|(july)|(august)|(september)|(october)|(november)|(december)";
		yearRange="([1-2][0-9]{3})([a-zA-Z,']+)|([1-2][0-9]{3})";//"(([1-2][0-9]{3})(..))|([1-2][0-9]{3})";
		shortMonth="^(?i:(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec))";
		dayRegex="(\\d{1,2})([',a-z]+)|^(\\d{1,2})";//"((\\d{1,2})(..))|((\\d{1,2}))|(\\d{1,2},)|((\\d{1,2})(...))";	//"((\\d{1,2})(..))|((\\d{1,2}))";//([0-2][0-9]|[3][0-1])|([0-9])";
		dayWithString="(\\d{1,2})(..)";//"(([0-2][0-9]|[3][0-1])|([0-9]))(..)";
		checkAdBc="([B].[C].)|([B][C])|([B].[C])|([A].[D].)|([A][D])|([A].[D])";//"([B].*[C].*)|([A].*[D].*)";
		yearNum="([0-9]+)(.*)";
		bcAdWithNum="(\\d+)(([B].*[C].*)|([A].*[D].*))";
		conbineTime="(\\d{1,2}+)(:)(\\d+)(AM|PM)";
		timeSeperate="(^([0-9]|0[0-9]|1[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$)|(^([0-9]|0[0-9]|"
				+ "1[0-9]|2[0-3]):([0-5][0-9])$)";
		timeIntegrated="(^([0-9]|0[0-9]|1[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])(PM|AM)$)|"
				+ "(^([0-9]|0[0-9]|1[0-9]|2[0-3]):([0-5][0-9])(PM|AM)$)";
		dateWithHyphen="([1-2]\\d{3})-(\\d{2})(.*)";
		strNum="^(\\d+)(.*)";

		checkFullMonth=Pattern.compile(fullMonth,Pattern.CASE_INSENSITIVE);
		checkYearRange=Pattern.compile(yearRange, Pattern.CASE_INSENSITIVE);
		//		checkDay=Pattern.compile(dayRegex, Pattern.CASE_INSENSITIVE);
		checkPatAdBc=Pattern.compile(checkAdBc, Pattern.CASE_INSENSITIVE);
		checkBcAdWithNum=Pattern.compile(bcAdWithNum, Pattern.CASE_INSENSITIVE);
		checkTimeSeperate=Pattern.compile(timeSeperate, Pattern.CASE_INSENSITIVE);
		checkTimeIntegrated=Pattern.compile(timeIntegrated, Pattern.CASE_INSENSITIVE);
		checkDateWithHyphen=Pattern.compile(dateWithHyphen, Pattern.CASE_INSENSITIVE);		
		checkNum=Pattern.compile(strNum, Pattern.CASE_INSENSITIVE);
		checkShortMon = Pattern.compile(shortMonth);
	}
	//
	//	String dateFormat="yyyyMMdd";
	String year="1900", month="01", day="01";
	int monthIndex=0, dayIndex=0, yearIndex=0;
	String second="00", hour="00", minute="00";
	boolean ad, bc;
	String extra="";

	private  String fullMonth;// = "(january)|(february)|(march)|(april)|(may)|\" + \"(june)|(july)|(august)|(september)|(october)|(november)|(december)";
	private  String yearRange;//="([1-2][0-9]{3})([a-zA-Z,']+)|([1-2][0-9]{3})";//"(([1-2][0-9]{3})(..))|([1-2][0-9]{3})";
	private  String shortMonth;//="^(?i:(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec))";
	private  String dayRegex;//="(\\d{1,2})([',a-z]+)|^(\\d{1,2})";//"((\\d{1,2})(..))|((\\d{1,2}))|(\\d{1,2},)|((\\d{1,2})(...))";	//"((\\d{1,2})(..))|((\\d{1,2}))";//([0-2][0-9]|[3][0-1])|([0-9])";
	private  String dayWithString;//="(\\d{1,2})(..)";//"(([0-2][0-9]|[3][0-1])|([0-9]))(..)";
	private  String checkAdBc;//="([B].[C].)|([B][C])|([B].[C])|([A].[D].)|([A][D])|([A].[D])";//"([B].*[C].*)|([A].*[D].*)";
	private  String yearNum;//="([0-9]+)(.*)";
	private  String bcAdWithNum;//="(\\d+)(([B].*[C].*)|([A].*[D].*))";
	private  String conbineTime;//="(\\d{1,2}+)(:)(\\d+)(AM|PM)";
	private  String timeSeperate;//="(^([0-9]|0[0-9]|1[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$)|(^([0-9]|0[0-9]|"
	//			+ "1[0-9]|2[0-3]):([0-5][0-9])$)";
	private  String timeIntegrated;//="(^([0-9]|0[0-9]|1[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])(PM|AM)$)|"
	//			+ "(^([0-9]|0[0-9]|1[0-9]|2[0-3]):([0-5][0-9])(PM|AM)$)";
	private  String dateWithHyphen;//="([1-2]\\d{3})-(\\d{2})(.*)";
	private String strNum;

	private Pattern checkPat= null;
	private Pattern checkFullMonth= null;
	private Pattern checkYearRange= null;
	private Pattern checkDay= null;
	private Pattern checkPatAdBc= null;
	private Pattern checkBcAdWithNum= null;
	private Pattern checkTimeSeperate= null;
	private Pattern checkTimeIntegrated= null;
	private Pattern checkDateWithHyphen= null;
	private Pattern checkNum=null;
	private Pattern checkShortMon=null;

	private Matcher matchText = null;

	@Override
	public boolean increment() throws TokenizerException {

		try
		{
			if (tStream.hasNext()) 
			{
				Token tk = tStream.next();

				if(tk!=null)
				{
					String tempToken = tk.getTermText();
					if (!tempToken.equals(null) && tempToken != "") 
					{						
						year="1900"; month="01"; day="01"; second="00"; hour="00"; minute="00";
						matchText=checkFullMonth.matcher(tempToken);
						
						//Match 1 with Month Name
						if(matchText.find())
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
							tk.setThisDate(true);
							return tStream.hasNext();
							//						}
						}

						matchText=checkShortMon.matcher(tempToken);

						//Match 1 with Month Name
						if(matchText.matches())
						{
							//To get the month number
							String[] shortMonths=new DateFormatSymbols().getShortMonths();
							String mon=tempToken.substring(0,1).toUpperCase()+tempToken.substring(1).toLowerCase();

							int i=Arrays.asList(shortMonths).indexOf(mon);
							//Check if required
							monthIndex=tStream.getIndex();

							month=String.format("%02d", i+1);
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
							tk.setThisDate(true);
							return tStream.hasNext();
							//						}
						}

						//End1
						//Match2 with Ad BC					
						//checkPat=Pattern.compile(checkAdBc,Pattern.CASE_INSENSITIVE);
						matchText=checkPatAdBc.matcher(tempToken);

						if(matchText.matches())
						{
							int index=tStream.getIndex();
							int k=2;
							while(k<4)
							{
								Token previous=tStream.getPrevious((index-k));
								if(previous!=null)
								{
									//checkPat=Pattern.compile("^(\\d+)(.*)",Pattern.CASE_INSENSITIVE);
									matchText=checkNum.matcher(previous.getTermText());

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
										tk.setThisDate(true);
										return tStream.hasNext();
									}
								}
								k++;
							}
						}
						//End-2
						//	Start-3				
						//checkPat=Pattern.compile(bcAdWithNum,Pattern.CASE_INSENSITIVE);
						matchText=checkBcAdWithNum.matcher(tempToken);

						if(matchText.matches())
						{

							int numYear=Integer.parseInt(matchText.group(1));
							year=String.format("%04d", numYear);
							//tStream.remove(index-k+1);

							if(tempToken.contains(","))
								extra=",";
							if(tempToken.contains("."))
								extra=".";

							if(tempToken.contains("BC")||tempToken.contains("B.C."))
								tempToken="-"+year+month+day+extra;
							else
								tempToken=year+month+day+extra;
							tk.setTermText(tempToken);
							tk.setThisDate(true);
							return tStream.hasNext();

						}
						//End-3
						//	Start-4		
						//checkPat=Pattern.compile(yearRange,Pattern.CASE_INSENSITIVE);
						matchText=checkYearRange.matcher(tempToken);

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
							tk.setThisDate(true);
							return tStream.hasNext();
						}
						//End-4
						//	Start-5		

						String trans=tempToken;
						trans=trans.substring(trans.length()-1, trans.length());

						if (trans.contains(".") || trans.contains(",") || trans.contains("?"))
						{
							trans=tempToken;
							trans=trans.substring(0, tempToken.length()-1);
						}
						else
							trans=tempToken;

						//checkPat=Pattern.compile(timeIntegrated,Pattern.CASE_INSENSITIVE);
						matchText=checkTimeIntegrated.matcher(trans);

						if(matchText.matches())
						{
							String type="";
							if(matchText.group(7)!=null)
							{
								hour=matchText.group(7);
								minute=matchText.group(8);
								type=matchText.group(9);
							}
							else
							{
								hour=matchText.group(2);
								minute=matchText.group(3);
								second=matchText.group(4);
								type=matchText.group(5);
							}

							int hr=Integer.parseInt(hour);

							if(type.toUpperCase().equals("PM"))
							{
								if(hr<12 && hr!=0)
									hr=hr+12;
								if(hr==12)
								{
									hr=0;
								}
							}
							hour=String.format("%02d", hr);
							if(tempToken.contains(","))
								extra=",";
							if(tempToken.contains("."))
								extra=".";

							tempToken=hour+":"+minute+":"+second+extra;
							tk.setTermText(tempToken);
							tk.setThisDate(true);
							return tStream.hasNext();			
						}

						//End-5
						//Start-6					
						trans=tempToken.toUpperCase();
						if(trans.contains("AM") || trans.contains("PM"))
						{
							int index=tStream.getIndex();
							Token previous=tStream.getPrevious(index-2);
							if(previous!=null)
							{
								//checkPat=Pattern.compile(timeSeperate,Pattern.CASE_INSENSITIVE);
								matchText=checkTimeSeperate.matcher(previous.getTermText());

								if(matchText.matches())
								{
									if(matchText.group(2)!=null)
									{
										hour=matchText.group(2);
										minute=matchText.group(3);
										second=matchText.group(4);
									}
									else
									{
										hour=matchText.group(6);
										minute=matchText.group(7);
									}

									int hr=Integer.parseInt(hour);

									if(trans.contains("PM"))
									{
										if(hr<12 && hr!=0)
											hr=hr+12;
										if(hr==12)
										{
											hr=0;
										}
									}
									hour=String.format("%02d", hr);

									if(tempToken.contains(","))
										extra=",";
									if(tempToken.contains("."))
										extra=".";

									tempToken=hour+":"+minute+":"+second+extra;
									tk.setTermText(tempToken);
									//tStream.updateList(tk, index-1);
									tStream.remove(index-1);
									tk.setThisDate(true);
									return tStream.hasNext();
								}
							}					
						}
						//End-6
						//Start-7						
						//checkPat=Pattern.compile(dateWithHyphen,Pattern.CASE_INSENSITIVE);
						matchText=checkDateWithHyphen.matcher(trans);

						if(matchText.matches())
						{
							String date1=matchText.group(1);
							String date2=matchText.group(2);

							if(tempToken.contains(","))
								extra=",";
							if(tempToken.contains("."))
								extra=".";

							String yearPrefix=date1.substring(0, 2);
							date1=date1+month+day;
							date2=yearPrefix+date2+month+day;

							tempToken=date1+"-"+date2+extra;	
							tk.setTermText(tempToken);
							tk.setThisDate(true);
							return tStream.hasNext();
						}
					}
				}
			}
			else
				return tStream.hasNext();
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
				if(previous==null) break;
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
			if(next==null)  break;
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
			if(tk!=null)
			{
				String str=tk.getTermText();
				if(str!=null && !"".equals(str))
				{
					if(str.contains(","))
						extra=",";
					if(str.contains("."))
						extra=".";
				}
			}
		}
		else if(monthIndex>yearIndex && monthIndex>dayIndex)
		{
			Token tk=tStream.getNext((monthIndex-1));
			if(tk!=null)
			{
				String str=tk.getTermText();
				if(str!=null && !"".equals(str))
				{
					if(str.contains(","))
						extra=",";
					if(str.contains("."))
						extra=".";
				}
			}
		}
		else
		{
			Token tk=tStream.getNext((dayIndex-1));
			if(tk!=null)
			{
				String str=tk.getTermText();
				if(str!=null && !"".equals(str))
				{
					if(str.contains(","))
						extra=",";
					if(str.contains("."))
						extra=".";
				}
			}
		}
	}

	@Override
	public TokenStream getStream() {

		return tStream;
	}

}
