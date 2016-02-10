# Java Code Style Guideline #

This document shows what code convetions are used by the GTGE project.

## Naming ##

  * Pick short and concisely names that describe the use and semantic. Use commen names for common use (e.g. `i` or `j` for counters).
  * Don't use pre- or suffixes. Package and class hirarchie is thought for deviding namespace not the name itself.
  * Type names are written in camel case with the first letter upper case. They should only contain nouns.
  * Package name are complete lower case and should not contain more the 10 characters.
  * Method names are written in camel case with the first letter lower case. They should begin with a verb. Use java common verb for beginning method names (e.g. get, set, is, has, add, remove, create).
  * Variable names are written in camle case with the first letter lower case. There is no naming difference between member and local variables.
  * Static final variable names are written only in upper case letters, deviding words with a underscore.

### Example ###

```
// Packages a only lower case and don't contain more the 10 characters.
package some.example.package;

// Type names are camel case.
class NamingExample {
    
    // Static finals only contain upper case and underscrores to differ words.
    public static final int A_CONSTANT_VALUE = 5;
    
    // Members are named like normal variables. They are camel case 
    // with first character lower case.
    private int[] myArray = {
            1, 2, 3, 4, 5, 6
    };
                      
    private   int    theInt     = 1;
    protected String someString = "Hello";
    protected double aDouble    = 3.0;
    
    // Methods are named camel case with first character lower case. First 
    // word is a verb.
    public void doSomething(int a, int b, int c, int d, int e, int f) {
        switch (a) {
            case 0:
                Other.doFoo();
                break;
            default:
                Other.doBaz();
        }
        
        for (int i = 0; i < 10; i++) {
            System.out.println(i + c + b);
        }
    }
}
```

## Layout & Style ##

  * Only one statement per line. If needed splite the statement into multiple lines.
  * Lines should never be much longer then 80 characters.
  * Non-static members are allways accessed with the `this` reference.
```
 // Do it like this:
 this.member = "Hello!";
 // Not like this:
 member = "Hello!";
```
  * Static members are accessed using the class name. Static final members don't have to be accessed like that, but it should be prefered.
```
 // Do it like this:
 ExampleClass.staticMember = 0;
 // Not like this:
 this.staticMember = 0;
 staticMember = 0;
```
  * Allways use absolute imports.
```
 // Do it like this:
 import java.util.Arrays;
 // Not like this:
 import java.awt.*;
```
  * Don't use magic numbers, prefre defining static finals or local finals.
  * Use minimal visibility to ensure encapsulation (`private`, `package`, `protected`, `public`).
  * Visibility is allways the first word of a declaration.
```
 // Do it like this:
 public static void doSomething();
 // Not like this:
 static public void doSomething();
```
  * The beginning braket of a block is within the same line as the statement it belongs to. The ending braket gets its own line.
```
 // Do it like this:
 if (true) {
     // ...
 }
 // Not like this:
 if (true) 
 {
     // ...
 }
 if (true) { /* ... */ }
```
  * Allways chose the smallest scope to declare a variable. Example: If a variable is only needed within one method, declare it there, not in class or somewhere else.
  * Group imports that import classes of the same package by dividing them from the other imports with a free line.
```
 import java.awt.Window;
 import java.awt.Frame;
 
 import java.util.List;
 import java.util.Map;
 
 import org.w3c.Element;
 import org.w3c.Node;
```

## Comments ##

  * **Every** class member and the class itself has an own javadoc comment describing it (Even if this is not done within the examples here).
  * Javadoc comments always contain all required tags (At least all needed `@param` and `@return`).
  * Use the common javadoc style:
```
 /**
  * Comment
  */
```

## Indentation ##

  * Indentation is done with tabs, where one indentation level consists of one tab.
  * A new indentation level is inserted within every block.
  * If a statment is broken up into multiple lines, each new line of the statement is idented twice.
  * Statments beneth a switch case are indented as if they were within a block.

### Example ###

```
// The content of every block is indented.
class IndentationExample {
    
    void foo(int a) {
        switch (a) {
            case 0:
                // Statements beneth a switch case are indented.
                Other.doFoo();
                break;
            default:
                Other.doBaz();
        }
        
        // Wrap to long calls. Everything that belongs to the call is 
        // indented twice.
        this.someString = new StringBuffer()
                .append("Humans ")
                .append("are ")
                .append("intelligent ")
                .append("apes.");
        
        this.someString = "Humans "
                + "are "
                + "intelligent "
                + "Apes.";
    }
    
    void bar(List listOfNumbers) {
        for (int i = 0; i < 10; i++) {
            listOfNumbers.add(new Integer(i));
        }
    }
}
```

## Whitespaces ##

  * The beginning braket of a block is seperated from its statement through a space.
```
 // Beginning block braket is separated form its statement by a space.
 if (true) {
     // ...
 }
```
  * Operators are seperated from their operands by a space. A exception of this rule are the following operators: semicolon, comma, brakets, dot.
```
 float var = a + i * (4 / Math.pow(0.5, x)) - 45.0f;
```
  * Commas and semicolons are whitespaced as in normal grammer. No space before, but one space after a comma.
```
 ExampleClass.exampleMethode(0, "Hello!", ExampleClass.CONSTANT);
```
  * A space is inserted between every statement and its parameter brakets if it has such.
```
 if (someBoolean) {
     // ...
 }

 for (int i = 0; i < 10; i++) {
     // ... (You can see whitespacing of semicolons in this example too)
 }
```

## File Structure ##

All properties are sorted by thier visibility using this direction: `public`, `protected`, `package`, `private`

  1. File comment and license (non javadoc)
  1. Package statement
  1. Imports
  1. Class comment (javadoc)
  1. Class
    1. Static and final properties
    1. Normal properties
    1. Constructor
    1. Methods
    1. Inner classes
  1. EOF

### Example ###

```
// 1. File comment and license (non javadoc)
/*
 * JOrbisOggRenderer.java
 *
 * Created on May 10, 2003, 1:04 PM
 *
 *
 * Copyright (c) 2008 Golden T Studios.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

// 2. Package statement
package com.golden.gamedev.engine.audio;

// 3. Imports (separate different libraries with free line)
// JFC
import java.io.InputStream;
import java.net.URL;

// JORBIS
import com.jcraft.jogg.SyncState;
import com.jcraft.jorbis.StreamState;

// GTGE
import com.golden.gamedev.engine.BaseAudioRenderer;


// 4. Class comment (javadoc)
/**
 * Play Ogg sound (*.ogg) using JOrbis library, <br>
 * JOrbis library is available to download at
 * <a href="http://www.jcraft.com/jorbis/" target="_blank">
 * http://www.jcraft.com/jorbis/</a>. <p>
 *
 * Make sure the downloaded library is included into your game classpath
 * before using this audio renderer. <p>
 *
 * How-to-use <code>JOrbisRenderer</code> in GTGE Frame Work :
 * <pre>
 *    public class YourGame extends Game {
 *
 *       protected void initEngine() {
 *          super.initEngine();
 *
 *          // set sound effect to use ogg
 *          bsSound.setSampleRenderer(new JOrbisRenderer());
 *
 *          // set music to use ogg
 *          bsMusic.setSampleRenderer(new JOrbisRenderer());
 *       }
 *
 *    }
 * </pre>
 *
 * @author Paulus Tuerah
 */
// 5. Class
public class JOrbisOggRenderer extends BaseAudioRenderer {

    // 5.1 Static and final properties
    // Final properties use UPPER_CASE
    // Start from public, protected, package, private modifiers
    // Every public, protected modifiers need javadoc
    /**
     * Audio renderer status indicates that the audio is currently playing.
     */
    public static final int  PLAYING = 1;

    private static final int BUFSIZE = 4096 * 2;  

    private static int    convsize   = BUFSIZE * 2;
    private static byte[] convbuffer = new byte[convsize];

    // 5.2 Normal properties
    /**
     * The audio renderer status. <p>
     *
     * Use this to manage renderer's {@link #END_OF_SOUND} status when the audio
     * has finished played or {@link #ERROR} status if the audio is failed to
     * play in {@link #playSound(URL)} method.
     */
    protected int status;

    private int   format;
    private int   rate;
    private int   channels;


    // 5.3 Constructor
    // Every public, protected modifiers need javadoc.
    // Use complete javadoc style with `@param` tags.
    /**
     * Creates a new instance of <code>JOrbisOggRenderer</code>.
     */
    public JOrbisOggRenderer() {
    }


    // 5.4 Methods
    // Every public, protected modifiers need javadoc.
    // Use complete javadoc style with `@param` and `@return` tags.
    /**
     * Plays sound with specified audio file.
     *
     * @param audiofile The audio file to play.
     */
    protected void playSound(URL audiofile) {
    
    }
    

    // 5.5 Inner classes
    private class OggPlayer extends Thread {
    
    }

// 6. EOF
}
```