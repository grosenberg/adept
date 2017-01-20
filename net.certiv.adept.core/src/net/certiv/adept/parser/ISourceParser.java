package net.certiv.adept.parser;

import java.util.List;

import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.tree.ParseTree;

import net.certiv.adept.model.Document;

public interface ISourceParser extends IParseErrorReporter {

	/**
	 * Fills in the given model by parsing the given document.
	 * 
	 * @throws Exception
	 * @throws RecognitionException
	 */
	void process(Collector model, Document doc) throws RecognitionException, Exception;

	/** Annotate the given model with features identified by walking the parse tree. */
	void annotateFeatures(Collector model);

	/** Returns the language specific list of feature types that are to be excluded. */
	List<Integer> excludedTypes();

	ParseTree getParseTree();

	List<String> getRuleNames();

	List<String> getTokenNames();
}
