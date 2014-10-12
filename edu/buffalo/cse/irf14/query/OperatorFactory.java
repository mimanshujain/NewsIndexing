package edu.buffalo.cse.irf14.query;

public class OperatorFactory {

	private static OperatorFactory instance = null;
	
	protected OperatorFactory() {
		// Exists only to defeat instantiation.		
	}
	
	public static OperatorFactory getInstance() {
	      if(instance == null) {
	         instance = new OperatorFactory();
	      }
	      return instance;
	   }
	
	public QueryExpression getOperatorByType(String type)
	{
		if(type!=null)
		{
			if(type.equals(OperatorType.OR.name()))
				return new OR();
			if(type.equals(OperatorType.AND.name()))
				return new AND();
			if(type.equals(OperatorType.NOT.name()))
				return new NOT();
		}
		return null;
	}

}
