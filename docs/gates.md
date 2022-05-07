<p align="center">
    <img src="images/logo.png" alt="logo" title="logo" width="150" height="150" />
</p>

> Nell is an interpreted language that describes simple circuits

<br>

## Table of contents

- [Table of contents](#table-of-contents)
- [Commands](#commands)
  - [input](#input)
  - [signal](#signal)
  - [define](#define)
  - [show](#show)
- [Gate types](#gate-types)
  - [NOT](#not)
  - [AND](#and)
  - [NAND](#nand)
  - [OR](#or)
  - [XOR](#xor)
  - [NOR](#nor)

<br>

## Commands

### input

```
input <NAME>
```

Defines a new input variable.

Upon simulation this variable will be set to all possible states (`0` and `1`).
If there is more than one input variable, all possible combinations of variable states will be simulated unless otherwise specified.

Specify a specific input state that should be simulated like this:

```
input <NAME> = <STATE, ...>
```

### signal

```
signal <NAME>
```

Acts as a placeholder for a signal that is going to be defined later on.

### define

```
define <SUBCIRCUIT> {
    ...
}
```

Defines a new subcircuit.

In here you can write commands like in any other `.nell` file.
The difference is that these won't get executed right away, instead you can call this subcircuit as if it were a gate, giving it input signals and saving its returned output signals.

Inside of a subcircuit you can't use any signals from the parent circuit.
To return values use the `show` command.

Use the subcircuit like this:

```
<SUBCIRCUIT> <INPUT, ...> = <OUTPUT, ...>
```

**Example:**

If you define your own `NAND` gate like this:

```
define nand {
    input a
    input b

    and a,b = AND
    not AND = NAND

    show NAND
}
```

You can then use your new gate in your code like this:

```
input x
input y

define nand {
    ...
}

nand x,y = NAND

show NAND
```

### show

```
show <VARIABLE>
```

Marks a variable to be shown in the program output.

If you use `show` in a nested circuit it will return the variables to the parent circuit as output of the gate.

<br>

## Gate types

### NOT

```
not <VARIABLE> = <OUTPUT>
```

Inverts the state of the variable.

|a|output|
|---|---|
|0|1|
|1|0|

### AND

```
and <VARIABLE, ...> = <OUTPUT>
```

Will be `true` if all input variables are `true`.

|a|b|output|
|---|---|---|
|0|0|0|
|0|1|0|
|1|0|0|
|1|1|1|

### NAND

```
nand <VARIABLE, ...> = <OUTPUT>
```

Will be `true` if atleast one input variable is `false`.

|a|b|output|
|---|---|---|
|0|0|1|
|0|1|1|
|1|0|1|
|1|1|0|

### OR

```
or <VARIABLE, ...> = <OUTPUT>
```

Will be `true` if atleast one input variable is `true`.

|a|b|output|
|---|---|---|
|0|0|0|
|0|1|1|
|1|0|1|
|1|1|1|

### XOR

```
xor <VARIABLE, ...> = <OUTPUT>
```

Will be `true` the amount of inputs that are `true` is uneven.

|a|b|output|
|---|---|---|
|0|0|0|
|0|1|1|
|1|0|1|
|1|1|0|

### NOR

```
nor <VARIABLE, ...> = <OUTPUT>
```

Will be `true` if all input variables are `false`.

|a|b|output|
|---|---|---|
|0|0|1|
|0|1|0|
|1|0|0|
|1|1|0|
