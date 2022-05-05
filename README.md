<p align="center">
    <img src="images/logo.png" alt="logo" title="logo" width="150" height="150" />
</p>

> Nell is an interpreted language that describes simple circuits

<br>

## Current version: [1.1.0](docs/changelog.md)

## Table of contents

- [Current version: 1.1.0](#current-version-110)
- [Table of contents](#table-of-contents)
- [Introduction](#introduction)
- [Installation](#installation)
- [Getting started](#getting-started)
  - [Input variables](#input-variables)
  - [The circuit logic](#the-circuit-logic)
  - [Output variables](#output-variables)
- [Gate types and commands](#gate-types-and-commands)
- [Example](#example)
- [Known issues](#known-issues)

<br>

## Introduction

Nell is an interpreted language that describes simple circuits using logic gates like AND, OR, NOT, ...

Its current goal is be a quick prototyping tool for generating truth tables for a given circuit.

There is also a VS Code extension that provides some syntax highlighting: [nellext](https://marketplace.visualstudio.com/items?itemName=ArianDannemann.nellext)

<br>

## Installation

To get started clone this repository by typing `git clone https://github.com/ArianDannemann/nell.git` into your command line.

You can then run `.nell` files by running `nell.jar` like this:

```
java -jar nell.jar <path to .nell file>
```

<br>

## Getting started

Any Nell file is structured like this:

1. Input variables
2. The actual logic of the circuit
3. Output variables

### Input variables

You can define any number of input variables for your circuit.

These will be changed into all possible combinations upon simulation.

Let's declare variable called `x` like this:

```
input x
```

Nell will automatically declare a negated version of the variable that is preceeded by an exclamation mark.
So declaring `x` will result in another variable `!x` being declared.
Also, it will create a `NOT` gate in order to invert the variable.

So our previous declaration is effectively the same as writing:

```
input x
not x = !x
```

You can declare as many input variables as you want.

So writing

```
input a
input b
input c
```

will result in the following list of variables to which you now have access: `a,b,c,!a,!b,!c`

Upon simulation these will be changed into the following settings:

|abc|
|---|
|000|
|001|
|010|
|011|
|100|
|101|
|...|

For each setting the program will run through the circuit using the input variables as starting points.

### The circuit logic

Now that you have declared inputs, you can create logic gates that use these inputs.

For example, if we wanted to create a simple `AND` gate we could write:

```
input a
input b

and a,b = c
```

This will create an `AND` gate that takes `a` and `b` as an input and writes the result to `c`.
`c` can now also be used as a variable in other gates.

### Output variables

You can mark any number of variables as output by typing

```
show a,b,c,...
```

This will tell Nell to display the variables in the resulting truth table.

<br>

## Gate types and commands

For a complete list of gate types and commands look [here](docs/gates.md).

<br>

## Example

There are some examples in the "examples" folder, but let's take a look at one here.

Let's say we wanted to build an `XOR` gate by only osing `NAND` gates (even though `XOR` gates are supported by Nell).
First of all we would start by declaring our inputs `x` and `y`:

```
input x
input y
```

Then we would create some gates to act as `NOT` gates, since we can't use `!x` or `!y` in this example:

```
nand x,x = notX
nand y,y = notY
```

Both use the same input variable multiple times, which is allowed by Nell.
We can use this trick to invert the actual signal, since if `x` is `0` the output of `NAND` will be `1` and vice versa.

Now we can create the gates that make up the final logic of our circuit:

```
nand notX,y = a
nand x,notY = b

nand a,b = z
```

And make sure we see our variable `z` in the truth table:

```
show z
```

The final `.nell` file then looks like this:

```
input x
input y

nand x,x = notX
nand y,y = notY

nand notX,y = a
nand x,notY = b

nand a,b = z

show z
```

And this is the resulting truth table when run:

```
> java -jar nell.jar ./examples/xor_from_nand.nell
[NELL] opening: ./examples/xor_from_nand.nell
[NELL] interpreting...
[NELL] simulating...
[NELL] results:

 xy |   z

 00 |   0
 01 |   1
 10 |   1
 11 |   0
```

<br>

## Known issues

- if a gate triggers, its result is propagated through the circuit even if the state of the result didn't change compared to before, this can lead to infinite loops
- code looks terrible and needs to be refactored badly
