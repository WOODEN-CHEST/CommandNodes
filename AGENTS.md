---
apply: always
---

# Agent Guidelines

## Code Style

This codebase follows a C#-inspired style. The rules below are mandatory.

### Naming Conventions

| Member | Convention | Example |
|---|---|---|
| Private / default fields | `_camelCase` | `_myField` |
| Private / default static fields | `s_camelCase` | `s_instance` |
| Public / protected fields | `PascalCase` | `MyField` |
| Constants (any access level) | `CAPITAL_SNAKE_CASE` | `MAX_HEALTH` |
| Methods & classes | `PascalCase` | `MyClass`, `DoThing()` |
| Interfaces | `IPascalCase` | `IMyInterface` |
| Function arguments | `camelCase` | `myArg` |
| Local variables | `PascalCase` | `LocalVar` |

### Booleans

Boolean properties must be named so they read as a yes/no question — never a noun or verb alone.

- Correct: `IsJumping`, `HasHealth`, `ArePlayersReady`
- Incorrect: `Jumping`, `Health`, `PlayersReady`

### Getters & Setters

Mutable properties must use explicit `Get`/`Set` prefixes. Non-mutable check methods omit `Get`.
For consistency's sake, the 'Get' word is appended to boolean getters if they are accompanied by a setter, otherwise
'Get' can be omitted.

- Correct: `GetIsJumping()`, `SetIsJumping(bool isJumping)`
- Incorrect: `IsJumping()` (as a setter), `SetJumping()`

### Curly Brackets

Opening braces go on a **new line**, not the same line as the statement.

---

## Documentation & Comments

- Add Javadoc to **all public methods and types**.
- If a base method has documentation, do **not** repeat it in overrides (no `@inheritdoc` either).
- Only add inline comments where the code is genuinely unclear without them. Do not over-comment.

---

## Code Organisation

### Member Order Within a Type

Order members by: **fields → constructors → methods → subtypes**.

Within each category, order by: **static first**, then by access: **public → protected → default → private → inherited**.

A non-exhaustive example, top to bottom:

1. Public static fields
2. Private fields
3. Public constructors
4. Public static methods
5. Public methods
6. Private methods
7. Public static nested classes

Within the subtypes group, interfaces, classes, and records may appear in any order.

### Group Comments

Each group of related members must have a single-line comment header describing it. Rules:

- Normal sentence case, ends with a period, has a space after `//`.
- Omit the word "Public" for public member groups.
- Examples: `// Static fields.`, `// Methods.`, `// Private methods.`, `// Inherited methods.`, `// Types.`

Separate groups with **two blank lines**.

---

## General Coding Rules

### Access & Visibility

- Keep everything as private as possible; only expose what is necessary.
- **Do not use default or protected access unless you actually plan on using it.**
- Avoid default and static interface methods.
- Avoid public fields unless getters/setters provide no real benefit (e.g. simple data containers), 
or it is a deliberate design choice. For example, the GameInstanceValues class uses public fields, has 50+ fields
and uses reflection to set them, or sets them directly. There would be too many getters and setters with no real benefit here.

### State & Architecture

- Avoid static mutable state. Pass service and component classes where they are needed instead.
- Static-method-only classes must be `final` with a private constructor.
- Build code to scale with the existing architecture — no bandaid fixes. Think long-term.

### Immutability

- Prefer immutability, including returning immutable copies of collections.
- If immutability significantly hurts performance or memory, use caching or skip it — do not add it at the cost of correctness or performance.

### Fields & Constructors

- Initialise fields at their declaration site when possible. Only use the constructor if initialisation requires it.

### Magic Numbers

- Avoid magic numbers; use named private or public constants instead.
- Exception: single-use config values in a method call (e.g. a sound effect volume) that are not repeated are fine without a name.

### Exceptions

- Only catch exceptions at the base of the callstack (not absolute base, I mean an operation's or functionality's base).
Letting the plugin crash is preferable to silently swallowing exceptions.
Basically, if you're gonna catch the exception, actually handle it, so catch it where it makes sense.
- Acceptable places to handle exceptions: checked exception sites, user input validation, and when intentionally building an exception-free API (e.g. the database layer, which logs internally).

### Refactoring

- Do **not** reformat or refactor existing code unless explicitly asked.
- If you notice code which requires refactoring, or some weird architecture decision and you see an improvement, suggest
the refactor after you complete your task, but don't refactor instantly.

---

## Threading & Bukkit API

> **Accessing the Bukkit API on non-main threads will cause a crash.**

- Do database access on non-main threads where possible, as the backing store may be slow.
- Use the database or scheduler class to dispatch work to the correct thread. Do not create threads manually.

---

## Platform & API

- **PaperMC API version:** 26.1.2
- **Java version:** 25
- Do not use any deprecated methods. Check the API docs when in doubt:
  [https://jd.papermc.io/paper/26.1.2/index.html](https://jd.papermc.io/paper/26.1.2/index.html)
- There is no need to build the plugin — the build script is out of scope and inaccessible.
- Do not run git commands that modify files. Read-only git commands are fine.

---

## Clarifications

If any details are unclear, ask rather than assume.