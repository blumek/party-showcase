# Java Rules

These rules are mandatory when writing or editing Java in this repository.

## Immutability & finality
- Apply `final` everywhere it is valid: local variables, fields, method parameters, and classes that are not designed for extension.
- Prefer immutable objects. Construct fully-initialized state; expose no setters. Use `record` for value/data carriers.
- Choose immutable collection views/copies when returning internal collections.

## Types & naming
- Use `var` for local variable declarations instead of explicit types (when the right side makes the type obvious).
- Use descriptive names for variables, methods, and classes. Never single-letter or placeholder names (`x`, `y`, `tmp`, `data`) except conventional loop counters where genuinely idiomatic.
- Method names are verbs/verb-phrases; classes are nouns.

## Visibility
- Always use the lowest visibility that works. Do not make a class, method, or field `public` unless it must be. Prefer package-private and `private`.

## Structure & complexity
- Keep methods short. Extract logic into smaller, well-named methods rather than long bodies.
- Do not overcomplicate; favor the simplest correct solution.
- Avoid nested if-else chains. Use guard clauses / early returns and untangle deeply nested code.
- Favor composition over inheritance.

## Collections, functional style, resources
- Choose collections over arrays for flexibility.
- Streamline functional interfaces with lambdas (and method references where clearer).
- Safeguard resources with try-with-resources.
- Never write empty `catch` blocks. Handle, rethrow, or log with context.

## Numbers
- Use `BigDecimal` for high-precision calculations such as money. Never `double`/`float` for monetary values. Construct `BigDecimal` from `String` and set scale/rounding explicitly.

## Dependencies
- Pass dependencies in from the outside via the constructor (constructor injection). No `new`-ing collaborators internally; no field/setter injection.

## Comments
- Do not put comments in code.
