package commander.test;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import java.util.Arrays;

import org.digiplex.bukkitplugin.commander.CommanderEngine;
import org.digiplex.bukkitplugin.commander.scripting.Executable;
import org.digiplex.bukkitplugin.commander.scripting.ScriptBlock;
import org.digiplex.bukkitplugin.commander.scripting.ScriptParser;
import org.digiplex.bukkitplugin.commander.scripting.exceptions.BadScriptException;
import org.digiplex.bukkitplugin.commander.scripting.exceptions.BreakScriptException;
import org.junit.Ignore;
import org.junit.Test;

public class TestUnitCases extends TestCase {
	
	///////////////////////////// Simple Scripting ////////////////////////////////
	
	@Test public void simpleLine() throws Exception {
		String command = "Hello World!";
		
		Executable sl = ScriptParser.parseScript(command);
		sl.execute(environment);
		
		assertTrue("Commands don't match!", server.checkCommands(command));
	}
	
	@Test public void multilineScript() throws Exception {
		String[] commands = new String[] {
				"Test Line 1  \n",
				"Test Command 2  ",
				" ec bc daytime!  ",
				"",
				"Hello World! \n",
		};
		
		Executable sl = ScriptParser.parseScript(commands);
		sl.execute(environment);
		
		assertTrue("Commands don't match!", server.checkCommands("Test Line 1", "Test Command 2", "ec bc daytime!", "Hello World!"));
	}
	
	@Test public void ignoreComments() throws Exception {
		String[] commands = new String[] {
				"Test Line 1",
				"Hello World! #world is someplace on the server",
				"# stuff!",
				"    # More stuff!!",
				"More stuff #Stuff #stuff!!",
				"###############################",
				" Test Line #42",
		};
		
		Executable sl = ScriptParser.parseScript(commands);
		sl.execute(environment);
		
		assertTrue("Commands don't match!", server.checkCommands("Test Line 1", "Hello World!", "More stuff", "Test Line"));
	}
	
	@Test(expected = BadScriptException.class) //this test WILL throw an exception. If it doesn't, fail it! :P
	public void detectUnevenParens() throws Exception {
		String[] commands = new String[] {
				"Test Line 1",
				"{", 
				"Test Line 2",
				"Test Line 3",
				"Test Line 4",
		};
		
		
		Executable sl = ScriptParser.parseScript(commands);
		sl.execute(environment);
	}
	
	@Test(expected = BadScriptException.class) 
	public void detectUnevenParensClose() throws Exception {
		String[] commands = new String[] {
				"Test Line 1",
				"Test Line 2",
				"Test Line 3",
				"}",
				"Test Line 4",
		};
		
		Executable sl = ScriptParser.parseScript(commands);
		sl.execute(environment);
	}
	
	@Test public void innerBraceScript() throws Exception {
		String[] commands = new String[] {
				"Test Line 1",
				"Test Line 2",
				"{",
				"    {",
				"        Inner-inner Block 1",
				"        Inner-inner Block 2",
				"    }",
				"    Inner Block 1",
				"    Inner Block 2",
				"}",
				"Test Line 3",
				"Test Line 4",
		};
		
		Executable sl = ScriptParser.parseScript(commands);
		sl.execute(environment);
		
		assertTrue("Commands don't match!", server.checkCommands(
				"Test Line 1", "Test Line 2", 
				"Inner-inner Block 1", "Inner-inner Block 2",
				"Inner Block 1", "Inner Block 2",
				"Test Line 3", "Test Line 4"));
	}
	
	@Test public void escapeCharacters() throws Exception {
		String command = "Esc\\aping \\characters \\@odds with thing i\\$s f\\un!";
		//note, Java requires escaping the escape character here, so escaping is done with one backslash (\)
		
		Executable sl = ScriptParser.parseScript(command);
		sl.execute(environment);
		
		assertTrue("Commands don't match!", server.checkCommands("Escaping characters @odds with thing i$s fun!"));
	}
	
	/////////////////////////// Variables ////////////////////////////////
	
	@Test public void variableReplacement() throws Exception {
		environment.setVariableValue("t1", "hello world");
		environment.setVariableValue("hope", "change");
		
		String command = "Variable @t1 Testing I'm @{hope}ing works";
		
		Executable sl = ScriptParser.parseScript(command);
		sl.execute(environment);
		
		assertTrue("Commands don't match!", server.checkCommands("Variable hello world Testing I'm changeing works"));
	}
	
	@Test public void varAssignmentAndScope() throws Exception {
		String[] commands = new String[] {
				"@vo = hello",
				"Testing Var @{vo}",
				"{",
				"    @qu = world",
				"    @vo = buddy",
				"    @gl := ten", //globally set the variable
				"    Testing Vars @qu @vo",
				"}",
				"Testing Vars @qu, @vo",
				"Test Line @gl",
		};
		
		Executable sl = ScriptParser.parseScript(commands);
		sl.execute(environment);
		
		assertTrue("Commands don't match!", server.checkCommands(
				"Testing Var hello", 
				"Testing Vars world buddy", 
				"Testing Vars \u00D8, buddy", //@qu is null, replaced with a o with slash through it 
				"Test Line ten"));
	}
	
	@Test public void legalVarAssignments() throws Exception {
		String[] commands = new String[] {
				"@x = hello", //string assignment
				"@y = 123", //"int" assignment, though it is a string
				"@z = @x", //assign variable to another variable, assigns value of var
				"@1 = world", //variable name can be number
				"@pnemoniamicroscopicsylivicaniconosis = 2", //no limit on length
				"Testing @x @{y} @1, @z",
		};
		
		Executable sl = ScriptParser.parseScript(commands);
		sl.execute(environment);
		
		assertTrue("Commands don't match!", server.checkCommands("Testing hello 123 world, hello"));
	}
	
	@Test(expected = BadScriptException.class)
	public void illegalVarAssignment() throws Exception {
		String[] commands = new String[] {
				"@ = hi", //bad variable assignment
		};
		
		Executable sl = ScriptParser.parseScript(commands);
		sl.execute(environment);
	}
	
	////////////////////////// If Construct ///////////////////////////
	
	@Test public void ifConstruct() throws Exception {
		environment.setVariableValue("hello", "world");
		environment.setVariableValue("i", "1");
		
		String[] commands = new String[] {
				"[if @hello = world]",
				"{",
				"    Good If Hello world!",
				"}",
				"Test Line 12",
				"[if @hello = @i]",
				"    This line shouldn't run",
				"[!if @i = 1]",
				"{",
				"    This line also shouldn't run",
				"}",
				"[if @i = 1]",
				"    But this line should",
				"[unless @i = 1]",
				"    This line should not",
				"[unless @i = 2]",
				"    This line runs!",
				"Test Line 42"
		};
		
		Executable sl = ScriptParser.parseScript(commands);
		sl.execute(environment);
		
		assertTrue("Commands don't match!", server.checkCommands("Good If Hello world!", "Test Line 12", "But this line should", "This line runs!", "Test Line 42"));
	}
	
	@Test public void ifElseConstruct() throws Exception {
		environment.setVariableValue("hello", "hi");
		environment.setVariableValue("i", "1");
		
		String[] commands = new String[] {
				"[if @hello = world]",
				"{",
				"    This command should not run",
				"}",
				"[else]", //else test
				"    But this command should run!",
				"[!if @i = 1]",
				"    This line also shouldn't run",
				"[else if @i = 1]",
				"{", //else if test
				"    But this line should",
				"    As should this line",
				"}",
				"[else]",
				"    One last no run",
				"",
				"[if @i = 13]", //chaining test
				"    No Run 1",
				"[else if @i = 14]",
				"    No Run 2",
				"[else if @i = 20]",
				"    No Run 3",
				"[else if @i = 25]",
				"    No Run 4",
				"[else]",
				"    Yes Run 1",
				"Test Line 196.2"
		};
		
		Executable sl = ScriptParser.parseScript(commands);
		sl.execute(environment);
		
		assertTrue("Commands don't match!", server.checkCommands("But this command should run!", "But this line should", "As should this line", "Yes Run 1", "Test Line 196.2"));
	}
	
	@Test public void comparisonCondition() throws Exception {
		environment.setVariableValue("x", 1);
		
		String[] commands = new String[] {
				"[if @x > 0] {",
				"    X is one here",
				"}",
				"@x--",
				"[if @x > 0]",
				"    X is now zero and cond false",
				"[if @x <= 0]",
				"    This will run",
				"[!if @x = 0]",
				"    This will not",
				"[else if @x < 0]",
				"    Also won't run",
				"[else if @x >= 0]",
				"    Runs",
				"Test Line 196.5"
		};
		
		Executable sl = ScriptParser.parseScript(commands);
		sl.execute(environment);
		
		assertTrue("Commands don't match!", server.checkCommands("X is one here", "This will run", "Runs", "Test Line 196.5"));
	}
	
	@Test public void checkCondition() throws Exception {
		environment.setVariableValue("x", true);
		environment.setVariableValue("y", false);
		environment.setVariableValue("z", null);
		environment.setVariableValue("w", "Hello");
		
		String[] commands = new String[] {
				"[if @x] {",
				"    True statement",
				"}",
				"[if @y]",
				"    false statement",
				"[if @z]",
				"    null statement",
				"[else if @w]",
				"    Object statement",
				"[!if @y]",
				"    Not false",
				"Test Line 295"
		};
		
		Executable sl = ScriptParser.parseScript(commands);
		sl.execute(environment);
		
		assertTrue("Commands don't match!", server.checkCommands("True statement", "Object statement", "Not false", "Test Line 295"));
	}
	
	//////////////////////// Permission (Ext of If) /////////////////////////
	
	@Test public void permissionConstruct() throws Exception {
		String[] commands = new String[] {
				"[if has commander.test1]", //hardcoded in TestPlayer to true
				"{",
				"    This command should run",
				"}",
				"",
				"[has commander.test2]", //false, test has shortcut 
				"    This line does not run",
				"[else if Player !has commander.test3]", //false, test not, other player, error handling
				"    This command does not run either",
				"[else if TestPlayer has commander.test1]", //true, test other player
				"    But this line should",
				"[else]",
				"    One last no run",
				"",
				"Test Line 196.5"
		};
		
		Executable sl = ScriptParser.parseScript(commands);
		sl.execute(environment);
		
		assertTrue("Commands don't match!", server.checkCommands("This command should run", "But this line should", "Test Line 196.5"));
	}
	
	@Test(expected = BadScriptException.class) 
	public void invalidPermissionFormat() throws Exception {
		String[] commands = new String[] {
				"[TestPlayer has commander.test1]",
				"    This construct is invalid, means nothing",
		};
		Executable sl;
		
		sl = ScriptParser.parseScript(commands);
		sl.execute(environment);
	}
	
	/////////////////////// Loop Construct ////////////////////////////
	
	@Test public void forIntLoop() throws Exception {
		environment.setVariableValue("e", 6);
		
		String[] commands = new String[] {
				"[loop @i = 0 to 5] {", //inclusive on both ends
				"    This is loop @i",
				"    @i = 42", //the loop construct does not care if you step on its variable, it simply overwrites it on next loop
				"}",
				"[loop @i = 0 to @e step 2]", //loop reads the end variable once
				"    Step 2 Loop @i",
				"[loop @i = 0 to 3 step 2]",
				"    Step 2 odd Loop @i",
				"Test Line 42"
		};
		
		Executable sl = ScriptParser.parseScript(commands);
		sl.execute(environment);
		
		assertTrue("Commands don't match!", server.checkCommands(
				"This is loop 0", "This is loop 1", "This is loop 2", "This is loop 3", "This is loop 4", "This is loop 5",
				"Step 2 Loop 0", "Step 2 Loop 2", "Step 2 Loop 4", "Step 2 Loop 6",
				"Step 2 odd Loop 0", "Step 2 odd Loop 2",
				"Test Line 42"));
	}
	
	@Test(expected = BadScriptException.class) 
	public void invalidLoopFormat() throws Exception {
		String[] commands = new String[] {
				"[loop @hello = w to z]",
				"    This loop is invalid",
		};
		Executable sl;
		
		sl = ScriptParser.parseScript(commands);
		sl.execute(environment);
	}
	
	////////////////////// For Each Construct ////////////////////////
	
	@Test public void forEachLoop() throws Exception {
		String id = environment.pushCollection(Arrays.asList("Hello", "World", "I Am", "And I shall", "Always", "Be"));
		environment.setVariableValue("coll", id);
		
		String[] commands = new String[] {
				"[foreach @i in @coll] {",
				"    say @i",
				"}",
				"Test Line 21"
		};
		
		Executable sl = ScriptParser.parseScript(commands);
		sl.execute(environment);
		
		assertTrue("Commands don't match!", 
				server.checkCommands("say Hello", "say World", "say I Am", "say And I shall", "say Always", "say Be", 
				"Test Line 21"));
	}
	
	@Test (expected = BadScriptException.class)
	public void illegalForEachLoop1() throws Exception {
		String[] commands = new String[] {
				"[for each @i in @coll]",
				"    say @i",
		};
		
		Executable sl = ScriptParser.parseScript(commands);
		sl.execute(environment);
	}
	
	////////////////////// While Construct /////////////////////////
	
	@Test public void whileLoop() throws Exception {
		environment.setVariableValue("i", "0");
		
		String[] commands = new String[] {
				"[while @i < 5] {", //if statement, except it loops
				"    This is loop @i",
				"    @i++",
				"}",
				"[until @i > 8] {",
				"    This is until @i",
				"    @i++",
				"}",
				"Test Line 42"
		};
		
		Executable sl = ScriptParser.parseScript(commands);
		sl.execute(environment);
		
		assertTrue("Commands don't match!", 
				server.checkCommands(
						"This is loop 0", "This is loop 1", "This is loop 2", "This is loop 3", "This is loop 4",
						"This is until 5", "This is until 6", "This is until 7", "This is until 8",
						"Test Line 42"));
	}
	
	@Test(expected = BadScriptException.class) 
	public void whileLoopLoopLimit1() throws Exception {
		environment.setVariableValue("i", "0");
		
		String[] commands = new String[] {
				"[while @i < 500] {", 
				"    @i++", //this will hit the default legal limit, 200, before it reaches the right time
				"}",
				"Test Line 42"
		};
		
		Executable sl = ScriptParser.parseScript(commands);
		sl.execute(environment);
	}
	@Test public void whileLoopLoopLimit2() throws Exception {
		environment.setVariableValue("i", "0");
		
		String[] commands = new String[] {
				"?looplim 1000",
				"[while @i < 500] {", 
				"    @i++", //with directive above, this will not hit the limit
				"}",
				"Test Line 42"
		};
		Executable sl = ScriptParser.parseScript(commands);
		sl.execute(environment);
		
		assertTrue("Commands don't match!", server.checkCommands("Test Line 42"));
	}
	
	//////////////////// Break Pseudo-Construct ///////////////////////
	
	@Test public void breakConstruct() throws Exception {
		environment.setVariableValue("i", "0");
		
		String[] commands = new String[] {
				"[loop @i = 2 to 5] {",
				"    [if @i = 4]",
				"        [break]", //breaks out of the loop
				"    Hello i = @i",
				"}",
				"[break]", //break out of script
				"Test Line 195.3"
		};
		
		try {
			Executable sl = ScriptParser.parseScript(commands);
			sl.execute(environment);
		} catch (BreakScriptException ex) {} //must catch the break exception
		
		assertTrue("Commands don't match!", 
				server.checkCommands("Hello i = 2", "Hello i = 3"));
	}
	
	///////////////////// Run Pseudo-Construct //////////////////////
	
	@Test public void runConstruct() throws Exception {
		String[] commands = new String[] {
				"Test Line 22",
				"Test Line 23",
				"Test Line 24",
		};
		ScriptBlock runblock = (ScriptBlock) ScriptParser.parseScript(commands);
		CommanderEngine.getInstance().setScriptForAlias("TestExtScript", runblock);
		
		commands = new String[] {
				"Test Line 192",
				"[run TestExtScript]",
				"Test Line 195.3",
		};
		
		Executable sl = ScriptParser.parseScript(commands);
		sl.execute(environment);
		
		assertTrue("Commands don't match!", 
				server.checkCommands("Test Line 192", "Test Line 22", "Test Line 23", "Test Line 24", "Test Line 195.3"));
	}
	
	////////////////////////////// Others //////////////////////////
	
	/**
	 * [switch @var]
	 * {
	 * 	 [case 1] {
	 *     case 1
	 *   }
	 *   [case 2] {
	 *     case 2
	 *   }
	 *   [else] {
	 *     case else
	 *   }
	 * }
	 * 
	 * [switch @num]
	 * {
	 *   [case 1-2] {
	 *     case 1 or 2
	 *   }
	 *   [case 3-6] {
	 *     case 3, 4, 5, or 6
	 *   }
	 *   [case > 7] {
	 *     cases above 7
	 *   }
	 *   [case < -3] {
	 *     cases below -3
	 *   }
	 *   [else] {
	 *     cases -1, -2, -3, and 7
	 *   }
	 * }
	 * 
	 * 
	 * @throws Exception
	 */
	@Ignore(value = "Case statements are unimplemented")
	@Test public void switchCase() throws Exception {
		fail("Not yet implemented");
	}
	
}
