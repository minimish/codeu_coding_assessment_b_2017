// Copyright 2017 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.codeu.mathlang.impl;

import java.io.IOException;

import com.google.codeu.mathlang.core.tokens.Token;
import com.google.codeu.mathlang.parsing.TokenReader;

//imports to add
import java.lang.NumberFormatException;
import java.lang.StringBuilder;
import com.google.codeu.mathlang.core.tokens.StringToken;
import com.google.codeu.mathlang.core.tokens.NameToken;
import com.google.codeu.mathlang.core.tokens.SymbolToken;
import com.google.codeu.mathlang.core.tokens.NumberToken;


// MY TOKEN READER
//
// This is YOUR implementation of the token reader interface. To know how
// it should work, read src/com/google/codeu/mathlang/parsing/TokenReader.java.
// You should not need to change any other files to get your token reader to
// work with the test of the system.
public final class MyTokenReader implements TokenReader {
  private String source;
  private int index;
  private StringBuilder token;

  public MyTokenReader(String source) {
    // Your token reader will only be given a string for input. The string will
    // contain the whole source (0 or more lines).
    this.source = source;
    this.index = 0;
    this.token = new StringBuilder();
  }

  @Override
  public Token next() throws IOException {
    // Most of your work will take place here. For every call to |next| you should
    // return a token until you reach the end. When there are no more tokens, you
    // should return |null| to signal the end of input.

    // If for any reason you detect an error in the input, you may throw an IOException
    // which will stop all execution.

    //read through all of the source
    while(inBounds()) {
      if(Character.isWhitespace(getChar())) {
        keepGoing();
      }
      //handle quotes
      else if(getChar() == '"') {
        return inQuotes();
      }
      //handle all other cases
      else {
        return specialTokens(outOfQuotes());
      }
    } //end while statement
    //end of input so return null
    return null;
  }

  public Token specialTokens(String tokenString) throws IOException {
    if(tokenString.length() > 1) {
      if(isNameToken(tokenString)) {
        return new NameToken(tokenString);
      }
      else {
        //try NumberToken, else must be a stringToken
        try {
          Double.parseDouble(tokenString);
          return new NumberToken(Double.parseDouble(tokenString));
        }
        catch(NumberFormatException e) {
          return new StringToken(tokenString);
        }
      }
    } //end if statement
    //edge case for when tokenString is only one character long
    else {
      char lastChar = tokenString.charAt(0);
      //symbol
      if (isSymbolToken(lastChar)) {
        return new SymbolToken(lastChar);
      }
		  if(Character.isLetter(lastChar)) {
		  	return new NameToken(tokenString);
      } if (Character.isDigit(lastChar)){
		  	return new NumberToken(Double.parseDouble(tokenString));
		  }
      //some other character
      else {
		  	throw new IOException("Error: Input character not defined");
		  }
		} //end edge case
  }

  private String outOfQuotes() throws IOException {
    token.setLength(0);
    //check inbounds and a token that is not whitespace
    while(inBounds() && !Character.isWhitespace(getChar())) {
      if(token.length() == 0) {
        token.append(keepGoing());
      }
      else {
    		int previousIndex = token.length() - 1;
        char lastToken = token.charAt(previousIndex);
    		if(lastToken == '=' || lastToken == '+' || isSymbolToken(getChar()))
	    		return token.toString();
        else
	    		token.append(keepGoing());
    	} //end else statement
    } //end of while
    return token.toString();
  }

  //read tokens in quotes
  private Token inQuotes() throws IOException {
    token.setLength(0);
    if(keepGoing() != '"') {
      throw new IOException("String needs to start with quotes");
    }
    //keep going until end quotes
    while(getChar() != '"') {
      token.append(keepGoing());
    }
    keepGoing();
    return new StringToken(token.toString());
  }

  //increments the index and gets the character
  private char keepGoing() throws IOException {
    final char c = getChar();
    index++;
    return c;
  }

  //returns the character at index
  private char getChar() throws IOException {
    if(index >= source.length()) {
      throw new IOException("Error: Index Out of Bounds.");
    } else
    return source.charAt(index);
  }

  //next three methods are helper checks

  private boolean inBounds() {
    return (source.length() - index) > 0;
  }

  private boolean isNameToken(String tokenString) {
    return tokenString.equals("note") || tokenString.equals("print") || tokenString.equals("let");
  }

  private boolean isSymbolToken(char c) {
    return c == '+' || c == '-' || c == '=' || c == ';';
  }
}
