# Contributing to Rakcha Desktop

First
off,
thank
you
for
considering
contributing
to
Rakcha
Desktop!
It's
people
like
you
that
make
Rakcha
Desktop
such
a
great
entertainment
platform.

## 📋 Table of Contents

- [Code of Conduct](#code-of-conduct)
- [How Can I Contribute?](#how-can-i-contribute)
- [Development Environment Setup](#development-environment-setup)
- [Pull Request Process](#pull-request-process)
- [Style Guides](#style-guides)
- [Community](#community)

## Code of Conduct

This
project
and
everyone
participating
in
it
is
governed
by
the [Rakcha Desktop Code of Conduct](CODE_OF_CONDUCT.md).
By
participating,
you
are
expected
to
uphold
this
code.
Please
report
unacceptable
behavior
to
maintainers@rakcha.com.

## How Can I Contribute?

### 🐛 Reporting Bugs

-

*

*
Ensure
the
bug
was
not
already
reported
**
by
searching
on
GitHub
under [Issues](https://github.com/rakcha/rakcha-desktop/issues).

-

If
you're
unable
to
find
an
open
issue
addressing
the
problem, [open a new one](https://github.com/rakcha/rakcha-desktop/issues/new).
Be
sure
to
include:
-
A
clear
title
and
description
-
Steps
to
reproduce
the
issue
-
Expected
behavior
vs.
actual
behavior
-
Screenshots
if
applicable
-
System
information (
OS,
Java
version,
etc.)

### 🚀 Feature Requests

-

Suggest
new
features
by
opening
an
issue
with
the
tag "
enhancement"

-

Clearly
describe
the
feature
and
its
benefits

-

Provide
any
relevant
examples
or
mock-ups

### 💻 Code Contributions

1.

Fork
the
repository

2.

Create
your
feature
branch:
`git checkout -b feature/amazing-feature`

3.

Make
your
changes

4.

Run
tests
to
ensure
they
pass

5.

Commit
your
changes:
`git commit -m 'Add some amazing feature'`

6.

Push
to
the
branch:
`git push origin feature/amazing-feature`

7.

Submit
a
pull
request

## Development Environment Setup

### 📋 Prerequisites

-

☕
Java
JDK
17
or
later

-

🛠️
Maven
3.6+

-

🗄️
MySQL
8.0+ (
with
XAMPP
or
standalone)

-

💻
IDE
with
JavaFX
support (
IntelliJ
IDEA,
Eclipse,
VS
Code
with
extensions)

### 🛠️ Setting Up Local Development

1.

*

*
Clone
the
repository
**:

```bash
git clone https://github.com/rakcha/rakcha-desktop.git
cd rakcha-desktop
```

2.

*

*
Install
dependencies
**:

```bash
mvn install
```

3.

*

*
Set
up
the
database
**:

    -
    Start
    your
    MySQL
    server
    -
    Run
    the
    database
    script:
    ```bash
    mysql -u root < rakcha_db.sql
    ```

4.

*

*
Configure
the
application
**:

    -
    Copy
    `config.properties.example`
    to
    `config.properties`
    -
    Update
    with
    your
    local
    settings

5.

*

*
Run
the
application
**:

```bash
mvn clean javafx:run
```

## Pull Request Process

1.

Update
the
README.md
or
documentation
with
details
of
changes
if
appropriate

2.

Follow
the
style
guides

3.

Make
sure
all
tests
pass

4.

Get
approval
from
at
least
one
reviewer

5.

Once
approved,
maintainers
will
merge
your
PR

## Style Guides

### 💾 Git Commit Messages

-

Use
the
present
tense ("
Add
feature"
not "
Added
feature")

-

Use
the
imperative
mood ("
Move
cursor
to..."
not "
Moves
cursor
to...")

-

Limit
the
first
line
to
72
characters
or
less

-

Reference
issues
and
pull
requests
liberally
after
the
first
line

-

Consider
starting
the
commit
message
with
an
applicable
emoji:
-
🎨
`:art:`
when
improving
UI
-
🚀
`:rocket:`
when
improving
performance
-
📝
`:memo:`
when
writing
docs
-
🐛
`:bug:`
when
fixing
a
bug
-
🔒
`:lock:`
when
dealing
with
security

### ☕ Java Style Guide

-

Follow
the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)

-

Use
descriptive
variable
names

-

Document
all
public
methods
and
classes
with
JavaDoc
comments

-

Keep
classes
focused
on
a
single
responsibility

### 🧪 Testing Guidelines

-

Write
unit
tests
for
all
new
features

-

Maintain
test
coverage
of
at
least
70%

-

Name
test
methods
descriptively:
`shouldDoSomethingWhenSomethingHappens()`

## Community

Join
our
community
channels
to
discuss
development:

-

💬 [Discord Server](https://discord.gg/rakcha)
-
📝 [Developer Forum](https://forum.rakcha.com)
-
📧 [Mailing List](mailto:dev@rakcha.com)

Thank
you
for
contributing
to
Rakcha
Desktop!
🎬✨
