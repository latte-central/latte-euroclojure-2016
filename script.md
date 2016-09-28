
# Slide 1: intro

Hello, my name is Frederic Peschanski. I am a professor
at the university Pierre et Marie Curie in Paris in France.

I am a big fan of the talks about live-coding music, graphics or games
 in Clojure! I find these mind-blowing.

but what about live-coding less obviously fun... like mathematics!
Ok, I enjoy doing mathematics, but it can really be boring when you have
 to write a long proof with pencil and paper, or perhaps even worse using LaTeX...

So I will now try to show you how mathematics can be fun.
For this, I will use a small library, named LaTTe, that makes
 it possible to do mathematics directly in Clojure.

Timing: 2:30 (total 2:30)

# Slide 2

... just before we begin, I would like to say that I started
all this after reading a book that I find very interesting.

 Type theory and formal proofs: an introduction

 If you are interested in the lambda-calculus, then I really suggest
reading this book.

Of course, you do not need to read this book to understand my talk
 or even to use LaTTe. It is about the theory behind the library.

Timing: 1:00 (total 3:30)

# Slide 3

Ok, so let's get started ... For this presentation, I will simply
use emacs and cider.

Timing 0:30 (total 4:00)

# Slide 4



There is in fact only one prerequisite for this talk: I need you to
know about the pure lambda-calclus. But of course I am not taking
any risk here since the pure lambda calculus is already *in* Clojure. 
If you take only the `fn` form with only 1 argument, variables and
 function application (plus some constants), then you *get* the pure lambda-calculus.

Here is a typical example: the identity function.
As another example you can compose functions such as with `comp`.
Here, we compose a function that takes an integer and returns a boolean, that
we compose with a function taking a boolean and returing a string.

Timing: 2:00 (total 6:00)

# Slide 5

The kernel of the LaTTe library is also a lambda-calculus. The difference with
pure lambda is that it is explicitly typed. It is in fact what logicians call
 a *type theory*.

 I know that types can be controversal in programming, and it is in fact
also the case in logic and mathematics. But let's say that you need types
to do mathematics with the lambda-calculus, and I guess even logicians agree
with this statement.

Timing: 1:00 (7:00)

# Slide 6

So in LaTTe you have a lambda-calculus with explicit types.
As an example, let's consider again the identity function but this time
a version said *type-generic*.

When I submit such a term to the LaTTe kernel, it either throws
an exception (for example when type-checking fails) or returns what
 I call a low-level lambda-term. Of course lambda is lambda, and the
 star is the type of types, also known as the keyword `:type`.

Although LaTTe is not a programming language, you would use such a
function by first supplying a type, and then a value of that type.
We have here a nice example of a term depending on a type, a feature
 of System F, also called the polymorphic lambda-calculus.

Timing: 1:00 (8:00)

# Slide 7

A question, then, is what is the type of the identity function.
Without boring you with details, the type of a lambda is a forall.
In this case we obtain this... <code>

An important remark here is that in the the variable
 x introduced by the second forall is not used in its body. In this
  case there is a nice syntactic shortcut <code>.

Thus, the identity function is a function from type A to type A, for any
 type A you might consider.

The take away here is that:

> The arrow type of functional programming is simply a special case of a universal quantification!

Timing: 1:30 (9:30)

# Slide 8

For the composition function, we need three types A B C
and we obtain the following type... Don't bother trying
to understand this, just witness the fact that the type checker
 of LaTTe agrees.

Timing: 1:30 (11:00)

# Slide 9

Maybe you saw it but something interesting happened.
I was talking about types but when reading the types what
we see are more like logical propositions.

For example, the type of the identity function is 
strangely similar to a well-known logical truth: that for any
proposition `A` we have `A` implies `A`. This is the reflexivity
 of implication.

For the composition function, the logical reading is that
if `A` implies `B` and `B` implies `C` then `A` implies `C`.
This is transitivity of implication.

Here, we considered types to be logical propositions. And this
is in fact one important facet of the famous Curry-Howard correspondence:
 the proposition-as-type interpretation of the typed lambda-calculus.

 Timing: 1:30 (12:30)

# Slide 10

There is one final ingredient I need: a logic rule to make
deductions. In Latin you would say that I need the modus ponens, but
 others would say the detachment rule, the cut rule, etc.

This central part of logic says that
if A implies B, and if A is true, then I can deduce
that B is true also.

In our type system, this can be expressed as follows.

Let's try to find a term satisfying this type.

<code>

Here it is!

What we see here is a very important aspect of the Curry-Howard 
correspondence: that modus ponens exactly correspond to function application!

If I apply a value of type A to a function from A to B, 
then I will of course get a B!

Timing : 2:00 (14:30)

# Slide 11

We saw that implication is just a special case of universal
quantification. This also works for deduction.

You probably know about the famous syllogism of Aristotle.

First, if something is a man then it is mortal
Moreover, socrate is mortal
Then we can deduce that socrate is mortal.

So let's prove this proposition by finding a corresponding
term.

<code>

So yes, Aristotle was right!

Timing: 2:00 (16:30)

# Slide 12

It is now time for a little pause...
We learned that a lambda-calculus with explicit types
may be used to make logical statements, and proving
 this amounts to finding terms carrying the corresponding types.
 
But this is all very low level and unfriendly... Maybe you are
already bored to tears!

So what we need now are abstractions, and this is what is
provided by the LaTTe proof assistant, in great part thanks
to the Clojure environment itself.

Timing: 1:00 (17:30)

# Slide 13

So LaTTe is a proof assistant: to let Clojure help us
doing mathematics and proving things.

The specificity of LaTTe is that it is completely integrated
in the Clojure ecosystem. It is a library with a handfull of macros,
 and thus you can use existing Clojure ide's to do maths.
 And if you develop a mathematical theory, then you can simply
 deploy it as a library on Clojars and make it available to the
 world! (yeah!).
 
Perhaps the most interesting feature of LaTTe is its domain specific
language for writing proofs. It is very concise having only 2 constructions,
 assume and have, but it is also very expressive.
 
So let me give you an example of how things look when using LaTTe.

Timing: 1:30 (19:00)

# Slide 14, 15, etc.: Peano arithmetics 

Timing:  19:00 max (38:00)

# Slide conclusion

Timing: 2:00 (40:00)

