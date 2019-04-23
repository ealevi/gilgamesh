/*
	Gilgamesh Artificial Intelligence Project
    Copyright (C) 2014  Eduardo Alevi

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
    
*/

package org.gilgamesh.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

import org.gilgamesh.model.Answer;
import org.gilgamesh.model.Gilgamesh;
import org.gilgamesh.model.core.Atom;
import org.gilgamesh.model.core.Core;
import org.gilgamesh.model.core.Fact;


public class CommandShell
{
	private Core<String> core = Gilgamesh.createStringCore();
	private static final String PROMPT = "gilgamesh: ";
	private static final String SINTAXE[] = {";", "?", " ", "'", "#", "?-"};
	
	private static enum Command
	{
		UNKNOWN, COMMENT, LINKS, HELP, EXIT, RESET, ATOMS, ENTRY, REINFORCE, PUNISH, FACT, FACTS, REMOVE, QUESTION, QUESTION_SUB,  
		SCRIPT,	LOAD, SAVE, DONE, ANSWER, ANSWERALL, ANSWERANY, ANSWERSUPP, ANSWERDIST, STATISTICS, ECHO, VERSION;
	};
	

	
	
	
	public CommandShell(String param)
	{
		printHeader();
		
		if(param == null || "".equals(param.trim()))
			shellLoop(System.in, System.out, -1);
		else
			runScript("script;" + param);
	}
	
	private void printHeader()
	{
		String gnu = "Gilgamesh Artificial Intelligence Project\n" + 
	    "Copyright (C) 2014  Eduardo Alevi\n" +
	    "This program is free software: you can redistribute it and/or modify\n" +
	    "it under the terms of the GNU General Public License as published by\n" +
	    "the Free Software Foundation, either version 3 of the License, or\n" +
	    "(at your option) any later version.\n" +
	    "This program is distributed in the hope that it will be useful,\n" +
	    "but WITHOUT ANY WARRANTY; without even the implied warranty of\n" +
	    "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n" +
	    "GNU General Public License for more details.\n" +
	    "You should have received a copy of the GNU General Public License\n" +
	    "along with this program.  If not, see <http://www.gnu.org/licenses/>.\n";

		System.out.printf("---------------------------------------------------------------------------------------------\n");
		System.out.printf("%s\n\nWelcome to Gilgamesh command shell!\nType \"help;\" followed by <ENTER> to see command line help.\n\n", gnu);
	}
	
	private void shellLoop(InputStream input, OutputStream output, int total)
	{
		int counter = 0;
		Scanner scanner = new Scanner(input);
		Command command = null;
		String params[];
		
		while(command != Command.DONE)
			try
			{
				if(total > 0)
					System.out.printf("Progress: %.2f%%\r", (counter++ / (double) total) * 100.0);
				else
					System.out.printf(PROMPT);
				
				String line = scanner.hasNextLine()? scanner.nextLine() : Command.DONE.name().toLowerCase() + SINTAXE[0];
				
				if(line == null || "".equals(line = line.trim()))
					continue;
				
				System.out.printf("                    \r"); // workaround for console output.
				command = getCommand(line);
	
				switch(command)
				{
					case HELP:
						printHelp();
						break;
					case EXIT:
						System.out.println("\nThanks for using Gilgamesh!\nExiting program...\n");
						System.exit(0);
						break;
					case RESET:
						core = Gilgamesh.createStringCore();
						System.gc();
						System.out.println("Memory erased.");
						break;
					case SCRIPT:
						runScript(line);
						break;
					case SAVE:
						save(line);
						break;
					case LOAD:
						load(line);
						break;
					case ATOMS:
						for(Atom<String> atom : core.getAtoms())
							System.out.printf("%s, ", atom);
						System.out.println();
						break;
					case FACTS:
						for(Fact<String> fact : core.getFacts())
							System.out.println(fact);
						break;
					case REMOVE:
						core.remove(getBlocks(line.substring(line.indexOf(SINTAXE[0])+1, line.length())));
						break;
					case ANSWER:
						for(Answer<Fact<String>> current : core.getAnswers(false, false, getBlocks(line.substring(line.indexOf(SINTAXE[0])+1, line.length()))))
							output.write((current.toString() + "\n").getBytes());
						output.flush();
						break;
					case ANSWERALL:
						for(Answer<Fact<String>> current : core.getAnswersForAll(false, getBlocks(line.substring(line.indexOf(SINTAXE[0])+1, line.length()))))
							output.write((current.toString() + "\n").getBytes());
						output.flush();
						break;
					case ANSWERANY:
						for(Answer<Fact<String>> current : core.getAnswers(true, false, getBlocks(line.substring(line.indexOf(SINTAXE[0])+1, line.length()))))
							output.write((current.toString() + "\n").getBytes());
						output.flush();
						break;
					case ANSWERSUPP:
						for(Answer<Fact<String>> current : core.getAnswers(false, true, getBlocks(line.substring(line.indexOf(SINTAXE[0])+1, line.length()))))
							output.write((current.toString() + "\n").getBytes());
						output.flush();
						break;
					case ANSWERDIST:
						for(Answer<Fact<String>> current : core.getAnswers(true, true, getBlocks(line.substring(line.indexOf(SINTAXE[0])+1, line.length()))))
							output.write((current.toString() + "\n").getBytes());
						output.flush();
						break;
					case QUESTION:
						output.write(stream(core.answer(getBlocks(line.substring(0, line.length()-1)))).getBytes());
						output.flush();
						break;
					case QUESTION_SUB:
						showSubQuestion(output, line);
						break;
					case FACT:
						try
						{
							params = getParameters(line);
							double force = Double.parseDouble(params[0]);
							core.fact(force, getBlocks(params[1]));
						}
						catch(Exception e)
						{
							System.out.println("Invalid parameters.");
						}
						break;
					case PUNISH:
						core.punish(getBlocks(getParameters(line)[0]));
						break;
					case REINFORCE:
						core.reinforce(getBlocks(getParameters(line)[0]));
						break;
					case ENTRY:
						core.reinforce(getBlocks(line));
						break;
					case DONE:
						System.out.println("Processing done.   ");
						if(output instanceof FileOutputStream)
							output.close();
						input.close();
						break;
					case STATISTICS:
						output.write((core.statistics().toString() + "\n").getBytes());
						output.flush();
						break;
					case ECHO:
						output.write((line.substring(line.indexOf(SINTAXE[0])+1, line.length()) + "\n").getBytes());
						output.flush();
						break;
					case COMMENT: // bypass
						break;
					case LINKS:
						for(Atom<String> atom : core.getAtoms())
							System.out.printf("%10d : %s\n", atom.getFacts().size(), atom.toString());
						break;
					case VERSION:
						System.out.printf("Version: %s\n", Gilgamesh.getVersion());
						break;
					case UNKNOWN:
					default:
						System.out.println("Command not recognized.");
				}
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
				System.err.println("\nAn unexpected error has occurred. Please, copy this error and send it to contact@gilgamesh-ai.org\nThank you.");
			}
		
		scanner.close();
	}
	
	private Command getCommand(String line)
	{
		if(line == null || "".equals(line.trim()))
			return Command.UNKNOWN;
		
		if(line.startsWith(SINTAXE[4]))
			return Command.COMMENT;
		
		if(line.endsWith(SINTAXE[1]))
			return Command.QUESTION;
		
		if(line.endsWith(SINTAXE[5]))
			return Command.QUESTION_SUB;
		
		if(line.contains(SINTAXE[0]))
		{
			String comm = line.substring(0, line.indexOf(SINTAXE[0])).trim();
			for(Command current : Command.values())
				if(current.name().toLowerCase().equals(comm))
					return current;
		}

		return Command.ENTRY;
	}
	
	private String[] getBlocks(String line)
	{
		ArrayList<String> blocks = new ArrayList<String>();
		String buffer = "";
		
		boolean split = false;
		boolean last  = split;
		
		for(int i=0; i<line.length(); i++)
		{
			char current = line.charAt(i);
			if(SINTAXE[3].equals("" + current))
				split = !split;
			
			if(last != split || (!split && SINTAXE[2].equals("" + current)))
			{
				if(!"".equals(buffer))
					blocks.add(buffer);
				buffer = "";
				last = split;
				continue;
			}
			
			buffer += current;
		}
		
		if(!"".equals(buffer))
			blocks.add(buffer);
		
		return blocks.toArray(new String[0]);
	}
	
	private String stream(String blocks[])
	{
		if(blocks == null || blocks.length <= 0)
			return "";
		
		String answer = "";
		for(String current : blocks)
			answer += current + " ";
		return answer + "\n";
	}
	
	private String[] getParameters(String line)
	{
		StringTokenizer token = new StringTokenizer(line, SINTAXE[0]);
		token.nextToken(); // bypass the command
		
		ArrayList<String> params = new ArrayList<String>();
		
		while(token.hasMoreTokens())
			params.add(token.nextToken().trim());
		
		return params.toArray(new String[0]);
	}
	
	private void showSubQuestion(OutputStream output, String line) throws IOException
	{
		String blocks[] = getBlocks(line.substring(0, line.length()-2));
		List<Answer<Fact<String>>> answers = core.getAnswers(false, true, blocks);
		
		if(answers.size() > 0)
		{
			output.write(stream(answers.get(0).value.values()).getBytes());
			output.flush();
		}
	}
	
	private void printHelp()
	{
		System.out.println("\n---------------------------------------------------------------------------------------------");
		System.out.println("<atoms>\t\t\tIt will be considered as a fact. Eg.: 'gilgamesh: dog eats meat' will enter that fact.");
		System.out.println("<atoms>?\t\tAnswer a question. Eg.: to the fact 'dog eats meat', the command 'dog eats?' ended with '?' will answer that fact.");
		System.out.println("<atoms>?-\t\tAnswer a question, except that it suppress the question atoms.");
		System.out.println("#<atoms>\t\tIndicate a comment. No action is done.");
		System.out.println("help;\t\t\tPrint this help menu.");
		System.out.println("exit;\t\t\tExit command shell.");
		System.out.println("version;\t\tShow Gilgamesh's version.");
		System.out.println("reset;\t\t\tReset the Gilgamesh Core memory.");
		System.out.println("fact;<number>;<atoms>\tCreate a fact with a specific force.");
		System.out.println("facts;\t\t\tList all memory facts.");
		System.out.println("atoms;\t\t\tList all memory atoms.");
		System.out.println("links;\t\t\tShow connection quantities from each atom.");
		System.out.println("reinforce;<atoms>\tIncrease the force by 1 for that fact (same as default entry).");
		System.out.println("punish;<atoms>\t\tDecrease the force by 1 for that fact.");
		System.out.println("remove;<atoms>\t\tDelete a fact from Gilgamesh memory.");
		System.out.println("answer;<atoms>\t\tGet all answers, matching all atoms.");
		System.out.println("answerall;<atoms>\tGet all answers, matching all atoms and fact size.");
		System.out.println("answerany;<atoms>\tGet all answers, matching any atom.");
		System.out.println("answersupp;<atoms>\tGet all answers, matching all atoms, but suppressing the question atoms.");
		System.out.println("answerdist;<atoms>\tGet all answers, matching any atom, but suppressing the question atoms.");
		System.out.println("script;<IN>;<OUT>\tRead the <IN> script file and answer in <OUT> text file (optional).");
		System.out.println("save;<OUT>\t\tSave the Gilgamesh Core memory in a binary file.");
		System.out.println("load;<IN>\t\tLoad the Gilgamesh Core memory from a binary file.");
		System.out.println("statistics;\t\tShow Gilgamesh Core statistics.");
		System.out.println("echo;<atoms>\t\tPrint atoms in the console (or output file).");
		System.out.println("---------------------------------------------------------------------------------------------\n");
	}

	private void runScript(String line)
	{
		try
		{
			if(line == null || "".equals(line.trim()))
				return;

			String params[] = getParameters(line);
			
			if(params == null || params.length <= 0)
			{
				System.out.println("Missing parameter (file to read).");
				return;
			}
			
			File in = new File(params[0]);
			
			if(!in.exists() || in.isDirectory())
			{
				System.out.println("File not found.");
				return;
			}

			int total = 0;
			Scanner scanner = new Scanner(in);
			while(scanner.hasNextLine())
			{
				total++;
				scanner.nextLine();
			}
			scanner.close();
			
			OutputStream output;
			
			if(params.length > 1)
				output = new FileOutputStream(new File(params[1]));
			else
				output = System.out;
			
			shellLoop(new FileInputStream(in), output, total);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("No file loaded.");
		}
	}
	
	public void save(String line)
	{
		try
		{
			if(line == null || "".equals(line.trim()))
				return;
			
			String params[] = getParameters(line);
			
			if(params.length <= 0)
			{
				System.out.println("File path and name not informed.");
				return;
			}
			
			File file = new File(params[0]);
			
			if(file.isDirectory())
			{
				System.out.println("\"" + params[0] + "\" is a folder.");
				return;
			}
			
			Gilgamesh.save(file, core);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Cannot save Gilgamesh Core memory to the file " + line);
		}
	}

	
	public void load(String line)
	{
		try
		{
			if(line == null || "".equals(line.trim()))
				return;
			
			String params[] = getParameters(line);
			
			if(params.length <= 0)
			{
				System.out.println("File path and name not informed.");
				return;
			}
			
			File file = new File(params[0]);
			
			if(file.isDirectory())
			{
				System.out.println("\"" + params[0] + "\" is a folder.");
				return;
			}
			
			Core<String> temp = Gilgamesh.load(file);
			
			if(temp == null)
				System.out.println("File not found.");
			else
				core = temp;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Cannot load Gilgamesh Core memory from file " + line);
		}
	}


	
	public static void main(String ... args)
	{
		String all = "";
		for(String curr : args)
			all += curr + SINTAXE[0];
		new CommandShell(all);
	}
}































