
/*
 [The "BSD licence"]
 Copyright (c) 2015 Adam Taylor
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 1. Redistributions of source code must retain the above copyright
	notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
	notice, this list of conditions and the following disclaimer in the
	documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
	derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/


       /*
	This grammar builds on top of the ANTLR4 Java grammar, but it uses
	lexical modes to lex the annotation form of AspectJ; hence in order to use it
	you need to break Java.g4 into separate Lexer (JavaLexer.g4) and Parser (JavaParser.g4) grammars.
  */
  
     	  grammar Comments ;

// hello<b>  there </b> {@   code	 blue       bonnet 		}    end
rule
	: A			// A
	| B						//   B
	;


//PackageClause  = "package" PackageName .
//PackageName    = identifier .
packageClause
    : 'package' IDENTIFIER
    ;

//   Eclipse Public License 	-      v 1.0, http://www.eclipse.org/legal/epl-v10.html
//		Copyright (c) 2013, Christian Wulf (chwchw@gmx.de)
//	  Copyright (c) 2016, Ivan Kochurkin (kvanttt@gmail.com),    Positive Technologies.
A
	: 'a'
	;

	/**
	 * Creates an embedded instance of the tool, pending configuration. To use
	 * <ul>
	 * <li>call setters to configure	  <li>call loadResources()
	 * <li>call validateOptions()
	 * <li>call execute()
	 * <li>call getSources() to retrieve the results, if generated.
	 * </ul>
	 */
B
	: A 'b'
	;


/**
Configure the tool using command-line styled arguments.
	@param args command-line styled arguments
	 * @return true iff all of the command-line styled arguments are recognized 	*/
WS
	: [ \t\r\n\f]*           -> channel(HIDDEN) ;
