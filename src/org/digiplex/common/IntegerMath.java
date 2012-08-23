package org.digiplex.common;

import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;


public class IntegerMath {
	private static enum PARSE_STATE {
		EXPECT_NUMBER,
		EXPECT_NUM_OR_OP,
		EXPECT_OPERATOR,
		PAREN_GROUP,
	}
	
	public static int parseMath(String str) throws IllegalArgumentException {
		CharBuffer cb = CharBuffer.wrap(str.toCharArray());
		
		List<MathObject> compiledForm = compileFormula(cb);
		
		return 0;
	}
	
	private static List<MathObject> compileFormula(CharBuffer buf) throws IllegalArgumentException {
		ArrayList<MathObject> mol = new ArrayList<IntegerMath.MathObject>();
		
		//TODO
		
		return mol;
	}
	
	
	private class MathObject {
		
	}
	private class NumberObject extends MathObject {
		public int number;
	}
	private class OperatorObject extends MathObject {
		public char op; //+ - * / ^
	}
	private class ParenGroup extends MathObject {
		public List<MathObject> inner;
	}
	
	
	/*
	private static enum OP {
		ADD ('+'),
		SUB ('-'),
		MULT ('*'),
		DIV ('/'),
		POW ('^'),
		;
		
		public char operator;
		private OP(char operator) {
			this.operator = operator;
		}
	}
	
	public static int parseMath(String str) throws IllegalArgumentException {
		return parseMath(str.toCharArray());
	}
	public static int parseMath(char[] mstr) throws IllegalArgumentException {
		int returnValue = 0;
		
		boolean workingNegative = false; //if workingNum is negative 
		int workingNum = 0;
		OP workingOp = null;
		
		PARSE_STATE state = PARSE_STATE.EXPECT_NUMBER;
		
		for (int i = 0; i < mstr.length; i++) {
			char c = mstr[i];
			
			switch (c) {
			case '1': case '2': case '3': case '4': case '5':
			case '6': case '7': case '8': case '9': case '0':
				switch (state) {
				case EXPECT_NUM_OR_OP:
				case EXPECT_NUMBER:
					workingNum = (workingNum * 10) + (c - '0');
					state = PARSE_STATE.EXPECT_NUM_OR_OP;
					continue;
				default:
					throw new IllegalArgumentException("Error parsing! Unexpected number! "+i);
				}
			case '-':
				switch (state) {
				case EXPECT_NUMBER:
					workingNegative = true;
					continue;
				case EXPECT_NUM_OR_OP:
				case EXPECT_OPERATOR:
					workingOp = OP.SUB;
					state = PARSE_STATE.EXPECT_NUMBER; //skip to this
					continue;
				default:
					throw new IllegalArgumentException("Error parsing! Unexpected negative sign! "+i);
				}
			case '+':
				switch (state) {
				case EXPECT_OPERATOR:
				case EXPECT_NUM_OR_OP:
					workingOp = OP.ADD;
					state = PARSE_STATE.EXPECT_NUMBER;
					continue;
				default:
					throw new IllegalArgumentException("Error parsing! Unexpected plus! "+i);
				}
			}
		}
		return returnValue;
	}*/
	
}
