# Contributing
Making a mod loader takes a lot of work, and we can't do it alone. If you would like to help us out by writing new features or fixing bugs, that would be greatly appreciated! We have a few guidelines that we need contributors to follow to ensure consistency and quality of the project.

## Patches
Patches are files that allow us to inject our code into the vanilla game. To create a new patch, simply edit the vanilla code in `minecraft/src/main/java` and then run `gradlew genPatches` in the project directory. Our gradle plugin will take care of the hard work and generate the patch files for you. The following is a list of guidelines for writing patch code. 
- Patches should not add or remove imports. If you need to access a class that is not already imported, use the full package name to reference the code. For example `xyz.openmodloader.OpenModLoader.INSTANCE.getEventBus().post` should be used over `OpenModLoader.INSTANCE.getEventBus().post`.
- Patches should be as small as possible. If you are adding in multiple lines, consider outsourcing them to another method. When doing small checks, you should also use one line rather than two. For example `if (someVariable) return;` takes up one line.
- Removing parts of the vanilla code should be avoided as much as possible. Less changes means that there are less points of failure. 

## JavaDocs
Documentation is very important for keeping the project accessible to everyone. We are aiming to provide docs for every new class, field and method. Below is a list of our JavaDoc standards.
- All new classes, fields and methods should be documented using JavaDocs. Methods which override existing methods are the only exception.
- Authorship tags should not be included in JavaDocs.
- When refering to a class, field or method, link to it!!!
- Methods require a description, and all parameters and returns be documented.
- Parameters and other things should **not** be alligned with each other. Instead, there should be a single space between the parameter name and its description. 

## Code Style and Formatting
All code that is part of Open Mod Loader should use the same code style for the sake of consistency. A formatter file for IntelliJ can be found [here](https://github.com/OpenModLoader/OpenModLoader/blob/master/OML-IDEA-code-formatter.xml). An eclipse one will be live soon. The following is a list of guidelines for our code style.
- Spaces should be used in place of tabs. Four spaces for every tab.
- Method braces should be kept on the same line as the method descriptor. 
- New lines should be added after the start of a class and between methods. New lines should not be added after the start of a method, but they should be added between significant code blocks. 
- Field, variable and parameter names should use actual names. Stuff like var1 or par2 are not acceptable. 
- Java 8 code should be used where possible
- No line wrapping. Small exception made for large streams.

## GitHub Etiquette
This section contains a list of best practices when using git.
- Commit messages should be meaningful. Stuff like `Blame X` or `¯\_(ツ)_/¯` are not acceptable.
- If you mess something up, or don't get a commit right the first time, revert it and try again. Reverting commits is very easy and helps keep things a lot cleaner. Reverting commits is simple, `git reset --soft HEAD~1` will undo the last commit without deleting your code. You can swap 1 out with any number to go back that many commits. 
- Pull requests and issues should be adequetly reviewed before they are merged/closed. 
- Issues and pull requests should be created with meaningful names and descriptions. 