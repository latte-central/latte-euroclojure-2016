
# Slide 1

Hello, my name is Frederic Peschanski. I am a professor
at the university Pierre et Marie Curie in Paris, France,
 and I am very happy to be here with you for a live-coding
 Mathematics session! (yeah!)
 
We will be using the LaTTe proof assistant, which is in
a Clojure library that you can get on clojars. You're only
 that far from your first Clojure proofs!

# Slide 2

... but first let me give credit where credit is due.
Most of what I will tell you comes from a very fine book:
 Type theory and formal proofs by Rob Nederpeldt and Herman
 Geuvers. I highly recommend this book if you are interested
 in the logic and its relation with the lambda-calculus.
 
# Slide 3 

Ok, so let's get started ... 

# Slide 4

The pure lambda-calculus is a language of first-class higher order
functions. In Clojure, this corresponds to only work with variables,
 anonymous functions built with `fn` and function application.

Let's take the example of probably the simplest useful function of all: identity.
If you apply this function to a value then you of course get the exact same value.

As a slightly more involved example: we consider the composition of two functions, a
 simpler variant of `comp`.
Here, we compose a function that takes an integer and returns a boolean, that
we compose with a function taking a boolean and returing a string.

# Slide 5

We can go very far with this apparently very simple language.
A thesis made by Church the inventor of the lambda-calculus in the nineteen-forties:
 that every functions computable by a mechanical mean can be encoded in the
 pure lambda-calculus.
 
 For example, you have several ways to make pairs in Clojure. Although this is
 not the simplest nor the most efficient way, you can use the pure lambda calculus
  to do so. Without going into the details, the `pair` function uses closures to
  record the two components of a pair, as well as a mean for accessing each
  of one of them. This is the role of the `fst` and `snd` functions.
  
Note that this encoding is not very programmer-friendly, but the only important
thing is that it works!

We could go a long way with such encodings, but what I want to do now
is to switch to a lambda-calculus suitable for doing mathematics.

# Slide 6

And this is exacly what is implemented by the kernel of the LaTTe proof assistant:
 a lambda-calculus. However, I have to warn you that it is a lambda calculus
 with explicit and very high-level types. It is what logicians call a type theory.
 
I know that types can be controversal in programming, and it is in fact
also the case in logic and mathematics. But let's say that you need types
to do mathematics with the lambda-calculus, I guess even logicians agree
with this statement.
 
# Slide 7

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
 
# Slide 8

One question, then, is what would be the type of the identity function.
Without boring you with details, the type of a lambda is a forall.
In this case we obtain this... <code>

An important remark here is that in the the variable
 x introduced by the second forall is not used in its body. In this
  case there is a nice shortcut <code>.

The identity function is a function from type A to type A, for any
 type A you might consider.

The arrow type is thus simply a special case of a forall!

# Slide 9

For the composition function, we need three types A B C
and we obtain the following type... Don't bother trying
to understand this, just witness the fact that the type checker
 of LaTTe agrees.

# Slide 10

Maybe you saw it but let me consider these examples from the point
of view of a logician.

The type of the identity function is 
strangely similar to a well-known logical truth: that for any
proposition `A` we have `A` implies `A`. This is the reflexivity
 of implication.

For the composition function, the logical reading is that
if `A` implies `B` and `B` implies `C` then `A` implies `C`.
This is transitivity of implication.

Here, we considered types to be logical propositions. And this
is in fact one important facet of the famous Curry-Howard correspondence:
 the proposition-as-type interpretation of the typed lambda-calculus.
 
# Slide 11

The pairing function has also a very interesting interpretation.
I'll go quickly here but here is the explicitly-typed version
of the pairing function. Let me know try to find its type.

<code>

Well, it works but this relatively complex type but I can give
 you a simpler interpretation of it. Let me rewrite this as follows
  and verify with the type checker.

<code>
  
The bottom line here is that the type of the pairing function is
what logicians call the introduction rule for conjunction.

# Slide 12

And the first and second accessors are called the elimination rules for
conjunction. 

<code>

From the conjunction of A and B you can get A, which is called the
left-elimination rule.

and of course you can get B also, by the right-elimination rule.

# Slide 13

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

# Slide 14

We have know everything at hand to write our first proof.

First let's state some logical proposition. That if A implies
 B and if I have both C and A true then I can deduce that B is true.
 
To prove this proposition, we will exploit another facet of the
Curry-Howard correspondence. In a type theory such as LaTTe, to
 prove that a proposition is true amounts to finding a term
  whose type is the given proposition.

This is called the proof-as-term interpretation. Thus you have
 propositions-as-types together with proofs-as-terms.
 
To introduce the two assumptions we use lambda's. So hypothesis H1 is
 that A implies B and hypothesis H2 is that both C and A are true.
 Now let me finish this and check that the type-checker agrees.
 
Ok so our proposition is true!

# Slide 15

It is now time for a little pause...
We learned that a lambda-calculus with explicit types
may be used to make logical statements, and proving
 this amounts to finding terms carrying the corresponding types.
 
But this is all very low level and unfriendly... Maybe you are
already bored to tears!

So what we need now are abstractions, and this is what is
provided by the LaTTe proof assistant, in great part thanks
to the Clojure environment itself.

# Slide 16

So LaTTe is a proof assistant: to let Clojure help us
doing mathematics and proving things.

The specificity of Clojure is that it is completely integrated
in the Clojure ecosystem. It is a library with a handfull of macros,
 and thus you can use existing Clojure ide's to do maths.
 And if you develop a mathematical theory, then you can simply
 deploy it as a library on Clojars and make it available to the
 world! (yeah!).
 
Perhaps the most interesting feature of LaTTe is its domain specific
language for writing proofs. It is very concise having only 2 constructions,
 assume and have, but it is also very expressive.
 
So let me give you an example of how things look when using LaTTe.

# Slide 17

Let's try to prove the same theorem we proved previously, but this
time using the high-level LaTTe. Here, I have a proof using directly a term but...

ooops ...

Somethings wrong... Let me see... Of course! I have no proof for the
`and-elim-right` theorem above! So LaTTe would not let you use a
theorem if you did not provide a proof first.

So let me give a proof.

<code>

Now everything's fine but it seems we gain nothing by using the
LaTTe abstractions.

So let's rewrite the proof term using the proof script