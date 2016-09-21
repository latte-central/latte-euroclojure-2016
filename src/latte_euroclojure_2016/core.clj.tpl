
;;; # Live-coding Mathematics
;;; ## Your first Clojure proofs

;;  using the LaTTe proof assistant: <<<https://github.com/fredokun/LaTTe|||(lambda (x) (browse-url "https://github.com/fredokun/LaTTe"))>>>

         ;;;                  ((((
         ;;;                 ((((
         ;;;                  ))))
         ;;;               _ .---.
         ;;;              ( |`---'|
         ;;;               \|     |
         ;;;               : .___, :
         ;;;                `-----'  -Karl

;;; ### Frédéric Peschanski @ Euroclojure 2016

;;; Associate Professor at UPMC
;; (Université Pierre & Marie Curie, Paris - France)

;;; Researcher at LIP6
;; (Computer science laboratory)



;;; # To give credit where credit is due ...


;;; The theory underlying LaTTe (as well as its basic library)
;;; is heavily influenced by the following book:

;;; ## Type Theory and Formal Proof: an Introduction
;;; ### Rob Nederpelt and Herman Geuvers
;; Cambridge University Press, 2012

;;;               __...--~~~~~-._   _.-~~~~~--...__
;;;             //               `V'               \\ 
;;;            //                 |                 \\ 
;;;           //__...--~~~~~~-._  |  _.-~~~~~~--...__\\ 
;;;          //__.....----~~~~._\ | /_.~~~~----.....__\\
;;;         ====================\\|//====================
;;;                         dwb `---`  



;;; # Let's get started ...

(ns latte-euroclojure-2016.core
  "This is a talk about LaTTe given @ Euroclojure 2016."

  ;; These belong to logic ;-)
  (:refer-clojure :exclude [and or not])

  ;; LaTTe core and main top-level forms
  (:require [latte.core :as latte
             :refer [definition defthm defaxiom defnotation
                     forall lambda ==>
                     assume have proof try-proof
                     term type-of check-type?]]

  ;; ... the "standard" library (propositions, quantifiers and equality) 
            [latte.prop :as p :refer [<=> and or not]]
            [latte.quant :as q :refer [exists]]
            [latte.equal :as eq :refer [equal]]))



;;; # You know `lambda`, right?

;;; Well, in Clojure, `lambda` is called `fn`  (Oh laziness!)
;;; ... we use it to construct first-class anonymous functions ...

;;; ### A trivial example: the identity function

((fn [x] x) 42)

;;; ### Another example: binary composition

((((fn [g] (fn [f] (fn [x] (f (g x)))))
   even?)                               ;; (==> int boolean)
   (fn [y] (if y "even" "odd")))        ;; (==> boolean String)
 42)



;;; # LaTTe (kernel) = Lambda with explicit types
;;; ## (a.k.a. a Type Theory)
;;;                _..._
;;;              .'     '.
;;;             /`\     /`\    |\         <<<but...|||(lambda (x) t)>>>
;;;            (__|     |__)|\  \\  /|
;;;            (     "     ) \\ || //
;;;             \         /   \\||//            <<<wait!?!|||(lambda (x) t)>>>
;;;              \   _   /  |\|`  /
;;;               '.___.'   \____/
;;;                (___)    (___)
;;;              /`     `\  / /
;;;             |         \/ /
;;;             | |     |\  /
;;;             | |     | "`
;;;             | |     |
;;;             | |     |
;;;             |_|_____|
;;;            (___)_____)
;;;            /    \   |
;;;           /   |\|   |
;;;          //||\\  Y  |
;;;         || || \\ |  |
;;;         |/ \\ |\||  |
;;;             \||__|__|
;;;              (___|___)
;;;         jgs  /   A   \
;;;             /   / \   \
;;;            \___/   \___/




;;; # Types, really?

;;; ### Example: the (type-generic) identity function

(term (lambda [A :type]
        (lambda [x A] x)))

;; e.g.:  (((lambda [A :type] (lambda [x A] x)) 42) int)
;;        --> ((lambda [x int] x) 42)
;;        --> 42



;;; # Let's do some type-checking ...

(check-type?

 ;; the lambda-term:
 
 (lambda [A :type]
   (lambda [x A] x))

 ;; is of type ...

 ✳)
 


;;; # The type-generic composition function

(check-type?

  ;; the lambda-term:

 (lambda [A B C :type]
   (lambda [g (==> A B)]
     (lambda [f (==> B C)]
       (lambda [x A]
         (f (g x))))))

 ;; is of type ...

 ✳)



;;; # The logical view...

;;; Given arbitrary types A, B and C

;;; ### - the type of the identity function on A is:

;;; (forall [A :type]
;;;   (==> A A))

;;; ###- the type of the composition function on A,B and C is:

;;; (forall [A B C :type]
;;;   (==> (==> A B)
;;;        (==> B C)
;;;        (==> A C)))



;;; # Deduction ...

;;; Logical (and thus mathematical) reasoning heavily relies on
;;; a simple albeit powerful law: <<<modus ponens|||(lambda (x) t)>>>
;; (a.k.a. detachment, cut, resolution, etc.)

;;; ### if we know that "A implies B"
;;; ### and if it is the case that "A holds"
;;; ### ... then we can deduce that "B holds" also.

(check-type? [A :type] [B :type]

   ;; find a term ...
   ✳

   ;; of type
   
   (==> (==> A B) A
        B))



;;; # Universal quantifier

;;; In type theory, the modus ponens naturally generalizes
;;; to instantiation of universal quantifiers.

(check-type?
 [Thing :type] [man (==> Thing :type)] [mortal (==> Thing :type)]
 [socrate Thing]

 ✳
 
 ;; ^^^ Was Aristotle right? ^^^
 
 (==> (forall [t Thing]
        (==> (man t) (mortal t)))
      (man socrate)
      ;; thus
      (mortal socrate)))




;;; # Our first (low-level) proof ...

;;; Let's try to prove something about
;;; the implication and conjunction

(check-type? [A :type] [B :type] [C :type]
             
  ✳
             
  ;; ^^^ the proof term ^^^
  
  (==> (==> A B) (and C A)
       B))



;;; # Entracte ...

;;; ## What we learned thus far ...

;;; ... that thanks to the Curry-Howard correspondence
;;; a lambda-calculus with types may be used to:

;;;   1) express logical propositions as types

;;;   2) formalise proofs of the propositions as terms carrying those types

;;; ## However ...

;;;   working directly with proofs-as-terms is cumbersome

;;;   and better <<<abstractions|||t>>> are required

;;; ---> so let's introduce the <<<LaTTe proof assistant|||t>>>



;;; # The LaTTe proof assistant

;;; ## Proof assistant
;;; a tool that allows to describe mathematical content on
;;; a computer, and assists in the mathematician's routine: proving things!


;;; ## About LaTTe
;;; LaTTe is a proof assistant implemented as a Clojure library
;;; with top-level forms for axioms, definitions, theorems and proofs.
;; available on Clojars: <<<[latte "0.3.2-SNAPSHOT"]|||(lambda (x) t)>>>

;;; ## Notable features

;;; - any Clojure Development environment can be used to do maths!
;; (e.g. I use both Cider and Gorilla Repl, sometimes together via nrepl...)

;;; - it leverages the Clojure (JVM/Maven) ecosystem for <<<proving in the large|||t>>>

;;; - it supports a DSL for declarative proof scripts <<<<-- hot!|||t>>>



;;; # Let's do some real maths...

;;; ### Our objective (in the few minutes to come):

;;; 1) live-code in LaTTe the <<<Peano arithmetics|||t>>>
;;; for natural numbers, and

;;; 2) demontrate an important inductive property
;;; about them...



;;; # The Peano arithmetics
;;; ### in (a bunch of) blinks of an eye

"The first Peano primive: ℕ is a primitive set"

"The second Peano primitive: 0 is in ℕ"

"The third Peano primitive: the successor function of type ℕ ⟶ ℕ"

"The first Peano axiom: there is no successor in ℕ that equals 0"

"The second Peano axiom: the successor function is injective"

"The third Peano axiom: induction principle on ℕ"
               


;;; # A proof by induction

(defthm nat-case
  "Proof by case analysis."
  [[P (==> nat :type)]]
  (==> (P zero)
       (forall [k nat] (P (succ k)))
       (forall [n nat] (P n))))

(proof nat-case
  :script
  "First we state our assumptions."

  "Now we proceed by induction on n."

  "base case (n=0): trivial since (P zero) by Hz"
  "inductive case. Suppose (P k) for some natural number k"

  "Let's prove that (P (succ k))"
  "Hence for any k (==> (P k) (P succ k))"

  "Thus (P n) is true for any n thanks to nat-induct."
  )

(definition nat-split
  "The split of natural numbers."
  [[n nat]]
  (or (equal nat n zero)
      (exists [m nat]
        (equal nat n (succ m)))))
  
(defthm nat-strong
  "A natural integer is either zero or the successor of
another integer"
  []
  (forall [n nat]
    (nat-split n)))
    
(proof nat-strong
    :script
  "We do the proof by case analysis on n."
  "1) case n=0"
  "0 = 0  by reflexivity"
  ;; TODO
  "hence the base case."
  ;; TODO

  "2) case n=k+1 assuming an arbitrary k"
    "Let the predicate Q(m) such that k+1=m+1"
    ;; TODO
    "Since  k+1 = k+1 by reflexivity we know that Q(k) is true."
    ;; TODO
    "hence there exists an m such that k+1=m+1 (namely k)"
    ;; TODO
    "from this we get that nat-split for ()k+1) is true."
    ;; TODO 
    "hence we can deduce the case for k+1."
    ;; TODO
  "we can conclude by applying the case analysis theorem."
  ;; TODO
  )
    


;;; # Yes, we could!

;;; ### Mathematics can be fun, (almost) as fun as live-coding in Clojure!
;; but ... wait! this *is* live-coding in Clojure!

;;; Formalizing and proving things can be a very addictive <<<puzzle game|||(lambda (x) t)>>>
;;; - with both a <<<single player mode|||(lambda (x) t)>>> and <<<multiplayer cooperation|||(lambda (x) t)>>> available!
;; (MMO being considered)

;;; - An un limited number of puzzles awaits you:

;;;    * starters: propositional logic, basic quantifiers, etc.

;;;    * serious challenges: numbers, inductive types, recursive functions, etc.
;;    (way better than Sudoku and even kakuro)

;;;    * professional-grade puzzles: modern mathematics



;;; # ... and what about a real challenge?

(defthm life-universe-rest
  "" [[Algos :type] [P Algos] [NP Algos]]
  (not (equal Algos P NP))) ;; or is-it?

(proof life-universe-rest
    :script
  "TODO"
  )

;;;
;;; ### Let's play together at: https://github.com/fredokun/LaTTe
;;; you're just a `lein new my-cool-maths` away...
;; (no? really? how unfortunate :~( )


;;; # Thank you!
