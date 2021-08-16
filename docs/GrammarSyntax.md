# Grammar Syntax v5.1.0

Each grammar has two sections: an optional **Label section**, followed
by a **Body section**. Comments begin with a single pound sign (#) and
last until the next newline.

### Label Section

The label section is optional and consists of a list of label statements.

- Each label statement starts in a new line, includes two parts: `label
  name` and `body section`.
- It works as a macro, whenever the label is presented in the following
  parts of grammar, it will be treated as a body section assigned to it.

The format is `[$label_name: label-body-section]` (`Note:` No space is
allowed before the colon)

- `label_name` must start with an alphabet (accepted characters from the
  target language) and can be followed by unlimited times of alphabet,
  numeric digit, dash, underscore and apostrophe.
- `label-body-section` is in the same format as the body section
  describe below.

There are three pre-defined labels available for **English** only:

- `$digit`: single numeric digit
- `$integer`: any integer number(sign is optional)
- `$real`: any real number(sign is optional)

### Body Section

The Body section lists all the target command with interleaved words and
defined labels.

- Each command can be separated by pipe "|" or newline character "\n".
  It is recommended to put each command in parentheses to remove
  ambiguity (For example, "(Hello World) | (How are you)").
- Words/labels inside command can be separated by a dot(".") or
  unlimited times of space(" "), tabulator("\t"), carriage return("\r")
  (For example, "(Hello.World) | (How are you)").
- Each word can start with alphabet and be followed by unlimited times
  of alphabet, dash, and apostrophe. (For example, john's).
- Word can also be a number represented in numerical digit format. Sign
  and decimal points are also supported. (For example, -15.46). This
  number will be interpolated as label `$integer` or `$real` based on
  the value of this number.

### Grammar

- Each label starts with the symbol `$`, one alphabet and followed by
  unlimited times of alphabet, numeric digit, dash, underscore and
  apostrophe. (For example: $pet1)
- Labels are case sensitive.
- Words in body section are case-insensitive.
- If any word/label needs to be repeated for specific or non-limited
  times. The following closure operator can be appended to simplify the
  listing.

  - `*`, repeat 0 or non-limited times.
  - `+`, repeat 1 or non-limited times.
  - `?`, repeat 0 or 1 time.
  - `^x`, repeat x times, where x is an integer.
  - `{x,y}`, repeat at least x times but no more than y times, where x
    and y are both integers and x <= y. Parentheses are used to clarify
    the precedence, especially for separating commands and adding
    closure operators.

An example grammar looks like:

```
##Label Section##
[$pet: dog|cat|rabbit|bird]
[$vehicle: bicycle|ship|car|plane]
[$action1: (turn on)|(turn off)]
[$action2: volume (up | off)]
##Body Section##
I.have.a.$pet
($action1 light)|$action2
My $pet weight 24.5 lb
Her $vehicle values $integer dollars
CLE stands for cleveland
I'm 26
```

### Notes:
1. To use a created label in active grammar, a label must be define with
   `$` sign to differentiate between a label and an actual value.
   (`$pet` as a label vs `pet` as an actual value).
2. SPEAR will replace an integer or real number in a command with
   `$integer` or `$real` pre-defined label. (For example: command `I'm
   26` will be replaced by `I'm $integer` and SPEAR will recognize any
   integer number with `I'm`.
3. If requirement is to only recognize a specific integer or real number
   and not all numbers, the number should be defined in words. (For
   example, `I AM TWENTY SIX` - SPEAR will only recognize `I AM TWENTY
   SIX`)

