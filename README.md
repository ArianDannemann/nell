<p align="center">
    <img src="images/logo.png" alt="logo" title="logo" width="150" height="150" />
</p>

# Nell

## Table of contents

- [Nell](#nell)
  - [Table of contents](#table-of-contents)
  - [Introduction](#introduction)
  - [Installing Nell](#installing-nell)
  - [Getting started](#getting-started)
    - [Input variables](#input-variables)
    - [The circuit logic](#the-circuit-logic)
    - [Output variables](#output-variables)
  - [Example](#example)
  - [Gate types and commands](#gate-types-and-commands)
    - [input](#input)
    - [show](#show)
    - [signal](#signal)
    - [AND](#and)
    - [NOT](#not)
    - [OR](#or)
    - [NAND](#nand)
    - [XOR](#xor)

## Introduction

Nell is an interpreted language that describes simple circuits using logic gates like AND, OR, NOT, ...

Its current goal is be a quick prototyping tool for generating truth tables for a given circuit.

There is also a VS Code extension that provides some syntax highlighting: [nellext](https://marketplace.visualstudio.com/items?itemName=ArianDannemann.nellext)

## Installing Nell

To get started clone this repository by typing `git clone https://github.com/ArianDannemann/nell.git` into your command line.

You can then run `.nell` files by executing running `nell.jar` like this:

```
java -jar nell.jar <path to .nell file>
```

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

## Gate types and commands

### input

*Usage*:
```
input <SIGNAL>
```
*Description*:

Declares a new input variable for our circuit.

### show

*Usage*:
```
show <SIGNAL,...>
```
*Description*:

Marks a veriable as output.
This will make it show up in the resulting truth table.

### signal

*Usage*:
```
signal <SIGNAL>
```
*Description*:

Acts as a placeholder for a variable that hasn't yet been created, for example as output of a logic gate, but should still be used as input in another logic gate.

Since Nell is interpreted it doesn't know about logic gates that get declared later than the current line, so if you want to use a variable that doesn't exist yet you can use `signal` to make the interpreter create a new variable that can be overwritten later.

### AND

*Usage*:
```
and <INPUT SIGNAL,...> <OUTPUT SIGNAL>
```
*Description*:

Will set the state of `<OUTPUT SIGNAL>` to `true` if all input variables are `true`.
Otherwise it will be `false`.

### NOT

*Usage*:
```
not <INPUT SIGNAL> <OUTPUT SIGNAL>
```
*Description*:

Will set the state of `<OUTPUT SIGNAL>` to `true` if the state of `<INPUT SIGNAL>` is `false`.

### OR

*Usage*:
```
or <INPUT SIGNAL,...> <OUTPUT SIGNAL>
```
*Description*:

Will set the state of `<OUTPUT SIGNAL>` to `true` if atleast one of the input variables is `true`.
Otherwise it will be `false`.

### NAND

*Usage*:
```
nand <INPUT SIGNAL,...> <OUTPUT SIGNAL>
```
*Description*:

Will set the state of `<OUTPUT SIGNAL>` to `true` if not all input variables are `true`.
Otherwise it will be `false`.

### XOR

*Usage*:
```
xor <INPUT SIGNAL,...> <OUTPUT SIGNAL>
```
*Description*:

Will set the state of `<OUTPUT SIGNAL>` to `true` if exactly one input variable is `true`.
Otherwise it will be `false`.
