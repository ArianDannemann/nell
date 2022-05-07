<p align="center">
    <img src="images/logo.png" alt="logo" title="logo" width="150" height="150" />
</p>

> Nell is an interpreted language that describes simple circuits

<br>

## Version 1.1.1

- Added `NOR` gate. It will return `true` if all inputs are `false`

<br>

## Version 1.1.0

- Added supported for nested circuit. You can now define a circuit using `define <NAME> { ... }` and later simulate a specific input setting for that circuit to use the results as a normal gate
- Fixed `XOR` expecting exactly one input to be `true`, instead of showing whether the count of `true` inputs is uneven

<br>

## Version 1.0.1

- Added option to simulate specific input by typing `input a = 1` instead of all possible input variations (by just typing `input a`). You can specify multiple input states by simply adding values: `input a = 1 0 0 1 1`

<br>

## Version 1.0.0

- Initial release
